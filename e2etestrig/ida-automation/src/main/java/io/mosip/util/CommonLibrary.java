package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class CommonLibrary extends BaseTestCase {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);

	

	public static void configFileWriter(String folderPath, String requestKeyFile, String generationType,
			String baseFileName) throws Exception {
		String splitRegex = Pattern.quote(System.getProperty("file.separator"));
		String string[] = new String[2];
		string = folderPath.split(splitRegex);
		String api = string[0];
		String testSuite = string[1];

		String requestFilePath = "src/test/resources/" + folderPath + "/" + requestKeyFile;
		String configFilePath = "src/test/resources/" + folderPath + "/" + "FieldConfig.properties";

		JSONObject requestKeys = (JSONObject) new JSONParser().parse(new FileReader(requestFilePath));
		Properties properties = new Properties();
		Properties cloneProperties = new Properties();
		properties.load(new FileReader(new File(configFilePath)));
		cloneProperties.load(new FileReader(new File(configFilePath)));
		Set<String> keys = properties.stringPropertyNames();

		try {
			for (Object key : requestKeys.keySet()) {

				if (properties.getProperty(key.toString()) != null) {
					properties.setProperty(key.toString(), "invalid");
					properties.setProperty("filename", "invalid_" + key.toString());
					File file = new File(configFilePath);
					FileOutputStream fileOut = new FileOutputStream(file);
					properties.store(fileOut, "FieldConfig.properties");
					/*
					 * try { new Main().TestRequestReponseGenerator(api,
					 * testSuite,generationType); }catch(org.json.JSONException
					 * exp) { exp.printStackTrace(); }
					 */
					properties.remove(key.toString());
					properties.setProperty(key.toString(), "valid");
					properties.remove("filename");
					properties.setProperty("filename", baseFileName);
					properties.store(fileOut, "FieldConfig.properties");
					fileOut.close();
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		cloneProperties.remove("prereg_id_custom");
		cloneProperties.setProperty("prereg_id_custom", "");
		properties.clear();
		File file = new File(configFilePath);
		FileOutputStream fileOut = new FileOutputStream(file);
		properties.store(fileOut, null);
		cloneProperties.store(fileOut, null);
	}

	
	public Response post_Request(String url, Object body, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();
		return postResponse;
	}
	public Response postRequestWithParm(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> pathValue) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().pathParams(pathValue).post(url).then().log().all()
				.extract().response();
		return postResponse;
	}

	public Response post_RequestWithoutBody(String url, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		return postResponse;
	}// end POST_REQUEST

	

	public Response authPost_Request(String url, Object body, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();
		return postResponse;
	}

	/**
	 * @author Arjun this method is specifically for email notification
	 * @param jsonString
	 * @param serviceUri
	 * @return
	 */
	public Response post_RequestWithBodyAsMultipartFormData(JSONObject jsonString, String serviceUri) {
		Response postResponse = null;
		if (jsonString.get("attachments").toString().isEmpty()) {
			postResponse = given().relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		} else {
			postResponse = given().relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("attachments", new File((String) jsonString.get("attachments")))
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		}
		return postResponse;
	}

	public Response put_Request(String url, Object body, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
				.extract().response();
		return putResponse;
	} // end PUT_REQUEST

	/**
	 * REST ASSURED GET request method
	 *
	 * @param url
	 *            destination of the request
	 * @return Response object that has the REST response
	 */
	public Response get_Request_Path_queryParam(String url, HashMap<String, String> path_value,
			HashMap<String, List<String>> query_value) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParameters(path_value)
				.queryParams(query_value).log().all().when().get(url).then().log().all().extract().response();
		return getResponse;
	} // end GET_REQUEST

	public Response putRequestWithPathParameter(String url, HashMap<String, String> path_value, JSONObject body,
			String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParameters(path_value)
				.body(body).contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log()
				.all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}
	public Response putRequestWithPathParameterWithoutBody(String url, HashMap<String, String> path_value,
			String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParameters(path_value)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log()
				.all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * REST ASSURED GET request method
	 *
	 * @param url
	 *            destination of the request
	 * @return Response object that has the REST response
	 */

	/*** ENDDDDDD ***/
	public Response get_Request_queryParam(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log()
				.all().when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	/*
	 * Defining with Template type of Param check later if any duplicate method
	 * exists then delete this
	 */

	public Response get_Request_pathAndQueryParam(String url, HashMap<String, String> map) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response getResponse = null;
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> thisEntry = (Entry<String, String>) iterator.next();

			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
					.queryParam(thisEntry.getKey().toString(), thisEntry.getValue().toString()).log().all().when()
					.get(url).then().log().all().extract().response();

		}

		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	/*
	 * Defining with Template type of Param check later if any duplicate method
	 * exists then delete this
	 */

	public Response deleteRequestPathAndQueryParam(String url, HashMap<String, String> map) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response getResponse = null;
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> thisEntry = (Entry<String, String>) iterator.next();

			getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
					.queryParam(thisEntry.getKey().toString(), thisEntry.getValue().toString()).log().all().when()
					.delete(url).then().log().all().extract().response();

		}

		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST
	public Response get_Request_multiplePathAndMultipleQueryParam(String url, HashMap<String, String> map) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response getResponse = null;

		int i = 0;

		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
				.queryParam(map.keySet().toArray()[i].toString(), map.get(map.keySet().toArray()[i]).toString())
				.queryParam(map.keySet().toArray()[i + 1].toString(), map.get(map.keySet().toArray()[i + 1]).toString())
				.log().all().when().get(url).then().log().all().extract().response();

		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	public Response get_Request_pathParam(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log().all()
				.when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	public Response getRequestWithoutParm(String url) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}


	/**
	 * REST ASSURED GET request method
	 *
	 * @param url
	 *            destination of the request
	 * @return Response object that has the REST response
	 */
	public Response get_Request_pathParameters(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log().all()
				.when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST
	public Response put_Request_pathAndMultipleQueryParam(String url, HashMap<String, String> map) {
        logger.info("REST-ASSURED: Sending a Put request to " + url);

        Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
        
        Response getResponse = null;
        
        int i=0;
               
                     getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParam(map.keySet().toArray()[i].toString(), map.get(map.keySet().toArray()[i]).toString())
                     .queryParam(map.keySet().toArray()[i+1].toString(), map.get(map.keySet().toArray()[i+1]).toString()).log()
                     .all().when().put(url).then().log().all().extract().response();
                     
        
        
        
        
        
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST
  
	public Response get_Request_pathAndMultipleQueryParam(String url, HashMap<String, String> map) {
        logger.info("REST-ASSURED: Sending a Put request to " + url);

        Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
        
        Response getResponse = null;
        
        int i=0;
               
                     getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParam(map.keySet().toArray()[i].toString(), map.get(map.keySet().toArray()[i]).toString())
                     .queryParam(map.keySet().toArray()[i+1].toString(), map.get(map.keySet().toArray()[i+1]).toString()).log()
                     .all().when().get(url).then().log().all().extract().response();
                     
        
        
        
        
        
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST
  

	public Response GET_REQUEST_withoutParameters(String url) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	public Response put_Request(String url, String contentHeader, String acceptHeader,
			HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log()
				.all().when().put(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		return getResponse;
	}

	public Response delete_Request(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log()
				.all().when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response deleteRequestWithPathParam(String url) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().delete(url)
				.then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response deleteRequest(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log().all()
				.when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}
	public Response deleteRequestWithPathAndQuery(String url, HashMap<String, String> valueMap, HashMap<String, String> query) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).queryParams(query).log().all().when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response delete_RequestPathParameters(String url, HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log().all()
				.when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response Post_DataPacket(File file, String url) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file).expect()
				.when().post(url);
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response Post_JSONwithFileWithParm(Object body, File file, String url, String contentHeader,
			HashMap<String, String> parm) {
		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request
		 */

		String Document_request = fetch_IDRepo().get("req.Documentrequest");

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(parm).multiPart("file", file)
				.formParam(Document_request, body).contentType(contentHeader).expect().when().post(url).then().log().all().extract().response();
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response Post_JSONwithFile(Object body, File file, String url, String contentHeader) {
		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request
		 */

		String Document_request = fetch_IDRepo().get("req.Documentrequest");

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file)
				.formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * REST ASSURED GET request method
	 *
	 * @param url
	 *            destination of the request
	 * @return Response object that has the REST response
	 */
	public Response get_Request_queryParam(String url, String contentHeader, String acceptHeader, String urls) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when()
				.get(url + "?" + urls).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	public static void backUpFiles(String source, String destination) {
		// String time =
		// java.time.LocalDate.now().toString()+"--"+java.time.LocalTime.now().toString();
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(Instant.now()));

		String result = String.format("%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
		// System.out.println(System.getProperty("APPDATA"));
		String filePath = "src/test/resources/APPDATA/MosipUtil/UtilFiles/" + destination + "/" + result;
		File sourceFolder = new File(source);
		File dest = new File(filePath);
		try {
			FileUtils.copyDirectory(sourceFolder, dest);
			logger.info("Please Check Your %APPDATA% in C drive to get access to the generted files");
		} catch (IOException e) {
			logger.info("Check %APPDATA%");
		}
	}

	public Response get_request_pathParam(String url, String id, String keyId, java.lang.String timestamp,
			java.lang.String keytimestamp) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		HashMap params = new HashMap();
		params.put(keyId, id);
		params.put(keytimestamp, timestamp);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(params).log().all()
				.when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST

	public Response put_RequestWithBody(String url, String contentHeader, String acceptHeader, JSONObject valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
				.contentType(MediaType.APPLICATION_JSON).body(valueMap.toJSONString()).log().all().when().put(url)
				.then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		return getResponse;
	}

	public Response post_Request_WithQueryParams(String url, Object body, String contentHeader, String acceptHeader,
			HashMap<String, String> valueMap) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.queryParams(valueMap).contentType(contentHeader).accept(acceptHeader).log().all().when().post(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST

	// GLOBAL CLASS VARIABLES

	Properties prop = new Properties();

	public Map<String, String> fetch_IDRepo() {
		try {
			prop.load(new FileInputStream(getResourcePath()+"config/IDRepo.properties"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> mapProp = prop.entrySet().stream()
				.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
		return mapProp;

	}
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 */
	public String getResourcePath() {
		return MosipTestRunner.getGlobalResourcePath()+"/";
	}

	public Response put_RequestWithoutBody(String url, String contentHeader, String acceptHeader) {
		logger.info("REST-ASSURED: Sending a PUT request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
				.contentType(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all().extract()
				.response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		return getResponse;
	}


	/**
	 * @author Arjun for id repo
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return
	 */
	public Response patch_Request(String url, Object body, String contentHeader, String acceptHeader) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().patch(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/*
	 * public Response Post_JSONwithFileParam(Object body,File file,String
	 * url,String contentHeader,String langCodeKey,String value) {
	 * logger.info("REST:ASSURED:Sending a data packet to"+url);
	 * logger.info("Request DTO for document upload is"+ body);
	 * logger.info("Name of the file is"+file.getName()); Response getResponse =
	 * null;
	 * 
	 * Fetch to get the param name to be passed in the request
	 * 
	 * 
	 * String Notification_request=fetch_IDRepo("req.notify");
	 * getResponse=given().relaxedHTTPSValidation().multiPart("file",file).
	 * formParam (Notification_request,
	 * body).formParam(langCodeKey,value).contentType(contentHeader).expect().
	 * when() .post(url);
	 * 
	 * 
	 * logger.info("REST:ASSURED: The response from request is:"+getResponse.
	 * asString()); logger.info("REST-ASSURED: the response time is: "+
	 * getResponse.time()); return getResponse; }
	 */

	// Notify
	public Response Post_JSONwithFileParam(Object body, File file, String url, String contentHeader, String langCodeKey,
			String value) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("Request DTO for document upload is" + body);
		logger.info("Name of the file is" + file.getName());
		Response getResponse = null;
		/*
		 * FeFtch to get the param name to be passed in the request
		 */

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		String Notification_request = fetch_IDRepo().get("req.notify");
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("attachment", file)
				.formParam(Notification_request, body).formParam(langCodeKey, value).contentType(contentHeader).expect()
				.when().post(url);

		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response get_RequestWithoutBody(String url, String contentHeader, String acceptHeader) {
		logger.info("REST-ASSURED: Sending a Get request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
				.contentType(MediaType.APPLICATION_JSON).log().all().when().get(url).then().log().all().extract()
				.response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		return getResponse;
	}


	public Response get_Request_Path_queryParamString(String url, HashMap<String, String> path_value,
			HashMap<String, String> query_value) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);
		Response getResponse = given().relaxedHTTPSValidation().pathParameters(path_value).queryParams(query_value)
				.log().all().when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	} // end GET_REQUEST



	public Response regProcSyncRequest(String url, Object body, String center_machine_refId, String ldt, String contentHeader) {
		Cookie.Builder builder = new Cookie.Builder("Authorization",regProcAuthToken);
		Response postResponse = given().cookie(builder.build()).header("Center-Machine-RefId",center_machine_refId).header("timestamp",ldt).relaxedHTTPSValidation().body("\""+body+"\"").contentType(contentHeader)
				.log().all().when().post(url).then().log().all().extract().response();
		return postResponse;
	}
	public Response regProcPacketUpload(File file,String url) {

		logger.info("REST:ASSURED:Sending a data packet to"+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization",regProcAuthToken);
		Response getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file",file).expect().when().post(url);
		logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
		logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
		return getResponse;

	}
	public Response regProcGetRequest(String url,HashMap<String, String> valueMap) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization",adminRegProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap)
				.log().all().when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	public Response postDataPacket(File file,String url) {
		logger.info("REST:ASSURED:Sending a data packet to"+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization",authToken);
		Response getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file",file).expect().when().post(url);
		logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
		logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
		return getResponse;
	}
	public Response regProcPostRequest(String url, HashMap<String, String> valueMap, String contentHeader) {
		logger.info("REST:ASSURED:Sending a post request to"+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization",adminRegProcAuthToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(valueMap).contentType(contentHeader)
				.log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
		public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("REST ASSURRED :: Request To Encrypt Is "+ body);
		Cookie.Builder builder = new Cookie.Builder("Authorization",regProcAuthToken);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();

		return postResponse;
	}
		
		

	public Response regProcPacketGenerator(Object body,String url,String contentHeader ) {
		logger.info("REST:ASSURED:Sending a post request to"+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization",adminRegProcAuthToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	public static Response postRequestToGetCookie(String url, Object body, String contentHeader, String acceptHeader) {
		logger.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

}
