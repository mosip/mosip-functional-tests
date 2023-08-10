package io.mosip.testrig.apirig.global.utils;

import org.testng.Reporter;

import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.restassured.response.Response;

public class GlobalMethods {

	public static void reportRequest(String request) {
		Reporter.log(GlobalConstants.REPORT_REQUEST_PREFIX + ReportUtil.getTextAreaJsonMsgHtml(request == null ? "No request body" : request)
				+ GlobalConstants.REPORT_REQUEST_SUFFIX);
	}

	public static void reportResponse(String url, Response response) {
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY
					+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}
	
	public static void reportResponse(String url, String response) {
		reportResponse(url, response, false);
	}
	
	public static void reportResponse(String url, String response, boolean formatResponse) {
		if (formatResponse)
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY
				+ ReportUtil.getTextAreaJsonMsgHtml(response) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
		else 
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + url + GlobalConstants.REPORT_RESPONSE_BODY
					+ response + GlobalConstants.REPORT_RESPONSE_SUFFIX);
}

}
