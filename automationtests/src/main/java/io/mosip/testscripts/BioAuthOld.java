package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
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

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.EncryptionDecrptionUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthPartnerProcessor;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.testrunner.HealthChecker;
import io.restassured.response.Response;

public class BioAuthOld extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(BioAuthOld.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;

	@BeforeClass
	public static void setPrerequiste() {
		logger.info("Starting authpartner demo service...");
		
		
	AuthPartnerProcessor.startProcess();
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
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		if(testCaseDTO.getEndPoint().contains("$partnerKeyURL$"))
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$partnerKeyURL$", props.getProperty("partnerKeyURL")));
		}
		JSONObject request = new JSONObject(testCaseDTO.getInput());
		String identityRequest = null, identityRequestTemplate = null, identityRequestEncUrl = null;
		if (request.has(GlobalConstants.IDENTITYREQUEST)) {
			identityRequest = request.get(GlobalConstants.IDENTITYREQUEST).toString();
			request.remove(GlobalConstants.IDENTITYREQUEST);
		}
		identityRequest = buildIdentityRequest(identityRequest);
		
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);

		String encryptedIdentityReq = null;
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identityRequest,
					getResourcePath() + props.getProperty("bioValueEncryptionTemplate"), testCaseName, isInternal);
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}

		Map<String, String> bioAuthTempMap = (isInternal)
				? encryptDecryptUtil.getInternalEncryptSessionKeyValue(encryptedIdentityReq)
				: encryptDecryptUtil.getEncryptSessionKeyValue(encryptedIdentityReq);
		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of bio auth request ******************");
		Reporter.log("<b><u>Modification of bio auth request</u></b>");
		authRequest = modifyRequest(authRequest, bioAuthTempMap,
				getResourcePath() + props.getProperty("idaMappingPath"));
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);

		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");

		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		
		if(testCaseName.toLowerCase().contains("kyc")) {
			JSONObject resJsonObject = new JSONObject(response.asString());
			String res="";
			try {
				res = resJsonObject.get("response").toString();
			} catch (JSONException e) {
				logger.error(e.getStackTrace());
			}
			Reporter.log("<b><u>Request for decrypting kyc data</u></b>");
			response = postWithBodyAcceptTextPlainAndCookie(EncryptionDecrptionUtil.getEncryptUtilBaseUrl()+props.getProperty("decryptkycdataurl"), 
						res, COOKIENAME, testCaseDTO.getRole(), "decryptEkycData");
		}
		

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
