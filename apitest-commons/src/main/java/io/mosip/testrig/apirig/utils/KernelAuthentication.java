package io.mosip.testrig.apirig.utils;

//import java.util.Base64.Encoder;
import java.util.Date;
//import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.DecodedJWT;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.OTPListener;
//import io.restassured.RestAssured;
import io.restassured.response.Response;

public class KernelAuthentication extends BaseTestCase {
	
	private static Logger logger = Logger.getLogger(KernelAuthentication.class);

	String folder = "kernel";
	String cookie;
	static String dataKey = "response";
	static String encodeBase64 ="";
	static String errorKey = "errors";
	static Map<String, String> tokens = new HashMap<>();
	public final Map<String, String> props = AdminTestUtil.readProperty("Kernel");

	private String admin_password = ConfigManager.getUserAdminPassword();

//	private String admin_userName = ConfigManager.getUserAdminName();

	private String partner_password = props.get("partner_user_password");
	private String partner_userName = props.get("partner_userName");
	private String partner_auth_externaluser_password = props.get("partner_auth_externaluser_password");
	private String partner_auth_external_userName = props.get("partner_auth_external_userName");
	private String partner_auth_userName = props.get("partner_auth_userName");
	private String device_provider_userName = props.get("device_provider_userName");
	private String partner_device_userName = props.get("partner_device_userName");
	private String partner_devicenew_userName = props.get("partner_devicenew_userName");
	private String partner_ftm_userName = props.get("partner_ftm_userName");
	private String partner_admin_userName = props.get("partner_admin_userName");
	private String partner_userName_without_role = props.get("policytest_userName");
	private String partner_userName_without_pm_role = props.get("policytest_without_pmrole_userName");

	private String registrationAdmin_appid = props.get("registrationAdmin_appid");
	private String registrationAdmin_password = props.get("registrationAdmin_password");
	private String registrationAdmin_userName = props.get("registrationAdmin_userName");

	private String registrationOfficer_appid = props.get("registrationOfficer_appid");
	private String registrationOfficer_password = props.get("registrationOfficer_password");
	private String registrationOfficer_userName = props.get("registrationOfficer_userName");

	private String registrationSupervisor_appid = props.get("registrationSupervisor_appid");
	private String registrationSupervisor_password = props.get("registrationSupervisor_password");
	private String registrationSupervisor_userName = props.get("registrationSupervisor_userName");

	private String zonalAdmin_password = props.get("zonalAdmin_password");
	private String zonalAdmin_userName = props.get("zonalAdmin_userName");

	private String zonalApprover_password = props.get("zonalApprover_password");
	private String zonalApprover_userName = props.get("zonalApprover_userName");

	private String authenticationEndpoint = props.get("authentication");
	private String authenticationInternalEndpoint = props.get("authenticationInternal");
	private String authRequest = "config/Authorization/request.json";
	private String authInternalRequest = "config/Authorization/internalAuthRequest.json";
	private String preregSendOtp = props.get("preregSendOtp");
	private String preregValidateOtp = props.get("preregValidateOtp");

	protected static final String ESIGNETUINCOOKIESRESPONSE = "ESignetUINCookiesResponse";
	protected static final String ESIGNETVIDCOOKIESRESPONSE = "ESignetVIDCookiesResponse";
	
	private static final String GRANT_TYPE = "client_credentials";
	private static final String GRANT_TYPE_PASSWORD = "password";
	private static final String CLIENT_ID = "client_id";
	private static final String CLIENT_SECRET = "client_secret";
	private static final String GRANT_TYPE_KEY = "grant_type";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String ACCESS_TOKEN = "access_token";
	
    private static String partnerKeycloakToken = null;
    private static String mobileAuthKeycloakCookie = null;

	private static String getKeycloakTokenUrl() {
		return ConfigManager.getIAMUrl() + "/realms/"
				+ ConfigManager.getIAMRealmId() + "/protocol/openid-connect/token";
	}

	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public String getTokenByRole(String role) {
		return getTokenByRole(role, null);
	}

