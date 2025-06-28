# Automated UI Test Framework

**Tech stack:** Java · Selenium · TestNG · Maven · Docker · Jenkins

---

## 1. Project Overview  
This repository demonstrates a full CI/CD pipeline for browser-based UI testing:

- Dynamic UI tests driven by TestNG  
- Headless Chrome in Docker + Xvfb  
- Jenkins Pipeline for build, test, and reporting  

---

## 2. Key Components

1. **Source & Tests**  
   - `src/test/java/.../DynamicUIComponentsTest.java`  
   - Generates its own TestNG XML (`dynamic-suite.xml`)

2. **Maven (`pom.xml`)**  
   - Declares Selenium, TestNG, and other dependencies  
   - Installs a custom JAR (`seleniumUpgrade-0.0.1-SNAPSHOT.jar`) into the local repo  

3. **Docker**  
   - **Dockerfile**  
     - Base image: `maven:3.8.7-eclipse-temurin-17`  
     - Installs Google Chrome and Xvfb  
     - Copies code into `/app`  
     - Includes `entrypoint.sh` to start Xvfb and run tests  
   - **entrypoint.sh**  
     ```bash
     #!/bin/bash
     set -e
     echo "Starting Xvfb on display :99"
     Xvfb :99 -screen 0 1280x1024x24 &
     export DISPLAY=:99
     echo "Running Maven tests"
     mvn clean test -B -Dheadless=true -Dsurefire.suiteXmlFiles=dynamic-suite.xml
     ```
   - **Build & Run**  
     ```bash
     # Build the Docker image
     docker build -t testing-docker:latest .

     # Run tests inside a container—
     # requires Docker installed on the host environment
     docker run --rm \
       -v "$PWD:/app" \
       -v "$PWD/.m2:/root/.m2" \
       -w /app \
       testing-docker:latest
     ```

4. **Jenkins (`Jenkinsfile`)**  
   - **Stages**:  
     1. Checkout  
     2. Install external JAR  
     3. Build Docker image  
     4. Generate TestNG XML  
     5. Run tests inside container (uses a unique Chrome profile per build)  
     6. Publish test results  

---

## 3. Intentional Test Failure

One test is designed to fail so you can observe how the pipeline surfaces and reports failures:

| Test Method                                         | Expected vs. Actual         | Result |
|-----------------------------------------------------|-----------------------------|--------|
| `selectDrillAndSetQuantity` in `DynamicUIComponentsTest` | asserts 4 but finds 1       | **FAIL** |

Use this failure to see:

- Automated test reporting in Jenkins  
- Failure annotations in the generated XML/HTML reports  
- Debug logs and console output  

---

## 4. Benefits

- **Consistency:** Docker + Xvfb guarantee the same environment everywhere.  
- **Visibility:** Jenkins captures every build, test, and failure detail.  
- **Scalability:** Easily add more tests or browsers without changing the CI logic.
