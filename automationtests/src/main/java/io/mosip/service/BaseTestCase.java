package io.mosip.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.testrunner.MosipTestRunner;
//import io.mosip.prereg.scripts.Create_PreRegistration;
import io.restassured.RestAssured;

/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */

public class BaseTestCase {

	protected static Logger logger = Logger.getLogger(BaseTestCase.class);

	public static List<String> preIds = new ArrayList<String>();
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	protected static String individualToken;
	public String preRegAdminToken;
	protected static String regClientToken;
	public String regProcToken;
	public final String COOKIENAME = "Authorization";
	public String individualCookie = null;
	public String idaCookie = null;
	public String idrepoCookie = null;
	public String regProcCookie = null;
	public String regAdminCookie = null;
	public String registrationOfficerCookie = null;
	public String regSupervisorCookie = null;
	public String zonalAdminCookie = null;
	public String zonalApproverCookie = null;
	public String adminCookie = null;
	public String partnerCookie = null;
	public String residentCookie = null;
	public String hotlistCookie = null;
	public String keycloakCookie = null;
	public String autoTstUsrCkie = null;
	public static List<String> listOfModules = null;

	public static KernelAuthentication kernelAuthLib = null;
	public static CommonLibrary kernelCmnLib = null;
	public static Map queries;
	public static HashMap<String, String> documentId = new HashMap<>();
	public static HashMap<String, String> regCenterId = new HashMap<>();
	public static String expiredPreId = null;
	public String batchJobToken = null;
	public static List<String> expiredPreRegIds = null;
	public static List<String> consumedPreRegIds = null;
	//static PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public static Map residentQueries;
	public static Map partnerQueries;
	public static boolean insertDevicedata = false;
	public static boolean proxy = true;
	/**
	 * Method that will take care of framework setup
	 */
	// GLOBAL CLASS VARIABLES

	public static String ApplnURI;
	public static String ApplnURIForKeyCloak;
	public static String authToken;
	public static String regProcAuthToken;
	public static String getStatusRegProcAuthToken;
	public static String environment;
	public static String testLevel;
	public static String adminRegProcAuthToken;
	public static String SEPRATOR = "";
	public static String buildNumber = "";
	public static List<String> languageList = new ArrayList<>();
	public static String genRid = "27847" + RandomStringUtils.randomNumeric(10);
	public static String genRidDel = "2785" + RandomStringUtils.randomNumeric(10);
	//public static HashMap<String, String> langcode = new HashMap<>();
	public static String publickey;

	public static String getOSType() {
		String type = System.getProperty("os.name");
		if (type.toLowerCase().contains("windows")) {
			SEPRATOR = "\\\\";
			return "WINDOWS";
		} else if (type.toLowerCase().contains("linux") || type.toLowerCase().contains("unix")) {
			SEPRATOR = "/";
			return "OTHERS";
		}
		return null;
	}

	public static void initialize() {
		PropertyConfigurator.configure(getLoggerPropertyConfig());
		kernelAuthLib = new KernelAuthentication();
		kernelCmnLib = new CommonLibrary();
		queries = kernelCmnLib.readProperty("adminQueries");
		partnerQueries = kernelCmnLib.readProperty("partnerQueries");
		residentQueries = kernelCmnLib.readProperty("residentServicesQueries");
		/**
		 * Make sure test-output is there
		 */

		getOSType();
		logger.info("We have created a Config Manager. Beginning to read properties!");

		environment = System.getProperty("env.user");
		logger.info("Environemnt is  ==== :" + environment);
		ApplnURI = System.getProperty("env.endpoint");
		logger.info("Application URI ======" + ApplnURI);
		ApplnURIForKeyCloak = System.getProperty("env.keycloak");
		logger.info("Application URI ======" + ApplnURIForKeyCloak);
		testLevel = System.getProperty("env.testLevel");
		logger.info("Test Level ======" + testLevel);
		languageList =Arrays.asList(System.getProperty("env.langcode").split(","));
		
		//langcode = System.getProperty("env.langcode");
		logger.info("Test Level ======" + languageList);

		logger.info("Configs from properties file are set.");

	}

	// ================================================================================================================
	// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
	// ================================================================================================================

	/*
	 * Saving TestNG reports to be published
	 */


