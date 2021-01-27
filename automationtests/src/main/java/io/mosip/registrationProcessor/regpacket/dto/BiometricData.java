package io.mosip.registrationProcessor.regpacket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BiometricData {

	private String format;
	private Float version;
	private String value;

}
