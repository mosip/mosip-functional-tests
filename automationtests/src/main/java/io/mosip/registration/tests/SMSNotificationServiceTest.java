package io.mosip.registration.tests;

import java.lang.reflect.Field;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestContext;
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
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;


public class SMSNotificationServiceTest extends BaseConfiguration implements ITest  {
	@Autowired
	NotificationService notificationService;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;
	private static final Logger logger = AppConfig.getLogger(SMSNotificationServiceTest.class);
	private static final String serviceName = "NotificationServices";
	private static final String subServiceName = "SMSNotificationService";
	private static final String testDataFileName = "TestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String testCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
	baseSetUp();
	centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
	stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}
	
	@DataProvider(name = "smsDataProvider")
	public Object[][] readTestCase(ITestContext context) {
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		String testType = "regression";
		if (testType.equalsIgnoreCase("smoke"))
			return testCaseReader.readTestCases(serviceName+"/"+subServiceName, "smoke");
		else
			return testCaseReader.readTestCases(serviceName+"/"+subServiceName, "regression");
	}

	@Test(dataProvider = "smsDataProvider", alwaysRun = true)
	public void validateSendSMSTest(String testCaseName, JSONObject object) {
        SMSNotificationServiceTest.testCaseName=testCaseName;
		Properties prop = commonUtil.readPropertyFile(serviceName+"/"+subServiceName, testCaseName, testCasePropertyFileName);

		String message = dataGenerator.getYamlData(serviceName, testDataFileName,"Message",prop.getProperty("Message"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"message:" + message);
		String mobileNumber = dataGenerator.getYamlData(serviceName, testDataFileName,"MobileNumber",prop.getProperty("MobileNumber"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"mobileNumber:" + mobileNumber);
		String registrationID = dataGenerator.getYamlData(serviceName, testDataFileName,"RegistrationID",prop.getProperty("RegistrationID"));
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"registrationID:" + registrationID);
		ResponseDTO resSMSDTO = notificationService.sendSMS(message, mobileNumber, registrationID);
//		commonUtil.verifyAssertionResponse(prop.getProperty("ExpectedResponse"), resSMSDTO);
commonUtil.verifyAssertNotNull(resSMSDTO);
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
			f.set(baseTestMethod, SMSNotificationServiceTest.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
