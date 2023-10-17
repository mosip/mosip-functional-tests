package io.mosip.testrig.apirig.ida.certificate;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.restassured.response.Response;

public class PartnerRegistration extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(PartnerRegistration.class);
	public static String partnerKeyUrl = null;
	public static String ekycPartnerKeyUrl = null;
	static String localHostUrl = null;

	static String address = "Bangalore";
	static String contactNumber = "8553967572";
	static String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
	static String emailId = "mosip_1" + timeStamp + "@gmail.com";
	static String emailId2 = "mosip_2" + timeStamp + "@gmail.com";
	static String emailId3 = "mosip_3" + timeStamp + "@gmail.com";
	static String emailIdForKyc = "mosip_4" + timeStamp + "@gmail.com";
	public static String organizationName = "mosip_partnerorg" + timeStamp;
	public static String ekycOrganizationName = "mosip_ekyc_partner" + timeStamp;
	public static String deviceOrganizationName = "mosip_deviceorg" + timeStamp;
	public static String ftmOrganizationName = "mosip_ftmorg" + timeStamp;
	public static String partnerId = organizationName;
	public static String ekycPartnerId = ekycOrganizationName;
	public static String partnerType = "AUTH_PARTNER";
	static String getPartnerType = "RELYING_PARTY";
	static String getEkycPartnerType = "AUTH_PARTNER";
	static String getEkycPartnerTypeForCert = "EKYC";
	public static String apiKey = "";
	public static String kycApiKey = "";
	public static String mispLicKey ="";
	public static String policyGroup = AdminTestUtil.policyGroup;
	public static String policyGroupForKyc = AdminTestUtil.policyGroup2;
	
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			lOGGER.setLevel(Level.ALL);
		else
			lOGGER.setLevel(Level.ERROR);
	}

	public static String generateAndGetPartnerKeyUrl() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Key manager cert store and IDA cert store
			// So use the predefined certificate folder and partner key
			partnerKeyUrl = ConfigManager.getPartnerUrlSuffix();
			partnerId = getPartnerIdFromPartnerURL(partnerKeyUrl);
			return ConfigManager.getPartnerUrlSuffix();
		}
		ftmGeneration();
		deviceGeneration();

		getAndUploadCertificates();
		apiKey = KeyCloakUserAndAPIKeyGeneration.createKCUserAndGetAPIKey();
		mispLicKey = MispPartnerAndLicenseKeyGeneration.getAndUploadCertificatesAndGenerateMispLicKey();
		
		if (apiKey.isEmpty() || mispLicKey.isEmpty()) {
			lOGGER.error("Failed to generate API key and MISP Lic key");
			return "";
		}
		partnerKeyUrl = mispLicKey + "/" + partnerId + "/" + apiKey;

		lOGGER.info("partnerKeyUrl = " + partnerKeyUrl);

		return partnerKeyUrl;
	}
	
	public static String generateAndGetEkycPartnerKeyUrl() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Key manager cert store and IDA cert store
			// So use the predefined certificate folder and partner key
			ekycPartnerKeyUrl = ConfigManager.getPartnerUrlSuffix();
			ekycPartnerId = getPartnerIdFromPartnerURL(ekycPartnerKeyUrl);
			return ConfigManager.getPartnerUrlSuffix();
		}
		
		/*
		 * ftmGeneration(); deviceGeneration();
		 */
		

		getAndUploadEkycCertificates();
		kycApiKey = KeyCloakUserAndAPIKeyGeneration.createKCUserAndGetAPIKeyForKyc();
		
		if (apiKey.isEmpty() || mispLicKey.isEmpty()) {
			lOGGER.error("Failed to generate API key and MISP Lic key");
			return "";
		}
		ekycPartnerKeyUrl = mispLicKey + "/" + ekycPartnerId + "/" + kycApiKey;

		lOGGER.info("ekycPartnerKeyUrl = " + ekycPartnerKeyUrl);

		return ekycPartnerKeyUrl;
	}

	public static void getAndUploadCertificates() {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}

		partnerGeneration();
		JSONObject certificateValue = getCertificates(partnerId, getPartnerType);
		String caCertValue = certificateValue.getString("caCertificate");
		lOGGER.info(caCertValue);
		String interCertValue = certificateValue.getString("interCertificate");
		lOGGER.info(interCertValue);
		String partnerCertValue = certificateValue.getString("partnerCertificate");
		lOGGER.info(partnerCertValue);

		uploadCACertificate(caCertValue, "Auth");
		uploadIntermediateCertificate(interCertValue, "Auth");

		JSONObject signedcertificateValue = uploadPartnerCertificate(partnerCertValue, "Auth", partnerId);

		String certValueSigned = signedcertificateValue.getString("signedCertificateData");
		lOGGER.info(certValueSigned);
		uploadSignedCertificate(certValueSigned, getPartnerType, partnerId, true);

	}
	
	public static void getAndUploadEkycCertificates() {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}

		partnerKycGeneration();
		JSONObject certificateValue = getCertificates(ekycPartnerId, getEkycPartnerTypeForCert);
		String caCertValue = certificateValue.getString("caCertificate");
		lOGGER.info(caCertValue);
		String interCertValue = certificateValue.getString("interCertificate");
		lOGGER.info(interCertValue);
		String partnerCertValue = certificateValue.getString("partnerCertificate");
		lOGGER.info(partnerCertValue);

		uploadCACertificate(caCertValue, "Auth");
		uploadIntermediateCertificate(interCertValue, "Auth");

		JSONObject signedcertificateValue = uploadPartnerCertificate(partnerCertValue, "Auth", ekycPartnerId);

		String certValueSigned = signedcertificateValue.getString("signedCertificateData");
		lOGGER.info(certValueSigned);
		uploadSignedCertificate(certValueSigned, getEkycPartnerTypeForCert, ekycPartnerId, true);

	}

	private static String getLocalHostUrl() {
		return ConfigManager.getAuthDemoServiceUrl() + "/";
	}

	public static void partnerGeneration() {
		String url = ApplnURI + properties.getProperty("putPartnerRegistrationUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailId);
		requestBody.put("organizationName", organizationName);
		requestBody.put(GlobalConstants.PARTNERID, partnerId);
		requestBody.put("partnerType", partnerType);
		requestBody.put("policyGroup", policyGroup);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, "LTS");

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
	}
	
	public static void partnerKycGeneration() {
		String url = ApplnURI + properties.getProperty("putPartnerRegistrationUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailIdForKyc);
		requestBody.put("organizationName", ekycOrganizationName);
		requestBody.put(GlobalConstants.PARTNERID, ekycPartnerId);
		requestBody.put("partnerType", getEkycPartnerType);
		requestBody.put("policyGroup", policyGroupForKyc);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, "LTS");

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);
	}

	public static JSONObject getCertificates(String partnerId, String partnerType) {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}
		String url = localHostUrl + properties.getProperty("getPartnerCertURL");

		HashMap<String, String> map = new HashMap<>();

		map.put("partnerName", partnerId);
		map.put("partnerType", partnerType);
		map.put("moduleName", BaseTestCase.certsForModule);
		map.put("keyFileNameByPartnerName", GlobalConstants.TRUE_STRING);

		String token = kernelAuthLib.getTokenByRole("partner");

		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);

		return responseJson;
	}

	public static JSONObject getDeviceCertificates(String partnerId, String partnerType) {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}
		String url = localHostUrl + properties.getProperty("getPartnerCertURL");

		HashMap<String, String> map = new HashMap<>();

		map.put("partnerName", partnerId);
		map.put("partnerType", partnerType);
		map.put("moduleName", BaseTestCase.certsForModule);
		if (partnerType.equals("RELYING_PARTY") || partnerType.equals("MISP")) {
			map.put("keyFileNameByPartnerName", GlobalConstants.TRUE_STRING);
		}

		String token = kernelAuthLib.getTokenByRole("partner");

		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);

		return responseJson;
	}

	public static void uploadCACertificate(String certValueCA, String partnerDomain) {
		String url = ApplnURI + properties.getProperty("uploadCACertificateUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("certificateData", certValueCA);
		requestBody.put("partnerDomain", partnerDomain);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		JSONObject reponseValue = new JSONObject(response.asString());
		lOGGER.info(reponseValue);
	}

	public static void uploadIntermediateCertificate(String certValueIntermediate, String partnerDomain) {
		String url = ApplnURI + properties.getProperty("uploadIntermediateCertificateUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("certificateData", certValueIntermediate);
		requestBody.put("partnerDomain", partnerDomain);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		JSONObject reponseValue = new JSONObject(response.asString());
		lOGGER.info(reponseValue);
	}

	public static JSONObject uploadPartnerCertificate(String certValuePartner, String partnerDomain, String partnerId) {
		String url = ApplnURI + properties.getProperty("uploadPartnerCertificateUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("certificateData", certValuePartner);
		requestBody.put("partnerDomain", partnerDomain);
		requestBody.put(GlobalConstants.PARTNERID, partnerId);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);

		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);

		JSONObject responseValue = (JSONObject) responseJson.get("response");
		lOGGER.info(responseValue);

		return responseValue;
	}

	public static void uploadSignedCertificate(String certValueSigned, String partnerType, String partnerId,
			Boolean keyFileNameByPartnerName) {
		String url = localHostUrl + properties.getProperty("uploadSignedCertificateUrl");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("certData", certValueSigned);

		HashMap<String, Object> queryParamMap = new HashMap<>();
		queryParamMap.put("partnerName", partnerId);
		queryParamMap.put("partnerType", partnerType);
		queryParamMap.put("moduleName", BaseTestCase.certsForModule);
		if (partnerType.equals("RELYING_PARTY")) {
			queryParamMap.put("keyFileNameByPartnerName", keyFileNameByPartnerName);
		}
		if (partnerType.equals("EKYC")) {
			queryParamMap.put("keyFileNameByPartnerName", keyFileNameByPartnerName);
		}

		Response response = RestClient.postRequestWithQueryParamsAndBody(url, requestBody, queryParamMap,
				MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);

		lOGGER.info(response);
	}

	public static void deviceGeneration() {
		String url = ApplnURI + properties.getProperty("putPartnerRegistrationUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailId3);
		requestBody.put("organizationName", deviceOrganizationName);
		requestBody.put(GlobalConstants.PARTNERID, deviceOrganizationName);
		requestBody.put("partnerType", "Device_Provider");
		requestBody.put("policyGroup", policyGroup);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, "LTS");

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);

		JSONObject certificateValue = getDeviceCertificates(deviceOrganizationName, "DEVICE");
		String caDeviceCertValue = certificateValue.getString("caCertificate");
		String interDeviceCertValue = certificateValue.getString("interCertificate");
		String partnerDeviceCertValue = certificateValue.getString("partnerCertificate");

		uploadCACertificate(caDeviceCertValue, "DEVICE");
		uploadIntermediateCertificate(interDeviceCertValue, "DEVICE");

		JSONObject signedDevicePartnerCert = uploadPartnerCertificate(partnerDeviceCertValue, "DEVICE",
				deviceOrganizationName);
		String signedDevicePartnerCertf = signedDevicePartnerCert.getString("signedCertificateData");

		uploadSignedCertificate(signedDevicePartnerCertf, "DEVICE", deviceOrganizationName, true);

	}

	public static void ftmGeneration() {
		String url = ApplnURI + properties.getProperty("putPartnerRegistrationUrl");

		String token = kernelAuthLib.getTokenByRole("partner");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailId2);
		requestBody.put("organizationName", ftmOrganizationName);
		requestBody.put(GlobalConstants.PARTNERID, ftmOrganizationName);
		requestBody.put("partnerType", "FTM_Provider");
		requestBody.put("policyGroup", policyGroup);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, "LTS");

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		lOGGER.info(response);
		JSONObject responseJson = new JSONObject(response.asString());
		lOGGER.info(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		lOGGER.info(responseValue);

		JSONObject certificateValue = getDeviceCertificates(ftmOrganizationName, "FTM");
		String caFtmCertValue = certificateValue.getString("caCertificate");
		String interFtmCertValue = certificateValue.getString("interCertificate");
		String partnerFtmCertValue = certificateValue.getString("partnerCertificate");

		uploadCACertificate(caFtmCertValue, "FTM");
		uploadIntermediateCertificate(interFtmCertValue, "FTM");

		JSONObject signedDevicePartnerCert = uploadPartnerCertificate(partnerFtmCertValue, "FTM", ftmOrganizationName);
		String signedDevicePartnerCertf = signedDevicePartnerCert.getString("signedCertificateData");

		uploadSignedCertificate(signedDevicePartnerCertf, "FTM", ftmOrganizationName, true);

	}

	public static void deleteCertificates() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Keymanager cert store and IDA cert store
			// So use the predefined certificate folder and partnerkey
			return ;
		}
		
		
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}
		String url = localHostUrl + properties.getProperty("clearCertificateURL");

		if (url.contains("$MODULENAME$")) {
			url = url.replace("$MODULENAME$", BaseTestCase.certsForModule);
		}

		if (url.contains("$CERTSDIR$")) {
			url = url.replace("$CERTSDIR$", ConfigManager.getauthCertsPath());
		}

		Response response = RestClient.deleteRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
		lOGGER.info(response);

	}

}
