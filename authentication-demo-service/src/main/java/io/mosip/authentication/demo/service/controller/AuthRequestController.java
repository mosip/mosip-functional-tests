/*
 * 
 */
package io.mosip.authentication.demo.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.demo.service.controller.Encrypt.SplittedEncryptedData;
import io.mosip.authentication.demo.service.dto.CertificateChainResponseDto;
import io.mosip.authentication.demo.service.dto.EncryptionRequestDto;
import io.mosip.authentication.demo.service.dto.EncryptionResponseDto;
import io.mosip.authentication.demo.service.helper.CertificateTypes;
import io.mosip.authentication.demo.service.helper.KeyMgrUtil;
import io.mosip.authentication.demo.service.helper.PartnerTypes;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils2;
import io.swagger.annotations.Api;

/**
 * The Class AuthRequestController is used to automate the creation of Auth
 * Request.
 * 
 * @author Arun Bose S
 */
@RestController
@Api(tags = { "Authentication Request Creation" })
public class AuthRequestController {

	private static final String PHONE = "PHONE";

	private static final String EMAIL = "EMAIL";

	private static final String CHANNELS = "channels";

	private static final String DATE_TIME = "dateTime";

	private static final String MOSIP_ENV = "mosip.env";

	private static final String MOSIP_DOMAINURI = "mosip.domainUri";

	private static final String REQ_ID = "reqId";

	private static final String PROP_PARTNER_URL_SUFFIX = "partnerUrlSuffix";

	private static final String SPEC_VERSION = "specVersion";

	private static final String MOSIP_BASE_URL = "mosip.base.url";

	private static final String ENV = "env";

	private static final String DOMAIN_URI = "domainUri";

	private static final String TRANSACTION_ID = "transactionId";

	private static final String DIGITAL_ID = "digitalId";

	private static final String SESSION_KEY = "sessionKey";

	private static final String BIO_VALUE = "bioValue";

	private static final String DATA = "data";

	private static final String BIOMETRICS = "biometrics";

	private static final String IDENTITY = "Identity";

	private static final String SECONDARY_LANG_CODE = "secondaryLangCode";

	/** The Constant TEMPLATE. */
	private static final String TEMPLATE = "Template";

	private static final String PIN = "pin";

	private static final String BIO = "bio";

	private static final String DEMO = "demo";

	private static final String OTP = "otp";

	private static final String TIMESTAMP = "timestamp";

	private static final String TXN = "txn";

	private static final String VER = "ver";

	private static final String IDA_API_VERSION = "ida.api.version";

	private static final String AUTH_TYPE = "authType";

	private static final String UIN = "UIN";

	private static final String ID_TYPE = "idType";

	private static final String IDA_AUTH_REQUEST_TEMPLATE = "ida.authRequest.template";

	private static final String ID = "id";

	private static final String CLASSPATH = "classpath";

	private static final String ENCODE_TYPE = "UTF-8";

	private static final String DEFAULT_OTP_REQ_TEMPLATE = "{\r\n" + "  \"id\": \"${reqId}\",\r\n"
			+ "  \"individualId\": \"${id}\",\r\n" + "  \"otpChannel\": [\r\n" + "    \"email\",\r\n"
			+ "    \"phone\"\r\n" + "  ],\r\n" + "  \"requestTime\": \"${timestamp}\",\r\n"
			+ "  \"transactionID\": \"${txn}\",\r\n" + "  \"version\": \"${ver}\"\r\n" + "}";

	private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
	private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Autowired
	private Encrypt encrypt;

	@Autowired
	private Environment environment;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	JWSSignAndVerifyController jWSSignAndVerifyController;

	@Autowired
	KeyMgrUtil keyMgrUtil;

	@PostConstruct
	public void idTemplateManagerPostConstruct() {
		templateManager = templateManagerBuilder.encodingType(ENCODE_TYPE).enableCache(false).resourceLoader(CLASSPATH)
				.build();
	}

