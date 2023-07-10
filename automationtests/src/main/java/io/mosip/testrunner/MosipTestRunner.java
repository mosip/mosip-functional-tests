package io.mosip.testrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.testng.TestNG;

import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.EncryptionDecrptionUtil;
import io.mosip.dbaccess.DBManager;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.ida.certificate.CertificateGenerationUtil;
import io.mosip.ida.certificate.PartnerRegistration;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.kernel.util.KeycloakUserManager;
import io.mosip.service.BaseTestCase;

/**
 * Class to initiate mosip api test execution
 * 
 * @author Vignesh
 *
 */
public class MosipTestRunner {
	private static final Logger LOGGER = Logger.getLogger(MosipTestRunner.class);
	


	public static String jarUrl = MosipTestRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	public static List<String> languageList = new ArrayList<>();
	public static File oidcJWK1 = new File("src/main/resources/oidcJWK1.txt");

	/**
	 * C Main method to start mosip test execution
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {

		try {

			Map<String, String> envMap = System.getenv();
			LOGGER.info("** ------------- Get ALL ENV varibales --------------------------------------------- **");
			for (String envName : envMap.keySet()) {
				LOGGER.info(String.format("ENV %s = %s%n", envName, envMap.get(envName)));
			}

			if (checkRunType().equalsIgnoreCase("JAR")) {
				ExtractResource.removeOldMosipTestTestResource();
				ExtractResource.extractResourceFromJar();
			}
			ConfigManager.init(); 
			BaseTestCase.suiteSetup();
			AdminTestUtil.encryptDecryptUtil = new EncryptionDecrptionUtil();
			HealthChecker healthcheck = new HealthChecker();
			healthcheck.setCurrentRunningModule(BaseTestCase.currentModule);
			Thread trigger = new Thread(healthcheck);
			trigger.start();

			KeycloakUserManager.createUsers(); // Langauge Independent

			String partnerKeyURL = "";

			if (BaseTestCase.listOfModules.contains("auth")
					|| BaseTestCase.listOfModules.contains(GlobalConstants.ESIGNET)) {
				PartnerRegistration.deleteCertificates();
				CertificateGenerationUtil.getThumbprints();
				AdminTestUtil.createAndPublishPolicy();
				partnerKeyURL = PartnerRegistration.generateAndGetPartnerKeyUrl();

			}

			List<String> localLanguageList = new ArrayList<String>(BaseTestCase.getLanguageList());


			if (BaseTestCase.listOfModules.contains(GlobalConstants.MASTERDATA)) {
				BaseTestCase.mapUserToZone();
				BaseTestCase.mapZone();

				for (int i = 0; i < localLanguageList.size(); i++) {
					BaseTestCase.languageList.clear();
					BaseTestCase.languageList.add(localLanguageList.get(i));

					DBManager.clearMasterDbData();
					BaseTestCase.currentModule = GlobalConstants.MASTERDATA;
					BaseTestCase.setReportName("masterdata-" + localLanguageList.get(i));
					startTestRunner();

				}

			} else if (BaseTestCase.listOfModules.contains("auth")
					|| BaseTestCase.listOfModules.contains(GlobalConstants.ESIGNET)) {
				if (partnerKeyURL.isEmpty())
					LOGGER.error("partnerKeyURL is null");
				else
					startTestRunner();
			} else {
				startTestRunner();
			}
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
		}

		MockSMTPListener.bTerminate = true;

		HealthChecker.bTerminate = true;

		System.exit(0);

	}

	/**
	 * The method to start mosip testng execution
	 * 
	 * @throws IOException
	 */
	public static void startTestRunner() {
		File homeDir = null;
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		List<String> modulesToRun = BaseTestCase.listOfModules;
		String os = System.getProperty("os.name");
		LOGGER.info(os);
		if (checkRunType().contains("IDE") || os.toLowerCase().contains("windows")) {
			homeDir = new File(System.getProperty("user.dir") + "/testNgXmlFiles");
			LOGGER.info("IDE :" + homeDir);
		}
		else {
			File dir = new File(System.getProperty("user.dir"));
			homeDir = new File(dir.getParent() + "/mosip/testNgXmlFiles");
			LOGGER.info("ELSE :" + homeDir);
		}
		for (File file : homeDir.listFiles()) {
			for (String fileName : modulesToRun) {
				if (file.getName().toLowerCase().contains(fileName)) {
					suitefiles.add(file.getAbsolutePath());
				} else if (fileName.equals("all") && file.getName().toLowerCase().contains("testng")) {
					suitefiles.add(file.getAbsolutePath());
				}
			}
		}
		runner.setTestSuites(suitefiles);
		System.getProperties().setProperty("testng.outpur.dir", "testng-report");
		runner.setOutputDirectory("testng-report");
		runner.run();
	}

	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 * @throws IOException
	 */
	public static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource";
		} else if (checkRunType().equalsIgnoreCase("IDE")) {
			String path = new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath();
			if (path.contains("test-classes"))
				path = path.replace("test-classes", "classes");
			return path;
		}
		return "Global Resource File Path Not Found";
	}

	public static String getResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath();
		} else if (checkRunType().equalsIgnoreCase("IDE")) {
			String path = new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
					.toString();
			if (path.contains("test-classes"))
				path = path.replace("test-classes", "classes");
			return path;
		}
		return "Global Resource File Path Not Found";
	}
	
	public static String generatePulicKey() {
		String publicKey = null;
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048, BaseTestCase.secureRandom);
			final KeyPair keypair = keyGenerator.generateKeyPair();
			publicKey = java.util.Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.getStackTrace());
		}
		return publicKey;
	}
	
	public static String generatePublicKeyForMimoto() {
        KeyPairGenerator keyPairGen;
        String vcString = "";
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            StringWriter stringWriter = new StringWriter();
            try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
                pemWriter.writeObject(publicKey);
                pemWriter.flush();
                vcString = stringWriter.toString();
        		if (System.getProperty("os.name").toLowerCase().contains("windows") == true) {
        			vcString = vcString.replaceAll("\r\n", "\\\\n");
        		} else {
        			vcString = vcString.replaceAll("\n", "\\\\n");
        		}
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
        	LOGGER.error(e.getStackTrace());
        }
        return vcString;
	}

	public static String generateJWKPublicKey() {
//		String publicKey = null;
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048, BaseTestCase.secureRandom);
			final KeyPair keypair = keyGenerator.generateKeyPair();
			RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keypair.getPublic()).keyID("RSAKeyID").keyUse(KeyUse.SIGNATURE)
				    .privateKey(keypair.getPrivate())
				    .build();
			
			return jwk.toJSONString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.getStackTrace());
			return null;
		}
	}
	
	public static Properties getproperty(String path) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
		} catch (Exception e) {
			LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}

	/**
	 * The method will return mode of application started either from jar or eclipse
	 * ide
	 * 
	 * @return
	 */
	public static String checkRunType() {
		if (MosipTestRunner.class.getResource("MosipTestRunner.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}

}
