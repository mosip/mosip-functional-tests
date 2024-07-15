package io.mosip.testrig.apirig.dataprovider.models.mds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class MDSDeviceCaptureModel {
	
	private static final Logger logger = LoggerFactory.getLogger(MDSDeviceCaptureModel.class);

	String bioType;
	String bioSubType;
	String qualityScore;
	String bioValue;
	String deviceServiceVersion;
	String deviceCode;
	String hash;
	String sb;
	String payload;
	
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
