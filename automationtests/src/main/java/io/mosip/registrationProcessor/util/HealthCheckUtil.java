package io.mosip.registrationProcessor.util;

import static io.restassured.RestAssured.given;

import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class HealthCheckUtil extends BaseTestCase {
	Response getResponse = null;
	private final String healthCheckUrl="/actuator/health";
	public boolean healthCheck(String url) {
		url=url.substring(0,url.lastIndexOf("/"));
		try {
			getResponse = given().relaxedHTTPSValidation().log()
					.all().when().get(ApplnURI+url+healthCheckUrl).then().log().all().extract().response();
			String status= getResponse.jsonPath().get("status").toString();
			if(status.equals("UP"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
}
