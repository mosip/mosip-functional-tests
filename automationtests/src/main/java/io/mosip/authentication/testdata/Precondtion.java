package io.mosip.authentication.testdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminKeywordUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.testdata.keywords.IdRepoKeywordUtil;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;
import io.mosip.kernel.fw.util.KernelKeywordUtil;
import io.mosip.pmp.fw.util.PartnerKeywordUtil;
import io.mosip.resident.fw.util.ResidentKeywordUtil;

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
		try {
			fieldvalue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to add
			Map<String, String> auditTxnValue = new HashMap<String, String>();
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				String orgKey = AuthTestsUtil.getPropertyFromFilePath(auditMappingPath).get(entry.getKey()).toString();
				auditTxnValue.put(orgKey, entry.getValue());
			}
			Properties prop = new Properties();
			OutputStream output = new FileOutputStream(outputFilePath);
				for (Entry<String, String> entry : auditTxnValue.entrySet()) {
					prop.setProperty(entry.getKey(), entry.getValue());
				}
				prop.store(output, "UTF-8");
			return auditTxnValue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
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
		try {
			fieldvalue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to
																									// add
			Map<String, String> emailTemplatevalue = new HashMap<String, String>();
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
			prop.store(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8"), null);
			return emailTemplatevalue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWritePropertyFile(Map<String, String> fieldvalue, String outputFilePath) {
		try {
			fieldvalue = getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to
																									// add
			Properties prop = new Properties();
			if (!new File(outputFilePath).exists())
				new File(outputFilePath).getParentFile().mkdirs();
			FileOutputStream output = new FileOutputStream(outputFilePath);
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
			output.close();
			output.flush();
			return fieldvalue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * The method return object of KeywordUtil
	 * 
	 * @param moduleName
	 * @return
	 */
	public static KeywordUtil getKeywordObject(String moduleName) {
		KeywordUtil objKeywordUtil = null;
		if (moduleName.equalsIgnoreCase("ida"))
			objKeywordUtil = new IdaKeywordUtil();
		else if (moduleName.equalsIgnoreCase("idrepo"))
			objKeywordUtil = new IdRepoKeywordUtil();
		else if (moduleName.equalsIgnoreCase("admin"))
			objKeywordUtil = new AdminKeywordUtil();
		else if (moduleName.equalsIgnoreCase("resident"))
			objKeywordUtil = new ResidentKeywordUtil();
		else if (moduleName.equalsIgnoreCase("partner"))
			objKeywordUtil = new PartnerKeywordUtil();
		else if (moduleName.equalsIgnoreCase("kernel"))
			objKeywordUtil = new KernelKeywordUtil();
		return objKeywordUtil;
	}

}
