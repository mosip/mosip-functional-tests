package io.mosip.registration.tests;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

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
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DBUtil;
import io.mosip.registration.util.DbQueries;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Leona Mary S
 *
 *         Validating the Packet Handler Service is working as expected with
 *         valid inputs and outputs
 */

public class PacketHandlerServiceTest extends BaseConfiguration implements ITest {
	@Autowired
	PacketHandlerService packetHandlerService;
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
	PacketSynchService packetSyncService;
	@Autowired
	private RegistrationDAO syncRegistrationDAO;
	@Autowired
	PacketUploadService packetUploadService;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger logger = AppConfig.getLogger(PacketHandlerServiceTest.class);
	private static final String serviceName = "PacketHandlerService";
	private static final String subServiceName = "RegClient_PacketCreation";
	private static final String testDataFileName = "PacketCreationTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@DataProvider(name = "PacketHandlerDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "PacketHandlerDataProvider", alwaysRun = true)
	public void validatePacketCreation(String testCaseName, JSONObject object) {
		logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, testCaseName);

		mTestCaseName = testCaseName;
		System.out.println(testCaseName);
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);
		try {
			SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean(prop.getProperty("isChild")));
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
			HashMap<String, String> packetResponse;

			if (prop.getProperty("UniqueCBEFF").equalsIgnoreCase("YES")) {
				// Set CBEFF to UNIQUE
				ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.YES);
				ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION,
						prop.getProperty("UniqueCBEFF_path"));
			} else {
				// Set CBEFF to UNIQUE & DUPLICATE
				ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.NO);
				ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION,
						prop.getProperty("DuplicateCBEFF_path"));
			}

			packetResponse = commonUtil.packetCreation(statusCode, biometricDataPath, demographicDataPath,
					proofImagePath, System.getProperty("userID"), centerID, stationID, prop.getProperty("status"),
					prop.getProperty("invalidRegID"));

			commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"),
					packetResponse.get(prop.getProperty("AssertValue")));
			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					packetResponse.get(prop.getProperty("AssertValue")));
			boolean isPresentInDB = DBUtil.checkRegID(packetResponse.get("RANDOMID"), DbQueries.GET_PACKETIDs);
			System.out.println("Status of db is ------->"+ isPresentInDB);
			Assert.assertEquals(isPresentInDB, true);
			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					"Created Registration ID in database: " + isPresentInDB);
			// Sync Packet
			String syncResponse = packetSyncService.packetSync(packetResponse.get("RANDOMID"));
			ResponseDTO uploadResponse = new ResponseDTO();
			boolean syncNotSuccess = false;
			boolean uploadNotSuccess = false;
			if (syncResponse.isEmpty()) {
				// Upload packet
				String seperator = "/";
/*				String filePath = String
						.valueOf(ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_LOCATION))
						.concat(seperator)
						.concat(formatDate(new Date(),
								String.valueOf(
										ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT))))
						.concat(seperator).concat(packetResponse.get("RANDOMID"));
				File packet = new File(filePath + RegistrationConstants.ZIP_FILE_EXTENSION);*/
				File packetStore=new File(System.getProperty("user.dir"));
				//File packetUpload=packetStore.getParentFile();
				File packetToBeUploaded=new File(packetStore.getAbsolutePath()+"/PacketStore/".concat(formatDate(new Date(), String.valueOf(ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT)))).concat(seperator).concat(packetResponse.get("RANDOMID"))+ RegistrationConstants.ZIP_FILE_EXTENSION);
				uploadResponse = packetUploadService.pushPacket(packetToBeUploaded);
				if (uploadResponse.getSuccessResponseDTO().getCode().equalsIgnoreCase("Success")) {

					Registration registration = syncRegistrationDAO.getRegistrationById(
							RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(),
							packetResponse.get("RANDOMID"));
					List<PacketStatusDTO> packetStatusDTO = new ArrayList<>();

					packetStatusDTO.add(commonUtil.packetStatusDtoPreperation(registration,
							RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode()));
					boolean result = packetUploadService.updateStatus(packetStatusDTO);
					boolean expectedResult = true;
					Assert.assertEquals(result, expectedResult);
					uploadNotSuccess = true;
				} else {
					logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
							"Packet not uploaded properly");
					Assert.assertTrue(uploadNotSuccess, "Packet not uploaded properly");
				}
				syncNotSuccess = true;
			} else {
				logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
						"Packet not synched properly");
				Assert.assertTrue(syncNotSuccess, "Packet not synched properly");
			}

		} catch (NullPointerException nullPointerException) {
			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
			Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
		} catch (RegBaseCheckedException regBaseCheckedException) {
			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			Reporter.log(ExceptionUtils.getStackTrace(regBaseCheckedException));
		} catch (URISyntaxException uriSyntaxException) {
			logger.info("PACKET_HANDLER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(uriSyntaxException));
			Reporter.log(ExceptionUtils.getStackTrace(uriSyntaxException));
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
			f.set(baseTestMethod, PacketHandlerServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
