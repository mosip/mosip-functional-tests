package io.mosip.testrig.apirig.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.S3Adapter;

/**
 * Reporter that generates a single-page HTML report of the test results.
 */
public class EmailableReport implements IReporter {
	private static final Logger LOG = Logger.getLogger(EmailableReport.class);

	protected PrintWriter writer;

	protected final List<SuiteResult> suiteResults = Lists.newArrayList();

	// Reusable buffer
	private final StringBuilder buffer = new StringBuilder();

	private String fileName = "emailable-report.html";

	private static final String JVM_ARG = GlobalConstants.EMAILABLEREPORT2NAME;

	private int totalPassedTests = 0;
	private int totalSkippedTests = 0;
	private int totalIgnoredTests = 0;
	private int totalFailedTests = 0;
	private long totalDuration = 0;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		try {
			writer = createWriter(outputDirectory);
		} catch (IOException e) {
			LOG.error("Unable to create output file", e);
			return;
		}

		for (ISuite suite : suites) {
			suiteResults.add(new SuiteResult(suite));
		}

		writeDocumentStart();
		writeHead();
		writeBody();
		writeDocumentEnd();
		writer.close();

		int totalTestCases = 0;
		if (ConfigManager.reportIgnoredTestCases()) {
			totalTestCases = totalPassedTests + totalSkippedTests + totalFailedTests + totalIgnoredTests;
		}else {
			totalTestCases = totalPassedTests + totalSkippedTests + totalFailedTests;
		}

