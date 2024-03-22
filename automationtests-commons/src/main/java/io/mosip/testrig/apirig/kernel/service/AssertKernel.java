package io.mosip.testrig.apirig.kernel.service;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.CommonLibrary;
import io.restassured.response.Response;

/**
 * @author Arjun and Ravikant
 *
 */
public class AssertKernel {
	
	private static Logger logger = Logger.getLogger(AssertKernel.class);
	/**
	 * this method accepts expected and actual response and return boolean value
	 * 
	 * @param expectedResponse
	 * @param actualResponse
	 * @param listOfElementToRemove
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean assertKernel(Response actualResponse, JSONObject expectedResponse,
			List<String> listOfElementToRemove){
		new CommonLibrary().checkResponseUTCTime(actualResponse);
		JSONObject actualResponseBody = null;
		JSONObject expectedResponseBody = expectedResponse;
		try {
			actualResponseBody = (JSONObject) new JSONParser().parse(actualResponse.asString());
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponseBody, listOfElementToRemove);
			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponse, listOfElementToRemove);
			} catch (ParseException e) {
				logger.info(e.getMessage());
			}
		
		return jsonComparison1(actualResponseBody, expectedResponseBody);

	}
	
	/**
	 * this method accepts expected and actual response and return boolean value
	 * 
	 * @param expectedResponse
	 * @param actualResponse
	 * @param listOfElementToRemove
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean assertKernelWithJsonObject(JSONObject actualResponse, JSONObject expectedResponse,
			List<String> listOfElementToRemove){
		
		JSONObject actualResponseBody = null;
		JSONObject expectedResponseBody = expectedResponse;
		try {
			actualResponseBody = (JSONObject) new JSONParser().parse(actualResponse.toString());
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponseBody, listOfElementToRemove);
			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponse, listOfElementToRemove);
			} catch (ParseException e) {
				logger.info(e.getMessage());
			}
		
		return jsonComparison(actualResponseBody, expectedResponseBody);

	}
	
	/**
	 * @author Arjun chandramohan
	 * Created for id repo assertion
	 * @param expectedResponse
	 * @param actualResponse
	 * @param listOfElementToRemove
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ParseException
	 */
		public boolean assertIdRepo(Object expectedResponse, Object actualResponse, List<String> listOfElementToRemove)
				throws JsonProcessingException, IOException, ParseException {
			JSONObject expectedResponseBody = (JSONObject) new JSONParser().parse(expectedResponse.toString());
			JSONObject actualResponseBody = (JSONObject) new JSONParser().parse(actualResponse.toString());

			expectedResponseBody = AssertKernel.removeElementFromBody(expectedResponseBody, listOfElementToRemove);
			actualResponseBody = AssertKernel.removeElementFromBody(actualResponseBody, listOfElementToRemove);

			return jsonComparison(expectedResponseBody, actualResponseBody);

		}
	/**
	 * this function compare the request and response json and return the boolean
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

			logger.error("======" + diffJson + "==========");
			if (diffJson.toString().equals("[]")) {
				logger.info(GlobalConstants.EQUAL);
				return true;
			}

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					logger.error("not equal");
					return false;
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.info(GlobalConstants.EQUAL);
		return true;

	}
	/**
	 * this function compare the request and response json and return the boolean
	 * value
	 * 
	 * @param expectedResponseBody
	 * @param actualResponseBody
	 * @return boolean value
	 */
	public boolean jsonComparison1(Object expectedResponseBody, Object actualResponseBody) {
		JSONObject reqObj = (JSONObject) expectedResponseBody;
		JSONObject resObj = (JSONObject) actualResponseBody;
		ObjectMapper mapper = new ObjectMapper();
		
			JsonNode requestJson = null;
			JsonNode responseJson = null;
			try {
				requestJson = mapper.readTree(reqObj.toString());
				responseJson = mapper.readTree(resObj.toString());
			} catch (IOException e1) {
				Assert.assertTrue(false, "File is not able to find "+e1.getClass());
			}
			
			
			JsonNode diffJson = JsonDiff.asJson(requestJson, responseJson);

			logger.error("======" + diffJson + "==========");
		

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					logger.error("not equal");
					Assert.assertTrue(false,"Response Data Mismatch Failure");
					return false;
				}
			}
			
				if (diffJson.toString().equals("[]")) {
					logger.info(GlobalConstants.EQUAL);
					return true;
				}
		
		logger.info(GlobalConstants.EQUAL);
		return true;

	}
	/**
	 * this method accept json as string and remove the element to remove
	 * 
	 * @param input
	 * @param removekey
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject removeElementFromBody(JSONObject response, List<String> listOfElementToRemove) throws ParseException {
		for (String elementToRemove : listOfElementToRemove) {
			if(response.containsKey(elementToRemove))
				response.remove(elementToRemove);
			else {
				JSONObject responseJson = null;

				if(response.containsKey(GlobalConstants.RESPONSE) && response.get(GlobalConstants.RESPONSE)!=null)
					{
					responseJson = (JSONObject)response.get(GlobalConstants.RESPONSE);
					if(responseJson.containsKey(elementToRemove))responseJson.remove(elementToRemove);
					response.put(GlobalConstants.RESPONSE, responseJson);
					}
				else if(response.containsKey(GlobalConstants.REQUEST) && response.get(GlobalConstants.REQUEST)!=null)
					{
					responseJson = (JSONObject)response.get(GlobalConstants.REQUEST);
					if(responseJson.containsKey(elementToRemove))responseJson.remove(elementToRemove);
					response.put(GlobalConstants.REQUEST, responseJson);
					}

			}
		}
		
		return response;
	}
	
	/**
	 * this method takes Json array of objects obtained in response, and key value of field send to fetch data and  
	 * list of attributes which should be present in response objects.
	 * @param responseArray
	 * @param attributesToValidate
	 * @param passedAttributes
	 * @return
	 */
	public static boolean validator(JSONArray responseArray, List<String> attributesToValidate, Map<String, String> passedAttributes)
	{
		for(int i=0;i<responseArray.size();i++) {
	    	 JSONObject object = (JSONObject) responseArray.get(i);
			 if(passedAttributes.size()>0)
			 {
				 String[] keys = passedAttributes.keySet().toArray(new String[passedAttributes.size()]);
				 for(int j=0;j<keys.length;j++) {
					 if(!passedAttributes.get(keys[j]).equals(object.get(keys[j]).toString()))
					 {
						 logger.error("Provided fields values is not Matching in obtained data");
						 Assert.fail("Provided fields values is not Matching in obtained data");
						 return false;
					 }
				 }
			 }
			 for(int j=0;j<attributesToValidate.size();j++) {
				 String key = attributesToValidate.get(j);
			 if (!object.containsKey(key) || object.get(key)==null) 
			        {
				 logger.error(key+"  is not present in obtained data");
				 Assert.fail(key + "  is not present in obtained data");
				 		return false;
			        }
			 }
			      
	    }
	return true;
	}

}
