package io.mosip.ivv.e2e.methods;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;

public class UpdateResidentWithGuardian extends BaseTestCaseUtil implements StepInterface{
	Logger logger = Logger.getLogger(UpdateResidentWithGuardian.class);
	@Override
	public void run() throws RigInternalError {
		residentPathGuardianRid= new LinkedHashMap<String, String>();
		for(String path:residentTemplatePaths.keySet()) {
			residentPathGuardianRid.put(path, packetUtility.updateResidentGuardian(path));
		}
		Reporter.log("<b><u>Checking Status Of Created Guardians</u></b>");
		CheckStatus checkStatus= new CheckStatus();
		checkStatus.tempPridAndRid=residentPathGuardianRid;
		checkStatus.run();
		
	}
	

}
