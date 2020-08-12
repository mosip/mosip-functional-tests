package io.mosip.authentication.idRepository.fw.util;

import java.io.File;     
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.dto.ErrorsDto;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;

/**
 * The class hold all the run config path available in runconfiguration file
 * 
 * @author Vignesh
 *
 */
public class IdRepoRunConfig extends RunConfig{
	
	private  Logger logger = Logger.getLogger(IdRepoRunConfig.class);
	private  String endPointUrl;
	private  String encryptUtilBaseUrl;
	private  String encryptionPath;
	private  String encodePath;
	private  String decodePath;
	private  String scenarioPath;
	private  String srcPath;
	private  String userDirectory;
	private  String testDataPath;
	private  String idRepoEndPointUrl;
	private  String testType;
	private  String generateUINPath;
	private  String idRepoRetrieveDataPath;
	private  String idRepoCreateUINRecordPath;
	private  String IdRepoUpdateVIDStatusPath;
	private  String idRepoCreateVIDRecordPath;
	private  String idRepoRetrieveUINByVIDPath;
	private  String storeUINDataPath;
	private  String encodeFilePath;
	private  String decodeFilePath;
	private  String vidGenPath;
	private  String testDataFolderName;
	private  String authVersion;
	private  String clientidsecretkey;
	private String moduleFolderName;
	private String decryptPath;
	private String uinIdentityMapper;
	private String internalEncryptionPath;
	private String validateSignaturePath;
	private String encryptionPort;
	private String idRepoRetrieveIdentityByRid;
	private String idRepoDeactivateVIDs;
	private String idRepoReactivateVIDs;

	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public  String getEncryptUtilBaseUrl() {
		return encryptUtilBaseUrl;
	}

	public String getModuleFolderName() {
		return moduleFolderName;
	}

