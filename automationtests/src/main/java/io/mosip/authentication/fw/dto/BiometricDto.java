package io.mosip.authentication.fw.dto;

import java.util.List; 
import java.util.Map;

/**
 * Dto Class to hold all biometric data from yml config file
 * 
 * @author Vignesh
 *
 */
public class BiometricDto {
	
	public static Map<String,Map<String, Map<String, Map<String, List<Object>>>>> biometrics;

	/**
	 * The method get all finger data
	 * 
	 * @return Map<String, Map<String, Map<String, List<Object>>>> - Finger
	 */
	public static Map<String,Map<String, Map<String, Map<String, List<Object>>>>> getBiometric() {
		return biometrics;
	}

	/**
	 * The method set finger data from config file
	 * 
	 * @param fir
	 */
	public static void setBiometric(Map<String,Map<String, Map<String, Map<String, List<Object>>>>> biometrics) {
		BiometricDto.biometrics = biometrics;
	}
}
