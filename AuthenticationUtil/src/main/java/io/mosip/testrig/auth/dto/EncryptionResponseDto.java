package io.mosip.testrig.auth.dto;

import lombok.Data;

@Data
public class EncryptionResponseDto {
	String encryptedSessionKey;
	String encryptedIdentity;
	String requestHMAC;
}
