package io.mosip.testscripts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.restassured.response.Response;
import io.mosip.dbaccess.*;

public class DBValidator extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(DBValidator.class);
	protected String testCaseName = "";
	public static List<String> templateFields = new ArrayList<>();
	public Response response = null;
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
		//String[] templateFields = testCaseDTO.getTemplateFields();
		//List<String> queryProp = Arrays.asList(templateFields);
		//System.out.println(queryProp);
		//Arrays.asList(templateFields.split(","));
		
		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		String replaceId = inputJsonKeyWordHandeler(inputJson, testCaseName);
		
		
		JSONObject jsonObject = new JSONObject(replaceId);
		System.out.println(jsonObject.keySet());
		//List<String> attributName = (List<String>) jsonObject.keySet();
		//String s1 = attributName.get(0);
		//System.out.println(s1);
		
		Set<String> set = new TreeSet<>();
	    set.addAll(jsonObject.keySet());
	    String filterId = set.stream().findFirst().get();
	    System.out.println(filterId);
		
		
		//List<Set<String>> attributName = Arrays.asList(jsonObject.keySet());
		//List<String> stringsList = new ArrayList<>(attributName);
		//stringsList.get(0);
		String query = testCaseDTO.getEndPoint() +" " + filterId + " = " +"'"+jsonObject.getString(filterId)+"'";
		
		
		System.out.println(query);
		//System.out.println(respTime);
		Map<String, Object> response = AuditDBManager.executeQueryAndGetRecord(testCaseDTO.getRole(), query);
		
		
		Map<String, List<OutputValidationDto>> objMap = new HashMap<String, List<OutputValidationDto>>();
		List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
		OutputValidationDto objOpDto = new OutputValidationDto();
		if(response.size()>0) {
			
			objOpDto.setStatus("PASS");
		}
		else {
			objOpDto.setStatus("FAIL");
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
		
		//String deleteQuery = "delete from audit.app_audit_log where cr_by = '"+propsKernel.getProperty("partner_userName")+"'";
		//System.out.println(deleteQuery);
		//AuditDBManager.executeQueryAndDeleteRecord("audit", deleteQuery);
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
