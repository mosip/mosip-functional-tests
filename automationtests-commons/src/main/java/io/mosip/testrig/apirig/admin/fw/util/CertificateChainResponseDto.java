package io.mosip.testrig.apirig.admin.fw.util;

import lombok.Data;

@Data
public class CertificateChainResponseDto {
    
    String caCertificate;
    
    String interCertificate;

    String partnerCertificate;
}