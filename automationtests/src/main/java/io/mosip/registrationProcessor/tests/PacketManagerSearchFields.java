package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.service.PacketUtil;
import io.mosip.registrationProcessor.util.HealthCheckUtil;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

public class PacketManagerSearchFields extends BaseTestCase implements ITest {

	private static Logger logger = Logger.getLogger(PacketManagerSearchFields.class);
	protected static String testCaseName = "";
	static String moduleName = "RegProc";
	String token = "";
	Properties prop = new Properties();
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	JSONObject expectedResponse = null;
	static String folderPath = "regProc/PacketManagerSearchFields";
	static String outputFile = "PacketManagerSearchFieldsOutput.json";
	static String requestKeyFile = "PacketManagerSearchFieldsRequest.json";
	String validToken = "";
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	String new_packet_path = "regProc/existingPacket/temp";
	
	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	/**
	 * This method is used for generating token
	 * 
	 * @param tokenType
	 * @return token
	 */
	public void getTokenForUser(String tokenType) {
		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		validToken = generateToken.getToken(tokenEntity);
	}

	public void getTokenForClientIdSecretKey() {
		Properties prop = new Properties();
		prop = generateToken.readPropertyFileToObtainProperty("config/tokenGeneration.properties");
		validToken = generateToken.getTokenForClientIdSecretKey(prop);
	}

	/**
	 * This method is used for fetching test case name
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		JSONObject object = (JSONObject) testdata[2];
		String apiName = "packetManagerSearchFields";
		testCaseName = moduleName + "_" + apiName + "_" + object.get("testCaseName").toString();
	}

	@BeforeClass
	public void healthCheck() throws Exception {
		String parentDir = apiRequests.getResourcePath();
		String propertyFilePath = apiRequests.getResourcePath() + "config/registrationProcessorAPI.properties";
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(new File(propertyFilePath)));
			HealthCheckUtil healthCheckUtil = new HealthCheckUtil();
			Boolean status = healthCheckUtil.healthCheck(properties.getProperty("packetManagerSearchFieldsApi"));
			if (status) {
				Assert.assertTrue(true);
				PacketUtil packetUtil = new PacketUtil();
				String existing_packet_path = parentDir + new_packet_path;
				String searchField_smoke = parentDir + folderPath + File.separator + "Valid_smoke";
				packetUtil.editPacketManagerRequestResponse(existing_packet_path, searchField_smoke);
				String searchField_smoke_cacheFalse = parentDir + folderPath + File.separator
						+ "Valid_smoke_byPassCacheFalse";
				packetUtil.editPacketManagerRequestResponse(existing_packet_path, searchField_smoke_cacheFalse);
			} else {
				throw new Exception("Health Check Failed For The Api");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "packetManagerSearchFields")
	public Object[][] readData(ITestContext context) {
		String propertyFilePath = apiRequests.getResourcePath() + "config/registrationProcessorAPI.properties";
		testLevel = System.getProperty("env.testLevel");
		Object[][] readFolder = null;
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
			switch (testLevel) {
			case "smoke":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
				break;
			case "regression":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
				break;
			default:
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
			}
		} catch (IOException | ParseException | IllegalArgumentException | NullPointerException e) {
			e.printStackTrace();
			Assert.assertTrue(false,
					"not able to read the folder in PacketReceiver class in readData method: " + e.getCause());
		}
		return readFolder;
	}

	@Test(dataProvider = "packetManagerSearchFields")
	public void packetManagerSearchFields(String testSuite, Integer i, JSONObject object) {
		String testcase = (String) object.get("testCaseName");
		String searchFieldsApi = prop.getProperty("packetManagerSearchFieldsApi");

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		String configPath = apiRequests.getResourcePath() + testSuite + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		JSONObject objectData = new JSONObject();
		boolean status = false;
		JSONObject actualRequest = null;
		try {
			actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		if (testcase.contains("forbidden")) {
			getTokenForUser("syncTokenGenerationFilePath");
			boolean tokenStatus = apiRequests.validateToken(validToken);
			while (!tokenStatus) {
				getTokenForUser("getStatusTokenGenerationFilePath");
				tokenStatus = apiRequests.validateToken(validToken);
			}
		} else {
			getTokenForClientIdSecretKey();
			boolean tokenStatus = apiRequests.validateToken(validToken);
			while (!tokenStatus) {
				getTokenForClientIdSecretKey();
				tokenStatus = apiRequests.validateToken(validToken);
			}
		}

		try {
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);
		} catch (IOException | ParseException e1) {
			e1.printStackTrace();
		}
		Response actualResponse = apiRequests.postRequestWithRequestResponseHeaders(searchFieldsApi, actualRequest,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, validToken);
		System.out.println("actualResponse.asString() :");
		System.out.println(actualResponse.asString());
		if (testcase.contains("smoke")) {
			Object responseFields = actualResponse.jsonPath().get("response.fields");
			Assert.assertNotNull(responseFields);

		} else {

			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			// innerKeys.add("actionTimeStamp");
			// innerKeys.add("updatedDateTime");
			try {
				status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Assert.assertTrue(status, "object are not equal");

		}
	}

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, PacketManagerSearchFields.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
			Reporter.log("Exception : " + e.getMessage());
		}
	}

}
