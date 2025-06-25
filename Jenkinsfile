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
                // Install your custom SNAPSHOT into the local Maven repo on the agent
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
                // This runs your image’s entrypoint.sh, which starts Xvfb & runs mvn test against testng.xml
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
                always {
                    // Collect Surefire (TestNG) reports so Jenkins shows "Tests run: 6, Failures: 1, Errors: 0, Skipped: 0"
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
