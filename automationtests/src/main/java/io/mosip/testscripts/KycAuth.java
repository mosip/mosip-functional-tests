package io.mosip.testscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.EncryptionDecrptionUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthPartnerProcessor;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.kernel.util.ConfigManager;
import io.restassured.response.Response;

public class KycAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(KycAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;

	@BeforeClass
	public static void setPrerequiste() {
//		logger.info("Starting authpartner demo service...");
		
		
//	AuthPartnerProcessor.startProcess();
	}

	/**
	 * get current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		isInternal = Boolean.parseBoolean(context.getCurrentXmlTest().getLocalParameters().get("isInternal"));
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
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
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
		JSONObject request = new JSONObject(testCaseDTO.getInput());
//		String jsonrequest = buildIdentityRequest(request.toString());
//		String input = getJsonFromTemplate(jsonrequest, testCaseDTO.getInputTemplate());
		
		String kycAuthEndPoint = null;
		if (request.has("kycAuthEndPoint")) {
			kycAuthEndPoint = request.get("kycAuthEndPoint").toString();
			request.remove("kycAuthEndPoint");
		}
		
		String input = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		
		String url = ConfigManager.getAuthDemoServiceUrl();
		
		logger.info("******Post request Json to EndPointUrl: " + url + testCaseDTO.getEndPoint() + " *******");
		
		Response authResponse = null;
		
		authResponse = postWithBodyAndCookieWithText(url + testCaseDTO.getEndPoint(), input,
				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		
		String signature = authResponse.getHeader("signature");
		
		System.out.println(signature);
		
		String authResBody = authResponse.getBody().asString();
		
		System.out.println(authResBody);
		
		JSONObject responseBody = new JSONObject(authResponse.getBody().asString());
		
		String requestJson = null;
//		try {
//			requestJson = bioDataUtil.constructBiorequest(input, getResourcePath() + props.getProperty("bioValueEncryptionTemplate"), isInternal, testCaseName);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			logger.error("Exception while signing oidcJWKKey for client assertion: " + e1.getMessage());
////			e1.printStackTrace();
//		}
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(SIGNATURE_HEADERNAME, signature);
		String token = kernelAuthLib.getTokenByRole(testCaseDTO.getRole());
		
		
		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");
		
		response = postRequestWithAuthHeaderAndSignatureForOtp(ApplnURI + kycAuthEndPoint, authResBody,
				COOKIENAME, token, headers, testCaseDTO.getTestCaseName());

//		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
//				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));

		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

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

	@AfterClass
	public static void authTestTearDown() {
//		logger.info("Terminating authpartner demo application...");
//		AuthPartnerProcessor.authPartherProcessor.destroyForcibly();
	}

	/*
	 * private static void storeValue(Map<String, String> bioAuthTempMap) {
	 * Properties properties = new Properties(); for (Map.Entry<String,String> entry
	 * : bioAuthTempMap.entrySet()) { properties.put(entry.getKey(),
	 * entry.getValue()); } try { properties.store(new
	 * FileOutputStream("data.properties"), null); } catch (FileNotFoundException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); } }
	 */
}
