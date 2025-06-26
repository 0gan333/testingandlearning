pipeline {
  agent { label 'docker-agent-02' }

  environment {
    MAVEN_LOCAL = '.m2\\repository'
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
          if not exist "%WORKSPACE%\\${MAVEN_LOCAL}" mkdir "%WORKSPACE%\\${MAVEN_LOCAL}"
          mvn install:install-file ^
            -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
            -DgroupId=AutomatSE ^
            -DartifactId=seleniumUpgrade ^
            -Dversion=0.0.1-SNAPSHOT ^
            -Dpackaging=jar ^
            -DgeneratePom=true ^
            -Dmaven.repo.local="%WORKSPACE%\\${MAVEN_LOCAL}"
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
        // Let entrypoint.sh start Xvfb & run mvn test against testng.xml
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
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
      echo '✅ You should now see:'
      echo '-------------------------------------------------------'
      echo ' T E S T S'
      echo '-------------------------------------------------------'
      echo 'Tests run: 6, Failures: 1, Errors: 0, Skipped: 0'
    }
    failure {
      echo '❌ CI failed — check the console above & your TestNG reports.'
    }
  }
}
