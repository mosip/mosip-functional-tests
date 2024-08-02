package io.mosip.testrig.apirig.dbaccess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.jdbc.Work;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
public class DBManager {
	private static Logger logger = Logger.getLogger(DBManager.class);
	public static void clearPMSDbData() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getPMSDbUrl(), ConfigManager.getPMSDbUser(),
					ConfigManager.getPMSDbPass(), ConfigManager.getPMSDbSchema());
			executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/pmsDataDeleteQueries.txt");		
		} catch (Exception e) {
			logger.error("Error:: While executing PMS DB Quiries." + e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public static void clearPartnerRevampDbData() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getPMSDbUrl(), ConfigManager.getPMSDbUser(),
					ConfigManager.getPMSDbPass(), ConfigManager.getPMSDbSchema());
			executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/partnerRevampDataDeleteQueries.txt");		
		} catch (Exception e) {
			logger.error("Error:: While executing PMS REVAMP DB Quiries." + e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public static void clearKeyManagerDbDataForPartnerRevamp() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getKMDbUrl(), ConfigManager.getKMDbUser(),
					ConfigManager.getKMDbPass(), ConfigManager.getKMDbSchema());
			executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/partnerRevampDataDeleteQueriesForKeyMgr.txt");				
			} catch (Exception e) {
				logger.error("Error:: While executing PMS DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	public static void clearIDADbDataForPartnerRevamp() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getIdaDbUrl(), ConfigManager.getIdaDbUser(),
					ConfigManager.getPMSDbPass(), ConfigManager.getIdaDbSchema());
			executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/partnerRevampDataDeleteQueriesForIDA.txt");				
			} catch (Exception e) {
				logger.error("Error:: While executing IDA DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	
	public static void clearKeyManagerDbData() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getKMDbUrl(), ConfigManager.getKMDbUser(),
					ConfigManager.getKMDbPass(), ConfigManager.getKMDbSchema());
			executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/keyManagerDataDeleteQueries.txt");				
			} catch (Exception e) {
				logger.error("Error:: While executing PMS DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	
		public static void clearKeyManagerDbCertData() {
			Session session = null;
			try {
				session = getDataBaseConnection(ConfigManager.getKMDbUrl(), ConfigManager.getKMDbUser(),
						ConfigManager.getKMDbPass(), ConfigManager.getKMDbSchema());
				executeQueries(session, MosipTestRunner.getGlobalResourcePath() + "/"
						+ "config/keyManagerCertDataDeleteQueries.txt");
			} catch (Exception e) {
				logger.error("Error:: While executing PMS DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	
	public static void clearMasterDbData() {
		Session session = null;
		try {
			session = getDataBaseConnection(ConfigManager.getMASTERDbUrl(), ConfigManager.getMasterDbUser(),
					ConfigManager.getMasterDbPass(), ConfigManager.getMasterDbSchema());
			executeQueries(session,  MosipTestRunner.getGlobalResourcePath() + "/"	+ "config/masterDataDeleteQueries.txt");
			} catch (Exception e) {
				logger.error("Error:: While executing MASTER DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	
		public static void clearMasterDbCertData() {
			Session session = null;
			try {
				session = getDataBaseConnection(ConfigManager.getMASTERDbUrl(), ConfigManager.getMasterDbUser(),
						ConfigManager.getMasterDbPass(), ConfigManager.getMasterDbSchema());
				executeQueries(session,
						MosipTestRunner.getGlobalResourcePath() + "/" + "config/masterDataCertDataDeleteQueries.txt");
			} catch (Exception e) {
				logger.error("Error:: While executing MASTER DB Quiries." + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	
		public static void clearIDADbCertData() {
			Session session = null;
			try {
				session = getDataBaseConnection(ConfigManager.getIdaDbUrl(), ConfigManager.getIdaDbUser(),
						ConfigManager.getPMSDbPass(), ConfigManager.getIdaDbSchema());
				executeQueries(session,
						MosipTestRunner.getGlobalResourcePath() + "/" + "config/idaCertDataDeleteQueries.txt");
			} catch (Exception e) {
				logger.error("Error:: While executing MASTER DB Quiries." + e.getMessage());
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
		logger.info("dburl : " + dburl + " userName : " + userName + " password : " + password);
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
			logger.error("Error while getting the db connection for ::" + dburl);
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