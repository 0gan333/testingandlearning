pipeline {
  agent {
    label 'docker-agent-02'
  }

  environment {
    MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install External JAR') {
      steps {
        bat '''
          if not exist ".m2\\repository" mkdir ".m2\\repository"
          mvn install:install-file ^
            -Dfile="lib\\seleniumUpgrade-0.0.1-SNAPSHOT.jar" ^
            -DgroupId=AutomatSE ^
            -DartifactId=seleniumUpgrade ^
            -Dversion=0.0.1-SNAPSHOT ^
            -Dpackaging=jar ^
            -DgeneratePom=true ^
            -Dmaven.repo.local=".m2/repository"
        '''
      }
    }

    stage('Build Docker Image') {
      steps {
        bat 'docker build -t testing-docker:latest .'
      }
    }

    stage('Run Only DynamicUIComponentsTest') {
      steps {
        script {
          def chromeProfile = "/tmp/profile-${UUID.randomUUID().toString()}"
          bat """
            echo ^<?xml version="1.0" encoding="UTF-8"?^> > dynamic-suite.xml
            echo ^<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd"^> >> dynamic-suite.xml
            echo ^<suite name="SingleTestSuite"^> >> dynamic-suite.xml
            echo ^  <test name="SingleTest"^> >> dynamic-suite.xml
            echo ^    <classes>^> >> dynamic-suite.xml
            echo ^      <class name="MavenProject.testingandlearning.DynamicUIComponentsTest"/> >> dynamic-suite.xml
            echo ^    </classes>^> >> dynamic-suite.xml
            echo ^  </test>^> >> dynamic-suite.xml
            echo ^</suite>^> >> dynamic-suite.xml
          """

          bat """
            docker run --rm ^
              -v "%cd%:/app" ^
              -v "%cd%\\.m2:/root/.m2" ^
              -w /app ^
              -e "_JAVA_OPTIONS=-Dwebdriver.chrome.userDataDir=${chromeProfile}" ^
              testing-docker:latest ^
              cmd /c "rmdir /s /q target && mvn clean surefire:test ^
                -Dsurefire.suiteXmlFiles=dynamic-suite.xml ^
                -Dwdm.chromeDriverVersion=134.0.6998.165 ^
                -Dwdm.offline=true ^
                -Dheadless=true ^
                -Dchrome.args=--headless --no-sandbox --disable-dev-shm-usage"
          """
        }
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
    failure {
      echo '❌ CI pipeline failed—check the logs.'
    }
  }
}
