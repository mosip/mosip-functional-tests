package io.mosip.ida.certificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.kernel.util.KeycloakUserManager;
import io.restassured.response.Response;

public class KeyCloakUserAndAPIKeyGeneration extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(KeyCloakUserAndAPIKeyGeneration.class);
	
	static String partnerId = PartnerRegistration.partnerId;
	static String emailId = PartnerRegistration.emailId;
	static String role = PartnerRegistration.partnerType;
	static String policyGroup = PartnerRegistration.policyGroup;
	static String randomAbbreviation = generateRandomAlphabeticString(4).toUpperCase();
	static String policyName = AdminTestUtil.policyName;
	
	public static String createKCUserAndGetAPIKey() {
		KeycloakUserManager.createKeyCloakUsers(partnerId, emailId, role);
		String mappingKey = submittingPartnerAndGetMappingKey();
		approvePartnerAPIKey(mappingKey);
		String apiKey = createAPIKey();
		
		return apiKey;
//		KeycloakUserManager.removeKeyCloakUser(partnerId);
		
	}
	public static String submittingPartnerAndGetMappingKey() {
		String url = ApplnURI + "/v1/partnermanager/partners/"+partnerId+"/policy/map";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("policyName", policyName);
		requestBody.put("useCaseDescription", "mapping Partner to policyName");
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
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
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("status", "Approved");
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
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
		
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("policyName", policyName);
		requestBody.put("label", randomAbbreviation);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
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

}
