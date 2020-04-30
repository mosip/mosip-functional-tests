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
		String inputFilePath = "D:\\Mosip_git\\mosip-functional-tests-mt\\1.0.4_Vignesh_Test\\automationtests\\src\\main\\resources\\resident\\TestData\\AuthLock\\input\\auth-lock-request.json";
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic(
				"D:\\Mosip_git\\mosip-functional-tests-mt\\1.0.4_Vignesh_Test\\automationtests\\src\\main\\resources\\resident\\TestData\\AuthLock\\input\\auth-lock-request.properties");
	}
}
