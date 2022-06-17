package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.testng.Assert;
import org.testng.ITest;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.testrunner.MosipTestRunner;
 


public class AuditDBManager extends AdminTestUtil {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(AuditDBManager.class);
	private static Map<String, Object> records;
	private static List<Map<String, Object>> allRecords;
	public static String env = System.getProperty("env.user");
	
	/**
	 * Execute query to get generated otp value
	 * 
	 * @param query
	 * @param moduleName
	 * @return otp record
	 */
	
	
	
	
	public static Map<String, Object> executeQueryAndGetRecord(String moduleName, String query) {
		Session session = getDataBaseConnection(moduleName);
		Map<String, Object> record = new HashMap<String, Object>();
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);
				ResultSetMetaData md = rs.getMetaData();
				int columns = md.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						record.put(md.getColumnName(i), rs.getObject(i));
					}
				}
			}
		});
		DBCONNECTION_LOGGER.info("==========session  closed=============");
		session.close();
		System.out.println(record);
		return record;
		
	}
	
	public static void executeQueryAndDeleteRecord(String moduleName, String deleteQuery) {
		Session session = getDataBaseConnection(moduleName);
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement statement = connection.createStatement();
				int rs = statement.executeUpdate(deleteQuery);
				if (rs > 0) {
				    System.out.println("deleted successfully!");
				}
			}
		});
		DBCONNECTION_LOGGER.info("==========session  closed=============");
		session.close();
		
	}
	
	
	private static Session getDataBaseConnection(String dbName) {
		SessionFactory factory = null;
		Session session = null;
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbConfig.xml";
		
		try {
			Configuration config = new Configuration();
			//config.setProperties(dbProps);
			config.setProperty("hibernate.connection.driver_class", propsKernel.getProperty("driver_class"));
			config.setProperty("hibernate.connection.url", propsKernel.getProperty(dbName+"_url"));
			config.setProperty("hibernate.connection.username", propsKernel.getProperty(dbName+"_username"));
			config.setProperty("hibernate.connection.password", propsKernel.getProperty(dbName+"_password"));
			config.setProperty("hibernate.default_schema", propsKernel.getProperty(dbName+"_default_schema"));
			config.setProperty("hibernate.connection.pool_size", propsKernel.getProperty("pool_size"));
			config.setProperty("hibernate.dialect", propsKernel.getProperty("dialect"));
			config.setProperty("hibernate.show_sql", propsKernel.getProperty("show_sql"));
			config.setProperty("hibernate.current_session_context_class", propsKernel.getProperty("current_session_context_class"));
			config.addFile(new File(dbConfigXml));
		factory = config.buildSessionFactory();
		session = factory.getCurrentSession();
		} catch (HibernateException e) {
			DBCONNECTION_LOGGER.info("Exception in Database Connection with following message: ");
			DBCONNECTION_LOGGER.info(e.getMessage());
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		}
		session.beginTransaction();
		DBCONNECTION_LOGGER.info("==========session  begins=============");
		return session;
	}	
}
