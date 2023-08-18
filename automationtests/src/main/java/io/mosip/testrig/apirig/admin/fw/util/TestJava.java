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

	public static void main(String[] args) {
		String spec = "{\r\n" + "			\"post\": {\r\n" + "				\"tags\": [\r\n"
				+ "					\"resident-otp-controller\"\r\n" + "				],\r\n"
				+ "				\"summary\": \"reqOtp\",\r\n" + "				\"description\": \"reqOtp\",\r\n"
				+ "				\"operationId\": \"reqOtp\",\r\n" + "				\"requestBody\": {\r\n"
				+ "					\"content\": {\r\n" + "						\"application/json\": {\r\n"
				+ "							\"schema\": {\r\n"
				+ "								\"$ref\": \"#/components/schemas/OtpRequestDTO\"\r\n"
				+ "							}\r\n" + "						}\r\n" + "					},\r\n"
				+ "					\"required\": true\r\n" + "				},\r\n"
				+ "				\"responses\": {\r\n" + "					\"201\": {\r\n"
				+ "						\"description\": \"Created\"\r\n" + "					},\r\n"
				+ "					\"401\": {\r\n" + "						\"description\": \"Unauthorized\"\r\n"
				+ "					},\r\n" + "					\"403\": {\r\n"
				+ "						\"description\": \"Forbidden\"\r\n" + "					},\r\n"
				+ "					\"200\": {\r\n" + "						\"description\": \"OK\",\r\n"
				+ "						\"content\": {\r\n" + "							\"*/*\": {\r\n"
				+ "								\"schema\": {\r\n"
				+ "									\"$ref\": \"#/components/schemas/OtpResponseDTO\"\r\n"
				+ "								}\r\n" + "							}\r\n"
				+ "						}\r\n" + "					},\r\n" + "					\"404\": {\r\n"
				+ "						\"description\": \"Not Found\"\r\n" + "					}\r\n"
				+ "				}\r\n" + "			}\r\n" + "		}";

		String response = "{\r\n" + "  \"responseTime\": \"2023-08-16T00:19:16.779Z\",\r\n" + "  \"errors\": [\r\n"
				+ "    {\r\n" + "      \"errorCode\": \"IDA-MLC-018\",\r\n"
				+ "      \"errorMessage\": \"VID not available in database\"\r\n" + "    }\r\n" + "  ]\r\n" + "}";
		
//		OpenApiValidator validator = new OpenApiValidator(); 
//		 
//		 // Set the OpenAPI specification to the OpenApiValidator object
//		 validator.setOpenApiSpecification(openapiSpecification); 
//		 
//		 // Set the JSON object to the OpenApiValidator object
//		 validator.setJson(jsonObject); 
		 
		 // Call the validate() method of the OpenApiValidator object
//		 validator.validate();

	}

}
