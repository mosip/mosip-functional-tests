package io.mosip.registrationProcessor.perf.dynamicFields.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DynamicObjectDto {

	private String code;
	private String value;
	private String langCode;
	private String active;

}
