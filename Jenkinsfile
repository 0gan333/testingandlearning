pipeline {
  agent { label 'docker-agent-02' }

  environment {
    IMAGE_NAME     = 'testing-docker'
    DOCKER_CRED_ID = 'DockerHub-Identifier'
    REGISTRY_URL   = 'https://registry.hub.docker.com'
  }

  stages {
    stage('Checkout') {
      steps {
        // Use the declarative SCM checkout
        checkout scm
      }
    }

    stage('Install External JAR') {
      steps {
        bat """
          mvn install:install-file ^
            -DgroupId=AutomatSE ^
            -DartifactId=seleniumUpgrade ^
            -Dversion=0.0.1-SNAPSHOT ^
            -Dpackaging=jar ^
            -Dfile="%WORKSPACE%\\lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar"
        """
      }
    }

    stage('Build Docker Image') {
      steps {
        bat "docker build -t %IMAGE_NAME% ."
      }
    }

    stage('Run TestNG Suite') {
      steps {
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%USERPROFILE%\\.m2:/root/.m2:ro" ^
            %IMAGE_NAME% ^
            mvn clean test -Dgroups="!known-issues" -Dwdm.chromeDriverVersion=134.0.6998.165 -Dheadless=true
        """
      }
      post {
        always {
          junit '**\\target\\surefire-reports\\*.xml'
        }
      }
    }

    stage('Push Image to Docker Hub') {
      when { branch 'master' }
      steps {
        withCredentials([usernamePassword(
          credentialsId: "${DOCKER_CRED_ID}",
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          bat """
            docker login %REGISTRY_URL% -u %DOCKER_USER% -p %DOCKER_PASS%
            docker tag %IMAGE_NAME% %DOCKER_USER%/%IMAGE_NAME%:latest
            docker push %DOCKER_USER%/%IMAGE_NAME%:latest
          """
        }
      }
    }
  }

  post {
    success { echo '✅ CI pipeline completed successfully!' }
    failure { echo '❌ CI pipeline failed—check the logs.' }
  }
}
