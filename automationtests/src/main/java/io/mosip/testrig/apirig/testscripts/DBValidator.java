package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestException;
import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.dto.OutputValidationDto;
import io.mosip.testrig.apirig.authentication.fw.util.AuthenticationTestException;
import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.dbaccess.AuditDBManager;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.restassured.response.Response;

public class DBValidator extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(DBValidator.class);
	protected String testCaseName = "";
	public static List<String> templateFields = new ArrayList<>();
	public Response response = null;
	
	@BeforeClass
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
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
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	
	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		
		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		String replaceId = inputJsonKeyWordHandeler(inputJson, testCaseName);
		
		
		JSONObject jsonObject = new JSONObject(replaceId);
		logger.info(jsonObject.keySet());
		Set<String> set = new TreeSet<>();
	    set.addAll(jsonObject.keySet());
	    String filterId = "";
	    
	    if (set.stream().findFirst().isPresent())
	    	filterId = set.stream().findFirst().get();
	    
	    logger.info(filterId);
		String query = testCaseDTO.getEndPoint() +" " + filterId + " = " +"'"+jsonObject.getString(filterId)+"'";
		
		
		logger.info(query);
		Map<String, Object> response = AuditDBManager.executeQueryAndGetRecord(testCaseDTO.getRole(), query);
		
		
		Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
		List<OutputValidationDto> objList = new ArrayList<>();
		OutputValidationDto objOpDto = new OutputValidationDto();
		if(response.size()>0) {
			
			objOpDto.setStatus("PASS");
		}
		else {
			objOpDto.setStatus(GlobalConstants.FAIL_STRING);
		}
		
		objList.add(objOpDto);
		objMap.put("expected vs actual", objList);

		if (!OutputValidationUtil.publishOutputResult(objMap))
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
