package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.e2e.constant.E2EConstants;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.restassured.response.Response;

public class Ridsync extends BaseTestCaseUtil implements StepInterface {
	Logger logger = Logger.getLogger(Wait.class);
	
	@Override
	public void run() throws RigInternalError {
		String process=null;
		if (step.getParameters() == null || step.getParameters().isEmpty()) {
			logger.error("Parameter is  missing from DSL step");
			assertTrue(false,"process paramter is  missing in step: "+step.getName());
		} else {
			process =step.getParameters().get(0);
		}
		pridsAndRids.clear();
		String registrationId=null;
		for (String packetPath : templatePacketPath.values()) {
			registrationId=ridsync(packetPath, E2EConstants.APPROVED_SUPERVISOR_STATUS,process);
			pridsAndRids.put(packetPath, registrationId);
		}
	}

	private String ridsync(String containerPath, String supervisorStatus,String process) {
		String url = baseUrl + props.getProperty("ridsyncUrl");
		JSONObject jsonReq = buildRequest(containerPath, supervisorStatus,process);
		Response response = postReqest(url, jsonReq.toString(), "Ridsync");
		
		JSONArray jsonArray = new JSONArray(response.asString());
		JSONObject responseJson = new JSONObject(jsonArray.get(0).toString());
		assertTrue(response.getBody().asString().contains("SUCCESS"),"Unable to do RID sync from packet utility");
		return responseJson.get("registrationId").toString();

	}

	private JSONObject buildRequest(String containerPath, String supervisorStatus,String process) {
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("containerPath", containerPath);
		jsonReq.put("email", "email");
		jsonReq.put("name", "name");
		jsonReq.put("phone", "phone");
		jsonReq.put("process", process);
		jsonReq.put("supervisorComment", "supervisorComment");
		jsonReq.put("supervisorStatus", supervisorStatus);
		return jsonReq;
	}

}
