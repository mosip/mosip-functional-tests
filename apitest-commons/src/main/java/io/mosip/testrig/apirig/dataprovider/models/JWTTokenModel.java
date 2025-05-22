package io.mosip.testrig.apirig.dataprovider.models;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
@Data
public class JWTTokenModel {
	private static final Logger logger = LoggerFactory.getLogger(JWTTokenModel.class);
	//Map<String, Object> jsonHeader; // = new HashMap<String, Object>();
	String jwtPayload;
	String jwtSign;
	JSONObject jwtHeader;
	public static final String BIOMETRIC_SEPERATOR = "(?<=\\.)(.*)(?=\\.)";
	
	public JWTTokenModel(String jwtToken) {
		logger.info(jwtToken);
		 java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
         String[] parts = jwtToken.split("\\."); // split out the "parts" (header, payload and signature)

         String headerJson = new String(decoder.decode(parts[0]));
         jwtPayload= new String(decoder.decode(parts[1]));
         
        // jwtPayload = new JSONObject(payloadJson);
//         String signatureJson = new String(decoder.decode(parts[2]));
         jwtHeader = new JSONObject(headerJson);
         //jwtSign = signatureJson;
        
        Pattern pattern = Pattern.compile(BIOMETRIC_SEPERATOR);
		Matcher matcher = pattern.matcher(jwtToken);
		if(matcher.find()) {
			//returns header..signature
			jwtSign= jwtToken.replace(matcher.group(1),"");
		}
        //JWTTokenHeader(headerJson);
	}
	

	 @Override
	 public String toString() {
	      return org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString(this);
	  }
	 
	 public static void main(String[] args) {
		 String jwtToken = "";
		 JWTTokenModel model = new JWTTokenModel(jwtToken);
		 logger.info(model.toString());
	 }
}
