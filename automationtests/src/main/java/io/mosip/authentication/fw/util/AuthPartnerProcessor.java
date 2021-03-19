package io.mosip.authentication.fw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.precon.XmlPrecondtion;

/**
 * Class is to create auth partner or demo app processor in new thread
 * 
 * @author Vignesh
 *
 */
public class AuthPartnerProcessor extends AdminTestUtil{

	private static final Logger DEMOAPP_LOGGER = Logger.getLogger(AuthPartnerProcessor.class);
	public static Process authPartherProcessor;

	/**
	 * Start demo app or auth partner applciation in seperate thread using runtime processor
	 */
	public static void startProcess() {
		String encryptUtilPort = props.getProperty("encryptUtilPort");
		try {
			authPartherProcessor = Runtime.getRuntime()
					.exec(new String[] { getJavaPath(), "-Dmosip.base.url="+ApplnURI,
							"-Dserver.port="+encryptUtilPort, "-jar", getDemoAppJarPath() });
			Runnable startDemoAppTask = () -> {
				try (InputStream inputStream = authPartherProcessor.getInputStream();
						BufferedReader bis = new BufferedReader(new InputStreamReader(inputStream));) {
					String line;
					while ((line = bis.readLine()) != null)
						DEMOAPP_LOGGER.info(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			new Thread(startDemoAppTask).start();
			Thread.sleep(60000);
		} catch (Exception e) {
			DEMOAPP_LOGGER.error("Exception occured in starting the demo auth partner processor");
		}
	}
	
	/**
	 * Method to get demo app jar path accodring to OS
	 * 
	 * @return filepath
	 */
	private static String getDemoAppJarPath() {
		String demoAppVersion = props.getProperty("demoAppVersion");
		if (getOSType().toString().equals("WINDOWS")) {
			return "C:/Users/" + System.getProperty("user.name")
					+ "/.m2/repository/io/mosip/authentication/authentication-demo-service/"
					+ demoAppVersion+ "/authentication-demo-service-" + demoAppVersion + ".jar";
		} else {
			DEMOAPP_LOGGER.info("Maven Path: " + RunConfigUtil.getLinuxMavenPath());
			String mavenPath = RunConfigUtil.getLinuxMavenPath();
			String settingXmlPath = mavenPath + "/conf/settings.xml";
			String repoPath = XmlPrecondtion.getValueFromXmlFile(settingXmlPath, "//localRepository");
			return repoPath + "/io/mosip/authentication/authentication-demo-service/" + demoAppVersion
					+ "/authentication-demo-service-" + demoAppVersion + ".jar";
		}
	}
	
	/**
	 * Method to get current device java path from environment detail according to OS
	 * 
	 * @return string
	 */
	private static String getJavaPath() {
		if (getOSType().toString().equals("WINDOWS")) {
			String javaHome = System.getenv("JAVA_HOME");
			if (javaHome != null || javaHome != "")
				return javaHome + "/bin/java";
			else
				return "java";
		} else
			return "java";
	}

}
