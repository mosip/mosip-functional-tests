package io.mosip.testrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.testng.TestNG;

import io.mosip.admin.fw.util.AdminTestUtil;
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

	/**
	 * C Main method to start mosip test execution
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {

		System.out.println("**-------------Printing env variables by getproperty -----------------------------**");
		System.out.println("MODULES:" + System.getProperty("MODULES"));
		System.out.println("ENV_USER:" + System.getProperty("ENV_USER"));
		System.out.println("ENV_ENDPOINT:" + System.getProperty("ENV_ENDPOINT"));
		System.out.println("ENV_TESTLEVEL:" + System.getProperty("ENV_TESTLEVEL"));
		System.out.println("ENV_LANGCODE:" + System.getProperty("ENV_LANGCODE"));
		System.out.println("work_dir:" + System.getProperty("work_dir"));
		
		System.out.println("**-------------Printing env variables by getenv -----------------------------**");
		System.out.println("MODULES:" + System.getenv("MODULES"));
		System.out.println("ENV_USER:" + System.getenv("ENV_USER"));
		System.out.println("ENV_ENDPOINT:" + System.getenv("ENV_ENDPOINT"));
		System.out.println("ENV_TESTLEVEL:" + System.getenv("ENV_TESTLEVEL"));
		System.out.println("ENV_LANGCODE:" + System.getenv("ENV_LANGCODE"));
		System.out.println("work_dir:" + System.getenv("work_dir"));
		System.out.println("DB Password from ENV::: "+ System.getenv("db-su-password"));
		System.out.println("DB Password from ENV::: "+ System.getenv("keycloak-external-url"));
		if (checkRunType().equalsIgnoreCase("JAR")) {
			ExtractResource.removeOldMosipTestTestResource();
			ExtractResource.extractResourceFromJar();
		}
		// Initializing or setting up execution
		ConfigManager.init();
		KeycloakUserManager.removeUser();
		KeycloakUserManager.createUsers();
		BaseTestCase.suiteSetup();
		BaseTestCase.mapUserToZone();
		BaseTestCase.mapZone();
		
		startTestRunner();

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
		// String specifiedModules = System.getProperty("modules");
		List<String> modulesToRun = BaseTestCase.listOfModules;
		String os = System.getProperty("os.name");
		LOGGER.info(os);
		// suitefiles.add(new File(System.getProperty("user.dir")
		// +"/testNgXmlFiles/healthCheckTest.xml").getAbsolutePath());
		if (checkRunType().contains("IDE") || os.toLowerCase().contains("windows") == true) {
			homeDir = new File(System.getProperty("user.dir") + "/testNgXmlFiles");
			LOGGER.info("IDE :" + homeDir);
		}
		/*
		 * if(checkRunType().contains("IDE") ||
		 * os.toLowerCase().contains("windows")==true) { URL res =
		 * MosipTestRunner.class.getClassLoader().getResource("testNgXmlFiles"); try {
		 * homeDir = Paths.get(res.toURI()).toFile(); } catch (URISyntaxException e) {
		 * LOGGER.error("Exception getting the xml file path :"+e.getMessage()); } }
		 */
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
		KeycloakUserManager.removeUser();
		System.exit(0);
	}

	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 * @throws IOException
	 */
	public static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource".toString();
		} else if (checkRunType().equalsIgnoreCase("IDE")) {
			String path = new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
					.toString();
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
			keyGenerator.initialize(2048, new SecureRandom());
			final KeyPair keypair = keyGenerator.generateKeyPair();
			publicKey = java.util.Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return publicKey;
	}
	
	public static Properties getproperty(String path) {
		Properties prop = new Properties();		
		try {
			File file = new File(path);
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.error("Exception " + e.getMessage());
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
