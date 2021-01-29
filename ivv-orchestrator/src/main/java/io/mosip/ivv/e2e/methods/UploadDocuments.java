package io.mosip.ivv.e2e.methods;

import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;

public class UploadDocuments extends BaseTestCaseUtil implements StepInterface {

	@Override
	public void run() throws RigInternalError {
		for (String resDataPath : residentPathsPrid.keySet()) {
			packetUtility.uploadDocuments(resDataPath, residentPathsPrid.get(resDataPath));
		}
		
	}
}
