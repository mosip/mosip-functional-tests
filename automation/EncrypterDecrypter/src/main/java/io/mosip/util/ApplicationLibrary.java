package io.mosip.util;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import org.apache.log4j.Logger;

import io.restassured.http.Cookie;
public class ApplicationLibrary {
	private static Logger logger = Logger.getLogger(ApplicationLibrary.class);
	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader,String token) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("REST ASSURRED :: Request To Encrypt Is "+ body);		Cookie.Builder builder = new Cookie.Builder("Authorization",token);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();

		return postResponse;
	}
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {


		

		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		//logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		//logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} //
}
