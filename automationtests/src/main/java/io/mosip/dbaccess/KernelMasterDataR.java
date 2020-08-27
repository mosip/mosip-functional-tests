package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import org.testng.annotations.BeforeClass;

import io.mosip.dbentity.OtpEntity;
import io.mosip.dbentity.UinEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;



/**
 * @author Ravi Kant
 * @author Arunakumar.Rati
 *
 */
@SuppressWarnings("deprecation")
public class KernelMasterDataR {
	public static SessionFactory factory;
	static Session session;
	public static Session session1;
	public static List<Object> objs = null;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);
	
	public static String env=System.getProperty("env.user");
	

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

	
	@BeforeClass
	public static Session dbCheck()
	{
		session1 = getDataBaseConnection("kernel");
		session1.beginTransaction();
		logger.info("----------------session has began----------------");
		return session1;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getData(String queryString)
	{
	  int size;
		Query query = session1.createSQLQuery(queryString); 
		
	
		List<String> objs = (List<String>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
			// commit the transaction
		//session1.getTransaction().commit();
						
		return objs;
			
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String[]> getArrayData(String queryString)
	{
	  int size;
		Query query = session1.createSQLQuery(queryString); 
		
	
		List<String[]> objs = (List<String[]>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
			// commit the transaction
		//session1.getTransaction().commit();
						
		return objs;
			
	}
	@SuppressWarnings("unchecked")
	private static boolean validateDatainDB(Session session, String queryString)
	{
		int size;
		 objs = session.createSQLQuery(queryString).list();
		size=objs.size();
		logger.info("Size is : " +size);
	
			// commit the transaction
					session.getTransaction().commit();
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
	

	@SuppressWarnings("rawtypes")
	public static List<String> getDataFromDB(Class dtoClass,String query)
	{
		List<String> data=null;
		session = getDataBaseConnection("masterdata");
		session.beginTransaction();
		data=getDbData(session, query);
		//logger.info("flag is : " +flag);
		return data;
		
		
	}
	


	public static boolean kernelMasterData_dbconnectivityCheck()
	{
		boolean flag=false;
		
		session = getDataBaseConnection("masterdata");
		session.beginTransaction();
		
		logger.info("Session value is :" +session);
		
			flag=session != null;
			logger.info("Flag is : " +flag);
			if(flag)
			{
				session.close();
				factory.close();
				return flag;
			}
				
			else
			return flag;
	}
		
	

		@SuppressWarnings("rawtypes")
		public static boolean masterDataDBConnection(Class dtoClass,String query)
		{
			boolean flag=false;
			session = getDataBaseConnection("masterdata");
			session.beginTransaction();
			flag=validateDatainDB(session, query);
			session.close();
			factory.close();
			logger.info("flag is : " +flag);
			return flag;
			
		
		}
		
		public static boolean validateKernelDB(String queryStr)
		{
			boolean flag=false;
				session = getDataBaseConnection("kernel");
				session.beginTransaction();
			
			flag=validateDatainDB(session, queryStr);
				session.close();
				factory.close();
			logger.info("obtained objects count from DB is : " +flag);
			return flag;
			
		}

		
		
		/**
		 * @param queryStr containing query to obtain data count in table
		 * @return count obtained from db
		 */
		public static long validateDBCount(String queryStr)
		{
			long flag=0;
				session = getDataBaseConnection("masterdata");
				session.beginTransaction();
			
			
			flag=((BigInteger)session.createSQLQuery(queryStr).getSingleResult()).longValue();
			
			// commit the transaction
					session.getTransaction().commit();
					session.close();
					factory.close();
			logger.info("obtained objects count from DB is : " +flag);
			return flag;
			
	}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static List<String> getDbData(Session session1,String queryString)
		{
		  int size;
			Query query = session1.createSQLQuery(queryString); 
			
		
			List<String> objs = (List<String>) query.list();
			size=objs.size();
			logger.info("Size is : " +size);
				// commit the transaction
			//session1.getTransaction().commit();
							
			return objs;
				
		}

@AfterClass(alwaysRun=true)
public void closingSession()
{
	
	session1.getTransaction().commit();
	factory.close();
	session1.close();
	
	
}

}