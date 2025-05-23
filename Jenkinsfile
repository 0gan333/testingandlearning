pipeline {
    agent any
    stages {
        stage('Install Custom JAR') {
            steps {
                script {
                    // Ensure .m2 directory exists
                    bat 'mkdir -p "${WORKSPACE}/.m2"'
                    // Use a Maven Docker image to install the custom JAR into workspace-local Maven repo
                    docker.image('maven:3.8.7-eclipse-temurin-17').inside("-v ${env.WORKSPACE}/.m2:/root/.m2:rw") {
                        bat '''
                            mvn install:install-file \
                                -Dfile=seleniumUpgrade-0.0.1-SNAPSHOT.jar \
                                -DgroupId=com.example \
                                -DartifactId=seleniumUpgrade \
                                -Dversion=0.0.1-SNAPSHOT \
                                -Dpackaging=jar \
                                -DgeneratePom=true
                        '''
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image from Dockerfile in repo
                    def customImage = docker.build("selenium-tests:${env.BUILD_ID}")
                    // Save image object for next stage
                    env.IMAGE_NAME = "selenium-tests:${env.BUILD_ID}"
                }
            }
        }
        stage('Run Tests in Container') {
            steps {
                script {
                    // Run Maven tests inside the built image. Mount .m2 for caching.
                    def testImage = docker.image(env.IMAGE_NAME)
                    testImage.inside("-v ${env.WORKSPACE}/.m2:/root/.m2:rw") {
                        // Example Maven command with Chrome options to use headless and unique user-data-dir
                        bat '''
                            mvn clean test \
                                -Dchrome.args="--headless --no-sandbox --disable-dev-shm-usage --user-data-dir=/tmp/chrome-user-data"
                        '''
                    }
                }
            }
        }
    }
    post {
        always {
            // Publish JUnit-style test reports (adjust path if needed)
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
