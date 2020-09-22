package io.mosip.device.register.dto;

public class DeviceRegDto {
	private String deviceId;
	private String purpose;
	private DeviceInfoRequestDto deviceInfo;
	private String foundationalTrustProviderId;
	private boolean hotlisted;
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
	public DeviceInfoRequestDto getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfoRequestDto deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getFoundationalTrustProviderId() {
		return foundationalTrustProviderId;
	}
	public void setFoundationalTrustProviderId(String foundationalTrustProviderId) {
		this.foundationalTrustProviderId = foundationalTrustProviderId;
	}
	public boolean isHotlisted() {
		return hotlisted;
	}
	public void setHotlisted(boolean hotlisted) {
		this.hotlisted = hotlisted;
	}
	
}
