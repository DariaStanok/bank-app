pipeline {
    agent any

    options {
        skipDefaultCheckout(false)
    }

    environment {
        DOCKERHUB_REPO = 'dariaku'
        K8S_NAMESPACE  = 'dev'
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
    }

    post {
        always {
            echo 'Pipeline finished (success or fail)'
        }
        success {
            echo 'Maven build succeeded'
        }
        failure {
            echo 'Maven build failed, check logs above'
        }
    }
}
