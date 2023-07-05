package io.mosip.testscripts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.global.utils.GlobalMethods;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.HealthChecker;
import io.restassured.response.Response;

public class GetWithQueryParamForDownloadCard extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(GetWithQueryParamForDownloadCard.class);
	protected String testCaseName = "";
	public Response response = null;
	public byte[] pdf=null;
	public String pdfAsText =null;
	public boolean sendEsignetToken = false;
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
		sendEsignetToken = context.getCurrentXmlTest().getLocalParameters().containsKey("sendEsignetToken");
		logger.info("Started executing yml: "+ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	

	/**
	 * Test method for OTP Generation execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws Exception 
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws Exception {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		String[] templateFields = testCaseDTO.getTemplateFields();

		if (testCaseDTO.getTemplateFields() != null && templateFields.length > 0) {
			ArrayList<JSONObject> inputtestCases = AdminTestUtil.getInputTestCase(testCaseDTO);
			 for (int i=0; i<languageList.size(); i++) {
		            	pdf = getWithQueryParamAndCookieForPdf(ApplnURI + testCaseDTO.getEndPoint(),
		    					getJsonFromTemplate(inputtestCases.get(i).toString(), testCaseDTO.getInputTemplate()), COOKIENAME,
		    					testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		            	try {
		       			 pdfAsText = PdfTextExtractor.getTextFromPage(new PdfReader(new ByteArrayInputStream(pdf)), 1);
		       			} catch (IOException e) {
		       				Reporter.log("Exception : " + e.getMessage());
		       			}
		       		 
			       		 if(pdf!=null && (new String(pdf).contains("errors")|| pdfAsText == null)) {
			       			GlobalMethods.reportResponse(ApplnURI + testCaseDTO.getEndPoint(), "Not able to download");
			       		 }
			       		 else {
			       			GlobalMethods.reportResponse(ApplnURI + testCaseDTO.getEndPoint(), pdfAsText);
			       		 }
		        }
		}  
		
		else {
			pdf = getWithQueryParamAndCookieForPdf(ApplnURI + testCaseDTO.getEndPoint(), getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate()), COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), sendEsignetToken);
			try {
				 pdfAsText = PdfTextExtractor.getTextFromPage(new PdfReader(new ByteArrayInputStream(pdf)), 1);
				} catch (IOException e) {
					Reporter.log("Exception : " + e.getMessage());
				}
			 
			 if(pdf!=null && (new String(pdf).contains("errors")|| pdfAsText == null)) {
				 GlobalMethods.reportResponse(ApplnURI + testCaseDTO.getEndPoint(), "Not able to download");
			 }
			 else {
				 GlobalMethods.reportResponse(ApplnURI + testCaseDTO.getEndPoint(), pdfAsText);
			 }
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
}
