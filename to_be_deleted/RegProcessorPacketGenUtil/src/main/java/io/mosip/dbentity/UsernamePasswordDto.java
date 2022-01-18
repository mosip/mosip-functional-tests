package io.mosip.dbentity;

public class UsernamePasswordDto {
	
	private String userName;
	private String password;
	private String appId;
	
	public UsernamePasswordDto() {
		
	}

	public UsernamePasswordDto(String userName, String password, String appId) {
		this.userName = userName;
		this.password = password;
		this.appId = appId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}
