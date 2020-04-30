package io.mosip.admin.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

public class DeviceRegister extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(DeviceRegister.class);
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
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "testcaselist")
	public void deviceRegister(TestParameters objTestParameters, String testScenario, String testcaseName) throws AuthenticationTestException, AdminTestException {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		displayContentInFile(testCaseName.listFiles(), "request");
		File inputFile = FileUtil.getFilePath(testCaseName, "request");
		
		try {
			JSONObject data = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
			JSONObject deviceInfo = (JSONObject) data.get("deviceInfo");
			JSONObject digitalId = (JSONObject) deviceInfo.get("digitalId");
			String digitalIdString = digitalId.toJSONString();
			String encodedDigIdStr = Base64.getEncoder().encodeToString(digitalIdString.getBytes());
			deviceInfo.put("digitalId", encodedDigIdStr);
			data.put("deviceInfo", deviceInfo);
			String encodedData = Base64.getEncoder().encodeToString(data.toJSONString().getBytes());
			File finalInput = new File(getGlobalResourcePath()+"/"+TESTDATA_PATH+"/final-request.json");
			JSONObject finalJsonData = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(finalInput), "UTF-8"));
			JSONObject requestField = (JSONObject) finalJsonData.get("request");
			requestField.put("deviceData", encodedData);
			finalJsonData.put("request", requestField);
			new FileUtil().writeOutput(inputFile.getAbsolutePath(), finalJsonData.toJSONString());
			
		} catch (IOException | ParseException e) {
			logger.info(e.getMessage());
			throw new AdminTestException(e.getMessage());
		}
		String url = RunConfigUtil.objRunConfig.getAdminEndPointUrl() + RunConfigUtil.objRunConfig.getDeviceRegisterPath();
		logger.info("******Post request Json to EndPointUrl: " + url+" *******");
		postRequestAndGenerateOuputFileWithCookie(testCaseName.listFiles(), url,"request", "output-1-actual-response", 0, AUTHORIZATHION_COOKIENAME,
				adminCookie);
		if (DeviceRegister.testCaseName.contains("_Valid_")) {
			File actualOutput = FileUtil.getFilePath(testCaseName, "output-1-actual");
			try {
				JSONObject finalJsonData = (JSONObject) new JSONParser()
						.parse(new InputStreamReader(new FileInputStream(actualOutput), "UTF-8"));
				if(finalJsonData.get("errors")==null) {
				String encodeResponse = (String) finalJsonData.get("response");
				String decodedMainData = new String(
						Base64.getDecoder().decode(encodeResponse.split("\\.")[1].getBytes()));
				JSONObject decodedMainJson = (JSONObject) new JSONParser().parse(decodedMainData);
				String encodedDigitalId = (String) decodedMainJson.get("digitalId");
				String decodedDigitalId = new String(Base64.getDecoder().decode(encodedDigitalId.getBytes()));
				JSONObject digitalId = (JSONObject) new JSONParser().parse(decodedDigitalId);
				decodedMainJson.put("digitalId", digitalId);
				new FileUtil().writeOutput(actualOutput.getAbsolutePath(), decodedMainJson.toJSONString());
			}
			}catch (IOException | ParseException e) {
				logger.info(e.getMessage());
				throw new AdminTestException(e.getMessage());
			}
			
		}
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if(!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");	
		
	}
	/**
	 * this method is for deleting or updating the inserted data in db for testing
	 * (managing class level data not test case level data)
	 * @throws AdminTestException 
	 */
	@AfterClass(alwaysRun = true)
	public void cleanup() throws AdminTestException {
		String historyMsg = "", deleteMsg = "";
		if (masterDB.validateDBCount(queries.get("validateHistory").toString(), "masterdata")==4)
			logger.info("history for registered devices validated successfully");
		else historyMsg="failed in History validation for registered devices";
		
		String histDelQuery = queries.get("DeleteRegDeviceCreatedByApi").toString().replace("registered_device_master", "registered_device_master_h");
		if (masterDB.executeQuery(queries.get("DeleteRegDeviceCreatedByApi").toString(), "masterdata")&&masterDB.executeQuery(histDelQuery, "masterdata"))
			logger.info("deleted created RegDevice and it's history successfully");
		else {
			logger.info("not able to delete RegDevice and history using query from query.properties");
			deleteMsg= "not able to delete RegDevice and history using query from query.properties";
		}
		if (historyMsg != "" || deleteMsg != "" )
			throw new AdminTestException(historyMsg+" "+deleteMsg);
		}
}
