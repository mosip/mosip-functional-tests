# Authentication Demo Service

## Overview
Demo authentication service used by automation tests.

## Build & run (for developers)
Prerequisites:
[Config Server](https://docs.mosip.io/1.2.0/modules/module-configuration#config-server)

The project requires JDK 1.21.
1. Build and install:
    ```
    $ cd authentication-demo-service
    $ mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
    ```

2. Build Docker for a service:
    ```
    $ cd <service folder>
    $ docker build -f Dockerfile
    ```

### Configuration
[application-default.properties](https://github.com/mosip/mosip-config/blob/dev-integration/application-default.properties)

defined here.

### Add auth-adapter in a class-path to run a services
   ```
   <dependency>
       <groupId>io.mosip.kernel</groupId>
       <artifactId>kernel-auth-adapter</artifactId>
       <version>${kernel.auth.adaptor.version}</version>
   </dependency>
   ```