	public String getTokenByRole(String role, String tokenType) {
		String insensitiveRole = null;
		if (role != null)
			insensitiveRole = role.toLowerCase();
		else
			return "";

		switch (insensitiveRole) {

		case "ida":
			if (!AdminTestUtil.isValidToken(idaCookie))
				idaCookie = kernelAuthLib.getAuthForIDA();
			return idaCookie;
		case "idrepo":
			if (!AdminTestUtil.isValidToken(idrepoCookie))
				idrepoCookie = kernelAuthLib.getAuthForIDREPO();
			return idrepoCookie;
		case "regproc":
		case "regpro":
			if (!AdminTestUtil.isValidToken(regProCookie))
				regProCookie = kernelAuthLib.getAuthForRegProc();
			return regProCookie;
		case "admin":
			if (!AdminTestUtil.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		case "testrig":
			if (!AdminTestUtil.isValidToken(testrigCookie))
				testrigCookie = kernelAuthLib.getAuthForTestRigClient();
			return testrigCookie;
		case "zonalapprover":
			if (!AdminTestUtil.isValidToken(zonalApproverCookie))
				zonalApproverCookie = kernelAuthLib.getAuthForZonalApprover();
			return zonalApproverCookie;
		case "partnerauth":
			if (!AdminTestUtil.isValidToken(partnerauthCookie))
				partnerauthCookie = kernelAuthLib.getAuthForPartnerAuth();
			return partnerauthCookie;
			
		case "partnerauthexternal":
			if (!AdminTestUtil.isValidToken(partnerauthexternalCookie))
				partnerauthexternalCookie = kernelAuthLib.getAuthForPartnerAuthExternal();
			return partnerauthexternalCookie;
		
		case "deviceprovider":
			if (!AdminTestUtil.isValidToken(deviceproviderCookie))
				deviceproviderCookie = kernelAuthLib.getAuthForDeviceProvider();
			return deviceproviderCookie;
			
		case "partnerdevice":
			if (!AdminTestUtil.isValidToken(partnerdeviceCookie))
				partnerdeviceCookie = kernelAuthLib.getAuthForPartnerRevampDevice();
			return partnerdeviceCookie;
			
		case "partnerdevicenew":
			if (!AdminTestUtil.isValidToken(partnerdevicenewCookie))
				partnerdevicenewCookie = kernelAuthLib.getAuthForPartnerRevampDeviceNew();
			return partnerdevicenewCookie;
			
		case "partnerftm":
			if (!AdminTestUtil.isValidToken(partnerftmCookie))
				partnerftmCookie = kernelAuthLib.getAuthForPartnerRevampFtm();
			return partnerftmCookie;
			
		case "partneradmin":
			if (!AdminTestUtil.isValidToken(partneradminCookie))
				partneradminCookie = kernelAuthLib.getAuthForPartnerRevampAdmin();
			return partneradminCookie;
		case "partner":
			if (!AdminTestUtil.isValidToken(partnerCookie))
				partnerCookie = kernelAuthLib.getAuthForPartner();
			return partnerCookie;
		case "partnernew":
			if (!AdminTestUtil.isValidToken(partnerNewCookie))
				partnerNewCookie = kernelAuthLib.getAuthForNewPartner();
			return partnerNewCookie;
		case "withoutpartner":
			if (!AdminTestUtil.isValidToken(withoutpartnerCookie))
				withoutpartnerCookie = kernelAuthLib.getAuthForPartnerWithoutPAdminRole();
			return withoutpartnerCookie;

		case "withoutpolicymanager":
			if (!AdminTestUtil.isValidToken(withoutpolicyCookie))
				withoutpolicyCookie = kernelAuthLib.getAuthForPartnerWithoutPManagerRole();
			return withoutpolicyCookie;

		case "partnernewkyc":
			if (!AdminTestUtil.isValidToken(partnerNewKycCookie))
				partnerNewKycCookie = kernelAuthLib.getAuthForNewKycPartner();
			return partnerNewKycCookie;
		case "esignetpartner":
			if (!AdminTestUtil.isValidToken(esignetPartnerCookie))
				esignetPartnerCookie = kernelAuthLib.getAuthForNewPartnerEsignet();
			return esignetPartnerCookie;
		case "esignetpartnerkyc":
			if (!AdminTestUtil.isValidToken(esignetPartnerKycCookie))
				esignetPartnerKycCookie = kernelAuthLib.getAuthForNewPartnerEsignetKyc();
			return esignetPartnerKycCookie;
		case "policytest":
			if (!AdminTestUtil.isValidToken(policytestCookie))
				policytestCookie = kernelAuthLib.getAuthForPolicytest();
			return policytestCookie;
		case "batch":
			if (!AdminTestUtil.isValidToken(batchJobToken))
				batchJobToken = kernelAuthLib.getPreRegToken();
			return batchJobToken;

		case "invalidbatch":
			if (!AdminTestUtil.isValidToken(invalidBatchJobToken))
				invalidBatchJobToken = kernelAuthLib.getPreRegInvalidToken();
			return invalidBatchJobToken;
		case "invalid":
			return "anyRandomString";
		case "invalidtoken":
			return	kernelAuthLib.encodeBase64("AnyRandomString-ToCreate-Jwt");
		case "noauth":
			return "";
		case "regAdmin":
			if (!AdminTestUtil.isValidToken(regAdminCookie))
				regAdminCookie = kernelAuthLib.getAuthForRegistrationAdmin();
			return regAdminCookie;
		case GlobalConstants.RESIDENT:
			if (!AdminTestUtil.isValidToken(residentCookie))
				residentCookie = kernelAuthLib.getAuthForResident();
			return residentCookie;
		case "residentnew":
			if (!AdminTestUtil.isValidToken(residentNewCookie.get(tokenType)))
				residentNewCookie = getAuthFromEsignet(ESIGNETUINCOOKIESRESPONSE);
			return residentNewCookie.get(tokenType);
		case "residentnewvid":
			if (!AdminTestUtil.isValidToken(residentNewVidCookie.get(tokenType)))
				residentNewVidCookie = getAuthFromEsignet(ESIGNETVIDCOOKIESRESPONSE);
			return residentNewVidCookie.get(tokenType);
		case "residentnewKc":
			if (!AdminTestUtil.isValidToken(residentNewCookieKc))
				residentNewCookieKc = kernelAuthLib.getAuthForNewResidentKc();
			return residentNewCookieKc;
		case "hotlist":
			if (!AdminTestUtil.isValidToken(hotlistCookie))
				hotlistCookie = kernelAuthLib.getAuthForHotlist();
			return hotlistCookie;

		case "globaladmin":
			if (!AdminTestUtil.isValidToken(zonemapCookie))
				zonemapCookie = kernelAuthLib.getAuthForzoneMap();
			return zonemapCookie;	
		case "user":
			dslUserCookie = kernelAuthLib.getAuthForUser();
			return dslUserCookie;
		case "mobileauth":
			if (!AdminTestUtil.isValidToken(mobileAuthCookie))
				mobileAuthCookie = kernelAuthLib.getAuthForMobile();
			return mobileAuthCookie;
		case "state":
			UUID uuid = UUID.randomUUID();

			String uuidAsString = uuid.toString();
			return uuidAsString;
		default:
			if (!AdminTestUtil.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		}
		
	}

	public static String getAuthTokenFromKeyCloak(String clientId, String clientSecret) {
		String tokenUrl = getKeycloakTokenUrl();
		Map<String, String> params = new HashMap<>();
		params.put(CLIENT_ID, clientId);
		params.put(CLIENT_SECRET, clientSecret);
		params.put(GRANT_TYPE_KEY, GRANT_TYPE);

		Response response = null;

		try {
			response = RestClient.postRequestWithFormDataBody(tokenUrl, params);
		} catch (Exception e) {
			logger.error("Error sending POST request to Keycloak token URL: " + tokenUrl, e);
			return "";
		}

		if (response == null) {
			logger.error("Keycloak token request returned null response");
			return "";
		}
		int statusCode = response.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			logger.error("Keycloak token request failed with status code: " + statusCode);
			return "";
		}
		logger.info("Keycloak token request successful");

		org.json.JSONObject responseJson = new org.json.JSONObject(response.getBody().asString());
		return responseJson.optString(ACCESS_TOKEN, "");
	}

	public static String getAuthTokenFromKeyCloakPassword(String clientId, String clientSecret, String username,
			String password) {
		String tokenUrl = getKeycloakTokenUrl();
		Map<String, String> params = new HashMap<>();
		params.put(CLIENT_ID, clientId);
		params.put(CLIENT_SECRET, clientSecret);
		params.put(GRANT_TYPE_KEY, GRANT_TYPE_PASSWORD);
		params.put(USERNAME_KEY, username);
		params.put(PASSWORD_KEY, password);

		Response response = null;

		try {
			response = RestClient.postRequestWithFormDataBody(tokenUrl, params);
		} catch (Exception e) {
			logger.error("Error sending POST request to Keycloak token URL: " + tokenUrl, e);
			return "";
		}

		if (response == null) {
			logger.error("Keycloak token request returned null response");
			return "";
		}
		int statusCode = response.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			logger.error("Keycloak token request failed with status code: " + statusCode);
			return "";
		}
		logger.info("Keycloak token request successful");

		org.json.JSONObject responseJson = new org.json.JSONObject(response.getBody().asString());
		return responseJson.optString(ACCESS_TOKEN, "");
	}

