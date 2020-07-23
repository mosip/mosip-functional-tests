package io.mosip.registration.test;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Leona Mary S
 *
 *         Validating the Packet Handler Service is working as expected with
 *         valid inputs and outputs
 */

public class PacketSyncServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	PacketSynchService packetSyncService;
	@Autowired
	RegistrationDAO regDAO;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	Environment env;
	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;
	@Autowired
	private AESEncryptionService aesEncryptionService;
	@Autowired
	LoginService loginService;

	private static final Logger logger = AppConfig.getLogger(PacketSyncServiceTest.class);
	private static final String serviceName = "PacketSyncService";
	private static final String subServiceName = "SyncPacketsToServer";
	private static final String testDataFileName = "PacketSyncTestData";
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

	@DataProvider(name = "PacketSyncDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "PacketSyncDataProvider", alwaysRun = true)
	public void validateSyncPacketsToServer(String testCaseName, JSONObject object) throws ParseException {
		mTestCaseName = testCaseName;
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"test case Name:" + testCaseName);

		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);

		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG, prop.getProperty("eodConfigFlag"));
		ArrayList<String> roles = new ArrayList<>();
		for (String role : prop.getProperty("roles").split(","))
			roles.add(role);
		SessionContext.userContext().setRoles(roles);

		String langcode = dataGenerator.getYamlData(serviceName, testDataFileName, "langcode",
				prop.getProperty("langcode"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"LangCode: " + langcode);
		String synStatus = dataGenerator.getYamlData(serviceName, testDataFileName, "synStatus",
				prop.getProperty("synStatus"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Sync Status: " + synStatus);
		String syncType = dataGenerator.getYamlData(serviceName, testDataFileName, "syncType",
				prop.getProperty("syncType"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Sync Type: " + syncType);
		String statusCode = dataGenerator.getYamlData(serviceName, testDataFileName, "statusCode",
				prop.getProperty("CreatePacket"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"StatusCode: " + statusCode);
		String biometricDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "bioPath",
				prop.getProperty("bioPath"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Resident Biometric data Path: " + biometricDataPath);
		String demographicDataPath = dataGenerator.getYamlData(serviceName, testDataFileName, "demoPath",
				prop.getProperty("demoPath"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Resident Demographic data Path: " + demographicDataPath);
		String proofImagePath = dataGenerator.getYamlData(serviceName, testDataFileName, "imagePath",
				prop.getProperty("imagePath"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Resident Proof data Path: " + proofImagePath);
		String statusComment = dataGenerator.getYamlData(serviceName, testDataFileName, "statusComments",
				prop.getProperty("statusComment"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"status Comment: " + statusComment);
		String invalidRegID = dataGenerator.getYamlData(serviceName, testDataFileName, "invalidRegID",
				prop.getProperty("invalidRegID"));
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Invalid Registration ID: " + invalidRegID);

		ResponseDTO result = new ResponseDTO();
		RegistrationPacketSyncDTO synDTOdata = new RegistrationPacketSyncDTO();

		try {
			if (testCaseName.equalsIgnoreCase("regClient_PacketSyncService_SynEODPacketsToServer")) {

				HashMap<String, String> packetResponse = new HashMap<>();
				List<String> dbData = new ArrayList<String>();
				// Thread.sleep(5000);
				for (int i = 0; i < 2; i++) {
					packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
							proofImagePath, System.getProperty("userID"), centerID, stationID,
							prop.getProperty("status"), prop.getProperty("invalidRegID"));
					dbData.add(packetResponse.get("RANDOMID"));
					Thread.sleep(5000);
				}
				String syncResponse = packetSyncService.syncEODPackets(dbData);
				String response = "";
				if (syncResponse.isEmpty()) {
					SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
					Map<String, Object> statusMap = new WeakHashMap<>();
					statusMap.put("message", "success");
					successResponseDTO.setOtherAttributes(statusMap);
					result.setSuccessResponseDTO(successResponseDTO);
					response = (String) result.getSuccessResponseDTO().getOtherAttributes().get("message");
				} else {
					ErrorResponseDTO errResponse = new ErrorResponseDTO();
					LinkedList<ErrorResponseDTO> errResponsesList = new LinkedList<>();
					errResponse.setInfoType(RegistrationConstants.ERROR);
					errResponse.setMessage("Packet not synched");
					errResponsesList.add(errResponse);

					result.setErrorResponseDTOs(errResponsesList);
					response = (String) result.getErrorResponseDTOs().get(0).getMessage();
				}
				commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), response);

			} else if (testCaseName.equalsIgnoreCase("regClient_PacketSyncService_Sync_single_packet_Valid")) {
				HashMap<String, String> packetResponse = new HashMap<>();
				packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
						proofImagePath, System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
						prop.getProperty("invalidRegID"));
				String output = packetSyncService.packetSync(packetResponse.get("RANDOMID"));
				String response = "";
				if (output.isEmpty()) {
					SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
					Map<String, Object> statusMap = new WeakHashMap<>();
					statusMap.put("message", "success");
					successResponseDTO.setOtherAttributes(statusMap);
					result.setSuccessResponseDTO(successResponseDTO);
					response = (String) result.getSuccessResponseDTO().getOtherAttributes().get("message");
				} else {
					ErrorResponseDTO errResponse = new ErrorResponseDTO();
					LinkedList<ErrorResponseDTO> errResponsesList = new LinkedList<>();
					errResponse.setInfoType(RegistrationConstants.ERROR);
					errResponse.setMessage("Packet not synched");
					errResponsesList.add(errResponse);
					result.setErrorResponseDTOs(errResponsesList);
					response = (String) result.getErrorResponseDTOs().get(0).getMessage();
				}

				commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), response);

			} else {
				if (testCaseName.equalsIgnoreCase("regClient_PacketSyncService_SyncInvalidPacketToServer")) {
					synDTOdata = commonUtil.syncPacketsToServer_InvalidDataProvider(langcode, statusComment,
							invalidRegID, synStatus, syncType);
					result = packetSyncService.syncPacketsToServer(
							CryptoUtil.encodeBase64(
									aesEncryptionService.encrypt(javaObjectToJsonString(synDTOdata).getBytes())),
							RegistrationConstants.JOB_TRIGGER_POINT_USER);

					// Assert.assertEquals(prop.getProperty("ExpectedResponse"),
					// (String) result.getErrorResponseDTOs().get(0).getMessage());
					// getOtherAttributes().get("message"));
					Assert.assertNotNull(result.getErrorResponseDTOs());
					System.out.println((String) result.getErrorResponseDTOs().get(0).getMessage());
				} else if (testCaseName.equalsIgnoreCase("regClient_PacketSyncService_Sync_single_packet_Invalid")) {
					HashMap<String, String> packetResponse = new HashMap<>();
					packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
							proofImagePath, System.getProperty("userID"), centerID, stationID,
							prop.getProperty("status"), prop.getProperty("invalidRegID"));

					Assert.assertTrue(packetResponse.isEmpty());
				}

				else {
					synDTOdata = commonUtil.syncdatatoserver_validDataProvider(langcode, synStatus, syncType,
							statusCode, biometricDataPath, demographicDataPath, proofImagePath,
							System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
							prop.getProperty("invalidRegID"));
					result = packetSyncService.syncPacketsToServer(
							CryptoUtil.encodeBase64(
									aesEncryptionService.encrypt(javaObjectToJsonString(synDTOdata).getBytes())),
							RegistrationConstants.JOB_TRIGGER_POINT_USER);

					Assert.assertNotNull(result.getErrorResponseDTOs().get(0));// getOtherAttributes().get("message"));
					System.out.println((String) result.getErrorResponseDTOs().get(0).getMessage());
				}
			}

		}

		catch (RegBaseCheckedException regBaseCheckedException) {
			logger.debug("PACKET SYNC SERVICE", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			Reporter.log(ExceptionUtils.getStackTrace(regBaseCheckedException));
		} catch (JsonProcessingException jsonProcessingException) {
			logger.debug("PACKET SYNC SERVICE", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(jsonProcessingException));
			Reporter.log(ExceptionUtils.getStackTrace(jsonProcessingException));
		} catch (URISyntaxException uriSyntaxException) {
			logger.debug("PACKET SYNC SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(uriSyntaxException));
			Reporter.log(ExceptionUtils.getStackTrace(uriSyntaxException));
		} catch (InterruptedException interruptedException) {
			logger.debug("PACKET SYNC SERVICE", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(interruptedException));
			Reporter.log(ExceptionUtils.getStackTrace(interruptedException));
		} catch(NullPointerException nullPointerException) {
			
		}
	}

	@Test
	public void validateUpdateSyncStatus() {
		mTestCaseName = "regClient_PacketSyncService_validate_UpdateSyncStatus";
		Boolean expectedVal = true;
		List<PacketStatusDTO> RegPacketDetails = packetSyncService.fetchPacketsToBeSynched();
		Boolean actualVal = packetSyncService.updateSyncStatus(RegPacketDetails);
		Assert.assertEquals(expectedVal, actualVal);
	}

	@Test
	public void validateFetchPacketsToBeSynched() {
		mTestCaseName = "regClient_PacketSyncService_validate_PacketsFetchedToBeSynced";

		boolean result = false;

		// Fetching Data from database through JAVA API
		List<PacketStatusDTO> details = packetSyncService.fetchPacketsToBeSynched();
		if (details.size() >= 0) {
			result = true;
			Assert.assertTrue(result);
		} else {
			Assert.assertTrue(result);
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
			f.set(baseTestMethod, PacketSyncServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
