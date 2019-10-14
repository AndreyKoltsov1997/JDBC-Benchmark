final TARGET_PIPELINE_NAME = "Pipeline B"

pipeline {
    agent any
    stages {
        stage("Pipeline Configuration") {
            steps {
                script {
                    currentBuild.rawBuild.project.displayName = "${TARGET_PIPELINE_NAME}"
                }
            }
        }
        stage("Get an invoke from pipeline A") {
            steps {
                script {
                    echo "I was invoked from the pipeline a."
                }
            }
        }
    }
}