package io.mosip.testrig.authentication.demo.service.helper;

import static java.util.Arrays.copyOfRange;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CryptoCoreUtil {

	private final static String RSA_ECB_OAEP_PADDING = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";

	private final static int THUMBPRINT_LENGTH = 32;
	private final static int NONCE = 12;
	private final static int AADSize = 32;
	public static final byte[] VERSION_RSA_2048 = "VER_R2".getBytes();

	@Value("${mosip.prependThumbprint:true}")
	private boolean isThumbprint;

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
		} catch (java.security.NoSuchAlgorithmException e) {
			log.error("Not able to decrypt the data : {}" , e);
			throw new NoSuchAlgorithmException(e);
		} catch (NoSuchPaddingException e) {
			throw new NoSuchPaddingException(e.getMessage());
		} catch (java.security.InvalidKeyException e) {
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
}