package io.mosip.authentication.fw.util;

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
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.testng.Assert;
 

/**
 * DB Connection and perform query operation for ida automation utility
 * 
 * @author Vignesh
 *
 */
public class DbConnection {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(DbConnection.class);
	private static Map<String, Object> records;
	
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
			else if (moduleName.equals("IDREPO"))
				if (query.toLowerCase().startsWith("delete")) {
					return executeUpdateQuery("idrepo", query);
				} else
					records = executeQueryAndGetRecord("idrepo", query);
			Map<String, String> returnMap = new HashMap<String, String>();
			for (Entry<String, Object> entry : records.entrySet()) {
				if (entry.getValue() == null || entry.getValue().equals(null) || entry.getValue() == "null"
						|| entry.getValue().equals("null"))
					returnMap.put(entry.getKey(), "null".toString());
				else
					returnMap.put(entry.getKey(), entry.getValue().toString());
			}
			return returnMap;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in execution statement: " + e);
			return null;
		}
	}
	
	private static Map<String, Object> executeQueryAndGetRecord(String moduleName, String query) {
		Session session = getDataBaseConnection(moduleName);
		Map<String, Object> rowData = new HashMap<String, Object>();
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);
				ResultSetMetaData md = rs.getMetaData();
				int columns = md.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						rowData.put(md.getColumnName(i), rs.getObject(i));
					}
				}
			}
		});
		DBCONNECTION_LOGGER.info("==========session  closed=============");
		session.close();
		return rowData;
	}
	
	private static Map<String, String> executeUpdateQuery(String moduleName, String query) {
		Session session = getDataBaseConnection(moduleName);
		Map<String, String> rowData = new HashMap<String, String>();
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement statement = connection.createStatement();
				int count = statement.executeUpdate(query);
				rowData.put("delete", "true");
				rowData.put("count", String.valueOf(count));
			}
		});
		DBCONNECTION_LOGGER.info("==========session  closed=============");
		session.close();
		return rowData;
	}
	
	private static Session getDataBaseConnection(String dbName) {
		String dbConfigXml = dbName + RunConfigUtil.getRunEvironment().toLowerCase() + ".cfg.xml";
		SessionFactory factory = null;
		Session session = null;
		try {
			factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
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
