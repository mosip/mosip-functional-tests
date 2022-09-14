package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
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
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthPartnerProcessor;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.restassured.response.Response;

public class MultiFactorAuthNew extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(MultiFactorAuthNew.class);
	protected String testCaseName = "";
	public Response response = null;
	
	@BeforeClass
	public static void setPrerequiste() {
		logger.info("Starting authpartner demo service...");
//		AuthPartnerProcessor.startProcess();
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
		logger.info("Started executing yml: "+ymlFile);
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
		String endPoint = testCaseDTO.getEndPoint();
		endPoint = uriKeyWordHandelerUri(endPoint, testCaseName );
		
		String individualId = "";
		
		if(endPoint.contains("id="))
		{
			int lengthOfEndPoint = endPoint.length();
			int charOfId = endPoint.indexOf("id=");
			
			
			for (int i = charOfId + 3 ; i < lengthOfEndPoint ; i++) {
				if (endPoint.charAt(i)!= '&') {
					char eachChar = endPoint.charAt(i);
					individualId += eachChar;
				}
				else {
					break;
				}
				}
		}
		System.out.println(individualId);
		if(testCaseDTO.getEndPoint().contains("$partnerKeyURL$"))
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$partnerKeyURL$", props.getProperty("partnerKeyURL")));
		}
		
		String input = testCaseDTO.getInput();
		
		String url = null;
		InetAddress inetAddress = null;
			try {
				inetAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		url =  "http://"+inetAddress.getHostName().toLowerCase()+":"+props.getProperty("encryptUtilPort");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("id", individualId);
		String token = kernelAuthLib.getTokenByRole("regproc");
		
		Response sendOtpReqResp = RestClient.postRequestWithQueryParm(url + "/v1/identity/createOtpReqest", requestBody, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, "Authorization", token);
				
		
		String otpInput = sendOtpReqResp.getBody().asString();
		System.out.println(otpInput);
		String signature = sendOtpReqResp.getHeader("signature");
		Object sendOtpBody=otpInput;  
		//JSONObject sendOtpBody = new JSONObject(otpInput);
		System.out.println(sendOtpBody);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(AUTHORIZATHION_HEADERNAME, token);
		headers.put(SIGNATURE_HEADERNAME, signature);

		
		Response otpRespon = RestClient.postRequestWithMultipleHeaders(ApplnURI + "/idauthentication/v1/otp/mXKMu0fM6IR3BKR6nhtEQczviTkoQLL0hMtTmGEMUCbfdXQQrm/mosip-9167/292749", sendOtpBody,  MediaType.APPLICATION_JSON,  MediaType.APPLICATION_JSON, "Authorization", token, headers);
		
		
		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null, sendOtpResTemplate = null;
		if(res.has("sendOtpResp")) {
			sendOtpResp = res.get("sendOtpResp").toString();
			res.remove("sendOtpResp");
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil
				.doJsonOutputValidation(otpRespon.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate));
		Reporter.log(ReportUtil.getOutputValiReport(ouputValidOtp));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
			throw new AdminTestException("Failed at Send OTP output validation");
		
		input = buildIdentityRequest(input);
		
		String authRequest = getJsonFromTemplate(input, testCaseDTO.getInputTemplate());
		
				
		logger.info("******Post request Json to EndPointUrl: " + url + testCaseDTO.getEndPoint() + " *******");		
		
		response = RestClient.postRequest(url + testCaseDTO.getEndPoint(), authRequest, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON );
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		
		/*
		 * if(testCaseName.toLowerCase().contains("kyc"))
		 * encryptDecryptUtil.validateThumbPrintAndIdentity(response,
		 * testCaseDTO.getEndPoint());
		 */
		
		//if(!encryptDecryptUtil.verifyResponseUsingDigitalSignature(response.asString(), response.getHeader(props.getProperty("signatureheaderKey"))))
			//throw new AdminTestException("Failed at Signature validation");

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
		logger.info("Terminating authpartner demo application...");
//		AuthPartnerProcessor.authPartherProcessor.destroyForcibly();
	}
}
