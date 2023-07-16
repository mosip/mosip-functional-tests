package io.mosip.testrig.apirig.report;

import java.io.FileReader;

import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

/**
 * Reporter class act as util for additional report class or listeners
 * 
 * @author Vignesh
 *
 */
public class Reporter {

	private static final Logger REPORTLOG = Logger.getLogger(Reporter.class);

	public static String getAppDepolymentVersion() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;
		String version = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(MosipTestRunner.getGlobalResourcePath()+"/metadata.xml");
			model = reader.read(fileReader);
			version = model.getParent().getVersion();
		} catch (Exception e) {
			REPORTLOG.error("Exception in tagging the build number" + e.getMessage());
		} finally {
			AdminTestUtil.closeFileReader(fileReader);
		}
		return version;
	}
	
	public static String getAppEnvironment() {
		return System.getProperty("env.user");
	}

}
