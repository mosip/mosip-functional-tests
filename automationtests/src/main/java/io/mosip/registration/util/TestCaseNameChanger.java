package io.mosip.registration.util;


import java.io.File;

/**
 * @author Arjun chandramohan
 *
 */
public class TestCaseNameChanger {
	/**
	 * this method the change the testcase name for api
	 * 
	 * @param provide
	 *            the module and service name
	 */
	public static void main(String[] arg) {
		String moduleName = "regClient";
		String serviceName = "getPreRegistrationIds";
		
		// if parent service name is there please provide it
		String parentServiceName = "PreRegistrationDataSyncService";

		//String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator + serviceName;
		
		// if parent service is there use this as path
		String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator+ "Registration"+ File.separator+parentServiceName + File.separator + serviceName;
		File file = new File(path);
		File[] listOfFolders = file.listFiles();

		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				File oldName = new File(path + file.separator + listOfFolders[j].getName());
				File newName = new File(
						path + file.separator + moduleName + "_" + parentServiceName + "_" + listOfFolders[j].getName());
				boolean isFileRenamed = oldName.renameTo(newName);

				if (isFileRenamed)
					System.out.println("File has been renamed");
				else
					System.out.println("Error renaming the file");
			}

		}
	}
}
