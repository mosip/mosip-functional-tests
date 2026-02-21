package io.mosip.testrig.apirig.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class JWKKeyUtil {
	private static final Logger logger = Logger.getLogger(JWKKeyUtil.class);
	private static final Map<String, String> jwkKeyCache = new HashMap<>();

	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	private static String cacheKey(String keyName, boolean isEnc) {
		String jwkKey = isEnc ? AdminTestUtil.generateJWKEncPublicKey() : AdminTestUtil.generateJWKPublicKey();
		if (jwkKey != null) {
			jwkKeyCache.put(keyName, jwkKey);}
		logger.info((isEnc ? "ENC " : "") + "keyName: " + keyName + " jwkKey: " + jwkKey);
		return jwkKey;
	}
	public static String generateAndCacheJWKKey(String keyName) {
		return cacheKey(keyName, false);
	}
	public static String generateAndCacheEncJWKKey(String keyName) {
		return cacheKey(keyName, true);
	}
	public static String getJWKKey(String keyName) {
		String jwkKey = jwkKeyCache.get(keyName);
		logger.info("keyName: " + keyName + " jwkKey: " + jwkKey);
		return jwkKey;
	}

}