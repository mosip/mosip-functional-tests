package io.mosip.healthCheck;

import static io.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.mosip.registrationProcessor.tests.StageValidationTests;
import io.mosip.service.BaseTestCase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class HealthCheckTest {
	protected static Logger logger = Logger.getLogger(BaseTestCase.class);
	private final String actuatorString="/actuator/health";
	protected static String testCaseName = "";
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		System.out.println(method);
		testCaseName =  (String) testdata[0];
	}

	@DataProvider(name = "statusMap")
	public Object[][] healthCheckStatus() throws FileNotFoundException {
		Map<String,String> statusMap=new HashMap<String,String>();
		Map<String,Object> apiMap = ParseYml.parseYML();
		String apiString=ParseYml.parseMap(apiMap);
		String[] apiArray=apiString.split(",");
		
		for(int i=0;i<apiArray.length-1;i++) {
			String api_url=System.getProperty("env.endpoint")+apiArray[i]+actuatorString;
			Response actuatorResponse= given().contentType(ContentType.JSON).get(api_url);
		
			ReadContext ctx = JsonPath.parse(actuatorResponse.getBody().asString());
			String status =null;
			try {
				status = ctx.read("$['status']");
				
			}catch(Exception exception) {	

			}
			statusMap.put(api_url, status);	
		}
		System.out.println(statusMap);
		String[][] array = new String[statusMap.size()][2];
		int count = 0;
		for(Map.Entry<String,String> entry : statusMap.entrySet()){
		    array[count][0] = entry.getKey();
		    array[count][1] = entry.getValue();
		    count++;
		}
		
		return array;

	}
	
	@Test(dataProvider = "statusMap")
	public void assertHealth(Object url,Object status) {
		try {
		if(status.equals("UP"))
			Assert.assertTrue(true);
		else 
			Assert.assertTrue(false, "The Api Is Not UP");
		}
		catch(NullPointerException exp) {
			Assert.assertTrue(false, "The Api Is Not UP");
		}
	}
	
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, HealthCheckTest.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
		}
		
	}
}
