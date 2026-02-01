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
                        docker compose -f docker-compose.yml up -d app-financeiro
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