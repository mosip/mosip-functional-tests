package io.mosip.device.register.dto;

import com.google.gson.Gson;

public class DeviceRegDto {
	private String deviceId;
	private String purpose;
	private String deviceInfo;
	private String foundationalTrustProviderId;
	//private boolean hotlisted;
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getFoundationalTrustProviderId() {
		return foundationalTrustProviderId;
	}
	public void setFoundationalTrustProviderId(String foundationalTrustProviderId) {
		this.foundationalTrustProviderId = foundationalTrustProviderId;
	}
	@Override
	public String toString() {
		Gson gson=new Gson();
	return gson.toJson(this);
	}	
}
