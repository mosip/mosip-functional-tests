
package io.mosip.testrig.apirig.kernel.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ibm.icu.text.Transliterator;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class Translator {
	static String IDlookupFile = "src/main/resource/config/lang-isocode-transid.csv";
	private static final Logger logger = Logger.getLogger(Translator.class);
	public static void main(String[] args) {
		String text = "Mohandas Karamchand Ghandhi"; // Translated text: Hallo Welt!
		logger.info("Text:" + text + ",Translated text: " + translate("heb", text));
	}

	static String getLanguageID(String langIsoCode) {

		String v = "Any-Any";

		try {
			//String filename = "D:\\Mosip_Automation_Test\\Mosip_Functional_Test_Develop\\mosip-functional-tests\\automationtests\\src\\main\\resources\\config\\lang-isocode-transid.csv";
			String filename = MosipTestRunner.getGlobalResourcePath() + "/"+"config/lang-isocode-transid.csv";
			//CSVHelper csv = new CSVHelper(IDlookupFile);
			CSVHelper csv = new CSVHelper(filename);
			String[] rec;
			csv.open();
			while ((rec = csv.readRecord()) != null) {
				if (rec[0].toLowerCase().equals(langIsoCode.toLowerCase())) {
					String val = rec[2].trim();
					if (val.equals(""))
						v = val;
					break;
				}
			}
			csv.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return v;
	}

	public static String translate(String toLanguageIsoCode, String text) {
		
		String lang_from_to = getLanguageID(toLanguageIsoCode);
	
		Transliterator toTrans = Transliterator.getInstance(lang_from_to);
		return toTrans.transliterate(text);
	}
}
