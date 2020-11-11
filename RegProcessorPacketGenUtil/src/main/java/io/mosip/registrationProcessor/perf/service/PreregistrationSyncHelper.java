/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.perf.preregsync.dto.PreRegArchiveDTO;
import io.mosip.registrationProcessor.perf.dynamicFields.dto.DynamicObjectDto;
import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectElement;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.mosip.resgistrationProcessor.perf.dbaccess.RegProcPerfDaoImpl;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * @author M1030608
 *
 */
public class PreregistrationSyncHelper {
	private static Logger logger = Logger.getLogger(PreregistrationSyncHelper.class);

	private String readPropertyFromFile(String apiName) {
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/apiList.properties";
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch (IOException e) {
			logger.error("Property File " + propertyFilePath + " Was Not Found", e);
		}
		String apiEndPoint = prop.getProperty(apiName);
		return apiEndPoint;
	}

	public void downloadPreregPacket(PropertiesUtil prop, String token)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		String preregId = prop.PREREG_ID;
		String preregSyncUrl = readPropertyFromFile("preregSyncAPI") + preregId;
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		HashMap<String, String> valueMap = new HashMap<String, String>();
		Response responseObj = apiRequests.regProcGetRequest(preregSyncUrl, valueMap, token, prop);
		String responseStr = responseObj.getBody().asString();
		System.out.println(responseStr);
		PreRegArchiveDTO syncResponse = new ObjectMapper().readValue(
				new ObjectMapper().writeValueAsString(responseObj.jsonPath().get("response")), PreRegArchiveDTO.class);

		String preRegPacketPath = System.getProperty("user.dir") + File.separator + prop.PREREG_PACKET_PATH;

