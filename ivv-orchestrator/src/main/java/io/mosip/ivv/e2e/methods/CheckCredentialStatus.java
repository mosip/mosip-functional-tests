package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.testng.Reporter;
import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testscripts.GetWithParam;

public class CheckCredentialStatus extends BaseTestCaseUtil implements StepInterface {
	private static final String check_status_YML = "preReg/credentialStatus/credentialStatus.yml";
	Logger logger = Logger.getLogger(CheckCredentialStatus.class);

    @SuppressWarnings("static-access")
	@Override
    public void run() {
    	String fileName = check_status_YML;
    	GetWithParam getWithPathParam= new GetWithParam();
    	Object[] casesList = getWithPathParam.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		
			boolean credentialIssued = false;
			try {
				for (Object object : testCaseList) {
					for(String requestid: this.uinReqIds.values()) {
						int counter=0;
					while(!credentialIssued && counter<Integer.parseInt(props.getProperty("loopCount"))) {
						counter++;
						try {
							logger.info("Waiting for 30 sec to get credential Issued");
							Thread.sleep(Long.parseLong(props.getProperty("waitTime")));
							TestCaseDTO test = (TestCaseDTO) object;
						test.setInput(test.getInput().replace("$requestId$", requestid));
						test.setOutput(test.getOutput().replace("$requestId$", requestid));
						Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
						
						long startTime = System.currentTimeMillis();
						logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
						getWithPathParam.test(test);
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
						Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
						credentialIssued = true;
						} catch (AuthenticationTestException | AdminTestException e) {
							logger.error("Failed at checking Credential status with error: " + e.getMessage());
						}
					}
					assertTrue(getWithPathParam.response.asString().contains("printing"), "Failed at credential issuance status check Response validation");
				}
			}
		} catch (InterruptedException e) {
			logger.error("Failed due to thread sleep: " + e.getMessage());
		}

	}

}
