package io.mosip.customReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import io.mosip.admin.fw.util.AdminTestUtil;

public class CustomTestNGReporter implements IReporter {
	
	private static final Logger logger = Logger.getLogger(CustomTestNGReporter.class);
	private static final String emailableReportTemplateFile = "D:\\sprint_10\\mosip\\automationtests\\src\\test\\java\\io\\mosip\\customReport\\customize-emailable-report-template.html";
	
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		FileWriter fileWriter = null;
		try
		{
			String customReportTemplateStr = this.readEmailabelReportTemplate();
			
			String customReportTitle = this.getCustomReportTitle("Custom TestNG Report");
			
			String customSuiteSummary = this.getTestSuiteSummary(suites);
			
			String customTestMethodSummary = this.getTestMehodSummary(suites);
			
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$TestNG_Custom_Report_Title\\$", customReportTitle);
			
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Summary\\$", customSuiteSummary);
			
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Detail\\$", customTestMethodSummary);
			
			File targetFile = new File(outputDirectory + "/custom-emailable-report.html");
			fileWriter = new FileWriter(targetFile);
			fileWriter.write(customReportTemplateStr);
			
		}catch(Exception e)
		{
			logger.error(e.getStackTrace());
		}
		finally {
			AdminTestUtil.closeFileWriter(fileWriter);
		}
	}
	
	private String readEmailabelReportTemplate()
	{
		StringBuffer retBuf = new StringBuffer();
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		try {
		
			File file = new File(this.emailableReportTemplateFile);
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			
			String line = bufferedReader.readLine();
			while(line!=null)
			{
				retBuf.append(line);
				line = bufferedReader.readLine();
			}
			
		} catch (NullPointerException |IOException e) {
			logger.error(e.getStackTrace());
		}finally{
			AdminTestUtil.closeBufferedReader(bufferedReader);
			AdminTestUtil.closeFileReader(fileReader);
		}
		return retBuf.toString();
	}
	
	private String getCustomReportTitle(String title)
	{
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(title + " " + this.getDateInStringFormat(new Date()));
		return retBuf.toString();
	}
	
	private String getTestSuiteSummary(List<ISuite> suites) {
		StringBuffer retBuf = new StringBuffer();

		try {
			int totalTestCount = 0;
			int totalTestPassed = 0;
			int totalTestFailed = 0;
			int totalTestSkipped = 0;

			for (ISuite tempSuite : suites) {
				retBuf.append("<tr><td colspan=11><center><b></b></center></td></tr>");

				Map<String, ISuiteResult> testResults = tempSuite.getResults();

				for (ISuiteResult result : testResults.values()) {

					retBuf.append("<tr>");

					ITestContext testObj = result.getTestContext();

					totalTestPassed = testObj.getPassedTests().getAllMethods().size();
					totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
					totalTestFailed = testObj.getFailedTests().getAllMethods().size();

					totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;

					retBuf.append("<td>");
					retBuf.append(testObj.getName());
					retBuf.append("</td>");

					retBuf.append("<td>");
					retBuf.append(totalTestCount);
					retBuf.append("</td>");

					retBuf.append("<td bgcolor=green>");
					retBuf.append(totalTestPassed);
					retBuf.append("</td>");

					retBuf.append("<td bgcolor=yellow>");
					retBuf.append(totalTestSkipped);
					retBuf.append("</td>");

					retBuf.append("<td bgcolor=red>");
					retBuf.append(totalTestFailed);
					retBuf.append("</td>");

					Date startDate = testObj.getStartDate();
					retBuf.append("<td>");
					retBuf.append(this.getDateInStringFormat(startDate));
					retBuf.append("</td>");

					Date endDate = testObj.getEndDate();
					retBuf.append("<td>");
					retBuf.append(this.getDateInStringFormat(endDate));
					retBuf.append("</td>");

					long deltaTime = endDate.getTime() - startDate.getTime();
					String deltaTimeStr = this.convertDeltaTimeToString(deltaTime);
					retBuf.append("<td>");
					retBuf.append(deltaTimeStr);
					retBuf.append("</td>");

					retBuf.append("<td>");
					retBuf.append(this.stringArrayToString(testObj.getIncludedGroups()));
					retBuf.append("</td>");

					retBuf.append("<td>");
					retBuf.append(this.stringArrayToString(testObj.getExcludedGroups()));
					retBuf.append("</td>");

					retBuf.append("</tr>");
				}
			}
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		return retBuf.toString();
	}

	private String getDateInStringFormat(Date date)
	{
		StringBuffer retBuf = new StringBuffer();
		if(date==null)
		{
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}
	
	private String convertDeltaTimeToString(long deltaTime)
	{
		StringBuffer retBuf = new StringBuffer();
		
		long milli = deltaTime;
		
		long seconds = deltaTime / 1000;
		
		long minutes = seconds / 60;
		
		long hours = minutes / 60;
		
		retBuf.append(hours + ":" + minutes + ":" + seconds + ":" + milli);
		
		return retBuf.toString();
	}
	
	private String getTestMehodSummary(List<ISuite> suites) {
		StringBuffer retBuf = new StringBuffer();

		try {
			for (ISuite tempSuite : suites) {
				retBuf.append("<tr><td colspan=7><center><b></b></center></td></tr>");

				Map<String, ISuiteResult> testResults = tempSuite.getResults();

				for (ISuiteResult result : testResults.values()) {

					ITestContext testObj = result.getTestContext();

					String testName = testObj.getName();

					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodReport(testName, testFailedResult, false, false);
					retBuf.append(failedTestMethodInfo);

					IResultMap testSkippedResult = testObj.getSkippedTests();
					String skippedTestMethodInfo = this.getTestMethodReport(testName, testSkippedResult, false, true);
					retBuf.append(skippedTestMethodInfo);

					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodReport(testName, testPassedResult, true, false);
					retBuf.append(passedTestMethodInfo);
				}
			}
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		return retBuf.toString();
	}
	
	private String getTestMethodReport(String testName, IResultMap testResultMap, boolean passedReault, boolean skippedResult)
	{
		String apiName="";
		String description="";
		StringBuffer retStrBuf = new StringBuffer();
		
		String resultTitle = testName;
		
		String color = "green";
		
		if(skippedResult)
		{
			resultTitle += " - Skipped ";
			color = "yellow";
		}else
		{
			if(!passedReault)
			{
				resultTitle += " - Failed ";
				color = "red";
			}else
			{
				resultTitle += " - Passed ";
				color = "green";
			}
		}
		
		retStrBuf.append("<tr bgcolor=" + color + "><td colspan=7><center><b>" + resultTitle + "</b></center></td></tr>");
			
		Set<ITestResult> testResultSet = testResultMap.getAllResults();
			
		for(ITestResult testResult : testResultSet)
		{
			String testClassName = "";
			String testMethodName = "";
			String startDateStr = "";
			String executeTimeStr = "";
			String paramStr = "";
			String reporterMessage = "";
			String exceptionMessage = "";
			
			testClassName = testResult.getTestClass().getName();
				
			testMethodName = testResult.getMethod().getMethodName();
			testMethodName=testMethodName.substring(testMethodName.indexOf(":")+1, testMethodName.lastIndexOf(":"));
			long startTimeMillis = testResult.getStartMillis();
			startDateStr = this.getDateInStringFormat(new Date(startTimeMillis));
				
			long deltaMillis = testResult.getEndMillis() - testResult.getStartMillis();
			executeTimeStr = this.convertDeltaTimeToString(deltaMillis);
				
			Object paramObjArr[] = testResult.getParameters();
			String testParm=testResult.getName();
			apiName=testParm.substring(0, testParm.indexOf(":"));
			description=testParm.substring(testParm.lastIndexOf(":")+1);
			paramStr+=description;
				
			List<String> repoterMessageList = Reporter.getOutput(testResult);
			for(String tmpMsg : repoterMessageList)				
			{
				reporterMessage += tmpMsg;
				reporterMessage += " ";
			}
				
			Throwable exception = testResult.getThrowable();
			if(exception!=null)
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				exception.printStackTrace(pw);
				StackTraceElement[] element=exception.getStackTrace();
				exceptionMessage = element[0].toString();
			}
			
			retStrBuf.append("<tr bgcolor=" + color + ">");
			
			retStrBuf.append("<td>");
			retStrBuf.append(testClassName);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(testMethodName);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(startDateStr);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(executeTimeStr);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(paramStr);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(apiName);
			retStrBuf.append("</td>");
			
			retStrBuf.append("<td>");
			retStrBuf.append(exceptionMessage);
			retStrBuf.append("</td>");
			
			retStrBuf.append("</tr>");

		}
		
		return retStrBuf.toString();
	}
	
	private String stringArrayToString(String strArr[])
	{
		StringBuffer retStrBuf = new StringBuffer();
		if(strArr!=null)
		{
			for(String str : strArr)
			{
				retStrBuf.append(str);
				retStrBuf.append(" ");
			}
		}
		return retStrBuf.toString();
	}

}