package io.mosip.device.register.dto;

public class UploadCaCertificateDto {
	public String certificateData;
	public String partnerDomain;
	public UploadCaCertificateDto(String certificateData, String partnerDomain) {
		super();
		this.certificateData = certificateData;
		this.partnerDomain = partnerDomain;
	}
}
