/**
 * 
 */
package io.mosip.registration.tests;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
public class LoginServiceLoginModesTest extends BaseConfiguration implements ITest {

	@Autowired
	private LoginServiceImpl loginService;


	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(LoginServiceLoginModesTest.class);
	private static final String serviceName = "LoginService";
	private static final String subServiceName = "LoginMode";
	private static final String testDataFileName = "LoginServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeClass
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	/**
	 * 
	 * @return
	 * 
	 * 		defines the data source for the test of the method providing login
	 *         modes
	 */

	@DataProvider(name = "loginModesDataProvider")
	public Object[][] readTestCase() {
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
	 *            defines test for the method providing login modes
	 */
	@Test(dataProvider = "loginModesDataProvider", alwaysRun = true)
	public void testModesOfLogin(String testCaseName, JSONObject object) {
		try {
		String subServiceName = "loginMode";
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"Test case: " + testCaseName);
		mTestCaseName = testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
				testCasePropertyFileName);
		String authType = dataGenerator.getYamlData(serviceName, testDataFileName, "authType",
				prop.getProperty("authType"));
		String roles = dataGenerator.getYamlData(serviceName, testDataFileName, "roleList",
				prop.getProperty("roleList"));
		Set<String> roleSet = new HashSet<>();
		// String[] rolesArr = roles.split(",");
		for (String role : roles.split(",")) {
			roleSet.add(role);
		}
		List<String> modes = loginService.getModesOfLogin(authType, roleSet); // onboard_auth
		if (testCaseName.contains("invalid"))
			assertTrue(modes.size() == 0);
		else
			assertTrue(modes.size() > 0);
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
			f.set(baseTestMethod, LoginServiceLoginModesTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
