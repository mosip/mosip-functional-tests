# E2E Tests
This readme provides detailed steps to build and execute the E2E TEST SUITE for sanity testing of the MOSIP platform. It can be run after successful deployment of the platform code.

## Pre-requisites:
For Windows
1. Java (8 or above) and Maven (3.6.0) software should be installed on the machine from where the automation tests will be executed
2. Git bash (2.18.0.windows.1)
3. settings.xml needs to be present in one place
   * regular maven conf folder

For Linux
1. Assume Java (8 or above), Maven (3.6.0) and Git softwares are available
2. settings.xml file needs to be present in two places
   * regular maven conf folder
   * copy the same settings.xml under /usr/local/maven/conf

DB Properties Change
Update the following configuration files under the e2etests/src/main/resources folder for the DB details; DB url, username, password as appropriate
   * auditqa.cfg.xml
   * kernelqa.cfg.xml
   * masterdataqa.cfg.xml
   * preregqa.cfg.xml
   * regproc_qa.cfg.xml

## E2ETest Automation Code (From Browser)
Github new repo location - 
-	‘Clone or download’ https://github.com/mosip/mosip-functional-tests as zip 
-	Unzip the contents
- Continue with below steps from a terminal (Linux) or command prompt (Windows)

## E2ETest Automation Code (From Git Bash)
- Copy the git link i.e "https://github.com/mosip/mosip-functional-tests"
- On your local sytem, gitbash at any location
- Run the "git clone -b https://github.com/mosip/mosip-functional-tests" command

### 1. E2E Tests Test Code Folder  
pom.xml file corresponding to the automation code, which can be used to build the code.
The pom.xml should have the latest version of mosip platform.

**Command to use:** 
<br>_cd e2etestrig<br>

### 2. Build test code
**Command to use:**
<br>_mvn clean compile assembly:single_<br>

This creates the jar file in the ‘target’ folder

### 3. Execute automation suite
Execute the jar from the target folder on the application code deployed. In this example, the application code is run on <base_env>

**Command to use:**
<br>_cd target/_<br>

_java -jar e2etests-0.12.16-jar-with-dependencies.jar_


The version of the jar file name changes as per development code version and you must have the dev code to run the e2e test. 

Example: Current version of Dev [code base](https://github.com/mosip/mosip-platform) is 0.12.16 so the jar name will be e2etests-0.12.16-jar-with-dependencies
