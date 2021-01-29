package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.service.BaseTestCase;
import io.mosip.testscripts.SimplePost;
import io.restassured.response.Response;

public class GetResidentDataAndPacketTemplate extends BaseTestCaseUtil implements StepInterface {
	private static final String check_status_YML = "preReg/getResident/getResident.yml";
	private String packetProcess = "NEW";
	Logger logger = Logger.getLogger(GetResidentDataAndPacketTemplate.class);
	
	@Override
	public void run() {
		//residentTemplatePaths.put("C:\\Users\\ALOK~1.KUM\\AppData\\Local\\Temp\\residents_12917218946908945723\\603736037360373.json", "C:\\Users\\ALOK~1.KUM\\AppData\\Local\\Temp\\packets_10996429582318871200\\603736037360373");
		
		String fileName = check_status_YML;
		SimplePost postScript = new SimplePost();
		BaseTestCase.testLevel = "smoke";
		Object[] casesList = postScript.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);

		  for (Object test : testCaseList) { 
		  TestCaseDTO testCaseDTO=(TestCaseDTO)test;
		  Reporter.log("<b><u>"+testCaseDTO.getTestCaseName()+ "</u></b>");
		  Reporter.log("<pre> <b>Get Resident: </b> <br/>"+testCaseDTO.getInput() + "</pre>"); 
		  //generateResidentDataUsingPOST 
			Response apiResponse = RestClient.postRequest(baseUrl + testCaseDTO.getEndPoint(), testCaseDTO.getInput(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + baseUrl + testCaseDTO.getEndPoint()
					+ ") <pre>" + apiResponse.getBody().asString() + "</pre>");
			JSONObject response = new JSONObject(apiResponse.asString());
			assertTrue(response.get("status").toString().equalsIgnoreCase("Success"));
			JSONArray resArray = new JSONObject(apiResponse.asString()).getJSONArray("response");
			for (int i = 0; i < resArray.length(); i++) {
				JSONObject obj = resArray.getJSONObject(i);
				String resFilePath = obj.get("path").toString();
				residentTemplatePaths.put(resFilePath, null);
			}
			System.out.println("RESIDENTTEMPLATEPATHS: " + residentTemplatePaths);
			JSONObject jsonReq = new JSONObject();
			JSONArray arr = new JSONArray();
			for (String residentPath : residentTemplatePaths.keySet()) {

				arr.put(residentPath);
			}
			jsonReq.put("personaFilePath", arr);
			Reporter.log("<pre> <b>Get Packet Template: </b> <br/>" + jsonReq.toString() + "</pre>");
			// createPacketsUsingPOST
			Response templateResponse = RestClient.postRequest(baseUrl + props.getProperty("getTemplateUrl"),
					jsonReq.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + baseUrl
					+ props.getProperty("getTemplateUrl") + ") <pre>" + templateResponse.getBody().asString()
					+ "</pre>");

			JSONObject jsonResponse = new JSONObject(templateResponse.asString());
			JSONArray resp = jsonResponse.getJSONArray("packets");

			for (int i = 0; i < resp.length(); i++) {
				JSONObject obj = resp.getJSONObject(i);
				String id = obj.get("id").toString();
				String tempFilePath = obj.get("path").toString();
				for (String residentPath : residentTemplatePaths.keySet()) {
					if (residentPath.contains(id)) {
						residentTemplatePaths.put(residentPath, tempFilePath);
						break;
					}
				}

			}
			System.out.println("RESIDENTTEMPLATEPATHS: " + residentTemplatePaths);
		}
		 
	}
}
