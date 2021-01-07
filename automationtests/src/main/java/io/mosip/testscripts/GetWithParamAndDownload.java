package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
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
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetWithParamAndDownload extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(GetWithParamAndDownload.class);
	protected String testCaseName = "";
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
		Response response =null;
		testCaseName = testCaseDTO.getTestCaseName(); 
		response = getWithPathParamAndCookie(ApplnURI + testCaseDTO.getEndPoint(), getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate()), COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		Map<String, List<OutputValidationDto>> ouputValid = null;
		String contentTypeValue=null;
		String fileNameValue=null;
		JSONObject jsonObject=new JSONObject();
		//JsonPath jsonPath = response.getBody().jsonPath();
		//Object errorr=jsonPath.get("error");
		if(response.getBody() != null && testCaseName.contains("CredentialsStatus_All_Valid_Smoke")) {
			for(Header header:response.getHeaders()) {
				if((header.getName().equals("Content-Type"))){
					contentTypeValue=header.getValue();	
				}
				if((header.getName().equals("Content-Disposition"))){
					fileNameValue=header.getValue().split(";")[1].split("=")[1];
					fileNameValue=fileNameValue.substring(1, fileNameValue.length()-1);
					
			}
				
		}
			jsonObject.put("Content-Type", contentTypeValue);
			jsonObject.put("filename", fileNameValue);
			 ouputValid = OutputValidationUtil
					.doJsonOutputValidation(jsonObject.toJSONString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
			Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		}
		else {
			 ouputValid = OutputValidationUtil
					.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
			Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		}
		
		
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
