package io.mosip.device.register.dto;

public class AuthenticationDto {
	public AuthRequestDto request;

	public String metadata;

	public String requesttime;

	public String id;

	public String version;
	
	public AuthenticationDto(AuthRequestDto request, String metadata, String requesttime, String id, String version) {
		super();
		this.request = request;
		this.metadata = metadata;
		this.requesttime = requesttime;
		this.id = id;
		this.version = version;
	}
}
