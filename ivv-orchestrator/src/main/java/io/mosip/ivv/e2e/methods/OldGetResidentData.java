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

public class OldGetResidentData extends BaseTestCaseUtil implements StepInterface {
	private static final String check_status_YML = "preReg/getResident/getResident.yml";
	Logger logger = Logger.getLogger(OldGetResidentData.class);

	@Override
	public void run() {

		String fileName = check_status_YML;
		SimplePost postScript = new SimplePost();
		BaseTestCase.testLevel = "smoke";
		Object[] casesList = postScript.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);

		for (Object test : testCaseList) {
			TestCaseDTO testCaseDTO = (TestCaseDTO) test;
			Reporter.log("<b><u>" + testCaseDTO.getTestCaseName() + "</u></b>");
			Reporter.log("<pre> <b>Get Resident: </b> <br/>" + testCaseDTO.getInput() + "</pre>");
			// generateResidentDataUsingPOST
			Response apiResponse = RestClient.postRequest(baseUrl + testCaseDTO.getEndPoint(), testCaseDTO.getInput(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + baseUrl + testCaseDTO.getEndPoint()
					+ ") <pre>" + apiResponse.getBody().asString() + "</pre>");
			JSONObject response = new JSONObject(apiResponse.asString());
			assertTrue(response.get("status").toString().equalsIgnoreCase("Success"),"Unable to get residentData from packet utility");
			JSONArray resArray = new JSONObject(apiResponse.asString()).getJSONArray("response");
			for (int i = 0; i < resArray.length(); i++) {
				JSONObject obj = resArray.getJSONObject(i);
				String resFilePath = obj.get("path").toString();
				residentTemplatePaths.put(resFilePath, null);
			}
			System.out.println("RESIDENTTEMPLATEPATHS: " + residentTemplatePaths);

		}
	}

}
