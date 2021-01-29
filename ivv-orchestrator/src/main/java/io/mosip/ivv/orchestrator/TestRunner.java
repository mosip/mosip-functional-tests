package io.mosip.ivv.orchestrator;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import org.testng.TestNG;

import io.mosip.service.BaseTestCase;

public class TestRunner {
	private static final Logger LOGGER = Logger.getLogger(TestRunner.class);
	public static String jarUrl = TestRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	public static void main(String[] args) {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			removeOldMosipTestTestResource();
			extractResourceFromJar();
		}
		copyTestResources();
		BaseTestCase.environment=System.getProperty("env.user");
		BaseTestCase.ApplnURI=System.getProperty("env.endpoint");
		BaseTestCase.testLevel=System.getProperty("env.testLevel");
		BaseTestCase.initialize();
		
		startTestRunner();
	}

	public static void startTestRunner() {
		File homeDir = null;
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		String os = System.getProperty("os.name");
		LOGGER.info(os);
		if (checkRunType().contains("IDE") || os.toLowerCase().contains("windows") == true) {
			homeDir = new File(TestResources.getResourcePath() + "testngFile");
		}
		else {
			File dir=new File(System.getProperty("user.dir"));
		homeDir = new File(dir.getParent() + "/testngFile");
		} 
		
		for (File file : homeDir.listFiles()) {
			if (file.getName().toLowerCase() != null) {
				suitefiles.add(file.getAbsolutePath());
			}
		}

		runner.setTestSuites(suitefiles);
		runner.setOutputDirectory("testng-report");
		runner.run();
		System.exit(0);
	}

	public static String checkRunType() {
		if (TestRunner.class.getResource("TestRunner.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}
	
	
	public static void removeOldMosipTestTestResource() {
		File mosipTestFile = new File(TestRunner.getGlobalResourcePath());
		if (mosipTestFile.exists())
			if (deleteDirectory(mosipTestFile))
				LOGGER.info("Old MosipTestResource folder successfully deleted!!");
			else
				LOGGER.error("Old MosipTestResource folder not deleted.");
	}
	
	public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
	}
	
	public static void extractResourceFromJar() {
		getListOfFilesFromJarAndCopyToExternalResource("testngFile/");
		getListOfFilesFromJarAndCopyToExternalResource("config/");
		getListOfFilesFromJarAndCopyToExternalResource("local/");
		getListOfFilesFromJarAndCopyToExternalResource("preReg/");
		getListOfFilesFromJarAndCopyToExternalResource("kernel/");
	}
	
	public static void getListOfFilesFromJarAndCopyToExternalResource(String key) {
		try {
			CodeSource src = TestRunner.class.getProtectionDomain().getCodeSource();
			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					if (name.startsWith(key) & name.contains(".")) {
						if (copyFilesFromJarToOutsideResource(name))
							LOGGER.info("Copied the file: " + name + " to external resource successfully..!");
						else
							LOGGER.error("Fail to copy file: " + name + " to external resource");
					}
				}
			} else {
				LOGGER.error("Something went wrong with jar location");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured in extracting resource: " + e.getMessage());
		}
	}
	
	public static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource".toString();
		} else if (checkRunType().equalsIgnoreCase("IDE")) {
			String path = new File(TestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
					.toString();
			if(path.contains("test-classes"))
				path = path.replace("test-classes", "classes");
			return path;
		}
		return "Global Resource File Path Not Found";
	}
	
	private static boolean copyFilesFromJarToOutsideResource(String path) {
		try {
			File resourceFile = new File(TestRunner.jarUrl).getParentFile();
			File destinationFile = new File(resourceFile.getAbsolutePath()+"/MosipTestResource/" + path);
			org.apache.commons.io.FileUtils.copyInputStreamToFile(TestRunner.class.getResourceAsStream("/" + path),
					destinationFile);
			return true;
		} catch (Exception e) {
			LOGGER.error(
					"Exception Occured in copying the resource from jar. Kindly build new jar to perform smooth test execution: "
							+ e.getMessage());
			return false;
		}
	}	
	
	private static void copyTestResources() {
		TestResources.copyTestResource("/testngFile");
		TestResources.copyTestResource("/preReg");
		TestResources.copyTestResource("/config");
		TestResources.copyTestResource("/kernel");
	}

	
	
}
