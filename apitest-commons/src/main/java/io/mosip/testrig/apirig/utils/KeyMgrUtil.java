package io.mosip.testrig.apirig.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
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
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.testrig.apirig.dto.CertificateChainResponseDto;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.Encrypt.SplittedEncryptedData;



/**
 * The Class KeyMgrUtil.
 * @author Md. Taheer
 * @author Loganathan Sekar
 */
public class KeyMgrUtil {
	
	private static 	SplittedEncryptedData 	splittedEncryptedData = new 	SplittedEncryptedData();
	private static final int TAG_LENGTH = 128;
	
	private static final Logger logger = Logger.getLogger(KeyMgrUtil.class);
    /** The Constant DOMAIN_URL. */
//    private static final String DOMAIN_URL = "mosip.base.url";

	/** The Constant CERTIFICATE_TYPE. */
	private static final String CERTIFICATE_TYPE = "X.509";

    /** The Constant CA_P12_FILE_NAME. */
    private static final String CA_P12_FILE_NAME = "-ca.p12";
	
	/** The Constant INTER_P12_FILE_NAME. */
	private static final String INTER_P12_FILE_NAME = "-inter.p12";
	
	/** The Constant PARTNER_P12_FILE_NAME. */
	private static final String PARTNER_P12_FILE_NAME = "-partner.p12";
	
	/** The Constant CA_CER_FILE_NAME. */
	private static final String CA_CER_FILE_NAME = "-ca.cer";
	
	/** The Constant INTER_CER_FILE_NAME. */
	private static final String INTER_CER_FILE_NAME = "-inter.cer";
	
	/** The Constant PARTNER_CER_FILE_NAME. */
	private static final String PARTNER_CER_FILE_NAME = "-partner.cer";

    /** The Constant TEMP_P12_PWD. */
    private static final char[] TEMP_P12_PWD = "qwerty@123".toCharArray();

    /** The Constant KEY_ALIAS. */
    private static final String KEY_ALIAS = "keyalias";
    
    /** The Constant KEY_STORE. */
    private static final String KEY_STORE = "PKCS12";
    
    /** The Constant RSA_ALGO. */
    private static final String RSA_ALGO = "RSA";
    
    /** The Constant RSA_KEY_SIZE. */
    private static final int RSA_KEY_SIZE = 2048;
    
    /** The Constant SIGN_ALGO. */
    private static final String SIGN_ALGO = "SHA256withRSA";
    

    /**
     * Convert to certificate.
     *
     * @param certData the cert data
     * @return the certificate
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CertificateException the certificate exception
     */
	public Certificate convertToCertificate(String certData) throws IOException, CertificateException {
		ByteArrayInputStream bIS = null;
		try {
			StringReader strReader = new StringReader(certData);
			PemReader pemReader = new PemReader(strReader);
			PemObject pemObject = pemReader.readPemObject();

			byte[] certBytes = pemObject.getContent();
			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
			bIS = new ByteArrayInputStream(certBytes);
			return certFactory.generateCertificate(bIS);
		} finally {
			AdminTestUtil.closeByteArrayInputStream(bIS);
		}
	}

