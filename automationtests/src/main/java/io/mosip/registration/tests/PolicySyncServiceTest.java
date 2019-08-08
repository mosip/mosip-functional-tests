package io.mosip.registration.tests;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.repositories.PolicySyncRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.TestCaseReader;
import io.mosip.registration.util.TestDataGenerator;

public class PolicySyncServiceTest extends BaseConfiguration implements ITest {
	@Autowired
	NotificationService notificationService;
	@Autowired
	TestDataGenerator dataGenerator;
	@Autowired
	TestCaseReader testCaseReader;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	PolicySyncService policySyncService;
	@Autowired
	private PolicySyncRepository policySyncDAO;

	private static final Logger logger = AppConfig.getLogger(PolicySyncServiceTest.class);
	private static final String serviceName = "NotificationServices";
	private static final String subServiceName = "EmailNotificationService";
	protected static String mTestCaseName = "";

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeMethod
	public void preSetUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@Test
	public void regClient_PolicySyncService_verifyingCode() {
		try {
			mTestCaseName = "regClient_PolicySyncService_verifyingCode";
			ResponseDTO response = policySyncService.fetchPolicy();
			assertEquals(response.getSuccessResponseDTO().getCode(), "INFORMATION");
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"Validate date:" + policySyncDAO.findByRefIdOrderByValidTillDtimesDesc(null));
		} catch (Exception exception) {
			logger.debug("POLICY SYNC SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	@Test
	public void regClient_PolicySyncService_verifyingMessage() {
		try {

			mTestCaseName = "regClient_PolicySyncService_verifyingMessage";
			ResponseDTO response = policySyncService.fetchPolicy();
			assertEquals(response.getSuccessResponseDTO().getMessage(), "SYNC_SUCCESS");
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"Validate date:" + policySyncDAO.findByRefIdOrderByValidTillDtimesDesc(null));
		} catch (Exception exception) {
			logger.debug("POLICY SYNC SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, PolicySyncServiceTest.mTestCaseName);
		} catch (Exception exception) {
			logger.debug("POLICY SYNC SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
