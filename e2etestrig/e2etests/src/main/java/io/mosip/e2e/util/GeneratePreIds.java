package io.mosip.e2e.util;

import org.json.simple.JSONObject;

import io.mosip.test.GetPreRegistartion;
/**
 * 
 * @author M1047227
 *
 */
public class GeneratePreIds {
	/**
	 * 
	 * @return
	 * Method to generate a json object with 4 preIds
	 */
	public JSONObject getPreids() {
		JSONObject preIds;
		GetPreRegistartion getPreRegistration=new GetPreRegistartion();
		try {
			preIds=getPreRegistration.getPreIdChild("childRequest");
			preIds=getPreRegistration.getPreIdAdult("adultRequest");
			return preIds;
		}catch(NullPointerException | IndexOutOfBoundsException e) {
			 e.printStackTrace();
			 return null;
		}
	}

}
