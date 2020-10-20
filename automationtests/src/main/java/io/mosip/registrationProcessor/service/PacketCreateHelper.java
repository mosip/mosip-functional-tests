package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;
import org.testng.Assert;

import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registrationProcessor.regpacket.dto.PhilIdentityObject;
import io.mosip.registrationProcessor.regpacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.FieldValueArray;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;
import io.mosip.util.HMACUtils;
import io.mosip.util.TokenGeneration;

public class PacketCreateHelper {
	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";
	private String SUB_FOLDER;
	private static Logger logger = Logger.getLogger(PacketCreateHelper.class);
	private PacketDemoDataUtil packetDataUtil;
	private String OLD_REGID;
	private String NEW_REGID;

	private String EXISTING_PACKET_DIR;
	private String TEMP_PATH_SECTION = File.separator + "temp";
	RegProcApiRequests apiRequests;

	public PacketCreateHelper() {
		this.SUB_FOLDER = SOURCE + File.separator + PROCESS;
		apiRequests = new RegProcApiRequests();
		packetDataUtil = new PacketDemoDataUtil();
	}

	public PacketCreateHelper(String OLD_REGID, String existingPacketDir) {
		this.SUB_FOLDER = SOURCE + File.separator + PROCESS;
		apiRequests = new RegProcApiRequests();
		packetDataUtil = new PacketDemoDataUtil();
		this.OLD_REGID = OLD_REGID;
		this.EXISTING_PACKET_DIR = existingPacketDir;
	}

	public Session getDataBaseConnection(String dbName) {
		String env = System.getProperty("env.user");
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbProps" + env + ".properties";
		Session session = null;
		try {
			InputStream iStream = new FileInputStream(new File(dbPropsPath));
			Properties dbProps = new Properties();
			dbProps.load(iStream);
			Configuration config = new Configuration();
			// config.setProperties(dbProps);
			config.setProperty("hibernate.connection.driver_class", dbProps.getProperty("driver_class"));
			config.setProperty("hibernate.connection.url", dbProps.getProperty(dbName + "_url"));
			config.setProperty("hibernate.connection.username", dbProps.getProperty(dbName + "_username"));
			config.setProperty("hibernate.connection.password", dbProps.getProperty(dbName + "_password"));
			config.setProperty("hibernate.default_schema", dbProps.getProperty(dbName + "_default_schema"));
			config.setProperty("hibernate.connection.pool_size", dbProps.getProperty("pool_size"));
			config.setProperty("hibernate.dialect", dbProps.getProperty("dialect"));
			config.setProperty("hibernate.show_sql", dbProps.getProperty("show_sql"));
			config.setProperty("hibernate.current_session_context_class",
					dbProps.getProperty("current_session_context_class"));
			config.addFile(new File(dbConfigXml));
			SessionFactory factory = config.buildSessionFactory();
			session = factory.getCurrentSession();
		} catch (HibernateException | IOException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
			e.printStackTrace();
			Assert.assertTrue(false, "Exception in creating the sessionFactory");
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		}
		session.beginTransaction();
		logger.info("==========session  begins=============");
		return session;
	}

	public String generateRegId(String centerId, String machineId) {

		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currUTCTime = f.format(new Date());

		int n = 10000 + new Random().nextInt(89000);
		String randomNumber = String.valueOf(n);

		this.NEW_REGID = centerId + machineId + randomNumber + currUTCTime;
		return this.NEW_REGID;
	}

