package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.perf.packet.dto.FieldValueArray;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class NewPacketHelper {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";
	private String PARENT_FOLDER_PATH;
	private String registrationId;

	public NewPacketHelper(String registrationId, String worker_path) {
		this.registrationId = registrationId;
		this.PARENT_FOLDER_PATH = worker_path;
	}

	public NewPacketHelper() {

	}

	@SuppressWarnings("unused")
	public void copyPacketToWorkLocation(String existing_packet_path) {

		File existing_packet_file = new File(existing_packet_path);
		File existing_packet = null;
		if (existing_packet_file.listFiles().length == 1) {
			for (File file : new File(existing_packet_path).listFiles()) {
				existing_packet = file;
			}
		}

		String originalRegId = existing_packet.getName();

		String packetFolder = PARENT_FOLDER_PATH + File.separator + registrationId;
		File packetFile = new File(packetFolder);
		if (packetFile.exists()) {
			try {
				FileUtils.deleteDirectory(packetFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
//				+ File.separator + PROCESS;

		String srcPath = existing_packet_path + "/" + originalRegId;
		srcPath += File.separator + SOURCE + File.separator + PROCESS;

		String destinationPath = PARENT_FOLDER_PATH + File.separator + registrationId;
		destinationPath += File.separator + SOURCE + File.separator + PROCESS;
		File destDir = new File(destinationPath);
		if (!destDir.exists())
			destDir.mkdir();
		String evidenceSrcPath = srcPath + File.separator + originalRegId + "_evidence";
		String idSrcPath = srcPath + File.separator + originalRegId + "_id";
		String optionalSrcPath = srcPath + File.separator + originalRegId + "_optional";

		String destOptionalPath = destinationPath + File.separator + registrationId + "_optional";
		String destIdPath = destinationPath + File.separator + registrationId + "_id";
		String destEvidencePath = destinationPath + File.separator + registrationId + "_evidence";

		new File(destOptionalPath).mkdir();
		new File(destIdPath).mkdir();
		new File(destEvidencePath).mkdir();

		copyDirectory(new File(evidenceSrcPath), new File(destEvidencePath));
		copyDirectory(new File(idSrcPath), new File(destIdPath));
		copyDirectory(new File(optionalSrcPath), new File(destOptionalPath));

		String srcIdJsonFile = srcPath + File.separator + originalRegId + "_id.json";
		String srcEvidenceJsonFile = srcPath + File.separator + originalRegId + "_evidence.json";
		String srcOptionalJsonFile = srcPath + File.separator + originalRegId + "_optional.json";

		String destIdJsonFile = destinationPath + File.separator + registrationId + "_id.json";
		String destEvidenceJsonFile = destinationPath + File.separator + registrationId + "_evidence.json";
		String destOptionalJsonFile = destinationPath + File.separator + registrationId + "_optional.json";

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void computeChecksumForEvidencePacket() {

		String centerId = registrationId.substring(0, 5);
		String machineId = registrationId.substring(5, 10);
		String userId = "110010";

		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;
		String packetMetaInfoFile = packetContentFolder + File.separator + registrationId + "_evidence" + File.separator
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
				keyVal.put("value", registrationId);
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
		identity.put("registrationId", registrationId);
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
					packetContentFolder + File.separator + registrationId + "_evidence");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String evidenceFolder = packetContentFolder + File.separator + registrationId + "_evidence";

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void computeChecksumForOptionalPacket() {
		String centerId = registrationId.substring(0, 5);
		String machineId = registrationId.substring(5, 10);
		String userId = "110010";

		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;
		String packetMetaInfoFile = packetContentFolder + File.separator + registrationId + "_optional" + File.separator
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
				keyVal.put("value", registrationId);
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
		identity.put("registrationId", registrationId);
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
					packetContentFolder + File.separator + registrationId + "_optional");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String optionalFolder = packetContentFolder + File.separator + registrationId + "_optional";

		try {
			packetDataUtil.writeChecksumToFile(optionalFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String generateRegId(String centerId, String machineId) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currUTCTime = f.format(new Date());

		int n = 10000 + new Random().nextInt(90000);
		String randomNumber = String.valueOf(n);

		String regID = centerId + machineId + randomNumber + currUTCTime;
		return regID;
	}

	synchronized void logRegIdCheckSumToFile(String packetGenPath, String logFilePath, String regId,
			String checksumStr) {
		String generatedPacket = packetGenPath + regId + ".zip";
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		File f = new File(generatedPacket);
		if (f.exists() && f.isFile()) {
			long sizeInBytes = f.length();
			String center_machine_refID = regId.substring(0, 5) + "_" + regId.substring(5, 10);
			packetDataUtil.logRegIdCheckSumToFile(logFilePath, regId, checksumStr, sizeInBytes, center_machine_refID,
					PROCESS);
			// new SyncRequestCreater().createSyncRequest(regId, checksumStr, sizeInBytes);
		}

	}

	synchronized void logRegIdsToFile(String logFilePath, String regId) throws Exception {
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		packetDataUtil.logRegIdsToFile(logFilePath, regId);
	}

	@SuppressWarnings("unused")
	void zipPacketContents(String packetFolderPath, String zippedPacketFolder, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) throws Exception {
		String foldername = packetFolderPath.substring(packetFolderPath.length() - 29);
		System.out.println(foldername);

		String subpath = File.separator + SOURCE + File.separator + PROCESS;
		File srcDir = new File(packetFolderPath);
		File destDir = new File(zippedPacketFolder + File.separator + foldername);

		copyDirectory(srcDir, destDir);

		String destFolder = zippedPacketFolder + File.separator + foldername + subpath;
		new File(destFolder).mkdir();

		byte[] idSign = zipInnerFolderAndSign(destFolder, foldername + "_id", encryptDecrypt, token, prop);
		byte[] evidenceSign = zipInnerFolderAndSign(destFolder, foldername + "_evidence", encryptDecrypt, token, prop);
		byte[] optionalSign = zipInnerFolderAndSign(destFolder, foldername + "_optional", encryptDecrypt, token, prop);
		System.out.println(idSign);
		System.out.println(evidenceSign);
		System.out.println(optionalSign);
		try {
			byte[] idEncBytes = encryptInnerZippedpacket(destFolder, foldername + "_id", encryptDecrypt, token, prop);

			byte[] evidenceEncBytes = encryptInnerZippedpacket(destFolder, foldername + "_evidence", encryptDecrypt,
					token, prop);

			byte[] optionalEncBytes = encryptInnerZippedpacket(destFolder, foldername + "_optional", encryptDecrypt,
					token, prop);

			String regid = foldername;
			String containerDir = destFolder; // path including source & method

			// zipDirectory(zippedPacketFolder, foldername);

			String idPacket = containerDir + File.separator + regid + "_id.zip";
			String evidencePacket = containerDir + File.separator + regid + "_evidence.zip";
			String optionalPacket = containerDir + File.separator + regid + "_optional.zip";

			String idMetaJson = containerDir + File.separator + regid + "_id.json";
			String evidenceMetaJson = containerDir + File.separator + regid + "_evidence.json";
			String optionalMetaJson = containerDir + File.separator + regid + "_optional.json ";

			// updatePacketMetaInfoIdFile(encryptDecrypt, idEncBytes, idPacket, idMetaJson,
			// regid, regid + "_id", token, prop);

			updateMetaInfoFile(idMetaJson, idSign, idEncBytes, regid, regid + "_id");
			updateMetaInfoFile(evidenceMetaJson, evidenceSign, evidenceEncBytes, regid, regid + "_evidence");
			updateMetaInfoFile(optionalMetaJson, optionalSign, optionalEncBytes, regid, regid + "_optional");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		/*
		 * Deleting the sub-directories from which zipped packets were created
		 */
		deleteDirectories(destFolder, foldername);
		zipAndEncryptDirectory(zippedPacketFolder, foldername, encryptDecrypt, token, prop);

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

	private byte[] zipInnerFolderAndSign(String folderPath, String folderToZip, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) {

		// File file = new File()

		byte[] sign = encryptDecrypt.zipDirectoryAndSign(folderPath, folderToZip, token, prop);

		return sign;

	}

	private byte[] encryptInnerZippedpacket(String folderPath, String zippedFileName, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) throws Exception {
		byte[] encryptedpacket = null;
		File file = new File(folderPath + File.separator + zippedFileName + ".zip");
		String regid = zippedFileName.split("_")[0];
		try {
			encryptedpacket = encryptDecrypt.encryptZippedPacket(regid, file, token, prop);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return encryptedpacket;

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
		String hash = new String(Base64.encodeBase64URLSafeString(HMACUtils.generateHash(packetEncBytes)));
		packetInfo.setEncryptedHash(hash);
		packetInfo.setProcess(PROCESS);
		packetInfo.setSource(SOURCE);
		String signatureEncoded = new String(Base64.encodeBase64URLSafeString(signature));
		System.out.println("signatureEncoded to be writtten to metajson " + signatureEncoded);
		packetInfo.setSignature(signatureEncoded);

		Gson gson = new Gson();
		jsonUtil.writeJsonToFile(gson.toJson(packetInfo), metaInfoJsonFile);

	}

	public void updateIDSchemaInIdentityFiles(PropertiesUtil prop) {
		String packetPathDir = PARENT_FOLDER_PATH + File.separator + registrationId;
		packetPathDir += File.separator + SOURCE + File.separator + PROCESS;
		String evidencePacket = packetPathDir + File.separator + registrationId + "_evidence";
		String optionalPacket = packetPathDir + File.separator + registrationId + "_optional";

		String filePath1 = evidencePacket + File.separator + "ID.json";
		float idSchemaVersion = Float.parseFloat(prop.ID_SCHEMA_VERSION);
		updateIdentitySchemaInFile(filePath1, idSchemaVersion);
		String filePath2 = optionalPacket + File.separator + "ID.json";
		updateIdentitySchemaInFile(filePath2, idSchemaVersion);
	}

	@SuppressWarnings("rawtypes")
	private void updateIdentitySchemaInFile(String filePath, Float idSchemaVersion) {
		JSONUtil jsonUtil = new JSONUtil();
		JSONObject idJson = jsonUtil.loadJsonFromFile(filePath);
		Map identity = (Map) idJson.get("identity");
		identity.put("IDSchemaVersion", idSchemaVersion);
		idJson.put("identity", identity);
		jsonUtil.writeJsonToFile(idJson.toJSONString(), filePath);
	}
}
