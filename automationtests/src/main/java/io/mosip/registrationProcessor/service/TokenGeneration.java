package io.mosip.registrationProcessor.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.tokengeneration.dto.TokenGenerationDto;
import io.mosip.tokengeneration.dto.TokenGenerationEntity;
import io.mosip.tokengeneration.dto.UsernamePasswordDto;
import io.mosip.tokengeneration.dto.UsernamePwdTokenEntity;
import io.restassured.response.Response;

public class TokenGeneration {

	// ApplicationLibrary applnMethods = new ApplicationLibrary();
	private static Logger logger = Logger.getLogger(TokenGeneration.class);

	public TokenGenerationEntity createTokenGeneratorDtoForClientIdSecretKey(String tokenGenerationFilePath) {
		TokenGenerationEntity tokenGenerationEntity = new TokenGenerationEntity();
		TokenGenerationDto tokenRequestDto = new TokenGenerationDto();
		tokenGenerationEntity.setId("string");
		tokenGenerationEntity.setMetadata("");
		Date currentDate = new Date();
		LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		tokenGenerationEntity.setRequesttime(requestTime);
		tokenGenerationEntity.setVersion("1.0");
		FileReader reader;
		Properties prop = new Properties();

		String propertyFilePath = System.getProperty("user.dir") + "/" + tokenGenerationFilePath;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);

		} catch (IOException e) {
			e.printStackTrace();
		}

		tokenRequestDto.setAppId(prop.getProperty("token.request.appid"));
		tokenRequestDto.setClientId(prop.getProperty("token.request.clientId"));
		tokenRequestDto.setSecretKey(prop.getProperty("token.request.secretKey"));
		tokenGenerationEntity.setRequest(tokenRequestDto);
		System.out.println(new Gson().toJson(tokenGenerationEntity));
		return tokenGenerationEntity;
	}

	public UsernamePwdTokenEntity createTokenGeneratorDtoForUserIdPassword(String tokenGenerationFilePath) {
		UsernamePwdTokenEntity tokenEntity = new UsernamePwdTokenEntity();
		tokenEntity.setId("io.mosip.registration.processor");
		Date currentDate = new Date();
		LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		tokenEntity.setRequesttime(requestTime);
		tokenEntity.setVersion("1.0");
		Properties prop = new Properties();
		FileReader reader;
		String propertyFilePath = System.getProperty("user.dir") + "/" + tokenGenerationFilePath;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);

		} catch (IOException e) {
			e.printStackTrace();
		}

		UsernamePasswordDto requestDto = new UsernamePasswordDto();
		requestDto.setUserName(prop.getProperty("token.request.username"));
		requestDto.setPassword(prop.getProperty("token.request.password"));
		requestDto.setAppId(prop.getProperty("token.request.appid"));
		tokenEntity.setRequest(requestDto);
		return tokenEntity;
	}

	public TokenGenerationEntity createTokenGeneratorDto(String tokenGenerationFilePath) throws IOException {
		TokenGenerationEntity generateTokenRequest = new TokenGenerationEntity();
		TokenGenerationDto tokenRequestDto = new TokenGenerationDto();
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		Date currentDate = new Date();
		LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/" + tokenGenerationFilePath;
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
			generateTokenRequest.setId(prop.getProperty("token.request.id"));
			generateTokenRequest.setMetadata("");
			tokenRequestDto.setAppId(prop.getProperty("token.request.appid"));
			// tokenRequestDto.setUserName(prop.getProperty("token.request.username"));
			tokenRequestDto.setClientId(prop.getProperty("token.request.clientId"));
			tokenRequestDto.setSecretKey(prop.getProperty("token.request.secretKey"));
			generateTokenRequest.setRequest(tokenRequestDto);
			generateTokenRequest.setRequesttime(requestTime);
			generateTokenRequest.setVersion(prop.getProperty("token.request.version"));
			reader.close();
		} catch (IOException e) {
			logger.error("Propert File Was Not Found", e);
		}
