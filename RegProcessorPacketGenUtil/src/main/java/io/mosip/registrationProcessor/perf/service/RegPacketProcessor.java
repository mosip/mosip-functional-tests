package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class RegPacketProcessor {

	public RegPacketProcessor() {

	}

	public void processValidPacket(String token, PropertiesUtil prop, Session session) {

		for (int i = 0; i < prop.NUMBER_OF_TEST_PACKETS; i++)
			try {
				processAPacketToGenerateNewpacket(token, prop, session);
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

	private void processAPacketToGenerateNewpacket(String token, PropertiesUtil prop, Session session)
			throws Exception {

		Properties folderPath = new Properties();
		try {
			FileReader reader = new FileReader(
					new File(System.getProperty("user.dir") + "/src/config/folderPaths.properties"));
			folderPath.load(reader);
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// String newPacketFolderPath = folderPath.getProperty("newPacketFolderPath");

//		String checksumslogFilePath = folderPath.getProperty("checksumslogFilePath");
		String newPacketFolderPath = prop.NEW_PACKET_FOLDER_PATH;
		// String validPacketForPacketGeneration =
		// PropertiesUtil.VALID_PACKET_PATH_FOR_PACKET_GENERATION;
		String checksumslogFilePath = prop.CHECKSUM_LOGFILE_PATH;
		/*
		 * Log the checksum for registration ID to a file, this file will be lated used
		 * to create request data for packet sync API
		 */
		String packetGenPath = newPacketFolderPath + "zipped" + File.separator;
		new File(packetGenPath).mkdirs();

		TweakRegProcPackets packetCreater = new TweakRegProcPackets();
		/*
		 * Decrypt a valid encrypted packet
		 */
		String validPacketForPacketGeneration = folderPath.getProperty("validPacketForPacketGeneration");
		if (prop.DECRYPTION_NEEDED)
			packetCreater.decryptPacket(token, validPacketForPacketGeneration, prop);
		/*
		 * decrypted packet will be present at decryptedPacket path
		 */
		// packetCreater.copyDecryptedpacketToNewLocation(newPacketFolderPath);
		/*
		 * Modify the content of ID.json file present in the decrypted packet
		 */

		byte[] encryptedFile = packetCreater.modifyDemoDataOfDecryptedPacket(newPacketFolderPath, packetGenPath,
				checksumslogFilePath, token, prop, session);
//		return encryptedFile;

		// packetCreater.logRegIdCheckSumToFile(packetGenPath, checksumslogFilePath);
		// packetCreater.deleteFolderInTempDir(newPacketFolderPath + "temp");
	}

}
