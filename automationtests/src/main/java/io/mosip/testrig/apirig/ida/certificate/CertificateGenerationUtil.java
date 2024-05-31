package io.mosip.testrig.apirig.ida.certificate;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import Util.AuthUtil;
import helper.CertificateTypes;
import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.util.RestClient;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.restassured.response.Response;

public class CertificateGenerationUtil extends AdminTestUtil {
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);

	static {
		if (ConfigManager.IsDebugEnabled())
			lOGGER.setLevel(Level.ALL);
		else
			lOGGER.setLevel(Level.ERROR);
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
		getAndUploadIdaCertificate(appId, properties.getProperty("partnerrefId"), CertificateTypes.IDA_FIR);
		getAndUploadIdaCertificate(appId, properties.getProperty("internalrefId"), CertificateTypes.PARTNER);
		getAndUploadIdaCertificate(appId, properties.getProperty("idaFirRefId"), CertificateTypes.INTERNAL);
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

//		JSONObject request = new JSONObject();
//		request.put("certData", idaCertValue);
		// actualrequest.put(GlobalConstants.REQUEST, request);

//		if (endPoint.contains("$MODULENAME$")) {
//			endPoint = endPoint.replace("$MODULENAME$", BaseTestCase.certsForModule);
//		}
//
//		if (endPoint.contains("$CERTSDIR$")) {
//			endPoint = endPoint.replace("$CERTSDIR$", ConfigManager.getauthCertsPath());
//		}
		HashMap<String, String> requestBodyMap = new HashMap<>();
		requestBodyMap.put("certData", idaCertValue);
		
		AuthUtil authUtil = new AuthUtil();
		try {
			authUtil.uploadIDACertificate(certificateType, requestBodyMap, null, BaseTestCase.certsForModule, ApplnURI.replace("https://", ""));
		} catch (CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Response reponse = RestClient.postRequest(ConfigManager.getAuthDemoServiceUrl() + "/" + endPoint,
//				request.toMap(), MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
	}

}
