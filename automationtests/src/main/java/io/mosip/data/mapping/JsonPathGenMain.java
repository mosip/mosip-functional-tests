package io.mosip.data.mapping;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;;


public class JsonPathGenMain {
	static JsonPathGenMain obj = new JsonPathGenMain();
	String parentpath="";
	public static void main(String arg[]) throws IOException
	{
		//String inputFilePath="D:\\MosipTestJavaWokspace\\IDAAutomation\\Resources\\masterdataIDA.json";
//		String inputFilePath="D:\\MOSIP\\test_DEV\\v4\\mosip-test\\automation\\mosip-qa\\src\\test\\resources\\preReg\\TestData\\Create_PreRegistration\\input\\request.json";
		String inputFilePath="/home/mudita/collegeWork/Mosip-1.1/mosip-functional-tests/automationtests/src/main/resources/kernel/TestData/FetchGenderType/output/output-1-expected-pos-response.json";

		//obj.generateJSONPath(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		JsonPathGen o = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
		o.generateJsonMappingDic("/home/mudita/collegeWork/Mosip-1.1/mosip-functional-tests/automationtests/src/main/resources/kernel/TestData/FetchGenderType/map.properties");
		//logger.info(o.getPathList());
	}
}
