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

    stage('Generate Single-Test Suite') {
      steps {
        // Create a TestNG XML that runs ONLY the one class we care about
        bat """
          (
            echo ^<?xml version="1.0" encoding="UTF-8"?^> 
            echo ^<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd"^>
            echo ^<suite name="SingleTestSuite"^>
            echo   ^<test name="RunOnlyDynamicUIComponents"^>
            echo     ^<classes^>
            echo       ^<class name="MavenProject.testingandlearning.DynamicUIComponentsTest"/^>
            echo     ^</classes^>
            echo   ^</test^>
            echo ^</suite^>
          ) > single-testng.xml
        """
      }
    }

    stage('Run Only That One Test') {
      steps {
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            %IMAGE_NAME%:%TAG% ^
            mvn clean test ^
              -Dsurefire.suiteXmlFiles=single-testng.xml ^
              -Dwdm.chromeDriverVersion=134.0.6998.165 ^
              -Dheadless=true ^
              -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-user-data"
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
    success { echo '✅ CI pipeline completed successfully!' }
    failure { echo '❌ CI pipeline failed—check the logs.' }
  }
}
