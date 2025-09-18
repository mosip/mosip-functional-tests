package io.mosip.testrig.apirig.report;

//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.DependencyResolver;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.S3Adapter;
import io.mosip.testrig.apirig.utils.SlackChannelIntegration;

/**
 * Reporter that generates a single-page HTML report of the test results.
 */
public class EmailableReport implements IReporter {
	private static final Logger LOG = Logger.getLogger(EmailableReport.class);

	protected PrintWriter writer;

	protected final List<SuiteResult> suiteResults = Lists.newArrayList();
	
	protected final boolean reportIgnoredTestCases = ConfigManager.reportIgnoredTestCases();
	protected final boolean reportKnownIssueTestCases = ConfigManager.reportKnownIssueTestCases();

	// Reusable buffer
	private final StringBuilder buffer = new StringBuilder();

	private String fileName = "emailable-report.html";
	private String mountPathForReport = ConfigManager.getMountPathForReport();

	private static final String JVM_ARG = GlobalConstants.EMAILABLEREPORT2NAME;

	private int totalPassedTests = 0;
	private static int totalSkippedTests = 0;
	private int totalIgnoredTests = 0;
	private int totalKnownIssueTests = 0;
	private static int totalFailedTests = 0;
	private long totalDuration = 0;
	
	private int suiteCount = 0;
	private int prerequisiteFailedCount = 0;
	private int mainSuiteFailedCount = 0;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public static int getFailedCount() {
		return totalFailedTests;
	}
	
