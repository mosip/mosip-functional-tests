package io.mosip.registration.test;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_HANLDER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.lang.reflect.Field;
import java.util.Properties;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
//import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DBUtil;
import io.mosip.registration.util.DbQueries;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Tabish Khan
 *
 *         Validating the Packet Handler Service is working as expected with
 *         valid inputs and outputs
 */
public class AuditServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	AuditManagerSerivceImpl auditManagerSerivceImpl;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	PacketHandlerService packetHandlerService;
	/**
	 * Class contains common methods used across various test cases
	 **/
	@Autowired
	CommonUtil commonUtil;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuditServiceTest.class);

	private static final String serviceName = "AuditService";
	private static final String subServiceName = "Validate_deleteAuditLogs";
	private static final String testDataFileName = "AuditLogTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	
	@BeforeClass(alwaysRun = true)
	public void preSetUp() {
		/**
		 * Pre SetUp for Sync, SessionContext, ApplicationContext
		 */
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@DataProvider(name = "AuditServiceDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "AuditServiceDataProvider", alwaysRun = true)

	public void validateAuditLogs(String testCaseName, JSONObject object) throws ParseException {
		LOGGER.info("AUDIT SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, testCaseName);
		mTestCaseName = testCaseName;

		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);
		
			if (!(testCaseName.equalsIgnoreCase("regClient_AuditService_delete_AuditLogs_configuredDaysAs_NULL"))) {

				DBUtil.updateValueInDB(prop.getProperty("Audit_log_deletion_configured_days"),
						DbQueries.UPDATE_AUDITLOG);
			}
			if (testCaseName.equalsIgnoreCase("regClient_AuditService_delete_AuditLogs_configuredDaysAs_futureDays")
					|| testCaseName
							.equalsIgnoreCase("regClient_AuditService_delete_AuditLogs_configuredDaysAs_currentDate")) {
				String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
						prop.getProperty("CreatePacket"));
				LOGGER.info("AUDIT SERVICE TEST - TEST DATA", APPLICATION_NAME, APPLICATION_ID,
						"StatusCode: " + statusCode);
				String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
						prop.getProperty("bioPath"));
				LOGGER.info("AUDIT SERVICE TEST - TEST DATA", APPLICATION_NAME, APPLICATION_ID,
						"Resident Biometric data Path: " + biometricDataPath);
				String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
						prop.getProperty("demoPath"));
				LOGGER.info("AUDIT SERVICE TEST - TEST DATA", APPLICATION_NAME, APPLICATION_ID,
						"Resident Demographic data Path: " + demographicDataPath);
				String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
						prop.getProperty("imagePath"));
				LOGGER.info("AUDIT SERVICE TEST - TEST DATA", APPLICATION_NAME, APPLICATION_ID,
						"Resident Proof data Path: " + proofImagePath);

				commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath, proofImagePath,
						System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
						prop.getProperty("invalidRegID"));
			}

			ApplicationContext.map().put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS,
					prop.getProperty("Audit_log_deletion_configured_days"));

			ResponseDTO response = auditManagerSerivceImpl.deleteAuditLogs();

			//commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), response);
		commonUtil.verifyAssertNotNull(response);
		
		
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
			f.set(baseTestMethod, AuditServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
