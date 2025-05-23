package io.mosip.testrig.apirig.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
public class ConfigManager {
	private static final Logger LOGGER = Logger.getLogger(ConfigManager.class);
	private static Map<String, String> mosip_components_base_urls = new HashMap<>();
	protected static Map<String, Object> propertiesMap = new HashMap<>();
	
	private static void init() {
		Properties kernelProps = new Properties();
		URL resourceUrl = ConfigManager.class.getClassLoader().getResource("config/Kernel.properties");
		LOGGER.info(resourceUrl);
		URL resourceUrl1 = Thread.currentThread().getContextClassLoader().getResource("config/Kernel.properties");
		LOGGER.info(resourceUrl1);
		try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config/Kernel.properties")) {
			if (input != null) {
				// Load the properties from the input stream
				kernelProps.load(input);
			} else {
				LOGGER.error("Couldn't find Kernel.properties file");
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		// Process all common properties in kernelProps and add them to propertiesMap
	    for (String key : kernelProps.stringPropertyNames()) {
	    	propertiesMap.put(key, kernelProps.getProperty(key));
	    }
	}

	public static void init(Map<String, Object> additionalPropertiesMap) {

		// Step 1: Load and process common properties (from Kernel.properties)
		init();

		// Step 2: Add all entries from the additionalPropertiesMap to propertiesMap
		propertiesMap.putAll(additionalPropertiesMap);

		// Step 3: Now, process all keys in propertiesMap (both common and additional
		// properties)
		for (String key : propertiesMap.keySet()) {
			getValueForKeyAddToPropertiesMap(propertiesMap, key);
		}

		// Log the final propertiesMap to ensure all values have been processed
		// correctly
		LOGGER.info("propertiesMap = " + propertiesMap);
	}
	
	public static void getValueForKeyAddToPropertiesMap(Map<String, Object> propsMap, String key) {
		if (key.equalsIgnoreCase("serverErrorsToMonitor")) {
			String value = System.getenv("serverErrorsToMonitor") == null
					? (String) propsMap.get("serverErrorsToMonitor")
					: (String) propsMap.get("serverErrorsToMonitor") + "," + System.getenv("serverErrorsToMonitor");
			propertiesMap.put(key, value);
		} else if (key.equalsIgnoreCase("eSignetbaseurl")) {
			String value = null;
			if (System.getenv("eSignetbaseurl") != null) {
				value = System.getenv("eSignetbaseurl");
			} else if ((String) propsMap.get("eSignetbaseurl") != null
					&& !((String) propsMap.get("eSignetbaseurl")).isBlank()) {
				value = (String) propsMap.get("eSignetbaseurl");
			} else {
				value = System.getProperty("env.endpoint").replace("api-internal", "esignet");
			}
			propertiesMap.put(key, value);
		} else if (key.equalsIgnoreCase("signupBaseUrl")) {
			String value = null;
			if (System.getenv("signupBaseUrl") != null) {
				value = System.getenv("signupBaseUrl");
			} else if ((String) propsMap.get("signupBaseUrl") != null
					&& !((String) propsMap.get("signupBaseUrl")).isBlank()) {
				value = (String) propsMap.get("signupBaseUrl");
			} else {
				value = System.getProperty("env.endpoint").replace("api-internal", "signup");
			}
			propertiesMap.put(key, value);
		} else if (key.equalsIgnoreCase("injiCertifyBaseURL")) {
			String value = null;
			if (System.getenv("injiCertifyBaseURL") != null) {
				value = System.getenv("injiCertifyBaseURL");
			} else if ((String) propsMap.get("injiCertifyBaseURL") != null
					&& !((String) propsMap.get("injiCertifyBaseURL")).isBlank()) {
				value = (String) propsMap.get("injiCertifyBaseURL");
			} else {
				value = System.getProperty("env.endpoint").replace("api-internal", "injicertify");
			}
			propertiesMap.put(key, value);
		} else if (key.equalsIgnoreCase("mosip_components_base_urls")) {
			String components_base_urls = System.getenv("mosip_components_base_urls") == null
					? (String) propsMap.get("mosip_components_base_urls")
					: System.getenv("mosip_components_base_urls");
			loadComponentBaseURLs(components_base_urls);
		} else {
			String value = System.getenv(key) == null ? (String) propsMap.get(key) : System.getenv(key);
			propertiesMap.put(key, value);
		}
	}
	
	public static void loadComponentBaseURLs(String components_base_urls) {
		if (components_base_urls != null && !components_base_urls.isEmpty()) {
			// Split the input string by semicolons
			String[] pairs = components_base_urls.split(";");

			// Iterate over the pairs and split each by the equals sign to get key and value
			for (String pair : pairs) {
				String[] keyValue = pair.split("=");
				if (keyValue.length == 2) {
					mosip_components_base_urls.put(keyValue[0], keyValue[1]);
				}
			}
		}
	}

	protected static Properties getproperties(String path) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
		} catch (IOException e) {
			LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}


