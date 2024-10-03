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
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
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
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.PdfReader;
import com.mifmif.common.regex.Generex;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dataprovider.BiometricDataProvider;
import io.mosip.testrig.apirig.dbaccess.AuditDBManager;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.testrunner.MessagePrecondtion;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 * @author Sohan
 *
 */
public class AdminTestUtil extends BaseTestCase {

	private static final Logger logger = Logger.getLogger(AdminTestUtil.class);
	protected static final Properties properties = getproperty(
			getGlobalResourcePath() + "/" + "config/application.properties");
	protected static final Properties propsMap = getproperty(
			getGlobalResourcePath() + "/" + "config/valueMapping.properties");
	protected static final Properties propsBio = getproperty(
			getGlobalResourcePath() + "/" + "config/bioValue.properties");
	protected static final Properties propsKernel = getproperty(
			getGlobalResourcePath() + "/" + "config/Kernel.properties");
	public static String propsHealthCheckURL = getGlobalResourcePath() + "/"
			+ "config/healthCheckEndpoint.properties";
	private static String serverComponentsCommitDetails;
	private static boolean foundHandlesInIdSchema= false;

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
	String mimotoAutoGeneratedIdPropFileName = properties.getProperty("mimotoAutoGeneratedIdPropFileName");
	String pmsAutoGeneratedIdPropFileName = properties.getProperty("pmsAutoGeneratedIdPropFileName");
	String injiCertifyAutoGeneratedIdPropFileName = properties.getProperty("injiCertifyAutoGeneratedIdPropFileName");
	String fullNameForSunBirdRC = properties.getProperty("fullNameForSunBirdRC");
	String dobForSunBirdRC = properties.getProperty("dobForSunBirdRC");
	
	public static final String PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION = properties
			.getProperty("passwordForAddIdentity");
	public static final String PASSWORD_TO_RESET = properties.getProperty("passwordToReset");
	public static final String RESOURCE_FOLDER_NAME = "MosipTemporaryTestResource";
	protected static String genertedUIN = null;
	protected static String generatedRid = null;
	protected static String policygroupId = null;
	protected static String regDeviceResponse = null;
	protected static String generatedVID = null;
	public  String RANDOM_ID = "mosip" + generateRandomNumberString(2)
			+ Calendar.getInstance().getTimeInMillis();
	public  final String RANDOM_ID_2 = "mosip" + generateRandomNumberString(2)
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
//	public static BioDataUtility bioDataUtil = new BioDataUtility();
//
//	public static BioDataUtility getBioDataUtil() {
//		return bioDataUtil;
//	}

//	public static EncryptionDecrptionUtil encryptDecryptUtil = null;
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
	protected static final String ESIGNET_PAYLOAD = "config/esignetPayload.json";
	protected static Map<String, String> keycloakRolesMap = new HashMap<>();
	protected static Map<String, String> keycloakUsersMap = new HashMap<>();
	protected static RSAKey oidcJWKKey1 = null;
	protected static RSAKey oidcJWKKey3 = null;
	protected static RSAKey oidcJWKKey4 = null;

