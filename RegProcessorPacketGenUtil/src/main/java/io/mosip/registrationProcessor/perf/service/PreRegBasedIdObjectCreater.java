/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectElement;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

/**
 * @author M1030608
 *
 */
public class PreRegBasedIdObjectCreater {

	public String frameIdentityJson(List<SchemaObjectElement> objectList, PropertiesUtil prop, Session session,
			String idSchemaVersion, String process, String individual_type, String token) {
		String idJson = "";
		PreregistrationSyncHelper preregSyncHelper = new PreregistrationSyncHelper();
		try {
			preregSyncHelper.downloadPreregPacket(prop, token);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean unzipped = false;
		try {
			unzipped = preregSyncHelper.unzipPreregPacket(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (unzipped) {
			List<String> identityKeys = preregSyncHelper.obtainIdentityKeys(objectList);
			Map<String, SchemaObjectElement> idSchemaMap = preregSyncHelper.obtainIdentityMap(objectList);
			JSONObject idJsonObject = preregSyncHelper.parsePreregIdentityJson(identityKeys, idSchemaMap, prop,
					session);

			preregSyncHelper.addDocumentsToIdJson(idJsonObject, prop);
		}

		return idJson;
	}

}
