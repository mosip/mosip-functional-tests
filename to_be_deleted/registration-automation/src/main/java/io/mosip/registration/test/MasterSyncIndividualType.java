package io.mosip.registration.test;

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
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.IndividualTypeRepository;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;


public class MasterSyncIndividualType extends BaseConfiguration implements ITest{
	@Autowired
	MasterSyncService masterSyncService;

	@Autowired
	IndividualTypeRepository individualTypeRepository;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(MasterSyncIndividualType.class);
	private static final String serviceName = "MasterSyncServices";
	private static final String individualTypeSubServiceName = "IndividualType";
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
	
	@DataProvider(name = "IndividualType")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + individualTypeSubServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + individualTypeSubServiceName, "regression");
	}

	@Test(dataProvider = "IndividualType", alwaysRun = true)
	public void verifyIndividualTypeByName(String testCaseName, JSONObject object) {
		try {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName=testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + individualTypeSubServiceName, testCaseName,
				testCasePropertyFileName);

		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName,
				"langCode", prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"langCode:" + langCode);
		String code = dataGenerator.getYamlData(serviceName, testDataFileName,
				"langCode", prop.getProperty("code"));
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Code:" + code);

		List<IndividualTypeDto> individualTypeService = masterSyncService.getIndividualType(code, langCode);
		List<IndividualType> individualTypeDB = individualTypeRepository.findByIndividualTypeIdCodeAndIndividualTypeIdLangCodeAndIsActiveTrue(code, langCode);
		List<String> individualTypeNameFromDB = new ArrayList<>();
		List<String> individualTypeNameFromService = new ArrayList<>();

		for (int i = 0; i < individualTypeService.size(); i++)
			individualTypeNameFromService.add(individualTypeService.get(i).getName());

		for (int i = 0; i < individualTypeDB.size(); i++)
			individualTypeNameFromDB.add(individualTypeDB.get(i).getName());

		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Individual type Name from DB:" + individualTypeNameFromDB);
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Individual type Name from service:" + individualTypeNameFromService);

		Assert.assertEquals(individualTypeNameFromDB, individualTypeNameFromService);
		}
		catch (Exception exception) {
			logger.debug("MASTER-SYNC", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, MasterSyncIndividualType.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
	
}
