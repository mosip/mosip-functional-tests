package io.mosip.registration.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class SyncStatusValidatorServiceTest extends BaseConfiguration implements ITest {
	@Autowired
	private SyncStatusValidatorService syncstatusvalidatorservice;

	@Autowired
	private GlobalParamService globalParamService;

	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	private static final Logger logger = AppConfig.getLogger(SyncStatusValidatorServiceTest.class);

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	protected static String mTestCaseName = "";

	@BeforeClass(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@Test
	public void regClient_SyncStatusValidatorService_validatingErrorResponse() {
		try {
		ApplicationContext.map()
		.put(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME,"0");
		mTestCaseName = "regClient_SyncStatusValidatorService_validate_validatingErrorResponse";
		ResponseDTO result = syncstatusvalidatorservice.validateSyncStatus();
		commonUtil.verifyAssertNotNull(result);
		}

		catch (Exception exception) {
			logger.debug("SYNC_STATUS VALIDATOR SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	@Test
	public void regClient_SyncStatusValidatorService_validatingSucessResponse() {
		try {
		ApplicationContext.map()
		.put(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME,"0");
		mTestCaseName = "regClient_SyncStatusValidatorService_validate_validatingSucessResponse";
		ResponseDTO result = syncstatusvalidatorservice.validateSyncStatus();
		commonUtil.verifyAssertNotNull(result);
		}

		catch (Exception exception) {
			logger.debug("SYNC_STATUS VALIDATOR SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
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
			f.set(baseTestMethod, SyncStatusValidatorServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
