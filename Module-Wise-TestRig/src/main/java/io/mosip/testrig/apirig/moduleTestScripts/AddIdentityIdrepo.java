package io.mosip.testrig.apirig.moduleTestScripts;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import io.mosip.testrig.apirig.admin.fw.util.RigInternalError;
import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.dto.OutputValidationDto;
import io.mosip.testrig.apirig.authentication.fw.precon.JsonPrecondtion;
import io.mosip.testrig.apirig.authentication.fw.util.AuthenticationTestException;
import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.kernel.util.KernelAuthentication;
import io.mosip.testrig.apirig.kernel.util.KeycloakUserManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.testscripts.AddIdentity;
import io.restassured.response.Response;

public class AddIdentityIdrepo extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(AddIdentityIdrepo.class);
	protected String testCaseName = "";
	public Response response = null;

	/**
	 * get current testcaseName
	 */
	
	@BeforeClass
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	

	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	@Override
	public String getTestName() {
		return testCaseName;

	}
	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO, ITestContext context) throws AuthenticationTestException, AdminTestException {
		Object[] testObj = getTestCaseList(context);
		TestCaseDTO test = (TestCaseDTO) testObj[0];
		testCaseName = testCaseDTO.getTestCaseName();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException(
					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
		}
		testCaseDTO.setInputTemplate(AdminTestUtil.modifySchemaGenerateHbs(testCaseDTO.isRegenerateHbs()));
		String uin = JsonPrecondtion
				.getValueFromJson(
						RestClient.getRequestWithCookie(ApplnURI + "/v1/idgenerator/uin", MediaType.APPLICATION_JSON,
								MediaType.APPLICATION_JSON, COOKIENAME,
								new KernelAuthentication().getTokenByRole(testCaseDTO.getRole())).asString(),
						"response.uin");
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		String timestampValue = dateFormatter.format(cal.getTime());
		String genRid = "27847" + generateRandomNumberString(10) + timestampValue;
		
		String jsonInput = testCaseDTO.getInput();

		String inputJson = getJsonFromTemplate(jsonInput, testCaseDTO.getInputTemplate(), false);
		
		inputJson = inputJson.replace("$UIN$", uin);
		inputJson = inputJson.replace("$RID$", genRid);
		String phoneNumber = "";
		if (inputJson.contains("$PHONENUMBERFORIDENTITY$")) {
			if (!phoneSchemaRegex.isEmpty())
				try {
					phoneNumber = genStringAsperRegex(phoneSchemaRegex);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			inputJson = replaceKeywordWithValue(inputJson, "$PHONENUMBERFORIDENTITY$", phoneNumber);
		}
		
		test.setInput(inputJson);
		
		AddIdentity addIdentity = new AddIdentity();
		addIdentity.test(test);
		Response response = addIdentity.response;
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()),
				testCaseDTO, response.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		if (testCaseDTO.getTestCaseName().contains("_Pos")) {
			writeAutoGeneratedId(testCaseDTO.getTestCaseName(), "UIN", uin);
			writeAutoGeneratedId(testCaseDTO.getTestCaseName(), "RID", genRid);
			writeAutoGeneratedId(testCaseDTO.getTestCaseName(), "EMAIL", testCaseDTO.getTestCaseName() + "@mosip.net");
			writeAutoGeneratedId(testCaseDTO.getTestCaseName(), "PHONE", phoneNumber);
		}
		if (!phoneNumber.isEmpty())
			writeAutoGeneratedId(testCaseDTO.getTestCaseName(), "PHONE", phoneNumber);

	}
	
	@AfterClass(alwaysRun = true)
	public void waittime() {

		try {
			if (BaseTestCase.currentModule.equals("auth") || BaseTestCase.currentModule.equals("esignet")) {
				logger.info("waiting for " + properties.getProperty("Delaytime")
						+ " mili secs after UIN Generation In IDREPO"); //
				Thread.sleep(Long.parseLong(properties.getProperty("Delaytime")));
			}
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			Thread.currentThread().interrupt();
		}

	}
	
	
}
