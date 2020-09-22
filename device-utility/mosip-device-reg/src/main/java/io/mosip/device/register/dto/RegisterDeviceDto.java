package io.mosip.device.register.dto;

public class RegisterDeviceDto {
	private String deviceData;

	private boolean isItForRegistrationDevice;

	public String getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	public boolean isItForRegistrationDevice() {
		return isItForRegistrationDevice;
	}

	public void setItForRegistrationDevice(boolean isItForRegistrationDevice) {
		this.isItForRegistrationDevice = isItForRegistrationDevice;
	}

}
