package io.mosip.ida.certificate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.admin.fw.util.EncryptionDecrptionUtil;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;


public class CertificateGenerationUtil extends AdminTestUtil{
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);
	
	
	static {
		System.out.println("EncryptUtilBaseUrl " + ConfigManager.getAuthDemoServiceUrl());
		getThumbprints();
	}
	public static void getThumbprints() {
		String appId = props.getProperty("appIdForCertificate");
		getAndUploadIdaCertificate(appId, props.getProperty("partnerrefId"), props.getProperty("uploadPartnerurl"));
		getAndUploadIdaCertificate(appId, props.getProperty("internalrefId"), props.getProperty("uploadInternalurl"));
		getAndUploadIdaCertificate(appId, props.getProperty("idaFirRefId"), props.getProperty("uploadIdaFirurl"));
	}
	
	
	
	public static void getAndUploadIdaCertificate(String applicationId, String referenceId, String endPoint) {
		String token = kernelAuthLib.getTokenByRole("resident");
		String url = ApplnURI + props.getProperty("getIdaCertificateUrl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("applicationId", applicationId);
		map.put("referenceId", referenceId);
		lOGGER.info("Getting certificate for "+referenceId);
		Response response = RestClient.getRequestWithCookieAndQueryParm(url, map, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", token);
		JSONObject responseJson = new JSONObject(response.asString());
		JSONObject responseValue = (JSONObject) responseJson.get("response");
		System.out.println(responseValue);
		String idaCertValue = responseValue.getString("certificate");
		System.out.println(idaCertValue);
		
		
		
		JSONObject request=new JSONObject();
		request.put("certData", idaCertValue);
		//actualrequest.put("request", request);
		
		if (endPoint.contains("$MODULENAME$")) {
			endPoint = endPoint.replace("$MODULENAME$", BaseTestCase.currentModule);
		}
		
		Response reponse=RestClient.postRequest(ConfigManager.getAuthDemoServiceUrl()+"/"+endPoint, request.toMap(), MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		System.out.println(reponse);
		
		
	}
	
}