	public void setModuleFolderName(String moduleFolderName) {
		this.moduleFolderName = moduleFolderName;
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
	public  void setEncryptionPath(String encryptionPath) {
		this.encryptionPath = encryptionPath;
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
	public  void setEncodePath(String encodePath) {
		this.encodePath = encodePath;
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
		String path =currentDir.toFile().getAbsolutePath().toString();
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
		setEndPointUrl(AuthTestsUtil.getPropertyValue("endPointUrl"));
		setAuthVersion(IdRepoTestsUtil.getPropertyValue("authVersion"));
		setSrcPath(IdRepoTestsUtil.getPropertyValue("srcPath"));
		setEncryptionPort(AuthTestsUtil.getPropertyValue(System.getProperty("env.user")+".encryptionPort"));
		setEncryptUtilBaseUrl(AuthTestsUtil.getPropertyValue("encryptUtilBaseUrl"));
		setEncryptionPath(AuthTestsUtil.getPropertyValue("encryptionPath"));
		setEncodePath(AuthTestsUtil.getPropertyValue("encodePath"));
		setDecodePath(AuthTestsUtil.getPropertyValue("decodePath"));
		setDecryptPath(AuthTestsUtil.getPropertyValue("decryptPath"));
		setInternalEncryptionPath(AuthTestsUtil.getPropertyValue("internalEncryptionPath"));
		setValidateSignaturePath(AuthTestsUtil.getPropertyValue("validateSignaturePath"));
		setUserDirectory();
		setTestDataPath(testDataPath);	
		setIdRepoEndPointUrl(IdRepoTestsUtil.getPropertyValue("idRepoEndPointUrl"));
		setIdRepoRetrieveDataPath(IdRepoTestsUtil.getPropertyValue("idRepoRetrieveDataPath"));
		setIdRepoRetrieveUINByVIDPath(IdRepoTestsUtil.getPropertyValue("idRepoRetrieveUINByVIDPath"));
		File testDataFilePath = new File(RunConfigUtil.getResourcePath()
		+ testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath,testDataPath);
		setTestType(testType);
		setGenerateUINPath(IdRepoTestsUtil.getPropertyValue("generateUINPath"));
		setIdRepoCreateUINRecordPath(IdRepoTestsUtil.getPropertyValue("idRepoCreateUINRecordPath"));
		setIdRepoCreateVIDRecordPath(IdRepoTestsUtil.getPropertyValue("idRepoCreateVIDRecordPath"));
		setIdRepoUpdateVIDStatusPath(IdRepoTestsUtil.getPropertyValue("idRepoUpdateVIDStatusPath"));
		setIdRepoRetrieveIdentityByRid(IdRepoTestsUtil.getPropertyValue("idRepoRetrieveIdentityByRid"));
		setStoreUINDataPath(IdRepoTestsUtil.getPropertyValue("storeUINDataPath"));
		setEncodeFilePath(IdRepoTestsUtil.getPropertyValue("encodeFilePath"));
		setDecodeFilePath(IdRepoTestsUtil.getPropertyValue("decodeFilePath"));
		setClientidsecretkey(IdRepoTestsUtil.getPropertyValue("clientidsecretkey"));
		//loadingConfigFile
		loadErrorsData(getErrorsConfigPath());
		setIdRepoDeactivateVIDs(IdRepoTestsUtil.getPropertyValue("idRepoDeactivateVIDs"));
		setIdRepoReactivateVIDs(IdRepoTestsUtil.getPropertyValue("idRepoReactivateVIDs"));
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
		this.vidGenPath = vidGenPath.replace("$authVersion$", this.getAuthVersion());
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
		return "idRepository/" + this.getTestDataFolderName() + "/RunConfig/errorCodeMsg.yml";
	}
	
	/**
	 * The method load yml error test data
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private  void loadErrorsData(String path) {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
			ErrorsDto.setErrors((Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public  String getClientidsecretkey() {
		return clientidsecretkey;
	}
	public  void setClientidsecretkey(String clientidsecretkey) {
		this.clientidsecretkey = clientidsecretkey;
	}
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public  void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	@Override
	public String getEndPointUrl() {
		// TODO Auto-generated method stub
		return endPointUrl;
	}

	@Override
	public void setEndPointUrl(String endPointUrl) {
		 this.endPointUrl= endPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
		
	}

	@Override
	public String getEkycPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEkycPath(String ekycPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAuthPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthPath(String authPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInternalAuthPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInternalAuthPath(String internalAuthPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOtpPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOtpPath(String otpPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getGenerateVIDPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenerateVIDPath(String generateVIDPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoCreateVIDRecordPath() {
		return idRepoCreateVIDRecordPath;
	}

	@Override
	public void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath) {
		this.idRepoCreateVIDRecordPath = idRepoCreateVIDRecordPath;
		
	}

	@Override
	public String getIdRepoUpdateVIDStatusPath() {		
		return IdRepoUpdateVIDStatusPath;
	}

	@Override
	public void setIdRepoUpdateVIDStatusPath(String IdRepoUpdateVIDStatusPath) {
		this.IdRepoUpdateVIDStatusPath = IdRepoUpdateVIDStatusPath;
		
	}

	@Override
	public String getIdRepoVersion() {
		// TODO Auto-generated method stub
		return "IdRepoVersion is not supported for idrepo";
	}

	@Override
	public void setIdRepoVersion(String idRepoVersion) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIdRepoRetrieveUINByVIDPath() {
		return this.idRepoRetrieveUINByVIDPath;
	}
	@Override
	public void setIdRepoRetrieveUINByVIDPath(String idRepoRetrieveUINByVIDPath) {
	    this.idRepoRetrieveUINByVIDPath = idRepoRetrieveUINByVIDPath;
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

	@Override
	public String getIdRepoRetrieveIdentityByUin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoRetrieveIdentityByUin(String idRepoRetrieveIdentityByUin) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIdRepoRetrieveIdentityByRid() {
		return this.idRepoRetrieveIdentityByRid;
	}
	@Override
	public void setIdRepoRetrieveIdentityByRid(String idRepoRetrieveIdentityByRid) {
		this.idRepoRetrieveIdentityByRid = idRepoRetrieveIdentityByRid;
		
	}

	@Override
	public String getIdRepoRegenerateVID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoRegenerateVID(String idRepoRegenerateVID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminEndPointUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminEndPointUrl(String adminEndPointUrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminCreateRegCentrePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminCreateRegCentrePath(String adminCreateRegCentrePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminCreateRegistrationCentrePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminCreateRegistrationCentrePath(String adminCreateRegistrationCentrePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSearchMachinePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSearchMachinePath(String searchMachinePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceSearchPath(String deviceSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceFilterPath(String deviceFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceSpecSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceSpecSearchPath(String deviceSpecSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceSpecFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceSpecFilterPath(String deviceSpecFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceTypeSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceTypeSearchPath(String deviceTypeSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceTypeFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeviceTypeFilterPath(String deviceTypeFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitleSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTitleSearchPath(String titleSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitleFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTitleFilterPath(String titleFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHolidaySearchPath(String holidaySearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHolidaySearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSearchDocumentTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSearchDocumentTypePath(String searchDocumentTypePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminMachineTypeSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminMachineTypeSearchPath(String adminMachineTypeSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminSearchTemplateDetailsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminSearchTemplateDetailsPath(String adminSearchTemplateDetailsPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminIndividualTypesSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminIndividualTypesSearchPath(String adminIndividualTypesSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminRegistrationCentreSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminRegistrationCentreSearchPath(String adminRegistrationCentreSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSearchBlackListedWords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSearchBlackListedWords(String searchBlackListedWordsPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSearchDocCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAdminUpdateRegistrationCentrePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminUpdateRegistrationCentrePath(String adminUpdateRegistrationCentrePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFilterTempateDetailsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterDocCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAdminGenderSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminGenderSearchPath(String adminGenderSearchPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminGenderFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminGenderFilterPath(String adminGenderFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAdminRegistrationCentreFilterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminRegistrationCentreFilterPath(String adminRegistrationCentreFilterPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String MapDocumentCategoryAndDocumentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterBlackListedWordsPath(String filterBlackListedWordsPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFilterBlackListedWordsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterDocCatTypMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterDocumentTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterDocumentTypePath(String filterDocumentTypePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFilterMachinesPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterMachinesPath(String filterMachinesPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSearchMachineSpec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterMachineSpec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSearchValidDocumentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdaInternalOtpPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalOtpPath(String internalPath) {
		// TODO Auto-generated method stub		
	}
	
	public String getIdRepoDeactivateVIDs() {
		return idRepoDeactivateVIDs;
	}

	public void setIdRepoDeactivateVIDs(String idRepoDeactivateVIDs) {
		this.idRepoDeactivateVIDs = idRepoDeactivateVIDs;
	}

	public String getIdRepoReactivateVIDs() {
		return idRepoReactivateVIDs;
	}

	public void setIdRepoReactivateVIDs(String idRepoReactivateVIDs) {
		this.idRepoReactivateVIDs = idRepoReactivateVIDs;
	}

	@Override
	public String getUnmapDocCategoryType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSearchLocationDataPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSearchLocationDataPath(String searchLocationPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFilterLocationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterLocationPath(String filterLocationPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCreateBlackListedWordsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreateBlackListedWordsPath(String createBlackListedWordsPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCreateLocationDataPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreateLocationDataPath(String createLocationDataPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdaInternalAuthTransactionWithVID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalAuthTransactionWithVID(String idaInternalAuthTransactionWithVID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdaInternalAuthTransactionWithUIN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalAuthTransactionWithUIN(String idaInternalAuthTransactionWithUIN) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdaInternalUpdateAuthTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalUpdateAuthTypePath(String idaInternalUpdateAuthTypePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdaInternalRetrieveAuthTypeStatusPathForUIN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalRetrieveAuthTypeStatusPathForUIN(String idaInternalRetrieveAuthTypeStatusPathForUIN) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdaInternalRetrieveAuthTypeStatusPathForVID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalRetrieveAuthTypeStatusPathForVID(String idaInternalRetrieveAuthTypeStatusPathForVID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCryptomanagerEncrypt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCryptomanagerEncrypt(String cryptomanagerEncrypt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSplitEncryptedData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSplitEncryptedData(String splitEncryptedData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDecommisionRegCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDecommisionDevicePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetGetLeafZones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetGetZoneNameBasedOnUserIDAndLangCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetZoneHierarchy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDecommisionMachinePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateLocationData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateBlackListedWordsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceValidatePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterIndividualTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterHolidaysPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthManagerUserIdPwd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthManagerUserIdPwd(String authManagerUserIdPwd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentGenerateVID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserIdPwd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserIdPwd(String userIdPwd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResidentGenerateVID(String residentGenerateVID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentRevokeVID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentRevokeVID(String residentRevokeVID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentAuthHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentAuthHistory(String residentAuthHistory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentAuthLock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentAuthLock(String residentAuthLock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentAuthUnlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentAuthUnlock(String residentAuthUnlock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidenteUin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidenteUin(String residenteUin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentPrintUin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentPrintUin(String residentPrintUin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentUpdateUin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentUpdateUin(String residentUpdateUin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResidentCheckStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResidentCheckStatus(String residentCheckStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSearchRegCenterTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilterRegCenterTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceRegisterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceValidateHistoryPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceDeRegisterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRegisterDevProviderPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDevProviderPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDeviceStatusPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRegisterFTPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateFTPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMapDeviceRegCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMapUserRegistrationCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnmapUserRegistrationCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateMDSPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnmapDeviceRegCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMapMachineRegCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnmapMachineRegCenterPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateMachinePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateMachinePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDevicePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDocumentTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDocumentTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFetchPacketStatusPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDevicePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFetchHolidayLocationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFetchRegCenterWorkingDays_kernelPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFetchRegCenterExceptionalHolidays_kernelPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRegisterMDSPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getUpdateHolidayPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateTitlePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateTitlePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateMachineTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDeviceSpecificationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateMachineTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDeviceSpecificationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateMachineSpecificationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateMachineSpecificationPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateRegCenterTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateRegCenterTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateTemplateFileFormatPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateTemplateFileFormatPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateTemplatePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDocumentCategoryPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDocumentCategoryPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateGenderPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateGenderPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateHolidayPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateTemplatePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateTemplateTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDeviceTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDeviceTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateIndividualTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateIndividualTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrievePartnerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRegisterPartnerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegisterPartnerPath(String registerPartnerPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUpdatePartnerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdatePartnerPath(String updatePartnerPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSubmitPartnerApiKeyReqPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSubmitPartnerApiKeyReqPath(String submitPartnerApiKeyReq) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUpdatePartnerApikeyToPolicyMappingsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdatePartnerApikeyToPolicyMappingsPath(String updatePartnerApikeyToPolicyMappingsPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApiKeyReqStatusPath(String apiKeyReqStatusPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApiKeyReqStatusPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActivatePartnerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivateDeactivatePartnerAPIKeyPath(String activateDeactivatePartnerAPIKeyPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActivateDeactivatePartnerAPIKeyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setApproveRejectPartnerAPIKeyReqPath(String approveRejectPartnerAPIKeyReqPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApproveRejectPartnerAPIKeyReqPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrieveParticularPartnerDetailsForGivenPartnerIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrieveTheRequestForPartnerAPIKeyForGivenRequestIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreatePolicyGroupPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreatePolicyGroupPath(String createPolicyGroupPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getGetPolicyBasedOnPolicyIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrievePartnerPolicyDetailsForGivenPartnerAPIKeyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateExistingPolicyForPolicyGroupPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdateExistingPolicyForPolicyGroupPath(String updateExistingPolicyForPolicyGroupPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath(
			String updateTheStatusActivateDeactivateForTheGivenPolicyIdPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateMISPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdateMISPPath(String updateMISPPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUpdateMISPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValidateMISPLicensePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApproveMISPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRejectMISPPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActivateMISPLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrieveMISPByMispIDPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrievePartnerAPIkeyToPolicyMappingsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRetrieveMISPsDetailsByGivenNamePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdateMispStatusByMispIdPath(String updateMispStatusByMispIdPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUpdateMispStatusByMispIdPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDownloadMispLicenseKeyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDynamicFieldPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllDynamicFieldPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
