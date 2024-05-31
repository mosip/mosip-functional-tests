package helper;

import io.mosip.kernel.core.crypto.exception.InvalidParamSpecException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.util.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.copyOfRange;

@Slf4j
@Component
public class CryptoCoreUtil {

	private final static String RSA_ECB_OAEP_PADDING = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";

	private final static int THUMBPRINT_LENGTH = 32;
	private final static int NONCE = 12;
	private final static int AADSize = 32;
	public static final byte[] VERSION_RSA_2048 = "VER_R2".getBytes();
	
	private static final String MGF1 = "MGF1";

	private static final String HASH_ALGO = "SHA-256";
	
	private static final String PKCS11_STORE_TYPE = "PKCS11";

	// Used as a hack for softhsm oeap padding decryption usecase will be when we
		// will use in HSM
		@SuppressWarnings("java:S106")
		private static final String RSA_ECB_NO_PADDING = "RSA/ECB/NoPadding"; // NOSONAR using the padding for allowing OAEP padding in PKCS11 library
		

	@Value("${mosip.prependThumbprint:true}")
	private boolean isThumbprint;
	
	@Value("${mosip.kernel.crypto.asymmetric-algorithm-name:" + RSA_ECB_OAEP_PADDING + "}")
	private String asymmetricAlgorithm;
	
	@Value("${mosip.kernel.keymanager.hsm.keystore-type:PKCS11}")
	private String keystoreType;
	
	@Value("${mosip.kernel.keygenerator.asymmetric-key-length:2048}")
	private int asymmetricKeyLength;


	public String decrypt(String data, PrivateKeyEntry privateKeyEntry) throws Exception {
		try {
			byte[] dataBytes = CryptoUtil.decodeURLSafeBase64(data);
			byte[] decryptedDataBytes = decryptData(dataBytes, privateKeyEntry);
			return new String(decryptedDataBytes);
		}catch (Exception e){
			log.error( "Not able to decrypt the data : {}", e);
		}
		throw new Exception("Unknown decryption exception.");
	}

