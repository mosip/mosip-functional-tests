package io.mosip.admin.fw.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.BytesUtil;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * The class to perform or construct biometric identity data which involves
 * encryption, hash, session key generation,signature generation
 * 
 * @author Vignesh
 * @author Ravi Kant
 *
 */

public class BioDataUtility extends AdminTestUtil {

	private static final Logger logger = Logger.getLogger(BioDataUtility.class);

	private String cryptoEncryptUrl = BaseTestCase.ApplnURI + "/idauthentication/v1/internal/encrypt";
	static String EncryptUtilBaseUrl = ConfigManager.getAuthDemoServiceUrl() + "/";

	private String encryptIsoBioValue(String isoBiovalue, String timestamp, String bioValueEncryptionTemplateJson,
			String transactionId, boolean isInternal) {
		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, 12);
		String salt = CryptoUtil.encodeBase64(saltLastBytes);
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, 16);
		String aad = CryptoUtil.encodeBase64(aadLastBytes);
		String jsonContent = FileUtil.readInput(bioValueEncryptionTemplateJson);
		if (isInternal) 
			jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, props.getProperty("internalrefId"),
					"request.referenceId");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, aad, "request.aad");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, salt, "request.salt");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,  CryptoUtil.encodeBase64(CryptoUtil.decodeBase64(isoBiovalue)), "request.data");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				AdminTestUtil.generateCurrentUTCTimeStamp(), "request.timeStamp");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				AdminTestUtil.generateCurrentUTCTimeStamp(), GlobalConstants.REQUESTTIME);
			
		residentCookie = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
		
		String content = RestClient.postRequestWithCookie(cryptoEncryptUrl, jsonContent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, COOKIENAME, residentCookie).asString();
		String data = JsonPrecondtion.getValueFromJson(content, "response.data");
		logger.info("data is" + data);
		return EncryptionDecrptionUtil.splitEncryptedData(data);
	}

	private String getHash(String content) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(content.getBytes()));
	}
	
	public String constructBiorequest(String input, String bioValueencryptionTemplateJson, boolean isInternal, String testCaseName) throws Exception {
		String bioValue = null;
		String  timestamp = null;
		String transactionId = null;
		String previousHash = getHash("");
		byte[] previousBioDataHash = null;
		byte [] previousDataByteArr =  "".getBytes(StandardCharsets.UTF_8);
		previousBioDataHash = generateHash(previousDataByteArr);
		
		JSONObject request = new JSONObject(input);
		if (request.has(GlobalConstants.REQUEST)) {
			JSONObject bioJson = request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.BIOMETRICS).getJSONObject(0).getJSONObject("data");
			bioValue = bioJson.getString(GlobalConstants.BIOVALUE);
			transactionId = bioJson.getString(GlobalConstants.TRANSACTIONID);
			timestamp = bioJson.getString("timestamp");
		}
		
        byte [] currentDataByteArr = org.apache.commons.codec.binary.Base64.decodeBase64(bioValue);
	       
        byte[] currentBioDataHash = generateHash (currentDataByteArr);
        byte[] finalBioDataHash = new byte[currentBioDataHash.length + previousBioDataHash.length];
        System.arraycopy(previousBioDataHash, 0, finalBioDataHash, 0, previousBioDataHash.length);
        System.arraycopy(currentBioDataHash, 0, finalBioDataHash, previousBioDataHash.length, currentBioDataHash.length);
		String hash = toHex (generateHash (finalBioDataHash));
		previousBioDataHash = decodeHex(hash);
		
		String encryptedContent = encryptIsoBioValue(bioValue, timestamp, bioValueencryptionTemplateJson,
				transactionId, isInternal);
		String encryptedBioValue = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedData");
		String encryptedSessionKey = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedSessionKey");
		encryptedSessionKeyString = encryptedSessionKey;
		request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.BIOMETRICS).getJSONObject(0).getJSONObject("data").put(GlobalConstants.BIOVALUE, encryptedBioValue);
		request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.BIOMETRICS).getJSONObject(0).put("hash", hash);
		logger.info(encryptedSessionKeyString);
		
		
		
		return request.toString();
	}

	public String constractBioIdentityRequest(String identityRequest, String bioValueencryptionTemplateJson,
			String testcaseName, boolean isInternal) throws Exception {
		int count = AuthTestsUtil.getNumberOfTimeWordPresentInString(identityRequest, "\"data\"");
		String previousHash = getHash("");
		byte[] previousBioDataHash = null;
		byte [] previousDataByteArr =  "".getBytes(StandardCharsets.UTF_8);
		previousBioDataHash = generateHash(previousDataByteArr);
		for (int i = 0; i < count; i++) {
			String biometricsMapper = "identityRequest.(biometrics)[" + i + "]";
			if (!isInternal) {
				String digitalId = JsonPrecondtion.getJsonValueFromJson(identityRequest,
						biometricsMapper + ".data.digitalId");
				digitalId = getSignedBiometrics(digitalId,"FTM");
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, digitalId,
						biometricsMapper + ".data.digitalId");
			}
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
					AdminTestUtil.generateCurrentUTCTimeStamp(), biometricsMapper + ".data.timestamp");

			String data = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + GlobalConstants.DATA);
			String bioValue = JsonPrecondtion.getValueFromJson(data, GlobalConstants.BIOVALUE);

			String timestamp = JsonPrecondtion.getValueFromJson(data, "timestamp");
			String transactionId = JsonPrecondtion.getValueFromJson(data, GlobalConstants.TRANSACTIONID);
			String encryptedContent = encryptIsoBioValue(bioValue, timestamp, bioValueencryptionTemplateJson,
					transactionId, isInternal);
			String encryptedBioValue = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedData");
			String encryptedSessionKey = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedSessionKey");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedBioValue,
					biometricsMapper + ".data.bioValue");
			String latestData = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + GlobalConstants.DATA);
			String signedData = "";
			if (isInternal == false) {
				signedData = getSignedBiometrics(latestData,"DEVICE");
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
						EncryptionDecrptionUtil.idaFirThumbPrint, biometricsMapper + ".thumbprint");
			} else if (isInternal) {
				signedData = EncryptionDecrptionUtil.getBase64EncodedString(latestData);
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
						EncryptionDecrptionUtil.internalThumbPrint, biometricsMapper + ".thumbprint");
			}

			if (testcaseName.toLowerCase().contains("without".toLowerCase())
					&& testcaseName.toLowerCase().contains("signature".toLowerCase())
					&& testcaseName.toLowerCase().contains("_neg".toLowerCase()))
				signedData = signedData.split(Pattern.quote("."))[1];
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, signedData,
					biometricsMapper + GlobalConstants.DATA);
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedSessionKey,
					biometricsMapper + ".sessionKey");
	        byte [] currentDataByteArr = org.apache.commons.codec.binary.Base64.decodeBase64(bioValue);
	       
	        byte[] currentBioDataHash = generateHash (currentDataByteArr);
	        byte[] finalBioDataHash = new byte[currentBioDataHash.length + previousBioDataHash.length];
	        System.arraycopy(previousBioDataHash, 0, finalBioDataHash, 0, previousBioDataHash.length);
	        System.arraycopy(currentBioDataHash, 0, finalBioDataHash, previousBioDataHash.length, currentBioDataHash.length);
			String hash = toHex (generateHash (finalBioDataHash));
			previousBioDataHash = decodeHex(hash);
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, hash,
					biometricsMapper + ".hash");
		}

		identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
				AdminTestUtil.generateCurrentUTCTimeStamp(), "identityRequest.timestamp");

		return identityRequest;
	}
	
	private String getSignedData(String identityDataBlock, String partnerId) {

		return generateSignatureWithRequest(identityDataBlock, "BOOLEAN:true", partnerId);
	}
	
	private String getSignedBiometrics(String identityDataBlock, String key) {

		return generateSignatureWithBioMetric(identityDataBlock, "BOOLEAN:true", key);
		
	}
	
	private String generateSignatureWithBioMetric(String identityDataBlock, String string, String key) {
		
		String singResponse = null;
		
        residentCookie = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
        HashMap<String, String> pathParamsMap = new HashMap<>();
        pathParamsMap.put("partnerType", key);
        pathParamsMap.put("moduleName", BaseTestCase.certsForModule);
        pathParamsMap.put("certsDir", ConfigManager.getauthCertsPath());
		Response response = RestClient.postRequestWithQueryParamBodyAndCookie(
				EncryptUtilBaseUrl + props.get("signRequest"), identityDataBlock, pathParamsMap,
				 MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, GlobalConstants.AUTHORIZATION,
				residentCookie);
		
		byte[] bytePayload = identityDataBlock.getBytes();
		String payloadData = Base64.getUrlEncoder().encodeToString(bytePayload);
		payloadData= payloadData.replace("=", "");
		String signNewResponse = response.asString().replace("..", "."+ payloadData +".");
		logger.info(signNewResponse);
		
        
         singResponse = response.asString();
		
		return signNewResponse;
	}

	private static final String HASH_ALGORITHM_NAME = "SHA-256";
    public static byte[] generateHash(final byte[] bytes) throws NoSuchAlgorithmException{
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
        return messageDigest.digest(bytes);
    }
    public static byte[] decodeHex(String hexData) throws DecoderException{
        return Hex.decodeHex(hexData);
    }
    public static String toHex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }
    

}
