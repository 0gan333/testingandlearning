pipeline {
    agent { label 'docker-agent-02' }

    environment {
        MAVEN_REPO_LOCAL = '.m2/repository'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install External JAR') {
            steps {
                bat """
                    if not exist "%MAVEN_REPO_LOCAL%" mkdir "%MAVEN_REPO_LOCAL%"
                    mvn install:install-file ^
                        -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
                        -DgroupId=AutomatSE ^
                        -DartifactId=seleniumUpgrade ^
                        -Dversion=0.0.1-SNAPSHOT ^
                        -Dpackaging=jar ^
                        -DgeneratePom=true ^
                        -Dmaven.repo.local=%MAVEN_REPO_LOCAL%
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
                bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'

                script {
                    // Generate a random profile directory name for Chrome to avoid user-data-dir conflict
                    def rand = new Random().nextInt(100000)
                    env._JAVA_OPTIONS = "-Dwebdriver.chrome.userDataDir=/tmp/jenkins-profile-${rand}"
                }

                bat '''
                    docker run --rm ^
                        -v "%cd%:/app" ^
                        -v "%cd%\\.m2:/root/.m2" ^
                        -w /app ^
                        testing-docker:latest ^
                        bash -c "Xvfb :99 & export DISPLAY=:99 && mvn clean test -Dsurefire.suiteXmlFiles=dynamic-suite.xml -Dheadless=true --no-transfer-progress"
                '''
            }
        }

        stage('Push on Success') {
            when {
                branch 'ci-setup'
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-push', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    bat '''
                        git config user.email "ci-bot@example.com"
                        git config user.name "ci-bot"
                        git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/0gan333/testingandlearning.git
                        git add .
                        git commit -m "🔄 Auto-push from Jenkins after successful test run"
                        git push origin ci-setup
                    '''
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo '✅ CI pipeline finished successfully.'
        }
        failure {
            echo '❌ CI pipeline failed — check the logs.'
        }
    }
}
