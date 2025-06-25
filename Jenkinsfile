pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Install JAR') {
      steps {
        bat 'mvn install:install-file -Dfile=lib/seleniumUpgrade-0.0.1-SNAPSHOT.jar ...'
      }
    }
    stage('Build Docker Image') {
      steps {
        bat 'docker build -t testing-docker:latest .'
      }
    }
    stage('Run DynamicUIComponentsTest') {
      steps {
        // Run tests inside the Docker container using its ENTRYPOINT
        bat 'docker run --rm ' +
            '-v "%WORKSPACE%:/app" ' +
            '-v "%WORKSPACE%/.m2:/root/.m2" ' +
            '-w /app ' +
            '-e _JAVA_OPTIONS ' +
            'testing-docker:latest'
      }
      post {
        always {
          // Publish Surefire (TestNG) reports
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }
  post {
    failure {
      echo 'CI pipeline failed – please check the logs.'
    }
  }
}
