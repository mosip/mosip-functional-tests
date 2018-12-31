package io.mosip.preRegistration.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileLoader {
	/**
	 * @param pageName
	 * 			accept the name of the config properties file
	 * @return
	 * 			return config file object to read config file
	 */
	public static Properties configFileReaderObject(String pageName) {
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			 input = new FileInputStream("./src/test/resources/PreRegistration/"+pageName+".properties");
			//input = new FileInputStream("./"+pageName+".properties");
			prop.load(input);
		} 
		catch (FileNotFoundException e) {
			System.out.println("Provide correct name for the API config file");
			System.exit(0);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}
