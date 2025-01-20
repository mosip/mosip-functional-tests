package io.mosip.testrig.apirig.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dto.EncryptionResponseDto;
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
	private static ObjectMapper objMapper = new ObjectMapper();
	private static CryptoUtil cryptoUtil = new CryptoUtil();
	private static KeyMgrUtil keymgrUtil = new KeyMgrUtil();
	
	static {
		getThumbprints();
	}
	public static void getThumbprints() {
		String appId = properties.getProperty("appIdForCertificate");
		try {
			partnerThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, properties.getProperty("partnerrefId")));
			internalThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, properties.getProperty("internalrefId")));
			idaFirThumbPrint = getCertificateThumbprint(getIdaCertificate(appId, properties.getProperty("idaFirRefId")));
		} catch (Exception e) {
			lOGGER.error(e.getMessage());
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public Map<String, String> getEncryptSessionKeyValue(String jsonString) {
		Map<String, String> ecryptData = new HashMap<>();
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		try {
			encryptionResponseDto = encrypt(jsonString);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			GlobalMethods.reportRequest(null, encryptionResponseDto.toString());
			ecryptData.put("key", encryptionResponseDto.getEncryptedSessionKey());
			ecryptData.put("data", encryptionResponseDto.getEncryptedIdentity());
			ecryptData.put("hmac", encryptionResponseDto.getRequestHMAC());
			ecryptData.put("thumbprint", partnerThumbPrint);
			return ecryptData;
		} catch (Exception e) {
			lOGGER.error(e);
			return Collections.emptyMap();
		}
	}
	
	/**
	 * The method get internal auth encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public Map<String, String> getInternalEncryptSessionKeyValue(String jsonString) {
		Map<String, String> ecryptData = new HashMap<>();
		try {
			String json = getIntenalEncryption(jsonString);
			JSONObject jsonobj = new JSONObject(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			GlobalMethods.reportRequest(null, json);
			ecryptData.put("key", jsonobj.get(key).toString());
			ecryptData.put("data", jsonobj.get(data).toString());
			ecryptData.put("hmac", jsonobj.get(hmac).toString());
			ecryptData.put("thumbprint", internalThumbPrint);
			return ecryptData;
		} catch (Exception e) {
			lOGGER.error(e);
			return Collections.emptyMap();
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return String , Ecrypted JSON
	 * 
	 */
	
	
	public static EncryptionResponseDto encrypt(String jsonString) throws Exception {
        
		String refId= null;
		boolean isInternal = false;
		boolean isBiometrics = true;
		
		if (refId == null) {
            refId = getRefId(isInternal, isBiometrics);
        }
        return kernelEncrypt(jsonString, refId);
    }
	
	private static String getRefId(boolean isInternal, boolean isBiometrics) {
        String refId;
        if (isBiometrics) {
            if (isInternal) {
                refId = props.getProperty("internal.biometric.reference.id");
            } else {
                refId = props.getProperty("internal.biometric.reference.id");
            }
        } else {
            if (isInternal) {
                refId = props.getProperty("internal.reference.id");
            } else {
                refId = props.getProperty("partner.reference.id");
            }
        }
        return refId;
    }
	
	private static EncryptionResponseDto kernelEncrypt(String identityBlock, String refId) throws Exception {
//        String identityBlock = objMapper.writeValueAsString(jsonString);
        SecretKey secretKey = cryptoUtil.genSecKey();
        EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
        
        lOGGER.info("Strated encrypting the Identity block");
        byte[] encryptedIdentityBlock = cryptoUtil.symmetricEncrypt(identityBlock.getBytes(StandardCharsets.UTF_8), secretKey);
        encryptionResponseDto.setEncryptedIdentity(Base64.getUrlEncoder().encodeToString(encryptedIdentityBlock));
        
        //ToDO: Cache it
        X509Certificate x509Cert = keymgrUtil.getCertificate(refId);
        
        
        PublicKey publicKey = x509Cert.getPublicKey();
        
        byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt(secretKey.getEncoded(), publicKey);
        encryptionResponseDto.setEncryptedSessionKey(Base64.getUrlEncoder().encodeToString(encryptedSessionKeyByte));
        
        byte[] byteArr = cryptoUtil.symmetricEncrypt(
            digestAsPlainText(HMACUtils2.generateHash(identityBlock.getBytes(StandardCharsets.UTF_8))).getBytes(), secretKey);
        encryptionResponseDto.setRequestHMAC(Base64.getUrlEncoder().encodeToString(byteArr));
        
        lOGGER.info("encryptionResponseDto is " + encryptionResponseDto.toString());
        
        return encryptionResponseDto;
    }
	
	public static String digestAsPlainText(byte[] data) {
		return DatatypeConverter.printHexBinary(data).toUpperCase();
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
			GlobalMethods.reportRequest(null, objectData.toString());
			return RestClient.postRequest(EncryptUtilBaseUrl+properties.get("internalEncryptionPath"), objectData.toString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			lOGGER.error(GlobalConstants.EXCEPTION+ e);
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
			return RestClient.postRequest(EncryptUtilBaseUrl+properties.get(GlobalConstants.ENCODEPATH), content, MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			lOGGER.error(GlobalConstants.EXCEPTION + e);
			return e.toString();
		}
	}
	
	public static String getCertificateThumbprint(Certificate cert){
		try {
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
		
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
		String url = ApplnURI + properties.getProperty("getIdaCertificateUrl");
		HashMap<String, String> map = new HashMap<>();
		map.put("applicationId", applicationId);
		map.put("referenceId", referenceId);
		lOGGER.info("Getting certificate for "+referenceId);
		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		JSONObject responseJson = new JSONObject(response.asString());
		if(!responseJson.get(GlobalConstants.RESPONSE).toString().equals("null"))
		{
			JSONObject certiResponseJson = new JSONObject(responseJson.get(GlobalConstants.RESPONSE).toString());
			cert = certiResponseJson.get("certificate").toString();
		}
		else {
			lOGGER.error("Not able to Fetch Certficate from: "+url);
			return null;
		}
		
		
		cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
		cert = cert.replaceAll("-----END (.*)----\n", "");
		cert = cert.replaceAll("\\s", "");
		try (ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(cert))) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return(X509Certificate) cf
					.generateCertificate(stream);
			
		} catch (CertificateException | IOException e) {
			lOGGER.error("Exception in generate Certficate for generating thumbprint: "+e.getMessage());
			return null;
		}
		
	}
	public static Certificate getPartnerCertificate(String partnerId) {
		String cert = null;
		String token = kernelAuthLib.getTokenByRole("regproc");
		String url = ApplnURI + properties.getProperty("getPartnerCertificateUrl");
		HashMap<String, String> map = new HashMap<>();
		map.put(GlobalConstants.PARTNERID, partnerId);
		lOGGER.info("Getting certificate for partner "+partnerId);
		Response response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		JSONObject responseJson = new JSONObject(response.asString());
		if(!responseJson.get(GlobalConstants.RESPONSE).toString().equals("null"))
		{
			JSONObject certiResponseJson = new JSONObject(responseJson.get(GlobalConstants.RESPONSE).toString());
			cert = certiResponseJson.get("certificateData").toString();
		}
		else {
			lOGGER.error("Not able to Fetch Certficate from: "+url);
			return null;
		}
		
		
		cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
		cert = cert.replaceAll("-----END (.*)----\n", "");
		cert = cert.replaceAll("\\s", "");
		try (ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(cert))) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return(X509Certificate) cf
					.generateCertificate(stream);
			
		} catch (CertificateException | IOException e) {
			lOGGER.error("Exception in generate Certficate for generating thumbprint: "+e.getMessage());
			return null;
		}
		
	}
	
	public boolean validateThumbPrint( String thumbPrint, String partnerId) {
		String expectedThumbPrint = "";
		try {
			expectedThumbPrint = getCertificateThumbprint(getPartnerCertificate(partnerId));
		} catch (Exception e) {
			lOGGER.error(e.getMessage());
		}
		return expectedThumbPrint.equals(thumbPrint);
	}
}
