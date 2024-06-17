package io.mosip.testrig.apirig.dataprovider.models;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
@Data
public class MosipBiometricAttributeModel implements Serializable{
	private static final Logger logger = LoggerFactory.getLogger(MosipBiometricAttributeModel.class);

	 private static final long serialVersionUID = 1L;
	 private String biometricTypeCode;
		
	 private String code;
	 private String description;
	 private Boolean isActive;
	 private String langCode;
	 private String name;
	 
	 public String toJSONString() {
			
			ObjectMapper Obj = new ObjectMapper();
			String jsonStr ="";
			try {
					jsonStr = Obj.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage());
			}	
			return jsonStr;
		}
}
