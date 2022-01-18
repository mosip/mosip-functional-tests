package io.mosip.device.register.service.impl;

import static io.restassured.RestAssured.given;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.mosip.device.register.constants.Urls;
import io.mosip.device.register.dto.DeviceInfoRequestDto;
import io.mosip.device.register.dto.DeviceRegDto;
import io.mosip.device.register.dto.DeviceRegisterRequestDto;
import io.mosip.device.register.dto.PolicyGroupRequestDto;
import io.mosip.device.register.dto.RequestBuilder;
import io.mosip.device.register.dto.SelfRegistrationRequestDto;
import io.mosip.device.register.dto.SignedRegisterDeviceDto;
import io.mosip.device.register.service.AuthenticationService;
import io.mosip.device.register.service.DeviceRegister;
import io.mosip.device.register.util.DeviceUtil;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class DeviceRegisterImpl implements DeviceRegister {
	public static Logger auditLog = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	DeviceUtil deviceUtil = new DeviceUtil();
	Properties config = deviceUtil.getConfig();

	AuthenticationService authenticationService = new AuthenticationServiceImpl();
	String cookie = "";

	@Override
	public String createPolicyGroup(Map<String, String> testDataMap) {
		String policyId = "";

		cookie = authenticationService.login();

		PolicyGroupRequestDto groupRequestDto = new PolicyGroupRequestDto(testDataMap.get("policy_desc"),
				testDataMap.get("policy_name"));
		RequestBuilder<PolicyGroupRequestDto> createPolicyGroupDto = new RequestBuilder<PolicyGroupRequestDto>(
				groupRequestDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String", config.getProperty("version"));
		String url = Urls.CREATE_POLICY_GROUP;

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = deviceUtil.postRequest(createPolicyGroupDto, url, cookie);
		System.out.println(postResponse.asString());
		ReadContext ctx = JsonPath.parse(postResponse.getBody().asString());
		if (ctx.read("$.response") == null) {
			auditLog.info("Request :" + createPolicyGroupDto);
			url = Urls.GET_POLICY_GROUP;
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("application/json")
					.log().all().when().get(config.getProperty("baseUrl") + url + testDataMap.get("policy_name")).then()
					.log().all().extract().response();
			policyId = postResponse.jsonPath().get("response.policyID").toString();
		} else
			policyId = postResponse.jsonPath().get("response.id").toString();
		return policyId;
	}

	@Override
	public String partnerSelfRegistration(Map<String, String> testDataMap) {
		String partnerId = "";
		//createPolicyGroup(testDataMap);
		cookie = authenticationService.login();

		SelfRegistrationRequestDto registrationRequestDto = new SelfRegistrationRequestDto();
		registrationRequestDto.setAddress(testDataMap.get("Address"));
		registrationRequestDto.setContactNumber(testDataMap.get("contact_no"));
		registrationRequestDto.setEmailId(testDataMap.get("email"));
		registrationRequestDto.setOrganizationName(testDataMap.get("organisation_name"));
		registrationRequestDto.setPartnerType(testDataMap.get("partner_type"));
		registrationRequestDto.setPartnerId(testDataMap.get("partner_id"));
		registrationRequestDto.setPolicyGroup(testDataMap.get("policy_name"));
		RequestBuilder<SelfRegistrationRequestDto> registrationDto = new RequestBuilder<SelfRegistrationRequestDto>(
				registrationRequestDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));

		String url = Urls.PARTNER_SELF_REGISTRATION;

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = deviceUtil.postRequest(registrationDto, url, cookie);
		ReadContext ctx = JsonPath.parse(postResponse.getBody().asString());
		if (ctx.read("$.response") == null) {
			url = Urls.RETREIVE_PARTNERS;
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("application/json")
					.log().all().when().get(config.getProperty("baseUrl") + url).then().log().all().extract()
					.response();

			io.restassured.path.json.JsonPath jsonPath = postResponse.jsonPath();
			List<Object> list = jsonPath.getList("response.partners");
			for (Object obj : list) {
				@SuppressWarnings("unchecked")
				Map<String, String> partner = (Map<String, String>) obj;
				if (partner.get("emailId").equals(testDataMap.get("email"))) {
					partnerId = partner.get("partnerID");
				}
			}
		} else
			partnerId = postResponse.jsonPath().get("response.partnerId").toString();

		deviceUtil.uploadCaCertificate(testDataMap, cookie);
		deviceUtil.uploadPartnerCertificate(testDataMap, partnerId, cookie);

		return partnerId;
	}

	@Override
	public void deviceRegister(Map<String, String> testDataMap) {
		String id = "";
		DeviceRegisterRequestDto registerRequestDto = new DeviceRegisterRequestDto();
		String selfRegistration = partnerSelfRegistration(testDataMap);
		registerRequestDto.setDeviceProviderId(selfRegistration);
		registerRequestDto.setDeviceSubTypeCode(testDataMap.get("deviceSubType"));
		registerRequestDto.setDeviceTypeCode(testDataMap.get("deviceType"));
		registerRequestDto.setId(testDataMap.get("device_id_desc"));
		if(testDataMap.get("purpose").equals("REGISTRATION")) {
			registerRequestDto.setIsItForRegistrationDevice(true);
		}else {
			registerRequestDto.setIsItForRegistrationDevice(false);
		}
		registerRequestDto.setMake(testDataMap.get("make"));
		registerRequestDto.setModel(testDataMap.get("model"));
		registerRequestDto.setPartnerOrganizationName(testDataMap.get("organisation_name"));
		RequestBuilder<DeviceRegisterRequestDto> deviceRegisterDto = new RequestBuilder<DeviceRegisterRequestDto>(
				registerRequestDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));

		String url = Urls.REGISTER_DEVICE;
		Response postResponse = deviceUtil.postRequest(deviceRegisterDto, url, cookie);
		ReadContext ctx = JsonPath.parse(postResponse.getBody().asString());
		if (ctx.read("$.response") == null) {
			id=testDataMap.get("device_id_desc");
		} else {
			id=postResponse.jsonPath().get("response.id").toString();
		}


		deviceUtil.approveDevice(id, cookie,Urls.REGISTER_DEVICE,registerRequestDto.getIsItForRegistrationDevice());
		String secureId=deviceUtil.saveSecureBiometrics(id, cookie,registerRequestDto.getIsItForRegistrationDevice());
		deviceUtil.approveDevice(secureId, cookie,Urls.APPROVE_BIOMETRICS,registerRequestDto.getIsItForRegistrationDevice());
//		try {
//			registerDevice(testDataMap, registerRequestDto);
//
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void registerDevice(Map<String, String> testDataMap, DeviceRegisterRequestDto registerRequestDto)
			throws UnsupportedEncodingException {
		String digitalId=deviceUtil.convertDiditalIdInfoToJWT(testDataMap,
				testDataMap.get("organisation_name"),
				registerRequestDto.getDeviceProviderId(),
				testDataMap.get("deviceSubType"), 
				testDataMap.get("model"),
				testDataMap.get("make"), 
				testDataMap.get("serial_no"),
				testDataMap.get("deviceType"));

		DeviceRegDto deviceRegDto=new DeviceRegDto();
		deviceRegDto.setDeviceId(testDataMap.get("device_id"));
		deviceRegDto.setFoundationalTrustProviderId(testDataMap.get("foundationalTrustProviderId"));
		deviceRegDto.setPurpose(testDataMap.get("purpose"));
		//TODO
		DeviceInfoRequestDto infoRequestDto=new DeviceInfoRequestDto();
		infoRequestDto.setCertification("L0");
		infoRequestDto.setDeviceSubId(testDataMap.get("device_sub_id"));
		infoRequestDto.setDigitalId(digitalId);
		infoRequestDto.setFirmware("firmware");
		infoRequestDto.setTimeStamp(DeviceUtil.getCurrentDateAndTimeForAPI());
		infoRequestDto.setDeviceExpiry(DeviceUtil.getFutureDateAndTime());
		String deviceInfoRequestString = deviceUtil.convertDeviceInfoToJWT(infoRequestDto,testDataMap);
		deviceRegDto.setDeviceInfo(deviceInfoRequestString);
		
		SignedRegisterDeviceDto signedRegisterDeviceDto=new SignedRegisterDeviceDto();
		//signedRegisterDeviceDto.setDeviceData(Base64.getEncoder().encodeToString(deviceRegDto.toString().getBytes()));
		
		String deviceRegString=deviceUtil.convertDeviceRegToJwt(deviceRegDto,testDataMap);
		signedRegisterDeviceDto.setDeviceData(deviceRegString);
		
		RequestBuilder<SignedRegisterDeviceDto> signedDeviceRegisterDto = new RequestBuilder<SignedRegisterDeviceDto>(
				signedRegisterDeviceDto, "", DeviceUtil.getCurrentDateAndTimeForAPI(), "String",
				config.getProperty("version"));
//	deviceUtil.postRequest(signedDeviceRegisterDto,Urls.SIGNED_REGISTER_BIOMETRICS , cookie);
	}

}
