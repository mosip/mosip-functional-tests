package io.mosip.admin.fw.util;

import lombok.Data;

@Data
public class TestCaseDTO {
	private String testCaseName;
	private String endPoint;
	private String role;
	private String input;
	private String output;
	private String inputTemplate;
	private String outputTemplate;
	private String restMethod;
}
