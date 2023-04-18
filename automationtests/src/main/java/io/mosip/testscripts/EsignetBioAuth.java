package io.mosip.testscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import io.mosip.ida.certificate.PartnerRegistration;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class EsignetBioAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(EsignetBioAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;

	@BeforeClass
	public static void setPrerequiste() {
		logger.info("Starting authpartner demo service...");

		// AuthPartnerProcessor.startProcess();
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
		/*
		 * if(testCaseDTO.getEndPoint().contains("$PartnerKeyURL$")) {
		 * testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$PartnerKeyURL$",
		 * PartnerRegistration.partnerKeyUrl)); }
		 * if(testCaseDTO.getEndPoint().contains("$PartnerName$")) {
		 * testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$PartnerName$",
		 * PartnerRegistration.partnerId)); }
		 */
		JSONObject request = new JSONObject(testCaseDTO.getInput());
		String identityRequest = null, identityRequestTemplate = null, identityRequestEncUrl = null;
		if (request.has("identityRequest")) {
			identityRequest = request.get("identityRequest").toString();
			request.remove("identityRequest");
		}
		identityRequest = buildIdentityRequest(identityRequest);

		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);

		// "domainUri": "https://idp.dev2.mosip.net",
		// https://api-internal.dev2.mosip.net

		if (identityRequest.contains("$DOMAINURI$")) {
			String domainUrl = ApplnURI.replace("api-internal", "esignet");
			identityRequest = identityRequest.replace("$DOMAINURI$", domainUrl);
		}
		String encryptedIdentityReq = null;
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identityRequest,
					getResourcePath() + props.getProperty("bioValueEncryptionTemplate"), testCaseName, isInternal);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject encryptedIdentityReqObject = new JSONObject(encryptedIdentityReq);

		JSONObject objIdentityRequest = encryptedIdentityReqObject.getJSONObject("identityRequest");
		System.out.println(objIdentityRequest);
		JSONArray arrayBiometrics = objIdentityRequest.getJSONArray("biometrics");

		// JSONObject objBiometrics=arrayBiometrics.getJSONObject(0);

		// System.out.println(objBiometrics.get("hash"));

		String bioData = arrayBiometrics.toString();
		System.out.println(bioData);

		byte[] byteBioData = bioData.getBytes();

		String challengeValue = Base64.getUrlEncoder().encodeToString(byteBioData);
		// String challengeValue =
		// Base64.getUrlEncoder().encodeToString(decodedBioMetricValue);
		System.out.println(challengeValue);

		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());

		if (authRequest.contains("$CHALLENGE$")) {
			authRequest = authRequest.replace("$CHALLENGE$", challengeValue);
		}
		if (testCaseName.contains("ESignet_")) {
			String tempUrl = ApplnURI.replace("-internal", "");
			response = postRequestWithCookieAuthHeaderAndXsrfToken(tempUrl + testCaseDTO.getEndPoint(), authRequest,
					COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

//			response = postWithBodyAndCookieForAutoGeneratedIdUrlEncoded(tempUrl + testCaseDTO.getEndPoint(), inputJson,
//					COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), idKeyName);
		} else {
			response = postWithBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), authRequest, COOKIENAME,
					testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		}
		String ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());

		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
			if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
					|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
				ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
			} else {
				ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINIdp.json").toString();
			}
		} else {
			if (testCaseDTO.getTestCaseName().contains("vid") || testCaseDTO.getTestCaseName().contains("VID")) {
				if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
						|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
					ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
				} else {
					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINIdp.json").toString();
				}
			}
		}

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), ActualOPJson);
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
		logger.info("Terminating authpartner demo application...");
		// AuthPartnerProcessor.authPartherProcessor.destroyForcibly();
	}

}
