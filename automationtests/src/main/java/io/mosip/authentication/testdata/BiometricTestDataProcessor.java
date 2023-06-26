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

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.dto.BiometricDto;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class BiometricTestDataProcessor {
	
	private static final Logger bioMetricTestDataProcLogger = Logger.getLogger(BiometricTestDataProcessor.class);
	
	@SuppressWarnings("unchecked")
	public static void loadBiometricTestData(File filePath) {
		FileInputStream inputStream = null;
		try {
			Yaml yaml = new Yaml();
			inputStream = new FileInputStream(filePath.getAbsoluteFile());
			BiometricDto.setBiometric((Map<String, Map<String, Map<String, Map<String, List<Object>>>>>) yaml.load(inputStream));
		} catch (Exception e) {
			bioMetricTestDataProcLogger.error("Exception Occured in biometric testdata processor : " + e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
	}
	
	public static String getBioMetricTestData(String bioType, String bioSubType, String thresholdPercentage) {
		loadBiometricTestData(new File(RunConfigUtil.getBioTestDataPath()));
		List<Object> listOfBioData = BiometricDto.getBiometric().get("biometrics").get(bioType).get(bioSubType)
				.get(thresholdPercentage);
		int randomNumber = Integer.parseInt(AuthTestsUtil.generateRandomNumberString(listOfBioData.size()));
		return listOfBioData.get(randomNumber).toString();
	}
}
