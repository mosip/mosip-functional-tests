package io.mosip.registrationProcessor.perf.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.perf.preregsync.dto.PreRegArchiveDTO;
import io.mosip.perf.preregsync.dto.SyncOuterResponse;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.restassured.response.Response;

public class PreregistrationSyncService {

	public void loadPreregData(PropertiesUtil prop, String token) throws FileNotFoundException, IOException {

		String preregId = "36042761273149";
		String preregSyncUrl = "/preregistration/v1/sync/" + preregId;
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		HashMap<String, String> valueMap = new HashMap<String, String>();
		Response responseObj = apiRequests.regProcGetRequest(preregSyncUrl, valueMap, token, prop);
		String responseStr = responseObj.getBody().asString();
		// String fileName = responseObj.jsonPath().get("response.zip-filename");
		// byte[] respBytes = responseObj.jsonPath().get("response.zip-bytes");
		System.out.println(responseStr);
		PreRegArchiveDTO syncResponse = new ObjectMapper().readValue(
				new ObjectMapper().writeValueAsString(responseObj.jsonPath().get("response")), PreRegArchiveDTO.class);

		// System.out.println(respBytes);
		// byte[] packetBytes = respBytes.getBytes();
//		InputStream inputstream = new ByteArrayInputStream(packetBytes);
//		String zipFile = "C:/MOSIP_PT/" + fileName + ".zip";
//		try {
//			FileOutputStream fos = new FileOutputStream(zipFile);
//			fos.write(packetBytes);
//			fos.close();
//			inputstream.close();
//		} catch ( IOException e) {
//			e.printStackTrace();
//		}

//		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(packetBytes));
//		ZipEntry entry = null;
//		while ((entry = zipStream.getNextEntry()) != null) {
//			String entryName = fileName;
//			FileOutputStream out = new FileOutputStream(entryName);
//			byte[] byteBuff = new byte[4096];
//			int bytesRead = 0;
//			while ((bytesRead = zipStream.read(byteBuff)) != -1) {
//				out.write(byteBuff, 0, bytesRead);
//			}
//			out.close();
//			zipStream.closeEntry();
//		}
//		zipStream.close();

		String filePath = "E:/MOSIP/" + preregId + ".zip";
		try {
			FileUtils.copyToFile(new ByteArrayInputStream(syncResponse.getZipBytes().getBytes()),
					FileUtils.getFile(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath)));
		} catch (io.mosip.kernel.core.exception.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(filePath);

	}

}
