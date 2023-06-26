package io.mosip.testrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.service.BaseTestCase;
import io.mosip.testscripts.BioAuth;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class HealthChecker implements Runnable {
	private static final Logger logger = Logger.getLogger(HealthChecker.class);
	public static boolean bTerminate = false;
	public static String propsHealthCheckURL = MosipTestRunner.getResourcePath() + "/" + "config/healthCheckEndpoint.properties";
	public static boolean signalTerminateExecution = false;
	public static Map<Object, Object> healthCheckFailureMapS = Collections.synchronizedMap(new HashMap<Object, Object>());

	public void run() {
		
		File file = new File(propsHealthCheckURL);
		FileReader fileReader = null;
		List<String> controllerPaths = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line;
		
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				String[] parts = line.trim().split("=");
				if (parts.length > 1) {
					controllerPaths.add(BaseTestCase.ApplnURI + parts[1]);
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
	
	public static String checkActuatorNoAuth(String url) {
		String urlAct = url + "/actuator/health";
		Response response =null;
		response = given().contentType(ContentType.JSON).get(urlAct);
		if(response != null && 	response.getStatusCode() == 200 ) {
			logger.info(response.getBody().asString());        	
			JSONObject jsonResponse = new JSONObject(response.getBody().asString());
			return jsonResponse.getString("status");
		}
		return "No Response";
	}
}