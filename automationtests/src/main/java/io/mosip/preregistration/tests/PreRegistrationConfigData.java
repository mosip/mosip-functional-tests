package io.mosip.preregistration.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class PreRegistrationConfigData extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(PreRegistrationConfigData.class);
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

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	/**
	 * Get Pre Registration configdata and compare with expected config data
	 */
	@Test
	public void getPreRegistrationConfigData() {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		boolean status = false;
		outerKeys.add("responsetime");
		testSuite = "PreRegistrationConfigData/PreRegistrationConfigData_smoke";
		Response getPreRegistrationConfigData = lib.getPreRegistrationConfigData(individualToken);
		JSONObject expectedRequest = lib.getRequest(testSuite);
		try {
			status = AssertResponses.assertResponses(getPreRegistrationConfigData, expectedRequest, outerKeys,
					innerKeys);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
			if (status)
				logger.info("No change in config parameter");
			else
			{
				logger.info("the config parameters do not match the expected auto suite requirements and that the suite will not run unless this is fixed manually");
			}
			
			
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
