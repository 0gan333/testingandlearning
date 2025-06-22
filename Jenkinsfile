pipeline {
    agent { label 'docker-agent-02' }

    environment {
        CHROME_DRIVER_VERSION = '134.0.6998.165'
        MAVEN_REPO = '.m2/repository'
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

        stage('Run Only DynamicUIComponentsTest') {
            steps {
                script {
                    def chromeProfile = "/tmp/profile-${UUID.randomUUID().toString()}"

                    // Generate XML file using PowerShell script
                    bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'

                    // Run the container with the dynamically created suite
                    bat """
                        docker run --rm ^
                          -v "%cd%:/app" ^
                          -v "%cd%\\.m2:/root/.m2" ^
                          -w /app ^
                          -e "_JAVA_OPTIONS=-Dwebdriver.chrome.userDataDir=${chromeProfile}" ^
                          testing-docker:latest ^
                          cmd /c "mvn clean surefire:test ^
                            -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
                            -Dwdm.chromeDriverVersion=${CHROME_DRIVER_VERSION} ^
                            -Dwdm.offline=true ^
                            -Dheadless=true ^
                            -Dchrome.args=--headless --no-sandbox --disable-dev-shm-usage"
                    """
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
