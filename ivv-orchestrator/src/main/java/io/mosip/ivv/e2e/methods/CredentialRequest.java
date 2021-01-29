package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertFalse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testscripts.PostWithBodyWithOtpGenerate;

public class CredentialRequest  extends BaseTestCaseUtil implements StepInterface {
	private static final String CredentialIssue_YML = "preReg/credentialIssue/credentialIssue.yml";
	Logger logger = Logger.getLogger(CredentialRequest.class);

    @SuppressWarnings("static-access")
	@Override
    public void run() {
    	try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	String fileName = CredentialIssue_YML;
    	PostWithBodyWithOtpGenerate postWithOtp= new PostWithBodyWithOtpGenerate();
    	Object[] casesList = postWithOtp.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		try {
			for (Object object : testCaseList) {
				for(String uin: this.uinReqIds.keySet()) {
				TestCaseDTO test = (TestCaseDTO) object;
				test.setInput(test.getInput().replace("$UIN$", uin).replace("$UIN$", uin));
				test.setOutput(test.getOutput().replace("$UIN$", uin));
				Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
				
				long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
				postWithOtp.test(test);
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
				Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
				JSONObject response = new JSONObject(postWithOtp.response.asString());
				if(!response.get("response").toString().equals("null"))
	    		{
					JSONObject responseJson = new JSONObject(response.get("response").toString());
						this.uinReqIds.put(uin, responseJson.get("requestId").toString());
					}
				}
			}
		} catch (AuthenticationTestException | AdminTestException e) {
			logger.error(e.getMessage());
			assertFalse(true, "Failed at credential issuance Response validation");
		}
	}

}
