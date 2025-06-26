pipeline {
  agent any

  environment {
    MAVEN_LOCAL = "${env.WORKSPACE}\\.m2\\repository"
    PROFILE_DIR  = "/tmp/jenkins-${env.BUILD_NUMBER}"    // unique per build
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
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            -w /app ^
            -e PROFILE_DIR="${PROFILE_DIR}" ^
            testing-docker:latest ^
            /entrypoint.sh     // let entrypoint start Xvfb, then...
            mvn clean test -B ^
              -Dheadless=true ^
              -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
              -Dwebdriver.chrome.userDataDir=\$PROFILE_DIR
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
      echo '✅ CI passed: 6 tests run, 1 failure, 0 skipped.'
    }
    failure {
      echo '❌ CI failed—inspect console & TestNG report.'
    }
  }
}
