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
        DOCKER_IMAGE = "docjv/sis-financeiro"
        DOCKER_TAG = "${params.ENVIRONMENT}-${env.BUILD_ID}"
        SPRING_PROFILE = "${params.ENVIRONMENT}"
        GIT_BRANCH = "${params.ENVIRONMENT == 'prod' ? 'main' : 'develop'}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "Checking out branch: ${env.GIT_BRANCH} for environment: ${params.ENVIRONMENT}"
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${env.GIT_BRANCH}"]],
                        userRemoteConfigs: [[
                            url: 'https://github.com/samueljdev/SIS-CONTROLE-FINANCEIRO',
                            credentialsId: 'GIT'
                        ]]
                    ])
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SONAR_QUBE_TOKEN', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SONAR_QUBE_SIS-FINANCEIRO') {
                        sh "${tool 'SONAR_QUBE_SCANNER_SIS_FINACEIRO'}/bin/sonar-scanner -Dsonar.projectKey=SIS-FINANCEIRO -Dsonar.host.url=${env.SONAR_QUBE_URL} -Dsonar.login=\${SONAR_TOKEN} -Dsonar.java.binaries=./"
                    }
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
                    withCredentials([
                        string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                        string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD')
                    ]) {
                        sh """
                            # Exporta variáveis para o docker compose
                            export DB_USERNAME=\${DB_USERNAME}
                            export DB_PASSWORD=\${DB_PASSWORD}
                            export SPRING_PROFILES_ACTIVE=${params.ENVIRONMENT}
                            
                            # Deploy usando docker compose
                            docker compose down app-financeiro || true
                            docker compose pull app-financeiro
                            docker compose up -d
                            
                            echo "Application deployed: http://localhost:8089"
                            echo "Grafana: http://localhost:3000"
                            echo "Prometheus: http://localhost:9090"
                        """
                    }
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