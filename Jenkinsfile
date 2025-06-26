pipeline {
  agent {
    dockerfile {
      filename 'Dockerfile'
      args       '-v ${WORKSPACE}/.m2:/root/.m2'
    }
  }

  environment {
    MAVEN_LOCAL = '/root/.m2/repository'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install External JAR') {
      steps {
        sh '''
          mkdir -p "${MAVEN_LOCAL}"
          mvn install:install-file \
            -Dfile=lib/seleniumUpgrade-0.0.1-SNAPSHOT.jar \
            -DgroupId=AutomatSE \
            -DartifactId=seleniumUpgrade \
            -Dversion=0.0.1-SNAPSHOT \
            -Dpackaging=jar \
            -DgeneratePom=true \
            -Dmaven.repo.local="${MAVEN_LOCAL}"
        '''
      }
    }

    stage('Generate TestNG Suite') {
      steps {
        // this writes dynamic-suite.xml into the container /app
        bat 'powershell -ExecutionPolicy Bypass -File generate-xml.ps1'
      }
    }

    stage('Run Tests (via entrypoint)') {
      steps {
        // simply invoke your entrypoint script—no need to re-implement Xvfb/mvn here
        sh './entrypoint.sh'
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
      echo '✅ Success! You should now see exactly 6 tests run, 1 failure, 0 skipped—just like your local Docker.'
    }
    failure {
      echo '❌ Pipeline failed—check console & TestNG report.'
    }
  }
}
