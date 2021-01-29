package io.mosip.ivv.e2e.methods;

import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;

public class GenerateAndUploadPacket extends BaseTestCaseUtil implements StepInterface {

	@Override
	public void run() throws RigInternalError {
		for (String resDataPath : residentTemplatePaths.keySet()) {
			String rid = packetUtility.generateAndUploadPacket(residentPathsPrid.get(resDataPath),
					residentTemplatePaths.get(resDataPath));
			pridsAndRids.put(residentPathsPrid.get(resDataPath), rid);
		}

	}

}
