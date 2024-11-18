package io.mosip.testrig.apirig.testrunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ExtractResource {
	
	private static final Logger LOGGER = Logger.getLogger(ExtractResource.class);
	
	
	public static void extractCommonResourceFromJar() {
		getListOfFilesFromJarAndCopyToExternalResource("config/");
		getListOfFilesFromJarAndCopyToExternalResource("customize-emailable-report-template.html");
		getListOfFilesFromJarAndCopyToExternalResource("metadata.xml");
		getListOfFilesFromJarAndCopyToExternalResource("log4j.properties");
		getListOfFilesFromJarAndCopyToExternalResource("spring.properties");
		getListOfFilesFromJarAndCopyToExternalResource("validations.properties");
		getListOfFilesFromJarAndCopyToExternalResource("dbFiles/");
		getListOfFilesFromJarAndCopyToExternalResource("testCaseSkippedList.txt");
	}
	
	public static void copyCommonResources(){
		copyCommonResources("config/");
		copyCommonResources("customize-emailable-report-template.html");
		copyCommonResources("metadata.xml");
		copyCommonResources("log4j.properties");
		copyCommonResources("spring.properties");
		copyCommonResources("validations.properties");
		copyCommonResources("dbFiles/");
		copyCommonResources("testCaseSkippedList.txt");
	}
	
	public static void copyCommonResources(String moduleName) {
		try {
			File destination = new File(BaseTestCase.getGlobalResourcePath());
			File source = new File(
					BaseTestCase.getGlobalResourcePath().replace("MosipTestResource/MosipTemporaryTestResource", "") + moduleName);
			if (source.isDirectory())
				FileUtils.copyDirectoryToDirectory(source, destination);
			else {
				destination = new File(BaseTestCase.getGlobalResourcePath() + "/" + moduleName);
				FileUtils.copyFile(source, destination);
			}

			LOGGER.info("Copied the test resource successfully for " + moduleName);
		} catch (Exception e) {
			LOGGER.error("Exception occured while copying the file for : " + moduleName + " Error : " + e.getMessage());
		}
	}
	
	public static void getListOfFilesFromJarAndCopyToExternalResource(String key) {
		ZipInputStream zipInputStream = null;
		try {
			CodeSource src = BaseTestCase.class.getProtectionDomain().getCodeSource();
			if (src != null) {
				URL jar = src.getLocation();
				zipInputStream = new ZipInputStream(jar.openStream());
				File resourceFile = new File(BaseTestCase.jarURLS).getParentFile();
				String resourceFileParentPath = resourceFile.getAbsolutePath() + "/MosipTestResource/";
				while (true) {
					ZipEntry e = zipInputStream.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					if (name.startsWith(key) && name.contains(".")) {
						if (copyFilesFromJarToOutsideResource(resourceFileParentPath, name))
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
		} finally {
			closeZipInputStream(zipInputStream);
		}
	}
	
	public static void closeZipInputStream(ZipInputStream zipInputStream) {
		if (zipInputStream != null) {
	        try {
	        	zipInputStream.close();
	        } catch (IOException e) {
	        }
	    }
	}
	
	/**
	 * The method to copy resource from jar to outside jar
	 * 
	 * @param path
	 * @return
	 */
	private static boolean copyFilesFromJarToOutsideResource(String resourceFileParentPath, String resourceFileName) {
		try {
			String resourceFileAbsolutePath =  resourceFileParentPath + "MosipTemporaryTestResource/" + resourceFileName;
			File destinationFile = new File(resourceFileAbsolutePath);
			LOGGER.info("resourceFile : " + BaseTestCase.jarURLS + "destinationFile : " + resourceFileAbsolutePath);
			org.apache.commons.io.FileUtils.copyInputStreamToFile(BaseTestCase.class.getResourceAsStream("/" + resourceFileName), destinationFile);
			return true;
		} catch (Exception e) {
			LOGGER.error(
					"Exception Occured in copying the resource from jar. Kindly build new jar to perform smooth test execution: "
							+ e.getMessage());
			return false;
		}
	}
	
	/**
	 * The method to remove old generated mosip test resource
	 */
	public static void removeOldMosipTestTestResource() {
		File mosipTestFile = new File(BaseTestCase.getGlobalResourcePath());
		if (mosipTestFile.exists()) {
			if (deleteDirectory(mosipTestFile))
				LOGGER.info("Old MosipTestResource folder successfully deleted!!");
			else
				LOGGER.error("Old MosipTestResource folder not deleted.");

		} else {
			LOGGER.error("Old MosipTestResource folder not exist.");
		}
	}
	
	/**
	 * The method to delete directory and its all file inside the directory
	 * 
	 * @param dir
	 * @return boolean 
	 */
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

}
