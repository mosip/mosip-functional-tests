package io.mosip.resident.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.dto.AuthTransactionDto;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuditValidation;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.DbConnection;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.StoreAuthenticationAppLogs;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.UINUtil;
import io.mosip.authentication.fw.util.VIDUtil;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;
import io.mosip.authentication.tests.OtpAuthentication;
import io.mosip.resident.fw.util.AuthHistoryDto;
import io.mosip.resident.fw.util.ResidentTestUtil;
import io.mosip.util.Cookie;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Funtional Test for Auth History -Resident service
 * 
 * @author Vignesh
 *
 */
public class AuthHistory extends ResidentTestUtil implements ITest{
	
	private static final Logger logger = Logger.getLogger(AuthHistory.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	private static String cookieValue;
	private static String residentCookieValue;
	//col names
	public String serialNumber="serialNumber";
	public String idUsed="ref_id_type";
	public String authModality="auth_type_code";
	public String dateTime="request_dtimes";
	public String date="date";
	public String time="time";
	public String partnerName="requested_entity_id";
	public String partnerTransactionId="request_trn_id";
	public String authResponse="status_comment";
	public String responseCode="status_code";
	
	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestTypeAndDbConnection() {
		this.testType = RunConfigUtil.getTestLevel();
	}
	
	public void setCookie() {
		cookieValue = getAuthorizationCookie(getCookieRequestFilePath(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
	}
	
	public void getResidentAccess() {
		residentCookieValue = getAuthorizationCookie(getCookieRequestFilePathForResidentAuth(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
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
		RunConfigUtil.getRunConfigObject("resident");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "resident");
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
			f.set(baseTestMethod, AuthHistory.testCaseName);
			if(!result.isSuccess())
				StoreAuthenticationAppLogs.storeApplicationLog(RunConfigUtil.getAuthSeriveName(), logFileName, getTestFolder());
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Test method for OTP authentication execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws Exception 
	 */
	@Test(dataProvider = "testcaselist")
	public void residentAuthHistoryNeg(TestParameters objTestParameters, String testScenario, String testcaseName)
			throws Exception {
		setCookie();
		getResidentAccess();
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		logger.info("*************Otp generation request ******************");
		Reporter.log("<b><u>Otp generation request</u></b>");
		displayContentInFile(testCaseName.listFiles(), "otp-generate");
		logger.info("******Post request Json to EndPointUrl: " + RunConfigUtil.objRunConfig.getEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdaInternalOtpPath() + " *******");
		if (!postRequestAndGenerateOuputFileForIntenalAuth(testCaseName.listFiles(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getIdaInternalOtpPath(),
				"otp-generate", "output-1-actual-res", AUTHORIZATHION_COOKIENAME, residentCookieValue, 200))
			throw new AuthenticationTestException("Failed at HTTP-POST otp-generate-request");
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AuthenticationTestException("Failed at otp-generate-response output validation");
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("pinInfovalue", getOtpValue(
				FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath(), mapping, "pinInfovalue"));
		Reporter.log("<b><u>Modification of otp value in authHistory-request</u></b>");
		if (!modifyRequest(testCaseName.listFiles(), tempMap, mapping, "authHistory-request"))
			throw new AuthenticationTestException(
					"Failed at modifying the otp value in authHistory-request file. Kindly check testdata.");
		displayContentInFile(testCaseName.listFiles(), "authHistory-request");
		logger.info("******Post request Json to EndPointUrl: " + RunConfigUtil.objRunConfig.getEndPointUrl()
				+ RunConfigUtil.objRunConfig.getResidentAuthHistory() + " *******");
		if (testCaseName.getName().toLowerCase().endsWith("_pos")) {
			Response response = postRequestAndGenerateOuputFileAndReturnResponse(testCaseName.listFiles(),
					RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getResidentAuthHistory(),
					"authHistory-request", "output-2-actual-res", AUTHORIZATHION_COOKIENAME, residentCookieValue, 200);
			String uin = JsonPrecondtion.getJsonValueFromJson(
					FileUtil.readInput(FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath()),
					"request.individualId");
			uin=uin.replace("\"", "");
			if (testCaseName.getName().equals("Resident_AuthHistory_With_UIN_smoke_Pos")) {
				testAuthHistoryWithUIN(uin, response);
			}
			else if (testCaseName.getName().equals("Resident_AuthHistory_With_VID_smoke_Pos")) {
				testAuthHistoryWithVID(uin, response);
			}
			else if (testCaseName.getName().equals("Resident_AuthHistory_With_UIN_PageStart_pageFetch_Pos")) {
				String ps = JsonPrecondtion.getJsonValueFromJson(
						FileUtil.readInput(FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath()),
						"request.pageStart");
				ps=ps.replace("\"", "");
				String pf = JsonPrecondtion.getJsonValueFromJson(
						FileUtil.readInput(FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath()),
						"request.pageFetch");
				pf=pf.replace("\"", "");
				testAuthHistoryWithUINAndPageStartPageFetch(uin, response,Integer.parseInt(ps),Integer.parseInt(pf));
			}
			else if (testCaseName.getName().equals("Resident_AuthHistory_With_VID_PageStart_pageFetch_Pos")) {
				String ps = JsonPrecondtion.getJsonValueFromJson(
						FileUtil.readInput(FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath()),
						"request.pageStart");
				ps=ps.replace("\"", "");
				String pf = JsonPrecondtion.getJsonValueFromJson(
						FileUtil.readInput(FileUtil.getFilePath(testCaseName, "authHistory-request").getAbsolutePath()),
						"request.pageFetch");
				pf=pf.replace("\"", "");
				testAuthHistoryWithVIDAndPageStartPageFetch(uin, response,Integer.parseInt(ps),Integer.parseInt(pf));
			}
		} else {
			if (!postRequestAndGenerateOuputFileForIntenalAuth(testCaseName.listFiles(),
					RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getResidentAuthHistory(),
					"authHistory-request", "output-2-actual-res", AUTHORIZATHION_COOKIENAME, residentCookieValue, 200))
				throw new AuthenticationTestException("Failed at HTTP-POST authHistory-request");
			Map<String, List<OutputValidationDto>> ouputValid2 = OutputValidationUtil.doOutputValidation(
					FileUtil.getFilePath(testCaseName, "output-2-actual").toString(),
					FileUtil.getFilePath(testCaseName, "output-2-expected").toString());
			Reporter.log(ReportUtil.getOutputValiReport(ouputValid2));
			if (!OutputValidationUtil.publishOutputResult(ouputValid2))
				throw new AuthenticationTestException("Failed at authHistory-request output validation");
		}
	}
	
	
	private String getAuthTxnQuery(String hashedValue) {
		return "select * from ida.auth_transaction where uin_hash='"
				+ hashedValue + "' order by cr_dtimes desc";
	}	
	private String getAuthTxnQuery(String hashedValue,int pageStart,int pageFetch) {
		return "select * from ida.auth_transaction where uin_hash='"
				+ hashedValue + "' order by cr_dtimes desc limit "+pageFetch+" offset "+((pageStart-1)*pageFetch);
	}	
	private void testAuthHistoryWithUIN(String uin,Response response) throws Exception{
		testAuthHistory(uin, response,getAuthTxnQuery(getUinHashWithSalt(uin)));
	}
	private void testAuthHistoryWithUINAndPageStartPageFetch(String uin,Response response,int pageStart,int pageFetch) throws Exception{
		testAuthHistory(uin, response,getAuthTxnQuery(getUinHashWithSalt(uin),pageStart,pageFetch));
	}
	private void testAuthHistoryWithVIDAndPageStartPageFetch(String uin,Response response,int pageStart,int pageFetch) throws Exception{
		testAuthHistory(UINUtil.getUinForVid(uin), response,getAuthTxnQuery(getUinHashWithSalt(UINUtil.getUinForVid(uin)),pageStart,pageFetch));
	}
	private void testAuthHistoryWithVID(String uin,Response response) throws Exception{
		testAuthHistory(UINUtil.getUinForVid(uin), response,getAuthTxnQuery(getUinHashWithSalt(UINUtil.getUinForVid(uin))));
	}
	
	private void testAuthHistory(String uin, Response response, String sqlQuery) throws Exception {
		AuthHistoryDto[] authTxn = response.jsonPath().getObject("response.authHistory", AuthHistoryDto[].class);
		List<Map<String, String>> listOfAuthTxnRecordsFromDB = DbConnection.getAllDataForQuery(sqlQuery, "IDA");
		if (authTxn.length != listOfAuthTxnRecordsFromDB.size())
			throw new AuthenticationTestException(
					"There mismatch in record between authTransaction list in response and database for uin: " + uin);
		Reporter.log(ReportUtil.getOutputValiReport(verifyAuthTxnAgainstDB(authTxn, listOfAuthTxnRecordsFromDB)));
	}
	
	private Map<String, List<OutputValidationDto>> verifyAuthTxnAgainstDB(AuthHistoryDto[] authHistoryDto,
			List<Map<String, String>> records) {
		Map<String, String> actual = new HashMap<String, String>();
		int actualCount = 0;
		for (AuthHistoryDto authHistory : authHistoryDto) {
			actual.put(serialNumber + actualCount, authHistory.serialNumber);
			actual.put(idUsed + actualCount, authHistory.idUsed);
			actual.put(authModality + actualCount, authHistory.authModality);
			actual.put(date + actualCount, authHistory.date);
			actual.put(time + actualCount, authHistory.time);
			actual.put(partnerName + actualCount, authHistory.partnerName);
			actual.put(partnerTransactionId + actualCount, authHistory.partnerTransactionId);
			actual.put(authResponse + actualCount, authHistory.authResponse);
			actual.put(responseCode + actualCount, authHistory.responseCode);
			actualCount++;
		}
		Map<String, String> expected = new HashMap<String, String>();
		for (int i = 0; i < records.size(); i++) {
			expected.put(serialNumber + i, String.valueOf(i + 1));
			expected.put(idUsed + i, records.get(i).get(idUsed));
			String reqTime[] = records.get(i).get(dateTime).split(Pattern.quote(" "));
			expected.put(date + i, reqTime[0]);
			expected.put(time + i, reqTime[1]);
			expected.put(authModality + i, records.get(i).get(authModality));
			expected.put(partnerName + i, records.get(i).get(partnerName));
			expected.put(partnerTransactionId + i, records.get(i).get(partnerTransactionId));
			expected.put(authResponse + i, records.get(i).get(authResponse));
			expected.put(responseCode + i, records.get(i).get(responseCode));
		}
		return OutputValidationUtil.compareActuExpValue(actual, expected, testCaseName);
	}

}
