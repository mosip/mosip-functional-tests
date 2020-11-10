package io.mosip.device.register.service;

import java.util.Map;

public interface DeviceRegister {
	public String createPolicyGroup(Map<String, String> testDataMap);

	public String partnerSelfRegistration(Map<String, String> testDataMap);

	public void deviceRegister(Map<String, String> testDataMap);
	
}
