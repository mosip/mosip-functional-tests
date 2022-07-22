package io.mosip.customReport;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.testng.ISuite;
import org.testng.reporters.EmailableReporter2;
import org.testng.xml.XmlSuite;

import io.mosip.kernel.util.S3Adapter;
import io.mosip.testrunner.MosipTestRunner;


public class EmailableReport extends EmailableReporter2 {

	public static Properties propsKernel = MosipTestRunner.getproperty(MosipTestRunner.getResourcePath() + "/"+"config/Kernel.properties");
	
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		super.generateReport(xmlSuites, suites, outputDirectory);
		File repotFile = new File(System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir") + "/"
				+ System.getProperty("emailable.report2.name"));
		S3Adapter s3Adapter = new S3Adapter();
		boolean isStoreSuccess = false;
		try {
			isStoreSuccess = s3Adapter.putObject(propsKernel.getProperty("object.store.s3.account"), System.getProperty("modules"), null, null,
					System.getProperty("emailable.report2.name"), repotFile);
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
