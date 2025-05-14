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
        // Pulls your ci-setup branch and Jenkinsfile from GitHub
        git url: 'https://github.com/0gan333/testingandlearning.git', credentialsId: 'GitHub-access-for-Jenkins', branch: 'ci-setup'
      }
    }

    stage('Build Docker Image') {
      steps {
        // Build the image; your Dockerfile will COPY both entrypoint.sh and the project
        bat "docker build -t %IMAGE_NAME% ."
      }
    }

    stage('Install External JAR') {
      steps {
        // Install the missing seleniumUpgrade-0.0.1-SNAPSHOT.jar into the image’s local repo
        sh """
          docker run --rm %IMAGE_NAME% mvn install:install-file \
            -DgroupId=AutomatSE \
            -DartifactId=seleniumUpgrade \
            -Dversion=0.0.1-SNAPSHOT \
            -Dpackaging=jar \
            -Dfile=/app/path/to/seleniumUpgrade-0.0.1-SNAPSHOT.jar
        """
      }
    }

    stage('Run TestNG Suite') {
      steps {
        // Run the entire TestNG suite inside Docker (including your failing test)
        bat """
          docker run --rm -v "%WORKSPACE%:/app" %IMAGE_NAME% ^
            mvn clean test -Dgroups="!known-issues" -Dwdm.chromeDriverVersion=134.0.6998.165 -Dheadless=true
        """
      }
      post {
        always {
          // Archive JUnit XML results so you see your “1 failing test” log
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
