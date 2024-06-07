package dto;

import lombok.Data;

import java.util.Map;

@Data
public class EncryptionRequestDto {
	
	private Map<String, Object> identityRequest;

}
