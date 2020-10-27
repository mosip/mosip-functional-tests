package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import io.mosip.dbdto.CryptomanagerDto;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class DecryptPacketHelper {

	private static Logger logger = Logger.getLogger(DecryptPacketHelper.class);

	public DecryptPacketHelper() {
	}

	public File decryptPacket(String existing_regid, File fileToOperate, String tempFolder, String token,
			PropertiesUtil prop) throws Exception {
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		CryptomanagerDto decryptionReq = encryptDecrypt.generateCryptographicDataToDecryptZippedFile(existing_regid,
				fileToOperate);
		try {
			File decryptedPacket = encryptDecrypt.decryptFile(token, decryptionReq, tempFolder, fileToOperate.getName(),
					prop);
			System.out.println("decryptedPacket " + decryptedPacket.getAbsolutePath());
			return decryptedPacket;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while decrypting: " + e.getMessage());
			throw new Exception(e);
		}
	}

	public void decryptFiles(String regid, String token, String extractedPath, PropertiesUtil prop) {
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		File filePath = new File(extractedPath);
		File[] listOfFiles = filePath.listFiles();
		List<File> filesToDelete = new ArrayList<>();
		int counter = 0;
		try {
			for (File file : listOfFiles) {
				String fileName = file.getName();
				if (fileName.endsWith(".json")) {

				} else if (fileName.contains(".zip")) {
					filesToDelete.add(file);
					CryptomanagerDto decryptionReq = encryptDecrypt.generateCryptographicDataToDecryptZippedFile(regid,
							file);
					File decryptedPacket = encryptDecrypt.decryptFile(token, decryptionReq, extractedPath,
							file.getName(), prop);
					System.out.println("decryptedPacket " + decryptedPacket.getAbsolutePath());
					// counter++;
					System.out.println("decrypted " + ++counter + "th packet");
				}
			}
			filesToDelete.forEach((fileD) -> {
				try {
					FileUtils.forceDelete(fileD);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
