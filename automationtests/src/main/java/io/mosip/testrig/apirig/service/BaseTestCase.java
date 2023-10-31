package io.mosip.testrig.apirig.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.nimbusds.jose.jwk.RSAKey;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.AuthTestsUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.dbaccess.DBManager;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.global.utils.GlobalMethods;
import io.mosip.testrig.apirig.kernel.util.CommonLibrary;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.kernel.util.KernelAuthentication;
import io.mosip.testrig.apirig.testrunner.MockSMTPListener;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */

public class BaseTestCase {

	protected static Logger logger = Logger.getLogger(BaseTestCase.class);
	protected static MockSMTPListener mockSMTPListener = null;
	public static List<String> preIds = new ArrayList<>();
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	protected static String individualToken;
	public String preRegAdminToken;
	protected static String regClientToken;
	public String regProcToken;
	public final String COOKIENAME = GlobalConstants.AUTHORIZATION;
	public final String IDTOKENCOOKIENAME = "id_token";
	public final String ACCESSTOKENCOOKIENAME = "access_token";
	public final String COOKIENAMESTATE = "state";
	public String individualCookie = null;
	public String idaCookie = null;
	public String idrepoCookie = null;
	public String regProcCookie = null;
	public String regProCookie = null;
	public String regAdminCookie = null;
	public String registrationOfficerCookie = null;
	public String regSupervisorCookie = null;
	public String zonalAdminCookie = null;
	public String zonalApproverCookie = null;
	public String adminCookie = null;
	public String partnerCookie = null;
	public String partnerNewCookie = null;
	public String partnerNewKycCookie = null;
	public String esignetPartnerCookie = null;
	public String policytestCookie = null;
	public String residentCookie = null;
	public HashMap<String, String> residentNewCookie = new HashMap<>();
	public HashMap<String, String> residentNewVidCookie = new HashMap<>();
	public String residentNewCookieKc = null;
	public String hotlistCookie = null;
	public String keycloakCookie = null;
	public String zonemapCookie = null;
	public String mobileAuthCookie = null;
	public String autoTstUsrCkie = null;
	public static String currentModule = GlobalConstants.MASTERDATA;
	public static String certsForModule = "DSL-IDA";
	public static List<String> listOfModules = null;
	public static List<String> languageList = new ArrayList<>();
	public static String languageCode = null;
	public static List<String> supportedIdType = new ArrayList<>();
	public static KernelAuthentication kernelAuthLib = null;
	public static CommonLibrary kernelCmnLib = null;
	public static Map<?, ?> queries;
	public static HashMap<String, String> documentId = new HashMap<>();
	public static HashMap<String, String> regCenterId = new HashMap<>();
	public static String expiredPreId = null;
	public String batchJobToken = null;
	public String invalidBatchJobToken = null;
	
	public static List<String> expiredPreRegIds = null;
	public static List<String> consumedPreRegIds = null;
	public static Map<?, ?> residentQueries;
	public static Map<?, ?> partnerQueries;
	public static boolean insertDevicedata = false;
	public static boolean proxy = true;

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
	public static List<String> t = new ArrayList<>();
	private static final char[] alphaNumericAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789"
			.toCharArray();
	private static final char[] alphabetsAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ"
			.toCharArray();
	private static final char[] nNumericAllowed = "0123456789".toCharArray();
	public static SecureRandom secureRandom = new SecureRandom();
	
	public static String hierarchyName = "";
	public static int hierarchyLevel = 0;
	public static String parentLocCode = "";
	
	public static String locationCode = "";
	public static String ZonelocationCode = "";
	public static String hierarchyZoneCode = "";
	
	
	
	public static String genRid = "27847" + generateRandomNumberString(10);

