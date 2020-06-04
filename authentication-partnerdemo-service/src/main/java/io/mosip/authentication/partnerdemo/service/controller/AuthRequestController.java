/*
 * 
 */
package io.mosip.authentication.partnerdemo.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOUtils;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.partnerdemo.service.controller.Encrypt.SplittedEncryptedData;
import io.mosip.authentication.partnerdemo.service.dto.EncryptionRequestDto;
import io.mosip.authentication.partnerdemo.service.dto.EncryptionResponseDto;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
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
	 * @throws KeyManagementException             the key management exception
	 * @throws InvalidKeyException                the invalid key exception
	 * @throws NoSuchAlgorithmException           the no such algorithm exception
	 * @throws InvalidKeySpecException            the invalid key spec exception
	 * @throws NoSuchPaddingException             the no such padding exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter
	 *                                            exception
	 * @throws IllegalBlockSizeException          the illegal block size exception
	 * @throws BadPaddingException                the bad padding exception
	 * @throws IOException                        Signals that an I/O exception has
	 *                                            occurred.
	 * @throws JSONException                      the JSON exception
	 * @throws IdAuthenticationAppException       the id authentication app
	 *                                            exception
	 * @throws IdAuthenticationBusinessException  the id authentication business
	 *                                            exception
	 * @throws JoseException 
	 * @throws UnrecoverableEntryException 
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(path = "/createAuthRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String createAuthRequest(@RequestParam(name = ID, required = true) @Nullable String id,
			@RequestParam(name = ID_TYPE, required = false) @Nullable String idType,
			@RequestParam(name = "isKyc", required = false) @Nullable boolean isKyc,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "Authtype", required = false) @Nullable String reqAuth,
			@RequestParam(name = "transactionId", required = false) @Nullable String transactionId,
			@RequestParam(name = "requestTime", required = false) @Nullable String requestTime,
			@RequestBody Map<String, Object> request)
			throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			IOException, JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException, CertificateException, UnrecoverableEntryException, JoseException {
		String authRequestTemplate = environment.getProperty(IDA_AUTH_REQUEST_TEMPLATE);
		Map<String, Object> reqValues = new HashMap<>();
		reqValues.put(OTP, false);
		reqValues.put(DEMO, false);
		reqValues.put(BIO, false);
		reqValues.put(PIN, false);
		
		if(requestTime == null) {
			requestTime = DateUtils
					.getUTCCurrentDateTimeString(environment.getProperty("datetime.pattern"));
			
		}

		idValuesMap(id, idType, isKyc, isInternal, reqValues, transactionId, requestTime);
		getAuthTypeMap(reqAuth, reqValues, request);
		applyRecursively(request, TIMESTAMP, requestTime);

		if (reqValues.get(BIO) != null && Boolean.valueOf(reqValues.get(BIO).toString())) {
			Object bioObj = request.get(BIOMETRICS);
			if (bioObj instanceof List) {
				List<Map<String, Object>> encipheredBiometrics = encipherBiometrics(isInternal,
						requestTime, (List<Map<String, Object>>) bioObj);
				request.put(BIOMETRICS, encipheredBiometrics);
			}
		}
		encryptValuesMap(request, reqValues, isInternal);

		StringWriter writer = new StringWriter();
		InputStream templateValue;
		if (request != null && request.size() > 0) {
			templateValue = templateManager
					.merge(new ByteArrayInputStream(authRequestTemplate.getBytes(StandardCharsets.UTF_8)), reqValues);

			if (templateValue != null) {
				IOUtils.copy(templateValue, writer, StandardCharsets.UTF_8);
				String res = writer.toString();
				if (reqValues.containsKey(SECONDARY_LANG_CODE)) {
					Map<String, Object> resMap = mapper.readValue(res.getBytes(StandardCharsets.UTF_8), Map.class);
					resMap.put(SECONDARY_LANG_CODE, reqValues.get(SECONDARY_LANG_CODE));
					return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resMap);
				}
				return res;
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

	@PostMapping(path = "/encipherBiometricData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> encipherBiometrics(
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "timestamp", required = false) @Nullable String timestampArg,
			@RequestBody List<Map<String, Object>> biometrics)
			throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			IOException, JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException, CertificateException, UnrecoverableEntryException, JoseException {
		String previousHash = digest(getHash(""));

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

				SplittedEncryptedData encryptedBiometrics = encrypt.encryptBiometrics(bioValue, timestamp, isInternal);
				dataMap.put(BIO_VALUE, encryptedBiometrics.getEncryptedData());
				bioMap.put(SESSION_KEY, encryptedBiometrics.getEncryptedSessionKey());

				Object digitalId = dataMap.get(DIGITAL_ID);
				if (digitalId instanceof Map) {
					Map<String, Object> digitalIdMap = (Map<String, Object>) digitalId;
					String digitalIdStr = mapper.writeValueAsString(digitalIdMap);
					String signedDititalId = jWSSignAndVerifyController.sign(digitalIdStr);
					dataMap.put(DIGITAL_ID, signedDititalId);
				}

				String dataStrJson = mapper.writeValueAsString(dataMap);
				try {
					String dataStr;
					if(isInternal) {
						dataStr = new String(CryptoUtil.encodeBase64(dataStrJson.getBytes()));
					} else {
						dataStr = jWSSignAndVerifyController.sign(dataStrJson);
					}
					bioMap.put(DATA, dataStr);
				} catch (KeyStoreException | CertificateException | UnrecoverableEntryException | JoseException e) {
					e.printStackTrace();
				}

				String currentHash = digest(getHash(dataStrJson));
				String concatenatedHash = previousHash + currentHash;
				byte[] finalHash = getHash(concatenatedHash);

				String finalHashDigest = digest(finalHash);
				bioMap.put("hash", finalHashDigest);
				previousHash = finalHashDigest;
			}
		}
		return biometrics;
	}

	private String digest(byte[] hash) {
		return HMACUtils.digestAsPlainText(hash);
	}

	/**
	 * Gets the hash.
	 *
	 * @param string the string
	 * @return the hash
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private byte[] getHash(String string) throws UnsupportedEncodingException {
		return getHash(string.getBytes(UTF_8));
	}

	/**
	 * Gets the hash.
	 *
	 * @param bytes the bytes
	 * @return the hash
	 */
	private byte[] getHash(byte[] bytes) {
		return HMACUtils.generateHash(bytes);
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
					.of(authTypeMapFunction.apply("demographics", "demo"), 
							authTypeMapFunction.apply(BIOMETRICS, "bio"),
							authTypeMapFunction.apply("otp", "otp"), 
							authTypeMapFunction.apply("staticPin", "pin"))
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
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyManagementException,
			JSONException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		EncryptionRequestDto encryptionRequestDto = new EncryptionRequestDto();
		encodeBioData(identity);
		encryptionRequestDto.setIdentityRequest(identity);
		EncryptionResponseDto encryptionResponse = encrypt.encrypt(encryptionRequestDto, null, isInternal, false);
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
					Object value = CryptoUtil.encodeBase64(mapper.writeValueAsBytes(dataMap));
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
			if (map.containsKey(key)) {
				map.put(key, value);
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

	private void idValuesMap(String id, String idType, boolean isKyc, boolean isInternal, Map<String, Object> reqValues,
			String transactionId, String utcCurrentDateTimeString) {
		reqValues.put(ID, id);
		if (null != idType) {
			reqValues.put(ID_TYPE, idType);
		} else {
			reqValues.put(ID_TYPE, UIN);
		}
		if (isKyc) {
			reqValues.put(AUTH_TYPE, "kyc");
			reqValues.put(SECONDARY_LANG_CODE, environment.getProperty("mosip.secondary-language"));

		} else {
			if (isInternal) {
				reqValues.put(AUTH_TYPE, "auth.internal");
			} else {
				reqValues.put(AUTH_TYPE, "auth");
			}
		}
		reqValues.put(TIMESTAMP, utcCurrentDateTimeString);
		reqValues.put(TXN, transactionId == null ? "1234567890" : transactionId);
		reqValues.put(VER, environment.getProperty(IDA_API_VERSION));
	}

}
