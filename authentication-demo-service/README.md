# Authentication Demo Service

## Overview
Demo authentication service used by automation tests.

## Build & run (for developers)
Prerequisites:

1. [Config Server](https://docs.mosip.io/1.2.0/modules/module-configuration#config-server)
2. JDK 1.21
3. Build and install:
    ```
    $ cd authentication-demo-service
    $ mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
    ```
