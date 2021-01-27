package io.mosip.device.register.dto;

public class ApproverDto {
    private String approvalStatus;

    private String id;

    private boolean isItForRegistrationDevice;

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public String getId() {
		return id;
	}

	public boolean getIsItForRegistrationDevice() {
		return isItForRegistrationDevice;
	}

	public void setIsItForRegistrationDevice(boolean isItForRegistrationDevice) {
		this.isItForRegistrationDevice = isItForRegistrationDevice;
	}
}
