package io.mosip.registrationProcessor.perf.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.service.LostPacketWorker;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;

public class LostPacketClient {

	public void createLostPacket() {

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		File parentFile = new File(prop.PARENT_FOLDER);
//		File[] filesToDelete = parentFile.listFiles();
//		for (File f : filesToDelete) {
//			try {
//				FileUtils.forceDelete(f);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		TokenGeneration generateToken = new TokenGeneration();
		String TOKEN_TYPE = "syncTokenGenerationFilePath";
		String tokenGenerationFilePath = generateToken.readPropertyFile(TOKEN_TYPE);
		String auth_token = "";
		if (prop.AUTH_TYPE_CLIENTID_SECRETKEY) {
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			tokenEntity = generateToken.createTokenGeneratorDtoForClientIdSecretKey(tokenGenerationFilePath);
			auth_token = generateToken.getAuthTokenForClientIdSecretKey(tokenEntity, prop);
		} else {
			UsernamePwdTokenEntity tokenEntity1 = generateToken
					.createTokenGeneratorDtoForUserIdPassword(tokenGenerationFilePath);
			auth_token = generateToken.getAuthTokenForUsernamePassword(tokenEntity1, prop);

		}

		LostPacketWorker worker = new LostPacketWorker();
		worker.createLostPacket(prop, auth_token);
	}
}
