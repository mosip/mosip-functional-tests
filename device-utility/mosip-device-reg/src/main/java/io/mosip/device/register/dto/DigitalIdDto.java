package io.mosip.device.register.dto;

import org.json.simple.JSONObject;

import com.google.gson.JsonObject;

public class DigitalIdDto {
	private String serialNo;

	private String deviceProvider;

	private String deviceProviderId;

	private String make;

	private String model;

	private String dateTime;

	private String type;

	private String deviceSubType;

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getDeviceProvider() {
		return deviceProvider;
	}

	public void setDeviceProvider(String deviceProvider) {
		this.deviceProvider = deviceProvider;
	}

	public String getDeviceProviderId() {
		return deviceProviderId;
	}

	public void setDeviceProviderId(String deviceProviderId) {
		this.deviceProviderId = deviceProviderId;
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

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeviceSubType() {
		return deviceSubType;
	}

	public void setDeviceSubType(String deviceSubType) {
		this.deviceSubType = deviceSubType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("serialNo", serialNo);
		jsonObject.put("deviceProvider", deviceProvider);
		jsonObject.put("deviceProviderId", deviceProviderId);
		jsonObject.put("make", make);
		jsonObject.put("model", model);
		jsonObject.put("dateTime", dateTime);
		jsonObject.put("type", type);
		jsonObject.put("deviceSubType", deviceSubType);
		return jsonObject.toJSONString();
		/*
		 * return "DeviceInfoDto [serialNo=" + serialNo + ", deviceProvider=" +
		 * deviceProvider + ", deviceProviderId=" + deviceProviderId + ", make=" + make
		 * + ", model=" + model + ", dateTime=" + dateTime + ", type=" + type +
		 * ", deviceSubType=" + deviceSubType + "]";
		 */
	}
}
