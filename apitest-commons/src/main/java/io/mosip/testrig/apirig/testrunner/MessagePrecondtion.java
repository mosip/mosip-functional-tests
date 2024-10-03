package io.mosip.testrig.apirig.testrunner;

import java.util.Map;

public abstract class MessagePrecondtion {
	
	public abstract Map<String, String> parseAndWriteFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName);
	
	public abstract String parseAndUpdateJson(String inputJson, Map<String, String> fieldvalue, String propFileName);

	public static MessagePrecondtion getPrecondtionObject(String filePath) {
		MessagePrecondtion msgPrecon = null;
		if (filePath.endsWith(".json"))
			msgPrecon = new JsonPrecondtion();
		else if (filePath.endsWith(".xml"))
			msgPrecon = new XmlPrecondtion();
		return msgPrecon;
	}

	public Map<String, String> retrieveMappingAndItsValueToPerformJsonOutputValidation(String json) {
		return null;
	}
}
