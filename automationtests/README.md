### Procedure to build and run the MOSIP functional test suite
The below procedure has been tried on Ubuntu LTS 18.04.  For other distros and Windows, you can contribute to this README by adding new sections 

1. Install maven 3.6.0 or above

		$ sudo apt-get install maven	

1. Create .m2 folder inside your home directory 

		$ mkdir $HOME/.m2
								
1. Install git 2.18 or above

		$ sudo apt-get install git
            
1. Install Java 8 (and not higher versions)

		$ sudo apt install openjdk-8-jdk
						
1. Clone the mosip-functional-tests repo from github

		$ git clone https://github.com/mosip/mosip-functional-tests
		$ cd mosip-functional-tests	
		$ cp settings.xml $HOME/.m2
	
1. Modify settings.xml inside $HOME/.m2.  Find the element <localRepository> and change its value to the full path where .m2 folder is located

1. Copy testngapi-sample.xml to testngapi.xml

		$ cp $HOME/mosip-functional-tests/automationtests/src/main/resource testngapi.xml testngapi.xml.backup		
 		$ cp $HOME/mosip-functional-tests/automationtests/src/main/resource testngapi-sample.xml testngapi.xml

1. Build 

		$ mvn clean install
	
1. Run
	
		$ cd target
		$ java -Denv.user=qa -Denv.endpoint=<base_env> -Denv.testLevel=smoke -jar automationtests-<version>-jar-with-dependencies.jar
	1. "env.user"		:	Change ‘qa’ to a valid user-id,
	1. "env.endpoint"	:	env where the application under test is deployed. For example, https://env.mosip.io,
	1. "env.testlevel"	:	'smoke' to run the basic smoke tests,	
	1. "version" 		:	MOSIP version deployed, example 1.0.5

1. Verify

	1. The test report is created in the folder $HOME/mosip-functional-tests/automationtests/target/testng-report.
	1. Open the file 'emailable-report.html' to get a view of the test cases passsed, skipped and failed. 
	1. Click the failed test to see the the details of what has failed and details of the error.


		

---
For other Linux distros and Windows, please add specific instructions here.  Separate each by a horizontal line.  
