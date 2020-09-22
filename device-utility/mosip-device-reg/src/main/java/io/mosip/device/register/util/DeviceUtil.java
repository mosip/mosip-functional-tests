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
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.mosip.device.register.constants.Urls;
import io.mosip.device.register.dto.ApproverDto;
import io.mosip.device.register.dto.DeviceInfoDto;
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
		URL res = getClass().getClassLoader().getResource("config.properties");
		try {
			config = new FileInputStream(Paths.get(res.toURI()).toFile());
			prop.load(config);
		} catch (IOException e) {
			auditLog.info("config.properties was not found in the project");
		} catch (URISyntaxException e) {
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

		URL res = getClass().getClassLoader().getResource("deviceData");
		try {

			File testFolder = Paths.get(res.toURI()).toFile();
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
		} catch (URISyntaxException e) {
			auditLog.info("Could not find any test data");
		} catch (IOException e) {
			auditLog.info("Could not extract any test data from the file");
		}

		return deviceData;

	}

	public static String getCurrentDateAndTimeForAPI() {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		LocalDateTime time = LocalDateTime.now();
		String currentTime = time.format(dateFormat);
		return currentTime;

	}

	public String readCertificate(String certificateName) {
		URL res = getClass().getClassLoader().getResource("DeviceData/certificates/" + certificateName);
		String pemFile = "";
		try {
			File certiFile = Paths.get(res.toURI()).toFile();
			pemFile = new String(Files.readAllBytes(certiFile.toPath()), Charset.defaultCharset());

		} catch (URISyntaxException e) {
			auditLog.info("Could Not Find The Certificates");
		} catch (IOException e) {
			auditLog.info("Could Not Find The Certificates");
		}
		return pemFile;
	}

	public void uploadCaCertificate(Map<String, String> testDataMap, String cookie) {
		UploadCaCertificateDto uploadCaCertificateDto = new UploadCaCertificateDto(
				readCertificate(testDataMap.get("ca_certificate_name")),
				testDataMap.get("partner_type").toUpperCase());
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
				testDataMap.get("partner_type").toUpperCase(), partnerId, testDataMap.get("partner_type"));

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

	public void approveDevice(String id, String cookie,String url) {
		ApproverDto approverDto = new ApproverDto();
		approverDto.setApprovalStatus("Activate");
		approverDto.setId(id);
		approverDto.setIsItForRegistrationDevice(true);
		RequestBuilder<ApproverDto> approveDeviceDetails = new RequestBuilder<ApproverDto>(approverDto, "",
				DeviceUtil.getCurrentDateAndTimeForAPI(), "String", config.getProperty("version"));
		//String url = Urls.REGISTER_DEVICE;
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(approveDeviceDetails)
				.contentType("application/json").log().all().when().patch(config.getProperty("baseUrl") + url).then()
				.log().all().extract().response();
	}

	public String saveSecureBiometrics(String deviceDetailId, String cookie) {
		SecureBiometricDto secureBiometricDto = new SecureBiometricDto();
		secureBiometricDto.setDeviceDetailId(deviceDetailId);
		secureBiometricDto.setIsItForRegistrationDevice(true);
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
	
	public String convertDeviceInfoToBase64(String deviceProvider,String deviceProviderId,String deviceSubType, String model, String make, String serialNo, String type) throws UnsupportedEncodingException {
		DeviceInfoDto deviceInfoDto=new DeviceInfoDto();
		deviceInfoDto.setDateTime(getCurrentDateAndTimeForAPI());
		deviceInfoDto.setDeviceProvider(deviceProvider);
		deviceInfoDto.setDeviceProviderId(deviceProviderId);
		deviceInfoDto.setDeviceSubType(deviceSubType);
		deviceInfoDto.setModel(model);
		deviceInfoDto.setMake(make);
		deviceInfoDto.setSerialNo(serialNo);
		deviceInfoDto.setType(type);
		return Base64.getEncoder().encodeToString(deviceInfoDto.toString().getBytes("UTF-8"));
	}

	public Response postRequest(RequestBuilder<?> request, String url, String cookie) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		return given().cookie(builder.build()).relaxedHTTPSValidation().body(request).contentType("application/json")
				.log().all().when().post(config.getProperty("baseUrl") + url).then().log().all().extract().response();
	}

}
