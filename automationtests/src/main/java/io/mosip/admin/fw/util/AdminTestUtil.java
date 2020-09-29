package io.mosip.admin.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Reporter;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.response.Response;


/**
 * @author Ravi Kant
 *
 */
public class AdminTestUtil extends BaseTestCase{
	
	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	static KernelDataBaseAccess masterDB = new KernelDataBaseAccess();
	String token = null;
	
	public static String getGlobalResourcePath() {
		return MosipTestRunner.getGlobalResourcePath();
	}
	
	public static void initiateAdminTest() {
		copyAdminTestResource();
	}
	
	public static void copyAdminTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/admin");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			logger.info("Copied the admin test resource successfully");
		} catch (Exception e) {
			logger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	/**
	 * method for creating test data in master DB for admin search and filter apis
	 */
	public static void createMasterDataForAdminFilterSearchApis()
	{
		String crtQuerKeys[] = queries.get("allAutoCrt").toString().split(",");
		List<String> crtQueries = new LinkedList<String>();
		for(String queryKeys: crtQuerKeys)
			crtQueries.add(queries.get(queryKeys).toString());
		if (masterDB.executeQueries(crtQueries, "masterdata"))
			logger.info("created test data for admin search and filter apis");
		else
			logger.info("not able to create test data for admin search and filter apis, Search and Filter APIs will Fail");
	}
	
	/**
	 * method for deleting created test data in master DB for admin search and filter apis
	 */
	public static void deleteMasterDataForAdminFilterSearchApis()
	{
		String dltQueryKeys[] = queries.get("allAutoDlt").toString().split(",");
		List<String> dltQueries = new LinkedList<String>();
		for(String queryKeys: dltQueryKeys)
			dltQueries.add(queries.get(queryKeys).toString());
		if (masterDB.executeQueries(dltQueries, "masterdata"))
			logger.info("deleted created test data for admin search and filter apis");
		else
			logger.info("not able to delete test data of admin search and filter apis");
	}
	
	public static void createDeviceManagementData()
	{
		String crtQuerKeys[] = queries.get("crtDeviceMngmntdata").toString().split(",");
		List<String> crtQueries = new LinkedList<String>();
		for(String queryKeys: crtQuerKeys)
			crtQueries.add(queries.get(queryKeys).toString());
		if (masterDB.executeQueries(crtQueries, "masterdata"))
			logger.info("created device management data for automation");
		else
			logger.info("not able to create device management data, IDA authentications will fail");
	}
	public static void deleteDeviceManagementData()
	{
		String dltQueryKeys[] = queries.get("dltDeviceMngmntdata").toString().split(",");
		List<String> dltQueries = new LinkedList<String>();
		for(String queryKeys: dltQueryKeys)
			dltQueries.add(queries.get(queryKeys).toString());
		if (masterDB.executeQueries(dltQueries, "masterdata"))
			logger.info("deleted created device management data for automation");
		else
			logger.info("not able to delete device management data");
		}
	
	public Object[] getYmlTestData(String ymlPath){
		String testType = testLevel;
		final ObjectMapper mapper = new ObjectMapper();
		List<TestCaseDTO> testCaseDTOList = new LinkedList<TestCaseDTO>();
		Map<String, Map<String, Map<String, String>>> scriptsMap = loadyaml(ymlPath);
		for (String key : scriptsMap.keySet()) {
			Map<String, Map<String, String>> testCases = scriptsMap.get(key);
			if(testType.equalsIgnoreCase("smoke")){
				testCases = testCases.entrySet().stream().filter(mapElement -> mapElement.getKey().toLowerCase().contains("smoke")).collect(Collectors.toMap(mapElement -> mapElement.getKey(), mapElement -> mapElement.getValue()));
			}
			for (String testCase : testCases.keySet()) {
				TestCaseDTO testCaseDTO = mapper.convertValue(testCases.get(testCase), TestCaseDTO.class);
						testCaseDTO.setTestCaseName(testCase);
						testCaseDTOList.add(testCaseDTO);
			}
		}
		return testCaseDTOList.toArray();
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Map<String, Map<String, String>>> loadyaml(String path) {
		Map<String,Map<String, Map<String, String>>> scriptsMap = null;
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
		scriptsMap = (Map<String,Map<String, Map<String, String>>>) yaml.load(inputStream);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return scriptsMap;
	}	
	
	/**
	 * This method will hit post request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response postWithBodyAndCookie(String url, String jsonInput, String cookieName, String role) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Post request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	/**
	 * This method will hit put request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response putWithBodyAndCookie(String url, String jsonInput, String cookieName, String role) {
		Response response=null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Put request Json to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson) + "</pre>");
		try {
			  response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	/**
	 * This method will hit get request and return the response
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */
	protected Response getWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role) {
		Response response=null;
		JSONObject inputJson=null;
		try {
			inputJson = (JSONObject) new JSONParser().parse(jsonInput);
		} catch (ParseException e) {
			logger.error("Error while parsing json request: "+jsonInput+"\n with message: "+e.getMessage());
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******get request to EndPointUrl: " + url + " *******");
		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(inputJson.toJSONString()) + "</pre>");
		try {
			  response = RestClient.getRequestWithCookieAndPathParm(url, inputJson, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			  Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + url + ") <pre>"
						+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
			return response;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return response;
		}
	}
	
	protected String getJsonFromTemplate(String input, String template)
	{
		String resultJson = null;
		
		try {
			Handlebars handlebars = new Handlebars();
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>(){}.getType();
			Map<String, Object> map = gson.fromJson(input, type);   
			Template compiledTemplate = handlebars.compile(template);
			Context context = Context.newBuilder(map).build();
			resultJson = compiledTemplate.apply(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	String inputJsonKeyWordHandeler(String jsonString)
	{
		if(jsonString==null)
		{
			logger.info(" Request Json String is :"+jsonString);
			return jsonString;
		}
		if(jsonString.contains("$TIMESTAMP$"))
			jsonString = jsonString.replace("$TIMESTAMP$", generateCurrentUTCTimeStamp());
		else if(jsonString.contains("$TIMESTAMPL$"))
			jsonString = jsonString.replace("$TIMESTAMPL$", generateCurrentLocalTimeStamp());
		return jsonString;
	}
	
	private String generateCurrentUTCTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
	private String generateCurrentLocalTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return dateFormat.format(date);
	}
}
