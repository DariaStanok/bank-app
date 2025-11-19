pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checking Jenkins takes from SCM in the job 
                checkout scm
            }
        }

        stage('Maven: check version') {
            steps {
                // Checking mvn inside the Jenkins container  
                sh 'mvn -v'
            }
        }

        stage('Maven: build') {
            steps {
                // Build multi-module project without tests
                sh 'mvn clean package -DskipTests'
            }
        }
    }
}
