package io.mosip.authentication.e2e;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import io.mosip.global.utils.GlobalConstants;

public class E2EReport implements IReporter{
	private static final Logger logger = Logger.getLogger(E2EReport.class);
	public static Map<String,String> e2eReport = new HashMap<String,String>();
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		e2eReport = getE2eAuthTestReport(suites);
	}
	
	@SuppressWarnings("finally")
	private Map<String,String> getE2eAuthTestReport(List<ISuite> suites) {
		Map<String,String> e2eReport = new HashMap<String,String>();
		try {			
			for (ISuite tempSuite : suites) {
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				for (ISuiteResult result : testResults.values()) {
					ITestContext testObj = result.getTestContext();
					String testName = testObj.getName();
					IResultMap testPassedResult = testObj.getPassedTests();
					Set<ITestResult> testPassedResultSet = testPassedResult.getAllResults();
					for (ITestResult testResult : testPassedResultSet) {
						e2eReport.put(testResult.getTestClass().getName(), "PASS");
					}
					IResultMap testFailedResult = testObj.getFailedTests();
					Set<ITestResult> testResultSet = testFailedResult.getAllResults();
					for (ITestResult testResult : testResultSet) {
						e2eReport.put(testResult.getTestClass().getName(), GlobalConstants.FAIL_STRING);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		} finally {
			return e2eReport;
		}
	}
}
