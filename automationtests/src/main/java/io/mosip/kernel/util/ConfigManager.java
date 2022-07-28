package io.mosip.kernel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

import io.mosip.testrunner.MosipTestRunner;

public class ConfigManager {

	private static final Logger LOGGER = Logger.getLogger(ConfigManager.class);

	private static String MOSIP_PMS_CLIENT_SECRET = "mosip_pms_client_secret";
	private static String MOSIP_PMS_CLIENT_ID = "mosip_pms_client_id";
	private static String MOSIP_PMS_APP_ID = "mosip_pms_app_id";

	private static String MOSIP_RESIDENT_CLIENT_SECRET = "mosip_resident_client_secret";
	private static String MOSIP_RESIDENT_CLIENT_ID = "mosip_resident_client_id";
	private static String MOSIP_RESIDENT_APP_ID = "mosip_resident_app_id";

	private static String MOSIP_IDREPO_CLIENT_SECRET = "mosip_idrepo_client_secret";
	private static String MOSIP_IDREPO_CLIENT_ID = "mosip_idrepo_client_id";
	private static String MOSIP_IDREPO_APP_ID = "mosip_idrepo_app_id";

	private static String MOSIP_ADMIN_CLIENT_SECRET = "mosip_admin_client_secret";
	private static String MOSIP_ADMIN_CLIENT_ID = "mosip_admin_client_id";
	private static String MOSIP_ADMIN_APP_ID = "mosip_admin_app_id";

	private static String MOSIP_REGPROC_CLIENT_SECRET = "mosip_regproc_client_secret";
	private static String MOSIP_REGPROC_CLIENT_ID = "mosip_regproc_client_id";
	private static String MOSIP_REGPROC_APP_ID = "mosip_regproc_app_id";

	private static String MOSIP_IDA_CLIENT_SECRET = "mosip_ida_client_secret";
	private static String MOSIP_IDA_CLIENT_ID = "mosip_ida_client_id";
	private static String MOSIP_IDA_APP_ID = "mosip_ida_app_id";

	private static String MOSIP_HOTLIST_CLIENT_SECRET = "mosip_hotlist_client_secret";
	private static String MOSIP_HOTLIST_CLIENT_ID = "mosip_hotlist_client_id";
	private static String MOSIP_HOTLIST_APP_ID = "mosip_hotlist_app_id";

	private static String MOSIP_AUTOMATION_CLIENT_SECRET = "mosip_automation_client_secret";
	private static String MOSIP_AUTOMATION_CLIENT_ID = "mosip_automation_client_id";
	private static String MOSIP_AUTOMATION_APP_ID = "mosip_automation_app_id";

	private static String S3_HOST = "s3-host";
	private static String S3_REGION = "s3-region";
	private static String S3_USER_KEY = "s3-user-key";
	private static String S3_SECRET_KEY = "s3-secret-key";
	private static String S3_ACCOUNT = "s3-account";
	private static String PUSH_TO_S3 ="push-reports-to-s3";

	private static String DB_PORT = "db-port";
	private static String DB_DOMAIN = "db-server";
	private static String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static String HIBERNATE_CONNECTION_POOL_SIZE = "hibernate.connection.pool_size";
	private static String HIBERNATE_DIALECT = "hibernate.dialect";
	private static String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static String HIBERNATE_CONTEXT_CLASS = "hibernate.current_session_context_class";

	private static String AUDIT_DB_USER = "db-su-user";
	private static String AUDIT_DB_PASS = "db-su-password";
	private static String AUDIT_DB_SCHEMA = "audit_db_schema";

	private static String IDA_DB_USER = "db-su-user";
	private static String IDA_DB_PASS = "db-su-password";
	private static String IDA_DB_SCHEMA = "ida_db_schema";

	private static String IAM_EXTERNAL_URL = "keycloak-external-url";
	private static String IAM_REALM_ID = "keycloak-realm-id";
	private static String IAM_USERS_TO_CREATE = "iam-users-to-create";
	private static String IAM_USERS_PASSWORD = "iam-users-password";

	private static String pms_client_secret;
	private static String pms_client_id;
	private static String pms_app_id;

	private static String resident_client_secret;
	private static String resident_client_id;
	private static String resident_app_id;

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

	private static String s3_region;
	private static String s3_host;
	private static String s3_user_key;
	private static String s3_account;
	private static String s3_secret_key;
	private static String push_reports_to_s3;

