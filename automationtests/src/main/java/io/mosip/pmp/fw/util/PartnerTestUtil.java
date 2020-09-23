package io.mosip.pmp.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.response.Response;

public class PartnerTestUtil extends AuthTestsUtil{
	
	private static final Logger partnerLogger = Logger.getLogger(PartnerTestUtil.class);
	CommonLibrary lib = new CommonLibrary();
	
	/**
	 * The method returns run config path
	 */
	public static String getAdminRunConfigFile() {
		return RunConfigUtil.getResourcePath()+"partner/TestData/RunConfig/runConfiguration.properties";
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File(getAdminRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataPath[" + index + "]");
	}
	
	/**
	 * The method get property value for the key
	 * 
	 * @param key
	 * @return string
	 */
	public static String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}
	
	
	
	/**
	 * The method get env config details
	 * 
	 * @return properties
	 */
	private static Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			RunConfigUtil.objRunConfig.setUserDirectory();
			input = new FileInputStream(new File(RunConfigUtil.getResourcePath()+"partner/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
			prop.load(input);
			input.close();
			return prop;
		} catch (Exception e) {
			partnerLogger.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	
	public static String getGlobalResourcePath() {
		return MosipTestRunner.getGlobalResourcePath();
	}

	public JSONObject getReqRespJson(File testCaseFoldername, String fileKeyword)
	{
		File listOfFiles[] = testCaseFoldername.listFiles();
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(fileKeyword)) {
				return lib.readJsonData(testCaseFoldername.getAbsolutePath()+"/"+listOfFiles[j].getName(), false);
			}
		}
		return null;
	}
	
