package io.mosip.registrationProcessor.perf.service;

import java.io.File;

import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class DecryptPacketWorker {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";

	public void decryptPacket(PropertiesUtil prop, String authToken) throws Exception {

		this.PROCESS = prop.PROCESS_DECRYPTION;

		DecryptPacketHelper helper = new DecryptPacketHelper();
		// String decryptionDir = prop.DECRYPTION_PACKET_PATH;
		File decryptionDir = new File(prop.DECRYPTION_PACKET_PATH);
		String registrationId = "";
		String centerId = "";
		String machineId = "";
		File[] files = decryptionDir.listFiles();
		File packetToDecrypt = null;
		for (File file : files) {
			if (file.getName().contains(".zip")) {
				packetToDecrypt = file;
				registrationId = file.getName().substring(0, file.getName().lastIndexOf(".zip"));
				centerId = file.getName().substring(0, 5);
				machineId = file.getName().substring(5, 10);
			}
		}

		String decryption_path = prop.DECRYPTION_PACKET_PATH + File.separator + prop.DECRYPTION_FOLDER;
		String extractedPath = "";
		if (null != packetToDecrypt) {
			File decryptedPacket = null;
			try {
				decryptedPacket = helper.decryptPacket(registrationId, packetToDecrypt, decryption_path, authToken,
						prop);
				extractedPath = decryptedPacket.getAbsolutePath();
				String INNERPATH_EXTRACTED = extractedPath + File.separator + SOURCE + File.separator + PROCESS; // TODO
				helper.decryptFiles(registrationId, authToken, INNERPATH_EXTRACTED, prop);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception(e);
			}

		}

	}

}
