package io.mosip.testrig.auth.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.Api;
import lombok.Data;



import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

public class JWSSignAndVerifyController {

    private static final String SIGN_ALGO = "RS256";

    public String sign(String dataToSign,
                       boolean includePayload,
                       boolean includeCertificate,
                       boolean includeCertHash,
                       String certificateUrl,
                       String dirPath,
                       PartnerTypes partnerType,
                       String organizationName,
                       boolean keyFileNameByPartnerName) throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, OperatorCreationException {

        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
        JsonWebSignature jwSign = new JsonWebSignature();
        PrivateKeyEntry keyEntry = keyMgrUtil.getKeyEntry(dirPath, partnerType, organizationName,
                keyFileNameByPartnerName);
        if (Objects.isNull(keyEntry)) {
            throw new KeyStoreException("Key file not available for partner type: " + partnerType.toString());
        }

        PrivateKey privateKey = keyEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) keyEntry.getCertificate();
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
    public static String trimBeginEnd(String pKey) {
        pKey = pKey.replaceAll("-*BEGIN([^-]*)-*(\r?\n)?", "");
        pKey = pKey.replaceAll("-*END([^-]*)-*(\r?\n)?", "");
        pKey = pKey.replaceAll("\\s", "");
        return pKey;
    }
}
