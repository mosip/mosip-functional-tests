package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestException;
import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.dto.OutputValidationDto;
import io.mosip.testrig.apirig.authentication.fw.precon.JsonPrecondtion;
import io.mosip.testrig.apirig.authentication.fw.util.AuthPartnerProcessor;
import io.mosip.testrig.apirig.authentication.fw.util.AuthenticationTestException;
import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.ida.certificate.PartnerRegistration;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.restassured.response.Response;

public class MultiFactorAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(MultiFactorAuth.class);
	protected String testCaseName = "";
	public Response response = null;

	@BeforeClass
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
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
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		JSONObject req = new JSONObject(testCaseDTO.getInput());
		if (testCaseDTO.getEndPoint().contains(GlobalConstants.$PARTNERKEYURL$)) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace(GlobalConstants.$PARTNERKEYURL$,
					properties.getProperty("partnerKeyURL")));
		}
		
		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$", PartnerRegistration.updatedpartnerKeyUrl));
		}
		String otpRequest = null;
		String sendOtpReqTemplate = null;
		String sendOtpEndPoint = null;
		String otpIdentyEnryptRequestPath = null;
		if (req.has(GlobalConstants.SENDOTP)) {
			otpRequest = req.get(GlobalConstants.SENDOTP).toString();
			req.remove(GlobalConstants.SENDOTP);
		}
		JSONObject otpReqJson = new JSONObject(otpRequest);
		sendOtpReqTemplate = otpReqJson.getString("sendOtpReqTemplate");
		otpReqJson.remove("sendOtpReqTemplate");
		sendOtpEndPoint = otpReqJson.getString("sendOtpEndPoint");

		otpReqJson.remove("sendOtpEndPoint");

		if (sendOtpEndPoint.contains(GlobalConstants.$PARTNERKEYURL$)) {
			sendOtpEndPoint = sendOtpEndPoint.replace(GlobalConstants.$PARTNERKEYURL$,
					properties.getProperty("partnerKeyURL"));
		}

		Response otpResponse = postRequestWithAuthHeaderAndSignature(ApplnURI + sendOtpEndPoint,
				getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), testCaseDTO.getTestCaseName());

		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null;
		String sendOtpResTemplate = null;
		if (res.has(GlobalConstants.SENDOTPRESP)) {
			sendOtpResp = res.get(GlobalConstants.SENDOTPRESP).toString();
			res.remove(GlobalConstants.SENDOTPRESP);
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
				otpResponse.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate),
				testCaseDTO.isCheckErrorsOnlyInResponse(), otpResponse.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));
		OutputValidationUtil.publishOutputResult(ouputValidOtp);

		String identityRequest = null;
		String identityRequestTemplate = null;
		String identityRequestEncUrl = null;
		if (req.has(GlobalConstants.IDENTITYREQUEST)) {
			identityRequest = req.get(GlobalConstants.IDENTITYREQUEST).toString();
			req.remove(GlobalConstants.IDENTITYREQUEST);
		}
		identityRequest = buildIdentityRequest(identityRequest);

		if (identityRequest.contains("$DATETIME$"))
			identityRequest = identityRequest.replace("$DATETIME$", generateCurrentUTCTimeStamp());
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);
		String identyEnryptRequest = updateTimestampOtp(identityRequest);
		String encryptedIdentityReq = "";
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identyEnryptRequest,
					getResourcePath() + properties.getProperty("bioValueEncryptionTemplate"), testCaseName, false);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		if (Arrays.asList(testCaseDTO.getTestCaseName().split("_")).contains("MultiFactorAuth")) {
			String demographicsMapper = "identityRequest.(demographics)";
			JSONObject jsonObject = new JSONObject(identityReqJson.toString());
			JSONObject jsonBioHbs = new JSONObject(encryptedIdentityReq);
			if (jsonObject.has("key") && jsonObject.has(GlobalConstants.VALUE)) {
				JSONObject jsonHbs = new JSONObject(jsonBioHbs.get(GlobalConstants.IDENTITYREQUEST).toString());
				encryptedIdentityReq = JsonPrecondtion.parseAndReturnJsonContent(encryptedIdentityReq,
						jsonObject.get(GlobalConstants.VALUE).toString(),
						demographicsMapper + jsonObject.get("key").toString());
			}

		}
		Map<String, String> bioAuthTempMap = encryptDecryptUtil.getEncryptSessionKeyValue(encryptedIdentityReq);
		String authRequest = getJsonFromTemplate(req.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of OTP auth request ******************");
		Reporter.log("<b><u>Modification of otp auth request</u></b>");
		authRequest = modifyRequest(authRequest, bioAuthTempMap,
				getResourcePath() + properties.getProperty("idaMappingPath"));
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);

		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");

		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()),
				testCaseDTO.isCheckErrorsOnlyInResponse(), response.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

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
		logger.info("Terminating authpartner demo application...");
		AuthPartnerProcessor.authPartherProcessor.destroyForcibly();
	}
}
