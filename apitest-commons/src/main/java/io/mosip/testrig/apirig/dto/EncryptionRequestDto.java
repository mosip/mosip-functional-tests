package io.mosip.testrig.apirig.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EncryptionRequestDto {
	
	private Map<String, Object> identityRequest;

}
