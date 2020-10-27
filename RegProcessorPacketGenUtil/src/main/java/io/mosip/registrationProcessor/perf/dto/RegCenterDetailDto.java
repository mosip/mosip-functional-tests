/**
 * 
 */
package io.mosip.registrationProcessor.perf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gaurav Sharan
 *
 */
@Data
@NoArgsConstructor
public class RegCenterDetailDto {
	
	private String regCenterId;
	private String userId;
	private String machineId;

}
