package io.mosip.testrig.apirig.admin.fw.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.kernel.util.ConfigManager;

public class CertsUtil {
	private static final Logger logger = Logger.getLogger(CertsUtil.class);
	private static final Map<String, String> certsCache = new HashMap<>();
	
	
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	
    public static void addCertificateToCache(String keyName, String certData) {
        certsCache.put(keyName, certData);
        logger.info("keyName: " + keyName + " certData: " + certData );
    }
    public static String getCertificate(String keyName) {
    	String certData = certsCache.get(keyName);
    	logger.info("keyName: " + keyName + " certData: " + certData );
        return certData;
    }
}
