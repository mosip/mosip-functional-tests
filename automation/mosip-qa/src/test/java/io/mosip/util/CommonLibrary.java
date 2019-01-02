package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;


public class CommonLibrary {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);

	public static void configFileWriter(String keys, String module, String config, Integer numberOfRuns,String positiveOrNegative)
			throws ParseException {
		try {
			switch (module) {
			case "ida":
				module = "IDA";
				break;
			case "kernel":
				module = "kernel";
				break;
			case "preReg":
				module = "preReg";
				break;
			}
			Properties properties = new Properties();
			properties.setProperty("NumberofOutputJson", numberOfRuns.toString());
			properties.setProperty("required.fields", keys);
			properties.setProperty("json.output.file.name", positiveOrNegative+module);
			JSONObject object = (JSONObject) new JSONParser().parse(config);
			for (Object key : object.keySet()) {
				properties.setProperty(key.toString(), object.get(key).toString());
			}

			File file = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\" + module+"\\Config.properties");

			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, "Config Properties");
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void scenarioFileCreator(String fileName,String module,String testType,String ouputFile) throws IOException, ParseException {
		String input = "";
		List<String> scenario = new ArrayList<String>();
		String filepath=System.getProperty("user.dir") + "\\src\\test\\resources\\" + module+"\\"+fileName;
		String configPaths = System.getProperty("user.dir") + "\\src\\test\\resources\\" +module;
		File folder = new File(configPaths);
		File[] listOfFolders = folder.listFiles();
		Map<String,String> jiraID= new HashMap<String,String>();
		int id=1000;
		for(int k=0;k<listOfFolders.length;k++) {
			jiraID.put(listOfFolders[k].getName(), "MOS-"+id);
			id++;
		}
		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(filepath));
		if(testType.equals("smoke")) {
			System.out.println("in Smoke---------------------------------------------------------------------------------------------->");
		input += "{";
		input += "\"testType\":" + "\"smoke\",";
		//input += "\"jiraId\":" + "\"MOS-1000\",";
		for(int k=0;k<listOfFolders.length;k++) {
		if(listOfFolders[k].getName().contains("smoke")) {
			input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
			input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
		}
		}
		/*for (Field f : fields) {
			input += '"' + f.getName() + '"' + ":" + "\"valid\",";
		}*/
		for(Object obj: requestKeys.keySet()) {
			input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
		}
		input += "\"status\":" + "\"\"";
		input += "}";
		scenario.add(input);
		System.out.println("Scenario is ---------------------------------------------------------------------->"+scenario);
	}
		else if(testType.equals("regression")) {
		input = "";
		int[] permutationValidInvalid = new int[requestKeys.size()];
		permutationValidInvalid[0] = 1;
		for (Integer data : permutationValidInvalid) {
			input += data;
		}
		List<String> validInvalid = permutation.pack.Permutation.permutation(input);
		System.out.println("--------------------------------->"+validInvalid);
		input = "";
		for (String validInv : validInvalid) {
			input += "{";
			input += "\"testType\":" + "\"regression\",";
			//input += "\"jiraId\":" + "\"MOS-1000\",";
			int i = 0;
			/*for (Field f : fields) {
				if (validInv.charAt(i) == '0')
					input += '"' + f.getName() + '"' + ":" + "\"valid\"" + ",";
				if (validInv.charAt(i) == '1')
					input += '"' + f.getName() + '"' + ":" + "\"invalid\"" + ",";
				i++;
			}
			*/
			for(Object obj: requestKeys.keySet()) {
				if (validInv.charAt(i) == '0')
					input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
				if (validInv.charAt(i) == '1') {
					input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
					for(int k=0;k<listOfFolders.length;k++) {
						if(listOfFolders[k].getName().toLowerCase().contains("_"+obj.toString().toLowerCase())) {
							input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
							input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
							id++;
						}
				}
				}
				i++;
		
			}
			
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			input = "";
		}
		}
		else {
			System.out.println("in Smoke---------------------------------------------------------------------------------------------->");
			input += "{";
			input += "\"testType\":" + "\"smoke\",";
			//input += "\"jiraId\":" + "\"MOS-1000\",";
			for(int k=0;k<listOfFolders.length;k++) {
			if(listOfFolders[k].getName().contains("smoke")) {
				input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
				input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
			}
			}
			/*for (Field f : fields) {
				input += '"' + f.getName() + '"' + ":" + "\"valid\",";
			}*/
			for(Object obj: requestKeys.keySet()) {
				input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
			}
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			System.out.println("Scenario is ---------------------------------------------------------------------->"+scenario);
			input = "";
			int[] permutationValidInvalid = new int[requestKeys.size()];
			permutationValidInvalid[0] = 1;
			for (Integer data : permutationValidInvalid) {
				input += data;
			}
			List<String> validInvalid = permutation.pack.Permutation.permutation(input);
			System.out.println("--------------------------------->"+validInvalid);
			input = "";
			for (String validInv : validInvalid) {
				input += "{";
				input += "\"testType\":" + "\"regression\",";
				//input += "\"jiraId\":" + "\"MOS-1000\",";
				int i = 0;
				/*for (Field f : fields) {
					if (validInv.charAt(i) == '0')
						input += '"' + f.getName() + '"' + ":" + "\"valid\"" + ",";
					if (validInv.charAt(i) == '1')
						input += '"' + f.getName() + '"' + ":" + "\"invalid\"" + ",";
					i++;
				}
				*/
				for(Object obj: requestKeys.keySet()) {
					if (validInv.charAt(i) == '0')
						input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
					if (validInv.charAt(i) == '1') {
						input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
						for(int k=0;k<listOfFolders.length;k++) {
							if(listOfFolders[k].getName().toLowerCase().contains("_"+obj.toString().toLowerCase())) {
								input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
								input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
								id++;
							}
					}
					}
					i++;
			
				}
				
				input += "\"status\":" + "\"\"";
				input += "}";
				scenario.add(input);
				input = "";
			}
		}
		
		
		
		//System.out.println(scenario);
		String configpath=System.getProperty("user.dir") + "\\src\\test\\resources\\" + module+"\\"+ouputFile;
		File json = new File(configpath);
		FileWriter fw = new FileWriter(json);
		fw.write(scenario.toString());
		fw.flush();
		fw.close();

	}
	
	
	public Response POST_REQUEST(String url, Object body, String contentHeader, String acceptHeader) {

		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST
	

    /**
    * REST ASSURED GET request method
    *
    * @param url
    *            destination of the request
    * @return Response object that has the REST response
    */
    public Response GET_REQUEST_queryParam(String url, String contentHeader, String acceptHeader, String urls) {
          logger.info("REST-ASSURED: Sending a GET request to " + url);
          Response getResponse = given().relaxedHTTPSValidation()
                      .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
          // log then response
          logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
          logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
          return getResponse;
    } // end GET_REQUEST
    
    public static void backUpFiles(String source, String destination) {
    	//String time = java.time.LocalDate.now().toString()+"--"+java.time.LocalTime.now().toString();
    	 Calendar cal = Calendar.getInstance();
         cal.setTime(Date.from(Instant.now()));
  
    	String result = String.format(
                  "%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
    //System.out.println(System.getProperty("APPDATA"));
		String filePath=System.getenv("APPDATA")+"\\MosipUtil\\UtilFiles\\"+destination+"\\"+result;
		File sourceFolder = new File(source);
		File dest = new File(filePath);
		try {
		FileUtils.copyDirectory(sourceFolder,dest);
		logger.info("Please Check Your App Data in C drive to get access to the util files");
		}catch(IOException e) {
			logger.info("Check %APPDATA%");
		}
    }

}

