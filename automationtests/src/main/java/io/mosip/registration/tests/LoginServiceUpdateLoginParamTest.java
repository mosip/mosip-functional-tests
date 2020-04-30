/**
 * 
 */
package io.mosip.registration.tests;

import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

public class LoginServiceUpdateLoginParamTest extends BaseConfiguration implements ITest {

	@Autowired
	private LoginServiceImpl loginService;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(LoginServiceUpdateLoginParamTest.class);
	private static final String serviceName = "LoginService";
	private static final String testDataFileName = "LoginServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	private static final String subServiceName = "UpdateLoginParam";
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
	 * 		Defines data source for updating the login parameters
	 */
	@DataProvider(name = "updateLoginParamProvider")
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
	 *            Defines test for updating login parameter
	 */
	@Test(dataProvider = "updateLoginParamProvider", alwaysRun = true)
	public void updateLoginParamsTest(String testCaseName, JSONObject object) {
		try {
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"updateLoginParamsTest invoked!");
			String subServiceName = "updateLoginParam";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"Test case: " + testCaseName);
			mTestCaseName = testCaseName;
			Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
					testCasePropertyFileName);
			String userIdCase = prop.getProperty("userId");
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"userIdCase " + userIdCase);
			String userId = dataGenerator.getYamlData(serviceName, testDataFileName, "userId",
					prop.getProperty("userId"));
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"userId read  is " + userId);

			if (testCaseName.contains("validateLoginparam")) {
				UserDTO userDetail = loginService.getUserDetail(userId);
				String newId = "newId" + System.currentTimeMillis();
				userDetail.setId(newId);
				loginService.updateLoginParams(userDetail);
				assertNotNull(loginService.getUserDetail(newId));

				// clean up
				userDetail = loginService.getUserDetail(newId);
				userDetail.setId(userId);
				loginService.updateLoginParams(userDetail);
			} else if (testCaseName.contains("invalid")) {
				try {
					UserDTO userDetail = new UserDTO();
					userDetail.setId("newId");
					// loginService.updateLoginParams(userDetail);
				} catch (DataIntegrityViolationException exception) {

				}
			}

		} catch (Exception exception) {
			logger.debug("LOGIN SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, LoginServiceUpdateLoginParamTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
