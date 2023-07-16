package io.mosip.testrig.authentication.demo.service.dto;

import lombok.Data;

@Data
public class CryptomanagerRequestDto {
	String applicationId;
	String data;
	String referenceId;
	String salt;
	String aad;
	String timeStamp;
	
}
