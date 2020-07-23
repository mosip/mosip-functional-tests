package io.mosip.preregistration.tests;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class Sample extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(Sample.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	protected static String testCaseName = "";
	static String folder = "preReg";
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();
	String updateSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
	PreregistrationDAO dao = new PreregistrationDAO();
	SoftAssert soft = new SoftAssert();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();

	}
	@Test
	public void invalidateToken() {
		String cookie = lib.getToken();
		Response invalidateTokenResponse = lib.logOut(cookie);
		String message = invalidateTokenResponse.jsonPath().get("response.message").toString();
		lib.compareValues(message, "Token has been invalidated successfully");
		Response createPreRegResponse = lib.CreatePreReg(cookie);
		String errorCode = createPreRegResponse.jsonPath().get("errors[0].errorCode").toString();
		String errorMessage = createPreRegResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "KER-ATH-401");
		lib.compareValues(errorMessage, "Invalid Token");
		
	}
	@BeforeMethod(alwaysRun = true)
	public void run() {
	/*	if (!lib.isValidToken(individualToken)) {
			individualToken = lib.getToken();
		}*/
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		System.out.println("method name:" + result.getMethod().getMethodName());

	}
}