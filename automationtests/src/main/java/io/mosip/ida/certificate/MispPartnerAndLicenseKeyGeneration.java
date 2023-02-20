package io.mosip.ida.certificate;

import java.net.InetAddress;
import java.security.cert.Certificate;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.kernel.util.ConfigManager;
import io.restassured.response.Response;

public class MispPartnerAndLicenseKeyGeneration extends AdminTestUtil{
	private static final Logger lOGGER = Logger.getLogger(MispPartnerAndLicenseKeyGeneration.class);
	static String localHostUrl = null;
	
	static String address = "Bangalore";
	static String contactNumber = "8553967572";
	static String emailId = "mosip"+timeStamp+"@gmail.com";
	public static String mispOrganizationName = "mosip-" + timeStamp;
	public static String mispPartnerId = mispOrganizationName;
	public static String mispPartnerType = "Misp_Partner";
	static String getPartnerType = "MISP";
	
	public static String getAndUploadCertificatesAndGenerateMispLicKey() {
		if (localHostUrl == null) {
			localHostUrl = getLocalHostUrl();
		}
		
		mispPartnerGeneration();
		JSONObject certificateValue = getCertificates(mispPartnerId, getPartnerType);
		String mispCACertValue = certificateValue.getString("caCertificate");
		System.out.println(mispCACertValue);
		String mispInterCertValue = certificateValue.getString("interCertificate");
		System.out.println(mispInterCertValue);
		String mispPartnerCertValue = certificateValue.getString("partnerCertificate");
		System.out.println(mispPartnerCertValue);
		
		
		uploadCACertificate(mispCACertValue, "Auth");
		uploadIntermediateCertificate(mispInterCertValue, "Auth");
		
		JSONObject mispSignedcertificateValue = uploadPartnerCertificate(mispPartnerCertValue, "Auth", mispPartnerId);
		
		String mispCertValueSigned = mispSignedcertificateValue.getString("signedCertificateData");
		System.out.println(mispCertValueSigned);
		uploadSignedCertificate(mispCertValueSigned, getPartnerType);
		String mispLicKey = generateMispLicKey(mispPartnerId);
		System.out.println(mispLicKey);
		
		return mispLicKey;
	}
	
	private static String getLocalHostUrl() {
			return ConfigManager.getAuthDemoServiceUrl() + "/";
	}

	public static void mispPartnerGeneration() { 
		String url = ApplnURI + "/v1/partnermanager/partners";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("address", address);
		requestBody.put("contactNumber", contactNumber);
		requestBody.put("emailId", emailId);
		requestBody.put("organizationName", mispOrganizationName);
		requestBody.put("partnerId", mispPartnerId);
		requestBody.put("partnerType", mispPartnerType);
		requestBody.put("policyGroup", policyGroup);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
		body.put("id", "string");
		body.put("metadata", new HashMap<>());
		body.put("request", requestBody);
		body.put("requesttime", generateCurrentUTCTimeStamp());
		body.put("version", "string");
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		System.out.println(response);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		System.out.println(responseValue);
	}
	
	public static JSONObject getCertificates(String partnerId, String partnerType) {
		String url = localHostUrl + "v1/identity/generatePartnerKeys";
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("partnerName", partnerId);
		map.put("partnerType", partnerType);
//		map.put("keyFileNameByPartnerName", "true");
		
//		String token = kernelAuthLib.getTokenByRole("partner");
		
		Response response = RestClient.getRequestWithQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
		System.out.println(response);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println(responseJson);
		
		return responseJson;
	}
	
	public static void uploadCACertificate(String certValueCA, String partnerDomain) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/ca/upload";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("certificateData", certValueCA);
		requestBody.put("partnerDomain", partnerDomain);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
		body.put("id", "string");
		body.put("metadata", new HashMap<>());
		body.put("request", requestBody);
		body.put("requesttime", generateCurrentUTCTimeStamp());
		body.put("version", "string");
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		
		JSONObject reponseValue = new JSONObject(response.asString());
		System.out.println(reponseValue);
	}
	
	public static void uploadIntermediateCertificate(String certValueIntermediate, String partnerDomain) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/ca/upload";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("certificateData", certValueIntermediate);
		requestBody.put("partnerDomain", partnerDomain);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
		body.put("id", "string");
		body.put("metadata", new HashMap<>());
		body.put("request", requestBody);
		body.put("requesttime", generateCurrentUTCTimeStamp());
		body.put("version", "string");
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		
		JSONObject reponseValue = new JSONObject(response.asString());
		System.out.println(reponseValue);
	}
	
	public static JSONObject uploadPartnerCertificate(String certValuePartner, String partnerDomain, String partnerId) {
		String url = ApplnURI + "/v1/partnermanager/partners/certificate/upload";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("certificateData", certValuePartner);
		requestBody.put("partnerDomain", partnerDomain);
		requestBody.put("partnerId", partnerId);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
		body.put("id", "string");
		body.put("metadata", new HashMap<>());
		body.put("request", requestBody);
		body.put("requesttime", generateCurrentUTCTimeStamp());
		body.put("version", "string");
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		System.out.println(response);
		
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println(responseJson);
		
		JSONObject responseValue = (JSONObject) responseJson.get("response");
		System.out.println(responseValue);
		
		return responseValue;
	}
	
	public static void uploadSignedCertificate(String certValueSigned, String partnerType) {
		String url = localHostUrl + props.getProperty("uploadSignedCertificateUrl");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("certData", certValueSigned);
		
		HashMap<String, Object> queryParamMap = new HashMap<String, Object>();
		
		queryParamMap.put("partnerType", partnerType);
		
		Response response = RestClient.postRequestWithQueryParamsAndBody(url, requestBody, queryParamMap, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		
		System.out.println(response);
	}
	
	public static String generateMispLicKey(String partnerId) {
		String url = ApplnURI + "/v1/partnermanager/misps";
		
		String token = kernelAuthLib.getTokenByRole("partner");
		
		HashMap<String, String> requestBody = new HashMap<String, String>();
		
		requestBody.put("providerId", partnerId);
		
		HashMap<String, Object> body = new HashMap<String, Object>();
		
		body.put("id", "string");
		body.put("metadata", new HashMap<>());
		body.put("request", requestBody);
		body.put("requesttime", generateCurrentUTCTimeStamp());
		body.put("version", "string");
		
		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		
		System.out.println(response);
		JSONObject responseJson = new JSONObject(response.asString());
		System.out.println(responseJson);
		JSONObject responseValue = (JSONObject) (responseJson.get("response"));
		System.out.println(responseValue);
		String licenseKey = responseValue.getString("licenseKey");
		System.out.println(licenseKey);
		
		return licenseKey;
	}
	
}

	
