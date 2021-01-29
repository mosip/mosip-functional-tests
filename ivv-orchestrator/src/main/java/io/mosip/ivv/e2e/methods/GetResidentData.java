package io.mosip.ivv.e2e.methods;

import java.util.List;
import org.apache.log4j.Logger;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;

public class GetResidentData extends BaseTestCaseUtil implements StepInterface {
	Logger logger = Logger.getLogger(GetResidentData.class);

	@Override
	public void run() {
		cleanData();
		int nofResident=1;
		Boolean bAdult=false;
		Boolean bSkipGuardian=false;
		String gender=null;
		if (step.getParameters() == null || step.getParameters().isEmpty() ||step.getParameters().size()<4) {
			logger.warn("GetResidentData Arugemnt is  Missing : Please pass the argument from DSL sheet");
		} else {
			nofResident=Integer.parseInt(step.getParameters().get(0));
			bAdult = Boolean.parseBoolean(step.getParameters().get(1));
			bSkipGuardian = Boolean.parseBoolean(step.getParameters().get(2));
			gender = step.getParameters().get(3);
		}
		//false,true,any
		List<String> generateDResidentData = packetUtility.generateResidents(nofResident,bAdult,bSkipGuardian,gender);
	    for (String path : generateDResidentData) {
	    	residentTemplatePaths.put(path, null);
	    }
	}
	
	

	private void cleanData() {
		pridsAndRids.clear();
		uinReqIds.clear();
		residentTemplatePaths.clear();
		residentPathsPrid.clear();
		templatePacketPath.clear();
	}
}
