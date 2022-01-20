/**
 * 
 */
package io.mosip.registrationProcessor.service;

import java.io.*;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

/**
 * @author Gaurav Sharan
 *
 */
public class JSONUtil {

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
			e.printStackTrace();
		}
	}

	public JSONObject loadJsonFromFile(String jsonFile) {

		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(jsonFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		JSONObject json = gson.fromJson(br, JSONObject.class);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public void writeJSONToFile(String packetMetaInfoFile, JSONObject jsonObject) {

		try {
			FileWriter file = new FileWriter(packetMetaInfoFile);
			file.write(jsonObject.toJSONString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
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
