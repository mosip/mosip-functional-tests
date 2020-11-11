package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import io.mosip.registration.processor.perf.packet.dto.FieldValueArray;
import io.mosip.registration.processor.perf.packet.dto.Identity;
import io.mosip.registration.processor.perf.packet.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.perf.regPacket.dto.PhilIdentityObject;
import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.IndividualType;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class NewPacketWorker {
	private static Logger logger = Logger.getLogger(NewPacketWorker.class);
	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";

	public void generateNewpacket(Session session, PropertiesUtil prop, String auth_token) throws Exception {

		String newPacketFolderPath = prop.NEW_PACKET_WORKER_PATH;

		String checksumslogFilePath = prop.CHECKSUM_LOGFILE_PATH;
		String packetGenPath = newPacketFolderPath + "zipped" + File.separator;
		new File(packetGenPath).mkdirs();
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		JSONUtil jsonUtil = new JSONUtil();
		Gson gson = new Gson();

		String centerId = "10010";
		String machineId = "10029";
		String userId = "110010";
		String registrationId = generateRegId(centerId, machineId);
		String PARENT_FOLDER_PATH = prop.NEW_PACKET_WORKER_PATH + "temp";
		NewPacketHelper helper = new NewPacketHelper(registrationId, PARENT_FOLDER_PATH);

		String packetFolder = PARENT_FOLDER_PATH + File.separator + registrationId;

		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;
		String existing_packet_path = "";
		if (!prop.IS_CHILD_PACKET)
			existing_packet_path = prop.NEW_PACKET_PATH;
		else
			existing_packet_path = prop.CHILD_PACKET_PATH;

		helper.copyPacketToWorkLocation(existing_packet_path);

		String identityFolder = packetContentFolder + File.separator + registrationId + "_id";
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
//		if (!prop.IDOBJECT_TYPE_PHIL) {
//			String idJsonPath = packetContentFolder + File.separator + registrationId + "_id" + File.separator
//					+ "ID.json";
//			RegProcIdDto updatedPacketDemoDto = packetDataUtil.modifyDemographicData(prop, session, idJsonPath);
//			jsonUtil.writeJsonToFile(gson.toJson(updatedPacketDemoDto), idJsonPath);
//
//		} else {
//			String idJsonPath = packetContentFolder + File.separator + registrationId + "_id" + File.separator
//					+ "ID.json";
//			PhilIdentityObject updatedIdObject = packetDataUtil.modifyPhilDemographicData(prop, session, idJsonPath);
//			jsonUtil.writeJsonToFile(gson.toJson(updatedIdObject), idJsonPath);
//		}

		IdObjectCreater idObjectCreater = new IdObjectCreater();
		String individual_type = "";
		if (prop.IS_CHILD_PACKET) {
			individual_type = IndividualType.NEW_CHILD.getIndividualType();
		} else {
			individual_type = IndividualType.NEW_ADULT.getIndividualType();
		}
		String identityJson = idObjectCreater.createIDObject(prop, auth_token, session, PROCESS, individual_type);
		String idJsonPath = packetContentFolder + File.separator + registrationId + "_id" + File.separator + "ID.json";
		jsonUtil.writeJsonToFile(identityJson, idJsonPath);
		helper.updateIDSchemaInIdentityFiles(prop);
		
		if(prop.SYNC_PREREG) {
			PreregistrationSyncHelper preregSyncHelper = new PreregistrationSyncHelper();
			preregSyncHelper.copyDocumentsPreregpacketToRegPacket(idJsonPath, identityFolder, prop);
		}
		

		String packetMetaInfoFile = packetContentFolder + File.separator + registrationId + "_id" + File.separator
				+ "packet_meta_info.json";
		packetDataUtil.modifyPacketMetaInfo(packetMetaInfoFile, registrationId, centerId, machineId, userId);

		System.out.println("packet meta info modified...._....");

		PacketMetaInfo jsonObj = null;
		try {
			jsonObj = new JSONUtil().parseMetaInfoFile(packetMetaInfoFile);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Exception(e1);
		}
		Identity identity = jsonObj.getIdentity();
		List<FieldValueArray> hashSequence = identity.getHashSequence1();
		String checksum = "";
		@SuppressWarnings("unused")
		String encryptedTempFile;
		try {
			checksum = encryptDecrypt.generateCheckSum(hashSequence,
					packetContentFolder + File.separator + registrationId + "_id");
			System.out.println("Generated checksum is " + checksum);
			// checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" modifyDemoDataOfDecryptedPacket generateChecksum: " + e.getMessage());
			throw new Exception(e);
		}

		helper.computeChecksumForEvidencePacket();
		helper.computeChecksumForOptionalPacket();

		try {
			packetDataUtil.writeChecksumToFile(identityFolder + "/packet_data_hash.txt", checksum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(" modifyDemoDataOfDecryptedPacket writeChecksumToFile: " + e.getMessage());
		}

		String zippedPacketFolder = prop.NEW_PACKET_WORKER_PATH + "zipped";
		new File(zippedPacketFolder).mkdir();
		helper.zipPacketContents(packetFolder, zippedPacketFolder, encryptDecrypt, auth_token, prop);

		helper.logRegIdCheckSumToFile(packetGenPath, checksumslogFilePath, registrationId, checksum);
		String regidLogFilePath = prop.REGID_LOG_FILE;
		helper.logRegIdsToFile(regidLogFilePath, registrationId);

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
