package io.mosip.authentication.demo.service.helper;

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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.demo.service.dto.CertificateChainResponseDto;

@Component
public class KeyMgrUtil {
    
    private static final String DOMAIN_URL = "mosip.base.url";

	private static final String CERTIFICATE_TYPE = "X.509";

    private static final String CA_P12_FILE_NAME = "-ca.p12";
	private static final String INTER_P12_FILE_NAME = "-inter.p12";
	private static final String PARTNER_P12_FILE_NAME = "-partner.p12";

    private static final char[] TEMP_P12_PWD = "qwerty@123".toCharArray();

    private static final String KEY_ALIAS = "keyalias";
    private static final String KEY_STORE = "PKCS12";
    private static final String RSA_ALGO = "RSA";
    private static final int RSA_KEY_SIZE = 2048;
    private static final String SIGN_ALGO = "SHA256withRSA";
    private static final String DEVICE_SPECIFIC_KEY = "-dsk";
    private static final String CHIP_SPECIFIC_KEY = "-csk";

	@Autowired
	private Environment environment;

    public Certificate convertToCertificate(String certData) throws IOException, CertificateException {
		StringReader strReader = new StringReader(certData);
		PemReader pemReader = new PemReader(strReader);
		PemObject pemObject = pemReader.readPemObject();
		
		byte[] certBytes = pemObject.getContent();
		CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
		return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
	}

