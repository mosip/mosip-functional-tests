package io.mosip.testrig.apirig.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.restassured.response.Response;

public class GlobalMethods {

	private static final Logger logger = Logger.getLogger(GlobalMethods.class);

	public static Map<Object, Object> serverFailuresMapS = Collections.synchronizedMap(new HashMap<Object, Object>());
	public static Map<Object, Object> captchaStatusMap = Collections.synchronizedMap(new HashMap<Object, Object>());

	public static Set<String> serverEndpoints = new HashSet<>();

	// Define the regex pattern to extract the domain and the path after the domain
	private static String module_name = "(mimoto|certify|signup|partnermanager|preregistration|resident|residentmobileapp|masterdata|esignet|idgenerator|policymanager|idauthentication|idrepository|auditmanager|authmanager|keymanager|mock-identity-system)"; 
	private static String regex_1 = "https://([^/]+)/(v[0-9]+)?/" + module_name + "/([^,]+)";
	private static String regex_2 = "https://([^/]+)/" + module_name + "/(v[0-9]+)/([^,]+)";

	// Compile the regex pattern
	private static Pattern pattern_1 = Pattern.compile(regex_1);
	private static Pattern pattern_2 = Pattern.compile(regex_2);
	
	public static String runContext = null;	
	
	public static boolean isXSSProtectionCheckEnabled() {
		return ConfigManager.getproperty("xssProtectionCheck").equalsIgnoreCase("yes") ? true : false;
	}
	
	//Method to check if X-XSS-Protection is enabled or not
	public static void checkXSSProtectionHeader(Response response, String url) throws SecurityXSSException {
        String xssHeader = response.getHeader("X-Xss-Protection");

        if (isXSSProtectionCheckEnabled() && 
            (xssHeader == null || !xssHeader.equalsIgnoreCase("1; mode=block"))) {

            reportResponseHeader(response.getHeaders().asList().toString(), url);
            throw new SecurityXSSException("Response Header does not have X-XSS-Protection");
        }
    }
	
	// Method to set the module name and recompile the regex patterns
	public static void setModuleNameAndReCompilePattern(String value) {
		if (value == null || value.trim().isEmpty()) {
			// If the value is null or empty, just return and do nothing
			return;
		}

		// Update module_name with the new value
		module_name = value;

		// Recreate the regex strings based on the updated module_name
		regex_1 = "https://([^/]+)/(v[0-9]+)?/" + module_name + "/([^,]+)";
		regex_2 = "https://([^/]+)/" + module_name + "/(v[0-9]+)/([^,]+)";

		// Recompile the regex patterns based on the new regex strings
		pattern_1 = Pattern.compile(regex_1);
		pattern_2 = Pattern.compile(regex_2);
	}
	
	public static String getRunContext() {
		// Generate a UUID, remove dashes,convert to lowercase, and take first 6
		// characters
		runContext = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase().substring(0, 3) + "_";
		logger.info("RUN_CONTEXT set to: " + runContext);
		return runContext;
	}

	public static void main(String[] arg) {

	}

	public static String getUpdatedEndPointURL(String url) {
		// Create a matcher for the current URL
		Matcher matcher = pattern_1.matcher(url);
		// Check if the first pattern matches
		boolean matcherAvailable = matcher.find();
		if (matcherAvailable) {
			String domain = matcher.group(1);
			String version = matcher.group(2) != null ? matcher.group(2) : ""; // Handle null for optional group
			String module = matcher.group(3);
			String endpoint = version + "/" + module + "/" + matcher.group(4);

			// Replace BaseURL if provided from outside
			String newBaseURL = ConfigManager.getComponentBaseURL(module);

			if (newBaseURL != null && !newBaseURL.isEmpty()) {
				// Replace the part in the URL
				return url.replace(domain, newBaseURL);
			} else {
				return url;
			}
		}

		// RegEX didn't match, try with other pattern...

		// Create a matcher for the current URL
		Matcher matcher2 = pattern_2.matcher(url);
		// Check if the second pattern matches
		if (matcher2.find()) {
			String domain = matcher2.group(1);
			String module = matcher2.group(2) != null ? matcher2.group(2) : ""; // Handle null for optional group
			String version = matcher2.group(3);
			String endpoint = module + "/" + version + "/" + matcher2.group(4);

			// Replace BaseURL if provided from outside
			String newBaseURL = ConfigManager.getComponentBaseURL(module);

			if (newBaseURL != null && !newBaseURL.isEmpty()) {
				// Replace the part in the URL
				return url.replace(domain, newBaseURL);
			} else {
				return url;
			}
		}

		// Both RegEx didn't match.. Needs revisit..
		logger.error("Needs RegEx revisit..." + "url is:" + url);
		return url;
	}