	protected static final String OIDCJWK1 = "oidcJWK1";
	protected static final String OIDCJWK2 = "oidcJWK2";
	protected static final String OIDCJWK3 = "oidcJWK3";
	protected static final String OIDCJWK4 = "oidcJWK4";
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
	private static String selectedHandlesValue=null;

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
		return triggerESignetKeyGen12;
	}

	protected static boolean triggerESignetKeyGen13 = true;

	private static void settriggerESignetKeyGen13(boolean value) {
		triggerESignetKeyGen13 = value;
	}

	private static boolean gettriggerESignetKeyGen13() {
		return triggerESignetKeyGen13;
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		jsonInput = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		token = kernelAuthLib.getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
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
		String headerTransactionID = "";
		String headerVerifedTransactionID = "";
		Map<String, String> cookiesMap = new HashMap<>();

		if (request.has(GlobalConstants.REQUEST)
				&& request.getJSONObject(GlobalConstants.REQUEST).has(GlobalConstants.TRANSACTIONID)) {
			transactionId = request.getJSONObject(GlobalConstants.REQUEST).get(GlobalConstants.TRANSACTIONID)
					.toString();
			headers.put(OAUTH_TRANSID_HEADERNAME, transactionId);
		}

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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			if (cookiesMap.containsKey(GlobalConstants.TRANSACTION_ID_KEY)
					|| cookiesMap.containsKey(GlobalConstants.VERIFIED_TRANSACTION_ID_KEY)) {
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
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			if (testCaseName.contains("_STransId"))
				getvalueFromResponseHeader(response, testCaseName);

			return response;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

	public void getvalueFromResponseHeader(Response response, String testCaseName) {

		if (testCaseName.contains("_STransId") && response.getHeaders().hasHeaderWithName("set-cookie")) {
			String headerTransactionId = "";
			String headerVerifyTransactionId = "";

			List<String> ListOfSetCookieValues = response.getHeaders().getValues("set-cookie");

			for (String eachSetCookieValues : ListOfSetCookieValues) {
				String[] setCookieValues = eachSetCookieValues.split(";");
				for (String eachSetCookieValue : setCookieValues) {
					if (eachSetCookieValue.trim().startsWith("VERIFIED_TRANSACTION_ID=")) {
						if (!eachSetCookieValue.split("=")[1].isBlank()) {
							headerVerifyTransactionId = eachSetCookieValue.split("=")[1];
							writeAutoGeneratedId(testCaseName, "VTransactionID", headerVerifyTransactionId);
							break;
						}
					}
					if (eachSetCookieValue.trim().startsWith("TRANSACTION_ID=")) {
						if (!eachSetCookieValue.split("=")[1].isBlank()) {
							headerTransactionId = eachSetCookieValue.split("=")[1];
							writeAutoGeneratedId(testCaseName, "TransactionID", headerTransactionId);
							break;
						}
					}
				}
			}
		}
	}

	protected Response postWithBodyAndCookieAuthHeaderAndXsrfTokenForAutoGeneratedId(String url, String jsonInput,
			String cookieName, String testCaseName, String idKeyName) {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);

		url = inputJsonKeyWordHandeler(url, testCaseName);
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		headers.put(XSRF_HEADERNAME, properties.getProperty(GlobalConstants.XSRFTOKEN));
		token = properties.getProperty(GlobalConstants.XSRFTOKEN);

		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)) {
			inputJson = smtpOtpHandler(inputJson, testCaseName);
		}

		token = properties.getProperty(GlobalConstants.XSRFTOKEN);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		} else if (testCaseName.contains("Wla_Vid_")) {
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);

		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return response;

	}

	protected Response postRequestWithAuthHeaderAndSignatureForOtpAutoGenId(String url, String jsonInput,
			String cookieName, String token, Map<String, String> headers, String testCaseName, String idKeyName) {
		Response response = null;
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		url = uriKeyWordHandelerUri(url, testCaseName);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);

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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
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
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		if (BaseTestCase.currentModule.equals(GlobalConstants.MIMOTO) || BaseTestCase.currentModule.equals("auth")
				|| BaseTestCase.currentModule.equals(GlobalConstants.ESIGNET)
				|| BaseTestCase.currentModule.equals(GlobalConstants.RESIDENT)
				|| BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)
				|| BaseTestCase.currentModule.equals(GlobalConstants.PREREG)) {
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
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else {
			token = kernelAuthLib.getTokenByRole(role);
		}
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(null, inputJson, url);
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
			GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, req.toString(), url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, jsonInput, url);

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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, req.toString(), url);
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
		GlobalMethods.reportRequest(null, req.toString(), url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(headers.toString(), req.toString(), url);
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
		GlobalMethods.reportRequest(null, inputJson, url);
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
		GlobalMethods.reportRequest(null, req.toString(), url);
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
		String headerTransactionID = "";
		Map<String, String> cookiesMap = new HashMap<>();
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

		if (bothAccessAndIdToken) {
			token = kernelAuthLib.getTokenByRole(role, ACCESSTOKENCOOKIENAME);
			idToken = kernelAuthLib.getTokenByRole(role, IDTOKENCOOKIENAME);
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
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
		} else if (testCaseName.contains("NOAUTH")) {
			token = "";
		} else {
			token = kernelAuthLib.getTokenByRole(role);
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
					|| testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)
					|| testCaseName.contains("_FullResponse")) {
				responseBody = new JSONObject(response.getBody().asString());
				if (testCaseName.contains(GlobalConstants.ESIGNET_KYCCREATEAUTHREQ)) {
					signature = response.getHeader(GlobalConstants.SIGNATURE);
				}
			} else if (testCaseName.contains("SunBirdR_")) {
				responseJson = new JSONObject(response.getBody().asString()).getJSONObject(GlobalConstants.RESULT)
						.getJSONObject(GlobalConstants.INSURANCE);
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
					else if (responseJson.has("partners")) {
						org.json.JSONArray responseArray = responseJson.getJSONArray("partners");
						String authPartnerId = getPartnerId(responseArray, "active", "Auth_Partner");
						props.put(identifierKeyName, authPartnerId);
					} else if (responseJson.has("data")) {
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
		if ((!BaseTestCase.isTargetEnvLTS()) && fieldName.equals("VID")
				&& (BaseTestCase.currentModule.equals("auth") || BaseTestCase.currentModule.equals("esignet")))
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName.toLowerCase();
		else
			autogenIdKeyName = autogenIdKeyName + "_" + fieldName;
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
			String autoGenIdFileName = getAutoGenIdFileName(testCaseName);
			uri = replaceIdWithAutogeneratedId(uri, "$ID:", autoGenIdFileName);
		}

		return uri;
	}
	
	public static String getAuthTransactionId(String oidcTransactionId) {
		final String transactionId = oidcTransactionId.replaceAll("_|-", "");
		String lengthOfTransactionId = AdminTestUtil.getValueFromEsignetActuator(
				ConfigManager.getEsignetActuatorPropertySection(), "mosip.esignet.auth-txn-id-length");
		int authTransactionIdLength = lengthOfTransactionId != null ? Integer.parseInt(lengthOfTransactionId): 0;
	    final byte[] oidcTransactionIdBytes = transactionId.getBytes();
	    final byte[] authTransactionIdBytes = new byte[authTransactionIdLength];
	    int i = oidcTransactionIdBytes.length - 1;
	    int j = 0;
	    while(j < authTransactionIdLength) {
	        authTransactionIdBytes[j++] = oidcTransactionIdBytes[i--];
	        if(i < 0) { i = oidcTransactionIdBytes.length - 1; }
	    }
	    return new String(authTransactionIdBytes);
	}

	public String replaceKeywordWithValue(String jsonString, String keyword, String value) {
		if (value != null && !value.isEmpty())
			return jsonString.replace(keyword, value);
		else
			throw new SkipException("Marking testcase as skipped as required fields are empty " + keyword);
	}

	public static String addIdentityPassword = "";
	public static String addIdentitySalt = "";

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

		if (jsonString.contains("$POLICYNUMBERFORSUNBIRDRC$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYNUMBERFORSUNBIRDRC$",
					properties.getProperty("policyNumberForSunBirdRC"));
		}
		
		
		

		if (jsonString.contains("$FULLNAMEFORSUNBIRDRC$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$FULLNAMEFORSUNBIRDRC$", fullNameForSunBirdRC);
		}

		if (jsonString.contains("$DOBFORSUNBIRDRC$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$DOBFORSUNBIRDRC$", dobForSunBirdRC);
		}
		
		if (jsonString.contains("$CHALLENGEVALUEFORSUNBIRDRC$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$CHALLENGEVALUEFORSUNBIRDRC$",
					properties.getProperty("challengeValueForSunBirdRC"));
		}
		
		if (jsonString.contains("$CHALLENGEVALUEFORSUNBIRDC$")) {

			HashMap<String, String> mapForChallenge = new HashMap<String, String>();
			mapForChallenge.put(GlobalConstants.FULLNAME, fullNameForSunBirdRC);
			mapForChallenge.put(GlobalConstants.DOB, dobForSunBirdRC);

			String challenge = gson.toJson(mapForChallenge);

			String challengeValue = BiometricDataProvider.toBase64Url(challenge);

			jsonString = replaceKeywordWithValue(jsonString, "$CHALLENGEVALUEFORSUNBIRDC$", challengeValue);
		}

		if (jsonString.contains("_$REGISTEREDUSERFULLNAME$")) {
			JSONObject inputReqJson = new JSONObject(jsonString);
			JSONObject fullNameJson = new JSONObject();
			String keyName = "";
			String stringArray = "";
			if (inputReqJson.has("request") && inputReqJson.getJSONObject("request").has("challengeInfo")
					&& inputReqJson.getJSONObject("request").getJSONArray("challengeInfo").length() > 1 && inputReqJson
							.getJSONObject("request").getJSONArray("challengeInfo").getJSONObject(1).has("challenge")) {
				keyName = inputReqJson.getJSONObject("request").getJSONArray("challengeInfo").getJSONObject(1)
						.getString("challenge");
				if (!keyName.isBlank() && keyName != null) {
					stringArray = CertsUtil.getCertificate(keyName);
					if (!stringArray.isBlank() && stringArray != null) {
						JSONArray fullNameArray = new JSONArray(stringArray);
						fullNameJson.put("fullName", fullNameArray);
						byte[] byteBioData = fullNameJson.toString().getBytes();

						String challengeValue = Base64.getUrlEncoder().encodeToString(byteBioData);
						logger.info(challengeValue);

						jsonString = replaceKeywordWithValue(jsonString, keyName, challengeValue);
					}
				}
			}
		}

		if (jsonString.contains("$PASSWORDFORAUTHENTICATION$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PASSWORDFORAUTHENTICATION$",
					PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION);
		}
		if (jsonString.contains("$RESETPASSWORDFORAUTHENTICATION$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RESETPASSWORDFORAUTHENTICATION$", PASSWORD_TO_RESET);
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
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUE$",
					BiometricDataProvider.getFromBiometricMap("BioValue"));
		}
		if (jsonString.contains("$BIOVALUEWITHOUTFACE$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BIOVALUEWITHOUTFACE$",
					BiometricDataProvider.getFromBiometricMap("BioValueWithoutFace"));
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


		if (jsonString.contains("$PHONENUMBERFORIDENTITY$")) {
			String phoneNumber = "";
			if (!phoneSchemaRegex.isEmpty())
				try {
					phoneNumber = genStringAsperRegex(phoneSchemaRegex);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			jsonString = replaceKeywordWithValue(jsonString, "$PHONENUMBERFORIDENTITY$", phoneNumber);
		}
		if (jsonString.contains("$1STLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$1STLANG$", BaseTestCase.languageList.get(0));
		if (jsonString.contains("$2NDLANG$"))
			jsonString = replaceKeywordWithValue(jsonString, "$2NDLANG$", BaseTestCase.languageList.get(1));
		

		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_1))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_1,
					ConfigManager.getproperty("KEYCLOAKUSER1"));
		if (jsonString.contains(GlobalConstants.KEYCLOAK_USER_2))
			jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.KEYCLOAK_USER_2,
					ConfigManager.getproperty("KEYCLOAKUSER2"));
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
					BaseTestCase.currentModule + ConfigManager.getproperty("admin_userName"));

		if (jsonString.contains("$LOCATIONCODE$"))
			jsonString = replaceKeywordWithValue(jsonString, "$LOCATIONCODE$", locationCode);

		// Need to handle int replacement
		if (jsonString.contains("$HIERARCHYLEVEL$"))
			getLocationData();
			jsonString = replaceKeywordWithValue(jsonString, "$HIERARCHYLEVEL$", String.valueOf(hierarchyLevel));

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
			jsonString = replaceKeywordWithValue(jsonString, "$PUBLICKEY$", generatePulicKey());
			publickey = JsonPrecondtion.getJsonValueFromJson(jsonString, "request.publicKey");
		}
		if (jsonString.contains("$PUBLICKEYFORBINDING$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PUBLICKEYFORBINDING$",
					generatePublicKeyForMimoto());
		}
		if (jsonString.contains("$BLOCKEDPARTNERID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BLOCKEDPARTNERID$", getPartnerId());
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
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2S3", RANDOM_ID_V2_S3);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2S2", RANDOM_ID_V2_S2);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$V2", RANDOM_ID_V2);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$2", RANDOM_ID_2);
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMID$", RANDOM_ID);
		}
		
		if (jsonString.contains("$RANDOMIDFOROIDCCLIENT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMIDFOROIDCCLIENT$",
					"mosip" + generateRandomNumberString(2) + Calendar.getInstance().getTimeInMillis());
		}
		
		if (jsonString.contains("$RANDOMUUID$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$RANDOMUUID$", UUID.randomUUID().toString());
		}
		if (jsonString.contains("$BASEURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$BASEURI$", ApplnURI);
		}
		if (jsonString.contains("$IDPUSER$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDPUSER$", ConfigManager.getproperty("idpClientId"));
		}
		if (jsonString.contains("$OIDCCLIENT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCCLIENT$",
					getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.iam.module.clientID"));
		}
			
			if (jsonString.contains("$DOB$")) {
				jsonString = replaceKeywordWithValue(jsonString, "$DOB$",
						getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES, "mosip.date-of-birth.pattern"));
		}
		if (jsonString.contains("$IDPREDIRECTURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$IDPREDIRECTURI$",
					ApplnURI.replace(GlobalConstants.API_INTERNAL, "healthservices") + "/userprofile");
		}
		if (jsonString.contains("$INJIREDIRECTURI$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$INJIREDIRECTURI$",
					ApplnURI.replace(GlobalConstants.API_INTERNAL, "injiweb") + "/redirect");
		}
		if (jsonString.contains("$BASE64URI$")) {
			String redirectUri = ApplnURI.replace(GlobalConstants.API_INTERNAL, GlobalConstants.RESIDENT)
					+ ConfigManager.getproperty("currentUserURI");
			jsonString = replaceKeywordWithValue(jsonString, "$BASE64URI$", encodeBase64(redirectUri));
		}
		if (jsonString.contains("$JWKKEY$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$JWKKEY$", generateJWKPublicKey());
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

		if (jsonString.contains("$OIDCJWKKEY4$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen13()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(OIDCJWK4);
				settriggerESignetKeyGen13(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(OIDCJWK4);
			}
			jsonString = replaceKeywordWithValue(jsonString, "$OIDCJWKKEY4$", jwkKey);
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
			
			String tempUrl = getValueFromEsignetWellKnownEndPoint("token_endpoint", ConfigManager.getEsignetBaseUrl());
			
			jsonString = replaceKeywordWithValue(jsonString, "$CLIENT_ASSERTION_JWK$",
					signJWKKey(clientId, oidcJWKKey1, tempUrl));
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
			
			String tempUrl = getValueFromEsignetWellKnownEndPoint("token_endpoint", ConfigManager.getEsignetBaseUrl());
			jsonString = replaceKeywordWithValue(jsonString, "$CLIENT_ASSERTION_USER3_JWK$",
					signJWKKey(clientId, oidcJWKKey3, tempUrl));
		}

		if (jsonString.contains("$CLIENT_ASSERTION_USER4_JWK$")) {
			String oidcJWKKeyString = JWKKeyUtil.getJWKKey(OIDCJWK4);
			logger.info("oidcJWKKeyString =" + oidcJWKKeyString);
			try {
				oidcJWKKey4 = RSAKey.parse(oidcJWKKeyString);
				logger.info("oidcJWKKey4 =" + oidcJWKKey4);
			} catch (java.text.ParseException e) {
				logger.error(e.getMessage());
			}
			JSONObject request = new JSONObject(jsonString);
			String clientId = null;
			if (request.has("client_id")) {
				clientId = request.get("client_id").toString();
			}
			jsonString = replaceKeywordWithValue(jsonString, "$CLIENT_ASSERTION_USER4_JWK$",
					signJWKKeyForMock(clientId, oidcJWKKey4));
		}

		if (jsonString.contains("$IDPCLIENTPAYLOAD$")) {
			String clientId = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES,
					"mosip.iam.module.clientID");
			String esignetBaseURI = getValueFromActuator(GlobalConstants.RESIDENT_DEFAULT_PROPERTIES,
					"mosip.iam.token_endpoint");
			int idTokenExpirySecs = Integer
					.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
							GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));

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
					generateDetachedSignature(jsonString, BINDINGCONSENTVIDSAMECLAIMJWK,
							BINDINGCERTCONSENTVIDSAMECLAIMFILE));
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

		if (jsonString.contains("$NAMEFORUPDATEUIN$")) {
			String name = getValueFromAuthActuator("json-property", "name");
			String nameResult = "";
			
			if (new JSONArray(name).length() > 1) {
				nameResult = new JSONArray(name).getString(0);
			}else {
				nameResult = name.replaceAll("\\[\"|\"\\]", "");
			}
			jsonString = replaceKeywordWithValue(jsonString, "$NAMEFORUPDATEUIN$", nameResult);
		}

		if (jsonString.contains("$UPDATEDEMAILATTR$")) {

			String email = getValueFromAuthActuator("json-property", "emailId");
			String emailResult = email.replaceAll("\\[\"|\"\\]", "");

			jsonString = replaceKeywordWithValue(jsonString, "$UPDATEDEMAILATTR$", emailResult);
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
			jsonString = replaceKeywordWithValue(jsonString, "$PROOFJWT$",
					signJWK(clientId, accessToken, oidcJWKKey1, testCaseName));
		}

		if (jsonString.contains("$PROOF_JWT_2$")) {

			String oidcJWKKeyString = JWKKeyUtil.getJWKKey(OIDCJWK4);
			logger.info("oidcJWKKeyString =" + oidcJWKKeyString);
			try {
				oidcJWKKey4 = RSAKey.parse(oidcJWKKeyString);
				logger.info("oidcJWKKey4 =" + oidcJWKKey4);
			} catch (java.text.ParseException e) {
				logger.error(e.getMessage());
			}

			JSONObject request = new JSONObject(jsonString);
			String clientId = "";
			String accessToken = "";
			String tempUrl = "";
			if (request.has("client_id")) {
				clientId = request.getString("client_id");
				request.remove("client_id");
			}
			if (request.has("idpAccessToken")) {
				accessToken = request.getString("idpAccessToken");
			}
			jsonString = request.toString();

			if (BaseTestCase.currentModule.equals(GlobalConstants.INJICERTIFY)) {
				String baseURL = ConfigManager.getInjiCertifyBaseUrl();
				if (testCaseName.contains("_GetCredentialSunBirdC")) {
					tempUrl = getValueFromInjiCertifyWellKnownEndPoint("credential_issuer",
							baseURL.replace("injicertify.", "injicertify-insurance."));
				}
			} else {
				tempUrl = getValueFromEsignetWellKnownEndPoint("issuer", ConfigManager.getEsignetBaseUrl());
				if (tempUrl.contains("esignet.")) {
					tempUrl = tempUrl.replace("esignet.", ConfigManager.getproperty("esignetMockBaseURL"));
				}
			}
			jsonString = replaceKeywordWithValue(jsonString, "$PROOF_JWT_2$",
					signJWKForMock(clientId, accessToken, oidcJWKKey4, testCaseName, tempUrl));
		}

		if (jsonString.contains(GlobalConstants.REMOVE))
			jsonString = removeObject(new JSONObject(jsonString));

		return jsonString;
	}

	public static String signJWKForMock(String clientId, String accessToken, RSAKey jwkKey, String testCaseName,
			String tempUrl) {
		int idTokenExpirySecs = Integer
				.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
						GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;
		String proofJWT = "";
		String typ = "openid4vci-proof+jwt";
		JWK jwkHeader = jwkKey.toPublicJWK();
		SignedJWT signedJWT = null;

		try {
			signer = new RSASSASigner(jwkKey);
			Date currentTime = new Date();

			// Create a Calendar instance to manipulate time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentTime);

			// Add one hour to the current time
			calendar.add(Calendar.HOUR_OF_DAY, (idTokenExpirySecs / 3600)); // Adding one hour

			// Get the updated expiration time
			Date expirationTime = calendar.getTime();

			String[] jwtParts = accessToken.split("\\.");
			String jwtPayloadBase64 = jwtParts[1];
			byte[] jwtPayloadBytes = Base64.getDecoder().decode(jwtPayloadBase64);
			String jwtPayload = new String(jwtPayloadBytes, StandardCharsets.UTF_8);
			JWTClaimsSet claimsSet = null;
			String nonce = new ObjectMapper().readTree(jwtPayload).get("c_nonce").asText();

			claimsSet = new JWTClaimsSet.Builder().audience(tempUrl).claim("nonce", nonce).issuer(clientId)
					.issueTime(currentTime).expirationTime(expirationTime).build();
			signedJWT = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).type(new JOSEObjectType(typ)).jwk(jwkHeader).build(),
					claimsSet);

			signedJWT.sign(signer);
			proofJWT = signedJWT.serialize();
		} catch (Exception e) {
			logger.error("Exception while signing proof_jwt to get credential: " + e.getMessage());
		}
		return proofJWT;
	}

	public static String signJWK(String clientId, String accessToken, RSAKey jwkKey, String testCaseName) {
		String tempUrl = getValueFromEsignetWellKnownEndPoint("issuer", ConfigManager.getEsignetBaseUrl());
		int idTokenExpirySecs = Integer
				.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
						GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;
		String proofJWT = "";
		String typ = "openid4vci-proof+jwt";
		JWK jwkHeader = jwkKey.toPublicJWK();
		SignedJWT signedJWT = null;

		try {
			signer = new RSASSASigner(jwkKey);
			Date currentTime = new Date();

			// Create a Calendar instance to manipulate time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentTime);

			// Add one hour to the current time
			calendar.add(Calendar.HOUR_OF_DAY, (idTokenExpirySecs / 3600)); // Adding one hour

			// Get the updated expiration time
			Date expirationTime = calendar.getTime();

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
					.issueTime(currentTime).expirationTime(expirationTime).build();

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
	
	public static String generatePublicKeyForMimoto() {

		String vcString = "";
		try {
			KeyPairGenerator keyPairGenerator = getKeyPairGeneratorInstance();
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			StringWriter stringWriter = new StringWriter();
			try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
				pemWriter.writeObject(publicKey);
				pemWriter.flush();
				vcString = stringWriter.toString();
				if (System.getProperty("os.name").toLowerCase().contains("windows")) {
					vcString = vcString.replaceAll("\r\n", "\\\\n");
				} else {
					vcString = vcString.replaceAll("\n", "\\\\n");
				}
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return vcString;
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

	public static String getDetachedSignature(String[] acceptedClaims, String[] permittedScope, RSAKey jwkKey,
			String certData) throws JoseException, JOSEException {
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
			autoGenFileName = masterDataAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("sync"))
			autoGenFileName = syncDataAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PREREG))
			autoGenFileName = preregAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PARTNER))
			autoGenFileName = partnerAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PARTNERNEW))
			autoGenFileName = pmsAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("idrepo"))
			autoGenFileName = idrepoAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.RESIDENT))
			autoGenFileName = residentAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("regproc"))
			autoGenFileName = regProcAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("auth"))
			autoGenFileName = authAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith("prerequisite"))
			autoGenFileName = prerequisiteAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.MIMOTO))
			autoGenFileName = mimotoAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.ESIGNET))
			autoGenFileName = esignetAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.PARTNERNEW))
			autoGenFileName = pmsAutoGeneratedIdPropFileName;
		else if (testCaseName.toLowerCase().startsWith(GlobalConstants.INJICERTIFY))
			autoGenFileName = injiCertifyAutoGeneratedIdPropFileName;
		
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

	public String updateTimestampOtp(String otpIdentyEnryptRequest, String otpChannel, String testCaseName) {
		otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest,
				generateCurrentUTCTimeStamp(), "timestamp");
		String otp = null;
	
		otp = OTPListener.getOtp(otpChannel);
		
		if(otp!=null && !otp.isBlank()){
			otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest, otp, "otp");
		}
		else {
			logger.error("Not Able To Fetch OTP From SMTP");
		}
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
//		else if (Integer.parseInt(responseStatusCode) >= 200 && Integer.parseInt(responseStatusCode) < 300)
//			customResponse.setStatus(GlobalConstants.FAIL_STRING);
//		else
//			throw new SkipException("API endpoint is not valid. Response code: " + responseStatusCode);

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
		String keyToReplace = "";
		
		// mock = email,phone; default
		// mock = phone;
		// mock = email;

		// $ID:AddIdentity_withValidParameters_smoke_Pos_EMAIL$
		
		// $ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$@phone
		
		

		if (keyForIdProperty.endsWith("_EMAIL") && ConfigManager.getMockNotificationChannel().equalsIgnoreCase("phone")) {
				String temp = idKey + keyForIdProperty + "$" ; //$ID:AddIdentity_withValidParameters_smoke_Pos_EMAIL$
				keyForIdProperty = keyForIdProperty.replace("_EMAIL", "_PHONE"); // AddIdentity_withValidParameters_smoke_Pos_PHONE
				keyToReplace = temp; // $ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$@phone
				
				jsonString = jsonString.replace(temp, temp + "@phone");
				
				
		} else if (keyForIdProperty.endsWith("_PHONE") && ConfigManager.getMockNotificationChannel().equalsIgnoreCase("email")) {
				String temp = idKey + keyForIdProperty + "$" ; //$ID:AddIdentity_withValidParameters_smoke_Pos_PHONE$
				keyForIdProperty = keyForIdProperty.replace("_PHONE", "_EMAIL"); // AddIdentity_withValidParameters_smoke_Pos_EMAIL
				keyToReplace = temp + "@phone";
		} else {
			keyToReplace = idKey + keyForIdProperty + "$"; //AddIdentity_withValidParameters_smoke_Pos_EMAIL
		}
		
		
		
		
		
		
		
		
		
		

		
		
		
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
				if (keyForIdProperty.equals("UploadPartnerCert_Misp_Valid_Smoke_sid_signedCertificateData")) {
					String certData = props.getProperty(keyForIdProperty);
					if (System.getProperty(GlobalConstants.OS_NAME).toLowerCase().contains(GlobalConstants.WINDOWS)) {
						certData = certData.replaceAll("\n", "\\\\n");
					} else {
						certData = certData.replaceAll("\n", "\\\\n");

					}
					jsonString = replaceKeywordWithValue(jsonString, keyToReplace, certData);
				} else
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
		GlobalMethods.reportRequest(null, jsonInput, url);
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
	
	public static void copyPmsNewTestResource() {
		copymoduleSpecificAndConfigFile(GlobalConstants.PARTNERNEW);
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
				} else if (jsonObject.has(GlobalConstants.KEYWORD_DATA)) {
					String filterValueToConvert = jsonObject.getJSONArray(GlobalConstants.KEYWORD_DATA).get(0).toString();
					JSONObject filtervalue = new JSONObject(filterValueToConvert);
					if (filtervalue.has(fieldToConvert)) {
						valueToConvert = filtervalue.getString(fieldToConvert);
						translatedValue = valueToConvert;
						isFilterRequired = false;
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

	public static String getKeysDirPath() {
//		String path = "/Users/kamalsingh/mosip/authcerts" + "/" + "IDA-" + environment + ".mosip.net";
//		logger.info("certificate path is::" + path);
//		return new File(path).getAbsolutePath();
		
		String certsTargetDir = System.getProperty("java.io.tmpdir") + File.separator + System.getProperty("parent.certs.folder.name", "AUTHCERTS");

		if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {
      		certsTargetDir = "/home/mosip/authcerts";
      	}
        logger.info("Certs target path is: " +certsTargetDir + File.separator + certsForModule + "-IDA-" + environment + ".mosip.net");
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

	public static String schemaRequiredField = "";
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
	    String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

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

	        schemaRequiredField = requiredPropsArray.toString();

	        String phone = getValueFromAuthActuator("json-property", "phone_number");
	        String result = phone.replaceAll("\\[\"|\"\\]", "");

	        if (!isElementPresent(requiredPropsArray, result)) {
	            requiredPropsArray.put(result);
	            phoneFieldAdditionallyAdded = true;
	        }
	        if (identityPropsJson.has(result)) {
	            phoneSchemaRegex = identityPropsJson.getJSONObject(result).getJSONArray("validators")
	                    .getJSONObject(0).getString("validator");
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
	        //requiredPropsArray.put("functionalId");
	        for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
	            String eachRequiredProp = requiredPropsArray.getString(i);

	            if (!identityPropsJson.has(eachRequiredProp)) {
	                continue;
	            }

	            JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp);
	            String randomValue = "";
	            if(eachRequiredProp == emailResult) {
	            	randomValue ="shshssh";
	            }
	            if(eachRequiredProp == result) {
	            	randomValue =phoneSchemaRegex ;
	            }
	            
	            
	            // Processing for TaggedListType
	            if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("TaggedListType")) {
	                JSONArray eachPropDataArrayForHandles = new JSONArray();
	                        JSONObject eachValueJsonForHandles = new JSONObject();
	                        if (eachRequiredProp.equals(emailResult)) {
	                            eachValueJsonForHandles.put("value", "$EMAILVALUE$");
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(emailResult);
	                            
	                        } else if (eachRequiredProp.equals(result)) {
	                            eachValueJsonForHandles.put("value", "$PHONENUMBERFORIDENTITY$");
	                            //"tags": ":["handle"]
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(result);
	                        } else {
	                            eachValueJsonForHandles.put("value", "$FUNCTIONALID$");
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(eachRequiredProp);
	                        }
	                        eachPropDataArrayForHandles.put(eachValueJsonForHandles);
	                        identityJson.put(eachRequiredProp, eachPropDataArrayForHandles);
	                    
	            }
	            
	           

	            else if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("simpleType")) {
	            	if(eachPropDataJson.has("handle")){
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
	                                            : propsMap.getProperty(eachRequiredProp) + BaseTestCase.getLanguageList().get(j));
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
	                	if(eachPropDataJson.has("handle")){
		            		selectedHandles.add(eachRequiredProp);
		            	}
	                    identityJson.put(eachRequiredProp, "$PHONENUMBERFORIDENTITY$");
	                }  else if (eachRequiredProp.equals(emailResult)) {
	                	if(eachPropDataJson.has("handle")){
		            		selectedHandles.add(eachRequiredProp);
		            	}
	                    identityJson.put(eachRequiredProp, "$EMAILVALUE$");
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
	                	if(eachPropDataJson.has("handle")){
		            		selectedHandles.add(eachRequiredProp);
		            	}
	                    identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
	                }
	            }
	        }
	        if (selectedHandles  != null && selectedHandles.size()>=1) {
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
	        //identityJson.put("functionalIds", functionalIdsArray);

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

	
	public static String updateIdentityHbs(boolean regenerateHbs) {
	    if (updateIdentityHbs != null && !regenerateHbs) {
	    	
	        return updateIdentityHbs;
	    }
	    JSONObject requestJson = new JSONObject();
	    kernelAuthLib = new KernelAuthentication();
	    String token = kernelAuthLib.getTokenByRole(GlobalConstants.ADMIN);
	    String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

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

	        schemaRequiredField = requiredPropsArray.toString();

	        String phone = getValueFromAuthActuator("json-property", "phone_number");
	        String result = phone.replaceAll("\\[\"|\"\\]", "");

	        if (!isElementPresent(requiredPropsArray, result)) {
	            requiredPropsArray.put(result);
	            phoneFieldAdditionallyAdded = true;
	        }
	        if (identityPropsJson.has(result)) {
	            phoneSchemaRegex = identityPropsJson.getJSONObject(result).getJSONArray("validators")
	                    .getJSONObject(0).getString("validator");
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
	        //requiredPropsArray.put("functionalId");
	        for (int i = 0, size = requiredPropsArray.length(); i < size; i++) {
	            String eachRequiredProp = requiredPropsArray.getString(i);

	            if (!identityPropsJson.has(eachRequiredProp)) {
	                continue;
	            }

	            JSONObject eachPropDataJson = (JSONObject) identityPropsJson.get(eachRequiredProp);
	            String randomValue = "";
	            if(eachRequiredProp == emailResult) {
	            	randomValue ="shshssh";
	            }
	            if(eachRequiredProp == result) {
	            	randomValue =phoneSchemaRegex ;
	            }
	            
	            
	            // Processing for TaggedListType
	            if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("TaggedListType")) {
	                JSONArray eachPropDataArrayForHandles = new JSONArray();
	                        JSONObject eachValueJsonForHandles = new JSONObject();
	                        if (eachRequiredProp.equals(emailResult)) {
	                            eachValueJsonForHandles.put("value", "$EMAILVALUE$");
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(emailResult);
	                            
	                        } else if (eachRequiredProp.equals(result)) {
	                            eachValueJsonForHandles.put("value", "$PHONENUMBERFORIDENTITY$");
	                            //"tags": ":["handle"]
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(result);
	                        } else {
	                            eachValueJsonForHandles.put("value", "$FUNCTIONALID$");
	                            eachValueJsonForHandles.put("tags", handleArray);
	                            selectedHandles.add(eachRequiredProp);
	                        }
	                        eachPropDataArrayForHandles.put(eachValueJsonForHandles);
	                        identityJson.put(eachRequiredProp, eachPropDataArrayForHandles);
	                    
	            }
	            
	           

	            else if (eachPropDataJson.has("$ref") && eachPropDataJson.get("$ref").toString().contains("simpleType")) {
	            	if(eachPropDataJson.has("handle")){
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
	                                            : propsMap.getProperty(eachRequiredProp) + BaseTestCase.getLanguageList().get(j));
	                        }
	                        eachPropDataArray.put(eachValueJson);
	                    }
	                }
	                identityJson.put(eachRequiredProp, eachPropDataArray);
	                
	            } else {
	                 if (eachRequiredProp.equals("IDSchemaVersion")) {
	                    identityJson.put(eachRequiredProp, schemaVersion);
	                } 
	                 else if (eachRequiredProp.equals("individualBiometrics")) {
		                    identityJson.remove("individualBiometrics");
	                 }
	                 else if (eachRequiredProp.equals(emailResult)) {
	                	 if(eachPropDataJson.has("handle")){
			            		selectedHandles.add(eachRequiredProp);
			            	}
		                    identityJson.put(eachRequiredProp, "$EMAILVALUE$");
		                }
	                 else if (eachRequiredProp.equals(result)) {
	                	 if(eachPropDataJson.has("handle")){
			            		selectedHandles.add(eachRequiredProp);
			            	}
		                    identityJson.put(eachRequiredProp, "$PHONENUMBERFORIDENTITY$");
		                }
	                 else if (eachRequiredProp.equals("proofOfIdentity")) {
		                    identityJson.remove("proofOfIdentity");
	                 }
	                 else {
	                	 if(eachPropDataJson.has("handle")){
			            		selectedHandles.add(eachRequiredProp);
			            	}
	                    identityJson.put(eachRequiredProp, "{{" + eachRequiredProp + "}}");
	                }
	            }
	        }
	        if (selectedHandles  != null) {
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
	    String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

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
		String url = ApplnURI + properties.getProperty(GlobalConstants.MASTER_SCHEMA_URL);

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

	public static String signJWKKeyForMock(String clientId, RSAKey jwkKey) {
		String tempUrl = getValueFromEsignetWellKnownEndPoint("token_endpoint", ConfigManager.getEsignetBaseUrl());
		if (tempUrl.contains("esignet.")) {
			tempUrl = tempUrl.replace("esignet.", ConfigManager.getproperty("esignetMockBaseURL"));
		}
		int idTokenExpirySecs = Integer
				.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
						GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;

		try {
			signer = new RSASSASigner(jwkKey);

			Date currentTime = new Date();

			// Create a Calendar instance to manipulate time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentTime);

			// Add one hour to the current time
			calendar.add(Calendar.HOUR_OF_DAY, (idTokenExpirySecs / 3600)); // Adding one hour

			// Get the updated expiration time
			Date expirationTime = calendar.getTime();

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(clientId).audience(tempUrl).issuer(clientId)
					.issueTime(currentTime).expirationTime(expirationTime).build();

			logger.info("JWT current and expiry time " + currentTime + " & " + expirationTime);

			SignedJWT signedJWT = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwkKey.getKeyID()).build(), claimsSet);

			signedJWT.sign(signer);
			clientAssertionToken = signedJWT.serialize();
		} catch (Exception e) {
			logger.error("Exception while signing oidcJWKKey for client assertion: " + e.getMessage());
		}
		return clientAssertionToken;
	}

	public static String signJWKKey(String clientId, RSAKey jwkKey, String tempUrl) {
		int idTokenExpirySecs = Integer
				.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
						GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
		JWSSigner signer;

		try {
			signer = new RSASSASigner(jwkKey);

			Date currentTime = new Date();

			// Create a Calendar instance to manipulate time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentTime);

			// Add one hour to the current time
			calendar.add(Calendar.HOUR_OF_DAY, (idTokenExpirySecs / 3600)); // Adding one hour

			// Get the updated expiration time
			Date expirationTime = calendar.getTime();

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(clientId).audience(tempUrl).issuer(clientId)
					.issueTime(currentTime).expirationTime(expirationTime).build();

			logger.info("JWT current and expiry time " + currentTime + " & " + expirationTime);

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
		String tempUrl = ConfigManager.getproperty("validateBindingEndpoint");
		int idTokenExpirySecs = Integer
				.parseInt(getValueFromEsignetActuator(ConfigManager.getEsignetActuatorPropertySection(),
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
			if (!BaseTestCase.isTargetEnvLTS())
				section = "/mosip-config/sandbox/application-lts.properties";
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

	public static String getValueFromEsignetWellKnownEndPoint(String key, String baseURL) {
		String url = baseURL + ConfigManager.getproperty("esignetWellKnownEndPoint");
		Response response = null;
		JSONObject responseJson = null;
		if (responseJson == null) {
			try {
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
				responseJson = new org.json.JSONObject(response.getBody().asString());
				return responseJson.getString(key);
			} catch (Exception e) {
				logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			}
		}
		return responseJson.getString(key);
	}
	
	public static String getValueFromInjiCertifyWellKnownEndPoint(String key, String baseURL) {
		String url = baseURL + ConfigManager.getproperty("injiCertifyWellKnownEndPoint");

		String actuatorCacheKey = url + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		Response response = null;
		JSONObject responseJson = null;
		try {
			response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			responseJson = new org.json.JSONObject(response.getBody().asString());
			if (responseJson.has(key)) {
				actuatorValueCache.put(actuatorCacheKey, responseJson.getString(key));
				return responseJson.getString(key);
			}
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}
		return responseJson.getString(key);
	}

	public static JSONObject signUpSettingsResponseJson = null;

	public String getValueFromSignUpSettings(String key) {
		String url = ApplnURI + ConfigManager.getproperty("signupSettingsEndPoint");
		String actuatorCacheKey = url + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		try {
			if (signUpSettingsResponseJson == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				signUpSettingsResponseJson = responseJson.getJSONObject("response").getJSONObject("configs");
			}

			if (signUpSettingsResponseJson.has(key)) {
				value = signUpSettingsResponseJson.getString(key);
				actuatorValueCache.put(actuatorCacheKey, value);
			}

			if (ConfigManager.IsDebugEnabled())
				logger.info("Actuator: " + url + " key: " + key + " value: " + value);
			return value;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return "";
		}

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
	
	public static JSONArray mimotoActuatorResponseArray = null;

	public static String getValueFromMimotoActuator(String section, String key) {
		String url = ApplnURI + ConfigManager.getproperty("actuatorMimotoEndpoint");
		if (!(System.getenv("useOldContextURL") == null)
				&& !(System.getenv("useOldContextURL").isBlank())
				&& System.getenv("useOldContextURL").equalsIgnoreCase("true")) {
			if (url.contains("/v1/mimoto/")) {
				url = url.replace("/v1/mimoto/", "/residentmobileapp/");
			}
		}
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		try {
			if (mimotoActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				mimotoActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}
			for (int i = 0, size = mimotoActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = mimotoActuatorResponseArray.getJSONObject(i);
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
			logger.error("Unable to fetch the value from the actuator. URL = " + url + " section = " + section + " key "
					+ key);
			return "";
		}

	}

	public static JSONArray regprocActuatorResponseArray = null;

	public static String getValueFromRegprocActuator(String section, String key) {
		String url = ApplnURI + ConfigManager.getproperty("regprocActuatorEndpoint");
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		try {
			if (regprocActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				regprocActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}
			for (int i = 0, size = regprocActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = regprocActuatorResponseArray.getJSONObject(i);
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

	public static JSONArray esignetActuatorResponseArray = null;

	public static String getValueFromEsignetActuator(String section, String key) {
		String url = ConfigManager.getEsignetBaseUrl() + ConfigManager.getproperty("actuatorEsignetEndpoint");
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

	public static JSONArray configActuatorResponseArray = null;

	public static String getValueFromConfigActuator() {

		String url = ApplnURI + ConfigManager.getproperty("actuatorEndpoint");

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
		String url = ApplnURI + ConfigManager.getproperty("actuatorRegprocEndpoint");

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
		JSONArray dobArray = new JSONArray(getValueFromAuthActuator("json-property", "dob"));
		JSONArray emailArray = new JSONArray(getValueFromAuthActuator("json-property", "emailId"));
		JSONArray individualBiometricsArray = new JSONArray(
				getValueFromAuthActuator("json-property", "individualBiometrics"));
		String dob = dobArray.getString(0);
		String email = emailArray.getString(0);
		String individualBiometrics = individualBiometricsArray.getString(0);

		if (BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.IDREPO)) {
			if (testCaseName.startsWith("IdRepository_") && testCaseName.contains("DOB")
					&& (!isElementPresent(new JSONArray(schemaRequiredField), dob))) {
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
			}
			
			if (testCaseName.startsWith("IdRepository_") && testCaseName.contains("_handle") && foundHandlesInIdSchema == false) {
				throw new SkipException(GlobalConstants.HANDLE_SCHEMA_NOT_DEPLOYED_MESSAGE);
			}
			
			

			else if (testCaseName.startsWith("IdRepository_") && testCaseName.contains("Email")
					&& (!isElementPresent(new JSONArray(schemaRequiredField), email))) {
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
			}

			else if (testCaseName.startsWith("IdRepository_") && testCaseName.contains("Invalid_BioVal")
					&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.ADMIN))) {
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
			}
		} else if (BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.AUTH)) {
			if (testCaseName.startsWith("auth_")
					&& (testCaseName.contains("_BioAuth_") || testCaseName.contains("_EkycBio_")
							|| testCaseName.contains("_MultiFactorAuth_") || testCaseName.contains("_DemoAuth")
							|| testCaseName.contains("_EkycDemo_"))
					&& (!isElementPresent(new JSONArray(schemaRequiredField), individualBiometrics))) {
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
			} else if (testCaseName.startsWith("auth_")
					&& ((testCaseName.contains("_DeactivateUINs_")) || (testCaseName.contains("PublishDraft_")))
					&& (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
							&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid"))) {
				throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
			} else if (testCaseName.startsWith("auth_")
					&& (testCaseName.contains("_AuthLock_") || testCaseName.contains("_AuthUnLock_"))
					&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.RESIDENT))) {
				throw new SkipException(GlobalConstants.SERVICE_NOT_DEPLOYED_MESSAGE);
			} else if (testCaseName.startsWith("auth_")
					&& (testCaseName.contains("_BlockHotlistAPI_") || testCaseName.contains("_HotlistAPI_")
							|| testCaseName.contains("_BlockPartnerId_")
							|| testCaseName.contains("_OTP_Auth_With_blocked_misp_Pos")
							|| testCaseName.contains("_OTP_Auth_With_blocked_partnerid_Pos"))
					&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.HOTLIST))) {
				throw new SkipException(GlobalConstants.SERVICE_NOT_DEPLOYED_MESSAGE);
			}
		} 
		
//		else if (BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.ESIGNET)) {
//			if ((testCaseName.startsWith("Esignet_") || testCaseName.startsWith("ESignet_"))
//					&& (testCaseName.contains("_KycBioAuth_") || testCaseName.contains("_BioAuth_")
//							|| testCaseName.contains("_SendBindingOtp_uin_Email_Valid_Smoke"))
//					&& (!isElementPresent(new JSONArray(schemaRequiredField), individualBiometrics))) {
//				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
//			}
//		}

		if ((ConfigManager.isInServiceNotDeployedList(GlobalConstants.ESIGNET))
				&& BaseTestCase.currentModule.equalsIgnoreCase("resident") && testCaseName.contains("_SignJWT_")) {
			throw new SkipException("esignet module is not deployed");
		}
		
		

		if ((ConfigManager.isInServiceNotDeployedList(GlobalConstants.ESIGNET))
				&& BaseTestCase.currentModule.equalsIgnoreCase("resident")
				&& (testCaseDTO.getRole() != null && (testCaseDTO.getRole().equalsIgnoreCase("residentNew")
						|| testCaseDTO.getRole().equalsIgnoreCase("residentNewVid")))) {
			throw new SkipException("esignet module is not deployed");
		}
		if (BaseTestCase.currentModule.equalsIgnoreCase(GlobalConstants.RESIDENT)) {
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

	public static String getRandomElement(List<String> list) {
		int randomIndex = secureRandom.nextInt(list.size());
		return list.get(randomIndex);
	}

	public String generateFullNameToRegisterUser(String inputJson, String testCaseName) {
		JSONArray fullNameArray = new JSONArray();
		String fullNamePattern = getValueFromSignUpSettings("fullname.pattern").toString();
		List<String> fullnames = Arrays.asList(" ឮᨪដ", "᧦", "ᨃ", "៻៥᧿", "ᩅᨎ", "ᩭឫ", "  ឃ  ំ ដ     ៹ម");
		String randomFullName = getRandomElement(fullnames);
		List<String> languageList = new ArrayList<>();
		languageList = BaseTestCase.getLanguageList();

		// For current sprint eng is removed.
		if (languageList.contains("eng"))
			languageList.remove("eng");
		if (testCaseName.contains("_Only_1st_Lang_On_Name_Field_Neg") && languageList.size() > 1)
			languageList.remove(1);

		for (int i = 0; i < languageList.size(); i++) {
			if (languageList.get(i) != null && !languageList.get(i).isEmpty()) {
				JSONObject eachValueJson = new JSONObject();
				if (testCaseName.contains("_Invalid_Value_On_Language_Field_Neg")) {
					eachValueJson.put(GlobalConstants.LANGUAGE, "sdbfkfj");
				} else if (testCaseName.contains("_Empty_Value_On_Language_Field_Neg")) {
					eachValueJson.put(GlobalConstants.LANGUAGE, "");
				} else
					eachValueJson.put(GlobalConstants.LANGUAGE, languageList.get(i));
				String generatedString = "";

				try {
					if (!fullNamePattern.isEmpty()) {
//						while (generatedString.isBlank()) {
//							generatedString = genStringAsperRegex(fullNamePattern);
//						}
//						eachValueJson.put(GlobalConstants.VALUE, generatedString);

						eachValueJson.put(GlobalConstants.VALUE, randomFullName);

						if (testCaseName.contains("_Only_Language_On_Name_Field_Neg"))
							eachValueJson.remove(GlobalConstants.VALUE);
						else if (testCaseName.contains("_Only_Value_On_Name_Field_Neg"))
							eachValueJson.remove(GlobalConstants.LANGUAGE);
						else if (testCaseName.contains("_Empty_Value_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, "");
						else if (testCaseName.contains("_Space_Value_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, " ");
						else if (testCaseName.contains("_Only_SpecialChar_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, "%^&*&** ^&&&");
						else if (testCaseName.contains("_Only_Num_Value_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, "564846841");
						else if (testCaseName.contains("_AlphaNum_Value_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, "អានុសា765651");
						else if (testCaseName.contains("_Exceeding_Limit_Value_On_Name_Field_Neg"))
							eachValueJson.put(GlobalConstants.VALUE, generateRandomAlphaNumericString(50));

					} else {
						logger.error("REGEX pattern not availble in the setting API");
						return "";
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					return "";
				}
				fullNameArray.put(eachValueJson);
			}

		}
		if (testCaseName.contains("_SName_Valid")) {
			CertsUtil.addCertificateToCache(testCaseName + "_$REGISTEREDUSERFULLNAME$", fullNameArray.toString());
		}
		inputJson = replaceKeywordWithValue(inputJson, "$FULLNAMETOREGISTERUSER$", fullNameArray.toString());

		return inputJson;
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
					if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
						emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
					
					if(testCaseName.contains("_INVALIDOTP")) {
						otp = "26258976";
					}
					else {
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
							if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
								emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
		if (BaseTestCase.currentModule.equals(GlobalConstants.MASTERDATA)) {
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
							if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
								emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
										if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
											emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
						if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
							emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
									if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
										emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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
						if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
							emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
						logger.info(emailId);
						if(testCaseName.contains("_EmptyChannel_Invalid_Neg"))
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
								if (emailId.endsWith(GlobalConstants.OTP_AS_PHONE))
									emailId = emailId.replace(GlobalConstants.OTP_AS_PHONE, "");
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

	public static void checkDbAndValidate(String timeStamp, String dbChecker) throws AdminTestException {

		String sqlQuery = "SELECT * FROM audit.app_audit_log WHERE log_dtimes > '" + timeStamp
				+ "' AND session_user_name = '" + dbChecker + "';";

		Map<String, Object> response = AuditDBManager
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
		JSONObject responseJson = null;
		String url = ApplnURI + props.getProperty("fetchLocationHierarchyLevels")
				+ BaseTestCase.getLanguageList().get(0);
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

	public String getPhoneNumber() {
		String phoneNumber = "";
		// TODO Regex needs to be taken from Schema
		String phoneNumberRegex = getValueFromSignUpSettings("identifier.pattern");
		if (!phoneNumberRegex.isEmpty())
			try {
				phoneNumber = genStringAsperRegex(phoneNumberRegex);
				return phoneNumber;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		return phoneNumber;
	}

	public String getPasswordPattern() {
		String password = "";
		// TODO Regex needs to be taken from Schema
		String passwordRegex = getValueFromSignUpSettings("password.pattern");
		if (!passwordRegex.isEmpty())
			try {
				password = genStringAsperRegex(passwordRegex);
				return password;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		return password;
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
	
	public String replaceArrayHandleValues(String inputJson, String testCaseName) {
	    JSONObject jsonObj = new JSONObject(inputJson);
	    JSONObject request = jsonObj.getJSONObject("request");
	    JSONObject identity = request.getJSONObject("identity");
	    JSONArray selectedHandles = identity.getJSONArray("selectedHandles");
	    String email = getValueFromAuthActuator("json-property", "emailId");
        String emailResult = email.replaceAll("\\[\"|\"\\]", "");

	    for (int i = 0; i < selectedHandles.length(); i++) {
	        String handle = selectedHandles.getString(i);

	        if (identity.has(handle)) {
	            Object handleObj = identity.get(handle); // Dynamically get the handle object

	            // Check if the handle is an array
	            if (handleObj instanceof JSONArray) {
	                JSONArray handleArray = (JSONArray) handleObj;
	                
	                if (testCaseName.contains("_onlywithtags")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("value");
	                    }
	                } else if (testCaseName.contains("_withouttags")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("tags");
	                    }
	                } else if (testCaseName.contains("_withtagwithoutselectedhandles")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("selectedHandles");
	                    }
	                } else if (testCaseName.contains("_withinvalidtag")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        JSONArray tags = obj.optJSONArray("tags");
	                        if (tags != null) {
	                            for (int k = 0; k < tags.length(); k++) {
	                                String tag = tags.getString(k);
	                                tags.put(k, tag + "_invalid" + "RANDOM_ID");
	                            }
	                            obj.put("tags", tags);
	                        }
	                    }
	                } else if (testCaseName.contains("_withmultiplevalues")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        JSONArray valuesArray = new JSONArray();
	                        valuesArray.put("mosip501724826584965_modified_1");
	                        valuesArray.put("mosip501724826584965_modified_2");
	                        valuesArray.put("mosip501724826584965_modified_3");
	                        obj.put("values", valuesArray);
	                    }
	                } else if (testCaseName.contains("_withmultiplevaluesandwithouttags")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        JSONArray valuesArray = new JSONArray();
	                        valuesArray.put("mosip501724826584965_modified_1");
	                        valuesArray.put("mosip501724826584965_modified_2");
	                        valuesArray.put("mosip501724826584965_modified_3");
	                        obj.put("values", valuesArray);
	                        obj.remove("tags");
	                    }
	                } else if (testCaseName.contains("_withemptyselecthandles")) {
	                    identity.put("selectedHandles", new JSONArray());
	                } else if (testCaseName.contains("_withoutselectedhandles")) {
	                    identity.remove("selectedHandles");
	                    break;
	                } else if (testCaseName.contains("_withmultiplehandleswithoutvalue")) {
	                    String phone = getValueFromAuthActuator("json-property", "phone_number");
	                    String result = phone.replaceAll("\\[\"|\"\\]", "");
	                    boolean containsPhone = false;
	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                        if (result.equalsIgnoreCase(selectedHandles.getString(j))) {
	                            containsPhone = true;
	                            break;
	                        }
	                    }
	                    if (!containsPhone) {
	                        selectedHandles.put(result);
	                        JSONObject phoneEntry = new JSONObject();
	                        phoneEntry.put("value", "$PHONENUMBERFORIDENTITY$");
	                        JSONArray phoneArray = new JSONArray();
	                        phoneArray.put(phoneEntry);
	                        identity.put(result, phoneArray);
	                    }
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("value");
	                    }
	                } else if (testCaseName.contains("_withfunctionalIds") && handle.equals("functionalId")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("tags");
	                    }
	                } else if (testCaseName.contains("_withfunctionalIdsUsedFirstTwoValue") && handle.equals("functionalId")) {
	                    if (handleArray.length() < 3) {
	                        JSONObject secondValue = new JSONObject();
	                        secondValue.put("value", "RANDOM_ID_2" + 12);
	                        secondValue.put("tags", new JSONArray().put("handle"));
	                        JSONObject thirdValue = new JSONObject();
	                        thirdValue.put("value", "RANDOM_ID_2" + 34);
	                        handleArray.put(secondValue);
	                        handleArray.put(thirdValue);
	                    }
	                } else if (testCaseName.contains("_withfunctionalIdsandPhoneWithoutTags")) {
	                    String phone = getValueFromAuthActuator("json-property", "phone_number");
	                    String result = phone.replaceAll("\\[\"|\"\\]", "");
	                    boolean containsPhone = false;
	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                        if (result.equalsIgnoreCase(selectedHandles.getString(j))) {
	                            containsPhone = true;
	                            break;
	                        }
	                    }
	                    if (!containsPhone) {
	                        selectedHandles.put(result);
	                        JSONObject phoneEntry = new JSONObject();
	                        phoneEntry.put("value", "$PHONENUMBERFORIDENTITY$");
	                        JSONArray phoneArray = new JSONArray();
	                        phoneArray.put(phoneEntry);
	                        identity.put(result, phoneArray);
	                    }
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.remove("tags");
	                    }
	                } else if (testCaseName.contains("_withfunctionalIdsUsedFirstTwoValueOutOfFive")) {
	                    String baseValue = "";
	                    if (handleArray.length() > 0) {
	                        baseValue = handleArray.getJSONObject(0).getString("value");
	                    }
	                    for (int j = 0; j < 4; j++) {
	                        JSONObject obj = new JSONObject();
	                        if (j < 1) {
	                            obj.put("value", baseValue + j);
	                            obj.put("tags", new JSONArray().put("handle"));
	                        } else {
	                            obj.put("value", baseValue + j);
	                        }
	                        handleArray.put(obj);
	                    }
	                }
	                //43 in update identity
	                else if (testCaseName.contains("_removeexceptfirsthandle")) {
	                    if (identity.has("selectedHandles")) {

	                        if (selectedHandles.length() > 0) {
	                            String firstHandleToKeep = selectedHandles.getString(0);

	                            for (int j = 1; j < selectedHandles.length(); j++) {
	                                if (identity.has(handle)) {
	                                    identity.remove(handle);
	                                }
	                            }
	                            while (selectedHandles.length() > 1) {
	                                selectedHandles.remove(1);
	                            }
	                        }
	                    }
	                }
	              //44 in update identity
	                else if (testCaseName.contains("_withinvaliddemofield_inupdate")) {
	                    if (identity.has("selectedHandles")) {

	                        if (selectedHandles.length() > 0) {
	                            String firstHandleToKeep = selectedHandles.getString(0);

	                            for (int j = 1; j < selectedHandles.length(); j++) {
	                                if (identity.has(handle)) {
	                                    identity.remove(handle);
	                                }
	                            }
	                            while (selectedHandles.length() > 1) {
	                                selectedHandles.remove(1);
	                            }
	                        }
	                    }
	                }
	                //50
	                else if (testCaseName.contains("_withonedemofield")) {
	                    if (identity.has("selectedHandles")) {
	                        String firstHandle = selectedHandles.getString(0);
	                        for (int j = 1; j < selectedHandles.length(); j++) {
	                            if (identity.has(handle)) {
	                                Object handleValue = identity.get(handle);
	                                if (handleValue instanceof JSONArray) {
	                                    identity.remove(handle);
	                                }
	                            }
	                        }
	                        JSONArray newSelectedHandles = new JSONArray();
	                        newSelectedHandles.put(firstHandle);
	                        identity.put("selectedHandles", newSelectedHandles);
	                    }
	                }
	                
	                //82
	                
	                else if (testCaseName.contains("_withcasesensitivehandles")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.put("value", "HANDLES");
	                    }
	                }
	                //77
	                else if (testCaseName.contains("_replaceselectedhandles")) {
	                	identity.put("selectedHandles", new JSONArray().put(emailResult));
	                }
	                //76
	                else if (testCaseName.contains("_onlywithemail")) {
	                	identity.put("selectedHandles", new JSONArray().put(emailResult));
	                }
	                
	                //73
	                else if (testCaseName.contains("_withoutselectedhandlesinidentity")) {
	                	 identity.remove("selectedHandles");
	                }
	              
	                else if (testCaseName.contains("_withdublicatevalue")) {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        if (testCaseName.contains("_save_withdublicatevalue"))
	                        selectedHandlesValue=obj.getString("value");
	                        obj.put("value", selectedHandlesValue);
	                    }
	                }
	                else if (testCaseName.contains("_withmultipledublicatevalue")) {
		                        JSONObject secondValue = new JSONObject();
		                        secondValue.put("value", selectedHandlesValue);
		                        secondValue.put("tags", new JSONArray().put("handle"));
		                        handleArray.put(secondValue);
	                } 
	                else if (testCaseName.contains("_removevalueaddexistingvalue")) {
	                	 for (int j = 0; j < handleArray.length(); j++) {
		                        JSONObject obj = handleArray.getJSONObject(j);
		                        obj.remove("value");
		                        obj.put("value", selectedHandlesValue);
	                	 }
            } 
	                else if (testCaseName.contains("_withselectedhandlephone")) {
	                    if (identity.has("selectedHandles")) {
	                        // Remove "email" and "functionalId", keep only "phone"
	                        JSONArray updatedHandles = new JSONArray();
	                        boolean containsPhone = false;

	                        for (int j = 0; j < selectedHandles.length(); j++) {
	                             handle = selectedHandles.getString(j);
	                            if (handle.equalsIgnoreCase("phone")) {
	                                containsPhone = true;
	                                updatedHandles.put("phone"); // Ensure "phone" is kept
	                            }
	                        }

	                        // Add "phone" if not present
	                        if (!containsPhone) {
	                            updatedHandles.put("phone");
	                        }

	                        // Update the identity with the modified selectedHandles array
	                        identity.put("selectedHandles", updatedHandles);
	                    }else if (testCaseName.contains("_removealltagshandles")) {
		                    removeTagsHandles(jsonObj);
			                   
			                   
		                } else {
	                        // If "selectedHandles" doesn't exist, create it with "phone"
	                        JSONArray newSelectedHandles = new JSONArray();
	                        newSelectedHandles.put("phone");
	                        identity.put("selectedHandles", newSelectedHandles);
	                    }
	                }
	                
	                
	                else {
	                    for (int j = 0; j < handleArray.length(); j++) {
	                        JSONObject obj = handleArray.getJSONObject(j);
	                        obj.put("value", obj.getString("value"));
	                    }
	                }

	                identity.put(handle, handleArray);
	            }
	        }
	    }

	    return jsonObj.toString();
	}
	private void removeTagsHandles(JSONObject jsonObj) {
	    for (String key : jsonObj.keySet()) {
	        Object value = jsonObj.get(key);
	        if (value instanceof JSONObject) {
	            JSONObject nestedObject = (JSONObject) value;
	            if (nestedObject.has("tags")) {
	                JSONArray tagsArray = nestedObject.getJSONArray("tags");
	                if (tagsArray.length() == 1 && "handles".equals(tagsArray.getString(0))) {
	                    nestedObject.remove("tags");
	                }
	            }
	            removeTagsHandles(nestedObject);  // Recursively call for deeper levels
	        } else if (value instanceof JSONArray) {
	            JSONArray jsonArray = (JSONArray) value;
	            for (int i = 0; i < jsonArray.length(); i++) {
	                Object arrayElement = jsonArray.get(i);
	                if (arrayElement instanceof JSONObject) {
	                    removeTagsHandles((JSONObject) arrayElement);  // Recursively handle each element
	                }
	            }
	        }
	    }
	}
	
	public String replaceArrayHandleValuesForUpdateIdentity(String inputJson, String testCaseName) {
	    JSONObject jsonObj = new JSONObject(inputJson);
	    JSONObject request = jsonObj.getJSONObject("request");
	    JSONObject identity = request.getJSONObject("identity");
	    JSONArray selectedHandles = identity.getJSONArray("selectedHandles");
	    String phone = getValueFromAuthActuator("json-property", "phone_number");
        String result = phone.replaceAll("\\[\"|\"\\]", "");
        String email = getValueFromAuthActuator("json-property", "emailId");
        String emailResult = email.replaceAll("\\[\"|\"\\]", "");
	    
	   

	    // Iterate over each handle in the selectedHandles array
	    for (int i = 0; i < selectedHandles.length(); i++) {
	        String handle = selectedHandles.getString(i);

	        // Check if the handle exists in identity and if its value is a JSONArray
	        if (identity.has(handle) && identity.get(handle) instanceof JSONArray) {
	            JSONArray handleArray = identity.getJSONArray(handle);

	            if (testCaseName.contains("_withupdatevalues")) {
	                for (int j = 0; j < handleArray.length(); j++) {
	                    JSONObject handleObj = handleArray.getJSONObject(j);
	                    handleObj.put("value", "mosip" + RANDOM_ID + "_" + j);
	                }
	            } else if (testCaseName.contains("_withmultiplevalues")) {
	                for (int j = 0; j < handleArray.length(); j++) {
	                    JSONObject handleObj = handleArray.getJSONObject(j);
	                    JSONArray valuesArray = new JSONArray();
	                    valuesArray.put("mosip501724826584965_modified_1");
	                    valuesArray.put("mosip501724826584965_modified_2");
	                    valuesArray.put("mosip501724826584965_modified_3");
	                    handleObj.put("values", valuesArray);
	                }
	            } else if (testCaseName.contains("_withupdatetags")) {
	                for (int j = 0; j < handleArray.length(); j++) {
	                    JSONObject handleObj = handleArray.getJSONObject(j);
	                    JSONArray tags = handleObj.optJSONArray("tags");
	                    if (tags != null) {
	                        for (int k = 0; k < tags.length(); k++) {
	                            tags.put(k, tags.getString(k) + "_invalid" + RANDOM_ID);
	                        }
	                    }
	                }
	            } else if (testCaseName.contains("_withupdatetagsandhandles")) {
	                for (int j = 0; j < handleArray.length(); j++) {
	                    JSONObject handleObj = handleArray.getJSONObject(j);
	                    JSONArray tags = handleObj.optJSONArray("tags");
	                    if (tags != null) {
	                        for (int k = 0; k < tags.length(); k++) {
	                            tags.put(k, tags.getString(k) + "_invalid" + RANDOM_ID);
	                        }
	                    }
	                    JSONArray values = handleObj.optJSONArray("value");
	                    if (values != null) {
	                        for (int k = 0; k < values.length(); k++) {
	                            values.put(k, values.getString(k) + "_invalid" + RANDOM_ID);
	                        }
	                    }
	                }
	            } else if (testCaseName.contains("_withmultipledemohandles")) {
	                // Handle specific demo handles by checking and adding them to the selectedHandles
	                
	                boolean containsPhone = false;
	                for (int j = 0; j < selectedHandles.length(); j++) {
	                    if (result.equalsIgnoreCase(selectedHandles.getString(j))) {
	                        containsPhone = true;
	                        break;
	                    }
	                }
	                if (!containsPhone) {
	                    selectedHandles.put(result);
	                    JSONObject phoneEntry = new JSONObject();
	                    phoneEntry.put("value", "$PHONENUMBERFORIDENTITY$");
	                    JSONArray phoneArray = new JSONArray();
	                    phoneArray.put(phoneEntry);
	                    identity.put(result, phoneArray);
	                }
	            } else if (testCaseName.contains("_withdeletehandlefromrecord")) {
	                for (int j = 0; j < selectedHandles.length(); j++) {
	                    String handleToDelete = selectedHandles.getString(j);
	                    if (identity.has(handleToDelete)) {
	                        identity.remove(handleToDelete);
	                    }
	                }
	                identity.remove("selectedHandles");
	            } else if (testCaseName.contains("_withupdatedselectedhandle")) {
	                String firstHandle = selectedHandles.getString(0);
	                String updatedHandle = firstHandle + RANDOM_ID;
	                selectedHandles.put(0, updatedHandle);
	            } else if (testCaseName.contains("_withupdatedselectedhandleanddemo")) {
	                if (selectedHandles.length() > 0) {
	                    String originalHandle = selectedHandles.getString(0);
	                    String updatedHandle = originalHandle + RANDOM_ID;
	                    selectedHandles.put(0, updatedHandle);
	                    if (identity.has(originalHandle)) {
	                        JSONArray originalHandleArray = identity.getJSONArray(originalHandle);
	                        for (int J = 0; J < originalHandleArray.length(); J++) {
	                            JSONObject handleObject = originalHandleArray.getJSONObject(i);
	                            String originalValue = handleObject.optString("value", "");
	                            handleObject.put("value", originalValue + RANDOM_ID);
	                            originalHandleArray.put(J, handleObject);
	                        }
	                        identity.remove(originalHandle);
	                        identity.put(updatedHandle, originalHandleArray);
	                    }
	                }
	            } else if (testCaseName.contains("_withupdatedselectedhandleandfirstattribute")) {
	                Iterator<String> keys = identity.keys();
	                if (keys.hasNext()) {
	                    String firstKey = keys.next();
	                    if (!firstKey.equals("selectedHandles")) {
	                        selectedHandles.put(0, firstKey);
	                        if (identity.has(firstKey)) {
	                            JSONArray originalArray = identity.getJSONArray(firstKey);
	                            for (int j = 0; j < originalArray.length(); j++) {
	                                JSONObject handleObject = originalArray.getJSONObject(j);
	                                if (handleObject.has("value")) {
	                                    String originalValue = handleObject.getString("value");
	                                    handleObject.put("value", originalValue + "123");
	                                }
	                                if (handleObject.has("tags")) {
	                                    JSONArray tagsArray = handleObject.getJSONArray("tags");
	                                    for (int k = 0; k < tagsArray.length(); k++) {
	                                        String tag = tagsArray.getString(k);
	                                        tagsArray.put(k, tag + "123");
	                                    }
	                                    handleObject.put("tags", tagsArray);
	                                }
	                                originalArray.put(j, handleObject);
	                            }
	                            identity.remove(firstKey);
	                            identity.put(firstKey, originalArray);
	                        }
	                    }
	                }
	            }
	            else if (testCaseName.contains("_withremovedtaggedattribute")) {
	                for (int j = 0; j < selectedHandles.length(); j++) {
	                    String handle1 = selectedHandles.getString(j);

	                    if (identity.has(handle1) && identity.get(handle1) instanceof JSONArray) {
	                        JSONArray handleArray1 = identity.getJSONArray(handle1);

	                        for (int k = 0; k < handleArray1.length(); k++) {
	                            JSONObject handleObject = handleArray1.getJSONObject(k);
	                            if (handleObject.has("tags")) {
	                                handleObject.remove("tags");
	                            }
	                        }
	                        identity.put(handle, handleArray);
	                    }
	                }
	            }
	            else if (testCaseName.contains("_withemptyhandles")) {
	                    identity.remove("selectedHandles");
	                 
	            }
	            
	            else if (testCaseName.contains("_withouthandlesattr")) {
	                if (identity.has("selectedHandles")) {
	                     selectedHandles = identity.getJSONArray("selectedHandles");
	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                         handle = selectedHandles.getString(j);
	                        if (identity.has(handle)) {
	                            identity.remove(handle);
	                        }
	                    }
	                    identity.remove("selectedHandles");
	                }
	            }
	            
	            //44
	            else if (testCaseName.contains("_withinvaliddemofield")) {
	                if (identity.has("selectedHandles")) {
	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                        if (identity.has(handle)) {
	                            Object currentValue = identity.get(handle);
	                            if (currentValue instanceof String) {
	                                identity.put(handle, "invalid_" + currentValue);
	                            } else if (currentValue instanceof JSONArray) {
	                                JSONArray jsonArray = (JSONArray) currentValue;
	                                for (int k = 0; k < jsonArray.length(); k++) {
	                                    JSONObject obj = jsonArray.getJSONObject(k);
	                                    if (obj.has("value")) {
	                                        obj.put("value", "invalid_" + obj.getString("value"));
	                                    }
	                                }
	                                identity.put(handle, jsonArray);
	                            }
	                        }
	                        selectedHandles.put(i, "invalid_" + handle);
	                    }
	                    identity.put("selectedHandles", selectedHandles);
	                }
	            }
	            //49
	            else if (testCaseName.contains("_withoutselectedhandlesandattri")) {

	                for (int j = 0; j < selectedHandles.length(); j++) {

	                    if (identity.has(handle)) {
	                        identity.remove(handle);
	                    }
	                }

	                identity.remove("selectedHandles");
	            }
	            
	            else if (testCaseName.contains("_withalldemofieldsremoved")) {

	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                        if (identity.has(handle)) {
	                            identity.remove(handle);
	                        }
	                    }
	            }
	            
	            else if (testCaseName.contains("_withemptyselectedhandle")) {
	                if (identity.has("selectedHandles")) {
	                    identity.put("selectedHandles", new JSONArray());
	                }
	            }
	            
	            
	            else if (testCaseName.contains("_witharandomnonhandleattr")) {
	                if (identity.has("selectedHandles")) {
	                    List<String> existingHandles = new ArrayList<>();
	                    for (int j = 0; j < selectedHandles.length(); j++) {
	                        existingHandles.add(selectedHandles.getString(j));
	                    }
	                    Iterator<String> keys = identity.keys();
	                    while (keys.hasNext()) {
	                        String key = keys.next();
	                        if (key.equals("selectedHandles")) {
	                            continue;
	                        }
	                        if (!existingHandles.contains(key)) {
	                            selectedHandles.put(key);
	                            break; 
	                        }
	                    }
	                }
	            }
	            
	            else if (testCaseName.contains("_updateselectedhandleswithinvalid")) {
	            	JSONArray updatedHandlesArray = new JSONArray();
	                updatedHandlesArray.put("invalidscehema123");
	                identity.put("selectedHandles", updatedHandlesArray);
	            }
	            
	            else if (testCaseName.contains("_withinvaliddhandle")) {
	            	    selectedHandles.put("newFieldHandle");
	            }
	            
	            else if (testCaseName.contains("_updateselectedhandleswithscehmaattrwhichisnothandle")) {
	                Iterator<String> keys = identity.keys();
	                while (keys.hasNext()) {
	                    String key = keys.next();
	                    if (!selectedHandles.toList().contains(key) && identity.optString(key) != null && identity.get(key) instanceof String) {
	                        selectedHandles.put(key); 
	                        break; 
	                    }
	                }
	            }
	            
	            else if (testCaseName.contains("_removeselectedhandle_updatephone")) {
	                if (identity.has("selectedHandles")) {
	                    identity.remove("selectedHandles");
	                }

	                if (identity.has(result)) {
	                    identity.put(result, generateRandomNumberString(10));
	                }
	            }
	            
	            else if (testCaseName.contains("_withupdatedhandlewhichisnotinschema")) {
	            	JSONArray newSelectedHandles = new JSONArray();
	                newSelectedHandles.put("invalid12@@");
	                identity.put("selectedHandles", newSelectedHandles);
	            }
	            
	            else if (testCaseName.contains("_replaceselectedhandles")) {
                	identity.put("selectedHandles", new JSONArray().put(result));
                }
	            
	            else if (testCaseName.contains("_updatewithphoneemail")) {
	                JSONArray updatedHandles = new JSONArray();
	                updatedHandles.put(emailResult);
	                updatedHandles.put(result);
	                
	                identity.put("selectedHandles", updatedHandles);
	            }
	            else if (testCaseName.contains("_withusedphone")) {
	                if (identity.has(result)) {
	                    identity.put(result, "$ID:AddIdentity_array_handle_value_smoke_Pos_withphonenumber_PHONE$" );
	                }
	            }
	            else if (testCaseName.contains("_withphonevalue")) {
	                if (identity.has(result)) {
	                    identity.put(result, "$ID:AddIdentity_array_handle_value_smoke_Pos_withselectedhandlephone_PHONE$" );
	                }
	            }
	            else if (testCaseName.contains("_removeselectedhandlesandupdateemail")) {
	            	 if (identity.has("selectedHandles")) {
		                    identity.remove("selectedHandles");
		                }
	            	if (identity.has(emailResult)) {
	                    identity.put(emailResult, "$ID:AddIdentity_array_handle_value_update_smoke_Pos_withselectedhandlephone_EMAIL$" );
	                }                 
            }

	            identity.put(handle, handleArray);
	        }
	    }

	    // Return the modified JSON as a string
	    return jsonObj.toString();
	}



	
	
	public static void setfoundHandlesInIdSchema(boolean foundHandles) {
		
		foundHandlesInIdSchema=foundHandles;
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
}


