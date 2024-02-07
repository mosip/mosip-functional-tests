package io.mosip.testrig.apirig.ida.certificate;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.kernel.util.KeycloakUserManager;
import io.restassured.response.Response;

public class KeyCloakUserAndAPIKeyGeneration extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(KeyCloakUserAndAPIKeyGeneration.class);
	
	static String partnerId = PartnerRegistration.partnerId;
	static String updatedPolicyId =AdminTestUtil.updatedPolicyId;
	static String emailId = PartnerRegistration.emailId;
	static String emailIdForKyc = PartnerRegistration.emailIdForKyc;
	static String role = PartnerRegistration.partnerType;
	static String policyGroup = PartnerRegistration.policyGroup;
	static String randomAbbreviation = generateRandomAlphabeticString(4).toUpperCase();
	static String policyName = AdminTestUtil.policyName;
	static String policyNameForUpdate = AdminTestUtil.policyNameForUpdate;
	
	static String policyGroup2 = AdminTestUtil.policyGroup2;
	static String policyName2 = AdminTestUtil.policyName2;
	
	static String ekycPartnerId = PartnerRegistration.ekycPartnerId;
	
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			lOGGER.setLevel(Level.ALL);
		else
			lOGGER.setLevel(Level.ERROR);
	}
	
	public static String createKCUserAndGetAPIKey() {
		KeycloakUserManager.createKeyCloakUsers(partnerId, emailId, role);
		String mappingKey = submittingPartnerAndGetMappingKey();
		approvePartnerAPIKey(mappingKey);
		return createAPIKey();
		
	}
	
	public static String createKCUserAndGetUpdatedAPIKey() {
		KeycloakUserManager.createKeyCloakUsers(partnerId, emailId, role);
		String updatedMappingKey = submittingPartnerAndGetMappingKeyWithUpdatePolicy();
	    approvePartnerAPIKey(updatedMappingKey);
		return createAPIKeyForUpdatedPolicy();
		
	}
	
	public static String createKCUserAndGetAPIKeyForKyc() {
		KeycloakUserManager.createKeyCloakUsers(ekycPartnerId, emailIdForKyc, role);
		String mappingKey = submittingPartnerAndGetMappingKeyForKyc();
		approvePartnerAPIKey(mappingKey);
		return createAPIKeyForEkyc();
	}
	
	public static String submittingPartnerAndGetMappingKey() {
		String url = ApplnURI + "/v1/partnermanager/partners/"+partnerId+"/policy/map";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyName);
		requestBody.put("useCaseDescription", "mapping Partner to policyName");
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String mappingKey = responseValue.getString("mappingkey");
		lOGGER.info(mappingKey);
		
		return mappingKey;
	}
	
	public static String submittingPartnerAndGetMappingKeyWithUpdatePolicy() {
		String url = ApplnURI + "/v1/partnermanager/partners/"+partnerId+"/policy/map";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyNameForUpdate);
		requestBody.put("useCaseDescription", "mapping Partner to policyName for update");
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String mappingKey = responseValue.getString("mappingkey");
		lOGGER.info(mappingKey);
		
		return mappingKey;
	}
	
	public static String submittingPartnerAndGetMappingKeyForKyc() {
		String url = ApplnURI + "/v1/partnermanager/partners/"+ekycPartnerId+"/policy/map";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyName2);
		requestBody.put("useCaseDescription", "mapping Partner to policyName");
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String mappingKey = responseValue.getString("mappingkey");
		lOGGER.info(mappingKey);
		
		return mappingKey;
	}
	
	public static void approvePartnerAPIKey(String mappingKey){
		String url = ApplnURI + "/v1/partnermanager/partners/policy/"+mappingKey;
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("status", "Approved");
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.putRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
	}
	
	public static String createAPIKey(){
		String url = ApplnURI + "/v1/partnermanager/partners/"+partnerId+"/generate/apikey";
		
		String token = kernelAuthLib.getTokenByRole("partnernew");
		
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyName);
		requestBody.put("label", randomAbbreviation);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.patchRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String apiKey = responseValue.getString(GlobalConstants.APIKEY);
		lOGGER.info(apiKey);
		
		return apiKey;
	}
	public static String createAPIKeyForUpdatedPolicy(){
		String url = ApplnURI + "/v1/partnermanager/partners/" + partnerId + "/generate/apikey";

		String updateApiKeyUrl = ApplnURI + "/v1/partnermanager/partners/" + partnerId + "/policy/" + updatedPolicyId
				+ "/apiKey/status";

		String token = kernelAuthLib.getTokenByRole("partnernew");
		
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyNameForUpdate);
		requestBody.put("label", randomAbbreviation);
		
		
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.patchRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String apiKey = responseValue.getString(GlobalConstants.APIKEY);
		lOGGER.info(apiKey);
		
		//UpdatedApiKey Call
		
		HashMap<String, String> updatedRequestBody = new HashMap<>();
		HashMap<String, Object> updatedRBody = new HashMap<>();
		
		updatedRequestBody.put("label", randomAbbreviation);
		updatedRequestBody.put("status", "Active");
		
		updatedRBody.put("id", GlobalConstants.STRING);
		updatedRBody.put(GlobalConstants.METADATA, new HashMap<>());
		updatedRBody.put(GlobalConstants.REQUEST, updatedRequestBody);
		updatedRBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		updatedRBody.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		
		Response updatedResponse = RestClient.patchRequestWithCookie(url, updatedRBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(updatedResponse.asString());
		
		return apiKey;
	}
	
	public static String createAPIKeyForEkyc(){
		String url = ApplnURI + "/v1/partnermanager/partners/"+ekycPartnerId+"/generate/apikey";
		
		String token = kernelAuthLib.getTokenByRole("partnernewkyc");
		
		
		HashMap<String, String> requestBody = new HashMap<>();
		
		requestBody.put("policyName", policyName2);
		requestBody.put("label", randomAbbreviation+randomAbbreviation);
		
		HashMap<String, Object> body = new HashMap<>();
		
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);
		
		Response response = RestClient.patchRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
		String apiKeyForEkyc = responseValue.getString(GlobalConstants.APIKEY);
		lOGGER.info(apiKeyForEkyc);
		
		return apiKeyForEkyc;
	}

}
