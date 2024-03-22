package io.mosip.testrig.apirig.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import io.restassured.response.Response;

public class AssertIda {
	protected static Logger logger = Logger.getLogger(AssertIda.class);
	public boolean assertKernel(Response expectedResponse, JSONObject actualResponse,
			ArrayList<String> listOfElementToRemove) throws JsonProcessingException, IOException, ParseException {
		JSONObject expectedResponseBody = (JSONObject) new JSONParser().parse(expectedResponse.asString());
		JSONObject actualResponseBody = actualResponse;

		expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponseBody, listOfElementToRemove);
		actualResponseBody = AssertKernel.removeElementFromBody(actualResponse, listOfElementToRemove);

		return jsonComparison(expectedResponseBody, actualResponseBody);

	}

	/**
	 * this funtion compare the request and response json and return the boolean
	 * value
	 * 
	 * @param expectedResponseBody
	 * @param actualResponseBody
	 * @return boolean value
	 */
	public boolean jsonComparison(Object expectedResponseBody, Object actualResponseBody) {
		JSONObject reqObj = (JSONObject) expectedResponseBody;
		JSONObject resObj = (JSONObject) actualResponseBody;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode requestJson = mapper.readTree(reqObj.toString());
			JsonNode responseJson = mapper.readTree(resObj.toString());
			JsonNode diffJson = JsonDiff.asJson(requestJson, responseJson);

			logger.info("======" + diffJson + "==========");
			if (diffJson.toString().equals("[]")) {
				logger.info("equal");
				return true;
			}

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					logger.info("not equal");
					return false;
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.info("equal");
		return true;

	}

	public static JSONObject removeElementFromBody(JSONObject responce, ArrayList<String> listOfElementToRemove)
			throws ParseException {
		for (String elementToRemove : listOfElementToRemove) {
			responce.remove(elementToRemove);
		}

		return responce;
	}

}
