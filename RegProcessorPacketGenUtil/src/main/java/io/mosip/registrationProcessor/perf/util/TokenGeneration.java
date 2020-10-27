package io.mosip.registrationProcessor.perf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.dbdto.TokenGenerationDto_Password;
import io.mosip.dbentity.TokenGenerationDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePasswordDto;
import io.mosip.dbentity.UsernamePwdTokenEntity;
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
				MediaType.APPLICATION_JSON, prop);
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
				MediaType.APPLICATION_JSON, prop);
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

	@SuppressWarnings("unchecked")
	public String getToken(TokenGenerationEntity tokenGenerateEntity, PropertiesUtil prop) {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		JSONObject requestToBeSent = new JSONObject();
		JSONObject nestedRequest = new JSONObject();
		/*
		 * Request body for ID and password based authentication.
		 */
//		nestedRequest.put("appId", tokenGenerateEntity.getRequest().getAppId());
//		nestedRequest.put("password", tokenGenerateEntity.getRequest().getPassword());
//		nestedRequest.put("userName", tokenGenerateEntity.getRequest().getUserName());
//		requestToBeSent.put("id", tokenGenerateEntity.getId());
//		requestToBeSent.put("metadata", "");
//		requestToBeSent.put("request", nestedRequest);
//		requestToBeSent.put("requesttime", tokenGenerateEntity.getRequesttime().atOffset(ZoneOffset.UTC).toString());
//		requestToBeSent.put("version", tokenGenerateEntity.getVersion());

		/*
		 * Request body for client ID and security key based authentication.
		 */

//		Response response = apiRequests.postRequest("/v1/authmanager/authenticate/useridPwd", requestToBeSent,
//				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,prop);
		// System.out.println(response.getCookie("Authorization"));
		// TODO auth token is hardcoded
		String authToken = prop.AUTH_TOKEN;
		// String authToken =
		// "eyJhbGciOiJSUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJRakpYVzgtemMwR1RWRkQ0RnZiWkdTblZLSlhDMWRXVnBPVUxMLWVZT0JNIn0.eyJqdGkiOiI3N2VlNjVkOS00NmQ5LTRiOTAtYTVhNi05NjZmNWU5YzczNGEiLCJleHAiOjE1Nzc5NzM3NzMsIm5iZiI6MCwiaWF0IjoxNTc3OTcxMDczLCJpc3MiOiJodHRwczovL21vc2lwa2V5Y2xvYWsuc291dGhpbmRpYS5jbG91ZGFwcC5henVyZS5jb20vYXV0aC9yZWFsbXMvbW9zaXAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmYxN2I4NzgtMzEyMC00NjBlLTk1NzAtNzE2MTA4ZmM4MTIzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmVnaXN0cmF0aW9uLXByb2Nlc3NvciIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImIzMzM2MjgzLTlmNzktNDM2Mi04ZjM3LTg1NGFlNGJlNDM0MyIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiUkVHSVNUUkFUSU9OX1BST0NFU1NPUiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVnaXN0cmF0aW9uLXByb2Nlc3NvciI6eyJyb2xlcyI6WyJ1bWFfcHJvdGVjdGlvbiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiY2xpZW50SWQiOiJyZWdpc3RyYXRpb24tcHJvY2Vzc29yIiwiY2xpZW50SG9zdCI6IjEwNC4yMTEuMjQ2LjIwMiIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LXJlZ2lzdHJhdGlvbi1wcm9jZXNzb3IiLCJjbGllbnRBZGRyZXNzIjoiMTA0LjIxMS4yNDYuMjAyIiwiZW1haWwiOiJzZXJ2aWNlLWFjY291bnQtcmVnaXN0cmF0aW9uLXByb2Nlc3NvckBwbGFjZWhvbGRlci5vcmcifQ.ADb5jns0aFwLtfbFrhQ9C0CE7CWTtev20SsmUiAuh_H_F4KoN_X5OoqWH8JqtOjGRnJqb2Xe22z7FukVru2K6c8waIjAe-Jmh1nNDdCUDiB-SkjXRRaxFDFxVxEz6cA9PptspGY8gofPeFVuk-MG2W4sMmrHaxjBQcT3xa-eqs0-VdQZ0l2Gw6K-RWvWqrKhnHunK6np1bjv2JMyalxZq1Q4pON9QkLFhBITRvB6P46s98MJ5vJrJJ9Bh2VgPRUPnI-sTTg5bCO0m6LpJ7zOi4bWWLEBYxfWiNugAZGomI7xmPN1bdCOuPQ1cF-ryvwJg7YPdS2j6BzZGcInYlU1pw";
		// return response.getCookie("Authorization");
		return authToken;
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