	private static String db_port;
	private static String db_domain;
	private static String hibernate_connection_driver_class;
	private static String hibernate_connection_pool_size;
	private static String hibernate_dialect;
	private static String hibernate_show_sql;
	private static String hibernate_current_session_context_class;

	private static String audit_db_user;
	private static String audit_db_pass;
	private static String audit_db_schema;

	private static String ida_db_user;
	private static String ida_db_pass;
	private static String ida_db_schema;

	private static String iam_external_url;
	private static String iam_realm_id;
	private static String iam_users_to_create;
	private static String iam_users_password;

	public static Properties propsKernel;

	public static void init() {
		propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/" + "config/Kernel.properties");
		pms_client_secret = System.getenv(MOSIP_PMS_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_PMS_CLIENT_SECRET)
				: System.getenv(MOSIP_PMS_CLIENT_SECRET);
		pms_client_id = System.getenv(MOSIP_PMS_CLIENT_ID) == null ? propsKernel.getProperty(MOSIP_PMS_CLIENT_ID)
				: System.getenv(MOSIP_PMS_CLIENT_ID);
		pms_app_id = System.getenv(MOSIP_PMS_APP_ID) == null ? propsKernel.getProperty(MOSIP_PMS_APP_ID)
				: System.getenv(MOSIP_PMS_APP_ID);

		resident_client_secret = System.getenv(MOSIP_RESIDENT_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_RESIDENT_CLIENT_SECRET)
				: System.getenv(MOSIP_RESIDENT_CLIENT_SECRET);
		resident_client_id = System.getenv(MOSIP_RESIDENT_CLIENT_ID) == null
				? propsKernel.getProperty(MOSIP_RESIDENT_CLIENT_ID)
				: System.getenv(MOSIP_RESIDENT_CLIENT_ID);
		resident_app_id = System.getenv(MOSIP_RESIDENT_APP_ID) == null ? propsKernel.getProperty(MOSIP_RESIDENT_APP_ID)
				: System.getenv(MOSIP_RESIDENT_APP_ID);

		idrepo_client_secret = System.getenv(MOSIP_IDREPO_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_IDREPO_CLIENT_SECRET)
				: System.getenv(MOSIP_IDREPO_CLIENT_SECRET);
		idrepo_client_id = System.getenv(MOSIP_IDREPO_CLIENT_ID) == null
				? propsKernel.getProperty(MOSIP_IDREPO_CLIENT_ID)
				: System.getenv(MOSIP_IDREPO_CLIENT_ID);
		idrepo_app_id = System.getenv(MOSIP_IDREPO_APP_ID) == null ? propsKernel.getProperty(MOSIP_IDREPO_APP_ID)
				: System.getenv(MOSIP_IDREPO_APP_ID);

		admin_client_secret = System.getenv(MOSIP_ADMIN_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_ADMIN_CLIENT_SECRET)
				: System.getenv(MOSIP_ADMIN_CLIENT_SECRET);
		admin_client_id = System.getenv(MOSIP_ADMIN_CLIENT_ID) == null ? propsKernel.getProperty(MOSIP_ADMIN_CLIENT_ID)
				: System.getenv(MOSIP_ADMIN_CLIENT_ID);
		admin_app_id = System.getenv(MOSIP_ADMIN_APP_ID) == null ? propsKernel.getProperty(MOSIP_ADMIN_APP_ID)
				: System.getenv(MOSIP_ADMIN_APP_ID);

		regproc_client_secret = System.getenv(MOSIP_REGPROC_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_REGPROC_CLIENT_SECRET)
				: System.getenv(MOSIP_REGPROC_CLIENT_SECRET);
		regproc_client_id = System.getenv(MOSIP_REGPROC_CLIENT_ID) == null
				? propsKernel.getProperty(MOSIP_REGPROC_CLIENT_ID)
				: System.getenv(MOSIP_REGPROC_CLIENT_ID);
		regproc_app_id = System.getenv(MOSIP_REGPROC_APP_ID) == null ? propsKernel.getProperty(MOSIP_REGPROC_APP_ID)
				: System.getenv(MOSIP_REGPROC_APP_ID);

		ida_client_secret = System.getenv(MOSIP_IDA_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_IDA_CLIENT_SECRET)
				: System.getenv(MOSIP_IDA_CLIENT_SECRET);
		ida_client_id = System.getenv(MOSIP_IDA_CLIENT_ID) == null ? propsKernel.getProperty(MOSIP_IDA_CLIENT_ID)
				: System.getenv(MOSIP_IDA_CLIENT_ID);
		ida_app_id = System.getenv(MOSIP_IDA_APP_ID) == null ? propsKernel.getProperty(MOSIP_IDA_APP_ID)
				: System.getenv(MOSIP_IDA_APP_ID);

		hotlist_client_secret = System.getenv(MOSIP_HOTLIST_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_HOTLIST_CLIENT_SECRET)
				: System.getenv(MOSIP_HOTLIST_CLIENT_SECRET);
		hotlist_client_id = System.getenv(MOSIP_HOTLIST_CLIENT_ID) == null
				? propsKernel.getProperty(MOSIP_HOTLIST_CLIENT_ID)
				: System.getenv(MOSIP_HOTLIST_CLIENT_ID);
		hotlist_app_id = System.getenv(MOSIP_HOTLIST_APP_ID) == null ? propsKernel.getProperty(MOSIP_HOTLIST_APP_ID)
				: System.getenv(MOSIP_HOTLIST_APP_ID);

		automation_client_secret = System.getenv(MOSIP_AUTOMATION_CLIENT_SECRET) == null
				? propsKernel.getProperty(MOSIP_AUTOMATION_CLIENT_SECRET)
				: System.getenv(MOSIP_AUTOMATION_CLIENT_SECRET);
		automation_client_id = System.getenv(MOSIP_AUTOMATION_CLIENT_ID) == null
				? propsKernel.getProperty(MOSIP_AUTOMATION_CLIENT_ID)
				: System.getenv(MOSIP_AUTOMATION_CLIENT_ID);
		automation_app_id = System.getenv(MOSIP_AUTOMATION_APP_ID) == null
				? propsKernel.getProperty(MOSIP_AUTOMATION_APP_ID)
				: System.getenv(MOSIP_AUTOMATION_APP_ID);

		s3_host = System.getenv(S3_HOST) == null ? propsKernel.getProperty(S3_HOST) : System.getenv(S3_HOST);
		s3_region = System.getenv(S3_REGION) == null ? propsKernel.getProperty(S3_REGION) : System.getenv(S3_REGION);
		s3_user_key = System.getenv(S3_USER_KEY) == null ? propsKernel.getProperty(S3_USER_KEY)
				: System.getenv(S3_USER_KEY);
		s3_secret_key = System.getenv(S3_SECRET_KEY) == null ? propsKernel.getProperty(S3_SECRET_KEY)
				: System.getenv(S3_SECRET_KEY);
		s3_account = System.getenv(S3_ACCOUNT) == null ? propsKernel.getProperty(S3_ACCOUNT)
				: System.getenv(S3_ACCOUNT);
		push_reports_to_s3 = System.getenv(PUSH_TO_S3) == null ? propsKernel.getProperty(PUSH_TO_S3)
				: System.getenv(PUSH_TO_S3);

		db_port = System.getenv(DB_PORT) == null ? propsKernel.getProperty(DB_PORT) : System.getenv(DB_PORT);
		db_domain = System.getenv(DB_DOMAIN) == null ? propsKernel.getProperty(DB_DOMAIN) : System.getenv(DB_DOMAIN);
		hibernate_connection_driver_class = System.getenv(HIBERNATE_CONNECTION_DRIVER_CLASS) == null
				? propsKernel.getProperty(HIBERNATE_CONNECTION_DRIVER_CLASS)
				: System.getenv(HIBERNATE_CONNECTION_DRIVER_CLASS);
		hibernate_connection_pool_size = System.getenv(HIBERNATE_CONNECTION_POOL_SIZE) == null
				? propsKernel.getProperty(HIBERNATE_CONNECTION_POOL_SIZE)
				: System.getenv(HIBERNATE_CONNECTION_POOL_SIZE);
		hibernate_dialect = System.getenv(HIBERNATE_DIALECT) == null ? propsKernel.getProperty(HIBERNATE_DIALECT)
				: System.getenv(HIBERNATE_DIALECT);
		hibernate_show_sql = System.getenv(HIBERNATE_SHOW_SQL) == null ? propsKernel.getProperty(HIBERNATE_SHOW_SQL)
				: System.getenv(HIBERNATE_SHOW_SQL);
		hibernate_current_session_context_class = System.getenv(HIBERNATE_CONTEXT_CLASS) == null
				? propsKernel.getProperty(HIBERNATE_CONTEXT_CLASS)
				: System.getenv(HIBERNATE_CONTEXT_CLASS);

		audit_db_user = System.getenv(AUDIT_DB_USER) == null ? propsKernel.getProperty(AUDIT_DB_USER)
				: System.getenv(AUDIT_DB_USER);
		audit_db_pass = System.getenv(AUDIT_DB_PASS) == null ? propsKernel.getProperty(AUDIT_DB_PASS)
				: System.getenv(AUDIT_DB_PASS);
		audit_db_schema = System.getenv(AUDIT_DB_SCHEMA) == null ? propsKernel.getProperty(AUDIT_DB_SCHEMA)
				: System.getenv(AUDIT_DB_SCHEMA);

		ida_db_user = System.getenv(IDA_DB_USER) == null ? propsKernel.getProperty(IDA_DB_USER)
				: System.getenv(IDA_DB_USER);
		ida_db_pass = System.getenv(IDA_DB_PASS) == null ? propsKernel.getProperty(IDA_DB_PASS)
				: System.getenv(IDA_DB_PASS);
		ida_db_schema = System.getenv(IDA_DB_SCHEMA) == null ? propsKernel.getProperty(IDA_DB_SCHEMA)
				: System.getenv(IDA_DB_SCHEMA);

		iam_external_url = System.getenv(IAM_EXTERNAL_URL) == null ? propsKernel.getProperty(IAM_EXTERNAL_URL)
				: System.getenv(IAM_EXTERNAL_URL);
		
		System.out.println("keycloakendpoint from config manager::"+ iam_external_url);
		iam_realm_id = System.getenv(IAM_REALM_ID) == null ? propsKernel.getProperty(IAM_REALM_ID)
				: System.getenv(IAM_REALM_ID);
		iam_users_to_create = System.getenv(IAM_USERS_TO_CREATE) == null ? propsKernel.getProperty(IAM_USERS_TO_CREATE)
				: System.getenv(IAM_USERS_TO_CREATE);
		iam_users_password = System.getenv(IAM_USERS_PASSWORD) == null ? propsKernel.getProperty(IAM_USERS_PASSWORD)
				: System.getenv(IAM_USERS_PASSWORD);
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
		return s3_host;
	}

