package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
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
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class PostWithPathParamsAndBody extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(PostWithPathParamsAndBody.class);
	protected String testCaseName = "";
	public String pathParams = null;
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
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testCaseName = testCaseDTO.getTestCaseName(); 
		Response slotAvailabilityResponse=RestClient.getRequestWithCookie(ApplnURI+"/preregistration/v1/appointment/availability/10003", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, COOKIENAME, new KernelAuthentication().getTokenByRole(testCaseDTO.getRole()));
		PreRegistrationLibrary liberary= new PreRegistrationLibrary();
		List<String> appointmentDetails = liberary.getAppointmentDetails(slotAvailabilityResponse);
		if(appointmentDetails.size()>=4) {
			try {
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);
			} catch (IndexOutOfBoundsException e) {
				logger.info("Center not available");
				Assert.fail("Centers unavailable");
			}
		}
		String inputJosn=getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		inputJosn=inputJosn.replace("$registration_center_id$", regCenterId);
		inputJosn=inputJosn.replace("$appointment_date$", appDate);
		inputJosn=inputJosn.replace("$time_slot_from$", timeSlotFrom);
		inputJosn=inputJosn.replace("$time_slot_to$", timeSlotTo);
		Response response = postWithPathParamsBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJosn, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), pathParams);
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
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
	
}
