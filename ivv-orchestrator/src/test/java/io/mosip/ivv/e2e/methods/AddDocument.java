package io.mosip.ivv.e2e.methods;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testscripts.PostWithFormPathParamAndFile;

public class AddDocument extends BaseTestCaseUtil implements StepInterface {
	private static final String PRE_REG_UPLOAD_DOCUMENT_YML = "preReg/uploadDocument/uploadDocument.yml";
	Logger logger = Logger.getLogger(AddDocument.class);

    @Override
    public void run() {
    	String fileName = PRE_REG_UPLOAD_DOCUMENT_YML;
    	PostWithFormPathParamAndFile postWithForm= new PostWithFormPathParamAndFile();
    	Object[] casesList = postWithForm.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		try {
			for (Object object : testCaseList) {
				TestCaseDTO test = (TestCaseDTO) object;
				Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
				long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
				
				postWithForm.test(test);
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
				Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
			}
		} catch (AuthenticationTestException | AdminTestException e) {
			logger.error(e.getMessage());
		}
    }
}
