package io.mosip.testrig.apirig.admin.fw.util;

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
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.authentication.fw.dto.OutputValidationDto;
import io.mosip.testrig.apirig.authentication.fw.precon.JsonPrecondtion;
import io.mosip.testrig.apirig.authentication.fw.precon.MessagePrecondtion;
import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.authentication.fw.util.RunConfigUtil;
import io.mosip.testrig.apirig.dbaccess.AuditDBManager;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.global.utils.GlobalMethods;
import io.mosip.testrig.apirig.ida.certificate.PartnerRegistration;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.kernel.util.KernelAuthentication;
import io.mosip.testrig.apirig.kernel.util.KeycloakUserManager;
import io.mosip.testrig.apirig.kernel.util.Translator;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.ExtractResource;
import io.mosip.testrig.apirig.testrunner.MockSMTPListener;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ravi Kant
 * @author Sohan
 *
 */
public class AdminTestUtil extends BaseTestCase {

	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	protected static final Properties properties = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/application.properties");
	protected static final Properties propsMap = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/valueMapping.properties");
	protected static final Properties propsBio = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/bioValue.properties");
	protected static final Properties propsKernel = getproperty(
			MosipTestRunner.getGlobalResourcePath() + "/" + "config/Kernel.properties");
	public static String propsHealthCheckURL = MosipTestRunner.getGlobalResourcePath() + "/"
			+ "config/healthCheckEndpoint.properties";
	private static String serverComponentsCommitDetails;

	protected static String token = null;
	String idToken = null;
	String adminAutoGeneratedIdPropFileName = properties.getProperty("adminAutoGeneratedIdPropFileName");
	String masterDataAutoGeneratedIdPropFileName = properties.getProperty("masterDataAutoGeneratedIdPropFileName");
	String syncDataAutoGeneratedIdPropFileName = properties.getProperty("syncDataAutoGeneratedIdPropFileName");
	String preregAutoGeneratedIdPropFileName = properties.getProperty("preregAutoGeneratedIdPropFileName");
	String partnerAutoGeneratedIdPropFileName = properties.getProperty("partnerAutoGeneratedIdPropFileName");
	String idrepoAutoGeneratedIdPropFileName = properties.getProperty("idrepoAutoGeneratedIdPropFileName");
	String residentAutoGeneratedIdPropFileName = properties.getProperty("residentAutoGeneratedIdPropFileName");
	String esignetAutoGeneratedIdPropFileName = properties.getProperty("esignetAutoGeneratedIdPropFileName");
	String authAutoGeneratedIdPropFileName = properties.getProperty("authAutoGeneratedIdPropFileName");
	String prerequisiteAutoGeneratedIdPropFileName = properties.getProperty("prerequisiteAutoGeneratedIdPropFileName");
	String regProcAutoGeneratedIdPropFileName = properties.getProperty("regProcAutoGeneratedIdPropFileName");
	String mobileIdAutoGeneratedIdPropFileName = properties.getProperty("mobileIdAutoGeneratedIdPropFileName");
	public static final String RESOURCE_FOLDER_NAME = "MosipTemporaryTestResource";
	protected static String genertedUIN = null;
	protected static String generatedRid = null;
	protected static String regDeviceResponse = null;
	protected static String generatedVID = null;
	public static final String RANDOM_ID = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String RANDOM_ID_2 = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public static final String RANDOM_ID_V2 = "mosip" + generateRandomNumberString(2)
	+ Calendar.getInstance().getTimeInMillis();
	public static final String TRANSACTION_ID = generateRandomNumberString(10);
	public static final String AUTHORIZATHION_HEADERNAME = GlobalConstants.AUTHORIZATION;
	public static final String AUTH_HEADER_VALUE = "Some String";
	public static final String SIGNATURE_HEADERNAME = GlobalConstants.SIGNATURE;
	public static BioDataUtility bioDataUtil = new BioDataUtility();

	public static BioDataUtility getBioDataUtil() {
		return bioDataUtil;
	}

	public static EncryptionDecrptionUtil encryptDecryptUtil = null;
	protected static String idField = null;
	protected static String identityHbs = null;
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
	protected static final String POLICY_GROUP_REQUEST = "config/policyGroup.json";
	protected static final String ESIGNET_PAYLOAD = "config/esignetPayload.json";
	protected static Map<String, String> keycloakRolesMap = new HashMap<>();
	protected static Map<String, String> keycloakUsersMap = new HashMap<>();
	protected static RSAKey oidcJWKKey1 = null;
	protected static RSAKey oidcJWKKey3 = null;
	
	protected static final String OIDCJWK1 = "oidcJWK1";
	protected static final String OIDCJWK2 = "oidcJWK2";
	protected static final String OIDCJWK3 = "oidcJWK3";
	protected static final String BINDINGJWK1 = "bindingJWK1";
	protected static final String BINDINGJWKVID = "bindingJWKVid";
	protected static final String BINDINGCONSENTJWK = "bindingConsentJWK";
	protected static final String BINDINGCONSENTJWKVID = "bindingConsentJWKVid";
	protected static final String BINDINGCONSENTSAMECLAIMJWK = "bindingConsentSameClaimJWK";
	protected static final String BINDINGCONSENTVIDSAMECLAIMJWK = "bindingConsentVidSameClaimJWK";
	protected static final String BINDINGCONSENTEMPTYCLAIMJWK = "bindingConsentEmptyClaimJWK";
	protected static final String BINDINGCONSENTUSER2JWK = "bindingConsentUser2JWK";
	protected static final String BINDINGCONSENTVIDUSER2JWK = "bindingConsentVidUser2JWK";
	public static final String XSRF_HEADERNAME = "X-XSRF-TOKEN";
	public static final String OAUTH_HASH_HEADERNAME = "oauth-details-hash";
	public static final String OAUTH_TRANSID_HEADERNAME = "oauth-details-key";
	protected static String encryptedSessionKeyString;
	
	protected static final String ESIGNETUINCOOKIESRESPONSE = "ESignetUINCookiesResponse";
	protected static final String ESIGNETVIDCOOKIESRESPONSE = "ESignetVIDCookiesResponse";
	
//	private static File eSignetUINCookiesFile = new File(getResourcePath() + "ESignetUINCookiesResponse.txt");
//	private static File eSignetVIDCookiesFile = new File(getResourcePath() + "ESignetVIDCookiesResponse.txt");
	
	public static final String BINDINGCERTFILE = "BINDINGCERTFile";
	public static final String BINDINGCERTFILEVID = "BINDINGCERTFileVid";
	public static final String BINDINGCERTCONSENTFILE = "BINDINGCERTCONSENTFile";
	public static final String BINDINGCERTCONSENTVIDFILE = "BINDINGCERTCONSENTVidFile";
	public static final String BINDINGCERTCONSENTSAMECLAIMFILE = "BINDINGCERTCONSENTSAMECLAIMFile";
	public static final String BINDINGCERTCONSENTVIDSAMECLAIMFILE = "BINDINGCERTCONSENTVIDSAMECLAIMFile";
	public static final String BINDINGCERTCONSENTEMPTYCLAIMFILE = "BINDINGCERTCONSENTEMPTYCLAIMFile";
	public static final String BINDINGCERTCONSENTUSER2FILE = "BINDINGCERTCONSENTUSER2File";
	public static final String BINDINGCERTVIDCONSENTUSER2FILE = "BINDINGCERTCONSENTVIDUSER2File";

	private static final String UIN_CODE_VERIFIER_POS_1 = generateRandomAlphaNumericString(GlobalConstants.INTEGER_36);

	/** The Constant SIGN_ALGO. */
	private static final String SIGN_ALGO = "RS256";
	public static final int OTP_CHECK_INTERVAL = 10000;
	
	private static final Map<String, String> actuatorValueCache = new HashMap<>();

	protected static boolean triggerESignetKeyGen1 = true;

	private static void settriggerESignetKeyGen1(boolean value) {
		triggerESignetKeyGen1 = value;
	}

	private static boolean gettriggerESignetKeyGen1() {
		return triggerESignetKeyGen1;
	}

	protected static boolean triggerESignetKeyGen2 = true;

	private static void settriggerESignetKeyGen2(boolean value) {
		triggerESignetKeyGen2 = value;
	}

	private static boolean gettriggerESignetKeyGen2() {
		return triggerESignetKeyGen2;
	}

	protected static boolean triggerESignetKeyGen3 = true;

	private static void settriggerESignetKeyGen3(boolean value) {
		triggerESignetKeyGen3 = value;
	}

	private static boolean gettriggerESignetKeyGen3() {
		return triggerESignetKeyGen3;
	}

	protected static boolean triggerESignetKeyGen4 = true;

	private static void settriggerESignetKeyGen4(boolean value) {
		triggerESignetKeyGen4 = value;
	}

	private static boolean gettriggerESignetKeyGen4() {
		return triggerESignetKeyGen4;
	}

	protected static boolean triggerESignetKeyGen5 = true;

	private static void settriggerESignetKeyGen5(boolean value) {
		triggerESignetKeyGen5 = value;
	}

	private static boolean gettriggerESignetKeyGen5() {
		return triggerESignetKeyGen5;
	}

	protected static boolean triggerESignetKeyGen6 = true;

	private static void settriggerESignetKeyGen6(boolean value) {
		triggerESignetKeyGen6 = value;
	}

	private static boolean gettriggerESignetKeyGen6() {
		return triggerESignetKeyGen6;
	}

	protected static boolean triggerESignetKeyGen7 = true;

	private static void settriggerESignetKeyGen7(boolean value) {
		triggerESignetKeyGen7 = value;
	}

	private static boolean gettriggerESignetKeyGen7() {
		return triggerESignetKeyGen7;
	}

	protected static boolean triggerESignetKeyGen8 = true;

	private static void settriggerESignetKeyGen8(boolean value) {
		triggerESignetKeyGen8 = value;
	}

	private static boolean gettriggerESignetKeyGen8() {
		return triggerESignetKeyGen8;
	}
	
	protected static boolean triggerESignetKeyGen9 = true;

	private static void settriggerESignetKeyGen9(boolean value) {
		triggerESignetKeyGen9 = value;
	}

	private static boolean gettriggerESignetKeyGen9() {
		return triggerESignetKeyGen9;
	}
	
	protected static boolean triggerESignetKeyGen10 = true;

	private static void settriggerESignetKeyGen10(boolean value) {
		triggerESignetKeyGen10 = value;
	}

	private static boolean gettriggerESignetKeyGen10() {
		return triggerESignetKeyGen10;
	}
	
	protected static boolean triggerESignetKeyGen11 = true;

	private static void settriggerESignetKeyGen11(boolean value) {
		triggerESignetKeyGen11 = value;
	}

	private static boolean gettriggerESignetKeyGen11() {
		return triggerESignetKeyGen11;
	}
	
	protected static boolean triggerESignetKeyGen12 = true;

	private static void settriggerESignetKeyGen12(boolean value) {
		triggerESignetKeyGen12 = value;
	}

