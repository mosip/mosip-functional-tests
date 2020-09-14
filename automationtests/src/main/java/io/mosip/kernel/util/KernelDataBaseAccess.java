
package io.mosip.kernel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import io.mosip.testrunner.MosipTestRunner;

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
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbProps"+env+".properties";
		
		try {
			InputStream iStream = new FileInputStream(new File(dbPropsPath));
			Properties dbProps = new Properties();
			dbProps.load(iStream);
			Configuration config = new Configuration();
			//config.setProperties(dbProps);
			config.setProperty("hibernate.connection.driver_class", dbProps.getProperty("driver_class"));
			config.setProperty("hibernate.connection.url", dbProps.getProperty(dbName+"_url"));
			config.setProperty("hibernate.connection.username", dbProps.getProperty(dbName+"_username"));
			config.setProperty("hibernate.connection.password", dbProps.getProperty(dbName+"_password"));
			config.setProperty("hibernate.default_schema", dbProps.getProperty(dbName+"_default_schema"));
			config.setProperty("hibernate.connection.pool_size", dbProps.getProperty("pool_size"));
			config.setProperty("hibernate.dialect", dbProps.getProperty("dialect"));
			config.setProperty("hibernate.show_sql", dbProps.getProperty("show_sql"));
			config.setProperty("hibernate.current_session_context_class", dbProps.getProperty("current_session_context_class"));
			config.addFile(new File(dbConfigXml));
		factory = config.buildSessionFactory();
		session = factory.getCurrentSession();
		} 
		catch (HibernateException | IOException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
			e.printStackTrace();
			Assert.assertTrue(false, "Exception in creating the sessionFactory");
		}catch (NullPointerException e) {
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
	
	public boolean executeQueries(List<String> queries, String dbName) {
		int res = 0;
		session = getDataBaseConnection(dbName.toLowerCase());
		try {
			for(String query: queries)
			{
				res = session.createSQLQuery(query).executeUpdate();
				logger.info("Result from the above query execution: "+res);
			}
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