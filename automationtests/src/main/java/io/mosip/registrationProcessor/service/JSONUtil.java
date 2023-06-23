/**
 * 
 */
package io.mosip.registrationProcessor.service;

import java.io.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.testscripts.BioAuth;

/**
 * @author Gaurav Sharan
 *
 */
public class JSONUtil {
	
	private static final Logger logger = Logger.getLogger(JSONUtil.class);
	/*
	 * public RegProcIdDto mapJsonFileToObject(String idJsonPath) { RegProcIdDto obj
	 * = null; try { Gson gson = new Gson(); InputStream in = new
	 * FileInputStream(new File(idJsonPath)); BufferedReader br = new
	 * BufferedReader(new InputStreamReader(in)); obj = gson.fromJson(br,
	 * RegProcIdDto.class); // System.out.println("ID.json read as String is " +
	 * gson.toJson(obj)); br.close(); } catch (IOException e) { e.printStackTrace();
	 * } return obj; }
	 */

	public void writeJsonToFile(String data, String filePath) {

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

	/*
	 * public PacketMetaInfo parseMetaInfoFile(String packetMetaInfoFile) throws
	 * Exception { Gson gson = new Gson(); BufferedReader br = null; try { br = new
	 * BufferedReader(new FileReader(packetMetaInfoFile)); PacketMetaInfo obj =
	 * gson.fromJson(br, PacketMetaInfo.class); return obj; } catch
	 * (FileNotFoundException e) { throw new Exception(e); } catch (Exception e) {
	 * throw new Exception(e); } finally { if (br != null) br.close(); }
	 * 
	 * }
	 */

	/*
	 * public PacketInfo parsePacketMetaInfoFile(String jsonPath) throws Exception {
	 * Gson gson = new Gson(); BufferedReader br = null; try { br = new
	 * BufferedReader(new FileReader(jsonPath)); PacketInfo obj = gson.fromJson(br,
	 * PacketInfo.class); return obj; } catch (FileNotFoundException e) { throw new
	 * Exception(e); } catch (Exception e) { throw new Exception(e); } finally { if
	 * (br != null) br.close(); } }
	 */

	/*
	 * public PhilIdentityObject mapJsonFileToPhilObject(String idJsonPath) {
	 * PhilIdentityObject obj = null; try { Gson gson = new Gson(); InputStream in =
	 * new FileInputStream(new File(idJsonPath)); BufferedReader br = new
	 * BufferedReader(new InputStreamReader(in)); obj = gson.fromJson(br,
	 * PhilIdentityObject.class); // System.out.println("ID.json read as String is "
	 * + gson.toJson(obj)); br.close(); } catch (IOException e) {
	 * e.printStackTrace(); } return obj; }
	 */

}
