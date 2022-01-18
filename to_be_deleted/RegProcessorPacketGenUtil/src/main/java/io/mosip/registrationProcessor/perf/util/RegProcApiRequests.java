package io.mosip.registrationProcessor.perf.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class RegProcApiRequests {

	private static Logger logger = Logger.getLogger(RegProcApiRequests.class);

	public RegProcApiRequests() {

	}

	public Response regProcSyncRequest(String url, Object body, String center_machine_refId, String ldt,
			String contentHeader, String regProcAuthToken, PropertiesUtil prop) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response postResponse = given().cookie(builder.build()).header("Center-Machine-RefId", center_machine_refId)
				.header("timestamp", ldt).relaxedHTTPSValidation().body("\"" + body + "\"").contentType(contentHeader)
				.when().post(prop.BASE_URL + url).then().extract().response();
		return postResponse;
	}

	public Response regProcPacketUpload(File file, String url, String regProcAuthToken, PropertiesUtil prop) {

		// logger.info("REST:ASSURED:Sending a data packet to" + prop.BASE_URL + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file).expect()
				.when().post(prop.BASE_URL + url);
		// logger.info("REST:ASSURED: The response from request is:" +
		// getResponse.asString());
		// logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;

	}

	public Response regProcGetRequest(String url, HashMap<String, String> valueMap, String regProcAuthToken,
			PropertiesUtil prop) {
		// logger.info("REST-ASSURED: Sending a GET request to " + prop.BASE_URL + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).when()
				.get(prop.BASE_URL + url).then().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	public Response regProcGetIdRepo(String url, String regProcAuthToken, PropertiesUtil prop) {
		logger.info("REST-ASSURED: Sending a GET request to " + prop.BASE_URL + url);
		String acceptHeader = MediaType.APPLICATION_JSON;
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().accept(acceptHeader).when()
				.get(prop.BASE_URL + url).then().extract().response();
		// log then response
		// logger.info("REST-ASSURED: The response from the request is: " +
		// getResponse.asString());
		// logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader,
			String token, PropertiesUtil prop) {
		// logger.info("REST:ASSURED:Sending a data packet to " + prop.BASE_URL + url);
		// logger.info("REST ASSURRED :: Request To Decrypt Is " + body);
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).when().log().all().post(prop.BASE_URL + url).then()
				.log().all().extract().response();

		return postResponse;
	}

	public Response postRequestToEncrypt(String url, Object body, String contentHeader, String acceptHeader,
			String token, PropertiesUtil prop) {
		try {
			// logger.info("REST:ASSURED:Sending a data packet for encryption to " +
			// prop.BASE_URL + url);
			// logger.info("REST ASSURRED :: Request data To encrypt is " + body);
			Cookie.Builder builder = new Cookie.Builder("Authorization", token);
			System.out.println("Sending encrypt request to: " + prop.BASE_URL + url);
			Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
					.contentType(contentHeader).accept(acceptHeader).when().post(prop.BASE_URL + url).then().extract()
					.response();
			// postResponse.then().statusCode(200);
			return postResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Response postRequestToSign(String url, Object body, String contentHeader, String acceptHeader, String token,
			PropertiesUtil prop) {
		Response postResponse = null;
		try {
			Cookie.Builder builder = new Cookie.Builder("Authorization", token);
			System.out.println("Sending sign request to: " + prop.BASE_URL + url);
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
					.contentType(contentHeader).accept(acceptHeader).log().all().when().post(prop.BASE_URL + url).then()
					.log().all().extract().response();
			// postResponse.then().statusCode(200);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return postResponse;
	}

	public Response regProcPostRequest(String url, HashMap<String, String> valueMap, String contentHeader, String token,
			PropertiesUtil prop) {
		// logger.info("REST:ASSURED:Sending a post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(valueMap)
				.contentType(contentHeader).when().post(prop.BASE_URL + url).then().extract().response();
		// log then response
		// logger.info("REST-ASSURED: The response from the request is: " +
		// postResponse.asString());
		// logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader,
			PropertiesUtil prop) {

		logger.info("URL IS :: " + prop.BASE_URL + url);
		System.out.println("Request data is : " + new Gson().toJson(body));
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(prop.BASE_URL + url).then().log().all().extract()
				.response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		System.out.println("REST-ASSURED: The response from the request is: " + postResponse.asString());
		// logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		// logger.info("REST-ASSURED:454545445 The response Time is: " +
		// postResponse.asString());
		return postResponse;
	}

	public boolean validateToken(String token, PropertiesUtil prop) {
		String url = "/v1/authmanager/authorize/admin/validateToken";
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);
		String AUTH_VALIDATION_URL = prop.BASE_URL + url;
		System.out.println(AUTH_VALIDATION_URL);
		Response response = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when()
				.get(AUTH_VALIDATION_URL).then().log().all().extract().response();
		// System.out.println(response.asString());
		List<String> errors = response.jsonPath().get("errors");
		if (errors == null) {
			return true;
		} else
			return false;
	}

}
