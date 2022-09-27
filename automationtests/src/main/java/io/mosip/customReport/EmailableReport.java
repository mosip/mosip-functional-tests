package io.mosip.customReport;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.testng.ISuite;
import org.testng.reporters.EmailableReporter2;
import org.testng.xml.XmlSuite;

import com.ibm.icu.impl.PropsVectors;

import io.mosip.kernel.util.ConfigManager;
import io.mosip.kernel.util.S3Adapter;

public class EmailableReport extends EmailableReporter2 {
	
	

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		
		String commitId = null;
		String branch = null;
		
		Properties properties = new Properties();
		try (InputStream is = EmailableReport.class.getClassLoader().getResourceAsStream("git.properties")) {
			properties.load(is);
			
			commitId = properties.getProperty("git.commit.id.abbrev");
			
			branch = properties.getProperty("git.branch");
			

		} catch (IOException io) {
			io.printStackTrace();
		}
		
		//this.writeHead("(commit-id=" + commitId + ")" + "(branchName=" + branch + ")");

		
		
		/*
		 * for (ISuite suite : suites) { String suiteName = suite.getName() +
		 * "(commit-id=" + commitId + ")" + "(branchName=" + branch + ")";
		 * suite.setAttribute("abcs", suiteName); System.out.println(suite);
		 * suiteResults.add(new SuiteResult(suite)); }
		 */
		 
		 

		super.generateReport(xmlSuites, suites, outputDirectory);
		System.out.println("valuetoPush" + ConfigManager.getPushReportsToS3());
		if (ConfigManager.getPushReportsToS3().equalsIgnoreCase("yes")) {
			File repotFile = new File(System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir")
					+ "/" + System.getProperty("emailable.report2.name"));
			System.out.println("reportFile is::" + System.getProperty("user.dir") + "/"
					+ System.getProperty("testng.outpur.dir") + "/" + System.getProperty("emailable.report2.name"));
			S3Adapter s3Adapter = new S3Adapter();
			boolean isStoreSuccess = false;
			try {
				isStoreSuccess = s3Adapter.putObject(ConfigManager.getS3Account(), System.getProperty("modules"), null,
						null, System.getProperty("emailable.report2.name"), repotFile);
				System.out.println("isStoreSuccess:: " + isStoreSuccess);
			} catch (Exception e) {
				System.out.println("error occured while pushing the object" + e.getLocalizedMessage());
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
