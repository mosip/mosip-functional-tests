package io.mosip.testrig.apirig.global.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Reporter;

import com.google.gson.JsonObject;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.restassured.response.Response;

public class GlobalMethods {

	private static final Logger logger = Logger.getLogger(GlobalMethods.class);
	public static void ReportRequestAndResponse(String reqHeader,String resHeader,String url, String requestBody, String response) {
	reportRequest(reqHeader,requestBody, url);
	reportResponse(resHeader,url, response);				
	}	
	
	public static void reportRequest(String requestHeader, String request) {
		reportRequest(requestHeader, request, "");
	}
	
	public static void reportRequest(String requestHeader, String request, String url) {
		
		String formattedHeader = ReportUtil.getTextAreaForHeaders(requestHeader);

		if (request != null && !request.equals("{}"))
			Reporter.log(GlobalConstants.REPORT_REQUEST_PREFIX + url + GlobalConstants.REPORT_REQUEST_BODY + formattedHeader + ReportUtil.getTextAreaJsonMsgHtml(request)
					+ GlobalConstants.REPORT_REQUEST_SUFFIX);
		else
			Reporter.log(
					GlobalConstants.REPORT_REQUEST_PREFIX + url + GlobalConstants.REPORT_REQUEST_BODY + formattedHeader + "No request body" + GlobalConstants.REPORT_REQUEST_SUFFIX);
	}

	public static void reportResponse(String responseHeader, String url, Response response) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
				+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}

	public static void reportResponse(String responseHeader, String url, String response) {
		reportResponse(responseHeader, url, response, false);
	}

	public static void reportResponse(String responseHeader, String url, String response, boolean formatResponse) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		if (formatResponse)
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
					+ ReportUtil.getTextAreaJsonMsgHtml(response) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
		else
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + responseHeader + response
					+ GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}
	
    // Hashes a string using SHA-256
	public static String sha256(String input) {
		String returnString = "";
        MessageDigest digest;
		try {
	        digest = MessageDigest.getInstance("SHA-256");
	        byte[] hashBytes = digest.digest(input.getBytes());

	        StringBuilder hexStringBuilder = new StringBuilder(2 * hashBytes.length);
	        for (byte hashByte : hashBytes) {
	            hexStringBuilder.append(String.format("%02x", hashByte));
	        }
	        returnString = hexStringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error("Failed while hashing SHA256 for VCI code challenge " + e.getMessage());
		}
		return returnString;

    }

}
