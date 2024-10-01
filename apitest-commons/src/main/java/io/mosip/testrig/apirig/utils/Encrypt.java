package io.mosip.testrig.apirig.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dto.EncryptionResponseDto;
@Component
public class Encrypt {

    private static final String SSL = "SSL";
    String publicKeyURL = "${mosip.ida.publicKey-url}";
    String appID = "${application.id}";

    String keySplitter = "#KEY_SPLITTER#";
    
    @Autowired
    CryptoUtil cryptoUtil;

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

    public SplittedEncryptedData splitEncryptedData(String data) throws Exception {
        //boolean encryptedDataHasVersion =  env.getProperty("encryptedDataHasVersion", boolean.class, false);
        boolean encryptedDataHasVersion = false;
        byte[] dataBytes = io.mosip.kernel.core.util.CryptoUtil.decodeURLSafeBase64(data);
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
        return new SplittedEncryptedData(io.mosip.kernel.core.util.CryptoUtil.encodeToURLSafeBase64(sessionKey), io.mosip.kernel.core.util.CryptoUtil.encodeToURLSafeBase64(encryptedData), digestAsPlainText(thumbPrint));
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
