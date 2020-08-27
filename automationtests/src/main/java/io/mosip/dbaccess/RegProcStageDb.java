package io.mosip.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.dbdto.DemoDedupeDto;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;

/**
 * This class is use for stage validation related data operations 
 * 
 * @author Sayeri Mishra
 *
 */
public class RegProcStageDb {
	SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(RegProcStageDb.class);
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	public String env = System.getProperty("env.user");
	static String dbName = "regproc";

	/**
	 * This method is use for fetching list of DemoDedupeDto from individual_demoghraphic_dedupe
	 * table based on reg id
	 * 
	 * @param regId
	 * @return List<DemoDedupeDto>
	 */
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
	
	@SuppressWarnings("deprecation")
	public List<DemoDedupeDto> regproc_IndividualDemoghraphicDedupe(String regId)
	{
		session = getDataBaseConnection(dbName);
		List<DemoDedupeDto> dto = getDemoById(session, regId);
		if(dto!=null && !dto.isEmpty())
		{
			session.close();
			factory.close();
			return dto;
		}
		return null;
	}

	/**
	 * This method is use for fetching list of DemoDedupeDto from individual_demoghraphic_dedupe
	 * table based on reg id and session
	 * 
	 * @param session2
	 * @param regId
	 * @return List<DemoDedupeDto>
	 */
	private List<DemoDedupeDto> getDemoById(Session session2, String regId) {
		DemoDedupeDto demoDedupeDto = null;
		List<DemoDedupeDto> demoDedupeDtoList = null;
		String queryString = "Select * from regprc.individual_demographic_dedup where "
				+ "regprc.individual_demographic_dedup.reg_id =:regId";
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId", regId);

		List<Object> records = query.getResultList();
		Object[] TestData = null;
		// reading data retrieved from query
		if(records!=null && !records.isEmpty()){
			demoDedupeDtoList = new ArrayList<>();
			for (Object record : records) {
				demoDedupeDto = new DemoDedupeDto();
				TestData = (Object[]) record;
				demoDedupeDto.setRegId((String)TestData[0]);
				demoDedupeDto.setUin((String)TestData[1]);
				demoDedupeDto.setName((String)TestData[2]);
				demoDedupeDto.setDob((String)TestData[3]);
				demoDedupeDto.setGenderCode((String)TestData[4]);
				demoDedupeDto.setLangCode((String)TestData[5]);

				demoDedupeDtoList.add(demoDedupeDto);
			}

			logger.info("demoDedupeDtoList : "+demoDedupeDtoList);
		}

		session.getTransaction().commit();

		return demoDedupeDtoList;
	}

	/**
	 * This method is use for fetching list of DemoDedupeDto having duplicate name, dob, gender,
	 * langCode from individual_demoghraphic_dedupe table
	 * 
	 * @param name
	 * @param genderCode
	 * @param dob
	 * @param langCode
	 * @return List<DemoDedupeDto>
	 */
	public List<DemoDedupeDto> regproc_AllIndividualDemoghraphicDedupe(String name, String genderCode, String dob,
			String langCode) {

		session = getDataBaseConnection(dbName);

		List<DemoDedupeDto> dto = getAllDemo(session, name, genderCode, dob, langCode);
		if(dto!=null && !dto.isEmpty())
		{
			session.close();
			factory.close();
			return dto;
		}
		return null;
	}

	/**
	 * This method is use for fetching list of DemoDedupeDto having duplicate name, dob, gender,
	 * langCode from individual_demoghraphic_dedupe table
	 * 
	 * @param session2
	 * @param name
	 * @param genderCode
	 * @param dob
	 * @param langCode
	 * @return
	 */
	private List<DemoDedupeDto> getAllDemo(Session session2, String name, String genderCode, String dob,
			String langCode) {
		DemoDedupeDto demoDedupeDto = null;
		List<DemoDedupeDto> demoDedupeDtoList = null;
		String queryString = "Select * from regprc.individual_demographic_dedup where "
				+ "regprc.individual_demographic_dedup.name =:name and regprc.individual_demographic_dedup.gender =:genderCode "
				+ "and regprc.individual_demographic_dedup.dob =:dob and regprc.individual_demographic_dedup.lang_code =:langCode and "
				+ "regprc.individual_demographic_dedup.is_active =:isActive and "
				+ "regprc.individual_demographic_dedup.uin IS NOT NULL";
		Query query = session.createSQLQuery(queryString);
		query.setParameter("name", name);
		query.setParameter("genderCode", genderCode);
		query.setParameter("dob", dob);
		query.setParameter("langCode", langCode);
		query.setParameter("isActive", true);

		List<Object> records = query.getResultList();
		Object[] TestData = null;
		// reading data retrieved from query
		if(records!=null && !records.isEmpty()){
			demoDedupeDtoList = new ArrayList<>();
			for (Object record : records) {
				demoDedupeDto = new DemoDedupeDto();
				TestData = (Object[]) record;
				demoDedupeDto.setRegId((String)TestData[0]);
				demoDedupeDto.setUin((String)TestData[1]);
				demoDedupeDto.setName((String)TestData[2]);
				demoDedupeDto.setDob((String)TestData[3]);
				demoDedupeDto.setGenderCode((String)TestData[4]);
				demoDedupeDto.setLangCode((String)TestData[5]);

				demoDedupeDtoList.add(demoDedupeDto);
			}

			logger.info("demoDedupeDtoList : "+demoDedupeDtoList);
		}


		return demoDedupeDtoList;
	}
	
	@SuppressWarnings("deprecation")
	public String regproc_getUIN(String regId)
	{
		session = getDataBaseConnection(dbName);
		String uin = getUINByRegId(session, regId);
		if(uin!=null)
		{
			session.close();
			factory.close();
			return uin;
		}
		return null;
	}
 
	private String getUINByRegId(Session session2, String regId) {
		String uin = null;
		String queryString = "Select regprc.individual_demographic_dedup.uin from regprc.individual_demographic_dedup "
				+ "where regprc.individual_demographic_dedup.reg_id =:regId";
		
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId", regId);
		uin = (String) query.getResultList().get(0);
		logger.info("UIN : "+uin);
		session.getTransaction().commit();
		return uin;
	}
}
