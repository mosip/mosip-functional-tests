package mosip.api.scripts;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.support.SoapUIException;

import mosip.api.services.BaseTestCase;
import mosip.api.util.MyTestListenerAdapter;
import mosip.api.util.TestNGCustom;
import mosip.api.util.LogConsoleHandler;
import mosip.api.report.CustomizedEmailableReport;

@Listeners({ TestNGCustom.class, MyTestListenerAdapter.class})
public class ida_module1  implements  ITest{
	List<String> failureReason = new ArrayList<String>();
	String request="";
	String response="";
	String testCaseName="";
	private ThreadLocal<String> testName = new ThreadLocal<>();
	Logger log = Logger.getLogger("Logger");
	  @DataProvider(name = "testSuite")
	  public  Object[][] credentials() throws XmlException, IOException, SoapUIException {
		  WsdlProject project = new WsdlProject(BaseTestCase.BaseTestCase("kernel"));
		  WsdlTestSuite ts=project.getTestSuiteByName("smsnotification-msg91");
		  System.out.println(ts.getTestCaseCount());
		  Object[][] dataController = new Object[ts.getTestCaseCount()][];
			int i=0;
			for(TestCase tc:ts.getTestCaseList())
			{
				Object[] data= new Object[2];
				data[0]= tc.getName();
				data[1]= ts;
				dataController[i]= data;
				i++;
			}
			return dataController;
	  }
	
	@Test(groups = { "smoke", "progression"} , priority=0, dataProvider="testSuite")
public void init(String TestCase,WsdlTestSuite ts) throws XmlException, IOException, SoapUIException {
log.info("Running Test Case : " + TestCase);
this.testCaseName=TestCase;
WsdlTestCase tc = ts.getTestCaseByName(TestCase);
String tc_name = tc.getName();
new LogConsoleHandler(tc_name);
TestRunner tcRunner = tc.run(new PropertiesMap(), false);

//tcRunner.getRunContext().getTestRunner().getReason().get
if(!tcRunner.getStatus().toString().equals(TestRunner.Status.FINISHED.toString())){
	System.out.println("Here");
  WsdlTestCaseRunner runner = (WsdlTestCaseRunner)tcRunner;

for(Map.Entry<String, TestStep> testStep:runner.getTestCase().getTestSteps().entrySet()){
	System.out.println(testStep.getValue());
	com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep restRequest = (com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep)testStep.getValue();
	response = restRequest.getHttpRequest().getResponse().getContentAsString();
	request=restRequest.getHttpRequest().getRequestContent();
}
  for(TestStepResult s:runner.getResults()){
	  for(String message: s.getMessages()){
		  System.out.println(message);
		  failureReason.add(message);
	  }
  }
  System.out.println(runner.getResults() +"Jyoti this is response");
}
Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);
int tc1 = ts.getTestCaseCount();
log.info("Count is : " + tc1);
log.info("Response body is : " + tcRunner.getReason());
}
	  @BeforeMethod
	   public void BeforeMethod1(Method method, Object[] testData){
		  System.out.println("BEfore Method : "+testData.toString());
	       testName.set(testData[0].toString());
	   }
	 
	  
	  @AfterMethod
	  public void afterMethod(ITestResult result)
	  {
	      try
	   {
	      if(result.getStatus() == ITestResult.SUCCESS)
	      {

	          //Do something here
	          System.out.println("passed **********");
	    	  result.setAttribute("request", request);
	    	  result.setAttribute("response", response);
	    	  request="";
              response="";
	      }

	      else if(result.getStatus() == ITestResult.FAILURE)
	      {
	    	  //result.setThrowable(arg0);
	           //Do something here
	    	  
	    	  result.setAttribute("SoapUIError", new ArrayList(failureReason));
	    	  result.setAttribute("request", request);
	    	  result.setAttribute("response", response);
	    	  failureReason.clear();
	          System.out.println("Failed ***********");
               request="";
               response="";
	      }

	       else if(result.getStatus() == ITestResult.SKIP ){

	          System.out.println("Skiped***********");
	    	  result.setAttribute("request", request);
	    	  result.setAttribute("response", response);
	    	  request="";
              response="";
	       }
	  }
	     catch(Exception e)
	     {
	       e.printStackTrace();
	     }

	  }
	   @Override
	   public String getTestName() {
	       return testName.get();
	   }
	
	/*@Test(groups = { "smoke", "progression"} , priority=0, dataProvider="testSuite")
public void init1(String TestCase,WsdlTestSuite ts) throws XmlException, IOException, SoapUIException {
log.info("Running Test Case : " + TestCase);

this.testCaseName=TestCase;
WsdlTestCase tc = ts.getTestCaseByName(TestCase);
String tc_name = tc.getName();
new LogConsoleHandler(tc_name);
TestRunner tcRunner = tc.run(new PropertiesMap(), false);
Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);
int tc1 = ts.getTestCaseCount();
log.info("Count is : " + tc1);
log.info("Response body is : " + tcRunner.getReason());
}
	  @BeforeMethod
	   public void BeforeMethod(Method method, Object[] testData){
	       testName.set(method.getName() + "_" + testData[0]);
	   }
	 
	   @Override
	   public String getTestName() {
	       return testName.get();
	   }*/
//	@Test(groups = { "smoke", "progression"} , priority=1)
//public void Trigger_OTP_smokeTest() throws XmlException, IOException, SoapUIException {
//	
//WsdlTestCase tc = this.ts.getTestCaseByName("Trigger_OTP_smokeTest");
//String tc_name = tc.getName();
//new LogConsoleHandler(tc_name);
//// run a specific testCase
//TestRunner tcRunner = tc.run(new PropertiesMap(), false);
////System.out.println(tcRunner.getStatus());
//int tc1 = ts.getTestCaseCount();
//log.info("Count is : " + tc1);
//log.info("Response body is : " + tcRunner.getReason());
//Assert.assertEquals(tcRunner.getStatus(), TestRunner.Status.FINISHED);
//}	

}
