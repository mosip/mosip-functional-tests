package io.mosip.testrig.apirig.utils;

import static io.restassured.RestAssured.given;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
//import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.PdfReader;
import com.mifmif.common.regex.Generex;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.StandardCharset;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import io.jsonwebtoken.JwtException;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dataprovider.BiometricDataProvider;
import io.mosip.testrig.apirig.dbaccess.DBManager;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.testrunner.MessagePrecondtion;
import io.mosip.testrig.apirig.testrunner.OTPListener;
//import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 * @author Sohan
 *
 */
public class AdminTestUtil extends BaseTestCase {

	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	protected static Properties properties = null;
	protected static Properties propsMap = null;
	protected static Properties propsBio = null;
	public static String propsHealthCheckURL = getGlobalResourcePath() + "/" + "config/healthCheckEndpoint.properties";
	private static String serverComponentsCommitDetails;
	public static boolean foundHandlesInIdSchema = false;
	public static JSONArray globalRequiredFields = null;
	protected static String token = null;
	String idToken = null;
	public static String PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION = null;
	public static String PASSWORD_TO_RESET = null;
	public static final String RESOURCE_FOLDER_NAME = "MosipTemporaryTestResource";
	protected static String genertedUIN = null;
	protected static String generatedRid = null;
	protected static String policygroupId = null;
	protected static String regDeviceResponse = null;
	protected static String generatedVID = null;
	public String RANDOM_ID = "mosip" + generateRandomNumberString(2) + Calendar.getInstance().getTimeInMillis();
	public final String RANDOM_ID_2 = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String RANDOM_ID_V2 = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String RANDOM_ID_V2_S2 = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String RANDOM_ID_V2_S3 = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String TRANSACTION_ID = generateRandomNumberString(10);
	public static final String AUTHORIZATHION_HEADERNAME = GlobalConstants.AUTHORIZATION;
	public static final String AUTH_HEADER_VALUE = "Some String";
	public static final String SIGNATURE_HEADERNAME = GlobalConstants.SIGNATURE;
	public static String updatedPolicyId = "";
	public static String currentLanguage;
	protected static String idField = null;
	protected static String identityHbs = null;
	protected static String updateIdentityHbs = null;
	protected static String draftHbs = null;
	protected static String preregHbsForCreate = null;
	protected static String preregHbsForUpdate = null;
	protected static String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
	protected static String policyGroup = "mosip auth policy group " + timeStamp;
	protected static String policyGroupForUpdate = "mosip auth policy group update " + timeStamp;
	protected static String policyGroup2 = "mosip auth policy group2 " + timeStamp;
	protected static String policyName = "mosip auth policy " + timeStamp;
	protected static String policyName2 = "mosip auth policy2 " + timeStamp;
	protected static String policyNameForUpdate = "mosip auth policy for update " + timeStamp;
	protected static final String UPDATE_UIN_REQUEST = "config/Authorization/requestIdentity.json";
	protected static final String AUTH_INTERNAL_REQUEST = "config/Authorization/internalAuthRequest.json";
	protected static final String AUTH_POLICY_BODY = "config/AuthPolicy.json";
	protected static final String AUTH_POLICY_REQUEST = "config/AuthPolicy3.json";
	protected static final String AUTH_POLICY_REQUEST_ATTR = "config/AuthPolicy2.json";
	protected static final String AUTH_POLICY_BODY1 = "config/AuthPolicy4.json";
	protected static final String AUTH_POLICY_REQUEST1 = "config/AuthPolicy5.json";
	protected static final String AUTH_POLICY_REQUEST_ATTR1 = "config/AuthPolicy6.json";
	protected static final String POLICY_GROUP_REQUEST = "config/policyGroup.json";
	protected static Map<String, String> keycloakRolesMap = new HashMap<>();
	protected static Map<String, String> keycloakUsersMap = new HashMap<>();
	public static final String XSRF_HEADERNAME = "X-XSRF-TOKEN";
	public static final String OAUTH_HASH_HEADERNAME = "oauth-details-hash";
	public static final String OAUTH_TRANSID_HEADERNAME = "oauth-details-key";
	protected static String encryptedSessionKeyString;

	protected static final String ESIGNETUINCOOKIESRESPONSE = "ESignetUINCookiesResponse";
	protected static final String ESIGNETVIDCOOKIESRESPONSE = "ESignetVIDCookiesResponse";

	private static final String UIN_CODE_VERIFIER_POS_1 = generateRandomAlphaNumericString(GlobalConstants.INTEGER_36);

	/** The Constant SIGN_ALGO. */
	private static final String SIGN_ALGO = "RS256";
	public static final int OTP_CHECK_INTERVAL = 10000;

	protected static final Map<String, String> actuatorValueCache = new HashMap<>();

	public static final Map<String, String> autoGeneratedIDValueCache = new HashMap<>();
	
	
	public static Map<String, List<String>> generators = new HashMap<>();
	public static Map<String, List<String>> consumers = new HashMap<>();

	public static void init() {
		properties = getproperty(getGlobalResourcePath() + "/" + "config/application.properties");
		propsMap = getproperty(getGlobalResourcePath() + "/" + "config/valueMapping.properties");
		propsBio = getproperty(getGlobalResourcePath() + "/" + "config/bioValue.properties");

		PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION = properties.getProperty("passwordForAddIdentity");
		PASSWORD_TO_RESET = properties.getProperty("passwordToReset");

		BaseTestCase.init();
	}

	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public static boolean isCaptchaEnabled() {
		String temp = getValueFromEsignetActuator(GlobalConstants.CLASS_PATH_APPLICATION_PROPERTIES,
				GlobalConstants.MOSIP_ESIGNET_CAPTCHA_REQUIRED);
		if (temp.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * This method will hit post request and return the response
	 * 
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */

	protected Response postWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return postWithBodyAndCookie(url, jsonInput, false, cookieName, role, testCaseName, false);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		return postWithBodyAndCookie(url, jsonInput, false, cookieName, role, testCaseName, bothAccessAndIdToken);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {

		return postWithBodyAndCookie(url, jsonInput, auditLogCheck, cookieName, role, testCaseName, false);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.PREREG) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else if (role.equals("userDefinedCookie")) {
			JSONObject req = new JSONObject(inputJson);
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
			inputJson = req.toString();
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}

			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(inputJson);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}

		return response;
	}

