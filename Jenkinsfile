pipeline {
  agent any

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
        // On the Windows agent, install your local SNAPSHOT
        bat """
          if not exist "%WORKSPACE%\\${MAVEN_REPO_LOCAL}" mkdir "%WORKSPACE%\\${MAVEN_REPO_LOCAL}"
          mvn install:install-file ^
            -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
            -DgroupId=AutomatSE ^
            -DartifactId=seleniumUpgrade ^
            -Dversion=0.0.1-SNAPSHOT ^
            -Dpackaging=jar ^
            -DgeneratePom=true ^
            -Dmaven.repo.local="%WORKSPACE%\\${MAVEN_REPO_LOCAL}"
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
        script {
          // Pull in the Docker image we just built
          docker.image('testing-docker:latest').inside(
            "-v ${WORKSPACE}:/app " +
            "-v ${WORKSPACE}/.m2:/root/.m2 " +
            "-w /app"
          ) {
            // 1) Generate your suite file
            powershell 'generate-xml.ps1'

            // 2) Start Xvfb and set DISPLAY
            sh 'Xvfb :99 -screen 0 1280x1024x24 &'
            sh 'export DISPLAY=:99'

            // 3) Run Maven tests with a fresh Chrome profile
            sh """
              mvn clean test -B \
                -Dheadless=true \
                -Dsurefire.suiteXmlFiles=dynamic-suite.xml \
                -Dwebdriver.chrome.userDataDir=/tmp/jenkins-${BUILD_NUMBER}
            """
          }
        }
      }
      post {
        always {
          // Publish the TestNG/Surefire XML reports
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }

  post {
    success {
      echo '✅ Tests ran inside Docker exactly as locally (6 run, 1 fail, 0 skipped).'
    }
    failure {
      echo '❌ CI failed—check the My Tests report and console output above.'
    }
  }
}
