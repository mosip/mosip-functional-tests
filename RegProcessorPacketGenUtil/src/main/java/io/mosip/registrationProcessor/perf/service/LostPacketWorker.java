package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class LostPacketWorker {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "LOST";

	public void createLostPacket(PropertiesUtil prop, String auth_token) {
		LostPacketHelper helper = new LostPacketHelper();
		String centerId = "10010";
		String machineId = "10029";
		String userId = "110010";
		String registrationId = helper.generateRegId(centerId, machineId);

		String existing_packet_path = prop.LOST_UIN_PATH;
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();

		String PARENT_FOLDER_PATH = prop.NEW_PACKET_WORKER_PATH + "temp";
		String packetFolder = PARENT_FOLDER_PATH + File.separator + registrationId;

		String packetContentFolder = PARENT_FOLDER_PATH + File.separator + registrationId + File.separator + SOURCE
				+ File.separator + PROCESS;
		helper.copyPacketToWorkLocation(existing_packet_path, PARENT_FOLDER_PATH, registrationId);

		String checksum = helper.modifyPacketMetaInfoAndComputeChecksums();
		helper.computeChecksumForEvidencePacket();
		helper.computeChecksumForOptionalPacket();

		String zippedPacketFolder = prop.NEW_PACKET_WORKER_PATH + "zipped";
		new File(zippedPacketFolder).mkdir();
		zipPacketContents(packetFolder, zippedPacketFolder, encryptDecrypt, auth_token, prop);
		String checksumslogFilePath = prop.CHECKSUM_LOGFILE_PATH;
		String packetGenPath = prop.NEW_PACKET_WORKER_PATH + "zipped" + File.separator;
		logRegIdCheckSumToFile(packetGenPath, checksumslogFilePath, registrationId, checksum);
		String regidLogFilePath = prop.REGID_LOG_FILE;
		try {
			logRegIdsToFile(regidLogFilePath, registrationId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private synchronized void logRegIdsToFile(String logFilePath, String regId) throws Exception {
		PacketDemoDataUtil packetDataUtil = new PacketDemoDataUtil();
		packetDataUtil.logRegIdsToFile(logFilePath, regId);
	}

	public synchronized void logRegIdCheckSumToFile(String packetGenPath, String logFilePath, String regId,
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

	private void zipPacketContents(String packetFolderPath, String zippedPacketFolder,
			EncrypterDecrypter encryptDecrypt, String token, PropertiesUtil prop) {
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
		}
		/*
		 * Deleting the sub-directories from which zipped packets were created
		 */
		deleteDirectories(destFolder, foldername);
		try {
			zipAndEncryptDirectory(zippedPacketFolder, foldername, encryptDecrypt, token, prop);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private byte[] zipInnerFolderAndSign(String folderPath, String folderToZip, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) {

		// File file = new File()

		byte[] sign = encryptDecrypt.zipDirectoryAndSign(folderPath, folderToZip, token, prop);

		return sign;

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

	private byte[] encryptInnerZippedpacket(String folderPath, String zippedFileName, EncrypterDecrypter encryptDecrypt,
			String token, PropertiesUtil prop) throws Exception {

		String regid = zippedFileName.split("_")[0];
		File file = new File(folderPath + File.separator + zippedFileName + ".zip");
		byte[] encryptedpacket = null;
		try {

			encryptedpacket = encryptDecrypt.encryptZippedPacket(regid, file, token, prop);

			// encryptedpacket = encryptDecrypt.encryptZippedPacket_0(folderPath,
			// zippedFileName, token, prop);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return encryptedpacket;

	}

	private void copyDirectory(File srcDir, File destDir) {
		try {
			FileUtils.copyDirectory(srcDir, destDir);
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

}