	public static String getAuthTokenByRole(String role) {
		if (role == null)
			return "";

		String roleLowerCase = role.toLowerCase();
		switch (roleLowerCase) {
		case "partner":
			if (!AdminTestUtil.isValidToken(partnerKeycloakToken)) {
				partnerKeycloakToken = getAuthTokenFromKeyCloak(ConfigManager.getPmsClientId(),
						ConfigManager.getPmsClientSecret());
			}
			return partnerKeycloakToken;
		case "mobileauth":
			if (!AdminTestUtil.isValidToken(mobileAuthKeycloakCookie)) {
				mobileAuthKeycloakCookie = getAuthTokenFromKeyCloak(ConfigManager.getMPartnerMobileClientId(),
						ConfigManager.getMPartnerMobileClientSecret());
			}
			return mobileAuthKeycloakCookie;
		default:
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getAuthFromEsignet(String keyName) {
		HashMap<String, String> tokens = new HashMap<>();

		org.json.JSONObject jsonCookies = new org.json.JSONObject(CertsUtil.getCertificate(keyName));
		tokens.put(GlobalConstants.ACCESSTOKEN, jsonCookies.get(GlobalConstants.ACCESSTOKEN).toString());
		tokens.put("id_token", jsonCookies.get("id_token").toString());

		return tokens;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForAdmin() {
		String username = BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName();
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getAdminClientId(), ConfigManager.getAdminClientSecret(),
				username, admin_password);
	}

	@SuppressWarnings("unchecked")
	public String getAuthForzoneMap() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getAdminClientId(), ConfigManager.getAdminClientSecret(),
				props.get("admin_zone_userName"), props.get("admin_zone_password"));
	}

