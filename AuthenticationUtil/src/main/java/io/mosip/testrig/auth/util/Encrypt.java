package Util;

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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import dto.EncryptionRequestDto;
import dto.EncryptionResponseDto;
import helper.CryptoUtility;
import helper.PropertiesReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.json.JSONException;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

public class Encrypt {

    private static final String SSL = "SSL";
    String publicKeyURL = "${mosip.ida.publicKey-url}";
    String appID = "${application.id}";

    String keySplitter = "#KEY_SPLITTER#";

    public EncryptionResponseDto encrypt(EncryptionRequestDto encryptionRequestDto,
                                         String refId,
                                         boolean isInternal,
                                         boolean isBiometrics) throws Exception {
        if (refId == null) {
            refId = getRefId(isInternal, isBiometrics);
        }
        return kernelEncrypt(encryptionRequestDto, refId);
    }

    private EncryptionResponseDto kernelEncrypt(EncryptionRequestDto encryptionRequestDto, String refId)
            throws Exception {
        ObjectMapper objMapper = new ObjectMapper();
        CryptoUtility cryptoUtil = new CryptoUtility();

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

    private String getRefId(boolean isInternal, boolean isBiometrics) {
        String refId;
        if (isBiometrics) {
            if (isInternal) {
                refId = PropertiesReader.readProperty(IdAuthConfigKeyConstants.INTERNAL_BIO_REFERENCE_ID);
            } else {
                refId = PropertiesReader.readProperty(IdAuthConfigKeyConstants.PARTNER_BIO_REFERENCE_ID);
            }
        } else {
            if (isInternal) {
                refId = PropertiesReader.readProperty(IdAuthConfigKeyConstants.INTERNAL_REFERENCE_ID);
            } else {
                refId = PropertiesReader.readProperty(IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID);
            }
        }
        return refId;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private X509Certificate getCertificate(String refId) throws IOException, KeyManagementException,
            NoSuchAlgorithmException, JSONException, CertificateException {
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

    private static String digestAsPlainText(byte[] data) {
        return DatatypeConverter.printHexBinary(data).toUpperCase();
    }

    public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance(Encrypt.SSL);
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String arg1)
                throws CertificateException {
        }
    }};

    private String generateAuthToken() {

        ObjectMapper objMapper = new ObjectMapper();
        ObjectNode requestBody = objMapper.createObjectNode();
//		requestBody.put("clientId", env.getProperty("auth-token-generator.rest.clientId"));
//		requestBody.put("secretKey", env.getProperty("auth-token-generator.rest.secretKey"));
//		requestBody.put("appId", env.getProperty("auth-token-generator.rest.appId"));
        requestBody.put("clientId", "mosip-resident-client");
        requestBody.put("secretKey", "SnZQ6nnVwN9YWvdM");
        requestBody.put("appId", "resident");
        RequestWrapper<ObjectNode> request = new RequestWrapper<>();
        request.setRequesttime(DateUtils.getUTCCurrentDateTime());
        request.setRequest(requestBody);
        ClientResponse response = WebClient.create(PropertiesReader.readProperty("auth-token-generator.rest.uri")).post()
                .syncBody(request).exchange().block();
        List<ResponseCookie> list = response.cookies().get("Authorization");
        if (list != null && !list.isEmpty()) {
            ResponseCookie responseCookie = list.get(0);
            return responseCookie.getValue();
        }
        return "";
    }

    public SplittedEncryptedData splitEncryptedData(String data) throws Exception {
        //boolean encryptedDataHasVersion =  env.getProperty("encryptedDataHasVersion", boolean.class, false);
        boolean encryptedDataHasVersion = false;
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
        } else {
            throw new Exception("Invalid Encrypted Session Key");
        }

        byte[] encryptedData = splits[1];
        return new SplittedEncryptedData(CryptoUtil.encodeToURLSafeBase64(sessionKey), CryptoUtil.encodeToURLSafeBase64(encryptedData), digestAsPlainText(thumbPrint));
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

    private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
        int index = findIndex(strBytes, sepBytes);
        if (index >= 0) {
            byte[] bytes1 = new byte[index];
            byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
            System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
            System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
            return new byte[][]{bytes1, bytes2};
        } else {
            return new byte[][]{strBytes, new byte[0]};
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
}
