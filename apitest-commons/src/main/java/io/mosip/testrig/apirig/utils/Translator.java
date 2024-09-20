
package io.mosip.testrig.apirig.utils;

import java.io.BufferedReader;
import java.io.FileReader;
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

		String value = "Any-Any";
		
		String filename = MosipTestRunner.getGlobalResourcePath() + "/"+"config/lang-isocode-transid.csv";
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rec = line.split(",");
                
                // Processing each record
                if (rec.length >= 3 && rec[0].toLowerCase().equals(langIsoCode.toLowerCase())) {
                	value = rec[2].trim();
                    if (!value.equals("")) {
                        System.out.println("Value found for translation: " + value);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return value;
	}

	public static String translate(String toLanguageIsoCode, String text) {
		
		String lang_from_to = getLanguageID(toLanguageIsoCode);
	
		Transliterator toTrans = Transliterator.getInstance(lang_from_to);
		return toTrans.transliterate(text);
	}
}
