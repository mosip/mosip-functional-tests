package io.mosip.ivv.e2e.methods;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestException;
import io.mosip.admin.fw.util.TestCaseDTO;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.testscripts.PostWithPathParamsAndBody;

public class OldBookAppointment extends BaseTestCaseUtil implements StepInterface {
	private static final String PRE_REG_BOOK_APPOINTMENT_YML = "preReg/bookAppointment/bookAppointment.yml";
	Logger logger = Logger.getLogger(OldBookAppointment.class);

	@Override
	public void run() {
		String fileName = PRE_REG_BOOK_APPOINTMENT_YML;
		PostWithPathParamsAndBody bookAppointment = new PostWithPathParamsAndBody();
		Object[] casesList = bookAppointment.getYmlTestData(fileName);
		Object[] testCaseList = filterTestCases(casesList);
		bookAppointment.pathParams = "preRegistrationId";
		logger.info("No. of TestCases in Yml file : " + testCaseList.length);
		try {
			for (Object object : testCaseList) {
				TestCaseDTO test = (TestCaseDTO) object;
				Reporter.log("<b><u>"+test.getTestCaseName()+ "</u></b>");
				long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
				bookAppointment.test(test);
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