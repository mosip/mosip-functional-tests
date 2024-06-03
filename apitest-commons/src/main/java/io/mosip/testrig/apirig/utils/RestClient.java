package io.mosip.testrig.apirig.utils;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * The Rest assured class to put, post, get request and response
 * 
 * @author Vignesh
 *
 */
public class RestClient {

	private static final Logger RESTCLIENT_LOGGER = Logger.getLogger(RestClient.class);

//	private static RestAssuredConfig config = RestAssured.config()
//			.httpClient(HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 500000)
//					.setParam("http.socket.timeout", 500000).setParam("http.connection-manager.timeout", 500000));

	private static RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig());
	protected static final Properties properties = AdminTestUtil.getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/application.properties");
	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequestWithAuthHeader(String url, Object body, String contentHeader, String acceptHeader,
			String authHeaderName, String authHeaderValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then()
					.log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract()
					.response();
		}
		
		return postResponse;
	}

	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithFormPathParamAndFile(String url, Map<String, String> formParams,
			Map<String, String> pathParams, File file, String fileKeyName, String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			RESTCLIENT_LOGGER.info("Name of the file is" + file.getName());
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).formParams(formParams).contentType(contentHeader).expect().when().post(url)
					.then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).formParams(formParams).contentType(contentHeader).expect().when().post(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithParamsAndFile(String url, Map<String, String> pathParams, File file,
			String fileKeyName, String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			RESTCLIENT_LOGGER.info("Name of the file is" + file.getName());
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).contentType(contentHeader).expect().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).contentType(contentHeader).expect().when().post(url).then().extract()
					.response();
		}

		return postResponse;
	}

	public static Response postWithParamsAndFile(String url, Map<String, String> pathParams, File file,
			String fileKeyName, String contentHeader, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(GlobalConstants.AUTHORIZATION, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			RESTCLIENT_LOGGER.info("Name of the file is" + file.getName());
			
			postResponse = given().cookies(tokens).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).contentType(contentHeader).expect().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookies(tokens).relaxedHTTPSValidation().multiPart(fileKeyName, file)
					.pathParams(pathParams).contentType(contentHeader).expect().when().post(url).then().extract()
					.response();
		}

		return postResponse;
	}

	public static Response postWithFormDataAndFile(String url, Map<String, String> formParams, String filePath,
			String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.multiPart("files", new File(filePath)).multiPart("tableName", formParams.get("tableName"))
					.multiPart(GlobalConstants.OPERATION, formParams.get(GlobalConstants.OPERATION))
					.multiPart("category", formParams.get("category")).expect().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
					.multiPart("files", new File(filePath)).multiPart("tableName", formParams.get("tableName"))
					.multiPart(GlobalConstants.OPERATION, formParams.get(GlobalConstants.OPERATION))
					.multiPart("category", formParams.get("category")).expect().when().post(url).then().extract()
					.response();
		}

		return postResponse;
	}

	public static Response postWithMultipartFormDataAndFile(String url, Map<String, String> formParams,
			String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);

		RequestSpecification requestSpecification = given().cookie(builder.build()).relaxedHTTPSValidation()
				.contentType(contentHeader);
		for (Map.Entry<String, String> entry : formParams.entrySet()) {
			requestSpecification.multiPart(entry.getKey(), entry.getValue());
		}
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			
			postResponse = requestSpecification.expect().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = requestSpecification.expect().when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithFormDataAndMultipleFile(String url, Map<String, String> formParams, File[] filePath,
			String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder(GlobalConstants.AUTHORIZATION, cookie);

		RequestSpecification requestSpecification = given().cookie(builder.build()).relaxedHTTPSValidation()
				.contentType(contentHeader);
		for (int i = 0; i < filePath.length; i++) {
			requestSpecification.multiPart("files", filePath[i]);
		}
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST:ASSURED:Sending post request with file to" + url);
			
			postResponse = requestSpecification.multiPart("tableName", formParams.get("tableName"))
					.multiPart(GlobalConstants.OPERATION, formParams.get(GlobalConstants.OPERATION))
					.multiPart("category", formParams.get("category")).expect().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info("REST-ASSURED: The response from request is: " + postResponse.asString());
			RESTCLIENT_LOGGER.info("REST-ASSURED: the response time is: " + postResponse.time());
		} else {
			postResponse = requestSpecification.multiPart("tableName", formParams.get("tableName"))
					.multiPart(GlobalConstants.OPERATION, formParams.get(GlobalConstants.OPERATION))
					.multiPart("category", formParams.get("category")).expect().when().post(url).then().extract()
					.response();
		}

		return postResponse;
	}

	/**
	 * REST ASSURED GET request method
	 * 
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param urls
	 * @return response
	 */
	public static Response getRequest(String url, String contentHeader, String acceptHeader, String urls) {
		Response getResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().log().all().when().get(url + "?" + urls)
					.then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().when().get(url + "?" + urls).then().extract()
					.response();
		}

		return getResponse;
	}

	public static Response postRequestWithQueryParamAndBody(String url, Object body, Map<String, String> queryParams,
			String contentHeader, String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request with query param " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithQueryParamsAndBody(String url, Object body, Map<String, Object> queryParams,
			String contentHeader, String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request with query param " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}
	
	public static Response postRequestWithQueryParamsAndBodyForDecryption(String url, Object body,
			Map<String, Object> queryParams, String contentHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request with query param " + url);

			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).log().all().when().post(url).then().log().all()
					.extract().response();

			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response putRequestWithQueryParamAndBody(String url, Object body, Map<String, String> queryParams,
			String contentHeader, String acceptHeader) {
		Response puttResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request with query param " + url);
			
			puttResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + puttResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + puttResponse.time());
		} else {
			puttResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.contentType(contentHeader).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return puttResponse;
	}

	/**
	 * REST ASSURED GET request method without type or after ?
	 * 
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param urls
	 * @return response
	 */
	public static Response getRequest(String url, String contentHeader, String acceptHeader) {
		Response getResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("RESSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().log().all().when().get(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().when().get(url).then().extract().response();
		}

		return getResponse;
	}

	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param File
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, File file, String contentHeader, String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().multiPart(file).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().multiPart(file).contentType(contentHeader)
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param string
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, String content, String contentHeader, MediaType acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(content).contentType(contentHeader)
					.accept(acceptHeader.toString()).log().all().when().post(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(content).contentType(contentHeader)
					.accept(acceptHeader.toString()).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	/**
	 * REST ASSURED PATCH request method
	 * 
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response patchRequest(String url, String body, String contentHeader, String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().patch(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static String getCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse.getCookie(cookieName);
	}

	public static Response postRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).log().all().when().post(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response deleteRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response deleteResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a DELETE request to " + url);
			
			deleteResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().delete(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + deleteResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + deleteResponse.time());
		} else {
			deleteResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().delete(url).then().extract()
					.response();
		}

		return deleteResponse;
	}

	public static Response postRequestWithoutCookie(String url, Object body, String contentHeader,
			String acceptHeader) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithBearerToken(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config)
					.contentType(contentHeader).relaxedHTTPSValidation().body(body).accept(acceptHeader).log().all()
					.when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config)
					.contentType(contentHeader).relaxedHTTPSValidation().body(body).accept(acceptHeader).when()
					.post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithHeder(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.header(cookieName, cookieValue).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.header(cookieName, cookieValue).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithMultipleHeaders(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, Map<String, String> headers) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithMultipleHeadersAndCookies(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, Map<String, String> headers) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie("XSRF-TOKEN", properties.getProperty(GlobalConstants.XSRFTOKEN))
					.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie("XSRF-TOKEN", properties.getProperty(GlobalConstants.XSRFTOKEN))
					.accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}
	
	public static Response postRequestWithMultipleHeadersAndCookies(String url, Object body, String contentHeader,
			String acceptHeader, Map<String, String> cookieValue, Map<String, String> headers) {
		Response postResponse;
		String key = GlobalConstants.TRANSACTION_ID_KEY;
		if (cookieValue.containsKey(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY))
			key = GlobalConstants.VERIFIED_TRANSACTION_ID_KEY;
		
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);

			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader)
					.cookie(GlobalConstants.XSRF_TOKEN, cookieValue.get(GlobalConstants.XSRF_TOKEN))
					.cookie(key, cookieValue.get(key)).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();

			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie("XSRF-TOKEN", cookieValue.get("X-XSRF-TOKEN"))
					.cookie(key, cookieValue.get(key)).accept(acceptHeader).when().post(url).then().log().all()
					.extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithMultipleHeadersWithoutCookie(String url, Object body, String contentHeader,
			String acceptHeader, Map<String, String> headers) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response patchRequestWithMultipleHeaders(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, Map<String, String> headers) {
		Response patchResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			patchResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.patch(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + patchResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + patchResponse.time());
		} else {
			patchResponse = given().config(config).relaxedHTTPSValidation().headers(headers).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url)
					.then().extract().response();
		}

		return patchResponse;
	}

	public static Response postRequestWithCookieAndHeader(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, String authHeaderName, String authHeaderValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log()
					.all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when()
					.post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithCookieAndHeader(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, String authHeaderName, String authHeaderValue,
			String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookies(tokens).accept(acceptHeader).log().all().when()
					.post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookies(tokens).accept(acceptHeader).when().post(url).then()
					.extract().response();
		}

		return postResponse;
	}

	public static Response patchRequestWithCookieAndHeader(String url, Object body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, String authHeaderName, String authHeaderValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log()
					.all().when().patch(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().header(authHeaderName, authHeaderValue)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when()
					.patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response getRequestWithCookie(String url, String contentHeader, String acceptHeader, String urls,
			String cookieName, String cookieValue) {
		Response getResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().cookie(cookieName, cookieValue).log().all()
					.when().get(url + "?" + urls).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().cookie(cookieName, cookieValue).when()
					.get(url + "?" + urls).then().extract().response();
		}

		return getResponse;
	}

	public static Response patchRequestWithCookie(String url, String body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().patch(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response patchRequestWithCookie(String url, String body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).log().all().when().patch(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).when().patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response patchRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().patch(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response getRequestWithCookie(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response getResponse;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().cookie(cookieName, cookieValue).log().all()
					.when().get(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().cookie(cookieName, cookieValue).when()
					.get(url).then().extract().response();
		}

		return getResponse;
	}
	
	public static Response getRequestWithMultipleCookie(String url, String contentHeader, String acceptHeader,
			Map<String, String> cookieMap) {
		Response getResponse;
		String key = GlobalConstants.TRANSACTION_ID_KEY;
		if (cookieMap.containsKey(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY))
			key = GlobalConstants.VERIFIED_TRANSACTION_ID_KEY;
		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);

			getResponse = given().config(config).relaxedHTTPSValidation()
					.cookie(GlobalConstants.XSRF_TOKEN, cookieMap.get(GlobalConstants.XSRF_TOKEN))
					.cookie(key, cookieMap.get(key)).log().all().when().get(url).then().log().all().extract()
					.response();

			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation()
					.cookie(GlobalConstants.XSRF_TOKEN, cookieMap.get(GlobalConstants.XSRF_TOKEN))
					.cookie(key, cookieMap.get(key)).when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithCookie(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().cookies(tokens).log().all().when().get(url)
					.then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().cookies(tokens).when().get(url).then()
					.extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithBearerToken(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().headers(cookieName, "Bearer " + cookieValue)
					.log().all().when().get(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().headers(cookieName, "Bearer " + cookieValue)
					.when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithCookieForKeyCloak(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config).contentType(contentHeader)
					.relaxedHTTPSValidation().accept(acceptHeader).log().all().when().get(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config).contentType(contentHeader)
					.relaxedHTTPSValidation().accept(acceptHeader).when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithCookieForUin(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation()
					.header(new Header("cookie", cookieName + cookieValue)).log().all().when().get(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation()
					.header(new Header("cookie", cookieName + cookieValue)).when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response postRequestWithCookie(String url, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response putRequestWithParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response patchRequestWithParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().patch(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response putWithPathParamsBodyAndCookie(String url, Map<String, String> pathParams, String body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.put(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().put(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response putWithPathParamsBodyAndBearerToken(String url, Map<String, String> pathParams, String body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).headers(cookieName, "Bearer " + cookieValue).accept(acceptHeader).log()
					.all().when().put(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).headers(cookieName, "Bearer " + cookieValue).accept(acceptHeader).when()
					.put(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithPathParamsBodyAndCookie(String url, Map<String, String> pathParams, String body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithPathParamsBodyHeadersAndCookie(String url, Map<String, String> pathParams,
			String body, String contentHeader, String acceptHeader, String cookieName, String cookieValue,
			Map<String, String> headers) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).pathParams(pathParams)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log()
					.all().when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().headers(headers).pathParams(pathParams)
					.body(body).contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when()
					.post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithQueryParamsBodyAndCookie(String url, Map<String, String> queryParams, String body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(queryParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(queryParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response postWithBodyAndCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response patchWithPathParamsBodyAndCookie(String url, Map<String, String> pathParams, String body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when()
					.patch(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().pathParams(pathParams).body(body)
					.contentType(contentHeader).cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url)
					.then().extract().response();
		}

		return postResponse;
	}

	public static Response postRequestWithQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().post(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().post(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response putRequestWithQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return postResponse;
	}

	public static Response putRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			putResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().put(url).then().log().all()
					.extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public static Response putRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response putResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PUT request to " + url);
			
			putResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).log().all().when().put(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + putResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + putResponse.time());
		} else {
			putResponse = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader)
					.cookies(tokens).accept(acceptHeader).when().put(url).then().extract().response();
		}

		return putResponse;
	}

	public static Response getRequestWithCookieAndPathParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).log().all().when().get(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithCookieAndPathParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).log().all()
					.when().get(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).when()
					.get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithCookieAndPathParmForKeyCloak(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config).contentType(contentHeader)
					.relaxedHTTPSValidation().body(body).accept(acceptHeader).log().all().when().get(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config).contentType(contentHeader)
					.relaxedHTTPSValidation().body(body).accept(acceptHeader).when().get(url).then().extract()
					.response();
		}

		return getResponse;
	}

	public static byte[] getPdf(String url, Map<String, String> body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType("application/pdf")
					.accept("*/*").cookie(cookieName, cookieValue).log().all().when().get(url).then().extract()
					.asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType("application/pdf")
					.accept("*/*").cookie(cookieName, cookieValue).when().get(url).then().extract().asByteArray();
		}
		return pdf;
	}

	public static byte[] getPdf(String url, Map<String, String> body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType("application/pdf")
					.accept("*/*").cookies(tokens).log().all().when().get(url).then().extract().asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().pathParams(body).contentType("application/pdf")
					.accept("*/*").cookies(tokens).when().get(url).then().extract().asByteArray();
		}

		return pdf;
	}

	public static byte[] postWithBodyForPdf(String url, String body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue) {
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader).accept("*/*")
					.cookie(cookieName, cookieValue).log().all().when().get(url).then().extract().asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader).accept("*/*")
					.cookie(cookieName, cookieValue).when().get(url).then().extract().asByteArray();
		}

		return pdf;
	}

	public static byte[] postWithBodyForPdf(String url, String body, String contentHeader, String acceptHeader,
			String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader).accept("*/*")
					.cookies(tokens).log().all().when().get(url).then().extract().asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().body(body).contentType(contentHeader).accept("*/*")
					.cookies(tokens).when().get(url).then().extract().asByteArray();
		}

		return pdf;
	}

	public static byte[] getPdfWithQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType("application/pdf")
					.accept("*/*").cookie(cookieName, cookieValue).log().all().when().get(url).then().extract()
					.asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType("application/pdf")
					.accept("*/*").cookie(cookieName, cookieValue).when().get(url).then().extract().asByteArray();
		}

		return pdf;
	}

	public static byte[] getPdfWithQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue, String idTokenName, String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		byte[] pdf;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			pdf = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType("application/pdf")
					.accept("*/*").cookies(tokens).log().all().when().get(url).then().extract().asByteArray();
		} else {
			pdf = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType("application/pdf")
					.accept("*/*").cookies(tokens).when().get(url).then().extract().asByteArray();
		}

		return pdf;
	}

	public static Response getRequestWithCookieAndQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader, String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().queryParams(body)
					.cookie(cookieName, cookieValue).log().all().when().get(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().queryParams(body)
					.cookie(cookieName, cookieValue).when().get(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response getRequestWithQueryParm(String url, Map<String, String> body, String contentHeader,
			String acceptHeader) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).log().all().when().get(url)
					.then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).when().get(url).then()
					.extract().response();
		}

		return getResponse;
	}

	public static Response patchRequestWithCookieAndQueryParm(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).log().all().when().patch(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().queryParams(body).contentType(contentHeader)
					.cookie(cookieName, cookieValue).accept(acceptHeader).when().patch(url).then().extract().response();
		}
		return postResponse;
	}

	public static Response deleteRequestWithCookieAndPathParm(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response deleteResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a DELETE request to " + url);
			
			deleteResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).log().all().when().delete(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + deleteResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + deleteResponse.time());
		} else {
			deleteResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).when().delete(url).then().extract().response();
		}

		return deleteResponse;
	}

	public static Response deleteRequestWithCookieAndPathParm(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue, String idTokenName,
			String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response deleteResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a DELETE request to " + url);
			
			deleteResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).log()
					.all().when().delete(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + deleteResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + deleteResponse.time());
		} else {
			deleteResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).when()
					.delete(url).then().extract().response();
		}

		return deleteResponse;
	}

	public static Response deleteRequest(String url, String contentHeader, String acceptHeader) {
		Response deleteResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a DELETE request to " + url);
			
			deleteResponse = given().config(config).relaxedHTTPSValidation().log().all().when().delete(url).then().log()
					.all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + deleteResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + deleteResponse.time());
		} else {
			deleteResponse = given().config(config).relaxedHTTPSValidation().when().delete(url).then().extract()
					.response();
		}

		return deleteResponse;
	}

	public static Response deleteRequestWithCookieAndPathParmForKeyCloak(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response deleteResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a DELETE request to " + url);
			
			deleteResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config)
					.contentType(contentHeader).relaxedHTTPSValidation().body(body).accept(acceptHeader).log().all()
					.when().delete(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + deleteResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + deleteResponse.time());
		} else {
			deleteResponse = given().headers(cookieName, "Bearer " + cookieValue).config(config)
					.contentType(contentHeader).relaxedHTTPSValidation().body(body).accept(acceptHeader).when()
					.delete(url).then().extract().response();
		}

		return deleteResponse;
	}

	public static Response postRequestWithCookieAndOnlyPathParm(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue) {
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).log().all().when().post(url).then().log().all().extract()
					.response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body)
					.cookie(cookieName, cookieValue).when().post(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response postRequestWithCookieAndOnlyPathParm(String url, Map<String, String> body,
			String contentHeader, String acceptHeader, String cookieName, String cookieValue, String idTokenName,
			String idTokenValue) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put(cookieName, cookieValue);
		tokens.put(idTokenName, idTokenValue);
		Response getResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_1 + url);
			
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).log().all()
					.when().post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + getResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + getResponse.time());
		} else {
			getResponse = given().config(config).relaxedHTTPSValidation().pathParams(body).cookies(tokens).when()
					.post(url).then().extract().response();
		}

		return getResponse;
	}

	public static Response postRequestWithQueryParamBodyAndCookie(String url, Object body,
			Map<String, String> queryParams, String contentHeader, String acceptHeader, String cookieName,
			String cookieValue) {
		Response postResponse;

		if (ConfigManager.IsDebugEnabled()) {
			RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request with query param " + url);
			
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.cookie(cookieName, cookieValue).contentType(contentHeader).accept(acceptHeader).log().all().when()
					.post(url).then().log().all().extract().response();
			
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_2 + postResponse.asString());
			RESTCLIENT_LOGGER.info(GlobalConstants.REST_ASSURED_STRING_3 + postResponse.time());
		} else {
			postResponse = given().config(config).relaxedHTTPSValidation().body(body).queryParams(queryParams)
					.cookie(cookieName, cookieValue).contentType(contentHeader).accept(acceptHeader).when().post(url)
					.then().extract().response();
		}

		return postResponse;
	}
}
