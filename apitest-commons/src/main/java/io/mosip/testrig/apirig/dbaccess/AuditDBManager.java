package io.mosip.testrig.apirig.dbaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.testng.Assert;

import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;

public class AuditDBManager extends AdminTestUtil {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(AuditDBManager.class);
	public static String env = System.getProperty("env.user");
	public static Map<String, SessionFactory> sessionFactoryMapS = Collections
			.synchronizedMap(new HashMap<String, SessionFactory>());

	/**
	 * Execute query to get generated otp value
	 * 
	 * @param query
	 * @param moduleName
	 * @return otp record
	 */

	public static Map<String, Object> executeQueryAndGetRecord(String moduleName, String query) {
		Session session = null;
		Map<String, Object> record = new HashMap<>();
		try {
			session = getDataBaseConnection(moduleName);
			if (session != null) {
				session.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						Statement statement = connection.createStatement();
						try {
							ResultSet rs = statement.executeQuery(query);
							ResultSetMetaData md = rs.getMetaData();
							int columns = md.getColumnCount();
							while (rs.next()) {
								for (int i = 1; i <= columns; i++) {
									record.put(md.getColumnName(i), rs.getObject(i));
								}
							}
						} finally {
							statement.close();
						}
					}
				});
			}
		} catch (NullPointerException e) {
			DBCONNECTION_LOGGER.error("Exception occured " + e.getMessage());
		} finally {
			closeDataBaseConnection(session);
		}
		return record;

	}

	public static void executeQueryAndDeleteRecord(String moduleName, String deleteQuery) {
		Session session = null;
		try {
			session = getDataBaseConnection(moduleName);
			if (session != null) {
				session.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						Statement statement = connection.createStatement();
						try {
							int rs = statement.executeUpdate(deleteQuery);
							if (rs > 0) {
								DBCONNECTION_LOGGER.info("deleted successfully!");
							}
						} finally {
							statement.close();
						}
					}
				});
			}
		} catch (NullPointerException e) {
			DBCONNECTION_LOGGER.error("Exception occured " + e.getMessage());
		} finally {
			closeDataBaseConnection(session);
		}
	}

	private static Session getDataBaseConnection(String dbName) {
		Session session = null;
		SessionFactory sessionFactory = sessionFactoryMapS.get(dbName);
		if (sessionFactory == null) {
			try {
				sessionFactory = getDataBaseConnectionSessionFactory(dbName);
				sessionFactoryMapS.put(dbName, sessionFactory);
			} catch (HibernateException e) {
				DBCONNECTION_LOGGER.error("Exception in Database Connection with following message: " + e.getMessage());
			} catch (NullPointerException e) {
				Assert.assertTrue(false, "Exception in getting the SessionFactory for DB Schema : " + dbName);
			}
		}
		if (sessionFactory != null) {
			session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			DBCONNECTION_LOGGER.info("Session begined with Schema : " + dbName);
		}
		return session;
	}

	private static SessionFactory getDataBaseConnectionSessionFactory(String dbName) {
		SessionFactory factory = null;
		String dbschema = ConfigManager.getAuditDbSchema();

		if (dbName.equalsIgnoreCase("partner"))
			dbschema = ConfigManager.getIdaDbSchema();

		if (dbName.equalsIgnoreCase("master"))
			dbschema = ConfigManager.getMasterDbSchema();

		try {
			Configuration config = new Configuration();
			config.setProperty("hibernate.connection.driver_class", ConfigManager.getproperty("driver_class"));
			config.setProperty("hibernate.connection.url", "jdbc:" + ConfigManager.getproperty("postgresqlUser") + "://"
					+ ConfigManager.getDbServer() + ":" + ConfigManager.getDbPort() + "/mosip_" + dbschema);
			config.setProperty("hibernate.connection.username", ConfigManager.getAuditDbUser());
			config.setProperty("hibernate.connection.password", ConfigManager.getAuditDbPass());
			config.setProperty("hibernate.default_schema", ConfigManager.getproperty(dbName + "_default_schema"));
			config.setProperty("hibernate.connection.pool_size", ConfigManager.getproperty("pool_size"));
			config.setProperty("hibernate.dialect", ConfigManager.getproperty("dialect"));
			config.setProperty("hibernate.show_sql", ConfigManager.getproperty("show_sql"));
			config.setProperty("hibernate.current_session_context_class",
					ConfigManager.getproperty("current_session_context_class"));
			factory = config.buildSessionFactory();
		} catch (HibernateException e) {
			DBCONNECTION_LOGGER.error("Exception in Database Connection with following message: " + e.getMessage());
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the SessionFactory for DB Schema : " + dbschema);
		}
		return factory;
	}

	public static void closeDataBaseConnection(Session session) {
		if (session != null) {
			DBCONNECTION_LOGGER.info("Session closed");
			session.close();
		}
	}

}
