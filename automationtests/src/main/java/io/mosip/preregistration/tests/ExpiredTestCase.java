package io.mosip.preregistration.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import com.mchange.util.AssertException;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class ExpiredTestCase extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(IntegrationScenarios.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String regCenterId;
	public String preID = null;
	public Response response = null;

	protected static String testCaseName = "";
	public static String folder = "preReg";
	String updateSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
	public PreregistrationDAO dao = new PreregistrationDAO();
	int i=0;

	@BeforeMethod(alwaysRun = true)
	public void login(Method method) {
		testCaseName = "preReg_ExpiredBatchJob_" + method.getName();
		preID  = expiredPreRegIds.get(i);
		i++;
		if (!lib.isValidToken(batchJobToken)) {
			batchJobToken =lib. batchToken();
		}
	}

	@Test(groups = { "IntegrationScenarios" })
	public void cancelAppointmentForExpiredApplication() {
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID, batchJobToken);
		lib.getPreRegistrationStatus(preID, batchJobToken);
		Response CancelBookingAppointmentResponse = lib.CancelBookingAppointment(preID, batchJobToken);
		String msg = CancelBookingAppointmentResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(msg, "Appointment cannot be canceled");

	}

	/**
	 * @author Ashish Update pre Registration data for expired application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void updatePreRegistrationDataForExpiredApplication() {
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID, batchJobToken);
		Response updateResponse = lib.updatePreReg(preID, batchJobToken);
		String updatePreId = updateResponse.jsonPath().get("response.preRegistrationId").toString();
		lib.compareValues(updatePreId, preID);
		lib.CancelBookingAppointment(preID, batchJobToken);
	}

	/**
	 * @author Ashish Book appointment for expired application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void bookAppointmentForExpiredApplication() throws FileNotFoundException, IOException, ParseException {
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID, batchJobToken);
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID, batchJobToken);
		lib.compareValues(getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString(), "Expired");
		Response avilibityResponse = lib.FetchCentre(batchJobToken);
		Response reBookAnAppointmentResponse = lib.BookAppointment(avilibityResponse, preID, batchJobToken);
		lib.compareValues(reBookAnAppointmentResponse.jsonPath().get("response.bookingMessage").toString(),
				"Appointment booked successfully");
	}

	/**
	 * @author Ashish Changing status to expired using batch job service
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void expiredBatchJobService() {
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID, batchJobToken);
		String status = getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString();
		lib.compareValues(status, "Expired");

	}
	/**
	 * Batch job service for expired application
	 */
	@Test
	public void batchJobForExpiredApplication() {
		lib.FetchAppointmentDetails(preID, batchJobToken);
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID, batchJobToken);
		String statusCode = null;
		try {
			statusCode = getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString();

		} catch (NullPointerException e) {
			Assert.assertTrue(false, "falied to get status from get preregistartion status response");
		}
		lib.compareValues(statusCode, "Expired");

	}

	@Test
	public void updateDemographicDetailsOfExpiredAppointment() {
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, preID, batchJobToken);
		lib.compareValues(preID,
				updateDemographicDetailsResponse.jsonPath().get("response.preRegistrationId").toString());
		Response getPreRegistrationData = lib.getPreRegistrationData(preID, batchJobToken);
	}

	@Override
	public String getTestName() {
		return testCaseName;
	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "PreReg_ExpiredTestCase_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
	
}
