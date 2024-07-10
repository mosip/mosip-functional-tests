package io.mosip.testrig.apirig.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;
	String requestHMAC;
}
