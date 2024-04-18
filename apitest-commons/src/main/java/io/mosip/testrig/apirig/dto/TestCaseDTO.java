package io.mosip.testrig.apirig.dto;

import lombok.Data;

@Data
public class TestCaseDTO {
	private String testCaseName;
	private String endPoint;
	private String role;
	private String[] templateFields;
	private String input;
	private String output;
	private String inputTemplate;
	private String outputTemplate;
	private String restMethod;
	private boolean regenerateHbs;
	private boolean validityCheckRequired;
	private boolean auditLogCheck;
	private boolean checkErrorsOnlyInResponse;
	private String allowedErrorCodes;
	private String[] kycFields;
}
