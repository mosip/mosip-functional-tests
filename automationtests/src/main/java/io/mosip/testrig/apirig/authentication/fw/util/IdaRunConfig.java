package io.mosip.testrig.apirig.authentication.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.dto.ErrorsDto;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;
import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

/**
 * The class hold all the run config path available in runconfiguration file
 * 
 * @author Vignesh
 *
 */
public class IdaRunConfig extends RunConfig{
	
	private  Logger logger = Logger.getLogger(IdaRunConfig.class);
	private  String endPointUrl;
	private  String ekycPath;
	private  String encryptUtilBaseUrl;
	private  String encryptionPath;
	private  String encodePath;
	private  String decodePath;
	private  String scenarioPath;
	private  String srcPath;
	private  String authPath;
	private  String internalAuthPath;
	private  String otpPath;
	private  String userDirectory;
	private  String testDataPath;
	private  String idRepoEndPointUrl;
	private  String testType;
	private  String PinPath;
	private  String generateUINPath;
	private  String idRepoRetrieveDataPath;
	private  String idRepoCreateUINRecordPath;
	private  String storeUINDataPath;
	private  String encodeFilePath;
	private  String decodeFilePath;
	private  String vidGenPath;
	private  String testDataFolderName;
	private  String authVersion;
	private  String clientidsecretkey;
	private String moduleFolderName;
	private String idRepoCreateVIDRecordPath;
	private String idRepoUpdateVIDStatusPath;
	private String idRepoVersion;
	private String decryptPath;
	private String uinIdentityMapper;
	private String internalEncryptionPath;
	private String validateSignaturePath;
	private String encryptionPort;
	private String idRepoRegenerateVID;
	private String idRepoRetrieveUINByVIDPath;
	private String idaInternalOtpPath;
	private String idaInternalAuthTransactionWithUIN;
	private String idaInternalAuthTransactionWithVID;
	private String idaInternalUpdateAuthTypePath;
	private String idaInternalRetrieveAuthTypeStatusPathForUIN;
	private String idaInternalRetrieveAuthTypeStatusPathForVID;
	private String cryptomanagerEncrypt;
	private String splitEncryptedData;
	private String authManagerUserIdPwd;
	private String uinSalt;
	private String partnerCodeSalt;
	private String authPartnerID;
	private int tokenIDLength;
	
	@Override
	public String getUinSalt() {
		return uinSalt;
	}
	public void setUinSalt(String uinSalt) {
		this.uinSalt = uinSalt;
	}
	@Override
	public String getPartnerCodeSalt() {
		return partnerCodeSalt;
	}
	public void setPartnerCodeSalt(String partnerCodeSalt) {
		this.partnerCodeSalt = partnerCodeSalt;
	}
	@Override
	public String getAuthPartnerID() {
		return authPartnerID;
	}
	public void setAuthPartnerID(String authPartnerID) {
		this.authPartnerID = authPartnerID;
	}
	@Override
	public int getTokenIDLength() {
		return tokenIDLength;
	}
	public void setTokenIDLength(int tokenIDLength) {
		this.tokenIDLength = tokenIDLength;
	}
	