	@SuppressWarnings("unchecked")
	public String getAuthForUser() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getAdminClientId(), ConfigManager.getAdminClientSecret(),
				BaseTestCase.dslUser, BaseTestCase.dslUserPwd);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartner() {
		String username = BaseTestCase.currentModule + "-" + partner_userName;
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				username, partner_password);
	}
	
	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerAuth() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_auth_userName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerAuthExternal() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_auth_external_userName, partner_auth_externaluser_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForDeviceProvider() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + device_provider_userName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerRevampDevice() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_device_userName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerRevampDeviceNew() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_devicenew_userName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerRevampFtm() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_ftm_userName, partner_password);
	}

	public String getAuthForPartnerRevampAdmin() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.runContext + partner_admin_userName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartner() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				PartnerRegistration.partnerId, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerWithoutPAdminRole() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.currentModule + "-" + partner_userName_without_role, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerWithoutPManagerRole() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				BaseTestCase.currentModule + "-" + partner_userName_without_pm_role, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewKycPartner() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				PartnerRegistration.ekycPartnerId, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartnerEsignet() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				AdminTestUtil.genPartnerName, partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartnerEsignetKyc() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				AdminTestUtil.genPartnerName + "2n", partner_password);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPolicytest() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getPmsClientId(), ConfigManager.getPmsClientSecret(),
				props.get("policytest_userName"), props.get("policytest_password"));
	}

	@SuppressWarnings("unchecked")
	public String getAuthForResident() {
		return getAuthTokenFromKeyCloak(ConfigManager.getResidentClientId(), ConfigManager.getResidentClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForMobile() {
		return getAuthTokenFromKeyCloak(ConfigManager.getMPartnerMobileClientId(), ConfigManager.getMPartnerMobileClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForNewResidentKc() {
		return getAuthTokenFromKeyCloakPassword(ConfigManager.getResidentClientId(), ConfigManager.getResidentClientSecret(),
				BaseTestCase.currentModule + "-" + props.get("new_Resident_User"), props.get("new_Resident_Password"));
	}

	@SuppressWarnings("unchecked")
	public String getAuthForHotlist() {
		return getAuthTokenFromKeyCloak(ConfigManager.getHotListClientId(), ConfigManager.getHotListClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getPreRegToken() {
		JSONObject actualRequest_generation = getRequestJson("config/prereg_SendOtp.json");
		actualRequest_generation.put(GlobalConstants.REQUESTTIME, AdminTestUtil.getCurrentUTCTime());
		((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).put("langCode",
				BaseTestCase.getLanguageList().get(0));
		String userId = AdminTestUtil.preRegUser;
        ((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).put("userId",
                userId);
		JSONObject actualRequest_validation = getRequestJson("config/prereg_ValidateOtp.json");
		AdminTestUtil.postWithJson(preregSendOtp, actualRequest_generation);
		String otp = null;
		if (ConfigManager.getUsePreConfiguredOtp().equalsIgnoreCase("yes"))
			//TODO REMOVE THE HARDCODING
			otp = "111111";
		else {
			otp = NotificationListener.getOtp(userId);
		}
		((JSONObject) actualRequest_validation.get(GlobalConstants.REQUEST)).put("otp", otp);
		actualRequest_validation.put(GlobalConstants.REQUESTTIME, AdminTestUtil.getCurrentUTCTime());
        ((JSONObject) actualRequest_validation.get(GlobalConstants.REQUEST)).put("userId",
                userId);
		Response otpValidate = AdminTestUtil.postWithJson(preregValidateOtp, actualRequest_validation);
		cookie = otpValidate.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getPreRegInvalidToken() {
		cookie = "ddhdh76478383hdgdgdgg@#$%$%%^^^^^$###$fgdhdhdjj";
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationProcessor() {
		return getAuthTokenFromKeyCloak(ConfigManager.getRegprocClientId(), ConfigManager.getRegprocClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegProc() {
		return getAuthTokenFromKeyCloak(ConfigManager.getRegprocClientId(), ConfigManager.getRegprocClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForIDA() {
		return getAuthTokenFromKeyCloak(ConfigManager.getResidentClientId(), ConfigManager.getResidentClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForIDREPO() {
		return getAuthTokenFromKeyCloak(ConfigManager.getidRepoClientId(), ConfigManager.getIdRepoClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForTestRigClient() {
		return getAuthTokenFromKeyCloak(ConfigManager.getAutomationClientId(), ConfigManager.getAutomationClientSecret());
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationAdmin() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, registrationAdmin_appid);
		request.put(GlobalConstants.PASSWORD, registrationAdmin_password);
		request.put(GlobalConstants.USER_NAME, registrationAdmin_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationOfficer() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, registrationOfficer_appid);
		request.put(GlobalConstants.PASSWORD, registrationOfficer_password);
		request.put(GlobalConstants.USER_NAME, registrationOfficer_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationSupervisor() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, registrationSupervisor_appid);
		request.put(GlobalConstants.PASSWORD, registrationSupervisor_password);
		request.put(GlobalConstants.USER_NAME, registrationSupervisor_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForZonalAdmin() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getAdminAppId());
		request.put(GlobalConstants.PASSWORD, zonalAdmin_password);
		request.put(GlobalConstants.USER_NAME, zonalAdmin_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForZonalApprover() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getAdminAppId());
		request.put(GlobalConstants.PASSWORD, zonalApprover_password);
		request.put(GlobalConstants.USER_NAME, zonalApprover_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForAutoUser() {
		JSONObject actualrequest = getRequestJson(authRequest);
		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, props.get("autoUsr_appid"));
		request.put(GlobalConstants.PASSWORD, props.get("autoUsr_password"));
		request.put(GlobalConstants.USER_NAME, props.get("autoUsr_user"));
		actualrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = AdminTestUtil.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String encodeBase64(String value) {
		  String secret = value;
	        // Create the token
	        String token = JWT.create()
	                .withSubject("user123")
	                .withIssuer("example.com")
	                .withClaim("role", "admin")
	                .withIssuedAt(new Date())
	                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
	                .sign(Algorithm.HMAC256(secret));
	        System.out.println("Generated Token: " + token);
			return token;
		}

}
