package io.mosip.testscripts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.restassured.response.Response;

public class GetWithParamForDownloadCard extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(GetWithParamForDownloadCard.class);
	protected String testCaseName = "";
	public Response response = null;
	public byte[] pdf=null;
	public String pdfAsText =null;
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
		pdf = getWithPathParamAndCookieForPdf(ApplnURI + testCaseDTO.getEndPoint(), getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate()), COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		 try {
			 pdfAsText = PdfTextExtractor.getTextFromPage(new PdfReader(new ByteArrayInputStream(pdf)), 1);
			} catch (IOException e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + ") <pre>"
				+ pdfAsText+ "</pre>");
       
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
