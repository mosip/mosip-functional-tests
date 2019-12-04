package io.mosip.authentication.fw.dto;

import java.util.Map;

public class AuthTypeStatusDto {
	
	public String authType;
	public String authSubType;
	public String locked;
	
	private static Map<String,String> authTypeStatus;

	public static Map<String, String> getAuthTypeStatus() {
		return authTypeStatus;
	}

	public static void setAuthTypeStatus(Map<String, String> authTypeStatus) {
		AuthTypeStatusDto.authTypeStatus = authTypeStatus;
	}

}
