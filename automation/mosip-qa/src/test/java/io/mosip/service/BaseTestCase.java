package io.mosip.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import io.mosip.prereg.scripts.Create_PreRegistration;
import io.restassured.RestAssured;
/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */

public class BaseTestCase {
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	
		
	/**
	 * Method that will take care of framework setup
	 */
	// GLOBAL CLASS VARIABLES
	private Properties prop;
	public static String ApplnURI;
	
	private void initialize()
	{
		try {
			logger.info("We have created a Config Manager. Beginning to read properties!");
			prop = new Properties();
			InputStream inputStream = new FileInputStream(
					System.getProperty("user.dir") + "//src//config//test.properties");
			prop.load(inputStream);
			logger.info("Setting test configs/TestEnvironment from " + System.getProperty("user.dir")
					+ "/src/config/test.properties");
			ApplnURI = prop.getProperty("testEnvironment");
			logger.info("Configs from properties file are set.");
			

		} catch (IOException e) {
			logger.error("Could not find the properties file.\n" + e);
		}
		
	
	}
	
	// ================================================================================================================
		// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
		// ================================================================================================================

		/**
		 * Before entire test suite we need to setup everything we will need.
		 */
		@BeforeSuite(alwaysRun = true)
		public void suiteSetup() {
			logger.info("Test Framework for Mosip api Initialized");
			logger.info("Logging initialized: All logs are located at " + System.getProperty("user.dir")
					+ "/src/logs/mosip-api-test.log");
			initialize();
			logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");
		} // End suiteSetup

		/**
		 * After the entire test suite clean up rest assured
		 */
		@AfterSuite(alwaysRun = true)
		public void testTearDown(ITestContext ctx) {
			RestAssured.reset();
			logger.info("\n\n");
			logger.info("Rest Assured framework has been reset because all tests have been executed.");
			logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
		} // end testTearDown

	}

