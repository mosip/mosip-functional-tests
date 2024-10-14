package io.mosip.testrig.apirig.dataprovider.mds;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.dataprovider.models.JWTTokenModel;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSDevice;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSDeviceCaptureModel;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSRCaptureModel;
import io.mosip.testrig.apirig.dataprovider.util.CommonUtil;
import io.mosip.testrig.apirig.utils.RestClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class MDSClient implements MDSClientInterface {

	private static final Logger logger = LoggerFactory.getLogger(MDSClient.class);
	public int port;
	public static String MDSURL = "http://127.0.0.1:";

	public MDSClient(int port) {
		if (port == 0)
			this.port = 4501;
		else
			this.port = port;
	}

	public void setProfile(String profile, int port, String contextKey) {

		String url = MDSURL + port + "/admin/profile";
		JSONObject body = new JSONObject();
		body.put("profileId", profile);
		body.put("type", "Biometric Device");

		try {
			logger.info("Inside Setprofile");
//			HttpRCapture capture = new HttpRCapture(url);
//			capture.setMethod("POST");
//			String response = RestClient.rawHttp(capture, body.toString());
			Response response = RestClient.post(url, body.toString());
//			JSONObject respObject = new JSONObject(response.as);

		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	}

	// Type ->"Finger", "Iris", "Face"
	public List<MDSDevice> getRegDeviceInfo(String type) {

		List<MDSDevice> devices = null;

		String url = MDSURL + port;
		JSONObject body = new JSONObject();
		body.put("type", type);
		Response response = given().contentType(ContentType.JSON).body(body.toString()).post(url);
		if (response.getStatusCode() == 200) {
			String resp = response.getBody().asString();

			if (resp != null) {
				JSONArray deviceArray = new JSONArray(resp);
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					devices = objectMapper.readValue(deviceArray.toString(),
							objectMapper.getTypeFactory().constructCollectionType(List.class, MDSDevice.class));

				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return devices;
	}

	// capture = mds.captureFromRegDevice(exceptionDevice, capture,
	// DataProviderConstants.MDS_DEVICE_TYPE_EXCEPTION_PHOTO,
	// null, 60, exceptionDevice.getDeviceSubId().get(0),
	// port,contextKey,bioexceptionlist);

	public MDSRCaptureModel captureFromRegDevice(MDSDevice device, MDSRCaptureModel rCaptureModel, String type,
			String bioSubType, int reqScore, String deviceSubId, int port, String contextKey,
			List<String> listbioexception) {
		String mosipVersion = null;
		try {
			mosipVersion = "1.2.1-SNAPSHOT";
		} catch (Exception e) {

		}

		if (rCaptureModel == null)
			rCaptureModel = new MDSRCaptureModel();

		String url = MDSURL + port + "/capture";
		JSONObject jsonReq = new JSONObject();
		jsonReq.put("env", "Developer");
		jsonReq.put("purpose", "Registration");
		jsonReq.put("specVersion", "0.9.5");
		jsonReq.put("timeout", "120000");
		jsonReq.put("captureTime", CommonUtil.getUTCDateTime(null));
		jsonReq.put("domainUri", "automated");
		jsonReq.put("transactionId", "123456789123");
		JSONObject bio = new JSONObject();
		bio.put("type", type);

		bio.put("count", 1);
		bio.put("deviceSubId", deviceSubId);

		if (type.equalsIgnoreCase("finger")) {

			switch (deviceSubId) {
			case "1":
				bio.put("count", 4);

				break;
			case "2":
				bio.put("count", 4);

				break;
			case "3":
				bio.put("count", 2);
				break;
			}

		}

		bio.put("requestedScore", reqScore);
		// bio.put("deviceId", Integer.valueOf(device.getDeviceId()));
		bio.put("deviceId", device.getDeviceId());
		if (listbioexception != null && !listbioexception.isEmpty())
			bio.put("exception", listbioexception);

		JSONArray arr = new JSONArray();
		arr.put(bio);
		jsonReq.put("bio", arr);
		/*
		 * Response response = given() .contentType(ContentType.JSON)
		 * .body(jsonReq.toString()) .post(url );
		 */
		try {
			HttpRCapture capture = new HttpRCapture(url);
			capture.setMethod("RCAPTURE");
			
//		    String method = "RCAPTURE"; // Custom method name
//		    String response = RestClient.rawHttp(url, jsonReq.toString(), method);
			
			String response = RestClient.rawHttp(capture, jsonReq.toString());

			JSONObject respObject = new JSONObject(response);
			JSONArray bioArray = respObject.getJSONArray("biometrics");
			List<MDSDeviceCaptureModel> lstBiometrics = rCaptureModel.getLstBiometrics().get(type);
			if (lstBiometrics == null)
				lstBiometrics = new ArrayList<MDSDeviceCaptureModel>();

			if (!CollectionUtils.isEmpty(listbioexception) && type.equalsIgnoreCase("face"))
				rCaptureModel.getLstBiometrics().put("exception", lstBiometrics);
			else
				rCaptureModel.getLstBiometrics().put(type, lstBiometrics);

			List<String> retriableErrorCodes = new ArrayList<String>();
			retriableErrorCodes.add("703");
			retriableErrorCodes.add("710");

			// Check if Rcapture returns an error response if on error, retry based on Error
			// ;code.
			while (bioArray.length() == 1 && retriableErrorCodes
					.contains(bioArray.getJSONObject(0).getJSONObject("error").getString("errorCode"))) {
				logger.info("Check if Rcapture returns an error response if on error, retry based on Error ;code. ");
				
//				 method = "RCAPTURE"; // Custom method name
//				 response = RestClient.rawHttp(url, jsonReq.toString(), method);
				
				response = RestClient.rawHttp(capture, jsonReq.toString());

				respObject = new JSONObject(response);
				bioArray = respObject.getJSONArray("biometrics");
			}

			for (int i = 0; i < bioArray.length(); i++) {
				JSONObject bioObject = bioArray.getJSONObject(i);
				String data = bioObject.getString("data");

				String hash = bioObject.getString("hash");
				JWTTokenModel jwtTok = new JWTTokenModel(data);
				JSONObject jsonPayload = new JSONObject(jwtTok.getJwtPayload());
				String jwtSign = jwtTok.getJwtSign();
				MDSDeviceCaptureModel model = new MDSDeviceCaptureModel();
				model.setBioType(CommonUtil.getJSONObjectAttribute(jsonPayload, "bioType", ""));
				model.setBioSubType(CommonUtil.getJSONObjectAttribute(jsonPayload, "bioSubType", ""));
				model.setQualityScore(CommonUtil.getJSONObjectAttribute(jsonPayload, "qualityScore", ""));
				model.setBioValue(CommonUtil.getJSONObjectAttribute(jsonPayload, "bioValue", ""));
				model.setDeviceServiceVersion(
						CommonUtil.getJSONObjectAttribute(jsonPayload, "deviceServiceVersion", ""));
				model.setDeviceCode(CommonUtil.getJSONObjectAttribute(jsonPayload, "deviceCode", ""));
				model.setHash(hash);
				if (mosipVersion != null && mosipVersion.startsWith("1.2")) {
					model.setSb(jwtSign); // SB is signature block (header..signature)
					// String temp=jwtTok.getJwtPayload().replace(model.getBioValue(),);

					String BIOVALUE_KEY = "bioValue";
					String BIOVALUE_PLACEHOLDER = "\"<bioValue>\"";
					int bioValueKeyIndex = jwtTok.getJwtPayload().indexOf(BIOVALUE_KEY) + (BIOVALUE_KEY.length() + 1);
					int bioValueStartIndex = jwtTok.getJwtPayload().indexOf('"', bioValueKeyIndex);
					int bioValueEndIndex = jwtTok.getJwtPayload().indexOf('"', (bioValueStartIndex + 1));
					String bioValue = jwtTok.getJwtPayload().substring(bioValueStartIndex, (bioValueEndIndex + 1));
					String payload = jwtTok.getJwtPayload().replace(bioValue, BIOVALUE_PLACEHOLDER);
					model.setPayload(payload);
				}
				lstBiometrics.add(model);

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return rCaptureModel;
	}

	public void setThresholdValue(String qualityScore) {

		String url = MDSURL + port + "/admin/score";
		JSONObject body = new JSONObject();
		body.put("type", "Biometric Device");
		body.put("qualityScore", qualityScore);
		body.put("fromIso", false);

		try {
			/*
			 * HttpRCapture capture = new HttpRCapture(url);
			 * capture.setMethod("SETTHRESHOLVALUE"); String response =
			 * RestClient.rawHttp(capture, body.toString()); JSONObject respObject = new
			 * JSONObject(response);
			 */

			Response response = given().contentType(ContentType.JSON).body(body.toString()).post(url);
			String resp = response.getBody().asString();
			logger.info(resp);

		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	}

	public static void main(String[] args) {

		MDSClient client = new MDSClient(0);
		// client.setProfile("res643726437264372");
		// client.setProfile("Default",port);
		List<MDSDevice> d = client.getRegDeviceInfo("Iris");
		d.forEach(dv -> {
			logger.info(dv.toJSONString());
		});

		List<MDSDevice> f = client.getRegDeviceInfo("Finger");

		f.forEach(dv -> {
			logger.info(dv.toJSONString());

			// MDSRCaptureModel r = client.captureFromRegDevice(dv, null,
			// "Finger",null,60,"1",0);
			// MDSRCaptureModel r = client.captureFromRegDevice(d.get(0),null,
			// "Iris",null,60,2);

		});

		// r = client.captureFromRegDevice(d.get(0),r, "Face",null,60,1);

	}

	@Override
	public List<MDSDevice> getRegDeviceInfo(String type, String contextKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
