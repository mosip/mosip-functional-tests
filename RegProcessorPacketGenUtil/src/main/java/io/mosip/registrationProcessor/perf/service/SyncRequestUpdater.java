/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.util.List;

import org.apache.commons.codec.binary.Base64;

import io.mosip.registrationProcessor.perf.util.FileUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

/**
 * @author Gaurav Sharan
 *
 */
public class SyncRequestUpdater {
	
	private FileUtil fileUtil;
	
	public SyncRequestUpdater() {
		fileUtil = new FileUtil();
	}

	public void decodeSyncRequestFromFile(String filePath) {

		List<String> lines = fileUtil.readLinesFromFile(filePath);
		for (String line : lines) {
			String[] tokens = line.split(",");
			String reg_id = tokens[0];
			String data = tokens[0];
			String packet_path = tokens[0];
			String ref_id = tokens[0];

			data = processData(data);
			break;
		}
	}

	private String processData(String data) {
		String modifieddata = "";
		byte[] base64Data = data.getBytes();
		//byte[] byteArr = Base64.decodeBase64(base64Data);
		 byte[] byteArr = Base64.decodeBase64(data);
		String strJson = new String(byteArr);
		System.out.println(strJson);
		return modifieddata;
	}

}
