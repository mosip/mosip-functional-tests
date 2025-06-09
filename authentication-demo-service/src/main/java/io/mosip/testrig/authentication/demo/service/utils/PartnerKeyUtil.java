package io.mosip.testrig.authentication.demo.service.utils;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.mosip.testrig.authentication.demo.service.dto.PartnerKeyRequestDto;
import io.mosip.testrig.authentication.demo.service.dto.PartnerKeyResponseDto;
import io.mosip.testrig.authentication.demo.service.exception.PartnerKeyException;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class PartnerKeyUtil {

    private static volatile PartnerKeyUtil instance;
    public static final char DASH_DELIMITER = '-';

    public PartnerKeyUtil() { }

    public static PartnerKeyUtil getInstance() {
        if (instance == null) {
            synchronized (PartnerKeyUtil.class) {
                if (instance == null) {
                    instance = new PartnerKeyUtil();
                }
            }
        }
        return instance;
    }

    public PartnerKeyResponseDto getPartnerCertificates(PartnerKeyRequestDto dto) throws PartnerKeyException {
        try {
            String filePrepend = buildFilePrepend(dto);
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime expiry = start.plusYears(dto.getRpPartnerCertExpiryYears());
            KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign);
            // CA certificate
            String caPath = buildFilePath(dto, filePrepend, dto.getCaP12FileName());
            KeyStore.PrivateKeyEntry caEntry = loadOrGenerateKeyEntry(dto, null, dto.getPrefixCA(), caPath, keyUsage, start, expiry);
            // Intermediate certificate
            String interPath = buildFilePath(dto, filePrepend, dto.getInterP12FileName());
            KeyStore.PrivateKeyEntry interEntry = loadOrGenerateKeyEntry(dto, caEntry.getPrivateKey(), dto.getPrefixINTER(), interPath, keyUsage, start, expiry);
            // Partner certificate
            String partnerPath = buildFilePath(dto, filePrepend, dto.getPartnerP12FileName());
            keyUsage = filePrepend.equalsIgnoreCase(dto.getPartnerType())
                    ? new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.encipherOnly | KeyUsage.decipherOnly)
                    : keyUsage;
            KeyStore.PrivateKeyEntry partnerEntry = loadOrGenerateKeyEntry(dto, interEntry.getPrivateKey(), dto.getPrefixPARTNER(), partnerPath, keyUsage, start, expiry);
            return new PartnerKeyResponseDto(
                    convertToPem(caEntry.getCertificate()),
                    convertToPem(interEntry.getCertificate()),
                    convertToPem(partnerEntry.getCertificate())
            );
        } catch (Exception e) {
            throw new PartnerKeyException("Error generating partner certificates", e);
        }
    }
    private String buildFilePrepend(PartnerKeyRequestDto dto) {
        String name = dto.getKeyFileNameByPartnerName();
        return (name != null && !name.isEmpty())
                ? getFilePrepend(dto.getPartnerType()) + DASH_DELIMITER + dto.getOrganization()
                : getFilePrepend(dto.getPartnerType());
    }
    private String buildFilePath(PartnerKeyRequestDto dto, String prefix, String fileName) {
        return dto.getDirPath() + File.separator + prefix + fileName;
    }
    private KeyStore.PrivateKeyEntry loadOrGenerateKeyEntry(PartnerKeyRequestDto dto, PrivateKey signerKey,
                                                            String prefix, String p12FilePath, KeyUsage usage,
                                                            LocalDateTime start, LocalDateTime expiry)
            throws Exception {
        KeyStore.PrivateKeyEntry entry = loadPrivateKeyEntry(dto, p12FilePath);
        if (entry == null) {
            entry = generateKeyEntry(dto, signerKey, prefix, p12FilePath, usage, start, expiry);
        }
        return entry;
    }
    private KeyStore.PrivateKeyEntry generateKeyEntry(PartnerKeyRequestDto dto, PrivateKey signerKey, String aliasPrefix,
                                                      String p12FilePath, KeyUsage usage,
                                                      LocalDateTime start, LocalDateTime expiry) throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance(dto.getRsaAlgorithm())
                .generateKeyPair();
        String subject = aliasPrefix + buildFilePrepend(dto);
        X509Certificate cert = generateCertificate(dto, signerKey == null ? keyPair.getPrivate() : signerKey,
                keyPair.getPublic(), aliasPrefix, subject, usage, start, expiry);
        KeyStore.PrivateKeyEntry entry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), new X509Certificate[]{cert});
        KeyStore keyStore = KeyStore.getInstance(dto.getKeyStoreType());
        keyStore.load(null, dto.getP12Password());
        keyStore.setEntry(dto.getKeyAlias(), entry, new KeyStore.PasswordProtection(dto.getP12Password()));
        Path path = Paths.get(p12FilePath).getParent();
        if (path != null && !Files.exists(path)) Files.createDirectories(path);
        try (OutputStream out = new FileOutputStream(p12FilePath)) {
            keyStore.store(out, dto.getP12Password());
        }
        return entry;
    }
    private X509Certificate generateCertificate(PartnerKeyRequestDto dto, PrivateKey signerKey, PublicKey subjectKey,
                                                String issuerCN, String subjectCN, KeyUsage usage,
                                                LocalDateTime start, LocalDateTime expiry)
            throws Exception {
        X500Name issuer = getX500Name(issuerCN, dto);
        X500Name subject = getX500Name(subjectCN, dto);
        Date notBefore = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date notAfter = Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant());
        BigInteger serial = new BigInteger(Long.toString(new SecureRandom().nextLong()));
        ContentSigner signer = new JcaContentSignerBuilder(dto.getSignAlgorithm()).build(signerKey);
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, subjectKey);
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        builder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(subjectKey));
        builder.addExtension(Extension.keyUsage, true, usage);
        X509CertificateHolder holder = builder.build(signer);
        return new JcaX509CertificateConverter().getCertificate(holder);
    }
    private X500Name getX500Name(String cn, PartnerKeyRequestDto dto) {
        X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE);
        builder.addRDN(BCStyle.C, dto.getCountry());
        builder.addRDN(BCStyle.ST, dto.getState());
        builder.addRDN(BCStyle.O, dto.getOrganization());
        builder.addRDN(BCStyle.OU, dto.getOrgUnit());
        builder.addRDN(BCStyle.CN, cn);
        return builder.build();
    }
    private KeyStore.PrivateKeyEntry loadPrivateKeyEntry(PartnerKeyRequestDto dto, String filePath) throws Exception {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return null;
        KeyStore store = KeyStore.getInstance(dto.getKeyStoreType());
        try (InputStream in = new FileInputStream(filePath)) {
            store.load(in, dto.getP12Password());
            return (KeyStore.PrivateKeyEntry) store.getEntry(dto.getKeyAlias(),
                    new KeyStore.PasswordProtection(dto.getP12Password()));
        }
    }
    private String convertToPem(java.security.cert.Certificate cert) throws IOException {
        StringWriter writer = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(cert);
        }
        return writer.toString();
    }
    public String getFilePrepend(String partnerType) {
        return switch (partnerType.toUpperCase()) {
            case "DEVICE" -> "device";
            case "RELYING_PARTY" -> "rp";
            case "FTM" -> "ftm";
            case "EKYC" -> "ekyc";
            case "MISP" -> "misp";
            default -> throw new IllegalArgumentException("Unknown partner type: " + partnerType);
        };
    }
}