package io.mosip.testrig.apirig.dataprovider.models.mds;

import lombok.Data;

@Data
public class MDSDataModel {
	
	byte[] faceISO;
	byte[] irisLeftISO;
	byte[] irisRightISO;
	byte[][] fingersISO;
	
	public MDSDataModel() {
		faceISO = null;
		irisLeftISO = null;
		irisRightISO = null;
		fingersISO = new byte[10][];
	}
}
