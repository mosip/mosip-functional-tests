package io.mosip.dbaccess;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.dbDTO.OtpEntity;



public class prereg_dbread {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(prereg_dbread.class);
	
	@SuppressWarnings("deprecation")
	public static boolean prereg_dbconnectivityCheck()
	{
		boolean flag=false;
		try {	
		factory = new Configuration().configure("prereg.cfg.xml")
	.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
		logger.info("Connection exception Received");
		return flag;
		}
	}
		
		
		@SuppressWarnings("deprecation")
		public static boolean prereg_dbDataPersistenceCheck(String preId)
		{
			boolean flag=false;
		
			factory = new Configuration().configure("prereg.cfg.xml")
		.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			session = factory.getCurrentSession();
			session.beginTransaction();
			
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
				
		String queryString=" Select *"+
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
						
						

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
		
		
	}
		
		try {
			
			if(size==1)
			{
				Assert.assertEquals(status_code, "Booked");
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

}
