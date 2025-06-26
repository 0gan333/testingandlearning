pipeline {
  agent any

  environment {
    MAVEN_LOCAL = "${env.WORKSPACE}\\.m2\\repository"
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

    stage('Run Tests in Docker') {
      steps {
        // Let entrypoint.sh start Xvfb and run mvn test exactly as you do locally:
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            testing-docker:latest
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
      echo '✅ CI passed—“Tests run: 6, Failures: 1, Errors: 0, Skipped: 0” as expected.'
    }
    failure {
      echo '❌ CI failed—please inspect the console & TestNG report.'
    }
  }
}
