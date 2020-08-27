package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.testrunner.MosipTestRunner;

public class PreRegistartionDataBaseAccess {
	public SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(PreRegistartionDataBaseAccess.class);

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
		try {
			return (List<String>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();

		} catch (IndexOutOfBoundsException | HibernateException e) {
			logger.info(e.getMessage());
			Assert.assertTrue(false, "error while getting data from db :"+dbName);
		}finally {
			session.getTransaction().commit();
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
			}
		return null;
	}
	@SuppressWarnings("unchecked")
	public void updateDbData(String queryString, String dbName) {
		try {
			Query query = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString);
			int res = query.executeUpdate();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.getTransaction().commit();
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
			}	
	}
	@SuppressWarnings("unchecked")
	public List<String> getConsumedStatus(String queryString, String dbName) {
		try {
			return (List<String>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.getTransaction().commit();
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
			}
		return null;
	}
	@SuppressWarnings("unchecked")
	public Date getHoliday(String queryString, String dbName) {
		try {
			return  (Date) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list().get(0);
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.getTransaction().commit();
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
			}
		return null;
	}
	@SuppressWarnings("unchecked")
	public void delete(String queryString, String dbName) {
		try {
			Query query = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString);
			int res = query.executeUpdate();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}finally {
			session.getTransaction().commit();
			session.close();
			factory.close();
			logger.info("==========session  closed=============");
			}	
	}
	
	
	@AfterClass(alwaysRun = true)
	public void closingSession() {
		if (session != null)
			session.getTransaction().commit();
		session.close();
		factory.close();
	}

	
	
}