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
        // Creates dynamic-suite.xml in workspace
        bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'
      }
    }

    stage('Run DynamicUIComponentsTest') {
      steps {
        script {
          // Unique Chrome profile dir per build
          def profile = "/tmp/jenkins-profile-${env.BUILD_NUMBER}"

          // Run exactly the same commands you use locally
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
                mvn clean test -B -Dheadless=true -Dsurefire.suiteXmlFiles=dynamic-suite.xml \
              "
          """
        }
      }
      post {
        always {
          // So Jenkins shows “Tests run: 6, Failures: 1, Errors: 0, Skipped: 0”
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }

  post {
    success {
      echo '✅ CI passed with the same 6 tests you see locally!'
    }
    failure {
      echo '❌ CI failed — check the console above and the TestNG report.'
    }
  }
}
