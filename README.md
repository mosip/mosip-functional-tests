# MOSIP Functional Tests

## Overview

The API Test Rig Commons is a shared code base that is used for the execution of module-wise automation API tests. This piece of code uses Java REST Assured and TestNG frameworks to automate testing for different modules like Pre-registration, Masterdata, Partner Management, PMS, ID Repository, IDA, Resident, E-Signet, and Mimoto.

## Pre-requisites

Ensure the following software is installed on the machine from where the automation tests will be executed:

- Java 11
- Maven 3.6.0 or higher
- Lombok (Refer to [Lombok Project](https://projectlombok.org/))

### For Windows

- Git Bash 2.18.0 or higher
- `settings.xml` needs to be present in the `.m2` folder.

### For Linux

- `settings.xml` file needs to be present in two places:
  - Regular Maven conf folder
  - Copy the same `settings.xml` under `/usr/local/maven/conf`

## Access Test Automation Code

### From Browser

1. Clone or download the repository as a zip file from [GitHub](https://github.com/mosip/mosip-functional-tests).
2. Unzip the contents.
3. Continue with the steps below from a terminal (Linux) or command prompt (Windows).

### From Git Bash

1. Copy the git link: `https://github.com/mosip/mosip-functional-tests`
2. On your local system, open Git Bash at any location.
3. Run the following command:
   ```sh
   git clone https://github.com/mosip/mosip-functional-tests

## Build Test Automation Code
cd ../apitest-commons
mvn clean install -Dgpg.skip=true -Dmaven.gitcommitid.skip=true

## License
This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)