	public static String genPolicyNumber = "9" + generateRandomNumberString(5);
	public static String genRidDel = "2785" + generateRandomNumberString(10);
	public String genPolicyGroupDesc = "policyGroupForAutomationEsi" + generateRandomNumberString(6);
	public String genMispPolicyGroupDesc = "policyGroupForMispEsi" + generateRandomNumberString(6)
			+ generateRandomNumberString(3);
	public String genPolicyGroupName = "policyGroupNameForAutomationEsi" + generateRandomNumberString(5);
	public String genMispPolicyGroupName = "policyGroupNameForMispEsi" + generateRandomNumberString(6)
			+ generateRandomNumberString(3);
	public String genPolicyDesc = "policyDescForAutomationEsi" + generateRandomNumberString(5);
	public String genMispPolicyDesc = "policyDescForMispEsit" + generateRandomNumberString(6)
			+ generateRandomNumberString(3);
	public String genPolicyName = "policyNameForAutomationEsi" + generateRandomNumberString(4);
	public String genPolicyNameNonAuth = "policyNameForEsignet" + generateRandomNumberString(4);
	public String genMispPolicyName = "policyNameForMispEsi" + generateRandomNumberString(6)
			+ generateRandomNumberString(3);
	public static String genPartnerName = "partnernameforautomationesi-" + generateRandomNumberString(6);
	public static String genPartnerNameNonAuth = "partnernameforesignet-" + generateRandomNumberString(6);
	public String genPartnerNameForDsl = "partnernameforautomationesi-" + generateRandomNumberString(6);
	public static String genMispPartnerName = "esignet_" + generateRandomNumberString(6)
			+ generateRandomNumberString(3);
	public static String genPartnerEmail = "automationpartneresi" + generateRandomNumberString(7)
			+ "@automationMosip.com";
	public String genPartnerEmailForDsl = "automationpartneresi" + generateRandomNumberString(10)
	+ "@automationMosip.com";
	public String genPartnerEmailNonAuth = "automationesignet" + generateRandomNumberString(10)
	+ "@automationMosip.com";
	public String genMispPartnerEmail = "misppartner" + generateRandomNumberString(4)
			+ generateRandomNumberString(4) + "@automationMosip.com";
	public static String publickey;
	public static RSAKey rsaJWK;
	public static String clientAssertionToken;
	private static String zoneMappingRequest = "config/Authorization/zoneMappingRequest.json";
	public static Properties props = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/application.properties");
	public static Properties propsKernel = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/Kernel.properties");
	
	public static String currentRunningLanguage = "";

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

		logger.info("Test Level ======" + languageList);

