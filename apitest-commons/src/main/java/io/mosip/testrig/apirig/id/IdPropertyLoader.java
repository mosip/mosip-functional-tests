package io.mosip.testrig.apirig.id;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;

public class IdPropertyLoader {

	private static final Logger LOGGER = LogManager.getLogger(IdPropertyLoader.class);
	private static boolean loaded = false;

	private IdPropertyLoader() {
	}

	public static synchronized void loadAllProperties() {

		if (loaded) {
			LOGGER.info("Properties already loaded. Skipping reload.");
			return;
		}
		loadFromActuator();
		loadFromFile();
		loaded = true;
	}

	private static void loadFromActuator() {
		try {
			LOGGER.info("Fetching properties from actuator...");

			Map<String, String> actuatorProps = AdminTestUtil.getAllActuatorProperties(
					ConfigManager.getproperty("actuatorMasterDataEndpoint"), GlobalConstants.ACTUATOR_PROPERTY_SECTION);

			if (actuatorProps != null && !actuatorProps.isEmpty()) {
				ConfigManager.id_default_propertiesMap.putAll(actuatorProps);
				LOGGER.info("Loaded {} properties from actuator", actuatorProps.size());
			} else {
				LOGGER.warn("No actuator properties received");
			}

		} catch (Exception e) {
			LOGGER.warn("Actuator unavailable. Falling back to file properties.");
		}
	}

	private static void loadFromFile() {

		try (InputStream input = IdPropertyLoader.class.getClassLoader()
				.getResourceAsStream("config/id-default.properties")) {

			if (input == null) {
				LOGGER.error("id-default.properties not found!");
				return;
			}

			Properties props = new Properties();
			props.load(input);

			for (String key : props.stringPropertyNames()) {
				if (!ConfigManager.id_default_propertiesMap.containsKey(key)) {
					ConfigManager.id_default_propertiesMap.put(key, props.getProperty(key));
				}
			}

			LOGGER.info("Fallback properties loaded from file");

		} catch (Exception e) {
			LOGGER.error("Failed to load fallback properties", e);
		}
	}
}