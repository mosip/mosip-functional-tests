package io.mosip.testrig.apirig.testrunner;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.SlackChannelIntegration;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class HealthChecker implements Runnable {
	
	public HealthChecker() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	private static final Logger logger = Logger.getLogger(HealthChecker.class);
	public static boolean bTerminate = false;
	public static String propsHealthCheckURL = BaseTestCase.getGlobalResourcePath() + "/" + "config/healthCheckEndpoint.properties";
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
					// only add health check required for the current running module
					if (parts[0].contains(currentRunningModule)) {
						if (parts[1].contains(GlobalConstants.ESIGNET)
								&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.ESIGNET)))
							continue;
						if (parts[1].contains(GlobalConstants.RESIDENT)
								&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.RESIDENT)))
							continue;
						if (parts[1].contains(GlobalConstants.RID_GENERATOR)
								&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.RID_GENERATOR)))
							continue;
//						if (ConfigManager.isMosipIDIntegrated() == false
//								&& parts[1].contains(GlobalConstants.ESIGNET) == false) {
//							continue;
//						}
						controllerPaths.add(BaseTestCase.ApplnURI + parts[1]);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("On ").append(ConfigManager.getTargetEnvName())
							.append(" Health check failed for this end point -- ").append(controllerPaths.get(i));
//					Report to slack. If slack integration is done
					SlackChannelIntegration.sendMessageToSlack(stringBuilder.toString());

					GlobalMethods.reportServerError(" Health check failed " + controllerPaths.get(i), serviceStatus);
				}

			}
			if (isAllServicesUp == false) {
				signalTerminateExecution = true;
			}
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}

	public static String checkActuatorNoAuth(String actuatorURL) {
		Response response = null;
		actuatorURL = GlobalMethods.addToServerEndPointMap(actuatorURL);
		response = given().contentType(ContentType.JSON).get(actuatorURL);
		if (response != null && response.getStatusCode() == 200) {
			logger.info(response.getBody().asString());
			JSONObject jsonResponse = new JSONObject(response.getBody().asString());
			return jsonResponse.getString("status");
		}
		return "No Response";
	}
}