    public CertificateChainResponseDto getPartnerCertificates(PartnerTypes partnerType, String dirPath, String organization, boolean keyFileNameByPartnerName) throws 
        NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException, OperatorCreationException {
    	String filePrepend = keyFileNameByPartnerName ? partnerType.getFilePrepend() + '-' + organization : partnerType.getFilePrepend();
        String caFilePath = dirPath + '/' + filePrepend + CA_P12_FILE_NAME;
        LocalDateTime dateTime = LocalDateTime.now(); 
        LocalDateTime dateTimeExp = dateTime.plusYears(1);
        PrivateKeyEntry caPrivKeyEntry = getPrivateKeyEntry(caFilePath);
        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
        if (Objects.isNull(caPrivKeyEntry)) {
            caPrivKeyEntry = generateKeys(null, "CA-" + filePrepend, "CA-" + filePrepend, caFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String caCertificate = getCertificate(caPrivKeyEntry);

        String interFilePath = dirPath + '/' + filePrepend + INTER_P12_FILE_NAME;
        PrivateKeyEntry interPrivKeyEntry = getPrivateKeyEntry(interFilePath);
        if (Objects.isNull(interPrivKeyEntry)) {
            interPrivKeyEntry = generateKeys(caPrivKeyEntry.getPrivateKey(), "CA-" + filePrepend, "INTER-" + filePrepend, interFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        String interCertificate = getCertificate(interPrivKeyEntry);

        String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
        PrivateKeyEntry partnerPrivKeyEntry = getPrivateKeyEntry(partnerFilePath);
        if (Objects.isNull(partnerPrivKeyEntry)) {
            if (filePrepend.equalsIgnoreCase(PartnerTypes.EKYC.name())){
                keyUsage = new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.encipherOnly | KeyUsage.decipherOnly);
            }
            partnerPrivKeyEntry = generateKeys(interPrivKeyEntry.getPrivateKey(), "INTER-" + filePrepend, "PARTNER-" + filePrepend, 
                        partnerFilePath, keyUsage, dateTime, dateTimeExp, organization);
        }
        
        //Invoke getKey entry for DEVICE and FTM partner type to automatically generate DSK and CSK certificates
        if(partnerType.equals(PartnerTypes.DEVICE) || partnerType.equals(PartnerTypes.FTM)) {
        	getKeyEntry(dirPath, partnerType);
        }
        
        
        String partnerCertificate = getCertificate(partnerPrivKeyEntry);
        CertificateChainResponseDto responseDto = new CertificateChainResponseDto();
        responseDto.setCaCertificate(caCertificate);
        responseDto.setInterCertificate(interCertificate);
        responseDto.setPartnerCertificate(partnerCertificate);
        return responseDto;
    }

    private PrivateKeyEntry getPrivateKeyEntry(String filePath) throws NoSuchAlgorithmException, UnrecoverableEntryException, 
    KeyStoreException, IOException, CertificateException{
        Path path = Paths.get(filePath);
        if (Files.exists(path)){
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
	            try(InputStream p12FileStream = new FileInputStream(filePath);) {
	            keyStore.load(p12FileStream, getP12Pass());
	            return (PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, new PasswordProtection (getP12Pass()));
            }
        }
        return null;
    }
    
    private char[] getP12Pass() {
		String pass = environment.getProperty("p12.password");
		return  pass == null ? TEMP_P12_PWD : pass.toCharArray();
	}

    private String getCertificate(PrivateKeyEntry keyEntry) throws IOException{
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(keyEntry.getCertificate());
        pemWriter.flush();
        return stringWriter.toString();
    }
    
    private PrivateKeyEntry generateKeys(PrivateKey signKey, String signCertType, String certType, String p12FilePath, KeyUsage keyUsage, 
            LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws 
            NoSuchAlgorithmException, OperatorCreationException, CertificateException, KeyStoreException, IOException   {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGO);
        SecureRandom random = new SecureRandom();
        generator.initialize(RSA_KEY_SIZE, random);
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
        OutputStream outputStream = new FileOutputStream(p12FilePath);
        keyStore.store(outputStream, getP12Pass());
        outputStream.flush();
        outputStream.close();
        return new PrivateKeyEntry(keyPair.getPrivate(), chain);
    }

    private X509Certificate generateX509Certificate(PrivateKey signPrivateKey, PublicKey publicKey, String signCertType, 
            String certType, KeyUsage keyUsage, LocalDateTime dateTime, LocalDateTime dateTimeExp, String organization) throws 
            OperatorCreationException, NoSuchAlgorithmException, CertIOException, CertificateException {
        X500Name certIssuer = getCertificateAttributes(signCertType, organization);
        X500Name certSubject = getCertificateAttributes(certType, organization);
        //LocalDateTime dateTime = LocalDateTime.now();
        Date notBefore = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        //LocalDateTime dateTimeExp = dateTime.plusYears(1);
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
		builder.addRDN( BCStyle.ST, "KA");
		builder.addRDN(BCStyle.O, organization);
		builder.addRDN(BCStyle.OU, "IDA-TEST-ORG-UNIT");
		builder.addRDN(BCStyle.CN, cn);
		return builder.build();
	}

    public PrivateKeyEntry getKeyEntry(String dirPath, PartnerTypes partnerType) throws NoSuchAlgorithmException, UnrecoverableEntryException, 
            KeyStoreException, CertificateException, IOException, OperatorCreationException {

        if (partnerType == PartnerTypes.EKYC) {
            String partnerFilePath = dirPath + '/' + PartnerTypes.EKYC.getFilePrepend() + PARTNER_P12_FILE_NAME;
            PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(partnerFilePath);
            //If ekyc key is not present, use relying partner key
            if(privateKeyEntry == null) {
            	partnerFilePath = dirPath + '/' + PartnerTypes.RELYING_PARTY.getFilePrepend() + PARTNER_P12_FILE_NAME;
                privateKeyEntry = getPrivateKeyEntry(partnerFilePath);
            }
			return privateKeyEntry;
        }
        
        String filePrepend = partnerType.getFilePrepend();
        if (partnerType == PartnerTypes.RELYING_PARTY) {
            String partnerFilePath = dirPath + '/' + partnerType.getFilePrepend() + PARTNER_P12_FILE_NAME;
            return getPrivateKeyEntry(partnerFilePath);
        }
        
        if (partnerType == PartnerTypes.FTM) {
            String csPartnerFilePath = dirPath + '/' + filePrepend + CHIP_SPECIFIC_KEY + PARTNER_P12_FILE_NAME;
            PrivateKeyEntry csKeyEntry = getPrivateKeyEntry(csPartnerFilePath);
            if (Objects.nonNull(csKeyEntry))
                return csKeyEntry;
            
            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            PrivateKeyEntry pKeyEntry = getPrivateKeyEntry(partnerFilePath);
            LocalDateTime dateTime = LocalDateTime.now(); 
            LocalDateTime dateTimeExp = dateTime.plusYears(1);
            KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
            
            X500Principal signerPrincipal = ((X509Certificate)pKeyEntry.getCertificate()).getSubjectX500Principal();
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
            PrivateKeyEntry dsKeyEntry = getPrivateKeyEntry(dsPartnerFilePath);
            if (Objects.nonNull(dsKeyEntry) && isCertificateValid((X509Certificate) dsKeyEntry.getCertificate()))
                return dsKeyEntry;

            String partnerFilePath = dirPath + '/' + filePrepend + PARTNER_P12_FILE_NAME;
            PrivateKeyEntry pKeyEntry = getPrivateKeyEntry(partnerFilePath);
            LocalDateTime dateTime = LocalDateTime.now(); 
            LocalDateTime dateTimeExp = dateTime.plusMonths(1);
            KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
           
            X500Principal signerPrincipal = ((X509Certificate)pKeyEntry.getCertificate()).getSubjectX500Principal();
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
            
            OutputStream outputStream = new FileOutputStream(partnerFilePath);
            keyStore.store(outputStream, getP12Pass());
            outputStream.flush();
            outputStream.close();
            return true;
        }
        return false;
    }

    public String getKeysDirPath() {
    	String domain = environment.getProperty(DOMAIN_URL, "localhost").replace("https://", "").replace("http://", "").replace("/", "");
		return System.getProperty("java.io.tmpdir") + File.separator + "IDA-" + domain;
    }
}
