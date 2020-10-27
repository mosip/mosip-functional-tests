package io.mosip.registrationProcessor.perf.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import io.mosip.dbdto.CryptomanagerDto;
import io.mosip.dbdto.CryptomanagerRequestDto;
import io.mosip.dbdto.DecrypterDto;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.util.CryptoUtil;
import io.mosip.util.DateUtils;
import io.restassured.response.Response;

public class EncryptData {
//	private String applicationId = "REGISTRATION";
//	ObjectMapper objectMapper = new ObjectMapper();
	private static Logger logger = Logger.getLogger(EncryptData.class);

	public String encodeAndEncryptSyncRequest(RegistrationPacketSyncDTO registrationPacketSyncDto, PropertiesUtil prop,
			String token) throws org.json.simple.parser.ParseException {
		String APPLICATION_ID = "REGISTRATION";
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String outputJson = "";
		try {
			outputJson = objectMapper.writeValueAsString(registrationPacketSyncDto);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] byteArray = outputJson.getBytes();
		String registrationId = registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId()
				.toString();

		String referenceId = registrationId.substring(0, 5) + "_" + registrationId.substring(5, 10);
		String packetString = CryptoUtil.encodeBase64String(byteArray);
		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
		cryptomanagerRequestDto.setApplicationId(APPLICATION_ID);
		cryptomanagerRequestDto.setData(packetString);
		cryptomanagerRequestDto.setReferenceId(referenceId);
		SecureRandom sRandom = new SecureRandom();
		byte[] nonce = new byte[CryptomanagerConstant.GCM_NONCE_LENGTH];
		byte[] aad = new byte[CryptomanagerConstant.GCM_AAD_LENGTH];
		sRandom.nextBytes(nonce);
		sRandom.nextBytes(aad);
		cryptomanagerRequestDto.setAad(CryptoUtil.encodeBase64String(aad));
		cryptomanagerRequestDto.setSalt(CryptoUtil.encodeBase64String(nonce));
		cryptomanagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTime());
		CryptomanagerDto request = new CryptomanagerDto();
		request.setId(CryptomanagerConstant.ENCRYPT_SERVICE_ID);
		request.setMetadata(null);
		request.setRequest(cryptomanagerRequestDto);
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setVersion(CryptomanagerConstant.APPLICATION_VERSION);

		RegProcApiRequests apiRequests = new RegProcApiRequests();
		String encrypterURL = readPropertyFromFile("encryptAPI");
		boolean tokenStatus = apiRequests.validateToken(token, prop);
		while (!tokenStatus) {
			try {
				token = getToken("syncTokenGenerationFilePath", prop);
			} catch (IOException e) {
				e.printStackTrace();
			}
			tokenStatus = apiRequests.validateToken(token, prop);
		}
		Response response = apiRequests.postRequestToEncrypt(encrypterURL, request, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, token, prop);

