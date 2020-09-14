package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.dbdto.TransactionStatusDTO;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;

public class GetUinFromDb {
	private static Logger logger = Logger.getLogger(GetUinFromDb.class);
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	String dbName="idrepo";
	public Session getCurrentSession() {
		SessionFactory factory=null;
		Session session=null;
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath()+"/dbFiles/dbPropsqa.properties";
		
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
	public List<String> readStatus(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		// String queryString="SELECT regprc.reg_id,regprc.status_code,regprc.status_comment FROM regprc.registration_transaction where reg_id='20916100110014920190218154630'";
		 
		 String queryString="SELECT  idrepo.uin.uin" + 
		 		" FROM idrepo.uin where idrepo.uin.reg_id= :regId";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<String> list=query.getResultList();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 statusComment.add((String) TestData[1]);
			 }
	        t.commit();
	        session.close();
			return statusComment;
	}
	public static void main(String[] args) {
		GetUinFromDb getUin=new GetUinFromDb();
		getUin.readStatus("27847657360002520190318163030");
	}
}
