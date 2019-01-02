package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class TestDataParseJSON {
	
	private JSONObject file;
	@SuppressWarnings("deprecation")
	public TestDataParseJSON(String fileName) {
		try {
			this.file= (JSONObject) new JSONParser().parse(new FileReader(new File(fileName)));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}	
	}
public String getDataFromJsonViaKey(String keyName){
return this.file.getAsString(keyName);	
}
}