		String filePath = preRegPacketPath + "/" + preregId + ".zip";
		byte[] packetBytes = CryptoUtil.decodeBase64(syncResponse.getZipBytes());
		try {
			FileUtils.copyToFile(new ByteArrayInputStream(packetBytes),
					FileUtils.getFile(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath)));
		} catch (io.mosip.kernel.core.exception.IOException e) {
			e.printStackTrace();
		}
	}

	public boolean unzipPreregPacket(PropertiesUtil prop) throws Exception {
		try {
			String preregPacketParentPath = System.getProperty("user.dir") + File.separator + prop.PREREG_PACKET_PATH;
			String preregId = prop.PREREG_ID;
			String packetStr = preregPacketParentPath + File.separator + preregId + ".zip";
			String destPath = preregPacketParentPath + File.separator + preregId;
			ZipFile zipFile = new ZipFile(packetStr);
			zipFile.extractAll(destPath);
			if (new File(destPath).listFiles().length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
//		File preregPacket = null;
//		File preregpacketParentDir = new File(preregPacketParentPath);
//		for( File file : preregpacketParentDir.listFiles()) {
//			if(file.getName().contains(preregId)) {
//				preregPacket = file;
//			}
//		}
//		if(preregPacket != null ) {
//			String preregPacketPath = preregPacket.getAbsolutePath();
//			String id_json_path = preregPacketPath + File.separator + "ID.json";
//		}
	}

	public List<String> obtainIdentityKeys(List<SchemaObjectElement> objectList) {
		List<String> identityKeys = new ArrayList<>();
		for (SchemaObjectElement element : objectList) {
			if (element.getType().equalsIgnoreCase("String") || element.getType().equalsIgnoreCase("number")
					|| element.getType().equalsIgnoreCase("simpleType")) {
				identityKeys.add(element.getId());
			}
		}
		return identityKeys;
	}

	public Map<String, SchemaObjectElement> obtainIdentityMap(List<SchemaObjectElement> objectList) {
		Map<String, SchemaObjectElement> map = new HashMap<>();
		for (SchemaObjectElement element : objectList) {
			map.put(element.getId(), element);
		}
		return map;
	}

	private Object obtainPreRegObjElement(JSONObject PreRegIdObject, String elementType, String key,
			PropertiesUtil prop, Session session) {
		Object value = "";
		if (elementType.equalsIgnoreCase("string")) {
			value = (String) PreRegIdObject.get(key);
		} else if (elementType.equalsIgnoreCase("number")) {
			value = PreRegIdObject.get(key);
		} else if (elementType.equalsIgnoreCase("simpleType")) {
			Map map = (Map) PreRegIdObject.get(key);
			String prereg_value = (String) map.get("value");

			if (!prop.MULTI_LANG) {
				List<Object> elList = new ArrayList<>();
				Map<String, String> elMap = new HashMap<>();
				String strValue = obtainValueForSimpleType(key, prereg_value, "eng", prop, session);
				elMap.put("language", "eng");
				elMap.put("value", strValue);
				elList.add(elMap);
				return elList;
			}
		}
		return value;
	}

	private String obtainValueForSimpleType(String key, String key_value, String language, PropertiesUtil prop,
			Session session) {
		String value = "";
		RegProcPerfDaoImpl dao = new RegProcPerfDaoImpl();
		if (key.contains("firstName") || key.contains("middleName") || key.contains("lastName")
				|| key.contains("fullName")) {
			return key_value;
		} else if (key.contains("addressLine")) {
			return key_value;
		} else if (key.equalsIgnoreCase("gender")) {
			value = dao.getGenderNameFromCode(key_value, language, prop, session);
		} else if (key.equalsIgnoreCase("residenceStatus")) {
			value = dao.getResidenceTypeFromResidenceCode(key_value, language, prop, session);
		} else if (key.equalsIgnoreCase("bloodType")) {
			String jsonString = dao.getDynamicData(key, session);
			value = getKeyValueFromJsonObject(jsonString, key_value);
		} else if (key.equalsIgnoreCase("maritalStatus")) {
			String jsonString = dao.getDynamicData(key, session);
			value = getKeyValueFromJsonObject(jsonString, key_value);
		} else if (key.equalsIgnoreCase("region") || key.equalsIgnoreCase("province") || key.equalsIgnoreCase("city")
				|| key.equalsIgnoreCase("zone")) {
			value = dao.getLocationNameByCode(key_value, language, prop, session);
		}
		return value;
	}

	private String getKeyValueFromJsonObject(String jsonString, String key) {
		String value = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<DynamicObjectDto> list = mapper.readValue(jsonString, new TypeReference<List<DynamicObjectDto>>() {
			});
			for (DynamicObjectDto dto : list) {
				if (key.equalsIgnoreCase(dto.getCode())) {
					value = dto.getValue();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public JSONObject parsePreregIdentityJson(List<String> identityKeys, Map<String, SchemaObjectElement> idSchemaMap,
			PropertiesUtil prop, Session session) {
		JSONObject identityObject = new JSONObject();
		JSONObject innerIdentityObject = new JSONObject();
		JSONUtil jsonUtil = new JSONUtil();
		String preregPacketParentPath = System.getProperty("user.dir") + File.separator + prop.PREREG_PACKET_PATH;
		String preregId = prop.PREREG_ID;
		String preregIdJsonPath = preregPacketParentPath + File.separator + preregId + File.separator + "ID.json";
		JSONObject PreRegIdObject_outer = jsonUtil.loadJsonFromFile(preregIdJsonPath);
		JSONObject PreRegIdObject = (JSONObject) PreRegIdObject_outer.get("identity");
		for (String key : identityKeys) {
			String elementType = idSchemaMap.get(key).getType();
			if (elementType.equalsIgnoreCase("string")) {
				innerIdentityObject.put(key, obtainPreRegObjElement(PreRegIdObject, elementType, key, prop, session));
			} else if (elementType.equalsIgnoreCase("number")) {
				innerIdentityObject.put(key, Float
						.parseFloat((String) obtainPreRegObjElement(PreRegIdObject, elementType, key, prop, session)));
			} else if (elementType.equalsIgnoreCase("simpleType")) {
				innerIdentityObject.put(key, obtainPreRegObjElement(PreRegIdObject, elementType, key, prop, session));

			}
		}
		identityObject.put("identity", innerIdentityObject);
		return identityObject;
	}

	public void addDocumentsToIdJson(JSONObject idJsonObject, PropertiesUtil prop) {
		JSONUtil jsonUtil = new JSONUtil();
		String preregPacketParentPath = System.getProperty("user.dir") + File.separator + prop.PREREG_PACKET_PATH;
		String preregId = prop.PREREG_ID;
		String preregIdJsonPath = preregPacketParentPath + File.separator + preregId + File.separator + "ID.json";
		JSONObject preRegIdObject = jsonUtil.loadJsonFromFile(preregIdJsonPath);
		if (preRegIdObject.containsKey("proofOfIdentity")) {
			idJsonObject.put("proofOfIdentity", preRegIdObject.get("proofOfIdentity"));
		} else if (preRegIdObject.containsKey("proofOfAddress")) {
			idJsonObject.put("proofOfAddress", preRegIdObject.get("proofOfAddress"));
		} else if (preRegIdObject.containsKey("proofOfAddress")) {
			idJsonObject.put("proofOfRelationship", preRegIdObject.get("proofOfRelationship"));
		}
	}

	public void copyDocumentsPreregpacketToRegPacket(String idJsonPath, String identityFolder, PropertiesUtil prop) {

	}

}
