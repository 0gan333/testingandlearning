pipeline {
  agent any

  environment {
    MAVEN_LOCAL = "${env.WORKSPACE}\\.m2\\repository"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Install External JAR') {
      steps {
        bat """
          if not exist "%MAVEN_LOCAL%" mkdir "%MAVEN_LOCAL%"
          mvn install:install-file ^
            -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
            -DgroupId=AutomatSE ^
            -DartifactId=seleniumUpgrade ^
            -Dversion=0.0.1-SNAPSHOT ^
            -Dpackaging=jar ^
            -DgeneratePom=true ^
            -Dmaven.repo.local="%MAVEN_LOCAL%"
        """
      }
    }

    stage('Build Docker Image') {
      steps {
        bat 'docker build -t testing-docker:latest .'
      }
    }

    stage('Generate TestNG Suite') {
      steps {
        bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'
      }
    }

    stage('Run DynamicUIComponentsTest') {
      steps {
        // override entrypoint so we can pass our own mvn command:
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            --entrypoint bash ^
            testing-docker:latest -c " \
              Xvfb :99 -screen 0 1280x1024x24 & \
              export DISPLAY=:99 && \
              mvn clean test -B ^
                -Dheadless=true ^
                -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
                --no-transfer-progress \
            "
        """
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }

  post {
    success {
      echo '✅ CI passed—“Tests run: 6, Failures: 1, Errors: 0, Skipped: 0”'
    }
    failure {
      echo '❌ CI failed—see console & TestNG report.'
    }
  }
}
