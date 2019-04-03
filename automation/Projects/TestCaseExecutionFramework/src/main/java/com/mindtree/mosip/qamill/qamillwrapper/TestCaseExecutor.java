package com.mindtree.mosip.qamill.qamillwrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.mindtree.mosip.qamill.utility.FileClient;
import com.mindtree.mosip.qamill.utility.RestClient;
import com.mindtree.mosip.qamill.utility.RestClientInput;
import com.mindtree.mosip.qamill.utility.RestClientOutput;
import com.mindtree.mosip.qamill.utility.qamillglobals;

public class TestCaseExecutor {

	private String testCaseCollectionsPath;
	private String testCaseCollectionsName;
	private String testCaseExecutionsPath;
	private String testCaseExecutionsName;
	private String mosipBaseUrl;

	public TestCaseExecutor(Map<String, String> mosipTestFrameworkProperties) {
		System.out.println("Entering TestCaseExecutor::TestCaseExecutor");
		testCaseCollectionsPath = mosipTestFrameworkProperties.get("TestCaseCollectionsPath");
		testCaseCollectionsName = mosipTestFrameworkProperties.get("TestCaseCollectionsName");
		testCaseExecutionsPath = mosipTestFrameworkProperties.get("TestCaseExecutionsPath");
		testCaseExecutionsName = mosipTestFrameworkProperties.get("TestCaseExecutionsName");
		mosipBaseUrl = mosipTestFrameworkProperties.get("MosipBaseUrl");
		System.out.println("Leaving TestCaseExecutor::TestCaseExecutor");
	}

	public String getTestCaseCollectionsPath() {
		return testCaseCollectionsPath;
	}

	public void setTestCaseCollectionsPath(String testCaseCollectionsPath) {
		this.testCaseCollectionsPath = testCaseCollectionsPath;
	}

	public String getTestCaseCollectionsName() {
		return testCaseCollectionsName;
	}

	public void setTestCaseCollectionsName(String testCaseCollectionsName) {
		this.testCaseCollectionsName = testCaseCollectionsName;
	}

	public String getTestCaseExecutionsPath() {
		return testCaseExecutionsPath;
	}

	public void setTestCaseExecutionsPath(String testCaseExecutionsPath) {
		this.testCaseExecutionsPath = testCaseExecutionsPath;
	}

	public String getTestCaseExecutionsName() {
		return testCaseExecutionsName;
	}

	public void setTestCaseExecutionsName(String testCaseExecutionsName) {
		this.testCaseExecutionsName = testCaseExecutionsName;
	}

	public String getMosipBaseUrl() {
		return mosipBaseUrl;
	}

	public void setMosipBaseUrl(String mosipBaseUrl) {
		this.mosipBaseUrl = mosipBaseUrl;
	}

	public void executeTestCases() throws Exception {
		System.out.println("Entering TestCaseExecutor::executeTestCases");
		FileClient fileClient = new FileClient();
		Map<String, RestClientOutput> testCaseNameAndResponseMap = new HashMap<String, RestClientOutput>();

		// clear the contents in testcaseexecution folder
		fileClient.purgeDirectory(getTestCaseExecutionsPath() + File.separator + getTestCaseExecutionsName());

		String testCaseExecutionSequenceFilePath = getTestCaseCollectionsPath() + File.separator
				+ getTestCaseCollectionsName() + File.separator + "TestCaseExecutionSequence.csv";
		ArrayList<String> executionSequenceFileContents = fileClient.readFromCsvFile(testCaseExecutionSequenceFilePath);

		if (executionSequenceFileContents.size() > 1) {
			for (int i = 1; i < executionSequenceFileContents.size(); i++) {
				String strApiNameAndSaveResponseInfo = executionSequenceFileContents.get(i);
				String[] apiNameAndSaveResponseInfo = strApiNameAndSaveResponseInfo.split(",");
				// System.out.println(
				// "api: " + apiNameAndSaveResponseInfo[0] + ", SaveResponse: " +
				// apiNameAndSaveResponseInfo[1]);
				RestClientOutput restClientOutput = processTestCase(apiNameAndSaveResponseInfo[0],
						apiNameAndSaveResponseInfo[1], testCaseNameAndResponseMap);

				// System.out.println("Executed Test Case: " + apiNameAndSaveResponseInfo[0] +
				// "Response Headers: " + restClientOutput.getHeaders());
				if (true == apiNameAndSaveResponseInfo[1].equalsIgnoreCase("yes")) {
					testCaseNameAndResponseMap.put(apiNameAndSaveResponseInfo[0], restClientOutput);
				}
			}
		}
		System.out.println("Leaving TestCaseExecutor::executeTestCases");
	}

