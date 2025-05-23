package io.mosip.testrig.apirig.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.util.EmptyCheckUtils;


/**
 * The Class CryptoUtility is used for encryption and decryption.
 *
 * @author Arun Bose S
 * The Class CryptoUtil.
 */
public class CryptoUtil {

	/** The Constant SYM_ALGORITHM. */
	private static final String SYM_ALGORITHM = "AES";

	/** The Constant SYM_ALGORITHM_LENGTH. */
	private static final int SYM_ALGORITHM_LENGTH = 256;

	/** The bouncy castle provider. */
	private static BouncyCastleProvider bouncyCastleProvider;
	
	private static Encoder urlSafeEncoder;
	
	static {
		urlSafeEncoder = Base64.getUrlEncoder().withoutPadding();
	}

	static {
		bouncyCastleProvider = addProvider();
	}

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	private CryptoCore cryptoCore = new CryptoCore();
	
	

	/**
	 * Symmetric encrypt.
	 *
	 * @param data the data
	 * @param secretKey the secret key
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws NoSuchPaddingException the no such padding exception
	 * @throws InvalidKeyException the invalid key exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException the bad padding exception
	 */
	public byte[] symmetricEncrypt(byte[] data, SecretKey secretKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return cryptoCore.symmetricEncrypt(secretKey, data, null);

	}
	
	public static String encodeToURLSafeBase64(byte[] data) {
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return null;
		}
		return urlSafeEncoder.encodeToString(data);
	}
	
	public static byte[] decodePlainBase64(String data) {
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return null;
		}
		return Base64.getDecoder().decode(data);
	}
	
	
	/**
	 * Symmetric decrypt.
	 *
	 * @param secretKey the secret key
	 * @param encryptedDataByteArr the encrypted data byte arr
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws NoSuchPaddingException the no such padding exception
	 * @throws InvalidKeyException the invalid key exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException the bad padding exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 */
	public byte[] symmetricDecrypt(SecretKey secretKey, byte[] encryptedDataByteArr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		return cryptoCore.symmetricDecrypt(secretKey, encryptedDataByteArr, null);
	}

	/**
	 * Adds the provider.
	 *
	 * @return the bouncy castle provider
	 */
	private static BouncyCastleProvider addProvider() {
		BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
		Security.addProvider(bouncyCastleProvider);
		return bouncyCastleProvider;
	}

	/**
	 * Gen sec key.
	 *
	 * @return the secret key
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public SecretKey genSecKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen;
		SecretKey secretKey = null;
        keyGen = KeyGenerator.getInstance(CryptoUtil.SYM_ALGORITHM, bouncyCastleProvider);
		keyGen.init(CryptoUtil.SYM_ALGORITHM_LENGTH, new SecureRandom());
		secretKey = keyGen.generateKey();
        return secretKey;

	}

	/**
	 * Asymmetric encrypt.
	 *
	 * @param data the data
	 * @param publicKey the public key
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws NoSuchPaddingException the no such padding exception
	 * @throws InvalidKeyException the invalid key exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException the bad padding exception
	 */
	public byte[] asymmetricEncrypt(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return cryptoCore.asymmetricEncrypt(publicKey, data);
	}

	public byte[] symmetricEncrypt(byte[] data, SecretKey secretKey, byte[] iv, byte[] aad)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return cryptoCore.symmetricEncrypt(secretKey, data, iv, aad);

	}
}
