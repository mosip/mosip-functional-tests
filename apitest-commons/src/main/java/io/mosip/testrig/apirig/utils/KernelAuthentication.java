package io.mosip.testrig.apirig.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.MockSMTPListener;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class KernelAuthentication extends BaseTestCase {

	String folder = "kernel";
	String cookie;
	static String dataKey = "response";
	static String errorKey = "errors";
	static Map<String, String> tokens = new HashMap<>();
	CommonLibrary clib = new CommonLibrary();
	public final Map<String, String> props = clib.readProperty("Kernel");

	private String admin_password = props.get("admin_password");

//	private String admin_userName = props.get("admin_userName");
	private String admin_userName = ConfigManager.getUserAdminName();

	private String partner_password = props.get("partner_user_password");
	private String partner_userName = props.get("partner_userName");
	private String partner_revamp_userName = props.get("partner_revamp_userName");
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
	private String sendOtp = props.get(GlobalConstants.SENDOTP);
	private String useridOTP = props.get("useridOTP");
	private ApplicationLibrary appl = new ApplicationLibrary();
	private String authRequest = "config/Authorization/request.json";
	private String authInternalRequest = "config/Authorization/internalAuthRequest.json";
	private String preregSendOtp = props.get("preregSendOtp");
	private String preregValidateOtp = props.get("preregValidateOtp");

	protected static final String ESIGNETUINCOOKIESRESPONSE = "ESignetUINCookiesResponse";
	protected static final String ESIGNETVIDCOOKIESRESPONSE = "ESignetVIDCookiesResponse";

//	private static File ESignetUINCookiesFile = new File(
//			AdminTestUtil.getResourcePath() + "ESignetUINCookiesResponse.txt");
//	private static File ESignetVIDCookiesFile = new File(
//			AdminTestUtil.getResourcePath() + "ESignetVIDCookiesResponse.txt");

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

		case "individual":
			if (!kernelCmnLib.isValidToken(individualCookie))
				individualCookie = kernelAuthLib.getAuthForIndividual();
			return individualCookie;
		case "ida":
			if (!kernelCmnLib.isValidToken(idaCookie))
				idaCookie = kernelAuthLib.getAuthForIDA();
			return idaCookie;
		case "idrepo":
			if (BaseTestCase.isTargetEnvLTS()) {
				if (!kernelCmnLib.isValidToken(idrepoCookie))
					idrepoCookie = kernelAuthLib.getAuthForIDREPO();
				return idrepoCookie;
			} else {
				if (!kernelCmnLib.isValidToken(regProCookie))
					regProCookie = kernelAuthLib.getAuthForRegProc();
				return regProCookie;
			}
		case "regproc":
			if (!kernelCmnLib.isValidToken(regProcCookie))
				regProcCookie = kernelAuthLib.getAuthForRegistrationProcessor();
			return regProcCookie;
		case "regpro":
			if (!kernelCmnLib.isValidToken(regProCookie))
				regProCookie = kernelAuthLib.getAuthForRegProc();
			return regProCookie;
		case "admin":
			if (!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
		case "zonalapprover":
			if (!kernelCmnLib.isValidToken(zonalApproverCookie))
				zonalApproverCookie = kernelAuthLib.getAuthForZonalApprover();
			return zonalApproverCookie;
		case "partnerrevamp":
			if (!kernelCmnLib.isValidToken(partnerrevampCookie))
				partnerrevampCookie = kernelAuthLib.getAuthForPartnerRevamp();
			return partnerrevampCookie;
		case "partner":
			if (!kernelCmnLib.isValidToken(partnerCookie))
				partnerCookie = kernelAuthLib.getAuthForPartner();
			return partnerCookie;
		case "partnernew":
			if (!kernelCmnLib.isValidToken(partnerNewCookie))
				partnerNewCookie = kernelAuthLib.getAuthForNewPartner();
			return partnerNewCookie;
		case "withoutpartner":
			if (!kernelCmnLib.isValidToken(withoutpartnerCookie))
				withoutpartnerCookie = kernelAuthLib.getAuthForPartnerWithoutPAdminRole();
			return withoutpartnerCookie;

		case "withoutpolicymanager":
			if (!kernelCmnLib.isValidToken(withoutpolicyCookie))
				withoutpolicyCookie = kernelAuthLib.getAuthForPartnerWithoutPManagerRole();
			return withoutpolicyCookie;

		case "partnernewkyc":
			if (!kernelCmnLib.isValidToken(partnerNewKycCookie))
				partnerNewKycCookie = kernelAuthLib.getAuthForNewKycPartner();
			return partnerNewKycCookie;
		case "esignetpartner":
			if (!kernelCmnLib.isValidToken(esignetPartnerCookie))
				esignetPartnerCookie = kernelAuthLib.getAuthForNewPartnerEsignet();
			return esignetPartnerCookie;
		case "esignetpartnerkyc":
			if (!kernelCmnLib.isValidToken(esignetPartnerKycCookie))
				esignetPartnerKycCookie = kernelAuthLib.getAuthForNewPartnerEsignetKyc();
			return esignetPartnerKycCookie;
		case "policytest":
			if (!kernelCmnLib.isValidToken(policytestCookie))
				policytestCookie = kernelAuthLib.getAuthForPolicytest();
			return policytestCookie;
		case "batch":
			if (!kernelCmnLib.isValidToken(batchJobToken))
				batchJobToken = kernelAuthLib.getPreRegToken();
			return batchJobToken;

		case "invalidbatch":
			if (!kernelCmnLib.isValidToken(invalidBatchJobToken))
				invalidBatchJobToken = kernelAuthLib.getPreRegInvalidToken();
			return invalidBatchJobToken;

		case "invalid":
			return "anyRandomString";
		case "regAdmin":
			if (!kernelCmnLib.isValidToken(regAdminCookie))
				regAdminCookie = kernelAuthLib.getAuthForRegistrationAdmin();
			return regAdminCookie;
		case GlobalConstants.RESIDENT:
			if (!kernelCmnLib.isValidToken(residentCookie))
				residentCookie = kernelAuthLib.getAuthForResident();
			return residentCookie;
		case "residentnew":
			if (!kernelCmnLib.isValidToken(residentNewCookie.get(tokenType)))
				residentNewCookie = getAuthFromEsignet(ESIGNETUINCOOKIESRESPONSE);
			return residentNewCookie.get(tokenType);
		case "residentnewvid":
			if (!kernelCmnLib.isValidToken(residentNewVidCookie.get(tokenType)))
				residentNewVidCookie = getAuthFromEsignet(ESIGNETVIDCOOKIESRESPONSE);
			return residentNewVidCookie.get(tokenType);
		case "residentnewKc":
			if (!kernelCmnLib.isValidToken(residentNewCookieKc))
				residentNewCookieKc = kernelAuthLib.getAuthForNewResidentKc();
			return residentNewCookieKc;
		case "hotlist":
			if (BaseTestCase.isTargetEnvLTS()) {
				if (!kernelCmnLib.isValidToken(hotlistCookie))
					residentCookie = kernelAuthLib.getAuthForHotlist();
				return residentCookie;
			} else {
				if (!kernelCmnLib.isValidToken(regProCookie))
					regProCookie = kernelAuthLib.getAuthForRegProc();
				return regProCookie;
			}
		case "globaladmin":
			if (!kernelCmnLib.isValidToken(zonemapCookie))
				zonemapCookie = kernelAuthLib.getAuthForzoneMap();
			return zonemapCookie;
		case "mobileauth":
			if (!kernelCmnLib.isValidToken(mobileAuthCookie))
				mobileAuthCookie = kernelAuthLib.getAuthForMobile();
			return mobileAuthCookie;
		case "state":
			UUID uuid = UUID.randomUUID();

			String uuidAsString = uuid.toString();
			return uuidAsString;
		default:
			if (!kernelCmnLib.isValidToken(adminCookie))
				adminCookie = kernelAuthLib.getAuthForAdmin();
			return adminCookie;
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

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getAdminAppId());
		request.put(GlobalConstants.PASSWORD, admin_password);

		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + ConfigManager.getUserAdminName());

		request.put(GlobalConstants.CLIENTID, ConfigManager.getAdminClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getAdminClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings("unchecked")
	public String getAuthForzoneMap() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getAdminAppId());
		request.put(GlobalConstants.PASSWORD, props.get("admin_zone_password"));
		request.put(GlobalConstants.USER_NAME, props.get("admin_zone_userName"));
		request.put(GlobalConstants.CLIENTID, ConfigManager.getAdminClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getAdminClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartner() {

		JSONObject request = new JSONObject();

		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + partner_userName);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		if (BaseTestCase.isTargetEnvLTS()) {
			request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
			request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		} else {
			request.put(GlobalConstants.CLIENTID, ConfigManager.getPartnerClientId());
			request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPartnerClientSecret());
		}
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());

		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}
	
	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerRevamp() {

		JSONObject request = new JSONObject();

		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, partner_revamp_userName);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		if (BaseTestCase.isTargetEnvLTS()) {
			request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
			request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		} else {
			request.put(GlobalConstants.CLIENTID, ConfigManager.getPartnerClientId());
			request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPartnerClientSecret());
		}
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());

		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartner() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, PartnerRegistration.partnerId);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerWithoutPAdminRole() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + partner_userName_without_role);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPartnerWithoutPManagerRole() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + partner_userName_without_pm_role);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewKycPartner() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, PartnerRegistration.ekycPartnerId);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartnerEsignet() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, AdminTestUtil.genPartnerName);
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}
	
	@SuppressWarnings({ "unchecked" })
	public String getAuthForNewPartnerEsignetKyc() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, partner_password);
		request.put(GlobalConstants.USER_NAME, AdminTestUtil.genPartnerName + "2n");
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings({ "unchecked" })
	public String getAuthForPolicytest() {

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, props.get("policytest_password"));
		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + props.get("policytest_userName"));
		JSONObject actualInternalrequest = getRequestJson(authInternalRequest);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualInternalrequest.put(GlobalConstants.REQUEST, request);
		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualInternalrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings("unchecked")
	public String getAuthForResident() {
		JSONObject actualrequest = getRequestJson(authRequest);
		logger.info("actualrequest " + actualrequest);
		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getResidentAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getResidentClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getResidentClientSecret());
		logger.info("request for  Resident: " + request);
		logger.info("request for  Resident " + request);
		actualrequest.put(GlobalConstants.REQUEST, request);
		logger.info(GlobalConstants.ACTU_AUTH_REQUESTFOR_RESIDENT + actualrequest);
		logger.info(GlobalConstants.ACTU_AUTH_REQUESTFOR_RESIDENT + actualrequest);
		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForMobile() {
		JSONObject actualrequest = getRequestJson(authRequest);
		logger.info("actualrequest " + actualrequest);
		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getMPartnerMobileClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getMPartnerMobileClientSecret());
		logger.info("request for  Resident: " + request);
		logger.info("request for  Resident " + request);
		actualrequest.put(GlobalConstants.REQUEST, request);
		logger.info(GlobalConstants.ACTU_AUTH_REQUESTFOR_RESIDENT + actualrequest);
		logger.info(GlobalConstants.ACTU_AUTH_REQUESTFOR_RESIDENT + actualrequest);
		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForNewResidentKc() {

		JSONObject actualrequest = getRequestJson(authInternalRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getResidentAppId());
		request.put(GlobalConstants.PASSWORD, props.get("new_Resident_Password"));
		request.put(GlobalConstants.USER_NAME, BaseTestCase.currentModule + "-" + props.get("new_Resident_User"));
		request.put(GlobalConstants.CLIENTID, ConfigManager.getResidentClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getResidentClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(authenticationInternalEndpoint, actualrequest);
		String responseBody = reponse.getBody().asString();
		return new org.json.JSONObject(responseBody).getJSONObject(dataKey).getString(GlobalConstants.TOKEN);
	}

	@SuppressWarnings("unchecked")
	public String getAuthForKeyCloak() {

		Response response = RestAssured.given().with().auth().preemptive()
				.basic(props.get("keycloak_username"), props.get("keycloak_password"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.formParam("grant_type", props.get("keycloak_granttype"))
				.formParam("client_id", props.get("keycloak_clientid"))
				.formParam(GlobalConstants.USERNAME, props.get("keycloak_username"))
				.formParam(GlobalConstants.PASSWORD, props.get("keycloak_password")).when()
				.post(ApplnURIForKeyCloak + props.get("keycloakAuthURL"));
		logger.info(response.getBody().asString());

		String responseBody = response.getBody().asString();
		String token = new org.json.JSONObject(responseBody).getString(GlobalConstants.ACCESSTOKEN);
		logger.info(token);
		return token;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForHotlist() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getHotListAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getHotListClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getHotListClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForIndividual() {
		JSONObject actualRequest_generation = getRequestJson("kernel/Authorization/OtpGeneration/request.json");
		((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).get("userId").toString();
		JSONObject actualRequest_validation = getRequestJson("kernel/Authorization/OtpGeneration/request.json");
		appl.postWithJson(sendOtp, actualRequest_generation);
		String otp = null;
		if (proxy)
			otp = "111111";
		else {
		}
		((JSONObject) actualRequest_validation.get(GlobalConstants.REQUEST)).put("otp", otp);
		Response otpValidate = appl.postWithJson(useridOTP, actualRequest_validation);
		cookie = otpValidate.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getPreRegToken() {
		JSONObject actualRequest_generation = getRequestJson("config/prereg_SendOtp.json");
		actualRequest_generation.put(GlobalConstants.REQUESTTIME, clib.getCurrentUTCTime());
		((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).put("langCode",
				BaseTestCase.getLanguageList().get(0));
		((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).get("userId").toString();
		String userId = ((JSONObject) actualRequest_generation.get(GlobalConstants.REQUEST)).get("userId").toString();
		JSONObject actualRequest_validation = getRequestJson("config/prereg_ValidateOtp.json");
		appl.postWithJson(preregSendOtp, actualRequest_generation);
		String otp = null;
		if (ConfigManager.getUsePreConfiguredOtp)
			//TODO REMOVE THE HARDCODING
			otp = "111111";
		else {
			otp = MockSMTPListener.getOtp(userId);
		}
		((JSONObject) actualRequest_validation.get(GlobalConstants.REQUEST)).put("otp", otp);
		actualRequest_validation.put(GlobalConstants.REQUESTTIME, clib.getCurrentUTCTime());
		Response otpValidate = appl.postWithJson(preregValidateOtp, actualRequest_validation);
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

		JSONObject actualrequest = getRequestJson(authRequest);
		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getRegprocAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getRegprocClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getRegprocClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		logger.info("Regproc Cookie is:: " + cookie);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegProc() {

		JSONObject actualrequest = getRequestJson(authRequest);
		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getRegprocessorAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getRegprocessorClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getRegprocessorClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		logger.info("Regproc Cookie is:: " + cookie);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForIDA() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getResidentAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getResidentClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getResidentClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForIDREPO() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getidRepoAppId());
		request.put(GlobalConstants.CLIENTID, ConfigManager.getidRepoClientId());
		request.put(GlobalConstants.SECRETKEY, ConfigManager.getIdRepoClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(props.get(GlobalConstants.AUTH_CLIENT_IDSECRET_KEYURL), actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationAdmin() {
		JSONObject actualrequest = getRequestJson(authRequest);

		JSONObject request = new JSONObject();
		request.put(GlobalConstants.APPID, registrationAdmin_appid);
		request.put(GlobalConstants.PASSWORD, registrationAdmin_password);
		request.put(GlobalConstants.USER_NAME, registrationAdmin_userName);
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
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

		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
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

		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
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

		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
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

		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
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
		Response reponse = appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie = reponse.getCookie(GlobalConstants.AUTHORIZATION);
		return cookie;
	}

}
