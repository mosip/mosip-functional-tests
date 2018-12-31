package io.mosip.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetHeader {

	public static String getHeader(JSONObject object) throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,String> result =
		        new ObjectMapper().readValue(object.toString(), HashMap.class);
		
		String string = "";
		for(Map.Entry<String,String> entry: result.entrySet()) {
			string=string+entry.getKey().toString();
			string= string+"="+entry.getValue();
			string=string+"&&";
		}
		return string.substring(0, string.length()-2);
	}
}
