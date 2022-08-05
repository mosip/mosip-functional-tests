package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
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
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KeycloakUserManager;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class UpdateDraft extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(UpdateDraft.class);
	protected String testCaseName = "";
	String pathParams = null;
	public Response response = null;

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
		pathParams = context.getCurrentXmlTest().getLocalParameters().get("pathParams");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	/**
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
		testCaseDTO.setInputTemplate(AdminTestUtil.generateHbsForUpdateDraft());

		String jsonInput = testCaseDTO.getInput();

		if (BaseTestCase.languageList.size() == 2) {
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Female\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Mrs Madhu.GN\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line1\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line2\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line3\" }", "");
		} else if (BaseTestCase.languageList.size() == 1) {
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"FR\" }, { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Female\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Female\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Mrs Madhu.GN\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Mrs Madhu.GN\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line1\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line1\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line2\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line2\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line3\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line3\" }",
					"");
		}

		String inputJson = getJsonFromTemplate(jsonInput, testCaseDTO.getInputTemplate(), false);

		if (inputJson.contains("$1STLANG$"))
			inputJson = inputJson.replace("$1STLANG$", BaseTestCase.languageList.get(0));
		if (inputJson.contains("$2NDLANG$"))
			inputJson = inputJson.replace("$2NDLANG$", BaseTestCase.languageList.get(1));
		if (inputJson.contains("$3RDLANG$"))
			inputJson = inputJson.replace("$3RDLANG$", BaseTestCase.languageList.get(2));

		response = patchWithPathParamsBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJson, COOKIENAME,
				testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), pathParams);

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

	@AfterClass(alwaysRun = true)
	public void waittime() {

		try {
			logger.info("waiting for" + props.getProperty("Delaytime") + " mili secs after UIN Generation In IDREPO");
			Thread.sleep(Long.parseLong(props.getProperty("Delaytime")));
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}

	}
}