package io.mosip.device.register.dto;

public class SecureBiometricDto {
	 private String deviceDetailId;

	    private boolean isItForRegistrationDevice;

	    private String swBinaryHash;

	    private String swCreateDateTime;

	    private String swExpiryDateTime;

	    private String swVersion;

		public String getDeviceDetailId() {
			return deviceDetailId;
		}

		public void setDeviceDetailId(String deviceDetailId) {
			this.deviceDetailId = deviceDetailId;
		}

		public boolean getIsItForRegistrationDevice() {
			return isItForRegistrationDevice;
		}

		public void setIsItForRegistrationDevice(boolean isItForRegistrationDevice) {
			this.isItForRegistrationDevice = isItForRegistrationDevice;
		}

		public String getSwBinaryHash() {
			return swBinaryHash;
		}

		public void setSwBinaryHash(String swBinaryHash) {
			this.swBinaryHash = swBinaryHash;
		}

		public String getSwCreateDateTime() {
			return swCreateDateTime;
		}

		public void setSwCreateDateTime(String swCreateDateTime) {
			this.swCreateDateTime = swCreateDateTime;
		}

		public String getSwExpiryDateTime() {
			return swExpiryDateTime;
		}

		public void setSwExpiryDateTime(String swExpiryDateTime) {
			this.swExpiryDateTime = swExpiryDateTime;
		}

		public String getSwVersion() {
			return swVersion;
		}

		public void setSwVersion(String swVersion) {
			this.swVersion = swVersion;
		}
}
