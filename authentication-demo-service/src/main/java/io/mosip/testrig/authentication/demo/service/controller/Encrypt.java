package io.mosip.testrig.authentication.demo.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_AAD_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_SALT_LAST_BYTES_NUM;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.BytesUtil;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.authentication.demo.service.dto.CryptomanagerRequestDto;
import io.mosip.testrig.authentication.demo.service.dto.EncryptionRequestDto;
import io.mosip.testrig.authentication.demo.service.dto.EncryptionResponseDto;
import io.mosip.testrig.authentication.demo.service.helper.CertificateTypes;
import io.mosip.testrig.authentication.demo.service.helper.CryptoUtility;
import io.mosip.testrig.authentication.demo.service.helper.KeyMgrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class Encrypt is used to encrypt the identity block using Kernel Api.
 *
 * @author Dinesh Karuppiah
 */

@RestController
@Api(tags = { "Encrypt" })
public class Encrypt {

	public static final int THUMBPRINT_LENGTH = 20;

	@Autowired
	private Environment env;

	/** The Constant ASYMMETRIC_ALGORITHM. */
	private static final String SSL = "SSL";

	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;

	@Autowired
	KeyMgrUtil keyMgrUtil;

	/** KeySplitter. */
	@Value("${" + IdAuthConfigKeyConstants.KEY_SPLITTER + "}")
	private String keySplitter;

	/** The encrypt URL. */
	@Value("${mosip.ida.publicKey-url}")
	private String publicKeyURL;

	/** The app ID. */
	@Value("${application.id}")
	private String appID;

	@Autowired
	private CryptoUtility cryptoUtil;

	@Value("${mosip.ida.encrypt-url}")
	private String encryptURL;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(Encrypt.class);

	/**
	 * Encrypt.
	 *
	 * @param encryptionRequestDto the encryption request dto
	 * @param isInternal           the is internal
	 * @return the encryption response dto
	 * @throws Exception
	 */
	@PostMapping(path = "/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto,
			@RequestParam(name = "refId", required = false) @Nullable String refId,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isBiometrics) throws Exception {
		if (refId == null) {
			refId = getRefId(isInternal, isBiometrics);
		}
		return kernelEncrypt(encryptionRequestDto, refId);
	}
	
	@PostMapping(path = "/asymmetricEncryptionForCertFile")
	@ApiOperation(value = "Asymmetric Encrypt data using certificate file", response = EncryptionResponseDto.class)
	public String asymmetricEncryptionForCertFile(@RequestBody String data,
			@RequestParam(name = "certFileName", required = true) String certFileName,
			@RequestParam(name = "certsDir", required = false) String certsDir,
			@RequestParam(name = "moduleName", required = false) String moduleName) throws Exception {
		return kernelAsymmetricEncryptForCertFile(data, certFileName, certsDir, moduleName);
	}
	
	@PostMapping(path = "/asymmetricEncryptionForPublicKeyPEMFile")
	@ApiOperation(value = "Asymmetric Encrypt data with public key PEM file", response = EncryptionResponseDto.class)
	public String asymmetricEncryptionForPublicKeyPEMFile(@RequestBody String data,
			@RequestParam(name = "publicKeyPEMFileName", required = true) String publicKeyPEMFileName,
			@RequestParam(name = "keysDir", required = false) String keysDir,
			@RequestParam(name = "moduleName", required = false) String moduleName) throws Exception {
		return kernelAsymmetricEncryptForPublicKeyPEMFile(data, publicKeyPEMFileName, keysDir, moduleName);
	}

	private String kernelAsymmetricEncryptForCertFile(String data, String certFileName, String certsDir, String moduleName) throws OperatorCreationException, IOException, GeneralSecurityException {
		return keyMgrUtil.asymmetricEncryptionForCert(data.getBytes(), certFileName, certsDir, moduleName);
	}
	
	private String kernelAsymmetricEncryptForPublicKeyPEMFile(String data, String publicKeyPEMFile, String certsDir, String moduleName) throws OperatorCreationException, IOException, GeneralSecurityException {
		return keyMgrUtil.asymmetricEncryptionForPemFile(data.getBytes(), publicKeyPEMFile, certsDir, moduleName);
	}