	public static String addToServerEndPointMap(String url) {
		String updatedURL = getUpdatedEndPointURL(url);
		serverEndpoints.add(updatedURL);
		return updatedURL;
	}

	public static String removeNumerics(String url) {
		// Define the regex patterns
		String regex1 = "/\\d+/"; // Remove numeric characters between slashes
		String regex2 = "/\\d+$"; // Remove numeric characters after the last slash at the end of the string
		String regex3 = "/mosip_[a-zA-Z0-9_]+/"; // Remove alphanumeric sequences starting with 'mosip_' between slashes
		// Compile the regex patterns
		Pattern pattern1 = Pattern.compile(regex1);
		Pattern pattern2 = Pattern.compile(regex2);
		Pattern pattern3 = Pattern.compile(regex3);
		// Apply the regex replacements sequentially
		String modifiedString = url;
		modifiedString = pattern1.matcher(modifiedString).replaceAll("/");
		modifiedString = pattern2.matcher(modifiedString).replaceAll("/");
		modifiedString = pattern3.matcher(modifiedString).replaceAll("/");
		return modifiedString;
	}

	public static String getComponentDetails() {
		// Define the regex pattern to extract the domain and the path after the domain
		String regex_1 = "https://([^/]+)/(v[0-9]+)?/" + module_name + "/([^,]+)";
		// Compile the regex pattern
		Pattern pattern_1 = Pattern.compile(regex_1);

		String regex_2 = "https://([^/]+)/" + module_name + "/(v[0-9]+)/([^,]+)";
		// Compile the regex pattern
		Pattern pattern_2 = Pattern.compile(regex_2);

		// Set to store unique results
		Set<String> uniqueResults = new HashSet<>();
		// Iterate over the set of URLs
		for (String url : serverEndpoints) {

			// Create a matcher for the current URL
			Matcher matcher_1 = pattern_1.matcher(url);
			// Find matches
			if (matcher_1.find()) {
				String domain = matcher_1.group(1);
				String version = matcher_1.group(2) != null ? matcher_1.group(2) : ""; // Handle null for optional group
				String module = matcher_1.group(3);
				String endpoint = version + "/" + module + "/" + matcher_1.group(4);
				String result = "Domain: " + domain + " ---- Module: " + module + " ---- End Point: "
						+ removeNumerics(endpoint);
				uniqueResults.add(result);
			} else {
				// Create a matcher for the current URL
				Matcher matcher_2 = pattern_2.matcher(url);
				// Find matches
				if (matcher_2.find()) {
					String domain = matcher_2.group(1);
					String module = matcher_2.group(2) != null ? matcher_2.group(2) : ""; // Handle null for optional
																							// group
					String version = matcher_2.group(3);
					String endpoint = module + "/" + version + "/" + matcher_2.group(4);
					String result = "Domain: " + domain + " ---- Module: " + module + " ---- End Point: "
							+ removeNumerics(endpoint);
					uniqueResults.add(result);
				}
			}
		}

		// Convert the set to an ArrayList
		List<String> uniqueList = new ArrayList<>(uniqueResults);
		StringBuilder stringBuilder = new StringBuilder();
		// Print the unique results
		for (String result : uniqueList) {
			stringBuilder.append("\n").append(result);
		}
		return stringBuilder.toString();
	}
	
	public static String getTestCaseVariableMapping() {
		VariableDependencyMapper mapper = new VariableDependencyMapper(AdminTestUtil.generators,
				AdminTestUtil.consumers);
		StringBuilder variableMappingBuilder = new StringBuilder();
		
		if (!mapper.getConsumerToGeneratorsMap().isEmpty()) {
			variableMappingBuilder.append("Consumer to Generators Mapping:\n");
	        mapper.getConsumerToGeneratorsMap().forEach((k, v) -> variableMappingBuilder.append(k + " → " + v).append("\n"));
	    }

	    String impactSummary = mapper.getImpactSummary();
	    if (impactSummary != null && !impactSummary.trim().isEmpty()) {
	    	variableMappingBuilder.append("\nImpact Summary:\n");
	    	variableMappingBuilder.append(impactSummary);
	    }

	    String impactByGenerator = mapper.getImpactSummaryBasedOnGenerator();
	    if (impactByGenerator != null && !impactByGenerator.trim().isEmpty()) {
	    	variableMappingBuilder.append("\nImpact Summary Based On Generator:\n");
	    	variableMappingBuilder.append(impactByGenerator);
	    }

	    String impactByConsumer = mapper.getImpactSummaryBasedOnConsumer();
	    if (impactByConsumer != null && !impactByConsumer.trim().isEmpty()) {
	    	variableMappingBuilder.append("\nImpact Summary Based On Consumer:\n");
	    	variableMappingBuilder.append(impactByConsumer);
	    }
		
		
		return variableMappingBuilder.toString();
	}

