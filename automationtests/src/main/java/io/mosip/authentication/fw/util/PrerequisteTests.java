package io.mosip.authentication.fw.util;

import org.apache.log4j.Logger; 
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class PrerequisteTests extends AuthTestsUtil{
	
	private static final Logger logger = Logger.getLogger(PrerequisteTests.class);
	/**
	 * The method will perform before suite begins
	 */
	@BeforeClass
	public static void setPrerequiste() {
		logger.info("Starting authpartner demo application...");
		AuthPartnerProcessor.startProcess();
		logger.info("Starting dbConnection session...");
		DbConnection.startAuditDbSession();
		DbConnection.startIdaDbSession();
	}
	
	/**
	 * After the entire test suite clean up rest assured
	 */
	@AfterClass
	public static void authTestTearDown() {
		logger.info("Terminating authpartner demo application...");
		AuthPartnerProcessor.authPartherProcessor.destroyForcibly();
		logger.info("Terminated dbConnection session...");
		DbConnection.terminateAuditDbSession();
		DbConnection.terminateIdaDbSession();
	}

}
