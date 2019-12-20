package io.mosip.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.testrunner.MosipTestRunner;
import io.mosip.util.PreRegistrationLibrary;
//import io.mosip.prereg.scripts.Create_PreRegistration;
import io.restassured.RestAssured;
/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */


public class BaseTestCase{

	protected static Logger logger = Logger.getLogger(BaseTestCase.class);
	
	public static List<String> preIds=new ArrayList<String> ();
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	protected static String individualToken;
	public String preRegAdminToken;
	protected static String regClientToken;
	public String regProcToken; 
	
	public String individualCookie=null;
	public String idaCookie=null;
	public String regProcCookie=null;
	public String regAdminCookie=null;
	public String registrationOfficerCookie=null;
	public String regSupervisorCookie=null;
	public String zonalAdminCookie=null;
	public String zonalApproverCookie=null; 
	public String adminCookie=null;
	public static KernelAuthentication kernelAuthLib = null;
	public static CommonLibrary kernelCmnLib = null;
	public static Map queries;
	public static HashMap<String, String> documentId=new HashMap<>();
	public static HashMap<String, String> regCenterId=new HashMap<>();
	public static String expiredPreId=null;
	public String batchJobToken=null;
	public static List<String> expiredPreRegIds=null;
	public static List<String> consumedPreRegIds=null;
	static PreRegistrationLibrary lib=new PreRegistrationLibrary();
	/**
	 * Method that will take care of framework setup
	 */
	// GLOBAL CLASS VARIABLES
	private Properties prop;
	public static String ApplnURI;	
	public static String authToken;
	public static String regProcAuthToken;
	public static String getStatusRegProcAuthToken;
	public static String environment;
	public static String testLevel;
	public static String adminRegProcAuthToken;
		public static String SEPRATOR="";
	public static String buildNumber="";
	public  static String getOSType(){
		String type=System.getProperty("os.name");
		if(type.toLowerCase().contains("windows")){
			SEPRATOR="\\\\";
			return "WINDOWS";
		}else if(type.toLowerCase().contains("linux")||type.toLowerCase().contains("unix"))
		{
			SEPRATOR="/";
			return "OTHERS";
		}
		return null;
	}
	
	
	
	public static void initialize()
	{
		copyDbInTarget();
		PropertyConfigurator.configure(getLoggerPropertyConfig());
		kernelAuthLib  = new KernelAuthentication();
		kernelCmnLib = new CommonLibrary();
		queries = kernelCmnLib.readProperty("adminQueries");
		/**
		 * Make sure test-output is there 
		 */
/*		File testOutput = new File("test-output");
		File oldReport = new File(System.getProperty("user.dir")+"/test-output/emailable-report.html");
		oldReport.delete();
		testOutput.mkdirs();*/
		
		getOSType();
		logger.info("We have created a Config Manager. Beginning to read properties!");
					                    
		environment = System.getProperty("env.user");
		logger.info("Environemnt is  ==== :" + environment);
		ApplnURI = System.getProperty("env.endpoint");
		logger.info("Application URI ======" + ApplnURI);
		testLevel = System.getProperty("env.testLevel");
		logger.info("Test Level ======" + testLevel);

		logger.info("Configs from properties file are set.");
		
	
	}

	
	// ================================================================================================================
		// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
		// ================================================================================================================


