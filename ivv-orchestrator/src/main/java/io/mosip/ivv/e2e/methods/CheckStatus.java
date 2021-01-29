package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testscripts.SimplePost;

public class CheckStatus extends BaseTestCaseUtil implements StepInterface {
	private static final String check_status_YML = "preReg/checkStatus/checkstatus.yml";
	Logger logger = Logger.getLogger(CheckStatus.class);
	public HashMap<String, String> tempPridAndRid = null;

	@Override
    public void run() {
    	//pridsAndRids.put("54253173891651", "10002100741000220210113045712");
    	if(tempPridAndRid ==null)
    		tempPridAndRid =pridsAndRids;
    	String fileName = check_status_YML;
    	SimplePost postScript= new SimplePost();
    	Object[] casesList = postScript.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		
			boolean packetProcessed = false;
			try {
				for (Object object : testCaseList) {
					for(String rid: this.tempPridAndRid.values()) {
						int counter=0;
					while(!packetProcessed && counter<Integer.parseInt(props.getProperty("loopCount"))) {
						counter++;
						try {
							logger.info("Waiting for 30 sec to get packet procesed");
							Thread.sleep(Long.parseLong(props.getProperty("waitTime")));
							TestCaseDTO test = (TestCaseDTO) object;
						test.setInput(test.getInput().replace("$RID$", rid));
						Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
						long startTime = System.currentTimeMillis();
						logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
						postScript.test(test);
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
						Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
							// packetProcessed = true; //this is to use when u don't want to wait for packet
							// process
						} catch (AuthenticationTestException | AdminTestException e) {
							logger.error("Failed at checking Packet status with error: " + e.getMessage());
							packetProcessed = true;
						}
					}
					assertTrue(postScript.response.asString().contains("PROCESSED"), "Failed at credential issuance status check Response validation");
				}
			}
		} catch (InterruptedException e) {
			logger.error("Failed due to thread sleep: " + e.getMessage());
		}

	}
    

}
