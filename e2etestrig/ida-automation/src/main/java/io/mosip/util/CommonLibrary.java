package io.mosip.util;

import static io.restassured.RestAssured.given; 

import org.apache.log4j.Logger;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class CommonLibrary extends BaseTestCase {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);
	
	public static Response postRequestToGetCookie(String url, Object body, String contentHeader, String acceptHeader) {
		logger.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

}
