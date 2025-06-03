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
        // Ensure a workspace-local Maven repo
        bat 'if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"'

        // Install your custom JAR
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
         * We invoke Docker’s Maven command with two key flags:
         *  1) -Dsurefire.suiteXmlFiles=  → clears any suite file so TestNG won’t pick up “TestSuite” or others
         *  2) -Dtest=MavenProject.testingandlearning.DynamicUIComponentsTest
         *     → forces Surefire to run ONLY that one test class.
         * We also append “--user-data-dir=/tmp/chrome-$BUILD_NUMBER” so ChromeDriver
         * doesn’t clash on a shared profile.
         */
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            %IMAGE_NAME%:%TAG% ^
            mvn clean test ^
              -Dsurefire.suiteXmlFiles= ^
              -Dtest=MavenProject.testingandlearning.DynamicUIComponentsTest ^
              -Dwdm.chromeDriverVersion=134.0.6998.165 ^
              -Dwdm.offline=true ^
              -Dheadless=true ^
              -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-%BUILD_NUMBER"
        """
      }
      post {
        always {
          // Archive the Surefire XML reports so you can see pass/fail details in Jenkins
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
