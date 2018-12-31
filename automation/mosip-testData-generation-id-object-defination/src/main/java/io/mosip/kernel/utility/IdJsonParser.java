package io.mosip.kernel.utility;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * @author Arjun chandramohan
 * This class parse the ID object definition file and return the property field tags
 *
 */
public class IdJsonParser {
public ArrayList<String> jsonPropertyFields(){
	
	ArrayList<String> propertiesList=new ArrayList<String>();
    Object obj = null;
	try {
		obj = new JSONParser().parse(new FileReader("./src/main/resources/IdObjectDefinition.json"));
		//obj = new JSONParser().parse(new FileReader("./IdObjectDefinition.json"));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParseException e) {
		e.printStackTrace();
	} 
    JSONObject jsonObject = (JSONObject) obj; 
    Object propertieskey = (Object) jsonObject.get("properties");
    Object identityKey=   ((HashMap) propertieskey).get("identity");
    Object innerPropertiesKey= ((JSONObject) identityKey).get("properties");
    Iterator<Map.Entry> iterator = ((Map) innerPropertiesKey).entrySet().iterator(); 
    while (iterator.hasNext()) { 
        Map.Entry pair = iterator.next(); 
        propertiesList.add((String) pair.getKey());
    }
   
	return propertiesList;
}
}
