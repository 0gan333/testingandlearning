pipeline {
  agent { label 'docker-agent-02' }

  environment {
    IMAGE_NAME     = 'testing-docker'
    DOCKER_CRED_ID = 'DockerHub-Identifier'
    REGISTRY_URL   = 'https://registry.hub.docker.com'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Check External JAR') {
      steps {
        bat 'dir "%WORKSPACE%\\lib"'
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
            -Dfile="%WORKSPACE%\\lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar" ^
            -Dmaven.repo.local="%WORKSPACE%\\.m2\\repository"
        """
      }
    }

    stage('Prepare Maven Cache') {
      steps {
        bat """
          if not exist "%WORKSPACE%\\.m2\\repository" mkdir "%WORKSPACE%\\.m2\\repository"
        """
      }
    }

    stage('Build Docker Image') {
      steps { bat "docker build -t %IMAGE_NAME% ." }
    }

    stage('Run TestNG Suite') {
      steps {
        bat """
          docker run --rm ^
            -v "%WORKSPACE%:/app" ^
            -v "%WORKSPACE%\\.m2:/root/.m2" ^
            %IMAGE_NAME% ^
            mvn clean test -Dgroups="!known-issues" -Dwdm.chromeDriverVersion=134.0.6998.165 -Dheadless=true
        """
      }
      post { always { junit '**\\target\\surefire-reports\\*.xml' } }
    }

    /* … Push stage, post { } … */
  }
}
