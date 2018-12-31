package io.mosip.kernel.utility;

import java.util.Properties;

import com.ibm.icu.text.Transliterator;

public class Transliteration {
	public static String languageConverter(String inputString, Properties prop) {

        final String language_translation_code = prop.getProperty("language.code");

        Transliterator input = Transliterator.getInstance(language_translation_code);
        String transliteratedString = input.transliterate(inputString);

        return transliteratedString;
    }

}
