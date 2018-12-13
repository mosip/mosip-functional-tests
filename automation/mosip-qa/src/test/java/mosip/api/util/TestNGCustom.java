package mosip.api.util;

import org.testng.TestListenerAdapter;

//import upgradeapi.util.MetricsManager;

public class TestNGCustom extends TestListenerAdapter {
	/*private static Logger logger = LogManager.getLogger(TestNGCustom.class);

	@Override
	public void onTestFailure(ITestResult tr) {
		// long responsetime=tr.getEndMillis()-tr.getStartMillis();

		if (tr.getTestContext().getAttribute("ResponseTime") != null) {
			Long responsetime = (long) tr.getTestContext().getAttribute("ResponseTime");
			String className = tr.getTestClass().getName();
			className = className.substring(className.indexOf(".") + 1);
			MetricsManager metrics = new upgradeapi.util.MetricsManager();
			metrics.registerGraphiteReporterStatus(className, tr.getMethod().getMethodName(), "Fail");
			metrics.registerGraphiteReporterResponseTime(className, tr.getMethod().getMethodName(), responsetime);
		} else {

			logger.info("ResponseTime not set to be sent to grafana for this test");
		}
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		// long responsetime=tr.getEndMillis()-tr.getStartMillis();

		if (tr.getTestContext().getAttribute("ResponseTime") != null) {
			Long responsetime = (long) tr.getTestContext().getAttribute("ResponseTime");
			String className = tr.getTestClass().getName();
			className = className.substring(className.indexOf(".") + 1);
			MetricsManager metrics = new upgradeapi.util.MetricsManager();
			metrics.registerGraphiteReporterStatus(className, tr.getMethod().getMethodName(), "Skipped");
			metrics.registerGraphiteReporterResponseTime(className, tr.getMethod().getMethodName(), responsetime);
		} else {

			logger.info("ResponseTime not set to be sent to grafana for this test");
		}
	}

	@Override
	public void onTestSuccess(ITestResult tr) {

		// long responsetime=tr.getEndMillis()-tr.getStartMillis();

		if (tr.getTestContext().getAttribute("ResponseTime") != null) {
			Long responsetime = (long) tr.getTestContext().getAttribute("ResponseTime");
			String className = tr.getTestClass().getName();
			className = className.substring(className.indexOf(".") + 1);
			MetricsManager metrics = new upgradeapi.util.MetricsManager();
			metrics.registerGraphiteReporterStatus(className, tr.getMethod().getMethodName(), "Passed");
			metrics.registerGraphiteReporterResponseTime(className, tr.getMethod().getMethodName(), responsetime);

		} else {

			logger.info("ResponseTime not set to be sent to grafana for this test");
		}

	}*/

}
