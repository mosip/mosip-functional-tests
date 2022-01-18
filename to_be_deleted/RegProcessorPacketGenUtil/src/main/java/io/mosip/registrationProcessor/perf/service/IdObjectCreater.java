/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectElement;
import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectList;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.mosip.registrationProcessor.perf.util.ResourcePathUtil;
import io.restassured.response.Response;

/**
 * @author M1030608
 *
 */
public class IdObjectCreater {
	ResourcePathUtil resourcePathUtil = new ResourcePathUtil();

	private String readPropertyFromFile(String apiName) {
		Properties prop = new Properties();
		String baseResourcePath = resourcePathUtil.getResourcePath();
		String propertyFilePath = baseResourcePath + "config/apiList.properties";
		FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch (IOException e) {
		}
		String apiEndPoint = prop.getProperty(apiName);
		return apiEndPoint;
	}

	public String createIDObject(PropertiesUtil prop, String token, Session session, String process,
			String individual_type) {

		String url = readPropertyFromFile("idschemaAPI");
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		HashMap<String, String> valueMap = new HashMap<String, String>();
		String idSchemaVersion = prop.ID_SCHEMA_VERSION;
		valueMap.put("schemaVersion", idSchemaVersion);
		Response responseObj = apiRequests.regProcGetRequest(url, valueMap, token, prop);
		List<Object> objectList = responseObj.jsonPath().getList("response.schema");

		IdObjectCreaterHelper helper = new IdObjectCreaterHelper();
		SchemaObjectList objList = helper.parseIdSchemaResponse(objectList);
		// System.out.println(objList.getObjectList().size());
		// System.out.println(objList.getObjectList().toString());
//		Set<String> types = new HashSet<>();
//		for (SchemaObjectElement o : objList.getObjectList()) {
//			types.add(o.getType());
//
//		}
		// System.out.println(gson.toJson(objList.getObjectList()));
		// System.out.println(types.toString());
		String identityJson = "";
		if (prop.SYNC_PREREG) {

			PreRegBasedIdObjectCreater creater = new PreRegBasedIdObjectCreater();
			creater.frameIdentityJson(objList.getObjectList(), prop, session, idSchemaVersion, process, individual_type,
					token);

		} else {
			identityJson = helper.frameIdentityJson(objList.getObjectList(), prop, session, idSchemaVersion, process,
					individual_type);
		}

		System.out.println("generated identityJson:::- " + identityJson);
		return identityJson;
	}

}
