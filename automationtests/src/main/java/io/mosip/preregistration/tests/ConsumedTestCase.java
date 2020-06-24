package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class ConsumedTestCase extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(BatchJob.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	public ApplicationLibrary applnLib = new ApplicationLibrary();
	public PreregistrationDAO dao = new PreregistrationDAO();
	int count = 0;

	/**
	 * @author Ashish Consumed booked appointment
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void consumedBookedAppointment() {
		String status = lib.getConsumedStatus(preID);
		String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(preID);
		String actualDocumentId = lib.getDocumentIdOfConsumedApplication(preID);
		lib.compareValues(actualDocumentId, documentId.get(preID));
		lib.compareValues(status, "Consumed");
		lib.compareValues(actualRegCenterId, regCenterId.get(preID));
	}

	/**
	 * @author Ashish Changing status to Consumed using batch job service
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void consumedBatchJobService() {
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID, batchJobToken);
		lib.compareValues(getPreRegistrationDataResponse.jsonPath().get("errors[0].message").toString(),
				"No data found for the requested pre-registration id");
		String status = lib.getConsumedStatus(preID);
		String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(preID);
		String actualDocumentId = lib.getDocumentIdOfConsumedApplication(preID);
		lib.compareValues(actualDocumentId, documentId.get(preID));
		lib.compareValues(status, "Consumed");
		lib.compareValues(actualRegCenterId, regCenterId.get(preID));
	}

	/**
	 * @author Ashish retrive PreRegistration data for consumed Application
	 */

	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegDataConsumedApplication() {
		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("errors[0].message").toString(),
				"No data found for the requested pre-registration id");

	}

	/**
	 * @author Ashish Consumed multiple pre registration ids
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void consumedMultiplePRID() {
		List<String> preRegistrationIds = new ArrayList<String>(consumedPreRegIds);
		for (String PreRegId : preRegistrationIds) {
			Response getPreRegistrationStatusResposne = lib.getPreRegistrationStatus(PreRegId, batchJobToken);
			lib.compareValues(getPreRegistrationStatusResposne.jsonPath().get("errors[0].message").toString(),
					"No data found for the requested pre-registration id");
		}

	}

	@Test(groups = { "IntegrationScenarios" })
	public void cosumedExpiredAppointment() {
		String expPreId = lib.expiredPreId;
		String status = lib.getConsumedStatus(expPreId);
		String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(expPreId);
		String actualDocumentId = lib.getDocumentIdOfConsumedApplication(expPreId);
		lib.compareValues(actualDocumentId, documentId.get(expPreId));
		lib.compareValues(status, "Consumed");
		lib.compareValues(actualRegCenterId, regCenterId.get(expPreId));
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@BeforeMethod(alwaysRun = true)
	public void login(Method method) {
		testCaseName = "preReg_BatchJob_" + method.getName();
		preID=consumedPreRegIds.get(count);
		if (!lib.isValidToken(batchJobToken)) {
			batchJobToken =lib. batchToken();
		}

	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_ConsumedTestCase_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}

}
