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
        // If workspace-local ~/.m2 doesn't exist, create it
        bat 'if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"'

        // Install your custom Selenium JAR into that local repo
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
         * Instead of “mvn clean test …”, we invoke “mvn clean surefire:test …”
         * so that Surefire runs only the specified test class and never auto-generates a full TestNG suite.
         */
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            %IMAGE_NAME%:%TAG% ^
            mvn clean surefire:test ^
              -Dtest=MavenProject.testingandlearning.DynamicUIComponentsTest ^
              -Dwdm.chromeDriverVersion=134.0.6998.165 ^
              -Dwdm.offline=true ^
              -Dheadless=true ^
              -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-%BUILD_NUMBER"
        """
      }
      post {
        always {
          // Publish only the DynamicUIComponentsTest results (Surefire writes them to target/surefire-reports)
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
