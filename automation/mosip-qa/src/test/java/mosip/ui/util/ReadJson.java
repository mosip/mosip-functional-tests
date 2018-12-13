package mosip.ui.util;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class ReadJson {
	public static Map demographicIputReader(String filePath) {
		String value="";
		String mainPath="C:\\Program Files\\SmartBear\\SoapUI-5.4.0\\bin\\"+filePath;
		Map<String, String> finalMap1 = new HashMap<String, String>();
		try {

			String configPath = "";
			for (int i = 0; i < System.getProperty("user.dir").split("\\\\").length - 1; i++) {
				configPath += System.getProperty("user.dir").split("\\\\")[i] + "\\";
			}
			//configPath += "bin" + "//" + fileName;

			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			Map<String, String> map = new Gson().fromJson(jsonObject.toString(), Map.class);
			ObjectMapper oMapper = new ObjectMapper();
			Map<String, Object> valueMap = new HashMap<String, Object>();

			//finalMap1 = new HashMap<String, String>();
			Map<String, Object> attributes = new HashMap<String, Object>();
			Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				attributes.put(entry.getKey(), jsonObject.get(entry.getKey()));
			}

			for (Map.Entry<String, Object> att : attributes.entrySet()) {
				if (att.getKey() != null) {
					valueMap = oMapper.convertValue(att.getValue(), Map.class);
					for (int i = 0; i < attributes.keySet().size(); i++) {

						for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
	
							finalMap1.put(entry.getKey(), entry.getValue().toString());

						}
					}
				}

			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return finalMap1;

	}
	public static void main(String args[]) {
		Map<String, String> finalMap = new HashMap<String, String>();
		finalMap=	ReadJson.demographicIputReader("C:\\Users\\M1013977\\Desktop\\Populatedata .json");//pass the file path here
		/*for(Map.Entry<String, String> entry: finalMap.entrySet()) {
			System.out.println("Key--->"+ entry.getKey());
			System.out.println("Value-->"+entry.getValue());
		}*/
		
			System.out.println("Full name is : " +finalMap.get("fullName"));
			System.out.println("Age is : " +finalMap.get("BirthDetails"));
			String age=finalMap.get("BirthDetails").substring(21);
			String ages[]=age.split("}");
			System.out.println("Final age is : " +ages[0]);
			
		
	}
}
