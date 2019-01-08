package userlib;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.TestDataParseJSON;

public class FileUploadLibrary {

	
	public static String getFileToUpload(String type) throws IOException, ParseException {
		TestDataParseJSON json= new TestDataParseJSON(System.getProperty("user.dir")+"\\images\\inputFiles.json");
		JSONArray datas= (JSONArray) new JSONParser().parse(json.getDataFromJsonViaKey(type));
		Random rand = new Random();
		int n = rand.nextInt(datas.size());
		return datas.get(n).toString();
	}
	public static String documentTypeSwitch(int number) {
		switch(number) {
		case 0: return "passport";
		case 1: return "adhaar";
		case 2: return "drivinglicense";
		case 3: return "pancard";
		}
		return null;
	}
	public static void ovverideImage(String fileName) throws IOException {
		  FileUtils.copyFile(new File(System.getProperty("user.dir")+"\\images\\"+fileName), new File(System.getProperty("user.dir")+"\\images\\PANStubbed.jpg"));
	}
}
