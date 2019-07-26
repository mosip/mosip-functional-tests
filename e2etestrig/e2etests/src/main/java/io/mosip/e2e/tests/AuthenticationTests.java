package io.mosip.e2e.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.e2e.AuthenticationE2E;
import io.mosip.e2e.util.Stagevalidations;
import io.mosip.e2e.util.TestRigException;
import io.mosip.testrunner.MosipTestRunner;

public class AuthenticationTests {
	Stagevalidations stageValidations=new Stagevalidations();
	protected static String testCaseName = "";
	Map<String,String> authenticationTestResults=new HashMap<String,String>();
	@DataProvider(name="uinMap")
	public Object[][] getUin() {
		Object[][] uin = new Object[EndToEndRun.uinMap.size()][2];
		Set<Entry<String, String>> entries = EndToEndRun.uinMap.entrySet();
		Iterator<Entry<String, String>> entriesIterator = entries.iterator();

		int i = 0;
		while(entriesIterator.hasNext()){

		    Map.Entry mapping = (Map.Entry) entriesIterator.next();

		    uin[i][0] = mapping.getKey();
		    uin[i][1] = mapping.getValue();

		    i++;
		}
		return uin;
		
	}
	@Test(dataProvider="uinMap",priority=1)
	 public void createUinFile(Object[] test) throws TestRigException {
		System.out.println(test[1].toString());
		System.out.println(test[0].toString());
		try {
			String filePath=MosipTestRunner.getGlobalResourcePath()+"/ida/TestData/RunConfig/uin.properties";
			OutputStream outStream=new FileOutputStream(new File(filePath));
			Properties prop=new Properties();
			prop.setProperty(test[0].toString(),test[1].toString());
			prop.store(outStream, null);
			outStream.close();
			authenticationTestResults=AuthenticationE2E.performAuthE2E();
			Assert.assertTrue(true);
		} catch (Exception e) {
			throw new TestRigException("Exception In Registration Processor ---> ");
		}
		 
	 }
	@Test(dataProvider="uinMap",priority=2)
	public void uinAuthentication_OtpGeneration(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.OtpGeneration");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=3)
	public void uinAuthentication_DemographicAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.DemographicAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=4)
	public void uinAuthentication_EkycWithOtpAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.EkycWithOtpAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=5)
	public void uinAuthentication_EkycWithBiometricAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.EkycWithBiometricAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=6)
	public void uinAuthentication_InternalBiometricAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.InternalBiometricAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=7)
	public void uinAuthentication_OtpAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.OtpAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@Test(dataProvider="uinMap",priority=8)
	public void uinAuthentication_BiometricAuthentication(Object[] test) throws TestRigException{
		String testStatus=authenticationTestResults.get("io.mosip.authentication.tests.BiometricAuthentication");
		if(testStatus.equalsIgnoreCase("PASS"))
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
	}
	
	@BeforeMethod
	public void getTestCaseName(Object[] test) {
		Object[] uinObject=(Object[])test[0];
		uinObject[1].toString();
		testCaseName=uinObject[1].toString();
	}
	
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			testCaseName ="E2ERun"+"_"+testCaseName+"_"+baseTestMethod.getMethodName();
			f.set(baseTestMethod, AuthenticationTests.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
