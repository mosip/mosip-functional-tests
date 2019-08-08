package io.mosip.registration.tests;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Leona Mary S
 *
 *         Validating the Storage Service is working as expected with valid
 *         inputs and outputs
 */

public class StorageServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;

	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	private static final Logger logger = AppConfig.getLogger(StorageServiceTest.class);
	private static final String serviceName = "StorageService";
	private static final String subServiceName = "StoreToDisk";
	private static final String testDataFileName = "StorageServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@DataProvider(name = "StorageServiceDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "StorageServiceDataProvider", alwaysRun = true)
	public void validateStoreToDisk(String testCaseName, JSONObject object) {
		try {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName = testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);

		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG, prop.getProperty("eodConfigFlag"));

		if (testCaseName.equalsIgnoreCase("regClient_StorageService_NewFolderToStore_Packet")) {
			logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Directory is created!");
			ApplicationContext.getInstance().map().put(RegistrationConstants.PACKET_STORE_LOCATION,
					prop.getProperty("path"));

		} else if (testCaseName.equalsIgnoreCase("regClient_StorageService_NoFolderToStore_Packet")) {
			ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION, "");
		}
		ArrayList<String> roles = new ArrayList<>();
		for (String role : prop.getProperty("roles").split(","))
			roles.add(role);
		SessionContext.userContext().setRoles(roles);

		String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
				prop.getProperty("CreatePacket"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"StatusCode: " + statusCode);
		String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
				prop.getProperty("bioPath"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Resident Biometric data Path: " + biometricDataPath);
		String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
				prop.getProperty("demoPath"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Resident Demographic data Path: " + demographicDataPath);
		String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
				prop.getProperty("imagePath"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Resident Proof data Path: " + proofImagePath);

		HashMap<String, String> packetresponse;

		packetresponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath, proofImagePath,
				System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
				prop.getProperty("invalidRegID"));

		Assert.assertEquals(commonUtil.verifyIfFileExist(packetresponse.get("FILEPATH")),
				Boolean.parseBoolean(prop.getProperty("ExpectedResponse")));
		
	}

	catch (Exception exception) {
		logger.debug("STORAGE SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
		Reporter.log(ExceptionUtils.getStackTrace(exception));
	}

	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, StorageServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
