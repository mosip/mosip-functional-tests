package mosip.api.util;

import org.junit.rules.TestWatcher;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

public class MyTestListenerAdapter extends TestListenerAdapter  {
	
//	@Override
//	public void onTestStart(ITestResult iTestResult) {
//    
//		testWatcher.set(extent.createTest(iTestResult.getTestContext().getAttribute("testName").toString()));
//	}
	@Override
	public void onTestFailure(ITestResult result) {
		if (result.getMethod().getRetryAnalyzer() != null) {
			MyRetryAnalyzer retryAnalyzer = (MyRetryAnalyzer) result.getMethod().getRetryAnalyzer();

			if (retryAnalyzer.isRetryAvailable()) {
				result.setStatus(ITestResult.SKIP);
			} else {
				result.setStatus(ITestResult.FAILURE);
			}
			Reporter.setCurrentTestResult(result);
		}
	}
}