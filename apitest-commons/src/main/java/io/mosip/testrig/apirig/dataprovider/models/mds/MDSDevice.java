package io.mosip.testrig.apirig.dataprovider.models.mds;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
@JsonIgnoreProperties({"error"})
public class MDSDevice {
	private static final Logger logger = LoggerFactory.getLogger(MDSDevice.class);

	String purpose;
	List<String> deviceSubId;
	String digitalId;
	String deviceStatus;
	String deviceId;
	String deviceCode;
	String certification;
	String serviceVersion;
	List<String> specVersion;
	String callbackId;
	

	public String toJSONString() {
		
		ObjectMapper mapper = new ObjectMapper();

		String jsonStr ="";
		try {
				jsonStr = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
				
			logger.error(e.getMessage());
		}	
		return jsonStr;
	}
}