	public byte[] decryptData(byte[] requestData, PrivateKeyEntry privateKey) throws Exception  {
		String keySplitter = "#KEY_SPLITTER#";
		SecretKey symmetricKey = null;
		byte[] encryptedData = null;
		byte[] encryptedSymmetricKey = null;
		final int cipherKeyandDataLength = requestData.length;
		final int keySplitterLength = keySplitter.length();

		int keyDemiliterIndex = getSplitterIndex(requestData, 0, keySplitter);
		byte[] encryptedKey = copyOfRange(requestData, 0, keyDemiliterIndex);
		byte[] headerBytes = parseEncryptKeyHeader(encryptedKey);
		byte[] decryptedSymmetricKey = null;
		try {
			encryptedData = copyOfRange(requestData, keyDemiliterIndex + keySplitterLength, cipherKeyandDataLength);
			if (Arrays.equals(headerBytes, VERSION_RSA_2048)) {
				encryptedSymmetricKey = Arrays.copyOfRange(encryptedKey, THUMBPRINT_LENGTH + VERSION_RSA_2048.length,
						encryptedKey.length);
				byte[] aad = Arrays.copyOfRange(encryptedData, 0, AADSize);
				byte[] nonce = Arrays.copyOfRange(aad, 0, NONCE);
				byte[] encData = Arrays.copyOfRange(encryptedData, AADSize, encryptedData.length);
				decryptedSymmetricKey = asymmetricDecrypt(privateKey.getPrivateKey(),
						((RSAPrivateKey) privateKey.getPrivateKey()).getModulus(), encryptedSymmetricKey);
				symmetricKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, "AES");
				return symmetricDecrypt(symmetricKey, encData, nonce, aad);
			} else if (isThumbprint) {
				encryptedSymmetricKey = Arrays.copyOfRange(encryptedKey, THUMBPRINT_LENGTH, encryptedKey.length);
				decryptedSymmetricKey = asymmetricDecrypt(privateKey.getPrivateKey(),
						((RSAPrivateKey) privateKey.getPrivateKey()).getModulus(), encryptedSymmetricKey);
				symmetricKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, "AES");
				return symmetricDecrypt(symmetricKey, encryptedData, null);
			} else {
				decryptedSymmetricKey = asymmetricDecrypt(privateKey.getPrivateKey(),
						((RSAPrivateKey) privateKey.getPrivateKey()).getModulus(), encryptedKey);
				symmetricKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, "AES");
				return symmetricDecrypt(symmetricKey, encryptedData, null);
			}
		} catch (Exception e) {
			log.error( "Not able to decrypt the data : {}", e);
		}
		throw new Exception("Unknown decryption exception.");
	}

	public byte[] parseEncryptKeyHeader(byte[] encryptedKey) {
		byte[] versionHeaderBytes = Arrays.copyOfRange(encryptedKey, 0, VERSION_RSA_2048.length);
		if (!Arrays.equals(versionHeaderBytes, VERSION_RSA_2048)) {
			return new byte[0];
		}
		return versionHeaderBytes;
	}

	private static int getSplitterIndex(byte[] encryptedData, int keyDemiliterIndex, String keySplitter) {
		final byte keySplitterFirstByte = keySplitter.getBytes()[0];
		final int keySplitterLength = keySplitter.length();
		for (byte data : encryptedData) {
			if (data == keySplitterFirstByte) {
				final String keySplit = new String(
						copyOfRange(encryptedData, keyDemiliterIndex, keyDemiliterIndex + keySplitterLength));
				if (keySplitter.equals(keySplit)) {
					break;
				}
			}
			keyDemiliterIndex++;
		}
		return keyDemiliterIndex;
	}

	/**
	 * 
	 * @param privateKey
	 * @param keyModulus
	 * @param data
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 */
	private static byte[] asymmetricDecrypt(PrivateKey privateKey, BigInteger keyModulus, byte[] data)
			throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, InvalidKeyException {

		Cipher cipher;
		try {
			cipher = Cipher.getInstance(RSA_ECB_OAEP_PADDING);
			OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
					PSpecified.DEFAULT);
			cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			log.error("Not able to decrypt the data : {}" , e);
			throw new NoSuchAlgorithmException(e);
		} catch (NoSuchPaddingException e) {
			throw new NoSuchPaddingException(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new InvalidKeyException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidAlgorithmParameterException(e);
		}
	}

	private static byte[] symmetricDecrypt(SecretKey key, byte[] data, byte[] aad) {
		byte[] output = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
			byte[] randomIV = Arrays.copyOfRange(data, data.length - cipher.getBlockSize(), data.length);
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, randomIV);

			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
			if (aad != null && aad.length != 0) {
				cipher.updateAAD(aad);
			}
			output = cipher.doFinal(Arrays.copyOf(data, data.length - cipher.getBlockSize()));
		} catch (Exception e) {

		}
		return output;
	}

	public byte[] symmetricDecrypt(SecretKey key, byte[] data, byte[] nonce, byte[] aad)
			throws Exception {
		// Objects.requireNonNull(key, null);
		// CryptoUtils.verifyData(data);
		byte[] output = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, nonce);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
			if (aad != null) {
				cipher.updateAAD(aad);
			}
			output = cipher.doFinal(data, 0, data.length);
		} catch (InvalidAlgorithmParameterException e) {
			throw new Exception("Invalid key exception.", e);
		} catch (IllegalBlockSizeException e) {
			throw new Exception("Invalid key exception.", e);
		} catch (BadPaddingException e) {
			throw new Exception("Certificate thumbprint error.", e);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("Certificate thumbprint error.", e);
		} catch (NoSuchPaddingException e) {
			throw new Exception("Certificate thumbprint error.", e);
		} catch (InvalidKeyException e) {
			throw new Exception("Invalid key exception.", e);
		}
		return output;
	}

	public static byte[] getCertificateThumbprint(Certificate cert) throws Exception {
		try {
			return DigestUtils.sha256(cert.getEncoded());
		} catch (java.security.cert.CertificateEncodingException e) {
			throw new Exception("Invalid key exception.", e);
		}
	}

	public byte[] asymmetricEncrypt(PublicKey key, byte[] data) throws GeneralSecurityException {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher;
		try {	
			cipher = Cipher.getInstance(asymmetricAlgorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw e;
		}
		final OAEPParameterSpec oaepParams = new OAEPParameterSpec(HASH_ALGO, MGF1, MGF1ParameterSpec.SHA256,
				PSpecified.DEFAULT);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, oaepParams);
		} catch (InvalidKeyException e) {
			throw e;
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}
		return doFinal(data, cipher);
	}
	
	public byte[] asymmetricDecrypt(PrivateKey privateKey, byte[] data) throws GeneralSecurityException, InvalidCipherTextException {
		if (PKCS11_STORE_TYPE.equalsIgnoreCase(keystoreType)) {
			BigInteger keyModulus = ((RSAPrivateKey) privateKey).getModulus();
			return asymmetricDecrypt(privateKey, keyModulus, data, null);
		}
		return jceAsymmetricDecrypt(privateKey, data, null);
	}
	
	private byte[] asymmetricDecrypt(PrivateKey privateKey, BigInteger keyModulus, byte[] data, String storeType) throws GeneralSecurityException, InvalidCipherTextException {
		Objects.requireNonNull(privateKey, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher;
		try {
			cipher = Objects.isNull(storeType) ? Cipher.getInstance(RSA_ECB_NO_PADDING) :  // NOSONAR using the padding for allowing OAEP padding in PKCS11 library
						Cipher.getInstance(RSA_ECB_NO_PADDING, storeType); // NOSONAR using the padding for allowing OAEP padding in PKCS11 library
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
			throw e;
		}

		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			throw e;
		}
		/*
		 * This is a hack of removing OEAP padding after decryption with NO Padding as
		 * SoftHSM does not support it.Will be removed after HSM implementation
		 */
		byte[] paddedPlainText = doFinal(data, cipher);
		if (paddedPlainText.length < asymmetricKeyLength / 8) {
			byte[] tempPipe = new byte[asymmetricKeyLength / 8];
			System.arraycopy(paddedPlainText, 0, tempPipe, tempPipe.length - paddedPlainText.length,
					paddedPlainText.length);
			paddedPlainText = tempPipe;
		}
		
		return unpadOAEPPadding(paddedPlainText, keyModulus);
	}

	//	  This is a hack of removing OEAP padding after decryption with NO Padding as
	//	  SoftHSM does not support it.Will be removed after HSM implementation
	/**
	 * 
	 * @param paddedPlainText
	 * @param privateKey
	 * @return
	 * @throws InvalidCipherTextException 
	 */
	private byte[] unpadOAEPPadding(byte[] paddedPlainText, BigInteger keyModulus) throws InvalidCipherTextException {
		
	    try {
	    	OAEPEncoding encode = new OAEPEncoding(new RSAEngine(), new SHA256Digest());
		    BigInteger exponent = new BigInteger("1");
		    RSAKeyParameters keyParams = new RSAKeyParameters(false, keyModulus, exponent);
		    encode.init(false, keyParams);
			return encode.processBlock(paddedPlainText, 0, paddedPlainText.length);
		} catch (InvalidCipherTextException e) {
			throw e;
		}	    
	}
	 
	private byte[] jceAsymmetricDecrypt(PrivateKey privateKey, byte[] data, String storeType) throws GeneralSecurityException{
		Objects.requireNonNull(privateKey, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher;
		try {
			cipher = Objects.isNull(storeType) ? Cipher.getInstance(asymmetricAlgorithm) : 
						Cipher.getInstance(asymmetricAlgorithm, storeType);
			OAEPParameterSpec oaepParams = new OAEPParameterSpec(HASH_ALGO, MGF1, MGF1ParameterSpec.SHA256,
				PSpecified.DEFAULT);
			cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
			return doFinal(data, cipher);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
			throw e;
		} catch (InvalidKeyException e) {
			throw e;
		} catch (InvalidAlgorithmParameterException e) {
			throw e;
		}
	}
	
	private byte[] doFinal(byte[] data, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			throw e;
		} catch (BadPaddingException e) {
			throw e;
		}
	}
}