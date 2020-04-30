package io.mosip.registration.util;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.GenderRepository;
import io.mosip.registration.repositories.IndividualTypeRepository;
import io.mosip.registration.repositories.LocationRepository;
import io.mosip.registration.service.external.StorageService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketCreationService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.testrunner.MosipTestRunner;


/**
 * @author Arjun chandramohan
 *
 */
@Service
public class CommonUtil {
	/**
	 * Class to retrieve the the Registration Packet Status
	 */
	@Autowired
	PacketCreationService packetCreationService;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(CommonUtil.class);
	@Autowired
	MasterSyncService masterSync;
	@Autowired
	LocationRepository locationRepository;
	@Autowired
	private RegistrationDAO syncRegistrationDAO;
	@Autowired
	PacketSynchService packetSyncService;
	@Autowired
	StorageService storageService;
	@Autowired
	RegPacketStatusService regPacket;
	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;
	@Autowired
	RidGenerator<String> ridGeneratorImpl;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	PacketUploadService PacketUploadservice;
	@Autowired
	RegistrationDAO regDAO;
	@Autowired
	OTPManager otpManager;
	@Autowired
	RegistrationApprovalService registrationApprovalService;
	@Autowired
	AESEncryptionService aesEncryptionService;
	@Autowired
	RegPacketStatusDAO regPacketStatusDAO;
	@Autowired
	MasterSyncService masterSyncService;
	@Autowired
	GenderRepository genderRepository;
	@Autowired
	IndividualTypeRepository individualTypeRepository;

