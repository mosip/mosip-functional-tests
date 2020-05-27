package io.mosip.authentication.partnerdemo.service.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.partnerdemo.service.controller.Encrypt.SplittedEncryptedData;
import io.mosip.authentication.partnerdemo.service.dto.CryptomanagerRequestDto;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.swagger.annotations.Api;;

/**
 * The Class Decrypt is used to decrypt the KYC Response.
 *  @author Arun Bose S
 * @author Sanjay Murali
 */
@RestController
@Api(tags = { "Decrypt" })
public class Decrypt {

	@Autowired
	private Environment env;
	
	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;
	
	/** The app ID. */
	@Value("${application.id}")
	private String appID;
	
	/** The app ID. */
	@Value("${" + IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID + "}")
	private String partnerId;
	
	/** The encrypt URL. */
	@Value("${mosip.kernel.decrypt-url}")
	private String decryptURL;
	
	/** The key splitter. */
	@Value("${" +IdAuthConfigKeyConstants.KEY_SPLITTER+ "}")
	private String keySplitter;
	
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(Decrypt.class);

	/**
	 * Decrypt.
	 *
	 * @param data the data
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyManagementException the key management exception
	 */
	@PostMapping(path = "/authRequest/decrypt", produces = MediaType.APPLICATION_JSON_VALUE) 
	public String decrypt(@RequestBody String data, 
			@RequestParam(name="refId",required=false) @Nullable String refId,
			@RequestParam(name="isInternal",required=false) @Nullable boolean isInternal,
			@RequestParam(name="isBiometrics",required=false) @Nullable boolean isBiometrics,
			@RequestParam(name="salt",required=false) @Nullable String salt,
			@RequestParam(name="aad",required=false) @Nullable String aad)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, KeyManagementException {
		if (refId == null) {
			refId = getRefId(isInternal, isBiometrics);
		}
		return kernelDecrypt(data, refId, salt, aad);
	}
	
	@PostMapping(path = "/authRequest/decryptSplittedData", produces = MediaType.APPLICATION_JSON_VALUE) 
	public String decryptSplittedData(@RequestBody SplittedEncryptedData splittedData,
			@RequestParam(name="refId",required=false) @Nullable String refId,
			@RequestParam(name="isInternal",required=false) @Nullable boolean isInternal,
			@RequestParam(name="isInternal",required=false) @Nullable boolean isBiometrics,
			@RequestParam(name="salt",required=false) @Nullable String salt,
			@RequestParam(name="aad",required=false) @Nullable String aad)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, KeyManagementException {
		String data = combine(splittedData.getEncryptedData(), splittedData.getEncryptedSessionKey());
		if (refId == null) {
			refId = getRefId(isInternal, isBiometrics);
		}
		return kernelDecrypt(data, refId, salt, aad);
	}
	
	private String getRefId(boolean isInternal, boolean isBiometrics) {
		String refId;
		if(isBiometrics) {
			if (isInternal) {
				refId = env.getProperty(IdAuthConfigKeyConstants.INTERNAL_BIO_REFERENCE_ID);
			} else {
				refId = env.getProperty(IdAuthConfigKeyConstants.PARTNER_BIO_REFERENCE_ID);
			}
		} else {
			if (isInternal) {
				refId = env.getProperty(IdAuthConfigKeyConstants.INTERNAL_REFERENCE_ID);
			} else {
				refId = env.getProperty(IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID);
			}
		}
		return refId;
	}

	private String combine(String request, String requestSessionKey) {
		byte[] encryptedRequest = CryptoUtil.decodeBase64(request);
		byte[] encryptedSessionKey = CryptoUtil.decodeBase64(requestSessionKey);
		return CryptoUtil.encodeBase64(
				CryptoUtil.combineByteArray(encryptedRequest, encryptedSessionKey, keySplitter));
	}

	/**
	 * This method is used to call the kernel decrypt api for decryption.
	 *
	 * @param data the data
	 * @param salt 
	 * @param aad 
	 * @return the string
	 * @throws KeyManagementException the key management exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String kernelDecrypt(String data, String refId, String salt, String aad)
			throws KeyManagementException, NoSuchAlgorithmException {
		Encrypt.turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateAuthToken();
				if(authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));

		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
		cryptomanagerRequestDto.setApplicationId(appID);
		cryptomanagerRequestDto.setReferenceId(refId);
		cryptomanagerRequestDto.setData(data);
		cryptomanagerRequestDto.setAad(aad);
		cryptomanagerRequestDto.setSalt(salt);
		cryptomanagerRequestDto.setTimeStamp(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		
		HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(createRequest(cryptomanagerRequestDto));
		ResponseEntity<Map> response = restTemplate.exchange(decryptURL, HttpMethod.POST, httpEntity, Map.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			String responseData = (String) ((Map<String, Object>) response.getBody().get("response")).get("data");
			return new String (CryptoUtil.decodeBase64(responseData), StandardCharsets.UTF_8);
		}
		return null;
	}
	
	/**
	 * Generate auth token.
	 *
	 * @return the string
	 */
	public String generateAuthToken() {
		ObjectNode requestBody = objMapper.createObjectNode();
		requestBody.put("clientId", env.getProperty("auth-token-generator.rest.clientId"));
		requestBody.put("secretKey", env.getProperty("auth-token-generator.rest.secretKey"));
		requestBody.put("appId", env.getProperty("auth-token-generator.rest.appId"));
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient.create(env.getProperty("auth-token-generator.rest.uri")).post()
				.syncBody(request)
				.exchange().block();
		logger.info("sessionID", "IDA", "DECRYPT", "AuthResponse :" +  response.toEntity(String.class).block().getBody());
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if(list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}
	
	/**
	 * Creates the request.
	 *
	 * @param <T> the generic type
	 * @param t the t
	 * @return the request wrapper
	 */
	public static <T> RequestWrapper<T> createRequest(T t){
    	RequestWrapper<T> request = new RequestWrapper<>();
    	request.setRequest(t);
    	request.setId("ida");
    	request.setRequesttime(DateUtils.getUTCCurrentDateTime());
    	return request;
    }
	

}
