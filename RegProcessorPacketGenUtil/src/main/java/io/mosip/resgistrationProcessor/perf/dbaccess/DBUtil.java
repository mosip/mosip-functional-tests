package io.mosip.resgistrationProcessor.perf.dbaccess;

import java.io.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class DBUtil {

	public Session session;

	public Session obtainSession(PropertiesUtil prop) {

//		SessionFactory sessionFactory = new AnnotationConfiguration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();
//		SessionFactory sessionFactory = new Configuration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();

		if ("default".equals(prop.ENVIRONMENT)) {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			session = sessionFactory.openSession();

		} else {
			File configFile = loadConfigFile(prop);
			SessionFactory sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
			session = sessionFactory.openSession();
		}
		return session;

	}

	private static File loadConfigFile(PropertiesUtil prop) {
		File file = null;
		if ("qa".equalsIgnoreCase(prop.ENVIRONMENT)) {

			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\regProc_qa.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		} else if ("preprod".equalsIgnoreCase(prop.ENVIRONMENT)) {
			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\regProc_preprod.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		} else if ("dev".equalsIgnoreCase(prop.ENVIRONMENT)) {
			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\regProc_dev.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		}else {
			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\hibernate.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		}

	}

}
