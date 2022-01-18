package io.mosip.registrationProcessor.perf.client;

import java.io.File;

import org.hibernate.Session;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.service.PacketUploadWorker;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;

public class PacketUploadClient {
	
	public void uploadPacket() {
		
		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		
		File parentFile = new File(prop.PARENT_FOLDER);
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
		
		PacketUploadWorker worker = new PacketUploadWorker();
		try {
			worker.syncAndUploadPackets(prop, auth_token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
