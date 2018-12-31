package io.mosip.service;

import javax.ws.rs.core.MediaType;

import io.mosip.util.CommonLibrary;
import io.restassured.response.Response;

public class ApplicationLibrary extends BaseTestCase{
	
	private static CommonLibrary commonLibrary = new CommonLibrary();
	
	

		
	public Response PostRequest(Object body, String Resource_URI) {
		return commonLibrary.POST_REQUEST(ApplnURI + Resource_URI, body,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
	}
	

}
