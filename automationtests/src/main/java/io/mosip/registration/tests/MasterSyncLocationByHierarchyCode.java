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

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.entity.Location;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.LocationRepository;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class MasterSyncLocationByHierarchyCode extends BaseConfiguration implements ITest {
	@Autowired
	MasterSyncService masterSyncService;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(MasterSyncLocationByHierarchyCode.class);
	private static final String serviceName = "MasterSyncServices";
	private static final String genderDetailServiceName = "LocationByHierarchyCode";
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

	@DataProvider(name = "LocationByHierarchyCode")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + genderDetailServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + genderDetailServiceName, "regression");
	}

	@Test(dataProvider = "LocationByHierarchyCode", alwaysRun = true)
	public void verifyLocationByHierarchyCodeByHierarchyLevel(String testCaseName, JSONObject object) {
		try {
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"test case Name:" + testCaseName);
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + genderDetailServiceName, testCaseName,
				testCasePropertyFileName);
		mTestCaseName = testCaseName;
		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode",
				prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"langCode:" + langCode);

		String hierarchylevelName = dataGenerator.getYamlData(serviceName, testDataFileName, "hierarchylevelName",
				prop.getProperty("hierarchylevelName"));
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"hierarchylevelName:" + hierarchylevelName);
		List<LocationDto> locationHierarchyService = masterSyncService.findLocationByHierarchyCode(hierarchylevelName,
				langCode);
		List<Location> locationHierarchyDB = locationRepository
				.findByIsActiveTrueAndHierarchyNameAndLangCode(hierarchylevelName, langCode);
		List<Integer> locationHierarchyLevelFromDB = new ArrayList<>();
		List<Integer> locationHierarchyFromService = new ArrayList<>();

		for (int i = 0; i < locationHierarchyService.size(); i++)
			locationHierarchyFromService.add(locationHierarchyService.get(i).getHierarchyLevel());

		for (int i = 0; i < locationHierarchyDB.size(); i++)
			locationHierarchyLevelFromDB.add(locationHierarchyDB.get(i).getHierarchyLevel());

		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Hiearchy level from DB:" + locationHierarchyLevelFromDB);
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Hierarchy level from service:" + locationHierarchyFromService);

		Assert.assertEquals(locationHierarchyLevelFromDB, locationHierarchyFromService);
		}
		catch (Exception exception) {
			logger.debug("MASTER-SYNC", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	@Test(dataProvider = "LocationByHierarchyCode", alwaysRun = true)
	public void verifyLocationByHierarchyCode(String testCaseName, JSONObject object) {
		
		logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"test case Name:" + testCaseName);
		mTestCaseName = testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + genderDetailServiceName, testCaseName,
				testCasePropertyFileName);

		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode",
				prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"langCode:" + langCode);

		String hierarchylevelName = dataGenerator.getYamlData(serviceName, testDataFileName, "hierarchylevelName",
				prop.getProperty("hierarchylevelName"));
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"hierarchyCode:" + hierarchylevelName);

		List<LocationDto> locationHierarchyService = masterSyncService.findLocationByHierarchyCode(hierarchylevelName,
				langCode);
		List<Location> locationHierarchyDB = locationRepository
				.findByIsActiveTrueAndHierarchyNameAndLangCode(hierarchylevelName, langCode);
		List<String> locationHierarchyNameFromDB = new ArrayList<>();
		List<String> locationHierarchyNameService = new ArrayList<>();

		for (int i = 0; i < locationHierarchyService.size(); i++)
			locationHierarchyNameService.add(locationHierarchyService.get(i).getCode());

		for (int i = 0; i < locationHierarchyDB.size(); i++)
			locationHierarchyNameFromDB.add(locationHierarchyDB.get(i).getCode());

		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Hiearchy Name from DB:" + locationHierarchyNameFromDB);
		logger.debug(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
				"Hierarchy Name from service:" + locationHierarchyNameService);

		Assert.assertEquals(locationHierarchyNameFromDB, locationHierarchyNameService);
		
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
			f.set(baseTestMethod, MasterSyncLocationByHierarchyCode.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
