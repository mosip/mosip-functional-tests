package io.mosip.resident.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;

/**
 * Holds utility function for resident testing
 * 
 * @author Vignesh
 *
 */
public class ResidentTestUtil extends AuthTestsUtil{
	
	private static final Logger residentLogger = Logger.getLogger(ResidentTestUtil.class);
	
	public static void initiateResidentTest() {
		copyResidentTestResource();
	}
	
	public static void copyResidentTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/resident");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			residentLogger.info("Copied the resident test resource successfully");
		} catch (Exception e) {
			residentLogger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	/**
	 * The method get property value for the key
	 * 
	 * @param key
	 * @return string
	 */
	public static String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}	
	
	
	/**
	 * The method get env config details
	 * 
	 * @return properties
	 */
	private static Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			RunConfigUtil.objRunConfig.setUserDirectory();
			input = new FileInputStream(new File(RunConfigUtil.getResourcePath()+"resident/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
			prop.load(input);
			input.close();
			return prop;
		} catch (Exception e) {
			residentLogger.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File(getRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataPath[" + index + "]");
	}
	
	/**
	 * The method will return test data file name from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataFileName(String className, int index) {
		return getPropertyAsMap(new File(getRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataFileName[" + index + "]");
	}
	
	/**
	 * The method returns run config path
	 */
	public String getRunConfigFile() {
		return RunConfigUtil.getGlobalResourcePath()+"/resident/TestData/RunConfig/runConfiguration.properties";
	}

}
