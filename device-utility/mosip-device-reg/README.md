### How to build and run

	Add correct details in the .csv file in \DeviceData,
	place correct certificates \DeviceData\certificates,
	proper configuration and environment details tobe given in config.properties file

	build the application 
	
	mvn clean install
	
	Run the java application
	
	java -jar mosip-device-reg-0.0.1-SNAPSHOT-jar-with-dependencies.jar

### What utility will do?

1.  It will do authentication.
2.	Create a partner.,If already exist it will fetch all partners.
3.	Upload a CA certificate.
4.	Upload a provider/partner certificate and return back signed certificate.
5.	Register device details for mapping make and model.
6.	Approve above details.
7.	Register software version validiti in secure biometrics.
8.	Approve above details.
