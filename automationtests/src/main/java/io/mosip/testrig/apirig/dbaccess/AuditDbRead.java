package io.mosip.testrig.apirig.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class AuditDbRead {
	
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(AuditDbRead.class);
	public static String env = System.getProperty("env.user");
	
	public static Session getDataBaseConnection(String dbName) {
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbProps"+env+".properties";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(dbPropsPath));
			Properties dbProps = new Properties();
			dbProps.load(inputStream);
			Configuration config = new Configuration();
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
			logger.error(e.getStackTrace());
			Assert.assertTrue(false, "Exception in creating the sessionFactory");
		}catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		session.beginTransaction();
		logger.info("==========session  begins=============");
		return session;
	}

}