		logger.info("Configs from properties file are set.");

	}
	
	


	public static void suiteSetup() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
		File logFile = new File("./src/logs/mosip-api-test.log");
		if (logFile.exists())
			try {
				FileUtils.forceDelete(logFile);
			} catch (IOException e1) {
				logger.error("Failed to delete old log file");
			}
		logger.info("Test Framework for Mosip api Initialized");
		logger.info("Logging initialized: All logs are located at " + "src/logs/mosip-api-test.log");
		initialize();
		logger.info("Done with BeforeSuite and test case setup! su TEST EXECUTION!\n\n");

		String[] modulesSpecified = System.getProperty("modules").split(",");
		listOfModules = new ArrayList<String>(Arrays.asList(modulesSpecified));
		if (!MosipTestRunner.checkRunType().equalsIgnoreCase("JAR")) {
			AuthTestsUtil.removeOldMosipTempTestResource();
		}
		if (listOfModules.contains("auth")) {
			setReportName("auth");
			BaseTestCase.currentModule = "auth";
			BaseTestCase.certsForModule = "IDA";
			AuthTestsUtil.initiateAuthTest();
			
			mockSMTPListener = new MockSMTPListener();
			mockSMTPListener.run();
		}
		if (listOfModules.contains("idrepo")) {
			setReportName("idrepo");
			BaseTestCase.currentModule = "idrepo";
			AdminTestUtil.copyIdrepoTestResource();
		}

		if (listOfModules.contains(GlobalConstants.MASTERDATA)) {
			DBManager.clearMasterDbData();
			BaseTestCase.currentModule = GlobalConstants.MASTERDATA;
			setReportName(GlobalConstants.MASTERDATA);
			AdminTestUtil.initiateMasterDataTest();
		}

		if (listOfModules.contains(GlobalConstants.MOBILEID)) {
			BaseTestCase.currentModule = GlobalConstants.MOBILEID;
			setReportName(GlobalConstants.MOBILEID);
			AdminTestUtil.initiateMobileIdTestTest();
			mockSMTPListener = new MockSMTPListener();
			mockSMTPListener.run();

		}

		if (listOfModules.contains(GlobalConstants.ESIGNET)) {

			BaseTestCase.currentModule = GlobalConstants.ESIGNET;
			BaseTestCase.certsForModule = GlobalConstants.ESIGNET;
			setReportName(GlobalConstants.ESIGNET);
			AdminTestUtil.initiateesignetTest();
			mockSMTPListener = new MockSMTPListener();
			mockSMTPListener.run();

		}
		if (listOfModules.contains(GlobalConstants.RESIDENT)) {
			BaseTestCase.currentModule = GlobalConstants.RESIDENT;
			setReportName(GlobalConstants.RESIDENT);
			AdminTestUtil.copyResidentTestResource();
		    mockSMTPListener = new MockSMTPListener();
			mockSMTPListener.run();
		}
		if (listOfModules.contains("partner")) {
			BaseTestCase.currentModule = "partner";
			DBManager.clearPMSDbData();
			DBManager.clearKeyManagerDbData();
			BaseTestCase.currentModule = "partner";
			setReportName("partner");
			AdminTestUtil.copyPartnerTestResource();
		}
		if (listOfModules.contains(GlobalConstants.PREREG)) {
			BaseTestCase.currentModule = GlobalConstants.PREREG;
			setReportName(GlobalConstants.PREREG);
			AdminTestUtil.copyPreregTestResource();
			mockSMTPListener = new MockSMTPListener();
			mockSMTPListener.run();

		}
	}

	public static void setReportName(String moduleName) {
		System.getProperties().setProperty("emailable.report2.name",
				"mosip-" + environment + "-" + moduleName + "-" + System.currentTimeMillis() + "-report.html");
	}

	/**
	 * After the entire test suite clean up rest assured
	 */
	@AfterSuite(alwaysRun = true)
	public void testTearDown(ITestContext ctx) {
		RestAssured.reset();
		copyReportAndLog();
		logger.info("\n\n");
		logger.info("Rest Assured framework has been reset because all tests have been executed.");
		logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
	} 

	public static Properties getproperty(String path) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}

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

	private void copyReportAndLog() {
		String folderForReport = kernelCmnLib.readProperty("Kernel").get("reportLogPath");
		String dirToReport = System.getProperty("user.home") + "/" + folderForReport;
		File dest = new File(dirToReport);
		if (!dest.exists())
			dest.mkdir();

		String os = System.getProperty("os.name");
		String projDirPath = null;
		if (MosipTestRunner.checkRunType().contains("IDE") || os.toLowerCase().contains("windows"))
			projDirPath = System.getProperty("user.dir");
		else
			projDirPath = new File(System.getProperty("user.dir")).getParent();

		File reportFolder = new File(projDirPath + "/testng-report");
		File logFolder = new File(projDirPath + "/src/logs");

		try {
			if (dest.listFiles().length != 0)
				FileUtils.cleanDirectory(dest);
			FileUtils.copyDirectoryToDirectory(reportFolder, dest);
			FileUtils.copyDirectoryToDirectory(logFolder, dest);
		} catch (Exception e) {
			logger.info("Not able to store the log and report at the specified path: " + dirToReport);
			logger.error(e.getMessage());
		}
		logger.info("Copied the logs and reports successfully in folder: " + dirToReport);
	}

	@SuppressWarnings("unchecked")
	public static void mapUserToZone() {

		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(zoneMappingRequest);
		JSONObject request = new JSONObject();
		request.put("zoneCode", hierarchyZoneCode);
		request.put("userId", BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());
		request.put("langCode", BaseTestCase.getLanguageList().get(0));
		request.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		actualrequest.put(GlobalConstants.REQUEST, request);
		logger.info(actualrequest);
		Response response = RestClient.postRequestWithCookie(url, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(ConfigManager.getUserAdminName() + "Mapped to" + props.get("zoneCode_to_beMapped") + "Zone");
		logger.info(response);

	}

	@SuppressWarnings("unchecked")
	public static void mapUserToZone(String user, String zone) {

		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(zoneMappingRequest);
		JSONObject request = new JSONObject();
		request.put("zoneCode", ZonelocationCode);
		request.put("userId", user);
		request.put("langCode", BaseTestCase.getLanguageList().get(0));
		request.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		actualrequest.put(GlobalConstants.REQUEST, request);
		logger.info(actualrequest);
		Response response = RestClient.postRequestWithCookie(url, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(user + "Mapped to" + zone + "Zone");
		logger.info(response);

	}

	public static void mapZone() {

		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingActivateUrl");
		HashMap<String, String> map = new HashMap<>();
		map.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		map.put("userId", BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);
	}

	public static void mapZone(String user) {

		String token = kernelAuthLib.getTokenByRole("globalAdmin");
		String url = ApplnURI + propsKernel.getProperty("zoneMappingActivateUrl");
		HashMap<String, String> map = new HashMap<>();
		map.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		map.put("userId", user);
		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);
	}

	public static boolean zoneName() {
		boolean firstUser = true;
		String token = kernelAuthLib.getTokenByRole("admin");
		String url = ApplnURI + propsKernel.getProperty("zoneNameUrl");

		HashMap<String, String> map = new HashMap<>();

		map.put("userID", BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());
		map.put("langCode", BaseTestCase.getLanguageList().get(0));

		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);

		String otpInput = response.getBody().asString();
		if (otpInput.contains("KER-MSD-391")) {
			firstUser = false;
		}
		return firstUser;

	}

	public static void userCenterMapping() {

		String token = kernelAuthLib.getTokenByRole("admin");
		String url = ApplnURI + propsKernel.getProperty("userCenterMappingUrl");

		HashMap<String, String> requestMap = new HashMap<>();

		requestMap.put("id", BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());
		requestMap.put("name", "automation");
		requestMap.put("statusCode", "active");
		// TODO remove hardcoding
		requestMap.put("regCenterId", "10005");
		requestMap.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		requestMap.put("langCode", "eng");

		HashMap<String, Object> map = new HashMap<>();

		map.put("id", GlobalConstants.STRING);
		map.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		map.put(GlobalConstants.REQUESTTIME, AdminTestUtil.generateCurrentUTCTimeStamp());
		map.put(GlobalConstants.METADATA, new HashMap<>());
		map.put(GlobalConstants.REQUEST, requestMap);

		Response response = RestClient.postRequestWithCookie(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);
	}

	public static void userCenterMappingStatus() {

		String token = kernelAuthLib.getTokenByRole("admin");
		String url = ApplnURI + propsKernel.getProperty("userCenterMappingUrl");

		HashMap<String, String> map = new HashMap<>();

		map.put(GlobalConstants.ISACTIVE, GlobalConstants.TRUE_STRING);
		map.put("id", BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());

		Response response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);
	}
	
	public static JSONArray idaActuatorResponseArray = null;
	
	public static String getValueFromActuators(String endPoint, String section, String key) {

		String url = ApplnURI + endPoint;
		String value = null;
		try {
			if (idaActuatorResponseArray == null) {
				Response response = null;
				org.json.JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

				responseJson = new org.json.JSONObject(response.getBody().asString());
				idaActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}
			logger.info("idaActuatorResponseArray="+idaActuatorResponseArray);

			for (int i = 0, size = idaActuatorResponseArray.length(); i < size; i++) {
				org.json.JSONObject eachJson = idaActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(section)) {
					value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
							.get(GlobalConstants.VALUE).toString();
					logger.info("value="+value);
					break;
				}
			}

			return value;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return value;
		}

	}

	public static List<String> getLanguageList() {
		if (!languageList.isEmpty()) {
			return languageList;
		}
		String section = "";
		String optionalLanguages=null;
		String mandatoryLanguages=null;
		if (isTargetEnvLTS()) 
			section = "/mosip-config/application-default.properties";
		else 
			section = "/mosip-config/sandbox/admin-mz.properties";
		try {
	
			optionalLanguages = getValueFromActuators(propsKernel.getProperty("actuatorAdminEndpoint"),
				section, "mosip.optional-languages");
			
			logger.info("optionalLanguages from env:" + optionalLanguages);
			
			mandatoryLanguages = getValueFromActuators(propsKernel.getProperty("actuatorAdminEndpoint"),
				section, "mosip.mandatory-languages");
			
			logger.info("mandatoryLanguages from env:" + mandatoryLanguages);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if (mandatoryLanguages != null && !mandatoryLanguages.isBlank())
			languageList.addAll(Arrays.asList(mandatoryLanguages.split(",")));
		
		if (optionalLanguages != null && !optionalLanguages.isBlank())
			languageList.addAll(Arrays.asList(optionalLanguages.split(",")));
		logger.info("languageList from env:" + languageList);
		return languageList;
	}
	
	private static String targetEnvVersion = "";
	
	public static boolean isTargetEnvLTS() {

		if (targetEnvVersion.isEmpty()) {

			Response response = null;
			org.json.JSONObject responseJson = null;
			String url = ApplnURI + propsKernel.getProperty("auditActuatorEndpoint");
			try {
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

				responseJson = new org.json.JSONObject(response.getBody().asString());

				targetEnvVersion = responseJson.getJSONObject("build").getString("version");

			} catch (Exception e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			}
		}
		return targetEnvVersion.contains("1.2");
	}

	public static List<String> getSupportedIdTypesValueFromActuator() {

		if (!supportedIdType.isEmpty()) {
			return supportedIdType;
		}
		
		String section = "/mosip-config/id-authentication-default.properties";
		if (!BaseTestCase.isTargetEnvLTS())
			section = "/mosip-config/sandbox/id-authentication-lts.properties";
		
		Response response = null;

		org.json.JSONObject responseJson = null;
		JSONArray responseArray = null;
		String url = ApplnURI + propsKernel.getProperty("actuatorIDAEndpoint");
		try {
			response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			responseJson = new org.json.JSONObject(response.getBody().asString());
			responseArray = responseJson.getJSONArray("propertySources");

			for (int i = 0, size = responseArray.length(); i < size; i++) {
				org.json.JSONObject eachJson = responseArray.getJSONObject(i);
				logger.info("eachJson is :" + eachJson.toString());
				if (eachJson.get("name").toString().contains(section)) {
					org.json.JSONObject idTypes = (org.json.JSONObject) eachJson.getJSONObject("properties")
							.get("request.idtypes.allowed");
					String newIdTypes = idTypes.getString(GlobalConstants.VALUE);

					supportedIdType.addAll(Arrays.asList((newIdTypes).split(",")));

					break;
				}
			}

			return supportedIdType;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return supportedIdType;
		}

	}

	public static JSONObject getRequestJson(String filepath) {
		return kernelCmnLib.readJsonData(filepath, true);

	}
	public static String generateRandomAlphaNumericString(int length) {
		StringBuilder alphaNumericString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			alphaNumericString.append(alphaNumericAllowed[secureRandom.nextInt(alphaNumericAllowed.length)]);
		}
		return alphaNumericString.toString();
	}
	
	public static String generateRandomAlphabeticString(int length) {
		StringBuilder alphaNumericString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			alphaNumericString.append(alphabetsAllowed[secureRandom.nextInt(alphabetsAllowed.length)]);
		}
		return alphaNumericString.toString();
	}

	public static String generateRandomNumberString(int length) {
		StringBuilder numericString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			numericString.append(nNumericAllowed[secureRandom.nextInt(nNumericAllowed.length)]);
		}
		return numericString.toString();
	}
}
