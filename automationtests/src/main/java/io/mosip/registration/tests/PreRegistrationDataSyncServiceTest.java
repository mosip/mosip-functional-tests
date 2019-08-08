package io.mosip.registration.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DBUtil;

import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Gaurav Sharan
 * 
 *         This class implements the test cases for the services exposed by
 *         PreRegistrationDataSyncService
 *
 */

public class PreRegistrationDataSyncServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	GlobalParamService globalParamService;

	@Autowired
	PreRegistrationDataSyncRepository preRegistrationDataSyncRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	PreRegistrationDataSyncDAO preRegistrationDAO;

	private static final Logger logger = AppConfig.getLogger(PreRegistrationDataSyncServiceTest.class);
	private static final String serviceName = "PreRegistrationDataSyncService";
	private static final String testDataFileName = "PreRegistrationDataSyncTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	private ApplicationContext context;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeClass(alwaysRun = true)
	public void setTestCaseName() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
		preRegistrationDataSyncService.getPreRegistrationIds(RegistrationConstants.JOB_TRIGGER_POINT_USER);

	}
	/*
		*//**
			 * Defines the common attributes to be used in the test cases.
			 * 
			 *//*
				 * private void preTestOperation() { context = ApplicationContext.getInstance();
				 * context.loadResourceBundle(); Map<String, Object> map =
				 * globalParamService.getGlobalParams();
				 * map.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS, "120");
				 * context.setApplicationMap(map); }
				 */

	/**
	 * 
	 * @return
	 * 
	 * 		Defines the data source for getPreRegistrationTest test
	 */
	@DataProvider(name = "preRegistrationDataProvider")
	public Object[][] readTestCaseForPreRegistration() {
		// preTestOperation();
		String testName = "getPreRegistration";
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

	/**
	 * Test for the getPreRegistration method
	 */
	@SuppressWarnings("static-access")
	@Test(dataProvider = "preRegistrationDataProvider", alwaysRun = true, enabled = false)
	public void getPreRegistrationTest(String testcaseName, JSONObject object) {
		try {

			mTestCaseName = testcaseName;
			String subServiceName = "getPreRegistration";
			String testCasePath = serviceName + "/" + subServiceName;
			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);

			String regCenterIdCase = prop.getProperty("regCenterId");
			String langCodeCase = prop.getProperty("langCode");

			String regCenterId = dataGenerator.getYamlData(serviceName, testDataFileName, "regCenterId",
					regCenterIdCase);
			String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode", langCodeCase);

			Map<String, Object> map = globalParamService.getGlobalParams();
			map.put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS, "120");

			RegistrationCenterDetailDTO regCenterDetail = loginService.getRegistrationCenterDetails(regCenterId,
					langCode);
			SessionContext.getInstance().userContext().setRegistrationCenterDetailDTO(regCenterDetail);
			context.setApplicationMap(map);

			if (testcaseName.contains("invalid")) {
				String preRegistrationIdCase = prop.getProperty("preRegistrationId");
				if (preRegistrationIdCase.equalsIgnoreCase("invalid")) {
					String preRegistrationId = dataGenerator.getYamlData(serviceName, testDataFileName,
							"preRegistrationId", preRegistrationIdCase);
					ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegistrationId);
					Assert.assertTrue(responseDTO.getErrorResponseDTOs().size() > 0);
				} else {
					String preRegistrationId = DBUtil.getPreRegIdFromDB();
					ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegistrationId);
					// TODO to implement once pre-reg sync starts working
				}

			} else {
				String preRegistrationId = DBUtil.getPreRegIdFromDB();
				ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegistrationId);
				// TODO to implement once pre-reg sync starts working
				// assertNotNull(responseDTO.getSuccessResponseDTO());
				// assertNull(responseDTO.getErrorResponseDTOs());
			}
		} catch (Exception exception) {
			logger.debug("PRE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}

	}

	/**
	 * @return
	 * 
	 * 		Defines the data source for the getPreRegistrationTest test
	 */
	@DataProvider(name = "preRegistrationIdsProvider")
	public Object[][] readTestCaseForPreRegistrationIds() {
		// preTestOperation();
		String testName = "getPreRegistrationIds";
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

	/**
	 * 
	 * Defines the test case for getPreRegistrationTest
	 */
	@SuppressWarnings("static-access")
	@Test(dataProvider = "preRegistrationIdsProvider", alwaysRun = true)
	public void getPreRegistrationIdsTest(String testcaseName, JSONObject object) {
		try {
			mTestCaseName = testcaseName;
			String subServiceName = "getPreRegistrationIds";
			String testCasePath = serviceName + "/" + subServiceName;
			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
			String regCenterIdCase = prop.getProperty("regCenterId");
			String syncJobIdCase = prop.getProperty("syncJobId");
			String langCodeCase = prop.getProperty("langCode");

			String regCenterId = dataGenerator.getYamlData(serviceName, testDataFileName, "regCenterId",
					regCenterIdCase);
			String syncJobId = dataGenerator.getYamlData(serviceName, testDataFileName, "syncJobId", syncJobIdCase);
			String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode", langCodeCase);

			// logger.info("for testcase " + testcaseName);
			// System.out.print("regCenterId:-" + regCenterId + " & ");
			// logger.info("syncJobId:-" + syncJobId);

			RegistrationCenterDetailDTO regCenterDetail = loginService.getRegistrationCenterDetails(regCenterId,
					langCode);
			SessionContext.getInstance().userContext().setRegistrationCenterDetailDTO(regCenterDetail);

			if (testcaseName.contains("invalid")) {
				ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistrationIds(syncJobId);
				List<ErrorResponseDTO> errors = responseDTO.getErrorResponseDTOs();
				Assert.assertTrue(errors.size() > 0);
				SuccessResponseDTO success = responseDTO.getSuccessResponseDTO();
				//logger.info("errors.size() gives " + errors.size());
			//	logger.info("errors is " + errors.toString());

			} else {

			}
		} catch (Exception exception) {
			logger.debug("PRE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}

	}

	/**
	 * Test case having valid scenario for getPreRegIdFromDB(). It fetches a
	 * preRegistrationId already present in DB and then invokes the method on that
	 */
	@Test
	public void testGetPreRegistrationRecordForDeletion() {
		try {
			mTestCaseName = "regClient_PreRegistrationDataSync_RecordForDeletion";
			String preRegistrationId = "37805175312708";
			// preRegistrationId = DBUtil.getPreRegIdFromDB();
			PreRegistrationList preRegList = preRegistrationDataSyncService
					.getPreRegistrationRecordForDeletion(preRegistrationId);
			Assert.assertNull(preRegList);
		} catch (Exception exception) {
			logger.debug("PRE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * 		Defines the data source for deleting pre-registration record, by
	 *         invoking the deletePreregRecords method
	 */

	@DataProvider(name = "deletePreregRecordsDataProvider")
	public Object[][] readTestCaseForDeletePreregRecords() {

		String testName = "deletePreregRecords";
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

	/**
	 * 
	 * @param testcaseName
	 * @param object
	 * 
	 *            Defines the test for deleting pre-registration record, by invoking
	 *            the deletePreregRecords method
	 */
	@Test(dataProvider = "deletePreregRecordsDataProvider", alwaysRun = true)
	public void testDeletePreRegRecords(String testcaseName, JSONObject object) {
		try {
			mTestCaseName = testcaseName;
			String subServiceName = "deletePreregRecords";
			String testCasePath = serviceName + "/" + subServiceName;

			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
			String packetAgeCase = prop.getProperty("packetAge");

			String packetAgeStr = dataGenerator.getYamlData(serviceName, testDataFileName, "packetAge", packetAgeCase);
			int packetAge = Integer.parseInt(packetAgeStr);

			StringBuilder sb = new StringBuilder();
			sb.append("Test String");
			String packetPath = System.getProperty("user.dir") + "\\samplePacket.zip";
			File samplePacket = new File(packetPath);
			ZipOutputStream out = null;
			try {
				out = new ZipOutputStream(new FileOutputStream(samplePacket));
				ZipEntry e = new ZipEntry("mytext.txt");

				out.putNextEntry(e);

				byte[] data = sb.toString().getBytes();
				out.write(data, 0, data.length);
				out.closeEntry();

			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			List<PreRegistrationList> list = preRegistrationDataSyncRepository.findAll();
			int sizeOriginal = list.size();
			PreRegistrationList preRegEntity = list.get(0);
			preRegEntity.setId("101");
			preRegEntity.setIsDeleted(false);
			preRegEntity.setPacketPath(packetPath);
			//logger.info(preRegEntity.getIsDeleted());
			Calendar c = Calendar.getInstance();

			// Age of packet is read from testData.yaml based on the test case

			c.add(Calendar.DATE, -packetAge);
			preRegEntity.setAppointmentDate(Date.from(c.toInstant()));
			preRegistrationDataSyncRepository.save(preRegEntity);

			// Calendar startCal = Calendar.getInstance();
			// startCal.add(Calendar.DATE,
			// -IntegrationTestConstants.PRE_REG_DELETION_CONFIGURED_DAYS);
			// Date startDate = Date.from(startCal.toInstant());
			// List<PreRegistrationList> preRegList =
			// preRegistrationDAO.fetchRecordsToBeDeleted(startDate);

			ApplicationContext.getInstance().map().put(RegistrationConstants.PRE_REG_DELETION_CONFIGURED_DAYS,
					ConstantValues.PRE_REG_DELETION_CONFIGURED_DAYS.toString());

			ResponseDTO responseDTO = new ResponseDTO();
			// preRegistrationDataSyncService.deletePreRegRecords(responseDTO, preRegList);
			responseDTO = preRegistrationDataSyncService.fetchAndDeleteRecords();
			SuccessResponseDTO successRespDto = responseDTO.getSuccessResponseDTO();
			List<ErrorResponseDTO> errors = responseDTO.getErrorResponseDTOs();

			if (testcaseName.contains("smoke")) {
				Assert.assertEquals("PRE_REG_DELETE_SUCCESS", successRespDto.getMessage());
				PreRegistrationList preRegistrationList = preRegistrationDataSyncRepository.findByPreRegId("101");
				// String packetPathExtracted = preRegistrationList.getPacketPath();
				// System.out.println(packetPathExtracted);
				Assert.assertTrue(preRegistrationList == null);
				Assert.assertNull(preRegistrationList);
				// List<PreRegistrationList> list1 =
				// preRegistrationDataSyncRepository.findAll();
				// int sizeNew = list1.size();
				// assertEquals(sizeNew, sizeOriginal);
			} else {
				// assertNotNull(errors);
				List<PreRegistrationList> list1 = preRegistrationDataSyncRepository.findAll();
				int sizeNew = list1.size();
				Assert.assertEquals(sizeOriginal + 1, sizeNew);
			}
		} catch (Exception exception) {
			logger.debug("PRE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}

	}

	/**
	 * @throws IOException
	 * 
	 *             Test Case when preRegPacket is deleted successfully
	 */
	@Test // (enabled = false)
	public void fetchAndDeleteRecordsTest() throws IOException {
		try {
			StringBuilder sb = new StringBuilder();
			mTestCaseName = "regClient_PreRegistrationDataSyncService_fetchAndDeleteRecordsTest";
			sb.append("Test String");
			String packetPath = System.getProperty("user.dir") + "\\samplePacket.zip";
			File samplePacket = new File(packetPath);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(samplePacket));
			ZipEntry e = new ZipEntry("mytext.txt");
			out.putNextEntry(e);

			byte[] data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();

			out.close();
			List<PreRegistrationList> list = preRegistrationDataSyncRepository.findAll();
			/*
			 * PreRegistrationList preRegEntity = list.get(0); preRegEntity.setId("101");
			 * preRegEntity.setIsDeleted(false); preRegEntity.setPacketPath(packetPath);
			 * logger.info(preRegEntity.getIsDeleted()); Calendar c =
			 * Calendar.getInstance(); c.add(Calendar.DATE, -130);
			 * preRegEntity.setAppointmentDate(Date.from(c.toInstant()));
			 * preRegistrationDataSyncRepository.save(preRegEntity);
			 * Assert.assertEquals("PRE_REG_DELETE_SUCCESS",
			 * preRegistrationDataSyncService.fetchAndDeleteRecords().getSuccessResponseDTO(
			 * ).getMessage());
			 */
			preRegistrationDataSyncRepository.saveAll(list);
		} catch (Exception exception) {
			logger.debug("PRE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, PreRegistrationDataSyncServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
