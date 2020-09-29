package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

public class PacketUtil {

	public PacketUtil() {

	}

	public void copyGeneratedPacketToSyncSmoke(String packet_gen_path, String sync_smoke_path) {

		File syncSmokeFile = new File(sync_smoke_path);

		for (File f : syncSmokeFile.listFiles()) {
			if (f.getName().endsWith(".zip")) {
				FileUtils.deleteQuietly(f);
			}
		}

		String file_name = new File(packet_gen_path).getName();
		String reg_id = file_name.substring(0, file_name.indexOf(".zip"));
		System.out.println("Reg id extracted is " + reg_id);
		File destFile = new File(sync_smoke_path + File.separator + file_name);

		try {
			FileUtils.copyFile(new File(packet_gen_path), destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Packet copied at -- " + destFile);

		/*
		 * Modify Response.json file to update regid
		 */
		String jsonfile = sync_smoke_path + File.separator + "Response.json";

		JSONUtil jsonUtil = new JSONUtil();
		JSONObject jsonObject = jsonUtil.loadJsonFromFile(jsonfile);
		Object responseObj = jsonObject.get("response");
		java.util.ArrayList response = (java.util.ArrayList) responseObj;
		Map actResp = (Map) response.get(0);
		actResp.put("registrationId", reg_id);
		jsonObject.put("response", response);
		System.out.println(jsonObject.toString());
		jsonUtil.writeJsonToFile(jsonObject.toString(), jsonfile);
	}

	@SuppressWarnings("unchecked")
	public void copyGeneratedPacketToPacketReceiverSmoke(String existing_packet_path, String pktReceiver_smoke) {

		File pktReceiverSmokeFile = new File(pktReceiver_smoke);

		for (File f : pktReceiverSmokeFile.listFiles()) {
			if (f.getName().endsWith(".zip")) {
				FileUtils.deleteQuietly(f);
			}
		}
		String file_name = "";
		for (File f : new File(existing_packet_path).listFiles()) {
			if (f.getName().endsWith(".zip")) {
				file_name = f.getName();
			}
		}
		
		String srcFile = existing_packet_path + File.separator + file_name;
		String destFile = pktReceiver_smoke + File.separator + file_name;
		try {
			FileUtils.copyFile(new File(srcFile), new File(destFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reg_id = file_name.substring(0, file_name.indexOf(".zip"));

		String reqJsonfile = pktReceiverSmokeFile + File.separator + "Request.json";

		JSONUtil jsonUtil = new JSONUtil();
		JSONObject jsonObject = jsonUtil.loadJsonFromFile(reqJsonfile);
		jsonObject.put("path", file_name);
		jsonUtil.writeJsonToFile(jsonObject.toString(), reqJsonfile);

	}

}