		JSONObject data = (JSONObject) new JSONParser().parse(response.asString());
		JSONObject responseObject = (JSONObject) data.get("response");
		System.out.println(
				"responseObject.toString().contains(\"data\") :- " + responseObject.toString().contains("data"));
		// byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
		String encryptedData = CryptoUtil.encodeBase64(
				mergeEncryptedData(CryptoUtil.decodeBase64((String) responseObject.get("data")), nonce, aad));
		return encryptedData;
	}

	private static byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
		byte[] finalEncData = new byte[encryptedData.length + CryptomanagerConstant.GCM_AAD_LENGTH
				+ CryptomanagerConstant.GCM_NONCE_LENGTH];
		System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
		System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
		System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length, encryptedData.length);
		return finalEncData;
	}

	public String readPropertyFromFile(String apiName) {
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/apiList.properties";
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch (IOException e) {
			logger.error("Property File " + propertyFilePath + " Was Not Found", e);
		}
		String apiEndPoint = prop.getProperty(apiName);
		return apiEndPoint;
	}

	public String getToken(String tokenType, PropertiesUtil prop) throws IOException {

//		String TOKEN_TYPE = "syncTokenGenerationFilePath";
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

	@SuppressWarnings("unchecked")
	public JSONObject encodeData_0(RegistrationPacketSyncDTO registrationPacketSyncDto) {
		String applicationId = "REGISTRATION";
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String outputJson = "";
		try {
			outputJson = objectMapper.writeValueAsString(registrationPacketSyncDto);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] byteArray = outputJson.getBytes();
		String encryptedString = Base64.encodeBase64URLSafeString(byteArray);
		JSONObject encryptRequest = new JSONObject();
		CryptomanagerDto cryptoReq = new CryptomanagerDto();
		JSONObject cryptographicRequest = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");

		DecrypterDto decrypterDto = new DecrypterDto();

		String registrationId = registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId()
				.toString();

		String referenceId = registrationId.substring(0, 5) + "_" + registrationId.substring(5, 10);

		try {

			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(referenceId);
			decrypterDto.setData(encryptedString);

			SecureRandom sRandom = new SecureRandom();
			byte[] nonce = new byte[CryptomanagerConstant.GCM_NONCE_LENGTH];
			byte[] aad = new byte[CryptomanagerConstant.GCM_AAD_LENGTH];
			sRandom.nextBytes(nonce);
			sRandom.nextBytes(aad);
			decrypterDto.setTimeStamp(getTime(registrationId));
			cryptoReq.setRequesttime(getTime(registrationId));
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedString);
			cryptographicRequest.put("referenceId", referenceId);
			cryptographicRequest.put("timeStamp", decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("id", "mosip.registration.sync");
			encryptRequest.put("metadata", "");
			encryptRequest.put("request", cryptographicRequest);
			encryptRequest.put("requesttime", cryptoReq.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("version", "1.0");
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptRequest;
	}

	/*
	 * public static void main(String[] args) { File f=new File(
	 * "D:\\sprint_10\\mosip\\automationtests\\src\\test\\resources\\regProc\\Packets\\ValidPackets\\packteForInvalidPackets\\10011100110001920190514120310.zip"
	 * ); EncryptData e=new EncryptData(); RegistrationPacketSyncDTO
	 * dto=e.createSyncRequest(f); System.out.println(dto.toString());
	 * System.out.println(e.encryptData(dto)); }
	 */
	public RegistrationPacketSyncDTO createSyncRequest(File f, String process) throws ParseException {
		String regId = f.getName().substring(0, f.getName().lastIndexOf("."));
		HashSequenceUtil hashSeqUtil = new HashSequenceUtil();
		String packetHash = hashSeqUtil.getPacketHashSequence(f);
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setLangCode("eng");
		syncRegistrationDto.setPacketHashValue(packetHash);
		syncRegistrationDto.setPacketSize(BigInteger.valueOf(f.length()));
		syncRegistrationDto.setRegistrationId(regId);
		syncRegistrationDto.setRegistrationType(process);
		syncRegistrationDto.setSupervisorComment("APPROVED");
		syncRegistrationDto.setSupervisorStatus("APPROVED");
		List<SyncRegistrationDto> syncRegistrationList = new ArrayList<SyncRegistrationDto>();
		syncRegistrationList.add(syncRegistrationDto);
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
		registrationPacketSyncDto.setId("mosip.registration.sync");

		registrationPacketSyncDto.setRequesttime(getCurrUTCTime() + "Z");
		// registrationPacketSyncDto.setRequesttime(getCurrTime());
		registrationPacketSyncDto.setVersion("1.0");
		registrationPacketSyncDto.setSyncRegistrationDTOs(syncRegistrationList);
		logger.info("Sync JSON is " + new Gson().toJson(registrationPacketSyncDto));
		return registrationPacketSyncDto;
	}

	private String getCurrUTCTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currUTCTime = f.format(new Date());
		return currUTCTime;
	}

	public String getCurrTime(PropertiesUtil prop) {
		LocalDateTime ldt = null;
		// String srcTimeStr = "2019-07-22T13:22:30.000";
		String srcTimeStr = prop.PACKET_UPLOAD_TIME;
		String timestamp = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Random r = new Random();
		Date currDate = new Date();
		try {
			currDate = formatter.parse(srcTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		int randomMinute = 0;
		int randMin_min = 30;
		int randMin_max = 155;
		randomMinute = r.nextInt(randMin_max - randMin_min) + randMin_min;

		cal.add(Calendar.MINUTE, -randomMinute);
		Date date = cal.getTime();
		// SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		timestamp = formatter.format(date);
		timestamp = timestamp + "Z";
		logger.info("Time added to sync request is " + timestamp);
		return timestamp;
	}

	public LocalDateTime getTime(String registrationId) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
		int n = 100 + new Random().nextInt(900);
		String milliseconds = String.valueOf(n);

		Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
				+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6) + milliseconds);
		LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		logger.info("Time added to sync request is " + ldt.toString() + "Z");
		return ldt;
	}

	public RegistrationPacketSyncDTO createSyncRequest(JSONObject jsonRequest) throws ParseException {
		String regId = null;
		String packetHash = null;
		long packetSize = 0;
		String langCode = null;
		String registrationType = null;
		String superVisiorStatus = null;
		JSONArray request = (JSONArray) jsonRequest.get("request");
		for (int j = 0; j < request.size(); j++) {
			JSONObject obj = (JSONObject) request.get(j);
			regId = obj.get("registrationId").toString();
			packetHash = obj.get("packetHashValue").toString();
			packetSize = (long) obj.get("packetSize");
			langCode = obj.get("langCode").toString();
			registrationType = obj.get("registrationType").toString();
			superVisiorStatus = obj.get("supervisorStatus").toString();
		}
		String id = jsonRequest.get("id").toString();
		String version = jsonRequest.get("version").toString();
		String requesttime = jsonRequest.get("requesttime").toString();

		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setLangCode(langCode);
		syncRegistrationDto.setPacketHashValue(packetHash);
		syncRegistrationDto.setPacketSize(BigInteger.valueOf(packetSize));
		syncRegistrationDto.setRegistrationId(regId);
		syncRegistrationDto.setRegistrationType(registrationType);
		syncRegistrationDto.setSupervisorComment("APPROVED");
		syncRegistrationDto.setSupervisorStatus(superVisiorStatus);
		List<SyncRegistrationDto> syncRegistrationList = new ArrayList<SyncRegistrationDto>();
		syncRegistrationList.add(syncRegistrationDto);
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
		registrationPacketSyncDto.setId(id);

		// LocalDateTime requestTime=LocalDateTime.ofInstant(currentDate.toInstant(),
		// ZoneId.systemDefault());
		registrationPacketSyncDto.setRequesttime(requesttime);
		registrationPacketSyncDto.setVersion(version);
		registrationPacketSyncDto.setSyncRegistrationDTOs(syncRegistrationList);
		return registrationPacketSyncDto;
	}
}
