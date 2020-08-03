package io.mosip.authentication.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestContext;
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

import io.mosip.authentication.fw.dto.AuthTypeStatusDto;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuditValidation;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.DbConnection;
import io.mosip.authentication.fw.util.EncryptDecrptUtil;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.StoreAuthenticationAppLogs;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.UINUtil;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;
import io.mosip.util.Cookie;

public class LockUIN extends AuthTestsUtil implements ITest{
	
	private static final Logger logger = Logger.getLogger(LockUIN.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	private static String cookieValue;
	private static String getCookieStartTime;
	private Map<String, String> storeUinVidLockStatusData = new HashMap<String, String>();

	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestType() {
		this.testType = RunConfigUtil.getTestLevel();
	}
	
	public void setCookie() {
		cookieValue = getAuthorizationCookie(getCookieRequestFilePathForResidentAuth(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
		getCookieStartTime = Cookie.getCookieCurrentDateTime();
	}

	/**
	 * Method set Test data path and its filename
	 * 
	 * @param index
	 */
	public void setTestDataPathsAndFileNames(int index) {
		this.TESTDATA_PATH = getTestDataPath(this.getClass().getSimpleName().toString(), index);
		this.TESTDATA_FILENAME = getTestDataFileName(this.getClass().getSimpleName().toString(), index);
	}

	/**
	 * Method set configuration
	 * 
	 * @param testType
	 */
	public void setConfigurations(String testType) {
		RunConfigUtil.getRunConfigObject("ida");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "ida");
	}

	/**
	 * The method set test case name
	 * 
	 * @param method
	 * @param testData
	 */
	@BeforeMethod
	public void testData(Method method, Object[] testData, ITestContext c) {
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
		this.testCaseName = String.format(testCase);	
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
		setConfigurations(this.testType);
		setCookie();
		return DataProviderClass.getDataProvider(
				RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), RunConfigUtil.objRunConfig.getTestType());
	}

	/**
	 * Set current testcaseName
	 */
	@Override
	public String getTestName() {
		return this.testCaseName;
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
			f.set(baseTestMethod, LockUIN.testCaseName);
			if(!result.isSuccess())
				StoreAuthenticationAppLogs.storeApplicationLog(RunConfigUtil.getInternalAuthSeriveName(), logFileName, getTestFolder());
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Test method for biometric authentication execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException 
	 */
	@Test(dataProvider = "testcaselist")
	public void lockUINTestMethod(TestParameters objTestParameters, String testScenario,
			String testcaseName) throws AuthenticationTestException {
		setCookie();
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		logger.info("************* Modification of internal update auth type request ******************");
		Reporter.log("<b><u>Modification of bio auth request</u></b>");
		displayContentInFile(testCaseName.listFiles(), "request");
		logger.info("******Post request Json to EndPointUrl: " + RunConfigUtil.objRunConfig.getEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdaInternalUpdateAuthTypePath() + " *******");
		if (!postRequestAndGenerateOuputFileForIntenalAuth(testCaseName.listFiles(),
				RunConfigUtil.objRunConfig.getEndPointUrl()
						+ RunConfigUtil.objRunConfig.getIdaInternalUpdateAuthTypePath(),
				"request", "output-1-actual-res", AUTHORIZATHION_COOKIENAME, cookieValue, 200))
			throw new AuthenticationTestException("Failed at HTTP-POST request");
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AuthenticationTestException("Failed at response output validation");
		if (OutputValidationUtil.publishOutputResult(ouputValid)
				&& testcaseName.toLowerCase().endsWith("_Pos".toLowerCase())) {
			String content = getContentFromFile(testCaseName.listFiles(), "request");
			String inputFilePath = FileUtil.getFileFromList(testCaseName.listFiles(), "request").getAbsolutePath();
			String uin = JsonPrecondtion.getValueFromJson(inputFilePath, mapping, "individualId");
			String type = JsonPrecondtion.getValueFromJson(inputFilePath, mapping, "individualIdType");
			String authType = JsonPrecondtion.getValueFromJson(inputFilePath, mapping, "authTyperequest");
			String status = JsonPrecondtion.getValueFromJson(inputFilePath, mapping, "lockedrequest");
			if (content.contains("authSubType")) {
				String authSubType = JsonPrecondtion.getValueFromJson(inputFilePath, mapping, "authSubTyperequest");
				if (!verifyAuthStatusTypeInDB(uin, type,authType + "-" + authSubType,"true"))
					throw new AuthenticationTestException("True value is not updated in status code in DB for uin/vid: "
							+ uin + " and type" + authType + "-" + authSubType);
				else
					storeUinVidLockStatusData.put(type + "." + authType + "." + authSubType + "." + status, uin);
			} else {
				if (!verifyAuthStatusTypeInDB(uin,type, authType,"true"))
					throw new AuthenticationTestException(
							"True value is not updated in status code in DB for uin/vid: " + uin + " and type" + authType);
				else
					storeUinVidLockStatusData.put(type + "." + authType + "." + status, uin);

			}
		}
	}
	
	/**
	 * The method store or modify updated UIN/VID status in property file
	 * 
	 */
	@AfterClass
	public void storeAuthTypeStatusData() {
		AuthTypeStatusDto.setAuthTypeStatus(storeUinVidLockStatusData);
		logger.info("Updated Auth Type Status: " + AuthTypeStatusDto.getAuthTypeStatus());
		generateMappingDic(RunConfigUtil.getAuthTypeStatusPath(), AuthTypeStatusDto.getAuthTypeStatus());
		generateMappingDic(new File(RunConfigUtil.getResourcePath() + "idRepository/" + RunConfigUtil.objRunConfig.getTestDataFolderName()
		+ "/RunConfig/authTypeStatus.properties").getAbsolutePath(), AuthTypeStatusDto.getAuthTypeStatus());
		String uinForResidentAuthLock = storeUinVidLockStatusData.get("UIN.otp.true");
		storeUinVidLockStatusData.remove("UIN.otp.true");
		storeUinVidLockStatusData.put("UIN.all.true", uinForResidentAuthLock);
		String vidForResidentAuthLock = storeUinVidLockStatusData.get("VID.otp.true");
		storeUinVidLockStatusData.remove("VID.otp.true");
		storeUinVidLockStatusData.put("VID.all.true", vidForResidentAuthLock);
		generateMappingDic(new File(RunConfigUtil.getResourcePath() + "resident/" + RunConfigUtil.objRunConfig.getTestDataFolderName()
		+ "/RunConfig/authTypeStatus.properties").getAbsolutePath(), AuthTypeStatusDto.getAuthTypeStatus());
		
	}

}
