package io.mosip.authentication.partnerdemo.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.util.StringUtils;

/**
 * @author Manoj SP
 *
 */
@Configuration
public class BioProviderConfig {

	@Autowired
	private Environment env;
	
	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	public void init() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(BIR.class, new BIRDeserializer());
		mapper.registerModule(module);
		mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
	}

	@Bean
	public IBioApi providerApi() throws BiometricException {
		try {
			if (StringUtils.isNotEmpty(env.getProperty("bio.test.server.provider"))) {
				System.err.println(env.getProperty("bio.test.server.provider"));
				return (IBioApi) Class.forName(env.getProperty("bio.test.server.provider")).newInstance();
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new BiometricException("", "Unable to load provider", e);
		}
		return null;
	}
}
