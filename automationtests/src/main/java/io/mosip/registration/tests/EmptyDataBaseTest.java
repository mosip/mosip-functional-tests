package io.mosip.registration.tests;

import java.io.File;
import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;

public class EmptyDataBaseTest implements ITest {
	
	private static final Logger logger = AppConfig.getLogger(EmptyDataBaseTest.class);
	protected static String mTestCaseName = "regClient_EmptyDataBaseCheck";

	
	
	@Test
	public void validateEmailTest() {
		mTestCaseName = mTestCaseName;
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME, "TestCaseName:"+mTestCaseName);
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME, "Deleting syned DB");
		String path = "./reg";
		new CommonUtil().recursiveDelete(new File(path));

		File sourceFolder = new File("./src/main/resources/db");

		// Target directory where files should be copied
		File destinationFolder = new File("./");

		// Call Copy function
		new CommonUtil().copyFolder(sourceFolder, destinationFolder);
		logger.debug(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME, "Copying empty DB sucess");
		
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, EmptyDataBaseTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
