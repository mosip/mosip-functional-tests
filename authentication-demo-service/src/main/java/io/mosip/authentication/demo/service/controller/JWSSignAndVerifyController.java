package io.mosip.authentication.demo.service.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.signature.dto.JWTSignatureRequestDto;
import io.mosip.kernel.signature.dto.JWTSignatureResponseDto;
import io.swagger.annotations.Api;
import lombok.Data;

// 
/**
 * @author Sanjay Murali The Class JWSSignAndVerifyController is used to
 *         digitally sign the request.
 */
@RestController
@Api(tags = { "JWS Signature" })
public class JWSSignAndVerifyController {

	@Autowired
	private CryptoCore cryptoCore;

	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	/** The sign refid. */
	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;

	@Autowired
	private Environment env;

	/**
	 * Sign.
	 *
	 * @param data the data
	 * @return the string
	 * @throws KeyStoreException            the key store exception
	 * @throws NoSuchAlgorithmException     the no such algorithm exception
	 * @throws CertificateException         the certificate exception
	 * @throws IOException                  Signals that an I/O exception has
	 *                                      occurred.
	 * @throws UnrecoverableEntryException  the unrecoverable entry exception
	 * @throws JoseException                the jose exception
	 * @throws InvalidKeySpecException      the invalid key spec exception
	 * @throws KeyManagementException
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping(path = "/sign", consumes = MediaType.TEXT_PLAIN, produces = MediaType.TEXT_PLAIN)
	public String sign(@RequestBody String data, boolean isPayloadRequired)
			throws KeyManagementException, NoSuchAlgorithmException, UnsupportedEncodingException {
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

		JWTSignatureRequestDto request = new JWTSignatureRequestDto();
		request.setApplicationId("IDA");
		request.setDataToSign(CryptoUtil.encodeBase64(data.getBytes("UTF-8")));
		request.setIncludeCertHash(true);
		request.setIncludeCertificate(true);
		request.setIncludePayload(isPayloadRequired);
		request.setReferenceId(signRefid);
		RequestWrapper<JWTSignatureRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setRequest(request);
		HttpEntity<RequestWrapper<JWTSignatureRequestDto>> requestEntity = new HttpEntity<>(requestWrapper);
		ResponseEntity<ResponseWrapper<JWTSignatureResponseDto>> exchange = restTemplate.exchange(
				env.getProperty("ida.internal.jwtSign.url"), HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<ResponseWrapper<JWTSignatureResponseDto>>() {
				});
		return exchange.getBody().getResponse().getJwtSignedData();
//		FileInputStream pkeyfis = new FileInputStream("lib/Keystore/PrivateKey.pem");
//		String pKey = getFileContent(pkeyfis, "UTF-8");
//		FileInputStream certfis = new FileInputStream("lib/Keystore/MosipTestCert.pem");
//		String cert =  getFileContent(certfis, "UTF-8");
//		pKey = trimBeginEnd(pKey);
//		cert = trimBeginEnd(cert);
//		CertificateFactory cf = CertificateFactory.getInstance("X.509");
//		X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
//
//		return cryptoCore.sign(data.getBytes("UTF-8"), privateKey, certificate);	 

	}

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
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if (list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}

	public static String trimBeginEnd(String pKey) {
		pKey = pKey.replaceAll("-*BEGIN([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("-*END([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("\\s", "");
		return pKey;
	}

	/**
	 * Sign.
	 *
	 * @param data the data
	 * @return the string
	 * @throws KeyStoreException           the key store exception
	 * @throws NoSuchAlgorithmException    the no such algorithm exception
	 * @throws CertificateException        the certificate exception
	 * @throws IOException                 Signals that an I/O exception has
	 *                                     occurred.
	 * @throws UnrecoverableEntryException the unrecoverable entry exception
	 * @throws JoseException               the jose exception
	 * @throws InvalidKeySpecException     the invalid key spec exception
	 */
	@PostMapping(path = "/verify", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
	public SignatureStatus verify(@RequestBody String jwsSignature) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableEntryException, JoseException, InvalidKeySpecException {
		SignatureStatus status = new SignatureStatus();
		if (cryptoCore.verifySignature(jwsSignature)) {
			status.setStatus("VALID");
			status.setPayload(getPayloadFromJwsSingature(jwsSignature));
		} else {
			status.setStatus("INVALID");
		}
		return status;
	}

	@PostMapping(path = "/getSplittedPayloadSectionFromJWS", consumes = MediaType.TEXT_PLAIN, produces = MediaType.TEXT_PLAIN)
	public String getSplittedPayloadSectionFromJwsSingature(@RequestBody String jws) {
		String[] split = jws.split("\\.");
		if (split.length >= 2) {
			return split[1];
		}
		return jws;
	}

	@PostMapping(path = "/getPayloadFromJWS", consumes = MediaType.TEXT_PLAIN, produces = MediaType.TEXT_PLAIN)
	public String getPayloadFromJwsSingature(@RequestBody String jws) {
		return new String(CryptoUtil.decodeBase64(getSplittedPayloadSectionFromJwsSingature(jws)));
	}

	/**
	 * Gets the file content.
	 *
	 * @param fis      the fis
	 * @param encoding the encoding
	 * @return the file content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getFileContent(FileInputStream fis, String encoding) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}

	/*
	 * private String dynamicCertificateAndSign(String data) throws IOException,
	 * NoSuchAlgorithmException, CertificateException, InvalidKeyException,
	 * NoSuchProviderException, SignatureException, CertificateParsingException,
	 * CertificateEncodingException, JoseException { String commonName = "sanz";
	 * String organizationalUnit = "Mindtree Hi-Tech World"; String organization =
	 * "Mindtree Limited"; String country = "india";
	 * 
	 * int keySize = 2048; int validDays = 9999; X500Name distinguishedName = new
	 * X500Name(commonName, organizationalUnit, organization, country); KeyPair kp =
	 * generateRSAKeyPair(keySize);
	 * 
	 * PrivateKey privkey = kp.getPrivate(); X509CertInfo info = new X509CertInfo();
	 * 
	 * Date since = new Date(); // Since Now Date until = new Date(since.getTime() +
	 * validDays * 86400000l); // Until x days (86400000 milliseconds in one // day)
	 * 
	 * CertificateValidity interval = new CertificateValidity(since, until);
	 * BigInteger sn = new BigInteger(64, new SecureRandom());
	 * 
	 * info.set(X509CertInfo.VALIDITY, interval);
	 * info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
	 * info.set(X509CertInfo.SUBJECT, distinguishedName);
	 * info.set(X509CertInfo.ISSUER, distinguishedName); info.set(X509CertInfo.KEY,
	 * new CertificateX509Key(kp.getPublic())); info.set(X509CertInfo.VERSION, new
	 * CertificateVersion(CertificateVersion.V3));
	 * 
	 * AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
	 * info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
	 * 
	 * // Sign the cert to identify the algorithm that is used. X509CertImpl cert =
	 * new X509CertImpl(info); cert.sign(privkey, "SHA1withRSA");
	 * 
	 * // Update the algorithm and sign again algo = (AlgorithmId)
	 * cert.get(X509CertImpl.SIG_ALG); info.set(CertificateAlgorithmId.NAME + "." +
	 * CertificateAlgorithmId.ALGORITHM, algo);
	 * 
	 * cert = new X509CertImpl(info); cert.sign(privkey, "SHA1withRSA");
	 * CertificateFactory cf = CertificateFactory.getInstance("X.509");
	 * X509Certificate certificate = (X509Certificate) cf .generateCertificate(new
	 * ByteArrayInputStream(cert.getEncoded())); List<X509Certificate> certList =
	 * new ArrayList<>(); certList.add(certificate); X509Certificate[] certArray =
	 * certList.toArray(new X509Certificate[] {});
	 * 
	 * JsonWebSignature jws = new JsonWebSignature();
	 * jws.setCertificateChainHeaderValue(certArray);
	 * jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256); byte[]
	 * emptyArray = new byte[0];
	 * jws.setPayload(HMACUtils.digestAsPlainText(HMACUtils.generateHash(emptyArray)
	 * )); jws.setKey(kp.getPrivate());
	 * 
	 * return jws.getCompactSerialization(); }
	 * 
	 * private static KeyPair generateRSAKeyPair(int keySize) throws
	 * NoSuchAlgorithmException {
	 * 
	 * KeyPairGenerator kpg;
	 * 
	 * kpg = KeyPairGenerator.getInstance("RSA"); kpg.initialize(keySize);
	 * 
	 * KeyPair kp = kpg.genKeyPair();
	 * 
	 * return kp; }
	 */

	@Data
	public static class SignatureStatus {
		private String status;
		private String payload;

	}
}
