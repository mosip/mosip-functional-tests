package io.mosip.device.register.dto;

import com.google.gson.Gson;

public class DeviceInfoRequestDto {
	  private String deviceSubId;

	    private String certification;

	    private String digitalId;

	    private String firmware;

	    private String deviceExpiry;

	    private String timeStamp;

		public String getDeviceSubId() {
			return deviceSubId;
		}

		public void setDeviceSubId(String deviceSubId) {
			this.deviceSubId = deviceSubId;
		}

		public String getCertification() {
			return certification;
		}

		public void setCertification(String certification) {
			this.certification = certification;
		}

		public String getDigitalId() {
			return digitalId;
		}

		public void setDigitalId(String digitalId) {
			this.digitalId = digitalId;
		}

		public String getFirmware() {
			return firmware;
		}

		public void setFirmware(String firmware) {
			this.firmware = firmware;
		}

		public String getDeviceExpiry() {
			return deviceExpiry;
		}

		public void setDeviceExpiry(String deviceExpiry) {
			this.deviceExpiry = deviceExpiry;
		}

		public String getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(String timeStamp) {
			this.timeStamp = timeStamp;
		}

		@Override
		public String toString() {
			Gson gson=new Gson();
		return gson.toJson(this);
		}	

		
}
