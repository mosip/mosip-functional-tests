package io.mosip.testrig.apirig.global.utils;

import org.testng.Reporter;

import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.restassured.response.Response;

public class GlobalMethods {

	
	public static void ReportRequestAndResponse(String reqHeader,String resHeader,String url, String requestBody, String response) {
	reportRequest(reqHeader,requestBody);
	reportResponse(resHeader,url, response);				
	}	
	
	
	public static void reportRequest(String requestHeader, String request) {
		
		String formattedHeader = ReportUtil.getTextAreaForHeaders(requestHeader);

		if (request != null)
			Reporter.log(GlobalConstants.REPORT_REQUEST_PREFIX + formattedHeader + ReportUtil.getTextAreaJsonMsgHtml(request)
					+ GlobalConstants.REPORT_REQUEST_SUFFIX);
		else
			Reporter.log(
					GlobalConstants.REPORT_REQUEST_PREFIX + formattedHeader + "No request body" + GlobalConstants.REPORT_REQUEST_SUFFIX);
	}

	public static void reportResponse(String responseHeader, String url, Response response) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
				+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}

	public static void reportResponse(String responseHeader, String url, String response) {
		reportResponse(responseHeader, url, response, false);
	}

	public static void reportResponse(String responseHeader, String url, String response, boolean formatResponse) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		if (formatResponse)
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
					+ ReportUtil.getTextAreaJsonMsgHtml(response) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
		else
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY + responseHeader + response
					+ GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}

}
