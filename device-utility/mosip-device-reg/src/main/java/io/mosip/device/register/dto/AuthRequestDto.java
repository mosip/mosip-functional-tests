package io.mosip.device.register.dto;


public class AuthRequestDto {
	public String password;

	public String appId;

	public String userName;

	public AuthRequestDto(String password, String appId, String userName) {
		super();
		this.password = password;
		this.appId = appId;
		this.userName = userName;
	}
}
