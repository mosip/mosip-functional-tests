package io.mosip.device.register.service.impl;

import static io.restassured.RestAssured.given;

import java.util.Properties;
import java.util.logging.Logger;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.mosip.device.register.constants.Urls;
import io.mosip.device.register.dto.AuthRequestDto;
import io.mosip.device.register.dto.AuthenticationDto;
import io.mosip.device.register.service.AuthenticationService;
import io.mosip.device.register.util.DeviceUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class AuthenticationServiceImpl implements AuthenticationService {
	public static Logger auditLog = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	@Override
	public String login() {
		 DeviceUtil deviceUtil=new DeviceUtil();
		 Properties config=deviceUtil.getConfig();
		 AuthRequestDto authRequestDto=new AuthRequestDto(config.getProperty("password"), config.getProperty("appId"), config.getProperty("username"));
		 AuthenticationDto authenticationDto=new AuthenticationDto(authRequestDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), config.getProperty("id"), config.getProperty("version"));
		    String url = Urls.USER_AUTHENTICATE;
		    RestAssured.baseURI = config.getProperty("baseUrl");
		    Response api_response = given()
		            .contentType(ContentType.JSON).body(authenticationDto).post(url).then()
		            .log().all().extract().response();
	
		    
		    ReadContext ctx = JsonPath.parse(api_response.getBody().asString());
		    if(ctx.read("$.response") == null) {
		    	auditLog.info("Request :"+ authenticationDto);
		    	throw new RuntimeException("Invalid Credentials");
		    }
		    String cookie=api_response.getCookie("Authorization");
		return cookie;
	}

}
