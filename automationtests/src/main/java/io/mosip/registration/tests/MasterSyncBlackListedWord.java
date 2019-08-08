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
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.BlacklistedWordsRepository;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class MasterSyncBlackListedWord extends BaseConfiguration implements ITest {
	@Autowired
	MasterSyncService masterSyncService;

	@Autowired
	BlacklistedWordsRepository masterSyncBlacklistedWordsRepository;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(MasterSyncBlackListedWord.class);
	private static final String serviceName = "MasterSyncServices";
	private static final String blackListedSubServiceName = "BlackListedWordService";
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

	@DataProvider(name = "BlackListedWords")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + blackListedSubServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + blackListedSubServiceName, "regression");
	}

	@Test(dataProvider = "BlackListedWords", alwaysRun = true)
	public void verifyBlackListedWords(String testCaseName, JSONObject object) {
		try {
		
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + testCaseName);
		mTestCaseName=testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + blackListedSubServiceName, testCaseName,
				testCasePropertyFileName);

		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode",
				prop.getProperty("langCode"));
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"langCode:" + langCode);

		List<BlacklistedWordsDto> blackListedWordService = masterSyncService.getAllBlackListedWords(langCode);
		List<BlacklistedWords> blackListedWordDB = masterSyncBlacklistedWordsRepository
				.findBlackListedWordsByIsActiveTrueAndLangCode(langCode);
		List<String> blackListedFromDB = new ArrayList<>();
		List<String> blackListedWordFromService = new ArrayList<>();

		for (int i = 0; i < blackListedWordService.size(); i++)
			blackListedWordFromService.add(blackListedWordService.get(i).getWord());

		for (int i = 0; i < blackListedWordDB.size(); i++)
			blackListedFromDB.add(blackListedWordDB.get(i).getWord());

		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"BlackListed words from DB:" + blackListedFromDB);
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"BlackListed words from service:" + blackListedWordFromService);

		Assert.assertEquals(blackListedFromDB, blackListedWordFromService);
		}
		catch (RegBaseCheckedException regBaseCheckedException) {
			logger.debug("MASTER-SYNC", "AUTOMATION", "REG",
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			Reporter.log(ExceptionUtils.getStackTrace(regBaseCheckedException));
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
			f.set(baseTestMethod, MasterSyncBlackListedWord.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
