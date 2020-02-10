Install maven 3.6.0 or aboc

	$ sudo apt-get install maven

Create .m2 folder inside your home directory 

	$ mkdir $HOME/.m2
								
Install git 2.18 or above

	$ sudo apt-get install git
            
Install Java 8 (and not higher versions)

	$ sudo apt install openjdk-8-jdk
						
Clone the mosip-functional-tests repo from github

	$ git clone https://github.com/mosip/mosip-functional-tests
	$ cd mosip-functional-tess
	$ cp settings.xml $HOME/.m2
	
Modify settings.xml inside $HOME/.m2.  Find the element <localRepository> and change its value to the full path where .m2 folder is located

Copy testng-sample-test.xml to testngapi.xml

	$ cp $HOME/mosip-functional-tests/automationtests/src/main/resource testngapi.xml testngapi.xml.backup
 	$ cp $HOME/mosip-functional-tests/automationtests/src/main/resource testngapi-sample.xml testngapi.xml

Build 

	$ mvn clean install
	
Execute
	
	$ cd target
	$ java -Denv.user=qa -Denv.endpoint=<base_env> -Denv.testLevel=smoke -jar automationtests-<version>-jar-with-dependencies.jar

env.user : Change ‘qa’ to a valid user-id
env.endpoint : env where the application under test is deployed. For example, https://env.mosip.io
env.testlevel : 'smoke' to run the basic smoke tests 
<version> : is the mosip version, example 1.0.5


Check the test output

