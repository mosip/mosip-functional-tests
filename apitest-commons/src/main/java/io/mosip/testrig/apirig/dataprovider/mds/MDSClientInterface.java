package io.mosip.testrig.apirig.dataprovider.mds;

import java.util.List;

import io.mosip.testrig.apirig.dataprovider.models.ResidentModel;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSDevice;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSRCaptureModel;

public interface MDSClientInterface {
	public  void setProfile(String profile,int port,String contextKey) ;
	//Type ->"Finger", "Iris", "Face"
	public  List<MDSDevice> getRegDeviceInfo(String type) ;
	public  MDSRCaptureModel captureFromRegDevice(MDSDevice device, 
			MDSRCaptureModel rCaptureModel,
			String type,
			String bioSubType, int reqScore,String deviceSubId,int port,String contextKey,List<String> exceptionlist) ;
	
	List<MDSDevice> getRegDeviceInfo(String type, String contextKey);
	
}
