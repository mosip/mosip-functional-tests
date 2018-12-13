package mosip.api.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import mosip.api.util.ConfigManager;



/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */

public class BaseTestCase {
	
	// GLOBAL CLASS VARIABLES
		protected static Logger logger = LogManager.getLogger(BaseTestCase.class);
		
		public static ConfigManager configManager;
		public static ApplicationHealthCheck applicationHealthcheck;
		public static String Appln_Environment;
		public static String projectPathIDA;
		public static String projectPath;
		public static String projectPathKernel;
		public static String projectPathReg_proc;
		public static String projectPathPre_reg;

		/**
		 * Method that will take care of framework setup
		 */
		private static void initialize() {
			

			
			// Setup the Config Manager
			applicationHealthcheck = new ApplicationHealthCheck();
			
			

		}
		
		public static String BaseTestCase(String moduleType) throws IOException {
			// Setup the Config Manager
						configManager = new ConfigManager();
						
			Appln_Environment=configManager.getTestEnv();	
			initialize();
			Logger logger = LogManager.getLogger(BaseTestCase.class);
		
			if (Appln_Environment.equals("qa")) {
				
					
				switch(moduleType) {
				
				case "pre-reg":
					projectPath=(System.getProperty("user.dir")+ "/src/test/resources/xmlProjects/Pre_Registration-readyapi-project.xml");
					
					break;
					
				case "kernel":
					projectPath=(System.getProperty("user.dir")+ "/src/test/resources/xmlProjects/kernal-new.xml");
				break;	
				
				case "ida":
					projectPath = "";
					break;	
					
				
				case "reg-proc":
					projectPath = "";
					break;	
				
					
				
				
				}
					
					
			}
			
			// testEnvironment = "dev";
			return projectPath;
		}
		
		// ================================================================================================================
		// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
		// ================================================================================================================

		/**
		 * Before entire test suite we need to setup everything we will need.
		 */
		@BeforeSuite(alwaysRun = true)
		public void suiteSetup() {
			logger.info("Test Framework for mosip_qa Initialized");
			logger.info("Logging initialized: All logs are located at " + System.getProperty("user.dir")
					+ "/src/logs/mosip_qa-test.log");
			initialize();

			logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");
		} // End suiteSetup


}
