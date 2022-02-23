package io.mosip.admin.fw.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.lang.JoseException;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.BytesUtil;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.service.BaseTestCase;

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

	private String encryptIsoBioValue(String isoBiovalue, String timestamp, String bioValueEncryptionTemplateJson,
			String transactionId, boolean isInternal) {
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
		if (isInternal)
			jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, props.getProperty("internalrefId"),
					"request.referenceId");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, aad, "request.aad");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent, salt, "request.salt");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,  CryptoUtil.encodeBase64(CryptoUtil.decodeBase64(isoBiovalue)), "request.data");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				AdminTestUtil.generateCurrentUTCTimeStamp(), "request.timeStamp");
		jsonContent = JsonPrecondtion.parseAndReturnJsonContent(jsonContent,
				AdminTestUtil.generateCurrentUTCTimeStamp(), "requesttime");
		residentCookie = kernelAuthLib.getTokenByRole("resident");
		
		String content = RestClient.postRequestWithCookie(cryptoEncryptUrl, jsonContent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, COOKIENAME, residentCookie).asString();
		String data = JsonPrecondtion.getValueFromJson(content, "response.data");
		return EncryptionDecrptionUtil.splitEncryptedData(data);
	}

	private String getHash(String content) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(content.getBytes()));
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
				digitalId = getSignedBiometrics(digitalId,"ftm");
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, digitalId,
						biometricsMapper + ".data.digitalId");
			}
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
					AdminTestUtil.generateCurrentUTCTimeStamp(), biometricsMapper + ".data.timestamp");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, BaseTestCase.ApplnURI,
					biometricsMapper + ".data.domainUri");
			/*
			 * identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
			 * BaseTestCase.ApplnURI, biometricsMapper + ".data.env");
			 */

			String data = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String bioValue = JsonPrecondtion.getValueFromJson(data, "bioValue");

			//storeValue(bioValue);
			String timestamp = JsonPrecondtion.getValueFromJson(data, "timestamp");
			String transactionId = JsonPrecondtion.getValueFromJson(data, "transactionId");
			String encryptedContent = encryptIsoBioValue(bioValue, timestamp, bioValueencryptionTemplateJson,
					transactionId, isInternal);
			String encryptedBioValue = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedData");
			String encryptedSessionKey = JsonPrecondtion.getValueFromJson(encryptedContent, "encryptedSessionKey");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedBioValue,
					biometricsMapper + ".data.bioValue");
			String latestData = JsonPrecondtion.getJsonValueFromJson(identityRequest, biometricsMapper + ".data");
			String signedData = "";
			if (isInternal == false) {
				signedData = getSignedBiometrics(latestData,"device");
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
						EncryptionDecrptionUtil.idaFirThumbPrint, biometricsMapper + ".thumbprint");
			} else if (isInternal == true) {
				signedData = EncryptionDecrptionUtil.getBase64EncodedString(latestData);
				identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest,
						EncryptionDecrptionUtil.internalThumbPrint, biometricsMapper + ".thumbprint");
			}

			//String signedData = EncryptDecrptUtil.getBase64EncodedString(latestData);
			if (testcaseName.toLowerCase().contains("without".toLowerCase())
					&& testcaseName.toLowerCase().contains("signature".toLowerCase())
					&& testcaseName.toLowerCase().contains("_neg".toLowerCase()))
				signedData = signedData.split(Pattern.quote("."))[1];
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, signedData,
					biometricsMapper + ".data");
			identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, encryptedSessionKey,
					biometricsMapper + ".sessionKey");
			//System.out.println(identityRequest);
			//instead of BioData, bioValue (before encrytion in case of Capture response) is used for computing the hash.
	        //byte [] currentDataByteArr = java.util.Base64.getUrlDecoder().decode(bioValue);
	        byte [] currentDataByteArr = org.apache.commons.codec.binary.Base64.decodeBase64(bioValue);
	       
	        // Here Byte Array
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
	
	//header
	private String getSignedData(String identityDataBlock, String partnerId) {

		return generateSignatureWithRequest(identityDataBlock, "BOOLEAN:true", partnerId);
		/*
		 * try { // Extract Certificate String resourcePath = getResourcePath();
		 * FileInputStream pkeyfis = new FileInputStream(resourcePath +
		 * "ida/TestData/RunConfig/keys/privateKey.pem"); String pKey =
		 * FileUtil.getFileContent(pkeyfis, "UTF-8"); FileInputStream certfis = new
		 * FileInputStream(resourcePath + "ida/TestData/RunConfig/keys/cert.pem");
		 * String cert = FileUtil.getFileContent(certfis, "UTF-8"); pKey =
		 * pKey.replaceAll("-----BEGIN (.*)-----\n", ""); pKey =
		 * pKey.replaceAll("-----END (.*)----\n", ""); pKey = pKey.replaceAll("\\s",
		 * ""); cert = cert.replaceAll("-----BEGIN (.*)-----\n", ""); cert =
		 * cert.replaceAll("-----END (.*)----\n", ""); cert = cert.replaceAll("\\s",
		 * ""); CertificateFactory cf = CertificateFactory.getInstance("X.509");
		 * X509Certificate certificate = (X509Certificate) cf .generateCertificate(new
		 * ByteArrayInputStream(Base64.getDecoder().decode(cert))); // Extract Private
		 * Key KeyFactory kf = KeyFactory.getInstance("RSA"); PrivateKey privateKey =
		 * kf.generatePrivate(new
		 * PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey))); JWSValidation jws =
		 * new JWSValidation(); return jws.jwsSign(identityDataBlock, privateKey,
		 * certificate); } catch (Exception e) {
		 * logger.error("Exception Occured in signing the bio data:" +
		 * e.getStackTrace()); return "Automation error occured: "+e.getMessage(); }
		 */
	}
	
	//Bio-metriv data(device) and digitalID(ftm)
	private String getSignedBiometrics(String identityDataBlock, String key) {

		return generateSignatureWithBioMetric(identityDataBlock, "BOOLEAN:true", key);
		
	}
	
	private String generateSignatureWithBioMetric(String identityDataBlock, String string, String key) {
		
		String singResponse = null;
		//call sing() 
		try {
		 singResponse =  sign(identityDataBlock, true, true, false, null, getKeysDirPath(), key);
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException
				| OperatorCreationException | JoseException | IOException e) {
			e.printStackTrace();
		}
		return singResponse;
	}

	private static final String HASH_ALGORITHM_NAME = "SHA-256";
    public static byte[] generateHash(final byte[] bytes) throws NoSuchAlgorithmException{
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
        return messageDigest.digest(bytes);
    }
    public static byte[] decodeHex(String hexData) throws DecoderException{
        return Hex.decodeHex(hexData);
    }
    //public static byte[] getCertificateThumbprint(Certificate cert) throws CertificateEncodingException {
      //  return DigestUtils.sha256(cert.getEncoded());
    //}
    public static String toHex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }
    
	/*
	 * private static void storeValue(String biovalue) { try { BufferedWriter out =
	 * new BufferedWriter(new FileWriter("BioValue.txt")); out.write(biovalue);
	 * out.close(); } catch (IOException e) { System.out.println("Exception "); } }
	 */

}