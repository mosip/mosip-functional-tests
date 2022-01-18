package io.mosip.registration.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.repositories.AuditLogControlRepository;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.packet.impl.RegPacketStatusServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class RegPacketStatusServiceSyncPacketsTest extends BaseConfiguration implements ITest {

	@Autowired
	private RegistrationDAO registrationDAO;
	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	private RegPacketStatusServiceImpl regPacketStatusServiceImpl;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private AuditLogControlRepository auditLogControlRepository;
	@Autowired
	private RegTransactionRepository regTransactionRepository;
	@Autowired
	RegPacketStatusService regPacketStatusService;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	Environment env;
	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	private static final Logger logger = AppConfig.getLogger(RegPacketStatusServiceSyncPacketsTest.class);
	private static final String serviceName = "RegPacketStatusService";
	private static final String subServiceName = "syncPackets";
	private static final String testDataFileName = "RegPacketStatusServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	@BeforeMethod
	public void setup() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
		ApplicationContext context = ApplicationContext.getInstance();
		context.loadResourceBundle();
		Map<String, Object> map = globalParamService.getGlobalParams();
		map.put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS, "120");
		context.setApplicationMap(map);

	}

	@DataProvider(name = "RegPacketStatusDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "RegPacketStatusDataProvider", alwaysRun = true)
	public void validatePacketCreation(String testCaseName, JSONObject object) {
		try {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName = testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);

		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG, prop.getProperty("eodConfigFlag"));
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

		ResponseDTO response = new ResponseDTO();

		response = regPacketStatusServiceImpl.syncPacket(prop.getProperty("triggerPoint"));
		System.out.println("========== "+response.getSuccessResponseDTO().getMessage());
		commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), response);
		
		}

		catch (Exception exception) {
			logger.debug("REG PACKET STATUS SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, RegPacketStatusServiceSyncPacketsTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
