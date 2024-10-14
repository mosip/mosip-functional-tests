/*
 * package io.mosip.testrig.apirig.admin.fw.config;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.ComponentScan; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.context.annotation.Import;
 * 
 * import io.mosip.testrig.apirig.utils.BioDataUtility; import
 * io.mosip.testrig.apirig.utils.CryptoCore; import
 * io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
 * 
 * 
 * @Configuration
 * 
 * @Import({CryptoCore.class})
 * 
 * @ComponentScan(basePackages = { "io.mosip.testrig.apirig",
 * "io.mosip.testrig.dslrig"}) public class BeanConfig { int i = 0;
 * 
 * @Bean public BioDataUtility bioDataUtility() { return new BioDataUtility(); }
 * 
 * @Bean public EncryptionDecrptionUtil encryptionDecrptionUtil() { return new
 * EncryptionDecrptionUtil(); }
 * 
 * }
 */