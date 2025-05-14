package io.mosip.testrig.apirig.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.icu.text.Transliterator;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;

public class Translator {

    static String IDlookupFile = "src/main/resource/config/lang-isocode-transid.csv";
    private static final Logger logger = Logger.getLogger(Translator.class);

    private static Map<String, String> langIsoCodeCache = null;

    public static void main(String[] args) {
        String text = "Mohandas Karamchand Ghandhi";
        logger.info("Text:" + text + ", Translated text: " + translate("heb", text));
    }

    static String getLanguageID(String langIsoCode) {
        if (langIsoCodeCache == null) {
            loadLanguageIDCache();
        }

        return langIsoCodeCache.getOrDefault(langIsoCode.toLowerCase(), "Any-Any");
    }

    private static synchronized void loadLanguageIDCache() {
        if (langIsoCodeCache != null) return; 

        langIsoCodeCache = new HashMap<>();
        String filename = BaseTestCase.getGlobalResourcePath() + "/" + "config/lang-isocode-transid.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rec = line.split(",");
                if (rec.length >= 3) {
                    String key = rec[0].trim().toLowerCase();
                    String value = rec[2].trim();
                    if (!value.isEmpty()) {
                        langIsoCodeCache.put(key, value);
                    }
                }
            }
            logger.info("Language ID cache loaded with " + langIsoCodeCache.size() + " entries.");
        } catch (IOException e) {
            logger.error("Error loading language ID CSV file: " + e.getMessage(), e);
        }
    }

    public static String translate(String toLanguageIsoCode, String text) {
        String lang_from_to = getLanguageID(toLanguageIsoCode);
        Transliterator toTrans = Transliterator.getInstance(lang_from_to);
        return toTrans.transliterate(text);
    }
}
