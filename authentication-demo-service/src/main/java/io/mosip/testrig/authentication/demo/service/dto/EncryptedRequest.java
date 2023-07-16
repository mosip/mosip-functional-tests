package io.mosip.testrig.authentication.demo.service.dto;

import lombok.Data;

@Data
public class EncryptedRequest {
	String key;
	String data;
}
