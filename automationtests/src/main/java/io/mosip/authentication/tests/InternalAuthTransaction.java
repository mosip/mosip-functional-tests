package io.mosip.authentication.tests;

import java.io.File; 
import java.lang.reflect.Field; 
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.fw.dto.AuthTransactionDto;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
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
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.util.Cookie;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * TestNG class to perform internal auth transaction execution
 * 
 * @author Vignesh
 *
 */
public class InternalAuthTransaction extends AuthTestsUtil implements ITest{
	
	private static final Logger logger = Logger.getLogger(InternalAuthTransaction.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	private static String cookieValue;
	private static String getCookieStartTime;
	public String transactionIDColName="request_trn_id";
	public String requestdatetimeColName="request_dtimes";
	public String authtypeCodeColName="auth_type_code";
	public String statusCodeColName="status_code";
	public String statusCommentColName="status_comment";
	public String referenceIdTypeColName="ref_id_type";
	public String entityNameColName="requested_entity_name";
	
	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestType() {
		this.testType = RunConfigUtil.getTestLevel();
		setTestDataPathsAndFileNames(1);
		setConfigurations(this.testType);
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
			f.set(baseTestMethod, InternalAuthTransaction.testCaseName);
			if(!result.isSuccess())
				StoreAuthenticationAppLogs.storeApplicationLog(RunConfigUtil.getInternalAuthSeriveName(), logFileName, getTestFolder());
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
	
	private void internalAuthTransaction(String uin, String apiUrl, String sqlQuery) throws Exception {
		setCookie();
		RestAssured.baseURI = RunConfigUtil.objRunConfig.getEndPointUrl() + apiUrl;
		Response response = RestAssured.given().relaxedHTTPSValidation().cookie(AUTHORIZATHION_COOKIENAME, cookieValue)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).log().all().when()
				.get(RestAssured.baseURI).then().log().all().extract().response();
		logger.info("******Get request url: " + RestAssured.baseURI + " *******");
		Reporter.log("******Get request url: " + RestAssured.baseURI + " *******");
		logger.info("******Get request content: " + response.asString() + " *******");
		Reporter.log("******Get request content: " + response.asString() + " *******");
		AuthTransactionDto[] authTxn = response.jsonPath().getObject("response.authTransactions",
				AuthTransactionDto[].class);
		List<Map<String, String>> listOfAuthTxnRecordsFromDB = DbConnection.getAllDataForQuery(sqlQuery, "IDA");
		if (authTxn.length != listOfAuthTxnRecordsFromDB.size())
			throw new AuthenticationTestException(
					"There mismatch in record between authTransaction list in response and database for uin: " + uin);
		Reporter.log(ReportUtil.getOutputValiReport(verifyAuthTxnAgainstDB(authTxn, listOfAuthTxnRecordsFromDB)));
	}
	
	private Map<String, List<OutputValidationDto>> verifyAuthTxnAgainstDB(AuthTransactionDto[] authTxnDto, List<Map<String, String>> records) {
		Map<String, String> actual = new HashMap<String, String>();
		int actualCount = 0;
		for (AuthTransactionDto authTxn : authTxnDto) {
			actual.put(transactionIDColName + actualCount, authTxn.transactionID);
			actual.put(requestdatetimeColName + actualCount, authTxn.requestdatetime);
			actual.put(authtypeCodeColName + actualCount, authTxn.authtypeCode);
			actual.put(statusCodeColName + actualCount, authTxn.statusCode);
			actual.put(statusCommentColName + actualCount, authTxn.statusComment);
			actual.put(referenceIdTypeColName + actualCount, authTxn.referenceIdType);
			actual.put(entityNameColName + actualCount, authTxn.entityName);
			actualCount++;
		}
		Map<String, String> expected = new HashMap<String, String>();
		for (int i = 0; i < records.size(); i++) {
			expected.put(transactionIDColName + i, records.get(i).get(transactionIDColName));
			String reqTime =records.get(i).get(requestdatetimeColName);
			if(reqTime.endsWith("0"))
				reqTime=reqTime.substring(0,reqTime.length()-1);
			expected.put(requestdatetimeColName + i, records.get(i).get(requestdatetimeColName));
			expected.put(authtypeCodeColName + i, records.get(i).get(authtypeCodeColName));
			expected.put(statusCodeColName + i, records.get(i).get(statusCodeColName));
			expected.put(statusCommentColName + i, records.get(i).get(statusCommentColName));
			expected.put(referenceIdTypeColName + i, records.get(i).get(referenceIdTypeColName));
			expected.put(entityNameColName + i, records.get(i).get(entityNameColName));
		}
		return OutputValidationUtil.compareActuExpValue(actual, expected, testCaseName);
	}
	
	private String getAuthTxnQuery(String hashedValue) {
		return "select request_trn_id,auth_type_code,status_code,status_comment,ref_id_type,requested_entity_name, to_char (request_dtimes, 'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as request_dtimes from ida.auth_transaction where uin_hash='"
				+ hashedValue + "' order by cr_dtimes desc";
	}
	
	private String getAuthTxnQuery(String hashedValue,int pageStart) {
		return "select request_trn_id,auth_type_code,status_code,status_comment,ref_id_type,requested_entity_name, to_char (request_dtimes, 'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as request_dtimes from ida.auth_transaction where uin_hash='"
				+ hashedValue + "' order by cr_dtimes desc limit "+(pageStart*10)+" offset "+((pageStart-1)*10);
	}
	
	private String getAuthTxnQuery(String hashedValue,int pageStart,int pageFetch) {
		return "select request_trn_id,auth_type_code,status_code,status_comment,ref_id_type,requested_entity_name, to_char (request_dtimes, 'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as request_dtimes from ida.auth_transaction where uin_hash='"
				+ hashedValue + "' order by cr_dtimes desc limit "+pageFetch+" offset "+((pageStart-1)*pageFetch);
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
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "ida");
		return DataProviderClass.getDataProvider(
				RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), RunConfigUtil.objRunConfig.getTestType());
	}
	
	/**
	 * Test method for internal auth transaction execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test(dataProvider = "testcaselist")
	public void internalAuthTransaction(TestParameters objTestParameters, String testScenario,
			String testcaseName) throws AuthenticationTestException {
		setCookie();
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		String uinOrVid = JsonPrecondtion.getValueFromJson(getContentFromFile(testCaseName.listFiles(), "request"),
				"uin");
		String idType = JsonPrecondtion.getValueFromJson(getContentFromFile(testCaseName.listFiles(), "request"),
				"type");
		String urlPath = "";
		if (idType.equals("UIN"))
			urlPath = RunConfigUtil.objRunConfig.getEndPointUrl()
					+ RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithUIN().replace("$uin$", uinOrVid);
		else if (idType.equals("VID"))
			urlPath = RunConfigUtil.objRunConfig.getEndPointUrl()
					+ RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithVID().replace("$vid$", uinOrVid);
		else
			urlPath = RunConfigUtil.objRunConfig.getEndPointUrl()
					+ RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithUIN().replace("UIN", idType).replace("$uin$", uinOrVid);
		logger.info("******Get request: " + urlPath + " *******");
		Reporter.log("******Get request: " + urlPath + " *******");
		getRequestAndGenerateOuputFileWithResponse(testCaseName.getAbsolutePath(), urlPath,
				"output-1-actual", AUTHORIZATHION_COOKIENAME, cookieValue);
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AuthenticationTestException("Failed at response output validation");
	}
	
	@Test
	public void performAuthTransactionWithUIN() throws Exception {
		testCaseName = "InternalAuthentication_VerifyAuthTransaction_history_against_database_With UIN_Pos";
		String uin = UINUtil.getRandomUINKey();
		String url = RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithUIN().replace("$uin$", uin);
		internalAuthTransaction(uin, url,getAuthTxnQuery(getUinHashWithSalt(uin)));
	}

	@Test
	public void performAuthTransactionWithVID() throws Exception {
		testCaseName = "InternalAuthentication_VerifyAuthTransaction_history_against_database_With VID_Pos";
		String vid = VIDUtil.getRandomVidKey();
		String url = RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithVID().replace("$vid$", vid);
		internalAuthTransaction(UINUtil.getUinForVid(vid), url,getAuthTxnQuery(getUinHashWithSalt(UINUtil.getUinForVid(vid))));
	}
	
	@Test
	public void performAuthTransactionWithUINAndPageStart() throws Exception {
		testCaseName = "InternalAuthentication_VerifyAuthTransaction_history_against_database_With UIN and PageStart_Pos";
		String uin = UINUtil.getRandomUINKey();
		int pageStart = generateRandomIntRange(1,3);
		String url = RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithUIN().replace("$uin$", uin)
				+ "?pageStart=" + pageStart;
		internalAuthTransaction(uin, url, getAuthTxnQuery(getUinHashWithSalt(uin), pageStart));
	}
	
	@Test
	public void performAuthTransactionWithVIDAndPageStartPageFetch() throws Exception {
		testCaseName = "InternalAuthentication_VerifyAuthTransaction_history_against_database_With VID,PageStart and PageFetch_Pos";
		String vid = VIDUtil.getRandomVidKey();
		int pageStart = generateRandomIntRange(1,3);
		int pageFetch = generateRandomIntRange(1,3);
		String url = RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithVID().replace("$vid$", vid)
				+ "?pageFetch=" + pageFetch + "&pageStart=" + pageStart;
		internalAuthTransaction(UINUtil.getUinForVid(vid), url,
				getAuthTxnQuery(getUinHashWithSalt(UINUtil.getUinForVid(vid)), pageStart, pageFetch));
	}
	
	@Test
	public void performAuthTransactionWithUINAndPageStartPageFetch() throws Exception {
		testCaseName = "InternalAuthentication_VerifyAuthTransaction_history_against_database_With UIN,PageStart and PageFetch_Pos";
		String uin = UINUtil.getRandomUINKey();
		int pageStart = generateRandomIntRange(1,3);
		int pageFetch = generateRandomIntRange(1,3);
		String url = RunConfigUtil.objRunConfig.getIdaInternalAuthTransactionWithUIN().replace("$uin$", uin)
				+ "?pageFetch=" + pageFetch + "&pageStart=" + pageStart;
		internalAuthTransaction(uin, url, getAuthTxnQuery(getUinHashWithSalt(uin), pageStart, pageFetch));
	}

}
