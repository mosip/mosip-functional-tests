package io.mosip.testrig.authentication.demo.service.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EncryptionRequestDto {
	
	private Map<String, Object> identityRequest;

}
