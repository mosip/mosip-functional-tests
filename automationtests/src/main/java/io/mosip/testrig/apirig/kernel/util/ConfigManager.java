package io.mosip.testrig.apirig.kernel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class ConfigManager {

	private static final Logger LOGGER = Logger.getLogger(ConfigManager.class);

	private static String MOSIP_PMS_CLIENT_SECRET = "mosip_pms_client_secret";
	private static String MOSIP_PMS_CLIENT_ID = "mosip_pms_client_id";
	private static String MOSIP_PMS_APP_ID = "mosip_pms_app_id";

	private static String MOSIP_RESIDENT_CLIENT_SECRET = "mosip_resident_client_secret";
	private static String MOSIP_RESIDENT_CLIENT_ID = "mosip_resident_client_id";
	private static String MOSIP_RESIDENT_APP_ID = "mosip_resident_app_id";

	private static String MOSIP_MOBILE_CLIENT_ID = "mpartner_default_mobile_client_id";
	private static String MOSIP_MOBILE_CLIENT_SECRET = "mpartner_default_mobile_secret";

	private static String MOSIP_IDREPO_CLIENT_SECRET = "mosip_idrepo_client_secret";
	private static String MOSIP_IDREPO_CLIENT_ID = "mosip_idrepo_client_id";
	private static String MOSIP_IDREPO_APP_ID = "mosip_idrepo_app_id";

	private static String MOSIP_ADMIN_CLIENT_SECRET = "mosip_admin_client_secret";
	private static String MOSIP_ADMIN_CLIENT_ID = "mosip_admin_client_id";
	private static String MOSIP_ADMIN_APP_ID = "mosip_admin_app_id";

	private static String MOSIP_REG_CLIENT_SECRET = "mosip_reg_client_secret";
	private static String MOSIP_REG_CLIENT_ID = "mosip_reg_client_id";
	private static String MOSIP_REGCLIENT_APP_ID = "mosip_regclient_app_id";

	private static String MOSIP_IDA_CLIENT_SECRET = "mosip_ida_client_secret";
	private static String MOSIP_IDA_CLIENT_ID = "mosip_ida_client_id";
	private static String MOSIP_IDA_APP_ID = "mosip_ida_app_id";

	private static String MOSIP_HOTLIST_CLIENT_SECRET = "mosip_hotlist_client_secret";
	private static String MOSIP_HOTLIST_CLIENT_ID = "mosip_hotlist_client_id";
	private static String MOSIP_HOTLIST_APP_ID = "mosip_hotlist_app_id";

	private static String MOSIP_AUTOMATION_CLIENT_SECRET = "mosip_testrig_client_secret";
	private static String MOSIP_AUTOMATION_CLIENT_ID = "mosip_testrig_client_id";
	private static String MOSIP_AUTOMATION_APP_ID = "mosip_automation_app_id";

	private static String S3_HOST = "s3-host";
	private static String S3_REGION = "s3-region";
	private static String S3_USER_KEY = "s3-user-key";
	private static String S3_SECRET_KEY = "s3-user-secret";
	private static String S3_ACCOUNT = "s3-account";
	private static String PUSH_TO_S3 = "push-reports-to-s3";
	private static String ENABLE_DEBUG = "enableDebug";
	private static String THREAD_COUNT = "threadCount";
	private static String LANG_SELECT = "langselect";

	private static String USEPRECONFIGOTP = "usePreConfiguredOtp";
	private static String ESIGNET_BASE_URL = "eSignetbaseurl";

	private static String PRECONFIGOTP = "preconfiguredOtp";
	private static String DB_PORT = "db-port";
	private static String DB_DOMAIN = "db-server";
	private static String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static String HIBERNATE_CONNECTION_POOL_SIZE = "hibernate.connection.pool_size";
	private static String HIBERNATE_DIALECT = "hibernate.dialect";
	private static String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static String HIBERNATE_CONTEXT_CLASS = "hibernate.current_session_context_class";

	private static String AUDIT_DB_USER = "db-su-user";
	private static String AUDIT_DB_PASS = "postgresql-password";
	private static String AUDIT_DB_SCHEMA = "audit_db_schema";

	private static String IDA_DB_USER = "db-su-user";
	private static String IDA_DB_PASS = "postgresql-password";
	private static String IDA_DB_SCHEMA = "ida_db_schema";

	private static String PMS_DB_USER = "db-su-user";
	private static String PMS_DB_PASS = "postgresql-password";
	private static String PMS_DB_SCHEMA = "pms_db_schema";

	private static String KM_DB_USER = "db-su-user";
	private static String KM_DB_PASS = "postgresql-password";
	private static String KM_DB_SCHEMA = "km_db_schema";

	private static String MASTER_DB_USER = "db-su-user";
	private static String MASTER_DB_PASS = "postgresql-password";
	private static String MASTER_DB_SCHEMA = "master_db_schema";

	private static String IAM_EXTERNAL_URL = "keycloak-external-url";
	private static String IAM_REALM_ID = "keycloak-realm-id";
	private static String IAM_USERS_TO_CREATE = "iam-users-to-create";
	private static String IAM_USERS_PASSWORD = "iam-users-password";

	private static String ESIGNET_DEPLOYED = "eSignetDeployed";
	private static String esignet_deployed;

	private static String AUTH_DEMO_SERVICE_PORT = "authDemoServicePort";
	private static String AUTH_DEMO_SERVICE_BASE_URL = "authDemoServiceBaseURL";
	private static String MOUNT_PATH = "mountPath";
	private static String AUTHCERTS_PATH = "authCertsPath";
	private static String MOUNT_PATH_FOR_SCENARIO = "mountPathForScenario";

	private static String PACKET_UTILITY_BASE_URL = "packetUtilityBaseUrl";

	private static String REPORT_EXPIRATION_IN_DAYS = "reportExpirationInDays";

	private static String SCENARIOS_TO_BE_SKIPPED = "scenariosToSkip";
	private static String toSkippedList;

	private static String pms_client_secret;
	private static String pms_client_id;
	private static String pms_app_id;

	private static String resident_client_secret;
	private static String resident_client_id;
	private static String resident_app_id;

	private static String mpartner_mobile_client_id;
	private static String mpartner_mobile_client_secret;

	private static String idrepo_client_secret;
	private static String idrepo_client_id;
	private static String idrepo_app_id;

	private static String admin_client_secret;
	private static String admin_client_id;
	private static String admin_app_id;

	private static String regproc_client_secret;
	private static String regproc_client_id;
	private static String regproc_app_id;

	private static String ida_client_secret;
	private static String ida_client_id;
	private static String ida_app_id;

	private static String hotlist_client_secret;
	private static String hotlist_client_id;
	private static String hotlist_app_id;

	private static String automation_client_secret;
	private static String automation_client_id;
	private static String automation_app_id;

	private static String s3Region;
	private static String s3Host;
	private static String s3UserKey;
	private static String s3Account;
	private static String s3SecretKey;
	private static String push_reports_to_s3;
	private static String enableDebug;
	private static String threadCount;
	private static String langselect;
	private static String usePreConfiguredOtp;
	private static String preconfiguredOtp;
	private static String eSignetbaseurl;
	
	private static String dbPort;
	private static String dbDomain;
	private static String hibernateConnectionDriverClass;
	private static String hibernateConnectionPoolSize;
	private static String hibernateDialect;
	private static String hibernateShowSql;
	private static String hibernate_current_session_context_class;

	private static String auditDBUser;
	private static String auditDBPass;
	private static String auditDBSchema;

	private static String idaDBUser;
	private static String idaDBPass;
	private static String idaDBSchema;

	private static String pmsDBUser;
	private static String pmsDBPass;
	private static String pmsDBSchema;

	private static String kmDBUser;
	private static String kmDBPass;
	private static String kmDBSchema;

	private static String masterDBUser;
	private static String masterDBPass;
	private static String masterDBSchema;

	private static String iamExternalURL;
	private static String iamRealmID;
	private static String iamUsersToCreate;
	private static String iamUsersPassword;
	private static String authDemoServicePort;
	private static String authDemoServiceBaseUrl;

	private static String mountPath;
	private static String authCertsPath;
	private static String mountPathForScenario;
	private static String packetUtilityBaseUrl;
	public static Properties propsKernel;
	private static String reportExpirationInDays;

	public static void setProperty(String key, String value) {
		// Overwrite the value with only if the key exists
		if (propsKernel.containsKey(key)) {
			propsKernel.setProperty(key, value);
		}
	}

	public static String getValueForKey(String key) {
		String value = System.getenv(key) == null ? propsKernel.getProperty(key) : System.getenv(key);
		setProperty(key, value);

		return value;
	}

	public static void init() {
		propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/" + "config/Kernel.properties");

		pms_client_secret = getValueForKey(MOSIP_PMS_CLIENT_SECRET);
		pms_client_id = getValueForKey(MOSIP_PMS_CLIENT_ID);
		pms_app_id = getValueForKey(MOSIP_PMS_APP_ID);
		resident_client_secret = getValueForKey(MOSIP_RESIDENT_CLIENT_SECRET);
		resident_client_id = getValueForKey(MOSIP_RESIDENT_CLIENT_ID);
		resident_app_id = getValueForKey(MOSIP_RESIDENT_APP_ID);
		mpartner_mobile_client_id = getValueForKey(MOSIP_MOBILE_CLIENT_ID);
		mpartner_mobile_client_secret = getValueForKey(MOSIP_MOBILE_CLIENT_SECRET);
		idrepo_client_secret = getValueForKey(MOSIP_IDREPO_CLIENT_SECRET);
		idrepo_client_id = getValueForKey(MOSIP_IDREPO_CLIENT_ID);
		idrepo_app_id = getValueForKey(MOSIP_IDREPO_APP_ID);
		admin_client_secret = getValueForKey(MOSIP_ADMIN_CLIENT_SECRET);
		admin_client_id = getValueForKey(MOSIP_ADMIN_CLIENT_ID);
		admin_app_id = getValueForKey(MOSIP_ADMIN_APP_ID);
		regproc_client_secret = getValueForKey(MOSIP_REG_CLIENT_SECRET);
		regproc_client_id = getValueForKey(MOSIP_REG_CLIENT_ID);
		regproc_app_id = getValueForKey(MOSIP_REGCLIENT_APP_ID);
		ida_client_secret = getValueForKey(MOSIP_IDA_CLIENT_SECRET);
		ida_client_id = getValueForKey(MOSIP_IDA_CLIENT_ID);
		ida_app_id = getValueForKey(MOSIP_IDA_APP_ID);
		hotlist_client_secret = getValueForKey(MOSIP_HOTLIST_CLIENT_SECRET);
		hotlist_client_id = getValueForKey(MOSIP_HOTLIST_CLIENT_ID);
		hotlist_app_id = getValueForKey(MOSIP_HOTLIST_APP_ID);
		automation_client_secret = getValueForKey(MOSIP_AUTOMATION_CLIENT_SECRET);
		automation_client_id = getValueForKey(MOSIP_AUTOMATION_CLIENT_ID);
		automation_app_id = getValueForKey(MOSIP_AUTOMATION_APP_ID);
		s3Host = getValueForKey(S3_HOST);
		s3Region = getValueForKey(S3_REGION);
		s3UserKey = getValueForKey(S3_USER_KEY);
		s3SecretKey = getValueForKey(S3_SECRET_KEY);
		s3Account = getValueForKey(S3_ACCOUNT);
		dbPort = getValueForKey(DB_PORT);
		dbDomain = getValueForKey(DB_DOMAIN);
		hibernateConnectionDriverClass = getValueForKey(HIBERNATE_CONNECTION_DRIVER_CLASS);
		hibernateConnectionPoolSize = getValueForKey(HIBERNATE_CONNECTION_POOL_SIZE);
		hibernateDialect = getValueForKey(HIBERNATE_DIALECT);
		hibernateShowSql = getValueForKey(HIBERNATE_SHOW_SQL);
		hibernate_current_session_context_class = getValueForKey(HIBERNATE_CONTEXT_CLASS);
		auditDBUser = getValueForKey(AUDIT_DB_USER);
		auditDBPass = getValueForKey(AUDIT_DB_PASS);
		auditDBSchema = getValueForKey(AUDIT_DB_SCHEMA);
		idaDBUser = getValueForKey(IDA_DB_USER);
		idaDBPass = getValueForKey(IDA_DB_PASS);
		idaDBSchema = getValueForKey(IDA_DB_SCHEMA);
		pmsDBUser = getValueForKey(PMS_DB_USER);
		pmsDBPass = getValueForKey(PMS_DB_PASS);
		pmsDBSchema = getValueForKey(PMS_DB_SCHEMA);
		kmDBUser = getValueForKey(KM_DB_USER);
		kmDBPass = getValueForKey(KM_DB_PASS);
		kmDBSchema = getValueForKey(KM_DB_SCHEMA);
		masterDBUser = getValueForKey(MASTER_DB_USER);
		masterDBPass = getValueForKey(MASTER_DB_PASS);
		masterDBSchema = getValueForKey(MASTER_DB_SCHEMA);
		iamExternalURL = getValueForKey(IAM_EXTERNAL_URL);
		LOGGER.info("keycloakendpoint from config manager::" + iamExternalURL);

		iamRealmID = getValueForKey(IAM_REALM_ID);
		iamUsersToCreate = getValueForKey(IAM_USERS_TO_CREATE);
		iamUsersPassword = getValueForKey(IAM_USERS_PASSWORD);

		admin_client_secret = System.getenv(MOSIP_ADMIN_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_ADMIN_CLIENT_SECRET)
				: System.getenv(MOSIP_ADMIN_CLIENT_SECRET);

		propsKernel.setProperty(MOSIP_ADMIN_CLIENT_SECRET, admin_client_secret);

		authDemoServicePort = System.getenv(AUTH_DEMO_SERVICE_PORT) == null
				? propsKernel.getProperty(AUTH_DEMO_SERVICE_PORT)
				: System.getenv(AUTH_DEMO_SERVICE_PORT);
		propsKernel.setProperty(AUTH_DEMO_SERVICE_PORT, authDemoServicePort);

		reportExpirationInDays = System.getenv(REPORT_EXPIRATION_IN_DAYS) == null
				? propsKernel.getProperty(REPORT_EXPIRATION_IN_DAYS)
				: System.getenv(REPORT_EXPIRATION_IN_DAYS);
		propsKernel.setProperty(REPORT_EXPIRATION_IN_DAYS, reportExpirationInDays);

		authDemoServiceBaseUrl = System.getenv(AUTH_DEMO_SERVICE_BASE_URL) == null
				? propsKernel.getProperty(AUTH_DEMO_SERVICE_BASE_URL)
				: System.getenv(AUTH_DEMO_SERVICE_BASE_URL);
		propsKernel.setProperty(AUTH_DEMO_SERVICE_BASE_URL, authDemoServiceBaseUrl);

		mountPath = System.getenv(MOUNT_PATH) == null ? propsKernel.getProperty(MOUNT_PATH) : System.getenv(MOUNT_PATH);
		propsKernel.setProperty(MOUNT_PATH, mountPath);

		authCertsPath = System.getenv(AUTHCERTS_PATH) == null ? propsKernel.getProperty(AUTHCERTS_PATH)
				: System.getenv(AUTHCERTS_PATH);
		propsKernel.setProperty(AUTHCERTS_PATH, authCertsPath);

		mountPathForScenario = System.getenv(MOUNT_PATH_FOR_SCENARIO) == null
				? propsKernel.getProperty(MOUNT_PATH_FOR_SCENARIO)
				: System.getenv(MOUNT_PATH_FOR_SCENARIO);
		propsKernel.setProperty(MOUNT_PATH_FOR_SCENARIO, mountPathForScenario);

		packetUtilityBaseUrl = System.getenv(PACKET_UTILITY_BASE_URL) == null
				? propsKernel.getProperty(PACKET_UTILITY_BASE_URL)
				: System.getenv(PACKET_UTILITY_BASE_URL);
		propsKernel.setProperty(PACKET_UTILITY_BASE_URL, packetUtilityBaseUrl);

		push_reports_to_s3 = System.getenv(PUSH_TO_S3) == null ? propsKernel.getProperty(PUSH_TO_S3)
				: System.getenv(PUSH_TO_S3);
		propsKernel.setProperty(PUSH_TO_S3, push_reports_to_s3);

		enableDebug = System.getenv(ENABLE_DEBUG) == null ? propsKernel.getProperty(ENABLE_DEBUG)
				: System.getenv(ENABLE_DEBUG);
		propsKernel.setProperty(ENABLE_DEBUG, enableDebug);

		threadCount = System.getenv(THREAD_COUNT) == null ? propsKernel.getProperty(THREAD_COUNT)
				: System.getenv(THREAD_COUNT);
		propsKernel.setProperty(THREAD_COUNT, threadCount);

		langselect = System.getenv(LANG_SELECT) == null ? propsKernel.getProperty(LANG_SELECT)
				: System.getenv(LANG_SELECT);
		propsKernel.setProperty(LANG_SELECT, langselect);

		usePreConfiguredOtp = System.getenv(USEPRECONFIGOTP) == null ? propsKernel.getProperty(USEPRECONFIGOTP)
				: System.getenv(USEPRECONFIGOTP);
		propsKernel.setProperty(USEPRECONFIGOTP, usePreConfiguredOtp);

		preconfiguredOtp = System.getenv(PRECONFIGOTP) == null ? propsKernel.getProperty(PRECONFIGOTP)
				: System.getenv(PRECONFIGOTP);
		propsKernel.setProperty(PRECONFIGOTP, preconfiguredOtp);

		esignet_deployed = System.getenv(ESIGNET_DEPLOYED) == null ? propsKernel.getProperty(ESIGNET_DEPLOYED)
				: System.getenv(ESIGNET_DEPLOYED);
		propsKernel.setProperty(ESIGNET_DEPLOYED, esignet_deployed);
		
		if (System.getenv(ESIGNET_BASE_URL) != null) {
			eSignetbaseurl =System.getenv(ESIGNET_BASE_URL);
		} else {
			eSignetbaseurl = System.getProperty("env.endpoint").replace("-internal", "");
		}
		propsKernel.setProperty(ESIGNET_BASE_URL, eSignetbaseurl);

		toSkippedList = System.getenv(SCENARIOS_TO_BE_SKIPPED) == null
				? propsKernel.getProperty(SCENARIOS_TO_BE_SKIPPED)
				: System.getenv(SCENARIOS_TO_BE_SKIPPED);
		propsKernel.setProperty(SCENARIOS_TO_BE_SKIPPED, toSkippedList);

	}

	public static boolean isInTobeSkippedList(String stringToFind) {
		synchronized (toSkippedList) {
			List<String> toBeSkippedLsit = Arrays.asList(toSkippedList.split(","));
			for (String string : toBeSkippedLsit) {
				if (string.equalsIgnoreCase(stringToFind))
					return true;
			}
		}
		return false;
	}

	public static Boolean IseSignetDeployed() {
		return esignet_deployed.equalsIgnoreCase("yes");
	}

	public static String getAuthDemoServicePort() {
		return authDemoServicePort;
	}

	public static String getReportExpirationInDays() {
		return reportExpirationInDays;
	}

	public static String getAuthDemoServiceBaseUrl() {
		return authDemoServiceBaseUrl;

	}

	public static String getLangselect() {
		return langselect;

	}
	
	public static String getEsignetBaseUrl() {
		return eSignetbaseurl;

	}
	public static String getUsePreConfiguredOtp() {
		return usePreConfiguredOtp;

	}

	public static String getPreConfiguredOtp() {
		return preconfiguredOtp;

	}

	public static String getThreadCount() {
		return threadCount;

	}

	public static String getEnableDebug() {
		return enableDebug;

	}

	public static String getmountPath() {
		return mountPath;
	}

	public static String getmountPathForScenario() {
		return mountPathForScenario;
	}

	public static String getpacketUtilityBaseUrl() {
		return packetUtilityBaseUrl;
	}

	public static String getauthCertsPath() {
		return authCertsPath;
	}

	public static Properties init(String abc) {
		propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/" + "config/Kernel.properties");

		return propsKernel;
	}

	public static String getPmsClientSecret() {
		return pms_client_secret;
	}

	public static String getPmsClientId() {
		return pms_client_id;
	}

	public static String getPmsAppId() {
		return pms_app_id;
	}

	public static String getResidentClientSecret() {
		return resident_client_secret;
	}

	public static String getResidentClientId() {
		return resident_client_id;
	}

	public static String getResidentAppId() {
		return resident_app_id;
	}

	public static String getMPartnerMobileClientId() {
		return mpartner_mobile_client_id;
	}

	public static String getMPartnerMobileClientSecret() {
		return mpartner_mobile_client_secret;
	}

	public static String getAdminClientSecret() {
		return admin_client_secret;
	}

	public static String getAdminClientId() {
		return admin_client_id;
	}

	public static String getAdminAppId() {
		return admin_app_id;
	}

	public static String getIdRepoClientSecret() {
		return idrepo_client_secret;
	}

	public static String getidRepoClientId() {
		return idrepo_client_id;
	}

	public static String getidRepoAppId() {
		return idrepo_app_id;
	}

	public static String getRegprocClientSecret() {
		return regproc_client_secret;
	}

	public static String getRegprocClientId() {
		return regproc_client_id;
	}

	public static String getRegprocAppId() {
		return regproc_app_id;
	}

	public static String getIdaClientSecret() {
		return ida_client_secret;
	}

	public static String getIdaClientId() {
		return ida_client_id;
	}

	public static String getIdaAppId() {
		return ida_app_id;
	}

	public static String getHotListClientSecret() {
		return hotlist_client_secret;
	}

	public static String getHotListClientId() {
		return hotlist_client_id;
	}

	public static String getHotListAppId() {
		return hotlist_app_id;
	}

	public static String getAutomationClientSecret() {
		return automation_client_secret;
	}

	public static String getAutomationClientId() {
		return automation_client_id;
	}

	public static String getAutomationAppId() {
		return automation_app_id;
	}

	public static String getS3Host() {
		return s3Host;
	}

	public static String getS3Region() {
		return s3Region;
	}

	public static String getS3UserKey() {
		return s3UserKey;
	}

	public static String getS3SecretKey() {
		return s3SecretKey;
	}

	public static String getS3Account() {
		return s3Account;
	}

	public static String getPushReportsToS3() {
		return push_reports_to_s3;
	}

	public static String getIdaDbUrl() {
		return "jdbc:postgresql://" + dbDomain + ":" + dbPort + "/mosip_ida";
	}

	public static String getAuditDbUrl() {
		return "jdbc:postgresql://" + dbDomain + ":" + dbPort + "/mosip_audit";
	}

	public static String getDbDriverClass() {
		return hibernateConnectionDriverClass;
	}

	public static String getDbConnectionPoolSize() {
		return hibernateConnectionPoolSize;
	}

	public static String getDbDialect() {
		return hibernateDialect;
	}

	public static String getShowSql() {
		return hibernateShowSql;
	}

	public static String getDbSessionContext() {
		return hibernate_current_session_context_class;
	}

	public static String getAuditDbUser() {
		return auditDBUser;
	}

	public static String getAuditDbPass() {
		LOGGER.info("DB Password from ENV::: " + System.getenv(AUDIT_DB_PASS));
		return auditDBPass;
	}

	public static String getAuditDbSchema() {
		return auditDBSchema;
	}

	public static String getIdaDbUser() {
		return idaDBUser;
	}

	public static String getIdaDbPass() {
		return idaDBPass;
	}

	public static String getIdaDbSchema() {
		return idaDBSchema;
	}

	public static String getPMSDbUrl() {
		return "jdbc:postgresql://" + dbDomain + ":" + dbPort + "/mosip_pms";
	}

	public static String getKMDbUrl() {
		return "jdbc:postgresql://" + dbDomain + ":" + dbPort + "/mosip_keymgr";
	}

	public static String getMASTERDbUrl() {
		return "jdbc:postgresql://" + dbDomain + ":" + dbPort + "/mosip_master";
	}

	public static String getPMSDbUser() {
		return pmsDBUser;
	}

	public static String getPMSDbPass() {
		return pmsDBPass;
	}

	public static String getPMSDbSchema() {
		return pmsDBSchema;
	}

	public static String getKMDbUser() {
		return kmDBUser;
	}

	public static String getKMDbPass() {
		return kmDBPass;
	}

	public static String getKMDbSchema() {
		return kmDBSchema;
	}

	public static String getMasterDbUser() {
		return masterDBUser;
	}

	public static String getMasterDbPass() {
		return masterDBPass;
	}

	public static String getMasterDbSchema() {
		return masterDBSchema;
	}

	public static String getIAMUrl() {
		LOGGER.info("keycloak url from ENV::: " + System.getenv(IAM_EXTERNAL_URL) + "/auth");
		LOGGER.info("keycloak url from Property::: " + System.getProperty(IAM_EXTERNAL_URL) + "/auth");
		LOGGER.info("keycloak url from Config::: " + propsKernel.getProperty(IAM_EXTERNAL_URL) + "/auth");
		LOGGER.info("keycloak url is:::" + iamExternalURL + "/auth");
		return iamExternalURL + "/auth";
	}

	public static String getIAMRealmId() {
		return iamRealmID;
	}

	public static String getIAMUsersToCreate() {
		return iamUsersToCreate;
	}

	public static String getIAMUsersPassword() {
		return iamUsersPassword;
	}

	public static String getRolesForUser(String userId) {
		propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/" + "config/Kernel.properties");
		return propsKernel.getProperty("roles." + userId);
	}

	private static Properties getproperty(String path) {
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

	public static String getAuthDemoServiceUrl() {
		return ConfigManager.getAuthDemoServiceBaseUrl() + ":" + ConfigManager.getAuthDemoServicePort();
	}
}
