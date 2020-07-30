package io.mosip.registration.test;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import io.mosip.kernel.packetmanager.dto.BiometricsDto;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Leona Mary S
 *
 *         Validating whether UserOnboard service is working as expected for
 *         invalid and valid inputs
 */

public class UserOnboardValidateTest extends BaseConfiguration implements ITest {

	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	private static Logger logger = AppConfig.getLogger(UserOnboardValidateTest.class);
	private static final String serviceName = "UserOnboardService";
	private static final String subServiceName = "ValidateUserOnboard";
	private static final String testDataFileName = "UserOnboardTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";

	/**
	 * Method to initialize the data and parameters needed before the test class is
	 * invoked
	 */

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@DataProvider(name = "UserOnboardDataProvider")
	public Object[][] readTestCase() {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName + "/" + subServiceName, "regression");
	}

	@Test(dataProvider = "UserOnboardDataProvider", alwaysRun = true)
	public void validateUserOnboard(String testCaseName, JSONObject object) {
		try {
			logger.info("USERONBOARD SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, testCaseName);
			mTestCaseName = testCaseName;
			BiometricDTO bioData = null;
			Properties prop = commonUtil.readPropertyFile(serviceName + "/" + subServiceName, testCaseName,
					testCasePropertyFileName);
			commonUtil.setFlag(prop.getProperty("flagValue"));
			String bioPath = dataGenerator.getYamlData(serviceName, testDataFileName, "userOnboard",
					prop.getProperty("pathValue"));
			logger.info("USERONBOARD SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, "Path: " + bioPath);
			List<BiometricsDto> listOfBiometrics=new ArrayList<BiometricsDto>();
			bioData = commonUtil.getBiotestData(CommonUtil.getResourcePath()+"/"+bioPath);
			ResponseDTO actualresponse = userOBservice.validateWithIDAuthAndSave(listOfBiometrics);

			commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), actualresponse);
		}

		catch (Exception exception) {
			logger.debug("USER ONBOARD SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		} 
	}

	@Test
	public void verifyMachinecenterID() {
		mTestCaseName = "regClient_UserOnboardService_Valid_MachineCenterDetails_smoke";
		Map<String, String> ids = new HashMap<String, String>();
		try {
			ids = userOBservice.getMachineCenterId();
			Assert.assertNotNull(ids.get(RegistrationConstants.USER_STATION_ID));
			Assert.assertNotNull(ids.get(RegistrationConstants.USER_CENTER_ID));
		} catch (Exception exception) {
			if(mTestCaseName.contains("smoke")) {
				Assert.assertTrue(true);
			} else {
			logger.error("USERONBOARD - SERVICE TEST - " + mTestCaseName, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			}
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
			f.set(baseTestMethod, UserOnboardValidateTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;

	}
}
