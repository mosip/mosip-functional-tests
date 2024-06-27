package io.mosip.testrig.apirig.authentication.fw.dto;
import lombok.Data;

@Data
public class CertificateChainResponseDto {
    String caCertificate;
    String interCertificate;
    String partnerCertificate;
}