	/**
	 * this method is used to create the auth request.
	 *
	 * @param id            the id
	 * @param idType        the id type
	 * @param isKyc         the is kyc
	 * @param isInternal    the is internal
	 * @param reqAuth       the req auth
	 * @param transactionId the transaction id
	 * @param request       the request
	 * @return the string
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(path = "/createAuthRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
			MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> createAuthRequest(@RequestParam(name = ID, required = true) @Nullable String id,
			@RequestParam(name = ID_TYPE, required = false) @Nullable String idType,
			@RequestParam(name = "isKyc", required = false) @Nullable boolean isKyc,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "Authtype", required = false) @Nullable String reqAuth,
			@RequestParam(name = TRANSACTION_ID, required = false) @Nullable String transactionId,
			@RequestParam(name = "requestTime", required = false) @Nullable String requestTime,
			@RequestParam(name = "isNewInternalAuth", required = false) @Nullable boolean isNewInternalAuth,
			@RequestParam(name = "isPreLTS", required = false) @Nullable boolean isPreLTS,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName,
			@RequestBody Map<String, Object> request) throws Exception {
		String authRequestTemplate = environment.getProperty(IDA_AUTH_REQUEST_TEMPLATE);
		Map<String, Object> reqValues = new HashMap<>();

		if (isPreLTS) {
			reqValues.put(OTP, false);
			reqValues.put(DEMO, false);
			reqValues.put(BIO, false);
			reqValues.put(PIN, false);
		}

		if (isNewInternalAuth) {
			isInternal = true;
		}

		boolean needsEncryption = !isInternal || !isNewInternalAuth;
		String keysDirPath = keyMgrUtil.getKeysDirPath();

		if (needsEncryption) {
			reqValues.put("thumbprint",
					digest(getCertificateThumbprint(encrypt.getCertificate(isInternal, keysDirPath))));
		}

		if (requestTime == null) {
			requestTime = DateUtils.getUTCCurrentDateTimeString(environment.getProperty("datetime.pattern"));

		}

		if (!request.containsKey(TIMESTAMP)) {
			request.put(TIMESTAMP, "");// Initializing. Setting value is done in further steps.
		}

		idValuesMap(id, isKyc, isInternal, reqValues, transactionId, requestTime);
		getAuthTypeMap(reqAuth, reqValues, request);
		applyRecursively(request, TIMESTAMP, requestTime);
		applyRecursively(request, DATE_TIME, requestTime);
		applyRecursively(request, TRANSACTION_ID, transactionId);

		if (needsEncryption) {
			if (reqValues.get(BIO) != null && Boolean.valueOf(reqValues.get(BIO).toString())) {
				Object bioObj = request.get(BIOMETRICS);
				if (bioObj instanceof List) {
					List<Map<String, Object>> encipheredBiometrics = encipherBiometrics(isInternal, requestTime,
							transactionId, partnerName, keyFileNameByPartnerName, (List<Map<String, Object>>) bioObj);
					request.put(BIOMETRICS, encipheredBiometrics);
				}
			}
			encryptValuesMap(request, reqValues, isInternal);
		}

		StringWriter writer = new StringWriter();
		InputStream templateValue;
		if (request != null && request.size() > 0) {
			templateValue = templateManager
					.merge(new ByteArrayInputStream(authRequestTemplate.getBytes(StandardCharsets.UTF_8)), reqValues);

			if (templateValue != null) {
				IOUtils.copy(templateValue, writer, StandardCharsets.UTF_8);
				String res = writer.toString();
				if (!needsEncryption) {
					Map<String, Object> resMap = mapper.readValue(res.getBytes(StandardCharsets.UTF_8), Map.class);
					resMap.put("request", request);
					resMap.put("requestHMAC", null);
					resMap.put("requestSessionKey", null);
					resMap.put("thumbprint", null);

					res = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resMap);
				}
				if (reqValues.containsKey(SECONDARY_LANG_CODE)) {
					Map<String, Object> resMap = mapper.readValue(res.getBytes(StandardCharsets.UTF_8), Map.class);
					resMap.put(SECONDARY_LANG_CODE, reqValues.get(SECONDARY_LANG_CODE));
					res = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resMap);
				}
				if (isPreLTS) {
					Map<String, Object> resMap = mapper.readValue(res.getBytes(StandardCharsets.UTF_8), Map.class);
					Map<String, Object> requestedAuth = new HashMap<>();
					resMap.put("individualIdType",
							idType == null || idType.trim().length() == 0 ? IdType.UIN.toString() : idType);
					resMap.put("requestedAuth", requestedAuth);
					if (Boolean.valueOf(String.valueOf(reqValues.get(OTP)))) {
						requestedAuth.put("otp", true);
					}
					if (Boolean.valueOf(String.valueOf(reqValues.get(DEMO)))) {
						requestedAuth.put("demo", true);
					}
					if (Boolean.valueOf(String.valueOf(reqValues.get(BIO)))) {
						requestedAuth.put("bio", true);
					}
					if (Boolean.valueOf(String.valueOf(reqValues.get(PIN)))) {
						requestedAuth.put("pin", true);
					}
					res = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resMap);
				}
				ObjectNode response = mapper.readValue(res.getBytes(), ObjectNode.class);

				HttpHeaders httpHeaders = new HttpHeaders();
				String responseStr = response.toString();
				// httpHeaders.add("signature", jWSSignAndVerifyController.sign(responseStr,
				// false));
				PartnerTypes partnerTypes = isKyc ? PartnerTypes.EKYC : PartnerTypes.RELYING_PARTY;

				String rpSignature = signRequest(partnerTypes, partnerName, keyFileNameByPartnerName, responseStr);
				httpHeaders.add("signature", rpSignature);
				return new ResponseEntity<>(responseStr, httpHeaders, HttpStatus.OK);
			} else {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), String.format(
								IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), TEMPLATE));
			}

		} else {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), IDENTITY));
		}
	}

	/**
	 * this method is used to create the auth request.
	 *
	 * @param id            the id
	 * @param idType        the id type
	 * @param isKyc         the is kyc
	 * @param isInternal    the is internal
	 * @param reqAuth       the req auth
	 * @param transactionId the transaction id
	 * @param request       the request
	 * @return the string
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(path = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> authenticate(
			@RequestParam(name = ID, required = true) @Nullable String id,
			@RequestParam(name = ID_TYPE, required = false) @Nullable String idType,
			@RequestParam(name = "isLocal", required = false) @Nullable boolean isLocal,
			@RequestParam(name = "isKyc", required = false) @Nullable boolean isKyc,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "Authtype", required = false) @Nullable String reqAuth,
			@RequestParam(name = TRANSACTION_ID, required = false) @Nullable String transactionId,
			@RequestParam(name = PROP_PARTNER_URL_SUFFIX, required = false) @Nullable String partnerUrlSuffix,
			@RequestParam(name = "requestTime", required = false) @Nullable String requestTime,
			@RequestParam(name = "isNewInternalAuth", required = false) @Nullable boolean isNewInternalAuth,
			@RequestParam(name = "isPreLTS", required = false) @Nullable boolean isPreLTS,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName,
			@RequestBody Map<String, Object> request) throws Exception {
		ResponseEntity<String> authRequest = this.createAuthRequest(id, idType, isKyc, isInternal, reqAuth,
				transactionId, requestTime, isNewInternalAuth, isPreLTS, partnerName, keyFileNameByPartnerName,
				request);
		String reqBody = authRequest.getBody();
		String reqSignature = authRequest.getHeaders().get("signature").get(0);

		RestTemplate restTemplate = encrypt.createRestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("signature", reqSignature);
		httpHeaders.add("Authorization", reqSignature);
		httpHeaders.add("Content-Type", "application/json");
		HttpEntity<String> httpEntity = new HttpEntity<>(reqBody, httpHeaders);
		Map<String, Object> reqBodyMap = mapper.readValue(reqBody, Map.class);
		URI authRequestUrl = getAuthRequestUrl((String) reqBodyMap.get("id"), isLocal, partnerUrlSuffix,
				isNewInternalAuth);

		Map<String, Object> respMap = new LinkedHashMap<>();

		respMap.put("URL", authRequestUrl);

		Map<String, Object> authReqMap = new LinkedHashMap<>();
		authReqMap.put("body", reqBody);
		authReqMap.put("signature", reqSignature);
		respMap.put("authRequest", authReqMap);

		Map<String, Object> authRespBody = new LinkedHashMap<>();
		Object respBody;
		String respSignature;
		try {
			ResponseEntity<Map> authResponse = restTemplate.exchange(authRequestUrl, HttpMethod.POST, httpEntity,
					Map.class);
			respBody = authResponse.getBody();
			List<ServiceError> serviceErrorList = ExceptionUtils
					.getServiceErrorList(mapper.writeValueAsString(respBody));
			if (serviceErrorList.isEmpty()) {
				List<String> signatureHeaders = authResponse.getHeaders().get("response-signature");
				respSignature = signatureHeaders != null ? authResponse.getHeaders().get("response-signature").get(0)
						: null;
				authRespBody.put("signature", respSignature);
			}
		} catch (RestClientException e) {
			respBody = e instanceof HttpServerErrorException ? ((HttpServerErrorException) e).getResponseBodyAsString()
					: ExceptionUtils.getStackTrace(e);
		}

		authRespBody.put("body", respBody);
		respMap.put("authResponse", authRespBody);

		ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity(respMap, HttpStatus.OK);
		return responseEntity;

	}

	/**
	 * this method is used to create the auth request.
	 *
	 * @param id            the id
	 * @param idType        the id type
	 * @param isKyc         the is kyc
	 * @param isInternal    the is internal
	 * @param reqAuth       the req auth
	 * @param transactionId the transaction id
	 * @param request       the request
	 * @return the string
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(path = "/sendOtp", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> sendOtp(@RequestParam(name = ID, required = true) @Nullable String id,
			@RequestParam(name = ID_TYPE, required = false) @Nullable String idType,
			@RequestParam(name = "isLocal", required = false) @Nullable boolean isLocal,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "isEmail", required = false, defaultValue = "true") @Nullable boolean isEmail,
			@RequestParam(name = "isPhone", required = false, defaultValue = "true") @Nullable boolean isPhone,
			@RequestParam(name = TRANSACTION_ID, required = false) @Nullable String transactionId,
			@RequestParam(name = PROP_PARTNER_URL_SUFFIX, required = false) @Nullable String partnerUrlSuffix,
			@RequestParam(name = "isPreLTS", required = false) @Nullable boolean isPreLTS,
			@RequestParam(name = "requestTime", required = false) @Nullable String requestTime,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName)
			throws Exception {

		ResponseEntity<String> otpReqEntity = createOtpRequestBody(isInternal, idType, isEmail, isPhone, id,
				transactionId, isPreLTS, requestTime, partnerName, keyFileNameByPartnerName);
		String reqSignature = otpReqEntity.getHeaders().get("signature").get(0);
		String reqBody = otpReqEntity.getBody();

		RestTemplate restTemplate = encrypt.createRestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("signature", reqSignature);
		httpHeaders.add("Authorization", reqSignature);
		httpHeaders.add("Content-Type", "application/json");
		HttpEntity<String> httpEntity = new HttpEntity<>(reqBody, httpHeaders);
		Map<String, Object> reqBodyMap = mapper.readValue(reqBody, Map.class);
		URI otpRequestUrl = getOtpRequestUrl((String) reqBodyMap.get("id"), isLocal, partnerUrlSuffix);

		Map<String, Object> respMap = new LinkedHashMap<>();

		respMap.put("URL", otpRequestUrl);

		Map<String, Object> otpReqMap = new LinkedHashMap<>();
		otpReqMap.put("body", reqBody);
		otpReqMap.put("signature", reqSignature);
		respMap.put("otpRequest", otpReqMap);

		Map<String, Object> otpRespBody = new LinkedHashMap<>();
		Object respBody;
		String respSignature;
		try {
			ResponseEntity<Map> authResponse = restTemplate.exchange(otpRequestUrl, HttpMethod.POST, httpEntity,
					Map.class);
			respBody = authResponse.getBody();
			respSignature = authResponse.getHeaders().get("response-signature").get(0);
			otpRespBody.put("signature", respSignature);
		} catch (RestClientException e) {
			respBody = e instanceof HttpServerErrorException ? ((HttpServerErrorException) e).getResponseBodyAsString()
					: ExceptionUtils.getStackTrace(e);
		}

		otpRespBody.put("body", respBody);
		respMap.put("otpResponse", otpRespBody);

		ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity(respMap, HttpStatus.OK);
		return responseEntity;

	}

	@SuppressWarnings("unchecked")
	@PostMapping(path = "/createOtpReqest", produces = { MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> createOtpRequestBody(
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = ID_TYPE, required = false) @Nullable String idType,
			@RequestParam(name = "isEmail", required = false, defaultValue = "true") @Nullable boolean isEmail,
			@RequestParam(name = "isPhone", required = false, defaultValue = "true") @Nullable boolean isPhone,
			@RequestParam(name = ID, required = true) @NonNull String id,
			@RequestParam(name = TRANSACTION_ID, required = false) @Nullable String transactionId,
			@RequestParam(name = "isPreLTS", required = false) @Nullable boolean isPreLTS,
			@RequestParam(name = "requestTime", required = false) @Nullable String requestTime,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName)
			throws IOException, IdAuthenticationBusinessException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableEntryException, KeyStoreException, CertificateException, OperatorCreationException,
			JoseException {
		String otpReqTemplate = environment.getProperty("otpRequestTemplate", DEFAULT_OTP_REQ_TEMPLATE);

		Map<String, Object> reqValues = new HashMap<>();

		if (requestTime == null) {
			requestTime = DateUtils.getUTCCurrentDateTimeString(environment.getProperty("datetime.pattern"));

		}
		if (isPreLTS) {
			if (null != idType) {
				reqValues.put(ID_TYPE, idType);
			} else {
				reqValues.put(ID_TYPE, UIN);
			}

		}

		List<String> channels = new ArrayList<String>();
		if (isEmail) {
			channels.add(EMAIL);
		}
		if (isPhone) {
			channels.add(PHONE);
		}

		if (!isEmail && !isPhone) {
			channels.add(EMAIL);
		}

		String channelStr = channels.stream().collect(Collectors.joining("\",\"", "\"", "\""));

		reqValues.put(CHANNELS, channelStr);
		idValuesMapForOtpReq(id, isInternal, reqValues, transactionId, requestTime);

		StringWriter writer = new StringWriter();
		InputStream templateValue;
		templateValue = templateManager.merge(new ByteArrayInputStream(otpReqTemplate.getBytes(StandardCharsets.UTF_8)),
				reqValues);

		if (templateValue != null) {
			IOUtils.copy(templateValue, writer, StandardCharsets.UTF_8);
			String res = writer.toString();
			if (isPreLTS) {
				Map<String, Object> resMap = mapper.readValue(res.getBytes(StandardCharsets.UTF_8), Map.class);
				resMap.put("individualIdType",
						idType == null || idType.trim().length() == 0 ? IdType.UIN.toString() : idType);
				res = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resMap);
			}
			ObjectNode response = mapper.readValue(res.getBytes(), ObjectNode.class);
			HttpHeaders httpHeaders = new HttpHeaders();
			String responseStr = response.toString();
			httpHeaders.add("signature",
					signRequest(PartnerTypes.RELYING_PARTY, partnerName, keyFileNameByPartnerName, responseStr));
			return new ResponseEntity<>(responseStr, httpHeaders, HttpStatus.OK);
		} else {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), TEMPLATE));
		}

	}

	@PostMapping(path = "/signRequest", produces = { MediaType.TEXT_PLAIN_VALUE })
	public String signRequest(
			@RequestParam(name = "partnerType", required = true, defaultValue = "RELYING_PARTY") @NonNull PartnerTypes partnerType,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName,
			@RequestBody String request) throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException,
			KeyStoreException, CertificateException, IOException, OperatorCreationException {
		return jWSSignAndVerifyController.sign(request, false, true, false, null, keyMgrUtil.getKeysDirPath(),
				partnerType, partnerName, keyFileNameByPartnerName);
	}

	private void idValuesMapForOtpReq(String id, boolean isInternal, Map<String, Object> reqValues,
			String transactionId, String utcCurrentDateTimeString) {
		reqValues.put(ID, id);
		if (isInternal) {
			reqValues.put(REQ_ID, "mosip.identity.otp.internal");
		} else {
			reqValues.put(REQ_ID, "mosip.identity.otp");
		}
		reqValues.put(TIMESTAMP, utcCurrentDateTimeString);
		reqValues.put(TXN, transactionId == null ? "1234567890" : transactionId);
		reqValues.put(VER, environment.getProperty(IDA_API_VERSION));
	}

	private URI getAuthRequestUrl(String reqId, boolean isLocal, String partnerUrlSuffix, boolean isNewInternalAuth) {
		String baseUrl;
		String urlSuffix;
		String envBaseUrl = environment.getProperty(MOSIP_BASE_URL);

		boolean isInternal = false;
		switch (reqId) {
		case "mosip.identity.auth":
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("auth.port", "8090") : envBaseUrl;
			urlSuffix = "/idauthentication/v1/auth";
			break;
		case "mosip.identity.kyc":
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("kyc.port", "8090") : envBaseUrl;
			urlSuffix = "/idauthentication/v1/kyc";
			break;
		case "mosip.identity.auth.internal":
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("internal.port", "8093") : envBaseUrl;
			urlSuffix = isNewInternalAuth ? "/idauthentication/v1/internal/verifyidentity"
					: "/idauthentication/v1/internal/auth";
			isInternal = true;
			break;
		default:
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("auth.port", "8090") : envBaseUrl;
			urlSuffix = "/idauthentication/v1/auth";
			break;
		}

		String url = baseUrl + urlSuffix;

		if (!isInternal) {
			String partnerSuffix = partnerUrlSuffix == null ? environment.getProperty(PROP_PARTNER_URL_SUFFIX)
					: partnerUrlSuffix;
			if (partnerSuffix == null) {
				throw new NullPointerException("partnerUrlSuffix is not specified");
			}
			url += "/" + partnerSuffix;
		}
		return URI.create(url);
	}

	private URI getOtpRequestUrl(String reqId, boolean isLocal, String partnerUrlSuffix) {
		String baseUrl;
		String urlSuffix;
		String envBaseUrl = environment.getProperty(MOSIP_BASE_URL);

		boolean isInternal = false;
		switch (reqId) {
		case "mosip.identity.otp":
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("otp.port", "8092") : envBaseUrl;
			urlSuffix = "/idauthentication/v1/otp";
			break;
		case "mosip.identity.otp.internal":
			baseUrl = isLocal ? "http://localhost:" + environment.getProperty("internal.port", "8093") : envBaseUrl;
			urlSuffix = "/idauthentication/v1/internal/otp";
			isInternal = true;
			break;
		default:
			baseUrl = isLocal ? "http://localhost:8092" : envBaseUrl;
			urlSuffix = "/idauthentication/v1/auth";
			break;
		}

		String url = baseUrl + urlSuffix;

		if (!isInternal) {
			String partnerSuffix = partnerUrlSuffix == null ? environment.getProperty(PROP_PARTNER_URL_SUFFIX)
					: partnerUrlSuffix;
			url += "/" + partnerSuffix;
		}
		return URI.create(url);
	}

	public byte[] getCertificateThumbprint(Certificate cert) throws CertificateEncodingException {
		return DigestUtils.sha256(cert.getEncoded());
	}

	@SuppressWarnings("unchecked")
	@PostMapping(path = "/encipherBiometricData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> encipherBiometrics(
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "timestamp", required = false) @Nullable String timestampArg,
			@RequestParam(name = "transactionId", required = false) @Nullable String transactionIdArg,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName,
			@RequestBody List<Map<String, Object>> biometrics) throws KeyManagementException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException,
			JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException,
			CertificateException, UnrecoverableEntryException, JoseException, OperatorCreationException {
		byte[] previousHash = getHash("");

		for (Map<String, Object> bioMap : biometrics) {
			Object data = bioMap.get(DATA);
			if (data == null) {
				break;
			}

			if (data instanceof Map) {
				Map<String, Object> dataMap = (Map<String, Object>) data;
				String bioValue = (String) dataMap.get(BIO_VALUE);
				String timestamp = timestampArg;
				if (timestamp == null) {
					timestamp = (String) dataMap.get(TIMESTAMP);
				}
				if (timestamp == null) {
					timestamp = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
				}

				dataMap.put(TIMESTAMP, timestamp);
				dataMap.put(DOMAIN_URI,
						environment.getProperty(MOSIP_DOMAINURI, environment.getProperty(MOSIP_BASE_URL)));
				dataMap.put(ENV, environment.getProperty(MOSIP_ENV, "Staging"));
				dataMap.put(SPEC_VERSION, "1.0");
				Object txnIdObj = dataMap.get(TRANSACTION_ID);
				if (txnIdObj == null) {
					dataMap.put(TRANSACTION_ID, transactionIdArg == null ? "1234567890" : transactionIdArg);
				}
				String transactionId = String.valueOf(dataMap.get(TRANSACTION_ID));
				String keysDirPath = keyMgrUtil.getKeysDirPath();

				// SplittedEncryptedData encryptedBiometrics =
				// encrypt.encryptBiometrics(bioValue, timestamp,
				// transactionId, isInternal);
				SplittedEncryptedData encryptedBiometrics = encrypt.encryptBio(bioValue, timestamp, transactionId,
						isInternal, keysDirPath);

				dataMap.put(BIO_VALUE, encryptedBiometrics.getEncryptedData());
				bioMap.put(SESSION_KEY, encryptedBiometrics.getEncryptedSessionKey());
				bioMap.put("thumbprint",
						digest(getCertificateThumbprint(encrypt.getBioCertificate(isInternal, keysDirPath))));

				Object digitalId = dataMap.get(DIGITAL_ID);
				if (digitalId instanceof Map) {
					Map<String, Object> digitalIdMap = (Map<String, Object>) digitalId;
					String digitalIdStr = mapper.writeValueAsString(digitalIdMap);
					String signedDititalId;
					if (!isInternal) {
						// String signedDititalId = jWSSignAndVerifyController.sign(digitalIdStr, true);
						signedDititalId = jWSSignAndVerifyController.sign(digitalIdStr, true, true, false, null,
								keysDirPath, PartnerTypes.FTM, partnerName, keyFileNameByPartnerName);
					} else {
						signedDititalId = CryptoUtil.encodeToURLSafeBase64(digitalIdStr.getBytes());
					}
					dataMap.put(DIGITAL_ID, signedDititalId);
				}

				String dataStrJson = mapper.writeValueAsString(dataMap);

				String dataStr;
				if (isInternal) {
					dataStr = CryptoUtil.encodeToURLSafeBase64(dataStrJson.getBytes());
				} else {
					// dataStr = jWSSignAndVerifyController.sign(dataStrJson, true);
					dataStr = jWSSignAndVerifyController.sign(dataStrJson, true, true, false, null, keysDirPath,
							PartnerTypes.DEVICE, partnerName, keyFileNameByPartnerName);
				}
				bioMap.put(DATA, dataStr);

				// Updating hash calculation as per latest changes - 29-May-2021
				byte[] currentHash = getHash(CryptoUtil.decodePlainBase64(bioValue));
				byte[] finalBioDataBytes = new byte[currentHash.length + previousHash.length];
				System.arraycopy(previousHash, 0, finalBioDataBytes, 0, previousHash.length);
				System.arraycopy(currentHash, 0, finalBioDataBytes, previousHash.length, currentHash.length);
				byte[] finalBioDataHash = getHash(finalBioDataBytes);

				/*
				 * String concatenatedHash = previousHash + currentHash; byte[] finalHash =
				 * getHash(concatenatedHash);
				 */
				String finalHashHexEncoded = digest(finalBioDataHash);
				bioMap.put("hash", finalHashHexEncoded);
				previousHash = finalBioDataHash;
			}
		}
		return biometrics;
	}

	private String digest(byte[] hash) throws NoSuchAlgorithmException {
		return DatatypeConverter.printHexBinary(hash).toUpperCase();
	}

	/**
	 * Gets the hash.
	 *
	 * @param string the string
	 * @return the hash
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] getHash(String string) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return getHash(string.getBytes(UTF_8));
	}

	/**
	 * Gets the hash.
	 *
	 * @param bytes the bytes
	 * @return the hash
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] getHash(byte[] bytes) throws NoSuchAlgorithmException {
		return HMACUtils2.generateHash(bytes);
	}

	/**
	 * 
	 *
	 * @param reqAuth
	 * @param reqValues
	 * @param request
	 */
	private void getAuthTypeMap(String reqAuth, Map<String, Object> reqValues, Map<String, Object> request) {
		String[] reqAuthArr;
		if (reqAuth == null) {
			BiFunction<String, String, Optional<String>> authTypeMapFunction = (key, authType) -> Optional
					.ofNullable(request).filter(map -> map.containsKey(key)).map(map -> authType);
			reqAuthArr = Stream
					.of(authTypeMapFunction.apply("demographics", "demo"), authTypeMapFunction.apply(BIOMETRICS, "bio"),
							authTypeMapFunction.apply("otp", "otp"), authTypeMapFunction.apply("staticPin", "pin"))
					.filter(Optional::isPresent).map(Optional::get).toArray(size -> new String[size]);
		} else {
			reqAuth = reqAuth.trim();
			if (reqAuth.contains(",")) {
				reqAuthArr = reqAuth.split(",");
			} else {
				reqAuthArr = new String[] { reqAuth };
			}
		}

		for (String authType : reqAuthArr) {
			authTypeSelectionMap(reqValues, authType);
		}
	}

	private void authTypeSelectionMap(Map<String, Object> reqValues, String authType) {

		if (authType.equalsIgnoreCase(MatchType.Category.OTP.getType())) {
			reqValues.put(OTP, true);
		} else if (authType.equalsIgnoreCase(MatchType.Category.DEMO.getType())) {
			reqValues.put(DEMO, true);
		} else if (authType.equalsIgnoreCase(MatchType.Category.BIO.getType())) {
			reqValues.put(BIO, true);
		} else if (authType.equalsIgnoreCase(MatchType.Category.SPIN.getType())) {
			reqValues.put("pin", true);
		}
	}

	private void encryptValuesMap(Map<String, Object> identity, Map<String, Object> reqValues, Boolean isInternal)
			throws Exception {
		EncryptionRequestDto encryptionRequestDto = new EncryptionRequestDto();
		encodeBioData(identity);
		encryptionRequestDto.setIdentityRequest(identity);
		EncryptionResponseDto encryptionResponse = encrypt.encrypt(encryptionRequestDto, isInternal,
				keyMgrUtil.getKeysDirPath());
		reqValues.put("encHmac", encryptionResponse.getRequestHMAC());
		reqValues.put("encSessionKey", encryptionResponse.getEncryptedSessionKey());
		reqValues.put("encRequest", encryptionResponse.getEncryptedIdentity());
	}

	@SuppressWarnings("unchecked")
	private void encodeBioData(Map<String, Object> identity) {
		List<Object> bioIdentity = (List<Object>) identity.get(IdAuthCommonConstants.BIOMETRICS);
		if (bioIdentity == null) {
			return;
		}
		List<Object> bioIdentityInfo = new ArrayList<>();

		for (Object obj : bioIdentity) {
			Map<String, Object> map = (Map<String, Object>) obj;
			Map<String, Object> dataMap = map.get(DATA) instanceof Map ? (Map<String, Object>) map.get(DATA) : null;
			try {
				if (Objects.nonNull(dataMap)) {
					Object value = CryptoUtil.encodeToURLSafeBase64(mapper.writeValueAsBytes(dataMap));
					map.replace(DATA, value);
				}
			} catch (JsonProcessingException e) {
			}
			bioIdentityInfo.add(map);
		}

		identity.replace(IdAuthCommonConstants.BIOMETRICS, bioIdentityInfo);

	}

	@SuppressWarnings("unchecked")
	private void applyRecursively(Object obj, String key, String value) {
		if (obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) obj;
			Optional<String> matchingKey = map.keySet().stream().filter(k -> k.equalsIgnoreCase(key)).findFirst();
			if (matchingKey.isPresent()) {
				map.put(matchingKey.get(), value);
			}

			for (Object val : map.values()) {
				applyRecursively(val, key, value);
			}
		} else if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			for (Object object : list) {
				applyRecursively(object, key, value);
			}
		}
	}

	private void idValuesMap(String id, boolean isKyc, boolean isInternal, Map<String, Object> reqValues,
			String transactionId, String utcCurrentDateTimeString) {
		reqValues.put(ID, id);
		if (isInternal) {
			reqValues.put(AUTH_TYPE, "auth.internal");
		} else {
			if (isKyc) {
				reqValues.put(AUTH_TYPE, "kyc");
				reqValues.put(SECONDARY_LANG_CODE, environment.getProperty("mosip.secondary-language"));
			} else {
				reqValues.put(AUTH_TYPE, "auth");
			}
		}

		reqValues.put(TIMESTAMP, utcCurrentDateTimeString);
		reqValues.put(TXN, transactionId == null ? "1234567890" : transactionId);
		reqValues.put(VER, environment.getProperty(IDA_API_VERSION));
		reqValues.put(DOMAIN_URI, environment.getProperty(MOSIP_DOMAINURI, environment.getProperty(MOSIP_BASE_URL)));
		reqValues.put(ENV, environment.getProperty(MOSIP_ENV, "Staging"));
	}

	@PostMapping(path = "/uploadIDACertificate", produces = MediaType.TEXT_PLAIN_VALUE)
	public String uploadIDACertificate(
			@RequestParam(name = "certificateType", required = true) CertificateTypes certificateType,
			@RequestBody Map<String, String> requestData) throws CertificateException, IOException {

		String certificateData = requestData.get("certData");
		String fileName = certificateType.getFileName();
		System.out.println("certificateType: " + certificateType.toString());
		System.out.println("FileName: " + fileName);

		X509Certificate x509Cert = (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
		Base64.Encoder base64Encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());
		byte[] certificateBytes = x509Cert.getEncoded();
		String encodedCertificateData = new String(base64Encoder.encode(certificateBytes));
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(BEGIN_CERTIFICATE);
		strBuilder.append(LINE_SEPARATOR);
		strBuilder.append(encodedCertificateData);
		strBuilder.append(LINE_SEPARATOR);
		strBuilder.append(END_CERTIFICATE);
		String certificateStr = strBuilder.toString();

		String keysDirPath = keyMgrUtil.getKeysDirPath();

		Path parentPath = Paths.get(keysDirPath + "/" + fileName).getParent();
		if (parentPath != null && !Files.exists(parentPath)) {
			Files.createDirectories(parentPath);
		}

		boolean isErrored = false;
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(keysDirPath + "/" + fileName))) {
			writer.write(certificateStr);
			writer.flush();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			isErrored = true;
		}

		return isErrored ? "Upload Failed" : "Upload Success";
	}

	@GetMapping(path = "/generatePartnerKeys", produces = MediaType.APPLICATION_JSON_VALUE)
	public CertificateChainResponseDto generatePartnerKeys(
			@RequestParam(name = "partnerType", required = true) PartnerTypes partnerType,
			@RequestParam(name = "partnerName", required = true) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName)
			throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException,
			KeyStoreException, OperatorCreationException {

		return keyMgrUtil.getPartnerCertificates(partnerType, keyMgrUtil.getKeysDirPath(), partnerName,
				keyFileNameByPartnerName);
	}

	@PostMapping(path = "/updatePartnerCertificate", produces = MediaType.TEXT_PLAIN_VALUE)
	public String updatePartnerCertificate(
			@RequestParam(name = "partnerType", required = true) PartnerTypes partnerType,
			@RequestParam(name = "partnerName", required = false) String partnerName,
			@RequestParam(name = "keyFileNameByPartnerName", required = false) boolean keyFileNameByPartnerName,
			@RequestBody Map<String, String> requestData) throws CertificateException, IOException,
			NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {

		String certificateData = requestData.get("certData");
		String filePrepend = partnerType.getFilePrepend();

		X509Certificate x509Cert = (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
		System.out.println("certificateType: " + partnerType.toString());
		System.out.println("filePrepend: " + filePrepend);
		boolean isUpdated = keyMgrUtil.updatePartnerCertificate(filePrepend, x509Cert, keyMgrUtil.getKeysDirPath(),
				partnerName, keyFileNameByPartnerName);
		return isUpdated ? "Update Success" : "Update Failed";
	}

}
