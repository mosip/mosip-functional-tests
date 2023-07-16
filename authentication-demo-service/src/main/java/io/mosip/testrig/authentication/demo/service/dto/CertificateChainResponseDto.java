package io.mosip.testrig.authentication.demo.service.dto;
import lombok.Data;

@Data
public class CertificateChainResponseDto {
    String caCertificate;
    String interCertificate;
    String partnerCertificate;
}
