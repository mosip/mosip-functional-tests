# Automationtests
This readme provides detailed steps to build and execute the AUTOMATION TEST SUITE for sanity testing of the MOSIP platform. It can be run after successful deployment of the platform code.

## Pre-requisites:
1. Java (8 or above) and Maven (3.6.0) software should be installed on the machine from where the automation tests will be executed
2. Git bash (2.18.0.windows.1); this is required if run from windows machine
3. settings.xml file needs to be present in two places if tests are run from a linux machine
- regular maven conf folder
- copy the same settings.xml under /usr/local/maven/conf
4. settings.xml needs to be present in one place, if tests are run from a windows machine
- regular maven conf folder

### 1. Test Automation Code
From Browser:
-	‘Clone or download’ https://github.com/mosip/mosip-functional-tests as zip 
-	Unzip the contents
- Continue with below steps from a terminal (Linux) or command prompt (Windows)

From Git Bash:
- Copy the git link i.e "https://github.com/mosip/mosip-functional-tests"
- On your local sytem, gitbash at any location
- Run the "git clone -b https://github.com/mosip/mosip-functional-tests" command

Make sure pom.xml file lists all the dependencies, as it is used to build the automation code.
Edit the pom.xml for the latest version of mosip platform Eg: 

**Command to use:** 
<br>_cd automationtests_<br>

### 2. Build test code
**Command to use:**
<br>_mvn clean install_<br>

This creates the jar file in the ‘target’ folder

### 3. Execute automation suite
Execute the jar from the target folder on the application code deployed. In this example, the application code is run on <base_env>

**Command to use:**
<br>_cd target/_<br>

_java -Denv.user=qa -Denv.endpoint=<base_env> -Denv.testLevel=smoke -jar automationtests-refactor-0.12.16-jar-with-dependencies.jar_

**Details of the arguments used**

_env.user_ = user of the env on which you will run the jar file. Change ‘qa’ to a valid user id on the test env.

_env.endpoint_ = env where the application under test is deployed. Change the env hostname from <base_env> to any env that you will work on

_env.testlevel_ = this parameter has to be ‘smoke’ to run only smoke test cases, and it has to be ‘smokeandRegression’ to run all tests of all modules, and it should be ‘regression’ to run all tests except smoke tests

_jar_ = specify the jar file to be executed

The version of the jar file name changes as per development code version. 

Example: Current version of Dev [code base](https://github.com/mosip/mosip-platform) is 0.12.16 so the jar name will be automationtests-refactor-0.12.16-jar-with-dependencies.jar

