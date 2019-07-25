package io.mosip.registration.tests;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.repositories.GenderRepository;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;


public class MasterSyncGenderDetail extends BaseConfiguration implements ITest{
	@Autowired
	MasterSyncService masterSyncService;

	@Autowired
	GenderRepository genderRepository;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(MasterSyncGenderDetail.class);
	private static final String serviceName = "MasterSyncServices";
	private static final String genderDetailServiceName = "GenderDetail";
	private static final String testDataFileName = "TestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeMethod
	public void setTestCaseName() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}
	
	@DataProvider(name = "GenderDetail")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + genderDetailServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + genderDetailServiceName, "regression");
	}

	@Test(dataProvider = "GenderDetail", alwaysRun = true)
	public void verifyGenderDetailByName(String testCaseName, JSONObject object) {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName=testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + genderDetailServiceName, testCaseName,
				testCasePropertyFileName);

		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName,
				"langCode", prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"langCode:" + langCode);

		List<GenderDto> genderDetailService = masterSyncService.getGenderDtls(langCode);
		List<Gender> genderDetailDB = genderRepository.findByIsActiveTrueAndLangCode(langCode);
		List<String> genderNameFromDB = new ArrayList<>();
		List<String> genderNameFromService = new ArrayList<>();

		for (int i = 0; i < genderDetailService.size(); i++)
			genderNameFromService.add(genderDetailService.get(i).getGenderName());

		for (int i = 0; i < genderDetailDB.size(); i++)
			genderNameFromDB.add(genderDetailDB.get(i).getGenderName().trim());

		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Gender Name from DB:" + genderNameFromDB);
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Gender Name from service:" + genderNameFromService);

		Assert.assertEquals(genderNameFromDB, genderNameFromService);
	}
	
	@Test(dataProvider = "GenderDetail", alwaysRun = true)
	public void verifyGenderDetailByCode(String testCaseName, JSONObject object) {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName=testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + genderDetailServiceName, testCaseName,
				testCasePropertyFileName);

		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName,
				"langCode", prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"langCode:" + langCode);
		ResponseDTO restResponse=masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER);
		List<GenderDto> genderDetailService = masterSyncService.getGenderDtls(langCode);
		List<Gender> genderDetailDB = genderRepository.findByIsActiveTrueAndLangCode(langCode);
		List<String> genderCodeFromDB = new ArrayList<>();
		List<String> genderCodeFromService = new ArrayList<>();

		for (int i = 0; i < genderDetailService.size(); i++)
			genderCodeFromService.add(genderDetailService.get(i).getCode());

		for (int i = 0; i < genderDetailDB.size(); i++)
			genderCodeFromDB.add(genderDetailDB.get(i).getCode().trim());

		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Gender Code from DB:" + genderCodeFromDB);
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Gender Code from service:" + genderCodeFromService);

		Assert.assertEquals(genderCodeFromDB, genderCodeFromService);
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
			f.set(baseTestMethod, MasterSyncGenderDetail.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
