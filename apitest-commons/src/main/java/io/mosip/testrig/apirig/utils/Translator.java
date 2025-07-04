package io.mosip.testrig.apirig.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.icu.text.Transliterator;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;

public class Translator {

    private static final Logger logger = Logger.getLogger(Translator.class);
    
    // Map is used to store language codes and their matching translation IDs
    // Avoid reading the file again and again
    private static Map<String, String> langIsoCodeCache = null;

    public static void main(String[] args) {
        String text = "Mohandas Karamchand Ghandhi";
        logger.info("Text:" + text + ", Translated text: " + translate("heb", text));
    }

    static String getLanguageID(String langIsoCode) {
    	// If the cache is empty, load the data from the file
        if (langIsoCodeCache == null) {
            loadLanguageIDCache();
        }
        
        // Return the translation ID for the given language code.
        // If not found, return a default value.
        return langIsoCodeCache.getOrDefault(langIsoCode.toLowerCase(), "Any-Any");
    }

    private static synchronized void loadLanguageIDCache() {
    	// If the cache is already loaded, do nothing
        if (langIsoCodeCache != null) return; 

        langIsoCodeCache = new HashMap<>();
        String filename = BaseTestCase.getGlobalResourcePath() + "/config/lang-isocode-transid.csv";
        
        File file = new File(filename);
        if (!file.exists()) {
        logger.error("Translation config file not found at path: " + filename);
        return;
        }
        
        // Read data from the CSV file and put it into the cache
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
            
            // This message will be shown only once in the console when the cache is loaded for the first time.
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
