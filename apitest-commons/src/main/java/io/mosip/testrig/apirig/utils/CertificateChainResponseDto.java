package io.mosip.testrig.apirig.utils;

import lombok.Data;

@Data
public class CertificateChainResponseDto {
    
    String caCertificate;
    
    String interCertificate;

    String partnerCertificate;
}