	private static boolean gettriggerESignetKeyGen12() {
		return triggerESignetKeyGen11;
	}

	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
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
			String testCaseName) {
		return postWithBodyAndCookie(url, jsonInput, false, cookieName, role, testCaseName, false);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		return postWithBodyAndCookie(url, jsonInput, false, cookieName, role, testCaseName, bothAccessAndIdToken);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName) {
		return postWithBodyAndCookie(url, jsonInput, auditLogCheck, cookieName, role, testCaseName, false);
	}

	protected Response postWithBodyAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName, boolean bothAccessAndIdToken) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.PREREG) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {

			if (testCaseName.contains("NOAUTH")) {
				token = "";
			} else {
				token = kernelAuthLib.getTokenByRole(role);
			}

		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}

			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(inputJson);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}

		return response;
	}

	protected Response deleteWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.deleteRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response postWithBodyAndCookieWithText(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, "*/*", cookieName,
					token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}
	
	protected Response postWithBodyAndCookieWithoutBody(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		Response response = null;
		jsonInput= null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON, "*/*", cookieName,
					token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeaderAndXsrfToken(String url, String jsonInput, String cookieName,
			String testCaseName) {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		JSONObject request = new JSONObject(inputJson);
		String encodedResp = null;
		String transactionId = null;

		if (request.has(GlobalConstants.REQUEST)
				&& request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.TRANSACTIONID)
					.toString();
		}
		
		if (request.has(GlobalConstants.ENCODEDHASH)) {
			encodedResp = request.get(GlobalConstants.ENCODEDHASH).toString();
			logger.info("encodedhash = " + encodedResp);
			headers.put(OAUTH_HASH_HEADERNAME, encodedResp);
			headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);
			request.remove(GlobalConstants.ENCODEDHASH);
		}
		inputJson = request.toString();
		if (BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
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
		token = properties.getProperty(GlobalConstants.XSRFTOKEN);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieAuthHeaderAndXsrfTokenForAutoGeneratedId(String url, String jsonInput,
			String cookieName, String testCaseName, String idKeyName) {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MOBILEID) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		token = properties.getProperty(GlobalConstants.XSRFTOKEN);

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}

	}

	protected Response postRequestWithCookieAuthHeaderAndXsrfTokenForAutoGenId(String url, String jsonInput,
			String cookieName, String testCaseName, String idKeyName) {
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
		if (request.has(GlobalConstants.REQUEST)
				&& request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.TRANSACTIONID)
					.toString();

		}
		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		headers.put(OAUTH_HASH_HEADERNAME, encodedResp);
		headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);

		inputJson = request.toString();
		if (BaseTestCase.currentModule.equals(GlobalConstants.MOBILEID) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		token = properties.getProperty(GlobalConstants.XSRFTOKEN);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersAndCookies(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeaderForAutoGenId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
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
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			if (testCaseName.toLowerCase().contains("_scert")) {
				cacheCertificate(response, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public static void cacheCertificate(Response response, String testCaseName) {
		String certsKey = null;
		if (testCaseName.contains("Wla_uin_")) {
			certsKey = BINDINGCERTFILE;
		} else if (testCaseName.contains("Wla_vid_")) {
			certsKey = BINDINGCERTFILEVID;
		} else if (testCaseName.contains("_Consentuin_")) {
			certsKey = BINDINGCERTCONSENTFILE;
		} else if (testCaseName.contains("_ConsentVid_")) {
			certsKey = BINDINGCERTCONSENTVIDFILE;
		} else if (testCaseName.contains("_Consent_SameClaim_uin_")) {
			certsKey = BINDINGCERTCONSENTSAMECLAIMFILE;
		} else if (testCaseName.contains("_Consent_SameClaim_Vid_")) {
			certsKey = BINDINGCERTCONSENTVIDSAMECLAIMFILE;
		} else if (testCaseName.contains("_Consent_EmptyClaim_uin_")) {
			certsKey = BINDINGCERTCONSENTEMPTYCLAIMFILE;
		} else if (testCaseName.contains("_Consent_User2_uin_SCert_")) {
			certsKey = BINDINGCERTCONSENTUSER2FILE;
		} else if (testCaseName.contains("_Consent_User2_Vid_SCert_")) {
			certsKey = BINDINGCERTVIDCONSENTUSER2FILE;
		}

		String certificateData = new JSONObject(response.getBody().asString()).getJSONObject(GlobalConstants.RESPONSE)
				.get("certificate").toString();
		
		CertsUtil.addCertificateToCache(certsKey, certificateData);
	}

	protected Response postRequestWithCookieAuthHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
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
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForKeyCloak(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postRequestWithBearerToken(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAcceptTextPlainAndCookie(String url, String jsonInput, String cookieName,
			String role, String testCaseName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.TEXT_PLAIN, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAuthHeaderAndSignature(String url, String jsonInput, String cookieName,
			String role, String testCaseName) {
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
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithAuthHeaderAndSignatureForOtp(String url, String jsonInput, String cookieName,
			String token, Map<String, String> headers, String testCaseName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		
		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
		
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
		
	}
	
	
	
	protected Response postRequestWithAuthHeaderAndSignatureForOtpAutoGenId(String url, String jsonInput, String cookieName,
			String token, Map<String, String> headers, String testCaseName,String idKeyName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		
		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
		
	}

	protected Response patchRequestWithCookieAuthHeaderAndSignature(String url, String jsonInput, String cookieName,
			String role, String testCaseName) {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, generateSignatureWithRequest(inputJson, null));
		token = kernelAuthLib.getTokenByRole(role);
		logger.info("******Patch request Json to EndPointUrl: " + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.patchRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithAuthHeaderAndSignature(String url, String jsonInput, String testCaseName) {
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
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithHeaderAndSignature(String url, String jsonInput, String signature,
			String testCaseName) {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, signature);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson);
		try {
			response = RestClient.postRequestWithMultipleHeadersWithoutCookie(url, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return postRequestWithCookieAndHeader(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response postRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MOBILEID) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE,
						IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchRequestWithCookieAndHeader(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		Response response = null;
		if (url.contains("ID:"))
			url = inputJsonKeyWordHandeler(url, testCaseName);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (BaseTestCase.currentModule.equals("auth") || BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.patchRequestWithCookieAndHeader(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return patchWithBodyAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response patchWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName) {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, auditLogCheck, cookieName, role, testCaseName,
				idKeyName, false);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName, idKeyName,
				false);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			boolean bothAccessAndIdToken, String cookieName, String role, String testCaseName, String idKeyName) {
		return postWithBodyAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName, idKeyName,
				bothAccessAndIdToken);
	}

	protected Response postWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MOBILEID) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
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
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}

		return response;
	}

	protected Response postWithBodyAndBearerTokenForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (testCaseName.contains("Invalid_Token")) {
			token = "xyz";
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postRequestWithBearerToken(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(String url, String jsonInput,
			String testCaseName, String idKeyName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(inputJson, Map.class);
			logger.info(GlobalConstants.POST_REQ_URL + url);
			logger.info(inputJson);
			GlobalMethods.reportRequest(null, inputJson);
			response = RestAssured.given().contentType("application/x-www-form-urlencoded; charset=utf-8")
					.formParams(map).when().post(url);
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
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookieWithAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
		Response response = null;
		if (url.contains("ID:")) {
			url = uriKeyWordHandelerUri(url, testCaseName);
		}
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithBodyAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.patchRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName) {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName,
				idKeyName, false);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken) {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, false, cookieName, role, testCaseName,
				idKeyName, bothAccessAndIdToken);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName) {
		return getWithPathParamAndCookieForAutoGeneratedId(url, jsonInput, auditLogCheck, cookieName, role,
				testCaseName, idKeyName, false);
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedId(String url, String jsonInput, boolean auditLogCheck,
			String cookieName, String role, String testCaseName, String idKeyName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, jsonInput);
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
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (auditLogCheck) {
				JSONObject jsonObject = new JSONObject(jsonInput);
				String timeStamp1 = jsonObject.getString(GlobalConstants.REQUESTTIME);
				String dbChecker = GlobalConstants.TEST_FULLNAME + BaseTestCase.getLanguageList().get(0);
				checkDbAndValidate(timeStamp1, dbChecker);
			}

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response getWithPathParamAndCookieForAutoGeneratedIdForKeyCloak(String url, String jsonInput,
			String cookieName, String role, String testCaseName, String idKeyName) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			if (url.contains("{") || url.contains("?")) {
				response = RestClient.getRequestWithCookieAndPathParmForKeyCloak(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
				if (testCaseName.toLowerCase().contains("_sid")) {
					writeAutoGeneratedIdForKeyCloak(response, idKeyName, testCaseName);
				}
			} else {
				response = RestClient.getRequestWithCookieForKeyCloak(url, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
				if (testCaseName.toLowerCase().contains("_sid")) {
					writeAutoGeneratedIdForKeyCloak(response, idKeyName, testCaseName);
				}
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public String encodeBase64(String value) {
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
			String idKeyName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postWithFormPathParamAndFile(url, formParams, pathParams, filetoUpload, fileKeyName,
					MediaType.MULTIPART_FORM_DATA, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithParamAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) {
		return postWithParamAndFile(url, jsonInput, role, testCaseName, idKeyName, false);
	}

	protected Response postWithParamAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, req.toString());
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postWithParamsAndFile(url, map, filetoUpload, fileKeyName,
						MediaType.MULTIPART_FORM_DATA, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postWithParamsAndFile(url, map, filetoUpload, fileKeyName,
						MediaType.MULTIPART_FORM_DATA, token);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithFormDataAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postWithFormDataAndFile(url, formParams, absolueFilePath,
					MediaType.MULTIPART_FORM_DATA, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithMultipartFormDataAndFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) {
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
		GlobalMethods.reportRequest(null, jsonInput);

		try {
			response = RestClient.postWithMultipartFormDataAndFile(url, formParams, MediaType.MULTIPART_FORM_DATA,
					token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithFormDataAndMultipleFile(String url, String jsonInput, String role, String testCaseName,
			String idKeyName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postWithFormDataAndMultipleFile(url, formParams, listFiles,
					MediaType.MULTIPART_FORM_DATA, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public static void initialUserCreation() {
		Response response = null;
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		org.json.simple.JSONObject actualRequestGeneration = BaseTestCase.getRequestJson("config/bulkUpload.json");
		String url = ApplnURI + propsKernel.getProperty("bulkUploadUrl");

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
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

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
			String testCaseName) {
		return putWithBodyAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response putWithBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.PUT_REQ_STRING + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.putRequestWithCookie(url, inputJson, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.putRequestWithParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.patchRequestWithParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) {
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
		GlobalMethods.reportRequest(null, req.toString());
		try {
			if (testCaseName.toLowerCase().contains("dynamic"))
				pathParamsMap.put("id", idField);
			response = RestClient.putWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response putWithPathParamsBodyAndBearerToken(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) {
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
		GlobalMethods.reportRequest(null, req.toString());
		try {
			if (testCaseName.toLowerCase().contains("dynamic"))
				pathParamsMap.put("id", idField);
			response = RestClient.putWithPathParamsBodyAndBearerToken(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) {
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

		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithPathParamsBodyHeaderAndCookie(String url, String jsonInput, String cookieName,
			String role, String testCaseName, String pathParams) {
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
		GlobalMethods.reportRequest(headers.toString(), req.toString());
		try {
			response = RestClient.postWithPathParamsBodyHeadersAndCookie(url, pathParamsMap, inputJson,
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithQueryParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String queryParams, String idKeyName) {
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
		GlobalMethods.reportRequest(null, inputJson);
		try {
			response = RestClient.postWithQueryParamsBodyAndCookie(url, queryParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, "*/*", cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			if (testCaseName.toLowerCase().contains("_sid")) {
				writeAutoGeneratedId(response, idKeyName, testCaseName);
			}
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response patchWithPathParamsBodyAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, String pathParams) {
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
		GlobalMethods.reportRequest(null, req.toString());
		try {
			response = RestClient.patchWithPathParamsBodyAndCookie(url, pathParamsMap, req.toString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
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
			String testCaseName) {
		return getWithPathParamAndCookie(url, jsonInput, false, cookieName, role, testCaseName, false);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
		return getWithPathParamAndCookie(url, jsonInput, false, cookieName, role, testCaseName, bothAccessAndIdToken);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName) {
		return getWithPathParamAndCookie(url, jsonInput, auditLogCheck, cookieName, role, testCaseName, false);
	}

	protected Response getWithPathParamAndCookie(String url, String jsonInput, boolean auditLogCheck, String cookieName,
			String role, String testCaseName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			if (url.contains("{") || url.contains("?")) {
				if (bothAccessAndIdToken) {
					response = RestClient.getRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
							MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
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
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;
	}

	protected Response deleteWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
		return deleteWithPathParamAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response deleteWithPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.deleteRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.deleteRequestWithCookieAndPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response deleteWithPathParamAndCookieForKeyCloak(String url, String jsonInput, String cookieName,
			String role, String testCaseName) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {

			response = RestClient.deleteRequestWithCookieAndPathParmForKeyCloak(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;

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
		GlobalMethods.reportRequest(null, jsonInput);

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
		GlobalMethods.reportRequest(null, jsonInput);
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
		GlobalMethods.reportRequest(null, jsonInput);
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
			String testCaseName) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
			return response;
		}
	}

	protected Response patchWithQueryParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			response = RestClient.patchRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
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
	void writeAutoGeneratedId(Response response, String idKeyName, String testCaseName) {
		String fileName = getAutoGenIdFileName(testCaseName);
		JSONObject responseJson = null;

		JSONObject responseBody = null;
		String signature = null;
		FileOutputStream outputStream = null;
		FileInputStream inputStream = null;
		Properties props = new Properties();
		try {
			if (testCaseName.contains("ESignet_GenerateToken")
					|| testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
				responseBody = new JSONObject(response.getBody().asString());
				if (testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
					signature = response.getHeader(GlobalConstants.SIGNATURE);
				}
			} else {
				responseJson = new JSONObject(response.getBody().asString()).getJSONObject(GlobalConstants.RESPONSE);
			}
			inputStream = new FileInputStream(getResourcePath() + fileName);
			props.load(inputStream);
			String[] fieldNames = idKeyName.split(",");

			for (String filedName : fieldNames) {
				if (responseJson != null) {
					String identifierKeyName = getAutogenIdKeyName(testCaseName, filedName);
					if (responseJson.has(filedName))
						props.put(identifierKeyName, responseJson.get(filedName).toString());
					else if(responseJson.has("partners")) {
						org.json.JSONArray responseArray = responseJson.getJSONArray("partners");
						String authPartnerId = getPartnerId (responseArray,"active","Auth_Partner");
						props.put(identifierKeyName, authPartnerId);
					}
					else if (responseJson.has("data")) {
						org.json.JSONArray responseArray = responseJson.getJSONArray("data");

						JSONObject eachJSON = (JSONObject) responseArray.get(0);
						logger.info(eachJSON.get(filedName));
						props.put(identifierKeyName, eachJSON.get(filedName));
					} else if (testCaseName.contains("_OAuthDetailsRequest_") && filedName.equals("encodedResp")) {
						Gson gson = new Gson();
						JsonObject json = gson.fromJson(response.getBody().asString(), JsonObject.class);
						String responseJsonString = json.getAsJsonObject(GlobalConstants.RESPONSE).toString();

						MessageDigest digest = MessageDigest.getInstance("SHA-256");
						byte[] hash = digest.digest(responseJsonString.getBytes(StandardCharsets.UTF_8));
						String urlEncodedResp = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
						logger.info("encoded response = " + urlEncodedResp);
						props.put(identifierKeyName, urlEncodedResp);
					} else
						props.put(identifierKeyName,
								responseJson.getJSONObject(GlobalConstants.IDENTITY).get(filedName));
				} else if (responseBody != null) {
					String identifierKeyName = getAutogenIdKeyName(testCaseName, filedName);
					if (responseBody.has(filedName)) {
						props.put(identifierKeyName, responseBody.get(filedName).toString());
					} else if (testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
						if (filedName.equals("authReqBody")) {
							props.put(identifierKeyName, responseBody.toString());
						} else if (filedName.equals("authSignature")) {
							props.put(identifierKeyName, signature);
						}
					} else
						logger.error(GlobalConstants.ERROR_STRING_3 + filedName + GlobalConstants.WRITE_STRING
								+ response.asString());
				} else {
					logger.error(GlobalConstants.ERROR_STRING_3 + filedName + GlobalConstants.WRITE_STRING
							+ response.asString());
				}
			}
			outputStream = new FileOutputStream(getResourcePath() + fileName);
			props.store(outputStream, GlobalConstants.AUTOGENERATED_FIELDS);
			logger.info(GlobalConstants.AUTOGENERATED_FIELDS_PROPERTY + getResourcePath() + fileName);
		} catch (JSONException | IOException | NoSuchAlgorithmException e) {
			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
		} finally {
			closeInputStream(inputStream);
			closeOutputStream(outputStream);
		}

	}

	void writeAutoGeneratedIdForKeyCloak(Response response, String idKeyName, String testCaseName) throws IOException {
		JSONArray responseJson = new JSONArray(response.asString());
		logger.info(responseJson.getJSONObject(0));
		FileOutputStream outputStream = null;
		FileInputStream inputStream = null;
		try {
			if (testCaseName.toLowerCase().contains("keycloakuserdetails")) {

				String fileName = getAutoGenIdFileName(testCaseName);
				JSONObject responseOutputJson = null;

				Properties props = new Properties();
				responseOutputJson = responseJson.getJSONObject(0);
				inputStream = new FileInputStream(getResourcePath() + fileName);
				props.load(inputStream);
				if (responseOutputJson.has(GlobalConstants.USERNAME)) {
					keycloakUsersMap.put(GlobalConstants.USERNAME,
							responseOutputJson.get(GlobalConstants.USERNAME).toString());
				}
				String[] fieldNames = idKeyName.split(",");
				for (String filedName : fieldNames) {
					if (responseJson.length() != 0) {
						String identifierKeyName = getAutogenIdKeyName(testCaseName, filedName);
						if (responseOutputJson.has(filedName))
							props.put(identifierKeyName, responseOutputJson.get(filedName).toString());
						else
							props.put(identifierKeyName,
									responseOutputJson.getJSONObject(GlobalConstants.IDENTITY).get(filedName));
					} else {
						logger.error(GlobalConstants.ERROR_STRING_3 + filedName + GlobalConstants.WRITE_STRING
								+ response.asString());
					}
				}
				outputStream = new FileOutputStream(getResourcePath() + fileName);
				props.store(outputStream, GlobalConstants.AUTOGENERATED_FIELDS);
				logger.info(GlobalConstants.AUTOGENERATED_FIELDS_PROPERTY + getResourcePath() + fileName);

			}

			if (testCaseName.toLowerCase().contains("keycloakroledetails")) {

				for (int i = 0; i < responseJson.length(); i++) { // OR iterate
					JSONObject tmp = responseJson.getJSONObject(i);
					{
						keycloakRolesMap.put(tmp.getString("name"), tmp.getString("id"));

					}
				}
			}
		} finally {
			closeInputStream(inputStream);
			closeOutputStream(outputStream);
		}
	}

//	public void writeAutoGeneratedId(String testCaseName, String idName, String value) {
//		if (testCaseName == null || idName == null || value == null) {
//			logger.error("autogenerated id is not stored as few details not available");
//			return;
//		}
//		String fileName = getAutoGenIdFileName(testCaseName);
//		String identifierKeyName = getAutogenIdKeyName(testCaseName, idName);
//		Properties props = new Properties();
//		try (FileInputStream inputStream = new FileInputStream(getResourcePath() + fileName);
//				FileOutputStream outputStream = new FileOutputStream(getResourcePath() + fileName)) {
//			props.load(inputStream);
//			props.put(identifierKeyName, value);
//			props.store(outputStream, GlobalConstants.AUTOGENERATED_FIELDS);
//			logger.info(GlobalConstants.AUTOGENERATED_FIELDS_PROPERTY + getResourcePath() + fileName);
//		} catch (JSONException | IOException e) {
//			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
//		}
//	}

	public void writeAutoGeneratedId(String testCaseName, String idName, String value) {
		if (testCaseName == null || idName == null || value == null) {
			logger.error("autogenerated id is not stored as few details not available");
			return;
		}
		String fileName = getAutoGenIdFileName(testCaseName);
		String identifierKeyName = getAutogenIdKeyName(testCaseName, idName);
		FileOutputStream outputStream = null;
		FileInputStream inputStream = null;
		Properties props = new Properties();
		try {
			inputStream = new FileInputStream(getResourcePath() + fileName);
			props.load(inputStream);
			props.put(identifierKeyName, value);
			outputStream = new FileOutputStream(getResourcePath() + fileName);
			props.store(outputStream, "autogenerated fields");
			logger.info("added autogenerated fields to property: " + getResourcePath() + fileName);
		} catch (JSONException | IOException e) {
			logger.error("Exception while getting autogenerated id and writing in property file:" + e.getMessage());
		} finally {
			closeInputStream(inputStream);
			closeOutputStream(outputStream);
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
		if ((!BaseTestCase.isTargetEnvLTS()) && fieldName.equals("VID") && BaseTestCase.currentModule.equals("auth"))
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName.toLowerCase();
		else
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName;
		logger.info("key for testCase: " + testCaseName + " : " + autogenIdKeyName);
		return autogenIdKeyName;
	}

	public static String getGlobalResourcePath() {
		return MosipTestRunner.getGlobalResourcePath();
	}

	public static String getResourcePath() {
		return MosipTestRunner.getGlobalResourcePath() + "/";
	}

	public static void initiateAdminTest() {
		copyAdminTestResource();
	}

	public static void initiateMasterDataTest() {
		copyMasterDataTestResource();
	}

	public static void initiateMobileIdTestTest() {
		copyMobileIdTestResource();
	}

	public static void initiateesignetTest() {
		copyEsignetTestResource();
	}

	public static void initiateSyncDataTest() {
		copySyncDataTestResource();
	}

	public static void copymoduleSpecificAndConfigFile(String moduleName) {
		if (MosipTestRunner.checkRunType().equalsIgnoreCase("JAR")) {
			ExtractResource.getListOfFilesFromJarAndCopyToExternalResource(moduleName + "/");
		} else {
			try {
				File destination = new File(RunConfigUtil.getGlobalResourcePath());
				File source = new File(RunConfigUtil.getGlobalResourcePath()
						.replace("MosipTestResource/MosipTemporaryTestResource", "") + moduleName);
				FileUtils.copyDirectoryToDirectory(source, destination);

//				source = new File(RunConfigUtil.getGlobalResourcePath() + "/config");
//				FileUtils.copyDirectoryToDirectory(source, destination);
				logger.info("Copied the test resource successfully for " + moduleName);
			} catch (Exception e) {
				logger.error(
						"Exception occured while copying the file for : " + moduleName + " Error : " + e.getMessage());
			}
		}

	}

	public static void copyAdminTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.ADMIN);
	}

	public static void copyMasterDataTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.MASTERDATA);
	}

	public static void copyMobileIdTestResource() {
		copymoduleSpecificAndConfigFile("mobileId");
	}

	public static void copyEsignetTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.ESIGNET);
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
		List<TestCaseDTO> testCaseDTOList = new LinkedList<TestCaseDTO>();
		Map<String, Map<String, Map<String, String>>> scriptsMap = loadyaml(ymlPath);
		for (String key : scriptsMap.keySet()) {
			Map<String, Map<String, String>> testCases = scriptsMap.get(key);
			if (testType.equalsIgnoreCase("smoke")) {
				testCases = testCases.entrySet().stream()
						.filter(mapElement -> mapElement.getKey().toLowerCase().contains("smoke")).collect(Collectors
								.toMap(mapElement -> mapElement.getKey(), mapElement -> mapElement.getValue()));
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
			Yaml yaml = new Yaml();
			scriptsMap = yaml.loadAs(bufferedInput, Map.class);
		} catch (Exception e) {
			logger.error("Error loading YAML: " + e.getMessage());
		} finally {
			closeInputStream(inputStream);
		}
		return scriptsMap;
	}

//	@SuppressWarnings("unchecked")
//	protected Map<String, Map<String, Map<String, String>>> loadyaml(String path) {
//		Map<String, Map<String, Map<String, String>>> scriptsMap = null;
//		FileInputStream inputStream = null;
//		try {
//			Yaml yaml = new Yaml();
//			inputStream = new FileInputStream(new File(getResourcePath() + path).getAbsoluteFile());
//			scriptsMap = (Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}finally {
//			closeInputStream(inputStream);
//		}
//		return scriptsMap;
//	}

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
//			logger.info(input);
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

		return List.of(propsKernel.getProperty("ROLES." + username.replaceAll(" ", "")).split(","));

	}

	public String uriKeyWordHandelerUri(String uri, String testCaseName) {
		if (uri == null) {
			logger.info(" Request Json String is :" + uri);
			return uri;
		}
		if (uri.contains(GlobalConstants.KEYCLOAK_USER_1))
			uri = uri.replace(GlobalConstants.KEYCLOAK_USER_1, propsKernel.getProperty("KEYCLOAKUSER1"));
		if (uri.contains(GlobalConstants.KEYCLOAK_USER_2))
			uri = uri.replace(GlobalConstants.KEYCLOAK_USER_2, propsKernel.getProperty("KEYCLOAKUSER2"));
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
			String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
			uri = replaceIdWithAutogeneratedId(uri, "$ID:", autoGenIdFileName);
		}

		return uri;
	}

	public String replaceKeywordWithValue(String jsonString, String keyword, String value) {
		if (value != null && !value.isEmpty())
			return jsonString.replace(keyword, value);
		else
			throw new SkipException("Marking testcase as skipped as required fields are empty " + keyword);
	}

	public String inputJsonKeyWordHandeler(String jsonString, String testCaseName) {
		if (jsonString == null) {
			logger.info(" Request Json String is :" + jsonString);
			return jsonString;
		}
		if (testCaseName.contains("ESignet_GenerateApiKey_"))
			KeycloakUserManager.createKeyCloakUsers(genPartnerName, genPartnerEmail, "AUTH_PARTNER");
		
		if (testCaseName.contains("ESignet_GenerateApiKeyKyc_"))
			KeycloakUserManager.createKeyCloakUsers(genPartnerName + "2n", "12d" + genPartnerEmail, "AUTH_PARTNER");
		if (jsonString.contains("$THUMBPRINT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$THUMBPRINT$", EncryptionDecrptionUtil.idaFirThumbPrint);
		}
		if (jsonString.contains("$ENCRYPTEDSESSIONKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$ENCRYPTEDSESSIONKEY$", encryptedSessionKeyString);
		}
		if (jsonString.contains(GlobalConstants.MODULENAME)) {
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.MODULENAME, BaseTestCase.certsForModule);
		}
		if (jsonString.contains(GlobalConstants.CERTSDIR)) {
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.CERTSDIR,
					ConfigManager.getauthCertsPath());
		}

		if (jsonString.contains("$BIOVALUE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUE$", propsBio.getProperty("BioValue"));
		}
		if (jsonString.contains("$BIOVALUEWITHOUTFACE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUEWITHOUTFACE$",
					propsBio.getProperty("BioValueWithoutFace"));
		}
		if (jsonString.contains("$CLAIMSFROMCONFIG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$CLAIMSFROMCONFIG$", getValueFromConfigActuator());
		if (jsonString.contains(GlobalConstants.TIMESTAMP))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp());
		if (jsonString.contains(GlobalConstants.TRANSACTION_ID))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.TRANSACTION_ID, TRANSACTION_ID);
		if (jsonString.contains("$DATESTAMP$"))
			jsonString = replaceKeywordWithValue(jsonString, "$DATESTAMP$", generateCurrentUTCDateStamp());
		if (jsonString.contains("$TIMESTAMPL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$TIMESTAMPL$", generateCurrentLocalTimeStamp());
		if (jsonString.contains("$RID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RID$", genRid);

		if (jsonString.contains("$SCHEMAVERSION$"))
			jsonString = replaceKeywordWithValue(jsonString, "$SCHEMAVERSION$", generateLatestSchemaVersion());
		if (jsonString.contains("$1STLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$1STLANG$", BaseTestCase.languageList.get(0));
		if (jsonString.contains("$2NDLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$2NDLANG$", BaseTestCase.languageList.get(1));
		if (jsonString.contains("$3RDLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$3RDLANG$", BaseTestCase.languageList.get(2));

		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_1))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_1,
					propsKernel.getProperty("KEYCLOAKUSER1"));
		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_2))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_2,
					propsKernel.getProperty("KEYCLOAKUSER2"));
		if (jsonString.contains("$RIDDEL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RIDDEL$", genRidDel);
		if (jsonString.contains("$ID:")) {
			String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
			jsonString = replaceIdWithAutogeneratedId(jsonString, "$ID:", autoGenIdFileName);
		}
		if (jsonString.contains("$POLICYGROUPDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYGROUPDESC$", genPolicyGroupDesc);

		if (jsonString.contains("$POLICYGROUPNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYGROUPNAME$", genPolicyGroupName);

		if (jsonString.contains("$POLICYDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYDESC$", genPolicyDesc);

		if (jsonString.contains("$POLICYDESC1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYDESC1$", genPolicyDesc + "pol");

		if (jsonString.contains("$POLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAME$", genPolicyName);

		if (jsonString.contains("$POLICYNAMENONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAMENONAUTH$", genPolicyNameNonAuth);

		if (jsonString.contains("$POLICYNAME1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNAME1$", genPolicyName + "pold");

		if (jsonString.contains(GlobalConstants.PARTNER_ID))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.PARTNER_ID, genPartnerName);

		if (jsonString.contains("$PARTNERIDFORDSL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERIDFORDSL$", genPartnerNameForDsl);

		if (jsonString.contains("$PARTNERID1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERID1$", genPartnerName + "2n");

		if (jsonString.contains("$PARTNERIDNONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERIDNONAUTH$", genPartnerNameNonAuth);

		if (jsonString.contains("$RANDOMPARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPARTNEREMAIL$",
					"automation" + generateRandomNumberString(15) + GlobalConstants.MOSIP_NET);

		if (jsonString.contains("$PARTNEREMAIL1$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAIL1$", "12d" + genPartnerEmail);

		if (jsonString.contains("$PARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAIL$", genPartnerEmail);

		if (jsonString.contains("$PARTNEREMAILFORDSL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAILFORDSL$", genPartnerEmailForDsl);

		if (jsonString.contains("$PARTNEREMAILNONAUTH$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNEREMAILNONAUTH$", genPartnerEmailNonAuth);

		if (jsonString.contains("$MISPPOLICYGROUPDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYGROUPDESC$", genMispPolicyGroupDesc);

		if (jsonString.contains("$MISPPOLICYGROUPNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYGROUPNAME$", genMispPolicyGroupName);

		if (jsonString.contains("$MISPPOLICYDESC$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYDESC$", genMispPolicyDesc);

		if (jsonString.contains("$MISPPOLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPOLICYNAME$", genMispPolicyName);

		if (jsonString.contains("$RANDOMPOLICYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPOLICYNAME$",
					generateRandomAlphaNumericString(15));

		if (jsonString.contains("$RANDOMPARTNERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMPARTNERID$", generateRandomAlphaNumericString(15));

		if (jsonString.contains("$MISPPARTNERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNERID$", genMispPartnerName);

		if (jsonString.contains("$MISPPARTNEREMAIL$"))
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNEREMAIL$", genMispPartnerEmail);

		if (jsonString.contains("$ZONE_CODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$ZONE_CODE$", ZonelocationCode);
		if (jsonString.contains("$USERID$"))
			jsonString = replaceKeywordWithValue(jsonString, "$USERID$",
					BaseTestCase.currentModule + propsKernel.getProperty("admin_userName"));

		if (jsonString.contains("$LOCATIONCODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$LOCATIONCODE$", locationCode);

		// Need to handle int replacement
		 if (jsonString.contains("$HIERARCHYLEVEL$"))
		 jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYLEVEL$",
				 String.valueOf(hierarchyLevel));

		if (jsonString.contains("$HIERARCHYNAME$"))
			jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYNAME$", hierarchyName);

		if (jsonString.contains("$PARENTLOCCODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$PARENTLOCCODE$", parentLocCode);

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

			jsonString = replaceKeywordWithValue(jsonString, "$CACERT$", caFtmCertValue);

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

			jsonString = replaceKeywordWithValue(jsonString, "$MISPCACERT$", caFtmCertValue);

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

			jsonString = replaceKeywordWithValue(jsonString, "$INTERCERT$", interFtmCertValue);

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

			jsonString = replaceKeywordWithValue(jsonString, "$MISPINTERCERT$", interFtmCertValue);

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
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNERCERT$", partnerFtmCertValue);

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
			jsonString = replaceKeywordWithValue(jsonString, "$MISPPARTNERCERT$", partnerFtmCertValue);

		}

		if (jsonString.contains("$PUBLICKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PUBLICKEY$", MosipTestRunner.generatePulicKey());
			publickey = JsonPrecondtion.getJsonValueFromJson(jsonString, "request.publicKey");
		}
		if (jsonString.contains("$PUBLICKEYFORBINDING$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PUBLICKEYFORBINDING$",
					MosipTestRunner.generatePublicKeyForMimoto());
		}
		if (jsonString.contains("$BLOCKEDPARTNERID$")) {
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.PARTNER_ID, getPartnerId());
		}
		if (jsonString.contains("$APIKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$APIKEY$", getAPIKey());
		}

		if (jsonString.contains("$MISPLICKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$MISPLICKEY$", getMISPLICKey());
		}

		if (jsonString.contains("$IDENTITYJSON$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDENTITYJSON$", generateIdentityJson(testCaseName));
		}
		if (jsonString.contains("$RANDOMID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2", RANDOM_ID_V2);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$2", RANDOM_ID_2);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$", RANDOM_ID);
		}
		if (jsonString.contains("$RANDOMUUID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMUUID$", UUID.randomUUID().toString());
		}
		if (jsonString.contains("$BASEURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BASEURI$", ApplnURI);
		}
		if (jsonString.contains("$IDPUSER$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDPUSER$", propsKernel.getProperty("idpClientId"));
		}
		if (jsonString.contains("$OIDCCLIENT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCCLIENT$",
					getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.iam.module.clientID"));
		}
		if (jsonString.contains("$IDPREDIRECTURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDPREDIRECTURI$",
					ApplnURI.replace(GlobalConstants.API_INTERNAL, "healthservices") + "/userprofile");
		}
		if (jsonString.contains("$BASE64URI$")) {
			String redirectUri = ApplnURI.replace(GlobalConstants.API_INTERNAL, GlobalConstants.RESIDENT)
					+ propsKernel.getProperty("currentUserURI");
			jsonString = replaceKeywordWithValue(jsonString, "$BASE64URI$", encodeBase64(redirectUri));
		}
		if (jsonString.contains("$JWKKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$JWKKEY$", MosipTestRunner.generateJWKPublicKey());
		}

		if (jsonString.contains("$BINDINGJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen3()) {
//				jwkKey = generateAndWriteJWKKey(bindingJWK1);
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGJWK1);
				
				settriggerESignetKeyGen3(false);
			} else {
//				jwkKey = getJWKKey(BINDINGJWK1);
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGJWK1);
				
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGJWKKEY$", jwkKey);
		}

		if (jsonString.contains("$BINDINGJWKKEYVID$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen4()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGJWKVID);
				settriggerESignetKeyGen4(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGJWKVID);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGJWKKEYVID$", jwkKey);
		}

		if (jsonString.contains("$BINDINGCONSENTJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen5()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTJWK);
				settriggerESignetKeyGen5(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTJWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTJWKKEY$", jwkKey);
		}

		if (jsonString.contains("$BINDINGCONSENTJWKKEYVID$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen6()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTJWKVID);
				settriggerESignetKeyGen6(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTJWKVID);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTJWKKEYVID$", jwkKey);
		}

		if (jsonString.contains("$BINDINGCONSENTSAMECLAIMJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen7()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTSAMECLAIMJWK);
				settriggerESignetKeyGen7(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTSAMECLAIMJWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTSAMECLAIMJWKKEY$", jwkKey);
		}

		if (jsonString.contains("$BINDINGCONSENTSAMECLAIMVIDJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen8()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTVIDSAMECLAIMJWK);
				settriggerESignetKeyGen8(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTVIDSAMECLAIMJWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTSAMECLAIMVIDJWKKEY$", jwkKey);
		}
		
		if (jsonString.contains("$BINDINGCONSENTEMPTYCLAIMJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen9()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTEMPTYCLAIMJWK);
				settriggerESignetKeyGen9(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTEMPTYCLAIMJWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTEMPTYCLAIMJWKKEY$", jwkKey);
		}
		
		if (jsonString.contains("$BINDINGCONSENTUSER2JWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen10()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTUSER2JWK);
				settriggerESignetKeyGen10(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTUSER2JWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTUSER2JWKKEY$", jwkKey);
		}
		
		if (jsonString.contains("$BINDINGCONSENTVIDUSER2JWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen11()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(BINDINGCONSENTVIDUSER2JWK);
				settriggerESignetKeyGen11(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(BINDINGCONSENTVIDUSER2JWK);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$BINDINGCONSENTVIDUSER2JWKKEY$", jwkKey);
		}

		if (jsonString.contains("$OIDCJWKKEY$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen1()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(OIDCJWK1);
				settriggerESignetKeyGen1(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(OIDCJWK1);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCJWKKEY$", jwkKey);
		}

		if (jsonString.contains("$OIDCJWKKEY2$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen2()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(OIDCJWK2);
				settriggerESignetKeyGen2(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(OIDCJWK2);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCJWKKEY2$", jwkKey);
		}
		
		if (jsonString.contains("$OIDCJWKKEY3$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen12()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(OIDCJWK3);
				settriggerESignetKeyGen12(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(OIDCJWK3);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCJWKKEY3$", jwkKey);
		}

		if (jsonString.contains("$CLIENT_ASSERTION_JWK$")) {
			String oidcJWKKeyString = JWKKeyUtil.getJWKKey(OIDCJWK1);
			logger.info("oidcJWKKeyString =" + oidcJWKKeyString);
			try {
				oidcJWKKey1 = RSAKey.parse(oidcJWKKeyString);
				logger.info("oidcJWKKey1 =" + oidcJWKKey1);
			} catch (java.text.ParseException e) {
				logger.error(e.getMessage());
			}
			JSONObject request = new JSONObject(jsonString);
			String clientId = null;
			if (request.has("client_id")) {
				clientId = request.get("client_id").toString();
			}
			jsonString = replaceKeywordWithValue(jsonString, "$CLIENT_ASSERTION_JWK$",
					signJWKKey(clientId, oidcJWKKey1));
		}
		
		if (jsonString.contains("$CLIENT_ASSERTION_USER3_JWK$")) {
			String oidcJWKKeyString = JWKKeyUtil.getJWKKey(OIDCJWK3);
			logger.info("oidcJWKKeyString =" + oidcJWKKeyString);
			try {
				oidcJWKKey3 = RSAKey.parse(oidcJWKKeyString);
				logger.info("oidcJWKKey3 =" + oidcJWKKey3);
			} catch (java.text.ParseException e) {
				logger.error(e.getMessage());
			}
			JSONObject request = new JSONObject(jsonString);
			String clientId = null;
			if (request.has("client_id")) {
				clientId = request.get("client_id").toString();
			}
			jsonString = replaceKeywordWithValue(jsonString, "$CLIENT_ASSERTION_USER3_JWK$",
					signJWKKey(clientId, oidcJWKKey3));
		}
		if (jsonString.contains("$IDPCLIENTPAYLOAD$")) {
			String clientId = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES,
					"mosip.iam.module.clientID");
			String esignetBaseURI = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES,
					"mosip.iam.token_endpoint");
			int idTokenExpirySecs = Integer.parseInt(getValueFromEsignetActuator(
					GlobalConstants.ESIGNET_DEFAULT_PROPERTIES, GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));

			Instant instant = Instant.now();

			logger.info("Current Instant: " + instant);

			long epochValue = instant.getEpochSecond();

			org.json.simple.JSONObject payloadBody = getRequestJson(ESIGNET_PAYLOAD);
			payloadBody.put("sub", clientId);
			payloadBody.put("iss", clientId);
			payloadBody.put("aud", esignetBaseURI);
			payloadBody.put("exp", epochValue + idTokenExpirySecs);
			payloadBody.put("iat", epochValue);

			jsonString = replaceKeywordWithValue(jsonString, "$IDPCLIENTPAYLOAD$",
					encodeBase64(payloadBody.toString()));
		}

		if (jsonString.contains("$WLATOKEN$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKEN$",
					generateWLAToken(jsonString, BINDINGJWK1, BINDINGCERTFILE));
		}

		if (jsonString.contains("$WLATOKENVID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENVID$",
					generateWLAToken(jsonString, BINDINGJWKVID, BINDINGCERTFILEVID));
		}

		if (jsonString.contains("$WLATOKENCONSENT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENT$",
					generateWLAToken(jsonString, BINDINGCONSENTJWK, BINDINGCERTCONSENTFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATURE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATURE$",
					generateDetachedSignature(jsonString, BINDINGCONSENTJWK, BINDINGCERTCONSENTFILE));
		}

		if (jsonString.contains("$WLATOKENCONSENTVID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTVID$",
					generateWLAToken(jsonString, BINDINGCONSENTJWKVID, BINDINGCERTCONSENTVIDFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATUREVID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATUREVID$",
					generateDetachedSignature(jsonString, BINDINGCONSENTJWKVID, BINDINGCERTCONSENTVIDFILE));
		}

		if (jsonString.contains("$WLATOKENCONSENTSAMECLAIM$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTSAMECLAIM$",
					generateWLAToken(jsonString, BINDINGCONSENTSAMECLAIMJWK, BINDINGCERTCONSENTSAMECLAIMFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATURESAMECLAIM$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATURESAMECLAIM$",
					generateDetachedSignature(jsonString, BINDINGCONSENTSAMECLAIMJWK, BINDINGCERTCONSENTSAMECLAIMFILE));
		}

		if (jsonString.contains("$WLATOKENCONSENTVIDSAMECLAIM$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTVIDSAMECLAIM$",
					generateWLAToken(jsonString, BINDINGCONSENTVIDSAMECLAIMJWK, BINDINGCERTCONSENTVIDSAMECLAIMFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATUREVIDSAMECLAIM$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATUREVIDSAMECLAIM$",
					generateDetachedSignature(jsonString, BINDINGCONSENTVIDSAMECLAIMJWK, BINDINGCERTCONSENTVIDSAMECLAIMFILE));
		}
		
		if (jsonString.contains("$WLATOKENCONSENTEMPTYCLAIM$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTEMPTYCLAIM$",
					generateWLAToken(jsonString, BINDINGCONSENTEMPTYCLAIMJWK, BINDINGCERTCONSENTEMPTYCLAIMFILE));
		}
		
		if (jsonString.contains("$WLATOKENCONSENTUSER2$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTUSER2$",
					generateWLAToken(jsonString, BINDINGCONSENTUSER2JWK, BINDINGCERTCONSENTSAMECLAIMFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATUREUSER2$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATUREUSER2$",
					generateDetachedSignature(jsonString, BINDINGCONSENTUSER2JWK, BINDINGCERTCONSENTUSER2FILE));
		}
		
		if (jsonString.contains("$WLATOKENCONSENTVIDUSER2$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$WLATOKENCONSENTVIDUSER2$",
					generateWLAToken(jsonString, BINDINGCONSENTVIDUSER2JWK, BINDINGCERTCONSENTSAMECLAIMFILE));
		}
		
		if (jsonString.contains("$CONSENTDETACHEDSIGNATUREVIDUSER2$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CONSENTDETACHEDSIGNATUREVIDUSER2$",
					generateDetachedSignature(jsonString, BINDINGCONSENTVIDUSER2JWK, BINDINGCERTCONSENTSAMECLAIMFILE));
		}

		if (jsonString.contains("$UINCODECHALLENGEPOS1$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$UINCODECHALLENGEPOS1$",
					GlobalMethods.sha256(UIN_CODE_VERIFIER_POS_1));
		}

		if (jsonString.contains("$UINCODEVERIFIERPOS1$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$UINCODEVERIFIERPOS1$", UIN_CODE_VERIFIER_POS_1);
		}

		if (jsonString.contains("$CODECHALLENGE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CODECHALLENGE$",
					properties.getProperty("codeChallenge"));
		}

		if (jsonString.contains("$CODEVERIFIER$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CODEVERIFIER$", properties.getProperty("codeVerifier"));
		}

		if (jsonString.contains("$VCICONTEXTURL$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$VCICONTEXTURL$",
					properties.getProperty("vciContextURL"));
		}

		if (jsonString.contains("$PROOFJWT$")) {

			String oidcJWKKeyString = JWKKeyUtil.getJWKKey(OIDCJWK1);
			logger.info("oidcJWKKeyString =" + oidcJWKKeyString);
			try {
				oidcJWKKey1 = RSAKey.parse(oidcJWKKeyString);
				logger.info("oidcJWKKey1 =" + oidcJWKKey1);
			} catch (java.text.ParseException e) {
				logger.error(e.getMessage());
			}

			JSONObject request = new JSONObject(jsonString);
			String clientId = "";
			String accessToken = "";
			if (request.has("client_id")) {
				clientId = request.getString("client_id");
				request.remove("client_id");
			}
			if (request.has("idpAccessToken")) {
				accessToken = request.getString("idpAccessToken");
			}
			jsonString = request.toString();
			jsonString = replaceKeywordWithValue(jsonString, "$PROOFJWT$", signJWK(clientId, accessToken, oidcJWKKey1, testCaseName));
		}

		if (jsonString.contains(GlobalConstants.REMOVE))
			jsonString = removeObject(new JSONObject(jsonString));

		return jsonString;
	}

	public static String signJWK(String clientId, String accessToken, RSAKey jwkKey, String testCaseName) {
		String tempUrl = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.iam.base.url");
		int idTokenExpirySecs = Integer.parseInt(getValueFromEsignetActuator(GlobalConstants.ESIGNET_DEFAULT_PROPERTIES,
				GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;
		String proofJWT = "";
		String typ = "openid4vci-proof+jwt";
		JWK jwkHeader = jwkKey.toPublicJWK();
		SignedJWT signedJWT = null;

		try {
			signer = new RSASSASigner(jwkKey);

			String[] jwtParts = accessToken.split("\\.");
			String jwtPayloadBase64 = jwtParts[1];
			byte[] jwtPayloadBytes = Base64.getDecoder().decode(jwtPayloadBase64);
			String jwtPayload = new String(jwtPayloadBytes, StandardCharsets.UTF_8);
			JWTClaimsSet claimsSet = null;
			String nonce = new ObjectMapper().readTree(jwtPayload).get("c_nonce").asText();

			if (testCaseName.contains("_Invalid_C_nonce_"))
				nonce = "jwt_payload.c_nonce123";
			else if (testCaseName.contains("_Empty_C_nonce_"))
				nonce = "";
			else if (testCaseName.contains("_SpaceVal_C_nonce_"))
				nonce = "  ";
			else if (testCaseName.contains("_Empty_Typ_"))
				typ = "";
			else if (testCaseName.contains("_SpaceVal_Typ_"))
				typ = "  ";
			else if (testCaseName.contains("_Invalid_Typ_"))
				typ = "openid4vci-123@proof+jwt";
			else if (testCaseName.contains("_Invalid_JwkHeader_"))
				jwkHeader = RSAKey.parse(JWKKeyUtil.getJWKKey(OIDCJWK2)).toPublicJWK();
			else if (testCaseName.contains("_Invalid_Aud_"))
				tempUrl = "sdfaf";
			else if (testCaseName.contains("_Empty_Aud_"))
				tempUrl = "";
			else if (testCaseName.contains("_SpaceVal_Aud_"))
				tempUrl = "  ";
			else if (testCaseName.contains("_Invalid_Iss_"))
				clientId = "sdfdsg";
			else if (testCaseName.contains("_Invalid_Exp_"))
				idTokenExpirySecs = 0;

			claimsSet = new JWTClaimsSet.Builder().audience(tempUrl).claim("nonce", nonce).issuer(clientId)
					.issueTime(new Date()).expirationTime(new Date(new Date().getTime() + idTokenExpirySecs)).build();

			if (testCaseName.contains("_Missing_Typ_")) {
				signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(jwkHeader).build(), claimsSet);
			} else if (testCaseName.contains("_Missing_JwkHeader_")) {
				signedJWT = new SignedJWT(
						new JWSHeader.Builder(JWSAlgorithm.RS256).type(new JOSEObjectType(typ)).build(), claimsSet);
			} else {
				signedJWT = new SignedJWT(
						new JWSHeader.Builder(JWSAlgorithm.RS256).type(new JOSEObjectType(typ)).jwk(jwkHeader).build(),
						claimsSet);
			}

			signedJWT.sign(signer);
			proofJWT = signedJWT.serialize();
		} catch (Exception e) {
			logger.error("Exception while signing proof_jwt to get credential: " + e.getMessage());
		}
		return proofJWT;
	}

	public static String generateWLAToken(String jsonString, String jwkKeyName, String certKeyName) {
		RSAKey jwkKey = null;
		String jwkKeyString = JWKKeyUtil.getJWKKey(jwkKeyName);
		logger.info("jwkKeyString =" + jwkKeyString);

		String individualId = "";
		String wlaToken = "";
		String certificate = CertsUtil.getCertificate(certKeyName);
		JSONObject request = new JSONObject(jsonString);
		individualId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.INDIVIDUALID).toString();

		try {
			jwkKey = RSAKey.parse(jwkKeyString);
			logger.info("jwkKey =" + jwkKey);
			wlaToken = getWlaToken(individualId, jwkKey, certificate);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return wlaToken;
	}
	
	public static String generateDetachedSignature(String jsonString, String jwkKeyName, String certKeyName) {
		RSAKey jwkKey = null;
		String jwkKeyString = JWKKeyUtil.getJWKKey(jwkKeyName);
		logger.info("jwkKeyString =" + jwkKeyString);

		String[] acceptedClaims = null;
		JSONArray claimJsonArray = null;
		String[] permittedScope = null;
		JSONArray permittedScopeArray = null;
		String detachedSignature = "";
		String certificate = CertsUtil.getCertificate(certKeyName);
		JSONObject request = new JSONObject(jsonString);
		claimJsonArray = getArrayFromJson(request, "acceptedClaims");
		permittedScopeArray = getArrayFromJson(request, "permittedAuthorizeScopes");
		
        acceptedClaims = new String[claimJsonArray.length()];
        permittedScope = new String[permittedScopeArray.length()];
        
        for (int i = 0; i < claimJsonArray.length(); i++) {
            acceptedClaims[i] = claimJsonArray.getString(i);
        }
        if (acceptedClaims != null && acceptedClaims instanceof String[]) {
            Arrays.sort(acceptedClaims);
        }
        
        for (int i = 0; i < permittedScopeArray.length(); i++) {
        	permittedScope[i] = permittedScopeArray.getString(i);
        }


		try {
			jwkKey = RSAKey.parse(jwkKeyString);
			logger.info("jwkKey =" + jwkKey);
			detachedSignature = getDetachedSignature(acceptedClaims, permittedScope, jwkKey, certificate);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return detachedSignature;
		
	}
	
	public static String getDetachedSignature(String[] acceptedClaims, String[] permittedScope, RSAKey jwkKey, String certData)
			throws JoseException, JOSEException {
		JSONObject payload = new JSONObject();
		String signedJWT = null;
		
        if (acceptedClaims != null && acceptedClaims instanceof String[]) {
            Arrays.sort(acceptedClaims);
            payload.put("accepted_claims", acceptedClaims);
        }
		
        if (permittedScope != null && permittedScope instanceof String[]) {
            Arrays.sort(permittedScope);
            payload.put("permitted_authorized_scopes", permittedScope);
        }

		X509Certificate certificate = (X509Certificate) convertToCertificate(certData);
		JsonWebSignature jwSign = new JsonWebSignature();
		if (certificate != null) {
			jwSign.setX509CertSha256ThumbprintHeaderValue(certificate);
			jwSign.setPayload(payload.toString());
			jwSign.setAlgorithmHeaderValue(SIGN_ALGO);
			jwSign.setKey(jwkKey.toPrivateKey());
			jwSign.setDoKeyValidation(false);
			signedJWT = jwSign.getCompactSerialization();
			String[] parts = signedJWT.split("\\.");

	        return parts[0] + "." + parts[2];
		}
		return "";
	}
	
	public static JSONArray getArrayFromJson(JSONObject request, String value) {
		
		if (request.getJSONObject(GlobalConstants.REQUEST).has(value)) {
			return request.getJSONObject(GlobalConstants.REQUEST).getJSONArray(value);
		}
		
		return null;
	}

//	public static String generateAndWriteJWKKey(File fileName) {
//		String jwkKey = MosipTestRunner.generateJWKPublicKey();
//		writeFileAsString(fileName, jwkKey);
//		return jwkKey;
//	}

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

	public String getAutoGenIdFileName(String testCaseName) {
		String autoGenFileName = null;
		if (testCaseName == null)
			return autoGenFileName;
		if (testCaseName.toLowerCase().startsWith(GlobalConstants.ADMIN))
			autoGenFileName = adminAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("master"))
			autoGenFileName =  masterDataAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("sync"))
			autoGenFileName =  syncDataAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PREREG))
			autoGenFileName =  preregAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PARTNER))
			autoGenFileName =  partnerAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("idrepo"))
			autoGenFileName =  idrepoAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.RESIDENT))
			autoGenFileName =  residentAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("regproc"))
			autoGenFileName =  regProcAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("auth"))
			autoGenFileName =  authAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("prerequisite"))
			autoGenFileName =  prerequisiteAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.MOBILEID))
			autoGenFileName =  mobileIdAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.ESIGNET))
			autoGenFileName = esignetAutoGeneratedIdPropFileName;
		else {
			autoGenFileName = "default";
			logger.info("testCaseName: " + testCaseName);
		}
		return autoGenFileName;
	}

	public String getAutoGeneratedFieldValue(String inputFromYml, String testCaseName) {
		String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
		if (!inputFromYml.contains("$ID:"))
			return "Input doesn't contain autogenerateIdKey";
		String keyForIdProperty = StringUtils.substringBetween(inputFromYml, "$ID:", "$");
		Properties props = new Properties();
		try (FileInputStream inputStream = new FileInputStream(getResourcePath() + autoGenIdFileName)) {
			props.load(inputStream);
		} catch (IOException e) {
			logger.error("Exception while loading the autogenerated id: " + e.getMessage());
		}
		return props.getProperty(keyForIdProperty);
	}

	public String updateTimestampOtp(String otpIdentyEnryptRequest) {
		otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest,
				generateCurrentUTCTimeStamp(), "identityRequest.timestamp");
		if (proxy)
			otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest,
					properties.getProperty("proxyOTP"), "identityRequest.otp");
		else
			return otpIdentyEnryptRequest;

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
			String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
			finalString = replaceIdWithAutogeneratedId(identityObect.toString(), "$ID:", autoGenIdFileName);
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

	private String replaceIdWithAutogeneratedId(String jsonString, String idKey, String autoGenIdFileName) {
		if (!jsonString.contains(idKey))
			return jsonString;
		String keyForIdProperty = StringUtils.substringBetween(jsonString, idKey, "$");
		String keyToReplace = idKey + keyForIdProperty + "$";
		Properties props = new Properties();

		try (FileInputStream inputStream = new FileInputStream(getResourcePath() + autoGenIdFileName);) {
			props.load(inputStream);
			if (keyForIdProperty.contains("time_slot_from")) {
				String time = props.getProperty(keyForIdProperty);
				if (time.compareTo("12:00") >= 0)
					time += " PM";
				else
					time += " AM";
				jsonString = replaceKeywordWithValue(jsonString, keyToReplace, time);
			} else {
				if(keyForIdProperty.equals("UploadPartnerCert_Misp_Valid_Smoke_sid_signedCertificateData")) {
					String certData = props.getProperty(keyForIdProperty);
					if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
						certData = certData.replaceAll("\n", "\\\\n");
					} else {
						certData = certData.replaceAll("\n", "\\\\n");
						
					}
					jsonString = replaceKeywordWithValue(jsonString, keyToReplace, certData);
				}
				else 
				    jsonString = replaceKeywordWithValue(jsonString, keyToReplace, props.getProperty(keyForIdProperty));
			}
			if (jsonString.contains("\u200B")) {
				jsonString = jsonString.replaceAll("\u200B", "");
			}
			if (jsonString.contains("\\p{Cf}")) {
				jsonString = jsonString.replaceAll("\\p{Cf}", "");
			}
			 

			jsonString = replaceIdWithAutogeneratedId(jsonString, idKey, autoGenIdFileName);
			
			if (jsonString.contains("\u200B")) {
				jsonString = jsonString.replaceAll("\u200B", "");
			}
			if (jsonString.contains("\\p{Cf}")) {
				jsonString = jsonString.replaceAll("\\p{Cf}", "");
			}

		} catch (IOException e) {
			logger.error("Exception while loading the autogenerated id: " + e.getMessage());
		}

		return jsonString;
	}

	public String removeObject(JSONObject object) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				String finalarrayContent = "";
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); ++i) {
						if (!array.toString().contains("{") && !array.toString().contains("}")) {
							Set<String> arr = new HashSet<>();
							for (int k = 0; k < array.length(); k++) {
								arr.add(array.getString(k));
							}
							finalarrayContent = removObjectFromArray(arr);
						} else {
							String arrayContent = removeObject(new JSONObject(array.get(i).toString()),
									finalarrayContent);
							finalarrayContent = finalarrayContent + "," + arrayContent;
						}
					}
					finalarrayContent = finalarrayContent.substring(1, finalarrayContent.length());
					object.put(key, new JSONArray("[" + finalarrayContent + "]"));
				} else
					object.put(key, new JSONArray("[]"));

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
			String testCaseName) {
		return postWithOnlyPathParamAndCookie(url, jsonInput, cookieName, role, testCaseName, false);
	}

	protected Response postWithOnlyPathParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName, boolean bothAccessAndIdToken) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			if (bothAccessAndIdToken) {
				response = RestClient.postRequestWithCookieAndOnlyPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token, IDTOKENCOOKIENAME, idToken);
			} else {
				response = RestClient.postRequestWithCookieAndOnlyPathParm(url, map, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, cookieName, token);
			}
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	protected Response postWithOnlyQueryParamAndCookie(String url, String jsonInput, String cookieName, String role,
			String testCaseName) {
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
		GlobalMethods.reportRequest(null, jsonInput);
		try {
			response = RestClient.postRequestWithQueryParm(url, map, "*/*", "*/*", cookieName, token);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
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
//			File file = new File(path);
//			inputStream = new FileInputStream(file);
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

	public static void copyPartnerTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.PARTNER);
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

	public String getKeysDirPath() {
		String path = System.getProperty("java.io.tmpdir") + "/" + "IDA-" + environment + ".mosip.net";
		logger.info("certificate path is::" + path);
		return new File(path).getAbsolutePath();
	}

	public static String buildIdentityRequest(String identityRequest) {
		if (identityRequest.contains("$DATETIME$"))
			identityRequest = identityRequest.replace("$DATETIME$", generateCurrentUTCTimeStamp());
		if (identityRequest.contains(GlobalConstants.TIMESTAMP))
			identityRequest = identityRequest.replace(GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp());
		if (identityRequest.contains(GlobalConstants.TRANSACTION_ID))
			identityRequest = identityRequest.replace(GlobalConstants.TRANSACTION_ID, TRANSACTION_ID);
		if (identityRequest.contains("$FACE$"))
			identityRequest = identityRequest.replace("$FACE$", propsBio.getProperty("FaceBioValue"));
		if (identityRequest.contains("$RIGHTIRIS$"))
			identityRequest = identityRequest.replace("$RIGHTIRIS$", propsBio.getProperty("RightIrisBioValue"));
		if (identityRequest.contains("$LEFTIRIS$"))
			identityRequest = identityRequest.replace("$LEFTIRIS$", propsBio.getProperty("LeftIrisBioValue"));
		if (identityRequest.contains("$RIGHTTHUMB$"))
			identityRequest = identityRequest.replace("$RIGHTTHUMB$", propsBio.getProperty("RightThumbBioValue"));
		if (identityRequest.contains("$LEFTTHUMB$"))
			identityRequest = identityRequest.replace("$LEFTTHUMB$", propsBio.getProperty("LeftThumbBioValue"));
		if (identityRequest.contains("$RIGHTLITTLEFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTLITTLEFINGER$",
					propsBio.getProperty("RightLittleFingerBioValue"));
		if (identityRequest.contains("$RIGHTMIDDLEFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTMIDDLEFINGER$", propsBio.getProperty("RightMiddleFinger"));
		if (identityRequest.contains("$RIGHTRINGFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTRINGFINGER$",
					propsBio.getProperty("RightRingFingerBioValue"));
		if (identityRequest.contains("$RIGHTINDEXFINGER$"))
			identityRequest = identityRequest.replace("$RIGHTINDEXFINGER$",
					propsBio.getProperty("RightIndexFingerBioValue"));
		if (identityRequest.contains("$LEFTLITTLEFINGER$"))
			identityRequest = identityRequest.replace("$LEFTLITTLEFINGER$",
					propsBio.getProperty("LeftLittleFingerBioValue"));
		if (identityRequest.contains("$LEFTINDEXFINGER$"))
			identityRequest = identityRequest.replace("$LEFTINDEXFINGER$",
					propsBio.getProperty("LeftIndexFingerBioValue"));
		if (identityRequest.contains("$LEFTMIDDLEFINGER$"))
			identityRequest = identityRequest.replace("$LEFTMIDDLEFINGER$",
					propsBio.getProperty("LeftMiddleFingerBioValue"));
		if (identityRequest.contains("$LEFTRINGFINGER$"))
			identityRequest = identityRequest.replace("$LEFTRINGFINGER$",
					propsBio.getProperty("LeftRingFingerBioValue"));
		if (identityRequest.contains("$FACEDRAFTVALUE$"))
			identityRequest = identityRequest.replace("$FACEDRAFTVALUE$",
					propsBio.getProperty("FACEDRAFTVALUE"));
		
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
				String date = fetchCenterResponse.jsonPath().get(GlobalConstants.RESPONSE_CENTER_DETAILS + i + "].date")
						.toString();
				String dayOfWeek = LocalDate.parse(date).getDayOfWeek().toString();

				if (dayOfWeek.equals("SATURDAY") || dayOfWeek.equals("SUNDAY")) {
					appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
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
		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		boolean emailFieldAdditionallyAdded = false;
		boolean phoneFieldAdditionallyAdded = false;
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
				phoneFieldAdditionallyAdded = true;
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

			for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
				String eachRequiredProp = requiredPropsArray.getString(i); // objIDJson3

				JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp); // rc1

				if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("simpleType")) {

					JSONArray eachPropDataArray = new JSONArray(); // jArray

					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
						if (BaseTestCase.getLanguageList().get(j) != null
								&& !BaseTestCase.getLanguageList().get(j).isEmpty()) {
							JSONObject eachValueJson = new JSONObject(); // studentJSON
							eachValueJson.put("language", BaseTestCase.getLanguageList().get(j));
							if (eachRequiredProp.contains(GlobalConstants.FULLNAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + "1")); // fullName1
							} else if (eachRequiredProp.contains(GlobalConstants.FIRST_NAME) && regenerateHbs == true) {
								eachValueJson.put(GlobalConstants.VALUE, propsMap.getProperty(eachRequiredProp + 1)); // fullName1
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
					}
					
					else if (eachRequiredProp.equals("proofOfAddress")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "txt");
						identityJson.getJSONObject(eachRequiredProp).put("type", "DOC001");
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					}
//					else if (eachRequiredProp.equals(result)) {
//						
//						if(phoneFieldAdditionallyAdded) {
//							identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
//						}
//						else {
//							identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
//						}
//					}
//
//					else if (eachRequiredProp.equals(emailResult)) {
//						if(emailFieldAdditionallyAdded) {
//							identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
//						}
//						else {
//							identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
//						}
//						
//					}

					else if (eachRequiredProp.equals("individualBiometrics")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "cbeff");
						identityJson.getJSONObject(eachRequiredProp).put("version", 1);
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
					}

					else if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					}

					else {
						identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
					}
				}
			}

			JSONArray requestDocArray = new JSONArray();
			JSONObject docJson = new JSONObject();
			docJson.put("value", "{{value}}");
			docJson.put("category", "{{category}}");
			requestDocArray.put(docJson);

			requestJson.getJSONObject("request").put("documents", requestDocArray);
			requestJson.getJSONObject("request").put("identity", identityJson);
			requestJson.put("requesttime", generateCurrentUTCTimeStamp());
			requestJson.put("version", "v1");

			System.out.println(requestJson);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}

		identityHbs = requestJson.toString();
		return identityHbs;
	}

//	public static String modifySchemaGenerateHbs(boolean regenerateHbs) {
//		String ja3 = "";
//		if (identityHbs != null && !regenerateHbs) {
//			return identityHbs;
//		}
//		StringBuffer everything = new StringBuffer("");
//		kernelAuthLib = new KernelAuthentication();
//		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
//		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);
//		
//
//		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
//				GlobalConstants.AUTHORIZATION, token);
//
//		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
//		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);
//
//		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
//		logger.info(schemaVersion);
//		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);
//
//		String schemaFile = schemaJsonData;
//		FileWriter fileWriter1 = null;
//		FileWriter fileWriter2 = null;
//		FileWriter fileWriter3 = null;
//		FileReader fileReader = null;
//		BufferedReader bufferedReader = null;
//
//		boolean emailFieldAdditionallyAdded=false;
//		boolean phoneFieldAdditionallyAdded=false;
//		try {
//			JSONObject jObj = new JSONObject(schemaFile);
//			JSONObject objIDJson4 = jObj.getJSONObject(GlobalConstants.PROPERTIES);
//			JSONObject objIDJson = objIDJson4.getJSONObject(GlobalConstants.IDENTITY);
//			JSONObject objIDJson2 = objIDJson.getJSONObject(GlobalConstants.PROPERTIES);
//			JSONArray objIDJson1 = objIDJson.getJSONArray(GlobalConstants.REQUIRED);
//
//			String phone = getValueFromAuthActuator("json-property", "phone_number");
//			String result = phone.replaceAll("\\[\"|\"\\]", "");
//
//			if (!isElementPresent(objIDJson1, result)) {
//				objIDJson1.put(result);
//				phoneFieldAdditionallyAdded=true;
//			}
//
//			//System.out.println("result is:" + result);
//			String email = getValueFromAuthActuator("json-property", "emailId");
//			String emailResult = email.replaceAll("\\[\"|\"\\]", "");
//			if (!isElementPresent(objIDJson1, emailResult)) {
//				objIDJson1.put(emailResult);
//				emailFieldAdditionallyAdded=true;
//			}
//			
//
//			fileWriter1 = new FileWriter(GlobalConstants.ADDIDENTITY_HBS);
//			fileWriter1.write("{\n");
//			fileWriter1.write("  \"id\": \"{{id}}\",\n");
//			fileWriter1.write("  \"request\": {\n");
//			fileWriter1.write("\t  \"registrationId\": \"{{registrationId}}\",\n");
//
//			fileWriter1.write("    \"identity\": {\n");
//			fileWriter1.write("\t  \"UIN\": \"{{UIN}}\",\n");
//			fileWriter1.close();
//
//			boolean flag = true;
//			for (int i = 0, size = objIDJson1.length(); i < size; i++) {
//				String objIDJson3 = objIDJson1.getString(i); // fullName
//
//				JSONObject rc1 = (JSONObject) objIDJson2.get(objIDJson3);
//
//				if (rc1.has("$ref") && rc1.get("$ref").toString().contains(GlobalConstants.SIMPLETYPE)) {
//
//					JSONArray jArray = new JSONArray();
//
//					ja3 = "{\n\t\t  \"language\":";
//					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
//
//					if(BaseTestCase.getLanguageList().get(j)!=null && !BaseTestCase.getLanguageList().get(j).isEmpty())	{
//							JSONObject studentJSON = new JSONObject();
//							studentJSON.put(GlobalConstants.LANGUAGE, BaseTestCase.getLanguageList().get(j));
//							if (objIDJson3.contains(GlobalConstants.FULLNAME) && regenerateHbs == true) {
//								studentJSON.put(GlobalConstants.VALUE, propsMap.getProperty(objIDJson3 + "1")); // fullName1
//							} else if (objIDJson3.contains(GlobalConstants.FIRST_NAME) && regenerateHbs == true) {
//								studentJSON.put(GlobalConstants.VALUE, propsMap.getProperty(objIDJson3 + 1)); // fullName1
//							} else if (objIDJson3.contains(GlobalConstants.GENDER)) {
//								studentJSON.put(GlobalConstants.VALUE, propsMap.getProperty(objIDJson3));
//							} else {
//								studentJSON.put(GlobalConstants.VALUE, (propsMap.getProperty(objIDJson3) == null)
//										? "TEST_" + objIDJson3
//										: propsMap.getProperty(objIDJson3) + BaseTestCase.getLanguageList().get(j));
//							}
//							jArray.put(studentJSON);
//						}
//
//					}
//
//					JSONObject mainObj = new JSONObject();
//					mainObj.put(GlobalConstants.FULLNAME, jArray);
//
//					logger.info(mainObj);
//
//					fileWriter2 = new FileWriter(GlobalConstants.ADDIDENTITY_HBS, flag);
//					flag = true;
//					fileWriter2.write("\t  \"" + objIDJson3 + "\": \n\t   ");
//
//					fileWriter2.write(jArray.toString());
//					fileWriter2.write("\n\t,\n");
//					fileWriter2.close();
//
//				} else {
//
//					fileWriter2 = new FileWriter(GlobalConstants.ADDIDENTITY_HBS, flag);
//					flag = true;
//
//					if (objIDJson3.equals(GlobalConstants.PROOFOFIDENTITY)) {
//						fileWriter2.write("\t  \"proofOfIdentity\": {\n" + "\t\t\"format\": \"txt\",\n"
//								+ "\t\t\"type\": \"DOC001\",\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  },\n");
//					}
//
//					else if (objIDJson3.equals(result)) {
//						
//						if(phoneFieldAdditionallyAdded) {
//							fileWriter2
//							.write(",\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + "\n");
//						}
//						else {
//							fileWriter2
//							.write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//						}
//						
//						/*
//						 * fileWriter2 .write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" +
//						 * objIDJson3 + "}}\"" + ",\n");
//						 */
//					}
//
//					else if (objIDJson3.equals(emailResult)) {
//						if(emailFieldAdditionallyAdded) {
//							fileWriter2
//							.write(",\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + "\n");
//						}
//						else {
//							fileWriter2
//							.write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//						}
//						
//					}
//
//					else if (objIDJson3.equals(GlobalConstants.INDIVIDUALBIOMETRICS)) {
//						fileWriter2.write("\t  \"individualBiometrics\": {\n" + "\t\t\"format\": \"cbeff\",\n"
//								+ "\t\t\"version\": 1,\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  }\n");
//					} else if (objIDJson3.equals(GlobalConstants.PROOF_OF_ADDRESS)) {
//						fileWriter2.write("\t  \"proofOfAddress\": {\n" + "\t\t\"format\": \"txt\",\n"
//								+ "\t\t\"type\": \"DOC001\",\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  },\n");
//					}
//
//					else if (objIDJson3.equals(GlobalConstants.IDSCHEMAVERSION)) {
//						fileWriter2.write("\t  \"" + objIDJson3 + "\":" + " " + "" + "" + schemaVersion + "" + ",\n");
//					}
//
//					else {
//						fileWriter2
//								.write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//
//					}
//					fileWriter2.close();
//
//				}
//
//			}
//			fileWriter3 = new FileWriter(GlobalConstants.ADDIDENTITY_HBS, true);
//
//			fileWriter3.write("\t},\n");
//			fileWriter3.write("\t\"documents\": [\n" + "\t  {\n" + "\t\t\"value\": \"{{value}}\",\n"
//					+ "\t\t\"category\": \"{{category}}\"\n" + "\t  }\n" + "\t]\n");
//			fileWriter3.write("},\n");
//
//			fileWriter3.write("\t\"requesttime\": \"{{requesttime}}\",\n");
//			fileWriter3.write("\t\"version\": \"{{version}}\"\n");
//			fileWriter3.write("}\n");
//			fileWriter3.close();
//
//			fileReader = new FileReader(GlobalConstants.ADDIDENTITY_HBS);
//			bufferedReader = new BufferedReader(fileReader);
//			try {
//				StringBuilder sb = new StringBuilder();
//				String line = bufferedReader.readLine();
//
//				while (line != null) {
//					sb.append(line);
//					sb.append(System.lineSeparator());
//					line = bufferedReader.readLine();
//
//					StringBuffer everythingtrue = new StringBuffer(sb.toString());
//					everything = everythingtrue;
//				}
//
//			} finally {
//				bufferedReader.close();
//			}
//
//		} catch (NullPointerException | IOException e) {
//			logger.error(e.getMessage());
//		} finally {
//			closeFileWriter(fileWriter1);
//			closeFileWriter(fileWriter2);
//			closeFileWriter(fileWriter3);
//			closeFileReader(fileReader);
//			closeBufferedReader(bufferedReader);
//		}
//		identityHbs = everything.toString();
//		return identityHbs;
//	}

	public static String generateLatestSchemaVersion() {

		kernelAuthLib = new KernelAuthentication();
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
		String latestSchemaVersion = Double.toString(schemaVersion);
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
		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
		logger.info(schemaVersion);
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		try {
			JSONObject schemaFileJson = new JSONObject(schemaFile); // jObj
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties"); // objIDJson4
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity"); // objIDJson
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties"); // objIDJson2
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required"); // objIDJson1

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
					
					else if (eachRequiredProp.equals("individualBiometrics")) {
						identityJson.put(eachRequiredProp, new HashMap<>());
						identityJson.getJSONObject(eachRequiredProp).put("format", "cbeff");
						identityJson.getJSONObject(eachRequiredProp).put("version", 1);
						identityJson.getJSONObject(eachRequiredProp).put("value", "fileReferenceID");
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

			JSONArray requestDocArray = new JSONArray();
			JSONObject docJson = new JSONObject();
			docJson.put("value", "{{value}}");
			docJson.put("category", "individualBiometrics");
			requestDocArray.put(docJson);

			requestJson.getJSONObject("request").put("documents", requestDocArray);
			requestJson.getJSONObject("request").put("identity", identityJson);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}

		draftHbs = requestJson.toString();
		return draftHbs;
	}

//	public static String generateHbsForUpdateDraft() {
//		if (draftHbs != null) {
//			return draftHbs;
//		}
//		StringBuffer everything = new StringBuffer("");
//		kernelAuthLib = new KernelAuthentication();
//		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
//		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);
//
//		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
//				GlobalConstants.AUTHORIZATION, token);
//
//		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
//		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);
//
//		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
//		logger.info(schemaVersion);
//		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);
//
//		String schemaFile = schemaJsonData;
//
//		FileWriter fileWriter1 = null;
//		FileWriter fileWriter2 = null;
//		FileWriter fileWriter3 = null;
//		FileReader fileReader = null;
//		BufferedReader bufferedReader = null;
//
//		try {
//			JSONObject jObj = new JSONObject(schemaFile);
//			JSONObject objIDJson4 = jObj.getJSONObject(GlobalConstants.PROPERTIES);
//			JSONObject objIDJson = objIDJson4.getJSONObject(GlobalConstants.IDENTITY);
//			JSONObject objIDJson2 = objIDJson.getJSONObject(GlobalConstants.PROPERTIES);
//			JSONArray objIDJson1 = objIDJson.getJSONArray(GlobalConstants.REQUIRED);
//
//			fileWriter1 = new FileWriter(GlobalConstants.UPDATEDRAFT_HBS);
//			fileWriter1.write("{\n");
//			fileWriter1.write("  \"id\": \"{{id}}\",\n");
//			fileWriter1.write("  \"requesttime\": \"{{requesttime}}\",\n");
//			fileWriter1.write("  \"version\": \"{{version}}\",\n");
//			fileWriter1.write("  \"registrationId\": \"{{registrationId}}\",\n");
//			fileWriter1.write("  \"request\": {\n");
//
//			fileWriter1.write("    \"identity\": {\n");
//			fileWriter1.close();
//
//			boolean flag = true;
//			for (int i = 0, size = objIDJson1.length(); i < size; i++) {
//				String objIDJson3 = objIDJson1.getString(i);
//
//				JSONObject rc1 = (JSONObject) objIDJson2.get(objIDJson3);
//
//				if (rc1.has("$ref") && rc1.get("$ref").toString().contains(GlobalConstants.SIMPLETYPE)) {
//
//					JSONArray jArray = new JSONArray();
//					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
//
//						{
//							JSONObject studentJSON = new JSONObject();
//							studentJSON.put(GlobalConstants.LANGUAGE, BaseTestCase.getLanguageList().get(j));
//							studentJSON.put(GlobalConstants.VALUE,
//									(propsMap.getProperty(objIDJson3) == null) ? "TEST_" + objIDJson3
//											: propsMap.getProperty(objIDJson3));
//
//							jArray.put(studentJSON);
//						}
//					}
//
//					JSONObject mainObj = new JSONObject();
//					mainObj.put(GlobalConstants.FULLNAME, jArray);
//
//					logger.info(mainObj);
//
//					fileWriter2 = new FileWriter(GlobalConstants.UPDATEDRAFT_HBS, flag);
//					flag = true;
//					fileWriter2.write("\t  \"" + objIDJson3 + "\": \n\t   ");
//
//					fileWriter2.write(jArray.toString());
//					fileWriter2.write("\n\t  ,\n");
//					fileWriter2.close();
//
//				} else {
//
//					fileWriter2 = new FileWriter(GlobalConstants.UPDATEDRAFT_HBS, flag);
//					flag = true;
//
//					if (objIDJson3.equals(GlobalConstants.PROOFOFIDENTITY)) {
//						fileWriter2.write("\t  \"proofOfIdentity\": {\n" + "\t\t\"format\": \"txt\",\n"
//								+ "\t\t\"type\": \"DOC001\",\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  },\n");
//					}
//
//					else if (objIDJson3.equals(GlobalConstants.INDIVIDUALBIOMETRICS)) {
//						fileWriter2.write("\t  \"individualBiometrics\": {\n" + "\t\t\"format\": \"cbeff\",\n"
//								+ "\t\t\"version\": 1,\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  }\n");
//					}
//
//					else if (objIDJson3.equals(GlobalConstants.IDSCHEMAVERSION)) {
//						fileWriter2.write("\t  \"" + objIDJson3 + "\":" + " " + "" + "" + schemaVersion + "" + ",\n");
//					}
//					else if (objIDJson3.equals(GlobalConstants.PROOF_OF_ADDRESS)) {
//						fileWriter2.write("\t  \"proofOfAddress\": {\n" + "\t\t\"format\": \"txt\",\n"
//								+ "\t\t\"type\": \"DOC001\",\n" + "\t\t\"value\": \"fileReferenceID\"\n" + "\t  },\n");
//					}
//
//					else {
//						fileWriter2
//								.write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//
//					}
//					fileWriter2.close();
//
//				}
//
//			}
//			fileWriter3 = new FileWriter(GlobalConstants.UPDATEDRAFT_HBS, true);
//
//			fileWriter3.write("\t},\n");
//			fileWriter3.write("\t\"documents\": [\n" + "\t  {\n" + "\t\t\"value\": \"{{value}}\",\n"
//					+ "\t\t\"category\": \"{{category}}\"\n" + "\t  }\n" + "\t]\n");
//			fileWriter3.write("},\n");
//
//			fileWriter3.write("}\n");
//			fileWriter3.close();
//
//			fileReader = new FileReader(GlobalConstants.UPDATEDRAFT_HBS);
//			bufferedReader = new BufferedReader(fileReader);
//			try {
//				StringBuilder sb = new StringBuilder();
//				String line = bufferedReader.readLine();
//
//				while (line != null) {
//					sb.append(line);
//					sb.append(System.lineSeparator());
//					line = bufferedReader.readLine();
//
//					StringBuffer everythingtrue = new StringBuffer(sb.toString());
//					everything = everythingtrue;
//
//				}
//
//			} finally {
//				bufferedReader.close();
//			}
//
//		} catch (NullPointerException | IOException e) {
//			logger.error(e.getMessage());
//		} finally {
//			closeFileWriter(fileWriter1);
//			closeFileWriter(fileWriter2);
//			closeFileWriter(fileWriter3);
//			closeFileReader(fileReader);
//			closeBufferedReader(bufferedReader);
//		}
//		draftHbs = everything.toString();
//		return draftHbs;
//	}
	
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
		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);

		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
		logger.info(schemaVersion);
		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);

		String schemaFile = schemaJsonData;

		try {
			
			JSONObject schemaFileJson = new JSONObject(schemaFile); // jObj
			JSONObject schemaPropsJson = schemaFileJson.getJSONObject("properties"); // objIDJson4
			JSONObject schemaIdentityJson = schemaPropsJson.getJSONObject("identity"); // objIDJson
			JSONObject identityPropsJson = schemaIdentityJson.getJSONObject("properties"); // objIDJson2
			JSONArray requiredPropsArray = schemaIdentityJson.getJSONArray("required"); // objIDJson1			
			
			boolean emailFieldAdditionallyAdded=false;
			boolean phoneFieldAdditionallyAdded=false;
			String phone = getValueFromAuthActuator("json-property", "phone_number");
			String result = phone.replaceAll("\\[\"|\"\\]", "");

			if (!isElementPresent(requiredPropsArray, result)) {
				requiredPropsArray.put(result);
				phoneFieldAdditionallyAdded=true;
			}

			//System.out.println("result is:" + result);
			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");
			if (!isElementPresent(requiredPropsArray, emailResult)) {
				requiredPropsArray.put(emailResult);
				emailFieldAdditionallyAdded=true;
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
						eachValueJson.put("value", (propsMap.getProperty(eachRequiredProp) == null) ? "TEST_" + eachRequiredProp
								: propsMap.getProperty(eachRequiredProp));
						eachPropDataArray.put(eachValueJson);
					}
					identityJson.put(eachRequiredProp, eachPropDataArray);
				}
				else {

					if (eachRequiredProp.equals("IDSchemaVersion")) {
						identityJson.put(eachRequiredProp, schemaVersion);
					}
					else {
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

//	public static String generateHbsForPrereg(boolean isItUpdate) {
//		if (isItUpdate && preregHbsForUpdate != null) {
//			return preregHbsForUpdate;
//		}
//
//		if (!isItUpdate && preregHbsForCreate != null) {
//			return preregHbsForCreate;
//		}
//		StringBuffer everything = new StringBuffer("");
//		kernelAuthLib = new KernelAuthentication();
//		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
//		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);
//
//		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
//				GlobalConstants.AUTHORIZATION, token);
//
//		org.json.JSONObject responseJson = new org.json.JSONObject(response.asString());
//		org.json.JSONObject schemaData = (org.json.JSONObject) responseJson.get(GlobalConstants.RESPONSE);
//
//		Double schemaVersion = (Double) schemaData.get(GlobalConstants.ID_VERSION);
//		logger.info(schemaVersion);
//		String schemaJsonData = schemaData.getString(GlobalConstants.SCHEMA_JSON);
//
//		String schemaFile = schemaJsonData;
//
//		FileWriter fileWriter1 = null;
//		FileWriter fileWriter2 = null;
//		FileWriter fileWriter3 = null;
//		FileReader fileReader = null;
//		BufferedReader bufferedReader = null;
//
//		try {
//			JSONObject jObj = new JSONObject(schemaFile);
//			JSONObject objIDJson4 = jObj.getJSONObject(GlobalConstants.PROPERTIES);
//			logger.info(objIDJson4);
//			JSONObject objIDJson = objIDJson4.getJSONObject(GlobalConstants.IDENTITY);
//			logger.info(objIDJson);
//			JSONObject objIDJson2 = objIDJson.getJSONObject(GlobalConstants.PROPERTIES);
//			logger.info(objIDJson2);
//			JSONArray objIDJson1 = objIDJson.getJSONArray(GlobalConstants.REQUIRED);
//			logger.info(objIDJson1);
//			boolean emailFieldAdditionallyAdded=false;
//			boolean phoneFieldAdditionallyAdded=false;
//			String phone = getValueFromAuthActuator("json-property", "phone_number");
//			String result = phone.replaceAll("\\[\"|\"\\]", "");
//
//			if (!isElementPresent(objIDJson1, result)) {
//				objIDJson1.put(result);
//				phoneFieldAdditionallyAdded=true;
//			}
//
//			//System.out.println("result is:" + result);
//			String email = getValueFromAuthActuator("json-property", "emailId");
//			String emailResult = email.replaceAll("\\[\"|\"\\]", "");
//			if (!isElementPresent(objIDJson1, emailResult)) {
//				objIDJson1.put(emailResult);
//				emailFieldAdditionallyAdded=true;
//			}
//
//			ArrayList<String> list = new ArrayList<>();
//
//			if (objIDJson1 != null) {
//				int len = objIDJson1.length();
//				for (int i = 0; i < len; i++) {
//					list.add(objIDJson1.get(i).toString());
//				}
//			}
//			list.remove(GlobalConstants.RESIDENCESTATUS);
//			list.remove("addressCopy");
//			list.remove("proofOfAddress");
//			list.remove(GlobalConstants.RESIDENCESTATUS);
//			list.add(GlobalConstants.RESIDENCESTATUS);
//			if (list.contains(GlobalConstants.PROOFOFIDENTITY)) {
//				list.remove(GlobalConstants.PROOFOFIDENTITY);
//			}
//
//			if (list.contains(GlobalConstants.INDIVIDUALBIOMETRICS)) {
//				list.remove(GlobalConstants.INDIVIDUALBIOMETRICS);
//			}
//
//			JSONArray newIdJson = new JSONArray(list);
//
//			fileWriter1 = new FileWriter(GlobalConstants.CREATEPREREG_HBS);
//			fileWriter1.write("{\n");
//			fileWriter1.write("  \"id\": \"{{id}}\",\n");
//			if (isItUpdate) {
//				fileWriter1.write("  \"preRegistrationId\": \"{{preRegistrationId}}\",\n");
//			}
//
//			fileWriter1.write("  \"requesttime\": \"{{requesttime}}\",\n");
//			fileWriter1.write("  \"version\": \"{{version}}\",\n");
//			fileWriter1.write("  \"request\": {\n");
//			fileWriter1.write("    \"langCode\": \"{{langCode}}\",\n");
//			fileWriter1.write("    \"requiredFields\": " + newIdJson + ",\n");
//			fileWriter1.write("    \"demographicDetails\": {\n");
//
//			fileWriter1.write("      \"identity\": {\n");
//
//			fileWriter1.close();
//
//			boolean flag = true;
//			for (int i = 0, size = newIdJson.length(); i < size; i++) {
//				String objIDJson3 = newIdJson.getString(i);
//
//				JSONObject rc1 = (JSONObject) objIDJson2.get(objIDJson3);
//
//				if ((rc1.has("$ref") && rc1.get("$ref").toString().contains(GlobalConstants.SIMPLETYPE))
//						|| objIDJson3.contains(GlobalConstants.RESIDENCESTATUS)) {
//
//					JSONArray jArray = new JSONArray();
//					for (int j = 0; j < BaseTestCase.getLanguageList().size(); j++) {
//
//						{
//							JSONObject studentJSON = new JSONObject();
//							studentJSON.put(GlobalConstants.LANGUAGE, BaseTestCase.getLanguageList().get(j));
//							studentJSON.put(GlobalConstants.VALUE,
//									(propsMap.getProperty(objIDJson3) == null) ? "TEST_" + objIDJson3
//											: propsMap.getProperty(objIDJson3));
//							jArray.put(studentJSON);
//						}
//					}
//
//					JSONObject mainObj = new JSONObject();
//
//					logger.info(mainObj);
//
//					fileWriter2 = new FileWriter(GlobalConstants.CREATEPREREG_HBS, flag);
//					flag = true;
//					fileWriter2.write("\t  ,\"" + objIDJson3 + "\": ");
//
//					fileWriter2.write(jArray.toString());
//					fileWriter2.write("\t");
//
//					if (jArray.toString().contains(GlobalConstants.RESIDENCESTATUS)
//							|| objIDJson3.contains(GlobalConstants.RESIDENCESTATUS)) {
//						fileWriter2.write("\n\t  \n}\n}\n}\n}\n");
//					} else {
//						fileWriter2.write("\n\t  \n");
//					} 
//					 
//
//					fileWriter2.close();
//
//				} else {
//
//					fileWriter2 = new FileWriter(GlobalConstants.CREATEPREREG_HBS, flag);
//					flag = true;
//
//					if (i == size - 1) {
//						fileWriter2.write("\t  ,\"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\""
//								+ "\n}\n}\n}\n}");
//
//					}
//
//					else if (objIDJson3.equals(GlobalConstants.IDSCHEMAVERSION)) {
//						fileWriter2.write("\t  \"" + objIDJson3 + "\":" + " " + "" + "" + schemaVersion + "" + "\n");
//					}
//					
//					else if (objIDJson3.equals(result)) {
//
//						if (phoneFieldAdditionallyAdded) {
//							fileWriter2.write(
//									",\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + "\n");
//						} else {
//							fileWriter2.write(
//									"\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//						}
//
//						/*
//						 * fileWriter2 .write("\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" +
//						 * objIDJson3 + "}}\"" + ",\n");
//						 */
//					}
//
//					else if (objIDJson3.equals(emailResult)) {
//						if (emailFieldAdditionallyAdded) {
//							fileWriter2.write(
//									",\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + "\n");
//						} else {
//							fileWriter2.write(
//									"\t  \"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + ",\n");
//						}
//
//					}
//
//					else {
//						fileWriter2
//								.write("\t  ,\"" + objIDJson3 + "\":" + " " + "\"" + "{{" + objIDJson3 + "}}\"" + "\n");
//
//					}
//
//					fileWriter2.close();
//
//				}
//
//			}
//			fileWriter3 = new FileWriter(GlobalConstants.CREATEPREREG_HBS, true);
//
//			fileWriter3.close();
//
//			fileReader = new FileReader(GlobalConstants.CREATEPREREG_HBS);
//
//			bufferedReader = new BufferedReader(fileReader);
//			try {
//				StringBuilder sb = new StringBuilder();
//				String line = bufferedReader.readLine();
//
//				while (line != null) {
//					sb.append(line);
//					sb.append(System.lineSeparator());
//					line = bufferedReader.readLine();
//
//					StringBuffer everythingtrue = new StringBuffer(sb.toString());
//					everything = everythingtrue;
//
//				}
//
//			} finally {
//				bufferedReader.close();
//			}
//
//		} catch (IOException | NullPointerException e) {
//			logger.error(e.getMessage());
//		} finally {
//			closeFileWriter(fileWriter1);
//			closeFileWriter(fileWriter2);
//			closeFileWriter(fileWriter3);
//			closeFileReader(fileReader);
//			closeBufferedReader(bufferedReader);
//		}
//		if (isItUpdate) {
//
//			preregHbsForUpdate = everything.toString();
//			return preregHbsForUpdate;
//		}
//		preregHbsForCreate = everything.toString();
//		return preregHbsForCreate;
//	}

	@SuppressWarnings("unchecked")
	public static void createAndPublishPolicy() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Key manager
			// cert store and IDA cert store
			// So use the predefined certificate folder and partner key
			return;
		}

		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group");
		modifiedReq.put("name", policyGroup);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);

		Response response2 = RestClient.postRequestWithCookie(url2, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody2 = response2.getBody().asString();
		String policygroupId = new org.json.JSONObject(responseBody2).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");
		
		String urlForUpdate = ApplnURI + properties.getProperty("authPolicyUrl")+"/"+policygroupId;
		Response responseForUpdate = RestClient.putRequestWithCookie(urlForUpdate, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(AUTH_POLICY_REQUEST_ATTR);

		actualrequest2.put("name", policyName);
		actualrequest2.put("policyGroupName", policyGroup);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);

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
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Key manager
			// cert store and IDA cert store
			// So use the predefined certificate folder and partner key
			return;
		}

		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group for update");
		modifiedReq.put("name", policyGroup);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);

		Response response2 = RestClient.postRequestWithCookie(url2, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody2 = response2.getBody().asString();
		String policygroupId = new org.json.JSONObject(responseBody2).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");
		String urlForUpdate = ApplnURI + properties.getProperty("authPolicyUrl")+"/"+policygroupId;
		Response responseForUpdate = RestClient.postRequestWithCookie(urlForUpdate, actualrequest, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(AUTH_POLICY_REQUEST_ATTR);

		actualrequest2.put("name", policyNameForUpdate);
		actualrequest2.put("policyGroupName", policyGroup);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);

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
	public static void createAndPublishPolicyForKyc() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Key manager
			// cert store and IDA cert store
			// So use the predefined certificate folder and partner key
			return;
		}

		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url2 = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject actualrequest = getRequestJson(POLICY_GROUP_REQUEST);

		org.json.simple.JSONObject modifiedReq = new org.json.simple.JSONObject();
		modifiedReq.put("desc", "desc mosip auth policy group");
		modifiedReq.put("name", policyGroup2);

		actualrequest.put(GlobalConstants.REQUEST, modifiedReq);

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

		Response response = RestClient.postRequestWithCookie(url, actualrequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String responseBody = response.getBody().asString();
		String policyId2 = new org.json.JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE).getString("id");

		String url3 = ApplnURI + properties.getProperty("publishPolicyurl");

		if (url3.contains("POLICYID")) {
			url3 = url3.replace("POLICYID", policyId2);
			url3 = url3.replace("POLICYGROUPID", policygroupId2);

		}

		Response response3 = RestClient.postRequestWithCookie(url3, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

	}

//	public static String getJWKKey(File fileName) {
//		String keyString = null;
//		try {
//			if (fileName.exists()) {
//				keyString = FileUtils.readFileToString(fileName, StandardCharset.UTF_8);
//			}
//			return keyString;
//		} catch (IOException e1) {
//			logger.error("Exception while getting oidcJWKKey for client assertion: " + e1.getMessage());
//			return null;
//		}
//	}

	public static String signJWKKey(String clientId, RSAKey jwkKey) {
		String tempUrl = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.iam.token_endpoint");
		int idTokenExpirySecs = Integer.parseInt(getValueFromEsignetActuator(GlobalConstants.ESIGNET_DEFAULT_PROPERTIES,
				GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;

		try {
			signer = new RSASSASigner(jwkKey);

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(clientId).audience(tempUrl).issuer(clientId)
					.issueTime(new Date()).expirationTime(new Date(new Date().getTime() + idTokenExpirySecs)).build();

			SignedJWT signedJWT = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwkKey.getKeyID()).build(), claimsSet);

			signedJWT.sign(signer);
			clientAssertionToken = signedJWT.serialize();
		} catch (Exception e) {
			logger.error("Exception while signing oidcJWKKey for client assertion: " + e.getMessage());
		}
		return clientAssertionToken;
	}

	public static String getWlaToken(String individualId, RSAKey jwkKey, String certData)
			throws JoseException, JOSEException {
		String tempUrl = propsKernel.getProperty("validateBindingEndpoint");
		int idTokenExpirySecs = Integer.parseInt(getValueFromEsignetActuator(GlobalConstants.ESIGNET_DEFAULT_PROPERTIES,
				GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		Instant instant = Instant.now();
		long epochValue = instant.getEpochSecond();

		JSONObject payload = new JSONObject();
		payload.put("iss", "postman-inji");
		payload.put("aud", tempUrl);
		payload.put("sub", individualId);
		payload.put("iat", epochValue);
		payload.put("exp", epochValue + idTokenExpirySecs);

		X509Certificate certificate = (X509Certificate) convertToCertificate(certData);
		JsonWebSignature jwSign = new JsonWebSignature();
		if (certificate != null) {
			jwSign.setKeyIdHeaderValue(certificate.getSerialNumber().toString(10));
			jwSign.setX509CertSha256ThumbprintHeaderValue(certificate);
			jwSign.setPayload(payload.toString());
			jwSign.setAlgorithmHeaderValue(SIGN_ALGO);
			jwSign.setKey(jwkKey.toPrivateKey());
			jwSign.setDoKeyValidation(false);
			return jwSign.getCompactSerialization();
		}
		return "";
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
		try {
			StringReader strReader = new StringReader(certData);
			PemReader pemReader = new PemReader(strReader);
			PemObject pemObject = pemReader.readPemObject();
			if (Objects.isNull(pemObject)) {
				return null;
			}
			byte[] certBytes = pemObject.getContent();
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
		} catch (IOException | CertificateException e) {
			return null;
		}
	}

	private static String otpExpTime = "";

	public static int getOtpExpTimeFromActuator() {
		if (otpExpTime.isEmpty()) {
			String section = "/mosip-config/application-default.properties";
			if (!BaseTestCase.isTargetEnvLTS())
				section = "/mosip-config/sandbox/application-lts.properties";
			Response response = null;
			org.json.JSONObject responseJson = null;
			JSONArray responseArray = null;
			String url = ApplnURI + propsKernel.getProperty("actuatorIDAEndpoint");
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
							logger.info("Actuator: " +url +" otpExpTime: "+otpExpTime);
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
		String url = ApplnURI + propsKernel.getProperty("actuatorEndpoint");
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
						logger.info("Actuator: " +url + " key: "+key+" value: "+value);
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
	
	public static JSONArray esignetActuatorResponseArray = null;

	public static String getValueFromEsignetActuator(String section, String key) {
		String url = ConfigManager.getEsignetBaseUrl() + propsKernel.getProperty("actuatorEsignetEndpoint");
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;
		
		try {
			if (esignetActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				responseJson = new JSONObject(response.getBody().asString());
				esignetActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}

			for (int i = 0, size = esignetActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = esignetActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(section)) {
					value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
							.get(GlobalConstants.VALUE).toString();
					if (ConfigManager.IsDebugEnabled())
						logger.info("Actuator: " +url + " key: "+key+" value: "+value);
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
	
	public static JSONArray authActuatorResponseArray = null;

	public static String getValueFromAuthActuator(String section, String key) {
		String url = ApplnURI + propsKernel.getProperty("actuatorIDAEndpoint");
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
						logger.info("Actuator: " +url + " key: "+key+" value: "+value);
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
	
	public static JSONArray configActuatorResponseArray = null;

	public static String getValueFromConfigActuator() {

		String url = ApplnURI + propsKernel.getProperty("actuatorEndpoint");

		String actuatorCacheKey = url + "mosip.iam.module.login_flow.claims";

		String claims = actuatorValueCache.get(actuatorCacheKey);

		if (claims != null && !claims.isEmpty())
			return claims;

		try {
			if (configActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

				responseJson = new JSONObject(response.getBody().asString());
				configActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}

			for (int i = 0, size = configActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = configActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES)) {
					String claimVal = eachJson.getJSONObject(GlobalConstants.PROPERTIES)
							.getJSONObject("mosip.iam.module.login_flow.claims").getString(GlobalConstants.VALUE);
					JSONObject claimJson = new JSONObject(claimVal);
					claims = claimJson.getJSONObject("userinfo").toString();
					break;
				}
			}

			actuatorValueCache.put(actuatorCacheKey, claims);

			return claims;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return claims;
		}

	}
	
	public static JSONArray regProcActuatorResponseArray = null;

	public static String getRegprocWaitFromActuator() {
		String url = ApplnURI + propsKernel.getProperty("actuatorRegprocEndpoint");
		
		String actuatorCacheKey = url + "registration.processor.reprocess.minutes";
		String waitInterval = actuatorValueCache.get(actuatorCacheKey);
		if (waitInterval != null && !waitInterval.isEmpty())
			return waitInterval;
		
		try {
			if (regProcActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

				responseJson = new JSONObject(response.getBody().asString());
				regProcActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}

			for (int i = 0, size = regProcActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = regProcActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains("registration-processor-default.properties")) {
					waitInterval = eachJson.getJSONObject(GlobalConstants.PROPERTIES)
							.getJSONObject("registration.processor.reprocess.minutes").get(GlobalConstants.VALUE)
							.toString();
					break;
				}
			}
			
			actuatorValueCache.put(actuatorCacheKey, waitInterval);

			return waitInterval;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return waitInterval;
		}
	}

	public static String isTestCaseValidForExecution(TestCaseDTO testCaseDTO) {
		String testCaseName = testCaseDTO.getTestCaseName();
		if ((!ConfigManager.IseSignetDeployed()) && BaseTestCase.currentModule.equalsIgnoreCase("resident")
				&& testCaseName.contains("_SignJWT_")) {
			throw new SkipException("esignet module is not deployed");
		}

		if ((!ConfigManager.IseSignetDeployed()) && BaseTestCase.currentModule.equalsIgnoreCase("resident")
				&& (testCaseDTO.getRole() != null && (testCaseDTO.getRole().equalsIgnoreCase("residentNew")
						|| testCaseDTO.getRole().equalsIgnoreCase("residentNewVid")))) {
			throw new SkipException("esignet module is not deployed");
		}
		if (BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.ESIGNET)) {
			if (testCaseDTO.getRole() != null && (testCaseDTO.getRole().equalsIgnoreCase(GlobalConstants.RESIDENTNEW)
					|| testCaseDTO.isValidityCheckRequired())) {
				if (testCaseName.contains("uin") || testCaseName.contains("UIN") || testCaseName.contains("Uin")) {
					if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
							&& BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
						throw new SkipException("Idtype UIN not supported skipping the testcase");
					}
				}
			} else if (testCaseDTO.getRole() != null && (testCaseDTO.getRole().equalsIgnoreCase("residentNewVid")
					|| testCaseDTO.isValidityCheckRequired())) {
				if (testCaseName.contains("vid") || testCaseName.contains("VID") || testCaseName.contains("Vid")) {
					if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
							&& BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
						throw new SkipException("Idtype VID not supported skipping the testcase");
					}
				}
			}
		}
		return testCaseName;
	}

	public static String smtpOtpHandler(String inputJson, String testCaseName) {

		JSONObject request = new JSONObject(inputJson);
		String emailId = null;
		String otp = null;
		if (BaseTestCase.currentModule.equals(GlobalConstants.MOBILEID) || testCaseName.startsWith("auth_OTP_Auth")
				|| testCaseName.startsWith("auth_EkycOtp") || testCaseName.startsWith("auth_MultiFactorAuth")
				|| testCaseName.startsWith("Ida_EkycOtp") || testCaseName.startsWith("Ida_OTP_Auth")) {
			if (request.has("otp")) {
				if (request.getString("otp").endsWith(GlobalConstants.MOSIP_NET)
						|| request.getString("otp").endsWith(GlobalConstants.MAILINATOR_COM)
						|| request.getString("otp").endsWith(GlobalConstants.MOSIP_IO)) {

					emailId = request.get("otp").toString();
					logger.info(emailId);
					otp = MockSMTPListener.getOtp(emailId);
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
					otp = MockSMTPListener.getOtp(emailId);
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
								.endsWith(GlobalConstants.MOSIP_NET)) {
							emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
							logger.info(emailId);
							otp = MockSMTPListener.getOtp(emailId);
							request.getJSONObject(GlobalConstants.REQUEST).put("otp", otp);
							inputJson = request.toString();
							return inputJson;
						}
					}
				}
			}
		}
		if (BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			if (testCaseName.startsWith("Resident_GenerateVID")
					|| testCaseName.startsWith("ESignet_AuthenticateUserIDP")
					|| testCaseName.startsWith("Resident_credential")) {
				if (request.has(GlobalConstants.REQUEST)) {
					if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
						if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
								.endsWith(GlobalConstants.MAILINATOR_COM)
								|| request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
										.endsWith(GlobalConstants.MOSIP_IO)) {
							emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
							logger.info(emailId);
							otp = MockSMTPListener.getOtp(emailId);
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
													.endsWith(GlobalConstants.MOSIP_IO)) {
										emailId = request.getJSONObject(GlobalConstants.REQUEST)
												.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
												.getString(GlobalConstants.CHALLENGE);
										logger.info(emailId);
										otp = MockSMTPListener.getOtp(emailId);
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
				|| testCaseName.startsWith("MobileId_WalletBinding")) {
			if (request.has(GlobalConstants.REQUEST)) {
				if (request.getJSONObject(GlobalConstants.REQUEST).has("otp")) {
					if (request.getJSONObject(GlobalConstants.REQUEST).getString("otp")
							.endsWith(GlobalConstants.MOSIP_NET)) {
						emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
						logger.info(emailId);
						otp = MockSMTPListener.getOtp(emailId);
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
										.getString(GlobalConstants.CHALLENGE).endsWith(GlobalConstants.MOSIP_NET)) {
									emailId = request.getJSONObject(GlobalConstants.REQUEST)
											.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
											.getString(GlobalConstants.CHALLENGE);
									logger.info(emailId);
									otp = MockSMTPListener.getOtp(emailId);
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
							.endsWith(GlobalConstants.MOSIP_NET)) {
						emailId = request.getJSONObject(GlobalConstants.REQUEST).get("otp").toString();
						logger.info(emailId);
						otp = MockSMTPListener.getOtp(emailId);
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
									.getString(GlobalConstants.CHALLENGE).endsWith(GlobalConstants.MOSIP_NET)) {
								emailId = request.getJSONObject(GlobalConstants.REQUEST)
										.getJSONArray(GlobalConstants.CHALLENGELIST).getJSONObject(0)
										.getString(GlobalConstants.CHALLENGE);
								logger.info(emailId);
								otp = MockSMTPListener.getOtp(emailId);
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

	public static void checkDbAndValidate(String timeStamp, String dbChecker) throws AdminTestException {

		String sqlQuery = "SELECT * FROM audit.app_audit_log WHERE log_dtimes > '" + timeStamp
				+ "' AND session_user_name = '" + dbChecker + "';";

		Map<String, Object> response = AuditDBManager
				.executeQueryAndGetRecord(propsKernel.getProperty("audit_default_schema"), sqlQuery);

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

					stringBuilder.append("\n")
							.append(getCommitDetails(BaseTestCase.ApplnURI + parts[1].replace("health", "info")));
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
			return "Group: " + jsonResponse.getJSONObject("build").getString("group") + ", Artifact: "
					+ jsonResponse.getJSONObject("build").getString("artifact") + ", version: "
					+ jsonResponse.getJSONObject("build").getString("version") + ", Commit ID: "
					+ jsonResponse.getJSONObject("git").getJSONObject("commit").getString("id");
		}
		return path + "- No Response";
	}

	public static void getLocationData() {

		Response response = null;
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationData");
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		try {
			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					GlobalConstants.AUTHORIZATION, token);

			responseJson = new JSONObject(response.getBody().asString());

			try {
				JSONObject responseObject = responseJson.getJSONObject("response");
				JSONArray data = responseObject.getJSONArray("data");

				for (int i = 0; i < data.length(); i++) {
					JSONObject entry = data.getJSONObject(i);
					String langCode = entry.getString("langCode");

					if (BaseTestCase.languageList.get(0).equals(langCode)) {
						hierarchyName = entry.getString("hierarchyName");
						hierarchyLevel = entry.getInt("hierarchyLevel");
						parentLocCode = entry.optString("parentLocCode", "");
						break;
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
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationHierarchyLevels") + BaseTestCase.getLanguageList().get(0);
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
		String topLevelName = null;
		String url2 = "";
		
		Response responseLocationHierarchy = null;
		JSONObject responseJsonLocationHierarchy = null;

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

			response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

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
			
			GlobalMethods.reportRequest(null, map.toString());

		Response response = RestClient.postRequestWithQueryParamsAndBody(url, map, queryParamMap,
				MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		return response.getBody().asString();
	}
	
	public static String getValueFromUrl(String url,String dataToFetch) {
		String idValue="";
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
	
	

}
