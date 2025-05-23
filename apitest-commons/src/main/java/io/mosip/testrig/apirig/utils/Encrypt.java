package io.mosip.testrig.apirig.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dto.EncryptionRequestDto;
import io.mosip.testrig.apirig.dto.EncryptionResponseDto;
public class Encrypt {

    private static final String SSL = "SSL";
    String publicKeyURL = "${mosip.ida.publicKey-url}";
    String appID = "${application.id}";

    String keySplitter = "#KEY_SPLITTER#";
    
    @Autowired
    CryptoUtil cryptoUtil;

    private static String digestAsPlainText(byte[] data) {
        return DatatypeConverter.printHexBinary(data).toUpperCase();
    }

    public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance(Encrypt.SSL);
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String arg1)
                throws CertificateException {
        }
    }};

    public SplittedEncryptedData splitEncryptedData(String data) throws Exception {
        //boolean encryptedDataHasVersion =  env.getProperty("encryptedDataHasVersion", boolean.class, false);
        boolean encryptedDataHasVersion = false;
        byte[] dataBytes = io.mosip.kernel.core.util.CryptoUtil.decodeURLSafeBase64(data);
        byte[][] splits = splitAtFirstOccurance(dataBytes, keySplitter.getBytes());
        byte[] thumbPrintAndSessionKey = splits[0];
        byte[] sessionKey;
        byte[] thumbPrint;

        if (thumbPrintAndSessionKey.length >= 288) {
            thumbPrint = Arrays.copyOfRange(thumbPrintAndSessionKey, thumbPrintAndSessionKey.length - 288,
                    thumbPrintAndSessionKey.length - 256);// Skip the 6 bytes version and take 32 bytes
            sessionKey = Arrays.copyOfRange(thumbPrintAndSessionKey, thumbPrintAndSessionKey.length - 256,
                    thumbPrintAndSessionKey.length);
        } else {
            throw new Exception("Invalid Encrypted Session Key");
        }

        byte[] encryptedData = splits[1];
        return new SplittedEncryptedData(io.mosip.kernel.core.util.CryptoUtil.encodeToURLSafeBase64(sessionKey), io.mosip.kernel.core.util.CryptoUtil.encodeToURLSafeBase64(encryptedData), digestAsPlainText(thumbPrint));
    }
    
	public EncryptionResponseDto encrypt(EncryptionRequestDto encryptionRequestDto, boolean isInternal, String dirPath)
			throws Exception {

		// Choose certificate file based on the internal flag
		String certFileName = isInternal ? CertificateTypes.INTERNAL.getFileName() : CertificateTypes.PARTNER.getFileName();

		// Load certificate from file
		X509Certificate x509Cert = getCertificate(dirPath, certFileName);

		// Proceed to encryption
		return kernelEncrypt(encryptionRequestDto, x509Cert);
	}
	
	private EncryptionResponseDto kernelEncrypt(
	        EncryptionRequestDto encryptionRequestDto,
	        X509Certificate x509Cert
	) throws Exception {

	    ObjectMapper objMapper = new ObjectMapper();
	    CryptoUtil cryptoUtil = new CryptoUtil();

	    // Convert identity request to JSON string
	    String identityBlock = objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest());

	    // Generate secret key
	    SecretKey secretKey = cryptoUtil.genSecKey();

	    // Prepare response object
	    EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();

	    // Encrypt the identity JSON block with secret key
	    byte[] encryptedIdentityBlock = cryptoUtil.symmetricEncrypt(
	            identityBlock.getBytes(StandardCharsets.UTF_8),
	            secretKey
	    );
	    encryptionResponseDto.setEncryptedIdentity(
	            Base64.encodeBase64URLSafeString(encryptedIdentityBlock)
	    );

	    // Encrypt the session key using public key
	    PublicKey publicKey = x509Cert.getPublicKey();
	    byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt(secretKey.getEncoded(), publicKey);
	    encryptionResponseDto.setEncryptedSessionKey(
	            Base64.encodeBase64URLSafeString(encryptedSessionKeyByte)
	    );

	    // Generate HMAC hash and encrypt it
	    byte[] hmacHash = HMACUtils2.generateHash(identityBlock.getBytes(StandardCharsets.UTF_8));
	    byte[] hmacPlainDigest = Encrypt.digestAsPlainText(hmacHash).getBytes();
	    byte[] encryptedHmac = cryptoUtil.symmetricEncrypt(hmacPlainDigest, secretKey);
	    encryptionResponseDto.setRequestHMAC(
	            Base64.encodeBase64URLSafeString(encryptedHmac)
	    );

	    return encryptionResponseDto;
	}


    public static class SplittedEncryptedData {
        private String encryptedSessionKey;
        private String encryptedData;
        private String thumbprint;

        public SplittedEncryptedData() {
            super();
        }

        public SplittedEncryptedData(String encryptedSessionKey, String encryptedData) {
            super();
            this.encryptedData = encryptedData;
            this.encryptedSessionKey = encryptedSessionKey;
        }

        public SplittedEncryptedData(String encryptedSessionKey, String encryptedData, String thumbprint) {
            super();
            this.encryptedData = encryptedData;
            this.encryptedSessionKey = encryptedSessionKey;
            this.thumbprint = thumbprint;
        }

        public String getEncryptedData() {
            return encryptedData;
        }

        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
        }

        public String getEncryptedSessionKey() {
            return encryptedSessionKey;
        }

        public void setEncryptedSessionKey(String encryptedSessionKey) {
            this.encryptedSessionKey = encryptedSessionKey;
        }

        public String getThumbprint() {
            return thumbprint;
        }

        public void setThumbprint(String thumbprint) {
            this.thumbprint = thumbprint;
        }
    }

    private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
        int index = findIndex(strBytes, sepBytes);
        if (index >= 0) {
            byte[] bytes1 = new byte[index];
            byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
            System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
            System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
            return new byte[][]{bytes1, bytes2};
        } else {
            return new byte[][]{strBytes, new byte[0]};
        }
    }

    private static int findIndex(byte arr[], byte[] subarr) {
        int len = arr.length;
        int subArrayLen = subarr.length;
        return IntStream.range(0, len).filter(currentIndex -> {
                    if ((currentIndex + subArrayLen) <= len) {
                        byte[] sArray = new byte[subArrayLen];
                        System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
                        return Arrays.equals(sArray, subarr);
                    }
                    return false;
                }).findFirst() // first occurence
                .orElse(-1); // No element found
    }
    
    private X509Certificate getCertificate(String dirPath, String fileName) throws CertificateException, IOException {
    	KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
		Path path = Paths.get(dirPath + "/" + fileName);
        if (!Files.exists(path)){
			throw new FileNotFoundException("Certificate File Not found in temp directory. FileName: " + fileName);
		}
		String certificateData = Files.readString(path);
		return (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
	}
    
	public SplittedEncryptedData encryptBio(String bioValue, String timestamp, String transactionId, boolean isInternal,
			String dirPath) throws Exception {

		// Load configuration values (or use defaults)
		final int DEFAULT_SALT_LAST_BYTES_NUM = 8;
		final int DEFAULT_AAD_LAST_BYTES_NUM = 12;

		int saltLastBytesNum = ConfigManager.getIntProperty("ida.salt.lastbytes.num", DEFAULT_SALT_LAST_BYTES_NUM);
		int aadLastBytesNum = ConfigManager.getIntProperty("ida.aad.lastbytes.num", DEFAULT_AAD_LAST_BYTES_NUM);

		// Instantiate encryption utility
		CryptoUtil cryptoUtil = new CryptoUtil();

		// Load appropriate certificate
		String certFileName = isInternal ? CertificateTypes.INTERNAL.getFileName() : CertificateTypes.IDA_FIR.getFileName();
		X509Certificate x509Cert = getCertificate(dirPath, certFileName);

		// Derive salt and AAD from XOR of timestamp and transactionId
		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, saltLastBytesNum);
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, aadLastBytesNum);

		// Generate secret key and encrypt the biometric data
		SecretKey secretKey = cryptoUtil.genSecKey();
		byte[] encryptedBioBlock = cryptoUtil.symmetricEncrypt(CryptoUtil.decodePlainBase64(bioValue), secretKey,
				saltLastBytes, aadLastBytes);

		// Encrypt session key with public key
		PublicKey publicKey = x509Cert.getPublicKey();
		byte[] encryptedSessionKey = cryptoUtil.asymmetricEncrypt(secretKey.getEncoded(), publicKey);

		// Return encoded result
		return new SplittedEncryptedData(CryptoUtil.encodeToURLSafeBase64(encryptedSessionKey),
				CryptoUtil.encodeToURLSafeBase64(encryptedBioBlock));
	}
	
	public X509Certificate getCertificate(boolean isInternal, String dirPath) throws CertificateException, IOException {
	    String certFileName = isInternal ? CertificateTypes.INTERNAL.getFileName() : CertificateTypes.PARTNER.getFileName();
	    return getCertificate(dirPath, certFileName);
	}
	
	public X509Certificate getBioCertificate(boolean isInternal, String dirPath)
			throws CertificateException, IOException {
		String certFileName = isInternal ? CertificateTypes.INTERNAL.getFileName() : CertificateTypes.IDA_FIR.getFileName();
		return getCertificate(dirPath, certFileName);
	}


}
