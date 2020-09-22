package io.mosip.device.register.dto;

public class SignedRegisterDeviceDto {
	private String deviceData;
	private boolean isItForRegistrationDevice;
	public String getDeviceData() {
		return deviceData;
	}
	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}
	public boolean getIsItForRegistrationDevice() {
		return isItForRegistrationDevice;
	}
	public void setIsItForRegistrationDevice(boolean isItForRegistrationDevice) {
		this.isItForRegistrationDevice = isItForRegistrationDevice;
	}

}