	public static int getSkippedCount() {
		return totalSkippedTests;
	}
	
	

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
	    // Sort suites in descending order by suite name
	    Collections.sort(suites, new Comparator<ISuite>() {
	        @Override
	        public int compare(ISuite suite1, ISuite suite2) {
	            return suite2.getName().compareTo(suite1.getName()); // Reverse the order for descending sort
	        }
	    });
		generateReport(xmlSuites, suites, outputDirectory, false); // Generate full report
		if (totalFailedTests > 0 || totalSkippedTests > 0) {
			generateReport(xmlSuites, suites, outputDirectory, true); // Generate error report
		}

	}
	
	private SuiteResult createFilteredSuiteResult(ISuite suite) {
	    ISuite filteredSuite = suite;
	    // Filter passed test results from suite
	    suite.getResults().forEach((testName, suiteResult) -> {
	        ITestContext testContext = suiteResult.getTestContext();
	        // Remove passed tests
	        testContext.getPassedTests().getAllResults().clear();
	    });
	    return new SuiteResult(filteredSuite);
	}
	
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory, boolean skipPassed) {
		try {
			writer = createWriter(outputDirectory);
		} catch (IOException e) {
			LOG.error("Unable to create output file", e);
			return;
		}
		suiteResults.clear();
		for (ISuite suite : suites) {
			if (skipPassed) {
				suiteResults.add(createFilteredSuiteResult(suite));
			} else {
				suiteResults.add(new SuiteResult(suite));
			}
		}
		
		writeDocumentStart();
		writeHead();
		writeBody(skipPassed, suites.size());
		writeDocumentEnd();
		writer.close();
		
		generateOutputFile(skipPassed);

	}
	
	public void generateOutputFile(boolean skipPassed) {
		
		int totalTestCases = 0;
		
		totalTestCases = totalPassedTests + totalSkippedTests + totalFailedTests;
		
		if (reportIgnoredTestCases) {
			totalTestCases = totalTestCases + totalIgnoredTests;
		}
		if (reportKnownIssueTestCases) {
			totalTestCases = totalTestCases + totalKnownIssueTests;
		}

		String oldString = System.getProperty(GlobalConstants.EMAILABLEREPORT2NAME);
		String temp = "";
		String reportContext = skipPassed == true ? "error-" : "full-";
		
		temp = "-" + reportContext + "report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
				+ totalFailedTests;

		if (reportIgnoredTestCases && reportKnownIssueTestCases) {
			temp = "-" + reportContext + "report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_I-" + totalIgnoredTests + "_KI-" + totalKnownIssueTests;
		} else if (reportIgnoredTestCases && !(reportKnownIssueTestCases)) {
			temp = "-" + reportContext + "report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_I-" + totalIgnoredTests;
		} else if (reportKnownIssueTestCases && !(reportIgnoredTestCases)) {
			temp = "-" + reportContext + "report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_KI-" + totalKnownIssueTests;
		}
		
		if (skipPassed == true) {
			temp = temp.replaceAll("P-\\d+_", ""); // Removes passed count (P-*_) from the report title.
		}
		
		String newString = oldString.replace("-report", temp);
		
		StringBuilder slackNotification = new StringBuilder();
		slackNotification
		    .append("Completed apitestrig run --- ")
		    .append(BaseTestCase.currentModule).append(" --- ")
		    .append(BaseTestCase.environment.replace("api-internal.", ""))
		    .append(" env --- ").append(BaseTestCase.testLevel)
		    .append(" --- ").append(temp.replace("-full-report_", ""));

		File orignialReportFile = new File(
				System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir") + "/"
						+ System.getProperty(GlobalConstants.EMAILABLEREPORT2NAME));
		LOG.info("reportFile is::" + System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir")
				+ "/" + System.getProperty(GlobalConstants.EMAILABLEREPORT2NAME));

		File newReportFile = new File(
				System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir") + "/" + newString);
		LOG.info("New reportFile is::" + System.getProperty("user.dir") + "/" + System.getProperty("testng.outpur.dir")
				+ "/" + newString);

		if (orignialReportFile.exists()) {
			if (orignialReportFile.renameTo(newReportFile)) {
				orignialReportFile.delete();
				LOG.info("Report File re-named successfully!");
				if (ConfigManager.getPushReportsToS3().equalsIgnoreCase("yes")) {
					S3Adapter s3Adapter = new S3Adapter();
					boolean isStoreSuccess = false;
					try {
						isStoreSuccess = s3Adapter.putObject(ConfigManager.getS3Account(),
								System.getProperty("modules"), null, null, newString, newReportFile);
						LOG.info("isStoreSuccess:: " + isStoreSuccess);
					} catch (Exception e) {
						LOG.info("error occured while pushing the object" + e.getLocalizedMessage());
						LOG.error(e.getMessage());
					}
					if (isStoreSuccess) {
						LOG.info("Pushed file to S3");
						String reportLink = "https://minio." 
						        + BaseTestCase.environment.replace("api-internal.", "") 
						        + ".mosip.net/browser/" 
						        + ConfigManager.getS3Account() + "/" 
						        + BaseTestCase.currentModule + "%2F" + newString;
						slackNotification.append(" --- ").append(reportLink);
					} else {
						LOG.info("Failed while pushing file to S3");
					}
				} else {
					try {
						Path mountFilePath = Path.of(mountPathForReport, newString);
						Files.copy(newReportFile.toPath(), mountFilePath, StandardCopyOption.REPLACE_EXISTING);
						LOG.info("Successfully copied report file to mount path: " + mountFilePath.toString());
					} catch (Exception e) {
						LOG.error("Error occurred while copying file to mount path: " + e.getLocalizedMessage());
					}
				}
			} else {
				LOG.error("Renamed report file doesn't exist");
			}
		} else {
			LOG.error("Original report File does not exist!");
		}
		
		try {
			SlackChannelIntegration.sendMessageToSlack(slackNotification.toString());
		} catch (Exception e) {
			LOG.error("Failed to send Slack notification: " + e.getMessage());
		}
	}

	private String getCommitId() {
		Properties properties = new Properties();
		try (InputStream is = EmailableReport.class.getClassLoader().getResourceAsStream("git.properties")) {
			properties.load(is);

			return "Commit Id is: " + properties.getProperty("git.commit.id.abbrev") + " & Branch Name is:"
					+ properties.getProperty("git.branch");

		} catch (IOException e) {
			LOG.error("Error getting git branch information: " + e.getMessage());
			return "";
		}

	}
	
	private static String convertMillisToTime(long milliseconds) {
		// Round to 1 second if duration is between 1 ms and 999 ms
		if (milliseconds > 0 && milliseconds < 1000) {
	        milliseconds = 1000;
	    }
		long seconds = (milliseconds / 1000) % 60;
		long minutes = (milliseconds / (1000 * 60)) % 60;
		long hours = (milliseconds / (1000 * 60 * 60)) % 24;
		// Format time into HH:MM:SS
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		String jvmArg = System.getProperty(JVM_ARG);
		if (jvmArg != null && !jvmArg.trim().isEmpty()) {
			fileName = jvmArg;
		}
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, fileName))));
	}

	protected void writeDocumentStart() {
		writer.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	}

	protected void writeHead() {
		writer.print("<head>");
		writer.print("<title>TestNG Report</title>");
		writeStylesheet();
		writer.print("</head>");
	}
	
	protected void writeStylesheet() {
	    writer.print("<style type=\"text/css\">");
	    writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show;width: 100%;}");
	    writer.print("th,td {border:1px solid #009;padding:.25em .5em;width: 25%;}");  // Set a fixed width for uniform cell sizes
	    writer.print("th {vertical-align:bottom}");
	    writer.print("td {vertical-align:top}");
	    writer.print("table a {font-weight:bold}");
	    writer.print(".stripe td {background-color: #E6EBF9}");
	    writer.print(".num {text-align:center}");
	    writer.print(".orange-bg {background-color: #FFD28E}");
	    writer.print(".grey-bg {background-color: #808080}");
	    writer.print(".thich-orange-bg {background-color: #CC5500}");
	    writer.print(".green-bg {background-color: #D0F0C0}");
	    writer.print(".attn {background-color: #E74C3C}");
	    writer.print(".passedodd td {background-color: #D0F0C0}");
	    writer.print(".passedeven td {background-color: #D0F0C0}");
	    writer.print(".skippedodd td {background-color: #FFD28E}");
	    writer.print(".skippedeven td,.stripe {background-color: #FFD28E}");
	    writer.print(".failedodd td {background-color: #E74C3C}");
	    writer.print(".failedeven td,.stripe {background-color: #E74C3C}");
	    writer.print(".ignoredodd td {background-color: #808080}");
	    writer.print(".ignoredeven td {background-color: #808080}");
	    writer.print(".known_issuesodd td {background-color: #CC5500}");
	    writer.print(".known_issueseven td {background-color: #CC5500}");
	    writer.print(".stacktrace {white-space:pre;font-family:monospace}");
	    writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
	    writer.print("</style>");
	}

	protected void writeBody(boolean skipPassed, int suiteSize) {
		writer.print("<body style='font-family: Arial, sans-serif;'>");
		writeSuiteSummary(skipPassed, suiteSize);
		writeScenarioSummary(skipPassed);
		writeScenarioDetails(skipPassed);
		writer.print("</body>");
	}

	protected void writeDocumentEnd() {
		writer.print("</html>");
	}

	protected void writeSuiteSummary(boolean skipPassed, int suiteSize) {
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();
		LocalDate currentDate = LocalDate.now();
		String formattedDate =null;
//		String branch = null;
		boolean endPointDetailsReported = false;
		// Format the current date as per your requirement
		try {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    formattedDate = currentDate.format(formatter);
//		Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        branch = reader.readLine();
		}
		catch (Exception e) {
			LOG.error("Error writing the date and branch information: " + e.getMessage());
		}
		
		totalPassedTests = 0;
		totalSkippedTests = 0;
		totalIgnoredTests = 0;
		totalKnownIssueTests = 0;
		totalFailedTests = 0;
		totalDuration = 0;

		writer.print("<table>");

		int testIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			
			int EverySuiteFailedTestCount = 0;
			
			if(suiteResult.getSuiteName().contains("Master Suite"))
				continue;
			
			suiteCount = suiteCount + 1;
			
			if (endPointDetailsReported == false) {
				// Left column: "Tested Component Details" with central alignment
				writer.print(
						"<th style=\"text-align: center; vertical-align: middle;\" colspan=\"1\"><span class=\"not-bold\"><pre>");
				writer.print(Utils.escapeHtml("Server Component Details"));
				writer.print("</span></th>");

				// Right column: Details from AdminTestUtil.getServerComponentsDetails() without
				// bold formatting
				writer.print("<td colspan=\"8\"><pre>");
				writer.print(Utils.escapeHtml(AdminTestUtil.getServerComponentsDetails()));
				writer.print("</pre></td>");
				writer.print(GlobalConstants.TRTR);

				// Left column: "Tested Component Details" with central alignment
				writer.print(
						"<th style=\"text-align: center; vertical-align: middle;\" colspan=\"1\"><span class=\"not-bold\"><pre>");
				writer.print(Utils.escapeHtml("End Points used"));
				writer.print("</span></th>");

				// Right column: Details from AdminTestUtil.getServerComponentsDetails() without
				// bold formatting
				writer.print("<td colspan=\"8\"><pre>");
				writer.print(Utils.escapeHtml(GlobalMethods.getComponentDetails()));
				writer.print("</pre></td>");
				writer.print(GlobalConstants.TRTR);
				
				/*
				 * writer.
				 * print("<tr><th colspan=\"9\"><span class=\"not-bold\"><pre style=\"white-space:pre-wrap; word-wrap:break-word;\">"
				 * );
				 * writer.print(Utils.escapeHtml(GlobalMethods.getTestCaseVariableMapping()));
				 * writer.print("</pre></span>"); writer.print(GlobalConstants.TRTR);
				 */
				
				if (GlobalMethods.getServerErrors().equals("No server errors")) {
					writer.print("<tr><th colspan=\"9\"><span class=\"not-bold\"><pre>");
				} else {
					writer.print(
							"<tr style=\"background-color: red;\"><th colspan=\"9\"><span class=\"not-bold\"><pre>");
				}
				writer.print(Utils.escapeHtml("Server Errors " + "\n" + GlobalMethods.getServerErrors()));
				writer.print("</pre></span>");
				writer.print(GlobalConstants.TRTR);
				
				if (GlobalMethods.getCaptchaStatus()) {
				    writer.print("<tr style=\"background-color: red;\"><th colspan=\"9\"><span class=\"not-bold\"><pre>");
				} else {
				    writer.print("<tr><th colspan=\"9\"><span class=\"not-bold\"><pre>");
				}
				writer.print(Utils.escapeHtml("Captcha Enabled" + "\n" + GlobalMethods.getCaptchaStatus()));
				writer.print("</pre></span>");
				writer.print(GlobalConstants.TRTR);

				endPointDetailsReported = true;
			}
			
			if (suiteSize > 2 && skipPassed == true) {
				if (suiteCount == 3 && prerequisiteFailedCount == 0) {
					continue;
				}
				if (suiteCount == 4 && mainSuiteFailedCount == 0) {
					continue;
				}
			}
			if (suiteSize == 2 && skipPassed == true) {
				if (suiteCount == 2 && prerequisiteFailedCount == 0) {
					continue;
				}
			}
			
			writer.print("<tr><th colspan=\"9\" style=\"background-color: #FFFFE0;\">");
			writer.print(Utils.escapeHtml(suiteResult.getSuiteName() + " ---- " + "Report Date: " + formattedDate
					+ " ---- " + "Tested Environment: "
					+ System.getProperty("env.endpoint").replaceAll(".*?\\.([^\\.]+)\\..*", "$1") + " ---- "
					+ getCommitId()));
			writer.print(GlobalConstants.TRTR);

			writer.print("<tr>");
			writer.print("<th>Test Scenario</th>");
			writer.print("<th># Total</th>");
			
			if (skipPassed == false)
				writer.print("<th># Passed</th>");
			
			writer.print("<th># Skipped</th>");
			writer.print("<th># Failed</th>");
			if (reportIgnoredTestCases) {
				writer.print("<th># Ignored</th>");
			}
			
			if (reportKnownIssueTestCases) {
				writer.print("<th># Known_Issues</th>");
			}
			writer.print("<th>Execution Time (HH:MM:SS)</th>");
			writer.print(GlobalConstants.TR);

			for (TestResult testResult : suiteResult.getTestResults()) {
				int passedTests = testResult.getPassedTestCount();
				int ignoredTests = testResult.getIgnoredTestCount();
				int knownIssueTests = testResult.getKnownIssueTestCount();
				int skippedTests = testResult.getSkippedTestCount();
				int failedTests = testResult.getFailedTestCount();
				long duration = testResult.getDuration();
				int totalTests = 0;
				EverySuiteFailedTestCount = EverySuiteFailedTestCount + failedTests;
				
				totalTests = passedTests + skippedTests + failedTests;
				
				if (reportIgnoredTestCases) {
					totalTests = totalTests + ignoredTests;
				}
				if (reportKnownIssueTestCases) {
					totalTests = totalTests + knownIssueTests;
				}
				// All test cases are ignored. Hence don't print anything in the report.
				if (totalTests < 1) 
					continue;

				writer.print("<tr");
				writer.print(">");

				buffer.setLength(0);
				

				
				writeTableData(buffer.append("<a href=\"#t").append(testIndex).append("\">")
						.append(Utils.escapeHtml(testResult.getTestName())).append("</a>").toString(), "num");
				
				writeTableData(integerFormat.format(totalTests), "num");
				
				if (skipPassed == false)
					writeTableData(integerFormat.format(passedTests), (passedTests > 0 ? "num green-bg" : "num"));
				
				writeTableData(integerFormat.format(skippedTests), (skippedTests > 0 ? "num orange-bg" : "num"));
				writeTableData(integerFormat.format(failedTests), (failedTests > 0 ? GlobalConstants.NUMATTN : "num"));
				// print the ignored column based on the flag
				if (reportIgnoredTestCases) {
					writeTableData(integerFormat.format(ignoredTests), (ignoredTests > 0 ? "num grey-bg" : "num"));
				}
				
				if (reportKnownIssueTestCases) {
					writeTableData(integerFormat.format(knownIssueTests), (knownIssueTests > 0 ? "num thich-orange-bg" : "num"));
				}
				writeTableData(convertMillisToTime(duration), "num");
				writer.print(GlobalConstants.TR);

				totalPassedTests += passedTests;
				totalSkippedTests += skippedTests;
				totalFailedTests += failedTests;
				totalIgnoredTests += ignoredTests;
				totalKnownIssueTests += knownIssueTests;
				totalDuration += duration;

				testIndex++;
			}
			
			if (skipPassed == false && suiteCount == 1 && EverySuiteFailedTestCount > 0) {
				prerequisiteFailedCount = EverySuiteFailedTestCount;
				System.out.println("prerequisiteFailedCount = " + prerequisiteFailedCount);
			}if (suiteSize > 2 && skipPassed == false && suiteCount == 2 && EverySuiteFailedTestCount > 0) {
				mainSuiteFailedCount = EverySuiteFailedTestCount;
				System.out.println("mainSuiteFailedCount = " + mainSuiteFailedCount);
			}
		}

