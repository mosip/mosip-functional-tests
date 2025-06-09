package io.mosip.testrig.authentication.demo.service.test;

import io.mosip.testrig.authentication.demo.service.dto.PartnerKeyRequestDto;
import io.mosip.testrig.authentication.demo.service.dto.PartnerKeyResponseDto;
import io.mosip.testrig.authentication.demo.service.exception.PartnerKeyException;
import io.mosip.testrig.authentication.demo.service.utils.PartnerKeyUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class PartnerKeyUtilTest {

    private PartnerKeyUtil partnerKeyUtil;

    @BeforeEach
    void setUp() {
        partnerKeyUtil = PartnerKeyUtil.getInstance();
    }

    private static final String TEST_DIR = "target/test-certs";

    @BeforeAll
    static void setup() throws Exception {
        Path path = Path.of(TEST_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Test
    void testCertificateGeneration() {
        PartnerKeyRequestDto request = new PartnerKeyRequestDto();
        request.setDirPath(TEST_DIR);
        request.setPartnerType("RELYING_PARTY");
        request.setOrganization("mosip-org");
        request.setKeyFileNameByPartnerName("true");

        PartnerKeyUtil util = PartnerKeyUtil.getInstance();

        try {
            PartnerKeyResponseDto response = util.getPartnerCertificates(request);

            assertNotNull(response.getCaCertificate(), "CA certificate should not be null");
            assertFalse(response.getCaCertificate().isEmpty(), "CA certificate should not be empty");

            assertNotNull(response.getInterCertificate(), "Intermediate certificate should not be null");
            assertFalse(response.getInterCertificate().isEmpty(), "Intermediate certificate should not be empty");

            assertNotNull(response.getPartnerCertificate(), "Partner certificate should not be null");
            assertFalse(response.getPartnerCertificate().isEmpty(), "Partner certificate should not be empty");

            File caFile = new File(TEST_DIR + File.separator + "rp-mosip-org-ca.p12");
            File interFile = new File(TEST_DIR + File.separator + "rp-mosip-org-inter.p12");
            File partnerFile = new File(TEST_DIR + File.separator + "rp-mosip-org-partner.p12");

            System.out.println("CA File exists? " + caFile.exists());
            System.out.println("Intermediate File exists? " + interFile.exists());
            System.out.println("Partner File exists? " + partnerFile.exists());

            assertTrue(caFile.exists(), "CA .p12 file should exist");
            assertTrue(interFile.exists(), "Intermediate .p12 file should exist");
            assertTrue(partnerFile.exists(), "Partner .p12 file should exist");

        } catch (PartnerKeyException e) {
            fail("Certificate generation failed: " + e.getMessage());
        }
    }

    @Test
    void testCertificateGeneration_Success() throws Exception {
        PartnerKeyRequestDto request = new PartnerKeyRequestDto();
        request.setDirPath(TEST_DIR);
        request.setPartnerType("RELYING_PARTY");
        request.setOrganization("mosip-org");
        request.setKeyFileNameByPartnerName("true");

        PartnerKeyUtil util = PartnerKeyUtil.getInstance();

        PartnerKeyResponseDto response = util.getPartnerCertificates(request);
        assertNotNull(response);
        assertNotNull(response.getCaCertificate());
        assertTrue(response.getCaCertificate().length() > 0);
    }

    @Test
    void testGetFilePrepend_validTypes() {
        assertEquals("rp", partnerKeyUtil.getFilePrepend("RELYING_PARTY"));
        assertEquals("device", partnerKeyUtil.getFilePrepend("DEVICE"));
        assertEquals("ftm", partnerKeyUtil.getFilePrepend("FTM"));
        assertEquals("ekyc", partnerKeyUtil.getFilePrepend("EKYC"));
        assertEquals("misp", partnerKeyUtil.getFilePrepend("MISP"));
    }

    @Test
    void testGetFilePrepend_invalidType_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            partnerKeyUtil.getFilePrepend("INVALID_TYPE");
        });
        assertEquals("Unknown partner type: INVALID_TYPE", ex.getMessage());
    }

    @Test
    void testGetPartnerCertificates_success(@TempDir Path tempDir) throws Exception {
        PartnerKeyRequestDto dto = new PartnerKeyRequestDto();
        dto.setPartnerType("RELYING_PARTY");
        dto.setOrganization("testorg");
        dto.setCountry("IN");
        dto.setState("KA");
        dto.setOrgUnit("MOSIP");
        dto.setDirPath(tempDir.toString());
        dto.setCaP12FileName("-ca.p12");
        dto.setInterP12FileName("-inter.p12");
        dto.setPartnerP12FileName("-partner.p12");
        dto.setPrefixCA("ca-");
        dto.setPrefixINTER("inter-");
        dto.setPrefixPARTNER("partner-");
        dto.setP12Password("testpass".toCharArray());
        dto.setKeyAlias("testkey");
        dto.setRsaAlgorithm("RSA");
        dto.setRsaKeySize(2048);
        dto.setKeyStoreType("PKCS12");
        dto.setSignAlgorithm("SHA256withRSA");
        dto.setRpPartnerCertExpiryYears(1);
        dto.setKeyFileNameByPartnerName("true");

        PartnerKeyResponseDto response = partnerKeyUtil.getPartnerCertificates(dto);

        assertNotNull(response);
        assertTrue(response.getCaCertificate().contains("BEGIN CERTIFICATE"));
        assertTrue(response.getInterCertificate().contains("BEGIN CERTIFICATE"));
        assertTrue(response.getPartnerCertificate().contains("BEGIN CERTIFICATE"));

        assertTrue(new File(tempDir.toFile(), "rp-testorg-ca.p12").exists());
        assertTrue(new File(tempDir.toFile(), "rp-testorg-inter.p12").exists());
        assertTrue(new File(tempDir.toFile(), "rp-testorg-partner.p12").exists());
    }

    @Test
    void testGetPartnerCertificates_withInvalidAlgorithm_throwsException() {
        PartnerKeyRequestDto dto = new PartnerKeyRequestDto();
        dto.setPartnerType("RELYING_PARTY");
        dto.setOrganization("org");
        dto.setCountry("IN");
        dto.setState("KA");
        dto.setOrgUnit("MOSIP");
        dto.setDirPath(System.getProperty("java.io.tmpdir")); // valid path
        dto.setCaP12FileName("-ca.p12");
        dto.setInterP12FileName("-inter.p12");
        dto.setPartnerP12FileName("-partner.p12");
        dto.setPrefixCA("ca-");
        dto.setPrefixINTER("inter-");
        dto.setPrefixPARTNER("partner-");
        dto.setP12Password("testpass".toCharArray());
        dto.setKeyAlias("testkey");
        dto.setRsaAlgorithm("INVALID_ALGO");
        dto.setRsaKeySize(2048);
        dto.setKeyStoreType("PKCS12");
        dto.setSignAlgorithm("SHA256withRSA");
        dto.setRpPartnerCertExpiryYears(1);
        dto.setKeyFileNameByPartnerName("true");

        PartnerKeyException ex = assertThrows(PartnerKeyException.class, () -> {
            partnerKeyUtil.getPartnerCertificates(dto);
        });

        assertTrue(ex.getMessage().contains("Error generating partner certificates"));
    }
}