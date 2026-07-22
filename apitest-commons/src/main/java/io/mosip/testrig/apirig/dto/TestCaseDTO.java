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
	private boolean checkOnlyStatusCodeInResponse;
	private String allowedErrorCodes;
	private String[] kycFields;
	private String description;
	private String uniqueIdentifier;
	private String additionalDependencies;
	private String[] requiredSchemaFields;
	// Field names that must be declared "handle": true in the live IdSchema for the test to apply.
	// Skip-only: if a listed field is not a handle in the current schema, the test is skipped (never
	// forced). Handle designation varies per schema (env/country), so a test that exercises a field's
	// handle semantics is meaningful only where that field is actually a handle.
	private String[] requiredHandleFields;
}
