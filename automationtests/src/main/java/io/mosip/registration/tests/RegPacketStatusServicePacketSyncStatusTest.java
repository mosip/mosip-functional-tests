package io.mosip.registration.tests;

import static io.mosip.kernel.core.util.DateUtils.formatDate;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
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
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class RegPacketStatusServicePacketSyncStatusTest extends BaseConfiguration implements ITest {

	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	RegPacketStatusService regPacketStatusService;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	Environment env;
	@Autowired
	PacketUploadService packetUploadService;
	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;
	@Autowired
	PacketSynchService packetSyncService;

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	private static final Logger logger = AppConfig.getLogger(RegPacketStatusServicePacketSyncStatusTest.class);
	private static final String serviceName = "RegPacketStatusService";
	private static final String subServiceName = "packetSyncStatus";
	private static final String testDataFileName = "RegPacketStatusServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	@BeforeMethod
	public void setup() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
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
		HashMap<String, String> packetResponse = new HashMap<String, String>();
		ResponseDTO response = new ResponseDTO();
		if (testCaseName
				.equalsIgnoreCase("regClient_RegpacketStatusService_SyncPackets_with_ClientStatusCode_PUSHED_smoke")) {

			List<String> packetIds = commonUtil.getPUSHEDPacketIdsfromDB();

				for (int i = 0; i < 3; i++) {
					packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
							proofImagePath, System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
							prop.getProperty("invalidRegID"));
					try {
					String string=packetSyncService.packetSync(packetResponse.get("RANDOMID"));
						String seperator = "/";
						String filePath = String
								.valueOf(ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_LOCATION))
								.concat(seperator)
								.concat(formatDate(new Date(),
										String.valueOf(ApplicationContext.map()
												.get(RegistrationConstants.PACKET_STORE_DATE_FORMAT))))
								.concat(seperator).concat(packetResponse.get("RANDOMID"));
						File packet = new File(filePath + RegistrationConstants.ZIP_FILE_EXTENSION);
						ResponseDTO responseDTO=packetUploadService.pushPacket(packet);
					} catch (RegBaseCheckedException e) {

						e.printStackTrace();
					} catch (URISyntaxException e) {

						e.printStackTrace();
					}
				}
	
				response = regPacketStatusService.packetSyncStatus(prop.getProperty("triggerPoint"));
		} else if (testCaseName
				.equalsIgnoreCase("regClient_RegpacketStatusService_Invalid_Sync_packet_RegID_with_lessthan_29chars")) {
			packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
					proofImagePath, System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
					prop.getProperty("invalidRegID"));
			/*try {
				packetSyncService.packetSync(packetResponse.get("RANDOMID"));
				String seperator = "/";
				String filePath = String
						.valueOf(ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_LOCATION))
						.concat(seperator)
						.concat(formatDate(new Date(),
								String.valueOf(
										ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT))))
						.concat(seperator).concat(packetResponse.get("RANDOMID"));
				File packet = new File(filePath + RegistrationConstants.ZIP_FILE_EXTENSION);
				packetUploadService.pushPacket(packet);
			} catch (RegBaseCheckedException e) {

				e.printStackTrace();
			} catch (URISyntaxException e) {

				e.printStackTrace();
			}*/

		//	response = regPacketStatusService.packetSyncStatus(prop.getProperty("triggerPoint"));

		} 
		commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"),packetResponse.get("RANDOMID") );
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
			f.set(baseTestMethod, RegPacketStatusServicePacketSyncStatusTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