//		if (testIndex > 1) {
			writer.print("<tr>");
			writer.print("<th>Total</th>");
			
			if (reportIgnoredTestCases && reportKnownIssueTestCases) {
				writeTableHeader(integerFormat.format(totalPassedTests + totalSkippedTests + totalFailedTests
						+ totalIgnoredTests + totalKnownIssueTests), "num");
			} else if (reportIgnoredTestCases && !(reportKnownIssueTestCases)) {
				writeTableHeader(integerFormat.format(totalPassedTests + totalSkippedTests + totalFailedTests
						+ totalIgnoredTests), "num");
			} else if (reportKnownIssueTestCases && !(reportIgnoredTestCases)) {
				writeTableHeader(integerFormat.format(totalPassedTests + totalSkippedTests + totalFailedTests
						+totalKnownIssueTests), "num");
			} else {
				writeTableHeader(integerFormat.format(totalPassedTests + totalSkippedTests + totalFailedTests), "num");
			}
			
			if (skipPassed == false)
				writeTableHeader(integerFormat.format(totalPassedTests), (totalPassedTests > 0 ? "num green-bg" : "num"));
			
			writeTableHeader(integerFormat.format(totalSkippedTests),
					(totalSkippedTests > 0 ? "num orange-bg" : "num"));
			writeTableHeader(integerFormat.format(totalFailedTests),
					(totalFailedTests > 0 ? GlobalConstants.NUMATTN : "num"));
			if (reportIgnoredTestCases) {
				writeTableHeader(integerFormat.format(totalIgnoredTests),
						(totalIgnoredTests > 0 ? "num grey-bg" : "num"));
			}
			if (reportKnownIssueTestCases) {
				writeTableHeader(integerFormat.format(totalKnownIssueTests),
						(totalKnownIssueTests > 0 ? "num thich-orange-bg" : "num"));
			}
			writeTableHeader(convertMillisToTime(totalDuration), "num");
			writer.print(GlobalConstants.TR);
