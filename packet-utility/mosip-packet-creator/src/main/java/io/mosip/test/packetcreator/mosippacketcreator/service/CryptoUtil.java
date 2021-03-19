package io.mosip.test.packetcreator.mosippacketcreator.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tss.Tpm;
import tss.TpmFactory;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.*;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;

@Component
public class CryptoUtil {
    Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_AAD_LENGTH = 32;
    private static final String HMAC_ALGORITHM_NAME = "SHA-256";
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    private static final String KEY_PATH = System.getProperty("user.home");
    private static final String KEYS_DIR = ".mosipkeys";
    private static final String PRIVATE_KEY = "reg.key";

    private static final byte[] NULL_VECTOR = new byte[0];

    @Value("${mosip.test.regclient.encryption.appid}")
    private String encryptionAppId;

    @Value("${mosip.test.keymanager.encryptapi}")
    private String encryptApi;

    @Value("${mosip.test.tpm.available}")
    private boolean tpmAvailable;

    @Value("${mosip.test.crypto.prependthumbprint}")
    private boolean prependthumbprint;

    @Autowired
    private APIRequestUtil apiUtil;

    private SecureRandom sr = new SecureRandom();

    private static Tpm tpm;

    private static CreatePrimaryResponse signingPrimaryResponse;

    @PostConstruct
    public void initialize() {
        if(tpmAvailable) {
            tpm = TpmFactory.platformTpm();
            signingPrimaryResponse = createSigningKey();
        }
    }
    
    public byte[] encrypt(byte[] data, String referenceId) throws Exception {
        JSONObject encryptObj = new JSONObject();
        
        encryptObj.put("aad", getRandomBytes(GCM_AAD_LENGTH));
        encryptObj.put("applicationId", encryptionAppId);
        encryptObj.put("data", org.apache.commons.codec.binary.Base64.encodeBase64String(data));
        encryptObj.put("prependThumbprint", prependthumbprint);
        encryptObj.put("referenceId", referenceId);
        encryptObj.put("salt", getRandomBytes(GCM_NONCE_LENGTH));
        encryptObj.put("timeStamp",apiUtil.getUTCDateTime(null));

        JSONObject wrapper = new JSONObject();
        wrapper.put("id", "mosip.registration.sync");
        wrapper.put("requesttime", apiUtil.getUTCDateTime(LocalDateTime.now(ZoneOffset.UTC)));
        wrapper.put("version", "1.0");
        wrapper.put("request", encryptObj);

        JSONObject secretObject = apiUtil.post(encryptApi, wrapper);
        byte[] encBytes = org.apache.commons.codec.binary.Base64.decodeBase64(secretObject.getString("data"));
        return mergeEncryptedData(encBytes, org.apache.commons.codec.binary.Base64.decodeBase64(encryptObj.getString("salt")),
                org.apache.commons.codec.binary.Base64.decodeBase64(encryptObj.getString("aad")));
    }

    public boolean encryptPacket(byte[] data, String referenceId, String packetLocation) throws  Exception {
        byte[] encData = encrypt(data, referenceId);
        try(FileOutputStream fos = new FileOutputStream(packetLocation)){
            fos.write(encData);
            fos.flush();
            return true;
        }
    }

    public byte[] encrypt(byte[] data, String referenceId, LocalDateTime timestamp) throws Exception {
        JSONObject encryptObj = new JSONObject();

        encryptObj.put("aad", getRandomBytes(GCM_AAD_LENGTH));
        encryptObj.put("applicationId", encryptionAppId);
        encryptObj.put("data", org.apache.commons.codec.binary.Base64.encodeBase64String(data));
        encryptObj.put("prependThumbprint", false);
        encryptObj.put("referenceId", referenceId);
        encryptObj.put("salt", getRandomBytes(GCM_NONCE_LENGTH));
        encryptObj.put("timeStamp", apiUtil.getUTCDateTime(timestamp));

        JSONObject wrapper = new JSONObject();
        wrapper.put("id", "mosip.registration.sync");
        wrapper.put("requesttime", apiUtil.getUTCDateTime(LocalDateTime.now(ZoneOffset.UTC)));
        wrapper.put("version", "1.0");
        wrapper.put("request", encryptObj);

        JSONObject secretObject = apiUtil.post(encryptApi, wrapper);
        byte[] encBytes = org.apache.commons.codec.binary.Base64.decodeBase64(secretObject.getString("data"));
        byte[] mergeddata = mergeEncryptedData(encBytes, org.apache.commons.codec.binary.Base64.decodeBase64(encryptObj.getString("salt")),
                org.apache.commons.codec.binary.Base64.decodeBase64(encryptObj.getString("aad")));

        //test(org.apache.commons.codec.binary.Base64.encodeBase64String(mergeddata), referenceId, encryptObj);
        return mergeddata;
    }

