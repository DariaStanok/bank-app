pipeline {
    agent any

    options {
        skipDefaultCheckout(false)
    }

    environment {
        DOCKERHUB_REPO   = 'dariaku'
        DOCKER_TAG       = 'v2'
        DOCKER_DRY_RUN   = 'false'

        K8S_NAMESPACE_DEV   = 'dev'
        K8S_NAMESPACE_KAFKA = 'default'
    }

    stages {

        stage('Checkout') {
            steps {
                ansiColor('xterm') {
                    checkout scm
                }
            }
        }

        stage('Build (Maven)') {
            steps {
                ansiColor('xterm') {
                    sh 'mvn -q -Dmaven.test.skip=true clean package'
                }
            }
        }

        stage('Build Docker images (dev)') {
            steps {
                ansiColor('xterm') {
                    script {

                        def services = [
                            [dir: 'auth-service',                image: "${DOCKERHUB_REPO}/auth-service:${DOCKER_TAG}"],
                            [dir: 'accounts-service',            image: "${DOCKERHUB_REPO}/accounts-service:${DOCKER_TAG}"],
                            [dir: 'cash-service',                image: "${DOCKERHUB_REPO}/cash-service:${DOCKER_TAG}"],
                            [dir: 'transfer-service',            image: "${DOCKERHUB_REPO}/transfer-service:${DOCKER_TAG}"],
                            [dir: 'notifications-service',       image: "${DOCKERHUB_REPO}/notifications-service:${DOCKER_TAG}"],
                            [dir: 'front-ui-service',            image: "${DOCKERHUB_REPO}/front-ui:${DOCKER_TAG}"],
                            [dir: 'blocker-service',             image: "${DOCKERHUB_REPO}/blocker-service:${DOCKER_TAG}"],
                            [dir: 'exchange-service',            image: "${DOCKERHUB_REPO}/exchange-service:${DOCKER_TAG}"],
                            [dir: 'exchange-generation-service', image: "${DOCKERHUB_REPO}/exchange-generator:${DOCKER_TAG}"]
                        ]

                        services.each { svc ->
                            sh """
                               docker build -t ${svc.image} -f ${svc.dir}/Dockerfile .
                            """
                        }
                    }
                }
            }
        }

        stage('Push Docker images (dev)') {
            steps {
                ansiColor('xterm') {
                    script {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-credentials',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                            def images = [
                                "${DOCKERHUB_REPO}/auth-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/accounts-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/cash-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/transfer-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/notifications-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/front-ui:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/blocker-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/exchange-service:${DOCKER_TAG}",
                                "${DOCKERHUB_REPO}/exchange-generator:${DOCKER_TAG}"
                            ]

                            images.each { img ->
                                sh "docker push ${img}"
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy Kafka (dev)') {
            steps {
                ansiColor('xterm') {
                    script {
                        sh """
                            helm dependency update charts/infra-kafka
                            helm upgrade --install infra-kafka charts/infra-kafka -n ${K8S_NAMESPACE_KAFKA}
                        """
                    }
                }
            }
        }

        stage('Deploy to dev (Helm)') {
            steps {
                ansiColor('xterm') {
                    script {
                        sh """
                            helm upgrade --install bank-app-dev charts/umbrella \\
                                -n ${K8S_NAMESPACE_DEV} \\
                                -f charts/umbrella/values-dev.yaml
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
        success {
            echo 'Build + Push + Deploy SUCCESS'
        }
        failure {
            echo 'Pipeline FAILED'
        }
    }
}
