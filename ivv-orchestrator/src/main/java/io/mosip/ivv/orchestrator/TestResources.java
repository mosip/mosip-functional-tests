package io.mosip.ivv.orchestrator;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class TestResources {
	public static Logger logger=Logger.getLogger(TestResources.class);
	private static String resourceFolderName="MosipTemporaryTestResource";;
	public static String jarUrl = TestResources.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	public static void copyPreRegTestResource() {
		try {
			File source = new File(TestResources.getGlobalResourcePaths() + "/preReg");
			File destination = new File(TestResources.getGlobalResourcePaths() + "/"+TestResources.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			String path=TestResources.getGlobalResourcePaths().replace("classes", "test-classes");
			File source2 = new File(TestResources.getGlobalResourcePaths()+"/config");
			File destination2 = new File(path);
			FileUtils.copyDirectoryToDirectory(source2, destination2);
			FileUtils.copyDirectoryToDirectory(source, destination2);
			logger.info("Copied the preReg test resource successfully");
		} catch (Exception e) {
			logger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	public static void copyTestResource(String resPath) {
		try {
			File source = new File(TestResources.getGlobalResourcePaths() + resPath);
			File destination = new File(TestResources.getGlobalResourcePaths() + "/"+TestResources.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			logger.info("Copied "+resPath+" the preReg test resource successfully to "+destination);
		} catch (Exception e) {
			logger.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	public static String getResourcePath() {
		return TestRunner.getGlobalResourcePath()+"/"+TestResources.resourceFolderName+"/";
	}
	public static String getGlobalResourcePaths() {
		return TestRunner.getGlobalResourcePath();
	}
	
	
	public static String checkRunType() {
		if (TestResources.class.getResource("TestResources.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}
	
	public static void removeOldMosipTempTestResource() {
		File authTestFile = new File(TestResources.getGlobalResourcePaths() + "/"+TestResources.resourceFolderName);
		if (authTestFile.exists())
			if (deleteDirectory(authTestFile))
				logger.info("Old "+TestResources.resourceFolderName+" folder successfully deleted!!");
			else
				logger.error("Old "+TestResources.resourceFolderName+" folder not deleted.");
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
	
}
