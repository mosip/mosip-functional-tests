package io.mosip.testrunner;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.service.BaseTestCase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class HealthChecker implements Runnable {
	private static final Logger logger = Logger.getLogger(HealthChecker.class);
	public static boolean bTerminate = false;
	public static String propsHealthCheckURL = MosipTestRunner.getResourcePath() + File.separator
			+ "config/healthCheckEndpoint.properties";
	public static boolean signalTerminateExecution = false;
	public static Map<Object, Object> healthCheckFailureMapS = Collections
			.synchronizedMap(new HashMap<Object, Object>());
	private String currentRunningModule = "";

	public void setCurrentRunningModule(String currentModule) {
		currentRunningModule = currentModule;
	}

	public void run() {

		File file = new File(propsHealthCheckURL);
		FileReader fileReader = null;
		List<String> controllerPaths = new ArrayList<>();
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equals("") || line.trim().startsWith("#"))
					continue;
				String[] parts = line.trim().split("=");
				if (parts.length > 1) {
					if (parts[0].contains(currentRunningModule)) 
					// only add health check required for the current running module
					if (parts[0].contains(currentRunningModule)) {
						// If the actuator link contains esignet add to the list only if esignet is deployed
						if (parts[1].contains(GlobalConstants.ESIGNET) && (!ConfigManager.IseSignetDeployed()))
							continue;
						controllerPaths.add(BaseTestCase.ApplnURI + parts[1]);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		} finally {
			AdminTestUtil.closeBufferedReader(bufferedReader);
			AdminTestUtil.closeFileReader(fileReader);
		}

		while (bTerminate == false) {
			logger.info("Checking Health");
			boolean isAllServicesUp = true;
			for (int i = 0; i < controllerPaths.size(); i++) {
				String serviceStatus = checkActuatorNoAuth(controllerPaths.get(i));

				if (serviceStatus.equalsIgnoreCase("UP") == false) {
					isAllServicesUp = false;
					healthCheckFailureMapS.put(controllerPaths.get(i), serviceStatus);
				}

			}
			if (isAllServicesUp == false) {
				signalTerminateExecution = true;
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				logger.error(e.getStackTrace());
				Thread.currentThread().interrupt();
			}
		}
	}

	public static String checkActuatorNoAuth(String actuatorURL) {
		Response response = null;
		response = given().contentType(ContentType.JSON).get(actuatorURL);
		if (response != null && response.getStatusCode() == 200) {
			logger.info(response.getBody().asString());
			JSONObject jsonResponse = new JSONObject(response.getBody().asString());
			return jsonResponse.getString("status");
		}
		return "No Response";
	}
}