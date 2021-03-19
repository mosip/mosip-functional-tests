package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.dto.RegPacketSyncDto;
import io.mosip.registrationProcessor.perf.util.EncryptData;
import io.mosip.registrationProcessor.perf.util.FileUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.restassured.response.Response;

public class SyncRequestCreater {
	private static Logger logger = Logger.getLogger(SyncRequestCreater.class);
	private final String encrypterURL = "/v1/cryptomanager/encrypt";
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	String validToken = "";

	private FileUtil fileUtil;

	private String packetType;

	public SyncRequestCreater(String packetType) {
		fileUtil = new FileUtil();
		this.packetType = packetType;
	}

	public void createSyncRequestMaster(PropertiesUtil prop) {
		List<String> checksumLines = new ArrayList<>();
		String fileWithPacketChecksum = prop.CHECKSUM_LOGFILE_PATH;
		try {
			checksumLines = fileUtil.readLinesOfFile(fileWithPacketChecksum);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String TOKEN_TYPE = "syncTokenGenerationFilePath";
		String tokenGenerationFilePath = generateToken.readPropertyFile(TOKEN_TYPE);
		String token = "";
		if (prop.AUTH_TYPE_CLIENTID_SECRETKEY) {
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			tokenEntity = generateToken.createTokenGeneratorDtoForClientIdSecretKey(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForClientIdSecretKey(tokenEntity, prop);
		} else {
			UsernamePwdTokenEntity tokenEntity1 = generateToken
					.createTokenGeneratorDtoForUserIdPassword(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForUsernamePassword(tokenEntity1, prop);

		}

		if ("GENERIC".equalsIgnoreCase(this.packetType)) {

			for (String fileLine : checksumLines) {
				String[] literals = fileLine.split(",");
				String regid = literals[0];
				String checksum = literals[1];
				Long fileSize = Long.parseLong(literals[2]);
				String process = literals[4];
				try {
					createSyncRequestForProcess(regid, checksum, fileSize, prop, process, token);
					// break;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}

		} else {
			for (String fileLine : checksumLines) {
				String[] literals = fileLine.split(",");
				String regid = literals[0];
				String checksum = literals[1];
				Long fileSize = Long.parseLong(literals[2]);
				try {
					// createSyncRequest(regid, checksum, fileSize, prop);
					createSyncRequestForProcess(regid, checksum, fileSize, prop, packetType, token);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

	}

	private void createSyncRequestForProcess(String regId, String checksum, Long fileSize, PropertiesUtil prop,
			String process, String token) {

		EncryptData encryptData = new EncryptData();
		JSONObject requestToEncrypt = null;
		// String filePath = PropertiesUtil.NEW_PACKET_FOLDER_PATH + "Generated/" +
		// regId + ".zip";
		String filePath = prop.NEW_PACKET_WORKER_PATH + "zipped/" + regId + ".zip";
		File file = new File(filePath);

		if (file.exists()) {

			RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
			if (file != null) {
				try {
					registrationPacketSyncDto = encryptData.createSyncRequest(file, process);
				} catch (ParseException e) {
					e.printStackTrace();
					logger.error("createSyncRequest createSyncRequest: " + e.getMessage());
				}
				// requestToEncrypt = encryptData.encodeData_0(registrationPacketSyncDto);
			}

			String encryptedData = "";
			try {
				encryptedData = encryptData.encodeAndEncryptSyncRequest(registrationPacketSyncDto, prop, token);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String referenceId = regId.substring(0, 5) + "_" + regId.substring(5, 10);
			RegPacketSyncDto syncDto = new RegPacketSyncDto();
			syncDto.setRegId(regId);
			syncDto.setSyncData(encryptedData);
			syncDto.setPacketPath(filePath);
			syncDto.setReferenceId(referenceId);
			RegProcApiRequests apiRequests = new RegProcApiRequests();
			String timeStamp = getUTCTime().toString();
			String syncApi = "/registrationprocessor/v1/registrationstatus/sync";
			Response syncResponse = apiRequests.regProcSyncRequest(syncApi, encryptedData, referenceId, timeStamp,
					MediaType.APPLICATION_JSON, token, prop);
			fileUtil.logSyncDataToFile(syncDto, prop.SYNCDATA__FILE_PATH);
		}

	}

	public Object getUTCTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now(Clock.systemUTC());
		String utcTime = time.format(dateFormat);
		return utcTime;

	}

//	private String getToken(String tokenType) {
//		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
//		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
//		String token = generateToken.getToken(tokenEntity);
//		return token;
//	}

	public void createSyncRequest(String regId, String checksum, Long fileSize, PropertiesUtil prop, String token) {

//		validToken = getToken("getStatusTokenGenerationFilePath");
//		boolean tokenStatus = apiRequests.validateToken(validToken);
//		while (!tokenStatus) {
//			validToken = getToken("syncTokenGenerationFilePath");
//			tokenStatus = apiRequests.validateToken(validToken);
//		}
		EncryptData encryptData = new EncryptData();
		JSONObject requestToEncrypt = null;
		// String filePath = PropertiesUtil.NEW_PACKET_FOLDER_PATH + "Generated/" +
		// regId + ".zip";
		String filePath = prop.NEW_PACKET_WORKER_PATH + "zipped/" + regId + ".zip";
		File file = new File(filePath);

		if (file.exists()) {

			RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
			if (file != null) {
				try {
					registrationPacketSyncDto = encryptData.createSyncRequest(file, packetType);
				} catch (ParseException e) {
					e.printStackTrace();
					logger.error("createSyncRequest createSyncRequest: " + e.getMessage());
				}
				// requestToEncrypt = encryptData.encodeData_0(registrationPacketSyncDto);
			}

			String encryptedData = "";
			try {
				encryptedData = encryptData.encodeAndEncryptSyncRequest(registrationPacketSyncDto, prop, token);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String referenceId = regId.substring(0, 5) + "_" + regId.substring(5, 10);
			RegPacketSyncDto syncDto = new RegPacketSyncDto();
			syncDto.setRegId(regId);
			syncDto.setSyncData(encryptedData);
			syncDto.setPacketPath(filePath);
			syncDto.setReferenceId(referenceId);
			fileUtil.logSyncDataToFile(syncDto, prop.SYNCDATA__FILE_PATH);
		}

	}
}
