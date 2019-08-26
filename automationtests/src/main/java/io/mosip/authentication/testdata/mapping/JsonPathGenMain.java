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
		String inputFilePath = "D:\\AdminTess\\mosip-functional-tests-mt\\automationtests\\src\\main\\resources\\admin\\TestData\\UnmapDocCategoryType\\output\\output-1-expected-pos-response.json";
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic(
				"D:\\AdminTess\\mosip-functional-tests-mt\\automationtests\\src\\main\\resources\\admin\\TestData\\UnmapDocCategoryType\\map.properties");
	}
}
