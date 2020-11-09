package io.mosip.registrationProcessor.perf.util;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

	public String REG_ID;
	public int NUMBER_OF_TEST_DATA;
	public String REGID_LOG_FILE;
	public String PACKET_UPLOAD_TIME;
	public String TEST_DATA_CSV_FILE_PATH;
	public String NEW_PACKET_WORKER_PATH;
	public String PARENT_FOLDER;
	public String VALID_PACKET_PATH_FOR_PACKET_GENERATION;
	public String CHECKSUM_LOGFILE_PATH;
	public String SYNCDATA__FILE_PATH;
	public int NUMBER_OF_TEST_PACKETS;
	public boolean USE_PROXY;
	public String ENVIRONMENT;
	public String BASE_URL;
	public String PROCESS_DECRYPTION;
	public String ID_JSON_FILE;
	public String NUMBER_OF_THREADS_PACKET_CREATION;
	public String NUMBER_OF_THREADS_TEST_DATA;
	public boolean AUTH_TYPE_CLIENTID_SECRETKEY;
	public boolean IS_CHILD_PACKET;
	public boolean DECRYPTION_NEEDED;
	public String AUTH_TOKEN;
	public String COUNTRY_CODE;
	public boolean IDOBJECT_TYPE_PHIL;
	public boolean MULTI_LANG;
	public String ID_SCHEMA_VERSION;
	public String UPDATE_PACKET_PATH;
	public String LOST_UIN_PATH;
	public String DECRYPTION_PACKET_PATH;
	public String DECRYPTION_FOLDER;
	public String NEW_PACKET_PATH;
	public String CHILD_PACKET_PATH;
	public String MULTI_LANG_VAL;

	/*
	 * public void loadProperties(String configFile) {
	 * 
	 * Properties properties = new Properties(); InputStream inputStream =
	 * this.getClass().getClassLoader().getResourceAsStream(configFile); try {
	 * properties.load(inputStream); } catch (IOException e) { e.printStackTrace();
	 * }
	 * 
	 * TEST_DATA_CSV_FILE_PATH = properties.getProperty("TEST_DATA_CSV_FILE_PATH");
	 * NEW_PACKET_FOLDER_PATH = properties.getProperty("NEW_PACKET_FOLDER_PATH");
	 * VALID_PACKET_PATH_FOR_PACKET_GENERATION =
	 * properties.getProperty("PATH_FOR_VALID_REG_PACKETS"); CHECKSUM_LOGFILE_PATH =
	 * properties.getProperty("CHECKSUM_LOGFILE_PATH"); NUMBER_OF_TEST_PACKETS =
	 * Integer.parseInt(properties.getProperty("NUMBER_OF_TEST_PACKETS")); USE_PROXY
	 * = Boolean.parseBoolean(properties.getProperty("USE_PROXY"));
	 * SYNCDATA__FILE_PATH = properties.getProperty("SYNCDATA__FILE_PATH");
	 * ENVIRONMENT = properties.getProperty("ENVIRONMENT"); BASE_URL =
	 * properties.getProperty("BASE_URL"); PACKET_UPLOAD_TIME =
	 * properties.getProperty("PACKET_UPLOAD_TIME"); }
	 */
	public PropertiesUtil(String configFile) {
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		REGID_LOG_FILE = properties.getProperty("REGID_LOG_FILE");
		TEST_DATA_CSV_FILE_PATH = properties.getProperty("TEST_DATA_CSV_FILE_PATH");
		NEW_PACKET_WORKER_PATH = properties.getProperty("NEW_PACKET_FOLDER_PATH");
		PARENT_FOLDER = properties.getProperty("PARENT_FOLDER");
		VALID_PACKET_PATH_FOR_PACKET_GENERATION = properties.getProperty("PATH_FOR_VALID_REG_PACKETS");
		CHECKSUM_LOGFILE_PATH = properties.getProperty("CHECKSUM_LOGFILE_PATH");
		NUMBER_OF_TEST_PACKETS = Integer.parseInt(properties.getProperty("NUMBER_OF_TEST_PACKETS"));
		USE_PROXY = Boolean.parseBoolean(properties.getProperty("USE_PROXY"));
		SYNCDATA__FILE_PATH = properties.getProperty("SYNCDATA__FILE_PATH");
		ENVIRONMENT = properties.getProperty("ENVIRONMENT");
		BASE_URL = properties.getProperty("BASE_URL");
		PROCESS_DECRYPTION = properties.getProperty("PROCESS_DECRYPTION");
		PACKET_UPLOAD_TIME = properties.getProperty("PACKET_UPLOAD_TIME");
		ID_JSON_FILE = properties.getProperty("ID_JSON_FILE");
		NUMBER_OF_THREADS_PACKET_CREATION = properties.getProperty("NUMBER_OF_THREADS_PACKET_CREATION");
		NUMBER_OF_THREADS_TEST_DATA = properties.getProperty("NUMBER_OF_THREADS_TEST_DATA");
		AUTH_TOKEN = properties.getProperty("AUTH_TOKEN");
		AUTH_TYPE_CLIENTID_SECRETKEY = Boolean.parseBoolean(properties.getProperty("AUTH_TYPE_CLIENTID_SECRETKEY"));
		DECRYPTION_NEEDED = Boolean.parseBoolean(properties.getProperty("DECRYPTION_NEEDED"));
		IS_CHILD_PACKET = Boolean.parseBoolean(properties.getProperty("IS_CHILD_PACKET"));
		NUMBER_OF_TEST_DATA = Integer.parseInt(properties.getProperty("NUMBER_OF_TEST_DATA"));
		COUNTRY_CODE = properties.getProperty("COUNTRY_CODE");
		REG_ID = properties.getProperty("REGID_TO_UPDATE");
		UPDATE_PACKET_PATH = properties.getProperty("UPDATE_PACKET_PATH");
		IDOBJECT_TYPE_PHIL = Boolean.parseBoolean(properties.getProperty("IDOBJECT_TYPE_PHIL"));
		MULTI_LANG = Boolean.parseBoolean(properties.getProperty("MULTI_LANG"));
		ID_SCHEMA_VERSION = properties.getProperty("ID_SCHEMA_VERSION");
		LOST_UIN_PATH = properties.getProperty("LOST_UIN_PATH");
		DECRYPTION_PACKET_PATH = properties.getProperty("DECRYPTION_PACKET_PATH");
		DECRYPTION_FOLDER = properties.getProperty("DECRYPTION_FOLDER");
		NEW_PACKET_PATH = properties.getProperty("NEW_PACKET_PATH");
		CHILD_PACKET_PATH = properties.getProperty("CHILD_PACKET_PATH");
		MULTI_LANG_VAL = properties.getProperty("MULTI_LANG_VAL");
	}
}
