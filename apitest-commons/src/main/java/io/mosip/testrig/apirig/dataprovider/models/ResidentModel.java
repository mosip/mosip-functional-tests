package io.mosip.testrig.apirig.dataprovider.models;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class ResidentModel  implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(ResidentModel.class);
	private static final long serialVersionUID = 1L;
	private BiometricDataModel biometric;
	
	
	private List<String> missAttributes;
	private List<String> invalidAttributes;
	private List<String> filteredBioAttribtures;
	private List<BioModality> bioExceptions;
	
	private String path;
	private Hashtable<String,Integer> docIndexes;
	private Hashtable<String,String> addtionalAttributes;
	
	private Boolean skipFinger;
	private Boolean skipFace;
	private Boolean skipIris;
	
	public ResidentModel() {
	
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