    /**
     * Gets the partner certificates.
     *
     * @param partnerId the partner id
     * @param organization the organization
     * @param dirPath the dir path
     * @return the partner certificates
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws UnrecoverableEntryException the unrecoverable entry exception
     * @throws KeyStoreException the key store exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CertificateException the certificate exception
     * @throws OperatorCreationException the operator creation exception
     */
    public void getPartnerCertificates(String partnerId, String organization,String dirPath) throws 
        NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException, OperatorCreationException {

        String caFilePath = dirPath + '/' + partnerId + CA_P12_FILE_NAME;
        LocalDateTime dateTime = LocalDateTime.now(); 
        LocalDateTime dateTimeExp = dateTime.plusYears(1);
        PrivateKeyEntry caPrivKeyEntry = getPrivateKeyEntry(caFilePath);
        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
        if (Objects.isNull(caPrivKeyEntry)) {
            caPrivKeyEntry = generateKeys(null, "CA-" + partnerId, "CA-" + partnerId, caFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String caCertificate = getCertificate(caPrivKeyEntry);

        String interFilePath = dirPath + '/' + partnerId + INTER_P12_FILE_NAME;
        PrivateKeyEntry interPrivKeyEntry = getPrivateKeyEntry(interFilePath);
        if (Objects.isNull(interPrivKeyEntry)) {
            interPrivKeyEntry = generateKeys(caPrivKeyEntry.getPrivateKey(), "CA-" + partnerId, "INTER-" + partnerId, interFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String interCertificate = getCertificate(interPrivKeyEntry);

        String partnerFilePath = dirPath + '/' + partnerId + PARTNER_P12_FILE_NAME;
        PrivateKeyEntry partnerPrivKeyEntry = getPrivateKeyEntry(partnerFilePath);
        if (Objects.isNull(partnerPrivKeyEntry)) {
            if (partnerId.equalsIgnoreCase("EKYC")){
                keyUsage = new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.encipherOnly | KeyUsage.decipherOnly);
            }
            partnerPrivKeyEntry = generateKeys(interPrivKeyEntry.getPrivateKey(), "INTER-" + partnerId, "PARTNER-" + partnerId, 
                        partnerFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String partnerCertificate = getCertificate(partnerPrivKeyEntry);
        CertificateChainResponseDto responseDto = new CertificateChainResponseDto();
        responseDto.setCaCertificate(caCertificate);
        responseDto.setInterCertificate(interCertificate);
        responseDto.setPartnerCertificate(partnerCertificate);
        
        String caCertFilePath = dirPath + '/' + partnerId + CA_CER_FILE_NAME;
        String interCertFilePath = dirPath + '/' + partnerId + INTER_CER_FILE_NAME;
        String partnerCertFilePath = dirPath + '/' + partnerId + PARTNER_CER_FILE_NAME;

        writeContentToFile(caCertificate, caCertFilePath);
        writeContentToFile(interCertificate, interCertFilePath);
        writeContentToFile(partnerCertificate, partnerCertFilePath);
    }

	/**
	 * Write content to file.
	 *
	 * @param caCertificate the ca certificate
	 * @param caCertFilePath the ca cert file path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeContentToFile(String caCertificate, String caCertFilePath) throws IOException {
		File file = new File(caCertFilePath);
		Files.write(file.toPath(), caCertificate.getBytes(), StandardOpenOption.CREATE_NEW);
	}

    /**
     * Gets the private key entry.
     *
     * @param filePath the file path
     * @return the private key entry
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws UnrecoverableEntryException the unrecoverable entry exception
     * @throws KeyStoreException the key store exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CertificateException the certificate exception
     */
    private PrivateKeyEntry getPrivateKeyEntry(String filePath) throws NoSuchAlgorithmException, UnrecoverableEntryException, 
    KeyStoreException, IOException, CertificateException{
        Path path = Paths.get(filePath);
        if (Files.exists(path)){
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            FileInputStream p12FileStream = null;
            
			try {
				p12FileStream = new FileInputStream(filePath);
				keyStore.load(p12FileStream, getP12Pass());
				return (PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, new PasswordProtection(getP12Pass()));
			} finally {
				AdminTestUtil.closeInputStream(p12FileStream);
			}
        }
        return null;
    }

    /**
     * Gets the certificate.
     *
     * @param keyEntry the key entry
     * @return the certificate
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String getCertificate(PrivateKeyEntry keyEntry) throws IOException{
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(keyEntry.getCertificate());
        pemWriter.flush();
        return stringWriter.toString();
    }
    
    /**
     * Generate keys.
     *
     * @param signKey the sign key
     * @param signCertType the sign cert type
     * @param certType the cert type
     * @param p12FilePath the p 12 file path
     * @param keyUsage the key usage
     * @param dateTime the date time
     * @param dateTimeExp the date time exp
     * @param organization the organization
     * @return the private key entry
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws OperatorCreationException the operator creation exception
     * @throws CertificateException the certificate exception
     * @throws KeyStoreException the key store exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private PrivateKeyEntry generateKeys(PrivateKey signKey, String signCertType, String certType, String p12FilePath, KeyUsage keyUsage, 
            LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws 
            NoSuchAlgorithmException, OperatorCreationException, CertificateException, KeyStoreException, IOException   {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGO);
        generator.initialize(RSA_KEY_SIZE, BaseTestCase.secureRandom);
        KeyPair keyPair = generator.generateKeyPair();
        X509Certificate signCert = null;
        if(Objects.isNull(signKey)) {
            signCert = generateX509Certificate(keyPair.getPrivate(), keyPair.getPublic(), signCertType, certType, keyUsage, dateTime, dateTimeExp, organization);
        } else {
            signCert = generateX509Certificate(signKey, keyPair.getPublic(), signCertType, certType, keyUsage, dateTime, dateTimeExp, organization);
        }
        X509Certificate[] chain = new X509Certificate[] {signCert};
        PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivate(), chain);

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
        keyStore.load(null, getP12Pass());
        keyStore.setEntry(KEY_ALIAS, privateKeyEntry, new PasswordProtection (getP12Pass()));
        Path parentPath = Paths.get(p12FilePath).getParent();
        if (parentPath != null && !Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }
        FileOutputStream outputStream =null;
        try {
        	outputStream = new FileOutputStream(p12FilePath);
            keyStore.store(outputStream, getP12Pass());
        }finally {
			AdminTestUtil.closeOutputStream(outputStream);
        }
        return new PrivateKeyEntry(keyPair.getPrivate(), chain);
    }

    /**
     * Generate X 509 certificate.
     *
     * @param signPrivateKey the sign private key
     * @param publicKey the public key
     * @param signCertType the sign cert type
     * @param certType the cert type
     * @param keyUsage the key usage
     * @param dateTime the date time
     * @param dateTimeExp the date time exp
     * @param organization the organization
     * @return the x 509 certificate
     * @throws OperatorCreationException the operator creation exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws CertIOException the cert IO exception
     * @throws CertificateException the certificate exception
     */
    private X509Certificate generateX509Certificate(PrivateKey signPrivateKey, PublicKey publicKey, String signCertType, 
            String certType, KeyUsage keyUsage, LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws 
            OperatorCreationException, NoSuchAlgorithmException, CertIOException, CertificateException {
        X500Name certIssuer = getCertificateAttributes(signCertType, organization);
        X500Name certSubject = getCertificateAttributes(certType, organization);
        Date notBefore = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date notAfter = Date.from(dateTimeExp.atZone(ZoneId.systemDefault()).toInstant());

        BigInteger certSerialNum = new BigInteger(Long.toString(BaseTestCase.secureRandom.nextLong()));
        
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
    
    /**
     * Gets the certificate attributes.
     *
     * @param cn the cn
     * @param organization the organization
     * @return the certificate attributes
     */
    private static X500Name getCertificateAttributes(String cn, String organization) {
		 
		X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
		builder.addRDN(BCStyle.C, "IN");
		builder.addRDN( BCStyle.ST, "KA");
		builder.addRDN(BCStyle.O, organization);
		builder.addRDN(BCStyle.OU, "IDA-TEST-ORG-UNIT");
		builder.addRDN(BCStyle.CN, cn);
		return builder.build();
	}

    /**
     * Gets the key entry.
     *
     * @param dirPath the dir path
     * @param parterId the parter id
     * @return the key entry
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws UnrecoverableEntryException the unrecoverable entry exception
     * @throws KeyStoreException the key store exception
     * @throws CertificateException the certificate exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws OperatorCreationException the operator creation exception
     */
    public PrivateKeyEntry getKeyEntry(String dirPath, String parterId) throws NoSuchAlgorithmException, UnrecoverableEntryException, 
            KeyStoreException, CertificateException, IOException, OperatorCreationException {
        String filePrepend = parterId;

        String partnerFilePath = dirPath + '/' +PartnerRegistration.appendEkycOrRp+ filePrepend + PARTNER_P12_FILE_NAME;;
        return getPrivateKeyEntry(partnerFilePath);
    }

    /**
     * Update partner certificate.
     *
     * @param partnerType the partner type
     * @param updateCert the update cert
     * @param dirPath the dir path
     * @return true, if successful
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws UnrecoverableEntryException the unrecoverable entry exception
     * @throws KeyStoreException the key store exception
     * @throws CertificateException the certificate exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean updatePartnerCertificate(String partnerType, X509Certificate updateCert, String dirPath) throws NoSuchAlgorithmException, 
            UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {

        String partnerFilePath = dirPath + '/' + partnerType + PARTNER_P12_FILE_NAME;
        PrivateKeyEntry partnerPrivKeyEntry = getPrivateKeyEntry(partnerFilePath);
        if (Objects.nonNull(partnerPrivKeyEntry)) {
            X509Certificate fileCert = (X509Certificate) partnerPrivKeyEntry.getCertificate();
            if (!Arrays.equals(fileCert.getPublicKey().getEncoded(), updateCert.getPublicKey().getEncoded())){
                throw new CertificateException("Public Key not matched. Please upload correct certificate.");
            }
            X509Certificate[] chain = new X509Certificate[] {updateCert};
            PrivateKeyEntry newPrivateKeyEntry = new PrivateKeyEntry(partnerPrivKeyEntry.getPrivateKey(), chain);

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null, getP12Pass());
            keyStore.setEntry(KEY_ALIAS, newPrivateKeyEntry, new PasswordProtection (getP12Pass()));
            
            FileOutputStream outputStream = null;
            
            try {
            	outputStream = new FileOutputStream(partnerFilePath);
                keyStore.store(outputStream, getP12Pass());
            }finally {
            	AdminTestUtil.closeOutputStream(outputStream);
            }
            return true;
        }
        return false;
    }

	/**
	 * Gets the p 12 pass.
	 *
	 * @return the p 12 pass
	 */
	private char[] getP12Pass() {
		String pass = System.getProperty("p12.password");
		return  pass == null ? TEMP_P12_PWD : pass.toCharArray();
	}
    
    /**
     * Gets the keys dir path.
     *
     * @return the keys dir path
     */
    public String getKeysDirPath() {
    	String domain = System.getProperty("env.endpoint", "localhost").replace("https://", "").replace("http://", "").replace("/", "");
		return System.getProperty("java.io.tmpdir") + "/" + "AUTHCERTS" + "/" + "IDA-" + domain;
    }

	/**
	 * Gets the certificate entry.
	 *
	 * @param dirPath the dir path
	 * @param partnerId the partner id
	 * @return the certificate entry
	 * @throws KeyStoreException the key store exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws CertificateException the certificate exception
	 */
	public X509Certificate getCertificateEntry(String dirPath, String partnerId)
			throws KeyStoreException, IOException, CertificateException {
		String partnerCertFilePath = dirPath + '/' + partnerId + PARTNER_CER_FILE_NAME;
		ByteArrayInputStream bIS = null;
		try {
			Path path = Paths.get(partnerCertFilePath);
			if (Files.exists(path)) {
				String cert = new String(Files.readAllBytes(path));
				cert = trimBeginEnd(cert);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				bIS = new ByteArrayInputStream(Base64.getDecoder().decode(cert));
				return (X509Certificate) cf
						.generateCertificate(bIS);

			}
			return null;
		} finally {
			AdminTestUtil.closeByteArrayInputStream(bIS);
		}
	}
	
	/**
	 * Trim begin end.
	 *
	 * @param pKey the key
	 * @return the string
	 */
	public static String trimBeginEnd(String pKey) {
		pKey = pKey.replaceAll("-*BEGIN([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("-*END([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("\\s", "");
		return pKey;
	}
	
	public X509Certificate getCertificate(String refId)
			throws KeyStoreException, IOException, CertificateException {
		String keysDirPath = AdminTestUtil.getKeysDirPath();
		
		String certFilePath = keysDirPath + '/' + "ida-partner.cer";
		logger.info("Certificate path is: " + certFilePath );
		ByteArrayInputStream bIS = null;
		try {
			Path path = Paths.get(certFilePath);
			if (Files.exists(path)) {
				String cert = new String(Files.readAllBytes(path));
				
				cert = trimBeginEnd(cert);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				bIS = new ByteArrayInputStream(Base64.getDecoder().decode(cert));
				return (X509Certificate) cf
						.generateCertificate(bIS);

			}
			return null;
		}
		finally {
			AdminTestUtil.closeByteArrayInputStream(bIS);
		}
	}
	public String ekycDataDecryption(JSONObject kycDataForDecryption, String partnerName) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(kycDataForDecryption.toString(), Map.class);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		GlobalMethods.reportRequest(null, map.toString());
		String identity = map.get("identity");
		PrivateKeyEntry ekycKey;
		logger.info(AdminTestUtil.getKeysDirPath() + "partnerName");
		ekycKey = getKeyEntry(AdminTestUtil.getKeysDirPath(), partnerName);
		String sessionKey = map.get("sessionKey");

		byte[] encSecKey;
		byte[] encKycData;
		if(sessionKey == null) {
			encSecKey = CryptoUtil.decodeURLSafeBase64(splittedEncryptedData.getEncryptedSessionKey());
			encKycData = CryptoUtil.decodeURLSafeBase64(splittedEncryptedData.getEncryptedData());
		} else {
			encSecKey = CryptoUtil.decodeURLSafeBase64(sessionKey);
			encKycData = CryptoUtil.decodeURLSafeBase64(identity);
		}

		byte[] decryptedSecrectKey = decryptSecretKey(ekycKey.getPrivateKey(), encSecKey);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); //NoPadding
		byte[] nonce = Arrays.copyOfRange(encKycData, encKycData.length - cipher.getBlockSize(), encKycData.length);
		byte[] encryptedKycData = Arrays.copyOf(encKycData, encKycData.length - cipher.getBlockSize());

		SecretKey secretKey =  new SecretKeySpec(decryptedSecrectKey, 0, decryptedSecrectKey.length, "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, nonce); 
		cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

		return new String(cipher.doFinal(encryptedKycData), "UTF-8");
	}

	private static byte[] decryptSecretKey(PrivateKey privKey, byte[] encKey) throws NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, java.security.InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING"); 
		OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
				PSpecified.DEFAULT);
		cipher.init(Cipher.DECRYPT_MODE, privKey, oaepParams);
		return cipher.doFinal(encKey, 0, encKey.length);
	}
	
	public  String getKeysDirPath(String moduleName) {
		String domain = System.getProperty("env.endpoint", "localhost").replace("https://", "").replace("http://", "").replace("/", "");
		return System.getProperty("java.io.tmpdir") + "/" + "AUTHCERTS" + "/" + moduleName+"-IDA-" + domain;
	}
}
