package io.mosip.registration.tests;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.repositories.GlobalParamRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

public class GlobalParamServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	GlobalParamRepository globalParamRepository;

	protected static String mTestCaseName = "";

	/**
	 * Declaring CenterID,StationID global
	 */
	private static final Logger logger = AppConfig.getLogger(GlobalParamServiceTest.class);
	private static String centerID = null;
	private static String stationID = null;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	@Test
	public void regClient_GlobalParamService_getGlobalParamsValues() {
		/*
		 * Fetch Value from DB
		 */
		mTestCaseName = "regClient_GlobalParamService_getGlobalParamsValues";
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"test case Name:" + mTestCaseName);
		List<GlobalParamName> globalParamsDB = globalParamRepository.findByIsActiveTrueAndValIsNotNull();
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamsDB.forEach(param -> globalParamMap.put(param.getName(), param.getVal()));
		io.mosip.registration.context.ApplicationContext.getInstance().getApplicationLanguageBundle();
		/*
		 * Fetch Value from Service ,Assert if Global is loaded properly
		 */
		HashMap<String, Object> globalParam = new LinkedHashMap<>();
		globalParam = (LinkedHashMap<String, Object>) globalParamService.getGlobalParams();

		Assert.assertTrue(globalParam.equals(globalParamMap));
	}

	/**
	 * SyncConfigData checking if Sync Config is running or not
	 */
	@Test
	public void regClient_GlobalParamService_synchConfigData() {

		mTestCaseName = "regClient_GlobalParamService_synchConfigData";
		ResponseDTO response = globalParamService.synchConfigData(true);
		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable() && globalParamService.getGlobalParams().isEmpty()) {
			Assert.assertEquals(response.getErrorResponseDTOs().get(0).getCode(), RegistrationConstants.ERROR);
			Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(),
					RegistrationConstants.GLOBAL_CONFIG_ERROR_MSG);
		} else {
			/*
			 * Assert.assertTrue(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE
			 * .equals(response.getSuccessResponseDTO().getMessage()) ||
			 * RegistrationConstants.MASTER_SYNC_FAILURE_MSG
			 * .equals(response.getSuccessResponseDTO().getMessage()));
			 */
			Assert.assertEquals(response.getSuccessResponseDTO().getCode(), RegistrationConstants.ALERT_INFORMATION);

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
			f.set(baseTestMethod, GlobalParamServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}
}
