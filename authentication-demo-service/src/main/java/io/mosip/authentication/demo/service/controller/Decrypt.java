package io.mosip.authentication.demo.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_AAD_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_SALT_LAST_BYTES_NUM;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.json.JSONException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.BytesUtil;
import io.mosip.authentication.demo.service.controller.Encrypt.SplittedEncryptedData;
import io.mosip.authentication.demo.service.dto.CryptomanagerRequestDto;
import io.mosip.authentication.demo.service.helper.KeyMgrUtil;
import io.mosip.authentication.demo.service.helper.PartnerTypes;
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

	private static final int TAG_LENGTH = 128;
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
	@Value("${mosip.ida.decrypt-url}")
	private String decryptURL;
	
	/** The key splitter. */
	@Value("${" +IdAuthConfigKeyConstants.KEY_SPLITTER+ "}")
	private String keySplitter;
	
	@Autowired
	KeyMgrUtil keyMgrUtil;

	@Autowired
	Encrypt encrypt;
	
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
		byte[] data = combineToByteArray(splittedData.getEncryptedData(), splittedData.getEncryptedSessionKey());
		if (refId == null) {
			refId = getRefId(isInternal, isBiometrics);
		}
		byte[] bytesFromThumbprint = getBytesFromThumbprint(splittedData.getThumbprint());

		data = ArrayUtils.addAll(bytesFromThumbprint, data);
		return kernelDecrypt(CryptoUtil.encodeBase64(data), refId, salt, aad);
	}
	
	public static byte[] getBytesFromThumbprint(String thumbprint) {
		try {
			//First try decoding with hex
			return decodeHex(thumbprint);
		} catch (DecoderException e) {
			try {
				//Then try decoding with base64
				return CryptoUtil.decodeBase64(thumbprint);
			} catch (Exception ex) {
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ex);
			}
		}
	}
	
	public static byte[] decodeHex(String hexData) throws DecoderException{
        return Hex.decodeHex(hexData);
    }
	
	@PostMapping(path = "/authRequest/decryptAuthRequest", produces = MediaType.APPLICATION_JSON_VALUE) 
	public String decryptAuthRequest(@RequestBody Map<String, Object> authRequestMap)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, KeyManagementException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, JSONException {

		boolean isInternal = authRequestMap.get("id").equals("mosip.identity.auth.internal");
		Map<String, Object> outputAuthRequestMap = new LinkedHashMap<>(authRequestMap);
		String request = (String) outputAuthRequestMap.get("request");
		String reqSessionKey = (String) outputAuthRequestMap.get("requestSessionKey");
		String thumbprint = (String) outputAuthRequestMap.get("thumbprint");
		
		String decryptSplittedData = this.decryptSplittedData(new SplittedEncryptedData(reqSessionKey, request, thumbprint), null, isInternal, false, null, null);
		Map<String, Object> requestMap = objMapper.readValue(decryptSplittedData.getBytes("UTF-8"), Map.class);
		outputAuthRequestMap.put("request", requestMap);
		
		decryptBiometricsInRequestBlock(isInternal, requestMap);
		
		return objMapper.writeValueAsString(outputAuthRequestMap);
	}

	@PostMapping(path = "/authRequest/decryptBiometricsInRequestBlock", produces = MediaType.APPLICATION_JSON_VALUE) 
	private Map<String, Object> decryptBiometricsInRequestBlock(
			@RequestParam(name="isInternal",required=false) @Nullable boolean isInternal,
			@RequestBody Map<String, Object> requestMap) throws IOException, JsonParseException,
			JsonMappingException, KeyManagementException, NoSuchAlgorithmException, JSONException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeySpecException {
		List<Map<String, Object>> biometricsMap = (List<Map<String, Object>>) requestMap.get("biometrics");
		decryptBiometricsListOfAuthRequest(isInternal, biometricsMap);
		return requestMap;
	}

	@PostMapping(path = "/authRequest/decryptBiometricsListOfAuthRequest", produces = MediaType.APPLICATION_JSON_VALUE) 
	private List<Map<String, Object>> decryptBiometricsListOfAuthRequest(
			@RequestParam(name="isInternal",required=false) @Nullable boolean isInternal,
			@RequestBody List<Map<String, Object>> biometricsMap) throws IOException,
			JsonParseException, JsonMappingException, KeyManagementException, NoSuchAlgorithmException, JSONException,
			InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeySpecException {
		if(biometricsMap != null) {
			for(Map<String, Object> segmentMap: biometricsMap) {
				String biometricDataStr = (String) segmentMap.get("data");
				Map<String, Object> bioDataMap;
				if(isInternal) {
					bioDataMap = objMapper.readValue(CryptoUtil.decodeBase64(biometricDataStr), Map.class);
				} else {
					bioDataMap = objMapper.readValue(CryptoUtil.decodeBase64(biometricDataStr.split("\\.")[1]), Map.class);
				}
				segmentMap.put("data", bioDataMap);
				
				String digitalIdStr = (String) bioDataMap.get("digitalId");
				if(!isInternal && digitalIdStr != null) {
					Map<String, Object> bioDigitalIdMap = objMapper.readValue(CryptoUtil.decodeBase64(digitalIdStr.split("\\.")[1]), Map.class);
					bioDataMap.put("digitalId", bioDigitalIdMap);
				}
				
				String encryptedBioValue = (String) bioDataMap.get("bioValue");
				String timestamp = (String) bioDataMap.get("timestamp");
				String transactionId = (String) bioDataMap.get("transactionId");
				String bioSessionKey = (String) segmentMap.get("sessionKey");
				String thumbprint = (String) segmentMap.get("thumbprint");
				
				byte[] bytesFromThumbprint = getBytesFromThumbprint(thumbprint);

				
				byte[] data = combineToByteArray(encryptedBioValue, bioSessionKey);
				data = ArrayUtils.addAll(bytesFromThumbprint, data);

				String decryptBiometrics = this.decryptBiometrics(CryptoUtil.encodeBase64(data), timestamp, transactionId, isInternal);
				
				bioDataMap.put("bioValue", decryptBiometrics);
			}
		}
		return biometricsMap;
	}
	
	@PostMapping(path = "/decryptBiometricValue")
	public String decryptBiometrics(@RequestBody String encryptedBioValue, 
			@RequestParam(name="timestamp",required=false) @Nullable String timestamp, 
			@RequestParam(name="transactionId",required=false) @Nullable String transactionId, 
			@RequestParam(name="isInternal",required=false) @Nullable boolean isInternal)
			throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeySpecException {
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
		
		
		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
		String salt = CryptoUtil.encodeBase64(saltLastBytes);
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
		String aad = CryptoUtil.encodeBase64(aadLastBytes);

		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setApplicationId(appID);
		request.setSalt(salt);
		request.setAad(aad);
		request.setReferenceId(getRefId(isInternal, true));
		request.setData(encryptedBioValue);
		request.setTimeStamp(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		
		HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(createRequest(request));
		ResponseEntity<Map> response = restTemplate.exchange(decryptURL, HttpMethod.POST, httpEntity, Map.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			String responseData = (String) ((Map<String, Object>) response.getBody().get("response")).get("data");
			return responseData;
		}
		return null ;
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
	
	private byte[] combineToByteArray(String request, String requestSessionKey) {
		byte[] encryptedRequest = CryptoUtil.decodeBase64(request);
		byte[] encryptedSessionKey = CryptoUtil.decodeBase64(requestSessionKey);
		return CryptoUtil.combineByteArray(encryptedRequest, encryptedSessionKey, keySplitter);
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
	
	@PostMapping(path = "/decryptEkycData", produces = MediaType.TEXT_PLAIN_VALUE)
	public String decryptEkycData(
			@RequestBody Map<String, String> requestData) throws CertificateException, IOException, 
			NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, OperatorCreationException, 
			InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
				
		String identity = requestData.get("identity");
		PrivateKeyEntry ekycKey = keyMgrUtil.getKeyEntry(keyMgrUtil.getKeysDirPath(), PartnerTypes.EKYC);
		
		SplittedEncryptedData encryptedData = encrypt.splitEncryptedData(identity);
		byte[] encSecKey = CryptoUtil.decodeBase64(encryptedData.getEncryptedSessionKey());
		byte[] encKycData = CryptoUtil.decodeBase64(encryptedData.getEncryptedData());
		byte[] decSecKey = decryptSecretKey(ekycKey.getPrivateKey(), encSecKey);
	     
	    Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding"); //NoPadding
	    byte[] nonce = Arrays.copyOfRange(encKycData, encKycData.length - cipher.getBlockSize(), encKycData.length);
	    byte[] encryptedKycData = Arrays.copyOf(encKycData, encKycData.length - cipher.getBlockSize());
		
		SecretKey secretKey =  new SecretKeySpec(decSecKey, 0, decSecKey.length, "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, nonce); 
		cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

		return new String(cipher.doFinal(encryptedKycData));
	}

	private byte[] decryptSecretKey(PrivateKey privKey, byte[] encKey) throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING"); 
		OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
				PSpecified.DEFAULT);
	     cipher.init(Cipher.DECRYPT_MODE, privKey, oaepParams);
	     return cipher.doFinal(encKey, 0, encKey.length);
	}

}
