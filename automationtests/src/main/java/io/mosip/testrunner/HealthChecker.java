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

import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.service.BaseTestCase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class HealthChecker implements Runnable {
	public static boolean bTerminate = false;
	public static String propsHealthCheckURL = MosipTestRunner.getResourcePath() + "/" + "config/healthCheckEndpoint.properties";
	public static boolean signalTerminateExecution = false;
	public static Map<Object, Object> healthCheckFailureMapS = Collections.synchronizedMap(new HashMap<Object, Object>());

	public void run() {
		
		File file = new File(propsHealthCheckURL);
		FileReader fr = null;
		List<String> controllerPaths = new ArrayList<String>();
		try {
			fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				String[] parts = line.trim().split("=");
				if (parts.length > 1) {
					controllerPaths.add(BaseTestCase.ApplnURI + parts[1]);
				}
			}
		
		    fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (bTerminate == false) {
			System.out.println("Checking Health");
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String checkActuatorNoAuth(String url) {
		String urlAct = url + "/actuator/health";
		Response response =null;
		response = given().contentType(ContentType.JSON).get(urlAct);
		if(response != null && 	response.getStatusCode() == 200 ) {
			System.out.println(response.getBody().asString());        	
			JSONObject jsonResponse = new JSONObject(response.getBody().asString());
			return jsonResponse.getString("status");
		}
		return "No Response";
	}
}