		String oldString = System.getProperty(GlobalConstants.EMAILABLEREPORT2NAME);
		String temp = "";
		if (ConfigManager.reportIgnoredTestCases()) {
			temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
					+ totalFailedTests + "_I-" + totalIgnoredTests;
		}else {
			temp = "-report_T-" + totalTestCases + "_P-" + totalPassedTests + "_S-" + totalSkippedTests + "_F-"
				+ totalFailedTests;
		}
		String newString = oldString.replace("-report", temp);

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
					} else {
						LOG.info("Failed while pushing file to S3");
					}
				}
			} else {
				LOG.error("Renamed report file doesn't exist");
			}
		} else {
			LOG.error("Original report File does not exist!");
		}
	}

	private String getCommitId() {
		Properties properties = new Properties();
		try (InputStream is = EmailableReport.class.getClassLoader().getResourceAsStream("git.properties")) {
			properties.load(is);

			return "Commit Id is: " + properties.getProperty("git.commit.id.abbrev") + " & Branch Name is:"
					+ properties.getProperty("git.branch");

		} catch (IOException e) {
			LOG.error(e.getMessage());
			return "";
		}

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
	    writer.print(".orange-bg {background-color: #FFA500}");
	    writer.print(".grey-bg {background-color: #808080}");
	    writer.print(".green-bg {background-color: #0A0}");
	    writer.print(".attn {background-color: #D00}");
	    writer.print(".passedodd td {background-color: #3F3}");
	    writer.print(".passedeven td {background-color: #0A0}");
	    writer.print(".skippedodd td {background-color: #FFA500}");
	    writer.print(".skippedeven td,.stripe {background-color: #FFA500}");
	    writer.print(".failedodd td {background-color: #F33}");
	    writer.print(".failedeven td,.stripe {background-color: #D00}");
	    writer.print(".ignoredodd td {background-color: #808080}");
	    writer.print(".ignoredeven td {background-color: #808080}");
	    writer.print(".stacktrace {white-space:pre;font-family:monospace}");
	    writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
	    writer.print("</style>");
	}

	protected void writeBody() {
		writer.print("<body>");
		writeSuiteSummary();
		writeScenarioSummary();
		writeScenarioDetails();
		writer.print("</body>");
	}

	protected void writeDocumentEnd() {
		writer.print("</html>");
	}

	protected void writeSuiteSummary() {
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();
		NumberFormat decimalFormat = NumberFormat.getNumberInstance();
		LocalDate currentDate = LocalDate.now();
		String formattedDate =null;
		 String branch = null;

		// Format the current date as per your requirement
		try {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    formattedDate = currentDate.format(formatter);
		Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        branch = reader.readLine();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		totalPassedTests = 0;
		totalSkippedTests = 0;
		totalIgnoredTests = 0;
		totalFailedTests = 0;
		totalDuration = 0;

		writer.print("<table>");

		int testIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			writer.print("<tr><th colspan=\"8\">");
			writer.print(Utils.escapeHtml(suiteResult.getSuiteName() + " ---- " + "Report Date: " + formattedDate
					+ " ---- " + "Tested Environment: "
					+ System.getProperty("env.endpoint").replaceAll(".*?\\.([^\\.]+)\\..*", "$1") + " ---- "
					+ getCommitId()));
			writer.print(GlobalConstants.TRTR);
			
			// Left column: "Tested Component Details" with central alignment
			writer.print("<th style=\"text-align: center; vertical-align: middle;\" colspan=\"2\"><span class=\"not-bold\"><pre>");
			writer.print(Utils.escapeHtml("Server Component Details"));
			writer.print("</span></th>");

			// Right column: Details from AdminTestUtil.getServerComponentsDetails() without bold formatting
			writer.print("<td colspan=\"6\"><pre>");
			writer.print(Utils.escapeHtml(AdminTestUtil.getServerComponentsDetails()));
			writer.print("</pre></td>");
			writer.print(GlobalConstants.TRTR);
			
			// Left column: "Tested Component Details" with central alignment
			writer.print("<th style=\"text-align: center; vertical-align: middle;\" colspan=\"2\"><span class=\"not-bold\"><pre>");
			writer.print(Utils.escapeHtml("End Points used"));
			writer.print("</span></th>");

			// Right column: Details from AdminTestUtil.getServerComponentsDetails() without bold formatting
			writer.print("<td colspan=\"6\"><pre>");
			writer.print(Utils.escapeHtml(GlobalMethods.getComponentDetails()));
			writer.print("</pre></td>");
			writer.print(GlobalConstants.TRTR);
			
			if (GlobalMethods.getServerErrors().equals("No server errors")) {
				writer.print("<tr><th colspan=\"8\"><span class=\"not-bold\"><pre>");
			} else {
				writer.print(
						"<tr style=\"background-color: red;\"><th colspan=\"8\"><span class=\"not-bold\"><pre>");
			}
			writer.print(Utils.escapeHtml("Server Errors " + "\n" + GlobalMethods.getServerErrors()));
			writer.print("</pre></span>");
			writer.print(GlobalConstants.TRTR);

			writer.print("<tr>");
			writer.print("<th>Test Scenario</th>");
			writer.print("<th>Prerequisite Step</th>");
			writer.print("<th># Total</th>");
			writer.print("<th># Passed</th>");
			writer.print("<th># Skipped</th>");
			writer.print("<th># Failed</th>");
			if (ConfigManager.reportIgnoredTestCases()) {
				writer.print("<th># Ignored</th>");
			}
			writer.print("<th>Execution Time (ms)</th>");
//			writer.print("<th>Included Groups</th>");
//			writer.print("<th>Excluded Groups</th>");
			writer.print(GlobalConstants.TR);

			for (TestResult testResult : suiteResult.getTestResults()) {
				int passedTests = testResult.getPassedTestCount();
				int ignoredTests = testResult.getIgnoredTestCount();
				int skippedTests = testResult.getSkippedTestCount();
				int failedTests = testResult.getFailedTestCount();
				long duration = testResult.getDuration();
				int totalTests = 0;
				totalTests = ConfigManager.reportIgnoredTestCases()
						? (passedTests + skippedTests + failedTests + ignoredTests)
						: (passedTests + skippedTests + failedTests);
				// All test cases are ignored. Hence don't print anything in the report.
				if (totalTests < 1) 
					continue;

				writer.print("<tr");
//				if ((testIndex % 2) == 1) {
//					writer.print(" class=\"stripe\"");
//				}
				writer.print(">");

				buffer.setLength(0);
				

				
				writeTableData(buffer.append("<a href=\"#t").append(testIndex).append("\">")
						.append(Utils.escapeHtml(testResult.getTestName())).append("</a>").toString(), "num");
				
				writeTableData(testResult.getPrerequisiteTest(), "num");
				
				writeTableData(integerFormat.format(totalTests), "num");
				writeTableData(integerFormat.format(passedTests), (passedTests > 0 ? "num green-bg" : "num"));
				writeTableData(integerFormat.format(skippedTests), (skippedTests > 0 ? "num orange-bg" : "num"));
				writeTableData(integerFormat.format(failedTests), (failedTests > 0 ? GlobalConstants.NUMATTN : "num"));
				// print the ignored column based on the flag
				if (ConfigManager.reportIgnoredTestCases()) {
					writeTableData(integerFormat.format(ignoredTests), (ignoredTests > 0 ? "num grey-bg" : "num"));
				}
				writeTableData(decimalFormat.format(duration), "num");
//				writeTableData(testResult.getIncludedGroups());
//				writeTableData(testResult.getExcludedGroups());

				writer.print(GlobalConstants.TR);

				totalPassedTests += passedTests;
				totalSkippedTests += skippedTests;
				totalFailedTests += failedTests;
				totalIgnoredTests += ignoredTests;
				totalDuration += duration;

				testIndex++;
			}
		}

		if (testIndex > 1) {
			writer.print("<tr>");
			writer.print("<th>Total</th>");
			writeTableHeader("", "num");
			if (ConfigManager.reportIgnoredTestCases()) {
				writeTableHeader(integerFormat
						.format(totalPassedTests + totalSkippedTests + totalFailedTests + totalIgnoredTests), "num");
			} else {
				writeTableHeader(integerFormat.format(totalPassedTests + totalSkippedTests + totalFailedTests), "num");
			}
			writeTableHeader(integerFormat.format(totalPassedTests), (totalPassedTests > 0 ? "num green-bg" : "num"));
			writeTableHeader(integerFormat.format(totalSkippedTests),
					(totalSkippedTests > 0 ? "num orange-bg" : "num"));
			writeTableHeader(integerFormat.format(totalFailedTests),
					(totalFailedTests > 0 ? GlobalConstants.NUMATTN : "num"));
			if (ConfigManager.reportIgnoredTestCases()) {
				writeTableHeader(integerFormat.format(totalIgnoredTests),
						(totalIgnoredTests > 0 ? "num grey-bg" : "num"));
			}
			writeTableHeader(decimalFormat.format(totalDuration), "num");
			writer.print(GlobalConstants.TR);
		}

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
							|| subSetString.contains(GlobalConstants.SERVICE_NOT_DEPLOYED)) {
						if (containsAny(throwable.getMessage(), subSetString)) {
							// Add only results which are skipped due to feature not supported
							testResultsSubList.add(result);
						} else {
							// Skip the test result
						}
					} else { // Service not deployed. Hence skipping the testcase // skipped
						if (!throwable.getMessage().contains(GlobalConstants.FEATURE_NOT_SUPPORTED)
								&& !throwable.getMessage().contains(GlobalConstants.SERVICE_NOT_DEPLOYED)) {
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
	protected void writeScenarioSummary() {
		writer.print("<table id='summary'>");
		writer.print("<thead>");
		writer.print("<tr>");
		writer.print("<th>Test Case</th>");
		writer.print("<th>Test Case Description</th>");
		writer.print("<th>Execution Time (ms)</th>");
		writer.print(GlobalConstants.TR);
		writer.print("</thead>");

		int testIndex = 0;
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			
			

			for (TestResult testResult : suiteResult.getTestResults()) {
				int passedTests = testResult.getPassedTestCount();
				int ignoredTests = testResult.getIgnoredTestCount();
				int skippedTests = testResult.getSkippedTestCount();
				int failedTests = testResult.getFailedTestCount();
				int totalTests = 0;
				totalTests = ConfigManager.reportIgnoredTestCases()
						? (passedTests + skippedTests + failedTests + ignoredTests)
						: (passedTests + skippedTests + failedTests);
				// All test cases are ignored. Hence don't print anything in the report.
				if (totalTests < 1) 
					continue;
				writer.print("<tbody id=\"t");
				writer.print(testIndex);
				writer.print("\">");

				String testName = Utils.escapeHtml(testResult.getTestName());

				if (ConfigManager.reportIgnoredTestCases()) {
					scenarioIndex += writeScenarioSummary(testName + " &#8212; Ignored",
							testResult.getIgnoredTestResults(), "ignored", scenarioIndex);
				}
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Failed", testResult.getFailedTestResults(),
						"failed", scenarioIndex);
				scenarioIndex += writeScenarioSummary(testName + " &#8212; Skipped", testResult.getSkippedTestResults(),
						"skipped", scenarioIndex);
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

	/**
	 * Writes the scenario summary for the results of a given state for a single
	 * test.
	 */
	private int writeScenarioSummary(String description, List<ClassResult> classResults, String cssClassPrefix,
			int startingScenarioIndex) {
		int scenarioCount = 0;
		if (!classResults.isEmpty()) {
			writer.print("<tr><th colspan=\"4\">");
			writer.print(description);
			writer.print(GlobalConstants.TRTR);

			int scenarioIndex = startingScenarioIndex;
			int classIndex = 0;
			for (ClassResult classResult : classResults) {
				String cssClass = cssClassPrefix + ((classIndex % 2) == 0 ? "even" : "odd");

				buffer.setLength(0);

				int scenariosPerClass = 0;
				int methodIndex = 0;
				for (MethodResult methodResult : classResult.getMethodResults()) {
					List<ITestResult> results = methodResult.getResults();
					int resultsCount = results.size();
					assert resultsCount > 0;

					ITestResult firstResult = results.iterator().next();
					String testCaseDescription = getTestCaseDescription(firstResult);
					String methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());
					long start = firstResult.getStartMillis();
					long duration = firstResult.getEndMillis() - start;

					if (methodIndex > 0) {
						buffer.append(GlobalConstants.TRCLASS).append(cssClass).append("\">");

					}
					buffer.append("<td style=\"text-align:center;\"><a href=\"#m").append(scenarioIndex).append("\">")
							.append(methodName).append("</a></td>").append("<td style=\"text-align:center;\">")
							.append(testCaseDescription).append("</td>")
							.append("<td style=\"text-align:center;\" rowspan=\"").append(resultsCount).append("\">")
							.append(duration).append("</td></tr>");

					scenarioIndex++;

					for (int i = 1; i < resultsCount; i++) {
						buffer.append("<tr class=\"").append(cssClass).append("\">")
								.append("<td style=\"text-align:center;\"><a href=\"#m").append(scenarioIndex)
								.append("\">").append(methodName).append("</a></td></tr>");
						scenarioIndex++;
					}

					scenariosPerClass += resultsCount;
					methodIndex++;
				}

				writer.print(GlobalConstants.TRCLASS);
				writer.print(cssClass);
				writer.print("\">");
//				writer.print(GlobalConstants.TDROWSPAN);
//				writer.print(scenariosPerClass);
//				writer.print("\">");
//				writer.print(Utils.escapeHtml(classResult.getClassName()));
//				writer.print("</td>");
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
	protected void writeScenarioDetails() {
		int scenarioIndex = 0;
		for (SuiteResult suiteResult : suiteResults) {
			for (TestResult testResult : suiteResult.getTestResults()) {
				writer.print("<h2>");
				writer.print(Utils.escapeHtml(testResult.getTestName()));
				writer.print("</h2>");

				if (ConfigManager.reportIgnoredTestCases()) {
					scenarioIndex += writeScenarioDetails(testResult.getIgnoredTestResults(), scenarioIndex);
				}
				scenarioIndex += writeScenarioDetails(testResult.getFailedConfigurationResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getFailedTestResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getSkippedConfigurationResults(), scenarioIndex);
				scenarioIndex += writeScenarioDetails(testResult.getSkippedTestResults(), scenarioIndex);
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
		writer.print(label);
		writer.print("</h3>");

		writer.print("<table class=\"result\">");

		Object[] parameters = result.getParameters();
		int parameterCount = (parameters == null ? 0 : parameters.length);
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
		private final String prerequisiteTest;
		private final List<ClassResult> failedConfigurationResults;
		private final List<ClassResult> failedTestResults;
		private final List<ClassResult> skippedConfigurationResults;
		private final List<ClassResult> skippedTestResults;
		private final List<ClassResult> passedTestResults;
		private final List<ClassResult> ignoredTestResults;
		private final int ignoredTestCount;
		private final int failedTestCount;
		private final int skippedTestCount;
		private final int passedTestCount;
		private final long duration;
		private final String includedGroups;
		private final String excludedGroups;

		public TestResult(ITestContext context) {
			testName = context.getName();
			prerequisiteTest = context.getCurrentXmlTest().getParameter("prerequisite") == null ? ""
					: context.getCurrentXmlTest().getParameter("prerequisite");

			Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
			Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
			Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
//			Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
			Set<ITestResult> skippedTests = getResultsSubSet(context.getSkippedTests().getAllResults(), GlobalConstants.SKIPPED);
			Set<ITestResult> ignoredTests =  getResultsSubSet(context.getSkippedTests().getAllResults(), GlobalConstants.IGNORED_SUBSET_STRING);
			Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

			failedConfigurationResults = groupResults(failedConfigurations);
			failedTestResults = groupResults(failedTests);
			skippedConfigurationResults = groupResults(skippedConfigurations);
			skippedTestResults = groupResults(skippedTests);
			ignoredTestResults = groupResults(ignoredTests);
			passedTestResults = groupResults(passedTests);

			failedTestCount = failedTests.size();
			skippedTestCount = skippedTests.size();
			passedTestCount = passedTests.size();
			ignoredTestCount = ignoredTests.size();

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
		
		public String getPrerequisiteTest() {
			return prerequisiteTest;
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
