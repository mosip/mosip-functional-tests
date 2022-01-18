package io.mosip.registrationProcessor.perf.util;

import java.io.File;

public class ResourcePathUtil {
	
	public static String jarUrl = ResourcePathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 */
	public String getResourcePath() {
		// return MosipTestRunner.getGlobalResourcePath()+"/";
		return ResourcePathUtil.getGlobalResourcePath() + File.separator;
	}
	
	private static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource".toString();
		} else if (checkRunType().equalsIgnoreCase("IDE"))
			return new File(ResourcePathUtil.class.getClassLoader().getResource("").getPath()).getAbsolutePath()
					.toString();
		return "Global Resource File Path Not Found";
	}
	
	/**
	 * The method will return mode of application started either from jar or eclipse
	 * ide
	 * 
	 * @return
	 */
	private static String checkRunType() {
		if (ResourcePathUtil.class.getResource("ResourcePathUtil.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}

}
