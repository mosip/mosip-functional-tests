package io.mosip.testrig.apirig.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EncryptionRequestDto {
	
	private Map<String, Object> identityRequest;

}
