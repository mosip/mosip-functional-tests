package io.mosip.authentication.testdata.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The class to generate json mapping in property file
 * 
 * @author Vignesh
 *
 */

public class JsonPathGenMain {

	public static void main(String arg[]) throws IOException {
		String inputFilePath = "D:\\Mosip_git\\mosip-functional-tests-mt\\0.9.1_IDA_V2\\automationtests\\src\\main\\resources\\ida\\TestData\\InternalAuth\\LockUIN\\output\\output-1-expected-error-response.json";
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic(
				"D:\\Mosip_git\\mosip-functional-tests-mt\\0.9.1_IDA_V2\\automationtests\\src\\main\\resources\\ida\\TestData\\InternalAuth\\LockUIN\\output\\output-1-expected-error-response.properties");
	}
}
