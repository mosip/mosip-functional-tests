package io.mosip.testrig.apirig.authentication.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.testng.Assert;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;
 

/**
 * DB Connection and perform query operation for ida automation utility
 * 
 * @author Vignesh
 *
 */
public class DbConnection {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(DbConnection.class);
	private static Map<String, Object> records;
	private static List<Map<String, Object>> allRecords;
	public static String env = System.getProperty("env.user");
	
	public static void main(String[] arg)
	{
		DBCONNECTION_LOGGER.info(getDataForQuery("update reg_center_machine_device set device_id = '3000022' where regcntr_id = '10003' and device_id='3000033'","MASTER"));
	}
	/**
	 * Execute query to get generated otp value
	 * 
	 * @param query
	 * @param moduleName
	 * @return otp record
	 */
	
	public static Map<String, String> getDataForQuery(String query, String moduleName) {
		records = null;
		try {
			DBCONNECTION_LOGGER.info("Start of dbConnection execution statement");
			DBCONNECTION_LOGGER.info("Query: " + query);
			if (moduleName.equals("KERNEL"))
				records = executeQueryAndGetRecord("kernel", query);
			else if (moduleName.equals("IDA"))
				records = executeQueryAndGetRecord("ida", query);
			else if (moduleName.equals("AUDIT"))
				records = executeQueryAndGetRecord("audit", query);
			else if (moduleName.equals("IDREPO")) {
				if (query.toLowerCase().startsWith("delete")) 
					return executeUpdateQuery(GlobalConstants.IDREPO, query);
				else 
					records = executeQueryAndGetRecord(GlobalConstants.IDREPO, query);
			}
			else if (moduleName.equals("MASTER")) {
				if (query.toLowerCase().startsWith("update")) 
					return executeUpdateQuery(GlobalConstants.MASTERDATA, query);
				 else 
					records = executeQueryAndGetRecord(GlobalConstants.MASTERDATA, query);
			}
			Map<String, String> returnMap = new HashMap<>();
			for (Entry<String, Object> entry : records.entrySet()) {
				
				if (entry.getValue() != null && entry.getValue() != "null"
						&& entry.getValue().equals("null"))
					returnMap.put(entry.getKey(), entry.getValue().toString());
					
				else
					returnMap.put(entry.getKey(), "null");
			}
			return returnMap;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in execution statement: " + e);
		}
		return Collections.emptyMap();
	}
	
	public static List<Map<String, String>> getAllDataForQuery(String query, String moduleName) {
		allRecords = null;
		try {
			DBCONNECTION_LOGGER.info("Start of dbConnection execution statement");
			DBCONNECTION_LOGGER.info("Query: " + query);
			if (moduleName.equals("KERNEL"))
				allRecords = executeQueryAndGetAllRecord("kernel", query);
			else if (moduleName.equals("IDA"))
				allRecords = executeQueryAndGetAllRecord("ida", query);
			else if (moduleName.equals("AUDIT"))
				allRecords = executeQueryAndGetAllRecord("audit", query);
			else if (moduleName.equals("IDREPO"))
				allRecords = executeQueryAndGetAllRecord("GlobalConstants.IDREPO", query);
			List<Map<String, String>> listOfRecordsToBeReturn = new ArrayList<>();
			for (int i = 0; i < allRecords.size(); i++) {
				Map<String, String> records = new HashMap<>();
				for (Entry<String, Object> entry : allRecords.get(i).entrySet()) {
					
					if (entry.getValue() != null && entry.getValue() != "null"
							&& entry.getValue().equals("null"))
						records.put(entry.getKey(), entry.getValue().toString());
						
					else
						records.put(entry.getKey(), "null");
					
				}
				listOfRecordsToBeReturn.add(records);
			}
			return listOfRecordsToBeReturn;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in execution statement: " + e);
		}
		return Collections.emptyList();
	}
	
	private static List<Map<String, Object>> executeQueryAndGetAllRecord(String moduleName, String query) {
		Session session = null;
		List<Map<String, Object>> allRecords = new ArrayList<>();
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
								Map<String, Object> record = new HashMap<>(columns);
								for (int i = 1; i <= columns; i++) {
									record.put(md.getColumnName(i), rs.getObject(i));
								}
								allRecords.add(record);
							}
						} finally {
							statement.close();
						}
					}
				});
			}
		}catch(NullPointerException e){
			DBCONNECTION_LOGGER.error("Exception in executeQueryAndGetAllRecord: " + e);
		}finally {
			closeDataBaseConnection(session);
		}
		return allRecords;
	}
	
	public static void closeDataBaseConnection(Session session) {
		if (session != null) {
			DBCONNECTION_LOGGER.info("==========session  closed=============");
			session.close();
		}
	}
	
	private static Map<String, Object> executeQueryAndGetRecord(String moduleName, String query) {
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
		}catch(NullPointerException e){
			DBCONNECTION_LOGGER.error("Exception in executeQueryAndGetRecord: " + e);
		}finally {
			closeDataBaseConnection(session);
		}
		return record;
	}
	
	private static Map<String, String> executeUpdateQuery(String moduleName, String query) {
		Map<String, String> rowData = new HashMap<>();
		Session session = null;
		try {
			session = getDataBaseConnection(moduleName);
			if (session != null) {
				session.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						Statement statement = connection.createStatement();
						try {
							int count = statement.executeUpdate(query);
							rowData.put("delete", GlobalConstants.TRUE_STRING);
							rowData.put("count", String.valueOf(count));
						} finally {
							statement.close();
						}
					}
				});
			}
		}catch(NullPointerException e){
			DBCONNECTION_LOGGER.error("Exception in executeUpdateQuery: " + e);
		}
		finally {
			closeDataBaseConnection(session);
		}
		return rowData;
	}
	
	private static Session getDataBaseConnection(String dbName) {
		SessionFactory factory = null;
		Session session = null;
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbProps" + env + ".properties";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(dbPropsPath));
			Properties dbProps = new Properties();
			dbProps.load(inputStream);
			Configuration config = new Configuration();
			config.setProperty("hibernate.connection.driver_class", dbProps.getProperty("driver_class"));
			config.setProperty("hibernate.connection.url", dbProps.getProperty(dbName + "_url"));
			config.setProperty("hibernate.connection.username", dbProps.getProperty(dbName + "_username"));
			config.setProperty("hibernate.connection.password", dbProps.getProperty(dbName + "_password"));
			config.setProperty("hibernate.default_schema", dbProps.getProperty(dbName + "_default_schema"));
			config.setProperty("hibernate.connection.pool_size", dbProps.getProperty("pool_size"));
			config.setProperty("hibernate.dialect", dbProps.getProperty("dialect"));
			config.setProperty("hibernate.show_sql", dbProps.getProperty("show_sql"));
			config.setProperty("hibernate.current_session_context_class",
					dbProps.getProperty("current_session_context_class"));
			config.addFile(new File(dbConfigXml));
			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();
			session.beginTransaction();
			DBCONNECTION_LOGGER.info("==========session  begins=============");
		} catch (HibernateException | IOException e) {
			DBCONNECTION_LOGGER.info("Exception in Database Connection with following message: " + e.getMessage());
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return session;
	}
}
