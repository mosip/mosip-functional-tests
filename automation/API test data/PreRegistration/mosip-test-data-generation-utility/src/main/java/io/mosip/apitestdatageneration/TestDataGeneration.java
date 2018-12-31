package io.mosip.apitestdatageneration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.moisp.preRegistration.PreRegistrationTestDataGenerator;
import io.mosip.apitestdatageneration.dto.YamlDTO;
import io.mosip.apitestdatageneration.helper.PropertyFileLoader;


public class TestDataGeneration {
	public void generateTestData() throws IOException {
		Yaml yaml = new Yaml();
		Properties fileLoader=new PropertyFileLoader().readPropertyFile("Config");
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileLoader.getProperty("master.yaml.location"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		YamlDTO yamldto = new YamlDTO();
		yamldto.setYamlData((Map<String, Map<String, List<Object>>>) yaml.load(inputStream));
		//List<Object> list = yamldto.getYamlData().get("valid"+"_"+"postalcode").get("BV");
		
		
		String inputFileLocation=fileLoader.getProperty("input.json.file.location");
		String outputFileLocation=fileLoader.getProperty("output.json.file.location");
		Set<Object> requiredKeys=fileLoader.keySet();
		requiredKeys.remove("NumberofOutputJson");
		requiredKeys.remove("master.yaml.location");
		requiredKeys.remove("json.output.file.name");
		requiredKeys.remove("module");
		requiredKeys.remove("apiname");
		requiredKeys.remove("input.json.file.location");
		requiredKeys.remove("output.json.file.location");
		
		System.err.println(fileLoader.keySet());
		
		
		System.out.println(requiredKeys);
		
		for(Object reqFields:requiredKeys) {
			if(reqFields.equals("FullName.value") || reqFields.equals("postalcode.value"))
			{
				List<Object> list = yamldto.getYamlData().get(fileLoader.get(reqFields).toString().split("_")[0]+"_"+reqFields.toString()).get(fileLoader.get(reqFields).toString().split("_")[1]);
				System.out.println(list);
				 Random ran = new Random();
				 String str = list.get(ran.nextInt(list.size())).toString();
				 System.out.println(str);
				 
				 PropertyUtils.setProperty(jsonObj, prop.getProperty("fullName[0].value.path"),str);
				 
				 PropertyUtils.setProperty(jsonObj, prop.getProperty("fullName[1].value.path"),str);
			}
			else
			{
				List<Object> list = yamldto.getYamlData().get(fileLoader.get(reqFields).toString().split("_")[0]+"_"+reqFields.toString()).get(fileLoader.get(reqFields).toString().split("_")[1]);
				System.out.println(list);
				 Random ran = new Random();
				 String str = list.get(ran.nextInt(list.size())).toString();
				 System.out.println(str);
				 
				 PropertyUtils.setProperty(jsonObj, prop.getProperty("fullName[0].value.path"),str);
			}
			
			
			 
			 
			
		}
		
		
		
		//stubbing in josn
		
		
		Path path = Paths.get(inputFileLocation);
		byte[] bytes = Files.readAllBytes(path);
		String contents = new String(bytes);
		ObjectMapper mapper = new ObjectMapper();
		Object jsonObj = mapper.readValue(contents, Object.class);
		mapper.writeValue(new FileOutputStream(outputFileLocation), jsonObj);
		
		// String str = list.get(ran.nextInt(list.size())).toString();
		 //System.out.println(str);
	}
	public static void main(String[] args) throws IOException {
		new TestDataGeneration().generateTestData();
	}

}
