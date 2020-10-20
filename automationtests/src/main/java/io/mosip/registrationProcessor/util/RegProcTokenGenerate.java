package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.util.TokenGeneration;

public class RegProcTokenGenerate {
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	RegProcApiRequests apiRequests = new RegProcApiRequests();

	public String getRegProcAuthToken() {
		String tokenGenerationProperties = generateToken.readPropertyFile("syncTokenGenerationFilePath");
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String regProcAuthToken = generateToken.getToken(tokenEntity);
		return regProcAuthToken;
	}

	public String getAdminRegProcAuthToken() {
		TokenGenerationEntity adminTokenEntity = new TokenGenerationEntity();
		String adminTokenGenerationProperties = generateToken.readPropertyFile("getStatusTokenGenerationFilePath");
		adminTokenEntity = generateToken.createTokenGeneratorDto(adminTokenGenerationProperties);
		String adminRegProcAuthToken = generateToken.getToken(adminTokenEntity);
		return adminRegProcAuthToken;
	}
	
	public String getAuthTokenUsingClientIdSecretKey() throws Exception {
		String authToken = "";
		String tokenGenerationFilePath = generateToken.readPropertyFile("syncTokenGenerationFilePath");
		Properties prop = new Properties();
		String propertyFilePath = apiRequests.getResourcePath() + tokenGenerationFilePath;
		
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
			authToken = generateToken.getTokenForClientIdSecretKey(prop);
			return authToken;
		} catch ( IOException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
		
		
	}
}
