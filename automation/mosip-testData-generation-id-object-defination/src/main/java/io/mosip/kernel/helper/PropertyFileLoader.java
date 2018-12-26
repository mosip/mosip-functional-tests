package io.mosip.kernel.helper;

import java.io.FileInputStream;
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
	public Properties configFileReaderObject(String pageName) {
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("./src/main/resources/"+pageName+".properties");
			//input = new FileInputStream("./"+pageName+".properties");
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
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
