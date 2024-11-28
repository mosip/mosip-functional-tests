# API Test Commons

## Overview

The API Test Commons is a shared codebase used for executing module-wise automation API tests. It utilizes Java REST Assured and TestNG frameworks to automate testing for various modules, including:
- Pre-registration
- Masterdata
- Partner Management
- PMS
- ID Repository
- IDA
- Resident
- ESignet
- ESignet-signup
- Mimoto
- Inji-Certify

---

## Pre-requisites

Ensure the following software is installed on the machine from where the automation tests will be executed:

- **Java 21** ([download here](https://jdk.java.net/)).
- **Maven 3.9.6** or higher ([installation guide](https://maven.apache.org/install.html)).
- **Lombok** (Refer to [Lombok Project](https://projectlombok.org/))
- **setting.xml** ([download here](https://github.com/mosip/mosip-functional-tests/blob/master/settings.xml))

---

### For Windows

- Git Bash 2.18.0 or higher
- `settings.xml` needs to be present in the `.m2` folder.

### For Linux

- `settings.xml` file needs to be present in two places:
  - Regular Maven conf folder
  - Copy the same `settings.xml` under `/usr/local/maven/conf`

---

## Access Test Automation Code

### From Browser

1. Clone or download the repository as a zip file from [GitHub](https://github.com/mosip/mosip-functional-tests).
2. Unzip the contents.
3. Continue with the steps below from a terminal (Linux) or command prompt (Windows).

### From Git Bash

1. Copy the git link: `https://github.com/mosip/mosip-functional-tests`
2. Open Git Bash at your desired location on your local systemn.
3. Run the following command to clone the repository:
   ```sh
   git clone https://github.com/mosip/mosip-functional-tests

---

## Update the property file
1. Navigate to the kernel.properties file located at:
    ```sh
    mosip-functional-tests\apitest-commons\src\main\resources\config\kernel.properties
2. Open the file in your preferred editor
3. Update the client secret values and other required credentials as per your environment

---

## Build Test Automation Code
1. From the already opened Git Bash, navigate to the apitest-commons directory:
    ```sh
    cd mosip-functional-tests/apitest-commons/
2. Run the following Maven command:
    ```sh
    mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true

---

## Using Apitest Commons as a dependency in the Project
- Add the following dependency in the POM of required project
    ```sh
    <dependency>
      <groupId>io.mosip.testrig.apirig.apitest.commons</groupId>
      <artifactId>apitest-commons</artifactId>
      <version>1.3.0</version>
    </dependency>

---

## License
This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)