		/*
		 * Saving TestNG reports to be published
		 */
	   // @BeforeSuite(alwaysRun = true)
		public static void suiteSetup() {
		
			logger.info("Test Framework for Mosip api Initialized");
			logger.info("Logging initialized: All logs are located at " +  "src/logs/mosip-api-test.log");
			initialize();
			logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");

	   PreRegistrationLibrary pil=new PreRegistrationLibrary();
			pil.PreRegistrationResourceIntialize();
			/*new PreregistrationDAO().deleteAvailableSlot();*/
			new PreregistrationDAO().makeAllRegistartionCenterActive();
			AuthTestsUtil.removeOldMosipTempTestResource();
			AuthTestsUtil.initiateAuthTest();
			AdminTestUtil.initiateAdminTest();
			
			/**
			 * expiredPreRegIds list contain list of pre registration ids of yesterday date
			 * Here after booking appointment setting booking date to yesterday. 
			 */
			//expiredPreRegIds=lib.BookExpiredApplication();
			/**
			 * consumedPreRegIds list contain list of consumed pre registration ids 
			 * 
			 */
			expiredPreRegIds=lib.BookExpiredApplication();
			consumedPreRegIds=lib.consumedPreId();
			
			/**
			 * here we are assuming batch job will run in every 5 min thats why we are giving wait for 10 min
			 */
		logger.info("waiting for job run to start");
		try {
				TimeUnit.MINUTES.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//authToken=pil.getToken();
			/*htmlReporter=new ExtentHtmlReporter(System.getProperty("user.dir")+"/test-output/MyOwnReport.html");
			extent=new ExtentReports();
			extent.setSystemInfo("Build Number", buildNumber);
			extent.attachReporter(htmlReporter);*/

			
			/*htmlReporter.config().setDocumentTitle("MosipAutomationTesting Report");
			htmlReporter.config().setReportName("Mosip Automation Report");
			htmlReporter.config().setTheme(Theme.STANDARD);*/
			/*TokenGeneration generateToken = new TokenGeneration();
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			String tokenGenerationProperties = generateToken.readPropertyFile("syncTokenGenerationFilePath");
			tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
			regProcAuthToken = generateToken.getToken(tokenEntity);
			TokenGenerationEntity adminTokenEntity = new TokenGenerationEntity();
			String adminTokenGenerationProperties = generateToken.readPropertyFile("getStatusTokenGenerationFilePath");
			adminTokenEntity = generateToken.createTokenGeneratorDto(adminTokenGenerationProperties);
			adminRegProcAuthToken = generateToken.getToken(adminTokenEntity);*/
			


		} // End suiteSetup

		/**
		 * After the entire test suite clean up rest assured
		 */
		@AfterSuite(alwaysRun = true)
		public void testTearDown(ITestContext ctx) {
			
			
			/*Calling up PreReg DB clean Up step*/
			/*if(preIds.size()>=1)
			{
            logger.info("Elements from PreId List are========");
            for(String elem : preIds) {
            	logger.info(elem.toString());
            }
            boolean status=false;
           status=PreRegDbread.prereg_db_CleanUp(preIds);
            if(status)
           	 logger.info("PreId is deleted from the DB");
            else
                   logger.info("PreId is NOT deleted from the DB");
			}*/
			/*
			 * Saving TestNG reports to be published
			 */
			
			/*String currentModule = ctx.getCurrentXmlTest().getClasses().get(0).getName().split("\\.")[2];
			Runnable reporting  = ()->{
				reportMove(currentModule);	
			};
			new Thread(reporting).start();*/
			RestAssured.reset();
			logger.info("\n\n");
			logger.info("Rest Assured framework has been reset because all tests have been executed.");
			logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
			//extent.flush();
		} // end testTearDown

		private static Properties getLoggerPropertyConfig() {
			Properties logProp = new Properties();
			logProp.setProperty("log4j.rootLogger", "INFO, Appender1,Appender2");
			logProp.setProperty("log4j.appender.Appender1", "org.apache.log4j.ConsoleAppender");
			logProp.setProperty("log4j.appender.Appender1.layout", "org.apache.log4j.PatternLayout");
			logProp.setProperty("log4j.appender.Appender1.layout.ConversionPattern", "%-7p %d [%t] %c %x - %m%n");
			logProp.setProperty("log4j.appender.Appender2", "org.apache.log4j.FileAppender");
			logProp.setProperty("log4j.appender.Appender2.File", "src/logs/mosip-api-test.log");
			logProp.setProperty("log4j.appender.Appender2.layout", "org.apache.log4j.PatternLayout");
			logProp.setProperty("log4j.appender.Appender2.layout.ConversionPattern", "%-7p %d [%t] %c %x - %m%n");
			return logProp;
		}
	private static void copyDbInTarget() {
		File db=new File(MosipTestRunner.getGlobalResourcePath().substring(0,MosipTestRunner.getGlobalResourcePath().lastIndexOf("target"))+"/db");
		File targetDb=new File(db.getPath().replace("/db", "/target/db"));
		try {
			FileUtils.copyDirectory(db,targetDb);
			logger.info("Copied :: "+targetDb.getPath()+":: to target");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	}