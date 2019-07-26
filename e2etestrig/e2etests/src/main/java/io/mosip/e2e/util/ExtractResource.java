package io.mosip.e2e.util;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class ExtractResource {
	final static Logger LOGGER = Logger.getLogger(ExtractResource.class);
	public static void removeOldMosipTestTestResource() {
		
		File mosipTestFile = new File(BaseUtil.getGlobalResourcePath());
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
		getListOfFilesFromJarAndCopyToExternalResource("ida/");
		getListOfFilesFromJarAndCopyToExternalResource("kernel/");
		getListOfFilesFromJarAndCopyToExternalResource("preReg/");
		getListOfFilesFromJarAndCopyToExternalResource("config/");
		getListOfFilesFromJarAndCopyToExternalResource("regProc/");
		getListOfFilesFromJarAndCopyToExternalResource("idRepository/");
		getListOfFilesFromJarAndCopyToExternalResource("customize-emailable-report-template.html");
		getListOfFilesFromJarAndCopyToExternalResource("testngapi.xml");
		getListOfFilesFromJarAndCopyToExternalResource("version.txt");
		getListOfFilesFromJarAndCopyToExternalResource("metadata.xml");
		getListOfFilesFromJarAndCopyToExternalResource("log4j.properties");
	}
	public static void getListOfFilesFromJarAndCopyToExternalResource(String key) {
		try {
			CodeSource src = BaseUtil.class.getProtectionDomain().getCodeSource();
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
	
	private static boolean copyFilesFromJarToOutsideResource(String path) {
		try {
			File resourceFile = new File(BaseUtil.jarUrl).getParentFile();
			File destinationFile = new File(resourceFile.getAbsolutePath()+"/MosipTestResource/" + path);
			org.apache.commons.io.FileUtils.copyInputStreamToFile(BaseUtil.class.getResourceAsStream("/" + path),
					destinationFile);
			return true;
		} catch (Exception e) {
			LOGGER.error(
					"Exception Occured in copying the resource from jar. Kindly build new jar to perform smooth test execution: "
							+ e.getMessage());
			return false;
		}
	}
}
