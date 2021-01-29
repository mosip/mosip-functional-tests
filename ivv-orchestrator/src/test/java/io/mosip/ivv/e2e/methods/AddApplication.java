package io.mosip.ivv.e2e.methods;

import java.util.Random;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.ivv.orchestrator.TestResources;
import io.mosip.service.BaseTestCase;
import io.mosip.testscripts.SimplePostForAutoGenId;
import io.mosip.util.PreRegistrationLibrary;

public class AddApplication extends BaseTestCaseUtil implements StepInterface {
	private static final String PRE_REG_CREATE_PREREG_YML = "preReg/createPrereg/createPrereg.yml";
	Logger logger = Logger.getLogger(AddApplication.class);

	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		intialize();
		String fileName = PRE_REG_CREATE_PREREG_YML;
		SimplePostForAutoGenId post = new SimplePostForAutoGenId();
		Object[] casesList = post.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		post.idKeyName="preRegistrationId";
		logger.info("No. of TestCases in Yml file : " + casesList.length);
		try {
			for (Object object : testCaseList) {
				TestCaseDTO test = (TestCaseDTO) object;
				Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
				long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
				post.test(test);
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
				Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
			}
		} catch (AuthenticationTestException | AdminTestException e) {
			logger.error(e.getMessage());
		} 
		String prid=this.readProperty();
		this.pridsAndRids.put(prid, null);
		logger.info("preRegistrationId : " + prid);
	}
	
	private void intialize() {
		TestResources.copyPreRegTestResource();
		TestResources.copyTestResource("/preReg");
		TestResources.copyTestResource("/config");
		TestResources.copyTestResource("/kernel");
		//TestResources.copyTestResource("/resident");
		BaseTestCase.initialize();
    	PreRegistrationLibrary prliberary= new PreRegistrationLibrary();
    	prliberary.PreRegistrationResourceIntialize();
    	
	}
	
	
	protected String getEmailString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

}