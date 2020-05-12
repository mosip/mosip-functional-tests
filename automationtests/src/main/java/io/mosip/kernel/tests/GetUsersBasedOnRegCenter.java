package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class GetUsersBasedOnRegCenter extends BaseTestCase implements ITest {

	public GetUsersBasedOnRegCenter() {
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetUsersBasedOnRegCenter.class);
	protected  String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetusersBasedOnRegCenter";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String getusersBasedOnRegCenter = props.get("getusersBasedOnRegCenter");
	private AssertKernel assertions = new AssertKernel();
	private KernelAuthentication auth=new KernelAuthentication();

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName + "_" + apiName + "_" + object.toString();
		if(!lib.isValidToken(adminCookie))
			adminCookie=auth.getAuthForAdmin();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "GetusersBasedOnRegCenter")
	public Object[][] readData1(ITestContext context) throws Exception {
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
	}
	
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getusersBasedOnRegCenter
	 * Given input Json as per defined folders When GET request is sent to /syncdata/v1.0/configuration/{registrationCenterId}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetusersBasedOnRegCenter")
	public void getusersBasedOnRegCenter(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		logger.info(testcaseName);
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		JSONObject expectedresponse = objectDataArray[1];		
		// Calling the get method 
		Response response=applicationLibrary.getWithPathParam(getusersBasedOnRegCenter, actualRequest,adminCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
				new CommonLibrary().responseAuthValidation(response);
				if (testcaseName.toLowerCase().contains("smoke")) {

					// fetching json object from response
					JSONObject responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(response.asString()))
							.get("response");
					if (responseJson == null || !responseJson.containsKey("userDetails"))
						Assert.assertTrue(false, "Response does not contain userDetails");
					String query = "select count(*) FROM master.reg_center_user where regcntr_id ='" + actualRequest.get("regid") + "' and is_active = true";
					long obtainedObjectsCount = new KernelDataBaseAccess().validateDBCount(query, "masterdata");

					// fetching json array of objects from response
					JSONArray userDetailsFromGet = (JSONArray) responseJson.get("userDetails");
					logger.info("===Dbcount===" + obtainedObjectsCount + "===Get-count===" + userDetailsFromGet.size());

					// validating number of objects obtained form db and from get request
					if (userDetailsFromGet.size() == obtainedObjectsCount) {

						// list to validate existance of attributes in response objects
						List<String> attributesToValidateExistance = new ArrayList<String>();
						attributesToValidateExistance.add("userName");
						attributesToValidateExistance.add("mail");
						attributesToValidateExistance.add("userPassword");
						attributesToValidateExistance.add("name");
						attributesToValidateExistance.add("roles");

						// key value of the attributes passed to fetch the data (should be same in all
						// obtained objects)
						HashMap<String, String> passedAttributesToFetch = new HashMap<String, String>();
						

						status = AssertKernel.validator(userDetailsFromGet, attributesToValidateExistance,
								passedAttributesToFetch);
					} else
						{
						status = false;
						logger.info("Response from the request: "+response.asString());
						}

				}

				else {
					// add parameters to remove in response before comparison like time stamp
					ArrayList<String> listOfElementToRemove = new ArrayList<String>();
					listOfElementToRemove.add("responsetime");
					status = assertions.assertKernel(response, expectedresponse, listOfElementToRemove);
				}

		if (!status) {
			logger.info(response.asString());
		}
		Verify.verify(status);
		softAssert.assertAll();
}
		@Override
		public String getTestName() {
			return this.testCaseName;
		} 
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
