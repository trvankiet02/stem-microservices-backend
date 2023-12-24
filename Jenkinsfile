pipeline {
    agent any

    tools {
        // Thêm đường dẫn đến Maven vào môi trường
        maven 'Maven 3.9.1'
    }

    stages {
        stage("Login to docker"){
            steps{
             withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                 sh 'echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin'
                 }
             }
        }
        stage('Checkout') {
            steps {
                git url: 'https://github.com/trvankiet02/stem-microservices-backend', branch: 'staging'
            }
        }
        stage('Build') {
            steps {
                sh 'java -version'
                sh 'mvn clean package'
            }
        }
        stage("Deploy"){
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}