	public static String getproperty(String key) {
		return propertiesMap.get(key) == null ? "" : propertiesMap.get(key).toString();
	}
	
	public static int getIntProperty(String key, int defaultValue) {
		try {
			String value = ConfigManager.getproperty(key);
			if (value != null && !value.trim().isEmpty()) {
				return Integer.parseInt(value.trim());
			}
		} catch (NumberFormatException e) {
			LOGGER.warn("Exception occured while parsing : " + e.getMessage());
		}
		return defaultValue;
	}


	public static String getRolesForUser(String userId) { 
		return getproperty("roles." + userId);
	}
	
	public static String getRolesForUser() { 
		return getproperty("roles");
	}

	public static String getServerErrorsToMonitor() { return getproperty("serverErrorsToMonitor");	}
	public static String getEnableDebug() {	return getproperty("enableDebug");	}
	public static String getPmsClientId() {	return getproperty("mosip_pms_client_id"); }
	public static String getmountPath() { return getproperty("mountPath"); }
	public static Boolean IsDebugEnabled() { return getproperty("enableDebug").equalsIgnoreCase("yes"); }  
	public static Boolean reportIgnoredTestCases() { return getproperty("reportIgnoredTestCases").equalsIgnoreCase("yes");	}
	public static String getS3Host() { return getproperty("s3-host"); }
	public static String getS3Region() { return getproperty("s3-region"); }
	public static String getS3UserKey() { return getproperty("s3-user-key"); }
	public static String getS3SecretKey() { return getproperty("s3-user-secret"); }
	public static String getS3Account() { return getproperty("s3-account"); } 
	public static String getS3AccountForPersonaData() { return getproperty("s3-account-for-persona-data"); }
	public static String getPushReportsToS3() { return getproperty("push-reports-to-s3"); }
	public static String getKMDbSchema() { return getproperty("km_db_schema"); }
	public static String getMasterDbSchema() { return getproperty("master_db_schema"); }
	public static String getPMSDbSchema() { return getproperty("pms_db_schema"); }
	public static String getAuditDbSchema() { return getproperty("audit_db_schema"); }
	
	public static String getDbPort() { return getproperty("db-port"); }
	public static String getDbServer() { return getproperty("db-server"); }
	public static String getMockNotificationChannel() { return getproperty("mockNotificationChannel"); }
	
	
	public static String getIdaDbSchema() { return getproperty("ida_db_schema"); }
	public static int getLangselect() {	return Integer.parseInt(getproperty("langselect")); }
	public static String getauthCertsPath(){ return getproperty("authCertsPath"); }
	public static String getAuthDemoServiceBaseUrl() { return  getproperty("authDemoServiceBaseURL"); }
	public static String getAuthDemoServicePort() { return  getproperty("authDemoServicePort"); }
	public static String getReportExpirationInDays() { return  getproperty("reportExpirationInDays"); }
	public static String getDbDriverClass() { return getproperty("hibernate.connection.driver_class"); }
	public static String getDbConnectionPoolSize() { return getproperty("hibernate.connection.pool_size"); }
	public static String getDbDialect() { return getproperty("hibernate.dialect");	}
	public static String getShowSql() { return getproperty("hibernate.show_sql"); }
	public static String getDbSessionContext() { return getproperty("hibernate.current_session_context_class"); }
	public static String getUsePreConfiguredOtp() { return getproperty("usePreConfiguredOtp"); }
	public static String getPreConfiguredOtp() { return getproperty("preconfiguredOtp"); }
	public static String getPartnerUrlSuffix() { return getproperty("partnerUrlSuffix"); }
	public static String getSlackWebHookUrl() { return getproperty("slack-webhook-url"); }
	public static String getIAMRealmId() { return getproperty("keycloak-realm-id"); }
	public static String getIdaDbUrl() { return "jdbc:postgresql://" + getproperty("db-server") + ":" + getproperty("db-port") + "/mosip_ida";	}
	public static String getAuditDbUrl() {	return "jdbc:postgresql://" + getproperty("db-server") + ":" + getproperty("db-port") + "/mosip_audit";	}
	
	public static String getIdRepoDbUrl() {	return "jdbc:postgresql://" + getproperty("db-server") + ":" + getproperty("db-port") + "/mosip_idrepo";	}
	