	public static void suiteSetup() {
		File logFile = new File("./src/logs/mosip-api-test.log");
		if (logFile.exists())
			try {
				FileUtils.forceDelete(logFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.error("Failed to delete old log file");
			}
		logger.info("Test Framework for Mosip api Initialized");
		logger.info("Logging initialized: All logs are located at " + "src/logs/mosip-api-test.log");
		initialize();
		logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");

		String[] modulesSpecified = System.getProperty("modules").split(",");
		listOfModules = new ArrayList<String>(Arrays.asList(modulesSpecified));
		AuthTestsUtil.removeOldMosipTempTestResource();
		if (listOfModules.contains("auth") || listOfModules.contains("all")) {
			AuthTestsUtil.initiateAuthTest();
			//new PMPDataManager(true);
		}
		if (listOfModules.contains("idrepo") || listOfModules.contains("all")) {
			AdminTestUtil.copyIdrepoTestResource();
		}
		if (listOfModules.contains("admin") || listOfModules.contains("all")) {
			AdminTestUtil.initiateAdminTest();
		}
		if (listOfModules.contains("masterdata") || listOfModules.contains("all")) {
			AdminTestUtil.initiateMasterDataTest();
		}
		if (listOfModules.contains("syncdata") || listOfModules.contains("all")) {
			AdminTestUtil.initiateSyncDataTest();
		}
		if (listOfModules.contains("resident") || listOfModules.contains("all")) {
			AdminTestUtil.copyResidentTestResource();
		}
		if (listOfModules.contains("partner") || listOfModules.contains("all")) {
			AdminTestUtil.copyPartnerTestResource();
		}
		if (listOfModules.contains("kernel") || listOfModules.contains("all")) {
			AdminTestUtil.initiateKernelTest();
		}
		if (listOfModules.contains("regproc") || listOfModules.contains("all")) {
			AdminTestUtil.initiateregProcTest();
		}
		if (listOfModules.contains("prereg") || listOfModules.contains("all")) {
			AdminTestUtil.copyPreregTestResource();
			
		}
		if (listOfModules.contains("prerequisite")) {
			AdminTestUtil.copyPrerequisiteTestResource();
			
		}
		
		//inserting device management data
		/*
		 * if(insertDevicedata) { AuthTestsUtil.deleteDeviceManagementData();
		 * logger.info("Inserting device management data");
		 * AuthTestsUtil.createDeviceManagementData(); }
		 */

	} 


	/**
	 * After the entire test suite clean up rest assured
	 */
	@AfterSuite(alwaysRun = true)
	public void testTearDown(ITestContext ctx) {
		String testsuite = ctx.getCurrentXmlTest().getSuite().getName();
		/*
		 * if(testsuite.contains("AuthenticationTest")) { new PMPDataManager(false);
		 * AuthTestsUtil.deleteDeviceManagementData(); } else
		 * if(testsuite.equalsIgnoreCase("Mosip API Suite")) { new
		 * PMPDataManager(false); AuthTestsUtil.deleteDeviceManagementData(); }
		 */
		RestAssured.reset();
		copyReportAndLog();
		logger.info("\n\n");
		logger.info("Rest Assured framework has been reset because all tests have been executed.");
		logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
		
		// extent.flush();
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
		File db = new File(MosipTestRunner.getGlobalResourcePath().substring(0,
				MosipTestRunner.getGlobalResourcePath().lastIndexOf("target")) + "/db");
		File targetDb = new File(db.getPath().replace("/db", "/target/db"));
		try {
			FileUtils.copyDirectory(db, targetDb);
			logger.info("Copied :: " + targetDb.getPath() + ":: to target");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void copyReportAndLog()
	{
		String folderForReport = kernelCmnLib.readProperty("Kernel").get("reportLogPath");
		String dirToReport = System.getProperty("user.home")+"/"+folderForReport;
		File dest = new File(dirToReport);
		if(!dest.exists())
			dest.mkdir();
		
		String os=System.getProperty("os.name");
		String projDirPath = null;
		 if(MosipTestRunner.checkRunType().contains("IDE") || os.toLowerCase().contains("windows")==false) 
			 projDirPath = System.getProperty("user.dir");
		else 
			projDirPath=new File(System.getProperty("user.dir")).getParent();
		 
		File reportFolder = new File(projDirPath+"/testng-report");
		File logFolder = new File(projDirPath+"/src/logs");
		
		try {
			if(dest.listFiles().length!=0)
			FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectoryToDirectory(reportFolder, dest);
			FileUtils.copyDirectoryToDirectory(logFolder, dest);
		} catch (Exception e) {
			logger.info("Not able to store the log and report at the specified path: "+dirToReport);
			logger.error(e.getMessage());
		}
		logger.info("Copied the logs and reports successfully in folder: "+dirToReport);
	}
	
	public static JSONObject getRequestJson(String filepath){
		return kernelCmnLib.readJsonData(filepath, true);
		
	}
}
