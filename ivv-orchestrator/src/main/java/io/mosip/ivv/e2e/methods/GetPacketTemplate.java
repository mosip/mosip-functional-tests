package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;

public class GetPacketTemplate extends BaseTestCaseUtil implements StepInterface {
	Logger logger = Logger.getLogger(GetPacketTemplate.class);

	@Override
	public void run() {
		String process = null;
		if (step.getParameters() == null || step.getParameters().isEmpty()) {
			logger.error("Argument is missing in DSL steps: " + step.getName());
		} else {
			process = step.getParameters().get(0);
		}
		
		JSONArray resp = packetUtility.getTemplate(residentTemplatePaths.keySet(), process);

		for (int i = 0; i < resp.length(); i++) {
			JSONObject obj = resp.getJSONObject(i);
			String id = obj.get("id").toString();
			String tempFilePath = obj.get("path").toString();
			for (String residentPath : residentTemplatePaths.keySet()) {
				if (residentPath.contains(id)) {
					residentTemplatePaths.put(residentPath, tempFilePath);
					break;
				}
			}
			
		}
		for (String residentPath : residentTemplatePaths.keySet()) {
			assertTrue(residentTemplatePaths.get(residentPath)!=null,"Unable to get packetTemplate from packet utility");
		}
		System.out.println("RESIDENTTEMPLATEPATHS: " + residentTemplatePaths);
	}

}
