package io.mosip.admin.fw.util;

import lombok.Data;

@Data
public class CertificateChainResponseDto {
    
    String caCertificate;
    
    String interCertificate;

    String partnerCertificate;
}