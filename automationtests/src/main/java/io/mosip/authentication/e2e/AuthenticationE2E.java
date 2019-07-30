package io.mosip.authentication.e2e;

import java.util.Map;

import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;

public class AuthenticationE2E {

	public static Map<String,String> performAuthE2E() {
		BaseTestCase.suiteSetup();
		MosipTestRunner.startTestRunner();
		return E2EReport.e2eReport;
	}
	
	public static void main(String arg[]) {
		performAuthE2E();
	}
}
