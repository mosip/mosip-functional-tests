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
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;
/**
 * @author Arunakumar Rati
 *
 */
public class GetDocTypeDocCatByLangCode extends BaseTestCase implements ITest{

	public GetDocTypeDocCatByLangCode() {
		super();
	}

	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetDocTypeDocCatByLangCode.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetDocType_DocCatByLangCode";
	private SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String getDocTypeDocCatByLangCode = props.get("getDocTypeDocCatByLangCode");
	private JSONObject Expectedresponse = null;
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
	@DataProvider(name = "GetDocType_DocCatByLangCode")
	public Object[][] readData1(ITestContext context) throws Exception {		 
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getAllConfiguration
	 * Given input Json as per defined folders When GET request is sent to /v1/masterdata/applicanttype/languages
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetDocType_DocCatByLangCode")
	public void getDocType_DocCatByLangCode(String testcaseName) throws ParseException 
    {
		logger.info(testcaseName);
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];

		// Calling the get method 
		Response res=applicationLibrary.getWithPathParam(getDocTypeDocCatByLangCode, actualRequest, adminCookie);
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		JSONObject responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(res.asString())).get("response");
		
		if (responseJson!= null && responseJson.containsKey("documentcategories")) {

			String query = "select count(DISTINCT doccat_code) from master.valid_document where is_active = true";

			long obtainedObjectsCount = new KernelDataBaseAccess().validateDBCount(query,"masterdata");

			// fetching json array of objects from response
			JSONArray responseArrayFromGet = (JSONArray) responseJson.get("documentcategories");
			logger.info("===Dbcount===" + obtainedObjectsCount + "===Get-count===" + responseArrayFromGet.size());

			// validating number of objects obtained form db and from get request
			if (responseArrayFromGet.size() == obtainedObjectsCount) {

				// list to validate existance of attributes in response objects
				List<String> attributesToValidateExistance = new ArrayList<String>();
				attributesToValidateExistance.add("code");
				attributesToValidateExistance.add("name");
				attributesToValidateExistance.add("langCode");
				attributesToValidateExistance.add("isActive");
				attributesToValidateExistance.add("documenttypes");

				// key value of the attributes passed to fetch the data, should be same in all
				// obtained objects
				HashMap<String, String> passedAttributesToFetch = new HashMap<String, String>();
				if (actualRequest != null) {
					passedAttributesToFetch.put("langCode", actualRequest.get("languagecode").toString());
				}

				status = AssertKernel.validator(responseArrayFromGet, attributesToValidateExistance,
						passedAttributesToFetch);
			} else
				status = false;

		}

		else {

			// add parameters to remove in response before comparison like time stamp
			ArrayList<String> listOfElementToRemove = new ArrayList<String>();
			listOfElementToRemove.add("responsetime");
			status = assertKernel.assertKernel(res, Expectedresponse, listOfElementToRemove);
		}

		if (!status) {
			logger.debug(res);
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
