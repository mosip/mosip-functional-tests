package io.mosip.admin.fw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJava {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//find any 6 digit number
		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
		
		String message = "Dear FR OTP for VID XXXXXXXX65185914 is 333333 and is valid for 3 minutes. (Generated on 21-03-2023 at 16:03:54 Hrs)\r\n"
				+ "عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)\r\n"
				+ "Cher $name_fra, OTP pour VID XXXXXXXX65185914 est 111111 et est valide pour 3 minutes. (Généré le 21-03-2023 à 16:03:54 Hrs)";
		
		String message2 = "Dear FR OTP for UIN XXXXXXXX02 is 111111 and is valid for 3 minutes. (Generated on 16-03-2023 at 15:43:39 Hrs)\r\n"
				+ "\r\n"
				+ "عزيزي $ name OTP لـ $ idvidType $ idvid هو $ 101010 وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)\r\n"
				+ "\r\n"
				+ "Cher $name_fra, OTP pour UIN XXXXXXXX02 est 123456 et est valide pour 3 minutes. (Généré le 16-03-2023 à 15:43:39 Hrs)";
		
		if(message!=null) {
		    Matcher mMatcher = mPattern.matcher(message);
		    if(mMatcher.find()) {
		        String otp = mMatcher.group(0);
		        otp = otp.trim();
		        System.out.println("Final OTP: "+ otp);
		    }else {
		        //something went wrong
		    	System.out.println("Failed to extract the OTP!! ");
		    }
		}
	}

}
