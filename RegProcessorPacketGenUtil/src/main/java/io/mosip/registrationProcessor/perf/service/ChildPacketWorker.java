package io.mosip.registrationProcessor.perf.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class ChildPacketWorker {

	private static Logger logger = Logger.getLogger(ChildPacketWorker.class);
	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";
	
	public void generateNewChildPacket(Session session, PropertiesUtil prop, String auth_token) {
		
	}

}
