package io.mosip.device.register;

import java.util.Map;

import io.mosip.device.register.config.DeviceLog;
import io.mosip.device.register.service.DeviceRegister;
import io.mosip.device.register.service.impl.DeviceRegisterImpl;
import io.mosip.device.register.util.DeviceUtil;

public class Runner {
	static {
		DeviceLog.setupLogger();
	}
	
	public static void main(String[] args) {
		DeviceRegister deviceRegister=new DeviceRegisterImpl();
		DeviceUtil deviceUtil = new DeviceUtil();
		Map<String, Map<String, String>> testDataMap = deviceUtil.loadDataFromCsv();
		for(Map.Entry<String,  Map<String, String>> entry:testDataMap.entrySet()) {
			deviceRegister.deviceRegister(entry.getValue());
		}
		}
		
}
