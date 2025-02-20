# MOSIP Functional Tests

The **`mosip-functional-tests`** repository contains the reusable **`apitest-commons`** library, which simplifies API testing with pre-built utilities and helpers. The **`apitest-commons`** can be used as a dependency in POM files for MOSIP testrigs of all the modules and can consume the reusable codes.

---

## Repository Structure

This repository consists of:
1. **`apitest-commons`**:
   - A reusable library for API testing.
   - Released independently to [Maven Central](https://search.maven.org/) under the artifact ID `apitest-commons`.
   - Includes its own [README](apitest-commons/README.md) for detailed instructions on setup and usage.

---

## Prerequisites

To use this repository, ensure you have:
- **Java 21** ([download here](https://jdk.java.net/)).
- **Maven 3.9.6** or higher ([installation guide](https://maven.apache.org/install.html)).
- Access to necessary MOSIP services or mocked test environments.

---

## Apitest Commons

### Setting Up and Building the Project
- Refer to the ReadMe file [README](apitest-commons/README.md)

### Using it as dependency in the Project
- Add the following dependency in the POM of required project
    ```sh
    <dependency>
      <groupId>io.mosip.testrig.apirig.apitest.commons</groupId>
      <artifactId>apitest-commons</artifactId>
      <version>1.3.2-SNAPSHOT</version>
    </dependency>

---

## License
This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)
