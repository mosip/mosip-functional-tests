/**
 * 
 */
package io.mosip.registration.tests;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_DATA_HASH_FILE_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_META_JSON_NAME;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.ZipCreationServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DataProvider;
import junit.framework.Assert;

/**
 * @author Gaurav Sharan
 *
 *         Test class to test the functionality of the services exposed by
 *         ZipCreationService
 */

public class ZipCreationServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	private ZipCreationServiceImpl zipCreationService;
	private static Logger logger = AppConfig.getLogger(ZipCreationServiceTest.class);
	protected static String mTestCaseName = "";
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	/**
	 * Method to initialize the data and parameters needed before the test class is
	 * invoked
	 */

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
	}

	/**
	 * Test case to check whether RegBasecheckedException is thrown in case of empty
	 * input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test(expectedExceptions = RegBaseCheckedException.class)
	public void createPacketcheckedExceptionEmptyInputsTest() throws RegBaseCheckedException {
		try {
			mTestCaseName = "regClient_ZipCreationService_createPacketcheckedExceptionEmptyInputsTest";
			RegistrationDTO registrationDTO = new RegistrationDTO();
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();
			zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		}

		catch (RegBaseCheckedException exception) {
			logger.debug("ZIP-CREATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test case to check whether RegBasecheckedException is thrown in case of empty
	 * files input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void createPacketcheckedExceptionEmptyFileInputTest() throws RegBaseCheckedException {
		try {
			mTestCaseName = "regClient_ZipCreationService_createPacketcheckedExceptionEmptyFileInputTest";
			RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();
			zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		}

		catch (RegBaseCheckedException exception) {
			logger.debug("ZIP-CREATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Assert.assertTrue(true);
			
		}
	}

	/**
	 * Test case to check whether RegBasecheckedException is thrown in case of null
	 * input
	 * 
	 * @throws RegBaseCheckedException
	 */

	@Test
	public void createPacketcheckedExceptionNullInputTest() throws RegBaseCheckedException {
		try {
			mTestCaseName = "regClient_ZipCreationService_createPacketcheckedExceptionNullInputTest";
			RegistrationDTO registrationDTO = null;
			Map<String, byte[]> filesGeneratedForPacket = null;
			zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		}

		catch (RegBaseCheckedException exception) {
			logger.debug("ZIP-CREATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Assert.assertTrue(true);
		}
	}

	/**
	 * 
	 * 
	 * Test case to check whether RegBasecheckedException is thrown in case of
	 * partial input passed
	 * 
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void createPacketcheckedExceptionIncompleteInputTest()
			throws RegBaseUncheckedException, RegBaseCheckedException {
		 try {
		mTestCaseName = "regClient_ZipCreationService_createPacketcheckedExceptionIncompleteInputTest";
		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		RegistrationDTO registrationDTO = new RegistrationDTO();

		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		 }

		
		  catch (RegBaseCheckedException exception) {
		  logger.debug("ZIP-CREATION SERVICE", "AUTOMATION", "REG",
		  ExceptionUtils.getStackTrace(exception));
	
		  Reporter.log(ExceptionUtils.getStackTrace(exception)); }
		 
	}

	/**
	 * This test checks if RegBaseCheckedException is thrown in case of invalid
	 * input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	// (enabled = false)

	public void createPacketCheckedExceptionTest() {
		mTestCaseName = "regClient_ZipCreationService_createPacketCheckedExceptionTest";
		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

		// complete file input
		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		filesGeneratedForPacket.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		filesGeneratedForPacket.put(PACKET_DATA_HASH_FILE_NAME, "HASHCode".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, "packet_osi_hash".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME,
				"applicant_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME,
				"introducer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, "officer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME,
				"supervisor_bio_cbeff".getBytes());

		RegistrationDTO registrationDTO = null;
		try {
			registrationDTO = DataProvider.getPacketDTO();
		} catch (RegBaseCheckedException regBaseCheckedException) {
			logger.info("ZIP-CREATION SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			Reporter.log(ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
		try {
			zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		} catch (Exception exception) {
			logger.debug("USER ONBOARD SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
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
			f.set(baseTestMethod, ZipCreationServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

}
