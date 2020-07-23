/**
 * 
 */
package io.mosip.registration.test;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.lang.reflect.Field;
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
import io.mosip.registration.entity.Template;
import io.mosip.registration.repositories.TemplateRepository;
import io.mosip.registration.service.template.impl.TemplateServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

/**
 * @author Gaurav Sharan
 * 
 *         Test class having test cases to test the functionality of the
 *         services exposed by TemplateService
 *
 */

public class TemplateServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	private TemplateServiceImpl templateServiceImpl;

	@Autowired
	private TemplateRepository<Template> templateRepo;

	@Autowired
	TestDataGenerator dataGenerator;

	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(TemplateServiceTest.class);
	private static final String serviceName = "TemplateService";
	private static final String testDataFileName = "TemplateServiceTestData";
	private static final String testCasePropertyFileName = "condition";
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

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

	/**
	 * @return
	 * 
	 * 		Method defining data source for testing the behavior of the method
	 *         providing template(s).
	 */
	@DataProvider(name = "templateProvider")
	public Object[][] templateDataSource() {

		String testName = "template";
		String testcasedir = serviceName + "/" + testName;
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] testCases = new Object[5][];
		switch ("default") {
		case "smoke":
			testCases = testCaseReader.readTestCases(testcasedir, "smoke");

		case "regression":
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "regression");
		default:
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "smokeAndRegression");
		}
		return testCases;
	}

	/**
	 * This method checks whether getTemplate method returns the Template with
	 * respect to the required input
	 * 
	 */
	@Test(dataProvider = "templateProvider", alwaysRun = true)
	public void getTemplateTest(String testcaseName, JSONObject object) {
		try {

			String subServiceName = "template";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"Test case: " + testcaseName);
			mTestCaseName = testcaseName;
			String testCasePath = serviceName + "/" + subServiceName;
			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
			String templateCase = prop.getProperty("emailTemplate");
			String langTestCase = prop.getProperty("language");

			String emailTemplateCode = dataGenerator.getYamlData(serviceName, testDataFileName, "emailTemplate",
					templateCase);
			String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "language", langTestCase);

			Template result = templateServiceImpl.getTemplate(emailTemplateCode, langCode);
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					result.getDescription());
			if (testcaseName.contains("invalid")) {
				assertNull(result.getDescription());
			} else {
				// Assert.assertEquals(result.getDescr(),"Acknowledgement generated after
				// registration - Part 1");
				assertNotNull(result.getDescription());
			}
		} catch (Exception exception) {
			logger.info("TEMPLATE SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * 		Method providing test cases for the test method responsible for
	 *         fetching an HTML template
	 */
	@DataProvider(name = "htmlTemplateProvider")
	public Object[][] htmlTemplateDataSource() {

		String testName = "htmlTemplate";
		String testcasedir = serviceName + "/" + testName;
		// String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] testCases = new Object[5][];
		switch ("default") {
		case "smoke":
			testCases = testCaseReader.readTestCases(testcasedir, "smoke");

		case "regression":
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "regression");
		default:
			testCases = testCaseReader.readTestCases(serviceName + "/" + testName, "smokeAndRegression");
		}
		return testCases;
	}

	/**
	 * 
	 * @param testcaseName
	 * @param object
	 * 
	 *            Test method to test the behavior of method providing HTML
	 *            template(s)
	 */
	@Test(dataProvider = "htmlTemplateProvider", alwaysRun = true)
	public void getHtmlTemplateTest(String testcaseName, JSONObject object) {
		try {
			mTestCaseName = testcaseName;
			String subServiceName = "htmlTemplate";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"Test case: " + testcaseName);
			String testCasePath = serviceName + "/" + subServiceName;
			Properties prop = commonUtil.readPropertyFile(testCasePath, testcaseName, testCasePropertyFileName);
			String templateCase = prop.getProperty("emailTemplate");
			String langTestCase = prop.getProperty("language");

			String emailTemplateCode = dataGenerator.getYamlData(serviceName, testDataFileName, "emailTemplate",
					templateCase);
			String langCode = dataGenerator.getYamlData(serviceName, testDataFileName, "language", langTestCase);

			String htmlTemplate = templateServiceImpl.getHtmlTemplate(emailTemplateCode, langCode);
			if (testcaseName.contains("invalid"))
				assertNull(htmlTemplate);
			else
				Assert.assertTrue(!htmlTemplate.isEmpty());
		} catch (Exception exception) {
			logger.info("TEMPLATE SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
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
			f.set(baseTestMethod, TemplateServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
