package io.mosip.ida.certificate;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.global.utils.GlobalConstants;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class CertificateGenerationUtil extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);

	static {
		lOGGER.info("EncryptUtilBaseUrl " + ConfigManager.getAuthDemoServiceUrl());
		getThumbprints();
	}

	public static void getThumbprints() {
		String appId = props.getProperty("appIdForCertificate");
		getAndUploadIdaCertificate(appId, props.getProperty("partnerrefId"), props.getProperty("uploadPartnerurl"));
		getAndUploadIdaCertificate(appId, props.getProperty("internalrefId"), props.getProperty("uploadInternalurl"));
		getAndUploadIdaCertificate(appId, props.getProperty("idaFirRefId"), props.getProperty("uploadIdaFirurl"));
	}

	public static void getAndUploadIdaCertificate(String applicationId, String referenceId, String endPoint) {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
		String url = ApplnURI + props.getProperty("getIdaCertificateUrl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("applicationId", applicationId);
		map.put("referenceId", referenceId);
		lOGGER.info("Getting certificate for " + referenceId);
		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		JSONObject responseJson = new JSONObject(response.asString());
		JSONObject responseValue = (JSONObject) responseJson.get("response");
		lOGGER.info(responseValue);
		String idaCertValue = responseValue.getString("certificate");
		lOGGER.info(idaCertValue);

		JSONObject request = new JSONObject();
		request.put("certData", idaCertValue);
		// actualrequest.put(GlobalConstants.REQUEST, request);

		if (endPoint.contains("$MODULENAME$")) {
			endPoint = endPoint.replace("$MODULENAME$", BaseTestCase.certsForModule);
		}

		if (endPoint.contains("$CERTSDIR$")) {
			endPoint = endPoint.replace("$CERTSDIR$", ConfigManager.getauthCertsPath());
		}

		Response reponse = RestClient.postRequest(ConfigManager.getAuthDemoServiceUrl() + "/" + endPoint,
				request.toMap(), MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		lOGGER.info(reponse);

	}

}
