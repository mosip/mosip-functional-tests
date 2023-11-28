package io.mosip.testrig.apirig.admin.fw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.mifmif.common.regex.Generex;

public class TestJava {
    private static final Logger LOGGER = Logger.getLogger(TestJava.class);

    public static void main(String[] args) {
    	String generatedregexString = "";
    	try {
//    		generatedregexString =genStringAsperRegex("^[+]([0-9]{3})([0-9]{9})$");
//    		System.out.println(generatedregexString);
    		generatedregexString =genStringAsperRegex("^[+]*([0-9]{1})([0-9]{9})$");
    		System.out.println(generatedregexString);
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public static String genStringAsperRegex(String regex) throws Exception {
		if (Generex.isValidPattern(regex)) {

			Generex generex = new Generex(regex);

			String randomStr = generex.random();
			// Generate all String that matches the given Regex.
			boolean bFound = false;
			do {
				bFound = false;
				if (randomStr.startsWith("^")) {
					int idx = randomStr.indexOf("^");
					randomStr = randomStr.substring(idx + 1);
					bFound = true;
				}
				if (randomStr.endsWith("$")) {
					int idx = randomStr.indexOf("$");
					randomStr = randomStr.substring(0, idx);
					bFound = true;
				}
			} while (bFound);
			return randomStr;
		}
		throw new Exception("invalid regex");

	}

}
