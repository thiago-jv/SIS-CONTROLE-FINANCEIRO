pipeline {
    agent any
    
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'prod'],
            description: 'Selecione o ambiente de deploy'
        )
    }
    
    environment {
        SONAR_QUBE_URL = 'http://localhost:9000'
        SONAR_QUBE_TOKEN = '7b2f83dbb39e3ee3a127c23639366fee62de2787'
        DOCKER_IMAGE = "docjv/sis-financeiro"
        DOCKER_TAG = "${params.ENVIRONMENT}-${env.BUILD_ID}"
        SPRING_PROFILE = "${params.ENVIRONMENT}"
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SONAR_QUBE_SIS-FINANCEIRO') {
                    sh "${tool 'SONAR_QUBE_SCANNER_SIS_FINACEIRO'}/bin/sonar-scanner -Dsonar.projectKey=SIS-FINANCEIRO -Dsonar.host.url=${env.SONAR_QUBE_URL} -Dsonar.login=${env.SONAR_QUBE_TOKEN} -Dsonar.java.binaries=./"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                sleep(10)
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:${params.ENVIRONMENT}-latest
                """
            }
        }
        
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:${params.ENVIRONMENT}-latest
                        docker logout
                    """
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    echo "Deploying to ${params.ENVIRONMENT} environment..."
                    sh """
                        # Garante que o banco está rodando
                        docker ps -q -f name=db-postgresql || docker compose up -d db-postgresql
                        
                        # Remove container antigo
                        docker stop app-financeiro 2>/dev/null || true
                        docker rm app-financeiro 2>/dev/null || true
                        
                        # Inicia nova versão
                        docker run -d \\
                            --name app-financeiro \\
                            --network sis-controle-financeiro_network-new-financeiro \\
                            -p 8089:8089 \\
                            -e SPRING_PROFILES_ACTIVE=${params.ENVIRONMENT} \\
                            -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-postgresql:5432/bdfinanceiro \\
                            -e SPRING_DATASOURCE_USERNAME=admin \\
                            -e SPRING_DATASOURCE_PASSWORD=admin \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                        
                        echo "Application deployed: http://localhost:8089"
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo "Pipeline executado com sucesso para ambiente: ${params.ENVIRONMENT}!"
        }
        failure {
            echo "Pipeline falhou para ambiente: ${params.ENVIRONMENT}!"
        }
    }
}