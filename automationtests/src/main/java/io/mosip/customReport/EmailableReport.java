package io.mosip.customReport;

import java.io.File;
import java.util.List;
import org.testng.ISuite;
import org.testng.reporters.EmailableReporter2;
import org.testng.xml.XmlSuite;

import io.mosip.kernel.util.ConfigManager;
import io.mosip.kernel.util.S3Adapter;

public class EmailableReport extends EmailableReporter2 {

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		super.generateReport(xmlSuites, suites, outputDirectory);
		if (ConfigManager.getPushReportsToS3().equalsIgnoreCase("yes")) {
			File repotFile = new File(System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir")
					+ "/" + System.getProperty("emailable.report2.name"));
			S3Adapter s3Adapter = new S3Adapter();
			boolean isStoreSuccess = false;
			try {
				isStoreSuccess = s3Adapter.putObject(ConfigManager.getS3Account(), System.getProperty("modules"), null,
						null, System.getProperty("emailable.report2.name"), repotFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isStoreSuccess) {
				System.out.println("Pushed file to S3");
			} else {
				System.out.println("Failed while pushing file to S3");
			}
		}
	}
}
