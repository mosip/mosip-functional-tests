package io.moisp.preRegistration.utility;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
	public void parseAndwriteJsonFile(String inputFilePath, Map<String, String> fieldvalue, String outputFilePath,
			Properties properties) {
		try {
			Path path = Paths.get(inputFilePath);
			byte[] bytes = Files.readAllBytes(path);
			String contents = new String(bytes);
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(contents, Object.class);
			for (Entry<String, String> map : fieldvalue.entrySet()) {
				PropertyUtils.setProperty(jsonObj, properties.getProperty(map.getKey()),
						map.getValue());
			}
			mapper.writeValue(new FileOutputStream(outputFilePath), jsonObj);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
