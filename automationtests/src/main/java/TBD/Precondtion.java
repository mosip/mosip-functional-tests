package TBD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

/**
 * Precondtion json file according to the input and mapping provided in test
 * data yml file
 * 
 * @author Vignesh
 */
public class Precondtion {
	
	private static final Logger PRECON_LOGGER = Logger.getLogger(Precondtion.class);
	
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param auditMappingPath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWritePropertyFile(String auditMappingPath,Map<String, String> fieldvalue,
			String outputFilePath) {
		FileOutputStream outputStream = null;
		Map<String, String> auditTxnValue = new HashMap<String, String>();
		try {
			if (getKeywordObject(TestDataConfig.getModuleName()) != null) {
				fieldvalue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);
				if (fieldvalue != null) {
					for (Entry<String, String> entry : fieldvalue.entrySet()) {
						String orgKey = AuthTestsUtil.getPropertyFromFilePath(auditMappingPath).get(entry.getKey()).toString();
						auditTxnValue.put(orgKey, entry.getValue());
					}
					Properties prop = new Properties();
					outputStream = new FileOutputStream(outputFilePath);
						for (Entry<String, String> entry : auditTxnValue.entrySet()) {
							prop.setProperty(entry.getKey(), entry.getValue());
						}
						prop.store(outputStream, "UTF-8");
				}
			}
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
		}finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
		return auditTxnValue;
	}
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param emailMappingPath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWriteEmailNotificationPropertyFile(String emailMappingPath,
			Map<String, String> fieldvalue, String outputFilePath) {
		FileOutputStream outputStream = null;
		Map<String, String> emailTemplatevalue = new HashMap<String, String>();
		try {
			if (getKeywordObject(TestDataConfig.getModuleName()) != null) {
				fieldvalue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);
				if (fieldvalue != null) {
					for (Entry<String, String> entry : fieldvalue.entrySet()) {
						String key = entry.getKey().toString();
						if (key.matches("email.template.*")) {
							String[] templates = entry.getValue().split(Pattern.quote("|"));
							emailTemplatevalue.put(templates[0], templates[1]);
						} else if (entry.getKey().toString().contains("email.otp")) {
							emailTemplatevalue.put(entry.getKey(), entry.getValue());
						}
					}
					Properties prop = new Properties();
					for (Entry<String, String> entry : emailTemplatevalue.entrySet()) {
						prop.setProperty(entry.getKey(), entry.getValue());
					}
					outputStream = new FileOutputStream(outputFilePath);
					prop.store(new OutputStreamWriter(outputStream, "UTF-8"), null);
				}
			}
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
		}finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
		return emailTemplatevalue;
	}
	
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWritePropertyFile(Map<String, String> fieldvalue, String outputFilePath) {
		FileOutputStream outputStream = null;
		Map<String, String> returnFieldValue = null;
		try {
			if (getKeywordObject(TestDataConfig.getModuleName()) != null) {
				returnFieldValue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);
				if (returnFieldValue != null) {
					Properties prop = new Properties();
					if (!new File(outputFilePath).exists())
						new File(outputFilePath).getParentFile().mkdirs();
					outputStream = new FileOutputStream(outputFilePath);
					for (Entry<String, String> entry : returnFieldValue.entrySet()) {
						prop.setProperty(entry.getKey(), entry.getValue());
					}
					prop.store(outputStream, null);
				}
			}
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
		}finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
		return returnFieldValue;
	}
	
	
	/**
	 * The method return object of KeywordUtil
	 * 
	 * @param moduleName
	 * @return
	 */
	public static KeywordUtil getKeywordObject(String moduleName) {
		KeywordUtil objKeywordUtil = null;
			//objKeywordUtil = new IdaKeywordUtil();
		return objKeywordUtil;
	}

}
