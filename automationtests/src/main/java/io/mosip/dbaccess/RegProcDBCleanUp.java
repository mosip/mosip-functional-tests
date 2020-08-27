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
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;



public class RegProcDBCleanUp {
	SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(RegProcDBCleanUp.class);
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	String dbName="regproc";
	public String env = System.getProperty("env.user");
	public SessionFactory getSessionFactory() {
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
		return factory;
	}
	
	public void deleteFromRegProcTables(String regID,String queryString) {
		logger.info("Reistration ID is :: "+regID);
		SessionFactory sessionFactory= getSessionFactory();
		session=sessionFactory.getCurrentSession();
		session.beginTransaction();
		//String queryString1="DELETE"+" FROM regprc.applicant_iris WHERE reg_id = :regIdValue";
		Query query=session.createSQLQuery(queryString);
		query.setParameter("regIdValue",regID);
		int result=query.executeUpdate();
		session.getTransaction().commit();
		session.close();
		sessionFactory.close();
		logger.info(regID + " Packet has been cleared from all tables");
	}
	
	public void prepareQueryList(String regID) {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		String deleteTransaction="DELETE"+" FROM regprc.registration_transaction WHERE reg_id = :regIdValue";
		String deleteApplicantAbis="DELETE"+" FROM regprc.reg_abisref WHERE reg_id = :regIdValue";
		String deleteApplicantUin="DELETE"+" FROM regprc.reg_uin WHERE reg_id = :regIdValue";
		String deleteRegistrationList="DELETE"+" FROM regprc.registration_list WHERE reg_id = :regIdValue";
		String deleteManualVerification="DELETE"+" FROM regprc.reg_manual_verification WHERE reg_id = :regIdValue";
		String deleteRegistration="DELETE"+" FROM regprc.registration WHERE id = :regIdValue";
		String deleteIndividualDemographicDedup="DELETE" +" FROM regprc.individual_demographic_dedup WHERE reg_id= :regIdValue";
		List<String> queryList=new ArrayList<String>();
		queryList.add(deleteTransaction);
		queryList.add(deleteApplicantAbis);
		queryList.add(deleteApplicantUin);
		queryList.add(deleteRegistrationList);
		queryList.add(deleteManualVerification);
		queryList.add(deleteRegistration);
		queryList.add(deleteIndividualDemographicDedup);
		for(String query:queryList) {
			cleanUp.deleteFromRegProcTables(regID, query);
		}

	}
	public static void main(String[] args) {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		String deleteTransaction="DELETE"+" FROM regprc.registration_transaction WHERE reg_id = :regIdValue";
		String deleteApplicantAbis="DELETE"+" FROM regprc.reg_abisref WHERE reg_id = :regIdValue";
		String deleteApplicantUin="DELETE"+" FROM regprc.reg_uin WHERE reg_id = :regIdValue";
		String deleteRegistrationList="DELETE"+" FROM regprc.registration_list WHERE reg_id = :regIdValue";
		String deleteManualVerification="DELETE"+" FROM regprc.reg_manual_verification WHERE reg_id = :regIdValue";
		String deleteRegistration="DELETE"+" FROM regprc.registration WHERE id = :regIdValue";
		String deleteIndividualDemographicDedup="DELETE" +" FROM regprc.individual_demographic_dedup WHERE reg_id= :regIdValue";
		List<String> queryList=new ArrayList<String>();
		queryList.add(deleteIndividualDemographicDedup);
		queryList.add(deleteTransaction);
		queryList.add(deleteApplicantAbis);
		queryList.add(deleteApplicantUin);
		queryList.add(deleteRegistrationList);
		queryList.add(deleteManualVerification);
		queryList.add(deleteRegistration);
		
		for(String query:queryList) {
			cleanUp.deleteFromRegProcTables("10006100060000720190518153303", query);
		}

	}
}
