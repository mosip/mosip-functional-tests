# Automationtests
This readme provides detailed steps to build and execute the AUTOMATION TEST SUITE for functional testing of the MOSIP platform. It can be run after successful deployment of the platform code.

## Pre-requisites:
For Windows
1. Java (8 or above) and Maven (3.6.0) software should be installed on the machine from where the automation tests will be executed
2. Git bash (2.18.0.windows.1)
3. settings.xml needs to be present in one place
   * regular maven conf folder

For Linux
1. Java (8 or above), Maven (3.6.0) and Git softwares are available
2. settings.xml file needs to be present in two places
   * regular maven conf folder
   * copy the same settings.xml under /usr/local/maven/conf

DB Properties Change
Update the following configuration files under the automationtests/src/main/resources/dbFiles folder for the DB details; DB url, username, password as appropriate
   * audit_{env}.cfg.xml
   * kernelqa_{env}.cfg.xml
   * masterdata_{env}.cfg.xml
   * prereg_{env}.cfg.xml
   * regproc_{env}.cfg.xml
   * ida_{env}.cfg.xml
   
Update all the .properties file in resources/config/ folder with the valid user and it's credentials.

Before running the automation suite please build the auth-partner-demo in the system as the automation suite needs this jar to set up.

TestNgXmlFiles
The testNgXmlFiles folder at the root will contain all the xml files which contains the path to the corresponding scripts to each modules file. You can add and remove the scripts by adding and removing the path to scripts in the xml files.

### 1. Access Test Automation Code
From Browser:
-	‘Clone or download’ https://github.com/mosip/mosip-functional-tests as zip 
-	Unzip the contents
- Continue with below steps from a terminal (Linux) or command prompt (Windows)

From Git Bash:
- Copy the git link i.e "https://github.com/mosip/mosip-functional-tests"
- On your local sytem, gitbash at any location
- Run the "git clone https://github.com/mosip/mosip-functional-tests" command

Make sure pom.xml file lists all the dependencies, as it is used to build the automation code.
Edit the pom.xml for the latest version of mosip platform Eg: <version>1.1</version>

**Command to use:** 
<br>_cd automationtests_<br>

### 2. Build Test Automation Code
**Command to use:**
TODO: steps for building the auth partner demo services to be placed here
<br>_mvn clean install_<br>

This creates the jar file in the ‘target’ folder

### 3. Execute Test Automation Suite
Execute the jar from the target folder on the application code deployed. In this example, the application code is run on <base_env>

**Command to use:**
<br>_cd target/_<br>

_java -Denv.user={env.user} -Denv.endpoint=<base_env> -Denv.testLevel=smoke -jar -DuserID={reg_client_userID} -Dmodules={modules_to_run} automationtests-refactor-1.1-jar-with-dependencies.jar_

**Details of the arguments used**

_env.user_ = user of the env on which you will run the jar file. Change ‘qa’ to a valid user id on the test env.

_env.endpoint_ = env where the application under test is deployed. Change the env hostname from <base_env> to any env that you will work on

_env.testlevel_ = this parameter has to be ‘smoke’ to run only smoke test cases, and it has to be ‘smokeandRegression’ to run all tests of all modules, and it should be ‘regression’ to run all tests except smoke tests

reg_client_userID = this parameter will specify the user id when we are running the reg client test.

For registration client tests to run, make sure the following roles has been assigned to the user.
REGISTRATION_ADMIN, REGISTRATION_OFFICER, REGISTRATION_OPERATOR, REGISTRATION_SUPERVISOR

modules= specify the name of module which you want to run.For eg : kernel,prereg etc. If we want to run all the test at once then specify "all" 

_jar_ = specify the jar file to be executed

The version of the jar file name changes as per development code version. 



Example: Current version of Dev [code base](https://github.com/mosip/mosip-platform) is 1.1 so the jar name will be automationtests-refactor-1.1-jar-with-dependencies.jar

Note:
Before building the automation suite project it is necessary that we build the authentication-partnerdemo-service project as it serves as a data provider to the modules and there is a dependency that is added for it in our parent project.