package io.mosip.preregistration.util;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import io.mosip.kernel.util.CommonLibrary;

public class PreRegistartionDataBaseAccess {
	public SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(PreRegistartionDataBaseAccess.class);

	public String env = System.getProperty("env.user");

	public Session getDataBaseConnection(String dbName) {

		String dbConfigXml = "/dbFiles/"+dbName+env+".cfg.xml";
		try {

		factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
		} catch (HibernateException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		session = factory.getCurrentSession();
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
