
package io.mosip.kernel.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

/**
 * 
 * @author Ravi Kant
 *
 */

public class KernelDataBaseAccess {

	public SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(KernelDataBaseAccess.class);

	public String env = System.getProperty("env.user");

	public Session getDataBaseConnection(String dbName) {

		String dbConfigXml ="/dbFiles/"+ dbName+env.toLowerCase()+".cfg.xml";
		try {
		factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
		session = factory.getCurrentSession();
		} 
		catch (HibernateException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
			Assert.assertTrue(false, "Exception in creating the sessionFactory");
		}
		
		catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		}
		session.beginTransaction();
		logger.info("==========session  begins=============");
		return session;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDbData(String queryString, String dbName) {

		List<String> data = null;
		try {
			data = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
		}
		return  data;

	}
	@SuppressWarnings("unchecked")
	public List<Object> getData(String queryString, String dbName) {

		List<Object> data = null;
		try {
			data = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
		}
		return  data;

	}
	public long validateDBCount(String queryStr, String dbName) {
		long count = 0;
		try {
			count = ((BigInteger) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryStr).getSingleResult()).longValue();
			logger.info("obtained objects count from DB is : " + count);
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
		}
		return count;
	}

	public boolean validateDataInDb(String queryString, String dbName) {
		int size=0;
		try {
			size = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list().size();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
		}
		return (size == 1) ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getArrayData(String queryString, String dbName) {
		List<Object[]> data = null;
		try {
			data = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
		}
		return data;
	}
	
	public boolean executeQuery(String queryString, String dbName) {
		int res = 0;
		try {
			res = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).executeUpdate();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
		session.getTransaction().commit();
		session.close();
		factory.close();
		logger.info("==========session  closed=============");
		}
		return (res>0) ? true : false;
	}

	@AfterClass(alwaysRun = true)
	public void closingSession() {
		if (session != null) {
			session.getTransaction().commit();
		session.close();
		factory.close();
		logger.info("==========session  closed=============");
		}
	}

}