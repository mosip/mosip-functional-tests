package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class SimplePostForRegisteredDevice extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(SimplePostForRegisteredDevice.class);
	protected String testCaseName = "";
	
	Encoder encoder = Base64.getEncoder();
	
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
		String inputJson =  getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		inputJson = inputJson.replace("$DEVICEDATA$", encoder.encodeToString(creatingDeviceDataForDeviceData().getBytes()));
		Response response = postWithBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJson, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		
		ReadContext ctx = JsonPath.parse(response.getBody().asString());
		JSONArray arrayOfErrors = ctx.read("$.errors");
		if (arrayOfErrors.isEmpty())
			regDeviceResponse = (String) ctx.read("$.response");
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

	}
	
	
	public String creatingDeviceDataForDigitalId() {
		JSONObject digitalId = new JSONObject();
		digitalId.put("serialNo", "TR001234567");
		digitalId.put("deviceProvider", "Techno");
		digitalId.put("deviceProviderId", "Tech-123");
		digitalId.put("make", "abcde");
		digitalId.put("model", "FRO90000");
		digitalId.put("dateTime", getCurrentDateAndTimeForAPI());
		digitalId.put("type", "Finger");
		digitalId.put("deviceSubType", "Slap");
		return digitalId.toString();
	}
	
	
	public String creatingDeviceDataForDeviceInfo() {
		String digitalId = creatingDeviceDataForDigitalId();
		JSONObject deviceInfo = new JSONObject();
		String[] deviceSubId = {"1","2"}; 
		deviceInfo.put("deviceSubId", deviceSubId);
		deviceInfo.put("certification", "L0");
		deviceInfo.put("digitalId", encoder.encodeToString(digitalId.getBytes()));
		deviceInfo.put("firmware", "001");
		deviceInfo.put("deviceExpiry", getCurrentDateAndTimeForAPI());
		deviceInfo.put("timestamp", getCurrentDateAndTimeForAPI());
		return deviceInfo.toString();
	}

	public String creatingDeviceDataForDeviceData() {
		String deviceInfo = creatingDeviceDataForDeviceInfo();
		JSONObject diviceData = new JSONObject();
		diviceData.put("deviceId", "device-id-123");
		diviceData.put("purpose", "REGISTRATION");
		diviceData.put("deviceInfo", encoder.encodeToString(deviceInfo.getBytes()));
		return diviceData.toString();
	}
    
	public static String getCurrentDateAndTimeForAPI() {
        return    javax.xml.bind.DatatypeConverter.printDateTime(
                Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            );
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
