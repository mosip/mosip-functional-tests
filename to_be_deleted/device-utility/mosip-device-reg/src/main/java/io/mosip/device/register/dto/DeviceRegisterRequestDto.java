package io.mosip.device.register.dto;

public class DeviceRegisterRequestDto {
	private String deviceProviderId;

	private String deviceSubTypeCode;

	private String deviceTypeCode;

	private String id;

	private boolean isItForRegistrationDevice;

	private String make;

	private String model;

	private String partnerOrganizationName;

	public String getDeviceProviderId() {
		return deviceProviderId;
	}

	public void setDeviceProviderId(String deviceProviderId) {
		this.deviceProviderId = deviceProviderId;
	}

	public String getDeviceSubTypeCode() {
		return deviceSubTypeCode;
	}

	public void setDeviceSubTypeCode(String deviceSubTypeCode) {
		this.deviceSubTypeCode = deviceSubTypeCode;
	}

	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}

	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public boolean getIsItForRegistrationDevice() {
		return isItForRegistrationDevice;
	}

	public void setIsItForRegistrationDevice(boolean isItForRegistrationDevice) {
		this.isItForRegistrationDevice = isItForRegistrationDevice;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPartnerOrganizationName() {
		return partnerOrganizationName;
	}

	public void setPartnerOrganizationName(String partnerOrganizationName) {
		this.partnerOrganizationName = partnerOrganizationName;
	}
}