	public RestClientOutput processTestCase(String testCaseName, String saveResponse,
			Map<String, RestClientOutput> testCaseNameAndResponseMap) throws Exception {
		System.out.println("Entering TestCaseExecutor::processTestCase");
		System.out.println("TestCase Name: " + testCaseName);

		String testCaseFolderPath = getTestCaseCollectionsPath() + File.separator + getTestCaseCollectionsName()
				+ File.separator + testCaseName;
		String executedTestCaseFolder = getTestCaseExecutionsPath() + File.separator + getTestCaseCollectionsName()
				+ File.separator + getTestCaseExecutionsName() + File.separator + testCaseName;

		RestClient restClient = new RestClient();
		RestClientInput restClientInput = new RestClientInput();
		RestClientOutput restClientOutput = new RestClientOutput();
		FileClient fileClient = new FileClient();
		String requestBody = null;
		String responseBody = null;
		String stitchingBody = null;

		String finalRequestBody = null;
		String finalExpectedResponseBody = null;
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();
		String url = null;
		String requestMethod = null;

		requestBody = fileClient.readFromFile(testCaseFolderPath + File.separator + "request.json");
		responseBody = fileClient.readFromFile(testCaseFolderPath + File.separator + "response.json");
		stitchingBody = fileClient.readFromFile(testCaseFolderPath + File.separator + "stitching.json");

		finalRequestBody = prepareRequestResponseBody(requestBody, stitchingBody, testCaseNameAndResponseMap, true);
		finalExpectedResponseBody = prepareRequestResponseBody(responseBody, stitchingBody, testCaseNameAndResponseMap,
				false);
		params = prepareParamsOrHeaders(stitchingBody, testCaseNameAndResponseMap, true);
		headers = prepareParamsOrHeaders(stitchingBody, testCaseNameAndResponseMap, false);
		headers.put("Content-Type", "application/json");

		Map<String, String> mapUrlAndRequestMethod = getUrlAndRequestMethodFromStitchingBody(stitchingBody);
		url = mapUrlAndRequestMethod.get("url");
		// url = url.replace("\\", "");
		requestMethod = mapUrlAndRequestMethod.get("requestMethod");

		url = getMosipBaseUrl() + url;

		/*
		 * System.out.println("testCaseFolderPath: " + testCaseFolderPath);
		 * System.out.println("finalRequestBody: " + finalRequestBody);
		 * System.out.println("finalExpectedResponseBody: " +
		 * finalExpectedResponseBody); System.out.println("params: " + params);
		 * System.out.println("headers: " + headers); System.out.println("url: " + url);
		 * System.out.println("requestMethod: " + requestMethod);
		 */

		restClientInput.setUrl(url);
		restClientInput.setRequestMethod(requestMethod);
		restClientInput.setRequestBody(finalRequestBody);
		restClientInput.setParams(params);
		restClientInput.setHeaders(headers);

		restClientOutput = restClient.makeRestCall(restClientInput);

		if ((null != finalRequestBody) && (false == finalRequestBody.isEmpty())) {
			String finalRequestJsonFilePath = executedTestCaseFolder + File.separator + "request.json";
			fileClient.writeToFile(finalRequestJsonFilePath, finalRequestBody);
		}
		if ((null != finalExpectedResponseBody) && (false == finalExpectedResponseBody.isEmpty())) {
			String finalExpectedResponseJsonFilePath = executedTestCaseFolder + File.separator
					+ "expectedresponse.json";
			fileClient.writeToFile(finalExpectedResponseJsonFilePath, finalExpectedResponseBody);
		}
		if ((null != restClientOutput.getResponseBody()) && (false == restClientOutput.getResponseBody().isEmpty())) {
			String finalActualResponseJsonFilePath = executedTestCaseFolder + File.separator + "actualresponse.json";
			fileClient.writeToFile(finalActualResponseJsonFilePath, restClientOutput.getResponseBody());
		}

		// System.out.println("Executed Test Case: " + testCaseFolderPath + "Response
		// Headers: " + restClientOutput.getHeaders());
		System.out.println("Leaving TestCaseExecutor::processTestCase");
		return restClientOutput;
	}