	protected Response deleteWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.deleteRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response postWithBodyAndCookieWithText(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, "*/*", cookieName,
					token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieWithoutBody(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;

		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, "*/*", cookieName,
					token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeaderAndXsrfToken(String url, String jsonInput, String cookieName,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject request = new JSONObject(inputJson);
		String encodedResp = null;
		String transactionId = null;
		String headerTransactionID = "";
		String headerVerifedTransactionID = "";
		Map<String, String> cookiesMap = new HashMap<>();

		if (request.has(GlobalConstants.REQUEST)
				&& request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.TRANSACTIONID)
					.toString();
			headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);
		}

		token = properties.getProperty(GlobalConstants.XSRFTOKEN);

		if (request.has(GlobalConstants.HEADERTRANSACTIONID)) {
			headerTransactionID = request.get(GlobalConstants.HEADERTRANSACTIONID).toString();
			cookiesMap.put(GlobalConstants.TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			request.remove(GlobalConstants.HEADERTRANSACTIONID);
		}
		if (request.has(GlobalConstants.VERIFIEDTRANSACTIONID)) {
			headerVerifedTransactionID = request.get(GlobalConstants.VERIFIEDTRANSACTIONID).toString();
			cookiesMap.put(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY, headerVerifedTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			request.remove(GlobalConstants.VERIFIEDTRANSACTIONID);
		}

		if (request.has(GlobalConstants.ENCODEDHASH)) {
			encodedResp = request.get(GlobalConstants.ENCODEDHASH).toString();
			logger.info("encodedhash = " + encodedResp);
			headers.put(OAUTH_HASH_HEADERNAME, encodedResp);
			request.remove(GlobalConstants.ENCODEDHASH);
		}
		if (request.has(GlobalConstants.IDV_TRANSACTION_ID)) {
			headerTransactionID = request.get(GlobalConstants.IDV_TRANSACTION_ID).toString();
			headers.put(GlobalConstants.IDV_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.IDV_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			request.remove(GlobalConstants.IDV_TRANSACTION_ID);
		}

		inputJson = request.toString();
		if (BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));

		if (testCaseName.contains("_IdpAccessToken_")) {
			JSONObject requestInput = new JSONObject(inputJson);
			headers.put(cookieName, "Bearer " + requestInput.get(GlobalConstants.IDP_ACCESS_TOKEN).toString());
			requestInput.remove(GlobalConstants.IDP_ACCESS_TOKEN);
			if (requestInput.has("client_id"))
				requestInput.remove("client_id");
			inputJson = requestInput.toString();
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			if (cookiesMap.containsKey(GlobalConstants.TRANSACTION_ID_KEY)
					|| cookiesMap.containsKey(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY)
					|| cookiesMap.containsKey(GlobalConstants.IDV_TRANSACTION_ID_KEY)) {
				if (testCaseName.contains("_Missing_CSRF_"))
					headers.remove(XSRF_HEADERNAME);
				response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson,
						MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookiesMap, headers);
			} else {
				if (testCaseName.contains("_GenerateChallengeNegTC_Missing_CSRF_"))
					headers.remove(XSRF_HEADERNAME);
				response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson,
						MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token, headers);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.contains("_STransId"))
				getvalueFromResponseHeader(response, testCaseName);

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public void getvalueFromResponseHeader(Response response, String testCaseName) {

		if (response.getHeaders().hasHeaderWithName("set-cookie")) {

			List<String> ListOfSetCookieValues = response.getHeaders().getValues("set-cookie");

			for (String eachSetCookieValues : ListOfSetCookieValues) {
				String[] setCookieValues = eachSetCookieValues.split(";");
				for (String eachSetCookieValue : setCookieValues) {
					if (eachSetCookieValue.trim().startsWith("VERIFIED_TRANSACTION_ID=")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "VTransactionID", testCaseName);
					}
					if (eachSetCookieValue.trim().startsWith("TRANSACTION_ID=")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "TransactionID", testCaseName);
					}
					if (eachSetCookieValue.trim().endsWith("~path-fragment")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "pathFragmentCookie", testCaseName);
					}
					if (eachSetCookieValue.trim().startsWith("IDV_TRANSACTION_ID")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "idvTransactionID", testCaseName);
					}
					if (eachSetCookieValue.trim().startsWith("IDV_SLOT_ALLOTTED")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "idvSlotAllotted", testCaseName);
					}
					if (eachSetCookieValue.trim().startsWith("SESSION=")) {
						getCookieAndWriteAutoGenId(eachSetCookieValue, "sessionCookie", testCaseName);
					}
				}
			}
		}
	}

	protected void getCookieAndWriteAutoGenId(String cookieValue, String key, String testCaseName) {
		if (cookieValue.split("=").length > 1 && !cookieValue.split("=")[1].isBlank()) {
			String value = cookieValue.split("=")[1];
			writeAutoGeneratedId(testCaseName, key, value);
		}
	}

	protected Response postWithBodyAndCookieAuthHeaderAndXsrfTokenForAutoGeneratedId(String url, String jsonInput,
			String cookieName, String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		token = properties.getProperty(GlobalConstants.XSRFTOKEN);

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}

	}

	protected Response postRequestWithCookieAuthHeaderAndXsrfTokenForAutoGenId(String url, String jsonInput,
			String cookieName, String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject request = new JSONObject(inputJson);
		String encodedResp = null;
		String transactionId = null;
		String headerTransactionID = "";
		String pathFragmentCookie = null;
		String pathFragmentCookieTransactionId = null;
		Map<String, String> cookiesMap = new HashMap<>();

		if (request.has(GlobalConstants.ENCODEDHASH)) {
			encodedResp = request.get(GlobalConstants.ENCODEDHASH).toString();
			request.remove(GlobalConstants.ENCODEDHASH);
		}
		if (request.has(GlobalConstants.REQUEST) && request.get(GlobalConstants.REQUEST) instanceof JSONObject
				&& request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.TRANSACTIONID)
					.toString();

		}
		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		headers.put(OAUTH_HASH_HEADERNAME, encodedResp);
		headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);

		if (request.has(GlobalConstants.PATH_FRAGMENT_COOKIE_TRANSACTIONID)
				&& request.has(GlobalConstants.PATH_FRAGMENT_COOKIE)) {
			pathFragmentCookieTransactionId = request.get(GlobalConstants.PATH_FRAGMENT_COOKIE_TRANSACTIONID)
					.toString();
			pathFragmentCookie = request.get(GlobalConstants.PATH_FRAGMENT_COOKIE).toString();
			request.remove(GlobalConstants.PATH_FRAGMENT_COOKIE_TRANSACTIONID);
			request.remove(GlobalConstants.PATH_FRAGMENT_COOKIE);
		}

		inputJson = request.toString();
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		token = properties.getProperty(GlobalConstants.XSRFTOKEN);

		if (request.has(GlobalConstants.IDV_TRANSACTION_ID)) {
			headerTransactionID = request.get(GlobalConstants.IDV_TRANSACTION_ID).toString();
			headers.put(GlobalConstants.IDV_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.IDV_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			request.remove(GlobalConstants.IDV_TRANSACTION_ID);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			if (pathFragmentCookie != null) {
				response = RestClient.postRequestWithMultipleHeadersAndMultipleCookies(url, inputJson,
						MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, pathFragmentCookieTransactionId,
						pathFragmentCookie, headers);
			} else if (cookiesMap.containsKey(GlobalConstants.IDV_TRANSACTION_ID_KEY)) {
				response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson,
						MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookiesMap, headers);
			} else {
				response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson,
						MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token, headers);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			if (testCaseName.contains("_STransId")) {
				getvalueFromResponseHeader(response, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response getRequestWithCookieAuthHeaderAndXsrfToken(String url, String jsonInput, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject request = new JSONObject(inputJson);
		String encodedResp = null;
		String transactionId = null;
		if (request.has(GlobalConstants.ENCODEDHASH)) {
			encodedResp = request.get(GlobalConstants.ENCODEDHASH).toString();
			request.remove(GlobalConstants.ENCODEDHASH);
		}
		if (request.has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.get(GlobalConstants.TRANSACTIONID).toString();
			request.remove(GlobalConstants.ENCODEDHASH);
		}
		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		headers.put(OAUTH_HASH_HEADERNAME, encodedResp);
		headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);

		token = null;

		if (request.has(GlobalConstants.IDV_SLOT_ALLOTED)) {
			token = request.get(GlobalConstants.IDV_SLOT_ALLOTED).toString();
			cookieName = "IDV_SLOT_ALLOTTED";
			request.remove(GlobalConstants.IDV_SLOT_ALLOTED);
		}
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(headers.toString(), null, url);
		try {
			response = RestClient.getRequestWithMultipleHeadersAndCookies(url, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		String apiKey = null;
		String partnerId = null;
		JSONObject req = new JSONObject(inputJson);
		apiKey = req.getString(GlobalConstants.APIKEY);
		req.remove(GlobalConstants.APIKEY);
		partnerId = req.getString(GlobalConstants.PARTNERID);
		req.remove(GlobalConstants.PARTNERID);

		HashMap<String, String> headers = new HashMap<>();
		headers.put("PARTNER-API-KEY", apiKey);
		headers.put("PARTNER-ID", partnerId);
		headers.put(cookieName, "Bearer " + token);
		inputJson = req.toString();
		if (BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForKeyCloak(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postRequestWithBearerToken(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAcceptTextPlainAndCookie(String url, String jsonInput, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.TEXT_PLAIN, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeaderAndSignature(String url, String jsonInput, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		Response response = null;
		String[] uriParts = url.split("/");
		String partnerId = uriParts[uriParts.length - 2];
		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, generateSignatureWithRequest(inputJson, partnerId));

		if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithAuthHeaderAndSignatureForOtp(String url, String jsonInput, String cookieName,
			String token, Map<String, String> headers, String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);

		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;

	}

	protected Response postRequestWithAuthHeaderAndSignatureForOtpAutoGenId(String url, String jsonInput,
			String cookieName, String token, Map<String, String> headers, String testCaseName, String idKeyName)
			throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);

		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;

	}

	protected Response patchRequestWithCookieAuthHeaderAndSignature(String url, String jsonInput, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, generateSignatureWithRequest(inputJson, null));
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Patch request Json to EndPointUrl: " + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.patchRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithAuthHeaderAndSignature(String url, String jsonInput, String testCaseName)
			throws SecurityXSSException {
		Response response = null;
		String[] uriParts = url.split("/");
		String partnerId = uriParts[uriParts.length - 2];
		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		if (testCaseName.contains("NOAUTH")) {
			headers.put(SIGNATURE_HEADERNAME, "");
		} else {
			headers.put(SIGNATURE_HEADERNAME, generateSignatureWithRequest(inputJson, partnerId));
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithHeaderAndSignature(String url, String jsonInput, String signature,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, signature);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return postRequestWithCookieAndHeader(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response postRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE,
						IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		if (url.contains("ID:"))
			url = inputJsonKeyWordHandeler(url, testCaseName);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (BaseTestCase.currentModule.equals("auth") || BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.patchRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return patchWithBodyAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response patchWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		if (url.contains("ID:")) {
			url = uriKeyWordHandelerUri(url, testCaseName);
		}
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, auditLogCheck, cookieName, role, testCaseName,
				idKeyName, false);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName, idKeyName,
				false);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			boolean bothAccessAndIdToken, String cookieName, String role, String testCaseName, String idKeyName)
			throws SecurityXSSException {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName, idKeyName,
				bothAccessAndIdToken);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken)
			throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.PREREG)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else if (role.equals("userDefinedCookie")) {
			JSONObject request = new JSONObject(inputJson);
			if (request.has(GlobalConstants.COOKIE)) {
				token = request.get(GlobalConstants.COOKIE).toString();
				request.remove(GlobalConstants.COOKIE);
				inputJson = request.toString();
				if (request.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = request.get(GlobalConstants.COOKIE_NAME).toString();
					request.remove(GlobalConstants.COOKIE_NAME);
				}
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(inputJson);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}

		return response;
	}

	protected Response postWithBodyAndBearerTokenForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (testCaseName.contains("Invalid_Token")) {
			token = "xyz";
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else if (role.equals("userDefinedCookie")) {
			JSONObject request = new JSONObject(inputJson);
			if (request.has(GlobalConstants.COOKIE)) {
				token = request.get(GlobalConstants.COOKIE).toString();
				request.remove(GlobalConstants.COOKIE);
				inputJson = request.toString();
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postRequestWithBearerToken(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			if (testCaseName.contains("_GoogleLoginToken_")) {
				getvalueFromResponseHeader(response, testCaseName);
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(String url, String jsonInput,
			String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(inputJson, Map.class);
			logger.info(GlobalConstants.POST_REQ_URL + url);
			logger.info(inputJson);
			GlobalMethods.reportRequest(null, inputJson, url);
			response = RestClient.postRequestWithFormDataBody(url, map);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			if (testCaseName.contains("UIN_Cookie") || testCaseName.contains("Vid_Cookie")) {
				String keyName = null;
				if (testCaseName.contains("UIN_Cookie"))
					keyName = ESIGNETUINCOOKIESRESPONSE;
				else
					keyName = ESIGNETVIDCOOKIESRESPONSE;

				CertsUtil.addCertificateToCache(keyName, response.getBody().asString());
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookieWithAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		if (url.contains("ID:")) {
			url = uriKeyWordHandelerUri(url, testCaseName);
		}
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName,
				idKeyName, false);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken)
			throws SecurityXSSException {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName,
				idKeyName, bothAccessAndIdToken);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName) throws SecurityXSSException {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, auditLogCheck, cookieName, role,
				testCaseName, idKeyName, false);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken)
			throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (testCaseName.contains("Resident_Login")) {
			cookieName = COOKIENAMESTATE;
		}
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (url.contains("{") || url.contains("?")) {
				if (bothAccessAndIdToken) {
					response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
				} else {
					response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token);
				}

			} else {
				if (bothAccessAndIdToken) {
					response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
				} else {
					response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token);
				}

			}

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(jsonInput);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	public static String encodeBase64(String value) {
		String encodedStr;
		try {
			Encoder encoder = Base64.getEncoder();
			encodedStr = encoder.encodeToString(value.getBytes());
			return encodedStr;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "Error While EncodeingBase64";
		}

	}

	protected Response postWithFormPathParamAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> formParams = new HashMap<>();
		HashMap<String, String> pathParams = new HashMap<>();

		File filetoUpload = null;
		String fileKeyName = null;
		if (req.has(GlobalConstants.FILE_PATH) && req.has(GlobalConstants.FILE_KEY_NAME)) {
			filetoUpload = new File(getResourcePath() + req.get(GlobalConstants.FILE_PATH).toString());
			req.remove(GlobalConstants.FILE_PATH);
			fileKeyName = req.get(GlobalConstants.FILE_KEY_NAME).toString();
			req.remove(GlobalConstants.FILE_KEY_NAME);
		} else
			logger.error("request doesn't contanin filePath and fileKeyName: " + inputJson);

		if (testCaseName.contains("Resident_UploadDocument")) {
			pathParams.put(GlobalConstants.TRANSACTIONID, req.get(GlobalConstants.TRANSACTIONID).toString());
			req.remove(GlobalConstants.TRANSACTIONID);
			try {
				formParams.put(GlobalConstants.REQUEST, encodeBase64(req.toString()));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			pathParams.put(GlobalConstants.PREREGISTRATIONID, req.get(GlobalConstants.PREREGISTRATIONID).toString());
			req.remove(GlobalConstants.PREREGISTRATIONID);
			formParams.put("Document request", req.toString());
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithFormPathParamAndFile(url, formParams, pathParams, filetoUpload, fileKeyName,
					MediaType.MULTIPART_FORM_DATA, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithParamAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) throws SecurityXSSException {
		return postWithParamAndFile(url, jsonInput, role, testCaseName, idKeyName, false);
	}

	protected Response postWithParamAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(inputJson);

		File filetoUpload = null;
		String fileKeyName = null;
		if (req.has(GlobalConstants.FILE_PATH) && req.has(GlobalConstants.FILE_KEY_NAME)) {
			filetoUpload = new File(getResourcePath() + req.get(GlobalConstants.FILE_PATH).toString());
			req.remove(GlobalConstants.FILE_PATH);
			fileKeyName = req.get(GlobalConstants.FILE_KEY_NAME).toString();
			req.remove(GlobalConstants.FILE_KEY_NAME);
		} else
			logger.error("request doesn't contanin filePath and fileKeyName: " + inputJson);

		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(req.toString(), new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, req.toString(), url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postWithParamsAndFile(url, map, filetoUpload, fileKeyName,
						MediaType.MULTIPART_FORM_DATA, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postWithParamsAndFile(url, map, filetoUpload, fileKeyName,
						MediaType.MULTIPART_FORM_DATA, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithFormDataAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> formParams = new HashMap<>();
		formParams.put(GlobalConstants.CATEGORY, req.getString(GlobalConstants.CATEGORY));
		formParams.put(GlobalConstants.OPERATION, req.getString(GlobalConstants.OPERATION));
		formParams.put(GlobalConstants.TABLENAME, req.getString(GlobalConstants.TABLENAME));

		String absolueFilePath = null;
		JSONArray josnArray = req.getJSONArray(GlobalConstants.FILES);
		for (int index = 0; index < josnArray.length(); index++) {
			String csvFilePath = (String) josnArray.get(index);
			absolueFilePath = getResourcePath() + csvFilePath;
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithFormDataAndFile(url, formParams, absolueFilePath,
					MediaType.MULTIPART_FORM_DATA, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithMultipartFormDataAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) throws SecurityXSSException {
		Response response = null;

		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		jsonInput = jsonInput.replace("\r\n", "");

		HashMap<String, String> formParams = new HashMap<>();
		formParams.put("NotificationRequestDTO", jsonInput);
		if (BaseTestCase.languageList.size() == 1)
			formParams.put(GlobalConstants.LANG_CODE, BaseTestCase.languageList.get(0));
		else if (BaseTestCase.languageList.size() == 2)
			formParams.put(GlobalConstants.LANG_CODE,
					BaseTestCase.languageList.get(0) + "," + BaseTestCase.languageList.get(1));
		else
			formParams.put(GlobalConstants.LANG_CODE, BaseTestCase.languageList.get(0) + ","
					+ BaseTestCase.languageList.get(1) + "," + BaseTestCase.languageList.get(2));
		formParams.put("attachment", "");

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, jsonInput, url);

		try {
			response = RestClient.postWithMultipartFormDataAndFile(url, formParams, MediaType.MULTIPART_FORM_DATA,
					token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithFormDataAndMultipleFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> formParams = new HashMap<>();
		formParams.put(GlobalConstants.CATEGORY, req.getString(GlobalConstants.CATEGORY));
		formParams.put(GlobalConstants.OPERATION, req.getString(GlobalConstants.OPERATION));
		formParams.put(GlobalConstants.TABLENAME, req.getString(GlobalConstants.TABLENAME));

		String absolueFilePath = null;
		JSONArray josnArray = req.getJSONArray(GlobalConstants.FILES);
		for (int index = 0; index < josnArray.length(); index++) {
			String csvFilePath = (String) josnArray.get(index);
			absolueFilePath = getResourcePath() + csvFilePath;
			if (formParams.get(GlobalConstants.CATEGORY).equalsIgnoreCase("masterData")) {
				absolueFilePath = StringUtils.substringBefore(absolueFilePath, GlobalConstants.FILES_TO_UPLOAD)
						+ GlobalConstants.FILES_TO_UPLOAD;
			}
		}
		File file = new File(absolueFilePath);
		File[] listFiles = file.listFiles();
		for (File specificFile : listFiles) {
			if (formParams.get(GlobalConstants.OPERATION).equalsIgnoreCase("insert")
					&& specificFile.getName().equals(formParams.get(GlobalConstants.TABLENAME) + ".csv")) {
//				specificFile = updateCSV(specificFile.getAbsolutePath(), "OLD", 1, 0);
				listFiles = new File[1];
				listFiles[0] = specificFile;
			} else {
				if (formParams.get(GlobalConstants.OPERATION).equalsIgnoreCase(GlobalConstants.UPDATE)
						&& specificFile.getName().equalsIgnoreCase(
								GlobalConstants.UPDATE + formParams.get(GlobalConstants.TABLENAME) + ".csv")) {
					listFiles = new File[1];
					listFiles[0] = specificFile;
				}
			}
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithFormDataAndMultipleFile(url, formParams, listFiles,
					MediaType.MULTIPART_FORM_DATA, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public static void initialUserCreation() throws SecurityXSSException {
		Response response = null;
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		org.json.simple.JSONObject actualRequestGeneration = BaseTestCase.getRequestJson("config/bulkUpload.json");
		String url = ApplnURI + ConfigManager.getproperty("bulkUploadUrl");

		JSONObject req = new JSONObject(actualRequestGeneration);

		HashMap<String, String> formParams = new HashMap<>();
		formParams.put(GlobalConstants.CATEGORY, req.getString(GlobalConstants.CATEGORY));
		formParams.put(GlobalConstants.OPERATION, req.getString(GlobalConstants.OPERATION));
		formParams.put(GlobalConstants.TABLENAME, req.getString(GlobalConstants.TABLENAME));

		String absolueFilePath = null;
		JSONArray josnArray = req.getJSONArray(GlobalConstants.FILES);
		for (int index = 0; index < josnArray.length(); index++) {
			String csvFilePath = (String) josnArray.get(index);
			absolueFilePath = getResourcePath() + csvFilePath;
			if (formParams.get(GlobalConstants.CATEGORY).equalsIgnoreCase("masterData")) {
				absolueFilePath = StringUtils.substringBefore(absolueFilePath, GlobalConstants.FILES_TO_UPLOAD)
						+ GlobalConstants.FILES_TO_UPLOAD;
			}
		}
		File file = new File(absolueFilePath);
		File[] listFiles = file.listFiles();

		for (File specificFile : listFiles) {
			if (formParams.get(GlobalConstants.OPERATION).equalsIgnoreCase("insert")
					&& specificFile.getName().equals(formParams.get(GlobalConstants.TABLENAME) + ".csv")) {
				specificFile = updateCSV(specificFile.getAbsolutePath(), "OLD", 1, 0);
				listFiles = new File[1];
				listFiles[0] = specificFile;
			} else {
				if (formParams.get(GlobalConstants.OPERATION).equalsIgnoreCase(GlobalConstants.UPDATE)
						&& specificFile.getName().equalsIgnoreCase(
								GlobalConstants.UPDATE + formParams.get(GlobalConstants.TABLENAME) + ".csv")) {
					listFiles = new File[1];
					listFiles[0] = specificFile;
				}
			}
		}
		try {
			response = RestClient.postWithFormDataAndMultipleFile(url, formParams, listFiles,
					MediaType.MULTIPART_FORM_DATA, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}

	}

	/**
	 * This method will hit put request and return the response
	 * 
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */

	protected Response putWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return putWithBodyAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response putWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(inputJson, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.putRequestWithParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(inputJson, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + inputJson + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.patchRequestWithParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		logger.info("inputJson is::" + inputJson);
		JSONObject req = new JSONObject(inputJson);
		logger.info(GlobalConstants.REQ_STR + req);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			logger.info("param is::" + param);
			if (req.has(param)) {
				logger.info(GlobalConstants.REQ_STR + req);
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, req.toString(), url);
		try {
			if (testCaseName.toLowerCase().contains("dynamic"))
				pathParamsMap.put("id", idField);
			response = RestClient.putWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamsBodyAndBearerToken(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		logger.info("inputJson is::" + inputJson);
		JSONObject req = new JSONObject(inputJson);
		logger.info(GlobalConstants.REQ_STR + req);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			logger.info("param is::" + param);
			if (req.has(param)) {
				logger.info(GlobalConstants.REQ_STR + req);
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}
		if (testCaseName.contains("Invalid_Token")) {
			token = "xyz";
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, req.toString(), url);
		try {
			if (testCaseName.toLowerCase().contains("dynamic"))
				pathParamsMap.put("id", idField);
			response = RestClient.putWithPathParamsBodyAndBearerToken(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		if (role.equals("userDefinedCookie")) {
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithPathParamsBodyAndCookieForAutoGeneratedId(String url, String jsonInput,
			String cookieName, String role, String testCaseName, String pathParams, String idKeyName)
			throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		if (role.equals("userDefinedCookie")) {
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response postWithPathParamsBodyHeadersAndCookieForAutoGeneratedId(String url, String jsonInput,
			String cookieName, String role, String testCaseName, String pathParams, String idKeyName,
			String headersValue) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		HashMap<String, String> headersMap = new HashMap<>();
		String[] params = pathParams.split(",");
		String[] headers = headersValue.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		for (String header : headers) {
			if (req.has(header)) {
				headersMap.put(header, req.get(header).toString());
				req.remove(header);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + header + GlobalConstants.IN_STRING + inputJson);
		}

		if (role.equals("userDefinedCookie")) {
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headersMap.toString(), inputJson, url);
		try {
			response = RestClient.postWithPathParamsBodyHeadersAndCookie(url, pathParamsMap, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token, headersMap);

			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response postWithPathParamsBodyHeaderAndCookie(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String pathParams) throws SecurityXSSException {
		Response response = null;
		String signature = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);

		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}
		if (req.has(GlobalConstants.SIGNATURE)) {
			signature = req.get(GlobalConstants.SIGNATURE).toString();
			req.remove(GlobalConstants.SIGNATURE);
		}
		headers.put(SIGNATURE_HEADERNAME, signature);
		if (req.has(GlobalConstants.REQUEST)) {
			req = new JSONObject(req.get(GlobalConstants.REQUEST).toString());
		}

		inputJson = req.toString();

		if (inputJson.contains("\u200B")) {
			inputJson = inputJson.replaceAll("\u200B", "");
		}
		if (inputJson.contains("\\p{Cf}")) {
			inputJson = inputJson.replaceAll("\\p{Cf}", "");
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), req.toString(), url);
		try {
			response = RestClient.postWithPathParamsBodyHeadersAndCookie(url, pathParamsMap, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token, headers);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithQueryParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String queryParams, String idKeyName) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> queryParamsMap = new HashMap<>();
		String[] params = queryParams.split(",");
		for (String param : params) {
			if (req.has(param)) {
				queryParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
		try {
			response = RestClient.postWithQueryParamsBodyAndCookie(url, queryParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, "*/*", cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		HashMap<String, String> pathParamsMap = new HashMap<>();
		String[] params = pathParams.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, req.toString(), url);
		try {
			response = RestClient.patchWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	/**
	 * This method will hit get request and return the response
	 * 
	 * @param url
	 * @param jsonInput
	 * @param cookieName
	 * @param role
	 * @return Response
	 */

	protected Response getWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return getWithPathParamAndCookie(url, jsonInput, false, cookieName, role, testCaseName, false);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		return getWithPathParamAndCookie(url, jsonInput, false, cookieName, role, testCaseName, bothAccessAndIdToken);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		return getWithPathParamAndCookie(url, jsonInput, auditLogCheck, cookieName, role, testCaseName, false);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		String headerTransactionID = "";
		Map<String, String> cookiesMap = new HashMap<>();

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else if (role.equals("userDefinedCookie")) {
			JSONObject req = new JSONObject(jsonInput);
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
			jsonInput = req.toString();
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (map != null && map.containsKey(GlobalConstants.HEADERTRANSACTIONID)) {
			headerTransactionID = map.get(GlobalConstants.HEADERTRANSACTIONID).toString();
			cookiesMap.put(GlobalConstants.TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			map.remove(GlobalConstants.HEADERTRANSACTIONID);
		}

		if (map != null && map.containsKey(GlobalConstants.VERIFIEDTRANSACTIONID)) {
			headerTransactionID = map.get(GlobalConstants.VERIFIEDTRANSACTIONID).toString();
			cookiesMap.put(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			map.remove(GlobalConstants.VERIFIEDTRANSACTIONID);
		}

		if (map != null && map.containsKey(GlobalConstants.IDV_TRANSACTION_ID)) {
			headerTransactionID = map.get(GlobalConstants.IDV_TRANSACTION_ID).toString();
			cookiesMap.put(GlobalConstants.IDV_TRANSACTION_ID_KEY, headerTransactionID);
			cookiesMap.put(GlobalConstants.XSRF_TOKEN, token);
			map.remove(GlobalConstants.IDV_TRANSACTION_ID);
		}

		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (url.contains("{") || url.contains("?")) {
				if (bothAccessAndIdToken) {
					response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
				} else if (cookiesMap.containsKey(GlobalConstants.IDV_TRANSACTION_ID_KEY)) {
					response = RestClient.getRequestWithMultipleCookieAndPathParam(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookiesMap);
				} else {
					response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token);
				}

			} else if (testCaseName.contains("_IdpAccessToken_")) {
				JSONObject request = new JSONObject(jsonInput);
				if (request.has(GlobalConstants.IDP_ACCESS_TOKEN)) {
					token = request.get(GlobalConstants.IDP_ACCESS_TOKEN).toString();
					request.remove(GlobalConstants.IDP_ACCESS_TOKEN);
				}
				response = RestClient.getRequestWithBearerToken(url, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			} else if (cookiesMap.containsKey(GlobalConstants.TRANSACTION_ID_KEY)
					|| cookiesMap.containsKey(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY)) {
				response = RestClient.getRequestWithMultipleCookie(url, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookiesMap);
			} else {
				if (bothAccessAndIdToken) {
					response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
				} else {
					response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token);
				}

				if (auditLogCheck) {
					JSONObject jsonObject = new JSONObject(jsonInput);
					String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
					String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
					checkDbAndValidate(timeStamp1, dbChecker);
				}
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response getWithPathParamsBodyHeadersAndCookie(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String pathParams, String headersValue) throws SecurityXSSException {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		JSONObject req = new JSONObject(inputJson);
		String acceptHeader = MediaType.APPLICATION_JSON;
		HashMap<String, String> pathParamsMap = new HashMap<>();
		HashMap<String, String> headersMap = new HashMap<>();
		String[] params = pathParams.split(",");
		String[] headers = headersValue.split(",");
		for (String param : params) {
			if (req.has(param)) {
				pathParamsMap.put(param, req.get(param).toString());
				req.remove(param);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + param + GlobalConstants.IN_STRING + inputJson);
		}

		for (String header : headers) {
			if (req.has(header)) {
				headersMap.put(header, req.get(header).toString());
				req.remove(header);
			} else
				logger.error(GlobalConstants.ERROR_STRING_2 + header + GlobalConstants.IN_STRING + inputJson);
		}

		if (role.equals("userDefinedCookie")) {
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		if (req.has(GlobalConstants.ACCEPT_HEADER)) {
			acceptHeader = req.get(GlobalConstants.ACCEPT_HEADER).toString();
			req.remove(GlobalConstants.ACCEPT_HEADER);
		}

		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(headersMap.toString(), inputJson, url);
		try {
			response = RestClient.getRequestWithPathParamsHeadersBodyAndCookie(url, pathParamsMap, inputJson,
					MediaType.APPLICATION_JSON, acceptHeader, cookieName, token, headersMap);

			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			String contentType = response.getHeader("Content-Type");
			if (contentType != null && !contentType.contains("application/pdf")) {
				GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			}

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response deleteWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return deleteWithPathParamAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response deleteWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject req = new JSONObject(jsonInput);
		HashMap<String, String> map = null;

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else if (role.equals("userDefinedCookie")) {
			if (req.has(GlobalConstants.COOKIE)) {
				token = req.get(GlobalConstants.COOKIE).toString();
				req.remove(GlobalConstants.COOKIE);
				if (req.has(GlobalConstants.COOKIE_NAME)) {
					cookieName = req.get(GlobalConstants.COOKIE_NAME).toString();
					req.remove(GlobalConstants.COOKIE_NAME);
				}
			}
			jsonInput = req.toString();
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}

		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.deleteRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.deleteRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response deleteWithPathParamAndCookieForKeyCloak(String url, String jsonInput, String cookieName,
			String role, String testCaseName) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {

			response = RestClient.deleteRequestWithCookieAndPathParmForKeyCloak(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;

		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected byte[] getWithPathParamAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return getWithPathParamAndCookieForPdf(url, jsonInput, false, cookieName, role, testCaseName, false);
	}

	protected byte[] getWithPathParamAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		return getWithPathParamAndCookieForPdf(url, jsonInput, false, cookieName, role, testCaseName,
				bothAccessAndIdToken);
	}

	protected byte[] getWithPathParamAndCookieForPdf(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName) {
		return getWithPathParamAndCookieForPdf(url, jsonInput, auditLogCheck, cookieName, role, testCaseName, false);
	}

	protected byte[] getWithPathParamAndCookieForPdf(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, boolean bothAccessAndIdToken) {
		byte[] pdf = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);

		try {
			if (bothAccessAndIdToken) {
				pdf = RestClient.getPdf(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName,
						token, IDTOKENCOOKIENAME, idToken);
			} else {
				pdf = RestClient.getPdf(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName,
						token);
			}

			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(jsonInput);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return pdf;
	}

	protected byte[] postWithBodyAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return postWithBodyAndCookieForPdf(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected byte[] postWithBodyAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			jsonInput = smtpOtpHandler(inputJson, testCaseName);
		}
		byte[] pdf = null;
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info("******post request to EndPointUrl: " + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (bothAccessAndIdToken) {
				pdf = RestClient.postWithBodyForPdf(url, jsonInput, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				pdf = RestClient.postWithBodyForPdf(url, jsonInput, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			return pdf;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return pdf;
		}
	}

	protected byte[] postWithFormDataBodyForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {

		HashMap<String, String> formDataMap = new HashMap<>();
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		logger.info("inputJson is::" + jsonInput);

		JSONObject req = new JSONObject(jsonInput);
		logger.info(GlobalConstants.REQ_STR + req);
		jsonInput = req.toString();

		byte[] pdf = null;

		try {
			formDataMap = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		logger.info("******Post request to EndPointUrl: " + url);
		GlobalMethods.reportRequest(null, jsonInput, url);

		try {
			pdf = RestClient.postRequestWithFormDataBodyForPdf(url, formDataMap);
			return pdf;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return pdf;
		}

	}

	protected byte[] getWithQueryParamAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return getWithQueryParamAndCookieForPdf(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected byte[] getWithQueryParamAndCookieForPdf(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		byte[] pdf = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (bothAccessAndIdToken) {
				pdf = RestClient.getPdfWithQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
						cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				pdf = RestClient.getPdfWithQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
						cookieName, token);
			}

			return pdf;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return pdf;
		}
	}

	protected Response getWithQueryParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			return response;
		}
	}

	protected Response patchWithQueryParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			return response;
		}
	}

	public static void closeInputStream(FileInputStream inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeOutputStream(FileOutputStream outputStream) {
		if (outputStream != null) {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeOutputStreamWriter(OutputStreamWriter outputStreamWriter) {
		if (outputStreamWriter != null) {
			try {
				outputStreamWriter.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeDataOutputStream(DataOutputStream dataOutputStream) {
		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeBufferedWriter(BufferedWriter bufferedWriter) {
		if (bufferedWriter != null) {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeBufferedReader(BufferedReader bufferedReader) {
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeFileWriter(FileWriter fileWriter) {
		if (fileWriter != null) {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeFileReader(FileReader fileReader) {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closeByteArrayInputStream(ByteArrayInputStream bIS) {
		if (bIS != null) {
			try {
				bIS.close();
			} catch (IOException e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	public static void closePdfReader(PdfReader pdfReader) {
		if (pdfReader != null) {
			try {
				pdfReader.close();
			} catch (Exception e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			}
		}
	}

	String getPartnerId(org.json.JSONArray responseArray, String active, String partnertype) {
		String partnerId = "";
		String partnerStatus = "";
		String partnerType = "";

		for (int i = 0; i < responseArray.length(); i++) {
			partnerStatus = responseArray.getJSONObject(i).getString("status");
			partnerType = responseArray.getJSONObject(i).getString("partnerType");
			if (partnerStatus != null && partnerStatus.equalsIgnoreCase(active)) {
				if (partnerType != null && partnerType.equalsIgnoreCase(partnertype)) {
					partnerId = responseArray.getJSONObject(i).getString("partnerID");
					break;
				}
			}
		}
		return partnerId;
	}
	
	protected void writeToCache(String idKeyName, String idKeyValue, String testCaseName) {
		// Add to the cache for the future use.
		autoGeneratedIDValueCache.put(idKeyName, idKeyValue);

		// Add to the generators to create dependency map in the report.
		if (generators.containsKey(testCaseName)) {
			generators.get(testCaseName).addAll(Arrays.asList(idKeyName));
		} else {
			generators.put(testCaseName, new ArrayList<>(Arrays.asList(idKeyName)));
		}

	}
	
	protected static void writeToConsumersMap(String idKeyName, String testCaseName) {

		// Add to the generators to create dependency map in the report.
		if (consumers.containsKey(testCaseName)) {
			consumers.get(testCaseName).addAll(Arrays.asList(idKeyName));
		} else {
			consumers.put(testCaseName, new ArrayList<>(Arrays.asList(idKeyName)));
		}

	}
	
	protected static void RemoveFromTheConsumersMap(String idKeyName, String testCaseName) {
		List<String> consumerList = consumers.get(testCaseName);
	    
	    if (consumerList != null) {
	    	idKeyName = idKeyName.replace("$ID:", "").replace("$", "");
	        consumerList.remove(idKeyName);
	        
	        if (consumerList.isEmpty()) {
	            consumers.remove(testCaseName);
	        }
	    }

	}
	
	protected static String getFromCache(String idKeyName, String testCaseName) {

		// Add to the generators to create dependency map in the report.
		writeToConsumersMap(idKeyName, testCaseName);

		// get the cache for the future use.
		return autoGeneratedIDValueCache.get(idKeyName);

	}

	protected void writeAutoGeneratedId(Response response, String idKeyName, String testCaseName) {
		JSONObject responseJson = null;
		JSONObject jsonObject = new JSONObject(response.getBody().asString());

		JSONObject responseBody = null;
		String signature = null;
		try {
			if (testCaseName.contains("ESignet_GenerateToken")
					|| testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)
					|| testCaseName.contains("_FullResponse")) {
				responseBody = jsonObject;
				if (testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
					signature = response.getHeader(GlobalConstants.SIGNATURE);
				}
			} else if (testCaseName.contains("SunBirdR_")) {
				responseJson = jsonObject.getJSONObject(GlobalConstants.RESULT)
						.getJSONObject(GlobalConstants.INSURANCE);
			} else if (jsonObject.has(GlobalConstants.RESPONSE)) {
				responseJson = jsonObject.getJSONObject(GlobalConstants.RESPONSE);
			} else {
				responseJson = jsonObject;
			}
			String[] fieldNames = idKeyName.split(",");

			for (String filedName : fieldNames) {
				if (responseJson != null) {
					String identifierKeyName = getAutogenIdKeyName(testCaseName, filedName);
					if (responseJson.has(filedName)) {
						writeToCache(identifierKeyName, responseJson.get(filedName).toString(), testCaseName);
					} else if (responseJson.has("partners")) {
						org.json.JSONArray responseArray = responseJson.getJSONArray("partners");
						String authPartnerId = getPartnerId(responseArray, "active", "Auth_Partner");
						writeToCache(identifierKeyName, authPartnerId, testCaseName);
					} else if (responseJson.has("data")) {
						org.json.JSONArray responseArray = responseJson.getJSONArray("data");

						JSONObject eachJSON = (JSONObject) responseArray.get(0);
						logger.info(eachJSON.get(filedName));
						writeToCache(identifierKeyName, eachJSON.get(filedName).toString(), testCaseName);
					} else if (testCaseName.contains("_OAuthDetailsRequest_") && filedName.equals("encodedResp")) {
						Gson gson = new Gson();
						JsonObject json = gson.fromJson(response.getBody().asString(), JsonObject.class);
						String responseJsonString = json.getAsJsonObject(GlobalConstants.RESPONSE).toString();

						MessageDigest digest = MessageDigest.getInstance("SHA-256");
						byte[] hash = digest.digest(responseJsonString.getBytes(StandardCharsets.UTF_8));
						String urlEncodedResp = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
						logger.info("encoded response = " + urlEncodedResp);
						writeToCache(identifierKeyName, urlEncodedResp, testCaseName);
					} else if (responseJson.has(GlobalConstants.IDENTITY)) {
						writeToCache(identifierKeyName,
								responseJson.getJSONObject(GlobalConstants.IDENTITY).get(filedName).toString(), testCaseName);
					} else {
						String keyValue = findClientId(responseJson.toString(), filedName);
						if (keyValue != null) {
							writeToCache(identifierKeyName, keyValue, testCaseName);
						}
					}
				} else if (responseBody != null) {
					String identifierKeyName = getAutogenIdKeyName(testCaseName, filedName);
					if (responseBody.has(filedName)) {
						writeToCache(identifierKeyName, responseBody.get(filedName).toString(), testCaseName);
					} else if (testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
						if (filedName.equals("authReqBody")) {
							writeToCache(identifierKeyName, responseBody.toString(), testCaseName);
						} else if (filedName.equals("authSignature")) {
							writeToCache(identifierKeyName, signature, testCaseName);
						}
					} else
						logger.error(GlobalConstants.ERROR_STRING_3 + filedName + GlobalConstants.WRITE_STRING
								+ response.asString());
				} else {
					logger.error(GlobalConstants.ERROR_STRING_3 + filedName + GlobalConstants.WRITE_STRING
							+ response.asString());
				}
			}
		} catch (JSONException | NoSuchAlgorithmException | IOException e) {
			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
		}

	}

	public void writeAutoGeneratedId(String testCaseName, String idName, String value) {
		if (testCaseName == null || idName == null || value == null) {
			logger.error("autogenerated id is not stored as few details not available");
			return;
		}
		String identifierKeyName = getAutogenIdKeyName(testCaseName, idName);
		try {
			writeToCache(identifierKeyName, value, testCaseName);
		} catch (JSONException e) {
			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
		}
	}

	/**
	 * @param testCaseName
	 * @param fieldName
	 * @return testCaseName_fieldname
	 */
	public String getAutogenIdKeyName(String testCaseName, String fieldName) {
		if (testCaseName == null)
			return null;
		int indexof = testCaseName.indexOf("_");
		String autogenIdKeyName = testCaseName.substring(indexof + 1);
		if (fieldName.equals("VID")
				&& (BaseTestCase.currentModule.equals("auth") || BaseTestCase.currentModule.equals("esignet"))) {
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName.toLowerCase();
		} else {
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName;
		}
		logger.info("key for testCase: " + testCaseName + " : " + autogenIdKeyName);
		return autogenIdKeyName;
	}

	public static String getGlobalResourcePath() {
		return BaseTestCase.getGlobalResourcePath();
	}

	public static String getResourcePath() {
		return BaseTestCase.getGlobalResourcePath() + "/";
	}

	public static void initiateAdminTest() {
		copyAdminTestResource();
	}

	public static void initiateMasterDataTest() {
		copyMasterDataTestResource();
	}

	public static void initiateMimotoTest() {
		copyMimotoTestResource();
	}

	public static void initiateesignetTest() {
		copyEsignetTestResource();
	}

	public static void initiateSignupTest() {
		copySignupTestResource();
	}

	public static void initiateSyncDataTest() {
		copySyncDataTestResource();
	}

	public static void copyAdminTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.ADMIN);
	}

	public static void copyMasterDataTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.MASTERDATA);
	}

	public static void copyMimotoTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.MIMOTO);
	}

	public static void copyEsignetTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.ESIGNET);
	}

	public static void copySignupTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.SIGNUP);
	}

	public static void copySyncDataTestResource() {
		copymoduleSpecificAndConfigFile("syncdata");
	}

	public static void initiateKernelTest() {
		copymoduleSpecificAndConfigFile("kernel");
	}

	public static void initiateregProcTest() {
		copymoduleSpecificAndConfigFile("regProc");
	}

	public Object[] getYmlTestData(String ymlPath) {
		String testType = testLevel;
		final ObjectMapper mapper = new ObjectMapper();
		List<TestCaseDTO> testCaseDTOList = new ArrayList<>();

		Map<String, Map<String, Map<String, String>>> scriptsMap = new LinkedHashMap<>(loadyaml(ymlPath));

		for (String key : scriptsMap.keySet()) {
			Map<String, Map<String, String>> testCases = new LinkedHashMap<>(scriptsMap.get(key));

			if (testType.equalsIgnoreCase("smoke")) {
				testCases = testCases.entrySet().stream()
		                .filter(mapElement -> mapElement.getKey().toLowerCase().contains("smoke"))
		                .collect(Collectors.toMap(
		                    Map.Entry::getKey,
		                    Map.Entry::getValue,
		                    (e1, e2) -> e1, // Merge function (not used, just to satisfy Collectors.toMap)
		                    LinkedHashMap::new // Preserve order
		                ));
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
	protected Map<String, Map<String, Map<String, String>>> loadyaml(String path) {
		Map<String, Map<String, Map<String, String>>> scriptsMap = null;
		FileInputStream inputStream = null;
		BufferedInputStream bufferedInput = null;
		int customBufferSize = 16384; // 16 KB
		try {
			inputStream = new FileInputStream(new File(getResourcePath() + path).getAbsoluteFile());
			bufferedInput = new BufferedInputStream(inputStream, customBufferSize);

			// Force YAML to use LinkedHashMap
			Yaml yaml = new Yaml(new Constructor(LinkedHashMap.class));
			scriptsMap = yaml.loadAs(bufferedInput, LinkedHashMap.class);

		} catch (Exception e) {
			logger.error("Error loading YAML: " + e.getMessage());
		} finally {
			closeInputStream(inputStream);
		}
		return scriptsMap;
	}

	public String getJsonFromTemplate(String input, String template) {
		return getJsonFromTemplate(input, template, true);

	}

	public static Handlebars handlebars = new Handlebars();
	public static Gson gson = new Gson();

	public String getJsonFromTemplate(String input, String template, boolean readFile) {
		String resultJson = null;
		try {
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> map = gson.fromJson(input, type);
			String templateJsonString;
			if (readFile) {
				templateJsonString = new String(Files.readAllBytes(Paths.get(getResourcePath() + template + ".hbs")),
						StandardCharsets.UTF_8);
			} else {
				templateJsonString = template;
			}

			Template compiledTemplate = handlebars.compileInline(templateJsonString);
			Context context = Context.newBuilder(map).build();
			resultJson = compiledTemplate.apply(context);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return resultJson;
	}

	public String getJsonFromTemplateForMapApi(String input, String inputTemplate, Map<String, String> map) {
		logger.info(inputTemplate);
		logger.info(input);

		List<String> requiredRoles = getRolesByUser(input.split(":")[1].split("}")[0]);
		keycloakUsersMap.clear();
		org.json.simple.JSONArray josnArray = new org.json.simple.JSONArray();
		for (String role : requiredRoles) {
			org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
			if (map.containsKey(role)) {
				jsonObject.put("id", map.get(role));
				jsonObject.put("name", role);
				josnArray.add(jsonObject);
			}
		}
		logger.info(josnArray.toString());
		return josnArray.toString();

	}

	public List<String> getRolesByUser(String username) {

		return List.of(ConfigManager.getproperty("ROLES." + username.replaceAll(" ", "")).split(","));

	}

	public String uriKeyWordHandelerUri(String uri, String testCaseName) {
		if (uri == null) {
			logger.info(" Request Json String is :" + uri);
			return uri;
		}
		if (uri.contains(GlobalConstants.KEYCLOAK_USER_1))
			uri = uri.replace(GlobalConstants.KEYCLOAK_USER_1, ConfigManager.getproperty("KEYCLOAKUSER1"));
		if (uri.contains(GlobalConstants.KEYCLOAK_USER_2))
			uri = uri.replace(GlobalConstants.KEYCLOAK_USER_2, ConfigManager.getproperty("KEYCLOAKUSER2"));
		if (uri.contains(GlobalConstants.MODULENAME)) {
			uri = uri.replace(GlobalConstants.MODULENAME, BaseTestCase.certsForModule);
		}

		if (uri.contains(GlobalConstants.CERTSDIR)) {
			uri = uri.replace(GlobalConstants.CERTSDIR, ConfigManager.getauthCertsPath());
		}

		if (uri.contains(GlobalConstants.TRANSACTION_ID)) {
			uri = uri.replace(GlobalConstants.TRANSACTION_ID, TRANSACTION_ID);
		}

		if (uri.contains("$ID:")) {
			uri = replaceIdWithAutogeneratedId(uri, "$ID:", testCaseName);
		}

		return uri;
	}

	private static String removeSuffixUnderscores(String input, String[] suffixes) {
		for (String suffix : suffixes) {
			if (input.endsWith(suffix)) {
				// Remove underscores from the suffix
				String modifiedSuffix = suffix.replace("_", "");
				// Replace the original suffix in the input string with the modified one
				return input.substring(0, input.length() - suffix.length()) + modifiedSuffix;
			}
		}
		return input; // Return the original input if no suffix matches
	}

	public static String getTestCaseIDFromKeyword(String keyword) {

		String[] suffixes = { "time_slot_from$", "appointment_date$", "access_token$" };

		keyword = removeSuffixUnderscores(keyword, suffixes);

		// Find the index of the last underscore
		int lastUnderscoreIndex = keyword.lastIndexOf("_");

		// Extract substring before the last underscore
		String result = keyword.substring(0, lastUnderscoreIndex);

		result = result.replace("$ID:", "");

		String tempString = getTestCaseUniqueIdentifier(result);

		if (tempString == null || tempString.isBlank()) {
			return result;
		}

		return tempString;
	}

	public static String replaceKeywordWithValue(String jsonString, String keyword, String value, String testCaseName) {
		if (value != null && !value.isEmpty()) {
			RemoveFromTheConsumersMap(keyword, testCaseName);
			return jsonString.replace(keyword, value);
		}
		else {
			if (keyword.contains("$ID:"))
				throw new SkipException("Marking testcase as skipped as required field is empty " + keyword
						+ " please check the results of testcase: " + getTestCaseIDFromKeyword(keyword));
			else
				throw new SkipException("Marking testcase as skipped as required field is empty " + keyword);

		}

	}

	public static String addIdentityPassword = "";
	public static String addIdentitySalt = "";

	public String inputJsonKeyWordHandeler(String jsonString, String testCaseName) {
		if (jsonString == null) {
			logger.info(" Request Json String is :" + jsonString);
			return jsonString;
		}

		if (jsonString.contains(GlobalConstants.MODULENAME)) {
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.MODULENAME, BaseTestCase.certsForModule, testCaseName);
		}
		if (jsonString.contains(GlobalConstants.CERTSDIR)) {
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.CERTSDIR,
					ConfigManager.getauthCertsPath(), testCaseName);
		}

		if (jsonString.contains("$BIOVALUE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUE$",
					BiometricDataProvider.getFromBiometricMap("BioValue"), testCaseName);
		}
		if (jsonString.contains("$BIOVALUEWITHOUTFACE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUEWITHOUTFACE$",
					BiometricDataProvider.getFromBiometricMap("BioValueWithoutFace"), testCaseName);
		}
		if (jsonString.contains(GlobalConstants.TIMESTAMP))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp(), testCaseName);
		if (jsonString.contains("$EXPIRYTIMESTAMP$"))
			jsonString = replaceKeywordWithValue(jsonString, "$EXPIRYTIMESTAMP$", generateExpiryUTCTimeStamp(), testCaseName);
		if (jsonString.contains(GlobalConstants.TRANSACTION_ID))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.TRANSACTION_ID, TRANSACTION_ID, testCaseName);
		if (jsonString.contains("$DATESTAMP$"))
			jsonString = replaceKeywordWithValue(jsonString, "$DATESTAMP$", generateCurrentUTCDateStamp(), testCaseName);
		if (jsonString.contains("$TIMESTAMPL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$TIMESTAMPL$", generateCurrentLocalTimeStamp(), testCaseName);
		if (jsonString.contains("$RID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RID$", genRid, testCaseName);

		if (jsonString.contains("$SCHEMAVERSION$"))
			jsonString = replaceKeywordWithValue(jsonString, "$SCHEMAVERSION$", generateLatestSchemaVersion(), testCaseName);

		if (jsonString.contains("$NRCID$")) {
			String nrcId = (100000 + new Random().nextInt(900000)) + "/" + (10 + new Random().nextInt(90)) + "/"
					+ (1 + new Random().nextInt(9));

			jsonString = replaceKeywordWithValue(jsonString, "$NRCID$", nrcId, testCaseName);
		}

		if (jsonString.contains("$1STLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$1STLANG$", BaseTestCase.languageList.get(0), testCaseName);
		if (jsonString.contains("$2NDLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$2NDLANG$", BaseTestCase.languageList.get(1), testCaseName);

		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_1))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_1,
					ConfigManager.getproperty("KEYCLOAKUSER1"), testCaseName);
		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_2))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_2,
					ConfigManager.getproperty("KEYCLOAKUSER2"),testCaseName);
		if (jsonString.contains("$RIDDEL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RIDDEL$", genRidDel, testCaseName);
		if (jsonString.contains("$ID:")) {
			jsonString = replaceIdWithAutogeneratedId(jsonString, "$ID:", testCaseName);
		}
		if (jsonString.contains("$POLICYGROUPDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYGROUPDESC$", genPolicyGroupDesc, testCaseName);

		if (jsonString.contains("$POLICYGROUPNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYGROUPNAME$", genPolicyGroupName, testCaseName);

		if (jsonString.contains("$POLICYDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYDESC$", genPolicyDesc, testCaseName);

		if (jsonString.contains("$POLICYDESC1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYDESC1$", genPolicyDesc + "pol", testCaseName);

		if (jsonString.contains("$POLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAME$", genPolicyName, testCaseName);

		if (jsonString.contains("$POLICYNAMENONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAMENONAUTH$", genPolicyNameNonAuth, testCaseName);

		if (jsonString.contains("$POLICYNAME1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAME1$", genPolicyName + "pold", testCaseName);

		if (jsonString.contains(GlobalConstants.PARTNER_ID))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.PARTNER_ID, genPartnerName, testCaseName);

		if (jsonString.contains("$PARTNERIDFORDSL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERIDFORDSL$", genPartnerNameForDsl, testCaseName);

		if (jsonString.contains("$PARTNERID1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERID1$", genPartnerName + "2n", testCaseName);

		if (jsonString.contains("$PARTNERIDNONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERIDNONAUTH$", genPartnerNameNonAuth, testCaseName);

		if (jsonString.contains("$RANDOMPARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPARTNEREMAIL$",
					"automation" + generateRandomNumberString(15) + GlobalConstants.MOSIP_NET, testCaseName);

		if (jsonString.contains("$PARTNEREMAIL1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAIL1$", "12d" + genPartnerEmail, testCaseName);

		if (jsonString.contains("$PARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAIL$", genPartnerEmail, testCaseName);

		if (jsonString.contains("$PARTNEREMAILFORDSL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAILFORDSL$", genPartnerEmailForDsl, testCaseName);

		if (jsonString.contains("$PARTNEREMAILNONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAILNONAUTH$", genPartnerEmailNonAuth, testCaseName);

		if (jsonString.contains("$MISPPOLICYGROUPDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYGROUPDESC$", genMispPolicyGroupDesc, testCaseName);

		if (jsonString.contains("$MISPPOLICYGROUPNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYGROUPNAME$", genMispPolicyGroupName, testCaseName);

		if (jsonString.contains("$MISPPOLICYDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYDESC$", genMispPolicyDesc, testCaseName);

		if (jsonString.contains("$MISPPOLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYNAME$", genMispPolicyName, testCaseName);

		if (jsonString.contains("$RANDOMPOLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPOLICYNAME$",
					generateRandomAlphaNumericString(15), testCaseName);

		if (jsonString.contains("$RANDOMPARTNERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPARTNERID$", generateRandomAlphaNumericString(15), testCaseName);

		if (jsonString.contains("$MISPPARTNERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNERID$", genMispPartnerName, testCaseName);

		if (jsonString.contains("$MISPPARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNEREMAIL$", genMispPartnerEmail, testCaseName);

		if (jsonString.contains("$ZONE_CODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$ZONE_CODE$", ZonelocationCode, testCaseName);
		if (jsonString.contains("$USERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$USERID$",
					BaseTestCase.currentModule + ConfigManager.getproperty("admin_userName"), testCaseName);

		if (jsonString.contains("$LEAF_ZONE_CODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$LEAF_ZONE_CODE$", leafZoneCode, testCaseName);

		if (jsonString.contains("$LOCATIONCODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$LOCATIONCODE$", locationCode, testCaseName);

		// Need to handle int replacement
		if (jsonString.contains("$HIERARCHYLEVEL$")) {
			getLocationData();
			jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYLEVEL$", String.valueOf(hierarchyLevel), testCaseName);
		}

		if (jsonString.contains("$HIERARCHYNAME$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYNAME$", hierarchyName, testCaseName);
		}

		if (jsonString.contains("$PARENTLOCCODE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PARENTLOCCODE$", parentLocCode, testCaseName);
		}

		if (jsonString.contains("$LOCATIONNAME$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$LOCATIONNAME$", locationName, testCaseName);
		}

		if (jsonString.contains("$HIERARCHYLEVELWITHLOCATIONCODE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYLEVELWITHLOCATIONCODE$",
					String.valueOf(hierarchyLevelWithLocationCode), testCaseName);
		}

		if (jsonString.contains("$CACERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.PARTNERID)) {
				partnerId = request.get(GlobalConstants.PARTNERID).toString();
				request.remove(GlobalConstants.PARTNERID);
			}
			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId,
					GlobalConstants.RELYING_PARTY);
			String caFtmCertValue = certificateValue.getString("caCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				caFtmCertValue = caFtmCertValue.replaceAll("\r\n", "\\\\n");
			} else {
				caFtmCertValue = caFtmCertValue.replaceAll("\n", "\\\\n");
			}

			jsonString = replaceKeywordWithValue(jsonString, "$CACERT$", caFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$MISPCACERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.PARTNERID)) {
				partnerId = request.get(GlobalConstants.PARTNERID).toString();
				request.remove(GlobalConstants.PARTNERID);
			}
			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId, "MISP");
			String caFtmCertValue = certificateValue.getString("caCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				caFtmCertValue = caFtmCertValue.replaceAll("\r\n", "\\\\n");
			} else {
				caFtmCertValue = caFtmCertValue.replaceAll("\n", "\\\\n");
			}

			jsonString = replaceKeywordWithValue(jsonString, "$MISPCACERT$", caFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$INTERCERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.PARTNERID)) {
				partnerId = request.get(GlobalConstants.PARTNERID).toString();
				request.remove(GlobalConstants.PARTNERID);
			}

			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId,
					GlobalConstants.RELYING_PARTY);
			String interFtmCertValue = certificateValue.getString("interCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				interFtmCertValue = interFtmCertValue.replaceAll("\r\n", "\\\\n");
			} else {
				interFtmCertValue = interFtmCertValue.replaceAll("\n", "\\\\n");
			}

			jsonString = replaceKeywordWithValue(jsonString, "$INTERCERT$", interFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$MISPINTERCERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.PARTNERID)) {
				partnerId = request.get(GlobalConstants.PARTNERID).toString();
				request.remove(GlobalConstants.PARTNERID);
			}
			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId, "MISP");
			String interFtmCertValue = certificateValue.getString("interCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				interFtmCertValue = interFtmCertValue.replaceAll("\r\n", "\\\\n");
			} else {
				interFtmCertValue = interFtmCertValue.replaceAll("\n", "\\\\n");
			}

			jsonString = replaceKeywordWithValue(jsonString, "$MISPINTERCERT$", interFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$PARTNERCERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.REQUEST)) {
				partnerId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.PARTNERID).toString();
			}

			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId,
					GlobalConstants.RELYING_PARTY);
			String partnerFtmCertValue = certificateValue.getString("partnerCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				partnerFtmCertValue = partnerFtmCertValue.replaceAll("\r\n", "\\\\r\\\\n");
			} else {
				partnerFtmCertValue = partnerFtmCertValue.replaceAll("\n", "\\\\r\\\\n");
			}
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERCERT$", partnerFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$MISPPARTNERCERT$")) {
			JSONObject request = new JSONObject(jsonString);
			String partnerId = null;
			if (request.has(GlobalConstants.REQUEST)) {
				partnerId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.PARTNERID).toString();
			}
			JSONObject certificateValue = PartnerRegistration.getDeviceCertificates(partnerId, "MISP");
			String partnerFtmCertValue = certificateValue.getString("partnerCertificate");

			if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
				partnerFtmCertValue = partnerFtmCertValue.replaceAll("\r\n", "\\\\r\\\\n");
			} else {
				partnerFtmCertValue = partnerFtmCertValue.replaceAll("\n", "\\\\r\\\\n");
			}
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNERCERT$", partnerFtmCertValue, testCaseName);

		}

		if (jsonString.contains("$PUBLICKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PUBLICKEY$", generatePulicKey(), testCaseName);
			publickey = JsonPrecondtion.getJsonValueFromJson(jsonString, "request.publicKey");
		}
		if (jsonString.contains("$BLOCKEDPARTNERID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BLOCKEDPARTNERID$", getPartnerId(), testCaseName);
		}
		if (jsonString.contains("$APIKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$APIKEY$", getAPIKey(), testCaseName);
		}

		if (jsonString.contains("$MISPLICKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$MISPLICKEY$", getMISPLICKey(), testCaseName);
		}

		if (jsonString.contains("$IDENTITYJSON$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDENTITYJSON$", generateIdentityJson(testCaseName), testCaseName);
		}
		if (jsonString.contains("$RANDOMID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2S3", RANDOM_ID_V2_S3, testCaseName);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2S2", RANDOM_ID_V2_S2, testCaseName);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2", RANDOM_ID_V2, testCaseName);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$2", RANDOM_ID_2, testCaseName);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$", RANDOM_ID, testCaseName);
		}

		if (jsonString.contains("$RANDOMUUID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMUUID$", UUID.randomUUID().toString(), testCaseName);
		}
		if (jsonString.contains("$BASEURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BASEURI$", ApplnURI, testCaseName);
		}

		if (jsonString.contains("$DOB$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$DOB$",
					getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.date-of-birth.pattern"), testCaseName);
		}
		if (jsonString.contains("$BASE64URI$")) {
			String redirectUri = ApplnURI.replace(GlobalConstants.API_INTERNAL, GlobalConstants.RESIDENT)
					+ ConfigManager.getproperty("currentUserURI");
			jsonString = replaceKeywordWithValue(jsonString, "$BASE64URI$", encodeBase64(redirectUri), testCaseName);
		}
		if (jsonString.contains("$JWKKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$JWKKEY$", generateJWKPublicKey(), testCaseName);
		}

		if (jsonString.contains("$UINCODECHALLENGEPOS1$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$UINCODECHALLENGEPOS1$",
					GlobalMethods.sha256(UIN_CODE_VERIFIER_POS_1), testCaseName);
		}

		if (jsonString.contains("$UINCODEVERIFIERPOS1$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$UINCODEVERIFIERPOS1$", UIN_CODE_VERIFIER_POS_1, testCaseName);
		}

		if (jsonString.contains("$CODECHALLENGE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CODECHALLENGE$",
					properties.getProperty("codeChallenge"), testCaseName);
		}

		if (jsonString.contains("$CODEVERIFIER$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CODEVERIFIER$", properties.getProperty("codeVerifier"), testCaseName);
		}

		if (jsonString.contains("$VCICONTEXTURL$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$VCICONTEXTURL$",
					properties.getProperty("vciContextURL"), testCaseName);
		}

		if (jsonString.contains("$NAMEFORUPDATEUIN$")) {
			String name = getValueFromAuthActuator("json-property", "name");
			String nameResult = "";

			if (new JSONArray(name).length() > 1) {
				nameResult = new JSONArray(name).getString(0);
			} else {
				nameResult = name.replaceAll("\\[\"|\"\\]", "");
			}
			jsonString = replaceKeywordWithValue(jsonString, "$NAMEFORUPDATEUIN$", nameResult, testCaseName);
		}

		if (jsonString.contains("$UPDATEDEMAILATTR$")) {

			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");

			jsonString = replaceKeywordWithValue(jsonString, "$UPDATEDEMAILATTR$", emailResult, testCaseName);
		}

		if (jsonString.contains(GlobalConstants.IDT_TOKEN)) {

			JSONObject request = new JSONObject(jsonString);
			String idtToken = request.get(GlobalConstants.IDT_TOKEN).toString();
			request.remove(GlobalConstants.IDT_TOKEN);
			jsonString = request.toString();
			Map<String, String> map = new HashMap<>();
			map.put(GlobalConstants.TOKEN, idtToken);
			JSONObject encodingToken = new JSONObject(map);

			if (jsonString.contains("$IDTINDIVIUALID$")) {
				String individualId = getSubjectFromJwt(idtToken);
				jsonString = replaceKeywordWithValue(jsonString, "$IDTINDIVIUALID$", individualId, testCaseName);
			}

			if (jsonString.contains("$IDTCHALLENGE$")) {
				String challenge = encodeBase64(encodingToken.toString());
				jsonString = replaceKeywordWithValue(jsonString, "$IDTCHALLENGE$", challenge, testCaseName);
			}

		}

		if (jsonString.contains(GlobalConstants.REMOVE))
			jsonString = removeObject(new JSONObject(jsonString));

		return jsonString;
	}

	public static String generatePulicKey() {
		String publicKey = null;
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048, BaseTestCase.secureRandom);
			final KeyPair keypair = keyGenerator.generateKeyPair();
			publicKey = java.util.Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}
		return publicKey;
	}

	public static KeyPairGenerator keyPairGen = null;

	public static KeyPairGenerator getKeyPairGeneratorInstance() {
		if (keyPairGen != null)
			return keyPairGen;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);

		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		return keyPairGen;
	}

	public static String generateJWKPublicKey() {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048, BaseTestCase.secureRandom);
			final KeyPair keypair = keyGenerator.generateKeyPair();
			RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keypair.getPublic()).keyID("RSAKeyID")
					.keyUse(KeyUse.SIGNATURE).privateKey(keypair.getPrivate()).build();

			return jwk.toJSONString();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public static JSONArray getArrayFromJson(JSONObject request, String value) {

		if (request.getJSONObject(GlobalConstants.REQUEST).has(value)) {
			return request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(value);
		}

		return null;
	}

	public String getPartnerId() {
		String[] uriParts = PartnerRegistration.partnerKeyUrl.split("/");
		return uriParts[uriParts.length - 2];
	}

	public String getAPIKey() {
		String[] uriParts = PartnerRegistration.partnerKeyUrl.split("/");
		return uriParts[uriParts.length - 1];
	}

	public String getMISPLICKey() {
		String[] uriParts = PartnerRegistration.partnerKeyUrl.split("/");
		return uriParts[0];
	}

	public String updateTimestampOtp(String otpIdentyEnryptRequest, String otpChannel, String testCaseName) {
		String otp = null;

		otp = OTPListener.getOtp(otpChannel);
		logger.info("Fetched OTP for otp auth= " + otp);

		if (otp != null && !otp.isBlank()) {
			otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest, otp, "otp");
		} else {
			logger.error("Not Able To Fetch OTP From SMTP");
		}
		otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest,
				generateCurrentUTCTimeStamp(), "timestamp");
		return otpIdentyEnryptRequest;
	}

	public OutputValidationDto customStatusCodeResponse(String responseStatusCode, String expectedOutputCode) {
		OutputValidationDto customResponse = new OutputValidationDto();
		customResponse.setActualValue(responseStatusCode);
		customResponse.setExpValue(expectedOutputCode);
		customResponse.setFieldName("status");
		if (customResponse.getActualValue().equals(customResponse.getExpValue())) {
			customResponse.setStatus("PASS");
		} else {
			customResponse.setStatus(GlobalConstants.FAIL_STRING);
		}

		return customResponse;
	}

	public static String generateCurrentUTCTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static String generateExpiryUTCTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, Integer.parseInt(ConfigManager.getproperty("expirationTime")));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(calendar.getTime());
	}

	public static String generateCurrentUTCDateStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	@SuppressWarnings("unchecked")
	public String generateIdentityJson(String testCaseName) {
		org.json.simple.JSONObject actualrequest = getRequestJson(UPDATE_UIN_REQUEST);
		logger.info(actualrequest);
		org.json.simple.JSONObject identityObect = (org.json.simple.JSONObject) actualrequest
				.get(GlobalConstants.IDENTITY);
		logger.info(identityObect);
		identityObect.replace(GlobalConstants.IDSCHEMAVERSION, GlobalConstants.IDSCHEMAVERSION,
				generateLatestSchemaVersion());
		org.json.simple.JSONArray jaData = (org.json.simple.JSONArray) identityObect.get("addressLine3");
		logger.info(actualrequest);
		for (int i = 0; i < jaData.size(); i++) {
			org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) jaData.get(i);
			jsonObj.replace(GlobalConstants.LANGUAGE, GlobalConstants.LANGUAGEVALUE, BaseTestCase.languageList.get(0));
			jsonObj.replace(GlobalConstants.VALUE, "valueOfAttribute", properties.getProperty("ValuetoBeUpdate"));
		}
		logger.info(jaData);
		String idObj = (String) identityObect.get("UIN");
		String finalString = null;
		org.json.simple.JSONObject finalObject = new org.json.simple.JSONObject();
		if (!idObj.isEmpty()) {
			finalString = replaceIdWithAutogeneratedId(identityObect.toString(), "$ID:", testCaseName);
			logger.info(finalString);
			finalObject = new org.json.simple.JSONObject();
			JSONParser parser = new JSONParser();

			try {
				finalObject.put(GlobalConstants.IDENTITY, parser.parse(finalString));
			} catch (ParseException e) {
				logger.error(e.getMessage());
			}
		}
		return Base64.getEncoder().encodeToString(finalObject.toString().getBytes());
	}

	private String generateCurrentLocalTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return dateFormat.format(date);
	}

	protected static String replaceIdWithAutogeneratedId(String jsonString, String idKey, String testCaseName) {
		if (!jsonString.contains(idKey))
			return jsonString;
		String keyForIdProperty = StringUtils.substringBetween(jsonString, idKey, "$");
		String keyToReplace = "";

		// mock = email,phone; default
		// mock = phone;
		// mock = email;

		// $ID:AddIdentity_withValidParameters_smoke_Pos_EMAIL$

		// $ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$@phone

		if (keyForIdProperty.endsWith("_EMAIL")
				&& ConfigManager.getMockNotificationChannel().equalsIgnoreCase("phone")) {
			String temp = idKey + keyForIdProperty + "$"; // $ID:AddIdentity_withValidParameters_smoke_Pos_EMAIL$
			keyForIdProperty = keyForIdProperty.replace("_EMAIL", "_PHONE"); // AddIdentity_withValidParameters_smoke_Pos_PHONE
			keyToReplace = temp; // $ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$@phone

			jsonString = jsonString.replace(temp, temp + "@phone");

		} else if (keyForIdProperty.endsWith("_PHONE")
				&& ConfigManager.getMockNotificationChannel().equalsIgnoreCase("email")) {
			String temp = idKey + keyForIdProperty + "$"; // $ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$
			keyForIdProperty = keyForIdProperty.replace("_PHONE", "_EMAIL"); // AddIdentity_withValidParameters_smoke_Pos_EMAIL
			keyToReplace = temp + "@phone";
		} else {
			keyToReplace = idKey + keyForIdProperty + "$"; // AddIdentity_withValidParameters_smoke_Pos_EMAIL
		}

		if (keyForIdProperty.contains("time_slot_from")) {
			
			String time = getFromCache(keyForIdProperty, testCaseName);
			if (time.compareTo("12:00") >= 0)
				time += " PM";
			else
				time += " AM";
			jsonString = replaceKeywordWithValue(jsonString, keyToReplace, time, testCaseName);
		} else {
			if (keyForIdProperty.equals("UploadPartnerCert_Misp_Valid_Smoke_sid_signedCertificateData")) {
				String certData = getFromCache(keyForIdProperty, testCaseName);
				if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
					certData = certData.replaceAll("\n", "\\\\n");
				} else {
					certData = certData.replaceAll("\n", "\\\\n");

				}
				jsonString = replaceKeywordWithValue(jsonString, keyToReplace, certData, testCaseName);
			} else {
				jsonString = replaceKeywordWithValue(jsonString, keyToReplace,
						getFromCache(keyForIdProperty, testCaseName), testCaseName);
			}
		}
		if (jsonString.contains("\u200B")) {
			jsonString = jsonString.replaceAll("\u200B", "");
		}
		if (jsonString.contains("\\p{Cf}")) {
			jsonString = jsonString.replaceAll("\\p{Cf}", "");
		}

		jsonString = replaceIdWithAutogeneratedId(jsonString, idKey, testCaseName);

		if (jsonString.contains("\u200B")) {
			jsonString = jsonString.replaceAll("\u200B", "");
		}
		if (jsonString.contains("\\p{Cf}")) {
			jsonString = jsonString.replaceAll("\\p{Cf}", "");
		}

		return jsonString;
	}

	public String removeObject(JSONObject object) {
		List<String> keysToRemove = new ArrayList<>();
		Iterator<String> keysItr = object.keys();

		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				JSONArray updatedArray = new JSONArray();

				for (int i = 0; i < array.length(); i++) {
					Object arrayElement = array.get(i);

					// Check if array element is JSONObject
					if (arrayElement instanceof JSONObject) {
						String updatedObject = removeObject((JSONObject) arrayElement);
						updatedArray.put(new JSONObject(updatedObject));

					} else if (arrayElement instanceof String) {
						if (!arrayElement.equals(GlobalConstants.REMOVE)) {
							updatedArray.put(arrayElement);
						}

					} else if (arrayElement instanceof JSONArray) {
						updatedArray.put(new JSONArray(removeObject((JSONArray) arrayElement)));
					}
				}

				object.put(key, updatedArray);

			} else if (value instanceof JSONObject) {
				String objectContent = removeObject((JSONObject) value);
				object.put(key, new JSONObject(objectContent));

			} else if (value instanceof String && value.equals(GlobalConstants.REMOVE)) {
				keysToRemove.add(key);
			}
		}

		for (String keyToRemove : keysToRemove) {
			object.remove(keyToRemove);
		}

		return object.toString();
	}

	// Helper method to process JSONArray elements
	private String removeObject(JSONArray array) {
		JSONArray updatedArray = new JSONArray();

		for (int i = 0; i < array.length(); i++) {
			Object arrayElement = array.get(i);

			if (arrayElement instanceof JSONObject) {
				updatedArray.put(new JSONObject(removeObject((JSONObject) arrayElement)));

			} else if (arrayElement instanceof String && !arrayElement.equals(GlobalConstants.REMOVE)) {
				updatedArray.put(arrayElement);
			} else if (arrayElement instanceof JSONArray) {
				updatedArray.put(new JSONArray(removeObject((JSONArray) arrayElement)));
			}
		}

		return updatedArray.toString();
	}

	private String removeObject(JSONObject object, String tempArrayContent) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				for (int i = 0; i < array.length(); ++i) {
					String arrayContent = removeObject(new JSONObject(array.get(i).toString()));
					object.put(key, new JSONArray("[" + arrayContent + "]"));
				}
			} else if (value instanceof JSONObject) {
				String objectContent = removeObject(new JSONObject(value.toString()));
				object.put(key, new JSONObject(objectContent));
			}
			if (value.toString().equals(GlobalConstants.REMOVE)) {
				object.remove(key);
				keysItr = object.keys();
			}
		}
		return object.toString();
	}

	private String removObjectFromArray(Set<String> content) {
		String array = "[";
		for (String str : content) {
			if (!str.contains(GlobalConstants.REMOVE))
				array = array + '"' + str + '"' + ",";
		}
		array = array.substring(0, array.length() - 1);
		array = array + "]";
		return array;
	}

	protected Response postWithOnlyPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		return postWithOnlyPathParamAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response postWithOnlyPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookieAndOnlyPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookieAndOnlyPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithOnlyQueryParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) throws SecurityXSSException {
		Response response = null;
		jsonInput = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		HashMap<String, String> map = null;
		try {
			map = new Gson().fromJson(jsonInput, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + jsonInput + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.GET_REQ_STRING + url);
		GlobalMethods.reportRequest(null, jsonInput, url);
		try {
			response = RestClient.postRequestWithQueryParm(url, map, "*/*", "*/*", cookieName, token);
			// check if X-XSS-Protection is enabled or not
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
		} catch (SecurityXSSException se) {
			String responseHeadersString = (response == null) ? "No response"
					: response.getHeaders().asList().toString();
			String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
					+ "\nError: " + se.getMessage();
			logger.error(errorMessageString, se);
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	private static File updateCSV(String fileToUpdate, String replace, int row, int col) {
		File inputFile = new File(fileToUpdate);
		List<String[]> csvBody;

		try (CSVReader csvReader = new CSVReader(new FileReader(inputFile), ',');
				CSVWriter csvWriter = new CSVWriter(new FileWriter(inputFile), ',')) {
			csvBody = csvReader.readAll();
			csvBody.get(row)[col] = replace;
			csvWriter.writeAll(csvBody);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return inputFile;
	}

	public static Properties getproperty(String path) {
		Properties prop = new Properties();
		try (FileInputStream inputStream = new FileInputStream(path);) {
			prop.load(inputStream);
		} catch (IOException e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
		}
		return prop;
	}

	public String generateSignatureWithRequest(String request, String partnerId) {
		String singResponse = null;
		try {
			singResponse = sign(request, false, true, false, null, getKeysDirPath(), partnerId);
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException
				| OperatorCreationException | JoseException | IOException e) {
			logger.error(e.getMessage());
		}
		return singResponse;

	}

	/**
	 * The method will modify json request with the given field values in map
	 * 
	 * @param listOfFiles
	 * @param fieldvalue
	 * @param propFileName
	 * @param keywordinFile
	 * @return true or false
	 */
	protected String modifyRequest(String inputJson, Map<String, String> fieldvalue, String propFileNameWithPath) {
		try {
			MessagePrecondtion jsonPrecon = new JsonPrecondtion();
			return jsonPrecon.parseAndUpdateJson(inputJson, fieldvalue, propFileNameWithPath);

		} catch (Exception e) {
			logger.error("Exception occured:" + e.getMessage());
			return inputJson;
		}
	}

	public static void copyPreregTestResource() {
		copymoduleSpecificAndConfigFile("preReg");
	}

	public static void copyPrerequisiteTestResource() {
		copymoduleSpecificAndConfigFile("prerequisite");
	}

	public static void copyIdrepoTestResource() {
		copymoduleSpecificAndConfigFile("idRepository");
	}

	public static void copyResidentTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.RESIDENT);
	}

	public static void copyPmsTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.PARTNER_MANAGEMENT_SERVICE);
	}

	public static ArrayList<JSONObject> getInputTestCase(TestCaseDTO testCaseDTO) {
		String[] templateFields = testCaseDTO.getTemplateFields();
		String input = testCaseDTO.getInput();
		ArrayList<JSONObject> listofjsonObject = new ArrayList<>();
		listofjsonObject = inputJsonConversion(input, templateFields);
		return listofjsonObject;
	}

	public static ArrayList<JSONObject> getOutputTestCase(TestCaseDTO testCaseDTO) {
		String[] templateFields = testCaseDTO.getTemplateFields();
		String output = testCaseDTO.getOutput();
		ArrayList<JSONObject> listofjsonObject = new ArrayList<>();
		listofjsonObject = outputJsonConversion(output, templateFields);
		return listofjsonObject;
	}

	public static ArrayList<JSONObject> inputJsonConversion(String input, String[] templateFields) {
		JSONObject inputJson = new JSONObject(input);
		return convertJson(templateFields, input, inputJson);
	}

	public static ArrayList<JSONObject> outputJsonConversion(String output, String[] templateFields) {
		JSONObject outputJson = new JSONObject(output);
		return convertJson(templateFields, output, outputJson);
	}

	private static ArrayList<JSONObject> convertJson(String[] templateFields, String template, JSONObject jsonObject) {

		ArrayList<JSONObject> listofjsonObject = new ArrayList<>();
		ArrayList<String> list = new ArrayList<>();
		Arrays.stream(templateFields).forEach(field -> list.add(field));
		for (String language : BaseTestCase.languageList) {
			JSONObject langjson = new JSONObject(template);
			for (String fieldToConvert : list) {
				boolean isFilterRequired = false;
				boolean isDataRequired = false;
				String valueToConvert = null;
				String translatedValue = null;
				if (jsonObject.has(fieldToConvert)) {
					valueToConvert = jsonObject.getString(fieldToConvert);
					translatedValue = valueToConvert;
				} else if (jsonObject.has(GlobalConstants.FILTERS)
						&& jsonObject.getJSONArray(GlobalConstants.FILTERS).length() >= 1) {
					String filterValueToConvert = jsonObject.getJSONArray(GlobalConstants.FILTERS).get(0).toString();
					JSONObject filtervalue = new JSONObject(filterValueToConvert);
					if (filtervalue.has(fieldToConvert)) {
						valueToConvert = filtervalue.getString(fieldToConvert);
						translatedValue = valueToConvert;
						isFilterRequired = true;
					}
				} else if (jsonObject.has(GlobalConstants.KEYWORD_DATA)) {
					String filterValueToConvert = jsonObject.getJSONArray(GlobalConstants.KEYWORD_DATA).get(0)
							.toString();
					JSONObject filtervalue = new JSONObject(filterValueToConvert);
					if (filtervalue.has(fieldToConvert)) {
						valueToConvert = filtervalue.getString(fieldToConvert);
						translatedValue = valueToConvert;
						isDataRequired = true;
					}
				}

				if (!language.equalsIgnoreCase("eng") && valueToConvert != null) {
					translatedValue = Translator.translate(language, valueToConvert);
				}
				if (isFilterRequired) {
					String filterValueToConvert = jsonObject.getJSONArray(GlobalConstants.FILTERS).get(0).toString();
					JSONObject filtervalue = new JSONObject(filterValueToConvert);
					String filtervalue1 = filtervalue.toString().replace(valueToConvert, translatedValue);
					JSONObject filteredvalue = new JSONObject(filtervalue1);
					JSONArray filtertransvalue = new JSONArray();
					filtertransvalue.put(filteredvalue);
					langjson.remove(GlobalConstants.FILTERS);
					langjson.put(GlobalConstants.FILTERS, filtertransvalue);

				} else if (isDataRequired) {
					String dataValueToConvert = jsonObject.getJSONArray(GlobalConstants.KEYWORD_DATA).get(0).toString();
					JSONObject dataValue = new JSONObject(dataValueToConvert);
					String dataValue1 = dataValue.toString().replace(valueToConvert, translatedValue);
					JSONObject translatedDataValue = new JSONObject(dataValue1);
					JSONArray dataTransValue = new JSONArray();
					dataTransValue.put(translatedDataValue);
					langjson.remove(GlobalConstants.KEYWORD_DATA);
					langjson.put(GlobalConstants.KEYWORD_DATA, dataTransValue);

				} else if (!isFilterRequired && !langjson.isNull(fieldToConvert) || translatedValue != null)
					langjson.put(fieldToConvert, translatedValue);
			}

			if (langjson.has(GlobalConstants.LANG_CODE)) {
				langjson.remove(GlobalConstants.LANG_CODE);
				langjson.put(GlobalConstants.LANG_CODE, language);
			} else if (langjson.has(GlobalConstants.LANGCODE)) {
				langjson.remove(GlobalConstants.LANGCODE);
				langjson.put(GlobalConstants.LANGCODE, language);
			} else {
				langjson.remove("languageCode");
				langjson.put("languageCode", language);
			}

			logger.info(langjson.toString());
			listofjsonObject.add(langjson);
		}
		return listofjsonObject;
	}

	public String sign(String dataToSign, boolean includePayload, boolean includeCertificate, boolean includeCertHash,
			String certificateUrl, String dirPath, String partnerId)
			throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
			CertificateException, IOException, OperatorCreationException {
		KeyMgrUtil keyMgrUtil = new KeyMgrUtil();
		JsonWebSignature jwSign = new JsonWebSignature();
		PrivateKeyEntry keyEntry = keyMgrUtil.getKeyEntry(dirPath, partnerId);
		if (Objects.isNull(keyEntry)) {
			throw new KeyStoreException("Key file not available for partner type: " + partnerId);
		}

		PrivateKey privateKey = keyEntry.getPrivateKey();

		X509Certificate x509Certificate = keyMgrUtil.getCertificateEntry(dirPath, partnerId);

		if (x509Certificate == null) {
			x509Certificate = (X509Certificate) keyEntry.getCertificate();
		}

		if (includeCertificate)
			jwSign.setCertificateChainHeaderValue(new X509Certificate[] { x509Certificate });

		if (includeCertHash)
			jwSign.setX509CertSha256ThumbprintHeaderValue(x509Certificate);

		if (Objects.nonNull(certificateUrl))
			jwSign.setHeader("x5u", certificateUrl);

		jwSign.setPayload(dataToSign);
		jwSign.setAlgorithmHeaderValue(SIGN_ALGO);
		jwSign.setKey(privateKey);
		jwSign.setDoKeyValidation(false);
		if (includePayload)
			return jwSign.getCompactSerialization();

		return jwSign.getDetachedContentCompactSerialization();

	}

	public static String getKeysDirPath() {
		String certsTargetDir = System.getProperty("java.io.tmpdir") + File.separator
				+ System.getProperty("parent.certs.folder.name", "AUTHCERTS");

		if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {
			certsTargetDir = "/home/mosip/authcerts";
		}
		logger.info("Certs target path is: " + certsTargetDir + File.separator + certsForModule + "-IDA-" + environment
				+ ".mosip.net");
		return certsTargetDir + File.separator + certsForModule + "-IDA-" + environment + ".mosip.net";
	}

	public static String buildIdentityRequest(String identityRequest) {
		if (identityRequest.contains("$DATETIME$"))
			identityRequest = identityRequest.replace("$DATETIME$", generateCurrentUTCTimeStamp());
		if (identityRequest.contains(GlobalConstants.TIMESTAMP))
			identityRequest = identityRequest.replace(GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp());
		if (identityRequest.contains(GlobalConstants.TRANSACTION_ID))
			identityRequest = identityRequest.replace(GlobalConstants.TRANSACTION_ID, TRANSACTION_ID);
		if (identityRequest.contains("$FACE$"))
			identityRequest = identityRequest.replace("$FACE$",
					BiometricDataProvider.getFromBiometricMap("FaceBioValue"));
		if (identityRequest.contains("$RIGHTIRIS$"))
			identityRequest = identityRequest.replace("$RIGHTIRIS$",
					BiometricDataProvider.getFromBiometricMap("RightIrisBioValue"));
		if (identityRequest.contains("$LEFTIRIS$"))
			identityRequest = identityRequest.replace("$LEFTIRIS$",
					BiometricDataProvider.getFromBiometricMap("LeftIrisBioValue"));
		if (identityRequest.contains("$RIGHTTHUMB$"))
			identityRequest = identityRequest.replace("$RIGHTTHUMB$",
					BiometricDataProvider.getFromBiometricMap("rightThumb"));
		if (identityRequest.contains("$LEFTTHUMB$"))
			identityRequest = identityRequest.replace("$LEFTTHUMB$",
					BiometricDataProvider.getFromBiometricMap("leftThumb"));
		if (identityRequest.contains("$RIGHTLITTLEFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTLITTLEFINGER$",
					BiometricDataProvider.getFromBiometricMap("rightLittle"));
		if (identityRequest.contains("$RIGHTMIDDLEFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTMIDDLEFINGER$",
					BiometricDataProvider.getFromBiometricMap("rightMiddle"));
		if (identityRequest.contains("$RIGHTRINGFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTRINGFINGER$",
					BiometricDataProvider.getFromBiometricMap("rightRing"));
		if (identityRequest.contains("$RIGHTINDEXFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTINDEXFINGER$",
					BiometricDataProvider.getFromBiometricMap("rightIndex"));
		if (identityRequest.contains("$LEFTLITTLEFINGER$"))
			identityRequest = identityRequest.replace("$LEFTLITTLEFINGER$",
					BiometricDataProvider.getFromBiometricMap("leftLittle"));
		if (identityRequest.contains("$LEFTINDEXFINGER$"))
			identityRequest = identityRequest.replace("$LEFTINDEXFINGER$",
					BiometricDataProvider.getFromBiometricMap("leftIndex"));
		if (identityRequest.contains("$LEFTMIDDLEFINGER$"))
			identityRequest = identityRequest.replace("$LEFTMIDDLEFINGER$",
					BiometricDataProvider.getFromBiometricMap("leftMiddle"));
		if (identityRequest.contains("$LEFTRINGFINGER$"))
			identityRequest = identityRequest.replace("$LEFTRINGFINGER$",
					BiometricDataProvider.getFromBiometricMap("leftRing"));
		if (identityRequest.contains("$FACEDRAFTVALUE$"))
			identityRequest = identityRequest.replace("$FACEDRAFTVALUE$",
					BiometricDataProvider.getFromBiometricMap("FACEDRAFTVALUE"));

		return identityRequest;
	}

	public static TestCaseDTO filterHbs(TestCaseDTO testCaseDTO) {
		if (BaseTestCase.languageList.size() == 2) {
			if (Boolean.parseBoolean(properties.getProperty("V3ENV"))) {
				if (testCaseDTO.getInputTemplate().contains(GlobalConstants.LANGNUMBER))
					testCaseDTO.setInputTemplate(
							testCaseDTO.getInputTemplate().replace(GlobalConstants.LANGNUMBER, "DOUBLE_V3"));
				if (testCaseDTO.getOutputTemplate().contains(GlobalConstants.LANGNUMBER))
					testCaseDTO.setOutputTemplate(
							testCaseDTO.getOutputTemplate().replace(GlobalConstants.LANGNUMBER, "DOUBLE_V3"));
			} else {
				if (testCaseDTO.getInputTemplate().contains(GlobalConstants.LANGNUMBER))
					testCaseDTO.setInputTemplate(
							testCaseDTO.getInputTemplate().replace(GlobalConstants.LANGNUMBER, "DOUBLE"));
				if (testCaseDTO.getOutputTemplate().contains(GlobalConstants.LANGNUMBER))
					testCaseDTO.setOutputTemplate(
							testCaseDTO.getOutputTemplate().replace(GlobalConstants.LANGNUMBER, "DOUBLE"));
			}

		}

		else if (BaseTestCase.languageList.size() == 3) {
			if (testCaseDTO.getInputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO
						.setInputTemplate(testCaseDTO.getInputTemplate().replace(GlobalConstants.LANGNUMBER, "TRIPLE"));
			if (testCaseDTO.getOutputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO.setOutputTemplate(
						testCaseDTO.getOutputTemplate().replace(GlobalConstants.LANGNUMBER, "TRIPLE"));
		}

		else if (BaseTestCase.languageList.size() == 1) {
			if (testCaseDTO.getInputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO
						.setInputTemplate(testCaseDTO.getInputTemplate().replace(GlobalConstants.LANGNUMBER, "SINGLE"));
			if (testCaseDTO.getOutputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO.setOutputTemplate(
						testCaseDTO.getOutputTemplate().replace(GlobalConstants.LANGNUMBER, "SINGLE"));
		}

		else {
			if (testCaseDTO.getInputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO.setInputTemplate(
						testCaseDTO.getInputTemplate().replace(GlobalConstants.LANGNUMBER, "DEFAULT"));
			if (testCaseDTO.getOutputTemplate().contains(GlobalConstants.LANGNUMBER))
				testCaseDTO.setOutputTemplate(
						testCaseDTO.getOutputTemplate().replace(GlobalConstants.LANGNUMBER, "DEFAULT"));
		}
		return testCaseDTO;
	}

	public static List<String> getAppointmentDetails(Response fetchCenterResponse) {
		int countCenterDetails = 0;
		List<String> appointmentDetails = new ArrayList<>();
		try {
			countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, GlobalConstants.ERROR_BOOK_APPOINTMENT);
		}
		for (int i = 0; i < countCenterDetails; i++) {
			try {
				fetchCenterResponse.jsonPath()
						.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + GlobalConstants.TIMESLOTS_FROMTIME)
						.toString();
			} catch (NullPointerException e) {
				continue;
			}

			try {
				appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
				appointmentDetails.add(fetchCenterResponse.jsonPath()
						.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + "].date").toString());
				appointmentDetails.add(fetchCenterResponse.jsonPath()
						.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + GlobalConstants.TIMESLOTS_FROMTIME)
						.toString());
				appointmentDetails.add(fetchCenterResponse.jsonPath()
						.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + "].timeSlots[0].toTime").toString());
			} catch (NullPointerException e) {
				Assert.assertTrue(false, GlobalConstants.ERROR_BOOK_APPOINTMENT);
			}
			break;
		}
		return appointmentDetails;
	}

	public static List<String> getAppointmentDetailsforHoliday(Response fetchCenterResponse) {
		int countCenterDetails = 0;
		String regCenterID = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		List<String> appointmentDetails = new ArrayList<>();
		boolean addAppointmentDetails = false;
		try {
			countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, GlobalConstants.ERROR_BOOK_APPOINTMENT);
		}
		for (int i = 0; i < countCenterDetails; i++) {
			try {
				fetchCenterResponse.jsonPath()
						.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + GlobalConstants.TIMESLOTS_FROMTIME)
						.toString();
			} catch (NullPointerException e) {
				continue;
			}

			try {
				String date = fetchCenterResponse.jsonPath().get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + "].date")
						.toString();
				String dayOfWeek = LocalDate.parse(date).getDayOfWeek().toString();

				if (dayOfWeek.equals("FRIDAY")) {

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate localDate = LocalDate.parse(date, formatter);

					// Add one day to the date
					LocalDate nextDay = localDate.plusDays(1);

					// Convert back to string
					String nextDayString = nextDay.format(formatter);
					System.out.println("Next day: " + nextDayString);

					if (!fetchCenterResponse.asString().contains(nextDayString)) {
						date = nextDayString;
						addAppointmentDetails = true;
					} else {
						continue;
					}
				}
				if (dayOfWeek.equals("SATURDAY")) {
					addAppointmentDetails = true;
				}

				if (addAppointmentDetails) {
					appointmentDetails.add(regCenterID);
					appointmentDetails.add(date);
					appointmentDetails.add(fetchCenterResponse.jsonPath()
							.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + GlobalConstants.TIMESLOTS_FROMTIME)
							.toString());
					appointmentDetails.add(fetchCenterResponse.jsonPath()
							.get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + "].timeSlots[0].toTime").toString());
					break;
				}
			} catch (NullPointerException e) {
				Assert.assertTrue(false, GlobalConstants.ERROR_BOOK_APPOINTMENT);
			}
		}
		return appointmentDetails;
	}

	public static String modifyIdSchemaInputJson(String inputJson) {
		inputJson = inputJson.replace("&quot;", "\\" + "\"");
		inputJson = inputJson.replace("/", "\\" + "/");
		inputJson = inputJson.replace("\\\\", "\\\\\\\\");
		return inputJson;
	}

	public static String generateTokenID(String uin, String partnerCode) {
		try {
			String uinHash = HMACUtils2
					.digestAsPlainText((uin + properties.getProperty("mosip.kernel.tokenid.uin.salt")).getBytes());
			String hash = HMACUtils2.digestAsPlainText((properties.getProperty("mosip.kernel.tokenid.partnercode.salt")
					+ properties.getProperty("partner_Token_Id") + uinHash).getBytes());
			return new BigInteger(hash.getBytes()).toString().substring(0, 36);
		} catch (NoSuchAlgorithmException e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return partnerCode;
		}
	}

	public static String addIdentityPhoneNumber = "";

	public static String genStringAsperRegex(String regex) throws Exception {
		if (Generex.isValidPattern(regex)) {

			Generex generex = new Generex(regex);

			String randomStr = generex.random();
			// Generate all String that matches the given Regex.
			boolean bFound = false;
			do {
				bFound = false;
				if (randomStr.startsWith("^")) {
					int idx = randomStr.indexOf("^");
					randomStr = randomStr.substring(idx + 1);
					bFound = true;
				}
				if (randomStr.endsWith("$")) {
					int idx = randomStr.indexOf("$");
					randomStr = randomStr.substring(0, idx);
					bFound = true;
				}
			} while (bFound);
			return randomStr;
		}
		throw new Exception("invalid regex");

	}

	public static void getPasswordSaltFromKeyManager(String passwordForAddIdentity) {
		String url = ApplnURI + properties.getProperty("generateArgon2HashURL");

		String token = kernelAuthLib.getTokenByRole("resident");

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("inputData", passwordForAddIdentity);

		HashMap<String, Object> body = new HashMap<>();

		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		logger.info(response);

		if (response != null) {
			JSONObject responseJson = new JSONObject(response.getBody().asString());
			addIdentityPassword = responseJson.getJSONObject("response").getString("hashValue");
			addIdentitySalt = responseJson.getJSONObject("response").getString("salt");
		}

	}

	String phoneNumber = "";
	public static String phoneSchemaRegex = "";
	public static String dateOfBirthSchemaRegex = "";
	public static Double idSchemaVersion;

	public static String modifySchemaGenerateHbs() {
		return modifySchemaGenerateHbs(false);
	}

	public static String modifySchemaGenerateHbs(boolean regenerateHbs) {
		if (identityHbs != null && !regenerateHbs) {

			return identityHbs;
		}
		JSONObject requestJson = new JSONObject();
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		idSchemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		boolean emailFieldAdditionallyAdded = false;
		boolean phoneFieldAdditionallyAdded = false;
		try {
			JSONObject schemaFileJson = new JSONObject(schemaFile);
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties");
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity");
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties");
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required");

			String phone = getValueFromAuthActuator("json-property", "phone_number");
			String result = phone.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, result)) {
				requiredPropsArray.put(result);
				phoneFieldAdditionallyAdded = true;
			}
			if (identityPropsJson.has(result)) {
				phoneSchemaRegex = identityPropsJson.getJSONObject(result).getJSONArray("validators").getJSONObject(0)
						.getString("validator");
			}

			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, emailResult)) {
				requiredPropsArray.put(emailResult);
				emailFieldAdditionallyAdded = true;
			}

			requestJson.put("id", "{{id}}");
			requestJson.put("request", new HashMap<>());
			requestJson.getJSONObject("request").put("registrationId", "{{registrationId}}");
			JSONObject identityJson = new JSONObject();
			identityJson.put("UIN", "{{UIN}}");
			JSONArray handleArray = new JSONArray();
			handleArray.put("handles");

			List<String> selectedHandles = new ArrayList<>();
			// requiredPropsArray.put("functionalId");
			for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
				String eachRequiredProp = requiredPropsArray.getString(i);

				if (!identityPropsJson.has(eachRequiredProp)) {
					continue;
				}

				JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp);
				String randomValue = "";
				if (eachRequiredProp == emailResult) {
					randomValue = "shshssh";
				}
				if (eachRequiredProp == result) {
					randomValue = phoneSchemaRegex;
				}

				// Processing for TaggedListType
				if (eachPropDataJson.has("$ref")
						&& eachPropDataJson.get("$ref").toString().contains("TaggedListType")) {
					JSONArray eachPropDataArrayForHandles = new JSONArray();
					JSONObject eachValueJsonForHandles = new JSONObject();
					if (eachRequiredProp.equals(emailResult)) {
						eachValueJsonForHandles.put("value", "$EMAILVALUE$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(emailResult);

					} else if (eachRequiredProp.equals(result)) {
						eachValueJsonForHandles.put("value", "$PHONENUMBERFORIDENTITY$");
						// "tags": ":["handle"]
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(result);
					}

					else if (eachRequiredProp.equals("nrcId")) {
						eachValueJsonForHandles.put("value", "$NRCID$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add("nrcId");
					}

					else {
						eachValueJsonForHandles.put("value", "$FUNCTIONALID$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(eachRequiredProp);
					}
					eachPropDataArrayForHandles.put(eachValueJsonForHandles);
					identityJson.put(eachRequiredProp, eachPropDataArrayForHandles);

				}

				else if (eachPropDataJson.has("$ref")
						&& eachPropDataJson.get("$ref").toString().contains("simpleType")) {
					if (eachPropDataJson.has("handle")) {
						selectedHandles.add(eachRequiredProp);
					}
					JSONArray eachPropDataArray = new JSONArray();

					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
						if (BaseTestCase.getLanguageList().get(j) != null
								&& !BaseTestCase.getLanguageList().get(j).isEmpty()) {
							JSONObject eachValueJson = new JSONObject();
							eachValueJson.put("language", BaseTestCase.getLanguageList().get(j));
							if (eachRequiredProp.contains(GlobalConstants.FULLNAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + "1"));
							} else if (eachRequiredProp.contains(GlobalConstants.FIRST_NAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + 1));
							} else if (eachRequiredProp.contains(GlobalConstants.GENDER)) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp));
							} else {
								eachValueJson.put(GlobalConstants.VALUE,
										(propsMap.getProperty(eachRequiredProp) == null) ? "TEST_" + eachRequiredProp
												: propsMap.getProperty(eachRequiredProp)
														+ BaseTestCase.getLanguageList().get(j));
							}
							eachPropDataArray.put(eachValueJson);
						}
					}
					identityJson.put(eachRequiredProp, eachPropDataArray);

				} else {
					if (eachRequiredProp.equals("proofOfIdentity")) {

						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "txt");
						identityJson.getJSONObject(eachRequiredProp).put("type", "DOC001");
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					} else if (eachRequiredProp.equals("proofOfAddress")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "txt");
						identityJson.getJSONObject(eachRequiredProp).put("type", "DOC001");
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					} else if (eachRequiredProp.equals("preferredLang")) {
						identityJson.put(eachRequiredProp, "$1STLANG$");
					} else if (eachRequiredProp.equals("registrationType")) {
						identityJson.put(eachRequiredProp, genStringAsperRegex(
								eachPropDataJson.getJSONArray("validators").getJSONObject(0).getString("validator")));
					} else if (eachRequiredProp.equals(result)) {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "$PHONENUMBERFORIDENTITY$");
					} else if (eachRequiredProp.equals(emailResult)) {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "$EMAILVALUE$");
					}

					else if (eachRequiredProp.equals("nrcId")) {
						String nrcID = "$NRCID$";
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, nrcID);
					}

					else if (eachRequiredProp.equals("password")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						if (addIdentityPassword.isBlank() && addIdentitySalt.isBlank())
							getPasswordSaltFromKeyManager(PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION);
						identityJson.getJSONObject(eachRequiredProp).put("hash", addIdentityPassword);
						identityJson.getJSONObject(eachRequiredProp).put("salt", addIdentitySalt);
					} else if (eachRequiredProp.equals("individualBiometrics")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "cbeff");
						identityJson.getJSONObject(eachRequiredProp).put("version", 1);
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					} else if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					} else {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
					}
				}
			}
			if (selectedHandles != null && selectedHandles.size() >= 1) {
				setfoundHandlesInIdSchema(true);
				identityJson.put("selectedHandles", selectedHandles);
			}

			// Constructing and adding functionalIds
			JSONArray functionalIdsArray = new JSONArray();
			for (String language : BaseTestCase.getLanguageList()) {
				if (language != null && !language.isEmpty()) {
					JSONObject functionalId = new JSONObject();
					functionalId.put("value", "TEST_CITY" + language);
					functionalIdsArray.put(functionalId);
				}
			}
			// identityJson.put("functionalIds", functionalIdsArray);

			if (isElementPresent(requiredPropsArray, "individualBiometrics")) {
				JSONArray requestDocArray = new JSONArray();
				JSONObject docJson = new JSONObject();
				docJson.put("value", "{{value}}");
				docJson.put("category", "{{category}}");
				requestDocArray.put(docJson);

				requestJson.getJSONObject("request").put("documents", requestDocArray);
			}
			requestJson.getJSONObject("request").put("identity", identityJson);
			requestJson.put("requesttime", "{{requesttime}}");
			requestJson.put("version", "{{version}}");

			System.out.println(requestJson);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		identityHbs = requestJson.toString();
		return identityHbs;
	}

	public static String getSchemaURL() {
		String schemaURL = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);
		String schemaVersion = ConfigManager.getproperty(GlobalConstants.SCHEMA_VERSION);

		if (schemaVersion != null && !schemaVersion.isEmpty()) {
			schemaURL = schemaURL + "?schemaVersion=" + schemaVersion;
		}

		return schemaURL;
	}

	public static String updateIdentityHbs(boolean regenerateHbs) {
		if (updateIdentityHbs != null && !regenerateHbs) {

			return updateIdentityHbs;
		}
		JSONObject requestJson = new JSONObject();
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		idSchemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		boolean emailFieldAdditionallyAdded = false;
		boolean phoneFieldAdditionallyAdded = false;
		try {
			JSONObject schemaFileJson = new JSONObject(schemaFile);
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties");
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity");
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties");
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required");

			String phone = getValueFromAuthActuator("json-property", "phone_number");
			String result = phone.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, result)) {
				requiredPropsArray.put(result);
				phoneFieldAdditionallyAdded = true;
			}
			if (identityPropsJson.has(result)) {
				phoneSchemaRegex = identityPropsJson.getJSONObject(result).getJSONArray("validators").getJSONObject(0)
						.getString("validator");
			}

			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, emailResult)) {
				requiredPropsArray.put(emailResult);
				emailFieldAdditionallyAdded = true;
			}

			requestJson.put("id", "{{id}}");
			requestJson.put("status", "ACTIVATED");
			requestJson.put("request", new HashMap<>());
			requestJson.getJSONObject("request").put("registrationId", "{{registrationId}}");
			JSONObject identityJson = new JSONObject();
			identityJson.put("UIN", "{{UIN}}");
			JSONArray handleArray = new JSONArray();
			handleArray.put("handles");

			List<String> selectedHandles = new ArrayList<>();
			// requiredPropsArray.put("functionalId");
			for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
				String eachRequiredProp = requiredPropsArray.getString(i);

				if (!identityPropsJson.has(eachRequiredProp)) {
					continue;
				}

				JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp);
				String randomValue = "";
				if (eachRequiredProp == emailResult) {
					randomValue = "shshssh";
				}
				if (eachRequiredProp == result) {
					randomValue = phoneSchemaRegex;
				}

				// Processing for TaggedListType
				if (eachPropDataJson.has("$ref")
						&& eachPropDataJson.get("$ref").toString().contains("TaggedListType")) {
					JSONArray eachPropDataArrayForHandles = new JSONArray();
					JSONObject eachValueJsonForHandles = new JSONObject();
					if (eachRequiredProp.equals(emailResult)) {
						eachValueJsonForHandles.put("value", "$EMAILVALUE$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(emailResult);

					} else if (eachRequiredProp.equals(result)) {
						eachValueJsonForHandles.put("value", "$PHONENUMBERFORIDENTITY$");
						// "tags": ":["handle"]
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(result);
					}

					else if (eachRequiredProp.equals("nrcId")) {
						eachValueJsonForHandles.put("value", "$NRCID$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add("nrcId");
					}

					else {
						eachValueJsonForHandles.put("value", "$FUNCTIONALID$");
						eachValueJsonForHandles.put("tags", handleArray);
						selectedHandles.add(eachRequiredProp);
					}

					eachPropDataArrayForHandles.put(eachValueJsonForHandles);
					identityJson.put(eachRequiredProp, eachPropDataArrayForHandles);

				}

				else if (eachPropDataJson.has("$ref")
						&& eachPropDataJson.get("$ref").toString().contains("simpleType")) {
					if (eachPropDataJson.has("handle")) {
						selectedHandles.add(eachRequiredProp);
					}
					JSONArray eachPropDataArray = new JSONArray();

					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
						if (BaseTestCase.getLanguageList().get(j) != null
								&& !BaseTestCase.getLanguageList().get(j).isEmpty()) {
							JSONObject eachValueJson = new JSONObject();
							eachValueJson.put("language", BaseTestCase.getLanguageList().get(j));
							if (eachRequiredProp.contains(GlobalConstants.FULLNAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + "1"));
							} else if (eachRequiredProp.contains(GlobalConstants.FIRST_NAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + 1));
							} else if (eachRequiredProp.contains(GlobalConstants.GENDER)) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp));
							} else {
								eachValueJson.put(GlobalConstants.VALUE,
										(propsMap.getProperty(eachRequiredProp) == null) ? "TEST_" + eachRequiredProp
												: propsMap.getProperty(eachRequiredProp)
														+ BaseTestCase.getLanguageList().get(j));
							}
							eachPropDataArray.put(eachValueJson);
						}
					}
					identityJson.put(eachRequiredProp, eachPropDataArray);

				} else {
					if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					} else if (eachRequiredProp.equals("individualBiometrics")) {
						identityJson.remove("individualBiometrics");
					} else if (eachRequiredProp.equals(emailResult)) {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "$EMAILVALUE$");
					} else if (eachRequiredProp.equals(result)) {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "$PHONENUMBERFORIDENTITY$");
					} else if (eachRequiredProp.equals("nrcId")) {
						String nrcID = "$NRCID$";
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, nrcID);
					} else if (eachRequiredProp.equals("proofOfIdentity")) {
						identityJson.remove("proofOfIdentity");
					} else {
						if (eachPropDataJson.has("handle")) {
							selectedHandles.add(eachRequiredProp);
						}
						identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
					}
				}
			}
			if (selectedHandles != null) {
				setfoundHandlesInIdSchema(true);
				identityJson.put("selectedHandles", selectedHandles);
			}

			// Constructing and adding functionalIds
			JSONArray functionalIdsArray = new JSONArray();
			for (String language : BaseTestCase.getLanguageList()) {
				if (language != null && !language.isEmpty()) {
					JSONObject functionalId = new JSONObject();
					functionalId.put("value", "TEST_CITY" + language);
					functionalIdsArray.put(functionalId);
				}
			}
			requestJson.getJSONObject("request").put("identity", identityJson);
			requestJson.put("requesttime", "{{requesttime}}");
			requestJson.put("version", "{{version}}");

			System.out.println(requestJson);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		updateIdentityHbs = requestJson.toString();
		return updateIdentityHbs;
	}

	public static String generateLatestSchemaVersion() {
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		BigDecimal schemaVersion = schemaData.getBigDecimal(GlobalConstants.ID_VERSION);
		String latestSchemaVersion = schemaVersion.toString();
		logger.info(latestSchemaVersion);
		return latestSchemaVersion;
	}

	public static String generateHbsForUpdateDraft() {
		if (draftHbs != null) {
			return draftHbs;
		}

		JSONObject requestJson = new JSONObject();
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		logger.info(schemaVersion);
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;
		String phoneNumber = "";

		try {
			JSONObject schemaFileJson = new JSONObject(schemaFile); // jObj
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties"); // objIDJson4
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity"); // objIDJson
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties"); // objIDJson2
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required"); // objIDJson1

			String phone = getValueFromAuthActuator("json-property", "phone_number");
			String result = phone.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, result)) {
				requiredPropsArray.put(result);
			}
			if (identityPropsJson.has(result))
				phoneNumber = genStringAsperRegex(identityPropsJson.getJSONObject(result).getJSONArray("validators")
						.getJSONObject(0).getString("validator"));

			requestJson.put("id", "{{id}}");
			requestJson.put("requesttime", "{{requesttime}}");
			requestJson.put("version", "{{version}}");
			requestJson.put("request", new HashMap<>());
			requestJson.put("registrationId", "{{registrationId}}");
			JSONObject identityJson = new JSONObject();

			for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
				String eachRequiredProp = requiredPropsArray.getString(i); // objIDJson3

				JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp); // rc1

				if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("simpleType")) {

					JSONArray eachPropDataArray = new JSONArray(); // jArray

					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
						JSONObject eachValueJson = new JSONObject(); // studentJSON
						eachValueJson.put("language", BaseTestCase.getLanguageList().get(j));
						eachValueJson.put("value",
								(propsMap.getProperty(eachRequiredProp) == null) ? "TEST_" + eachRequiredProp
										: propsMap.getProperty(eachRequiredProp));
						eachPropDataArray.put(eachValueJson);
					}
					identityJson.put(eachRequiredProp, eachPropDataArray);
				} else {
					if (eachRequiredProp.equals("proofOfIdentity")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "txt");
						identityJson.getJSONObject(eachRequiredProp).put("type", "DOC001");
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					}

					else if (eachRequiredProp.equals("nrcId")) {
						String nrcID = "$NRCID$";
						identityJson.put(eachRequiredProp, nrcID);
					}

					else if (eachRequiredProp.equals("individualBiometrics")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "cbeff");
						identityJson.getJSONObject(eachRequiredProp).put("version", 1);
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					} else if (eachRequiredProp.equals("password")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("hash", addIdentityPassword);
						identityJson.getJSONObject(eachRequiredProp).put("salt", addIdentitySalt);
					} else if (eachRequiredProp.equals("preferredLang")) {
						identityJson.put(eachRequiredProp, "$1STLANG$");
					} else if (eachRequiredProp.equals("registrationType")) {
						identityJson.put(eachRequiredProp, genStringAsperRegex(
								eachPropDataJson.getJSONArray("validators").getJSONObject(0).getString("validator")));
					} else if (eachRequiredProp.equals(result)) {
//						String regexPattern = genStringAsperRegex(eachPropDataJson.getJSONArray("validators").getJSONObject(0).getString("validator"));
//						if (regexPattern != null)
						if (phoneNumber != null)
							identityJson.put(eachRequiredProp, phoneNumber);
					}

					else if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					}

					else if (eachRequiredProp.equals("proofOfAddress")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "txt");
						identityJson.getJSONObject(eachRequiredProp).put("type", "DOC001");
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					}

					else {
						identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
					}
				}
			}

			if (isElementPresent(requiredPropsArray, "individualBiometrics")) {
				JSONArray requestDocArray = new JSONArray();
				JSONObject docJson = new JSONObject();
				docJson.put("value", "{{value}}");
				docJson.put("category", "{{category}}");
				requestDocArray.put(docJson);

				requestJson.getJSONObject("request").put("documents", requestDocArray);
			}
			requestJson.getJSONObject("request").put("identity", identityJson);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		draftHbs = requestJson.toString();
		return draftHbs;
	}

	public static String generateHbsForPrereg(boolean isItUpdate) {
		if (isItUpdate && preregHbsForUpdate != null) {
			return preregHbsForUpdate;
		}

		if (!isItUpdate && preregHbsForCreate != null) {
			return preregHbsForCreate;
		}
		JSONObject requestJson = new JSONObject();
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		logger.info(schemaVersion);
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		try {

			JSONObject schemaFileJson = new JSONObject(schemaFile); // jObj
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties"); // objIDJson4
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity"); // objIDJson
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties"); // objIDJson2
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required"); // objIDJson1

			boolean emailFieldAdditionallyAdded = false;
			boolean phoneFieldAdditionallyAdded = false;
			String phone = getValueFromAuthActuator("json-property", "phone_number");
			String result = phone.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, result)) {
				requiredPropsArray.put(result);
				phoneFieldAdditionallyAdded = true;
			}

			// System.out.println("result is:" + result);
			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");
			if (!isElementPresent(requiredPropsArray, emailResult)) {
				requiredPropsArray.put(emailResult);
				emailFieldAdditionallyAdded = true;
			}

			ArrayList<String> list = new ArrayList<>();

			if (requiredPropsArray != null) {
				int len = requiredPropsArray.length();
				for (int i = 0; i < len; i++) {
					list.add(requiredPropsArray.get(i).toString());
				}
			}
			list.remove(GlobalConstants.RESIDENCESTATUS);
			list.remove("addressCopy");
			list.remove("proofOfAddress");
			list.remove(GlobalConstants.RESIDENCESTATUS);
			list.add(GlobalConstants.RESIDENCESTATUS);
			if (list.contains(GlobalConstants.PROOFOFIDENTITY)) {
				list.remove(GlobalConstants.PROOFOFIDENTITY);
			}

			if (list.contains(GlobalConstants.INDIVIDUALBIOMETRICS)) {
				list.remove(GlobalConstants.INDIVIDUALBIOMETRICS);
			}

			JSONArray newIdJson = new JSONArray(list);

			requestJson.put("id", "{{id}}");
			if (isItUpdate) {
				requestJson.put("preRegistrationId", "{{preRegistrationId}}");
			}

			requestJson.put("requesttime", "{{requesttime}}");
			requestJson.put("version", "{{version}}");
			requestJson.put("request", new HashMap<>());
			requestJson.getJSONObject("request").put("langCode", "{{langCode}}");
			requestJson.getJSONObject("request").put("requiredFields", newIdJson);
			requestJson.getJSONObject("request").put("demographicDetails", new HashMap<>());

			JSONObject identityJson = new JSONObject();

			for (int i = 0, size = newIdJson.length(); i < size; i++) {
				String eachRequiredProp = newIdJson.getString(i); // objIDJson3

				JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp); // rc1

				if ((eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("simpleType"))
						|| eachRequiredProp.contains(GlobalConstants.RESIDENCESTATUS)) {

					JSONArray eachPropDataArray = new JSONArray(); // jArray

					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
						JSONObject eachValueJson = new JSONObject(); // studentJSON
						eachValueJson.put("language", BaseTestCase.getLanguageList().get(j));
						eachValueJson.put("value",
								(propsMap.getProperty(eachRequiredProp) == null) ? "TEST_" + eachRequiredProp
										: propsMap.getProperty(eachRequiredProp));
						eachPropDataArray.put(eachValueJson);
					}
					identityJson.put(eachRequiredProp, eachPropDataArray);
				} else {

					if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					} else {
						identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
					}
				}
			}
			requestJson.getJSONObject("request").getJSONObject("demographicDetails").put("identity", identityJson);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}
		if (isItUpdate) {

			preregHbsForUpdate = requestJson.toString();
			return preregHbsForUpdate;
		}
		preregHbsForCreate = requestJson.toString();
		return preregHbsForCreate;
	}

	@SuppressWarnings("unchecked")
	public static void createAndPublishPolicy() {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group");
		modifiedReq.put("name", policyGroup);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);
		actualrequest.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response2 = RestClient.postRequestWithCookie(url2, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody2 = response2.getBody().asString();
		policygroupId = new org.json.JSONObject(responseBody2).getJSONObject(GlobalConstants.RESPONSE).getString("id");

		String urlForUpdate = ApplnURI + properties.getProperty("authPolicyUrl") + "/" + policygroupId;
		Response responseForUpdate = RestClient.putRequestWithCookie(urlForUpdate, actualrequest,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(AUTH_POLICY_REQUEST_ATTR);

		actualrequest2.put("name", policyName);
		actualrequest2.put("policyGroupName", policyGroup);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);
		actualrequestBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response = RestClient.postRequestWithCookie(url, actualrequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody = response.getBody().asString();
		String policyId = new org.json.JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE).getString("id");

		String url3 = ApplnURI + properties.getProperty("publishPolicyurl");

		if (url3.contains("POLICYID")) {
			url3 = url3.replace("POLICYID", policyId);
			url3 = url3.replace("POLICYGROUPID", policygroupId);

		}

		Response response3 = RestClient.postRequestWithCookie(url3, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

	}

	@SuppressWarnings("unchecked")
	public static void createEditAndPublishPolicy() {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group for update");
		modifiedReq.put("name", policyGroup);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);
		actualrequest.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		String urlForUpdate = ApplnURI + properties.getProperty("authPolicyUrl") + "/" + policygroupId;
		Response responseForUpdate = RestClient.postRequestWithCookie(urlForUpdate, actualrequest,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY1);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST1);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(AUTH_POLICY_REQUEST_ATTR1);

		actualrequest2.put("name", policyNameForUpdate);
		actualrequest2.put("policyGroupName", policyGroup);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);
		actualrequestBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response = RestClient.postRequestWithCookie(url, actualrequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody = response.getBody().asString();
		updatedPolicyId = new org.json.JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE).getString("id");

		String url3 = ApplnURI + properties.getProperty("publishPolicyurl");

		if (url3.contains("POLICYID")) {
			url3 = url3.replace("POLICYID", updatedPolicyId);
			url3 = url3.replace("POLICYGROUPID", policygroupId);

		}

		Response response3 = RestClient.postRequestWithCookie(url3, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

	}

	@SuppressWarnings("unchecked")
	public static void createAndPublishPolicyForKyc() {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group");
		modifiedReq.put("name", policyGroup2);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);
		actualrequest.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response2 = RestClient.postRequestWithCookie(url2, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody2 = response2.getBody().asString();
		String policygroupId2 = new org.json.JSONObject(responseBody2).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(AUTH_POLICY_REQUEST_ATTR);

		actualrequest2.put("name", policyName2);
		actualrequest2.put("policyGroupName", policyGroup2);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);
		actualrequestBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response = RestClient.postRequestWithCookie(url, actualrequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody = response.getBody().asString();
		String policyId2 = new org.json.JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");

		String url3 = ApplnURI + properties.getProperty("publishPolicyurl");

		if (url3.contains("POLICYID")) {
			url3 = url3.replace("POLICYID", policyId2);
			url3 = url3.replace("POLICYGROUPID", policygroupId2);

		}

		Response response3 = RestClient.postRequestWithCookie(url3, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

	}

	public static void writeFileAsString(File fileName, String content) {

		try {
			FileUtils.touch(fileName);// File got created
			FileUtils.writeStringToFile(fileName, content, StandardCharset.UTF_8.name());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static Certificate convertToCertificate(String certData) {
		ByteArrayInputStream bIS = null;
		try {
			StringReader strReader = new StringReader(certData);
			PemReader pemReader = new PemReader(strReader);
			PemObject pemObject = pemReader.readPemObject();
			if (Objects.isNull(pemObject)) {
				return null;
			}
			byte[] certBytes = pemObject.getContent();
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			bIS = new ByteArrayInputStream(certBytes);
			return certFactory.generateCertificate(bIS);
		} catch (IOException | CertificateException e) {
			return null;
		} finally {
			closeByteArrayInputStream(bIS);
		}
	}

	private static String otpExpTime = "";

	public static int getOtpExpTimeFromActuator() {
		if (otpExpTime.isEmpty()) {
			String section = "/mosip-config/application-default.properties";
			Response response = null;
			org.json.JSONObject responseJson = null;
			JSONArray responseArray = null;
			String url = ApplnURI + ConfigManager.getproperty("actuatorIDAEndpoint");
			try {
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new org.json.JSONObject(response.getBody().asString());
				responseArray = responseJson.getJSONArray("propertySources");

				for (int i = 0, size = responseArray.length(); i < size; i++) {
					org.json.JSONObject eachJson = responseArray.getJSONObject(i);
					logger.info("eachJson is :" + eachJson.toString());
					if (eachJson.get("name").toString().contains(section)) {

						org.json.JSONObject otpExpiryTime = (org.json.JSONObject) eachJson
								.getJSONObject(GlobalConstants.PROPERTIES).get("mosip.kernel.otp.expiry-time");
						otpExpTime = otpExpiryTime.getString(GlobalConstants.VALUE);
						if (ConfigManager.IsDebugEnabled())
							logger.info("Actuator: " + url + " otpExpTime: " + otpExpTime);
						break;
					}
				}
			} catch (Exception e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			}
		}
		return Integer.parseInt(otpExpTime);
	}

	public static JSONArray residentActuatorResponseArray = null;

	public static String getValueFromActuator(String section, String key) {
		String url = ApplnURI + ConfigManager.getproperty("actuatorEndpoint");
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		try {
			if (residentActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				residentActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}
			for (int i = 0, size = residentActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = residentActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(section)) {
					value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
							.get(GlobalConstants.VALUE).toString();
					if (ConfigManager.IsDebugEnabled())
						logger.info("Actuator: " + url + " key: " + key + " value: " + value);
					break;
				}
			}
			actuatorValueCache.put(actuatorCacheKey, value);

			return value;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return "";
		}

	}

	public static JSONArray getActiveProfilesFromActuator(String url, String key) {
		JSONArray activeProfiles = null;

		try {
			Response response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			JSONObject responseJson = new JSONObject(response.getBody().asString());

			// If the key exists in the response, return the associated JSONArray
			if (responseJson.has(key)) {
				activeProfiles = responseJson.getJSONArray(key);
			} else {
				logger.warn("The key '" + key + "' was not found in the response.");
			}

		} catch (Exception e) {
			// Handle other errors like network issues, etc.
			logger.error("Error fetching active profiles from the actuator: " + e.getMessage());
		}

		return activeProfiles;
	}

	public static JSONArray esignetActiveProfiles = null;

	public static JSONArray esignetActuatorResponseArray = null;

	public static String getValueFromEsignetActuator(String section, String key) {
		String value = null;

		// Try to fetch profiles if not already fetched
		if (esignetActiveProfiles == null || esignetActiveProfiles.length() == 0) {
			esignetActiveProfiles = getActiveProfilesFromActuator(GlobalConstants.ESIGNET_ACTUATOR_URL,
					GlobalConstants.ACTIVE_PROFILES);
		}

		// Normalize the key
		String keyForEnvVariableSection = key.toUpperCase().replace("-", "_").replace(".", "_");

		// Try fetching the value from different sections
		value = getValueFromEsignetActuator(GlobalConstants.SYSTEM_ENV_SECTION, keyForEnvVariableSection,
				GlobalConstants.ESIGNET_ACTUATOR_URL);

		// Fallback to other sections if value is not found
		if (value == null) {
			value = getValueFromEsignetActuator(GlobalConstants.CLASS_PATH_APPLICATION_PROPERTIES, key,
					GlobalConstants.ESIGNET_ACTUATOR_URL);
		}

		if (value == null) {
			value = getValueFromEsignetActuator(GlobalConstants.CLASS_PATH_APPLICATION_DEFAULT_PROPERTIES, key,
					GlobalConstants.ESIGNET_ACTUATOR_URL);
		}

		// Try profiles from active profiles if available
		if (value == null) {
			if (esignetActiveProfiles != null && esignetActiveProfiles.length() > 0) {
				for (int i = 0; i < esignetActiveProfiles.length(); i++) {
					String propertySection = esignetActiveProfiles.getString(i).equals(GlobalConstants.DEFAULT_STRING)
							? GlobalConstants.MOSIP_CONFIG_APPLICATION_HYPHEN_STRING
									+ esignetActiveProfiles.getString(i) + GlobalConstants.DOT_PROPERTIES_STRING
							: esignetActiveProfiles.getString(i) + GlobalConstants.DOT_PROPERTIES_STRING;

					value = getValueFromEsignetActuator(propertySection, key, GlobalConstants.ESIGNET_ACTUATOR_URL);

					if (value != null) {
						break;
					}
				}
			} else {
				logger.warn("No active profiles were retrieved.");
			}
		}

		// Fallback to a default section
		if (value == null) {
			value = getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(), key,
					GlobalConstants.ESIGNET_ACTUATOR_URL);
		}

		// Final fallback to the original section if no value was found
		if (value == null) {
			value = getValueFromEsignetActuator(section, key, GlobalConstants.ESIGNET_ACTUATOR_URL);
		}

		// Log the final result or an error message if not found
		if (value == null) {
			logger.error("Value not found for section: " + section + ", key: " + key);
		}

		return value;
	}

	public static String getValueFromEsignetActuator(String section, String key, String url) {
		// Combine the cache key to uniquely identify each request
		String actuatorCacheKey = url + section + key;

		// Check if the value is already cached
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null) {
			return value; // Return cached value if available
		}

		try {
			// Fetch the actuator response array if it's not already populated
			if (esignetActuatorResponseArray == null) {
				Response response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				JSONObject responseJson = new JSONObject(response.getBody().asString());
				esignetActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}

			// Loop through the "propertySources" to find the matching section and key
			for (int i = 0, size = esignetActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = esignetActuatorResponseArray.getJSONObject(i);
				// Check if the section matches
				if (eachJson.get("name").toString().contains(section)) {
					// Get the value from the properties object
					JSONObject properties = eachJson.getJSONObject(GlobalConstants.PROPERTIES);
					if (properties.has(key)) {
						value = properties.getJSONObject(key).get(GlobalConstants.VALUE).toString();
						// Log the value if debug is enabled
						if (ConfigManager.IsDebugEnabled()) {
							logger.info("Actuator: " + url + " key: " + key + " value: " + value);
						}
						break; // Exit the loop once the value is found
					} else {
						logger.warn("Key '" + key + "' not found in section '" + section + "'.");
					}
				}
			}

			// Cache the retrieved value for future lookups
			if (value != null) {
				actuatorValueCache.put(actuatorCacheKey, value);
			} else {
				logger.warn("No value found for section: " + section + ", key: " + key);
			}

			return value;
		} catch (JSONException e) {
			// Handle JSON parsing exceptions separately
			logger.error("JSON parsing error for section: " + section + ", key: " + key + " - " + e.getMessage());
			return null; // Return null if JSON parsing fails
		} catch (Exception e) {
			// Catch any other exceptions (e.g., network issues)
			logger.error("Error fetching value for section: " + section + ", key: " + key + " - " + e.getMessage());
			return null; // Return null if any other exception occurs
		}
	}

	public static JSONArray authActuatorResponseArray = null;

	public static String getValueFromAuthActuator(String section, String key) {
		String url = ApplnURI + ConfigManager.getproperty("actuatorIDAEndpoint");
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;
		try {
			if (authActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				authActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}

			for (int i = 0, size = authActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = authActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(section)) {
					value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
							.get(GlobalConstants.VALUE).toString();
					if (ConfigManager.IsDebugEnabled())
						logger.info("Actuator: " + url + " key: " + key + " value: " + value);
					break;
				}
			}
			actuatorValueCache.put(actuatorCacheKey, value);

			return value;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return value;
		}

	}

	private static Map<String, String> testcaseIDNameMap = new HashMap<>();

	public static void addTestCaseDetailsToMap(String testCaseName, String uniqueIdentifier) {
		testcaseIDNameMap.put(testCaseName, uniqueIdentifier);
	}

	public static String getTestCaseUniqueIdentifier(String testCaseName) {
		return testcaseIDNameMap.get(testCaseName);
	}

	public static String getRandomElement(List<String> list) {
		int randomIndex = secureRandom.nextInt(list.size());
		return list.get(randomIndex);
	}

	public static String smtpOtpHandler(String inputJson, String testCaseName) {
		JSONObject request = new JSONObject(inputJson);
		String emailId = null;
		String otp = null;
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || testCaseName.startsWith("auth_OTP_Auth")
				|| testCaseName.startsWith("auth_EkycOtp") || testCaseName.startsWith("auth_MultiFactorAuth")
				|| testCaseName.startsWith("Ida_EkycOtp") || testCaseName.startsWith("Ida_OTP_Auth")) {
			if (request.has("otp")) {
				if (request.getString("otp").endsWith(GlobalConstants.MOSIP_NET)
						|| request.getString("otp").endsWith(GlobalConstants.MAILINATOR_COM)
						|| request.getString("otp").endsWith(GlobalConstants.MOSIP_IO)
						|| request.getString("otp").endsWith(GlobalConstants.OTP_AS_PHONE)) {
					emailId = request.get("otp").toString();
					if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
						emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
						emailId = removeLeadingPlusSigns(emailId);
					}
					logger.info(emailId);
					otp = OTPListener.getOtp(emailId);
					request.put("otp", otp);
					inputJson = request.toString();
					return inputJson;
				}
			}
		}
		if (BaseTestCase.currentModule.equals(GlobalConstants.PREREG)) {
			if (request.has(GlobalConstants.REQUEST)) {
				if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
					emailId = request.getJSONObject(GlobalConstants.REQUEST).getString("userId");
					logger.info(emailId);

					if (testCaseName.contains("_INVALIDOTP")) {
						otp = "26258976";
					} else {
						otp = OTPListener.getOtp(emailId);
					}

					request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
					inputJson = request.toString();
					return inputJson;
				}
			}
		}
		if (BaseTestCase.currentModule.equals("auth")) {
			if (testCaseName.startsWith("auth_GenerateVID") || testCaseName.startsWith("auth_AuthLock")
					|| testCaseName.startsWith("auth_AuthUnLock") || testCaseName.startsWith("auth_RevokeVID")) {
				if (request.has(GlobalConstants.REQUEST)) {
					if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
						if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
								.endsWith(GlobalConstants.MOSIP_NET)
								|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
										.endsWith(GlobalConstants.OTP_AS_PHONE)) {
							emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
							if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
								emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
								emailId = removeLeadingPlusSigns(emailId);
							}
							logger.info(emailId);
							otp = OTPListener.getOtp(emailId);
							request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
							inputJson = request.toString();
							return inputJson;
						}
					}
				}
			}
		}
		if (BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.DSL)) {
			if (testCaseName.startsWith("Resident_GenerateVID")
					|| testCaseName.startsWith("ESignet_AuthenticateUserIDP")
					|| testCaseName.startsWith("Resident_credential")) {
				if (request.has(GlobalConstants.REQUEST)) {
					if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
						if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
								.endsWith(GlobalConstants.MAILINATOR_COM)
								|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
										.endsWith(GlobalConstants.MOSIP_IO)
								|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
										.endsWith(GlobalConstants.OTP_AS_PHONE)) {
							emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
							if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
								emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
								emailId = removeLeadingPlusSigns(emailId);
							}
							logger.info(emailId);
							otp = OTPListener.getOtp(emailId);
							request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
							inputJson = request.toString();
							return inputJson;
						}
					} else if (request.has(GlobalConstants.REQUEST)) {
						if (request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.CHALLENGELIST)) {
							if (request.getJSONObject(GlobalConstants.REQUEST)
									.getJSONArray(GlobalConstants.CHALLENGELIST).length() > 0) {
								if (request.getJSONObject(GlobalConstants.REQUEST)
										.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
										.has(GlobalConstants.CHALLENGE)) {
									if (request.getJSONObject(GlobalConstants.REQUEST)
											.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
											.getString(GlobalConstants.CHALLENGE)
											.endsWith(GlobalConstants.MAILINATOR_COM)
											|| request.getJSONObject(GlobalConstants.REQUEST)
													.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
													.getString(GlobalConstants.CHALLENGE)
													.endsWith(GlobalConstants.MOSIP_IO)
											|| request.getJSONObject(GlobalConstants.REQUEST)
													.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
													.getString(GlobalConstants.CHALLENGE)
													.endsWith(GlobalConstants.OTP_AS_PHONE)) {
										emailId = request.getJSONObject(GlobalConstants.REQUEST)
												.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
												.getString(GlobalConstants.CHALLENGE);
										if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
											emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
											emailId = removeLeadingPlusSigns(emailId);
										}
										logger.info(emailId);
										otp = OTPListener.getOtp(emailId);
										request.getJSONObject(GlobalConstants.REQUEST)
												.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
												.put(GlobalConstants.CHALLENGE, otp);
										inputJson = request.toString();
										return inputJson;
									}
								}
							}
						}
					}
				}
			}
		}
		if (BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| testCaseName.startsWith("Mimoto_WalletBinding")
				|| testCaseName.startsWith("Mimoto_ESignet_AuthenticateUser")) {
			if (request.has(GlobalConstants.REQUEST)) {
				if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
					if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
							.endsWith(GlobalConstants.MOSIP_NET)
							|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
									.endsWith(GlobalConstants.OTP_AS_PHONE)) {
						emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
						if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
							emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
							emailId = removeLeadingPlusSigns(emailId);
						}
						logger.info(emailId);
						otp = OTPListener.getOtp(emailId);
						request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
						inputJson = request.toString();
						return inputJson;
					}
				} else if (request.has(GlobalConstants.REQUEST)) {
					if (request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.CHALLENGELIST)) {
						if (request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.CHALLENGELIST)
								.length() > 0) {
							if (request.getJSONObject(GlobalConstants.REQUEST)
									.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
									.has(GlobalConstants.CHALLENGE)) {
								if (request.getJSONObject(GlobalConstants.REQUEST)
										.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
										.getString(GlobalConstants.CHALLENGE).endsWith(GlobalConstants.MOSIP_NET)
										|| request.getJSONObject(GlobalConstants.REQUEST)
												.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
												.getString(GlobalConstants.CHALLENGE)
												.endsWith(GlobalConstants.OTP_AS_PHONE)) {
									emailId = request.getJSONObject(GlobalConstants.REQUEST)
											.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
											.getString(GlobalConstants.CHALLENGE);
									if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
										emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
										emailId = removeLeadingPlusSigns(emailId);
									}
									logger.info(emailId);
									otp = OTPListener.getOtp(emailId);
									request.getJSONObject(GlobalConstants.REQUEST)
											.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
											.put(GlobalConstants.CHALLENGE, otp);
									inputJson = request.toString();
								}
							}
						}
					}
					return inputJson;
				}
			}
		}
		if (BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			if (request.has(GlobalConstants.REQUEST)) {
				if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
					if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
							.endsWith(GlobalConstants.MOSIP_NET)
							|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
									.endsWith(GlobalConstants.OTP_AS_PHONE)) {
						emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
						if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
							emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
							emailId = removeLeadingPlusSigns(emailId);
						}
						logger.info(emailId);
						if (testCaseName.contains("_EmptyChannel_Invalid_Neg"))
							otp = "";
						else
							otp = OTPListener.getOtp(emailId);
						request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
						inputJson = request.toString();
					}
				} else if (request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.CHALLENGELIST)) {
					if (request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.CHALLENGELIST)
							.length() > 0) {
						if (request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(GlobalConstants.CHALLENGELIST)
								.getJSONObject(0).has(GlobalConstants.CHALLENGE)) {
							if (request.getJSONObject(GlobalConstants.REQUEST)
									.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
									.getString(GlobalConstants.CHALLENGE).endsWith(GlobalConstants.MOSIP_NET)
									|| request.getJSONObject(GlobalConstants.REQUEST)
											.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
											.getString(GlobalConstants.CHALLENGE)
											.endsWith(GlobalConstants.OTP_AS_PHONE)) {
								emailId = request.getJSONObject(GlobalConstants.REQUEST)
										.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
										.getString(GlobalConstants.CHALLENGE);
								if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE)) {
									emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
									emailId = removeLeadingPlusSigns(emailId);
								}
								logger.info(emailId);
								otp = OTPListener.getOtp(emailId);
								request.getJSONObject(GlobalConstants.REQUEST)
										.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
										.put(GlobalConstants.CHALLENGE, otp);
								inputJson = request.toString();
								return inputJson;
							}
						}
					}
				}
			}
		}
		return inputJson;
	}

	public static String removeLeadingPlusSigns(String input) {
		// Remove leading '+' characters
		return input.replaceAll("^\\+*", "");
	}

	public static void checkDbAndValidate(String timeStamp, String dbChecker) throws AdminTestException {

		String sqlQuery = "SELECT * FROM audit.app_audit_log WHERE log_dtimes > '" + timeStamp
				+ "' AND session_user_name = '" + dbChecker + "';";

		Map<String, Object> response = DBManager
				.executeQueryAndGetRecord(ConfigManager.getproperty("audit_default_schema"), sqlQuery);

		Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
		List<OutputValidationDto> objList = new ArrayList<>();
		OutputValidationDto objOpDto = new OutputValidationDto();
		if (response.size() > 0) {

			objOpDto.setStatus("PASS");
		} else {
			objOpDto.setStatus(GlobalConstants.FAIL_STRING);
		}

		objList.add(objOpDto);
		objMap.put(GlobalConstants.EXPECTED_VS_ACTUAL, objList);

		if (!OutputValidationUtil.publishOutputResult(objMap))
			throw new AdminTestException("Failed at output validation");
		Reporter.log(ReportUtil.getOutputValidationReport(objMap));
	}

	public static String getPartnerIdFromPartnerURL(String partnerKeyURLSuffix) {
		Pattern pattern = Pattern.compile("/(.*?)/");
		Matcher matcher = pattern.matcher(partnerKeyURLSuffix);
		String substring = "";
		if (matcher.find()) {
			substring = matcher.group(1);
		}
		System.out.println(substring); // substring
		return substring;
	}

	public static String getServerComponentsDetails() {
		if (serverComponentsCommitDetails != null && !serverComponentsCommitDetails.isEmpty())
			return serverComponentsCommitDetails;
		String commitDetailsResponse = "";
		File file = new File(propsHealthCheckURL);
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equals("") || line.trim().startsWith("#"))
					continue;
				String[] parts = line.trim().split("=");
				if (parts.length > 1) {
					if (ConfigManager.isInServiceNotDeployedList(parts[1])) {
						continue;
					}
					commitDetailsResponse = getCommitDetails(
							BaseTestCase.ApplnURI + parts[1].replace("health", "info"));
					if (commitDetailsResponse.contains("No Response"))
						continue;
					else {
						stringBuilder.append("\n").append(commitDetailsResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			AdminTestUtil.closeBufferedReader(bufferedReader);
			AdminTestUtil.closeFileReader(fileReader);
		}
		serverComponentsCommitDetails = stringBuilder.toString();
		return serverComponentsCommitDetails;
	}

	public static String getCommitDetails(String path) {

		Response response = null;
		response = given().contentType(ContentType.JSON).get(path);
		if (response != null && response.getStatusCode() == 200) {
			logger.info(response.getBody().asString());
			JSONObject jsonResponse = new JSONObject(response.getBody().asString());
			return "Group: " + jsonResponse.getJSONObject("build").getString("group") + " ---- Artifact: "
					+ jsonResponse.getJSONObject("build").getString("artifact") + " ---- version: "
					+ jsonResponse.getJSONObject("build").getString("version") + " ---- Commit ID: "
					+ jsonResponse.getJSONObject("git").getJSONObject("commit").getString("id");
		}
		return path + "- No Response";
	}

	public static void getLocationData() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationData");
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		int recommendedHierarchyLevel = getRecommendedHierarchyLevel();
		try {
			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONObject("response");
				JSONArray data = responseObject.getJSONArray("data");

				if (!(languageList.size() > 1)) {
					currentLanguage = BaseTestCase.languageList.get(0);
				}

				for (int i = 0; i < data.length(); i++) {
					JSONObject entry = data.getJSONObject(i);
					String langCode = entry.getString("langCode");
					hierarchyLevel = entry.getInt("hierarchyLevel");

					if (hierarchyLevel == recommendedHierarchyLevel) {
						if (currentLanguage.equals(langCode)) {
							hierarchyName = entry.getString("hierarchyName");
							parentLocCode = entry.optString("parentLocCode", "");
							break;
						}
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getLocationLevelData() {

		Response response = null;
//		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationHierarchyLevels")
				+ BaseTestCase.getLanguageList().get(0);
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String topLevelName = null;
		String url2 = "";

		Response responseLocationHierarchy = null;
//		JSONObject responseJsonLocationHierarchy = null;

		try {

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			JSONArray locationHierarchyLevels = jsonObject.getJSONObject("response")
					.getJSONArray("locationHierarchyLevels");

			int topLevel = -1;

			for (int i = 0; i < locationHierarchyLevels.length(); i++) {
				JSONObject hierarchy = locationHierarchyLevels.getJSONObject(i);
				int hierarchyLevel = hierarchy.getInt("hierarchyLevel");
				if (hierarchyLevel > topLevel) {
					topLevel = hierarchyLevel;
					topLevelName = hierarchy.getString("hierarchyLevelName");
				}
			}

			url2 = ApplnURI + props.getProperty("fetchLocationHierarchy") + topLevelName;
			responseLocationHierarchy = RestClient.getRequestWithCookie(url2, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

			JSONObject locationJsonObject = new JSONObject(responseLocationHierarchy.getBody().asString());
			JSONArray locations = locationJsonObject.getJSONObject("response").getJSONArray("locations");

			if (locations.length() > 0) {
				JSONObject firstLocation = locations.getJSONObject(0);
				locationCode = firstLocation.getString("code");
				System.out.println("First Location Code: " + locationCode);
			} else {
				System.out.println("No locations found in the response.");
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getHierarchyZoneCode() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchZoneCode") + BaseTestCase.getLanguageList().get(0);
		String token = kernelAuthLib.getTokenByRole("globalAdmin");

		try {

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONArray("response").getJSONObject(0);

				hierarchyZoneCode = responseObject.getString("code");

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getZoneName() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchZone");
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);

		Map<String, String> map = new HashMap<>();

		map.put("langCode", BaseTestCase.getLanguageList().get(0));
		map.put("userID", "masterdata-" + ConfigManager.getUserAdminName());

		try {

			response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONObject("response");

				ZonelocationCode = responseObject.getString("zoneCode");

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getLeafZone() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("leafZoneUrl") + BaseTestCase.getLanguageList().get(0);
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);

		try {

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONArray("response").getJSONObject(0);

				leafZoneCode = responseObject.getString("code");

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getRegistrationCenterData() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchRegCent");
		String token = kernelAuthLib.getTokenByRole("globalAdmin");

		try {

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONObject("response").getJSONArray("registrationCenters")
						.getJSONObject(0);

				locationCode = responseObject.getString("locationCode");

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static void getLocationDataWithLocationCode(String locationCode) {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationDataWithCode") + locationCode + "/"
				+ BaseTestCase.getLanguageList().get(0);
		String token = kernelAuthLib.getTokenByRole("globalAdmin");

		try {

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONObject("response");

				hierarchyLevelWithLocationCode = responseObject.getInt("hierarchyLevel");
				locationName = responseObject.getString("name");

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
	}

	public static String inputTitleHandler(String jsonString) {
		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchTitle");
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String firstWord = "";
		String value = "";
		String langcode = "";
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONArray nameArray = jsonObject.getJSONArray("name");
		if (nameArray.length() > 0) {
			JSONObject nameObject = nameArray.getJSONObject(0);
			value = nameObject.getString("value");
			firstWord = value.split("\\s+")[0]; // Miss-TitleFromServer
			jsonString = jsonString.replace(firstWord, propsMap.getProperty(firstWord));
			firstWord = propsMap.getProperty(firstWord); // MIS
			langcode = nameObject.getString("language");

		} else {
			System.out.println("No 'name' data found.");
		}

		url = url + "/" + langcode;
		try {
			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {

			JSONArray titleList = responseJson.getJSONObject("response").getJSONArray("titleList");
			for (int i = 0; i < titleList.length(); i++) {
				JSONObject titleObject = titleList.getJSONObject(i);
				if (titleObject.getString("code").equals(firstWord)) {
					String titleName = titleObject.getString("titleName");
					jsonString = jsonString.replace(firstWord, titleName);
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return jsonString;
	}

	public static boolean isElementPresent(JSONArray inputArray, String element) {
		for (int i = 0; i < inputArray.length(); i++) {
			String tempString = inputArray.getString(i);
			if (tempString.equalsIgnoreCase(element)) {
				return true;
			}
		}
		return false;
	}

	public static String ekycDataDecryption(String url, JSONObject kycDataForDecryption, String partnerName,
			Boolean keyFileNameByPartnerName) {
		url = url + properties.getProperty("decryptKycUrl");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(kycDataForDecryption.toString(), Map.class);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}

		HashMap<String, Object> queryParamMap = new HashMap<>();
		queryParamMap.put("partnerName", partnerName);
		queryParamMap.put("moduleName", BaseTestCase.certsForModule);

		queryParamMap.put("keyFileNameByPartnerName", keyFileNameByPartnerName);

		Response response = RestClient.postRequestWithQueryParamsAndBody(url, map, queryParamMap,
				MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		return response.getBody().asString();
	}

	public static String ekycDataDecryptionForDemo(String url, JSONObject kycDataForDecryption, String partnerName,
			Boolean keyFileNameByPartnerName) {
		url = url + properties.getProperty("decryptKycUrl");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(kycDataForDecryption.toString(), Map.class);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}

		HashMap<String, Object> queryParamMap = new HashMap<>();
		queryParamMap.put("partnerName", partnerName);
		queryParamMap.put("moduleName", BaseTestCase.certsForModule);

		queryParamMap.put("keyFileNameByPartnerName", keyFileNameByPartnerName);

		GlobalMethods.reportRequest(null, map.toString(), url);

		Response response = RestClient.postRequestWithQueryParamsAndBody(url, map, queryParamMap,
				MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		return response.getBody().asString();
	}

	public static String getValueFromUrl(String url, String dataToFetch) {
		String idValue = "";
		try {
			URI uri = new URI(url);
			Map<String, String> queryParams = new HashMap<>();
			String query = uri.getQuery();
			if (query != null) {
				String[] pairs = query.split("&");
				for (String pair : pairs) {
					String[] param = pair.split("=");
					String key = param[0];
					String value = param.length > 1 ? param[1] : "";
					queryParams.put(key, value);
				}

			}
			idValue = queryParams.get("id");
			if (idValue != null) {
				System.out.println("Value of 'id' parameter: " + idValue);
			} else {
				System.out.println("'id' parameter not found in the URL.");
			}

		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return idValue;

	}

	public String getSubjectFromJwt(String JwtEncodedString) {
		String subject = "";
		try {
			DecodedJWT decodedJWT = JWT.decode(JwtEncodedString);
			subject = decodedJWT.getSubject();
			logger.info("The subject of the Jwt Encoded String is " + subject);
		} catch (JwtException e) {
			logger.info("Invalid JWT token.");
		}
		return subject;
	}

	public static JSONArray ArrayOfJsonObjects = null;

	public static JSONArray getSyncDataResponseArray() {
		if (ArrayOfJsonObjects != null) {
			return ArrayOfJsonObjects;
		}
		String url = ApplnURI + "/v1/syncdata/getcacertificates";

		String token = kernelAuthLib.getTokenByRole("admin");

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);
		JSONObject responseJson = new JSONObject(response.asString());
		if (responseJson.has("response") && responseJson.getJSONObject("response").has("certificateDTOList")) {
			ArrayOfJsonObjects = responseJson.getJSONObject("response").getJSONArray("certificateDTOList");
		}
		return ArrayOfJsonObjects;
	}

	public static boolean IsCertSyncd(String certSubjectSubString) {
		if (ArrayOfJsonObjects == null) {
			ArrayOfJsonObjects = getSyncDataResponseArray();
		}

		if (ArrayOfJsonObjects != null) {
			for (int i = 0; i < ArrayOfJsonObjects.length(); i++) {
				if (ArrayOfJsonObjects.getJSONObject(i).has("certSubject") && ArrayOfJsonObjects.getJSONObject(i)
						.getString("certSubject").contains(certSubjectSubString)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setfoundHandlesInIdSchema(boolean foundHandles) {

		foundHandlesInIdSchema = foundHandles;
	}

	public static boolean isHandlesAvailableInIdSchema(boolean foundHandles) {

		return foundHandlesInIdSchema;
	}

//	public static boolean checkIsCertTrusted(String certIssuer, JSONArray ArrayOfJsonObjects, int recursiveCount) {
//		for (int i = 0; i < ArrayOfJsonObjects.length(); i++) {
//			if (ArrayOfJsonObjects.getJSONObject(i).has("certSubject")
//					&& ArrayOfJsonObjects.getJSONObject(i).has("certIssuer")
//					&& ArrayOfJsonObjects.getJSONObject(i).getString("certSubject").equals(certIssuer)) {
//
//				String certSubjectValue = ArrayOfJsonObjects.getJSONObject(i).getString("certSubject");
//				String certIssuerValue = ArrayOfJsonObjects.getJSONObject(i).getString("certIssuer");
//
//				if (!(certSubjectValue.equals(certIssuerValue)) && recursiveCount <= 5) {
//					recursiveCount++;
//					return checkIsCertTrusted(certIssuerValue, ArrayOfJsonObjects, recursiveCount);
//				} else if (certSubjectValue.equals(certIssuerValue)) {
//					return true;
//				} else {
//					break;
//				}
//			}
//		}
//		return false;
//	}
//	
//	public static boolean IsCertTrusted(String certSubjectSubString) {
//		if (ArrayOfJsonObjects == null) {
//			ArrayOfJsonObjects = getSyncDataResponseArray();
//		}
//
//		if (ArrayOfJsonObjects != null) {
//			for (int i = 0; i < ArrayOfJsonObjects.length(); i++) {
//				if (ArrayOfJsonObjects.getJSONObject(i).has("certSubject") && ArrayOfJsonObjects.getJSONObject(i)
//						.getString("certSubject").contains(certSubjectSubString)) {
//					if (ArrayOfJsonObjects.getJSONObject(i).has("certIssuer")) {
//						return checkIsCertTrusted(ArrayOfJsonObjects.getJSONObject(i).getString("certIssuer"),
//								ArrayOfJsonObjects, 1);
//					}
//				}
//			}
//		}
//		return false;
//	}

	public static JSONArray getRequiredField() {

		JSONObject requestJson = new JSONObject();
		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = getSchemaURL();

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

//	    Double schemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		idSchemaVersion = ((BigDecimal) schemaData.get(GlobalConstants.ID_VERSION)).doubleValue();
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		JSONObject schemaFileJson = new JSONObject(schemaFile);
		JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties");
		JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity");
		globalRequiredFields = schemaIdentityJson.getJSONArray("required");

		return globalRequiredFields;

	}

	public static Response postWithJson(String endpoint, Object body) {
		return RestClient.postWithJson(BaseTestCase.ApplnURI + endpoint, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}

	public static Response getWithoutParams(String endpoint, String cookie) {
		return RestClient.getWithoutParams(ApplnURI + endpoint, cookie);
	}

	public void checkResponseUTCTime(Response response) {
		logger.info(response.asString());
		org.json.simple.JSONObject responseJson = null;
		String responseTime = null;
		try {
			responseJson = (org.json.simple.JSONObject) new JSONParser().parse(response.asString());
		} catch (ParseException e1) {
			logger.info(e1.getMessage());
			return;
		}
		if (responseJson != null && responseJson.containsKey("responsetime"))
			responseTime = response.jsonPath().get("responsetime").toString();
		else
			return;
		String cuurentUTC = (String) getCurrentUTCTime();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		try {
			Date d1 = sdf.parse(responseTime.substring(14, 16));
			Date d2 = sdf.parse(cuurentUTC.substring(14, 16));

			long elapse = Math.abs(d1.getTime() - d2.getTime());
			if (elapse > 300000) {
				Assert.assertTrue(false, "Response time is not UTC, response time : " + responseTime);
			}

		} catch (java.text.ParseException e) {
			logger.error(e.getMessage());
		}

	}

	public static Object getCurrentUTCTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now(Clock.systemUTC());
		String utcTime = time.format(dateFormat);
		return utcTime;

	}

	public Object getCurrentLocalTime() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now();
		String currentTime = time.format(dateFormat);
		return currentTime;

	}

	public List<String> getFoldersFilesNameList(String folderRelativePath, boolean isfolder) {
		String configPath = folderRelativePath;
		List<String> listFoldersFiles = new ArrayList<>();

		final File file = new File(getResourcePath() + folderRelativePath);
		logger.info("=====" + getResourcePath() + folderRelativePath);
		logger.info("=======" + file.getAbsolutePath());
		logger.info("=========" + file.getPath());
		for (File f : file.listFiles()) {
			if (f.isDirectory() == isfolder)
				listFoldersFiles.add(configPath + "/" + f.getName());
		}
		return listFoldersFiles;
	}

	public String getResourcePathForKernel() {
		return getGlobalResourcePath() + "/";
	}

	public static org.json.simple.JSONObject readJsonData(String path, boolean isRelative) {
		logger.info("path : " + path);
		if (isRelative)
			path = getResourcePath() + path;
		logger.info("Relativepath : " + path);
		FileInputStream inputStream = null;
		org.json.simple.JSONObject jsonData = null;
		try {
			File fileToRead = new File(path);
			logger.info("fileToRead : " + fileToRead);
			inputStream = new FileInputStream(fileToRead);
			jsonData = (org.json.simple.JSONObject) new JSONParser()
					.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		} catch (IOException | ParseException | NullPointerException e) {
			logger.error(e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return jsonData;
	}

	public static Map<String, String> readProperty(String propertyFileName) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		Map<String, String> mapProp = null;
		try {
			try (InputStream input = ConfigManager.class.getClassLoader()
					.getResourceAsStream("config/Kernel.properties")) {
				if (input != null) {
					// Load the properties from the input stream
					prop.load(input);
				} else {
					logger.error("Couldn't find  Kernel.properties file");
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}

			/*
			 * inputStream = new FileInputStream(propertyFile); prop.load(inputStream);
			 */
			mapProp = prop.entrySet().stream()
					.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}

		return mapProp;
	}

	public static boolean isValidToken(String cookie) {
		boolean bReturn = false;
		if (cookie == null)
			return bReturn;
		try {
			DecodedJWT decodedJWT = JWT.decode(cookie);
			long expirationTime = decodedJWT.getExpiresAt().getTime();
			if (expirationTime < System.currentTimeMillis()) {
				logger.info("The token is expired");
			} else {
				bReturn = true;
				logger.info("The token is not expired");
			}
		} catch (JWTDecodeException e) {
			logger.error("The token is invalid");
		}
		return bReturn;
	}

	public static String findClientId(String jsonResponse, String keyWord) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonResponse);

		if (rootNode.has(keyWord)) {
			return rootNode.get(keyWord).asText();
		}
		for (JsonNode child : rootNode) {
			if (child.isObject() || child.isArray()) {
				String clientId = findClientId(child.toString(), keyWord);
				if (clientId != null) {
					return clientId;
				}
			}
		}
		return null;
	}

}
