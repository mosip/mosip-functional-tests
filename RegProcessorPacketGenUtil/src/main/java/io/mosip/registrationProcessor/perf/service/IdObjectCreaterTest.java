package io.mosip.registrationProcessor.perf.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;

public class IdObjectCreaterTest {

	public static void main(String[] args) {

		String process = "NEW";
		IdObjectCreater idObjectCreater = new IdObjectCreater();
		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
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
		// Session session = new DBUtil().obtainSession(prop);
		// idObjectCreater.createIDObject(prop, auth_token, session, process);
		PreregistrationSyncService preRegSyncService = new PreregistrationSyncService();
		preRegSyncService.loadPreregData(prop, auth_token);

	}

}