	public void copyFolderContentsToModify(File srcPath) {
		/*
		 * Source folder string contains the path section till process name
		 */
		String tempDir = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;

		File tempDirFile = new File(tempDir);
		String destinationPath = tempDir + File.separator + NEW_REGID + File.separator + this.SUB_FOLDER;

		new File(destinationPath).mkdir();

		String evidenceSrcPath = srcPath + File.separator + OLD_REGID + "_evidence";
		String idSrcPath = srcPath + File.separator + OLD_REGID + "_id";
		String optionalSrcPath = srcPath + File.separator + OLD_REGID + "_optional";

		String destOptionalPath = destinationPath + File.separator + NEW_REGID + "_optional";
		String destIdPath = destinationPath + File.separator + NEW_REGID + "_id";
		String destEvidencePath = destinationPath + File.separator + NEW_REGID + "_evidence";

		new File(destOptionalPath).mkdir();
		new File(destIdPath).mkdir();
		new File(destEvidencePath).mkdir();

		copyDirectory(new File(evidenceSrcPath), new File(destEvidencePath));
		copyDirectory(new File(idSrcPath), new File(destIdPath));
		copyDirectory(new File(optionalSrcPath), new File(destOptionalPath));

		String srcIdJsonFile = srcPath + File.separator + OLD_REGID + "_id.json";
		String srcEvidenceJsonFile = srcPath + File.separator + OLD_REGID + "_evidence.json";
		String srcOptionalJsonFile = srcPath + File.separator + OLD_REGID + "_optional.json";

		String destIdJsonFile = destinationPath + File.separator + NEW_REGID + "_id.json";
		String destEvidenceJsonFile = destinationPath + File.separator + NEW_REGID + "_evidence.json";
		String destOptionalJsonFile = destinationPath + File.separator + NEW_REGID + "_optional.json";

		copyFile(new File(srcIdJsonFile), new File(destIdJsonFile));
		copyFile(new File(srcEvidenceJsonFile), new File(destEvidenceJsonFile));
		copyFile(new File(srcOptionalJsonFile), new File(destOptionalJsonFile));

	}

