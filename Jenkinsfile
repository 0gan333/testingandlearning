pipeline {
    agent { label 'docker-agent-02' }

    environment {
        MAVEN_OPTS = '-Dwebdriver.chrome.userDataDir='
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install External JAR') {
            steps {
                bat '''
                    if not exist ".m2\\repository" mkdir ".m2\\repository"
                    mvn install:install-file ^
                        -Dfile="lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar" ^
                        -DgroupId=AutomatSE ^
                        -DartifactId=seleniumUpgrade ^
                        -Dversion=0.0.1-SNAPSHOT ^
                        -Dpackaging=jar ^
                        -DgeneratePom=true ^
                        -Dmaven.repo.local=".m2/repository"
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t testing-docker:latest .'
            }
        }

        stage('Run DynamicUIComponentsTest') {
            steps {
                script {
                    bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'
                    bat '''
                        docker run --rm ^
                            -v "%cd%:/app" ^
                            -v "%cd%\\.m2:/root/.m2" ^
                            -w /app ^
                            testing-docker:latest ^
                            cmd /c "mvn clean surefire:test ^
                            -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
                            -Dwdm.chromeDriverVersion=134.0.6998.165 ^
                            -Dwdm.offline=true ^
                            -Dheadless=true ^
                            -Dchrome.args=--headless --no-sandbox --disable-dev-shm-usage ^
                            -DchromeOptions.args=--no-sandbox --disable-dev-shm-usage --remote-allow-origins=*"
                    '''
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            echo '📦 CI pipeline finished.'
        }
        failure {
            echo '❌ CI pipeline failed—check the logs.'
        }
    }
}
