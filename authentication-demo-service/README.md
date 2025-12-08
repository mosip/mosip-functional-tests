# Authentication Demo Service

## Overview
The **Authentication Demo Service** is a lightweight demo implementation used by MOSIP automation test suites.  
It is mainly consumed by **mosip-functional-tests** to mock authentication-related behavior.

This README provides clear and complete instructions to **build, run, and configure** the service locally using:

- Local JAR execution  
- Docker  
- Docker Compose

---

## Prerequisites

### 1. Java Version
The project requires **JDK 21**.  
Verify installation:

```bash
java -version
```

### 2. MOSIP Config Server
This service depends on configuration served by the **MOSIP Config Server**.  
Documentation:  
https://docs.mosip.io/1.2.0/modules/module-configuration#config-server

### 3. Maven
Requires **Maven 3.6+** to build the JAR.

### 4. Docker (Optional)
Required if running the service via Docker or Docker Compose.

---

## Build Instructions (Developer Setup)

### Step 1: Clone the Repository

```bash
git clone https://github.com/mosip/mosip-functional-tests.git
cd mosip-functional-tests/authentication-demo-service
```

---

## Build & Run Locally (JAR Method)

### Step 1: Build the JAR

```bash
mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
```

After build, the packaged JAR will be created at:

```
target/authentication-demo-service.jar
```

---

### Step 2: Configure Application Properties

Default config file:

```
application-default.properties
```

Override configuration via:

```
src/main/resources/application.properties
```

Or load external config:

```bash
java -jar authentication-demo-service.jar --spring.config.location=<path>
```

---

### Step 3: Run the Service

```bash
java -jar target/authentication-demo-service.jar
```

The service starts on the default port defined in application properties.

---

## Run via Docker

### Step 1: Build Docker Image

```bash
docker build -t authentication-demo-service .
```

### Step 2: Run Container

```bash
docker run -p 8080:8080 authentication-demo-service
```

---

## Run Using Docker Compose (Recommended)

If using Docker Compose, navigate to the directory containing:

```
docker-compose.yml
```

Then run:

```bash
docker compose up --build
```

This will start the Authentication Demo Service along with required dependent services.

---
