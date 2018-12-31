package io.moisp.preRegistration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.beanutils.PropertyUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.moisp.preRegistration.dto.YamlDTO;
import io.moisp.preRegistration.utility.JsonParser;
import io.mosip.preRegistration.helper.PropertyFileLoader;
import javafx.print.JobSettings;


public class PreRegistrationTestDataGenerator {
	public static String generateTestData() {
		Yaml yaml = new Yaml();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("./src/test/resources/PreRegistration/PreRegistration-MasterData.yaml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//YamlDemoConfig obj = yaml.load(inputStream);
		YamlDTO obj = new YamlDTO();
		 obj.setPreReg((Map<String, Map<String, List<Object>>>) yaml.load(inputStream));
		 String[] requiredfield=PropertyFileLoader.configFileReaderObject("PreRegistration-Config").getProperty("required.fields").split(",");
		 List<Object> list = obj.getPreReg().get("valid"+"_"+requiredfield[0]).get("ANY");
		 Random ran = new Random();
		 String str = list.get(ran.nextInt(list.size())).toString();
		 System.out.println(str);
		 return str;
	}
	public static void demo() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String fileName = "./src/test/resources/PreRegistration/inputJson.json";
		Path path = Paths.get(fileName);
		byte[] bytes = Files.readAllBytes(path);
		String contents = new String(bytes);
		ObjectMapper mapper = new ObjectMapper();

		Object jsonObj = mapper.readValue(contents, Object.class);
		Object name = PropertyUtils.getProperty(jsonObj, "request.demographicDetails.identity.(FullName)[1].value");
		System.out.println("Name:" + name);
		Properties prop=PropertyFileLoader.configFileReaderObject("PreRegistration-Config");
		PropertyUtils.setProperty(jsonObj, prop.getProperty("FullName.path"), PreRegistrationTestDataGenerator.generateTestData());
		mapper.writeValue(new FileOutputStream(fileName), jsonObj);
		
		
		 Object obj = null;
			try {
				obj = new JSONParser().parse(new FileReader("./src/test/resources/PreRegistration/inputJson.json"));
				//obj = new JSONParser().parse(new FileReader("./IdObjectDefinition.json"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} 
		    
		    JSONObject obj2=new JSONObject();
		    obj2.putAll((Map) jsonObj);
		    
		    
		    
		    String jsonLocation="src\\test\\resources\\PreRegistration\\"+"output.json";
			FileWriter file=null;
			try {
					file = new FileWriter(jsonLocation+".json");
				file.write(obj2.toJSONString());
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    
		   
	}

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		/*PreRegistrationTestDataGenerator.generateTestData();
		JsonParser jsonParser=new JsonParser();
		Properties properties=PropertyFileLoader.configFileReaderObject("PreRegistration-Config");
		Map<String, String> map = null;
		map.put("request.demographicDetails.identity.(FullName)[1].language", "arjun");
		PropertyUtils.setProperty(jsonObj, "request.demographicDetails.identity.(FullName)[1].language", "yyyy");
		jsonParser.parseAndwriteJsonFile("./src/test/resources/PreRegistration/inputJson.json", map, "./src/test/resources/PreRegistration/output.json", properties);
	*/
		PreRegistrationTestDataGenerator.demo();
	}

}
