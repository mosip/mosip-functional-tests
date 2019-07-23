package io.mosip.e2e.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BaseUtil {
	private Properties prop;
	public static String ApplnURI;
	public static String environment;
	public static String testLevel;
	public static String adminRegProcAuthToken;
	public static String SEPRATOR = "";
	public static String buildNumber = "";
	protected static Logger logger = Logger.getLogger(BaseUtil.class);
	public static String jarUrl = BaseUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	/**
	 * Main method to start mosip test execution
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			ExtractResource.removeOldMosipTestTestResource();
			ExtractResource.extractResourceFromJar();
		}
		System.out.println(getGlobalResourcePath());
		
	
	}

	/**
	 * The method to start mosip testng execution
	 * 
	 * @throws IOException
	 */

	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 * @throws IOException
	 */
	public static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource".toString();
		} else if (checkRunType().equalsIgnoreCase("IDE"))
			return new File(BaseUtil.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
					.toString();
		return "Global Resource File Path Not Found";
	}

	/**
	 * The method will return mode of application started either from jar or eclipse
	 * ide
	 * 
	 * @return
	 */
	public static String checkRunType() {
		if (BaseUtil.class.getResource("BaseUtil.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}
}
