/**
 * 
 */
package io.mosip.registrationProcessor.perf.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.perf.service.RegPacketProcessor;
import io.mosip.registrationProcessor.perf.util.CSVUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;

/**
 * @author Gaurav Sharan
 *
 */
public class PacketCreationClient implements Runnable {
	private static Logger logger = Logger.getLogger(PacketCreationClient.class);
	private String authToken;

	/**
	 * 
	 */
	public PacketCreationClient(String authToken) {
		this.authToken = authToken;
	}

	@Override
	public void run() {
		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
//		File parentFile = new File(prop.PARENT_FOLDER);
//		File[] filesToDelete = parentFile.listFiles();
//		for (File f : filesToDelete) {
//			try {
//				FileUtils.forceDelete(f);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		Session session = new DBUtil().obtainSession(prop);
		try {
			processRegPacket(prop, session, this.authToken);
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processRegPacket(PropertiesUtil prop, Session session, String authToken) throws IOException {

		RegPacketProcessor regpacketProcessor = new RegPacketProcessor();
//		String token = getToken("syncTokenGenerationFilePath", prop);
//		logger.debug("token :" + token);
//		logger.info("token :" + token);
		System.out.println("token :" + authToken);
		// regpacketProcessor.processValidPacket(token, prop, session);
		regpacketProcessor.processValidPacket(authToken, prop, session);

	}

	private String getToken(String tokenType, PropertiesUtil prop) throws IOException {

		TokenGeneration generateToken = new TokenGeneration();
		TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token = generateToken.getToken(tokenEntity, prop);
		return token;

	}

}
