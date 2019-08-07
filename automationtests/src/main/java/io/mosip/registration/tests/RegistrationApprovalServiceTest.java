/**
 * 
 */
package io.mosip.registration.tests;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Gaurav Sharan
 *
 */
public class RegistrationApprovalServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	private RegistrationApprovalService registrationApprovalService;

	@Autowired
	PacketSynchService packetSynchService;

	@Autowired
	PacketUploadService packetUploadService;

	@Autowired
	Environment env;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	RegistrationRepository registrationRepository;

	@Autowired
	private RegistrationDAO syncRegistrationDAO;

	private static final String serviceName = "RegistrationApprovalService";
	private static final String testDataFileName = "RegistrationApprovalServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static final Logger logger = AppConfig.getLogger(PacketHandlerServiceTest.class);

	private static String centerID = null;
	private static String stationID = null;

	@BeforeMethod
	public void setTestCaseName() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@DataProvider(name = "enrolmentByStatus")
	public Object[][] enrolmentDataSource() {

		String testName = "getEnrolment";
		String testcasedir = serviceName + "/" + testName;
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] testCases = new Object[5][];
		switch ("default") {
		case "smoke":
			testCases = testCaseReader.readTestCases(testcasedir, "smoke");

		case "regression":
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "regression");
		default:
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "smokeAndRegression");
		}
		return testCases;
	}

	@Test(dataProvider = "enrolmentByStatus", alwaysRun = true)
	public void getEnrollmentByStatusTest(String testcaseName, JSONObject object) {
		try {
			commonUtil();
			String subServiceName = "getEnrolment";
			// logger.info("Test case: " + testcaseName);
			mTestCaseName = testcaseName;
			System.out.println("testcaseName=== " + testcaseName);
			String testCasePath = serviceName + "/" + subServiceName;
			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
			String statusCodeCase = prop.getProperty("statusCode");
			String status = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode", statusCodeCase);
			HashMap<String, String> packetresponse = new HashMap<String, String>();
			if (testcaseName
					.equalsIgnoreCase("regClient_RegistrationApprovalService_getEnrolment_validateEnrolment_smoke")) {
				SessionContext.map().put(RegistrationConstants.IS_Child,
						Boolean.parseBoolean(prop.getProperty("isChild")));
				ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG,
						prop.getProperty("eodConfigFlag"));
				ArrayList<String> roles = new ArrayList<>();
				for (String role : prop.getProperty("roles").split(","))
					roles.add(role);
				SessionContext.userContext().setRoles(roles);

				String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
						prop.getProperty("CreatePacket"));
				logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
						"StatusCode: " + statusCode);
				String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
						prop.getProperty("bioPath"));
				logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
						"Resident Biometric data Path: " + biometricDataPath);
				String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
						prop.getProperty("demoPath"));
				logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
						"Resident Demographic data Path: " + demographicDataPath);
				String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
						prop.getProperty("imagePath"));
				logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
						"Resident Proof data Path: " + proofImagePath);

				packetresponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
						proofImagePath, System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
						prop.getProperty("invalidRegID"));
				commonUtil.verifyAssertionResponse(prop.getProperty("PacketResponse"),
						packetresponse.get("SUCCESSRESPONSE"));
				// Update UPD_DTIMES
				Registration registration = syncRegistrationDAO.getRegistrationById(statusCode,
						packetresponse.get("RANDOMID"));
				registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
				registrationRepository.update(registration);
			}
			List<RegistrationApprovalDTO> list = new ArrayList<>();
			list = registrationApprovalService.getEnrollmentByStatus(status);
			if (testcaseName.contains("invalid")) {
				assertFalse(list.size() > 0);
			} else {
				assertTrue(list.size() > 0);
			}
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@DataProvider(name = "updateRegistration")
	public Object[][] registrationDataSource() {

		String testName = "updateRegistration";
		String testcasedir = serviceName + "/" + testName;
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] testCases = new Object[5][];
		switch ("default") {
		case "smoke":
			testCases = testCaseReader.readTestCases(testcasedir, "smoke");

		case "regression":
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "regression");
		default:
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "smokeAndRegression");
		}
		return testCases;
	}

	@Test(dataProvider = "updateRegistration", alwaysRun = true)
	public void updateRegistrationTest(String testcaseName, JSONObject object) {
		commonUtil();
		String subServiceName = "updateRegistration";
		// logger.info("Test case: " + testcaseName);
		mTestCaseName = testcaseName;
		System.out.println("testcaseName=== " + testcaseName);
		String testCasePath = serviceName + "/" + subServiceName;
		Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
		String statusCodeCase = prop.getProperty("statusCode");

		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG, prop.getProperty("eodConfigFlag"));
		ArrayList<String> roles = new ArrayList<>();
		for (String role : prop.getProperty("roles").split(","))
			roles.add(role);
		SessionContext.userContext().setRoles(roles);

		String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
				prop.getProperty("CreatePacket"));
		logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, "StatusCode: " + statusCode);
		String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
				prop.getProperty("bioPath"));
		logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
				"Resident Biometric data Path: " + biometricDataPath);
		String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
				prop.getProperty("demoPath"));
		logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
				"Resident Demographic data Path: " + demographicDataPath);
		String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
				prop.getProperty("imagePath"));
		logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
				"Resident Proof data Path: " + proofImagePath);
		HashMap<String, String> packetresponse;

		packetresponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath, proofImagePath,
				System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
				prop.getProperty("invalidRegID"));
		commonUtil.verifyAssertionResponse(prop.getProperty("PacketResponse"), packetresponse.get("SUCCESSRESPONSE"));

		// Update UPD_DTIMES
		Registration registration = syncRegistrationDAO.getRegistrationById(statusCode, packetresponse.get("RANDOMID"));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationRepository.update(registration);

		String originalStatus = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode", statusCodeCase);
		String NEW_STATUS = "APPROVED";
		// Test logic
		RegistrationApprovalDTO obj = registrationApprovalService.getEnrollmentByStatus(originalStatus).get(0);
		String registrationID = obj.getId();
		String statusComment = obj.getStatusComment();
		try {
			Registration updatedRegistration = registrationApprovalService.updateRegistration(registrationID, "updating",
					NEW_STATUS);
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<RegistrationApprovalDTO> list = new ArrayList<>();
		list = registrationApprovalService.getEnrollmentByStatus("REGISTERED");
		assertFalse(list.contains(obj));

		boolean passed = false;

		list = registrationApprovalService.getEnrollmentByStatus("APPROVED");
		for (RegistrationApprovalDTO objUpdated : list) {
			if (objUpdated.getId().compareTo(registrationID) == 0) {
				passed = true;
				break;
			}
		}

		assertTrue(passed);

		try {
			registrationApprovalService.updateRegistration(registrationID, statusComment, "REGISTERED");
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void commonUtil() {
		List<String> roles = new ArrayList<>();
		String[] roleset = ConstantValues.REREGISTRATION_ROLES.split(",");
		if (roleset.length != 2) {
			fail("Number of roles wrongly configured, needs to be 2");
			return;
		}
		roles.add(roleset[0]);
		roles.add(roleset[1]);
		SessionContext.getInstance().getUserContext().setUserId(System.getProperty("userID"));
		SessionContext.getInstance().getUserContext().setRoles(roles);
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
			f.set(baseTestMethod, RegistrationApprovalServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
