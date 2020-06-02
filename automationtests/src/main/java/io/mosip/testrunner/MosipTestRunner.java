package io.mosip.testrunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.TestNG;

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

	/**C
	 * Main method to start mosip test execution
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			ExtractResource.removeOldMosipTestTestResource();
			ExtractResource.extractResourceFromJar();
		}
		// Initializing or setting up execution
		BaseTestCase.suiteSetup();
		startTestRunner();
	}

	/**
	 * The method to start mosip testng execution
	 * 
	 * @throws IOException
	 */
	public static void startTestRunner() {
		File homeDir=null;
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		
		String specifiedModules = System.getProperty("modules");
		String[] modulesToRun = specifiedModules.split(",");
		String os=System.getProperty("os.name");
		System.out.println(os);
		//suitefiles.add(new File(System.getProperty("user.dir") +"/testNgXmlFiles/healthCheckTest.xml").getAbsolutePath());
		 if(checkRunType().contains("IDE") || os.toLowerCase().contains("windows")==false) {
			 homeDir = new File(System.getProperty("user.dir") + "/testNgXmlFiles");
		}
		else {
			File dir=new File(System.getProperty("user.dir"));
		homeDir = new File(dir.getParent() + "/testNgXmlFiles");
		} 
		System.out.println(homeDir.getAbsolutePath());
		for (File file : homeDir.listFiles()) {
			for (String fileName : modulesToRun) {
				if (file.getName().toLowerCase().contains(fileName)) {
					suitefiles.add(file.getAbsolutePath());
				} else if(fileName.equals("all") && file.getName().toLowerCase().contains("testng")) {
					suitefiles.add(file.getAbsolutePath());
				}
			}
		}
		
		runner.setTestSuites(suitefiles);
		runner.setOutputDirectory("testng-report");
		runner.run();
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
		} else if (checkRunType().equalsIgnoreCase("IDE"))
			return new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
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
		if (MosipTestRunner.class.getResource("MosipTestRunner.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}
	
}
