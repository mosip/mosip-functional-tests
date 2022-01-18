package io.mosip.device.register.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.mosip.device.register.Runner;
import io.mosip.device.register.constants.Urls;
import io.mosip.device.register.dto.ApproverDto;
import io.mosip.device.register.dto.DeviceInfoRequestDto;
import io.mosip.device.register.dto.DeviceRegDto;
import io.mosip.device.register.dto.DigitalIdDto;
import io.mosip.device.register.dto.RequestBuilder;
import io.mosip.device.register.dto.SecureBiometricDto;
import io.mosip.device.register.dto.UploadCaCertificateDto;
import io.mosip.device.register.dto.UploadPartnerCertificateDto;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class DeviceUtil {
	public static Logger auditLog = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public static int deviceNumber = 1;
	Properties config = getConfig();
	ObjectMapper mapper = new ObjectMapper();

	public static String checkRunType() {
		if (DeviceUtil.class.getResource("DeviceUtil.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}

	public static String getCurrentDateAndTime() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date = new Date();

		return format.format(date).toString();
	}

	public static String getFutureDateAndTime() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date = new Date();
		long ltime = date.getTime() + 7 * 24 * 60 * 60 * 1000;
		Date date7 = new Date(ltime);
		return format.format(date7).toString();
	}

	public Properties getConfig() {
		Properties prop = new Properties();
		FileInputStream config = null;
		
		try {
			File file=getGlobalResourcePath("config.properties");
			config = new FileInputStream(file);
			prop.load(config);
		} catch (IOException e) {
			auditLog.info("config.properties was not found in the project");
		} finally {
			try {
				config.close();
			} catch (IOException e) {
				auditLog.info("config.properties was not found in the project");
			}

		}
		return prop;

	}

	public Map<String, Map<String, String>> loadDataFromCsv() {
		Map<String, Map<String, String>> deviceData = new HashMap<>();
		//URL res = getClass().getClassLoader().getResource("deviceData");
		try {

			File testFolder = getGlobalResourcePath("DeviceData");
			File[] testFiles = testFolder.listFiles();
			for (File testFile : testFiles) {
				if (testFile.getName().contains(".csv")) {
					Reader reader = new FileReader(testFile);
					Iterator<Map<String, String>> iterator = new CsvMapper().readerFor(Map.class)
							.with(CsvSchema.emptySchema().withHeader()).readValues(reader);
					while (iterator.hasNext()) {
						deviceData.put("Device" + deviceNumber, iterator.next());
						deviceNumber++;
					}
				}
			}
		} catch (IOException e) {
			auditLog.info("Could not extract any test data from the file");
		}

		return deviceData;

	}

	public static String getCurrentDateAndTimeForAPI() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
		String currentTime = time.format(dateFormat);
		return currentTime;

	}

	public String readCertificate(String certificateName) {
	//	URL res = getClass().getClassLoader().getResource("DeviceData/certificates/" + certificateName);
		String pemFile = "";
		try {
			File certiFile = new File(getGlobalResourcePath("DeviceData").getAbsolutePath()+"/certificates/"+certificateName);
			pemFile = new String(Files.readAllBytes(certiFile.toPath()), Charset.defaultCharset());

		} catch (IOException e) {
			auditLog.info("Could Not Find The Certificates");
		}
		return pemFile;
	}

	public void uploadCaCertificate(Map<String, String> testDataMap, String cookie) {
		UploadCaCertificateDto uploadCaCertificateDto = new UploadCaCertificateDto(
				readCertificate(testDataMap.get("ca_certificate_name")),
				testDataMap.get("partner_domain").toUpperCase());
		RequestBuilder<UploadCaCertificateDto> uploadCertificate = new RequestBuilder<UploadCaCertificateDto>(
				uploadCaCertificateDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));

		String url = Urls.UPLOAD_CA_CERTIFICATE;
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(uploadCertificate)
				.contentType("application/json").log().all().when().post(config.getProperty("baseUrl") + url).then()
				.log().all().extract().response();
		System.out.println(postResponse.asString());

	}

	public void uploadPartnerCertificate(Map<String, String> testDataMap, String partnerId, String cookie) {
		UploadPartnerCertificateDto partnerCertificateDto = new UploadPartnerCertificateDto(
				readCertificate(testDataMap.get("partner_certificate_name")), testDataMap.get("organisation_name"),
				testDataMap.get("partner_domain").toUpperCase(), partnerId, testDataMap.get("partner_type"));

		RequestBuilder<UploadPartnerCertificateDto> uploadCertificate = new RequestBuilder<UploadPartnerCertificateDto>(
				partnerCertificateDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));

		String url = Urls.UPLOAD_Partner_CERTIFICATE;
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(uploadCertificate)
				.contentType("application/json").log().all().when().post(config.getProperty("baseUrl") + url).then()
				.log().all().extract().response();
		System.out.println(postResponse.asString());

	}

	public void approveDevice(String id, String cookie, String url, boolean regType) {
		ApproverDto approverDto = new ApproverDto();
		approverDto.setApprovalStatus("Activate");
		approverDto.setId(id);
		approverDto.setIsItForRegistrationDevice(regType);
		RequestBuilder<ApproverDto> approveDeviceDetails = new RequestBuilder<ApproverDto>(approverDto, "",
				DeviceUtil.getCurrentDateAndTimeForAPI(), "String", config.getProperty("version"));
		// String url = Urls.REGISTER_DEVICE;
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(approveDeviceDetails)
				.contentType("application/json").log().all().when().patch(config.getProperty("baseUrl") + url).then()
				.log().all().extract().response();
	}

	public String saveSecureBiometrics(String deviceDetailId, String cookie, boolean regType) {
		SecureBiometricDto secureBiometricDto = new SecureBiometricDto();
		secureBiometricDto.setDeviceDetailId(deviceDetailId);
		secureBiometricDto.setIsItForRegistrationDevice(regType);
		secureBiometricDto.setSwBinaryHash("1");
		secureBiometricDto.setSwCreateDateTime(getCurrentDateAndTime());
		secureBiometricDto.setSwExpiryDateTime(getFutureDateAndTime());
		secureBiometricDto.setSwVersion("1");
		RequestBuilder<SecureBiometricDto> secureBiometricUpload = new RequestBuilder<SecureBiometricDto>(
				secureBiometricDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));
		String url = Urls.APPROVE_BIOMETRICS;
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(secureBiometricUpload)
				.contentType("application/json").log().all().when().post(config.getProperty("baseUrl") + url).then()
				.log().all().extract().response();
		return postResponse.jsonPath().get("response.id").toString();
	}

	public String convertDiditalIdInfoToJWT(Map<String, String> testDataMap, String deviceProvider,
			String deviceProviderId, String deviceSubType, String model, String make, String serialNo, String type)
			throws UnsupportedEncodingException {
		DigitalIdDto digitalIdDto = new DigitalIdDto();
		String digitalId = null;
		digitalIdDto.setDateTime(getCurrentDateAndTimeForAPI());
		digitalIdDto.setDeviceProvider(deviceProvider);
		digitalIdDto.setDeviceProviderId(deviceProviderId);
		digitalIdDto.setDeviceSubType(deviceSubType);
		digitalIdDto.setModel(model);
		digitalIdDto.setMake(make);
		digitalIdDto.setSerialNo(serialNo);
		digitalIdDto.setType(type);
		try {
			digitalId = getDigitalModality(mapper.readValue(new String(digitalIdDto.toString()), Map.class),
					testDataMap);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return digitalId;
		// return
		// Base64.getEncoder().encodeToString(digitalIdDto.toString().getBytes("UTF-8"));
	}

	public String convertDeviceInfoToJWT(DeviceInfoRequestDto infoRequestDto, Map<String, String> testDataMap) {
		Map<String, Map<String, String>> deviceinfoData = mapper.convertValue(infoRequestDto, Map.class);
		String result = null;
		JwtUtility j = new JwtUtility();
		try {
			result = JwtUtility.getJwt(mapper.writeValueAsBytes(deviceinfoData), j.getPrivateKey(),
					JwtUtility.getCertificate(testDataMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String convertDeviceRegToJwt(DeviceRegDto deviceRegDto, Map<String, String> testDataMap) {
		Map<String, Map<String, String>> deviceRegData = mapper.convertValue(deviceRegDto, Map.class);
		String result = null;
		JwtUtility j = new JwtUtility();
		try {
			result = JwtUtility.getJwt(mapper.writeValueAsBytes(deviceRegData), j.getPrivateKey(),
					JwtUtility.getCertificate(testDataMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String getDigitalModality(Map<String, String> digitalIdMap, Map<String, String> testDataMap) {
		JwtUtility j = new JwtUtility();
		String result = null;
		Map<String, String> digitalMap = new LinkedHashMap<String, String>();
		digitalMap.put("dateTime", getCurrentDateAndTimeForAPI());
		digitalMap.put("deviceProvider", digitalIdMap.get("deviceProvider"));
		digitalMap.put("deviceProviderId", digitalIdMap.get("deviceProviderId"));
		digitalMap.put("make", digitalIdMap.get("make"));
		digitalMap.put("serialNo", digitalIdMap.get("serialNo"));
		digitalMap.put("model", digitalIdMap.get("model"));
		digitalMap.put("deviceSubType", digitalIdMap.get("deviceSubType"));
		digitalMap.put("type", digitalIdMap.get("type"));
		try {
			result = JwtUtility.getJwt(mapper.writeValueAsBytes(digitalMap), j.getPrivateKey(),
					JwtUtility.getCertificate(testDataMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Response postRequest(RequestBuilder<?> request, String url, String cookie) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		return given().cookie(builder.build()).relaxedHTTPSValidation().body(request).contentType("application/json")
				.log().all().when().post(config.getProperty("baseUrl") + url).then().log().all().extract().response();
	}
	
	public static File getGlobalResourcePath(String fileName) {
		File homeDir=null;
		String os=System.getProperty("os.name");
		 if(checkRunType().contains("IDE") || os.toLowerCase().contains("windows")==false) {
			 homeDir = new File(System.getProperty("user.dir") + "/"+fileName);
		}
		else {
			File dir=new File(System.getProperty("user.dir"));
		homeDir = new File(dir.getParent()  + "/"+fileName);
		}
		return homeDir; 
	}
	


}
