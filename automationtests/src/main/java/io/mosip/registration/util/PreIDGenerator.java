/*package io.mosip.registration.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.mosip.test.GetPreRegistartion;

public class PreIDGenerator {
	public void generatePreID(String centerid) {
		new GetPreRegistartion().generatePID(centerid);

		JSONObject jsonFromPreReg = null;
		try {
			jsonFromPreReg = (JSONObject) readJsonFile("PreRegIds.json");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonToStub = null;
		try {
			jsonToStub = (JSONObject) readJsonFile(
					"./src/main/resources/testData/PacketHandlerServiceData/PreRegIds.json");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(jsonFromPreReg);
		System.out.println(jsonFromPreReg.get("childPreidWithDocs1"));
		jsonToStub.put("PrIdOfAdultWithoutDocs", jsonFromPreReg.get("PrIdOfAdultWithoutDocs"));
		jsonToStub.put("PrIdOfChildWithoutDocs", jsonFromPreReg.get("PrIdOfChildWithoutDocs"));

		jsonToStub.put("PrIdOfChildInvalidRID", jsonFromPreReg.get("childPreidWithDocs1"));
		jsonToStub.put("PrIdOfChildInvalidParentUIN", jsonFromPreReg.get("childPreidWithDocs2"));
		jsonToStub.put("PrIdOfChildValidParentRID", jsonFromPreReg.get("childPreidWithDocs3"));
		jsonToStub.put("PrIdOfChildValidParentUIN", jsonFromPreReg.get("childPreidWithDocs4"));

		jsonToStub.put("PrIdOfAdultWithDocs", jsonFromPreReg.get("PrIdOfAdultWithDocs1"));
		jsonToStub.put("PrIdOfAdultFingerPrintException", jsonFromPreReg.get("PrIdOfAdultWithDocs2"));
		jsonToStub.put("PrIdOfAdultIrisException", jsonFromPreReg.get("PrIdOfAdultWithDocs5"));
		jsonToStub.put("PrIdOfAdultDuplicateDemo", jsonFromPreReg.get("PrIdOfAdultWithDocs6"));
		jsonToStub.put("PrIdOfAdultDuplicateBio", jsonFromPreReg.get("PrIdOfAdultWithDocs3"));
		jsonToStub.put("PrIdOfAdultUnique", jsonFromPreReg.get("PrIdOfAdultWithDocs4"));
	
		FileWriter file = null;
		try {
			file = new FileWriter("./src/main/resources/testData/PacketHandlerServiceData/PreRegIds.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			file.write(jsonToStub.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			file.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object readJsonFile(String filename) throws Exception {
		FileReader reader = new FileReader(filename);
		JSONParser jsonParser = new JSONParser();
		return jsonParser.parse(reader);
	}

}
*/