	public static String getS3Region() {
		return s3_region;
	}

	public static String getS3UserKey() {
		return s3_user_key;
	}

	public static String getS3SecretKey() {
		return s3_secret_key;
	}

	public static String getS3Account() {
		return s3_account;
	}
	
	public static String getPushReportsToS3() {
		return push_reports_to_s3;
	}

	public static String getIdaDbUrl() {
		return "jdbc:postgresql://" + db_domain + ":" + db_port + "/mosip_ida";
	}

	public static String getAuditDbUrl() {
		return "jdbc:postgresql://" + db_domain + ":" + db_port + "/mosip_audit";
	}

	public static String getDbDriverClass() {
		return hibernate_connection_driver_class;
	}

	public static String getDbConnectionPoolSize() {
		return hibernate_connection_pool_size;
	}

	public static String getDbDialect() {
		return hibernate_dialect;
	}

	public static String getShowSql() {
		return hibernate_show_sql;
	}

	public static String getDbSessionContext() {
		return hibernate_current_session_context_class;
	}

	public static String getAuditDbUser() {
		return audit_db_user;
	}

	public static String getAuditDbPass() {
		return audit_db_pass;
	}

	public static String getAuditDbSchema() {
		return audit_db_schema;
	}

	public static String getIdaDbUser() {
		return ida_db_user;
	}

	public static String getIdaDbPass() {
		return ida_db_pass;
	}

	public static String getIdaDbSchema() {
		return ida_db_schema;
	}

	// from docker env getting only host url
	public static String getIAMUrl() {
		
		System.out.println("keycloak url is:::"+iam_external_url + "/auth");
		return iam_external_url + "/auth";
	}

	public static String getIAMRealmId() {
		return iam_realm_id;
	}

	public static String getIAMUsersToCreate() {
		return iam_users_to_create;
	}

	public static String getIAMUsersPassword() {
		return iam_users_password;
	}

	public static String getRolesForUser(String userId) {
		propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/" + "config/Kernel.properties");
		return propsKernel.getProperty("roles." + userId);
	}

	private static Properties getproperty(String path) {
		Properties prop = new Properties();
		try {
			File file = new File(path);
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.error("Exception " + e.getMessage());
		}
		return prop;
	}
}
