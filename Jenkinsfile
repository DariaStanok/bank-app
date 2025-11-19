pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                  mvn -v
                  mvn clean package -DskipTests
                '''
            }
        }
    }
}
