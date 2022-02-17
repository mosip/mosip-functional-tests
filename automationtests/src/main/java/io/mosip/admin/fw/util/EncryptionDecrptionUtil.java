package io.mosip.admin.fw.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.restassured.response.Response;

/**
 * Perform encryption and decryption activity using local executable demoApp jar. 
 * 
 * Dependency: Run demo app application in locally
 * 
 * @author Vignesh
 * @author Ravi Kant
 *
 */
public class EncryptionDecrptionUtil extends AdminTestUtil{
	private static final Logger lOGGER = Logger.getLogger(EncryptionDecrptionUtil.class);
	private static String key="encryptedSessionKey";
	private static String data="encryptedIdentity";
	private static String hmac="requestHMAC";
	static String EncryptUtilBaseUrl = null;
	public static String partnerThumbPrint =null;
	public static String internalThumbPrint =null;
	public static String idaFirThumbPrint =null;
	
	static {
		if(EncryptUtilBaseUrl==null)
			EncryptUtilBaseUrl = getEncryptUtilBaseUrl();
		getThumbprints();
	}
	public static void getThumbprints() {
		String appId = props.getProperty("appIdForCertificate");
		partnerThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, props.getProperty("partnerrefId")));
		internalThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, props.getProperty("internalrefId")));
		idaFirThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, props.getProperty("idaFirRefId")));	
	}
	
	public static String getEncryptUtilBaseUrl() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			return "http://"+inetAddress.getHostName().toLowerCase()+":"+props.getProperty("encryptUtilPort")+"/";
			
		} catch (Exception e) {
			lOGGER.error("Execption in RunConfig " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public Map<String, String> getEncryptSessionKeyValue(String jsonString) {
		Map<String, String> ecryptData = new HashMap<String, String>();
		try {
			String json = getEncryption(jsonString);
			JSONObject jsonobj = new JSONObject(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(json) + "</pre>");
			ecryptData.put("key", jsonobj.get(key).toString());
			ecryptData.put("data", jsonobj.get(data).toString());
			ecryptData.put("hmac", jsonobj.get(hmac).toString());
			ecryptData.put("thumbprint", partnerThumbPrint);
			return ecryptData;
		} catch (Exception e) {
			lOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * The method get internal auth encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public Map<String, String> getInternalEncryptSessionKeyValue(String jsonString) {
		Map<String, String> ecryptData = new HashMap<String, String>();
		try {
			String json = getIntenalEncryption(jsonString);
			JSONObject jsonobj = new JSONObject(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(json) + "</pre>");
			ecryptData.put("key", jsonobj.get(key).toString());
			ecryptData.put("data", jsonobj.get(data).toString());
			ecryptData.put("hmac", jsonobj.get(hmac).toString());
			ecryptData.put("thumbprint", internalThumbPrint);
			return ecryptData;
		} catch (Exception e) {
			lOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return String , Ecrypted JSON
	 */
	private String getEncryption(String jsonString) {
		try {
			JSONObject objectData = new JSONObject(jsonString);
			Reporter.log("<b><u> Identity request:</u></b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(objectData.toString())+"</pre>");
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("encryptionPath"), objectData.toString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return String , Ecrypted JSON
	 */
	private String getIntenalEncryption(String jsonString) {
		try {
			JSONObject objectData = new JSONObject(jsonString);
			Reporter.log("<b><u> Identity request:</u></b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(objectData.toString())+"</pre>");
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("internalEncryptionPath"), objectData.toString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get encoded data from json content in file
	 * 
	 * @param filename
	 * @return String, Encoded data
	 */
	public String getEncode(String jsonString) {
		try {
			JSONObject objectData = new JSONObject(jsonString);
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("encodePath"), objectData.toString(), MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method will get encoded data from cbeff file
	 * 
	 * @param filename
	 * @return String, Encoded data
	 */
	public String getCbeffEncode(String filename) {
		try {
			String objectData = FileUtil.readInput(filename);
			objectData=objectData.replaceAll(" xmlns=\"\"", "");
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("encodePath"), objectData, MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method get decoded content in file
	 * 
	 * @param content, String to decode
	 * @return String, decoded content
	 */
	public String getDecodeFile(String content) {
		try {
			return RestClient.postRequest(EncryptUtilBaseUrl + props.get("decodeFilePath"), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get encoded data from file
	 * 
	 * @param file, file to be encoded
	 * @return String, encoded data
	 */
	public String getEncodeFile(File file) {
		try {
			return RestClient.postRequest(EncryptUtilBaseUrl + props.get("encodeFilePath"), file,
					MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decoded data from file
	 * 
	 * @param filename, file to to be decoded
	 * @return String, decoded data
	 */
	public String getDecodeFromFile(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return RestClient.postRequest(EncryptUtilBaseUrl + props.get("decodePath"),
					objectData.toString(), MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decoded data from string
	 * 
	 * @param content, String to be decoded
	 * @return String, decoded data
	 */
	public String getDecodeFromStr(String content) {
		try {
			return RestClient.postRequest(EncryptUtilBaseUrl + props.get("decodePath"), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method get decrypt data from file
	 * 
	 * @param filename, file to to be decoded
	 * @return String, decoded data
	 */
	public String getDecryptFromFile(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return RestClient.postRequest(EncryptUtilBaseUrl + props.get("decryptPath"),
					objectData.toString(), MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decrypt data from string
	 * 
	 * @param content, String to be decoded
	 * @return String, decoded data
	 */
	public String getDecyptFromStr(String content, String referenceId, boolean isInternal) {
		try {
			HashMap<String, Object> queryParams = new HashMap<String, Object>();
			// this partner is created in pmpdatamanager class, partner certificate is uploaded for this partner.
			queryParams.put("refId", referenceId);
			queryParams.put("isInternal", isInternal);
			return RestClient.postRequestWithQueryParamsAndBody(EncryptUtilBaseUrl + props.get("decryptPath"), content, queryParams, 
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method will get encoded data from json content in file
	 * 
	 * @param content to be encode
	 * @return String, Encoded data
	 */
	public static String getBase64EncodedString(String content) {
		try {
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("encodePath"), content.toString(), MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	public static String splitEncryptedData(String content)
	{
		try {
			return RestClient.postRequest(EncryptUtilBaseUrl+props.get("splitEncryptedData"), content.toString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
			
		} catch (Exception e) {
			lOGGER.error("Exception: " + e);
			return e.toString();
		}
		
	}
	
	public static String getCertificateThumbprint(Certificate cert){
		try {
			//return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(DigestUtils.sha256(cert.getEncoded()));
			return toHex(DigestUtils.sha256(cert.getEncoded()));
		} catch (CertificateEncodingException e) {
			lOGGER.error("Exception in generate thumbrint: "+e.getMessage());
			return e.getMessage();
		}
	}
	
	public static String toHex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }
	public static Certificate getIdaCertificate(String applicationId, String referenceId) {
		String cert = null;
		String token = kernelAuthLib.getTokenByRole("regproc");
		String url = ApplnURI + props.getProperty("getIdaCertificateUrl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("applicationId", applicationId);
		map.put("referenceId", referenceId);
		lOGGER.info("Getting certificate for "+referenceId);
		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		JSONObject responseJson = new JSONObject(response.asString());
		if(!responseJson.get("response").toString().equals("null"))
		{
			JSONObject certiResponseJson = new JSONObject(responseJson.get("response").toString());
			cert = certiResponseJson.get("certificate").toString();
		}
		else {
			lOGGER.error("Not able to Fetch Certficate from: "+url);
			return null;
		}
		
		
		cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
		cert = cert.replaceAll("-----END (.*)----\n", "");
		cert = cert.replaceAll("\\s", "");
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
			return certificate;
		} catch (CertificateException e) {
			lOGGER.error("Exception in generate Certficate for generating thumbprint: "+e.getMessage());
			return null;
		}
		
	}
	public static Certificate getPartnerCertificate(String partnerId) {
		String cert = null;
		String token = kernelAuthLib.getTokenByRole("regproc");
		String url = ApplnURI + props.getProperty("getPartnerCertificateUrl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("partnerId", partnerId);
		lOGGER.info("Getting certificate for partner "+partnerId);
		Response response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		JSONObject responseJson = new JSONObject(response.asString());
		if(!responseJson.get("response").toString().equals("null"))
		{
			JSONObject certiResponseJson = new JSONObject(responseJson.get("response").toString());
			cert = certiResponseJson.get("certificateData").toString();
		}
		else {
			lOGGER.error("Not able to Fetch Certficate from: "+url);
			return null;
		}
		
		
		cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
		cert = cert.replaceAll("-----END (.*)----\n", "");
		cert = cert.replaceAll("\\s", "");
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
			return certificate;
		} catch (CertificateException e) {
			lOGGER.error("Exception in generate Certficate for generating thumbprint: "+e.getMessage());
			return null;
		}
		
	}
	
	public boolean validateThumbPrint( String thumbPrint, String partnerId) {
		String expectedThumbPrint = getCertificateThumbprint(getPartnerCertificate(partnerId));
		return expectedThumbPrint.equals(thumbPrint);
	}
	
	public boolean validateEkycResponseIdentity(String identity, String partnerId, boolean isInternal) {
		String decryptedKycIdentity = getDecyptFromStr(identity, partnerId, isInternal);
		Reporter.log("<b><u>Decrypted Kyc Response: </u></b>(EndPointUrl: " + EncryptUtilBaseUrl + props.get("decryptPath") + ") <pre>"
				+ ReportUtil.getTextAreaJsonMsgHtml(decryptedKycIdentity) + "</pre>");
		String keysToValidateInKYC[] = props.getProperty("keysToValidateInKYC").split(",");
		JSONObject decryptedKycJson = new JSONObject(decryptedKycIdentity);
		if(decryptedKycJson!=null ) {
		for(String key : keysToValidateInKYC) 
			if(!decryptedKycJson.has(key)) return false;
		}
		else return false;
		return true;
	}
	

	public boolean validateThumbPrintAndIdentity(Response response, String ekycUri) throws AdminTestException {
		String thumbPrint = JsonPrecondtion.getValueFromJson(response.asString(), "response.thumbprint");
		String uriParts[] = ekycUri.split("/");
		String partnerId = uriParts[uriParts.length-2];
		boolean thumprintValid = validateThumbPrint(thumbPrint, partnerId);
			if(!thumprintValid)	throw new AdminTestException("Failed in Thumbprint validation");
			
		String identity = JsonPrecondtion.getValueFromJson(response.asString(), "response.identity");
		boolean ekycResponseValid = validateEkycResponseIdentity(identity, partnerId, false);
			if(!ekycResponseValid)	throw new AdminTestException("Failed in KYC Response validation");
		
		return thumprintValid&&ekycResponseValid;
	}
	
	public boolean verifyResponseUsingDigitalSignature(String resonseContent, String digitalSignature) {
		HashMap<String, String> queryparams = new HashMap<String, String>();
		queryparams.put("signature", digitalSignature);
		String signatureApiPath = EncryptUtilBaseUrl + props.getProperty("validateSignatureUrl");
		Response response = RestClient.postRequestWithQueryParamAndBody(signatureApiPath, resonseContent,queryparams, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
		if (response.asString().contains("success"))
			return true;
		else
			return false;
	}
}
