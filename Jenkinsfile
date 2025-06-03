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
        // Create workspace-local Maven repo if needed
        bat 'if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"'

        // Install your custom JAR into that local repo
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
         * We mount the workspace and local ~/.m2, then run Maven in Docker:
         *   mvn clean test 
         *     -Dsurefire.suiteXmlFiles= 
         *     -Dtest=MavenProject.testingandlearning.DynamicUIComponentsTest 
         *     -Dwdm.chromeDriverVersion=… 
         *     -Dwdm.offline=true 
         *     -Dheadless=true 
         *     -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-%BUILD_NUMBER"
         *
         * 1) -Dsurefire.suiteXmlFiles=   → clears any existing suite so TestNG won’t run “TestSuite.”  
         * 2) -Dtest=<fully-qualified-class>  → forces Surefire to pick only that single test class.  
         * 3) Chrome’s “--user-data-dir=/tmp/chrome-%BUILD_NUMBER” avoids the “user data dir already in use” error.  
         */
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            %IMAGE_NAME%:%TAG% ^
            mvn clean test -Dsurefire.suiteXmlFiles= -Dtest=MavenProject.testingandlearning.DynamicUIComponentsTest -Dwdm.chromeDriverVersion=134.0.6998.165 -Dwdm.offline=true -Dheadless=true -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-%BUILD_NUMBER"
        """
      }
      post {
        always {
          // Archive Surefire reports so Jenkins shows pass/fail
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
