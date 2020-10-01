package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.dbentity.OtpEntity;
import io.mosip.preregistration.entity.DemographicEntity;
import io.mosip.preregistration.util.PreRegistartionDataBaseAccess;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;




public class PreregDB {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(PreregDB.class);
	
	public PreRegistartionDataBaseAccess dbAccess=new PreRegistartionDataBaseAccess();
	public static String env = System.getProperty("env.user");
	
	public static <E> Session getDataBaseConnection(String dbName,Class<E> entity) {
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
			if(entity!=null)
			config.addAnnotatedClass(entity);
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
	public static boolean prereg_dbconnectivityCheck()
	{
		boolean flag=false;
		session = getDataBaseConnection("prereg",null);
		logger.info("Session value is :" +session);
		
			flag=session != null;
		//	Assert.assertTrue(flag);
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
		
		
		@SuppressWarnings("deprecation")
		public static boolean prereg_dbDataPersistenceCheck(String preId)
		{
			boolean flag=false;
		
			session = getDataBaseConnection("prereg",null);
			
			flag=validatePreIdinDB(session, preId);
			//	Assert.assertTrue(flag);
				logger.info("Flag is : " +flag);
				if(flag)
				{
					//session.close();
					return flag;
				}
					
				/*else
				return flag;*/
			
			
			return flag;
				
		
	}
	
	
	private static boolean validatePreIdinDB(Session session, String preId)
	{
		int size ;
		String status_code = null;
				
		String queryString=" Select prereg_id, status_code"+
                        " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";
		
		/*String queryString=
                "  SELECT * FROM prereg.applicant_demographic where prereg_id='74157648721735' ";
	*/	
																																						
		Query query = session.createSQLQuery(queryString);
		query.setParameter("preId_value", preId);
		@SuppressWarnings("unchecked")
		
		List<Object> objs = (List<Object>) query.list();
		//logger.info("First Element of List Elements are : " +objs.get(1));
		size=objs.size();
		logger.info("Size is : " +size);
		
		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			 status_code = (String) (TestData[9]);
			
			logger.info("Status is : " +status_code);
			
			// commit the transaction
					session.getTransaction().commit();
					session.close();	
						

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
		
		
	}
		
		try {
			
			if(size==1)
			{
				Assert.assertEquals(status_code, "Pending_Appointment");
				return true;
			}
			else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	
	}
	
	

	public static boolean prereg_db_CleanUp(List<String> preIds)
    {
		
		boolean flag=false;
	
		for(String preId : preIds)
		
		{
			session = getDataBaseConnection("prereg",null);
	
		  /*
         * Query to Delete PreId data in applicant_demographic table
         */
         String queryString= "Delete from prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";
       //  String queryString= "Delete from prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= '97846295631728' ";
         int size=0;
         
         List<Object> objs = null;
        /* Query query = session.executeUpdate(queryString);
         query = session.createSQLQuery(queryString);*/
        
         Query query = session.createSQLQuery(queryString);
         logger.info("Query after replacing PreId =================== :" +query);
        query.setParameter("preId_value", preId);

         int res=query.executeUpdate();
         session.getTransaction().commit();
          
         logger.info("Result size is ============: " +res);
          if(res==1)
          {
        	  logger.info("Data Deleted Successfully ======");
        	  
        	  flag=true;
          }
          else
          {
        	  logger.info("Data NOT Deleted Successfully ======"); 
        	  flag=false;
          }
         
		}
          session.close();
			factory.close();
           return flag;      
          
    }
	
	
	public static boolean prereg_db_Update(String preId)
    {
		
		boolean flag=false;
		//String preId;
		
		session = getDataBaseConnection("prereg",null);
	
		  /*
         * Query to Delete PreId data in applicant_demographic table
         */
         String queryString= "Update prereg.applicant_demographic set encrypted_dtimes= encrypted_dtimes - interval '48' hour where prereg.applicant_demographic.prereg_id= :preId_value ";
       // String queryString= "Update prereg.applicant_demographic set encrypted_dtimes= encrypted_dtimes - interval '48' hour  WHERE prereg_id='49758245369132' ";
         int size=0;         
        
         Query query = session.createSQLQuery(queryString);
        logger.info("Query after replacing PreId =================== :" +query);
        query.setParameter("preId_value", preId);

         int res=query.executeUpdate();
         session.getTransaction().commit();
          
         logger.info("Result size is ============: " +res);
          if(res==1)
          {
        	  logger.info("Data Updated Successfully ======");
        	  
        	  flag=true;
          }
          else
          {
        	  logger.info("Data NOT Updated Successfully ======"); 
        	  flag=false;
          }
         
		
          session.close();
			factory.close();
           return flag;      
          
    }
	
	
	@SuppressWarnings("unchecked")
	private static List<Object> fetchingData(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		
		Query query = session.createSQLQuery(queryString);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		
		// commit the transaction
		session.getTransaction().commit();
			session.close();
			factory.close();
		
		
			return objs;
		
	
	}
	@SuppressWarnings("deprecation")
	public static List<Object> fetchOTPFromDB(String queryStr, Class dtoClass)
	{
		List<Object> objs =null;
		session = getDataBaseConnection("kernel",null);
		objs=fetchingOTPData(session, queryStr);
		
		return objs;
		

	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> fetchingOTPData(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		
		Query query = session.createSQLQuery(queryString);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		
		// commit the transaction
		session.getTransaction().commit();
			session.close();
			factory.close();
		
		
			return objs;
		
	
	}
	
	
	
	public static List<?> validateDB(String queryStr)
	{
		List<?> flag;
		
		session = getDataBaseConnection("prereg",null);
		flag=validateDBdata(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	public static List<?> validateDBDataVal(String queryStr)
	{
		List<?> flag;
		
		session = getDataBaseConnection("prereg",null);
		flag=validateDBdata(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	public static int validateDBUpdate(String queryStr)
	{
		int flag;
		
		
		session = getDataBaseConnection("prereg",null);
		flag=validateDBdataUpdate(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	
	public static int validateDBdata(String queryStr,String dbName)
	{
		int flag;
		session = getDataBaseConnection(dbName,DemographicEntity.class);
		flag=validateDBdataUpdate(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	public static List<?> validateDBVal(String queryStr,String dbName)
	{
		List<?> flag;
		session = getDataBaseConnection(dbName,DemographicEntity.class);
		flag=validateDBdata(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> getData(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		
		Query query = session.createSQLQuery(queryString);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		
		// commit the transaction
		session.getTransaction().commit();
			session.close();
			factory.close();
		
		
			return objs;
		
	
	}

	private static void UpdateData(Session session, String queryStr)
	{
		int size;		
		String queryString=queryStr;
		Query query = session.createSQLQuery(queryString);
		int res = query.executeUpdate();
		session.getTransaction().commit();
		session.close();
			factory.close();
	}
	
	
	
	public static List<Object> validateDBdata(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		org.hibernate.query.Query query= session.createQuery(queryStr);
		
		//Query query = session.createSQLQuery(queryString);
	
		//List<Object> objs = (List<Object>) query.list();
		List<Object> objs = query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		
		// commit the transaction
		session.getTransaction().commit();
		//session.close();
			factory.close();
		
		return objs;
		
	
	}

	
	
	public static  int validateDBdataUpdate(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		org.hibernate.query.Query query= session.createQuery(queryStr);
		
		//Query query = session.createSQLQuery(queryString);
	
		//List<Object> objs = (List<Object>) query.list();
		int result = query.executeUpdate();
		
		logger.info("Size is : " +result);
		
		// commit the transaction
		session.getTransaction().commit();
			session.close();
			factory.close();
		
		return result;
		
	
	}


}