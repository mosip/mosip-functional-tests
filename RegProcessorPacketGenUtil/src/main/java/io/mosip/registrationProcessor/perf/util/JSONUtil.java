/**
 * 
 */
package io.mosip.registrationProcessor.perf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.registration.processor.perf.packet.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.perf.regPacket.dto.PhilIdentityObject;
import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;

/**
 * @author Gaurav Sharan
 *
 */
public class JSONUtil {

	public RegProcIdDto mapJsonFileToObject1(PropertiesUtil prop) {
		RegProcIdDto obj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			obj = mapper.readValue(new File(prop.ID_JSON_FILE), RegProcIdDto.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public RegProcIdDto mapJsonFileToObject(PropertiesUtil prop) {
		RegProcIdDto obj = null;
		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(prop.ID_JSON_FILE));
			obj = gson.fromJson(br, RegProcIdDto.class);

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public PhilIdentityObject mapJsonFileToPhilObject(String idJsonPath) {
		PhilIdentityObject obj = null;
		try {
			Gson gson = new Gson();
			InputStream in = new FileInputStream(new File(idJsonPath));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			obj = gson.fromJson(br, PhilIdentityObject.class);
			// System.out.println("ID.json read as String is " + gson.toJson(obj));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public RegProcIdDto mapJsonFileToObject(String idJsonPath) {
		// String jsonFile = "/sampleJson/ID.json";
		// idJsonPath.replaceAll("\\\\", "/");
		RegProcIdDto obj = null;
		try {
			Gson gson = new Gson();
			// idJsonPath = idJsonPath;
			// InputStream in = JSONUtil.class.getResourceAsStream(idJsonPath);
			InputStream in = new FileInputStream(new File(idJsonPath));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			obj = gson.fromJson(br, RegProcIdDto.class);
			// System.out.println("ID.json read as String is " + gson.toJson(obj));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public RegProcIdDto mapJsonFileToObject() {
		String jsonFile = "/sampleJson/ID.json";
		RegProcIdDto obj = null;
		try {
			Gson gson = new Gson();
			InputStream in = JSONUtil.class.getResourceAsStream(jsonFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			obj = gson.fromJson(br, RegProcIdDto.class);
			// System.out.println("ID.json read as String is " + gson.toJson(obj));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public void writeJsonToFile(String data, String filePath) {

		try (FileWriter file = new FileWriter(filePath)) {

			file.write(data);
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONObject loadJsonFromFile(String packetMetaInfoFile) {

		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(packetMetaInfoFile));
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

	public PacketMetaInfo parseMetaInfoFile(String packetMetaInfoFile) throws Exception {
		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(packetMetaInfoFile));
			PacketMetaInfo obj = gson.fromJson(br, PacketMetaInfo.class);
			return obj;
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (br != null)
				br.close();
		}

	}

	public PacketInfo parsePacketMetaInfoFile(String jsonPath) throws Exception {
		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(jsonPath));
			PacketInfo obj = gson.fromJson(br, PacketInfo.class);
			return obj;
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (br != null)
				br.close();
		}
	}
}
