# Functional Tests

## Overview
This repo contains API automation test rig. The automation written using Java REST Assured and TestNG framework. The following modules are covered:
1. Pre-registration
1. Masterdata
1. Partner Management
1. ID Repository
1. IDA 
1. Resident

The test rig has multi-language support - input can be provided in any of the languages configured in a given MOSIP installation. 

## Test categories
*  Smoke: only positive scenarios.
*  Regression: all scenarios. 

## Coverage
Only external API endpoints are covered. 

## Pre-requisites:
1.	Java 11 and Maven (3.6.0) software should be installed on the machine from where the automation tests will be executed
2.	Lombok should be configured. Ref. https://projectlombok.org/
  
### For Windows
1.  Git bash (2.18.0.windows.1)
2.	`settings.xml` needs to be present in one place in `.\m2`.
  
### For Linux
1. `settings.xml` file needs to be present in two places:
    * regular maven conf folder
    * copy the same settings.xml under /usr/local/maven/conf

## Access test automation code
1.  From Browser:
   *  ‘Clone or download’ https://github.com/mosip/mosip-functional-tests as zip
   *  Unzip the contents
   *  Continue with below steps from a terminal (Linux) or command prompt (Windows)
    
2.  From Git Bash:
  *  Copy the git link i.e "https://github.com/mosip/mosip-functional-tests"
  *	 On your local sytem, gitbash at any location
  *	 Run the "git clone https://github.com/mosip/mosip-functional-tests" command

```
cd automationtests
```

## Build Test Automation Code

```
mvn clean install  
```
This creates the jar file in the ‘target’ folder

## Execute Test Automation Suite
  
  Execute the jar from the target folder on the application code deployed. In this example, the application code is run on <base_env>
  
  ### Command to use:
```
cd target/
java -jar -Dmodules=prereg -Denv.user=dev2 -Denv.endpoint= <base_env> -Denv.langcode=eng,ara,fra -Denv.testLevel=smokeAndRegression automationtests-1.2.0.1-SNAPSHOT-jar-with-dependencies.jar
```
 ### Details of the arguments used
* env.user = user of the env on which you will run the jar file. 
* env.endpoint = env where the application under test is deployed. Change the env hostname from <base_env> to any env that you will work on
* env.testlevel = this parameter has to be ‘smoke’ to run only smoke test cases, and it has to be ‘smokeandRegression’ to run all tests of all modules
* env.langcode = languages which are configured in the env.
* jar = specify the jar file to be executed
* The version of the jar file name changes as per development code version.
Example: Current version of Dev Code Base is 1.2.0.1 so the jar name will be automationtests-1.2.0.1-SNAPSHOT-jar-with-dependencies.jar

## Build and run

* Run smoke and regression

## License
This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)