	private HashMap<String, Registration> registrationMap = new HashMap<>();

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert response DTO
	 */
	public void verifyAssertionResponse(String expectedMsg, ResponseDTO response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert ResponseDTO");
		try {
			if (!response.getSuccessResponseDTO().getMessage().isEmpty()) {
				Assert.assertEquals(response.getSuccessResponseDTO().getMessage(), expectedMsg);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"SUCCESS MESSAGE - " + response.getSuccessResponseDTO().getMessage());
				// Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
			}
		} catch (NullPointerException e) {

			Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(), expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"FAILURE MESSAGE - " + response.getErrorResponseDTOs().get(0).getMessage());
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert response DTO
	 */
	public void verifyAssertNotNull(ResponseDTO response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert ResponseDTO");
		try {
			Assert.assertNotNull(response.getSuccessResponseDTO().getMessage());
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"SUCCESS MESSAGE - " + response.getSuccessResponseDTO().getMessage());
			// Reporter.log(ExceptionUtils.getStackTrace(nullPointerException));
		} catch (NullPointerException e) {
			Assert.assertNotNull(response.getErrorResponseDTOs().get(0).getMessage());
			// Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(),
			// expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"FAILURE MESSAGE - " + response.getErrorResponseDTOs().get(0).getMessage());
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert String
	 */

	public void verifyAssertionResponse(String expectedMsg, String response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert String");
		try {
			if (response.equalsIgnoreCase("Success")) {
				Assert.assertEquals(response, expectedMsg);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "SUCCESS MESSAGE - " + response);
			}
		} catch (NullPointerException e) {
			Assert.assertEquals(response, expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "FAILURE MESSAGE - " + response);
		}
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to assert String
	 */

	public boolean verifyAssertionResponseMessage(String expectedMsg, String response) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Assert String");
		boolean output = false;
		try {
			if (response.equalsIgnoreCase("SYNC_SUCCESS") || response.equalsIgnoreCase("Sync successful")
					|| response.equalsIgnoreCase("Success")) {
				Assert.assertEquals(response, expectedMsg);
				output = true;
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "SUCCESS MESSAGE - " + response);
			}
		} catch (NullPointerException e) {
			Assert.assertEquals(response, expectedMsg);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "FAILURE MESSAGE - " + response);
		}
		return output;
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to verify file exist in given path
	 */

	public boolean verifyIfFileExist(String filePath) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "verifyIfFileExist");

		boolean result = false;
		File file = new File(filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION));
		if (file.exists()) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"File exist in the specified loacation-- " + filePath);
			result = true;
		}
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Return Value - " + result);
		return result;
	}

	/**
	 * @param pageName
	 *            accept the name of the config properties file
	 * @return return config file object to read config file
	 */
	public Properties readPropertyFile(String apiname, String testCaseName, String propertyFileName) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "readPropertyFile");
		Properties prop = new Properties();
		InputStream input = null;
		/*
		 * String propertiesFilePath = "src" + File.separator + "main" + File.separator
		 * + "resources" + File.separator+"Registration"+File.separator + apiname +
		 * File.separator + testCaseName + File.separator + propertyFileName +
		 * ".properties";
		 */
		/*
		 * String path = this.getClass().getClassLoader() .getResource("./" +
		 * "Registration" +"/" + apiName).getPath();
		 */
	/*	String propertiesFilePath = this.getClass().getClassLoader().getResource(
				"./" + "Registration" + "/" + apiname + "/" + testCaseName + "/" + propertyFileName + ".properties")
				.getPath();*/
		String propertiesFilePath=MosipTestRunner.getGlobalResourcePath()+"/"+"Registration" + "/" + apiname + "/" + testCaseName + "/" + propertyFileName + ".properties";
		
		
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Property File Path - " + propertiesFilePath);
		try {
			input = new FileInputStream(propertiesFilePath);
			prop.load(input);
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"Provide correct name for the config file" + ExceptionUtils.getStackTrace(fileNotFoundException));
		} catch (IOException ioException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
		}

		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioExceptionFinally) {
					LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
							ExceptionUtils.getStackTrace(ioExceptionFinally));
				}
			}
		}
		return prop;
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to create test data for PacketSyncService
	 *            "syncPacketsToServer"
	 */

	public RegistrationPacketSyncDTO syncdatatoserver_validDataProvider(String langcode, String synStatus,
			String syncType, String statusCode, String biometricDataPath, String demographicDataPath,
			String proofImagePath, String userID, String centerID, String stationID, String status,
			String invalidRegID) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "syncdatatoserver_validDataProvider");
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		try {
			HashMap<String, String> packetResponse = new HashMap<>();
			Set<String> dbData = new HashSet<String>();
			List<PacketStatusDTO> packetDto = new ArrayList<>();
			List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				packetResponse = packetCreation(statusCode, biometricDataPath, demographicDataPath, proofImagePath,
						System.getProperty("userID"), centerID, stationID, status, invalidRegID);
				dbData.add(packetResponse.get("RANDOMID"));
				Thread.sleep(2000);

				Registration registration = syncRegistrationDAO.getRegistrationById(
						RegistrationClientStatusCode.APPROVED.getCode(), packetResponse.get("RANDOMID"));

				packetDto.add(packetStatusDtoPreperation(registration, registration.getClientStatusCode()));
			}
			ResponseDTO response = null;
			if (!packetDto.isEmpty()) {
				for (PacketStatusDTO packetToBeSynch : packetDto) {
					SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
					syncDto.setLangCode(langcode);
					syncDto.setRegistrationId(packetToBeSynch.getFileName());
					syncDto.setRegistrationType(packetToBeSynch.getPacketStatus().toUpperCase());
					syncDto.setPacketHashValue(packetToBeSynch.getPacketHash());
					syncDto.setPacketSize(packetToBeSynch.getPacketSize());
					syncDto.setSupervisorStatus(packetToBeSynch.getSupervisorStatus());
					syncDto.setSupervisorComment(packetToBeSynch.getSupervisorComments());
					syncDtoList.add(syncDto);
				}
				registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
				registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
				registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
			}
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return registrationPacketSyncDTO;
	}

	/**
	 * @author Leona Mary
	 * @param expectedMsg
	 * @param response
	 * 
	 *            this method to create invalid test data for PacketSyncService
	 *            "syncPacketsToServer"
	 */
	public RegistrationPacketSyncDTO syncPacketsToServer_InvalidDataProvider(String langcode, String statusComment,
			String registrationID, String synStatus, String syncType) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "syncPacketsToServer_InvalidDataProvider");
		List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
		RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
		SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
		try {
			syncDto.setLangCode(langcode);
			syncDto.setRegistrationId(registrationID);
			syncDtoList.add(syncDto);
			registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
			registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
			registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
			registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return registrationPacketSyncDTO;
	}

	/**
	 * @author Leona Mary
	 * @param statusCode
	 * @param userJsonFile
	 * @param identityJsonFile
	 * @param POAPOBPORPOIJpg
	 * @return HashMap with reg ID and success message and failure message
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public HashMap<String, String> packetCreation(String statusCode, String userJsonFile, String identityJsonFile,
			String POAPOBPORPOIJpg, String userID, String centerID, String stationID, String createPacket,
			String invalidRegID) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "packetCreation");
		HashMap<String, String> returnValues = new HashMap<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JSR310Module());
			mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);
			RegistrationDTO registrationDTO;
			File bioPath =new File(CommonUtil.getResourcePath()+userJsonFile);
			//File bioPath = new File(this.getClass().getClassLoader().getResource(userJsonFile).getPath());
			registrationDTO = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(bioPath.getAbsolutePath())), StandardCharsets.UTF_8),
					RegistrationDTO.class);
			File demoPath=new File(CommonUtil.getResourcePath()+identityJsonFile);
			//File demoPath = new File(this.getClass().getClassLoader().getResource(identityJsonFile).getPath());

			IndividualIdentity identity = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(demoPath.getAbsolutePath())), StandardCharsets.UTF_8),
					IndividualIdentity.class);

			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Create and set Document DTO to identity");
			Map<String, DocumentDetailsDTO> documents = setDocumentDetailsDTO(identity, CommonUtil.getResourcePath()+POAPOBPORPOIJpg);
			registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
			registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Set Identity DTO to Registration DTO");
			registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);
			SessionContext.getInstance().getUserContext().setUserId(userID);
			RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
			registrationCenter.setRegistrationCenterId(centerID);
			SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
			registrationDTO.getOsiDataDTO().setOperatorID(userID);
			registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
			registrationDTO.getRegistrationMetaDataDTO().setMachineId(userID);
			String randomId = "";
			if (invalidRegID.equalsIgnoreCase("YES")) {
				randomId = "1234567890";
				/* ridGeneratorImpl.generateId(centerID, stationID); */
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"Invalid Registration ID generated - " + randomId);
				returnValues.put("RANDOMID", randomId);
				registrationDTO.setRegistrationId(randomId);
				System.out.println("ID==== " + randomId);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"Invalid Registration ID generated - " + randomId);
			} else {
				randomId = ridGeneratorImpl.generateId(centerID, stationID);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"Valid Registration ID generated - " + randomId);
				returnValues.put("RANDOMID", randomId);
				registrationDTO.setRegistrationId(randomId);
				System.out.println("ID==== " + randomId);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"Valid Registration ID generated - " + randomId);
			}
			Thread.sleep(2000);
			if (createPacket.equalsIgnoreCase("YES")) {

				ResponseDTO response = packetHandlerService.handle(registrationDTO);

				if (!(response.getSuccessResponseDTO().getMessage() == null)) {

					returnValues.put("SUCCESSRESPONSE", response.getSuccessResponseDTO().getMessage());
					LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
							response.getSuccessResponseDTO().getMessage());
					LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
							(String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG));
					if ((response.getSuccessResponseDTO().getMessage().contains("Success"))
							&& (((String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG))
									.equalsIgnoreCase(RegistrationConstants.DISABLE))) {
						registrationApprovalService.updateRegistration(randomId, RegistrationConstants.EMPTY,
								RegistrationClientStatusCode.APPROVED.getCode());
						LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
								randomId + " Packet has been APPROVED");
					}

				}
			} else {
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "for Storage Service");
				// 1. create packet
				byte[] inMemoryZipFile = packetCreationService.create(registrationDTO);

				byte[] encryptedPacket = aesEncryptionService.encrypt(inMemoryZipFile);

				// Generate Zip File Name with absolute path
				String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), encryptedPacket);

				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "filePath----- " + filePath);

				returnValues.put("FILEPATH", filePath);
			}

		} catch (IOException | RegBaseCheckedException exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		} catch (InterruptedException interruptedException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(interruptedException));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return returnValues;
	}

	/**
	 * method to create the packet when we update the UIN
	 * 
	 * @param statusCode
	 * @param biometricPath
	 * @param demographicPath
	 * @param proofOfImagePath
	 * @param userID
	 * @param centerID
	 * @param stationID
	 * @return
	 */
	public HashMap<String, String> packetCreationForUINUpdate(String statusCode, String biometricPath,
			String demographicPath, String proofOfImagePath, String userID, String centerID, String stationID,
			String uin) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "packetCreation");
		HashMap<String, String> packetCreationMap = new HashMap<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JSR310Module());
			mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);
			RegistrationDTO registrationDTO;
			File bioPath=new File(CommonUtil.getResourcePath()+biometricPath);
			//File bioPath = new File(this.getClass().getClassLoader().getResource(biometricPath).getPath());
			registrationDTO = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(bioPath.getAbsolutePath())), StandardCharsets.UTF_8),
					RegistrationDTO.class);
			File demoPath=new File(CommonUtil.getResourcePath()+demographicPath);
			//File demoPath = new File(this.getClass().getClassLoader().getResource(demographicPath).getPath());

			IndividualIdentity identity = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(demoPath.getAbsolutePath())), StandardCharsets.UTF_8),
					IndividualIdentity.class);

			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Create and set Document DTO to identity");

			Map<String, DocumentDetailsDTO> documents = setDocumentDetailsDTO(identity, proofOfImagePath);
			registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
			registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Set Identity DTO to Registration DTO");
			registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);

			registrationDTO.getSelectionListDTO().setUinId(uin);
			registrationDTO.getOsiDataDTO().setOperatorID(userID);
			registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
			registrationDTO.getRegistrationMetaDataDTO().setMachineId(stationID);
			registrationDTO.getRegistrationMetaDataDTO().setUin(uin);

			SessionContext.getInstance().getUserContext().setUserId(userID);
			RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
			registrationCenter.setRegistrationCenterId(centerID);
			SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
			String randomId = "";
			/**
			 * getting rid
			 */
			randomId = ridGeneratorImpl.generateId(centerID, stationID);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"Valid Registration ID generated - " + randomId);
			packetCreationMap.put("RANDOMID", randomId);
			registrationDTO.setRegistrationId(randomId);
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"Valid Registration ID generated - " + randomId);

			Thread.sleep(2000);

			ResponseDTO response = packetHandlerService.handle(registrationDTO);

			if (!(response.getSuccessResponseDTO().getMessage() == null)) {

				packetCreationMap.put("SUCCESSRESPONSE", response.getSuccessResponseDTO().getMessage());
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						response.getSuccessResponseDTO().getMessage());
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						(String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG));
				if ((response.getSuccessResponseDTO().getMessage().contains("Success"))
						&& (((String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG))
								.equalsIgnoreCase(RegistrationConstants.DISABLE))) {
					registrationApprovalService.updateRegistration(randomId, RegistrationConstants.EMPTY,
							RegistrationClientStatusCode.APPROVED.getCode());
					LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
							randomId + " Packet has been APPROVED");
				}
				packetCreationMap.put("MESSAGE", response.getSuccessResponseDTO().getMessage());
			} else {
				packetCreationMap.put("MESSAGE", response.getErrorResponseDTOs().get(0).getMessage());
			}

		} catch (IOException | RegBaseCheckedException exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		} catch (InterruptedException interruptedException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(interruptedException));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return packetCreationMap;
	}

	/**
	 * 
	 * @param Path
	 *            - To fetch the Finger print byte array from json file
	 * @return - the list of FingerPrint details
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<IrisDetailsDTO> getIrisTestData(String Path) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getIrisTestData");
		List<IrisDetailsDTO> irisData = new ArrayList<>();
		try {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "Convert Json to IrisDetailsDTO");
			ObjectMapper mapper = new ObjectMapper();
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(this.getClass().getClassLoader().getResource(Path).getPath());
			// Read JSON file
			Object obj = jsonParser.parse(reader);
			JSONArray jArray = (JSONArray) obj;
			String s = jArray.toString();
			irisData = mapper.readValue(s,
					mapper.getTypeFactory().constructCollectionType(List.class, IrisDetailsDTO.class));

		} catch (IOException | ParseException exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return irisData;
	}

	/**
	 * 
	 * @param Path
	 *            - To create DocumentDetailsDTO from JSON
	 * @return - the list of FingerPrint details
	 * @throws IOException
	 * @throws ParseException
	 */

	public Map<String, DocumentDetailsDTO> setDocumentDetailsDTO(IndividualIdentity identity, String path) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setDocumentDetailsDTO");
		byte[] data;
		Map<String, DocumentDetailsDTO> documents = new HashMap<String, DocumentDetailsDTO>();
		try {
			data = IOUtils.toByteArray(
					new FileInputStream(new File(path)));
			DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
			documentDetailsDTOAddress.setDocument(data);
			documentDetailsDTOAddress.setType("Passport");
			documentDetailsDTOAddress.setFormat("jpg");
			documentDetailsDTOAddress.setValue("ProofOfIdentity");
			documentDetailsDTOAddress.setOwner("Self");
			identity.setProofOfIdentity(documentDetailsDTOAddress);
			documents.put("POI", documentDetailsDTOAddress);
			DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
			documentDetailsResidenceDTO.setDocument(data);
			documentDetailsResidenceDTO.setType("Passport");
			documentDetailsResidenceDTO.setFormat("jpg");
			documentDetailsResidenceDTO.setValue("ProofOfAddress");
			documentDetailsResidenceDTO.setOwner("hof");
			identity.setProofOfAddress(documentDetailsResidenceDTO);
			documents.put("POA", documentDetailsResidenceDTO);
			documentDetailsDTOAddress = new DocumentDetailsDTO();
			documentDetailsDTOAddress.setDocument(data);
			documentDetailsDTOAddress.setType("Passport");
			documentDetailsDTOAddress.setFormat("jpg");
			documentDetailsDTOAddress.setValue("ProofOfRelationship");
			documentDetailsDTOAddress.setOwner("Self");
			identity.setProofOfRelationship(documentDetailsDTOAddress);
			documents.put("POR", documentDetailsDTOAddress);
			documentDetailsResidenceDTO = new DocumentDetailsDTO();
			documentDetailsResidenceDTO.setDocument(data);
			documentDetailsResidenceDTO.setType("Passport");
			documentDetailsResidenceDTO.setFormat("jpg");
			documentDetailsResidenceDTO.setValue("DateOfBirthProof");
			documentDetailsResidenceDTO.setOwner("hof");
			identity.setProofOfDateOfBirth(documentDetailsResidenceDTO);
			documents.put("POB", documentDetailsResidenceDTO);
			DocumentDetailsDTO documentDetailsDTO = identity.getProofOfIdentity();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfAddress();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfRelationship();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfDateOfBirth();
			documentDetailsDTO.setDocument(data);
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(fileNotFoundException));
		} catch (IOException ioException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
		}
		return documents;
	}

	/**
	 * 
	 * @param Path
	 *            - To create ApplicantdocumentDTO from JSON
	 * @return - ApplicantDocumentDTO
	 * @throws IOException
	 * @throws ParseException
	 */
	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws RegBaseCheckedException {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setApplicantDocumentDTO");
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		byte[] data;
		try {
			data = IOUtils.toByteArray(new FileInputStream(new File(MosipTestRunner.getGlobalResourcePath()+ConstantValues.ACKNOWLEDGEMENT_IMAGE)));
			applicantDocumentDTO.setAcknowledgeReceipt(data);
			applicantDocumentDTO.setAcknowledgeReceiptName("RegistrationAcknowledgement.jpg");
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return applicantDocumentDTO;
	}

	/**
	 * 
	 * @param Path
	 *            - To getImageDTO
	 * @return - byteArray of image
	 * @throws IOException
	 * @throws ParseException
	 */

	public static byte[] getImageBytes(String filePath) {
		filePath = ConstantValues.PCK_HANDLER_PATH.concat(filePath);
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getImageBytes");
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, filePath);
		byte[] bytesArray = null;
		try {
			InputStream file = CommonUtil.class.getResourceAsStream(filePath);
			bytesArray = new byte[(int) file.available()];
			file.read(bytesArray);
			file.close();
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(fileNotFoundException));
		} catch (IOException exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return bytesArray;
	}

	/**
	 * 
	 * @param Path
	 *            - To getImageDTO
	 * @return - byteArray of image
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getFingerprintDetailsDTO");
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();
		try {

			if (personType.equals("applicant")) {
				FingerprintDetailsDTO fingerprint = buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"BothThumbs.jpg", 85.0, false, "thumbs", 0);
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"rightThumb.jpg", 80.0, false, "rightThumb", 2));
				fingerList.add(fingerprint);
				fingerprint = buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG, "LeftPalm.jpg", 80.0, false,
						"leftSlap", 3);
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"leftIndex.jpg", 80.0, false, "leftIndex", 3));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"leftMiddle.jpg", 80.0, false, "leftMiddle", 1));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"leftRing.jpg", 80.0, false, "leftRing", 2));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"leftLittle.jpg", 80.0, false, "leftLittle", 0));
				fingerList.add(fingerprint);

				fingerprint = buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG, "RightPalm.jpg", 95.0, false,
						"rightSlap", 2);
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"rightIndex.jpg", 80.0, false, "rightIndex", 3));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"rightMiddle.jpg", 80.0, false, "rightMiddle", 1));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"rightRing.jpg", 80.0, false, "rightRing", 2));
				fingerprint.getSegmentedFingerprints().add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG,
						"rightLittle.jpg", 80.0, false, "rightLittle", 0));
				fingerList.add(fingerprint);
			} else {
				fingerList.add(buildFingerPrintDetailsDTO(ConstantValues.THUMB_JPG, personType + "LeftThumb.jpg", 0,
						false, "leftThumb", 0));
			}
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}

		return fingerList;
	}

	/**
	 * 
	 * @param Path
	 *            - To getImageDTO
	 * @return - byteArray of image
	 * @throws IOException
	 * @throws ParseException
	 */
	private static FingerprintDetailsDTO buildFingerPrintDetailsDTO(String imageLoc, String fingerprintImageName,
			double qualityScore, boolean isForceCaptured, String fingerType, int numRetry) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "buildFingerPrintDetailsDTO");
		FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
		byte[] data;
		try {
			data = IOUtils.toByteArray(new FileInputStream(new File(imageLoc)));
			fingerprintDetailsDTO.setFingerPrint(data);
			fingerprintDetailsDTO.setFingerprintImageName(fingerprintImageName);
			fingerprintDetailsDTO.setQualityScore(qualityScore);
			fingerprintDetailsDTO.setForceCaptured(isForceCaptured);
			fingerprintDetailsDTO.setFingerType(fingerType);
			fingerprintDetailsDTO.setNumRetry(numRetry);
			fingerprintDetailsDTO.setSegmentedFingerprints(new ArrayList<>());
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return fingerprintDetailsDTO;
	}

	/**
	 * 
	 * @param String
	 *            - Packet Ids
	 */
	public void deleteProcessedPackets(String syncResult) {

		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
				"REGISTRATION - INTEGRATION SCENARIO" + " DELETE PROCESSED PACKET");
		try {
			ResponseDTO delepacketStatus = regPacket.deleteRegistrationPackets();
			Assert.assertEquals("REGISTRATION_DELETION_BATCH_JOBS_SUCCESS",
					delepacketStatus.getSuccessResponseDTO().getMessage());
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
	}

	/**
	 * 
	 * @param String
	 *            - Biometric json file path
	 */
	public BiometricDTO getBiotestData(String Path) throws IOException, ParseException {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getBiometrictestData");
		BiometricDTO biodto = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(new File(Path));
			Object obj = jsonParser.parse(reader);
			String s = obj.toString();
			biodto = mapper.readValue(s, BiometricDTO.class);
			biodto.getApplicantBiometricDTO();
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(fileNotFoundException));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return biodto;
	}

	/**
	 * 
	 * @param String
	 *            - Value to Set Flag
	 */
	public void setFlag(String val) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setFlag");
		try {
			ApplicationContext.map().put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, val);
			ApplicationContext.map().put(RegistrationConstants.IRIS_DISABLE_FLAG, val);
			ApplicationContext.map().put(RegistrationConstants.FACE_DISABLE_FLAG, val);
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
	}

	/**
	 * Convertion of Registration to Packet Status DTO
	 * 
	 * @param registration
	 * @return
	 */
	public PacketStatusDTO packetStatusDtoPreperation(Registration registration, String status) {
		PacketStatusDTO statusDTO = new PacketStatusDTO();
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "setFlag");
		try {
			statusDTO.setFileName(registration.getId());
			statusDTO.setUploadStatus(status);
			statusDTO.setPacketClientStatus(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
			statusDTO.setPacketPath(registration.getAckFilename());
			statusDTO.setPacketServerStatus(registration.getServerStatusCode());
			statusDTO.setUploadStatus(registration.getFileUploadStatus());
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		}
		return statusDTO;
	}

	/**
	 * Get PUSHED client status code packets IDs from DB
	 * 
	 * 
	 * @return List<String>
	 */
	public List<String> getPUSHEDPacketIdsfromDB() {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getting packets by status PUSHED");
		List<String> packetIds = new ArrayList<>();
		try {
			List<Registration> registrationList = regPacketStatusDAO.getPacketIdsByStatusUploaded();
			for (Registration registration : registrationList) {
				String registrationId = registration.getId();
				registrationMap.put(registrationId, registration);
				packetIds.add(registrationId);
			}
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"getting packets by status post-sync has been ended");
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return packetIds;
	}

	/**
	 * create packet using PRE REG IDs
	 * 
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> preRegPacketCreation(RegistrationDTO preRegistrationDTO, String statusCode,
			String userJsonFile, String identityJsonFile, String documentFile, String userID, String centerID,
			String stationID, String packetType, String uin) {
		ObjectMapper mapper = new ObjectMapper();
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
				"preRegPacketCreation - create packets using PRE REG ID");
		RegistrationDTO registrationDTO;
		HashMap<String, String> returnValues = new HashMap<>();
		Map<String, DocumentDetailsDTO> documents = new HashMap<String, DocumentDetailsDTO>();
		IndividualIdentity identity = null;
		String randomId = "";
		/** The application language bundle. */
		ResourceBundle applicationLanguageBundle;
		try {
			File bioPath=new File(CommonUtil.getResourcePath()+userJsonFile);
			//File bioPath = new File(this.getClass().getClassLoader().getResource(userJsonFile).getPath());
			registrationDTO = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(bioPath.getAbsolutePath())), StandardCharsets.UTF_8),
					RegistrationDTO.class);

			if ((packetType.equalsIgnoreCase("PrIdOfChildWithoutDocs")
					|| packetType.equalsIgnoreCase("PrIdOfAdultWithoutDocs"))) {
				// Create RegistrationDTO without docs
				// Set Registration ID to RegistrationDTO
				registrationDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
				// Set Registration ID to RegistrationDTO
				registrationDTO.setRegistrationId(preRegistrationDTO.getRegistrationId());
				// Get identity from preRegistrationDTO to RegistrationDTO

				identity = (IndividualIdentity) preRegistrationDTO.getDemographicDTO().getDemographicInfoDTO()
						.getIdentity();

				HashMap<String, String> genderDetail = new HashMap<String, String>();
				for (int i = 0; i < identity.getGender().size(); i++) {
					List<GenderDto> genderDetailService = masterSyncService
							.getGenderDtls(identity.getGender().get(i).getLanguage());
					for (int j = 0; j < genderDetailService.size(); j++) {
						genderDetail.put(genderDetailService.get(j).getCode(),
								genderDetailService.get(j).getGenderName());
					}
					String genderValue = genderDetail.get(identity.getGender().get(i).getValue());
					identity.getGender().get(i).setValue(genderValue);
				}

				for (int i = 0; i < identity.getResidenceStatus().size(); i++) {
					List<IndividualType> individual_typeDetails = individualTypeRepository
							.findByIndividualTypeIdCodeAndIndividualTypeIdLangCodeAndIsActiveTrue(
									identity.getResidenceStatus().get(i).getValue(),
									identity.getResidenceStatus().get(i).getLanguage());
					identity.getResidenceStatus().get(i).setValue(individual_typeDetails.get(0).getName());
				}

				// Set Address details from code to Value in the RegistrationDTO
				for (int j = 0; j < identity.getRegion().size(); j++) {

					String lang = identity.getRegion().get(j).getLanguage();
					Locale applicationLanguageLocale = new Locale(lang != null ? lang.substring(0, 2) : "");
					applicationLanguageBundle = ResourceBundle.getBundle("labels", applicationLanguageLocale);
					// get Region details
					List<LocationDto> regionDetails = masterSync.findLocationByHierarchyCode(
							applicationLanguageBundle.getString("region"), identity.getRegion().get(j).getLanguage());

					identity.getRegion().get(j).setValue(regionDetails.get(0).getName());
					// get Province details
					List<LocationDto> provincedetails = masterSync.findProvianceByHierarchyCode(
							regionDetails.get(0).getCode(), identity.getProvince().get(j).getLanguage());

					String provinceCode = identity.getProvince().get(j).getValue();

					List<LocationDto> provinceResult = provincedetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(provinceCode)).collect(Collectors.toList());

					identity.getProvince().get(j).setValue(provinceResult.get(0).getName());

					// get City details
					List<LocationDto> cityDetails = masterSync.findProvianceByHierarchyCode(
							provinceResult.get(0).getCode(), identity.getCity().get(j).getLanguage());

					String cityCode = identity.getCity().get(j).getValue();

					List<LocationDto> cityResult = cityDetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(cityCode)).collect(Collectors.toList());

					identity.getCity().get(j).setValue(cityResult.get(0).getName());

					// get local admin authority details

					List<LocationDto> localadminDetails = masterSync.findProvianceByHierarchyCode(
							cityDetails.get(0).getCode(), identity.getZone().get(j).getLanguage());

					String localadminCode = identity.getZone().get(j).getValue();

					List<LocationDto> localadminResult = localadminDetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(localadminCode)).collect(Collectors.toList());

					identity.getZone().get(j).setValue(localadminResult.get(0).getName());

				}

				documents = setDocumentDetailsDTO(identity, documentFile);
				registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
				registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
				registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);

				registrationDTO.setRegistrationMetaDataDTO(preRegistrationDTO.getRegistrationMetaDataDTO());
				registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
				registrationDTO.getRegistrationMetaDataDTO().setMachineId(stationID);
				registrationDTO.getRegistrationMetaDataDTO().setConsentOfApplicant("YES");
			} else {
				// Set PreRegistration ID to RegistrationDTO
				registrationDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
				// Set Registration ID to RegistrationDTO
				registrationDTO.setRegistrationId(preRegistrationDTO.getRegistrationId());
				// Get identity from preRegistrationDTO to RegistrationDTO
				identity = (IndividualIdentity) preRegistrationDTO.getDemographicDTO().getDemographicInfoDTO()
						.getIdentity();

				HashMap<String, String> genderDetail = new HashMap<String, String>();
				for (int i = 0; i < identity.getGender().size(); i++) {
					List<GenderDto> genderDetailService = masterSyncService
							.getGenderDtls(identity.getGender().get(i).getLanguage());
					for (int j = 0; j < genderDetailService.size(); j++) {
						genderDetail.put(genderDetailService.get(j).getCode(),
								genderDetailService.get(j).getGenderName());
					}
					String genderValue = genderDetail.get(identity.getGender().get(i).getValue());
					identity.getGender().get(i).setValue(genderValue);
				}

				for (int i = 0; i < identity.getResidenceStatus().size(); i++) {
					List<IndividualType> individual_typeDetails = individualTypeRepository
							.findByIndividualTypeIdCodeAndIndividualTypeIdLangCodeAndIsActiveTrue(
									identity.getResidenceStatus().get(i).getValue(),
									identity.getResidenceStatus().get(i).getLanguage());
					identity.getResidenceStatus().get(i).setValue(individual_typeDetails.get(0).getName());
				}
				// Set Address details from code to Value in the RegistrationDTO
				for (int j = 0; j < identity.getRegion().size(); j++) {

					String lang = identity.getRegion().get(j).getLanguage();
					Locale applicationLanguageLocale = new Locale(lang != null ? lang.substring(0, 2) : "");
					applicationLanguageBundle = ResourceBundle.getBundle("labels", applicationLanguageLocale);
					// get Region details
					List<LocationDto> regionDetails = masterSync.findLocationByHierarchyCode(
							applicationLanguageBundle.getString("region"), identity.getRegion().get(j).getLanguage());

					identity.getRegion().get(j).setValue(regionDetails.get(0).getName());

					// get Province details
					List<LocationDto> provincedetails = masterSync.findProvianceByHierarchyCode(
							regionDetails.get(0).getCode(), identity.getProvince().get(j).getLanguage());

					String provinceCode = identity.getProvince().get(j).getValue();

					List<LocationDto> provinceResult = provincedetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(provinceCode)).collect(Collectors.toList());

					identity.getProvince().get(j).setValue(provinceResult.get(0).getName());

					// get City details
					List<LocationDto> cityDetails = masterSync.findProvianceByHierarchyCode(
							provinceResult.get(0).getCode(), identity.getCity().get(j).getLanguage());

					String cityCode = identity.getCity().get(j).getValue();

					List<LocationDto> cityResult = cityDetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(cityCode)).collect(Collectors.toList());

					identity.getCity().get(j).setValue(cityResult.get(0).getName());

					// get local admin authority details

					List<LocationDto> localadminDetails = masterSync.findProvianceByHierarchyCode(
							cityDetails.get(0).getCode(), identity.getZone().get(j).getLanguage());

					String localadminCode = identity.getZone().get(j).getValue();

					List<LocationDto> localadminResult = localadminDetails.stream()
							.filter(o -> o.getCode().equalsIgnoreCase(localadminCode)).collect(Collectors.toList());

					identity.getZone().get(j).setValue(localadminResult.get(0).getName());

					// get local admin authority details

					List<LocationDto> postalCodeDetails = masterSync.findProvianceByHierarchyCode(
							localadminResult.get(0).getCode(), identity.getZone().get(j).getLanguage());
					/*
					 * String postalCodeCode =
					 * identity.getLocalAdministrativeAuthority().get(j).getValue();
					 * 
					 * List<LocationDto> postalCodeResult = postalCodeDetails.stream() .filter(o ->
					 * o.getCode().equalsIgnoreCase(postalCodeCode)).collect(Collectors.toList());
					 */

					identity.setPostalCode(postalCodeDetails.get(0).getCode());

				}
				documents = setDocumentDetailsDTO(identity, documentFile);
				registrationDTO.getDemographicDTO().setApplicantDocumentDTO(setApplicantDocumentDTO());
				registrationDTO.getDemographicDTO().getApplicantDocumentDTO().setDocuments(documents);
				registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);

				registrationDTO.setRegistrationMetaDataDTO(preRegistrationDTO.getRegistrationMetaDataDTO());
				registrationDTO.getRegistrationMetaDataDTO().setCenterId(centerID);
				registrationDTO.getRegistrationMetaDataDTO().setMachineId(stationID);
				registrationDTO.getRegistrationMetaDataDTO().setConsentOfApplicant("YES");

			}

			if (packetType.equalsIgnoreCase("childPreidWithDocs")
					|| packetType.equalsIgnoreCase("PrIdOfChildWithoutDocs")
					|| packetType.equalsIgnoreCase("PrIdOfChildInvalidParentUIN")
					|| packetType.equalsIgnoreCase("PrIdOfChildValidParentUIN")
					|| packetType.equalsIgnoreCase("PrIdOfChildValidParentRID")
					|| packetType.equalsIgnoreCase("PrIdOfChildInvalidRID")) {
				registrationDTO.getSelectionListDTO().setUinId(uin);
				registrationDTO.getRegistrationMetaDataDTO().setUin(uin);
			}

			registrationDTO.getOsiDataDTO().setOperatorID(userID);
			registrationDTO.getOsiDataDTO().setOperatorAuthenticatedByPassword(true);
			returnValues.put("RANDOMID", registrationDTO.getRegistrationId());
			ResponseDTO response = packetHandlerService.handle(registrationDTO);

			if (!(response.getSuccessResponseDTO().getMessage() == null)) {

				returnValues.put("SUCCESSRESPONSE", response.getSuccessResponseDTO().getMessage());
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						response.getSuccessResponseDTO().getMessage());
				if ((response.getSuccessResponseDTO().getMessage().contains(ConstantValues.SUCCESS))
						&& (((String) ApplicationContext.map().get(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG))
								.equalsIgnoreCase(RegistrationConstants.DISABLE))) {
					registrationApprovalService.updateRegistration(registrationDTO.getRegistrationId(),
							RegistrationConstants.EMPTY, RegistrationClientStatusCode.APPROVED.getCode());

				}
			} else {

			}

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValues;
	}

	/**
	 * To get PRE REG IDs from JSON
	 * 
	 * @param registration
	 * @return
	 */
	public HashMap<String, String> getPreRegIDs() {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getPreRegIDs - to get PRE REG IDs");
		HashMap<String, String> response = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			response = mapper.readValue(
					new File(MosipTestRunner.getGlobalResourcePath()+"/"+ConstantValues.PRE_REG_PATH),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		} catch (JsonParseException jsonParseException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(jsonParseException));
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(fileNotFoundException));
		} catch (JsonMappingException jsonMappingException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(jsonMappingException));
		} catch (IOException ioException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(ioException));
		}
		
		return response;
	}

	/**
	 * This method will create registration DTO object
	 */
	public RegistrationDTO createRegistrationDTOObject(String registrationCategory, String centerID, String stationID) {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "createRegistrationDTOObject");
		try {
			// Create objects for Biometric DTOS
			BiometricDTO biometricDTO = new BiometricDTO();
			biometricDTO.setApplicantBiometricDTO(createBiometricInfoDTO());
			biometricDTO.setIntroducerBiometricDTO(createBiometricInfoDTO());
			biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
			biometricDTO.setSupervisorBiometricDTO(createBiometricInfoDTO());
			registrationDTO.setBiometricDTO(biometricDTO);

			// Create object for Demographic DTOS
			DemographicDTO demographicDTO = new DemographicDTO();
			ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
			applicantDocumentDTO.setDocuments(new HashMap<>());

			demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
			DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
			Identity identity = new Identity();
			demographicInfoDTO.setIdentity(identity);
			demographicDTO.setDemographicInfoDTO(demographicInfoDTO);

			applicantDocumentDTO.setDocuments(new HashMap<>());

			registrationDTO.setDemographicDTO(demographicDTO);

			// Create object for OSIData DTO
			registrationDTO.setOsiDataDTO(new OSIDataDTO());

			// Create object for RegistrationMetaData DTO
			RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
			registrationMetaDataDTO.setRegistrationCategory(registrationCategory);

			RegistrationCenterDetailDTO registrationCenter = SessionContext.userContext()
					.getRegistrationCenterDetailDTO();

			if (RegistrationConstants.ENABLE.equalsIgnoreCase(
					(String) ApplicationContext.map().get(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG))) {
				registrationMetaDataDTO
						.setGeoLatitudeLoc(Double.parseDouble(registrationCenter.getRegistrationCenterLatitude()));
				registrationMetaDataDTO
						.setGeoLongitudeLoc(Double.parseDouble(registrationCenter.getRegistrationCenterLongitude()));
			}

			Map<String, Object> applicationContextMap = ApplicationContext.map();

			registrationMetaDataDTO
					.setCenterId((String) applicationContextMap.get(RegistrationConstants.USER_CENTER_ID));
			registrationMetaDataDTO
					.setMachineId((String) applicationContextMap.get(RegistrationConstants.USER_STATION_ID));
			registrationMetaDataDTO
					.setDeviceId((String) applicationContextMap.get(RegistrationConstants.DONGLE_SERIAL_NUMBER));

			registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);

			// Set RID
			String registrationID = ridGeneratorImpl.generateId(centerID, stationID);

			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					"Registration Started for RID  : [ " + registrationID + " ] ");

			registrationDTO.setRegistrationId(registrationID);
			// Put the RegistrationDTO object to SessionContext Map

			SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return registrationDTO;
	}

	/**
	 * This method will create the biometrics info DTO
	 */
	public BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "createBiometricInfoDTO");
		try {
			biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
			biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
			biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
			biometricInfoDTO.setFace(new FaceDetailsDTO());
			biometricInfoDTO.setExceptionFace(new FaceDetailsDTO());
		} catch (NullPointerException nullPointerException) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(nullPointerException));
		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
		return biometricInfoDTO;
	}

	/**
	 * This method will create the RegistrationDTO with PRE REG Details
	 */

	public RegistrationDTO getPreRegistrationDetails(String preRegId) {
		LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, "getPreRegistrationDetails");
		RegistrationDTO registrationDTO = new RegistrationDTO();
		try {
			ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegId);

			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();

			if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null
					&& successResponseDTO.getOtherAttributes().containsKey(RegistrationConstants.REGISTRATION_DTO)) {
				SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA,
						successResponseDTO.getOtherAttributes().get(RegistrationConstants.REGISTRATION_DTO));
				registrationDTO = (RegistrationDTO) successResponseDTO.getOtherAttributes()
						.get(RegistrationConstants.REGISTRATION_DTO);
				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						"SUCCESS MESSAGE - " + successResponseDTO.getMessage());
			} else if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {

				LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID,
						RegistrationConstants.ERROR + errorResponseDTOList.get(0).getMessage());
			}

		} catch (Exception exception) {
			LOGGER.info("CommonUtil - ", APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}

		return registrationDTO;

	}

	public void recursiveDelete(File file) {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				recursiveDelete(f);
			}
		}
		file.delete();
		LOGGER.info(this.getClass().getName(), ConstantValues.MODULE_NAME, ConstantValues.MODULE_ID,
				"Deleted file/folder: " + file.getAbsolutePath());
	}

	public void copyFolder(File sourceFolder, File destinationFolder) {
		if (sourceFolder.isDirectory()) {
			if (!destinationFolder.exists()) {
				destinationFolder.mkdir();
			}
			String files[] = sourceFolder.list();
			for (String file : files) {
				File srcFile = new File(sourceFolder, file);
				File destFile = new File(destinationFolder, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			try {
				Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOGGER.error(this.getClass().getName(), ConstantValues.MODULE_NAME, ConstantValues.MODULE_ID,
						e.getMessage());
			}
		}
	}
	
	public static String getResourcePath() {
		return MosipTestRunner.getGlobalResourcePath();
	}

}