    /*private void test(String requestBody, String refId,JSONObject encryptObj) throws Exception {
        byte[] packet = org.apache.commons.codec.binary.Base64.decodeBase64(requestBody);
        byte[] nonce = Arrays.copyOfRange(packet, 0, GCM_NONCE_LENGTH);
        byte[] aad = Arrays.copyOfRange(packet, GCM_NONCE_LENGTH, GCM_NONCE_LENGTH + GCM_AAD_LENGTH);
        byte[] encryptedData = Arrays.copyOfRange(packet, GCM_NONCE_LENGTH + GCM_AAD_LENGTH, packet.length);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("applicationId", "REGISTRATION");
        jsonObject.put("referenceId", refId);
        jsonObject.put("aad", org.apache.commons.codec.binary.Base64.encodeBase64String(aad));
        jsonObject.put("salt", org.apache.commons.codec.binary.Base64.encodeBase64String(nonce));
        jsonObject.put("data", org.apache.commons.codec.binary.Base64.encodeBase64String(encryptedData));
        jsonObject.put("timeStamp", encryptObj.get("timeStamp"));

        JSONObject wrapper = new JSONObject();
        wrapper.put("id", "mosip.cryptomanager.decrypt");
        wrapper.put("version", "1.0");
        wrapper.put("requesttime",  apiUtil.getUTCDateTime(null));
        wrapper.put("request", jsonObject);

        JSONObject secretObject = apiUtil.post("https://qa2.mosip.net/v1/keymanager/decrypt", wrapper);
        logger.info("decrypt respose >>>>>>>>>>>>>>>> {}", secretObject);

        String plaindata = new String(Base64.getDecoder().decode(secretObject.getString("data")));
        logger.info("decrypt plaindata >>>>>>>>>>>>>>>> {}", plaindata);
    }*/

    public String getHash(byte[] data) throws Exception{
        try{
            MessageDigest messageDigest = MessageDigest.getInstance(HMAC_ALGORITHM_NAME);
            messageDigest.update(data);
            return Base64.getUrlEncoder().encodeToString(messageDigest.digest());
        } catch(Exception ex){
            logger.error("Cryptoutil getHash err ", ex);
            throw new Exception("Invalid crypto util");
        }
    }

    public String getHexEncodedHash(byte[] data) throws Exception{
        try{
            MessageDigest messageDigest = MessageDigest.getInstance(HMAC_ALGORITHM_NAME);
            messageDigest.update(data);
            return DatatypeConverter.printHexBinary(messageDigest.digest()).toUpperCase();
        } catch(Exception ex){
            logger.error("Cryptoutil getHexEncodedHash err ", ex);
            throw new Exception("Invalid crypto util");
        }
    }

    public byte[] sign(byte[] dataToSign) throws Exception {
        try {
            if(tpmAvailable) {
                CreatePrimaryResponse signingKey = createSigningKey();
                TPMU_SIGNATURE signedData = tpm.Sign(signingKey.handle,
                        TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, dataToSign).digest, new TPMS_NULL_SIG_SCHEME(),
                        TPMT_TK_HASHCHECK.nullTicket());
                logger.info("Completed Signing data using TPM");
                return ((TPMS_SIGNATURE_RSASSA) signedData).sig;
            }

            Signature sign = Signature.getInstance(SIGN_ALGORITHM);
            sign.initSign(getMachinePrivateKey());

            try(ByteArrayInputStream in = new ByteArrayInputStream(dataToSign)) {
                byte[] buffer = new byte[2048];
                int len = 0;
                while((len = in.read(buffer)) != -1) {
                    sign.update(buffer, 0, len);
                }
                return sign.sign();
            }
        } catch (Exception ex) {
            logger.error("Failed to sign data", ex);
            throw new Exception("Failed to sign data");
        }
    }

    private PrivateKey getMachinePrivateKey() throws Exception {
        byte[] key = Files.readAllBytes(Path.of(KEY_PATH ,KEYS_DIR ,PRIVATE_KEY));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    /**
     * 
     * @param length in bytes
     * @return base64 value of the random byte
     */
    private String getRandomBytes(int length){
        byte[] rand = new byte[length];
        sr.nextBytes(rand);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(rand);
    }

    private byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
        byte[] finalEncData = new byte[encryptedData.length + GCM_AAD_LENGTH + GCM_NONCE_LENGTH];
        System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
        System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
        System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length,	encryptedData.length);
        return finalEncData;
    }

    private CreatePrimaryResponse createSigningKey() {
        logger.info("Creating the Key from Platform TPM");

        if(signingPrimaryResponse != null)
            return signingPrimaryResponse;

        TPMT_PUBLIC template = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
                new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sign,
                        TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
                new byte[0],
                new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL),
                        new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256), 2048, 65537),
                new TPM2B_PUBLIC_KEY_RSA());

        TPM_HANDLE primaryHandle = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

        TPMS_SENSITIVE_CREATE dataToBeSealedWithAuth = new TPMS_SENSITIVE_CREATE(NULL_VECTOR, NULL_VECTOR);

        logger.info("Completed creating the Signing Key from Platform TPM");

        //everytime this is called key never changes until unless either seed / template change.
        signingPrimaryResponse = tpm.CreatePrimary(primaryHandle, dataToBeSealedWithAuth, template,
                NULL_VECTOR, new TPMS_PCR_SELECTION[0]);
        return signingPrimaryResponse;
    }
    

}