	public String getAuthManagerUserIdPwd() {
		return authManagerUserIdPwd;
	}
	public void setAuthManagerUserIdPwd(String authManagerUserIdPwd) {
		this.authManagerUserIdPwd = authManagerUserIdPwd;
	}
	public String getSplitEncryptedData() {
		return splitEncryptedData;
	}
	public void setSplitEncryptedData(String splitEncryptedData) {
		this.splitEncryptedData = splitEncryptedData;
	}
	public String getCryptomanagerEncrypt() {
		return cryptomanagerEncrypt;
	}
	public void setCryptomanagerEncrypt(String cryptomanagerEncrypt) {
		this.cryptomanagerEncrypt = cryptomanagerEncrypt;
	}
	public String getIdaInternalRetrieveAuthTypeStatusPathForUIN() {
		return idaInternalRetrieveAuthTypeStatusPathForUIN;
	}
	public void setIdaInternalRetrieveAuthTypeStatusPathForUIN(String idaInternalRetrieveAuthTypeStatusPathForUIN) {
		this.idaInternalRetrieveAuthTypeStatusPathForUIN = idaInternalRetrieveAuthTypeStatusPathForUIN;
	}
	public String getIdaInternalRetrieveAuthTypeStatusPathForVID() {
		return idaInternalRetrieveAuthTypeStatusPathForVID;
	}
	public void setIdaInternalRetrieveAuthTypeStatusPathForVID(String idaInternalRetrieveAuthTypeStatusPathForVID) {
		this.idaInternalRetrieveAuthTypeStatusPathForVID = idaInternalRetrieveAuthTypeStatusPathForVID;
	}
	public String getIdaInternalUpdateAuthTypePath() {
		return idaInternalUpdateAuthTypePath;
	}
	public void setIdaInternalUpdateAuthTypePath(String idaInternalUpdateAuthTypePath) {
		this.idaInternalUpdateAuthTypePath = idaInternalUpdateAuthTypePath;
	}
	public String getIdaInternalAuthTransactionWithVID() {
		return idaInternalAuthTransactionWithVID;
	}
	public void setIdaInternalAuthTransactionWithVID(String idaInternalAuthTransactionWithVID) {
		this.idaInternalAuthTransactionWithVID = idaInternalAuthTransactionWithVID;
	}
	public String getIdaInternalAuthTransactionWithUIN() {
		return idaInternalAuthTransactionWithUIN;
	}
	public void setIdaInternalAuthTransactionWithUIN(String idaInternalAuthTransactionWithUIN) {
		this.idaInternalAuthTransactionWithUIN = idaInternalAuthTransactionWithUIN;
	}
	public String getEncryptionPort() {
		return encryptionPort;
	}
	public void setEncryptionPort(String encryptionPort) {
		this.encryptionPort = encryptionPort;
	}
	public String getValidateSignaturePath() {
		return validateSignaturePath;
	}
	public void setValidateSignaturePath(String validateSignaturePath) {
		this.validateSignaturePath = validateSignaturePath;
	}
	public String getInternalEncryptionPath() {
		return internalEncryptionPath;
	}
	public void setInternalEncryptionPath(String internalEncryptionPath) {
		this.internalEncryptionPath = internalEncryptionPath;
	}
	public String getUinIdentityMapper() {
		return uinIdentityMapper;
	}
	public void setUinIdentityMapper(String uinIdentityMapper) {
		this.uinIdentityMapper = uinIdentityMapper;
	}
	public String getDecryptPath() {
		return decryptPath;
	}
	public void setDecryptPath(String decryptPath) {
		this.decryptPath = decryptPath;
	}
	public String getIdRepoVersion() {
		return idRepoVersion;
	}
	public void setIdRepoVersion(String idRepoVersion) {
		this.idRepoVersion = idRepoVersion;
	}
	/**
	 * The method get endpoint url for IDA
	 * 
	 * @return string
	 */
	public String getEndPointUrl() {
		return endPointUrl;
	}
	/**
	 * The method set endpoint url
	 * 
	 * @param endPointUrl
	 */
	public void setEndPointUrl(String endPointUrl) {
		this.endPointUrl = endPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	/**
	 * The method get ekyc url path
	 * 
	 * @returnstring 
	 */
	public  String getEkycPath() {
		return ekycPath;
	}
	/**
	 * The method set ekyc url path
	 * 
	 * @param ekycPath
	 */
	public  void setEkycPath(String ekycPath) {
		this.ekycPath = ekycPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public  String getEncryptUtilBaseUrl() {
		return encryptUtilBaseUrl;
	}

	/**
	 * The method will set encryption endpoint path
	 * 
	 * @param encryptUtilBaseUrl
	 */
	public  void setEncryptUtilBaseUrl(String encryptUtilBaseUrl) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String actualUrl = encryptUtilBaseUrl.replace("$hostname$", inetAddress.getHostName().toLowerCase());
			actualUrl=actualUrl.replace("$port$", RunConfigUtil.objRunConfig.getEncryptionPort());
			this.encryptUtilBaseUrl = actualUrl;
		} catch (Exception e) {
			logger.error("Execption in RunConfig " + e.getMessage());
		}
	}
	/**
	 * The method will get encryption path
	 * 
	 * @return string
	 */
	public  String getEncryptionPath() {
		return encryptionPath;
	}
	/**
	 * The method set encryption path
	 * 
	 * @param encryptionPath
	 */
	public  void setEncryptionPath(String encrypPath) {
		this.encryptionPath = encrypPath;
	}
	/**
	 * The method get encode path
	 * 
	 * @return string
	 */
	public  String getEncodePath() {
		return encodePath;
	}
	/**
	 * The method set encode path
	 * 
	 * @param encodePath
	 */
	public  void setEncodePath(String encPath) {
		this.encodePath = encPath;
	}
	/**
	 * The method get decode path 
	 * 
	 * @return string
	 */
	public  String getDecodePath() {
		return decodePath;
	}
	/**
	 * The method set decode path
	 * 
	 * @param decodePath
	 */
	public  void setDecodePath(String decodePath) {
		this.decodePath = decodePath;
	}
	/**
	 * The method get scenario path of current test execution
	 * 
	 * @return string
	 */
	public  String getScenarioPath() {
		return scenarioPath;
	}
	/**
	 * The method set scenatio path of current test execution
	 * 
	 * @param scenarioPath
	 */
	public  void setScenarioPath(String scenarioPath) {
		this.scenarioPath = scenarioPath;
	}
	/**
	 * The method get source path from config file
	 * 
	 * @return string
	 */
	public  String getSrcPath() {
		return srcPath;
	}
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public  void setSrcPath(String srcPath) {
		this.srcPath = MosipTestRunner.getGlobalResourcePath();
	}
	/**
	 * The method get auth path
	 * 
	 * @return String
	 */
	public  String getAuthPath() {
		return authPath;
	}
	/**
	 * The method set auth path
	 * 
	 * @param authPath
	 */
	public  void setAuthPath(String authPath) {
		this.authPath = authPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get internal auth path
	 * 
	 * @return string
	 */
	public  String getInternalAuthPath() {
		return internalAuthPath;
	}
	/**
	 * The method set internal auth path
	 * 
	 * @param internalAuthPath
	 */
	public  void setInternalAuthPath(String internalAuthPath) {
		this.internalAuthPath = internalAuthPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get otp path
	 * 
	 * @return string
	 */
	public  String getOtpPath() {
		return otpPath;
	}
	/**
	 * The method set otp path
	 * 
	 * @param otpPath
	 */
	public  void setOtpPath(String otpPath) {
		this.otpPath = otpPath.replace("$authVersion$", getAuthVersion());
	}	
	/**
	 * The method get current test data path
	 * 
	 * @return string
	 */
	public  String getTestDataPath() {
		return testDataPath;
	}
	/**
	 * The method set current test data path
	 * 
	 * @param testDataPath
	 */
	public  void setTestDataPath(String testDataPath) {
		this.testDataPath = testDataPath;
	}
	/**
	 * The method get user directory of project
	 * 
	 * @return string
	 */
	public  String getUserDirectory() {
		return userDirectory;
	}
	/**
	 * The method set user directory
	 */
	public  void setUserDirectory() {
		Path currentDir = Paths.get(".");
		String path =currentDir.toFile().getAbsolutePath();
		path=path.substring(0, path.length()-1);
		this.userDirectory = path;
	}
	/**
	 * The method get idrepo endpoint url
	 * 
	 * @return string
	 */
	public  String getIdRepoEndPointUrl() {
		return idRepoEndPointUrl;
	}
	/**
	 * The method set idrepo endpoint url
	 * 
	 * @param idRepoEndPointUrl
	 */
	public  void setIdRepoEndPointUrl(String idRepoEndPointUrl) {
		this.idRepoEndPointUrl = idRepoEndPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	
	/**
	 * The method set configuration 
	 * 
	 * @param testDataPath
	 * @param testDataFileName
	 * @param testType
	 */
	public  void setConfig(String testDataPath,String testDataFileName,String testType) {
		String env = System.getProperty("env.user");
		setUinSalt(AuthTestsUtil.getPropertyValue("uinSalt"+env));
		setPartnerCodeSalt(AuthTestsUtil.getPropertyValue("partnerCodeSalt"+env));
		setAuthPartnerID(AuthTestsUtil.getPropertyValue("authPartnerID"+env));
		setTokenIDLength(Integer.parseInt(AuthTestsUtil.getPropertyValue("tokenIDLength")));
		setIdRepoVersion(AuthTestsUtil.getPropertyValue("idrepoVersion"));
		setAuthVersion(AuthTestsUtil.getPropertyValue("authVersion"));
		setEndPointUrl(AuthTestsUtil.getPropertyValue("endPointUrl"));
		setEkycPath(AuthTestsUtil.getPropertyValue("ekycPath"));
		setSrcPath(AuthTestsUtil.getPropertyValue("srcPath"));
		setAuthPath(AuthTestsUtil.getPropertyValue("authPath"));
		setInternalAuthPath(AuthTestsUtil.getPropertyValue("internalAuthPath"));
		setOtpPath(AuthTestsUtil.getPropertyValue("otpPath"));
		setEncryptionPort(AuthTestsUtil.getPropertyValue(RunConfigUtil.getRunEvironment()+".encryptionPort"));
		setEncryptUtilBaseUrl(AuthTestsUtil.getPropertyValue("encryptUtilBaseUrl"));
		setEncryptionPath(AuthTestsUtil.getPropertyValue("encryptionPath"));
		setEncodePath(AuthTestsUtil.getPropertyValue(GlobalConstants.ENCODEPATH));
		setDecodePath(AuthTestsUtil.getPropertyValue("decodePath"));
		setDecryptPath(AuthTestsUtil.getPropertyValue(GlobalConstants.DECRYPTPATH));
		setInternalEncryptionPath(AuthTestsUtil.getPropertyValue("internalEncryptionPath"));
		setValidateSignaturePath(AuthTestsUtil.getPropertyValue("validateSignaturePath"));
		setUserDirectory();
		setTestDataPath(testDataPath);	
		setIdRepoEndPointUrl(AuthTestsUtil.getPropertyValue("idRepoEndPointUrl"));
		setIdRepoRetrieveDataPath(AuthTestsUtil.getPropertyValue("idRepoRetrieveDataPath"));
		setIdRepoCreateVIDRecordPath(AuthTestsUtil.getPropertyValue("idRepoCreateVIDRecordPath"));
		setIdRepoUpdateVIDStatusPath(AuthTestsUtil.getPropertyValue("idRepoUpdateVIDStatusPath"));
		setIdRepoRegenerateVID(AuthTestsUtil.getPropertyValue("idRepoRegenerateVidPath"));
		setIdRepoRetrieveUINByVIDPath(AuthTestsUtil.getPropertyValue("idRepoRetrieveUinByVidPath"));
		File testDataFilePath = new File(RunConfigUtil.getResourcePath()
		+ testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath,testDataPath);
		setTestType(RunConfigUtil.getTestLevel());
		setGenerateUINPath(AuthTestsUtil.getPropertyValue("generateUINPath"));
		setPinPath(AuthTestsUtil.getPropertyValue("staticPinPath"));
		setIdRepoCreateUINRecordPath(AuthTestsUtil.getPropertyValue("idRepoCreateUINRecordPath"));
		setStoreUINDataPath(AuthTestsUtil.getPropertyValue("storeUINDataPath"));
		setEncodeFilePath(AuthTestsUtil.getPropertyValue("encodeFilePath"));
		setDecodeFilePath(AuthTestsUtil.getPropertyValue("decodeFilePath"));
		setClientidsecretkey(AuthTestsUtil.getPropertyValue("clientidsecretkey"));
		//loadingConfigFile
		loadErrorsData(getErrorsConfigPath());
		setUinIdentityMapper(AuthTestsUtil.getPropertyValue("uinIdentityMapper"));
		setIdaInternalOtpPath(AuthTestsUtil.getPropertyValue("internalOtpPath"));
		setIdaInternalAuthTransactionWithUIN(AuthTestsUtil.getPropertyValue("internalAuthTransactionWithUIN"));
		setIdaInternalAuthTransactionWithVID(AuthTestsUtil.getPropertyValue("internalAuthTransactionWithVID"));
		setIdaInternalUpdateAuthTypePath(AuthTestsUtil.getPropertyValue("internalUpdateAuthTypePath"));
		setIdaInternalRetrieveAuthTypeStatusPathForUIN(AuthTestsUtil.getPropertyValue("internalRetrieveAuthTypeStatusPathForUIN"));
		setIdaInternalRetrieveAuthTypeStatusPathForVID(AuthTestsUtil.getPropertyValue("internalRetrieveAuthTypeStatusPathForVID"));
		setCryptomanagerEncrypt(AuthTestsUtil.getPropertyValue("cryptomanagerEncrypt"));
		setSplitEncryptedData(AuthTestsUtil.getPropertyValue("splitEncryptedData"));
		setAuthManagerUserIdPwd(AuthTestsUtil.getPropertyValue("useridPwd"));
	}	
	
	/**
	 * The method set file path from test data file name
	 * 
	 * @param filePath
	 * @param testDataPath
	 */
	private  void setFilePathFromTestdataFileName(File filePath, String testDataPath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "/" + folderList[i];
		}
		String testDataFolderName = "";
		String moduleFolderName="";
		if (testDataPath.contains("\\")) {
			String[] list = testDataPath.split(Pattern.quote("\\\\"));
			testDataFolderName = list[1];
		} else if (testDataPath.contains("/")) {
			String[] list = testDataPath.split(Pattern.quote("/"));
			moduleFolderName=list[0];
			testDataFolderName = list[1];
		}
		setTestDataFolderName(testDataFolderName);
		setModuleFolderName(moduleFolderName);
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
	}
	/**
	 * The method get test type of current execution
	 * 
	 * @return string
	 */
	public  String getTestType() {
		return testType;
	}
	/**
	 * The method set test type of current execution
	 * 
	 * @param testType
	 */
	public  void setTestType(String testType) {
		this.testType = testType;
	}	
	/**
	 * The method get  pin path
	 * 
	 * @return string
	 */
	public  String getPinPath() {
		return PinPath;
	}
	/**
	 * The method set  pin path
	 * 
	 * @param PinPath
	 */
	public  void setPinPath(String PinPath) {
		this.PinPath = PinPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get UIN generation path
	 * 
	 * @return string
	 */
	public  String getGenerateUINPath() {
		return generateUINPath;
	}
	/**
	 * The method set UIN generation path
	 * 
	 * @param generateUINPath
	 */
	public  void setGenerateUINPath(String generateUINPath) {
		this.generateUINPath = generateUINPath;
	}	
	
	/**
	 * The method get retrieve idrepo path
	 * 
	 * @return string
	 */
	public  String getIdRepoRetrieveDataPath() {
		return idRepoRetrieveDataPath;
	}
	/**
	 * The method set retrieve idrepo path
	 * 
	 * @param idRepoRetrieveDataPath
	 */
	public  void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath) {
		this.idRepoRetrieveDataPath = idRepoRetrieveDataPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @return string
	 */
	public  String getIdRepoCreateUINRecordPath() {
		return idRepoCreateUINRecordPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @param idRepoCreateUINRecordPath
	 */
	public  void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath) {
		this.idRepoCreateUINRecordPath = idRepoCreateUINRecordPath;
	}	
	/**
	 * The method get store UIN Data path
	 * 
	 * @return string
	 */
	public  String getStoreUINDataPath() {
		return storeUINDataPath;
	}
	/**
	 * The method set store UIN Data path
	 * 
	 * @param storeUINDataPath
	 */
	public  void setStoreUINDataPath(String storeUINDataPath) {
		this.storeUINDataPath = storeUINDataPath;
	}	
	
	/**
	 * The method get encode file path
	 * 
	 * @return string
	 */
	public  String getEncodeFilePath() {
		return encodeFilePath;
	}
	/**
	 * The method will set encode file path
	 * 
	 * @param encodeFile
	 */
	public  void setEncodeFilePath(String encodeFile) {
		this.encodeFilePath = encodeFile;
	}	
	/**
	 * The method get decode file path
	 * 
	 * @return string
	 */
	public  String getDecodeFilePath() {
		return decodeFilePath;
	}
	/**
	 * The method set decode file path
	 * 
	 * @param decodeFilePath
	 */
	public  void setDecodeFilePath(String decodeFilePath) {
		this.decodeFilePath = decodeFilePath;
	}	
	/**
	 * The method set VID generation path
	 * 
	 * @return
	 */
	public  String getVidGenPath() {
		return vidGenPath;
	}
	/**
	 * The method set VID generation path
	 * 
	 * @param vidGenPath
	 */
	public  void setVidGenPath(String vidGenPath) {
		this.vidGenPath = vidGenPath.replace("$authVersion$", getAuthVersion());
	}	
	/**
	 * The method get test data folder name of current test execution
	 * 
	 * @return string
	 */
	public  String getTestDataFolderName() {
		return testDataFolderName;
	}
	/**
	 * The method set test data folder name of current execution
	 * 
	 * @param testDataFolderName
	 */
	public  void setTestDataFolderName(String testDataFolderName) {
		this.testDataFolderName = testDataFolderName;
	}	
	/**
	 * The method get current auth version 
	 * 
	 * @return string
	 */
	public  String getAuthVersion() {
		return authVersion;
	}
	/**
	 * The method set current auth version from config file
	 * 
	 * @param authVersion
	 */
	public  void setAuthVersion(String authVersion) {
		this.authVersion = authVersion;
	}
	
	/**
	 * The method get error config path
	 * 
	 * @return string
	 */
	public  String getErrorsConfigPath() {
		return "ida/" + getTestDataFolderName() + "/RunConfig/errorCodeMsg.yml";
	}
	
	/**
	 * The method load yml error test data
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private  void loadErrorsData(String path) {
		FileInputStream inputStream = null;
		try {
			Yaml yaml = new Yaml();
			inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
			ErrorsDto.setErrors((Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
	}
	
	public  String getClientidsecretkey() {
		return clientidsecretkey;
	}
	public  void setClientidsecretkey(String cleintIdSecKey) {
		this.clientidsecretkey = cleintIdSecKey;
	}
	@Override
	public String getModuleFolderName() {
		return this.moduleFolderName;
	}
	@Override
	public void setModuleFolderName(String moduleFolderName) {
		this.moduleFolderName=moduleFolderName;
		
	}
	@Override
	public String getGenerateVIDPath() {
		return null;
	}
	@Override
	public void setGenerateVIDPath(String generateVIDPath) {
		
	}
	@Override
	public String getIdRepoCreateVIDRecordPath() {
		return this.idRepoCreateVIDRecordPath;
	}
	@Override
	public void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath) {
		this.idRepoCreateVIDRecordPath=idRepoCreateVIDRecordPath;		
	}
	@Override
	public String getIdRepoUpdateVIDStatusPath() {
		return this.idRepoUpdateVIDStatusPath;
	}
	@Override
	public void setIdRepoUpdateVIDStatusPath(String idRepoUpdateVIDStatusPath) {
		this.idRepoUpdateVIDStatusPath=idRepoUpdateVIDStatusPath;		
	}
	@Override
	public String getIdRepoRetrieveIdentityByUin() {
		return null;
	}
	@Override
	public void setIdRepoRetrieveIdentityByUin(String idRepoRetrieveIdentityByUin) {
		
	}
	@Override
	public String getIdRepoRetrieveUINByVIDPath() {
		return this.idRepoRetrieveUINByVIDPath;
	}
	@Override
	public void setIdRepoRetrieveUINByVIDPath(String idRepoRetrieveUINByVIDPath) {
		this.idRepoRetrieveUINByVIDPath=idRepoRetrieveUINByVIDPath;	
	}
	@Override
	public String getIdRepoRetrieveIdentityByRid() {
		return null;
	}
	@Override
	public void setIdRepoRetrieveIdentityByRid(String idRepoRetrieveIdentityByRid) {
		
	}
	@Override
	public String getIdRepoRegenerateVID() {
		return this.idRepoRegenerateVID;
	}
	@Override
	public void setIdRepoRegenerateVID(String idRepoRegenerateVID) {
		this.idRepoRegenerateVID=idRepoRegenerateVID;		
	}
	@Override
	public String getAdminEndPointUrl() {
		return null;
	}
	@Override
	public void setAdminEndPointUrl(String adminEndPointUrl) {
		return;
	}
	@Override
	public String getAdminCreateRegCentrePath() {
		return null;
	}
	@Override
	public void setAdminCreateRegCentrePath(String adminCreateRegCentrePath) {
		return;
	}
	@Override
	public String getAdminCreateRegistrationCentrePath() {
		return null;
	}
	@Override
	public void setAdminCreateRegistrationCentrePath(String adminCreateRegistrationCentrePath) {
		return;
	}
	@Override
	public String getSearchMachinePath() {
		return null;
	}
	@Override
	public void setSearchMachinePath(String searchMachinePath) {
		return;
	}
	@Override
	public String getDeviceSearchPath() {
		return null;
	}
	@Override
	public void setDeviceSearchPath(String deviceSearchPath) {
		return;
	}
	@Override
	public String getDeviceFilterPath() {
		return null;
	}
	@Override
	public void setDeviceFilterPath(String deviceFilterPath) {
		return;
	}
	@Override
	public String getDeviceSpecSearchPath() {
		return null;
	}
	@Override
	public void setDeviceSpecSearchPath(String deviceSpecSearchPath) {
		return;
	}
	@Override
	public String getDeviceSpecFilterPath() {
		return null;
	}
	@Override
	public void setDeviceSpecFilterPath(String deviceSpecFilterPath) {
		return;
	}
	@Override
	public String getDeviceTypeSearchPath() {
		return null;
	}
	@Override
	public void setDeviceTypeSearchPath(String deviceTypeSearchPath) {
		return;
	}
	@Override
	public String getDeviceTypeFilterPath() {
		return null;
	}
	@Override
	public void setDeviceTypeFilterPath(String deviceTypeFilterPath) {
		return;
	}
	@Override
	public String getTitleSearchPath() {
		return null;
	}
	@Override
	public void setTitleSearchPath(String titleSearchPath) {
		return;
	}
	@Override
	public String getTitleFilterPath() {
		return null;
	}
	@Override
	public void setTitleFilterPath(String titleFilterPath) {
		return;
	}
	@Override
	public void setHolidaySearchPath(String holidaySearchPath) {
		return;
	}
	@Override
	public String getHolidaySearchPath() {
		return null;
	}
	@Override
	public String getSearchDocumentTypePath() {
		return null;
	}
	@Override
	public void setSearchDocumentTypePath(String searchDocumentTypePath) {
		return;
	}
	@Override
	public String getAdminMachineTypeSearchPath() {
		return null;
	}
	@Override
	public void setAdminMachineTypeSearchPath(String adminMachineTypeSearchPath) {
		return;
	}
	@Override
	public String getAdminSearchTemplateDetailsPath() {
		return null;
	}
	@Override
	public void setAdminSearchTemplateDetailsPath(String adminSearchTemplateDetailsPath) {
		return;
	}
	@Override
	public String getAdminIndividualTypesSearchPath() {
		return null;
	}
	@Override
	public void setAdminIndividualTypesSearchPath(String adminIndividualTypesSearchPath) {
		return;
	}
	@Override
	public String getAdminRegistrationCentreSearchPath() {
		return null;
	}
	@Override
	public void setAdminRegistrationCentreSearchPath(String adminRegistrationCentreSearchPath) {
		return;
	}
	@Override
	public String getSearchBlackListedWords() {
		
		return null;
	}
	@Override
	public void setSearchBlackListedWords(String searchBlackListedWordsPath) {
		return;
		
	}
	@Override
	public String getSearchDocCategories() {
		
		return null;
	}
	@Override
	public String getAdminUpdateRegistrationCentrePath() {
		
		return null;
	}
	@Override
	public void setAdminUpdateRegistrationCentrePath(String adminUpdateRegistrationCentrePath) {
		
		return;
	}
	@Override
	public String getFilterTempateDetailsPath() {
		
		return null;
	}
	@Override
	public String getFilterDocCategory() {
		
		return null;
	}
	@Override
	public String getAdminGenderSearchPath() {
		
		return null;
	}
	@Override
	public void setAdminGenderSearchPath(String adminGenderSearchPath) {
		
		return;
	}
	@Override
	public String getAdminGenderFilterPath() {
		
		return null;
	}
	@Override
	public void setAdminGenderFilterPath(String adminGenderFilterPath) {
		
		return;
	}
	@Override
	public String getAdminRegistrationCentreFilterPath() {
		
		return null;
	}
	@Override
	public void setAdminRegistrationCentreFilterPath(String adminRegistrationCentreFilterPath) {
		return;
		
	}
	@Override
	public String MapDocumentCategoryAndDocumentType() {
		
		return null;
	}
	@Override
	public void setFilterBlackListedWordsPath(String filterBlackListedWordsPath) {
		
		return;
	}
	@Override
	public String getFilterBlackListedWordsPath() {
		
		return null;
	}
	@Override
	public String getFilterDocCatTypMapping() {
		
		return null;
	}
	@Override
	public String getFilterDocumentTypePath() {
		
		return null;
	}
	@Override
	public void setFilterDocumentTypePath(String filterDocumentTypePath) {
		
		return;
	}
	@Override
	public String getFilterMachinesPath() {
		
		return null;
	}
	@Override
	public void setFilterMachinesPath(String filterMachinesPath) {
		
		return;
	}
	@Override
	public String getSearchMachineSpec() {
		
		return null;
	}
	@Override
	public String getFilterMachineSpec() {
		
		return null;
	}
	@Override
	public String getSearchValidDocumentPath() {
		
		return null;
	}
	@Override
	public String getIdaInternalOtpPath() {
		return this.idaInternalOtpPath;
	}
	@Override
	public void setIdaInternalOtpPath(String internalPath) {
		this.idaInternalOtpPath=internalPath;
	}
	@Override
	public String getIdRepoDeactivateVIDs() {
		
		return null;
	}
	@Override
	public void setIdRepoDeactivateVIDs(String idRepoDeactivateVIDs) {
		return;
		
	}
	@Override
	public String getIdRepoReactivateVIDs() {
		
		return null;
	}
	@Override
	public void setIdRepoReactivateVIDs(String idRepoReactivateVIDs) {
		
		return;
	}
	@Override
	public String getUnmapDocCategoryType() {
		
		return null;
	}
	@Override
	public String getSearchLocationDataPath() {
		
		return null;
	}
	@Override
	public void setSearchLocationDataPath(String searchLocationPath) {
		return;
		
	}
	@Override
	public String getFilterLocationPath() {
		
		return null;
	}
	@Override
	public void setFilterLocationPath(String filterLocationPath) {
		
		return;
	}
	@Override
	public String getCreateBlackListedWordsPath() {
		
		return null;
	}
	@Override
	public void setCreateBlackListedWordsPath(String createBlackListedWordsPath) {
		
		return;
	}
	@Override
	public String getCreateLocationDataPath() {
		
		return null;
	}
	@Override
	public void setCreateLocationDataPath(String createLocationDataPath) {
		
		return;
	}
	@Override
	public String getDecommisionRegCenterPath() {
		
		return null;
	}
	@Override
	public String getDecommisionDevicePath() {
		
		return null;
	}
	@Override
	public String getSetGetLeafZones() {
		
		return null;
	}
	@Override
	public String getSetGetZoneNameBasedOnUserIDAndLangCode() {
		
		return null;
	}
	@Override
	public String getSetZoneHierarchy() {
		
		return null;
	}
	@Override
	public String getDecommisionMachinePath() {
		
		return null;
	}
	@Override
	public String getUpdateLocationData() {
		
		return null;
	}
	@Override
	public String getUpdateBlackListedWordsPath() {
		
		return null;
	}
	@Override
	public String getDeviceValidatePath() {
		
		return null;
	}
	@Override
	public String getFilterIndividualTypePath() {
		
		return null;
	}
	@Override
	public String getFilterHolidaysPath() {
		
		return null;
	}
	@Override
	public String getResidentGenerateVID() {
		
		return null;
	}
	@Override
	public String getUserIdPwd() {
		
		return null;
	}
	@Override
	public void setUserIdPwd(String userIdPwd) {
		
		return;
	}
	@Override
	public void setResidentGenerateVID(String residentGenerateVID) {
		
		return;
	}
	@Override
	public String getResidentRevokeVID() {
		
		return null;
	}
	@Override
	public void setResidentRevokeVID(String residentRevokeVID) {
		
		return;
	}
	@Override
	public String getResidentAuthHistory() {
		
		return null;
	}
	@Override
	public void setResidentAuthHistory(String residentAuthHistory) {
		
		return;
	}
	@Override
	public String getResidentAuthLock() {
		
		return null;
	}
	@Override
	public void setResidentAuthLock(String residentAuthLock) {
		
		return;
	}
	@Override
	public String getResidentAuthUnlock() {
		
		return null;
	}
	@Override
	public void setResidentAuthUnlock(String residentAuthUnlock) {
		
		return;
	}
	
	@Override
	public String getResidentPrintUin() {
		
		return null;
	}
	@Override
	public void setResidentPrintUin(String residentPrintUin) {
		
		return;
	}
	@Override
	public String getResidentUpdateUin() {
		
		return null;
	}
	@Override
	public void setResidentUpdateUin(String residentUpdateUin) {
		
		return;
	}
	@Override
	public String getResidentCheckStatus() {
		
		return null;
	}
	@Override
	public void setResidentCheckStatus(String residentCheckStatus) {
		
		return;
	}
	@Override
	public String getResidenteUin() {
		
		return null;
	}
	@Override
	public void setResidenteUin(String residenteUin) {
		
		return;
	}
	@Override
	public String getSearchRegCenterTypePath() {
		
		return null;
	}
	@Override
	public String getFilterRegCenterTypePath() {
		
		return null;
	}
	@Override
	public String getDeviceRegisterPath() {
		
		return null;
	}
	@Override
	public String getDeviceValidateHistoryPath() {
		
		return null;
	}
	@Override
	public String getDeviceDeRegisterPath() {
		
		return null;
	}
	@Override
	public String getRegisterDevProviderPath() {
		
		return null;
	}
	@Override
	public String getUpdateDevProviderPath() {
		
		return null;
	}
	@Override
	public String getUpdateDeviceStatusPath() {
		
		return null;
	}
	@Override
	public String getRegisterFTPPath() {
		
		return null;
	}
	@Override
	public String getUpdateFTPPath() {
		
		return null;
	}
	@Override
	public String getMapDeviceRegCenterPath() {
		
		return null;
	}
	@Override
	public String getMapUserRegistrationCenterPath() {
		
		return null;
	}
	@Override
	public String getUnmapUserRegistrationCenterPath() {
		
		return null;
	}
	@Override
	public String getUpdateMDSPath() {
		
		return null;
	}
	@Override
	public String getUnmapDeviceRegCenterPath() {
		
		return null;
	}
	@Override
	public String getMapMachineRegCenterPath() {
		
		return null;
	}
	@Override
	public String getUnmapMachineRegCenterPath() {
		
		return null;
	}
	@Override
	public String getCreateMachinePath() {
		
		return null;
	}
	@Override
	public String getUpdateMachinePath() {
		
		return null;
	}
	@Override
	public String getCreateDevicePath() {
		
		return null;
	}
	@Override
	public String getCreateDocumentTypePath() {
		
		return null;
	}
	@Override
	public String getUpdateDocumentTypePath() {
		
		return null;
	}
	@Override
	public String getFetchPacketStatusPath() {
		
		return null;
	}
	@Override
	public String getUpdateDevicePath() {
		
		return null;
	}
	@Override
	public String getFetchHolidayLocationPath() {
		
		return null;
	}
	@Override
	public String getFetchRegCenterWorkingDays_kernelPath() {
		
		return null;
	}
	@Override
	public String getFetchRegCenterExceptionalHolidays_kernelPath() {
		
		return null;
	}
	@Override
	public String getRegisterMDSPath() {
		
		return null;
	}
	
	@Override
	public String getUpdateHolidayPath() {
		
		return null;
	}
	@Override
	public String getCreateTitlePath() {
		
		return null;
	}
	@Override
	public String getUpdateTitlePath() {
		
		return null;
	}
	@Override
	public String getCreateMachineTypePath() {
		
		return null;
	}
	@Override
	public String getCreateDeviceSpecificationPath() {
		
		return null;
	}
	@Override
	public String getUpdateMachineTypePath() {
		
		return null;
	}
	@Override
	public String getUpdateDeviceSpecificationPath() {
		
		return null;
	}
	@Override
	public String getCreateMachineSpecificationPath() {
		
		return null;
	}
	@Override
	public String getUpdateMachineSpecificationPath() {
		
		return null;
	}
	@Override
	public String getCreateRegCenterTypePath() {
		
		return null;
	}
	@Override
	public String getUpdateRegCenterTypePath() {
		
		return null;
	}
	@Override
	public String getCreateTemplateFileFormatPath() {
		
		return null;
	}
	@Override
	public String getUpdateTemplateFileFormatPath() {
		
		return null;
	}
	@Override
	public String getCreateTemplatePath() {
		
		return null;
	}
	@Override
	public String getUpdateDocumentCategoryPath() {
		
		return null;
	}
	@Override
	public String getCreateDocumentCategoryPath() {
		
		return null;
	}
	@Override
	public String getUpdateGenderPath() {
		
		return null;
	}
	@Override
	public String getCreateGenderPath() {
		
		return null;
	}
	@Override
	public String getCreateHolidayPath() {
		
		return null;
	}
	@Override
	public String getUpdateTemplatePath() {
		
		return null;
	}
	@Override
	public String getCreateTemplateTypePath() {
		
		return null;
	}
	@Override
	public String getCreateDeviceTypePath() {
		
		return null;
	}
	@Override
	public String getUpdateDeviceTypePath() {
		
		return null;
	}
	@Override
	public String getCreateIndividualTypePath() {
		
		return null;
	}
	@Override
	public String getUpdateIndividualTypePath() {
		
		return null;
	}
	@Override
	public String getRetrievePartnerPath() {
		
		return null;
	}
	@Override
	public String getRegisterPartnerPath() {
		
		return null;
	}
	@Override
	public void setRegisterPartnerPath(String registerPartnerPath) {
		
		return;
	}
	@Override
	public String getUpdatePartnerPath() {
		
		return null;
	}
	@Override
	public void setUpdatePartnerPath(String updatePartnerPath) {
		
		return;
	}
	@Override
	public String getSubmitPartnerApiKeyReqPath() {
		
		return null;
	}
	@Override
	public void setSubmitPartnerApiKeyReqPath(String submitPartnerApiKeyReq) {
		
		return;
	}
	@Override
	public String getUpdatePartnerApikeyToPolicyMappingsPath() {
		
		return null;
	}
	@Override
	public void setUpdatePartnerApikeyToPolicyMappingsPath(String updatePartnerApikeyToPolicyMappingsPath) {
		
		return;
	}
	@Override
	public void setApiKeyReqStatusPath(String apiKeyReqStatusPath) {
		
		return;
	}
	@Override
	public String getApiKeyReqStatusPath() {
		
		return null;
	}
	@Override
	public String getActivatePartnerPath() {
		
		return null;
	}
	@Override
	public void setActivateDeactivatePartnerAPIKeyPath(String activateDeactivatePartnerAPIKeyPath) {
		
		return;
	}
	@Override
	public String getActivateDeactivatePartnerAPIKeyPath() {
		
		return null;
	}
	@Override
	public void setApproveRejectPartnerAPIKeyReqPath(String approveRejectPartnerAPIKeyReqPath) {
		
		return;
	}
	@Override
	public String getApproveRejectPartnerAPIKeyReqPath() {
		
		return null;
	}
	@Override
	public String getRetrieveParticularPartnerDetailsForGivenPartnerIdPath() {
		
		return null;
	}
	@Override
	public String getRetrieveTheRequestForPartnerAPIKeyForGivenRequestIdPath() {
		
		return null;
	}
	@Override
	public String getCreatePolicyGroupPath() {
		
		return null;
	}
	@Override
	public void setCreatePolicyGroupPath(String createPolicyGroupPath) {
		
		return;
	}
	@Override
	public String getGetPolicyBasedOnPolicyIdPath() {
		
		return null;
	}
	@Override
	public String getRetrievePartnerPolicyDetailsForGivenPartnerAPIKeyPath() {
		
		return null;
	}
	@Override
	public String getUpdateExistingPolicyForPolicyGroupPath() {
		
		return null;
	}
	@Override
	public void setUpdateExistingPolicyForPolicyGroupPath(String updateExistingPolicyForPolicyGroupPath) {
		
		return;
	}
	@Override
	public void setUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath(
			String updateTheStatusActivateDeactivateForTheGivenPolicyIdPath) {
		
		return;
	}
	@Override
	public String getUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath() {
		
		return null;
	}
	@Override
	public String getCreateMISPPath() {
		
		return null;
	}
	@Override
	public void setUpdateMISPPath(String updateMISPPath) {
		
		return;
	}
	@Override
	public String getUpdateMISPPath() {
		
		return null;
	}
	@Override
	public String getValidateMISPLicensePath() {
		
		return null;
	}
	@Override
	public String getApproveMISPPath() {
		
		return null;
	}
	@Override
	public String getRejectMISPPath() {
		
		return null;
	}
	@Override
	public String getActivateMISPLicense() {
		
		return null;
	}
	@Override
	public String getRetrieveMISPByMispIDPath() {
		
		return null;
	}
	@Override
	public String getRetrievePartnerAPIkeyToPolicyMappingsPath() {
		
		return null;
	}
	@Override
	public String getRetrieveMISPsDetailsByGivenNamePath() {
		
		return null;
	}
	@Override
	public void setUpdateMispStatusByMispIdPath(String updateMispStatusByMispIdPath) {
		
		return;
	}
	@Override
	public String getUpdateMispStatusByMispIdPath() {
		
		return null;
	}
	@Override
	public String getDownloadMispLicenseKeyPath() {
		
		return null;
	}
	@Override
	public String getCreateDynamicFieldPath() {
		return null;
	}
	@Override
	public String getAllDynamicFieldPath() {
		
		return null;
	}
	@Override
	public String getDefinePolicyPath() {
		
		return null;
	}
	@Override
	public void setDefinePolicyPath(String definePolicyPath) {
		
		return;
	}
	@Override
	public void setUpdatePolicyDetailsPath(String updatePolicyDetailsPath) {
		
		return;
	}
	@Override
	public String getUpdatePolicyDetailsPath() {
		
		return null;
	}
	@Override
	public void setUpdatePolicyStatusPath(String updatePolicyStatusPath) {
		
		return;
	}
	@Override
	public String getUpdatePolicyStatusPath() {
		
		return null;
	}
	@Override
	public String getPublishPolicyPath() {
		
		return null;
	}
	@Override
	public void setPublishPolicyPath(String publishPolicyPath) {
		
		return;
	}
	@Override
	public String getGetPolicyGroupPath() {
		
		return null;
	}
	@Override
	public String getGetPolicyAgainstApiKeyPath() {
		
		return null;
	}
	@Override
	public String getGetPartnersPolicyPath() {
		
		return null;
	}
	@Override
	public String getAddContactPath() {
		
		return null;
	}
	@Override
	public void setAddContactPath(String addContactPath) {
		
		return;
	}
	@Override
	public String getSaveDeviceDetailPath() {
		
		return null;
	}
	@Override
	public void setSaveDeviceDetailPath(String saveDeviceDetailPath) {
		
		return;
	}
	@Override
	public String getApproveRejectDeviceDetailPath() {
		
		return null;
	}
	@Override
	public String getUpdateDeviceDetailPath() {
		
		return null;
	}
	@Override
	public void setUpdateDeviceDetailPath(String updateDeviceDetailPath) {
		
		return;
	}
	@Override
	public String getSaveSecureBiometricInterfacePath() {
		
		return null;
	}
	@Override
	public void setSaveSecureBiometricInterfacePath(String saveSecureBiometricInterfacePath) {
		
		return;
	}
	@Override
	public String getApproveRejectSecureBiometricInterfacePath() {
		
		return null;
	}
	@Override
	public String getSaveFtpChipDetailPath() {
		
		return null;
	}
	@Override
	public void setSaveFtpChipDetailPath(String saveFtpChipDetailPath) {
		
		return;
	}
	@Override
	public String getApproveRejectFtpChipDetailPath() {
		
		return null;
	}
}
