package io.mosip.authentication.fw.util;

import javax.ws.rs.core.MediaType;  

import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.util.JWSValidation;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
 
/**
 * The class to perform or construct biometric identity data which involves
 * encryption, hash, session key generation,signature generation
 * 
 * @author Vignesh
 *
 */

public class BiometricDataUtility extends AuthTestsUtil {
	
	private static final Logger logger = Logger.getLogger(BiometricDataUtility.class);

	private static String cryptoEncryptUrl = RunConfigUtil.objRunConfig.getEndPointUrl()
			+ RunConfigUtil.objRunConfig.getCryptomanagerEncrypt();

	private static String encryptIsoBioValue(String isoBiovalue, String timestamp,String bioValueEncryptionTemplateJson, String transactionId) {
		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, 12);
		String salt = CryptoUtil.encodeBase64(saltLastBytes);
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, 16);
		String aad = CryptoUtil.encodeBase64(aadLastBytes);
		String jsonContent = FileUtil.readInput(bioValueEncryptionTemplateJson);
		/*
		 * String aad =
		 * EncryptDecrptUtil.getBase64EncodedString(timestamp.substring(timestamp.length
		 * () - 16)); String salt =
		 * EncryptDecrptUtil.getBase64EncodedString(timestamp.substring(timestamp.length
		 * () - 12));
		 */
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, aad, "request.aad");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, salt, "request.salt");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, isoBiovalue, "request.data");
		//jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				//IdaKeywordUtil.generateTimeStampWithZTimeZone(), "request.timeStamp");
		//jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				//IdaKeywordUtil.generateTimeStampWithZTimeZone(), "requesttime");
		String cookieValue = getAuthorizationCookie(getCookieRequestFilePathForInternalAuth(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
		String content = RestClient.postRequestWithCookie(cryptoEncryptUrl, jsonContent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, AUTHORIZATHION_COOKIENAME, cookieValue).asString();
		String data = JsonPrecondtion.getValueFromJson(content, "response.data");
		return EncryptDecrptUtil.splitEncryptedData(data);
	}

	private static String getHash(String content) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(content.getBytes()));
	}

	public static String constractBioIdentityRequest(String identityRequest, String bioValueencryptionTemplateJson,String testcaseName,
			boolean isInternal) {
		int count = getNumberOfTimeWordPresentInString(identityRequest, "\"data\"");
		String previousHash = getHash("");
		for (int i = 0; i < count; i++) {
			String biometricsMapper = "identityRequest.(biometrics)[" + i + "]";
			String digitalId = JsonPrecondtion.getJsonValueFromJson(identityRequest,
					biometricsMapper + ".data.digitalId");
			digitalId = getSignedData(digitalId);
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, digitalId,
					biometricsMapper + ".data.digitalId");
			String data = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String bioValue = JsonPrecondtion.getValueFromJson(data, "bioValue");
			String timestamp = JsonPrecondtion.getValueFromJson(data, "timestamp");
			String transactionId = JsonPrecondtion.getValueFromJson(data, "transactionId");
			String encryptedContent = encryptIsoBioValue(bioValue, timestamp, bioValueencryptionTemplateJson, transactionId);
			String encryptedBioValue = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedData");
			String encryptedSessionKey = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedSessionKey");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedBioValue,
					biometricsMapper + ".data.bioValue");
			String latestData = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String signedData = "";
			if (isInternal == false)
				signedData = getSignedData(latestData);
			else if (isInternal == true)
				signedData = EncryptDecrptUtil.getBase64EncodedString(latestData);
			// String signedData = EncryptDecrptUtil.getBase64EncodedString(latestData);
			if(testcaseName.toLowerCase().contains("without".toLowerCase()) && testcaseName.toLowerCase().contains("signature".toLowerCase()) && testcaseName.toLowerCase().contains("_neg".toLowerCase()))
				signedData=signedData.split(Pattern.quote("."))[1];
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, signedData,
					biometricsMapper + ".data");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedSessionKey,
					biometricsMapper + ".sessionKey");
			String hash = getHash(previousHash + getHash(latestData));
			previousHash = hash;
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, hash,
					biometricsMapper + ".hash");

		}
		return identityRequest;
	}
	
	private static String getSignedData(String identityDataBlock) {
		try {
			// Extract Certificate
			String resourcePath = RunConfigUtil.getResourcePath();
			FileInputStream pkeyfis = new FileInputStream(resourcePath + "ida/TestData/RunConfig/keys/privateKey.pem");
			String pKey = FileUtil.getFileContent(pkeyfis, "UTF-8");
			FileInputStream certfis = new FileInputStream(resourcePath + "ida/TestData/RunConfig/keys/cert.pem");
			String cert = FileUtil.getFileContent(certfis, "UTF-8");
			pKey = pKey.replaceAll("-----BEGIN (.*)-----\n", "");
			pKey = pKey.replaceAll("-----END (.*)----\n", "");
			pKey = pKey.replaceAll("\\s", "");
			cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
			cert = cert.replaceAll("-----END (.*)----\n", "");
			cert = cert.replaceAll("\\s", "");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
			// Extract Private Key
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
			JWSValidation jws = new JWSValidation();
			return jws.jwsSign(identityDataBlock, privateKey, certificate);
		} catch (Exception e) {
			logger.error("Exception Occured in signing the bio data:" + e.getStackTrace());
			return "Automation error occured: "+e.getMessage();
		}
	}
	
	public static Map<String,Map<String,String>> allDeviceParam = new HashMap<String,Map<String,String>>();

	public static void storeDeviceDetail(String id) {
		if (!allDeviceParam.containsKey(id)) {
			String deviceParams[] = id.split(":");
			Map<String, String> deviceParam = getDataFromRegisteredDeviceMaster(deviceParams[0],deviceParams[1]);
			deviceParam.putAll(getDataFromMosipDeviceService(deviceParams[0],deviceParams[2]));
			allDeviceParam.put(id, deviceParam);
		}
	}
	
	private static Map<String,String> getDataFromRegisteredDeviceMaster(String id,String deviceId) {
		String query = "select * from master.registered_device_master where provider_id='" + id + "'"+" and device_id='"+deviceId+"'";
		return DbConnection.getDataForQuery(query, "MASTER");
	}
	
	private static Map<String,String> getDataFromMosipDeviceService(String id,String model) {
		String query = "select * from master.mosip_device_service where dprovider_id='" + id + "'"+" and model='"+model+"'";
		return DbConnection.getDataForQuery(query, "MASTER");
	}
	
	
}
