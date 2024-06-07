package Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EncryptionRequestDto;
import dto.EncryptionResponseDto;
import helper.*;
import dto.CertificateChainResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class AuthUtil {

    public AuthUtil() {
        mapper = new ObjectMapper();
    }

    private ObjectMapper mapper;
    private static final String PIN = "pin";

    private static final String BIO = "bio";

    private static final String DEMO = "demo";

    private static final String OTP = "otp";
    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String TIMESTAMP = "timestamp";

    private static final String ID = "id";

    private static final String AUTH_TYPE = "authType";

    private static final String SECONDARY_LANG_CODE = "secondaryLangCode";

    private static final String TXN = "txn";

    private static final String VER = "ver";

    private static final String ENV = "env";

    private static final String DOMAIN_URI = "domainUri";

    private static final String IDA_API_VERSION = "ida.api.version";

    private static final String MOSIP_ENV = "Staging";

    private static final String MOSIP_DOMAINURI = "mosip.base.url";
    public static final String BIOMETRICS = "biometrics";

    private static final String IDA_AUTH_REQUEST_TEMPLATE = "ida.authRequest.template";

    private static final String DATE_TIME = "dateTime";

    private static final String TRANSACTION_ID = "transactionId";

    private static final String IDENTITY = "Identity";

    private static final String TEMPLATE = "Template";


    public void clearKeys(String certsDir, String moduleName, String targetEnv) throws IOException {
        KeyMgrUtil keyMgrUtil = new KeyMgrUtil();
        keyMgrUtil.deleteFile(new File(keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv).toString()));
    }

    public CertificateChainResponseDto generatePartnerKeys(
            PartnerTypes partnerType, String partnerName, boolean keyFileNameByPartnerName, String certsDir, String moduleName, String targetEnv) throws UnrecoverableEntryException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException {
        KeyMgrUtil keyMgrUtil = new KeyMgrUtil();
        return keyMgrUtil.getPartnerCertificates(partnerType, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv), partnerName,
                keyFileNameByPartnerName);
    }

    public String updatePartnerCertificate(
            PartnerTypes partnerType, String partnerName, boolean keyFileNameByPartnerName, Map<String, String> requestData, String certsDir,
            String moduleName, String targetEnv) throws CertificateException,
            IOException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        KeyMgrUtil keyMgrUtil = new KeyMgrUtil();
        String certificateData = requestData.get("certData");
        String filePrepend = partnerType.getFilePrepend();

        X509Certificate x509Cert = (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
        System.out.println("certificateType: " + partnerType.toString());
        System.out.println("filePrepend: " + filePrepend);
        boolean isUpdated = keyMgrUtil.updatePartnerCertificate(filePrepend, x509Cert, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv),
                partnerName, keyFileNameByPartnerName);
        return isUpdated ? "Update Success" : "Update Failed";
    }

    public String uploadIDACertificate(
            CertificateTypes certificateType,
            Map<String, String> requestData,
            String certsDir,
            String moduleName,
            String targetEnv)
            throws CertificateException, IOException {
        KeyMgrUtil keyMgrUtil = new KeyMgrUtil();

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

        String keysDirPath = keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv);

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

    private String digest(byte[] hash) throws NoSuchAlgorithmException {
        return DatatypeConverter.printHexBinary(hash).toUpperCase();
    }

    public byte[] getCertificateThumbprint(Certificate cert) throws CertificateEncodingException {
        return DigestUtils.sha256(cert.getEncoded());
    }

    private void idValuesMap(String id, boolean isKyc, boolean isInternal, Map<String, Object> reqValues,
                             String transactionId, String utcCurrentDateTimeString) {
        reqValues.put(ID, id);
        if (isInternal) {
            reqValues.put(AUTH_TYPE, "auth.internal");
        } else {
            if (isKyc) {
                reqValues.put(AUTH_TYPE, "kyc");
                reqValues.put(SECONDARY_LANG_CODE, PropertiesReader.readProperty("mosip.secondary-language"));
            } else {
                reqValues.put(AUTH_TYPE, "auth");
            }
        }

        reqValues.put(TIMESTAMP, utcCurrentDateTimeString);
        reqValues.put(TXN, transactionId == null ? "1234567890" : transactionId);
        reqValues.put(VER, PropertiesReader.readProperty(IDA_API_VERSION));
        reqValues.put(DOMAIN_URI, PropertiesReader.readProperty(MOSIP_DOMAINURI));
        reqValues.put(ENV, MOSIP_ENV);
    }

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
                reqAuthArr = new String[]{reqAuth};
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

    public String signRequest(
            PartnerTypes partnerType,
            String partnerName,
            boolean keyFileNameByPartnerName,
            String request,
            String certsDir,
            String moduleName,
            String targetEnv)
            throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            CertificateException, IOException, OperatorCreationException {
        KeyMgrUtil keyMgrUtil = new KeyMgrUtil();
        JWSSignAndVerifyController jWSSignAndVerifyController = new JWSSignAndVerifyController();
        return jWSSignAndVerifyController.sign(request, false,
                true, false, null, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv), partnerType, partnerName, keyFileNameByPartnerName);
    }


}
