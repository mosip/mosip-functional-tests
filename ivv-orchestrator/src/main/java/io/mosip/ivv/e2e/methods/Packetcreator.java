package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import org.json.JSONObject;

import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.e2e.constant.E2EConstants;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.restassured.response.Response;

public class Packetcreator extends BaseTestCaseUtil implements StepInterface {
	
	@Override
	public void run() throws RigInternalError {
		
		String packetPath=null;
		for(String templatePath:residentTemplatePaths.values()) {
			String idJosn=templatePath + "/REGISTRATION_CLIENT/" + E2EConstants.LOST_PROCESS + "/rid_id/" + "ID.json";
			packetPath=createPacket(idJosn,templatePath);
			templatePacketPath.put(templatePath, packetPath);
		}
	}
	
	
	
	private String createPacket(String idJsonPath,String templatePath) {
		String url = baseUrl + props.getProperty("packetCretorUrl");
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("idJsonPath", idJsonPath);
		jsonReq.put("process", E2EConstants.LOST_PROCESS);
		jsonReq.put("source", E2EConstants.SOURCE);
		jsonReq.put("templatePath", templatePath);
		Response response =postReqest(url,jsonReq.toString(),"CreatePacket");
		assertTrue(response.getBody().asString().contains("zip"),"Unable to get packet from packet utility");
		return response.getBody().asString().replaceAll("\\\\", "\\\\\\\\");
		
	}

}
