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

public class UpdateUINDetail extends BaseTestCaseUtil implements StepInterface {
	private static final String UPDATE_DEMOPHRAPIC_DETAIL = "preReg/updateUINDetail/UpdateUIN.yml";
	Logger logger = Logger.getLogger(UpdateUINDetail.class);

    @Override
    public void run() {
    	String fileName = UPDATE_DEMOPHRAPIC_DETAIL;
    	PostWithBodyWithOtpGenerate postWithBodyWithOtpGenerate= new PostWithBodyWithOtpGenerate();
    	Object[] casesList = postWithBodyWithOtpGenerate.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		
				for (Object object : testCaseList) {
					for(String uin: BaseTestCaseUtil.uinReqIds.keySet()) {
						try {
						TestCaseDTO test = (TestCaseDTO) object;
						String input=test.getInput().replace("$UIN$", uin).replace("$UIN$", uin).replace("$UIN$", uin);
						JSONObject inputJson = new JSONObject(input);
						String idJsonValue=inputJson.get("identityJsonValue").toString();
						inputJson.remove("identityJsonValue");
						String encodedIdJson=encoder(idJsonValue);
						String actualInput=inputJson.toString().replace("$IDJSON$", encodedIdJson);
						test.setInput(actualInput);
						Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
						long startTime = System.currentTimeMillis();
						logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
						postWithBodyWithOtpGenerate.test(test);
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
						Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
						JSONObject res = new JSONObject(postWithBodyWithOtpGenerate.response.asString());
					JSONObject responseJson = new JSONObject(res.get("response").toString());
					for (String prid : pridsAndRids.keySet()) {
						pridsAndRids.put(prid, responseJson.get("registrationId").toString());
					}

				} catch (AuthenticationTestException | AdminTestException e) {
					logger.error("Failed at downloading card: " + e.getMessage());
					assertFalse(true, "Failed at downloading card");
				}
			}
		}

	}

}
