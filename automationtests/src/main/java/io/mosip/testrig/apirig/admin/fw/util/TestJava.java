package io.mosip.testrig.apirig.admin.fw.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class TestJava {
	private static final Logger lOGGER = Logger.getLogger(TestJava.class);
	private static final SecureRandom secureRandom = new SecureRandom();

	public static void main(String[] args) {
		int num = 1011111;

		JsonToCsvConverter("E:\\scenarios.json");

//		String transactionID = (num + AdminTestUtil.generateRandomNumberString(8)).substring(0, 10);
//		lOGGER.info(transactionID);
//
//		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
//		
//		String message = "Dear FR OTP for VID XXXXXXXX65185914 is 333333 and is valid for 3 minutes. (Generated on 21-03-2023 at 16:03:54 Hrs)\r\n"
//				+ "عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)\r\n"
//				+ "Cher $name_fra, OTP pour VID XXXXXXXX65185914 est 111111 et est valide pour 3 minutes. (Généré le 21-03-2023 à 16:03:54 Hrs)";
//		
//		message = "Dear TEST_FULLNAMEeng OTP for UIN XXXXXXXX98 is 523478 and is valid for 3 minutes. (Generated on 22-05-2023 at 11:40:11 Hrs) عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs) UIN XXXXXXXX98 ಗಾಗಿ ಆತ್ಮೀಯ TEST_FULLNAMEkan OTP 523478 ಆಗಿದೆ ಮತ್ತು ಇದು 3 ನಿಮಿಷಗಳವರೆಗೆ ಮಾನ್ಯವಾಗಿರುತ್ತದೆ. (22-05-2023 ದಂದು 11:40:11 ಗಂಟೆಗೆ ರಚಿಸಲಾಗಿದೆ)";
//		
//		if(message!=null) {
//		    Matcher mMatcher = mPattern.matcher(message);
//		    if(mMatcher.find()) {
//		        String otp = mMatcher.group(0);
//		        otp = otp.trim();
//		        lOGGER.info("Final OTP: "+ otp);
//		    }else {
//		    	lOGGER.info("Failed to extract the OTP!! ");
//		    }
//		}
	}

	public static String JsonToCsvConverter(String jsonFilePath) {
		String tempCSVPath = "E:\\scenarios.csv";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

			FileWriter fileWriter = new FileWriter(tempCSVPath);
			String[] header = { "tc_no", "tags", "persona_class", "persona", "group_name", "description", "step0",
					"step1", "step2", "step3", "step4", "step5", "step6", "step7", "step8", "step9", "step10", "step11",
					"step12", "step13", "step14", "step15", "step16", "step17", "step18", "step19", "step20", "step21",
					"step22", "step23", "step24", "step25", "step26", "step27", "step28", "step29", "step30", "step31",
					"step32", "step33", "step34", "step35", "step36", "step37", "step38", "step39", "step40", "step41",
					"step42", "step43", "step44", "step45", "step46", "step47", "step48", "step49", "step50" };
			for (String string : header) {
				fileWriter.write(string + ",");
			}
			fileWriter.write("\r\n");
			for (JsonNode jsonNode : rootNode) {
				String[] csvLine = { jsonNode.get("tc_no").asText(), jsonNode.get("tags").asText(),
						jsonNode.get("persona_class").asText(), jsonNode.get("persona").asText(),
						jsonNode.get("group_name").asText(), jsonNode.get("description").asText(),
						jsonNode.get("step0") == null ? "" : "\"" + jsonNode.get("step0").asText() + "\"",
						jsonNode.get("step1") == null ? "" : "\"" + jsonNode.get("step1").asText() + "\"",
						jsonNode.get("step2") == null ? "" : "\"" + jsonNode.get("step2").asText() + "\"",
						// ... continue for other fields ...
						jsonNode.get("step49") == null ? "" : "\"" + jsonNode.get("step49").asText() + "\"",
						jsonNode.get("step50") == null ? "" : "\"" + jsonNode.get("step50").asText() + "\"" };
				for (String string : csvLine) {
					fileWriter.write(string + ",");
				}
				fileWriter.write("\r\n");
			}
			fileWriter.close();
		} catch (Exception e) {

		}
		return tempCSVPath;
	}

	public static String generateRandomAlphanumericString(int length) {
		byte[] bytes = new byte[length];
		secureRandom.nextBytes(bytes);
		String randomString = new String(bytes);
		return randomString.replaceAll("[^0-9a-zA-Z]", "");
	}

	public static String generateRandomString(int length) {
		byte[] bytes = new byte[length];
		secureRandom.nextBytes(bytes);
		String randomString = new String(bytes);
		return randomString;
	}

	public static String generateRandomNumericString(int length) {
		byte[] bytes = new byte[length];
		secureRandom.nextBytes(bytes);
		String randomString = new String(bytes);
		return randomString.replaceAll("[^0-9]", "");
	}

}