	/**
	 * The method will return test data file name from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataFileName(String className, int index) {
		return getPropertyAsMap(new File(getAdminRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataFileName[" + index + "]");
	}
	
	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response=null;
					String responseJson = "";
					if (code == 0)
						response = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,code,cookieName,cookieValue);
					responseJson=response.asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean patchRequestAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					if (code == 0)
						response = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code,cookieName,cookieValue);
					responseJsonToVerifyDigtalSignature=response.asString();
					responseDigitalSignatureValue=response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean patchRequestWithBodyAndParameter(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code, String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String partnerId = "";
					if (objectData.containsKey("partnerId")) {
						partnerId = objectData.get("partnerId").toString();
						objectData.remove("partnerId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String subUrlPath = token[1];
					StringTokenizer st1 = new StringTokenizer(subUrlPath,"}");
					String[] token1 = new String[2];
					for (int i = 0; i <token1.length; ) {
						while (st1.hasMoreTokens()) {
							String test1=st1.nextToken();
							token1[i]=test1;
							i++;
						}
					}
					
					String newUrlPath = token[0] + partnerId + token1[1];

					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	
	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean getRequestAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response=null;
					String responseJson = "";
					if (code == 0)
						response = getRequestWithPathParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = getRequestWithPathParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response.asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean getRequestWithQueryAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response=null;
					String responseJson = "";
					if (code == 0)
						response = getRequestWithQueryParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = getRequestWithQueryParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response.asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean deleteRequestWithPathAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response=null;
					String responseJson = "";
					if (code == 0)
						response = deleteRequestWithPathParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = deleteRequestWithPathParm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response.asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}	
	
	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean putRequestWithParmAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					if (code == 0)
						response = putRequestWithparm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = putRequestWithparm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}

	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean putRequestWithQueryParmAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					if (code == 0)
						response = putRequestWithQueryparm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = putRequestWithQueryparm(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	/**
	 * The method will put request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean putRequestAndGenerateOuputFileWithCookie(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					if (code == 0)
						response = putRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					else
						response = putRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean putRequestWithBodyAndParameter(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					
					String partnerId="";
					
					if (objectData.containsKey("partnerId")) {
						partnerId = objectData.get("partnerId").toString();
						objectData.remove("partnerId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath);
					String newUrlPath = st.nextToken("{") + partnerId;
					
					if (code == 0)
					    response = putRequestWithParameter(objectData, newUrlPath, cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath, cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	public static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.getResourcePath()
				+ "partner/TestData/Security/GetCookie/getCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePathForRegClient() {
		return RunConfigUtil.getResourcePath()
				+ "partner/TestData/Security/GetCookie/getRegClientCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePath(String fileName) {
		return RunConfigUtil.getResourcePath()
				+ "partner/TestData/Security/GetCookie/"+fileName+".json".toString();
	}
	
	public static void initiatePartnerTest() {
		copyPartnerTestResource();
	}
	
	public static void copyPartnerTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/partner");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			partnerLogger.info("Copied the partner test resource successfully");
		} catch (Exception e) {
			partnerLogger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	
	protected boolean patchRequestWithBodyAndParameteForActiveAndDeactivePartner(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code, String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String partnerId = "";
					if (objectData.containsKey("partnerId")) {
						partnerId = objectData.get("partnerId").toString();
						objectData.remove("partnerId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + partnerId;

					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean patchRequestWithBodyAndParameterForActivateDeactivatePartnerAPIKey(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code, String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String partnerId = "";
					String partnerApiKey = "";
					if (objectData.containsKey("partnerId") && objectData.containsKey("partnerApiKey")) {
						partnerId = objectData.get("partnerId").toString();
						partnerApiKey = objectData.get("partnerApiKey").toString();
						objectData.remove("partnerId");
						objectData.remove("partnerApiKey");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[3];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + partnerId + "/" + partnerApiKey;

					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean patchRequestWithBodyAndParameterForApproveRejectPartnerAPIKeyReq(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String apiKeyReqId = "";
					if (objectData.containsKey("apiKeyReqId")) {
						apiKeyReqId = objectData.get("apiKeyReqId").toString();
						objectData.remove("apiKeyReqId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + apiKeyReqId;
					
					
					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJsonToVerifyDigtalSignature=response.asString();
					responseDigitalSignatureValue=response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean putRequestWithBodyAndParameterForUpdatePartnerApikeyToPolicyMappings(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String partnerId = "";
					String partnerApiKey = "";
					
					if (objectData.containsKey("partnerId") && objectData.containsKey("partnerApiKey")) {
						partnerId = objectData.get("partnerId").toString();
						partnerApiKey = objectData.get("partnerApiKey").toString();
						objectData.remove("partnerId");
						objectData.remove("partnerApiKey");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[3];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + partnerId + "/" + partnerApiKey;
					
					if (code == 0)
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean putUpdateExistingPolicyForPolicyGroup(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String policyGroupId = "";
					
					if (objectData.containsKey("policyGroupId")) {
						policyGroupId = objectData.get("policyGroupId").toString();
						objectData.remove("policyGroupId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + policyGroupId;
					
					if (code == 0)
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean putUpdatePolicyDetails(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String policyID = "";
					
					if (objectData.containsKey("policyID")) {
						policyID = objectData.get("policyID").toString();
						objectData.remove("policyID");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + policyID;
					
					if (code == 0)
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean patchUpdateTheStatusActivateDeactivateForTheGivenPolicyId(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String policyID = "";
					if (objectData.containsKey("policyID")) {
						policyID = objectData.get("policyID").toString();
						objectData.remove("policyID");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + policyID;
					
					
					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJsonToVerifyDigtalSignature=response.asString();
					responseDigitalSignatureValue=response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean patchUpdatePolicyStatus(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String policyGroupId = "";
					String policyID = "";
					
					if (objectData.containsKey("policyGroupId") && objectData.containsKey("policyID")) {
						policyGroupId = objectData.get("policyGroupId").toString();
						policyID = objectData.get("policyID").toString();
						objectData.remove("policyGroupId");
						objectData.remove("policyID");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[3];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + policyGroupId + "/policyId/" + policyID;
					
					
					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJsonToVerifyDigtalSignature=response.asString();
					responseDigitalSignatureValue=response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean putUpdateMISP(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String mispId = "";
					
					if (objectData.containsKey("mispId")) {
						mispId = objectData.get("mispId").toString();
						objectData.remove("mispId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + mispId;
					
					if (code == 0)
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	
	protected boolean patchApproveMISPRequestWithBodyAndParameter(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code, String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String mispId = "";
					if (objectData.containsKey("mispId")) {
						mispId = objectData.get("mispId").toString();
						objectData.remove("mispId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String subUrlPath = token[1];
					StringTokenizer st1 = new StringTokenizer(subUrlPath,"}");
					String[] token1 = new String[2];
					for (int i = 0; i <token1.length; ) {
						while (st1.hasMoreTokens()) {
							String test1=st1.nextToken();
							token1[i]=test1;
							i++;
						}
					}
					
					String newUrlPath = token[0] + mispId + token1[1];

					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean putActivateDeactivateMISPLincense(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String mispId = "";
					if (objectData.containsKey("mispId")) {
						mispId = objectData.get("mispId").toString();
						objectData.remove("mispId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String subUrlPath = token[1];
					StringTokenizer st1 = new StringTokenizer(subUrlPath,"}");
					String[] token1 = new String[2];
					for (int i = 0; i <token1.length; ) {
						while (st1.hasMoreTokens()) {
							String test1=st1.nextToken();
							token1[i]=test1;
							i++;
						}
					}
					
					String newUrlPath = token[0] + mispId + token1[1];
					
					if (code == 0)
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = putRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response;
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean patchUpdateMispStatusByMispId(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response;
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String mispId = "";
					if (objectData.containsKey("mispId")) {
						mispId = objectData.get("mispId").toString();
						objectData.remove("mispId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[2];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + mispId;
					
					
					if (code == 0)
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = patchRequestWithParameter(objectData, newUrlPath,cookieName,cookieValue);
					responseJsonToVerifyDigtalSignature=response.asString();
					responseDigitalSignatureValue=response.getHeader(responseDigitalSignatureKey);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
					fos.write(response.asString().getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean postPublishPolicy(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code,String cookieName, String cookieValue) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response response=null;
					String responseJson = "";
					
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(listOfFiles[j].getAbsolutePath()));
					String policyGroupId = "";
					String policyId = "";
					
					if (objectData.containsKey("policyGroupId") && objectData.containsKey("policyId")) {
						policyGroupId = objectData.get("policyGroupId").toString();
						policyId = objectData.get("policyId").toString();
						objectData.remove("policyGroupId");
						objectData.remove("policyId");
					}
					
					StringTokenizer st = new StringTokenizer(urlPath,"{");
					String[] token = new String[3];
					for (int i = 0; i <token.length; ) {
						while (st.hasMoreTokens()) {
							String test=st.nextToken();
							token[i]=test;
							i++;
						}
					}
					
					String newUrlPath = token[0] + policyGroupId + "/policyId/" + policyId;
					
					if (code == 0)
						response = postRequestWithCookieforPublishPolicy(objectData, newUrlPath,cookieName,cookieValue);
					else
						response = postRequestWithCookieforPublishPolicy(objectData, newUrlPath,cookieName,cookieValue);
					responseJson=response.asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + newUrlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					responseJson=JsonPrecondtion.toPrettyFormat(responseJson);
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			partnerLogger.error("Exception " + e);
			return false;
		}
	}
}
