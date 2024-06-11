package io.mosip.testrig.auth.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    public static String readProperty(String key) {
        Properties prop = new Properties();

        // The path to your properties file
        String propFileName = "C:/MOSIP-33327/mosip-functional-tests/AuthenticationUtil/src/main/resources/application.properties";

        try (InputStream inputStream = new FileInputStream(propFileName)) {
            // Load the properties file
            prop.load(inputStream);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(key);
    }
}
