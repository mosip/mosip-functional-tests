### How to build and run

	Add below partner details in the .csv file in home location \DeviceData,
		partner_id
		organisation_name 
		Address
		email
		contact_no
		partner_domain (FTM/DEVICE)
		purpose (REGISTRATION/AUTH)
		partner_type (FTM_Provider/Device_Provider)
		make 
		model 
		deviceSubType (Finger-Single/Slab/Touchless, Iris-Double/Single , Face-Full Face)
		deviceType (Finger/Iris/Face)
		
		partner_certificate_name (file name of provider certificate in \DeviceData\certificates)
		ca_certificate_name (file name of root certificate in \DeviceData\certificates)
		
	
	place valid partner certificates \DeviceData\certificates (file extension .cer)
	
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
