package io.mosip.registrationProcessor.perf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserIdDto {

	private String centerId;
	private String machineId;
	private String userId;
}
