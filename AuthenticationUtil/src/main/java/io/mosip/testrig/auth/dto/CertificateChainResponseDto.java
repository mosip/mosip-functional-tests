package io.mosip.testrig.auth.dto;
import lombok.Data;

@Data
public class CertificateChainResponseDto {
    String caCertificate;
    String interCertificate;
    String partnerCertificate;
}
