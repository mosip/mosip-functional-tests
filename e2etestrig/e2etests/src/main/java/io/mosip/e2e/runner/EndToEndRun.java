package io.mosip.e2e.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

import io.mosip.testrunner.MosipTestRunner;
/**
 * 
 * @author M1047227
 *
 */

public class EndToEndRun {
	/**
	 * 
	 * @param args
	 * Main method to run the e2e
	 */
public static void main(String[] args) {
	
	TestNG runner = new TestNG();
	List<String> suitefiles = new ArrayList<String>();
	suitefiles.add(new File(MosipTestRunner.getGlobalResourcePath()+"/testNg.xml").getAbsolutePath());
	runner.setTestSuites(suitefiles);
	runner.setOutputDirectory("testng-report");
	runner.run();
}

}
