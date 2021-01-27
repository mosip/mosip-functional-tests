package io.mosip.registrationProcessor.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.dbdto.CryptomanagerDto;
import io.mosip.dbdto.CryptomanagerRequestDto;
import io.mosip.dbdto.DecrypterDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.FieldValueArray;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.tokengeneration.dto.UsernamePwdTokenEntity;
import io.mosip.util.DateUtils;
import io.mosip.util.HMACUtils;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

public class EncrypterDecrypter {

	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();

	public synchronized String generateCheckSum(List<FieldValueArray> hashSequence, String packetPath)
			throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (FieldValueArray fieldValueArray : hashSequence) {

			if (PacketFiles.BIOMETRICSEQUENCE.name().equalsIgnoreCase(fieldValueArray.getLabel())) {

				generateBiometricsHash(fieldValueArray.getValue(), packetPath, outputStream);

			} else if (PacketFiles.DEMOGRAPHICSEQUENCE.name().equalsIgnoreCase(fieldValueArray.getLabel())) {

				generateDemographicHash(fieldValueArray.getValue(), packetPath, outputStream);
			}

		}

		byte[] dataByte = HMACUtils.generateHash(outputStream.toByteArray());
		String hashCodeGenerated = new String(HMACUtils.digestAsPlainText(dataByte).getBytes(), StandardCharsets.UTF_8);
		return hashCodeGenerated;
	}

	private synchronized void generateBiometricsHash(List<String> hashOrder, String packetPath,
			ByteArrayOutputStream outputStream) {
		hashOrder.forEach(file -> {
			byte[] fileByte = null;
			try {
//				InputStream fileStream = adapter.getFile(registrationId,
//						PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR + file.toUpperCase());
				String filePath = packetPath + File.separator + file.toUpperCase();
				if (filePath.toUpperCase().contains("BIO_CBEFF")) {
					filePath += ".xml";
				}
				InputStream fileStream = new FileInputStream(new File(filePath));

				fileByte = IOUtils.toByteArray(fileStream);
				outputStream.write(fileByte);
				fileStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Generate demographic hash.
	 *
	 * @param fieldValueArray the field value array
	 * @param registrationId  the registration id
	 */
	private synchronized void generateDemographicHash(List<String> hashOrder, String packetPath,
			ByteArrayOutputStream outputStream) {
		hashOrder.forEach(document -> {
			byte[] fileByte = null;
			try {
				// InputStream fileStream = adapter.getFile(registrationId,
				// PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + document.toUpperCase());

				String filePath = packetPath + File.separator + document.toUpperCase();
				if (filePath.toUpperCase().endsWith("ID")) {
					filePath += ".json";
				} else {
					filePath += ".jpg";
				}
				InputStream fileStream = new FileInputStream(new File(filePath));

				fileByte = IOUtils.toByteArray(fileStream);
				outputStream.write(fileByte);
				fileStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public byte[] zipDirectoryAndSign(String folderPath, String folderToZip, String token, PropertiesUtil prop) {

		byte[] sign = null;
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		System.out.println("Folder path is::- " + folderPath);
		System.out.println("name of folder to zip is::- " + folderToZip);
		File dirToZip = new File(folderPath + File.separator + folderToZip);
		File zipFile = new File(folderPath + File.separator + folderToZip + ".zip");
		org.zeroturnaround.zip.ZipUtil.pack(dirToZip, zipFile);
		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(zipFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject signRequestBody = new JSONObject();
		signRequestBody = generateSignRequestData(zipFile);
		String SIGN_URL = "/v1/keymanager/sign";
		boolean tokenStatus = apiRequests.validateToken(token);
		while (!tokenStatus) {
			try {
				token = getToken("syncTokenGenerationFilePath");
			} catch (IOException e) {
				e.printStackTrace();
			}
			tokenStatus = apiRequests.validateToken(token);
		}
		Response response = apiRequests.postRequestToSign(SIGN_URL, signRequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, token);
		System.out.println(response.asString());

		JSONObject data;
		try {
			data = (JSONObject) new JSONParser().parse(response.asString());
			JSONObject responseObject = (JSONObject) data.get("response");
			sign = responseObject.get("signature").toString().getBytes();

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return sign;
	}

	private JSONObject generateSignRequestData(File zipFile) {
		JSONObject signReqOuterDto = new JSONObject();
		try {
			byte[] fileInBytes = FileUtils.readFileToByteArray(zipFile);
			String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

			String packetData = new String(fileInBytes, StandardCharsets.UTF_8);

			JSONObject signReqData = new JSONObject();

			signReqData.put("data", packetData);
			signReqOuterDto.put("id", "");
			signReqOuterDto.put("metadata", null);
			signReqOuterDto.put("version", "v1");
			signReqOuterDto.put("request", signReqData);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
			LocalDateTime localdatetime = LocalDateTime.parse(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN),
					format);
			signReqOuterDto.put("requesttime", localdatetime.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signReqOuterDto;
	}

	public String getToken(String tokenType) throws IOException {
		PropertiesUtil prop = new PropertiesUtil();
		TokenGeneration generateToken = new TokenGeneration();
		String token = "";
		if (prop.AUTH_TYPE_CLIENTID_SECRETKEY) {
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
			tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
			token = generateToken.getToken(tokenEntity);
		} else {
			/*
			 * Code to generate token if username, password are there
			 */
			String tokenGenerationFilePath = generateToken.readPropertyFile(tokenType);
			UsernamePwdTokenEntity tokenEntity1 = generateToken
					.createTokenGeneratorDtoForUserIdPassword(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForUsernamePassword(tokenEntity1);

		}

		return token;
	}

	public byte[] encryptZippedPacket(String folderPath, String zippedFileName, String token, PropertiesUtil prop)
			throws Exception {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		String zipFile = folderPath + File.separator + zippedFileName + ".zip";

		JSONObject encryptedFileBody = new JSONObject();
		encryptedFileBody = generateCryptographicDataEncryption(new File(zipFile));
		// logger.info("encrypt request packet : " + encryptedFileBody);
		String encrypterURL = "v1/keymanager/encrypt";
		boolean tokenStatus = apiRequests.validateToken(token);
		while (!tokenStatus) {
			token = getToken("getStatusTokenGenerationFilePath");
			tokenStatus = apiRequests.validateToken(token);
		}
		Response response = apiRequests.postRequestWithRequestResponseHeaders(encrypterURL, encryptedFileBody,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, token);
		InputStream inputstream = null;
		try {
			JSONObject data = (JSONObject) new JSONParser().parse(response.asString());
			JSONObject responseObject = (JSONObject) data.get("response");
			System.out.println(data);
			// String encryptedPacketString=
			// CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
			System.out.println(
					"responseObject.toString().contains(\"data\") :- " + responseObject.toString().contains("data"));
			byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
			inputstream = new ByteArrayInputStream(encryptedPacket);
			// logger.info("Outstream is " + inputstream);
			FileOutputStream fos = new FileOutputStream(zipFile);
			fos.write(encryptedPacket);
			fos.close();
			inputstream.close();
			return encryptedPacket;
		} catch (ParseException e) {
			System.out.println("Response of encryption:- " + response.asString());
			e.printStackTrace();
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject generateCryptographicDataEncryption(File file) {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		String applicationId = "REGISTRATION";
		// InputStream outstream = null;
		// TokenGeneration generateToken = new TokenGeneration();
		// TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
		// String validToken = "";
		JSONObject encryptRequest = new JSONObject();
		CryptomanagerDto request = new CryptomanagerDto();
		JSONObject cryptographicRequest = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket = null;
		DecrypterDto decrypterDto = new DecrypterDto();
		// String centerId=file.getName().substring(0,5);
		String refId = file.getName().substring(0, 5) + "_" + file.getName().substring(5, 10);
		try {
			encryptedPacket = new FileInputStream(file);
			byte[] fileInBytes = FileUtils.readFileToByteArray(file);
			String encryptedPacketString = Base64.getEncoder().encodeToString(fileInBytes);
			// String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			encryptedPacketString = encryptedPacketString.replaceAll("\\s+", "");
			String registrationId = file.getName().substring(0, file.getName().lastIndexOf('_'));
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			encryptedPacket.close();
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6) + milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			Date currentDate = new Date();
			LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(refId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			request.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			// System.out.println("encrypter request data : " + encryptedPacketString);
			cryptographicRequest.put("referenceId", refId);
			cryptographicRequest.put("timeStamp", decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("id", "");
			encryptRequest.put("metadata", "");
			encryptRequest.put("request", cryptographicRequest);
			encryptRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("version", "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptRequest;
	}

	public byte[] encryptZippedPacket(String id, File zipFile, String token, PropertiesUtil prop) throws Exception {
		byte[] packet_bytes = null;
		try {
			packet_bytes = Files.readAllBytes(zipFile.toPath());
		} catch (IOException e1) {
			throw new Exception(e1);
		}
		String centerId = id.substring(0, 5);
		String machineId = id.substring(5, 10);
		String refId = centerId + "_" + machineId;
		String packetString = CryptoUtil.encodeBase64String(packet_bytes);
		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
		cryptomanagerRequestDto.setApplicationId(CryptomanagerConstant.APPLICATION_ID);
		cryptomanagerRequestDto.setData(packetString);
		cryptomanagerRequestDto.setReferenceId(refId);
		SecureRandom sRandom = new SecureRandom();
		byte[] nonce = new byte[CryptomanagerConstant.GCM_NONCE_LENGTH];
		byte[] aad = new byte[CryptomanagerConstant.GCM_AAD_LENGTH];
		sRandom.nextBytes(nonce);
		sRandom.nextBytes(aad);
		cryptomanagerRequestDto.setAad(CryptoUtil.encodeBase64String(aad));
		cryptomanagerRequestDto.setSalt(CryptoUtil.encodeBase64String(nonce));

		String packetCreatedDateTime = id.substring(id.length() - 14);
		String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
				+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);

		cryptomanagerRequestDto
				.setTimeStamp(LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")));
		CryptomanagerDto request = new CryptomanagerDto();
		request.setId(CryptomanagerConstant.ENCRYPT_SERVICE_ID);
		request.setMetadata(null);
		request.setRequest(cryptomanagerRequestDto);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(CryptomanagerConstant.DATETIME_PATTERN);
		LocalDateTime localdatetime = LocalDateTime
				.parse(DateUtils.getUTCCurrentDateTimeString(CryptomanagerConstant.DATETIME_PATTERN), format);
		request.setRequesttime(localdatetime);
		request.setVersion(CryptomanagerConstant.APPLICATION_VERSION);

		RegProcApiRequests apiRequests = new RegProcApiRequests();

		String encrypterURL = "/v1/keymanager/encrypt";
		boolean tokenStatus = apiRequests.validateToken(token);
		while (!tokenStatus) {
			try {
				token = getToken("syncTokenGenerationFilePath");
			} catch (IOException e) {
				e.printStackTrace();
			}
			tokenStatus = apiRequests.validateToken(token);
		}
		Response response = apiRequests.postRequestWithRequestResponseHeaders(encrypterURL, request, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, token);
		InputStream inputstream = null;
		try {
			JSONObject data = (JSONObject) new JSONParser().parse(response.asString());
			JSONObject responseObject = (JSONObject) data.get("response");
			System.out.println(data);
			// String encryptedPacketString=
			// CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
			System.out.println(
					"responseObject.toString().contains(\"data\") :- " + responseObject.toString().contains("data"));
//			byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
			byte[] encryptedPacket = mergeEncryptedData(CryptoUtil.decodeBase64((String) responseObject.get("data")),
					nonce, aad);
			inputstream = new ByteArrayInputStream(encryptedPacket);
			FileOutputStream fos = new FileOutputStream(zipFile);
			fos.write(encryptedPacket);
			fos.close();
			inputstream.close();
			return encryptedPacket;
		} catch (ParseException e) {
			System.out.println("Response of encryption:- " + response.asString());
			e.printStackTrace();
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	private static byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
		byte[] finalEncData = new byte[encryptedData.length + CryptomanagerConstant.GCM_AAD_LENGTH
				+ CryptomanagerConstant.GCM_NONCE_LENGTH];
		System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
		System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
		System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length, encryptedData.length);
		return finalEncData;
	}

}
