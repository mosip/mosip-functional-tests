package io.mosip.testrig.apirig.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class ConfigManager {

	private static final Logger LOGGER = Logger.getLogger(ConfigManager.class);

	private static String MOSIP_PMS_CLIENT_SECRET = "mosip_pms_client_secret";
	private static String MOSIP_PARTNER_CLIENT_SECRET = "mosip_partner_client_secret";
	private static String MOSIP_PMS_CLIENT_ID = "mosip_pms_client_id";
	private static String MOSIP_PMS_APP_ID = "mosip_pms_app_id";
	private static String MOSIP_PARTNER_CLIENT_ID = "mosip_partner_client_id";

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

	private static String MOSIP_REGPROC_CLIENT_SECRET = "mosip_regproc_client_secret";
	private static String MOSIP_REGPROC_CLIENT_ID = "mosip_regproc_client_id";
	private static String MOSIP_REGPROC_APP_ID = "mosip_regprocclient_app_id";

	private static String MOSIP_IDA_CLIENT_SECRET = "mosip_ida_client_secret";
	private static String MOSIP_IDA_CLIENT_ID = "mosip_ida_client_id";
	private static String MOSIP_IDA_APP_ID = "mosip_ida_app_id";

	private static String MOSIP_HOTLIST_CLIENT_SECRET = "mosip_hotlist_client_secret";
	private static String MOSIP_HOTLIST_CLIENT_ID = "mosip_hotlist_client_id";
	private static String MOSIP_HOTLIST_APP_ID = "mosip_hotlist_app_id";

	private static String MOSIP_AUTOMATION_CLIENT_SECRET = "mosip_testrig_client_secret";
	private static String MOSIP_AUTOMATION_CLIENT_ID = "mosip_testrig_client_id";
	private static String MOSIP_AUTOMATION_APP_ID = "mosip_automation_app_id";

	public static String DB_PASSWORD_KEY = "postgres-password";
	public static String DB_USER_KEY = "db-su-user";

	private static String S3_HOST = "s3-host";
	private static String S3_REGION = "s3-region";
	private static String S3_USER_KEY = "s3-user-key";
	private static String S3_SECRET_KEY = "s3-user-secret";
	private static String S3_ACCOUNT = "s3-account";
	private static String S3_ACCOUNT_FOR_PERSONA_DATA = "s3-account-for-persona-data";
	private static String PUSH_TO_S3 = "push-reports-to-s3";
	private static String ENABLE_DEBUG = "enableDebug";
	private static String REPORT_IGNORED_TEST_CASES = "reportIgnoredTestCases";
	private static String THREAD_COUNT = "threadCount";
	private static String LANG_SELECT = "langselect";

	private static String USEPRECONFIGOTP = "usePreConfiguredOtp";
	private static String ESIGNET_BASE_URL = "eSignetbaseurl";
	private static String ESIGNET_ACTUATOR_PROPERTY_SECTION = "esignetActuatorPropertySection";
	private static String INJI_CERTIFY_BASE_URL = "injiCertifyBaseURL";

	private static String ESIGNET_MOCK_BASE_URL = "esignetMockBaseURL";
	private static String SUNBIRD_BASE_URL = "sunBirdBaseURL";

	private static String PRECONFIGOTP = "preconfiguredOtp";
	private static String DB_PORT = "db-port";
	private static String DB_DOMAIN = "db-server";
	private static String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static String HIBERNATE_CONNECTION_POOL_SIZE = "hibernate.connection.pool_size";
	private static String HIBERNATE_DIALECT = "hibernate.dialect";
	private static String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static String HIBERNATE_CONTEXT_CLASS = "hibernate.current_session_context_class";

	private static String AUDIT_DB_USER = DB_USER_KEY;
	private static String AUDIT_DB_PASS = DB_PASSWORD_KEY;
	private static String AUDIT_DB_SCHEMA = "audit_db_schema";

	private static String IDA_DB_USER = DB_USER_KEY;
	private static String IDA_DB_PASS = DB_PASSWORD_KEY;
	private static String IDA_DB_SCHEMA = "ida_db_schema";

	private static String PMS_DB_USER = DB_USER_KEY;
	private static String PMS_DB_PASS = DB_PASSWORD_KEY;
	private static String PMS_DB_SCHEMA = "pms_db_schema";

	private static String KM_DB_USER = DB_USER_KEY;
	private static String KM_DB_PASS = DB_PASSWORD_KEY;
	private static String KM_DB_SCHEMA = "km_db_schema";

	private static String MASTER_DB_USER = DB_USER_KEY;
	private static String MASTER_DB_PASS = DB_PASSWORD_KEY;
	private static String MASTER_DB_SCHEMA = "master_db_schema";

	private static String IAM_EXTERNAL_URL = "keycloak-external-url";
	private static String IAM_REALM_ID = "keycloak-realm-id";
	private static String IAM_USERS_TO_CREATE = "iam-users-to-create";
	private static String IAM_USERS_PASSWORD = "iam-users-password";
	
	private static String SLACK_WEBHOOK_URL = "slack-webhook-url";

	private static String USE_EXTERNAL_SCENARIO_SHEET = "useExternalScenarioSheet";
	private static String useExternalScenario_sheet;

	private static String MOUNT_PATH = "mountPath";
	private static String AUTHCERTS_PATH = "authCertsPath";
	private static String MOUNT_PATH_FOR_SCENARIO = "mountPathForScenario";
	private static String MOCK_NOTIFICATION_CHANNEL = "mockNotificationChannel";
	
	private static String SERVER_ERRORS_TO_MONITOR = "serverErrorsToMonitor";

	private static String PACKET_UTILITY_BASE_URL = "packetUtilityBaseUrl";

	private static String REPORT_EXPIRATION_IN_DAYS = "reportExpirationInDays";

	private static String SCENARIOS_TO_BE_SKIPPED = "scenariosToSkip";
	private static String SCENARIOS_TO_BE_EXECUTED = "scenariosToExecute";

	private static String SERVICES_NOT_DEPLOYED = "servicesNotDeployed";

	private static String ADMIN_USER_NAME = "admin_userName";

	private static String PARTNER_URL_SUFFIX = "partnerUrlSuffix";
	
	private static String MOSIP_COMPONENTS_BASE_URLS = "mosip_components_base_urls";
	private static Map<String, String> mosip_components_base_urls = new HashMap<>();

	private static String partnerUrlSuffix;

	private static String serviceNotDeployedList;

	private static String toSkippedList;
	private static String toExecuteList;
	private static String userAdminName;

	private static String pms_client_secret;
	private static String partner_client_secret;
	private static String pms_client_id;
	private static String pms_app_id;
	private static String partner_client_id;

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

	private static String regprocessor_client_secret;
	private static String regprocessor_client_id;
	private static String regprocessor_app_id;

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
	private static String s3AccountForPersonaData;
	private static String s3SecretKey;
	private static String push_reports_to_s3;
	private static String enableDebug;
	private static String reportIgnoredTestCases;
	private static String threadCount;
	private static String langselect;
	private static String usePreConfiguredOtp;
	private static String preconfiguredOtp;
	private static String eSignetbaseurl;
	private static String esignetMockBaseURL;
	private static String sunBirdBaseURL;
	private static String esignetActuatorPropertySection;
	private static String injiCertifyBaseURL;

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
	private static String getSlackWebHookUrl;
	
	private static String iamUsersToCreate;
	private static String iamUsersPassword;
	
	private static String serverErrorsToMonitor;

	private static String mountPath;
	private static String authCertsPath;
	private static String mockNotificationChannel;
	private static String mountPathForScenario;
	private static String packetUtilityBaseUrl;
	public static Properties propsKernel;
	private static String reportExpirationInDays;

	public static boolean getUsePreConfiguredOtp;

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
		propsKernel = getproperty(MosipTestRunner.getGlobalResourcePath() + "/" + "config/Kernel.properties");

		pms_client_secret = getValueForKey(MOSIP_PMS_CLIENT_SECRET);
		partner_client_secret = getValueForKey(MOSIP_PARTNER_CLIENT_SECRET);
		pms_client_id = getValueForKey(MOSIP_PMS_CLIENT_ID);
		partner_client_id = getValueForKey(MOSIP_PARTNER_CLIENT_ID);

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

		regprocessor_client_secret = getValueForKey(MOSIP_REGPROC_CLIENT_SECRET);
		regprocessor_client_id = getValueForKey(MOSIP_REGPROC_CLIENT_ID);
		regprocessor_app_id = getValueForKey(MOSIP_REGPROC_APP_ID);

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
		s3AccountForPersonaData = getValueForKey(S3_ACCOUNT_FOR_PERSONA_DATA);
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
		getSlackWebHookUrl = getValueForKey(SLACK_WEBHOOK_URL);
		iamUsersToCreate = getValueForKey(IAM_USERS_TO_CREATE);
		iamUsersPassword = getValueForKey(IAM_USERS_PASSWORD);

		admin_client_secret = System.getenv(MOSIP_ADMIN_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_ADMIN_CLIENT_SECRET)
				: System.getenv(MOSIP_ADMIN_CLIENT_SECRET);

		propsKernel.setProperty(MOSIP_ADMIN_CLIENT_SECRET, admin_client_secret);
		
		serverErrorsToMonitor = System.getenv(SERVER_ERRORS_TO_MONITOR) == null
				? propsKernel.getProperty(SERVER_ERRORS_TO_MONITOR)
				: propsKernel.getProperty(SERVER_ERRORS_TO_MONITOR) + "," + System.getenv(SERVER_ERRORS_TO_MONITOR);
		propsKernel.setProperty(SERVER_ERRORS_TO_MONITOR, serverErrorsToMonitor);

		reportExpirationInDays = System.getenv(REPORT_EXPIRATION_IN_DAYS) == null
				? propsKernel.getProperty(REPORT_EXPIRATION_IN_DAYS)
				: System.getenv(REPORT_EXPIRATION_IN_DAYS);
		propsKernel.setProperty(REPORT_EXPIRATION_IN_DAYS, reportExpirationInDays);

		mountPath = System.getenv(MOUNT_PATH) == null ? propsKernel.getProperty(MOUNT_PATH) : System.getenv(MOUNT_PATH);
		propsKernel.setProperty(MOUNT_PATH, mountPath);

		mockNotificationChannel = System.getenv(MOCK_NOTIFICATION_CHANNEL) == null
				? propsKernel.getProperty(MOCK_NOTIFICATION_CHANNEL)
				: System.getenv(MOCK_NOTIFICATION_CHANNEL);
		propsKernel.setProperty(MOCK_NOTIFICATION_CHANNEL, mockNotificationChannel);

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

		reportIgnoredTestCases = System.getenv(REPORT_IGNORED_TEST_CASES) == null
				? propsKernel.getProperty(REPORT_IGNORED_TEST_CASES)
				: System.getenv(REPORT_IGNORED_TEST_CASES);
		propsKernel.setProperty(REPORT_IGNORED_TEST_CASES, reportIgnoredTestCases);

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

		useExternalScenario_sheet = System.getenv(USE_EXTERNAL_SCENARIO_SHEET) == null
				? propsKernel.getProperty(USE_EXTERNAL_SCENARIO_SHEET)
				: System.getenv(USE_EXTERNAL_SCENARIO_SHEET);
		propsKernel.setProperty(USE_EXTERNAL_SCENARIO_SHEET, useExternalScenario_sheet);
		
		esignetActuatorPropertySection = System.getenv(ESIGNET_ACTUATOR_PROPERTY_SECTION) == null
		? propsKernel.getProperty(ESIGNET_ACTUATOR_PROPERTY_SECTION)
		: System.getenv(ESIGNET_ACTUATOR_PROPERTY_SECTION);
		propsKernel.setProperty(ESIGNET_ACTUATOR_PROPERTY_SECTION, esignetActuatorPropertySection);

		if (System.getenv(ESIGNET_BASE_URL) != null) {
			eSignetbaseurl = System.getenv(ESIGNET_BASE_URL);
		} else {
			eSignetbaseurl = System.getProperty("env.endpoint").replace("api-internal", "esignet");
		}
		propsKernel.setProperty(ESIGNET_BASE_URL, eSignetbaseurl);
		
		if (System.getenv(INJI_CERTIFY_BASE_URL) != null) {
			injiCertifyBaseURL = System.getenv(INJI_CERTIFY_BASE_URL);
		} else {
			injiCertifyBaseURL = System.getProperty("env.endpoint").replace("api-internal", "injicertify");
		}
		propsKernel.setProperty(INJI_CERTIFY_BASE_URL, injiCertifyBaseURL);

		esignetMockBaseURL = System.getenv(ESIGNET_MOCK_BASE_URL) == null
				? propsKernel.getProperty(ESIGNET_MOCK_BASE_URL)
				: System.getenv(ESIGNET_MOCK_BASE_URL);
		propsKernel.setProperty(ESIGNET_MOCK_BASE_URL, esignetMockBaseURL);
		
		sunBirdBaseURL = System.getenv(SUNBIRD_BASE_URL) == null
				? propsKernel.getProperty(SUNBIRD_BASE_URL)
				: System.getenv(SUNBIRD_BASE_URL);
		propsKernel.setProperty(SUNBIRD_BASE_URL, sunBirdBaseURL);

		serviceNotDeployedList = System.getenv(SERVICES_NOT_DEPLOYED) == null
				? propsKernel.getProperty(SERVICES_NOT_DEPLOYED)
				: System.getenv(SERVICES_NOT_DEPLOYED);
		propsKernel.setProperty(SERVICES_NOT_DEPLOYED, serviceNotDeployedList);

		toSkippedList = System.getenv(SCENARIOS_TO_BE_SKIPPED) == null
				? propsKernel.getProperty(SCENARIOS_TO_BE_SKIPPED)
				: System.getenv(SCENARIOS_TO_BE_SKIPPED);
		propsKernel.setProperty(SCENARIOS_TO_BE_SKIPPED, toSkippedList);

		toExecuteList = System.getenv(SCENARIOS_TO_BE_EXECUTED) == null
				? propsKernel.getProperty(SCENARIOS_TO_BE_EXECUTED)
				: System.getenv(SCENARIOS_TO_BE_EXECUTED);
		propsKernel.setProperty(SCENARIOS_TO_BE_EXECUTED, toExecuteList);

		partnerUrlSuffix = System.getenv(PARTNER_URL_SUFFIX) == null ? propsKernel.getProperty(PARTNER_URL_SUFFIX)
				: System.getenv(PARTNER_URL_SUFFIX);
		propsKernel.setProperty(PARTNER_URL_SUFFIX, partnerUrlSuffix);

		userAdminName = System.getenv(ADMIN_USER_NAME) == null ? propsKernel.getProperty(ADMIN_USER_NAME)
				: System.getenv(ADMIN_USER_NAME);
		propsKernel.setProperty(ADMIN_USER_NAME, userAdminName);
		
		String components_base_urls = System.getenv(MOSIP_COMPONENTS_BASE_URLS) == null
				? propsKernel.getProperty(MOSIP_COMPONENTS_BASE_URLS)
				: System.getenv(MOSIP_COMPONENTS_BASE_URLS);
		loadComponentBaseURLs(components_base_urls);
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

	public static boolean isInServiceNotDeployedList(String stringToFind) {
		synchronized (serviceNotDeployedList) {
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
		}
		return false;
	}

	public static boolean isInTobeSkippedList(String stringToFind) {
		synchronized (toSkippedList) {
			List<String> toBeSkippedLsit = Arrays.asList(toSkippedList.split(","));
			if (ConfigManager.IsDebugEnabled())
				LOGGER.info("toSkippedList:  " + toSkippedList + ", toBeSkippedLsit : " + toBeSkippedLsit
						+ ", stringToFind : " + stringToFind);
			for (String string : toBeSkippedLsit) {
				if (string.equalsIgnoreCase(stringToFind))
					return true;
			}
		}
		return false;
	}

	public static boolean isInTobeExecuteList(String stringToFind) {
		synchronized (toExecuteList) {
			// If there are no specific execution list is provided , execute all scenarios
			if (toExecuteList != null && toExecuteList.isEmpty())
				return true;

			List<String> toBeExecuteList = Arrays.asList(toExecuteList.split(","));
			if (ConfigManager.IsDebugEnabled())
				LOGGER.info("toExecuteList:  " + toExecuteList + ", toBeExecuteList : " + toBeExecuteList
						+ ", stringToFind : " + stringToFind);
			for (String string : toBeExecuteList) {
				if (string.trim().equalsIgnoreCase(stringToFind))
					return true;
			}
		}
		return false;
	}
	
	public static String getEsignetActuatorPropertySection() {
		return esignetActuatorPropertySection;

	}
	
	public static String getComponentBaseURL(String component) {
		return mosip_components_base_urls.get(component);
	}
	
	public static String getServerErrorsToMonitor() {
		return serverErrorsToMonitor;
	}

	public static String getUserAdminName() {
		return userAdminName;

	}

	public static String getPartnerUrlSuffix() {
		return partnerUrlSuffix;
	}

	public static Boolean useExternalScenarioSheet() {
		return useExternalScenario_sheet.equalsIgnoreCase("yes");
	}

	public static Boolean IsDebugEnabled() {
		return enableDebug.equalsIgnoreCase("yes");
	}

	public static Boolean reportIgnoredTestCases() {
		return reportIgnoredTestCases.equalsIgnoreCase("yes");
	}

	public static String getReportExpirationInDays() {
		return reportExpirationInDays;
	}

	public static int getLangselect() {
		return Integer.parseInt(langselect);

	}

	public static String getEsignetBaseUrl() {
		return eSignetbaseurl;

	}
	
	public static String getInjiCertifyBaseUrl() {
		return injiCertifyBaseURL;

	}

	public static String getEsignetMockBaseURL() {
		return esignetMockBaseURL;

	}
	
	public static String getSunBirdBaseURL() {
		return sunBirdBaseURL;

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
	
	public static String getMockNotificationChannel() {
		return mockNotificationChannel;
	}

	public static String getauthCertsPath() {
		return authCertsPath;
	}

	public static Properties init(String abc) {
		propsKernel = getproperty(MosipTestRunner.getGlobalResourcePath() + "/" + "config/Kernel.properties");

		return propsKernel;
	}

	public static String getPmsClientSecret() {
		return pms_client_secret;
	}

	public static String getPartnerClientSecret() {
		return partner_client_secret;
	}

	public static String getPmsClientId() {
		return pms_client_id;
	}

	public static String getPartnerClientId() {
		return partner_client_id;
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

	public static String getRegprocessorClientSecret() {
		return regprocessor_client_secret;
	}

	public static String getRegprocessorClientId() {
		return regprocessor_client_id;
	}

	public static String getRegprocessorAppId() {
		return regprocessor_app_id;
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

	public static String getS3AccountForPersonaData() {
		return s3AccountForPersonaData;
	}

	public static String getPushReportsToS3() {
		return push_reports_to_s3;
	}
	
	public static String getTargetEnvName() {
		return dbDomain;
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
	
	public static String getSlackWebHookUrl() {
		return getSlackWebHookUrl;
	}

	public static String getRolesForUser(String userId) {
		propsKernel = getproperty(MosipTestRunner.getGlobalResourcePath() + "/" + "config/Kernel.properties");
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
}