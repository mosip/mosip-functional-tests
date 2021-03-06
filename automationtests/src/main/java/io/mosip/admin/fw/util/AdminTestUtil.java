package io.mosip.admin.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Reporter;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.testrunner.MosipTestRunner;
import io.restassured.response.Response;

public class AdminTestUtil extends AuthTestsUtil{
	
	private static final Logger adminLogger = Logger.getLogger(AdminTestUtil.class);
	CommonLibrary lib = new CommonLibrary();
	
	/**
	 * The method returns run config path
	 */
	public static String getAdminRunConfigFile() {
		return RunConfigUtil.getResourcePath()+"admin/TestData/RunConfig/runConfiguration.properties";
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
			input = new FileInputStream(new File(RunConfigUtil.getResourcePath()+"admin/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
			prop.load(input);
			input.close();
			return prop;
		} catch (Exception e) {
			adminLogger.error("Exception: " + e.getMessage());
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
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
			adminLogger.error("Exception " + e);
			return false;
		}
	}
	
	public static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.getResourcePath()
				+ "admin/TestData/Security/GetCookie/getCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePathForRegClient() {
		return RunConfigUtil.getResourcePath()
				+ "admin/TestData/Security/GetCookie/getRegClientCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePath(String fileName) {
		return RunConfigUtil.getResourcePath()
				+ "admin/TestData/Security/GetCookie/"+fileName+".json".toString();
	}
	
	public static void initiateAdminTest() {
		copyAdminTestResource();
	}
	
	public static void copyAdminTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/admin");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			adminLogger.info("Copied the admin test resource successfully");
		} catch (Exception e) {
			adminLogger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	static KernelDataBaseAccess masterDB = new KernelDataBaseAccess();
	
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
}
