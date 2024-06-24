package io.mosip.testrig.apirig.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.util.JWSValidation;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
 
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
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, aad, "request.aad");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, salt, "request.salt");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, isoBiovalue, "request.data");
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
			String biometricsMapper = "(biometrics)[" + i + "]";
			String digitalId = JsonPrecondtion.getJsonValueFromJson(identityRequest,
					biometricsMapper + ".data.digitalId");
			digitalId = getSignedData(digitalId);
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, digitalId,
					biometricsMapper + ".data.digitalId");
			String data = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String bioValue = JsonPrecondtion.getValueFromJson(data, GlobalConstants.BIOVALUE);
			String timestamp = JsonPrecondtion.getValueFromJson(data, "timestamp");
			String transactionId = JsonPrecondtion.getValueFromJson(data, GlobalConstants.TRANSACTIONID);
			String encryptedContent = encryptIsoBioValue(bioValue, timestamp, bioValueencryptionTemplateJson, transactionId);
			String encryptedBioValue = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedData");
			String encryptedSessionKey = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedSessionKey");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedBioValue,
					biometricsMapper + ".data.bioValue");
			String latestData = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String signedData = "";
			if (isInternal == false)
				signedData = getSignedData(latestData);
			else if (isInternal)
				signedData = EncryptDecrptUtil.getBase64EncodedString(latestData);
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
		FileInputStream pkeyfis = null;
		FileInputStream certfis = null;
		ByteArrayInputStream bIS = null;
		try {
			String resourcePath = RunConfigUtil.getResourcePath();
			pkeyfis = new FileInputStream(resourcePath + "ida/TestData/RunConfig/keys/privateKey.pem");
			String pKey = FileUtil.getFileContent(pkeyfis, StandardCharsets.UTF_8.name());
			certfis = new FileInputStream(resourcePath + "ida/TestData/RunConfig/keys/cert.pem");
			String cert = FileUtil.getFileContent(certfis, StandardCharsets.UTF_8.name());
			pKey = pKey.replaceAll("-----BEGIN (.*)-----\n", "");
			pKey = pKey.replaceAll("-----END (.*)----\n", "");
			pKey = pKey.replaceAll("\\s", "");
			cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
			cert = cert.replaceAll("-----END (.*)----\n", "");
			cert = cert.replaceAll("\\s", "");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			bIS = new ByteArrayInputStream(Base64.getDecoder().decode(cert));
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(bIS);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
			JWSValidation jws = new JWSValidation();
			return jws.jwsSign(identityDataBlock, privateKey, certificate);
		} catch (Exception e) {
			logger.error("Exception Occured in signing the bio data:" + e.getMessage());
			return "Automation error occured: "+e.getMessage();
		}finally {
			AdminTestUtil.closeInputStream(pkeyfis);
			AdminTestUtil.closeInputStream(certfis);
			AdminTestUtil.closeByteArrayInputStream(bIS);
		}
	}
	
	public static Map<String,Map<String,String>> allDeviceParam = new HashMap<>();

	
	
	
	
	
	
	
}