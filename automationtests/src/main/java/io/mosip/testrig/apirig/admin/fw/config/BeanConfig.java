package io.mosip.testrig.apirig.admin.fw.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.crypto.jce.core.CryptoCore;

@Configuration
@Import({CryptoCore.class})
@ComponentScan(basePackages = {"io.mosip.testrig.apirig.admin.fw.util", "io.mosip.testrig.apirig.authentication.fw.dto"})
public class BeanConfig {

}
