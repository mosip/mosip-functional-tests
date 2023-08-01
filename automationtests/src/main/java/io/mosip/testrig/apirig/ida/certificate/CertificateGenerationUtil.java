package io.mosip.testrig.apirig.ida.certificate;

import java.io.File;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.restassured.response.Response;

public class CertificateGenerationUtil extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);

	static {
		lOGGER.info("EncryptUtilBaseUrl " + ConfigManager.getAuthDemoServiceUrl());
		getThumbprints();
	}

	public static void getThumbprints() {
		if (!BaseTestCase.isTargetEnvLTS()) {
			// In case of 1.1.5 we don't have auto sync of certificates between Keymanager cert store and IDA cert store
			// So use the predefined certificate folder and partnerkey
			return ;
		}
		String appId = properties.getProperty("appIdForCertificate");
		getAndUploadIdaCertificate(appId, properties.getProperty("partnerrefId"), properties.getProperty("uploadPartnerurl"));
		getAndUploadIdaCertificate(appId, properties.getProperty("internalrefId"), properties.getProperty("uploadInternalurl"));
		getAndUploadIdaCertificate(appId, properties.getProperty("idaFirRefId"), properties.getProperty("uploadIdaFirurl"));
	}

	public static void getAndUploadIdaCertificate(String applicationId, String referenceId, String endPoint) {
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
