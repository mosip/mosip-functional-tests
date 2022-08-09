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
import io.restassured.response.Response;


public class CertificateGenerationUtil extends AdminTestUtil{
	private static final Logger lOGGER = Logger.getLogger(CertificateGenerationUtil.class);
	
	static String EncryptUtilBaseUrl = null;
	public static Certificate partnerThumbPrint =null;
	public static Certificate internalThumbPrint =null;
	public static Certificate idaFirThumbPrint =null;
	
	static {
		if(EncryptUtilBaseUrl==null)			
			EncryptUtilBaseUrl = getEncryptUtilBaseUrl();
		System.out.println("EncryptUtilBaseUrl " + EncryptUtilBaseUrl);
		getThumbprints();
	}
	public static void getThumbprints() {
		String appId = props.getProperty("appIdForCertificate");
		partnerThumbPrint = getIdaCertificate(appId, props.getProperty("partnerrefId"));
		internalThumbPrint = getIdaCertificate(appId, props.getProperty("internalrefId"));
		idaFirThumbPrint = getIdaCertificate(appId, props.getProperty("idaFirRefId"));	
	}
	
	public static String getEncryptUtilBaseUrl() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			return "http://"+inetAddress.getHostName().toLowerCase()+":"+props.getProperty("encryptUtilPort")+"/";
			
		} catch (Exception e) {
			lOGGER.error("Execption in RunConfig " + e.getMessage());
			return null;
		}
	}
	
	
	
	public static Certificate getIdaCertificate(String applicationId, String referenceId) {
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
		
		Response reponse=RestClient.postRequest(CertificateGenerationUtil.getEncryptUtilBaseUrl()+props.getProperty("uploadIdaFirurl"), request.toMap(), MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		JSONObject reponseValue = new JSONObject(reponse.asString());
		System.out.println(reponseValue);
		return idaFirThumbPrint;
		
		
	}
	
}
