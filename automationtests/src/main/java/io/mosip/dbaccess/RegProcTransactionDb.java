package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
/**
 * 
 * @author M1047227
 *
 */
public class RegProcTransactionDb {
	
	private static Logger logger = Logger.getLogger(RegProcTransactionDb.class);
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	static String dbName = "regproc";
	public String env = System.getProperty("env.user");
	public Session getCurrentSession() {
		SessionFactory factory = null;
		Session session = null;
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
	public List<String> readStatus(String regId) { 
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.trn_type_code,regprc.registration_transaction.status_code,regprc.registration_transaction.cr_dtimes" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId order by cr_dtimes";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<String> list=query.getResultList();
		 Map<String,String> mapOfTransaction=new LinkedHashMap<String,String>();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 transactionStatus.setReg_id(TestData[0].toString());
			 transactionStatus.setTrn_type_code(TestData[1].toString());
			 transactionStatus.setStatus_Code(TestData[2].toString());
			 transactionStatus.setCr_dtimes(TestData[3].toString());
			 statusComment.add((String) TestData[1]);
			 mapOfTransaction.put(TestData[1].toString(),TestData[2].toString());
			 }
		
		
		 	List<String> listOfEntries=new ArrayList<String>(mapOfTransaction.values());
		 	List<String> transactionList=new ArrayList<String>();
		 	for(int i=0;i<listOfEntries.size();i++) {
		 		transactionList.add(listOfEntries.get(i));
		 	}
	 
		 	
	        t.commit();
	        session.close();
			return transactionList;
	} 
	public boolean compareTransactionOfDeactivatePackets(String regId,String testCaseName) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		 boolean compareStatus=false;
		// String queryString="SELECT regprc.reg_id,regprc.status_code,regprc.status_comment FROM regprc.registration_transaction where reg_id='20916100110014920190218154630'";
		 
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.status_code,regprc.registration_transaction.status_comment" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<String> list=query.getResultList();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 statusComment.add((String) TestData[1]);
			 }
		 logger.info("DB Status_Codes are :: "+statusComment);
	        t.commit();
	        session.close();
	        if(testCaseName.equals("invalid_deactivatedUin")) {
	        	for(String status:statusComment) {
	        		if(status.equals("PACKET_UIN_UPDATION_FAILURE")) {
	        			compareStatus=true;
	        		}
	        	}
	        } else {
	        	for(String status:statusComment) {
	        		if(status.equals("PACKET_UIN_UPDATION_SUCCESS")) {
	        			compareStatus=true;
	        		}
	        	}
	        }
		return compareStatus;	
	}

	public boolean uinGenerator(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString="SELECT * " + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId "
		 		+ "AND regprc.registration_transaction.trn_type_code = :uinGenerator "
		 		+ "AND regprc.registration_transaction.status_code = :statusCode";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 query.setParameter("uinGenerator", "UIN_GENERATOR"); 
		 query.setParameter("statusCode", "SUCCESS"); 
		 List<String> list=query.getResultList();
		 logger.info("inside uin generator : "+list);
		if(list.size()==1) {
			return true ;
		}
		return false;
	}
	
	public boolean printAndPost(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString="SELECT * " + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId "
		 		+ "AND regprc.registration_transaction.trn_type_code = :print "
		 		+ "AND regprc.registration_transaction.status_code = :statusCode";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 query.setParameter("print", "PRINT_POSTAL_SERVICE"); 
		 query.setParameter("statusCode", "PROCESSED"); 
		 List<String> list=query.getResultList();
		 logger.info("inside print and post : "+list);
		if(list.size()==1) {
			return true ;
		}
		return false;
	}
	public String getRef_Id(String regId) {
		String refId="";
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		 String queryString="Select bio_ref_id "+
		 " FROM regprc.reg_bio_ref where regprc.reg_bio_ref.reg_id= :regId ";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 List<String> list=query.getResultList();
		 for(String id: list) {
			 refId=id;
		 }
		return refId;
		 
	}
	
}
