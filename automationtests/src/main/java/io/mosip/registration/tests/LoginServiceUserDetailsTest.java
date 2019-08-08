/**
 * 
 */
package io.mosip.registration.tests;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.service.login.impl.LoginServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Gaurav Sharan
 * 
 *         This test class implements the unit tests for the services exposed by
 *         the LoginService class
 *
 */
public class LoginServiceUserDetailsTest extends BaseConfiguration implements ITest {

	@Autowired
	private LoginServiceImpl loginService;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(LoginServiceUserDetailsTest.class);
	private static final String serviceName = "LoginService";
	private static final String subServiceName = "UserDetail";
	private static final String testDataFileName = "LoginServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	
	@BeforeMethod
	public  void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);

	}

	/**
	 * 
	 * @return
	 * 
	 * 		Defines data source for the method providing user details
	 */
	@DataProvider(name = "userDetailsDataProvider")
	public Object[][] userDetailDataSource() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	/**
	 * 
	 * @param testCaseName
	 * @param object
	 * 
	 *            Defines test for the method providing user details
	 */
	@Test(dataProvider = "userDetailsDataProvider", alwaysRun = true)
	public void testGetUserDetails(String testCaseName, JSONObject object) {
		try {

		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Test case: " + testCaseName);
		mTestCaseName = testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);
		String userIdCase = prop.getProperty("userId");
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"userIdCase " + userIdCase);
		String userId = dataGenerator.getYamlData(serviceName, testDataFileName, "userId", prop.getProperty("userId"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"userId read  is " + userId);
		UserDTO userDetail = loginService.getUserDetail(userId);
		if (testCaseName.contains("invalid")) {
			assertTrue(userDetail == null);
		} else {
			assertTrue(userDetail != null);
			assertTrue(userDetail.getId() != null);
		}
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
			f.set(baseTestMethod, LoginServiceUserDetailsTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
