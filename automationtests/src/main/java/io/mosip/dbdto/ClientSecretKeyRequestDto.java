package io.mosip.dbdto;

public class ClientSecretKeyRequestDto {

	private String clientId;
	private String secretKey;
	private String appId;

	public ClientSecretKeyRequestDto() {

	}

	public ClientSecretKeyRequestDto(String clientId, String secretKey, String appId) {
		super();
		this.clientId = clientId;
		this.secretKey = secretKey;
		this.appId = appId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}
