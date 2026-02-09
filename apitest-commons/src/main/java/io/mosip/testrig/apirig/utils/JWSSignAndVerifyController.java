package io.mosip.testrig.apirig.utils;

import java.io.IOException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

public class JWSSignAndVerifyController {

    private static final String SIGN_ALGO = "RS256";
    CryptoCoreUtil cryptoCoreUtil = new CryptoCoreUtil();

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

        KeyMgrUtility keyMgrUtil = new KeyMgrUtility(cryptoCoreUtil);
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
    	if (pKey == null) {
            return null;
        }

        pKey = pKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        return pKey;
    }
}
