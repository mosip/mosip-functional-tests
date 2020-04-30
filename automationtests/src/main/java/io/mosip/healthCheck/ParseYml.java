package io.mosip.healthCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.mosip.testrunner.MosipTestRunner;


public class ParseYml {
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> parseYML() throws FileNotFoundException {
		InputStream inputStream = null;
		inputStream = new FileInputStream(MosipTestRunner.getGlobalResourcePath()+"/healthCheck/mosip-services.yml");
		Yaml yaml = new Yaml();
		Map<String, Object> obj = (Map<String, Object>) yaml.load(inputStream);
		Map<String, Object> apiMap = (Map<String, Object>) obj;
		return apiMap;
	}
	
	public static String parseMap(Map<String,Object> apiMap) {
		String apiString="";
		for(Map.Entry<String, Object> entry:apiMap.entrySet()) {
			List<Map<String,Object>> listMap=(List<Map<String, Object>>) entry.getValue();
			for(int i=0;i<listMap.size()-1;i++) {
			String api=listMap.get(i).toString().substring(6,listMap.get(i).toString().length()-1);
			apiString=apiString+api+",";
			}
		}
		return apiString.substring(0,apiString.length());
	}
	private  File getFileFromResources(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}
}