//		String file = System.getProperty("user.dir") + "//src//main//resources//prepodusers_1.txt";
//		List<String> content = new ArrayList<String>();
//		FileReader fileReader = new FileReader(new File(file));
//		BufferedReader bufferedReader = new BufferedReader(fileReader);
//		String line = "";
//		while ((line = bufferedReader.readLine()) != null) {
//			content.add(line);
//		}
//		bufferedReader.close();
//		fileReader.close();
//		Random rand = new Random();
//		int rand_int1 = rand.nextInt(60);
//		tokenRequestDto.setClientId(content.get(rand_int1));
		return generateTokenRequest;

	}

	public TokenGenerationEntity createTokenGeneratorDtoWithCurrentUser(String tokenGenerationFilePath, int index)
			throws IOException {
		TokenGenerationEntity generateTokenRequest = new TokenGenerationEntity();
		TokenGenerationDto tokenRequestDto = new TokenGenerationDto();
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		Date currentDate = new Date();
		LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/" + tokenGenerationFilePath;
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
			generateTokenRequest.setId(prop.getProperty("token.request.id"));
			generateTokenRequest.setMetadata("");
			tokenRequestDto.setAppId(prop.getProperty("token.request.appid"));
			tokenRequestDto.setClientId(prop.getProperty("token.request.username"));
			tokenRequestDto.setSecretKey(prop.getProperty("token.request.password"));
			generateTokenRequest.setRequest(tokenRequestDto);
			generateTokenRequest.setRequesttime(requestTime);
			generateTokenRequest.setVersion(prop.getProperty("token.request.version"));
			reader.close();
		} catch (IOException e) {
			logger.error("Property File Was Not Found", e);
		}
		String file = System.getProperty("user.dir") + "//src//main//resources//regProc_users.csv";
		List<String> content = new ArrayList<String>();
		FileReader fileReader = new FileReader(new File(file));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			content.add(line);
		}
		bufferedReader.close();
		fileReader.close();

		tokenRequestDto.setClientId(content.get(index));
		generateTokenRequest.setRequest(tokenRequestDto);
		return generateTokenRequest;

	}

	@SuppressWarnings("unchecked")
	public String getAuthTokenForClientIdSecretKey(TokenGenerationEntity tokenEntity, PropertiesUtil prop) {
		String token = "";
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		JSONObject requestBodyToBeSent = new JSONObject();
		JSONObject nestedRequest = new JSONObject();
		nestedRequest.put("clientId", tokenEntity.getRequest().getClientId());
		nestedRequest.put("secretKey", tokenEntity.getRequest().getSecretKey());
		nestedRequest.put("appId", tokenEntity.getRequest().getAppId());
		requestBodyToBeSent.put("request", nestedRequest);
		requestBodyToBeSent.put("id", tokenEntity.getId());
		requestBodyToBeSent.put("metadata", "");
		requestBodyToBeSent.put("version", tokenEntity.getVersion());
		requestBodyToBeSent.put("requesttime", tokenEntity.getRequesttime().atOffset(ZoneOffset.UTC).toString());
		String AUTH_URL = "/v1/authmanager/authenticate/clientidsecretkey"; // TODO
		Response response = apiRequests.postRequest(AUTH_URL, requestBodyToBeSent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
		System.out.println("Authtoken generation request response: " + response.asString());
		// response.
		if (null != response && !response.toString().contains("errorCode")) {
			token = response.getCookie("Authorization");
		}
		return token;
	}

	@SuppressWarnings("unchecked")
	public String getAuthTokenForUsernamePassword(UsernamePwdTokenEntity tokenEntity, PropertiesUtil prop) {
		String token = "";
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		JSONObject requestBodyToBeSent = new JSONObject();
		JSONObject nestedRequest = new JSONObject();
		nestedRequest.put("userName", tokenEntity.getRequest().getUserName());
		nestedRequest.put("password", tokenEntity.getRequest().getPassword());
		nestedRequest.put("appId", tokenEntity.getRequest().getAppId());
		requestBodyToBeSent.put("metadata", "");
		requestBodyToBeSent.put("version", tokenEntity.getVersion());
		requestBodyToBeSent.put("id", tokenEntity.getId());
		requestBodyToBeSent.put("requesttime", tokenEntity.getRequesttime().atOffset(ZoneOffset.UTC).toString());
		requestBodyToBeSent.put("request", nestedRequest);

		String AUTH_URL = "/v1/authmanager/authenticate/useridPwd";
		Response response = apiRequests.postRequest(AUTH_URL, requestBodyToBeSent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
		System.out.println("Authtoken generation request response: " + response.asString());
		try {
			if (null != response && !response.toString().contains("errorCode")) {
				token = response.getCookie("Authorization");
			}
		} catch (Exception ex) {
			logger.error("Exception occured while generating authtoken, with error message:- " + ex.getMessage());
			ex.printStackTrace();
		}

		return token;
	}

	public String readPropertyFile(String tokenGenerationFilePathproperty) {
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/folderPaths.properties";
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch (IOException e) {
			logger.error("Property File Was Not Found", e);
		}
		String tokenGenFilePath = prop.getProperty(tokenGenerationFilePathproperty);
		return tokenGenFilePath;
	}

}
