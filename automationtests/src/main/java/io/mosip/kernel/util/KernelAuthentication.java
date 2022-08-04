package io.mosip.kernel.util;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class KernelAuthentication extends BaseTestCase{
	
	// Declaration of all variables
	String folder="kernel";
	String cookie;
	static String dataKey = "response";
    static String errorKey = "errors";
    static Map<String, String> tokens = new HashMap<String,String>();
	CommonLibrary clib= new CommonLibrary();
	public final Map<String, String> props = clib.readProperty("Kernel");
	
	private String admin_password=props.get("admin_password");
	private String admin_userName=props.get("admin_userName");
	
	private String partner_password=props.get("partner_user_password");
	private String partner_userName=props.get("partner_userName");
	
	private String registrationAdmin_appid=props.get("registrationAdmin_appid");;
	private String registrationAdmin_password=props.get("registrationAdmin_password");
	private String registrationAdmin_userName=props.get("registrationAdmin_userName");
	
	
	private String registrationOfficer_appid=props.get("registrationOfficer_appid");
	private String registrationOfficer_password=props.get("registrationOfficer_password");
	private String registrationOfficer_userName=props.get("registrationOfficer_userName");
	
	private String registrationSupervisor_appid=props.get("registrationSupervisor_appid");
	private String registrationSupervisor_password=props.get("registrationSupervisor_password");
	private String registrationSupervisor_userName=props.get("registrationSupervisor_userName");
	
	private String zonalAdmin_password=props.get("zonalAdmin_password");
	private String zonalAdmin_userName=props.get("zonalAdmin_userName");
	
	private String zonalApprover_password=props.get("zonalApprover_password");
	private String zonalApprover_userName=props.get("zonalApprover_userName");
	
	private String authenticationEndpoint = props.get("authentication");
	private String authenticationInternalEndpoint = props.get("authenticationInternal");
	private String sendOtp = props.get("sendOtp");
	private String useridOTP = props.get("useridOTP");
	private ApplicationLibrary appl=new ApplicationLibrary();
	private String authRequest="config/Authorization/request.json";
	private String authInternalRequest="config/Authorization/internalAuthRequest.json";
	private String preregSendOtp= props.get("preregSendOtp");
	private String preregValidateOtp= props.get("preregValidateOtp");

	
	
	public String getTokenByRole(String role)
	{
		String insensitiveRole = null;
		if(role!=null)
			insensitiveRole = role.toLowerCase();
		else return "";
		
		switch(insensitiveRole) {
		
		case "individual":
			if(!kernelCmnLib.isValidToken(individualCookie))
				individualCookie = kernelAuthLib.getAuthForIndividual();
			return individualCookie;
		case "ida":
			if(!kernelCmnLib.isValidToken(idaCookie))
				idaCookie = kernelAuthLib.getAuthForIDA();
			return idaCookie;
		case "idrepo":
			if(!kernelCmnLib.isValidToken(idrepoCookie))
				idrepoCookie = kernelAuthLib.getAuthForIDREPO();
			return idrepoCookie;
		case "regproc":
			if(!kernelCmnLib.isValidToken(regProcCookie))
				regProcCookie = kernelAuthLib.getAuthForRegistrationProcessor();
			return regProcCookie;
		case "admin":
			if(!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		case "zonalapprover":
			if(!kernelCmnLib.isValidToken(zonalApproverCookie))
				zonalApproverCookie = kernelAuthLib.getAuthForZonalApprover();
			return zonalApproverCookie;
		case "partner":
			if(!kernelCmnLib.isValidToken(partnerCookie))
				partnerCookie = kernelAuthLib.getAuthForPartner();
			return partnerCookie;
		case "policytest":
			if(!kernelCmnLib.isValidToken(policytestCookie))
				policytestCookie = kernelAuthLib.getAuthForPolicytest();
			return policytestCookie;
		case "batch":
			if (!kernelCmnLib.isValidToken(batchJobToken)) 
				batchJobToken = kernelAuthLib.getPreRegToken();
			return batchJobToken;
		case "invalid":
			return "anyRandomString";
		case "regAdmin":
			if (!kernelCmnLib.isValidToken(regAdminCookie)) 
				regAdminCookie = kernelAuthLib.getAuthForRegistrationAdmin();
			return regAdminCookie;
		case "resident":
			if(!kernelCmnLib.isValidToken(residentCookie))
				residentCookie = kernelAuthLib.getAuthForResident();
			return residentCookie;
		case "residentnew":
			if(!kernelCmnLib.isValidToken(residentNewCookie))
				residentNewCookie = kernelAuthLib.getAuthForNewResident();
			return residentNewCookie;
		case "hotlist":
			if(!kernelCmnLib.isValidToken(hotlistCookie))
				residentCookie = kernelAuthLib.getAuthForHotlist();
			return residentCookie;
		case "zonemap":
			if(!kernelCmnLib.isValidToken(zonemapCookie))
				zonemapCookie = kernelAuthLib.getAuthForzoneMap();
			return zonemapCookie;
		default:
			if(!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;			
		}
		 
	}
	
	
	@SuppressWarnings("unchecked")
	public String getAuthForAdmin() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put("appId", ConfigManager.getAdminAppId());
		request.put("password", admin_password);
		request.put("userName", admin_userName);
		request.put("clientId", ConfigManager.getAdminClientId());
		request.put("clientSecret", ConfigManager.getAdminClientSecret());
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;
	}
	
	
	@SuppressWarnings("unchecked")
	public String getAuthForzoneMap() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put("appId", ConfigManager.getAdminAppId());
		request.put("password", props.get("admin_zone_password"));
		request.put("userName", props.get("admin_zone_userName"));
		request.put("clientId", ConfigManager.getAdminClientId());
		request.put("clientSecret", ConfigManager.getAdminClientSecret());
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;
	}
	
	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartner() {		
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getPmsAppId());
		request.put("password", partner_password);
		request.put("userName", partner_userName);	
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put("clientId", ConfigManager.getPmsClientId());
		request.put("clientSecret", ConfigManager.getPmsClientSecret());
		actualInternalrequest.put("request", request);
		Response reponse=appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;			
	}
	
	@SuppressWarnings({ "unchecked" })
	public String getAuthForPolicytest() {		
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getPmsAppId());
		request.put("password", props.get("policytest_password"));
		request.put("userName", props.get("policytest_userName"));
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put("clientId", ConfigManager.getPmsClientId());
		request.put("clientSecret", ConfigManager.getPmsClientSecret());
		actualInternalrequest.put("request", request);
		Response reponse=appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;			
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForResident() {
		JSONObject actualrequest = getRequestJson(authRequest);
		logger.info("actualrequest " + actualrequest);
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getResidentAppId());
		request.put("clientId", ConfigManager.getResidentClientId());
		request.put("secretKey", ConfigManager.getResidentClientSecret());
		System.out.println("request for  Resident: " + request);
		logger.info("request for  Resident " + request);
		actualrequest.put("request", request);
		System.out.println("Actual Auth Request for Resident: " + actualrequest);
		logger.info("Actual Auth Request for Resident: " + actualrequest);
		Response reponse=appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForNewResident() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put("appId", props.get("resident_appid"));
		request.put("password", props.get("new_Resident_Password"));
		request.put("userName", props.get("new_Resident_User"));
		request.put("clientId", props.get("resident_clientId"));
		request.put("clientSecret", props.get("resident_secretKey"));
		actualrequest.put("request", request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString("token");
		return token;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForKeyCloak() {
		
		Response response = RestAssured.given().with().auth().preemptive()
				.basic(props.get("keycloak_username"), props.get("keycloak_password"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.formParam("grant_type", props.get("keycloak_granttype"))
				.formParam("client_id", props.get("keycloak_clientid"))
				.formParam("username", props.get("keycloak_username"))
				.formParam("password", props.get("keycloak_password")).when()
				.post(ApplnURIForKeyCloak + props.get("keycloakAuthURL"));
		System.out.println(response.getBody().asString());
		
		String responseBody = response.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getString("access_token");
		System.out.println(token);
		return token;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForHotlist() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getHotListAppId());
		request.put("clientId", ConfigManager.getHotListClientId());
		request.put("secretKey", ConfigManager.getHotListClientSecret());
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForIndividual() {	
		// getting request and expected response jsondata from json files.
        JSONObject actualRequest_generation = getRequestJson("kernel/Authorization/OtpGeneration/request.json");
        ((JSONObject)actualRequest_generation.get("request")).get("userId").toString();
        // getting request and expected response jsondata from json files.
        JSONObject actualRequest_validation = getRequestJson("kernel/Authorization/OtpGeneration/request.json");
        //sending for otp
        appl.postWithJson(sendOtp, actualRequest_generation);
        String otp=null;
        		if (proxy)
        			otp = "111111";
        		else {
        		}
        ((JSONObject)actualRequest_validation.get("request")).put("otp", otp);
        Response otpValidate=appl.postWithJson(useridOTP, actualRequest_validation);
        cookie=otpValidate.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getPreRegToken() {	
		// getting request and expected response jsondata from json files.
        JSONObject actualRequest_generation = getRequestJson("config/prereg_SendOtp.json");
        actualRequest_generation.put("requesttime", clib.getCurrentUTCTime());
        ((JSONObject)actualRequest_generation.get("request")).put("langCode", languageList.get(0));
        ((JSONObject)actualRequest_generation.get("request")).get("userId").toString();
        // getting request and expected response jsondata from json files.
        JSONObject actualRequest_validation = getRequestJson("config/prereg_ValidateOtp.json");
        //sending for otp
        appl.postWithJson(preregSendOtp, actualRequest_generation);
        String otp=null;
        		if (proxy)
        			otp = "111111";
        		else {
        		}
        ((JSONObject)actualRequest_validation.get("request")).put("otp", otp);
        actualRequest_validation.put("requesttime", clib.getCurrentUTCTime());
        Response otpValidate=appl.postWithJson(preregValidateOtp, actualRequest_validation);
        cookie=otpValidate.getCookie("Authorization");
		return cookie;
	}

	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationProcessor() {
	
	JSONObject actualrequest = getRequestJson(authRequest);
	JSONObject request=new JSONObject();
	request.put("appId", ConfigManager.getRegprocAppId());
	request.put("clientId", ConfigManager.getRegprocClientId());
	request.put("secretKey", ConfigManager.getRegprocClientSecret());
	actualrequest.put("request", request);
	
	Response reponse=appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
	cookie=reponse.getCookie("Authorization");
	return cookie;
}
	
	
	@SuppressWarnings("unchecked")
	public String getAuthForIDA() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getResidentAppId());
		request.put("clientId", ConfigManager.getResidentClientId());
		request.put("secretKey", ConfigManager.getResidentClientSecret());
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForIDREPO() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getidRepoAppId());
		request.put("clientId", ConfigManager.getidRepoClientId());
		request.put("secretKey", ConfigManager.getIdRepoClientSecret());
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(props.get("authclientidsecretkeyURL"), actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationAdmin() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request=new JSONObject();
		request.put("appId", registrationAdmin_appid);
		request.put("password", registrationAdmin_password);
		request.put("userName", registrationAdmin_userName);
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationOfficer() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", registrationOfficer_appid);
		request.put("password", registrationOfficer_password);
		request.put("userName", registrationOfficer_userName);
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationSupervisor() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", registrationSupervisor_appid);
		request.put("password", registrationSupervisor_password);
		request.put("userName", registrationSupervisor_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForZonalAdmin() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getAdminAppId());
		request.put("password", zonalAdmin_password);
		request.put("userName", zonalAdmin_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForZonalApprover() {
		JSONObject actualrequest = getRequestJson(authRequest);
		
		JSONObject request=new JSONObject();
		request.put("appId", ConfigManager.getAdminAppId());
		request.put("password", zonalApprover_password);
		request.put("userName", zonalApprover_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForAutoUser() {
		JSONObject actualrequest = getRequestJson(authRequest);	
		JSONObject request=new JSONObject();
		request.put("appId", props.get("autoUsr_appid"));
		request.put("password", props.get("autoUsr_password"));
		request.put("userName", props.get("autoUsr_user"));
		actualrequest.put("request", request);
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	
	
	
}
