package io.mosip.authentication.fw.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.testng.Assert;
 

/**
 * DB Connection and perform query operation for ida automation utility
 * 
 * @author Vignesh
 *
 */
public class DbConnection {
	private static final Logger DBCONNECTION_LOGGER = Logger.getLogger(DbConnection.class);
	
	private static Session sessionForKernel;
	private static Session sessionForIda;
	private static Session sessionForAudit;
	private static Session sessionForIdrepo;

	/**
	 * Kernel db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public static void startKernelDbSession() {
		sessionForKernel=getDataBaseConnection("kernel");
	}
	
	/**
	 * Ida db connection
	 * 
	 * @return dbConnection
	 */
	public static void startIdaDbSession() {
		sessionForIda=getDataBaseConnection("ida");
	}
	
	/**
	 * Audit db connection
	 * 
	 * @return dbConnection
	 */
	public static void startAuditDbSession() {
		sessionForAudit=getDataBaseConnection("audit");
	}
	
	/**
	 * Execute query to get generated otp value
	 * 
	 * @param query
	 * @param moduleName
	 * @return otp record
	 */
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static Map<String, String> getDataForQuery(String query, String moduleName) {
		Query<Map<String, Object>> records = null;
		try {
			if (moduleName.equals("KERNEL"))
				records = executeQueryAndGetRecord(sessionForKernel, query);
			else if (moduleName.equals("IDA"))
				records = executeQueryAndGetRecord(sessionForIda, query);
			else if (moduleName.equals("AUDIT"))
				records = executeQueryAndGetRecord(sessionForAudit, query);
			else if (moduleName.equals("IDREPO"))
				if (query.toLowerCase().startsWith("delete")) {
					return executeUpdateQuery(sessionForIdrepo, query);
				} else
					records = executeQueryAndGetRecord(sessionForIdrepo, query);
			DBCONNECTION_LOGGER.info("Query: " + query);
			List<Map<String, Object>> aliasToValueMapListTemp = records.list();
			System.out.println(aliasToValueMapListTemp.get(0));
			records.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String, Object>> aliasToValueMapList = records.list();
			Map<String, String> returnMap = new HashMap<String, String>();
			for (Entry<String, Object> entry : aliasToValueMapList.get(0).entrySet()) {
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
	
	@SuppressWarnings("unchecked")
	private static Query<Map<String, Object>> executeQueryAndGetRecord(Session session, String query) {
			return session.createSQLQuery(query);
	}
	
	private static Map<String, String> executeUpdateQuery(Session session, String query) {
			Map<String, String> returnMap = new HashMap<String, String>();
			int count=session.createSQLQuery(query).executeUpdate();
			returnMap.put("delete", "true");
			returnMap.put("count", String.valueOf(count));
			return returnMap;
	}
	
	/**
	 * Idrepo db connection
	 * 
	 * @return dbConnection
	 */
	public static void startIdrepoDbSession() {
		sessionForIdrepo=getDataBaseConnection("idrepo");
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
	
	/**
	 *Terminate Kernel db connection to get generated otp value
	 * 
	 * @return dbConnection
	 */
	public static void terminateKernelDbSession() {
		sessionForKernel.close();
	}
	
	/**
	 * Terminate Ida db connection
	 * 
	 * @return dbConnection
	 */
	public static void terminateIdaDbSession() {
		sessionForIda.close();
	}
	
	/**
	 * Terminate Audit db connection
	 * 
	 * @return dbConnection
	 */
	public static void terminateAuditDbSession() {
		sessionForAudit.close();
	}
	
	/**
	 * Terminate Idrepo db connection
	 * 
	 * @return dbConnection
	 */
	public static void terminateIdrepoDbSession() {
		sessionForIdrepo.close();
	}
	
}
