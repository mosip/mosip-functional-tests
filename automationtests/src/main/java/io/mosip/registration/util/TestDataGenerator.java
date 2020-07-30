package io.mosip.registration.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import io.mosip.testDataDTO.YamlDTO;
import io.mosip.testrunner.MosipTestRunner;

/**
 * @author Arjun chandramohan
 *
 */
@Service
public class TestDataGenerator {
	/**
	 * @param serviceName
	 * @param testDataFileName
	 * @param inputDataParameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getYamlData(String serviceName, String testDataFileName, String input, String property) {
		String inputDataParameter = input + "_" + property;
		Yaml yaml = new Yaml();
		String testdata = null;
		InputStream inputStream = null;
		/*
		 * String testDataFilePath = "src" + "/" + "main" + "/" +
		 * "resources" + "/"+"Registration" + "/" + serviceName +
		 * "/" + testDataFileName + ".yaml";
		 */
		/*String testDataFilePath = this.getClass().getClassLoader().getResource("." + "/" + "Registration"
				+ "/" + serviceName + "/" + testDataFileName + ".yaml").getPath();*/
		String testDataFilePath=MosipTestRunner.getGlobalResourcePath()+"/"+"Registration"
				+ "/" + serviceName + "/" + testDataFileName + ".yaml";

		try {
			inputStream = new FileInputStream(testDataFilePath);
			if (inputDataParameter.contains("_empty"))
				return "";
			if (inputDataParameter.contains("_space"))
				return " ";
			if (inputDataParameter.contains("_null"))
				return null;
			YamlDTO obj = new YamlDTO();
			obj.setYamlObject((Map<String, List<Object>>) yaml.load(inputStream));
			List<Object> list = obj.getYamlObject().get(inputDataParameter);
			Random random = new Random();
			testdata = (String) list.get(random.nextInt(list.size())).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return testdata;

	}
}
