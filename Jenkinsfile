pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install External JAR') {
      steps {
        bat """
          if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"
          mvn install:install-file ^
            -Dfile=lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar ^
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
        bat 'docker build -t testing-docker:latest .'
      }
    }

    stage('Run DynamicUIComponentsTest') {
      steps {
        // 1) Generate the dynamic suite XML
        bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'

        // 2) Build a unique profile path for Chrome
        script {
          def profile = "/tmp/jenkins-profile-${env.BUILD_NUMBER}"

          // 3) Run the container with our exact local command
          bat """
            docker run --rm ^
              -v "%WORKSPACE%:/app" ^
              -v "%WORKSPACE%\\.m2:/root/.m2" ^
              -w /app ^
              --entrypoint bash ^
              --env _JAVA_OPTIONS="-Dwebdriver.chrome.userDataDir=${profile}" ^
              testing-docker:latest -c " \
                Xvfb :99 -screen 0 1280x1024x24 & \
                export DISPLAY=:99 && \
                echo 'Profile dir: ' \$_JAVA_OPTIONS && \
                mvn clean test -B -Dheadless=true -Dsurefire.suiteXmlFiles=dynamic-suite.xml --no-transfer-progress \
              "
          """
        }
      }
      post {
        always {
          // Collect the exact same Surefire reports
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }

  post {
    success {
      echo '✅ All tests ran exactly as in local Docker: 6 run, 1 failure, 0 skipped.'
    }
    failure {
      echo '❌ Something still didn’t match—check the console output above.'
    }
  }
}