	public static String getPMSDbUrl() { return "jdbc:postgresql://" + getproperty("db-server") + ":" +  getproperty("db-port") + "/mosip_pms";	}
	public static String getKMDbUrl() { return "jdbc:postgresql://" + getproperty("db-server") + ":" +  getproperty("db-port") + "/mosip_keymgr"; }
	public static String getMASTERDbUrl() { return "jdbc:postgresql://" + getproperty("db-server") + ":" +  getproperty("db-port") + "/mosip_master"; }
	public static String getUserAdminName() { return getproperty("admin_userName"); }
	public static String getUserAdminPassword() { return getproperty("admin_password"); }
	public static String getPmsClientSecret() {	return getproperty("mosip_pms_client_secret"); }
	public static String getPartnerClientSecret() {	return getproperty("mosip_partner_client_secret"); }
	public static String getPartnerClientId() {	return getproperty("mosip_partner_client_id"); }
	public static String getPmsAppId() { return getproperty("mosip_pms_app_id"); }
	public static String getResidentClientSecret() { return getproperty("mosip_resident_client_secret"); }
	public static String getResidentClientId() { return getproperty("mosip_resident_client_id"); }
	public static String getResidentAppId() { return getproperty("mosip_resident_app_id"); }
	public static String getMPartnerMobileClientId() {	return getproperty("mpartner_default_mobile_client_id"); }
	public static String getMPartnerMobileClientSecret() { return getproperty("mpartner_default_mobile_secret"); }
	public static String getAdminClientSecret() { return getproperty("mosip_admin_client_secret"); }
	public static String getAdminClientId() { return getproperty("mosip_admin_client_id"); }
	public static String getAdminAppId() { return getproperty("mosip_admin_app_id"); }
	public static String getIdRepoClientSecret() { return getproperty("mosip_idrepo_client_secret"); }
	public static String getidRepoClientId() { return getproperty("mosip_idrepo_client_id"); }
	public static String getidRepoAppId() { return getproperty("mosip_idrepo_app_id"); }
	public static String getRegprocClientSecret() { return getproperty("mosip_regproc_client_secret"); }
	public static String getRegprocClientId() { return getproperty("mosip_regproc_client_id"); }
	public static String getRegprocAppId() { return getproperty("mosip_regprocclient_app_id"); }
	public static String getIdaClientSecret() { return getproperty("mosip_ida_client_secret"); }
	public static String getIdaClientId() { return getproperty("mosip_ida_client_id"); }
	public static String getIdaAppId() { return getproperty("mosip_ida_app_id"); }
	public static String getHotListClientSecret() { return getproperty("mosip_hotlist_client_secret"); }
	public static String getHotListClientId() { return getproperty("mosip_hotlist_client_id"); }
	public static String getHotListAppId() { return getproperty("mosip_hotlist_app_id"); }
	public static String getAutomationClientSecret() { return getproperty("mosip_testrig_client_secret"); }
	public static String getAutomationClientId() { return getproperty("mosip_testrig_client_id"); }
	public static String getAutomationAppId() { return getproperty("mosip_automation_app_id"); }
	public static String getTargetEnvName() { return getproperty("db-server");	}
	public static String getAuditDbUser() { return getproperty("db-su-user");	}
	public static String getIdRepoDbUser() { return getproperty("db-su-user");	}
	public static String getAuditDbPass() { return getproperty("postgres-password"); }
	public static String getIdaDbUser() { return getAuditDbUser();	}
	public static String getIdaDbPass() { return getAuditDbPass();	}
	public static String getPMSDbUser() { return getAuditDbUser();	}
	public static String getPMSDbPass() { return getAuditDbPass();	}
	public static String getKMDbUser() { return getAuditDbUser(); }
	public static String getKMDbPass() { return getAuditDbPass(); }	
	public static String getMasterDbUser() { return getAuditDbUser(); }
	public static String getMasterDbPass() { return getAuditDbPass(); }
	public static String getIAMUsersToCreate() { return getproperty("iam-users-to-create");  }
	public static String getIAMUsersPassword() { return getproperty("iam-users-password"); }
	public static String getIAMUrl() {	return getproperty("keycloak-external-url")  + "/auth";	}
	public static String getMountPathForReport() {	return getproperty("mountPathForReport");	}
	public static Boolean reportKnownIssueTestCases() { return getproperty("reportKnownIssueTestCases").equalsIgnoreCase("yes");}
	
	// TO DO -- To be removed from commons
	public static String getEsignetActuatorPropertySection() { return getproperty("esignetActuatorPropertySection");}
	public static String getEsignetBaseUrl() { return getproperty("eSignetbaseurl");}
	public static String getInjiCertifyBaseUrl() { return getproperty("injiCertifyBaseURL");}
	public static String getSunBirdBaseURL() { return getproperty("sunBirdBaseURL");}
	public static String getSignupBaseUrl() { return getproperty("signupBaseUrl");}
	
	public static synchronized boolean isInServiceNotDeployedList(String stringToFind) {
		String serviceNotDeployedList = getproperty("servicesNotDeployed"); 

		if (serviceNotDeployedList.isBlank())
			return false;

		List<String> serviceNotDeployed = Arrays.asList(serviceNotDeployedList.trim().split(","));
		if (ConfigManager.IsDebugEnabled())
			LOGGER.info("serviceNotDeployedList:  " + serviceNotDeployedList + ", serviceNotDeployed : "
					+ serviceNotDeployed + ", stringToFind : " + stringToFind);
		for (String string : serviceNotDeployed) {
			if (string.equalsIgnoreCase(stringToFind))
				return true;
			else if (stringToFind.toLowerCase().contains(string.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	
	public static String getComponentBaseURL(String component) {
		return mosip_components_base_urls.get(component);
	}
	
}
