package io.mosip.testrig.apirig.utils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.testrig.apirig.dto.CertificateChainResponseDto;

@Component
public class KeyMgrUtility {
	
	
@Autowired
    private CryptoCoreUtil cryptoCoreUtil;

    private static final String DOMAIN_URL = "mosip.base.url";
    private static final String CA_P12_FILE_NAME = "-ca.p12";
    private int rpPartnerCertExpiryYears = 5;

    private int ftmCertificateExpiryYears = 1;

    private int deviceCertificateExpiryMonths = 6;
    private static final String KEY_ALIAS = "keyalias";

    private static final String INTER_P12_FILE_NAME = "-inter.p12";
    private static final String PARTNER_P12_FILE_NAME = "-partner.p12";
    private static final String KEY_STORE = "PKCS12";

    private static final String RSA_ALGO = "RSA";
    private static final int RSA_KEY_SIZE = 2048;

    private static final String SIGN_ALGO = "SHA256withRSA";
    private static final String CHIP_SPECIFIC_KEY = "-csk";

    private static final String DEVICE_SPECIFIC_KEY = "-dsk";

    private static final char[] TEMP_P12_PWD = "qwerty@123".toCharArray();
    private static final String CERTIFICATE_TYPE = "X.509";

    public boolean deleteFile(File file) throws IOException {
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (File f : files) {
                    deleteFile(f);
                }
            }
            return Files.deleteIfExists(file.toPath());
        }
        return false;
    }

    public String getKeysDirPath(String certsDir, String moduleName, String targetEnv) {
        //String domain = environment.getProperty(DOMAIN_URL, "localhost").replace("https://", "").replace("http://", "").replace("/", "");
        //String domain = "api-internal.camdgc-dev.mosip.net";
        String certsTargetDir = System.getProperty("java.io.tmpdir") + File.separator + System.getProperty("parent.certs.folder.name", "AUTHCERTS");

        if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {
            certsTargetDir = ConfigManager.getauthCertsPath();
        }

        String certsModuleName = "IDA";


        if (certsDir != null && certsDir.length() != 0) {
            certsTargetDir = certsDir;
        }

        if (moduleName != null && moduleName.length() != 0) {
            certsModuleName = moduleName;
        }
        return certsTargetDir + File.separator + certsModuleName + "-IDA-" + targetEnv;

    }

    public CertificateChainResponseDto getPartnerCertificates(PartnerTypes partnerType, String dirPath, String organization, boolean keyFileNameByPartnerName) throws
            NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException, OperatorCreationException {
        String filePrepend = keyFileNameByPartnerName ? partnerType.getFilePrepend() + '-' + organization : partnerType.getFilePrepend();
        String caFilePath = dirPath + '/' + filePrepend + CA_P12_FILE_NAME;
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime dateTimeExp = dateTime.plusYears(rpPartnerCertExpiryYears);
        KeyStore.PrivateKeyEntry caPrivKeyEntry = getPrivateKeyEntry(caFilePath);
        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
        if (Objects.isNull(caPrivKeyEntry)) {
            caPrivKeyEntry = generateKeys(null, "CA-" + filePrepend, "CA-" + filePrepend, caFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String caCertificate = getCertificate(caPrivKeyEntry);

        String interFilePath = dirPath + '/' + filePrepend + INTER_P12_FILE_NAME;
        KeyStore.PrivateKeyEntry interPrivKeyEntry = getPrivateKeyEntry(interFilePath);
        if (Objects.isNull(interPrivKeyEntry)) {
            interPrivKeyEntry = generateKeys(caPrivKeyEntry.getPrivateKey(), "CA-" + filePrepend, "INTER-" + filePrepend, interFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String interCertificate = getCertificate(interPrivKeyEntry);

        String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
        KeyStore.PrivateKeyEntry partnerPrivKeyEntry = getPrivateKeyEntry(partnerFilePath);
        if (Objects.isNull(partnerPrivKeyEntry)) {
            if (filePrepend.equalsIgnoreCase(PartnerTypes.EKYC.name())) {
                keyUsage = new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.encipherOnly | KeyUsage.decipherOnly);
            }
            partnerPrivKeyEntry = generateKeys(interPrivKeyEntry.getPrivateKey(), "INTER-" + filePrepend, "PARTNER-" + filePrepend,
                    partnerFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }

        if (partnerType.equals(PartnerTypes.DEVICE) || partnerType.equals(PartnerTypes.FTM)) {
            getKeyEntry(dirPath, partnerType, organization, keyFileNameByPartnerName);
        }


        String partnerCertificate = getCertificate(partnerPrivKeyEntry);
        CertificateChainResponseDto responseDto = new CertificateChainResponseDto();
        responseDto.setCaCertificate(caCertificate);
        responseDto.setInterCertificate(interCertificate);
        responseDto.setPartnerCertificate(partnerCertificate);
        return responseDto;
    }

    public KeyStore.PrivateKeyEntry getPrivateKeyEntry(String filePath) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, IOException, CertificateException {
        return getPrivateKeyEntry(filePath, getP12Pass(), getKeyAlias());
    }

    public KeyStore.PrivateKeyEntry getPrivateKeyEntry(String filePath, char[] p12Pass, String keyAlias) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, IOException, CertificateException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            try (InputStream p12FileStream = new FileInputStream(filePath);) {
                keyStore.load(p12FileStream, p12Pass);
                return (KeyStore.PrivateKeyEntry) keyStore.getEntry(getKeyAlias(keyAlias), new KeyStore.PasswordProtection(getP12Pass(p12Pass)));
            }
        }
        return null;
    }

    private char[] getP12Pass(char[] p12Pass) {
        return p12Pass == null ? getP12Pass() : p12Pass;
    }

    private char[] getP12Pass() {
        //String pass = environment.getProperty("p12.password");
        //return  pass == null ? TEMP_P12_PWD : pass.toCharArray();
        return TEMP_P12_PWD;
    }

    private String getKeyAlias(String keyAlias) {
        return keyAlias == null ? getKeyAlias() : keyAlias;
    }

    private String getKeyAlias() {
        //return environment.getProperty("p12.key.alias", KEY_ALIAS);
        return KEY_ALIAS;
    }

    private KeyStore.PrivateKeyEntry generateKeys(PrivateKey signKey, String signCertType, String certType, String p12FilePath, KeyUsage keyUsage,
                                                  LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGO);
        SecureRandom random = new SecureRandom();
        generator.initialize(RSA_KEY_SIZE, random);
        KeyPair keyPair = generator.generateKeyPair();
        X509Certificate signCert = null;
        if (Objects.isNull(signKey)) {
            signCert = generateX509Certificate(keyPair.getPrivate(), keyPair.getPublic(), signCertType, certType, keyUsage, dateTime, dateTimeExp, organization);
        } else {
            signCert = generateX509Certificate(signKey, keyPair.getPublic(), signCertType, certType, keyUsage, dateTime, dateTimeExp, organization);
        }
        X509Certificate[] chain = new X509Certificate[]{signCert};
        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), chain);

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
        keyStore.load(null, getP12Pass());
        keyStore.setEntry(getKeyAlias(), privateKeyEntry, new KeyStore.PasswordProtection(getP12Pass()));
        Path parentPath = Paths.get(p12FilePath).getParent();
        if (parentPath != null && !Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }
        OutputStream outputStream = new FileOutputStream(p12FilePath);
        keyStore.store(outputStream, getP12Pass());
        outputStream.flush();
        outputStream.close();
        return new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), chain);
    }

    private X509Certificate generateX509Certificate(PrivateKey signPrivateKey, PublicKey publicKey, String signCertType,
                                                    String certType, KeyUsage keyUsage, LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException {
        X500Name certIssuer = getCertificateAttributes(signCertType, organization);
        X500Name certSubject = getCertificateAttributes(certType, organization);
        Date notBefore = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date notAfter = Date.from(dateTimeExp.atZone(ZoneId.systemDefault()).toInstant());

        BigInteger certSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        ContentSigner certContentSigner = new JcaContentSignerBuilder(SIGN_ALGO).build(signPrivateKey);
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(certIssuer, certSerialNum, notBefore,
                notAfter, certSubject, publicKey);
        JcaX509ExtensionUtils certExtUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, certExtUtils.createSubjectKeyIdentifier(publicKey));
        certBuilder.addExtension(Extension.keyUsage, true, keyUsage);
        X509CertificateHolder certHolder = certBuilder.build(certContentSigner);
        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }

    private static X500Name getCertificateAttributes(String cn, String organization) {

        X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
        builder.addRDN(BCStyle.C, "IN");
        builder.addRDN(BCStyle.ST, "KA");
        builder.addRDN(BCStyle.O, organization);
        builder.addRDN(BCStyle.OU, "IDA-TEST-ORG-UNIT");
        builder.addRDN(BCStyle.CN, cn);
        return builder.build();
    }

    private String getCertificate(KeyStore.PrivateKeyEntry keyEntry) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(keyEntry.getCertificate());
        pemWriter.flush();
        return stringWriter.toString();
    }

    public KeyStore.PrivateKeyEntry getKeyEntry(String dirPath, PartnerTypes partnerType, String organization, boolean keyFileNameByPartnerName) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, OperatorCreationException {

        if (partnerType == PartnerTypes.EKYC) {
            String filePrepend = keyFileNameByPartnerName ? PartnerTypes.EKYC.getFilePrepend() + '-' + organization
                    : PartnerTypes.EKYC.getFilePrepend();
            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(partnerFilePath);
            if (privateKeyEntry == null) {
                filePrepend = keyFileNameByPartnerName ? PartnerTypes.RELYING_PARTY.getFilePrepend() + '-' + organization
                        : PartnerTypes.RELYING_PARTY.getFilePrepend();
                partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
                privateKeyEntry = getPrivateKeyEntry(partnerFilePath);
            }
            return privateKeyEntry;
        }


        if (partnerType == PartnerTypes.RELYING_PARTY) {
            String filePrepend = keyFileNameByPartnerName ? PartnerTypes.RELYING_PARTY.getFilePrepend() + '-' + organization
                    : PartnerTypes.RELYING_PARTY.getFilePrepend();
            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            return getPrivateKeyEntry(partnerFilePath);
        }

        if (partnerType == PartnerTypes.MISP) {
            String filePrepend = keyFileNameByPartnerName ? PartnerTypes.MISP.getFilePrepend() + '-' + organization
                    : PartnerTypes.MISP.getFilePrepend();
            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            return getPrivateKeyEntry(partnerFilePath);
        }

        String filePrepend = partnerType.getFilePrepend();
        if (partnerType == PartnerTypes.FTM) {
            String csPartnerFilePath = dirPath + '/' + filePrepend + CHIP_SPECIFIC_KEY + PARTNER_P12_FILE_NAME;
            KeyStore.PrivateKeyEntry csKeyEntry = getPrivateKeyEntry(csPartnerFilePath);
            if (Objects.nonNull(csKeyEntry))
                return csKeyEntry;

            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            KeyStore.PrivateKeyEntry pKeyEntry = getPrivateKeyEntry(partnerFilePath);
            LocalDateTime dateTime = LocalDateTime.now();
            LocalDateTime dateTimeExp = dateTime.plusYears(ftmCertificateExpiryYears);
            KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);

            X500Principal signerPrincipal = ((X509Certificate) pKeyEntry.getCertificate()).getSubjectX500Principal();
            X500Name x500Name = new X500Name(signerPrincipal.getName());
            RDN[] rdns = x500Name.getRDNs(BCStyle.CN);
            String cName = IETFUtils.valueToString((rdns[0]).getFirst().getValue());
            System.out.println("signerPrincipal:: " + signerPrincipal.toString());
            System.out.println("cName:: " + cName);

            RDN[] o = x500Name.getRDNs(BCStyle.O);
            String oName = IETFUtils.valueToString((o[0]).getFirst().getValue());
            System.out.println("oName:: " + oName);
            return generateKeys(pKeyEntry.getPrivateKey(), cName, "CSK-" + partnerType,
                    csPartnerFilePath, keyUsage, dateTime, dateTimeExp, oName);

        }

        if (partnerType == PartnerTypes.DEVICE) {
            String dsPartnerFilePath = dirPath + '/' + filePrepend + DEVICE_SPECIFIC_KEY + PARTNER_P12_FILE_NAME;
            KeyStore.PrivateKeyEntry dsKeyEntry = getPrivateKeyEntry(dsPartnerFilePath);
            if (Objects.nonNull(dsKeyEntry) && isCertificateValid((X509Certificate) dsKeyEntry.getCertificate()))
                return dsKeyEntry;

            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            KeyStore.PrivateKeyEntry pKeyEntry = getPrivateKeyEntry(partnerFilePath);
            LocalDateTime dateTime = LocalDateTime.now();
            LocalDateTime dateTimeExp = dateTime.plusMonths(deviceCertificateExpiryMonths);
            KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);

            X500Principal signerPrincipal = ((X509Certificate) pKeyEntry.getCertificate()).getSubjectX500Principal();
            X500Name x500Name = new X500Name(signerPrincipal.getName());
            RDN[] rdns = x500Name.getRDNs(BCStyle.CN);
            String cName = IETFUtils.valueToString((rdns[0]).getFirst().getValue());
            System.out.println("signerPrincipal:: " + signerPrincipal.toString());
            System.out.println("cName:: " + cName);

            RDN[] o = x500Name.getRDNs(BCStyle.O);
            String oName = IETFUtils.valueToString((o[0]).getFirst().getValue());
            System.out.println("oName:: " + oName);
            return generateKeys(pKeyEntry.getPrivateKey(), cName, "DSK-" + partnerType,
                    dsPartnerFilePath, keyUsage, dateTime, dateTimeExp, oName);
        }
        return null;
    }

    private boolean isCertificateValid(X509Certificate cert) {
        try {
            cert.checkValidity();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public Certificate convertToCertificate(String certData) throws IOException, CertificateException {
        StringReader strReader = new StringReader(certData);
        PemReader pemReader = new PemReader(strReader);
        PemObject pemObject = pemReader.readPemObject();

        byte[] certBytes = pemObject.getContent();
        CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
        return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    public boolean updatePartnerCertificate(String partnerType, X509Certificate updateCert, String dirPath, String organization, boolean keyFileNameByPartnerName) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
        String filePrepend = keyFileNameByPartnerName ? partnerType + '-' + organization : partnerType;
        String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;

        KeyStore.PrivateKeyEntry partnerPrivKeyEntry = getPrivateKeyEntry(partnerFilePath);
        if (Objects.nonNull(partnerPrivKeyEntry)) {
            X509Certificate fileCert = (X509Certificate) partnerPrivKeyEntry.getCertificate();
            if (!Arrays.equals(fileCert.getPublicKey().getEncoded(), updateCert.getPublicKey().getEncoded())) {
                throw new CertificateException("Public Key not matched. Please upload correct certificate.");
            }
            X509Certificate[] chain = new X509Certificate[]{updateCert};
            KeyStore.PrivateKeyEntry newPrivateKeyEntry = new KeyStore.PrivateKeyEntry(partnerPrivKeyEntry.getPrivateKey(), chain);

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null, getP12Pass());
            keyStore.setEntry(getKeyAlias(), newPrivateKeyEntry, new KeyStore.PasswordProtection(getP12Pass()));

            OutputStream outputStream = new FileOutputStream(partnerFilePath);
            keyStore.store(outputStream, getP12Pass());
            outputStream.flush();
            outputStream.close();
            return true;
        }
        return false;
    }

    private X509Certificate getX509Certificate(String partnerCertificate) throws CertificateException {
        String certificate = trimBeginEnd(partnerCertificate);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x509cert = (X509Certificate) cf
                .generateCertificate(new ByteArrayInputStream(java.util.Base64.getDecoder().decode(certificate)));
        return x509cert;
    }

    private PublicKey getPublicKey(String publicKeyPEM) throws CertificateException, NoSuchAlgorithmException, InvalidKeySpecException {
        String trimmedPublicKeyPEM = trimBeginEnd(publicKeyPEM);
        byte[] encoded = java.util.Base64.getDecoder().decode(trimmedPublicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static String trimBeginEnd(String pKey) {
        pKey = pKey.replaceAll("-{0,30}BEGIN([^-]{0,30})-{0,30}(\r?\n)?", "");
        pKey = pKey.replaceAll("-{0,30}END([^-]{0,30})-{0,30}(\r?\n)?", "");
        pKey = pKey.replaceAll("\\s", "");
        return pKey;
    }

    public String readStringFile(String dirPath, String fileName) throws IOException {
        return Files.readString(new File(dirPath + File.separator + fileName).toPath());
    }

    public String asymmetricEncryptionPublicKey(byte[] dataToEncrypt, PublicKey publicKey) throws GeneralSecurityException {
        byte[] encryptedData = cryptoCoreUtil.asymmetricEncrypt(publicKey, dataToEncrypt);
        System.out.println("AssymetricEncrypted data -- Start" + encryptedData + " End--AssymetricEncrypted data");
        return CryptoUtil.encodeBase64(encryptedData);
    }
}
