pipeline {
    agent any
    stages {
        stage("Get an invoke from pipeline A") {
            steps {
                script {
                    echo "I was invoked from the pipeline a."
                }
            }
        }
    }
}