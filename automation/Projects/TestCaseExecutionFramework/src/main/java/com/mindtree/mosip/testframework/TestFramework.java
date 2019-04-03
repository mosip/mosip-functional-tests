package com.mindtree.mosip.testframework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.mindtree.mosip.qamill.qamillwrapper.TestCaseExecutor;
import com.mindtree.mosip.qamill.utility.FileClient;

/*
 * import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mindtree.mosip.qamill.utility.RestClient;
import com.mindtree.mosip.qamill.utility.RestClientInput;
import com.mindtree.mosip.qamill.utility.RestClientOutput;
import com.mindtree.mosip.qamill.utility.qamillglobals;
*/

public class TestFramework {
	public static void main(String[] args) {
		System.out.println("Entering TestFramework::main");
		Map<String, String> mosipTestFrameworkProperties = new HashMap<String, String>();
		FileClient fileClient = new FileClient();
		try {
			mosipTestFrameworkProperties = fileClient.getMosipTestFrameworkProperties();

			if (mosipTestFrameworkProperties.get("Operation").equals("execute")) {
				System.out.println("TestFramework::main  -- operation=execute");
				TestCaseExecutor testCaseExecutor = new TestCaseExecutor(mosipTestFrameworkProperties);
				testCaseExecutor.executeTestCases();
			} /*else if (mosipTestFrameworkProperties.get("operation").equals("test")) {
				String url = "https://integ.mosip.io/pre-registration/v1.0/document/getDocument";
				RestClient restClient = new RestClient();
				RestClientInput restClientInput = new RestClientInput();
				restClientInput.setUrl(url);
				Map<String,String> params = new HashMap<String, String>();
				params.put("pre_registration_id", "12345");
				restClientInput.setRequestMethod("GET");
				restClientInput.setParams(params);
				RestClientOutput restClientOutput = restClient.makeRestCall(restClientInput);
				System.out.println("responsecode: " + restClientOutput.getResponseCode());
				System.out.println("responsebody: " + restClientOutput.getResponseBody());
			}*/else {
				System.out.println("TestFramework::main  -- operation=error");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Leaving TestFramework::main");
	}
}