package mosip.api.report;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

public class MyTestListenerAdapter extends TestListenerAdapter {
	@Override
	public void onTestStart(ITestResult result) {
	result.setAttribute("name", result.getParameters()[0]);
	System.out.println(result.getParameters()[0]);
		
	}

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