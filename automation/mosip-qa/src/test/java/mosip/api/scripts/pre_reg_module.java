package mosip.api.scripts;



//import static org.hamcrest.CoreMatchers.instanceOf;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.teststeps.JdbcRequestTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.JdbcTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.support.SoapUIException;
import mosip.api.report.*;
import mosip.api.services.BaseTestCase;

@Listeners({ TestNGCustom.class, MyTestListenerAdapter.class, CustomizedEmailableReport.class })
public class pre_reg_module implements ITest {

	List<String> failureReason = new ArrayList<String>();
	String request = "";
	String response = "";
	String testCaseName = "";
	private ThreadLocal<String> testName = new ThreadLocal<String>();
	Logger log = Logger.getLogger(ida_module.class);

	@DataProvider(name = "testSuite")
	public Object[][] credentials() throws XmlException, IOException, SoapUIException {
		WsdlProject project = new WsdlProject(BaseTestCase.BaseTestCase("pre-reg"));
		 List<Object[]> data = ReusableFunction.runCustomTestCasesInProject(project, "smoke");
		Object[][] dataController = new Object[data.size()][];
		int i = 0;
		for (Object[] insideData : data) {
			Object[] dataCollector = new Object[2];
			dataCollector[0] = insideData[0];
			dataCollector[1] = insideData[1];
			dataController[i] = dataCollector;
			i++;
		}
		return dataController;
	}

	@DataProvider(name = "TestSuite1")
	public Object[][] credentials1() throws XmlException, IOException, SoapUIException {
		WsdlProject project = new WsdlProject(BaseTestCase.BaseTestCase("pre-reg"));
		List<Object[]> dataRegression = ReusableFunction.runCustomTestCasesInProject(project, "progression");
		Object[][] dataControllerProg = new Object[dataRegression.size()][];
		int j = 0;
		for (Object[] insideData : dataRegression) {
			Object[] dataCollector = new Object[2];
			dataCollector[0] = insideData[0];
			dataCollector[1] = insideData[1];
			dataControllerProg[j] = dataCollector;
			j++;
		}
		return dataControllerProg;
	}

	@DataProvider(name = "TestSuite2")
	public Object[][] credentials2() throws XmlException, IOException, SoapUIException {
		WsdlProject project = new WsdlProject(BaseTestCase.BaseTestCase("pre-reg"));
		List<Object[]> dataRegression = ReusableFunction.runCustomTestCasesInProject(project, "regression");
		Object[][] dataControllerReg = new Object[dataRegression.size()][];
		int j = 0;
		for (Object[] insideData : dataRegression) {
			Object[] dataCollector = new Object[2];
			dataCollector[0] = insideData[0];
			dataCollector[1] = insideData[1];
			dataControllerReg[j] = dataCollector;
			j++;
		}
		return dataControllerReg;
	}