	private void copyFile(File srcFile, File destFile) {

		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copyDirectory(File srcDir, File destDir) {
		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void emptyTempLocation() {
		String tempDir = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;

		File tempFile = new File(tempDir);
		System.out.println(tempFile.getAbsolutePath());
		List<File> filesToDelete = new ArrayList<>();
		if (tempFile.exists()) {
			for (File f : tempFile.listFiles()) {
				filesToDelete.add(f);
				try {
					if (f.isDirectory())
						FileUtils.deleteDirectory(f);
					else if (f.isFile())
						FileUtils.deleteQuietly(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println(filesToDelete.size() + " number of files deleted....");
		}

	}

	public void modifyDemographicAndPacketMetaInfoFile(String packetContentFolder, String centerId, String machineId,
			String userId, PropertiesUtil prop) throws Exception {
		JSONUtil jsonUtil = new JSONUtil();
		Gson gson = new Gson();
		String identityFolder = packetContentFolder + File.separator + NEW_REGID + "_id";

		String idJsonPath = identityFolder + File.separator + "ID.json";
		String dbName = "masterdata";
		Session session = null;
		try {
			session = getDataBaseConnection(dbName);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex);
		}
		try {
			if (prop.IDOBJECT_TYPE_PHIL) {
				PhilIdentityObject updatedIdObject = packetDataUtil.modifyPhilDemographicData(prop, session,
						idJsonPath);
				jsonUtil.writeJsonToFile(gson.toJson(updatedIdObject), idJsonPath);
			} else {
				RegProcIdDto updatedPacketDemoDto = packetDataUtil.modifyDemographicData(prop, session, idJsonPath);
				jsonUtil.writeJsonToFile(gson.toJson(updatedPacketDemoDto), idJsonPath);
			}

			String packetMetaInfoFile = packetContentFolder + File.separator + NEW_REGID + "_id" + File.separator
					+ "packet_meta_info.json";
			packetDataUtil.modifyPacketMetaInfo(packetMetaInfoFile, NEW_REGID, centerId, machineId, userId);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}

	}

	public void computeChecksumForIdentityPacket() throws Exception {

		String tempDir = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;
		String packetContentFolder = tempDir + File.separator + NEW_REGID + File.separator + this.SUB_FOLDER;

		String packetMetaInfoFile = packetContentFolder + File.separator + NEW_REGID + "_id" + File.separator
				+ "packet_meta_info.json";
		JSONUtil jsonUtil = new JSONUtil();
		JSONObject packetmetaInfo = jsonUtil.loadJsonFromFile(packetMetaInfoFile);
		Map identity = (Map) packetmetaInfo.get("identity");
		ArrayList<Object> hashOneList = (ArrayList) identity.get("hashSequence1");

		List<FieldValueArray> hashSequence1 = new ArrayList<>();
		for (Object element : hashOneList) {
			FieldValueArray fieldValueArrayObj = new FieldValueArray();
			Map map = (Map) element;
			map.get("label");
			fieldValueArrayObj.setLabel((String) map.get("label"));
			List<String> values = (List<String>) map.get("value");
			fieldValueArrayObj.setValue(values);
			hashSequence1.add(fieldValueArrayObj);
		}
		String checksum = "";
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence1,
					packetContentFolder + File.separator + NEW_REGID + "_id");

			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);

			String identityFolder = packetContentFolder + File.separator + NEW_REGID + "_id";

			packetDataUtil.writeChecksumToFile(identityFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("computeChecksumForIdentityPacket: " + e.getMessage());
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	public void computeChecksumForEvidencePacket() {

		String centerId = NEW_REGID.substring(0, 5);
		String machineId = NEW_REGID.substring(5, 10);
		String userId = "110010";
		String PARENT_FOLDER_PATH = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;
		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + NEW_REGID + File.separator + SOURCE
				+ File.separator + PROCESS;
		String packetMetaInfoFile = packetContentFolder + File.separator + NEW_REGID + "_evidence" + File.separator
				+ "packet_meta_info.json";
		JSONUtil jsonUtil = new JSONUtil();
		JSONObject packetmetaInfo = jsonUtil.loadJsonFromFile(packetMetaInfoFile);
		Map identity = (Map) packetmetaInfo.get("identity");
		ArrayList<Object> metadata = (ArrayList<Object>) identity.get("metaData");
		ArrayList<Object> operationsData = (ArrayList<Object>) identity.get("operationsData");
		int counter = 1;
		for (int i = 0; i < metadata.size(); i++) {

			Map keyVal = (Map) metadata.get(i);
			if ("registrationId".equals(keyVal.get("label"))) {
				keyVal.put("value", NEW_REGID);
				counter++;
			} else if ("machineId".equals(keyVal.get("label"))) {
				keyVal.put("value", machineId);
				counter++;
			} else if ("centerId".equals(keyVal.get("label"))) {
				keyVal.put("value", centerId);
				counter++;
			} else if ("creationDate".equals(keyVal.get("label"))) {
				keyVal.put("value", getCurrUTCDate());
				counter++;
			} else if (counter == 5) {
				break;
			}

		}

		for (int i = 0; i < operationsData.size(); i++) {

			Map keyVal = (Map) operationsData.get(i);
			if ("officerId".equals(keyVal.get("label"))) {
				keyVal.put("value", userId);
				break;
			}

		}

		identity.put("metaData", metadata);
		identity.put("operationsData", operationsData);
		/*
		 * Edit creationDate time in identity
		 */
		String creationDate = getCurrUTCDate();
		identity.put("creationDate", creationDate);
		identity.put("registrationId", NEW_REGID);
		packetmetaInfo.put("identity", identity);

		jsonUtil.writeJSONToFile(packetMetaInfoFile, packetmetaInfo);

		List<FieldValueArray> hashSequence1 = new ArrayList<>();

		ArrayList<Object> hashOneList = (ArrayList) identity.get("hashSequence1");

		for (Object element : hashOneList) {
			FieldValueArray fieldValueArrayObj = new FieldValueArray();
			Map map = (Map) element;
			map.get("label");
			fieldValueArrayObj.setLabel((String) map.get("label"));
			List<String> values = (List<String>) map.get("value");
			fieldValueArrayObj.setValue(values);
			hashSequence1.add(fieldValueArrayObj);
		}
		String checksum = "";
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence1,
					packetContentFolder + File.separator + NEW_REGID + "_evidence");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String evidenceFolder = packetContentFolder + File.separator + NEW_REGID + "_evidence";

		try {
			packetDataUtil.writeChecksumToFile(evidenceFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private String getCurrUTCDate() {
		String timestamp = "";
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		SimpleDateFormat f = new SimpleDateFormat(timestampFormat);
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		timestamp = f.format(new Date());
		timestamp = timestamp + "Z";
		return timestamp;
	}

	public void computeChecksumForOptionalPacket() {
		String centerId = NEW_REGID.substring(0, 5);
		String machineId = NEW_REGID.substring(5, 10);
		String userId = "110010";
		String PARENT_FOLDER_PATH = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;
		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + NEW_REGID + File.separator + SOURCE
				+ File.separator + PROCESS;
		String packetMetaInfoFile = packetContentFolder + File.separator + NEW_REGID + "_optional" + File.separator
				+ "packet_meta_info.json";
		JSONUtil jsonUtil = new JSONUtil();
		JSONObject packetmetaInfo = jsonUtil.loadJsonFromFile(packetMetaInfoFile);
		Map identity = (Map) packetmetaInfo.get("identity");
		ArrayList<Object> metadata = (ArrayList<Object>) identity.get("metaData");
		ArrayList<Object> operationsData = (ArrayList<Object>) identity.get("operationsData");
		int counter = 1;
		for (int i = 0; i < metadata.size(); i++) {

			Map keyVal = (Map) metadata.get(i);
			if ("registrationId".equals(keyVal.get("label"))) {
				keyVal.put("value", NEW_REGID);
				counter++;
			} else if ("machineId".equals(keyVal.get("label"))) {
				keyVal.put("value", machineId);
				counter++;
			} else if ("centerId".equals(keyVal.get("label"))) {
				keyVal.put("value", centerId);
				counter++;
			} else if ("creationDate".equals(keyVal.get("label"))) {
				keyVal.put("value", getCurrUTCDate());
				counter++;
			} else if (counter == 5) {
				break;
			}

		}

		for (int i = 0; i < operationsData.size(); i++) {

			Map keyVal = (Map) operationsData.get(i);
			if ("officerId".equals(keyVal.get("label"))) {
				keyVal.put("value", userId);
				break;
			}

		}

		identity.put("metaData", metadata);
		identity.put("operationsData", operationsData);
		/*
		 * Edit creationDate time in identity
		 */
		String creationDate = getCurrUTCDate();
		identity.put("creationDate", creationDate);
		identity.put("registrationId", NEW_REGID);
		packetmetaInfo.put("identity", identity);

		jsonUtil.writeJSONToFile(packetMetaInfoFile, packetmetaInfo);

		List<FieldValueArray> hashSequence1 = new ArrayList<>();

		ArrayList<Object> hashOneList = (ArrayList) identity.get("hashSequence1");

		for (Object element : hashOneList) {
			FieldValueArray fieldValueArrayObj = new FieldValueArray();
			Map map = (Map) element;
			map.get("label");
			fieldValueArrayObj.setLabel((String) map.get("label"));
			List<String> values = (List<String>) map.get("value");
			fieldValueArrayObj.setValue(values);
			hashSequence1.add(fieldValueArrayObj);
		}
		String checksum = "";
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence1,
					packetContentFolder + File.separator + NEW_REGID + "_optional");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String optionalFolder = packetContentFolder + File.separator + NEW_REGID + "_optional";

		try {
			packetDataUtil.writeChecksumToFile(optionalFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void zipPacketContents(String parentDir, String authToken) {
		/*
		 * parentDir is the path of folder containing the packet in decrypted form
		 * parentDir contains a folder named as the new registration ID
		 */
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		PropertiesUtil prop = new PropertiesUtil();

		String inner_container = parentDir + File.separator + this.NEW_REGID + File.separator + this.SUB_FOLDER;

		String foldername = this.NEW_REGID;
		byte[] idSign = zipInnerFolderAndSign(inner_container, foldername + "_id", encryptDecrypt, authToken, prop);
		byte[] evidenceSign = zipInnerFolderAndSign(inner_container, foldername + "_evidence", encryptDecrypt,
				authToken, prop);
		byte[] optionalSign = zipInnerFolderAndSign(inner_container, foldername + "_optional", encryptDecrypt,
				authToken, prop);
		System.out.println(idSign);
		System.out.println(evidenceSign);
		System.out.println(optionalSign);
		try {
			byte[] idEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_id", encryptDecrypt, authToken,
					prop);

			byte[] evidenceEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_evidence",
					encryptDecrypt, authToken, prop);

			byte[] optionalEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_optional",
					encryptDecrypt, authToken, prop);

			String regid = foldername;
			String containerDir = inner_container; // path including source & method

			String idPacket = containerDir + File.separator + regid + "_id.zip";
			String evidencePacket = containerDir + File.separator + regid + "_evidence.zip";
			String optionalPacket = containerDir + File.separator + regid + "_optional.zip";

			String idMetaJson = containerDir + File.separator + regid + "_id.json";
			String evidenceMetaJson = containerDir + File.separator + regid + "_evidence.json";
			String optionalMetaJson = containerDir + File.separator + regid + "_optional.json ";

			updateMetaInfoFile(idMetaJson, idSign, idEncBytes, regid, regid + "_id");
			updateMetaInfoFile(evidenceMetaJson, evidenceSign, evidenceEncBytes, regid, regid + "_evidence");
			updateMetaInfoFile(optionalMetaJson, optionalSign, optionalEncBytes, regid, regid + "_optional");
			/*
			 * Deleting the sub-directories from which zipped packets were created
			 */
			deleteDirectories(containerDir, foldername); // foldername is same as new regid
			zipAndEncryptDirectory(parentDir, foldername, encryptDecrypt, authToken,prop);
			System.out.println("zipped directory, packet got generated at " + parentDir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void zipAndEncryptDirectory(String zippedPacketFolder, String foldername, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) throws Exception {
		File dirToZip = new File(zippedPacketFolder + "/" + foldername);
		File zipFile = new File(zippedPacketFolder + "/" + foldername + ".zip");

		org.zeroturnaround.zip.ZipUtil.pack(dirToZip, zipFile);
		try {
			FileUtils.deleteDirectory(dirToZip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String regid = foldername;

		encryptDecrypt.encryptZippedPacket(regid, zipFile, token, prop);

	}

	private byte[] encryptInnerZippedpacket(String folderPath, String zippedFileName, EncrypterDecrypter encryptDecrypt,
			String authToken, PropertiesUtil prop) throws Exception {
		byte[] encryptedpacket = null;
		File file = new File(folderPath + File.separator + zippedFileName + ".zip");
		String regid = zippedFileName.split("_")[0];
		try {
			encryptedpacket = encryptDecrypt.encryptZippedPacket(regid, file, authToken, prop);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return encryptedpacket;
	}

	private byte[] zipInnerFolderAndSign(String folderPath, String folderToZip, EncrypterDecrypter encryptDecrypt,
			String authToken, PropertiesUtil prop) {

		byte[] sign = encryptDecrypt.zipDirectoryAndSign(folderPath, folderToZip, authToken, prop);
		return sign;
	}

	private void deleteDirectories(String directory, String regid) {

		File dir1 = new File(directory + "/" + regid + "_evidence");
		File dir2 = new File(directory + "/" + regid + "_id");
		File dir3 = new File(directory + "/" + regid + "_optional");

		try {
			FileUtils.deleteDirectory(dir1);
			FileUtils.deleteDirectory(dir2);
			FileUtils.deleteDirectory(dir3);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void updateMetaInfoFile(String metaInfoJsonFile, byte[] signature, byte[] packetEncBytes, String regid,
			String packetname) {
		PacketInfo packetInfo = null;
		JSONUtil jsonUtil = new JSONUtil();
		try {
			packetInfo = jsonUtil.parsePacketMetaInfoFile(metaInfoJsonFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		packetInfo.setId(regid);
		packetInfo.setPacketName(packetname);
		String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
		LocalDateTime localdatetime = LocalDateTime.parse(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN),
				format);
		packetInfo.setCreationDate(localdatetime.toString());
		packetInfo.setSchemaVersion("1.0");
		String hash = new String(org.apache.commons.codec.binary.Base64
				.encodeBase64URLSafeString(HMACUtils.generateHash(packetEncBytes)));
		packetInfo.setEncryptedHash(hash);
		packetInfo.setProcess(PROCESS);
		packetInfo.setSource(SOURCE);
		String signatureEncoded = new String(
				org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(signature));
		System.out.println("signatureEncoded to be writtten to metajson " + signatureEncoded);
		packetInfo.setSignature(signatureEncoded);

		Gson gson = new Gson();
		jsonUtil.writeJsonToFile(gson.toJson(packetInfo), metaInfoJsonFile);

	}

	/**
	 * This method is used for creating token
	 * 
	 * @param tokenType
	 * @return token
	 */
	public String getToken(String tokenType) {
		TokenGeneration generateToken = new TokenGeneration();
		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
		TokenGenerationEntity tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token = generateToken.getToken(tokenEntity);
		return token;
	}

}
