package io.mosip.testrig.authentication.demo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerKeyRequestDto {

    private String domainUrl;
    private String caP12FileName = "-ca.p12";
    private String interP12FileName = "-inter.p12";
    private String partnerP12FileName = "-partner.p12";
    private char[] p12Password = "qwerty@123".toCharArray();
    private String keyAlias = "keyalias";
    private String keyStoreType = "PKCS12";
    private String rsaAlgorithm = "RSA";
    private int rsaKeySize = 2048;
    private String signAlgorithm = "SHA256withRSA";
    private int rpPartnerCertExpiryYears = 5;

    private String prefixCA = "CA-";
    private String prefixINTER = "INTER-";
    private String prefixPARTNER  = "PARTNER-";

    private String country = "IN";
    private String state = "KA";
    private String orgUnit = "IDA-TEST-ORG-UNIT";

    private String partnerType;
    private String dirPath;
    private String organization;
    private String keyFileNameByPartnerName;
}
