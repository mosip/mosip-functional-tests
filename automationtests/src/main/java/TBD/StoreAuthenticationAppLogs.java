package TBD;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Class to store applciation log to track failure scenario
 * 
 * @author Vignesh
 *
 */
public class StoreAuthenticationAppLogs {
	
	private static final Logger storeAuthAppLogger = Logger.getLogger(StoreAuthenticationAppLogs.class);
	
	/**
	 * Store applciation log
	 * 
	 * @param serviceName
	 * @param logFileName
	 * @param filePath
	 */
	//kubectl cp default/authentication-service-58789b6457-9bvt2:logs/id-auth.log /ida-logs-07-02
	public static void storeApplicationLog(String serviceName, String logFileName, File filePath) {
		try {
			String pathExp = filePath.getAbsolutePath().replaceAll("[a-zA-Z]\\:", "");
			String[] cmdarray = new String[] { "kubectl", "cp", "default/" + serviceName + ":logs/" + logFileName,
					pathExp };
			Process process = Runtime.getRuntime().exec(cmdarray);
			process.waitFor();
		} catch (InterruptedException | IOException e) {
			storeAuthAppLogger.error("Exception occured in storing the log " + e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

}
