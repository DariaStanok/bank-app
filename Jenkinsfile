pipeline {
    agent any

    options {
        skipDefaultCheckout(false)
    }

    environment {
        DOCKERHUB_REPO   = 'dariaku'
        DOCKER_TAG       = 'v2'
        DOCKER_DRY_RUN   = 'true'  

        K8S_NAMESPACE_DEV = 'dev'
        HELM_DRY_RUN      = 'true' 
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
                            [dir: 'exchange-generation-service', image: "${DOCKERHUB_REPO}/exchange-generator:${DOCKER_TAG}"],
                        ]

                        services.each { svc ->
                            if (env.DOCKER_DRY_RUN == 'true') {
                                echo "DRY RUN (build): docker build -t ${svc.image} -f ${svc.dir}/Dockerfile ."
                            } else {
                                sh """
                                  docker build \\
                                    -t ${svc.image} \\
                                    -f ${svc.dir}/Dockerfile .
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Push Docker images (dev)') {
            steps {
                ansiColor('xterm') {
                    script {
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

                        if (env.DOCKER_DRY_RUN == 'true') {
                            images.each { img ->
                                echo "DRY RUN (push): docker push ${img}"
                            }
                        } else {
                            withCredentials([usernamePassword(
                                credentialsId: 'dockerhub-creds',
                                usernameVariable: 'DOCKER_USER',
                                passwordVariable: 'DOCKER_PASS'
                            )]) {
                                sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                                images.each { img ->
                                    sh "docker push ${img}"
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy to dev (Helm)') {
            steps {
                ansiColor('xterm') {
                    script {
                        if (env.HELM_DRY_RUN == 'true') {
                            echo "DRY RUN (helm): helm upgrade --install bank-app-dev charts/umbrella -n ${K8S_NAMESPACE_DEV} -f charts/umbrella/values-dev.yaml --dry-run --debug"
                            sh """
                              helm upgrade --install bank-app-dev charts/umbrella \\
                                -n ${K8S_NAMESPACE_DEV} \\
                                -f charts/umbrella/values-dev.yaml \\
                                --dry-run --debug
                            """
                        } else {
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
    }

    post {
        always {
            echo 'Pipeline finished (success or fail)'
        }
        success {
            echo 'Maven + Docker + Helm (dev) pipeline succeeded'
        }
        failure {
            echo 'Pipeline failed, check stages above'
        }
    }
}
