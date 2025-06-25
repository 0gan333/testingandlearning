pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install External JAR') {
            steps {
                // Install your custom snapshot into the local repo inside the agent
                bat """
                    if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"
                    mvn install:install-file ^
                        -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
                        -DgroupId=AutomatSE ^
                        -DartifactId=seleniumUpgrade ^
                        -Dversion=0.0.1-SNAPSHOT ^
                        -Dpackaging=jar ^
                        -DgeneratePom=true ^
                        -Dmaven.repo.local="%WORKSPACE%\\.m2\\repository"
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t testing-docker:latest .'
            }
        }

        stage('Run DynamicUIComponentsTest') {
            steps {
                // Let the image’s ENTRYPOINT (entrypoint.sh) handle Xvfb & mvn test
                bat """
                    docker run --rm ^
                        -v "%WORKSPACE%:/app" ^
                        -v "%WORKSPACE%\\.m2:/root/.m2" ^
                        -w /app ^
                        -e _JAVA_OPTIONS ^
                        testing-docker:latest
                """
            }
            post {
                // Always collect the Surefire XML to show results in Jenkins
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        success {
            echo '✅ CI pipeline finished successfully.'
        }
        failure {
            echo '❌ CI pipeline failed — check the logs above for details.'
        }
    }
}
