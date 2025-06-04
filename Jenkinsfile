pipeline {
  agent { label 'docker-agent-02' }

  environment {
    IMAGE_NAME = "testing-docker"
    TAG        = "latest"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install External JAR') {
      steps {
        bat 'if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"'
        bat """
          mvn install:install-file ^
            -Dfile="%WORKSPACE%\\lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar" ^
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
        bat 'docker build -t %IMAGE_NAME%:%TAG% .'
      }
    }

    stage('Run Only DynamicUIComponentsTest') {
      steps {
        /*
         * 1. Create dynamic-suite.xml in the workspace. It names exactly one TestNG class.
         * 2. Mount the entire workspace into Docker, so dynamic-suite.xml appears at /app/dynamic-suite.xml.
         * 3. Invoke mvn clean surefire:test -Dsurefire.suiteXmlFiles=dynamic-suite.xml.
         */
        bat """
          rem — create a minimal TestNG suite that runs only DynamicUIComponentsTest
          >dynamic-suite.xml echo ^<?xml version="1.0" encoding="UTF-8"?^>
          >>dynamic-suite.xml echo ^<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd"^>
          >>dynamic-suite.xml echo ^<suite name="SingleTestSuite"^>
          >>dynamic-suite.xml echo   ^<test name="SingleTest"^>
          >>dynamic-suite.xml echo     ^<classes^>
          >>dynamic-suite.xml echo       ^<class name="MavenProject.testingandlearning.DynamicUIComponentsTest"/^>
          >>dynamic-suite.xml echo     ^</classes^>
          >>dynamic-suite.xml echo   ^</test^>
          >>dynamic-suite.xml echo ^</suite^>

          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            %IMAGE_NAME%:%TAG% ^
            mvn clean surefire:test ^
              -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
              -Dwdm.chromeDriverVersion=134.0.6998.165 ^
              -Dwdm.offline=true ^
              -Dheadless=true ^
              -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage"
        """
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }
  }

  post {
    success {
      echo '✅ CI pipeline completed successfully!'
    }
    failure {
      echo '❌ CI pipeline failed—check the logs.'
    }
  }
}
