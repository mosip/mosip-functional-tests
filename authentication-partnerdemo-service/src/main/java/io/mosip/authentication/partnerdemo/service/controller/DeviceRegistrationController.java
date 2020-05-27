package io.mosip.authentication.partnerdemo.service.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.partnerdemo.service.controller.JWSSignAndVerifyController.SignatureStatus;
import io.mosip.authentication.partnerdemo.service.helper.DBUtil;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.swagger.annotations.Api;
/**
 * 
 * @author Loganathan Sekar
 *
 */
@RestController
@Api(tags = { "Device Registration" })
public class DeviceRegistrationController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JWSSignAndVerifyController jWSSignAndVerifyController;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private DBUtil dbUtil;

	@Value("${add-device-provider-url}")
	private String addDeviceProviderUrl;
	
	@Value("${add-mds-request-url}")
	private String addMdsRequestUrl;
	
	@Value("${add-device-request-url}")
	private String registerDeviceRequestUrl;
	
	@Value("${validate-device-request-url}")
	private String validateDeviceRequestUrl;
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(DeviceRegistrationController.class);
	
	@PostMapping(path = "/registerDeviceInfos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> registerDevice(
			@RequestBody List<Map<String, Object>> listOfDeviceInfosToRegister)
			throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			IOException, JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException, CertificateException, UnrecoverableEntryException, JoseException {
				
		 List<Map<String, Object>> registeredDevices = new ArrayList<>();
		for (Map<String, Object> map : listOfDeviceInfosToRegister) {
			Map<String, Object> registeredDevice = registerDevice(map);
			if(registeredDevice != null && !registeredDevice.isEmpty()) {
				registeredDevices.add(registeredDevice);
			}
		}
		return registeredDevices;
		
	}
	
	@PostMapping(path = "/registerDeviceInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerDevice(
			@RequestBody Map<String, Object> deviceInfoToRegister)
			throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			IOException, JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException, CertificateException, UnrecoverableEntryException, JoseException {
		if(deviceInfoToRegister.get("error") == null ||
				Optional.of(deviceInfoToRegister.get("error"))
				.filter(errMap -> errMap instanceof Map)
				.map(errorMap -> ((Map<String, Object>)errorMap).get("errorCode"))
				.filter(errCode -> String.valueOf(errCode).equals("0"))
						.isPresent()) {
			Map<String, Object> decodedDeviceInfo = decodeDeviceInfo(deviceInfoToRegister);
			return registerDecodedDeviceInfo(decodedDeviceInfo);
		} else {
			logger.info("sessionID", "IDA", "registerDevice", "device not ready or active :" +  mapper.writeValueAsString(deviceInfoToRegister));
		}
		return Collections.emptyMap();
		
	}
	
	@PostMapping(path = "/decodeDeviceInfos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> decodeDeviceInfos(
			@RequestBody List<Map<String, Object>> deviceInfoToRegister
			) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, InvalidKeySpecException, IOException, JoseException {
		List<Map<String, Object>> decodedDeviceInfos = new ArrayList<>();
		for (Map<String, Object> deviceInfoMap : deviceInfoToRegister) {
			decodedDeviceInfos.add(decodeDeviceInfo(deviceInfoMap));
		}
		return decodedDeviceInfos;
	}

	@PostMapping(path = "/decodeDeviceInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> decodeDeviceInfo(
			@RequestBody Map<String, Object> deviceInfoToRegister
			) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, InvalidKeySpecException, IOException, JoseException {
		Object devInfoObj = deviceInfoToRegister.get("deviceInfo");
		if (devInfoObj instanceof String) {
			String deviceInfoJws = (String) devInfoObj;
			SignatureStatus status = jWSSignAndVerifyController.verify(deviceInfoJws);
			if (status.getStatus().equals("VALID")) {
				String payload = status.getPayload();

				Map<String, Object> deviceInfoMap = mapper.readValue(CryptoUtil.decodeBase64(payload), Map.class);
				Object digitalIdObj = deviceInfoMap.get("digitalId");
				if(digitalIdObj instanceof String) {
					String digitalIdStr = (String) digitalIdObj;
					Map<String, Object> digitalIdMap = mapper.readValue(CryptoUtil.decodeBase64(digitalIdStr), Map.class);
					deviceInfoMap.put("digitalId", digitalIdMap);
				}
				
				return deviceInfoMap;
			}
		}
		return Collections.emptyMap();
	}

	@PostMapping(path = "/registerDecodedDeviceInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerDecodedDeviceInfo(
			@RequestBody Map<String, Object> deviceInfoMap) throws JsonParseException, JsonMappingException, IOException {
		
		String deviceProviderId = getDigitalIdInfo(deviceInfoMap, "deviceProviderId");
		String deviceProviderName = getDigitalIdInfo(deviceInfoMap, "deviceProvider");
		
		String deviceType = getDigitalIdInfo(deviceInfoMap,"type");
		String deviceSubType = getDigitalIdInfo(deviceInfoMap,"subType");
		
		String make = getDigitalIdInfo(deviceInfoMap,"make");
		String model = getDigitalIdInfo(deviceInfoMap,"model");
		String serviceVersion = (String) deviceInfoMap.get("serviceVersion");
		
		String deviceId = String.valueOf(deviceInfoMap.get("deviceId"));
		String deviceSubId = String.valueOf(((List) deviceInfoMap.get("deviceSubId")).get(0));
		String serialNumber = getDigitalIdInfo(deviceInfoMap,"serialNo");
		String deviceExpiryDate = (String) deviceInfoMap.get("DeviceExpiryDate");
		String purpose = (String) deviceInfoMap.get("purpose");
		String deviceCode = (String) deviceInfoMap.get("deviceCode");
		
		Map<String, Object> deviceRegistrationResponse = new LinkedHashMap<>();
		deviceRegistrationResponse.put("decodedDeviceInfo", deviceInfoMap);
		
		Map<String, Object> registerDeviceInfoResponse = registerDeviceInfoValues(deviceProviderId, deviceProviderName, deviceType, deviceSubType, make, model,
				serviceVersion, deviceId, deviceSubId, serialNumber, deviceExpiryDate, purpose, deviceCode);
		deviceRegistrationResponse.putAll(registerDeviceInfoResponse);
		
		return deviceRegistrationResponse;
	}
	
	@PostMapping(path = "/registerAuthRequestDeviceInfos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> registerAuthRequestDeviceInfos(
			@RequestBody List<Map<String, Object>> listOfDeviceInfosToRegister)
			throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			IOException, JSONException, IdAuthenticationAppException, IdAuthenticationBusinessException, KeyStoreException, CertificateException, UnrecoverableEntryException, JoseException {
				
		List<Map<String, Object>> registeredDevices = new ArrayList<>();
		for (Map<String, Object> map : listOfDeviceInfosToRegister) {
			Map<String, Object> registeredDevice = registerAuthRequestDeviceInfo(map);
			if(registeredDevice != null && !registeredDevice.isEmpty()) {
				registeredDevices.add(registeredDevice);
			}
		}
		return registeredDevices;
		
	}
	
	@PostMapping(path = "/registerAuthRequestDeviceInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerAuthRequestDeviceInfo(
			@RequestBody Map<String, Object> deviceInfoMap) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> bioDataMap = (Map)deviceInfoMap.get("data");
		String deviceProviderId = getDigitalIdInfo(bioDataMap, "deviceProviderId");
		String deviceProviderName = getDigitalIdInfo(bioDataMap, "deviceProvider");
		
		String deviceType = getDigitalIdInfo(bioDataMap,"type");
		String deviceSubType = getDigitalIdInfo(bioDataMap,"subType");
		
		String make = getDigitalIdInfo(bioDataMap,"make");
		String model = getDigitalIdInfo(bioDataMap,"model");
		String serviceVersion = (String) bioDataMap.get("deviceServiceVersion");
		
		String deviceId = String.valueOf(1);
		String deviceSubId = String.valueOf(1);
		String deviceExpiryDate = "2025-11-11T15:27:52.973+05:30";
		
		String serialNumber = getDigitalIdInfo(bioDataMap,"serialNo");
		String purpose = (String) bioDataMap.get("mosipProcess");
		String deviceCode = (String) bioDataMap.get("deviceCode");
		
		Map<String, Object> deviceRegistrationResponse = new LinkedHashMap<>();
		deviceRegistrationResponse.put("authBioInfo", deviceInfoMap);
		
		Map<String, Object> registerDeviceInfoResponse = registerDeviceInfoValues(deviceProviderId, deviceProviderName, deviceType, deviceSubType, make, model,
				serviceVersion, deviceId, deviceSubId, serialNumber, deviceExpiryDate, purpose, deviceCode);
		deviceRegistrationResponse.putAll(registerDeviceInfoResponse);
		
		return deviceRegistrationResponse;
	}

	@PostMapping(path = "/registerDeviceInfoValues", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerDeviceInfoValues(
			@RequestParam(name = "deviceProviderId", required = true) String deviceProviderId,
			@RequestParam(name = "deviceProviderName", required = true) String deviceProviderName, 
			@RequestParam(name = "deviceType", required = true) String deviceType, 
			@RequestParam(name = "deviceSubType", required = true) String deviceSubType, 
			@RequestParam(name = "make", required = true) String make, 
			@RequestParam(name = "model", required = true) String model,
			@RequestParam(name = "serviceVersion", required = true) String serviceVersion, 
			@RequestParam(name = "deviceId", required = true) String deviceId, 
			@RequestParam(name = "deviceSubId", required = true) String deviceSubId, 
			@RequestParam(name = "serialNumber", required = true) String serialNumber, 
			@RequestParam(name = "deviceExpiryDate", required = true) String deviceExpiryDate,
			@RequestParam(name = "purpose", required = true) String purpose, 
			@RequestParam(name = "deviceCode", required = true) String deviceCode)
			throws JsonParseException, JsonMappingException, IOException, JsonProcessingException {
		Map<String, Object> deviceRegistrationResponse = new LinkedHashMap<>();

		Map<String, Object> validateDeviceResponse = validateDevice(
				deviceProviderId, 
				deviceProviderName, 
				deviceType, 
				deviceSubType, 
				make, 
				model, 
				serialNumber, 
				deviceCode, 
				serviceVersion);
		logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "validateDeviceResponse :" +  mapper.writeValueAsString(validateDeviceResponse));
		deviceRegistrationResponse.put("validateDeviceResponse", validateDeviceResponse);
		
		//If device is already registered, it will be valid, and hence not proceeding to re-registering.
		if(!String.valueOf(validateDeviceResponse.get("responseHttpStatus")).equals("200")
				|| ((Map)validateDeviceResponse.get("responseHttpBody")).get("errors") != null) {
			
			String deviceTypeCode = getDeviceTypeCode(deviceType);
			if(deviceTypeCode == null) {
				deviceTypeCode = insertDeviceTypeCode(deviceType);
				if(deviceTypeCode == null) {
					deviceRegistrationResponse.put("error", "DeviceType cannot be inserted");
					return deviceRegistrationResponse;
				}
			}
			
			String deviceSubTypeCode = getDeviceSubTypeCode(deviceSubType, deviceTypeCode);
			if(deviceSubTypeCode == null) {
				deviceSubTypeCode = insertDeviceSubTypeCode(deviceSubType, deviceTypeCode);
				if(deviceSubTypeCode == null) {
					deviceRegistrationResponse.put("error", "DeviceSubType cannot be inserted");
					return deviceRegistrationResponse;
				}
			}
			
			if(isDeviceProviderIdExists(deviceProviderId)) {
				deviceRegistrationResponse.put("DeviceProviderIDExists", true);
			} else {
				Map<String, Object> addDeviceProviderResponse = addDeviceProvider(deviceProviderId, deviceProviderName);
				logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "addDeviceProviderResponse :" +  mapper.writeValueAsString(addDeviceProviderResponse));
				deviceRegistrationResponse.put("addDeviceProviderResponse", addDeviceProviderResponse);
			}
			
			Map<String, Object> registerMosipDeviceServiceResponse = registerMosipDeviceService(
					deviceProviderId, 
					deviceTypeCode, 
					deviceSubTypeCode, 
					make, 
					model, 
					serviceVersion);
			logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "registerMosipDeviceServiceResponse :" +  mapper.writeValueAsString(registerMosipDeviceServiceResponse));
			deviceRegistrationResponse.put("registerMosipDeviceServiceResponse", registerMosipDeviceServiceResponse);
			
			
			Map<String, Object> registerDeviceResponse = registerDevice(
					deviceProviderId, 
					deviceProviderName, 
					deviceTypeCode, 
					deviceSubTypeCode, 
					make, 
					model, 
					deviceId, 
					deviceSubId, 
					serialNumber, 
					deviceExpiryDate, 
					purpose,
					deviceCode);
			logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "registerDeviceResponse :" +  mapper.writeValueAsString(registerDeviceResponse));
			deviceRegistrationResponse.put("registerDeviceResponse", registerDeviceResponse);
	
			
			validateDeviceResponse = validateDevice(
					deviceProviderId, 
					deviceProviderName, 
					deviceType, 
					deviceSubType, 
					make, 
					model, 
					serialNumber, 
					deviceCode, 
					serviceVersion);
			logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "validateDeviceResponse :" +  mapper.writeValueAsString(validateDeviceResponse));
			deviceRegistrationResponse.put("validateDeviceResponse", validateDeviceResponse);
		
		}

		return deviceRegistrationResponse;
	}
	
	@PostMapping(path = "/validateDeviceInfoDecoded", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> validateDecodedDeviceInfo(
			@RequestBody Map<String, Object> deviceInfoMap) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> deviceRegistrationResponse = new LinkedHashMap<>();
		String deviceProviderId = getDigitalIdInfo(deviceInfoMap, "deviceProviderId");
		String deviceProviderName = getDigitalIdInfo(deviceInfoMap, "deviceProvider");
		
		String deviceType = getDigitalIdInfo(deviceInfoMap,"type");
		String deviceSubType = getDigitalIdInfo(deviceInfoMap,"subType");
		
		String make = getDigitalIdInfo(deviceInfoMap,"make");
		String model = getDigitalIdInfo(deviceInfoMap,"model");
		String serviceVersion = (String) deviceInfoMap.get("serviceVersion");
		
		String serialNumber = getDigitalIdInfo(deviceInfoMap,"serialNo");
		String deviceCode = (String) deviceInfoMap.get("deviceCode");
		
		Map<String, Object> validateDeviceResponse = validateDevice(
				deviceProviderId, 
				deviceProviderName, 
				deviceType, 
				deviceSubType, 
				make, 
				model, 
				serialNumber, 
				deviceCode, 
				serviceVersion);
		logger.info("sessionID", "IDA", "registerDecodedDeviceInfo", "validateDeviceResponse :" +  mapper.writeValueAsString(validateDeviceResponse));
		deviceRegistrationResponse.put("validateDeviceResponse", validateDeviceResponse);
				
		return deviceRegistrationResponse;
	}

	private String insertDeviceSubTypeCode(String deviceSubType, String deviceTypeCode) {
		String jdbcUrl = getDbUrl();
	    String username = getDbUser();
	    String password = getDbPass();
		String sql = "insert into master.reg_device_sub_type values (\n" + 
				"	'" + deviceSubType + "',\n" + 
				"	'" + deviceTypeCode + "',\n" + 
				"	'" + deviceSubType + " "+ deviceTypeCode +"',\n" + 
				"	'" + deviceSubType + " " + deviceTypeCode + "',\n" + 
				"	true,\n" + 
				"	'now()',\n" + 
				"	'" + DateUtils.formatToISOString(LocalDateTime.now()) + "',\n" + 
				"	null,\n" + 
				"	null,\n" + 
				"	null,\n" + 
				"	null\n" + 
				");";
		
		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
				Statement stmt = conn.createStatement();) {
			int rowCount = stmt.executeUpdate(sql);
			if(rowCount == 1) {
				return deviceSubType;
			}
		} catch (SQLException e) {
			logger.info("sessionID", "IDA", "getDeviceTypeCode", "SQLException :" + e.getMessage());
		}
		return null;
	}

	/**
	 * Note that this update is only for registering devices for testing purpose.
	 * This step is not is not required for actual device registration.
	 */
	private String updateDeviceProviderId(String createdProviderid, String deviceProviderId) {
		String jdbcUrl = getDbUrl();
		String username = getDbUser();
		String password = getDbPass();
		String sql = "UPDATE master.device_provider set id = '" + deviceProviderId + "' where id = '"
				+ createdProviderid + "';";

		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
				Statement stmt = conn.createStatement();) {

			stmt.executeUpdate(sql);
			return "Database updated successfully";
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	private boolean isDeviceProviderIdExists(String deviceProviderId) {
		String jdbcUrl = getDbUrl();
	    String username = getDbUser();
	    String password = getDbPass();
	    String sql = "SELECT id FROM master.device_provider where id = '" + deviceProviderId
				+ "';";

	    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password); 
	        Statement stmt = conn.createStatement();
	    	ResultSet resultSet = stmt.executeQuery(sql);
	    		) {
	    	return resultSet.next() ? true : false;
	    } catch (SQLException e) {
			logger.info("sessionID", "IDA", "getDeviceTypeCode", "SQLException :" + e.getMessage());
			return false;
		}
	}
	
	private String getDeviceSubTypeCode(String deviceSubType, String deviceTypeCode) {
		String jdbcUrl = getDbUrl();
		String username = getDbUser();
		String password = getDbPass();
		String sql = "select code from master.reg_device_sub_type where dtyp_code='" + deviceTypeCode + "' and code='" + deviceSubType + "';";
		
		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password); 
				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(sql);
				) {
			return resultSet.next() ? resultSet.getString(1) : null;
		} catch (SQLException e) {
			logger.info("sessionID", "IDA", "getDeviceTypeCode", "SQLException :" + e.getMessage());
			return null;
		}
	}

	private String insertDeviceTypeCode(String deviceType) {
		String jdbcUrl = getDbUrl();
	    String username = getDbUser();
	    String password = getDbPass();
		String deviceTypeCode = deviceType;
		String sql = "insert into master.reg_device_type values (\n" + 
				"	'" + deviceTypeCode + "',\n" + 
				"	'" + deviceType + "',\n" + 
				"	'" + deviceType + " biometric',\n" + 
				"	true,\n" + 
				"	'now()',\n" + 
				"	'" + DateUtils.formatToISOString(LocalDateTime.now()) + "',\n" + 
				"	null,\n" + 
				"	null,\n" + 
				"	null,\n" + 
				"	null\n" + 
				");";
		
		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
				Statement stmt = conn.createStatement();) {
			int rowCount = stmt.executeUpdate(sql);
			if(rowCount == 1) {
				return deviceTypeCode;
			}
		} catch (SQLException e) {
			logger.info("sessionID", "IDA", "getDeviceTypeCode", "SQLException :" + e.getMessage());
		}
		return null;
	}

	private String getDeviceTypeCode(String deviceType) {
		String jdbcUrl = getDbUrl();
	    String username = getDbUser();
	    String password = getDbPass();
	    String sql = "select code from master.reg_device_type where code='" + deviceType + "';";

	    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password); 
	        Statement stmt = conn.createStatement();
	    	ResultSet resultSet = stmt.executeQuery(sql);
	    		) {
	    	return resultSet.next() ? resultSet.getString(1) : null;
	    } catch (SQLException e) {
			logger.info("sessionID", "IDA", "getDeviceTypeCode", "SQLException :" + e.getMessage());
			return null;
		}
	}

	private String getDigitalIdInfo(Map<String, Object> deviceInfoMap, Object attribute) {
		return (String)((Map)deviceInfoMap.get("digitalId")).get(attribute);
	}

	@PostMapping(path = "/registerMosipDeviceService", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerMosipDeviceService(
			@RequestParam(name = "deviceProviderId", required = true) String deviceProviderId, 
			@RequestParam(name = "deviceTypeCode", required = true) String deviceTypeCode, 
			@RequestParam(name = "deviceSubTypeCode", required = true) String deviceSubTypeCode,
			@RequestParam(name = "make", required = true) String make, 
			@RequestParam(name = "model", required = true) String model, 
			@RequestParam(name = "serviceVersion", required = true) String serviceVersion
	) throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> addMdsRequestTemplate = new LinkedHashMap<>();

		addMdsRequestTemplate.put("$DEVICE_PROVIDER_ID", deviceProviderId);
		addMdsRequestTemplate.put("$MAKE", make);
		addMdsRequestTemplate.put("$MODEL", model);
		addMdsRequestTemplate.put("$DEVICE_TYPE_CODE", deviceTypeCode);
		addMdsRequestTemplate.put("$DEVICE_SUB_TYPE_CODE", deviceSubTypeCode);
		addMdsRequestTemplate.put("$SERVICE_VERSION", serviceVersion);
		return invokeDeviceMgntApi("add-mds-request-template", addMdsRequestTemplate, addMdsRequestUrl);
	}

	@PostMapping(path = "/addDeviceProvider", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> addDeviceProvider(
			@RequestParam(name = "deviceProviderId", required = true) @Nullable String deviceProviderId,
			@RequestParam(name = "deviceProviderName", required = true) @Nullable String deviceProviderName)
			throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> addDeviceProviderRequestTemplate = new LinkedHashMap<>();
		addDeviceProviderRequestTemplate.put("$DEVICE_PROVIDER_ID", deviceProviderId);
		addDeviceProviderRequestTemplate.put("$DEVICE_PROVIDER_NAME", deviceProviderName);
		return invokeDeviceMgntApi("add-device-provider-request-template", addDeviceProviderRequestTemplate,
				addDeviceProviderUrl,
				(devMgntResponse, responseMap) -> {
					if(devMgntResponse.getStatusCode() == HttpStatus.OK) {
						Map body = devMgntResponse.getBody();
						if(!(body.get("errors") instanceof List && !((List)body.get("errors")).isEmpty())) {
							Object object = body.get("response");
							if(object instanceof Map) {
								Map resMap = (Map)object;
								String createdId=(String)resMap.get("id");
								String dbUpdateStatus = updateDeviceProviderId(createdId, deviceProviderId);
								responseMap.put("deviceProviderIdUpdateInDBStatus", dbUpdateStatus);
							} else {
								responseMap.put("deviceProviderIdInDBStatus", "Not updated");
							}
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	@PostMapping(path = "/registerDevice", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> registerDevice(
			@RequestParam(name = "deviceProviderId", required = true) String deviceProviderId, 
			@RequestParam(name = "deviceProviderName", required = true) String deviceProviderName, 
			@RequestParam(name = "deviceTypeCode", required = true) String deviceTypeCode, 
			@RequestParam(name = "deviceSubTypeCode", required = true) String deviceSubTypeCode,
			@RequestParam(name = "make", required = true) String make, 
			@RequestParam(name = "model", required = true) String model, 
			@RequestParam(name = "deviceId", required = true) String deviceId,
			@RequestParam(name = "deviceSubId", required = true) String deviceSubId,
			@RequestParam(name = "serialNumber", required = true) String serialNumber,
			@RequestParam(name = "deviceExpiryDate", required = true) String deviceExpiryDate,
			@RequestParam(name = "purpose", required = true) String purpose,
			@RequestParam(name = "deviceCode", required = true) String deviceCode			
			) throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> addMdsRequestTemplate = new LinkedHashMap<>();
		addMdsRequestTemplate.put("$DEVICE_ID", deviceId);
		addMdsRequestTemplate.put("$DEVICE_SUB_ID", deviceSubId);
		addMdsRequestTemplate.put("$DEVICE_PROVIDER_ID", deviceProviderId);
		addMdsRequestTemplate.put("$DEVICE_PROVIDER_NAME", deviceProviderName);
		addMdsRequestTemplate.put("$MAKE", make);
		addMdsRequestTemplate.put("$MODEL", model);
		addMdsRequestTemplate.put("$SERIAL_NO", serialNumber);
		addMdsRequestTemplate.put("$DEVICE_TYPE_CODE", deviceTypeCode);
		addMdsRequestTemplate.put("$DEVICE_SUB_TYPE_CODE", deviceSubTypeCode);
		addMdsRequestTemplate.put("$DEVICE_EXPIRY_DATE", DateUtils
				.toISOString(DateUtils.parseToDate(deviceExpiryDate, env.getProperty("datetime.pattern.withzone"))));
		addMdsRequestTemplate.put("$PURPOSE", purpose);
		
		Map<String, Object> unencodedRequestMap = new LinkedHashMap<>();
		
		return invokeDeviceMgntApi("add-device-request-template", addMdsRequestTemplate, registerDeviceRequestUrl, 
				requestMap -> {
					Map<String, Object> deviceDataMap = (Map<String, Object>) requestMap.get("deviceData");
					Map<String, Object> deviceInfoMap = (Map<String, Object>) deviceDataMap.get("deviceInfo");
					Map<String, Object> digitalIdMap = (Map<String, Object>) deviceInfoMap.get("digitalId");

					unencodedRequestMap.putAll(Collections.unmodifiableMap(requestMap));
					
					LinkedHashMap<String, Object> requestMapNew = new LinkedHashMap<>(requestMap);
					LinkedHashMap<String, Object> deviceDataMapNew = new LinkedHashMap<>(deviceDataMap);
					LinkedHashMap<String, Object> deviceInfoMapNew = new LinkedHashMap<>(deviceInfoMap);

					try {
						deviceInfoMapNew.put("digitalId", CryptoUtil.encodeBase64(mapper.writeValueAsBytes(digitalIdMap)));
						deviceInfoMapNew.put("timeStamp", DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
						deviceDataMapNew.put("deviceInfo", deviceInfoMapNew);
						requestMapNew.put("deviceData", CryptoUtil.encodeBase64(mapper.writeValueAsBytes(deviceDataMapNew)));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					
					return requestMapNew;
				},
				(devMgntResponse, responseMap) -> {
					responseMap.put("FullRequestMap", unencodedRequestMap);
					if(devMgntResponse.getStatusCode() == HttpStatus.OK) {
						Map body = devMgntResponse.getBody();
						if(!(body.get("errors") instanceof List && ((List)body.get("errors")).isEmpty())) {
							Object object = body.get("response");
							if(object instanceof String) {
								try {
									String jwsResponse = (String)object;
									String resStr = jWSSignAndVerifyController.getPayloadFromJwsSingature(jwsResponse);
									Map<String, Object> resMap = mapper.readValue(resStr.getBytes(), Map.class);
									String createdCode=(String)resMap.get("deviceCode");
									String dbUpdateStatus = updateDeviceCode(createdCode, deviceCode);
									responseMap.put("deviceCodeUpdateInDBStatus", dbUpdateStatus);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								responseMap.put("deviceCodeUpdateInDBStatus", "Not updated");
							}
						}
					}
				});
	}
	
	@PostMapping(path = "/validateDevice", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> validateDevice(
			@RequestParam(name = "deviceProviderId", required = true) String deviceProviderId, 
			@RequestParam(name = "deviceProviderName", required = true) String deviceProviderName, 
			@RequestParam(name = "deviceType", required = true) String deviceType, 
			@RequestParam(name = "deviceSubType", required = true) String deviceSubType,
			@RequestParam(name = "make", required = true) String make, 
			@RequestParam(name = "model", required = true) String model, 
			@RequestParam(name = "serialNumber", required = true) String serialNumber,
			@RequestParam(name = "deviceCode", required = true) String deviceCode,
			@RequestParam(name = "serviceVersion", required = true) String serviceVersion
			) throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> validateDeviceTemplate = new LinkedHashMap<>();
		validateDeviceTemplate.put("$DEVICE_PROVIDER_ID", deviceProviderId);
		validateDeviceTemplate.put("$DEVICE_PROVIDER_NAME", deviceProviderName);
		validateDeviceTemplate.put("$MAKE", make);
		validateDeviceTemplate.put("$MODEL", model);
		validateDeviceTemplate.put("$SERIAL_NO", serialNumber);
		validateDeviceTemplate.put("$DEVICE_TYPE", deviceType);
		validateDeviceTemplate.put("$DEVICE_SUB_TYPE", deviceSubType);
		validateDeviceTemplate.put("$DEVICE_CODE", deviceCode);
		validateDeviceTemplate.put("$SERVICE_VERSION", serviceVersion);
		
		return invokeDeviceMgntApi("validate-device-request-template", validateDeviceTemplate, validateDeviceRequestUrl);
		
	}
	
	private Map<String, Object> invokeDeviceMgntApi(String requestTemplateProperty, 
			Map<String, String> templateProperties, 
			String deviceMgntRequestUrl) throws JsonParseException, JsonMappingException, IOException {
		return invokeDeviceMgntApi(requestTemplateProperty, templateProperties, deviceMgntRequestUrl, null);
	}
	
	private Map<String, Object> invokeDeviceMgntApi(String requestTemplateProperty, 
			Map<String, String> templateProperties, 
			String deviceMgntRequestUrl, 
			BiConsumer<ResponseEntity<Map>,Map<String, Object>> responseConsumer) throws JsonParseException, JsonMappingException, IOException {
		return invokeDeviceMgntApi(requestTemplateProperty, templateProperties, deviceMgntRequestUrl, Function.identity(), responseConsumer);
	}
	
	
	private Map<String, Object> invokeDeviceMgntApi(String requestTemplateProperty, 
			Map<String, String> templateProperties, 
			String deviceMgntRequestUrl, 
			Function<Map<String, Object>, Map<String, Object>> requestMapModifier, 
			BiConsumer<ResponseEntity<Map>,Map<String, Object>> responseConsumer) throws JsonParseException, JsonMappingException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateUserAuthToken();
				if(authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		
		String requestTemplate = env.getProperty(requestTemplateProperty, String.class);
		for (String propertyKey : templateProperties.keySet()) {
			requestTemplate = requestTemplate.replace(propertyKey, templateProperties.get(propertyKey));
		}
		
		Map<String, Object> response = new LinkedHashMap<>();
		
		Map<String, Object> requestMap = mapper.readValue(requestTemplate, Map.class);
		
		if(requestMapModifier != null) {
			requestMap = requestMapModifier.apply(requestMap);
		}
		
		Map<String, Object> wrappedRequest = createRequest(requestMap);
		response.put("requestHttpBody", wrappedRequest);
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(wrappedRequest);
		ResponseEntity<Map> deviceMgntResponse;
		try {
			deviceMgntResponse = restTemplate.exchange(deviceMgntRequestUrl, HttpMethod.POST, httpEntity, Map.class);
			response.put("responseHttpStatus", deviceMgntResponse.getStatusCodeValue());
			response.put("responseHttpBody", deviceMgntResponse.getBody());
			if(responseConsumer != null) {
				responseConsumer.accept(deviceMgntResponse, response);
			}
		} catch (RestClientResponseException e) {
			System.err.println(e.getResponseBodyAsString());
			response.put("responseHttpStatus", e.getRawStatusCode());
			byte[] responseBodyAsByteArray = e.getResponseBodyAsByteArray();
			if(responseBodyAsByteArray != null && responseBodyAsByteArray.length > 0) {
				Map<String, Object> errorResponseMap = mapper.readValue(responseBodyAsByteArray, Map.class);
				response.put("responseHttpBody", errorResponseMap);
				if(responseConsumer != null) {
					responseConsumer.accept(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build(), response);
				}
			}
		}
		
		
		return response;

	}

	/**
	 * Note that this update is only for registering devices for testing purpose.
	 * This step is not is not required for actual device registration.
	 */
	private String updateDeviceCode(String createdCode, String deviceCode) {
		String jdbcUrl = getDbUrl();
	    String username = getDbUser();
	    String password = getDbPass();
	    String sql = "update master.registered_device_master set code='" + deviceCode + "' where code='" + createdCode + "'";

	    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password); 
	        Statement stmt = conn.createStatement();) {
	      
	      stmt.executeUpdate(sql);
	      return "Database updated successfully";
	    } catch (SQLException e) {
	      e.printStackTrace();
	      return e.getMessage();
	    }
	}

	private String getDbUser() {
		return dbUtil.getDbUser(DBUtil.MASTER);
	}
	
	private String getDbPass() {
		return dbUtil.getDbPass(DBUtil.MASTER);
	}

	private String getDbUrl() {
		return dbUtil.getDbUrl(DBUtil.MASTER);
	}

	public static Map<String, Object> createRequest(Object requestBlock){
    	Map<String, Object> requestMap= new LinkedHashMap<>();
    	requestMap.put("request", requestBlock);
    	requestMap.put("id", "ida");
    	requestMap.put("requesttime", DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
    	return requestMap;
    }
	
	public String generateUserAuthToken() {
		ObjectNode requestBody = mapper.createObjectNode();
		requestBody.put("userName", env.getProperty("auth-token-generator-user.rest.userName"));
		requestBody.put("password", env.getProperty("auth-token-generator-user.rest.password"));
		requestBody.put("appId", env.getProperty("auth-token-generator-user.rest.appId"));
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient.create(env.getProperty("auth-token-generator-user.rest.uri")).post()
				.syncBody(request)
				.exchange().block();
		logger.info("sessionID", "IDA", "User Aythentication", "AuthResponse :" +  response.toEntity(String.class).block().getBody());
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if(list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}



}
