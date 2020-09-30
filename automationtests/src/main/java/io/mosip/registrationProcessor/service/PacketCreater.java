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
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.dbaccess.PreregDB;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registrationProcessor.regpacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.FieldValueArray;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.Identity;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;
import io.mosip.util.HMACUtils;
import io.mosip.util.TokenGeneration;

public class PacketCreater {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";
	private String SUB_FOLDER;
	private String OLD_REGID;
	private String NEW_REGID;
	private String EXISTING_PACKET_DIR;

	private String TEMP_PATH_SECTION = File.separator + "temp";

	private static Logger logger = Logger.getLogger(PacketCreater.class);

	static Session session;
	public static SessionFactory factory;

	private PacketDemoDataUtil packetDataUtil;

	RegProcApiRequests apiRequests = new RegProcApiRequests();
	String validToken = "";
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();

	public PacketCreater() {
		this.SUB_FOLDER = SOURCE + File.separator + PROCESS;
		packetDataUtil = new PacketDemoDataUtil();

	}

	private Session getDataBaseConnection_0(String dbName) throws Exception {
		try {
			String env = System.getProperty("env.user");
			String dbFileName = dbName + "_" + env + ".cfg.xml";
			dbFileName = "hibernate.cfg.xml";
			String dbConfigXml = MosipTestRunner.getGlobalResourcePath() + File.separator + "regProc" + File.separator
					+ "dbFiles" + File.separator + dbFileName;
			String dbConfigFile = System.getProperty("user.dir") + "\\" + "src\\main\\resources\\" + dbFileName;
			dbConfigFile = System.getProperty("user.dir") + "\\" + "src\\main\\resources\\" + dbFileName;
			System.out.println("dbconfig-xml:- " + dbConfigXml);
			System.out.println("dbconfig-file:- " + dbConfigFile);
			SessionFactory sessionFactory = new Configuration().configure(dbConfigXml).buildSessionFactory();
			sessionFactory.openSession();
			return session;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex);
		}
	}

	public static Session getDataBaseConnection(String dbName) {
		String env = System.getProperty("env.user");
		String dbConfigXml = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbConfig.xml";
		String dbPropsPath = MosipTestRunner.getGlobalResourcePath() + "/dbFiles/dbProps" + env + ".properties";

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
			factory = config.buildSessionFactory();
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

	public void generateRegId(String centerId, String machineId) {
		String regID = "";

		Date currDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		cal.add(Calendar.MINUTE, -332);
		Date date = cal.getTime();

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
		timeStamp.replaceAll(".", "");
		int n = 10000 + new Random().nextInt(90000);
		String randomNumber = String.valueOf(n);

		regID = centerId + machineId + randomNumber + timeStamp;
		this.NEW_REGID = regID;
	}

	private void copyFolderContentsToModify(File srcPath) {
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

	private void modifyDemographicAndPacketMetaInfoFile(String packetContentFolder, String centerId, String machineId,
			String userId) {
		JSONUtil jsonUtil = new JSONUtil();
		Gson gson = new Gson();
		String identityFolder = packetContentFolder + File.separator + NEW_REGID + "_id";

		PropertiesUtil prop = new PropertiesUtil();
		String idJsonPath = packetContentFolder + File.separator + NEW_REGID + "_id" + File.separator + "ID.json";
		String dbName = "masterdata";
		try {
			session = getDataBaseConnection(dbName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			RegProcIdDto updatedPacketDemoDto = packetDataUtil.modifyDemographicData(prop, session, idJsonPath);
			jsonUtil.writeJsonToFile(gson.toJson(updatedPacketDemoDto), idJsonPath);

			String packetMetaInfoFile = packetContentFolder + File.separator + NEW_REGID + "_id" + File.separator
					+ "packet_meta_info.json";
			packetDataUtil.modifyPacketMetaInfo(packetMetaInfoFile, NEW_REGID, centerId, machineId, userId);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void emptyTempLocation() {
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

	public String createPacketFromExistingPacket(String parentDir, String existingPacketPath) {
		String created_packet = "";
		System.out.println("parent dir is " + parentDir);
		// A file separator char is already added in end of parentDir variable's value.
		EXISTING_PACKET_DIR = parentDir + existingPacketPath;
		System.out.println("existingPacketDir :- " + EXISTING_PACKET_DIR);

		File packetParentDir = new File(EXISTING_PACKET_DIR);

		String fileToCopy = null;
		for (File file : packetParentDir.listFiles()) {
			if (file.getName().length() == 29) {
				fileToCopy = file.getAbsolutePath();
				OLD_REGID = file.getName();
			}
		}
		System.out.println("fileToCOpy " + fileToCopy);
		System.out.println("oldRegId :- " + OLD_REGID);
		System.out.println();
		String centerId = OLD_REGID.substring(0, 5);
		String machineId = OLD_REGID.substring(5, 10);
		String userId = "110119";
		try {
			generateRegId(centerId, machineId);

			emptyTempLocation();
			copyFolderContentsToModify(new File(fileToCopy + File.separator + this.SUB_FOLDER));

			System.out.println("folder contents copied");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * tempDir is the location to modify content of file and zip those to generate a
		 * zipped packet
		 */
		String tempDir = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;
		File tempDirFile = new File(tempDir);
		String destPacketPath = tempDir + File.separator + NEW_REGID + File.separator + this.SUB_FOLDER;

		modifyDemographicAndPacketMetaInfoFile(destPacketPath, centerId, machineId, userId);
		String packetMetaInfoFile = destPacketPath + File.separator + NEW_REGID + "_id" + File.separator
				+ "packet_meta_info.json";
		PacketMetaInfo jsonObj = null;
		try {
			jsonObj = new JSONUtil().parseMetaInfoFile(packetMetaInfoFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Identity identity = jsonObj.getIdentity();
		List<FieldValueArray> hashSequence = identity.getHashSequence1();
		String checksum = "";
		@SuppressWarnings("unused")
		String encryptedTempFile;
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		String identityFolder = destPacketPath + File.separator + NEW_REGID + "_id";
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence, identityFolder);
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" modifyDemoDataOfDecryptedPacket generateChecksum: " + e.getMessage());
		}

		try {
			packetDataUtil.writeChecksumToFile(identityFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(" modifyDemoDataOfDecryptedPacket writeChecksumToFile: " + e.getMessage());
		}

		// new File(zippedDirPath).mkdir();

		validToken = getToken("getStatusTokenGenerationFilePath");
		boolean tokenStatus = apiRequests.validateToken(validToken);
		while (!tokenStatus) {
			validToken = getToken("getStatusTokenGenerationFilePath");
			tokenStatus = apiRequests.validateToken(validToken);
		}

		String parentFolder = "";
		String innerDir = parentFolder;

		zipPacketContents(tempDir);
		created_packet = tempDir + File.separator + NEW_REGID + ".zip";
		try {
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return created_packet;
	}

	private void zipPacketContents(String parentDir) {
		/*
		 * parentDir is the path of folder containing the packet in decrypted form
		 * parentDir contains a folder named as the new registration ID
		 */
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		PropertiesUtil prop = new PropertiesUtil();

		String inner_container = parentDir + File.separator + this.NEW_REGID + File.separator + this.SUB_FOLDER;

		String foldername = this.NEW_REGID;
		byte[] idSign = zipInnerFolderAndSign(inner_container, foldername + "_id", encryptDecrypt, prop);
		byte[] evidenceSign = zipInnerFolderAndSign(inner_container, foldername + "_evidence", encryptDecrypt, prop);
		byte[] optionalSign = zipInnerFolderAndSign(inner_container, foldername + "_optional", encryptDecrypt, prop);
		System.out.println(idSign);
		System.out.println(evidenceSign);
		System.out.println(optionalSign);

		byte[] idEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_id", encryptDecrypt, prop);

		byte[] evidenceEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_evidence", encryptDecrypt,
				prop);

		byte[] optionalEncBytes = encryptInnerZippedpacket(inner_container, foldername + "_optional", encryptDecrypt,
				prop);

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
		zipDirectory(parentDir, foldername);
		System.out.println("zipped directory, packet got generated at " + parentDir);

	}

	private void zipDirectory(String zippedPacketFolder, String foldername) {

		File dirToZip = new File(zippedPacketFolder + "/" + foldername);
		File zipFile = new File(zippedPacketFolder + "/" + foldername + ".zip");

		org.zeroturnaround.zip.ZipUtil.pack(dirToZip, zipFile);
		try {
			FileUtils.deleteDirectory(dirToZip);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	private byte[] encryptInnerZippedpacket(String folderPath, String zippedFileName, EncrypterDecrypter encryptDecrypt,
			PropertiesUtil prop) {
		File file = new File(folderPath + File.separator + zippedFileName + ".zip");

		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			return bytes;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		try {
//			byte[] encBytes = encryptDecrypt.encryptZippedPacket(folderPath, zippedFileName, token, prop);
//
//			return encBytes;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}

	private byte[] zipInnerFolderAndSign(String folderPath, String folderToZip, EncrypterDecrypter encryptDecrypt,
			PropertiesUtil prop) {

		byte[] sign = encryptDecrypt.zipDirectoryAndSign(folderPath, folderToZip, validToken, prop);
		return sign;
	}

	/**
	 * This method is used for creating token
	 * 
	 * @param tokenType
	 * @return token
	 */
	public String getToken(String tokenType) {
		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token = generateToken.getToken(tokenEntity);
		return token;
	}

}
