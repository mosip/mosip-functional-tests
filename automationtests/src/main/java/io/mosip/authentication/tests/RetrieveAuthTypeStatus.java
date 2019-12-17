package io.mosip.authentication.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.dto.AuthTypeStatusDto;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.DbConnection;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.StoreAuthenticationAppLogs;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.UINUtil;
import io.netty.util.Constant;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * The testng class to perform retrieve auth type status service execution
 * 
 * @author Vignesh
 *
 */

public class RetrieveAuthTypeStatus extends AuthTestsUtil implements ITest{
	
	private static final Logger logger = Logger.getLogger(RetrieveAuthTypeStatus.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	private static String cookieValue;
	private static String authTypeColName="auth_type_code";
	private static String authSubTypeColName="authSubType";
	private static String lockedColName="status_code";
	private static List<String> uinKey = new ArrayList<String>();
	private static List<String> vidKey = new ArrayList<String>();

	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestType() {
		this.testType = RunConfigUtil.getTestLevel();
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(this.testType);
	}
	
	public void setCookie() {
		cookieValue = getAuthorizationCookie(getCookieRequestFilePathForResidentAuth(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
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
		RunConfigUtil.getRunConfigObject("ida");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		//TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "ida");
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
			f.set(baseTestMethod, RetrieveAuthTypeStatus.testCaseName);
			if(!result.isSuccess())
				StoreAuthenticationAppLogs.storeApplicationLog(RunConfigUtil.getInternalAuthSeriveName(), logFileName, getTestFolder());
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
	
	@Test
	public void verifyAuthTypeStatusForUin() throws AuthenticationTestException {
		testCaseName = "InternalAuthentication_RetrieveAuthTypeStatus_UIN_Pos";
		if (this.testType.equalsIgnoreCase("smoke"))
			throw new SkipException("This test not eligible to run as part of sanity or smoke execution");
		else {
			uinKey.add("UIN.demo.true");
			uinKey.add("UIN.bio.IIR.true");
			uinKey.add("UIN.bio.FACE.true");
			uinKey.add("UIN.bio.FIR.true");
			uinKey.add("UIN.otp.true");
			String value = AuthTestsUtil.getValueFromPropertyFile(RunConfigUtil.getAuthTypeStatusPath(),
					uinKey.get(new Random().nextInt(uinKey.size() - 1)));
			String url = RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig
					.getIdaInternalRetrieveAuthTypeStatusPathForUIN().replace("$uin$", value);
			retrieveAuthTypeStatus(value, url);
		}
	}
	
	@Test
	public void verifyAuthTypeStatusForVid() throws AuthenticationTestException {
		testCaseName = "InternalAuthentication_RetrieveAuthTypeStatus_VID_Pos";
		if (this.testType.equalsIgnoreCase("smoke"))
			throw new SkipException("This test not eligible to run as part of sanity or smoke execution");
		else {
			vidKey.add("VID.demo.true");
			vidKey.add("VID.bio.IIR.true");
			vidKey.add("VID.bio.FACE.true");
			vidKey.add("VID.bio.FIR.true");
			vidKey.add("VID.otp.true");
			String value = AuthTestsUtil.getValueFromPropertyFile(RunConfigUtil.getAuthTypeStatusPath(),
					vidKey.get(new Random().nextInt(vidKey.size() - 1)));
			String url = RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig
					.getIdaInternalRetrieveAuthTypeStatusPathForVID().replace("$vid$", value);
			String uinValue = UINUtil.getUinForVid(value);
			retrieveAuthTypeStatus(uinValue, url);
		}
	}

	/**
	 * The method to perform test retreive auth type service 
	 * 
	 * @param uin
	 * @param apiUrl
	 * @throws AuthenticationTestException
	 */
	private void retrieveAuthTypeStatus(String uin, String apiUrl) throws AuthenticationTestException {
		setCookie();
		Reporter.log("GET URL: " + apiUrl);
		RestAssured.baseURI = apiUrl;
		RequestSpecification request = RestAssured.given().cookie(AUTHORIZATHION_COOKIENAME, cookieValue)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		System.out.println(response.asString());
		AuthTypeStatusDto[] authTxn = response.jsonPath().getObject("response.authTypes", AuthTypeStatusDto[].class);
		List<Map<String, String>> listOfAuthTypeStatusFromDB = DbConnection.getAllDataForQuery(
				getAuthTypeStatusQuery(uin), "IDA");
		if (authTxn.length != listOfAuthTypeStatusFromDB.size())
			throw new AuthenticationTestException(
					"There mismatch in record between authType Status against DB list in response and database");
		Map<String, List<OutputValidationDto>> outputResultVerification = verifyAuthTypeStatusAgainstDB(authTxn,
				listOfAuthTypeStatusFromDB);
		Reporter.log(ReportUtil.getOutputValiReport(outputResultVerification));
		if (!OutputValidationUtil.publishOutputResult(outputResultVerification))
			throw new AuthenticationTestException("Mismatch in record in DB and response from api");
	}
	
	
	private Map<String, List<OutputValidationDto>> verifyAuthTypeStatusAgainstDB(AuthTypeStatusDto[] authTypeStatusDto,
			List<Map<String, String>> records) {
		Map<String, String> actual = new HashMap<String, String>();
		int actualCount = 0;
		for (AuthTypeStatusDto authTypeStatus : authTypeStatusDto) {
			actual.put(authTypeColName + actualCount, authTypeStatus.authType);
			String authSubType="";
			if(authTypeStatus.authSubType==null || authTypeStatus.authSubType.equals(null))
				authSubType="null";
			else
				authSubType=authTypeStatus.authSubType;
			actual.put(authSubTypeColName + actualCount, authSubType);
			actual.put(lockedColName + actualCount, authTypeStatus.locked);
			actualCount++;
		}
		Map<String, String> expected = new HashMap<String, String>();
		for (int i = 0; i < records.size(); i++) {
			String authType = records.get(i).get(authTypeColName);
			String authSubType = "";
			if (authType.contains("-")) {
				authSubType = authType.split(Pattern.quote("-"))[1];
				authType = authType.split(Pattern.quote("-"))[0];
			} else
				authSubType = "null";
			expected.put(authTypeColName + i, authType);
			expected.put(authSubTypeColName + i, authSubType);
			expected.put(lockedColName + i, records.get(i).get(lockedColName));
		}
		return OutputValidationUtil.compareActuExpValue(actual, expected, testCaseName);
	}
	
	/*private String getAuthTypeStatusQuery(String uin) {
		return "select * from ida.uin_auth_lock where lock_request_datetime in ((select lock_request_datetime from ida.uin_auth_lock where uin = '"
				+ uin + "' and auth_type_code = 'demo' order by cr_dtimes desc limit 1),"
				+ " (select lock_request_datetime from ida.uin_auth_lock where uin = '" + uin
				+ "' and auth_type_code = 'otp' order by cr_dtimes desc limit 1),"
				+ " (select lock_request_datetime from ida.uin_auth_lock where uin = '" + uin
				+ "' and auth_type_code = 'bio-FACE' order by cr_dtimes desc limit 1),"
				+ " (select lock_request_datetime from ida.uin_auth_lock where uin = '" + uin
				+ "' and auth_type_code = 'bio-FIR' order by cr_dtimes desc limit 1),"
				+ " (select lock_request_datetime from ida.uin_auth_lock where uin = '" + uin
				+ "' and auth_type_code = 'bio-IIR' order by cr_dtimes desc limit 1)" + ") order by cr_dtimes desc";
	}*/
	
	private String getAuthTypeStatusQuery(String uin) {
		return "select t.* " + 
				"from  ida.uin_auth_lock t " + 
				"inner join ( " + 
				"    select auth_type_code, MAX(cr_dtimes) as crd " + 
				"    from ida.uin_auth_lock " + 
				"    group by uin_hash, auth_type_code " + 
				") tm on t.auth_type_code = tm.auth_type_code and t.cr_dtimes = tm.crd " + 
				"where t.uin ='"+uin+"'";
	}

}
