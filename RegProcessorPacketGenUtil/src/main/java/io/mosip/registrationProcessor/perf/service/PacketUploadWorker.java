package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.ws.rs.core.MediaType;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.util.FileUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.restassured.response.Response;

public class PacketUploadWorker {

	public void syncAndUploadPackets(PropertiesUtil prop, String auth_token) throws Exception {
		FileUtil fileUtil = new FileUtil();
		String syncDataFile = prop.SYNCDATA__FILE_PATH;
		List<String> syncDataLines = null;
		try {
			syncDataLines = fileUtil.readLinesOfFile(syncDataFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		for (String fileLine : syncDataLines) {
			String[] literals = fileLine.split(",");
			String regid = literals[0];
			String syncData = literals[1];
			String packetpath = literals[2];
			String machineRefId = literals[3];
			syncAndUploadPacket(prop, auth_token, regid, syncData, packetpath, machineRefId);
		}

	}

	private void syncAndUploadPacket(PropertiesUtil prop, String auth_token, String regid, String syncData,
			String packetpath, String center_machine_refID) {
		RegProcApiRequests apiRequests = new RegProcApiRequests();

		boolean tokenStatus = apiRequests.validateToken(auth_token, prop);
		while (!tokenStatus) {
			try {
				auth_token = getToken("syncTokenGenerationFilePath", prop);
			} catch (IOException e) {
				e.printStackTrace();
			}
			tokenStatus = apiRequests.validateToken(auth_token, prop);
		}
		String timeStamp = getUTCTime().toString();
		String syncApi = "/registrationprocessor/v1/registrationstatus/sync";
		Response syncResponse = apiRequests.regProcSyncRequest(syncApi, syncData, center_machine_refID, timeStamp,
				MediaType.APPLICATION_JSON, auth_token, prop);

		String syncStatus = syncResponse.jsonPath().get("response[0].status");
		System.out.println("Sync status for regid " + regid + " is " + syncStatus);
		if (syncStatus.equalsIgnoreCase("success")) {
			String uploadApi = "/registrationprocessor/v1/packetreceiver/registrationpackets";
			File file = new File(packetpath);
			Response uploadResponse = apiRequests.regProcPacketUpload(file, uploadApi, auth_token, prop);
			System.out.println("Packet upload response for regid " + regid + " is\n" + uploadResponse.asString());
		}

	}

	public String getToken(String tokenType, PropertiesUtil prop) throws IOException {

		TokenGeneration generateToken = new TokenGeneration();
		String token = "";
		if (prop.AUTH_TYPE_CLIENTID_SECRETKEY) {
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
			tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
			token = generateToken.getToken(tokenEntity, prop);
		} else {
			/*
			 * Code to generate token if username, password are there
			 */
			String tokenGenerationFilePath = generateToken.readPropertyFile(tokenType);
			UsernamePwdTokenEntity tokenEntity1 = generateToken
					.createTokenGeneratorDtoForUserIdPassword(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForUsernamePassword(tokenEntity1, prop);

		}

		return token;
	}

	public Object getUTCTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now(Clock.systemUTC());
		String utcTime = time.format(dateFormat);
		return utcTime;

	}

}
