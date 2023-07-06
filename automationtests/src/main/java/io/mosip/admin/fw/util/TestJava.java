package io.mosip.admin.fw.util;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

public class TestJava {
	private static final Logger lOGGER = Logger.getLogger(TestJava.class);
	private static final SecureRandom secureRandom = new SecureRandom();
	public static void main(String[] args) {
		int num = 1011111;
		
		String transactionID = (num + RandomStringUtils.randomNumeric(8)).substring(0, 10);
		lOGGER.info(transactionID);

		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
		
		String message = "Dear FR OTP for VID XXXXXXXX65185914 is 333333 and is valid for 3 minutes. (Generated on 21-03-2023 at 16:03:54 Hrs)\r\n"
				+ "عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)\r\n"
				+ "Cher $name_fra, OTP pour VID XXXXXXXX65185914 est 111111 et est valide pour 3 minutes. (Généré le 21-03-2023 à 16:03:54 Hrs)";
		
		message = "Dear TEST_FULLNAMEeng OTP for UIN XXXXXXXX98 is 523478 and is valid for 3 minutes. (Generated on 22-05-2023 at 11:40:11 Hrs) عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs) UIN XXXXXXXX98 ಗಾಗಿ ಆತ್ಮೀಯ TEST_FULLNAMEkan OTP 523478 ಆಗಿದೆ ಮತ್ತು ಇದು 3 ನಿಮಿಷಗಳವರೆಗೆ ಮಾನ್ಯವಾಗಿರುತ್ತದೆ. (22-05-2023 ದಂದು 11:40:11 ಗಂಟೆಗೆ ರಚಿಸಲಾಗಿದೆ)";
		
		if(message!=null) {
		    Matcher mMatcher = mPattern.matcher(message);
		    if(mMatcher.find()) {
		        String otp = mMatcher.group(0);
		        otp = otp.trim();
		        lOGGER.info("Final OTP: "+ otp);
		    }else {
		    	lOGGER.info("Failed to extract the OTP!! ");
		    }
		}
	}
	public static String generateRandomAlphanumericString(int length)  {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        String randomString = new String(bytes);
        return randomString.replaceAll("[^0-9a-zA-Z]", "");
    }
	
	 public static String generateRandomString(int length) {
	        byte[] bytes = new byte[length];
	        secureRandom.nextBytes(bytes);
	        String randomString = new String(bytes);
	        return randomString;
	    }
	 
	 public static String generateRandomNumericString(int length) {
	        byte[] bytes = new byte[length];
	        secureRandom.nextBytes(bytes);
	        String randomString = new String(bytes);
	        return randomString.replaceAll("[^0-9]", "");
	    }

}
