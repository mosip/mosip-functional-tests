package io.mosip.registrationProcessor.perf.service;

import java.io.IOException;

import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class PreregistrationSyncService {

	public void loadPreregData(PropertiesUtil prop, String token) {

		PreregistrationSyncHelper helper = new PreregistrationSyncHelper();
		try {
			helper.downloadPreregPacket(prop, token);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean unzipped = false;
		try {
		unzipped =	helper.unzipPreregPacket(prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
