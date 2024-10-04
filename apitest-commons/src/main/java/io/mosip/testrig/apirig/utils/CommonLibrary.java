package io.mosip.testrig.apirig.utils;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class CommonLibrary extends BaseTestCase {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	
	public CommonLibrary() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public void checkResponseUTCTime(Response response) {
		logger.info(response.asString());
		JSONObject responseJson = null;
		String responseTime = null;
		try {
			responseJson = (JSONObject) new JSONParser().parse(response.asString());
		} catch (ParseException e1) {
			logger.info(e1.getMessage());
			return;
		}
		if (responseJson != null && responseJson.containsKey("responsetime"))
			responseTime = response.jsonPath().get("responsetime").toString();
		else
			return;
		String cuurentUTC = (String) getCurrentUTCTime();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		try {
			Date d1 = sdf.parse(responseTime.substring(14, 16));
			Date d2 = sdf.parse(cuurentUTC.substring(14, 16));

			long elapse = Math.abs(d1.getTime() - d2.getTime());
			if (elapse > 300000) {
				Assert.assertTrue(false, "Response time is not UTC, response time : " + responseTime);
			}

		} catch (java.text.ParseException e) {
			logger.error(e.getMessage());
		}

	}

	public Object getCurrentUTCTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now(Clock.systemUTC());
		String utcTime = time.format(dateFormat);
		return utcTime;

	}

	public Object getCurrentLocalTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now();
		String currentTime = time.format(dateFormat);
		return currentTime;

	}

	public List<String> getFoldersFilesNameList(String folderRelativePath, boolean isfolder) {
		String configPath = folderRelativePath;
		List<String> listFoldersFiles = new ArrayList<>();

		final File file = new File(getResourcePath() + folderRelativePath);
		logger.info("=====" + getResourcePath() + folderRelativePath);
		logger.info("=======" + file.getAbsolutePath());
		logger.info("=========" + file.getPath());
		for (File f : file.listFiles()) {
			if (f.isDirectory() == isfolder)
				listFoldersFiles.add(configPath + "/" + f.getName());
		}
		return listFoldersFiles;
	}

	public String getResourcePath() {
		return getGlobalResourcePath() + "/";
	}

	public String getResourcePathForKernel() {
		return getGlobalResourcePath() + "/";
	}

	public JSONObject readJsonData(String path, boolean isRelative) {
		logger.info("path : " + path);
		if (isRelative)
			path = getResourcePath() + path;
		logger.info("Relativepath : " + path);
		FileInputStream inputStream = null;
		JSONObject jsonData = null;
		try {
			File fileToRead = new File(path);
			logger.info("fileToRead : " + fileToRead);
			inputStream = new FileInputStream(fileToRead);
			jsonData = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		} catch (IOException | ParseException | NullPointerException e) {
			logger.error(e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return jsonData;
	}

	public Map<String, String> readProperty(String propertyFileName) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		Map<String, String> mapProp = null;
		try {
			try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config/Kernel.properties")) {
				if (input != null) {
					// Load the properties from the input stream
					prop.load(input);
				}
				else {
					logger.error("Couldn't find  Kernel.properties file");
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
			
			/*
			 * inputStream = new FileInputStream(propertyFile); prop.load(inputStream);
			 */
			mapProp = prop.entrySet().stream()
					.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}

		return mapProp;
	}

	public void responseAuthValidation(Response response) {
		JSONArray errors = null;
		String errorCode = null;
		String errorMessage = null;
		int statusCode = response.getStatusCode();
		try {
			if (statusCode > 500)
				Assert.assertTrue(false, "Service is Unavailable and the statusCode=" + statusCode);

			errors = (JSONArray) ((JSONObject) new JSONParser().parse(response.asString())).get("errors");
		} catch (ParseException pe) {
			Assert.assertTrue(false, "Response from the service is not able to read and exception is " + pe.getClass());
		} catch (NullPointerException npe) {
			Assert.assertTrue(false, "Errors in the response is not null and exception is " + npe.getClass());
		}
		if (errors != null) {
			try {
				errorCode = ((JSONObject) errors.get(0)).get("errorCode").toString();
				errorMessage = ((JSONObject) errors.get(0)).get("message").toString();
				if (errorCode.contains("ATH")) {
					Assert.assertTrue(false,
							"Failed due to Authentication failure. Error message is='" + errorMessage + "'");
				}
			} catch (IndexOutOfBoundsException aibe) {
				Assert.assertTrue(false,
						"Not able to find the errorCode or errorMessage from errors array and exception is "
								+ aibe.getClass());
			}
		}
	}

	public boolean jsonComparator(String requestJson, String responseJson) throws AdminTestException {
		try {
			JSONAssert.assertEquals(requestJson, responseJson, false);
			return true;
		} catch (JSONException | AssertionError e) {
			logger.info("EXPECTED AND ACTUAL DATA MISMATCH");
			logger.info("MISMATCH DETAILS:");
			logger.info(e.getMessage());
			logger.info("Obtained ACTUAL RESPONSE is:== " + responseJson);
			throw new AdminTestException("Failed at output validation");
		}
	}

	public boolean isValidToken(String cookie) {
		boolean bReturn = false;
		if (cookie == null)
			return bReturn;
        try {
            DecodedJWT decodedJWT = JWT.decode(cookie);
            long expirationTime = decodedJWT.getExpiresAt().getTime();
            if (expirationTime < System.currentTimeMillis()) {
            	logger.info("The token is expired");
            } else {
            	bReturn = true;
            	logger.info("The token is not expired");
            }
        } catch (JWTDecodeException e) {
        	logger.error("The token is invalid");
        }
        return bReturn;
    }

	public boolean isValidTokenOnline(String cookie) {

		logger.info("========= Revalidating the token =========");

		Response response = applicationLibrary.getWithoutParams("/v1/authmanager/authorize/admin/validateToken",
				cookie);
		JSONObject responseJson = null;
		try {
			responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(response.asString())).get("response");

			if (responseJson != null && responseJson.get("errors") == null) {
				logger.info("========= Valid Token =========");
				return true;
			}
		} catch (ParseException | NullPointerException e) {
			logger.error(e.getMessage());
		}
		logger.info("========= InValid Token =========");
		return false;

	}

	public String removeJsonElement(String readFilePath, ArrayList<String> eleToRemove) throws ParseException {
		String jsnString = null;
		String val = null;

		try {
			String yourActualJSONString = new String(Files.readAllBytes(Paths.get(readFilePath)),
					StandardCharsets.UTF_8);
			DocumentContext jsonContext = JsonPath.parse(yourActualJSONString);

			for (int i = 0; i < eleToRemove.size(); i++) {
				val = eleToRemove.get(i);
				jsonContext.delete(val);
				jsnString = jsonContext.jsonString();
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return jsnString;

	}

	public void responseLogger(Response response) {
		int statusCode = response.statusCode();
		if (statusCode < 200 || statusCode > 299) {
			logger.info(response.asString());
		} else
			logger.info("status code: " + statusCode + "(success)");

	}

	public Response postWithoutJson(String url, String contentHeader, String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public Response postWithJson(String url, Object body, String contentHeader, String acceptHeader) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);

			postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader).accept(acceptHeader)
					.log().all().when().post(url).then().log().all().extract().response();

			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString()
					+ GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader).accept(acceptHeader)
					.when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public Response postWithJson(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public Response postWithPathParams(String url, Object body, Map<String, String> pathParams, String contentHeader,
			String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}


		return postResponse;
	}

	public Response postWithOnlyPathParams(String url, Map<String, String> pathParams, String contentHeader,
			String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}


		return postResponse;
	}

	public Response postWithOnlyFile(String url, File file, String fileKeyName, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.expect().when().post(url).then().log().all().extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.expect().when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public Response postWithFile(String url, Object body, File file, String fileKeyName, String contentHeader,
			String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.body(body).contentType(contentHeader).expect().when().post(url).then().log().all().extract()
					.response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.body(body).contentType(contentHeader).expect().when().post(url).then().extract().response();
		}


		return postResponse;
	}

	public Response postWithFileFormParams(String url, Map<String, String> formParams, File file, String fileKeyName,
			String contentHeader, String cookie) {
		
		logger.info("Name of the file is" + file.getName());
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST:ASSURED:Sending post request to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.formParams(formParams).contentType(contentHeader).expect().when().post(url).then().log().all()
					.extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.formParams(formParams).contentType(contentHeader).expect().when().post(url).then().extract()
					.response();
		}

		return postResponse;
	}

	public Response postWithFilePathParamsFormParams(String url, Map<String, String> pathParams,
			Map<String, String> formParams, File file, String fileKeyName, String contentHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		logger.info("Name of the file is" + file.getName());

		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.multiPart(fileKeyName, file).formParams(formParams).contentType(contentHeader).expect().when()
				.post(url);
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		return postResponse;
	}

	public Response postWithQueryParams(String url, Map<String, String> queryparams, Object body, String contentHeader,
			String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).queryParams(queryparams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).queryParams(queryparams)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}

		
		return postResponse;
	}

	public Response postWithMultiHeaders(String endpoint, Object body, Map<String, String> headers,
			String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			postResponse = given().cookie(builder.build()).headers(headers).relaxedHTTPSValidation()
					.body("\"" + body + "\"").contentType(contentHeader).log().all().when().post(endpoint).then().log()
					.all().extract().response();
			
			logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).headers(headers).relaxedHTTPSValidation()
					.body("\"" + body + "\"").contentType(contentHeader).when().post(endpoint).then().extract()
					.response();
		}

		
		return postResponse;
	}

	public Response postRequestEmailNotification(String serviceUri, JSONObject jsonString, String cookie) {
		logger.info(GlobalConstants.REST_ASSURED_STRING_1 + serviceUri);
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse = null;
		if (jsonString.get("attachments").toString().isEmpty()) {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("attachments", new File(getResourcePath() + (String) jsonString.get("attachments")))
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		}

		logger.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		return postResponse;
	}

	public Response patchRequest(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a Patch request to " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().patch(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().patch(url).then().extract().response();
		}

		
		return putResponse;
	}

	public Response getWithoutParams(String url, String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a Get request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url);
			
			responseLogger(getResponse);
			logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().when().get(url);
		}

		
		return getResponse;
	}

	public Response getWithPathParam(String url, Map<String, String> patharams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(patharams).log().all()
					.when().get(url);
			
			responseLogger(getResponse);
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(patharams).when()
					.get(url);
		}

		
		return getResponse;
	}

	public Response getWithQueryParam(String url, Map<String, String> queryParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log().all()
					.when().get(url);
			
			responseLogger(getResponse);
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).when()
					.get(url);
		}

		
		return getResponse;
	}

	public Response getWithQueryParamList(String url, Map<String, List<String>> queryParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log().all()
					.when().get(url);
			
			responseLogger(getResponse);
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).when()
					.get(url);
		}
		return getResponse;
	}

	public Response getWithPathQueryParam(String url, Map<String, String> pathParams, Map<String, String> queryParams,
			String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).log().all().when().get(url);
			
			responseLogger(getResponse);
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).when().get(url);
		}
		
		return getResponse;
	}

	public Response getWithPathParamQueryParamList(String url, Map<String, String> pathParams,
			Map<String, List<String>> queryParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).log().all().when().get(url);
			
			responseLogger(getResponse);
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).when().get(url);
		}

		return getResponse;
	}

	public Response putWithoutData(String url, String contentHeader, String acceptHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a PUT request to   " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.accept(acceptHeader).log().all().when().put(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public Response putWithJson(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a PUT request to   " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().put(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public Response putWithPathParams(String url, Map<String, String> pathParams, String contentHeader,
			String acceptHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a PUT request to   " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.contentType(contentHeader).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public Response putWithQueryParams(String url, Map<String, String> queryParams, String contentHeader,
			String acceptHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a PUT request to   " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public Response putWithPathParamsBody(String url, Map<String, String> pathParams, Object body, String contentHeader,
			String acceptHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a PUT request to   " + url);
			
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public Response deleteWithPathParams(String url, Map<String, String> pathParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).log().all()
					.when().delete(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).when()
					.delete(url).then().extract().response();
		}
		return getResponse;
	}

	public Response deleteWithQueryParams(String url, Map<String, String> queryParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log().all()
					.when().delete(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).when()
					.delete(url).then().extract().response();
		}
		return getResponse;
	}

	public Response deleteWithPathQueryParams(String url, Map<String, String> pathParams,
			Map<String, String> queryParams, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).log().all().when().delete(url).then().log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
					.queryParams(queryParams).when().delete(url).then().extract().response();
		}

		
		return getResponse;
	}

	public Response getConfigProperties(String url) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().relaxedHTTPSValidation().log().all().when().get(url).then().log().all().extract()
					.response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().relaxedHTTPSValidation().when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public Response deleteWithoutParams(String url, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
			
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().delete(url).then()
					.log().all().extract().response();
			
			logger.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		} else {
			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().when().delete(url).then().extract()
					.response();
		}
		return getResponse;
	}

	public Response postJSONwithFile(Object body, File file, String url, String contentHeader, String cookie) {
		Response getResponse = null;

		String Document_request = readProperty("IDRepo").get("req.Documentrequest");

		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file)
				.formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}
}
