package io.mosip.testrig.apirig.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;

/**
 * Class to show the result in table and text area format in testng report
 * 
 * @author Vignesh
 *
 */
public class ReportUtil {
	
	/**
	 * Publish the request and response headers in text area
	 * @param content
	 * @return test area html
	 */
	public static String getTextAreaForHeaders(String headers) {
		String formattedHeader = "No headers";
		if (headers != null && !headers.isEmpty())
			formattedHeader = headers;
		StringBuilder sb = new StringBuilder();
		sb.append("<div style='padding: 0; margin: 0;'><textarea style='border: solid 1px gray; background-color: lightgray; width: 100%; padding: 0; margin: 0;' name='headers' rows='2' readonly='true'>");
		sb.append(formattedHeader);
		sb.append("</textarea></div>");
		return sb.toString();
	}
	
	/**
	 * Method to show the output validation result in table format in testng report
	 * 
	 * @param outputresultRunConfigUtil.getResourcePath()
	 * @return html table
	 */
	public static String getOutputValidationReport(Map<String, List<OutputValidationDto>> outputresult) {
		String htmlforReport = "<table width='100%' charset='UTF8'>\r\n" + "  <tr style='background-color: #d3d3d3;'>\r\n" + "    <th>FieldName</th>\r\n"
				+ "    <th>Expected Value</th> \r\n" + "    <th>Actual Value</th>\r\n" + "    <th>Status</th>\r\n"
				+ "  </tr>\r\n";
		boolean outputValidationDone = false;
		String temp = "";

		for (Entry<String, List<OutputValidationDto>> entry : outputresult.entrySet()) {
			temp = "<b> Output validation: </b>" + entry.getKey()+ "\r\n";
			for (OutputValidationDto dto : entry.getValue()) {
				if (dto.getStatus().equals("PASS")) {
					htmlforReport = htmlforReport + "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n"
							+ "    <td>" + dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue()
							+ "</td>\r\n" + "    <td bgcolor='Green'>" + dto.getStatus() + "</td>\r\n" + "  </tr>\r\n";
					outputValidationDone = true;
				} else if (dto.getStatus().equals(GlobalConstants.FAIL_STRING)) {
					htmlforReport = htmlforReport + "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n"
							+ "    <td>" + dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue()
							+ "</td>\r\n" + "    <td bgcolor='RED'>" + dto.getStatus() + "</td>\r\n" + "  </tr>\r\n";
					outputValidationDone = true;
				}
				// If it is warning basically we haven't compared or ignored the comparison.
				// so no point in printing that content in the report.
				/*
				 * else if (dto.getStatus().equals("WARNING")) { htmlforReport = htmlforReport +
				 * "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n" + "    <td>" +
				 * dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue() +
				 * "</td>\r\n" + "    <td bgcolor='LIGHTYELLOW'>" + dto.getStatus() +
				 * "</td>\r\n" + "  </tr>\r\n"; }
				 */
			}
		}
		if (!outputValidationDone) {
		    return "<b style=\"background-color: #0A0;\">Marking test case as passed. As Output validation not performed and no errors in the response</b><br>\n";
		}
			
		htmlforReport = temp + htmlforReport + "</table>";
		return htmlforReport;
	}


	/**
	 * Publish the request and response message in text area 
	 * 
	 * @param content
	 * @return test area html
	 */
	public static String getTextAreaJsonMsgHtml(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div> <textarea style='border:solid 1px black;' name='message' rows='6' cols='160' readonly='true'>");
		try {
			sb.append(JsonPrecondtion.toPrettyFormat(content));
		} catch (Exception e) {
			sb.append(content);
		}
		sb.append("</textarea> </div>");
		return sb.toString();
	}
}