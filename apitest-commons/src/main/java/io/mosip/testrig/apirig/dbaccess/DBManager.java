package io.mosip.testrig.apirig.dbaccess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.jdbc.Work;
import org.testng.Assert;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
public class DBManager {
	private static Logger logger = Logger.getLogger(DBManager.class);
	public static String env = System.getProperty("env.user");
	public static Map<String, SessionFactory> sessionFactoryMapS = Collections
			.synchronizedMap(new HashMap<String, SessionFactory>());
	
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

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
			logger.error("Exception occured " + e.getMessage());
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
								logger.info("deleted successfully!");
							}
						} finally {
							statement.close();
						}
					}
				});
			}
		} catch (NullPointerException e) {
			logger.error("Exception occured " + e.getMessage());
		} finally {
			closeDataBaseConnection(session);
		}
	}
	
	public static void executeDBWithQueries(String dbURL, String dbUser, String dbPassword, String dbSchema,
			String dbQueries) throws AdminTestException {
		Session session = null;
		try {
			session = getDataBaseConnection(dbURL, dbUser, dbPassword, dbSchema);
			if (session != null)
				executeQueryAndInsertData(session, dbQueries);
			else
				throw new AdminTestException("Error:: While getting DB connection");
		} catch (Exception e) {
			logger.error("Error:: While executing DB Quiries." + e.getMessage());
			throw new AdminTestException(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public static void executeQueryAndInsertData(Session session, String deleteQuery) throws AdminTestException {
		try {
			if (session != null) {
				session.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						Statement statement = connection.createStatement();
						try {
							int rs = statement.executeUpdate(deleteQuery);
							if (rs > 0) {
								logger.info("Inserted Data successfully!");
							}
						} finally {
							statement.close();
						}
					}
				});
			}
		} catch (Exception e) {
			logger.error("Exception occured " + e.getMessage());
			throw new AdminTestException("Exception occured " + e.getMessage());
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
				logger.error("Exception in Database Connection with following message: " + e.getMessage());
			} catch (NullPointerException e) {
				Assert.assertTrue(false, "Exception in getting the SessionFactory for DB Schema : " + dbName);
			}
		}
		if (sessionFactory != null) {
			session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			logger.info("Session begined with Schema : " + dbName);
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
			logger.error("Exception in Database Connection with following message: " + e.getMessage());
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the SessionFactory for DB Schema : " + dbschema);
		}
		return factory;
	}

	public static void closeDataBaseConnection(Session session) {
		if (session != null) {
			logger.info("Session closed");
			session.close();
		}
	}
	
	public static void executeDBQueries(String dbURL, String dbUser, String dbPassword, String dbSchema, String dbQueryFile) {
		Session session = null;
		try {
			session = getDataBaseConnection(dbURL, dbUser, dbPassword, dbSchema);
			executeQueries(session, dbQueryFile);		
		} catch (Exception e) {
			logger.error("Error:: While executing DB Quiries." + e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public static void executeQueries(Session session, String strQueriesFilePath) throws Exception {
		if (session != null) {
				session.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						Statement statement = connection.createStatement();
						// Read the delete queries from a file and iterate
						try {
							File file = new File(strQueriesFilePath);
							FileReader fileReader = null;
							BufferedReader bufferedReader = null;
							try {
								fileReader = new FileReader(file);
								bufferedReader = new BufferedReader(fileReader);
								String line;
								while ((line = bufferedReader.readLine()) != null) {
									if (line.trim().equals("") || line.trim().startsWith("#"))
										continue;
									line = line.replace("${currentModule}", BaseTestCase.currentModule);
									logger.info("Current query is = " + line);
									statement.addBatch(line);
								}
							} catch (IOException e) {
								logger.error("Error while executing db queries for ::" + e.getMessage());
							} finally {
								closeBufferedReader(bufferedReader);
								closeFileReader(fileReader);
							}
							int[] result = statement.executeBatch();
							logger.info("Success:: Executed DB quiries successfully.");
							for (int i : result) {
								logger.info("deleted records: " + i);
							}
						} finally {
							statement.close();
						}
					}
				});
			}
	}
	public static Session getDataBaseConnection(String dburl, String userName, String password, String schema) {
		SessionFactory factory = null;
		Session session = null;
		logger.info("dburl : " + dburl + " userName : " + userName + " password : " + (password!= null && !password.isBlank() ? "Masked" : ""));
		try {
			Configuration config = new Configuration();
			config.setProperty(Environment.DRIVER, ConfigManager.getDbDriverClass());
			config.setProperty(Environment.URL, dburl);
			config.setProperty(Environment.USER, userName);
			config.setProperty(Environment.PASS, password);
			config.setProperty(Environment.DEFAULT_SCHEMA, schema);
			config.setProperty(Environment.POOL_SIZE, ConfigManager.getDbConnectionPoolSize());
			config.setProperty(Environment.DIALECT, ConfigManager.getDbDialect());
			config.setProperty(Environment.SHOW_SQL, ConfigManager.getShowSql());
			config.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, ConfigManager.getDbSessionContext());
			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();
			session.beginTransaction();
		} catch (HibernateException | NullPointerException e) {
			logger.error("Error while getting the db connection for ::" + dburl, e);
		}
		return session;
	}
	
	public static void closeBufferedReader(BufferedReader bufferedReader) {
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}
	
	public static void closeFileReader(FileReader fileReader) {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}
}