package io.mosip.testrig.apirig.admin.fw.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.testrig.auth.util.CryptoCoreUtil;
import io.mosip.testrig.auth.util.KeyMgrUtility;

@Configuration
@Import({CryptoCore.class})
@ComponentScan(basePackages = {"io.mosip.testrig.auth.util", "io.mosip.testrig.apirig.admin.fw.util"})
public class BeanConfig {

}