//		}

		writer.print(GlobalConstants.TABLE);
	}
	
	protected static Set<ITestResult> getResultsSubSet(Set<ITestResult> resultsSet, String subSetString) {
		List<ITestResult> testResultsSubList = Lists.newArrayList();
		if (!resultsSet.isEmpty()) {
			List<ITestResult> resultsList = Lists.newArrayList(resultsSet);
			Iterator<ITestResult> resultsIterator = resultsList.iterator();
			while (resultsIterator.hasNext()) {
				ITestResult result = resultsIterator.next();
				Throwable throwable = result.getThrowable();
				if (throwable != null) {
					if (subSetString.contains(GlobalConstants.FEATURE_NOT_SUPPORTED)
							|| subSetString.contains(GlobalConstants.SERVICE_NOT_DEPLOYED)
							|| subSetString.contains(GlobalConstants.NOT_IN_RUN_SCOPE)) {
						if (containsAny(throwable.getMessage(), subSetString)) {
							// Add only results which are skipped due to feature not supported
							testResultsSubList.add(result);
						} else {
							// Skip the test result
						}
					} else if (subSetString.contains(GlobalConstants.KNOWN_ISSUES_STRING)) {
						if (containsAny(throwable.getMessage(), subSetString)) {
							// Add only results which are skipped due to feature not supported
							testResultsSubList.add(result);
						} else {
							// Skip the test result
						}
					} else { // Service not deployed. Hence skipping the testcase // skipped
						if (!throwable.getMessage().contains(GlobalConstants.FEATURE_NOT_SUPPORTED)
								&& !throwable.getMessage().contains(GlobalConstants.SERVICE_NOT_DEPLOYED)
								&& !throwable.getMessage().contains(GlobalConstants.KNOWN_ISSUES)
								&& !throwable.getMessage().contains(GlobalConstants.NOT_IN_RUN_SCOPE)) {
							// Add only results which are not skipped due to feature not supported
							testResultsSubList.add(result);
						} else {
							// Skip the test result
						}
					}
				}
			}
		}
		Set<ITestResult> testResultsSubSet = Set.copyOf(testResultsSubList);
		return testResultsSubSet;
	}
	
	public static boolean containsAny(String stringToCheckIn, String delimitedString) {
	    // Split the input string into an array based on semicolons
        String[] stringsToCheckFor = delimitedString.split(";");
		
        for (String str : stringsToCheckFor) {
            if (stringToCheckIn.contains(str)) {
                return true; // If any string is found, return true
            }
        }
        return false; // If none of the strings are found, return false
    }

	/**
	 * Writes a summary of all the test scenarios.
	 */
	protected void writeScenarioSummary(boolean skipPassed) {
		writer.print("<table id='summary'>");
		writer.print("<thead>");
		writer.print("<tr>");
		writer.print("<th>TestCase Number</th>");
		writer.print("<th>TestCase Description</th>");
		writer.print("<th>Execution Time (HH:MM:SS)</th>");
		writer.print(GlobalConstants.TR);
		writer.print("</thead>");

		int testIndex = 0;
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			
			

			for (TestResult testResult : suiteResult.getTestResults()) {
				int passedTests = testResult.getPassedTestCount();
				int ignoredTests = testResult.getIgnoredTestCount();
				int knownIssueTests = testResult.getKnownIssueTestCount();
				int skippedTests = testResult.getSkippedTestCount();
				int failedTests = testResult.getFailedTestCount();
				int totalTests = 0;
				
				totalTests = passedTests + skippedTests + failedTests;

				if (reportIgnoredTestCases) {
					totalTests = totalTests + ignoredTests;
				}
				if (reportKnownIssueTestCases) {
					totalTests = totalTests + knownIssueTests;
				}
				// All test cases are ignored. Hence don't print anything in the report.
				if (totalTests < 1) 
					continue;
				writer.print("<tbody id=\"t");
				writer.print(testIndex);
				writer.print("\">");

				String testName = Utils.escapeHtml(testResult.getTestName());

				if (reportIgnoredTestCases) {
					scenarioIndex += writeScenarioSummary(testName + " &#8212; Ignored",
							testResult.getIgnoredTestResults(), "ignored", scenarioIndex);
				}

				if (reportKnownIssueTestCases) {
					scenarioIndex += writeScenarioSummary(testName + " &#8212; known_issues",
							testResult.getKnownIssueTestResults(), "known_issues", scenarioIndex);
				}
				
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Failed", testResult.getFailedTestResults(),
						"failed", scenarioIndex);
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Skipped", testResult.getSkippedTestResults(),
						"skipped", scenarioIndex);
				
				if (skipPassed == false)
					scenarioIndex += writeScenarioSummary(testName + " &#8212; Passed", testResult.getPassedTestResults(),
						"passed", scenarioIndex);

				writer.print("</tbody>");

				testIndex++;
			}
		}

		writer.print(GlobalConstants.TABLE);
	}
	
	private String getTestCaseDescription(ITestResult result) {
		Object[] parameters = result.getParameters();
		if (parameters != null && parameters.length > 0 && parameters[0] instanceof TestCaseDTO) {
			TestCaseDTO testCase = (TestCaseDTO) parameters[0];
			if (testCase.getDescription() == null)
				return "";
			else
				return testCase.getDescription();
		}

		return "";
	}
	
	
	private String getTestCaseUniqueIdentifier(ITestResult result) {
		Object[] parameters = result.getParameters();
		if (parameters != null && parameters.length > 0 && parameters[0] instanceof TestCaseDTO) {
			TestCaseDTO testCase = (TestCaseDTO) parameters[0];
			if (testCase.getUniqueIdentifier()  == null)
				return "";
			else
				return testCase.getUniqueIdentifier();
		}

		return "";
	}

	/**
	 * Writes the scenario summary for the results of a given state for a single
	 * test.
	 */
	private int writeScenarioSummary(String description, List<ClassResult> classResults, String cssClassPrefix,
			int startingScenarioIndex) {
		int scenarioCount = 0;
		if (!classResults.isEmpty()) {
			writer.print("<tr><th colspan=\"3\">");
			writer.print(description);
			writer.print(GlobalConstants.TRTR);

			int scenarioIndex = startingScenarioIndex;
			int classIndex = 0;
			for (ClassResult classResult : classResults) {
				String cssClass = cssClassPrefix + ((classIndex % 2) == 0 ? "even" : "odd");

				buffer.setLength(0);

//				int scenariosPerClass = 0;
				int methodIndex = 0;
				for (MethodResult methodResult : classResult.getMethodResults()) {
					List<ITestResult> results = methodResult.getResults();
					int resultsCount = results.size();
					assert resultsCount > 0;

					ITestResult firstResult = results.iterator().next();
					String testCaseDescription = getTestCaseDescription(firstResult);
					String uniqueIdentifier = getTestCaseUniqueIdentifier(firstResult);
					String methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());
					long start = firstResult.getStartMillis();
					long duration = firstResult.getEndMillis() - start;

					if (methodIndex > 0) {
						buffer.append(GlobalConstants.TRCLASS).append(cssClass).append("\">");

					}
					
					String temp = uniqueIdentifier.isEmpty() ? methodName : uniqueIdentifier;
					
					
					buffer.append("<td style=\"text-align:left;\"><a href=\"#m").append(scenarioIndex).append("\">")
					.append(temp).append("</a></td>").append("<td style=\"text-align:left;\">")
					.append(testCaseDescription).append("</td>")
					.append("<td style=\"text-align:left;\" rowspan=\"").append(resultsCount).append("\">")
					.append(convertMillisToTime(duration)).append("</td></tr>");
					
					scenarioIndex++;

					for (int i = 1; i < resultsCount; i++) {
						buffer.append("<tr class=\"").append(cssClass).append("\">")
								.append("<td style=\"text-align:center;\"><a href=\"#m").append(scenarioIndex)
								.append("\">").append(temp).append("</a></td></tr>");
						scenarioIndex++;
					}

//					scenariosPerClass += resultsCount;
					methodIndex++;
				}

				writer.print(GlobalConstants.TRCLASS);
				writer.print(cssClass);
				writer.print("\">");
				writer.print(buffer);

				classIndex++;
			}
			scenarioCount = scenarioIndex - startingScenarioIndex;
		}
		return scenarioCount;
	}

	/**
	 * Writes the details for all test scenarios.
	 */
	protected void writeScenarioDetails(boolean skipPassed) {
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			for (TestResult testResult : suiteResult.getTestResults()) {

				if (reportIgnoredTestCases) {
					scenarioIndex += writeScenarioDetails(testResult.getIgnoredTestResults(), scenarioIndex);
				}
				
				if (reportKnownIssueTestCases) {
					scenarioIndex += writeScenarioDetails(testResult.getKnownIssueTestResults(), scenarioIndex);
				}
				scenarioIndex += writeScenarioDetails(testResult.getFailedConfigurationResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getFailedTestResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getSkippedConfigurationResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getSkippedTestResults(), scenarioIndex);
				
				if (skipPassed == false)
					scenarioIndex += writeScenarioDetails(testResult.getPassedTestResults(), scenarioIndex);
				
			}
		}
	}

	/**
	 * Writes the scenario details for the results of a given state for a single
	 * test.
	 */
	private int writeScenarioDetails(List<ClassResult> classResults, int startingScenarioIndex) {
		int scenarioIndex = startingScenarioIndex;
		String label = "";
		for (ClassResult classResult : classResults) {
			String className = classResult.getClassName();
			for (MethodResult methodResult : classResult.getMethodResults()) {
				List<ITestResult> results = methodResult.getResults();
				assert !results.isEmpty();
				if (ConfigManager.IsDebugEnabled())
					label = Utils.escapeHtml(className + "#" + results.iterator().next().getMethod().getMethodName());
				else
					label = Utils.escapeHtml(results.iterator().next().getMethod().getMethodName());
				for (ITestResult result : results) {
					writeScenario(scenarioIndex, label, result);
					scenarioIndex++;
				}
			}
		}

		return scenarioIndex - startingScenarioIndex;
	}

	/**
	 * Writes the details for an individual test scenario.
	 */
	private void writeScenario(int scenarioIndex, String label, ITestResult result) {
		writer.print("<h3 id=\"m");
		writer.print(scenarioIndex);
		writer.print("\">");

		Object[] parameters = result.getParameters();
		int parameterCount = (parameters == null ? 0 : parameters.length);
		
		String testCaseName = result.getMethod().getMethodName();
		// Get the class name
		String className = result.getMethod().getTestClass().getRealClass().getSimpleName();

		String uniqueIdentifier = (parameterCount > 0 && parameters[0] instanceof TestCaseDTO)
				? (((TestCaseDTO) parameters[0]).getUniqueIdentifier() != null
						? ((TestCaseDTO) parameters[0]).getUniqueIdentifier()
						: "TestCase ID is not available")
				: "UNKNOWN";

		String description = (parameterCount > 0 && parameters[0] instanceof TestCaseDTO)
				? (((TestCaseDTO) parameters[0]).getDescription() != null
						? ((TestCaseDTO) parameters[0]).getDescription()
						: "No description available.")
				: "UNKNOWN";

		// Replace test case name with: TestcaseNumber # TestcaseDescription
		writer.print(Utils.escapeHtml(uniqueIdentifier + " # " + description));
		writer.print("</h3>");

		writer.print("<table class=\"result\">");
		
		// Add TestCaseName
		writer.print("<tr class=\"param\">");
		writer.print("<th>Testcase Name</th>");
		writer.print("</tr><tr class=\"param stripe\">");
		writer.print("<td>");
		writer.print(Utils.escapeHtml(testCaseName));
		writer.print("</td></tr>");
		
		// Add Dependency
		writer.print("<tr class=\"param\">");
		writer.print("<th>Testcase Dependency</th>");
		writer.print("</tr><tr class=\"param stripe\">");
		List<String> dependencies = DependencyResolver.getDependencies(uniqueIdentifier);
		dependencies.remove(uniqueIdentifier);
		writer.print("<td>");
		writer.print(Utils.escapeHtml(dependencies.toString()));
		writer.print("</td>");
		
		// Add ClassName
		writer.print("<tr class=\"param\">");
		writer.print("<th>Class Name</th>");
		writer.print("</tr><tr class=\"param stripe\">");
		writer.print("<td>");
		writer.print(Utils.escapeHtml(className));
		writer.print("</td></tr>");
		
		writer.print(GlobalConstants.TR);
		
		if (ConfigManager.IsDebugEnabled()) {
			if (parameterCount > 0) {
				writer.print("<tr class=\"param\">");
				for (int i = 1; i <= parameterCount; i++) {
					writer.print("<th>Testcase Input");
					writer.print("</th>");
				}
				writer.print("</tr><tr class=\"param stripe\">");
				for (Object parameter : parameters) {
					String testcaseDTO = Utils.toString(parameter).replace("TestCaseDTO(", "");
					writer.print("<td>");
					writer.print(Utils.escapeHtml(testcaseDTO.substring(0, testcaseDTO.length() - 1)));
					writer.print("</td>");
				}
				writer.print(GlobalConstants.TR);
			}
		}

		List<String> reporterMessages = Reporter.getOutput(result);
		if (!reporterMessages.isEmpty()) {
			writer.print("<tr><th");
			if (parameterCount > 1) {
				writer.print(GlobalConstants.colspan);
				writer.print(parameterCount);
				writer.print("\"");
			}
			writer.print(">Messages</th></tr>");

			writer.print("<tr><td");
			if (parameterCount > 1) {
				writer.print(GlobalConstants.colspan);
				writer.print(parameterCount);
				writer.print("\"");
			}
			writer.print(">");
			writeReporterMessages(reporterMessages);
			writer.print(GlobalConstants.TDTR);
		}

		Throwable throwable = result.getThrowable();
		if (throwable != null) {
			writer.print("<tr><th");
			if (parameterCount > 1) {
				writer.print(GlobalConstants.colspan);
				writer.print(parameterCount);
				writer.print("\"");
			}
			writer.print(">");
			writer.print(
					(result.getStatus() == ITestResult.SUCCESS ? "Expected Exception" : "Output Validation Exception"));
			writer.print(GlobalConstants.TRTR);

			writer.print("<tr><td");
			if (parameterCount > 1) {
				writer.print(GlobalConstants.colspan);
				writer.print(parameterCount);
				writer.print("\"");
			}
			writer.print(">");
			writeStackTrace(throwable);
			writer.print(GlobalConstants.TDTR);
		}

		writer.print(GlobalConstants.TABLE);
		writer.print("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
	}

	protected void writeReporterMessages(List<String> reporterMessages) {
		writer.print("<div class=\"Request and Response messages including headers\">");
		Iterator<String> iterator = reporterMessages.iterator();
		assert iterator.hasNext();
		if (Reporter.getEscapeHtml()) {
			writer.print(Utils.escapeHtml(iterator.next()));
		} else {
			writer.print(iterator.next());
		}
		while (iterator.hasNext()) {
			writer.print("<br/>");
			if (Reporter.getEscapeHtml()) {
				writer.print(Utils.escapeHtml(iterator.next()));
			} else {
				writer.print(iterator.next());
			}
		}
		writer.print("</div>");
	}

	protected void writeStackTrace(Throwable throwable) {
		writer.print("<div class=\"stacktrace\">");
		writer.print(Utils.shortStackTrace(throwable, true));
		writer.print("</div>");
	}

	/**
	 * Writes a TH element with the specified contents and CSS class names.
	 * 
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTableHeader(String html, String cssClasses) {
		writeTag("th", html, cssClasses);
	}

	/**
	 * Writes a TD element with the specified contents.
	 * 
	 * @param html the HTML contents
	 */
	protected void writeTableData(String html) {
		writeTableData(html, null);
	}

	/**
	 * Writes a TD element with the specified contents and CSS class names.
	 * 
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTableData(String html, String cssClasses) {
		writeTag("td", html, cssClasses);
	}

	/**
	 * Writes an arbitrary HTML element with the specified contents and CSS class
	 * names.
	 * 
	 * @param tag        the tag name
	 * @param html       the HTML contents
	 * @param cssClasses the space-delimited CSS classes or null if there are no
	 *                   classes to apply
	 */
	protected void writeTag(String tag, String html, String cssClasses) {
		writer.print("<");
		writer.print(tag);
		if (cssClasses != null) {
			writer.print(" class=\"");
			writer.print(cssClasses);
			writer.print("\"");
		}
		writer.print(">");
		writer.print(html);
		writer.print("</");
		writer.print(tag);
		writer.print(">");
	}

	/**
	 * Groups {@link TestResult}s by suite.
	 */
	protected static class SuiteResult {
		private final String suiteName;
		private final List<TestResult> testResults = Lists.newArrayList();

		public SuiteResult(ISuite suite) {
			suiteName = suite.getName();
			for (ISuiteResult suiteResult : suite.getResults().values()) {
				testResults.add(new TestResult(suiteResult.getTestContext()));
			}
		}

		public String getSuiteName() {
			return suiteName;
		}

		/**
		 * @return the test results (possibly empty)
		 */
		public List<TestResult> getTestResults() {
			return testResults;
		}
	}

	/**
	 * Groups {@link ClassResult}s by test, type (configuration or test), and
	 * status.
	 */
	protected static class TestResult {
		/**
		 * Orders test results by class name and then by method name (in lexicographic
		 * order).
		 */
		protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
			@Override
			public int compare(ITestResult o1, ITestResult o2) {
				int result = o1.getTestClass().getName().compareTo(o2.getTestClass().getName());
				if (result == 0) {
					result = o1.getMethod().getMethodName().compareTo(o2.getMethod().getMethodName());
				}
				return result;
			}
		};

		private final String testName;
		private final List<ClassResult> failedConfigurationResults;
		private final List<ClassResult> failedTestResults;
		private final List<ClassResult> skippedConfigurationResults;
		private final List<ClassResult> skippedTestResults;
		private final List<ClassResult> passedTestResults;
		private final List<ClassResult> ignoredTestResults;
		private final List<ClassResult> knownIssueTestResults;
		private final int ignoredTestCount;
		private final int knownIssueTestCount;
		private final int failedTestCount;
		private final int skippedTestCount;
		private final int passedTestCount;
		private final long duration;
		private final String includedGroups;
		private final String excludedGroups;

		public TestResult(ITestContext context) {
			testName = context.getName();

			Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
			Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
			Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
//			Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
			Set<ITestResult> skippedTests = getResultsSubSet(context.getSkippedTests().getAllResults(), GlobalConstants.SKIPPED);
			Set<ITestResult> ignoredTests =  getResultsSubSet(context.getSkippedTests().getAllResults(), GlobalConstants.IGNORED_SUBSET_STRING);
			Set<ITestResult> knownIssueTests =  getResultsSubSet(context.getSkippedTests().getAllResults(), GlobalConstants.KNOWN_ISSUE_SUBSET_STRING);
			Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

			failedConfigurationResults = groupResults(failedConfigurations);
			failedTestResults = groupResults(failedTests);
			skippedConfigurationResults = groupResults(skippedConfigurations);
			skippedTestResults = groupResults(skippedTests);
			ignoredTestResults = groupResults(ignoredTests);
			knownIssueTestResults = groupResults(knownIssueTests);
			passedTestResults = groupResults(passedTests);

			failedTestCount = failedTests.size();
			skippedTestCount = skippedTests.size();
			passedTestCount = passedTests.size();
			ignoredTestCount = ignoredTests.size();
			knownIssueTestCount = knownIssueTests.size();

			duration = context.getEndDate().getTime() - context.getStartDate().getTime();

			includedGroups = formatGroups(context.getIncludedGroups());
			excludedGroups = formatGroups(context.getExcludedGroups());
		}

		/**
		 * Groups test results by method and then by class.
		 */
		protected List<ClassResult> groupResults(Set<ITestResult> results) {
			List<ClassResult> classResults = Lists.newArrayList();
			if (!results.isEmpty()) {
				List<MethodResult> resultsPerClass = Lists.newArrayList();
				List<ITestResult> resultsPerMethod = Lists.newArrayList();

				List<ITestResult> resultsList = Lists.newArrayList(results);
				Collections.sort(resultsList, RESULT_COMPARATOR);
				Iterator<ITestResult> resultsIterator = resultsList.iterator();
				assert resultsIterator.hasNext();

				ITestResult result = resultsIterator.next();
				resultsPerMethod.add(result);

				String previousClassName = result.getTestClass().getName();
				String previousMethodName = result.getMethod().getMethodName();
				while (resultsIterator.hasNext()) {
					result = resultsIterator.next();

					String className = result.getTestClass().getName();
					if (!previousClassName.equals(className)) {
						assert !resultsPerMethod.isEmpty();
						resultsPerClass.add(new MethodResult(resultsPerMethod));
						resultsPerMethod = Lists.newArrayList();

						assert !resultsPerClass.isEmpty();
						classResults.add(new ClassResult(previousClassName, resultsPerClass));
						resultsPerClass = Lists.newArrayList();

						previousClassName = className;
						previousMethodName = result.getMethod().getMethodName();
					} else {
						String methodName = result.getMethod().getMethodName();
						if (!previousMethodName.equals(methodName)) {
							assert !resultsPerMethod.isEmpty();
							resultsPerClass.add(new MethodResult(resultsPerMethod));
							resultsPerMethod = Lists.newArrayList();

							previousMethodName = methodName;
						}
					}
					resultsPerMethod.add(result);
				}
				assert !resultsPerMethod.isEmpty();
				resultsPerClass.add(new MethodResult(resultsPerMethod));
				assert !resultsPerClass.isEmpty();
				classResults.add(new ClassResult(previousClassName, resultsPerClass));
			}
			return classResults;
		}

		public String getTestName() {
			return testName;
		}
		
		/**
		 * @return the results for failed configurations (possibly empty)
		 */
		public List<ClassResult> getFailedConfigurationResults() {
			return failedConfigurationResults;
		}

		/**
		 * @return the results for failed tests (possibly empty)
		 */
		public List<ClassResult> getFailedTestResults() {
			return failedTestResults;
		}
		
		public List<ClassResult> getIgnoredTestResults() {
			return ignoredTestResults;
		}
		
		public List<ClassResult> getKnownIssueTestResults() {
			return knownIssueTestResults;
		}

		/**
		 * @return the results for skipped configurations (possibly empty)
		 */
		public List<ClassResult> getSkippedConfigurationResults() {
			return skippedConfigurationResults;
		}

		/**
		 * @return the results for skipped tests (possibly empty)
		 */
		public List<ClassResult> getSkippedTestResults() {
			return skippedTestResults;
		}

		/**
		 * @return the results for passed tests (possibly empty)
		 */
		public List<ClassResult> getPassedTestResults() {
			return passedTestResults;
		}

		public int getFailedTestCount() {
			return failedTestCount;
		}

		public int getSkippedTestCount() {
			return skippedTestCount;
		}

		public int getPassedTestCount() {
			return passedTestCount;
		}
		
		public int getIgnoredTestCount() {
			return ignoredTestCount;
		}
		
		public int getKnownIssueTestCount() {
			return knownIssueTestCount;
		}

		public long getDuration() {
			return duration;
		}

		public String getIncludedGroups() {
			return includedGroups;
		}

		public String getExcludedGroups() {
			return excludedGroups;
		}

		/**
		 * Formats an array of groups for display.
		 */
		protected String formatGroups(String[] groups) {
			if (groups.length == 0) {
				return "";
			}

			StringBuilder builder = new StringBuilder();
			builder.append(groups[0]);
			for (int i = 1; i < groups.length; i++) {
				builder.append(", ").append(groups[i]);
			}
			return builder.toString();
		}
	}

	/**
	 * Groups {@link MethodResult}s by class.
	 */
	protected static class ClassResult {
		private final String className;
		private final List<MethodResult> methodResults;

		/**
		 * @param className     the class name
		 * @param methodResults the non-null, non-empty {@link MethodResult} list
		 */
		public ClassResult(String className, List<MethodResult> methodResults) {
			this.className = className;
			this.methodResults = methodResults;
		}

		public String getClassName() {
			return className;
		}

		/**
		 * @return the non-null, non-empty {@link MethodResult} list
		 */
		public List<MethodResult> getMethodResults() {
			return methodResults;
		}
	}

	/**
	 * Groups test results by method.
	 */
	protected static class MethodResult {
		private final List<ITestResult> results;

		/**
		 * @param results the non-null, non-empty result list
		 */
		public MethodResult(List<ITestResult> results) {
			this.results = results;
		}

		/**
		 * @return the non-null, non-empty result list
		 */
		public List<ITestResult> getResults() {
			return results;
		}
	}

}
