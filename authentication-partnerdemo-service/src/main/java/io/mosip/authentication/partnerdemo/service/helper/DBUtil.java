package io.mosip.authentication.partnerdemo.service.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.CryptoUtil;

@Component
public class DBUtil {
	
	public static final String MASTER = "master";
	public static final String IDA = "ida";
	
	@Autowired
	private Environment env;
	
	public String getDbUser(String db) {
		return new String(CryptoUtil.decodeBase64(env.getProperty(db + ".db.user")));
	}
	
	public String getDbPass(String db) {
		return new String(CryptoUtil.decodeBase64(env.getProperty(db + ".db.pass")));
	}

	public String getDbUrl(String db) {
		String baseUrl = env.getProperty("mosip.base.url");
		if(baseUrl != null) {
			if(baseUrl.contains("dev")) {
				return env.getProperty("dev." + db + ".db.url");
			} else if(baseUrl.contains("qa")) {
				return env.getProperty("qa." + db + ".db.url");
			}  else if(baseUrl.contains("ext-int")) {
				return env.getProperty("ext-int." + db + ".db.url");
			}  else if(baseUrl.contains("preprod")) {
				return env.getProperty("preprod." + db + ".db.url");
			} else if(baseUrl.contains("int")) {
				return env.getProperty("int." + db + ".db.url");
			} else {
				return env.getProperty(env.getProperty("spring.profiles.active") + "." + db + ".db.url");
			}
		}
		return null;
	}

}