	public String prepareRequestResponseBody(String requestOrResponseBody, String stitchingBody,
			Map<String, RestClientOutput> testCaseNameAndResponseMap, Boolean isRequestBody) throws ParseException {
		System.out.println("Entering TestCaseExecutor::prepareRequestResponseBody");
		String returnBody = requestOrResponseBody;
		JSONParser parser = new JSONParser();
		JSONArray dynamicStitchingDataList = null;
		JSONObject stitchingJson = (JSONObject) parser.parse(stitchingBody);

		if (true == isRequestBody) {
			dynamicStitchingDataList = (JSONArray) stitchingJson.get("request");
		} else {
			dynamicStitchingDataList = (JSONArray) stitchingJson.get("response");
		}

		if ((null != dynamicStitchingDataList) && (0 < dynamicStitchingDataList.size())) {
			for (int i = 0; i < dynamicStitchingDataList.size(); i++) {
				JSONObject dynamicStitchingData = (JSONObject) dynamicStitchingDataList.get(i);
				JSONObject dynamic = (JSONObject) dynamicStitchingData.get("dynamic");
				String stitchingTestCaseName = (String) dynamic.get("stitchingTestCaseName");
				String responseDataType = (String) dynamic.get("responseDataType");
				String responseKey = (String) dynamic.get("responseKey");

				String strDynamicValue = null;
				Integer nValue = null;
				JSONObject jsonValue = null;
				JSONArray jsonArrayValue = null;

				String key = (String) dynamicStitchingData.get("key");
				String valueDataType = (String) dynamicStitchingData.get("valueDataType");

				strDynamicValue = getvalueFromPreviouslyExecutedApi(testCaseNameAndResponseMap, stitchingTestCaseName,
						responseDataType, responseKey);

				DocumentContext ctx = JsonPath.parse(returnBody);
				if (true == valueDataType.equals(qamillglobals.VALUEDATATYPE_STRING)) {
					returnBody = ctx.set(key, strDynamicValue).jsonString();
				} else if (true == valueDataType.equals(qamillglobals.VALUEDATATYPE_INTEGER)) {
					nValue = Integer.valueOf(strDynamicValue);
					returnBody = ctx.set(key, nValue).jsonString();
				} else if (true == valueDataType.equals(qamillglobals.VALUEDATATYPE_JSON)) {
					jsonValue = (JSONObject) parser.parse(strDynamicValue);
					returnBody = ctx.set(key, jsonValue).jsonString();
				} else if (true == valueDataType.equals(qamillglobals.VALUEDATATYPE_JSONARRAY)) {
					JSONParser parser2 = new JSONParser();
					jsonArrayValue = (JSONArray) parser2.parse(strDynamicValue);
					returnBody = ctx.set(key, jsonArrayValue).jsonString();
				}
			}
		}
		System.out.println("Leaving TestCaseExecutor::prepareRequestResponseBody");
		return returnBody;
	}

