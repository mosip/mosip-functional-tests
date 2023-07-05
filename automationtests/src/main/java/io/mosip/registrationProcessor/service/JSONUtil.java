/**
 * 
 */
package io.mosip.registrationProcessor.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.admin.fw.util.AdminTestUtil;

/**
 * @author Gaurav Sharan
 *
 */
public class JSONUtil {
	
	private static final Logger logger = Logger.getLogger(JSONUtil.class);

	public void writeDataToFile(String data, String filePath) {

		try (FileWriter file = new FileWriter(filePath)) {

			file.write(data);
			file.flush();

		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
	}

	public JSONObject loadJsonFromFile(String jsonFile) {

		Gson gson = new Gson();
		BufferedReader bufferedReader = null;
		JSONObject json = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(jsonFile);
			bufferedReader = new BufferedReader(fileReader);
			json = gson.fromJson(bufferedReader, JSONObject.class);
		} catch (FileNotFoundException | NullPointerException e) {
			logger.error(e.getStackTrace());
		} finally {
			AdminTestUtil.closeBufferedReader(bufferedReader);
			AdminTestUtil.closeFileReader(fileReader);
		}
		return json;
	}

	public void writeJSONToFile(String packetMetaInfoFile, JSONObject jsonObject) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(packetMetaInfoFile);
			fileWriter.write(jsonObject.toJSONString());
		} catch (NullPointerException | IOException e) {
			logger.error(e.getStackTrace());
		}finally {
			AdminTestUtil.closeFileWriter(fileWriter);
		}

	}

	
}
