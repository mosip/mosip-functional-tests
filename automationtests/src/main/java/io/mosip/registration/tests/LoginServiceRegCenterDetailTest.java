/**
 * 
 */
package io.mosip.registration.tests;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import org.json.JSONObject;
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
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.service.login.impl.LoginServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Tabish Khan
 * 
 *         This test class implements the unit tests for the services exposed by
 *         the LoginService class
 *
 */
public class LoginServiceRegCenterDetailTest extends BaseConfiguration implements ITest {

	@Autowired
	private LoginServiceImpl loginService;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	private static final Logger logger = AppConfig.getLogger(LoginServiceRegCenterDetailTest.class);
	private static final String serviceName = "LoginService";
	private static final String testDataFileName = "LoginServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	private static final String subServiceName = "RegCenterDetail";
	
	@BeforeClass
	public  void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	/**
	 * 
	 * @return
	 * 
	 * 		Defines data source for obtaining registration data source
	 */
	@DataProvider(name = "registrationCenterDetailsDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	/**
	 * Defines test for obtaining registration center details
	 */
	@Test(dataProvider = "registrationCenterDetailsDataProvider", alwaysRun = true)
	public void testGetRegistrationCenterDetails(String testCaseName, JSONObject object) {
		try {

		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,testCaseName);
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,object.toString());
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"testGetUserDetails invoked!");
		mTestCaseName=testCaseName;
		String subServiceName = "regCenterDetail";
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Test case: " + testCaseName);
		String testCasePath = serviceName + File.separator + subServiceName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);
		String regCenterCase = prop.getProperty("regCenter");
		String langCodeCase = prop.getProperty("langCode");

		String regCenter = dataGenerator.getYamlData(serviceName, testDataFileName, "regCenter",
				prop.getProperty("regCenter"));
		String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "langCode",
				prop.getProperty("langCode"));

		RegistrationCenterDetailDTO centerDetailDTO = loginService.getRegistrationCenterDetails(regCenter, langCode);
		if (testCaseName.contains("invalid")) {
			assertTrue(centerDetailDTO.getRegistrationCenterName() == null);
		} else
			assertTrue(centerDetailDTO.getRegistrationCenterName() != null);
		}
		catch (Exception exception) {
			logger.debug("LOGIN SERVICE", "AUTOMATION", "REG",
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
			f.set(baseTestMethod, LoginServiceRegCenterDetailTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
