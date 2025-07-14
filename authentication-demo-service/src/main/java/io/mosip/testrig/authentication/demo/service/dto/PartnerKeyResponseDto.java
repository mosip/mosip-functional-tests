package io.mosip.testrig.authentication.demo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartnerKeyResponseDto {

    private String caCertificate;
    private String interCertificate;
    private String partnerCertificate;
}