	public Map<String, String> prepareParamsOrHeaders(String stitchingBody,
			Map<String, RestClientOutput> testCaseNameAndResponseMap, Boolean isParams) throws ParseException {
		System.out.println("Entering TestCaseExecutor::prepareParamsOrHeaders");
		Map<String, String> paramOrHeaderMap = new HashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONArray stitchingDataList = null;
		JSONObject stitchingJson = (JSONObject) parser.parse(stitchingBody);

		if (true == isParams) {
			stitchingDataList = (JSONArray) stitchingJson.get("params");
		} else {
			stitchingDataList = (JSONArray) stitchingJson.get("headers");
		}

		if ((null != stitchingDataList) && (0 < stitchingDataList.size())) {
			for (int i = 0; i < stitchingDataList.size(); i++) {
				JSONObject stitchingData = (JSONObject) stitchingDataList.get(i);

				String key = null;
				String value = null;
				String valueType = null;
				String valueDataType = null;

				key = (String) stitchingData.get("key");
				valueDataType = (String) stitchingData.get("valueDataType");
				valueType = (String) stitchingData.get("valueType");
				// System.out.println("valueType: " + valueType);

				if (true == valueType.equals("STATIC")) {
					JSONObject staticData = (JSONObject) stitchingData.get("static");
					value = (String) staticData.get("value");
					// System.out.println("static Value: " + value);
				} else {
					JSONObject dynamic = (JSONObject) stitchingData.get("dynamic");
					String stitchingTestCaseName = (String) dynamic.get("stitchingTestCaseName");
					String responseDataType = (String) dynamic.get("responseDataType");
					String responseKey = (String) dynamic.get("responseKey");
					value = getvalueFromPreviouslyExecutedApi(testCaseNameAndResponseMap, stitchingTestCaseName,
							responseDataType, responseKey);
				}
				paramOrHeaderMap.put(key, value);
			}
		}
		System.out.println("Leaving TestCaseExecutor::prepareParamsOrHeaders");
		return paramOrHeaderMap;
	}

	public Map<String, String> getUrlAndRequestMethodFromStitchingBody(String stitchingBody) throws ParseException {
		System.out.println("Entering TestCaseExecutor::getUrlAndRequestMethodFromStitchingBody");
		Map<String, String> mapUrlAndRequestMethod = new HashMap<String, String>();
		String url = null;
		String requestMethod = null;

		JSONParser parser = new JSONParser();
		JSONObject stitchingJson = (JSONObject) parser.parse(stitchingBody);

		url = (String) stitchingJson.get("url");
		requestMethod = (String) stitchingJson.get("requestMethod");
		mapUrlAndRequestMethod.put("url", url);
		mapUrlAndRequestMethod.put("requestMethod", requestMethod);
		System.out.println("Leaving TestCaseExecutor::getUrlAndRequestMethodFromStitchingBody");
		return mapUrlAndRequestMethod;
	}

	public String getvalueFromPreviouslyExecutedApi(Map<String, RestClientOutput> testCaseNameAndResponseMap,
			String stitchingTestCaseName, String responseDataType, String responseKey) {
		System.out.println("Entering TestCaseExecutor::getvalueFromPreviouslyExecutedApi");
		String value = null;
		RestClientOutput restClientOutput = null;
		restClientOutput = testCaseNameAndResponseMap.get(stitchingTestCaseName);

		if (responseDataType.equals("RESPONSEDATATYPE.RESPONSE-BODY")) {
			Object objEndPointDataElementValue = null;

			Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
			DocumentContext ctx = JsonPath.using(conf).parse(restClientOutput.getResponseBody());
			objEndPointDataElementValue = ctx.read(responseKey);
			if (null != objEndPointDataElementValue) {
				value = objEndPointDataElementValue.toString();
			}
		} else if (responseDataType.equals("RESPONSEDATATYPE.RESPONSE-HEADER")) {
			Map<String, List<String>> responseHeaders = restClientOutput.getHeaders();
			value = responseHeaders.get(responseKey).get(0);
		} else {
			// throw exception
		}

		System.out.println("Entering TestCaseExecutor::getvalueFromPreviouslyExecutedApi");
		return value;
	}
}
