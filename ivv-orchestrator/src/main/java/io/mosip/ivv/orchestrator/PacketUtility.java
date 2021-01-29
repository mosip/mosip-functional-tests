package io.mosip.ivv.orchestrator;

import static org.testng.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.ivv.e2e.methods.CheckStatus;
import io.restassured.response.Response;

public class PacketUtility extends BaseTestCaseUtil {
	
	public List<String> generateResidents(int n, Boolean bAdult, Boolean bSkipGuardian, String gender) {
		String url = baseUrl + props.getProperty("getResidentUrl") + n;
		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		if (bAdult) {
			residentAttrib.put("Age", "RA_Adult");
		} else {
			residentAttrib.put("Age", "RA_Minor");
			residentAttrib.put("SkipGaurdian", bSkipGuardian);
		}
		residentAttrib.put("Gender", gender);
		residentAttrib.put("PrimaryLanguage", "eng");
		jsonReq.put("PR_ResidentAttribute", residentAttrib);
		jsonwrapper.put("requests", jsonReq);

		Response response = postReqest(url, jsonwrapper.toString(), "GENERATE_RESIDENTS_DATA");
		assertTrue(response.getBody().asString().contains("SUCCESS"),"Unable to get residentData from packet utility");
		JSONArray resp = new JSONObject(response.getBody().asString()).getJSONArray("response");
		List<String> residentPaths= new ArrayList<>();
		for (int i = 0; i < resp.length(); i++) {
			JSONObject obj = resp.getJSONObject(i);
			String resFilePath = obj.get("path").toString();
			residentPaths.add(resFilePath);
			//residentTemplatePaths.put(resFilePath, null);
		}
		return residentPaths;

	}
	
	public JSONArray getTemplate(Set<String> resPath,String process) {
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		for (String residentPath : resPath) {

			arr.put(residentPath);
		}
		jsonReq.put("personaFilePath", arr);
		String url = baseUrl + props.getProperty("getTemplateUrl") + process + "/ /";
		Response templateResponse = postReqest(url, jsonReq.toString(), "GET-TEMPLATE");
		JSONObject jsonResponse = new JSONObject(templateResponse.asString());
		JSONArray resp = jsonResponse.getJSONArray("packets");
		return resp;
	}
	
	public  void requestOtp(String resFilePath){
		String url = baseUrl+props.getProperty("sendOtpUrl");
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		postReqest(url,jsonReq.toString(),"Send Otp");

	}
	
	public  void verifyOtp(String resFilePath){
		String url = baseUrl+props.getProperty("verifyOtpUrl");
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		Response response =postReqest(url,jsonReq.toString(),"Verify Otp");
		//assertTrue(response.getBody().asString().contains("VALIDATION_SUCCESSFUL"),"Unable to Verify Otp from packet utility");

	}
	
	public  String preReg(String resFilePath){
		String url = baseUrl+props.getProperty("preregisterUrl");
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		Response response =postReqest(url,jsonReq.toString(),"AddApplication");
		String prid = response.getBody().asString();
		assertTrue((int)prid.charAt(0)>47 && (int)prid.charAt(0)<58 ,"Unable to pre-register from packet utility");
		return prid;

	}
	public  void uploadDocuments(String resFilePath, String prid){
		String url = baseUrl + "/documents/"+ prid;
		JSONObject jsonReq = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(resFilePath);
		jsonReq.put("personaFilePath", jsonArray);
		postReqest(url,jsonReq.toString(),"Upload Documents");
	}
	
	public  void bookAppointment(String prid, int nthSlot){
		String url = baseUrl + "/bookappointment/"+ prid + "/" + nthSlot;
		JSONObject jsonReq = new JSONObject();
		Response response =postReqest(url,jsonReq.toString(),"BookAppointment");
		assertTrue(response.getBody().asString().contains("Appointment booked successfully") ,"Unable to BookAppointment from packet utility");
	}
	
	public  String generateAndUploadPacket(String prid, String packetPath){
		String url = baseUrl + "/packet/sync/"+ prid ;
		JSONObject jsonReq = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put(packetPath);	
		jsonReq.put("personaFilePath",arr);
		Response response =postReqest(url,jsonReq.toString(),"Generate And UploadPacket");
		JSONObject jsonResp = new JSONObject(response.getBody().asString());
		String rid = jsonResp.getJSONObject("response").getString("registrationId");
		assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to Generate And UploadPacket from packet utility");
		return rid;
	}
	
	public  String updateResidentGuardian(String residentFilePath) {
		Reporter.log("<b><u>Execution Steps for Generating GuardianPacket And linking with Child Resident: </u></b>");
		List<String> generatedResidentData = generateResidents(1, true,true,"Any");
		JSONArray jsonArray=getTemplate(new HashSet<String>(generatedResidentData), "NEW");
		JSONObject obj = jsonArray.getJSONObject(0);
		String templatePath = obj.get("path").toString();
		requestOtp(generatedResidentData.get(0));
		verifyOtp(generatedResidentData.get(0));
		String prid=preReg(generatedResidentData.get(0));
		uploadDocuments(generatedResidentData.get(0), prid);
		bookAppointment(prid, 1);
		String rid=generateAndUploadPacket(prid, templatePath);
		// call Dsl step wait her sleep for 2 minute
		String url = baseUrl+props.getProperty("updateResidentUrl")+"?RID="+rid;
		JSONObject jsonwrapper = new JSONObject();
		JSONObject jsonReq = new JSONObject();
		JSONObject residentAttrib = new JSONObject();
		residentAttrib.put("guardian", generatedResidentData.get(0));
		residentAttrib.put("child", residentFilePath);
		jsonReq.put("PR_ResidentList", residentAttrib);
		jsonwrapper.put("requests", jsonReq);
		Response response =postReqest(url,jsonwrapper.toString(),"Update Resident Guardian");
		assertTrue(response.getBody().asString().contains("SUCCESS") ,"Unable to update Resident Guardian from packet utility");
		Reporter.log("<b><u>Generated GuardianPacket with Rid: "+rid+" And linked to child </u></b>");
		return rid;
		
	}
	
	
}
