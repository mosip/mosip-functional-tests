package io.mosip.testrig.apirig.utils;

import org.apache.log4j.Logger;
import com.mifmif.common.regex.Generex;

public class TestJava {
	private static final Logger LOGGER = Logger.getLogger(TestJava.class);

	public static void main(String[] args) {
		String generatedregexString = "";
		try {
//    		generatedregexString =genStringAsperRegex("^[+]([0-9]{3})([0-9]{9})$");
//    		System.out.println(generatedregexString);
//    		generatedregexString =genStringAsperRegex("^\\+855[1-9]\\d{7,8}$");
			generatedregexString = genStringAsperRegex("^[\\ក-\\៿\\᧠-\\᧿\\ᨀ-\\᪟\\ ]{1,30}$");
//    		generatedregexString =genStringAsperRegex("^[\u1780-\u17FF\u19E0-\u19FF\u1A00-\u1A9F\u0020]{1,30}$");
//    		                                           ^[\u1780-\u17FF\u19E0-\u19FF\u1A00-\u1A9F\u0020]{1,30}$
//    		generatedregexString =genStringAsperRegex("^(?=.{8,20}$)(?=.*[A-Za-z])(?=.*\\d).*$");

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
