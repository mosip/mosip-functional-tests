package io.mosip.admin.tests;

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
import io.mosip.admin.fw.util.AdminTestUtil;
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

public class DecommisionDevice extends AdminTestUtil implements ITest {

	private static final Logger logger = Logger.getLogger(DecommisionDevice.class);
	protected String testCaseName = "";
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
		// replacing the deviceID(Tdevice to new ID Tdevice1)
		String query = queries.get("createDevice").toString().replace("Tdevice", queries.get("deviceId1").toString());
		// replacing the deviceID(Tdevice to new ID Tdevice2) and zoneCode(CST to BRK) 
		String query1 = queries.get("createDevice").toString().replace("Tdevice", queries.get("deviceId2").toString()).replace("CST", queries.get("zoneCode").toString());
		if(masterDB.executeQuery(queries.get("createDevice").toString(), "masterdata")&&masterDB.executeQuery(query, "masterdata")&&masterDB.executeQuery(query1, "masterdata"))
			logger.info("created 3 Devices successfully using query from query.properties");
		else logger.info("not able to create device using query from query.properties");
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
		RunConfigUtil.getRunConfigObject("admin");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "admin");
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
		if(!kernelCmnLib.isValidToken(adminCookie))
			adminCookie = kernelAuthLib.getAuthForAdmin();
		//mapping center 10001 to newly created device. so center 10001 should be present in db.
		if (testCaseName.contains("regCentMapToDevice")) {
			String query = queries.get("mapRegCentDevice").toString().replace("Tcnt1", "10001").replace("3000025",
					"Tdevice1");
			if (masterDB.executeQuery(query, "masterdata"))
				logger.info("mapped device regCenter successfully using query from query.properties");
			else
				logger.info("not able to map device regCenter using query from query.properties");
		}
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[][] getTestCaseList() {
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
		if (testCaseName.contains("regCentMapToDevice")) {
			// replacing centerid with device id columnName and value both
			String query = queries.get("deleteMappedRegCentDevice").toString().replace("regcntr_id", "device_id").replace("Tcnt1",
					queries.get("deviceId1").toString());
			if (masterDB.executeQuery(query, "masterdata"))
				logger.info("deleted mapped device regCenter successfully using query from query.properties");
			else
				logger.info("not able to delete mapped device regCenter using query from query.properties");
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
	public void decommisionDevice(TestParameters objTestParameters, String testScenario, String testcaseName) throws AuthenticationTestException, AdminTestException {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		displayContentInFile(testCaseName.listFiles(), "request");
		String url=RunConfigUtil.objRunConfig.getAdminEndPointUrl() + RunConfigUtil.objRunConfig.getDecommisionDevicePath();
		logger.info("******Post request Json to EndPointUrl: " + url+
				 " *******");
		boolean cookieChanged=false;
		if(this.testCaseName.contains("unAuthorizedRole"))
			{
			adminCookie=kernelAuthLib.getAuthForRegistrationOfficer();
			cookieChanged=true;
			}
		putRequestWithParmAndGenerateOuputFileWithCookie(testCaseName.listFiles(), url, "request", "output-1-actual-response", 0, AUTHORIZATHION_COOKIENAME, adminCookie);
		adminCookie = (cookieChanged) ? kernelAuthLib.getAuthForAdmin():adminCookie;
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if(!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
}

	/**
	 * this method is for deleting or updating data inserted or modified data in db
	 * (managing class level data not test case level data)
	 * @throws AdminTestException 
	 */
	@AfterClass(alwaysRun = true)
	public void cleanup() throws AdminTestException {
		String query = queries.get("deleteDevice").toString().replace("role", kernelAuthLib.props.get("admin_user"));
		if (masterDB.executeQuery(queries.get("deleteDevice1").toString(), "masterdata") && masterDB.executeQuery(query, "masterdata"))
			logger.info("deleted 3 created devices successfully");
		else {
			logger.info("not able to delete devices using query from query.properties");
			throw new AdminTestException("DB is not updated properly after decommission, not able to delete data form DB");
		}
	}
}