package io.mosip.testrig.apirig.authentication.fw.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;
	String requestHMAC;
}
