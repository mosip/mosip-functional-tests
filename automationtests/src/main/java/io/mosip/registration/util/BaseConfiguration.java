package io.mosip.registration.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.packetmanager.dto.BiometricsDto;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.RegistrationCenterUserRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.service.sync.impl.PublicKeySyncImpl;

@ContextConfiguration(classes = { AppConfig.class, DaoConfig.class })
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
		"io.mosip.kernel.idobjectvalidator.impl.IdObjectCompositeValidator",
		"io.mosip.kernel.idobjectvalidator.impl.IdObjectMasterDataValidator",
		"io.mosip.kernel.packetmanager.impl.PacketDecryptorImpl","io.mosip.kernel.cbeffutil.CbeffImpl",
		 "io.mosip.kernel.packetmanager.util.IdSchemaUtils"}), basePackages = {
				"io.mosip.registration", "io.mosip.kernel.core", "io.mosip.kernel.keygenerator",
				"io.mosip.kernel.idvalidator", "io.mosip.kernel.ridgenerator","io.mosip.kernel.qrcode",
				"io.mosip.kernel.core.signatureutil", "io.mosip.kernel.crypto", "io.mosip.kernel.jsonvalidator",
				"io.mosip.kernel.idgenerator", "io.mosip.kernel.virusscanner", "io.mosip.kernel.transliteration",
				"io.mosip.kernel.applicanttype", "io.mosip.kernel.cbeffutil", "io.mosip.kernel.core.pdfgenerator.spi",
				"io.mosip.kernel.pdfgenerator.itext.impl", "io.mosip.kernel.cryptosignature",
				"io.mosip.kernel.core.signatureutil", "io.mosip.kernel.idobjectvalidator.impl",
				"io.mosip.kernel.packetmanager.impl", "io.mosip.kernel.packetmanager.util",
				"io.mosip.kernel.biosdk.provider.impl", "io.mosip.kernel.biosdk.provider.factory"})
public class BaseConfiguration extends AbstractTestNGSpringContextTests {

	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	PublicKeySyncImpl publicKeySyncImpl;
	@Autowired
	MasterSyncService masterSyncService;
	@Autowired
	UserDetailService userDetailService;
	@Autowired
	UserSaltDetailsService userSaltDetailsService;
	@Autowired
	PolicySyncService policySyncService;
	@Autowired
	MachineMasterRepository machineMasterRepository;
	@Autowired
	CenterMachineRepository centerMachineRepository;
	@Autowired
	RegistrationCenterUserRepository centeruserRepo;

	/** The policy sync DAO. */
	@Autowired
	private PolicySyncDAO policySyncDAO;
	@Autowired
	UserMachineMappingRepository userMachineMappingRepository;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	HashMap<String, String> data = new HashMap<String, String>();
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseConfiguration.class);

	// creating the instance of mosip ApplicationContext
	static {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.loadResourceBundle();
		
	}

	public void baseSetUp() { 
		try {

			// Fetching the Global param values from the database
			ApplicationContext.setApplicationMap(globalParamService.getGlobalParams());
			ApplicationContext.map().put("mosip.registration.client.id", "mosip-reg-client");
			ApplicationContext.map().put("mosip.registration.secret.Key", "abc123");
			// Set spring Application Context to SessionContext
			SessionContext.setApplicationContext(applicationContext);
			
			// Sync
			List<String> loginSync=loginService.initialSync();
			if(loginSync.get(0).toLowerCase().equals("success")){
				ResponseDTO policysyncResponse = policySyncService.fetchPolicy();
				if(policysyncResponse.getSuccessResponseDTO()!=null) {
					System.out.println(policysyncResponse.getSuccessResponseDTO().getMessage());
				} else
				{
					for(ErrorResponseDTO errorResponse:policysyncResponse.getErrorResponseDTOs()) {
						System.out.println(errorResponse.getCode());
						System.out.println(errorResponse.getMessage());
					}
				}
			}
			
			// Get User details from User Detail table
			System.out.println(System.getProperty("userID"));
			UserDTO userDTO = loginService.getUserDetail(System.getProperty("userID"));
			LoginUserDTO loginUserDTO = new LoginUserDTO();
			loginUserDTO.setUserId(System.getProperty("userID"));
			loginUserDTO.setPassword("mosip");
			// SetUp to create SessionContext
			AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
			authenticationValidatorDTO.setUserId(System.getProperty("userID"));
			authenticationValidatorDTO.setPassword("mosip");
			// Set User details UserDTO to ApllicationContext
			ApplicationContext.map().put(RegistrationConstants.USER_DTO, loginUserDTO);
			// Create SessionContext
			Boolean sessionContext_Status = SessionContext.create(userDTO, RegistrationConstants.PWORD, true, false,
					authenticationValidatorDTO);
			System.out.println("sessionContext_Status -----: "+sessionContext_Status);
			if (sessionContext_Status) {
				// Set CenterID
				centerID = userOBservice.getMachineCenterId().get(ConstantValues.CENTERIDLBL);
				// Set StationID
				stationID = userOBservice.getMachineCenterId().get(ConstantValues.STATIONIDLBL);
				// Set values to applicationContext
				ApplicationContext.map().put(ConstantValues.CENTERIDLBL, centerID);
				ApplicationContext.map().put(ConstantValues.STATIONIDLBL, stationID);
				ApplicationContext.map().put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, ConstantValues.NO);
				ApplicationContext.map().put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS, "120");
				// SetUp to create Packet
				RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
				registrationCenter.setRegistrationCenterId(centerID);
				SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
				SessionContext.userContext().setUserId(System.getProperty("userID"));

				// Checkin if User Onboarded or not
				if (SessionContext.map().get(RegistrationConstants.ONBOARD_USER)!=null) {
					// Onboard user
					BiometricDTO bioData = null;
					String biometricPath= CommonUtil.getResourcePath() +"/UserOnboardDetail/Resident_BiometricData.json";
					String bioPath = "UserOnboardDetail/Resident_BiometricData.json";
					ClassLoader classLoader = getClass().getClassLoader();
					LOGGER.info("USERONBOARD SERVICE TEST - ", "AUTOMATION", "REG", "Path: " + bioPath);
					bioData = commonUtil.getBiotestData(biometricPath);
					List<BiometricsDto> listOfBiometrics=new ArrayList<BiometricsDto>();
					ResponseDTO actualresponse = userOBservice.validateWithIDAuthAndSave(listOfBiometrics);
					commonUtil.verifyAssertionResponse("USER_ONBOARD_SUCCESS", actualresponse);
					System.out.println();
				} else {
					// User already Onboarded
					LOGGER.info("BASE OCNFIGURATION", "AUTOMATION", "REG", "User already Onboarded");
				}

			} else {
				Assert.assertTrue(sessionContext_Status);
			}
		} catch (IOException ioException) {
			LOGGER.info("BASE OCNFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(ioException));

		} catch (ParseException parseException) {
			LOGGER.info("BASE OCNFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(parseException));

		}
		catch (Exception exception) {
			LOGGER.info("BASE OCNFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(exception));

		}

	}

}
