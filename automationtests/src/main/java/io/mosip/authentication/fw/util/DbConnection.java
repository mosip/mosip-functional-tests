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
	private static List<Map<String, Object>> allRecords;
	
	public static void main(String[] arg)
	{
		System.out.println(getDataForQuery("update reg_center_machine_device set device_id = '3000022' where regcntr_id = '10003' and device_id='3000033'","MASTER"));
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
			if (moduleName.equals("MASTER"))
				records = executeQueryAndGetRecord("masterdata", query);
			else if (moduleName.equals("IDA"))
				records = executeQueryAndGetRecord("ida", query);
			else if (moduleName.equals("AUDIT"))
				records = executeQueryAndGetRecord("audit", query);
			else if (moduleName.equals("IDREPO"))
				if (query.toLowerCase().startsWith("delete")) {
					return executeUpdateQuery("idrepo", query);
				} else
					records = executeQueryAndGetRecord("idrepo", query);
			else if (moduleName.equals("MASTER"))
				if (query.toLowerCase().startsWith("update")) {
					return executeUpdateQuery("masterdata", query);
				} else
					records = executeQueryAndGetRecord("masterdata", query);
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
				allRecords = executeQueryAndGetAllRecord("idrepo", query);
			List<Map<String, String>> listOfRecordsToBeReturn = new ArrayList<Map<String, String>>();
			for (int i = 0; i < allRecords.size(); i++) {
				Map<String, String> records = new HashMap<String, String>();
				for (Entry<String, Object> entry : allRecords.get(i).entrySet()) {
					if (entry.getValue() == null || entry.getValue().equals(null) || entry.getValue() == "null"
							|| entry.getValue().equals("null"))
						records.put(entry.getKey(), "null".toString());
					else
						records.put(entry.getKey(), entry.getValue().toString());
				}
				listOfRecordsToBeReturn.add(records);
			}
			return listOfRecordsToBeReturn;
		} catch (Exception e) {
			DBCONNECTION_LOGGER.error("Execption in execution statement: " + e);
			return null;
		}
	}
	
	private static List<Map<String, Object>> executeQueryAndGetAllRecord(String moduleName, String query) {
		Session session = getDataBaseConnection(moduleName);
		List<Map<String, Object>> allRecords = new ArrayList<Map<String, Object>>();
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);
				ResultSetMetaData md = rs.getMetaData();
				int columns = md.getColumnCount();
				while (rs.next()) {
					Map<String, Object> record = new HashMap<String, Object>(columns);
					for (int i = 1; i <= columns; i++) {
						record.put(md.getColumnName(i), rs.getObject(i));
					}
					allRecords.add(record);
				}
			}
		});
		DBCONNECTION_LOGGER.info("==========session  closed=============");
		session.close();
		return allRecords;
	}
	
	private static Map<String, Object> executeQueryAndGetRecord(String moduleName, String query) {
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
		return record;
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
