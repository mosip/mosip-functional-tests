package io.mosip.testrig.apirig.utils;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.restassured.response.Response;

public class CertificateGenerationUtil extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);

	static {
		if (ConfigManager.IsDebugEnabled())
			lOGGER.setLevel(Level.ALL);
		else
			lOGGER.setLevel(Level.ERROR);
		getThumbprints();
	}

	public static void getThumbprints() {
		String appId = properties.getProperty("appIdForCertificate");
		getAndUploadIdaCertificate(appId, properties.getProperty("partnerrefId"), CertificateTypes.PARTNER);
		getAndUploadIdaCertificate(appId, properties.getProperty("internalrefId"), CertificateTypes.INTERNAL);
		getAndUploadIdaCertificate(appId, properties.getProperty("idaFirRefId"), CertificateTypes.IDA_FIR);
	}

	public static void getAndUploadIdaCertificate(String applicationId, String referenceId, CertificateTypes certificateType) {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
		String url = ApplnURI + properties.getProperty("getIdaCertificateUrl");
		HashMap<String, String> map = new HashMap<>();
		map.put("applicationId", applicationId);
		map.put("referenceId", referenceId);
		lOGGER.info("Getting certificate for " + referenceId);
		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		JSONObject responseJson = new JSONObject(response.asString());
		JSONObject responseValue = (JSONObject) responseJson.get("response");
		String idaCertValue = responseValue.getString("certificate");
		HashMap<String, String> requestBodyMap = new HashMap<>();
		requestBodyMap.put("certData", idaCertValue);
		
		AuthTestsUtil authUtil = new AuthTestsUtil();
		try {
			authUtil.uploadIDACertificate(certificateType, requestBodyMap, null, BaseTestCase.certsForModule, ApplnURI.replace("https://", ""));
		} catch (CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
