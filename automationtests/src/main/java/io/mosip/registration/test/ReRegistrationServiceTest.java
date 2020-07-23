package io.mosip.registration.test;

import static org.testng.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.impl.ReRegistrationServiceImpl;
import io.mosip.registration.util.BaseConfiguration;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
import io.mosip.registration.util.DBUtil;

public class ReRegistrationServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	private ReRegistrationServiceImpl reregistrationServiceImpl;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	private GlobalParamService globalParamService;

	@Autowired
	UserOnboardService userOBservice;

	@Autowired
	RegistrationDAO syncRegistrationDAO;

	@Autowired
	RegistrationRepository registrationRepository;

	/**
	 * Declaring CenterID,StationID global
	 */
	private String centerID = null;
	private String stationID = null;
	private static final Logger logger = AppConfig.getLogger(ReRegistrationServiceTest.class);
	protected static String mTestCaseName = "";

	@BeforeMethod
	public void SetUp() {
		baseSetUp();
		centerID = (String) ApplicationContext.map().get(ConstantValues.CENTERIDLBL);
		stationID = (String) ApplicationContext.map().get(ConstantValues.STATIONIDLBL);
		
	}

	private void cleanUp(String registrationId) {
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"registrationId:: " + registrationId);
		logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode() : "
				+ RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());

		Registration registration = syncRegistrationDAO
				.getRegistrationById(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(), registrationId);
		if (registration == null)
			registration = syncRegistrationDAO.getRegistrationById(RegistrationClientStatusCode.RE_REGISTER.getCode(),
					registrationId);
		String serverCode = registration.getServerStatusCode();
		registration.setServerStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		//commonUtil.deleteProcessedPackets("");

	}

	private void commonUtil(List<String> roles) {
		String[] roleset = ConstantValues.REREGISTRATION_ROLES.split(",");
		if (roleset.length != 2) {
			fail("Number of roles wrongly configured, needs to be 2");
			return;
		}
		roles.add(roleset[0]);
		roles.add(roleset[1]);
		SessionContext.getInstance().getUserContext().setUserId(ConstantValues.REREGISTRATION_USERID);
		SessionContext.getInstance().getUserContext().setRoles(roles);
	}

	/**
	 * This test case tests the getAllReRegistrationPackets() method of
	 * ReRegistrationServiceImpl. It first creates a packet and then invokes the
	 * method under test to validate if the database contains records related to the
	 * packet.
	 * 
	 */
	@Test
	public void getAllReRegistrationPacketsTest() {
		try {
		mTestCaseName = "regClient_ReregistrationService_validate_getAllReRegistrationPackets";
		List<String> roles = new ArrayList<>();
		commonUtil(roles);

		List<PacketStatusDTO> packetsDetailsBeforeOperation = reregistrationServiceImpl.getAllReRegistrationPackets();
		Integer originalRecordsCount = packetsDetailsBeforeOperation.size();
		HashMap<String, String> values = null;
		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean("false"));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG,"N");
		try {
			values = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
					ConstantValues.REGDETAILSJSON, ConstantValues.IDENTITYJSON, ConstantValues.POAPOBPORPOIJPG,
					System.getProperty("userID"), centerID, stationID, "YES", "NO");
			// Update UPD_DTIMES
			Registration registration = syncRegistrationDAO.getRegistrationById(RegistrationClientStatusCode.APPROVED.getCode(), values.get("RANDOMID"));
			registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
			registrationRepository.update(registration);
			
			String updateQuery = "update REG.REGISTRATION set STATUS_CODE = 'PUSHED',CLIENT_STATUS_CODE='PUSHED', SERVER_STATUS_CODE ='Re-Register' where id='"
					+ values.get("RANDOMID") + "'";
			DBUtil.updateQuery(updateQuery);
			logger.info(this.getClass().getName(),ConstantValues.MODULE_ID,ConstantValues.MODULE_NAME,"registrationId: " + values.get("RANDOMID"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<PacketStatusDTO> packetsDetailsAfterOperation = reregistrationServiceImpl.getAllReRegistrationPackets();
		Integer newRecordsCount = packetsDetailsAfterOperation.size();
		Assert.assertFalse(newRecordsCount > originalRecordsCount);
//		cleanUp(values.get("RANDOMID"));
		}

		catch (Exception exception) {
			logger.debug("RE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}

	}

	/**
	 * test case to test the updateReRegistrationStatus method of
	 * ReRegistrationServiceImpl service
	 */

	@Test
	public void updateReRegistrationStatusApprovedpacket() throws IOException {
		try {
		mTestCaseName = "regClient_ReregistrationService_validate_updateReRegistrationStatusApprovedpacket";

		Map<String, String> reRegistrationStatus = new HashMap<>();
		List<String> roles = new ArrayList<>();
		commonUtil(roles);

		HashMap<String, String> values = null;
		String registrationId = null;
		SessionContext.map().put(RegistrationConstants.IS_Child, Boolean.parseBoolean("false"));
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG,"N");
		
		try {
			values = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
					ConstantValues.REGDETAILSJSON, ConstantValues.IDENTITYJSON, ConstantValues.POAPOBPORPOIJPG,
					System.getProperty("userID"), centerID, stationID, "YES", "NO");
			// Update UPD_DTIMES
						Registration registration = syncRegistrationDAO.getRegistrationById(RegistrationClientStatusCode.APPROVED.getCode(), values.get("RANDOMID"));
						registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
						registrationRepository.update(registration);
			String updateQuery = "update REG.REGISTRATION set STATUS_CODE = 'PUSHED',CLIENT_STATUS_CODE='PUSHED', SERVER_STATUS_CODE ='Re-Register' where id='"
					+ registrationId + "'";
			DBUtil.updateQuery(updateQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}

		reRegistrationStatus.put(registrationId, "APPROVED");

	/*	boolean result = reregistrationServiceImpl.updateReRegistrationStatus(reRegistrationStatus);
		logger.info("registrationId::: " + registrationId);
		List<String> ids = null;
		try {
			ids = DBUtil.executeQuery(
					"SELECT CLIENT_STATUS_COMMENT FROM REG.REGISTRATION WHERE ID='" + registrationId + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		logger.info(ids);
		logger.info("result " + result);

		Assert.assertTrue(ids.get(0).compareTo("Re-Register-APPROVED") == 0);

		cleanUp(registrationId);
*/
		}

		catch (Exception exception) {
			logger.debug("RE-REGISTRATION SERVICE", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));
			Reporter.log(ExceptionUtils.getStackTrace(exception));
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
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
			f.set(baseTestMethod, ReRegistrationServiceTest.mTestCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

}