	@Test(groups = { "smoke" }, priority = 0, dataProvider = "testSuite")
	public void init(String TestCase, WsdlTestSuite ts) throws XmlException, IOException, SoapUIException {
		log.info("Running Test Case : " + TestCase);
		this.testCaseName = TestCase;
		WsdlTestCase tc = ts.getTestCaseByName(TestCase);
		if (tc == null) {
			System.out.println("Here");
		} else if (tc != null) {
			System.out.println("Hie Smoke...." + tc.getName());
			String tc_name = tc.getName();
			new LogConsoleHandler(tc_name);
			TestRunner tcRunner = tc.run(new PropertiesMap(), false);
			WsdlTestCaseRunner runner = (WsdlTestCaseRunner) tcRunner;
			// tcRunner.getRunContext().getTestRunner().getReason().get
			if (!tcRunner.getStatus().toString().equals(TestRunner.Status.FINISHED.toString())) {
				for (TestStepResult s : runner.getResults()) {
					for (String message : s.getMessages()) {
						failureReason.add(message);
						break;
					}
				}
				System.out.println(runner.getResults());
			}
			for (Map.Entry<String, TestStep> testStep : runner.getTestCase().getTestSteps().entrySet()) {
				try {
					System.out.println("TestStep VAlue------>" + testStep.getValue());
					if (testStep.getValue() instanceof WsdlGroovyScriptTestStep) {
						System.out.println("It's working fine ----------");
						com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep groovyRequest = (WsdlGroovyScriptTestStep) testStep
								.getValue();
						response = "";
						request = "";

					} else if (testStep.getValue() instanceof JdbcRequestTestStep) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcRequestTestStep jdbcRequest = (JdbcRequestTestStep) testStep
								.getValue();
						response = jdbcRequest.getResponseContent();
						request = jdbcRequest.getProperty("XMLRequest").toString();
					} else if (testStep.getValue() instanceof JdbcTestStepResult) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcTestStepResult jdbcResult = (JdbcTestStepResult) testStep
								.getValue();
						response = jdbcResult.getResponseContent();
						request = jdbcResult.getProperty("XMLRequest").toString();
					} else {
						com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep restRequest = (com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep) testStep
								.getValue();
						response = restRequest.getHttpRequest().getResponse().getContentAsString();

						request = restRequest.getProperty("RawRequest").getValue();
						System.out.println("request check--------->" + request);
					}
				} catch (NullPointerException exp) {
					continue;
				}
			}
			int tc1 = ts.getTestCaseCount();
			log.info("Count is : " + tc1);
			log.info("Request is : " + this.request);
			log.info("Response body is : " + tcRunner.getReason());
			if (this.failureReason.size() > 1) {
				log.info("HTTP Request  Failed  with Reason : " + this.failureReason);
			}
			Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);

		}

	}

	@BeforeMethod(alwaysRun = true)
	public void BeforeMethod(Method method, Object[] testData) {
		System.out.println("BEfore Method : " + testData.toString());
		testName.set(testData[0].toString());
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult result) {
		try {
			if (result.getStatus() == ITestResult.SUCCESS) {

				// Do something here
				System.out.println("passed **********");
				// result.setAttribute("request", request);
				// result.setAttribute("response", response);
				request = "";
				response = "";
				System.out.println("Result is" + result);
			}

			else if (result.getStatus() == ITestResult.FAILURE) {
				// result.setThrowable(arg0);
				// Do something here

				result.setAttribute("SoapUIError", new ArrayList(failureReason));
				result.setAttribute("request", request);
				result.setAttribute("response", response);
				failureReason.clear();
				System.out.println("Failed ***********");
				request = "";
				response = "";
				System.out.println("Result is" + result);
			}

			else if (result.getStatus() == ITestResult.SKIP) {

				System.out.println("Skiped***********");
				result.setAttribute("request", request);
				result.setAttribute("response", response);
				request = "";
				response = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getTestName() {
		return testName.get();
	}

	// @Test(groups = { "smoke", "progression"} , priority=1)
	// public void Trigger_OTP_smokeTest() throws XmlException, IOException,
	// SoapUIException {
	//
	// WsdlTestCase tc = this.ts.getTestCaseByName("Trigger_OTP_smokeTest");
	// String tc_name = tc.getName();
	// new LogConsoleHandler(tc_name);
	//// run a specific testCase
	// TestRunner tcRunner = tc.run(new PropertiesMap(), false);
	//// System.out.println(tcRunner.getStatus());
	// int tc1 = ts.getTestCaseCount();
	// log.info("Count is : " + tc1);
	// log.info("Response body is : " + tcRunner.getReason());
	// Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);
	// }

	@Test(groups = { "progression" }, priority = 0, dataProvider = "TestSuite1")
	public void init1(String TestCase, WsdlTestSuite ts) throws XmlException, IOException, SoapUIException {
		log.info("Running Test Case : " + TestCase);
		this.testCaseName = TestCase;
		WsdlTestCase tc = ts.getTestCaseByName(TestCase);
		if (tc == null) {
			System.out.println("Here");
		} else if (tc != null) {
			System.out.println("Hie Smoke...." + tc.getName());
			String tc_name = tc.getName();
			new LogConsoleHandler(tc_name);
			TestRunner tcRunner = tc.run(new PropertiesMap(), false);
			WsdlTestCaseRunner runner = (WsdlTestCaseRunner) tcRunner;
			// tcRunner.getRunContext().getTestRunner().getReason().get
			if (!tcRunner.getStatus().toString().equals(TestRunner.Status.FINISHED.toString())) {
				for (TestStepResult s : runner.getResults()) {
					for (String message : s.getMessages()) {
						failureReason.add(message);
						break;
					}
				}
				System.out.println(runner.getResults());
			}
			for (Map.Entry<String, TestStep> testStep : runner.getTestCase().getTestSteps().entrySet()) {
				try {
					System.out.println("TestStep VAlue------>" + testStep.getValue());
					if (testStep.getValue() instanceof WsdlGroovyScriptTestStep) {
						System.out.println("It's working fine ----------");
						com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep groovyRequest = (WsdlGroovyScriptTestStep) testStep
								.getValue();
						response = "";
						request = "";

					} else if (testStep.getValue() instanceof JdbcRequestTestStep) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcRequestTestStep jdbcRequest = (JdbcRequestTestStep) testStep
								.getValue();
						response = jdbcRequest.getResponseContent();
						request = jdbcRequest.getProperty("XMLRequest").toString();
					} else if (testStep.getValue() instanceof JdbcTestStepResult) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcTestStepResult jdbcResult = (JdbcTestStepResult) testStep
								.getValue();
						response = jdbcResult.getResponseContent();
						request = jdbcResult.getProperty("XMLRequest").toString();
					} else {
						com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep restRequest = (com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep) testStep
								.getValue();
						response = restRequest.getHttpRequest().getResponse().getContentAsString();

						request = restRequest.getProperty("RawRequest").getValue();
						System.out.println("request check--------->" + request);
					}
				} catch (NullPointerException exp) {
					continue;
				}
			}
			int tc1 = ts.getTestCaseCount();
			log.info("Count is : " + tc1);
			log.info("Request is : " + this.request);
			log.info("Response body is : " + tcRunner.getReason());
			if (this.failureReason.size() > 1) {
				log.info("HTTP Request  Failed  with Reason : " + this.failureReason);
			}
			Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);

		}

	}

	//
	@Test(groups = { "regression" }, priority = 0, dataProvider = "TestSuite2")
	public void init2(String TestCase, WsdlTestSuite ts) throws XmlException, IOException, SoapUIException {
		log.info("Running Test Case : " + TestCase);
		this.testCaseName = TestCase;
		WsdlTestCase tc = ts.getTestCaseByName(TestCase);
		if (tc == null) {
			System.out.println("Here");
		} else if (tc != null) {
			System.out.println("Hie Smoke...." + tc.getName());
			String tc_name = tc.getName();
			new LogConsoleHandler(tc_name);
			TestRunner tcRunner = tc.run(new PropertiesMap(), false);
			WsdlTestCaseRunner runner = (WsdlTestCaseRunner) tcRunner;
			// tcRunner.getRunContext().getTestRunner().getReason().get
			if (!tcRunner.getStatus().toString().equals(TestRunner.Status.FINISHED.toString())) {
				for (TestStepResult s : runner.getResults()) {
					for (String message : s.getMessages()) {
						failureReason.add(message);
						break;
					}
				}
				System.out.println(runner.getResults());
			}
			for (Map.Entry<String, TestStep> testStep : runner.getTestCase().getTestSteps().entrySet()) {
				try {
					System.out.println("TestStep VAlue------>" + testStep.getValue());
					if (testStep.getValue() instanceof WsdlGroovyScriptTestStep) {
						System.out.println("It's working fine ----------");
						com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep groovyRequest = (WsdlGroovyScriptTestStep) testStep
								.getValue();
						response = "";
						request = "";

					} else if (testStep.getValue() instanceof JdbcRequestTestStep) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcRequestTestStep jdbcRequest = (JdbcRequestTestStep) testStep
								.getValue();
						response = jdbcRequest.getResponseContent();
						request = jdbcRequest.getProperty("XMLRequest").toString();
					} else if (testStep.getValue() instanceof JdbcTestStepResult) {
						com.eviware.soapui.impl.wsdl.teststeps.JdbcTestStepResult jdbcResult = (JdbcTestStepResult) testStep
								.getValue();
						response = jdbcResult.getResponseContent();
						request = jdbcResult.getProperty("XMLRequest").toString();
					} else {
						com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep restRequest = (com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep) testStep
								.getValue();
						response = restRequest.getHttpRequest().getResponse().getContentAsString();

						request = restRequest.getProperty("RawRequest").getValue();
						System.out.println("request check--------->" + request);
					}
				} catch (NullPointerException exp) {
					continue;
				}
			}
			int tc1 = ts.getTestCaseCount();
			log.info("Count is : " + tc1);
			log.info("Request is : " + this.request);
			log.info("Response body is : " + tcRunner.getReason());
			if (this.failureReason.size() > 1) {
				log.info("HTTP Request  Failed  with Reason : " + this.failureReason);
			}
			Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);

		}

	}

}
