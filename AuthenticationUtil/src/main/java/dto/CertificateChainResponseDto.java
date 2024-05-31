package dto;
import lombok.Data;

@Data
public class CertificateChainResponseDto {
    String caCertificate;
    String interCertificate;
    String partnerCertificate;
}
