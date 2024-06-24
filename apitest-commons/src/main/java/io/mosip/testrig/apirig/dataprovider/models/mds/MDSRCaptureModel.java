package io.mosip.testrig.apirig.dataprovider.models.mds;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class MDSRCaptureModel {
	
	private static final Logger logger = LoggerFactory.getLogger(MDSRCaptureModel.class);

	Hashtable<String, List<MDSDeviceCaptureModel>> lstBiometrics;

	public MDSRCaptureModel() {
		lstBiometrics = new Hashtable<String, List<MDSDeviceCaptureModel>>();
	}
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
