package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.testrunner.HealthChecker;
import io.restassured.response.Response;

public class PostWithAutogenIdWithOtpGenerate extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(PostWithAutogenIdWithOtpGenerate.class);
	protected String testCaseName = "";
	public String idKeyName = null;
	public Response response = null;
	public boolean auditLogCheck = false;

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
		idKeyName = context.getCurrentXmlTest().getLocalParameters().get("idKeyName");
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
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO)
			throws AuthenticationTestException, AdminTestException, NumberFormatException, InterruptedException {
		testCaseName = testCaseDTO.getTestCaseName();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
		JSONObject req = new JSONObject(testCaseDTO.getInput());
		auditLogCheck = testCaseDTO.isAuditLogCheck();
		String otpRequest = null;
		String sendOtpReqTemplate = null;
		String sendOtpEndPoint = null;
		if (req.has(GlobalConstants.SENDOTP)) {
			otpRequest = req.get(GlobalConstants.SENDOTP).toString();
			req.remove(GlobalConstants.SENDOTP);
		}
		JSONObject otpReqJson = new JSONObject(otpRequest);
		sendOtpReqTemplate = otpReqJson.getString("sendOtpReqTemplate");
		otpReqJson.remove("sendOtpReqTemplate");
		sendOtpEndPoint = otpReqJson.getString("sendOtpEndPoint");
		otpReqJson.remove("sendOtpEndPoint");

		Response otpResponse = null;
		int maxLoopCount = Integer.parseInt(properties.getProperty("uinGenMaxLoopCount"));
		int currLoopCount = 0;
		while (currLoopCount < maxLoopCount) {
			if (testCaseName.contains(GlobalConstants.ESIGNET_)) {
				if (!ConfigManager.IseSignetDeployed()) {
					throw new SkipException("esignet is not deployed hence skipping the testcase");
				}
				String tempUrl = ApplnURI.replace("-internal", "");
				otpResponse = postRequestWithCookieAuthHeaderAndXsrfToken(tempUrl + sendOtpEndPoint,
						getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), COOKIENAME, GlobalConstants.RESIDENT,
						testCaseDTO.getTestCaseName());
			} else {
				otpResponse = postWithBodyAndCookie(ApplnURI + sendOtpEndPoint,
						getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), COOKIENAME, GlobalConstants.RESIDENT,
						testCaseDTO.getTestCaseName());
			}

			if (otpResponse != null && otpResponse.asString().contains("IDA-MLC-018")) {
				logger.info(
						"waiting for: " + properties.getProperty("uinGenDelayTime") + " as UIN not available in database");
				try {
					Thread.sleep(Long.parseLong(properties.getProperty("uinGenDelayTime")));
				} catch (NumberFormatException | InterruptedException e) {
					logger.error(e.getStackTrace());
					Thread.currentThread().interrupt();
				} 
			} else {
				break;
			}

			currLoopCount++;
		}

		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null; 
		String	sendOtpResTemplate = null;
		if (res.has(GlobalConstants.SENDOTPRESP)) {
			sendOtpResp = res.get(GlobalConstants.SENDOTPRESP).toString();
			res.remove(GlobalConstants.SENDOTPRESP);
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		if (otpResponse != null) {
			Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
					otpResponse.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate));
			Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));
			
			if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
				throw new AdminTestException("Failed at otp output validation");
		}
		else {
			throw new AdminTestException("Invalid otp response");
		}
		



		if (testCaseName.contains(GlobalConstants.ESIGNET_)) {
			if (!ConfigManager.IseSignetDeployed()) {
				throw new SkipException("esignet is not deployed hence skipping the testcase");
			}
			String tempUrl = ApplnURI.replace("-internal", "");
			response = postRequestWithCookieAuthHeaderAndXsrfTokenForAutoGenId(tempUrl + testCaseDTO.getEndPoint(),
					getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate()), COOKIENAME,
					testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), idKeyName);
		} else {
			response = postWithBodyAndCookieForAutoGeneratedId(ApplnURI + testCaseDTO.getEndPoint(),
					getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate()), auditLogCheck,
					COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), idKeyName);
		}


		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(res.toString(), testCaseDTO.getOutputTemplate()));
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

	@AfterClass(alwaysRun = true)
	public void waittime() {
		try {
			if ((!testCaseName.contains(GlobalConstants.ESIGNET_)) && (!testCaseName.contains("Resident_CheckAidStatus"))) {
				logger.info("waiting for" + properties.getProperty("Delaytime")
						+ " mili secs after VID Generation In RESIDENT SERVICES");
				Thread.sleep(Long.parseLong(properties.getProperty("Delaytime")));
			}
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			Thread.currentThread().interrupt();
		}

	}
}
