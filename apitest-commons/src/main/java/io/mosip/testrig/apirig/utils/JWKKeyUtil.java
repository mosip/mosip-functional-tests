package io.mosip.testrig.apirig.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class JWKKeyUtil {
	private static final Logger logger = Logger.getLogger(JWKKeyUtil.class);
	private static final Map<String, String> jwkKeyCache = new HashMap<>();
	
	
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	
    public static String generateAndCacheJWKKey(String keyName) {
        String jwkKey = MosipTestRunner.generateJWKPublicKey();
        jwkKeyCache.put(keyName, jwkKey);
        logger.info("keyName: " + keyName + " jwkKey: " + jwkKey );
        return jwkKey;
    }
    public static String getJWKKey(String keyName) {
    	String jwkKey = jwkKeyCache.get(keyName);
    	logger.info("keyName: " + keyName + " jwkKey: " + jwkKey );
        return jwkKey;
    }
}
