package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import io.mosip.registration.processor.perf.packet.dto.FieldValueArray;
import io.mosip.registration.processor.perf.packet.dto.Identity;
import io.mosip.registration.processor.perf.packet.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.restassured.response.Response;

public class UpdatePacketHelper {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "UPDATE";

	private String registrationId;
	private String PARENT_FOLDER_PATH;

	public void copyPacketToWorkLocation(String existing_packet_path, String worker_path, String newRegId) {
		registrationId = newRegId;
		File existing_packet_file = new File(existing_packet_path);
		File existing_packet = null;
		if (existing_packet_file.listFiles().length == 1) {
			for (File file : new File(existing_packet_path).listFiles()) {
				existing_packet = file;
			}
		}

		String originalRegId = existing_packet.getName();
		PARENT_FOLDER_PATH = worker_path;
		String packetFolder = worker_path + File.separator + newRegId;
		File packetFile = new File(packetFolder);
		if (packetFile.exists()) {
			try {
				FileUtils.deleteDirectory(packetFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String packetContentFolder = worker_path + File.separator + newRegId + File.separator + SOURCE + File.separator
				+ PROCESS;

		String srcPath = existing_packet_path + "/" + originalRegId;
		srcPath += File.separator + SOURCE + File.separator + PROCESS;

		String destinationPath = worker_path + File.separator + newRegId;
		destinationPath += File.separator + SOURCE + File.separator + PROCESS;
		File destDir = new File(destinationPath);
		if (!destDir.exists())
			destDir.mkdir();

		String evidenceSrcPath = srcPath + File.separator + originalRegId + "_evidence";
		String idSrcPath = srcPath + File.separator + originalRegId + "_id";
		String optionalSrcPath = srcPath + File.separator + originalRegId + "_optional";

		String destOptionalPath = destinationPath + File.separator + newRegId + "_optional";
		String destIdPath = destinationPath + File.separator + newRegId + "_id";
		String destEvidencePath = destinationPath + File.separator + newRegId + "_evidence";

		new File(destOptionalPath).mkdir();
		new File(destIdPath).mkdir();
		new File(destEvidencePath).mkdir();

		copyDirectory(new File(evidenceSrcPath), new File(destEvidencePath));
		copyDirectory(new File(idSrcPath), new File(destIdPath));
		copyDirectory(new File(optionalSrcPath), new File(destOptionalPath));

		String srcIdJsonFile = srcPath + File.separator + originalRegId + "_id.json";
		String srcEvidenceJsonFile = srcPath + File.separator + originalRegId + "_evidence.json";
		String srcOptionalJsonFile = srcPath + File.separator + originalRegId + "_optional.json";

		String destIdJsonFile = destinationPath + File.separator + newRegId + "_id.json";
		String destEvidenceJsonFile = destinationPath + File.separator + newRegId + "_evidence.json";
		String destOptionalJsonFile = destinationPath + File.separator + newRegId + "_optional.json";

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

	public void updateUINInFiles(String registrationId, String worker_path, PropertiesUtil prop, String auth_token) {
		RegProcApiRequests regProcAPis = new RegProcApiRequests();
		String idrepo_retrive_by_rid = "/idrepository/v1/identity/idvid/" + prop.REG_ID;

		Response getResponse = regProcAPis.regProcGetIdRepo(idrepo_retrive_by_rid, auth_token, prop);

		System.out.println(getResponse.asString());

		String uin = (String) getResponse.jsonPath().get("response.identity.UIN");

		System.out.println(uin);

		String packetFolder = worker_path + File.separator + registrationId;
		String packetContentFolder = worker_path + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;

		String optionalPath = packetContentFolder + File.separator + registrationId + "_optional";
		String idPath = packetContentFolder + File.separator + registrationId + "_id";
		String evidencePath = packetContentFolder + File.separator + registrationId + "_evidence";

		editIdJson(idPath + "/ID.json", uin);
		editIdJson(optionalPath + "/ID.json", uin);
		editIdJson(evidencePath + "/ID.json", uin);
	}

	private void editIdJson(String idJsonPath, String uin) {

		JSONUtil jsonUtil = new JSONUtil();
		JSONObject jsonObject = jsonUtil.loadJsonFromFile(idJsonPath);
		System.out.println(jsonObject.toJSONString());
		Map mapObj = (Map) jsonObject.get("identity");
		mapObj.put("UIN", uin);
		jsonObject.put("identity", mapObj);
		jsonUtil.writeJsonToFile(jsonObject.toString(), idJsonPath);
	}

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

	public String modifyPacketMetaInfoAndComputeChecksums() {
		String packetFolder = PARENT_FOLDER_PATH + File.separator + registrationId;
		String centerId = registrationId.substring(0, 5);
		String machineId = registrationId.substring(5, 10);
		String userId = "110010";

		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;
		String packetMetaInfoFile = packetContentFolder + File.separator + registrationId + "_id" + File.separator
				+ "packet_meta_info.json";
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		packetDataUtil.modifyPacketMetaInfo(packetMetaInfoFile, registrationId, centerId, machineId, userId);

		JSONObject packetMetaInfo = null;

		packetMetaInfo = (new JSONUtil()).loadJsonFromFile(packetMetaInfoFile);

		Map identityMap = (Map) packetMetaInfo.get("identity");
		ArrayList<Object> hashOneList = (ArrayList) identityMap.get("hashSequence1");

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

		System.out.println(hashSequence1.size());

//		PacketMetaInfo jsonObj = null;
//		try {
//			jsonObj = new JSONUtil().parseMetaInfoFile(packetMetaInfoFile);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		// Identity identity = jsonObj.getIdentity();
		// List<FieldValueArray> hashSequence = identity.getHashSequence1();
		String checksum = "";
		@SuppressWarnings("unused")
		String encryptedTempFile;
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence1,
					packetContentFolder + File.separator + registrationId + "_id");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String identityFolder = packetContentFolder + File.separator + registrationId + "_id";

		try {
			packetDataUtil.writeChecksumToFile(identityFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return checksum;
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

}