	public static void reportServerError(Object code, Object errorMessage) {
		serverFailuresMapS.put(code, errorMessage);
	}

	public static String getServerErrors() {
		// Construct server errors using string builder
//		StringBuilder stringBuilder = new StringBuilder();
		if (serverFailuresMapS.size() == 0) {
			return "No server errors";
		} else {
			return serverFailuresMapS.toString();
		}

	}
	
	public static void reportCaptchaStatus(Object code, Object captchaMessage) {
		captchaStatusMap.put(code, captchaMessage);
	}
	
	public static boolean getCaptchaStatus() {
		Object value = captchaStatusMap.get(GlobalConstants.CAPTCHA_ENABLED);
	    return Boolean.parseBoolean(String.valueOf(value));

	}

	public static String maskOutSensitiveInfo(String strInput) {
		if (ConfigManager.IsDebugEnabled())
			return strInput;
		
		Pattern secretKeyPattern = Pattern
				.compile("\"secretKey\"\\s*:\\s*\"[^\"]*\"");
		Matcher secretKeyMatcher = secretKeyPattern.matcher(strInput);
		String maskedInput = secretKeyMatcher
				.replaceAll("\"secretKey\": \"***** MASKED *****\"");
		
		Pattern clientSecretKeyPattern = Pattern
				.compile("\"client_secret\"\\s*:\\s*\"[^\"]*\"");
		Matcher clientSecretKeyMatcher = clientSecretKeyPattern.matcher(strInput);
		maskedInput = clientSecretKeyMatcher
				.replaceAll("\"client_secret\": \"***** MASKED *****\"");
		
		Pattern INDIVIDUAL_BIOMETRICS_PATTERN = Pattern
				.compile("\"category\":\\s?\"individualBiometrics\",\\s?\"value\":\\s?\"(.*?)\"");
		Matcher biometricsMatcher = INDIVIDUAL_BIOMETRICS_PATTERN.matcher(maskedInput);
		maskedInput = biometricsMatcher
				.replaceAll("\"category\": \"individualBiometrics\", \"value\": \"***** MASKED *****\"");

		return maskedInput;
	}

	public static void ReportRequestAndResponse(String reqHeader, String resHeader, String url, String requestBody,
			String response, boolean formatResponse) {
		reportRequest(reqHeader, requestBody, url);
		reportResponse(resHeader, url, response, formatResponse);
	}

	public static void ReportRequestAndResponse(String reqHeader, String resHeader, String url, String requestBody,
			String response) {
		reportRequest(reqHeader, requestBody, url);
		reportResponse(resHeader, url, response);
	}

	public static void reportRequest(String requestHeader, String request) {
		reportRequest(requestHeader, request, "");
	}

	public static void reportRequest(String requestHeader, String request, String url) {
		
		if (url != null && url.isBlank() == false) {
			url = GlobalMethods.addToServerEndPointMap(url);
		}

		String formattedHeader = ReportUtil.getTextAreaForHeaders(requestHeader);

		if (request != null && !request.equals("{}"))
			Reporter.log(GlobalConstants.REPORT_REQUEST_PREFIX + url + GlobalConstants.REPORT_REQUEST_BODY
					+ formattedHeader + ReportUtil.getTextAreaJsonMsgHtml(maskOutSensitiveInfo(request))
					+ GlobalConstants.REPORT_REQUEST_SUFFIX);
		else
			Reporter.log(GlobalConstants.REPORT_REQUEST_PREFIX + url + GlobalConstants.REPORT_REQUEST_BODY
					+ formattedHeader + ReportUtil.getTextAreaJsonMsgHtml("No request body")
					+ GlobalConstants.REPORT_REQUEST_SUFFIX);
	}

	public static void reportResponse(String responseHeader, String url, Response response) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
				+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + GlobalConstants.REPORT_RESPONSE_SUFFIX);
	}
	public static void reportResponseHeader(String responseHeader, String url) {
		String formattedHeader = ReportUtil.getTextAreaForHeaders(responseHeader);

		Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + formattedHeader
				 + GlobalConstants.REPORT_RESPONSE_SUFFIX);
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
			Reporter.log(GlobalConstants.REPORT_RESPONSE_PREFIX + GlobalConstants.REPORT_RESPONSE_BODY + responseHeader
					+ response + GlobalConstants.REPORT_RESPONSE_SUFFIX);
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
