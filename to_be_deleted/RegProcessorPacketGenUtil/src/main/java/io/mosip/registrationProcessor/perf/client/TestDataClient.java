package io.mosip.registrationProcessor.perf.client;

import java.io.File;

import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.service.TestDataGenerator;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;

public class TestDataClient implements Runnable {

	public TestDataClient() {

	}

	@Override
	public void run() {
		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		String csvPath = prop.TEST_DATA_CSV_FILE_PATH;
		TestDataGenerator testDataGenerator = new TestDataGenerator();
		String folder = csvPath.substring(0, csvPath.lastIndexOf('/'));
		File file = new File(folder);
		file.mkdirs();
		Session session = new DBUtil().obtainSession(prop);
		testDataGenerator.generateTestDataInCSV(csvPath, prop, session);
	}

}
