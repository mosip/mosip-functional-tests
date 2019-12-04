package io.mosip.authentication.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.dto.BiometricDto;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class BiometricTestDataProcessor {
	
	private static final Logger bioMetricTestDataProcLogger = Logger.getLogger(BiometricTestDataProcessor.class);
	
	@SuppressWarnings("unchecked")
	public static void loadBiometricTestData(File filePath) {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(filePath.getAbsoluteFile());
			BiometricDto.setBiometric((Map<String, Map<String, Map<String, Map<String, List<Object>>>>>) yaml.load(inputStream));
			inputStream.close();
		} catch (Exception e) {
			bioMetricTestDataProcLogger.error("Exception Occured in biometric testdata processor : " + e.getMessage());
		}
	}
	
	public static String getBioMetricTestData(String bioType, String bioSubType, String thresholdPercentage) {
		loadBiometricTestData(new File(RunConfigUtil.getBioTestDataPath()));
		List<Object> listOfBioData = BiometricDto.getBiometric().get("biometrics").get(bioType).get(bioSubType)
				.get(thresholdPercentage);
		Random random = new Random();
		return (String) listOfBioData.get(random.nextInt(listOfBioData.size())).toString();
	}
}
