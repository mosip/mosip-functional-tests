package io.mosip.pmp.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.pmp.fw.util.PartnerTestUtil;

public class RetrievePartner extends PartnerTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(RetrievePartner.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	KernelDataBaseAccess masterDB = new KernelDataBaseAccess();

	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestType() {
		this.testType = RunConfigUtil.getTestLevel();
		String createPolicyQuery = partnerQueries.get("createPartnerpolicy").toString();
		String createAuthQuery = partnerQueries.get("createPartnerAuth").toString();
		String registerPartnerQuery = partnerQueries.get("registerPartner").toString();
		if (masterDB.executeQuery(createPolicyQuery, "pmp")
				&& masterDB.executeQuery(createAuthQuery, "pmp")
				&& masterDB.executeQuery(registerPartnerQuery, "pmp"))
			logger.info("retrievePartner with id as Test successfully using query from partnerQueries.properties");
		else
			logger.info("not able to retrievePartner using query from partnerQueries.properties");
	}

	/**
	 * Method set Test data path and its filename
	 * 
	 * @param index
	 */
	public void setTestDataPathsAndFileNames(int index) {
		this.TESTDATA_PATH = getTestDataPath(getClass().getSimpleName().toString(), index);
		this.TESTDATA_FILENAME = getTestDataFileName(getClass().getSimpleName().toString(), index);
	}

	/**
	 * Method set configuration
	 * 
	 * @param testType
	 */
	public void setConfigurations(String testType) {
		RunConfigUtil.getRunConfigObject("partner");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "partner");
	}

	/**
	 * The method set test case name
	 * 
	 * @param method
	 * @param testData
	 */
	@BeforeMethod
	public void testData(Method method, Object[] testData) {
		String testCase = "";
		if (testData != null && testData.length > 0) {
			TestParameters testParams = null;
			// Check if test method has actually received required parameters
			for (Object testParameter : testData) {
				if (testParameter instanceof TestParameters) {
					testParams = (TestParameters) testParameter;
					break;
				}
			}
			if (testParams != null) {
				testCase = testParams.getTestCaseName();
			}
		}
		testCaseName = String.format(testCase);
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[][] getTestCaseList() {
		System.out.println("inside dataprovider");
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(testType);
		return DataProviderClass.getDataProvider(
				RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), RunConfigUtil.objRunConfig.getTestType());
	}

	/**
	 * Set current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Test method for OTP Generation execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException 
	 * @throws AdminTestException
	 */
	@Test(dataProvider = "testcaselist")
	public void retrievePartner(TestParameters objTestParameters, String testScenario, String testcaseName) throws AuthenticationTestException, AdminTestException {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		String cookieValue=null;
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		//String mapping = TestDataUtil.getMappingPath();
		displayContentInFile(testCaseName.listFiles(), "request");	
		String url = RunConfigUtil.objRunConfig.getAdminEndPointUrl() + RunConfigUtil.objRunConfig.getRetrievePartnerPath();
		logger.info("******Get request Json to EndPointUrl: " + url+" *******");
		if(testcaseName.contains("smoke"))
		{
			cookieValue = getAuthorizationCookie(getCookieRequestFilePath("northZonalPartner"),"https://"+
					System.getProperty("env.user")+".mosip.io/v1/authmanager/authenticate/useridPwd",AUTHORIZATHION_COOKIENAME);
		}
		else if (testcaseName.toLowerCase().contains("northuser")) {
			cookieValue = getAuthorizationCookie(getCookieRequestFilePath("northZonalPartner"),"https://"+
					System.getProperty("env.user")+".mosip.io/v1/authmanager/authenticate/useridPwd",AUTHORIZATHION_COOKIENAME);
		}
		else if (testcaseName.toLowerCase().contains("nozonemap")) {
			cookieValue = getAuthorizationCookie(getCookieRequestFilePath("noZoneMap"),"https://"+
					System.getProperty("env.user")+".mosip.io/v1/authmanager/authenticate/useridPwd",AUTHORIZATHION_COOKIENAME);
		}
		else
		{
			cookieValue = getAuthorizationCookie(getCookieRequestFilePath("northZonalPartner"),
					System.getProperty("env.endpoint")+"/v1/authmanager/authenticate/useridPwd",AUTHORIZATHION_COOKIENAME);
		}
		
		getRequestAndGenerateOuputFileWithCookie(testCaseName.listFiles(), url,"request", "output-1-actual-response", 0, AUTHORIZATHION_COOKIENAME,
				  cookieValue); 		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if(!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanup() throws AdminTestException {
		if (masterDB.executeQuery(partnerQueries.get("deleteRegisterPartner").toString(), "pmp")
				&& masterDB.executeQuery(partnerQueries.get("deletePartnerAuth").toString(), "pmp")
				&& masterDB.executeQuery(partnerQueries.get("deletePartnerpolicy").toString(), "pmp"))
			logger.info("retrievePartner all Register Partner data successfully");
		else {
			logger.info("not able to delete retrievePartner data using query from query.properties");
		}
		logger.info("END");
	}
}
