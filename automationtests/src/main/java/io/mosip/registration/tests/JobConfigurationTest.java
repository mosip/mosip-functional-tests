/**
 * 
 */
package io.mosip.registration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.repositories.JobConfigRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.config.JobConfigurationService;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.ConstantValues;

/**
 * @author Tabish Khan
 * 
 *         Defines the tests for the services exposed for configuring jobs
 *
 */
public class JobConfigurationTest extends BaseConfiguration implements ITest {

	@Autowired
	JobConfigurationService jobConfigurationService;

	@Autowired
	private JobConfigRepository jobConfigRepository;

	@Autowired
	private GlobalParamService globalParamService;

	// private static GlobalParamService globalParamService;

	private static ApplicationContext applicationContext;
	protected static String mTestCaseName = "";
	private static final Logger logger = AppConfig.getLogger(JobConfigurationTest.class);
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@BeforeClass
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	/**
	 * Method to initiate jobs
	 */
	@Test(priority = 1)
	public void initiateJobsTest() {
		try {
			mTestCaseName = "regClient_JobConfigurationService_initiateJobs";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			jobConfigurationService.initiateJobs();
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Implements the test method for configuring jobs
	 */
	@Test(priority = 2)
	public void startSchedulerTest() {
		try {
			mTestCaseName = "regClient_JobConfigurationService_startScheduler";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			ResponseDTO response = jobConfigurationService.startScheduler();
			assertNotNull(response);
			assertEquals("BATCH_JOB_START_SUCCESS_MESSAGE", response.getSuccessResponseDTO().getMessage());
			assertNull(response.getErrorResponseDTOs());
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Test method to obtain the details of currently running jobs
	 */
	@Test(priority = 3)
	public void getCurrentRunningJobDetailsTest() {
		/**
		 * Create Copy of data
		 */
		try {
			mTestCaseName = "regClient_JobConfigurationService_getCurrentRunningJobDetails";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			List<SyncJobDef> data = jobConfigRepository.findAll();
			if (data.size() > 0) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("s1", false);
				params.put("s2", true);
				jobConfigRepository
						.createQueryUpdateOrDelete("UPDATE SyncJobDef  SET IS_ACTIVE=:s1 where IS_ACTIVE=:s2", params);
				ResponseDTO response = jobConfigurationService.getCurrentRunningJobDetails();
				assertEquals(response.getErrorResponseDTOs().get(0).getCode(), RegistrationConstants.ERROR);
				assertEquals(response.getErrorResponseDTOs().get(0).getMessage(),
						RegistrationConstants.NO_JOBS_RUNNING);
				for (SyncJobDef def : data) {
					jobConfigRepository.save(def);
				}
				response = jobConfigurationService.getCurrentRunningJobDetails();
			} else {
				logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
						"Nothing is there no Testing can be done");
			}
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Test method to stop currently active scheduler
	 */
	@Test(priority = 4)
	public void stopSchedulerTest() {
		try {
			mTestCaseName = "regClient_JobConfigurationService_stopScheduler";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			ResponseDTO stopSchedulerResponseDto = null;
			ResponseDTO response = jobConfigurationService.startScheduler();
			if ("SYNC_DATA_PROCESS_ALREADY_STARTED".equals(response.getErrorResponseDTOs().get(0).getMessage())) {
				stopSchedulerResponseDto = jobConfigurationService.stopScheduler();
			}
			SuccessResponseDTO successResponse = stopSchedulerResponseDto.getSuccessResponseDTO();
			String code = successResponse.getCode();
			String message = successResponse.getMessage();
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					code + ", message: " + message);
			assertEquals("BATCH_JOB_STOP_SUCCESS_MESSAGE", message);
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Test method to obtain details of the last competed sync job
	 */
	@Test(priority = 5)
	public void getLastCompletedSyncJobsTest() {
		try {
			mTestCaseName = "regClient_JobConfigurationService_getLastCompletedSyncJobs";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			jobConfigurationService.initiateJobs();
			jobConfigurationService.executeAllJobs();

			ResponseDTO respDto = jobConfigurationService.getLastCompletedSyncJobs();
			SuccessResponseDTO successResponse = respDto.getSuccessResponseDTO();
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					successResponse.getMessage());
			Map<String, Object> jobs = successResponse.getOtherAttributes();
			assertTrue(jobs.size() > 0);
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Test method to validate getting sync job transaction
	 */
	@Test(priority = 6)
	public void getSyncJobsTransactionTest() {
		try {
			mTestCaseName = "regClient_JobConfigurationService_getSyncJobsTransaction";
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"test case Name:" + mTestCaseName);
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"getSyncJobsTransactionTest");
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"applicationContext:: " + applicationContext);
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					"globalParamService:: " + globalParamService);
			applicationContext.setApplicationMap(globalParamService.getGlobalParams());

			jobConfigurationService.initiateJobs();
			jobConfigurationService.executeAllJobs();

			ResponseDTO respDto = jobConfigurationService.getSyncJobsTransaction();
			SuccessResponseDTO successRespDto = respDto.getSuccessResponseDTO();
			// List<ErrorResponseDTO> errors = respDto.getErrorResponseDTOs();
			assertTrue(successRespDto.getOtherAttributes().size() > 0);
			logger.info(this.getClass().getName(), ConstantValues.MODULE_ID, ConstantValues.MODULE_NAME,
					successRespDto.getMessage());
			// logger.info(errors.toString());
		} catch (Exception exception) {
			logger.debug("JOB CONFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, JobConfigurationTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
