package io.mosip.testrig.authentication.demo.service;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;
import io.mosip.testrig.authentication.demo.service.config.SwaggerConfig;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import(value = {  TemplateManagerImpl.class, VelocityEngine.class, SwaggerConfig.class, CryptoCore.class})
public class PartnerDemoApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PartnerDemoApplication.class, args);
	}

}