	/**
	 * this method is used to call Kernel encrypt api.
	 *
	 * @param encryptionRequestDto the encryption request dto
	 * @param isInternal           the is internal
	 * @return the encryption response dto
	 * @throws Exception
	 */
	private EncryptionResponseDto kernelEncrypt(EncryptionRequestDto encryptionRequestDto, String refId)
			throws Exception {
		String identityBlock = objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest());
		SecretKey secretKey = cryptoUtil.genSecKey();
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		byte[] encryptedIdentityBlock = cryptoUtil.symmetricEncrypt(identityBlock.getBytes(StandardCharsets.UTF_8),
				secretKey);
		encryptionResponseDto.setEncryptedIdentity(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));
		X509Certificate x509Cert = getCertificate(refId);
		PublicKey publicKey = x509Cert.getPublicKey();
		byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt((secretKey.getEncoded()), publicKey);
		encryptionResponseDto.setEncryptedSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));
		byte[] byteArr = cryptoUtil.symmetricEncrypt(Encrypt
				.digestAsPlainText(HMACUtils2.generateHash(identityBlock.getBytes(StandardCharsets.UTF_8))).getBytes(),
				secretKey);
		encryptionResponseDto.setRequestHMAC(Base64.encodeBase64URLSafeString(byteArr));
		return encryptionResponseDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(path = "/encryptBiometricValue")

	public SplittedEncryptedData encryptBiometrics(@RequestBody String bioValue,
			@RequestParam(name = "timestamp", required = false) @Nullable String timestamp,
			@RequestParam(name = "transactionId", required = false) @Nullable String transactionId,
			@RequestParam(name = "isInternal", required = false) @Nullable boolean isInternal)
			throws Exception {
		RestTemplate restTemplate = createRestTemplate();

		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
				IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
		String salt = CryptoUtil.encodeToURLSafeBase64(saltLastBytes);
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
				IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
		String aad = CryptoUtil.encodeToURLSafeBase64(aadLastBytes);

		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setApplicationId(appID);
		request.setSalt(salt);
		request.setAad(aad);
		request.setReferenceId(getRefId(isInternal, true));
		request.setData(bioValue);
		request.setTimeStamp(timestamp);

		HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(createRequest(request));
		ResponseEntity<Map> response = restTemplate.exchange(encryptURL, HttpMethod.POST, httpEntity, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			String responseData = (String) ((Map<String, Object>) response.getBody().get("response")).get("data");
			SplittedEncryptedData splitedEncryptedData = splitEncryptedData(responseData);
			return splitedEncryptedData;
		}
		return null;
	}

	public RestTemplate createRestTemplate() throws NoSuchAlgorithmException, KeyManagementException {
		Encrypt.turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateAuthToken();
				if (authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		return restTemplate;
	}

	/**
	 * Creates the request.
	 *
	 * @param <T> the generic type
	 * @param t   the t
	 * @return the request wrapper
	 */
	public static <T> RequestWrapper<T> createRequest(T t) {
		RequestWrapper<T> request = new RequestWrapper<>();
		request.setRequest(t);
		request.setId("ida");
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		return request;
	}

	@PostMapping(path = "/splitEncryptedData", produces = MediaType.APPLICATION_JSON_VALUE)
	public SplittedEncryptedData splitEncryptedData(@RequestBody String data) throws Exception {
		boolean encryptedDataHasVersion = env.getProperty("encryptedDataHasVersion", boolean.class, false);
		byte[] dataBytes = CryptoUtil.decodeURLSafeBase64(data);
		byte[][] splits = splitAtFirstOccurance(dataBytes, keySplitter.getBytes());
		byte[] thumbPrintAndSessionKey = splits[0];	
		byte[] sessionKey;
		byte[] thumbPrint;
		
		if (thumbPrintAndSessionKey.length >= 288) {
			thumbPrint = Arrays.copyOfRange(thumbPrintAndSessionKey, thumbPrintAndSessionKey.length - 288,
					thumbPrintAndSessionKey.length - 256);// Skip the 6 bytes version and take 32 bytes
			sessionKey = Arrays.copyOfRange(thumbPrintAndSessionKey, thumbPrintAndSessionKey.length - 256,
					thumbPrintAndSessionKey.length);
		}
		else {
			 throw new Exception("Invalid Encrypted Session Key");		}
		
		byte[] encryptedData = splits[1];
		return new SplittedEncryptedData(CryptoUtil.encodeToURLSafeBase64(sessionKey), CryptoUtil.encodeToURLSafeBase64(encryptedData), digestAsPlainText(thumbPrint));
	}

	@PostMapping(path = "/combineDataToEncrypt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String combineDataToEncrypt(@RequestBody SplittedEncryptedData splittedData) {
		return CryptoUtil
				.encodeToURLSafeBase64(CryptoUtil.combineByteArray(CryptoUtil.decodeURLSafeBase64(splittedData.getEncryptedData()),
						CryptoUtil.decodeURLSafeBase64(splittedData.getEncryptedSessionKey()), keySplitter));
	}

	private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
		int index = findIndex(strBytes, sepBytes);
		if (index >= 0) {
			byte[] bytes1 = new byte[index];
			byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
			System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
			System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
			return new byte[][] { bytes1, bytes2 };
		} else {
			return new byte[][] { strBytes, new byte[0] };
		}
	}

	private static int findIndex(byte arr[], byte[] subarr) {
		int len = arr.length;
		int subArrayLen = subarr.length;
		return IntStream.range(0, len).filter(currentIndex -> {
			if ((currentIndex + subArrayLen) <= len) {
				byte[] sArray = new byte[subArrayLen];
				System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
				return Arrays.equals(sArray, subarr);
			}
			return false;
		}).findFirst() // first occurence
				.orElse(-1); // No element found
	}

	/**
	 * Gets the encrypted value.
	 *
	 * @param data       the data
	 * @param isInternal the is internal
	 * @return the encrypted value
	 * @throws IOException              Signals that an I/O exception has occurred.
	 * @throws KeyManagementException   the key management exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws RestClientException      the rest client exception
	 * @throws JSONException            the JSON exception
	 * @throws CertificateException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public X509Certificate getCertificate(String refId) throws IOException, KeyManagementException,
			NoSuchAlgorithmException, RestClientException, JSONException, CertificateException {
		turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateAuthToken();
				if (authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));

		Map<String, String> uriParams = new HashMap<>();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicKeyURL)
				.queryParam("applicationId", appID).queryParam("referenceId", refId);
		ResponseEntity<Map> response = restTemplate.exchange(builder.build(uriParams), HttpMethod.GET, null, Map.class);
		String certificate = (String) ((Map<String, Object>) response.getBody().get("response")).get("certificate");

		certificate = JWSSignAndVerifyController.trimBeginEnd(certificate);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate x509cert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(java.util.Base64.getDecoder().decode(certificate)));
		return x509cert;
	}

	public String getRefId(boolean isInternal, boolean isBiometrics) {
		String refId;
		if (isBiometrics) {
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

	/**
	 * Generate auth token.
	 *
	 * @return the string
	 */
	private String generateAuthToken() {
		ObjectNode requestBody = objMapper.createObjectNode();
		requestBody.put("clientId", env.getProperty("auth-token-generator.rest.clientId"));
		requestBody.put("secretKey", env.getProperty("auth-token-generator.rest.secretKey"));
		requestBody.put("appId", env.getProperty("auth-token-generator.rest.appId"));
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient.create(env.getProperty("auth-token-generator.rest.uri")).post()
				.syncBody(request).exchange().block();
		logger.info("sessionID", "IDA", "ENCRYPT",
				"AuthResponse :" + response.toEntity(String.class).block().getBody());
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if (list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}

	/**
	 * Gets the headers.
	 *
	 * @param req the req
	 * @return the headers
	 */
	@SuppressWarnings("unused")
	private HttpEntity<CryptomanagerRequestDto> getHeaders(CryptomanagerRequestDto req) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<CryptomanagerRequestDto>(req, headers);
	}
	
	@PostMapping(path = "/prependThumbprintToDecrypt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String prependThumbprintToDecrypt(@RequestBody SplittedData splittedData) {
		return CryptoUtil.encodeToURLSafeBase64(
				ArrayUtils.addAll(CryptoUtil.decodeURLSafeBase64(splittedData.getThumbprint()), CryptoUtil.decodeURLSafeBase64(splittedData.getEncryptedData())));
	}

	/**
	 * The Constant UNQUESTIONING_TRUST_MANAGER nullifies the check for certificates
	 * for SSL Connection
	 */
	private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String arg1)
				throws CertificateException {
		}
	} };

	/**
	 * Turns off the ssl checking.
	 *
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyManagementException   the key management exception
	 */
	public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance(Encrypt.SSL);
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public static class SplittedEncryptedData {
		private String encryptedSessionKey;
		private String encryptedData;
		private String thumbprint;

		public SplittedEncryptedData() {
			super();
		}
		
		public SplittedEncryptedData(String encryptedSessionKey, String encryptedData) {
			super();
			this.encryptedData = encryptedData;
			this.encryptedSessionKey = encryptedSessionKey;
		}

		public SplittedEncryptedData(String encryptedSessionKey, String encryptedData, String thumbprint) {
			super();
			this.encryptedData = encryptedData;
			this.encryptedSessionKey = encryptedSessionKey;
			this.thumbprint = thumbprint;
		}

		public String getEncryptedData() {
			return encryptedData;
		}

		public void setEncryptedData(String encryptedData) {
			this.encryptedData = encryptedData;
		}

		public String getEncryptedSessionKey() {
			return encryptedSessionKey;
		}

		public void setEncryptedSessionKey(String encryptedSessionKey) {
			this.encryptedSessionKey = encryptedSessionKey;
		}
		
		public String getThumbprint() {
			return thumbprint;
		}
		
		public void setThumbprint(String thumbprint) {
			this.thumbprint = thumbprint;
		}
	}

	public static String digestAsPlainText(byte[] data) {
		return DatatypeConverter.printHexBinary(data).toUpperCase();
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SplittedData {
		private String thumbprint;
		private String encryptedData;
	}

	public EncryptionResponseDto encrypt(EncryptionRequestDto encryptionRequestDto, boolean isInternal, String dirPath) throws CertificateException, IOException, 
		InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			X509Certificate x509Cert = isInternal ? getCertificate(dirPath, CertificateTypes.INTERNAL.getFileName()) :
						getCertificate(dirPath, CertificateTypes.PARTNER.getFileName());

		return kernelEncrypt(encryptionRequestDto, x509Cert);
	}

	private EncryptionResponseDto kernelEncrypt(EncryptionRequestDto encryptionRequestDto, X509Certificate x509Cert) throws JsonProcessingException, NoSuchAlgorithmException, 
		InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException	 {
		String identityBlock = objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest());
		SecretKey secretKey = cryptoUtil.genSecKey();
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		byte[] encryptedIdentityBlock = cryptoUtil.symmetricEncrypt(identityBlock.getBytes(StandardCharsets.UTF_8),
				secretKey);
		encryptionResponseDto.setEncryptedIdentity(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));
		PublicKey publicKey = x509Cert.getPublicKey();
		byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt((secretKey.getEncoded()), publicKey);
		encryptionResponseDto.setEncryptedSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));
		byte[] byteArr = cryptoUtil.symmetricEncrypt(Encrypt
				.digestAsPlainText(HMACUtils2.generateHash(identityBlock.getBytes(StandardCharsets.UTF_8))).getBytes(),
				secretKey);
		encryptionResponseDto.setRequestHMAC(Base64.encodeBase64URLSafeString(byteArr));
		return encryptionResponseDto;
	}

	private X509Certificate getCertificate(String dirPath, String fileName) throws CertificateException, IOException {
		Path path = Paths.get(dirPath + "/" + fileName);
        if (!Files.exists(path)){
			throw new FileNotFoundException("Certificate File Not found in temp directory. FileName: " + fileName);
		}
		String certificateData = Files.readString(path);
		return (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
	}	

	public SplittedEncryptedData encryptBio(String bioValue, String timestamp, String transactionId, boolean isInternal, String dirPath)
			throws KeyManagementException, NoSuchAlgorithmException, IOException, JSONException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeySpecException, CertificateException {
		
		X509Certificate x509Cert = isInternal ? getCertificate(dirPath, CertificateTypes.INTERNAL.getFileName()) :
								getCertificate(dirPath, CertificateTypes.IDA_FIR.getFileName());

		byte[] xorBytes = BytesUtil.getXOR(timestamp, transactionId);
		byte[] saltLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
				IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
		byte[] aadLastBytes = BytesUtil.getLastBytes(xorBytes, env.getProperty(
				IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));

		SecretKey secretKey = cryptoUtil.genSecKey();
		byte[] encryptedBioBlock = cryptoUtil.symmetricEncrypt(CryptoUtil.decodePlainBase64(bioValue),
				secretKey, saltLastBytes, aadLastBytes);

		PublicKey publicKey = x509Cert.getPublicKey();
		byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt(secretKey.getEncoded(), publicKey);
		return new SplittedEncryptedData(CryptoUtil.encodeToURLSafeBase64(encryptedSessionKeyByte), CryptoUtil.encodeToURLSafeBase64(encryptedBioBlock));
	
	}

	public X509Certificate getCertificate(boolean isInternal, String dirPath) throws CertificateException, IOException {

		return isInternal ? getCertificate(dirPath, CertificateTypes.INTERNAL.getFileName()) :
						getCertificate(dirPath, CertificateTypes.PARTNER.getFileName());
	}

	public X509Certificate getBioCertificate(boolean isInternal, String dirPath) throws CertificateException, IOException {

		return isInternal ? getCertificate(dirPath, CertificateTypes.INTERNAL.getFileName()) :
						getCertificate(dirPath, CertificateTypes.IDA_FIR.getFileName());
	}

}
