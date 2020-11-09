package io.mosip.device.register.dto;

public class UploadPartnerCertificateDto {
	public String certificateData;

	public String organizationName;

	public String partnerDomain;

	public String partnerId;

	public String partnerType;

	public UploadPartnerCertificateDto(String certificateData, String organizationName, String partnerDomain,
			String partnerId, String partnerType) {
		super();
		this.certificateData = certificateData;
		this.organizationName = organizationName;
		this.partnerDomain = partnerDomain;
		this.partnerId = partnerId;
		this.partnerType = partnerType;
	}
	
	

}
