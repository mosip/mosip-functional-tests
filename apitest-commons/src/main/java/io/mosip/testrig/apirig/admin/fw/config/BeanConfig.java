package io.mosip.testrig.apirig.admin.fw.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.mosip.testrig.apirig.encrypt.util.CryptoCore;


@Configuration
@Import({CryptoCore.class})
@ComponentScan(basePackages = { "io.mosip.testrig.apirig", "io.mosip.testrig.dslrig"})
public class BeanConfig {
	int i = 0;
}
