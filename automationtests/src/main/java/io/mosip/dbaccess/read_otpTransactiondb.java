package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.dbentity.MasterGender;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;

@Test
public class read_otpTransactiondb {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(read_otpTransactiondb.class);
	
	public static String env = System.getProperty("env.user");

	public static Session getDataBaseConnection(String dbName) {
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
	
	@SuppressWarnings("deprecation")
	public static boolean readotpTransaction(String otp)
	{
		boolean flag=false;
		session = getDataBaseConnection("kernel");
		session.beginTransaction();
		flag=validateOTPinDB(session, otp);
		logger.info("flag is : " +flag);
		return flag;
		
		//session.close();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean validateOTPinDB(Session session, String otp)
	{
		int size;
				
		String queryString=" Select kernel.otp_transaction.*"+
                        " From kernel.otp_transaction where kernel.otp_transaction.otp= :otp_value ";
		
		Query query = session.createSQLQuery(queryString); 
		query.setParameter("otp_value", otp);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			String status_code = (String) TestData[3];
			logger.info("Status is : " +status_code);
			
			// commit the transaction
					session.getTransaction().commit();
						
						factory.close();

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
	}
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
	@SuppressWarnings("deprecation")
	public static boolean readGenderType(String code)
	{
		boolean flag=false;

		session = getDataBaseConnection("masterdata");
		session.beginTransaction();
		flag=validateGenderCode(session, code);
		logger.info("flag is : " +flag);
		return flag;
		
		//session.close();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean validateGenderCode(Session session, String code)
	{
		int size;
				
		String queryString="SELECT master.gender.* FROM master.gender where master.gender.code= :code_value";
				
		Query query = session.createSQLQuery(queryString);
		query.setParameter("code_value", code);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			String status_code = (String) TestData[code.length()];
			logger.info("Status is : " +status_code);
			
			// commit the transaction
					session.getTransaction().commit();
						
						factory.close();

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
	}
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
}