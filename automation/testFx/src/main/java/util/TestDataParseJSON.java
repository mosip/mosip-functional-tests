package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class TestDataParseJSON {

	private JSONObject file;

	public TestDataParseJSON(String fileName) {
		try {
			this.file = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE)
					.parse(new FileReader(new File(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getDataFromJsonViaKey(String keyName) {
		return this.file.get(keyName).toString();
	}

	public Object getDataFromJsonViaKey(Object obj, String keyName) throws ParseException {

		JSONObject jsonObj;
		try {
			
			jsonObj = (JSONObject) new JSONParser().parse(obj.toString());
			
			return jsonObj.get(keyName);
		} catch (ClassCastException e) {
			// in case value is an array
			JSONArray jsonArray = null;
			 jsonArray = (JSONArray) obj;
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				if (object.get("language").equals(keyName)) {
					return object.get("value");
				}
				;
			}
		}
		return "";

	}

	
	public static void main(String args[]) throws ParseException {
	TestDataParseJSON dataParseJSON = new TestDataParseJSON(System.getProperty("user.dir") + "\\test_data\\SampleData.json");
	String[] idObj = { "request", "demographicDetails", "identity", "FullName","fr"};
	Object	obj = dataParseJSON.getDataFromJsonViaKey(idObj);
	System.out.println(obj);
}
	/**
	 * @param keyName A String type array, containing the key names in hierarchical
	 *                order
	 * @return Object containing the value corresponding to key
	 * @throws ParseException 
	 */
	public Object getDataFromJsonViaKey(String... keyName) throws ParseException {
		int level = keyName.length;
		Object obj = getDataFromJsonViaKey(keyName[0]);
		for (int i = 1; i < level; i++) {
			obj = getDataFromJsonViaKey(obj, keyName[i]);
		}
		return obj;
	}

}
