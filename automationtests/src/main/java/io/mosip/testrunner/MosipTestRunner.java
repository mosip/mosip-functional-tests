package io.mosip.testrunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.TestNG;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.PMPDataManager;
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
			try {
				copyDbToTarget();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public static void copyDbToTarget() throws IOException {
		String seperator="";
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			seperator="\\";
		} else {
			seperator="/";
		}
		File target=new File(System.getProperty("user.dir"));
		File db=new File(target.getParent()+seperator+"db");
		System.out.println("Target is :: "+target.getAbsolutePath());
		System.out.println(db.getAbsolutePath());
		FileUtils.copyDirectoryToDirectory(db, target);
		
	}
	
	
	
}
