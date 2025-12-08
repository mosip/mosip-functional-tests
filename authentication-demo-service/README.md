Authentication Demo Service
Overview

The Authentication Demo Service is a lightweight demo implementation used by the MOSIP automation test suites.
It is primarily consumed by mosip-functional-tests to mock authentication-related behavior.

This README provides clear instructions on how to build, run, and configure the service locally — using either local JAR execution or Docker/Docker Compose.

Prerequisites
1. Java Version

The project requires JDK 21 (Java 21).
Ensure Java 21 is installed and set as the active version:

java -version

2. MOSIP Config Server

This service depends on configuration files served by the MOSIP Config Server.
Documentation:
https://docs.mosip.io/1.2.0/modules/module-configuration#config-server

3. Maven (for building JAR)

Ensure Maven 3.6+ is installed.

4. Docker (optional)

Required if running via Docker image or Docker Compose.

Build Instructions (Developer Setup)
1. Clone the repository
git clone https://github.com/mosip/mosip-functional-tests.git
cd mosip-functional-tests/authentication-demo-service

Build & Run Locally (JAR Method)
Step 1: Build the JAR
mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true


The packaged JAR will be available under:

target/authentication-demo-service.jar

Step 2: Configure Application Properties

The default configs are defined here:
application-default.properties

You may override configurations using:

src/main/resources/application.properties


or external config using:

java -jar authentication-demo-service.jar --spring.config.location=<path-to-config>

Step 3: Run the service locally
java -jar target/authentication-demo-service.jar


The service will start at the default port (as per application configuration).

Run via Docker
Step 1: Build Docker Image Manually
docker build -t authentication-demo-service .

Step 2: Run the Docker Container
docker run -p 8080:8080 authentication-demo-service

Option C — Run using Docker Compose

If you prefer running through Docker Compose (recommended for automation environments):

Navigate to the root folder where docker-compose.yml exists.

Run:

docker compose up --build


This will automatically start all dependent services required by the Authentication Demo Service.
