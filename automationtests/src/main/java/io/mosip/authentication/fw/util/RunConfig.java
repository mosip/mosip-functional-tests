package io.mosip.authentication.fw.util;

public abstract class RunConfig {

	/**
	 * The method get endpoint url for IDA
	 * 
	 * @return string
	 */
	public abstract String getEndPointUrl();
	/**
	 * The method set endpoint url
	 * 
	 * @param endPointUrl
	 */
	public abstract void setEndPointUrl(String endPointUrl);
	/**
	 * The method get ekyc url path
	 * 
	 * @returnstring 
	 */
	public abstract String getEkycPath();
	/**
	 * The method set ekyc url path
	 * 
	 * @param ekycPath
	 */
	public abstract void setEkycPath(String ekycPath);
	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public abstract String getEncryptUtilBaseUrl();

	/**
	 * The method will set encryption endpoint path
	 * 
	 * @param encryptUtilBaseUrl
	 */
	public abstract void setEncryptUtilBaseUrl(String encryptUtilBaseUrl);
	/**
	 * The method will get encryption path
	 * 
	 * @return string
	 */
	public abstract String getEncryptionPath();
	/**
	 * The method set encryption path
	 * 
	 * @param encryptionPath
	 */
	public abstract void setEncryptionPath(String encryptionPath);
	/**
	 * The method get encode path
	 * 
	 * @return string
	 */
	public abstract String getEncodePath();
	/**
	 * The method set encode path
	 * 
	 * @param encodePath
	 */
	public abstract void setEncodePath(String encodePath);
	/**
	 * The method get decode path 
	 * 
	 * @return string
	 */
	public abstract String getDecodePath();
	/**
	 * The method set decode path
	 * 
	 * @param decodePath
	 */
	public abstract void setDecodePath(String decodePath);
	/**
	 * The method get scenario path of current test execution
	 * 
	 * @return string
	 */
	public abstract String getScenarioPath();
	/**
	 * The method set scenatio path of current test execution
	 * 
	 * @param scenarioPath
	 */
	public abstract void setScenarioPath(String scenarioPath);
	/**
	 * The method get source path from config file
	 * 
	 * @return string
	 */
	public abstract String getSrcPath();
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public abstract void setSrcPath(String srcPath);
	/**
	 * The method get auth path
	 * 
	 * @return String
	 */
	public abstract String getAuthPath();
	/**
	 * The method set auth path
	 * 
	 * @param authPath
	 */
	public abstract void setAuthPath(String authPath);
	/**
	 * The method get internal auth path
	 * 
	 * @return string
	 */
	public abstract String getInternalAuthPath();
	/**
	 * The method set internal auth path
	 * 
	 * @param internalAuthPath
	 */
	public abstract void setInternalAuthPath(String internalAuthPath);
	/**
	 * The method get otp path
	 * 
	 * @return string
	 */
	public abstract String getOtpPath();
	/**
	 * The method set otp path
	 * 
	 * @param otpPath
	 */
	public abstract void setOtpPath(String otpPath);	
	/**
	 * The method get current test data path
	 * 
	 * @return string
	 */
	public abstract String getTestDataPath();
	/**
	 * The method set current test data path
	 * 
	 * @param testDataPath
	 */
	public abstract void setTestDataPath(String testDataPath);
	/**
	 * The method get user directory of project
	 * 
	 * @return string
	 */
	public abstract String getUserDirectory();
	/**
	 * The method set user directory
	 */
	public abstract void setUserDirectory();
	/**
	 * The method get idrepo endpoint url
	 * 
	 * @return string
	 */
	public abstract String getIdRepoEndPointUrl();
	/**
	 * The method set idrepo endpoint url
	 * 
	 * @param idRepoEndPointUrl
	 */
	public abstract void setIdRepoEndPointUrl(String idRepoEndPointUrl);
	
	/**
	 * The method set configuration 
	 * 
	 * @param testDataPath
	 * @param testDataFileName
	 * @param testType
	 */
	public abstract void setConfig(String testDataPath,String testDataFileName,String testType);	
	/**
	 * The method get test type of current execution
	 * 
	 * @return string
	 */
	public abstract String getTestType();
	/**
	 * The method set test type of current execution
	 * 
	 * @param testType
	 */
	public abstract void setTestType(String testType);	
	/**
	 * The method get UIN generation path
	 * 
	 * @return string
	 */
	public abstract String getGenerateUINPath();
	/**
	 * The method set UIN generation path
	 * 
	 * @param generateUINPath
	 */
	public abstract void setGenerateUINPath(String generateUINPath);	
	
	/**
	 * The method get retrieve idrepo path
	 * 
	 * @return string
	 */
	public abstract String getIdRepoRetrieveDataPath();
	/**
	 * The method set retrieve idrepo path
	 * 
	 * @param idRepoRetrieveDataPath
	 */
	public abstract void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath);
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @return string
	 */
	public abstract String getIdRepoCreateUINRecordPath();
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @param idRepoCreateUINRecordPath
	 */
	public abstract void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath);	
	/**
	 * The method get store UIN Data path
	 * 
	 * @return string
	 */
	public abstract String getStoreUINDataPath();
	/**
	 * The method set store UIN Data path
	 * 
	 * @param storeUINDataPath
	 */
	public abstract void setStoreUINDataPath(String storeUINDataPath);	
	/**
	 * The method get encode file path
	 * 
	 * @return string
	 */
	public abstract String getEncodeFilePath();
	/**
	 * The method will set encode file path
	 * 
	 * @param encodeFile
	 */
	public abstract void setEncodeFilePath(String encodeFile);
	/**
	 * The method get decode file path
	 * 
	 * @return string
	 */
	public abstract String getDecodeFilePath();
	/**
	 * The method set decode file path
	 * 
	 * @param decodeFilePath
	 */
	public abstract void setDecodeFilePath(String decodeFilePath);	
	/**
	 * The method set VID generation path
	 * 
	 * @return
	 */
	public abstract String getVidGenPath();
	/**
	 * The method set VID generation path
	 * 
	 * @param vidGenPath
	 */
	public abstract void setVidGenPath(String vidGenPath);	
	/**
	 * The method get test data folder name of current test execution
	 * 
	 * @return string
	 */
	public abstract String getTestDataFolderName();
	/**
	 * The method set test data folder name of current execution
	 * 
	 * @param testDataFolderName
	 */
	public abstract void setTestDataFolderName(String testDataFolderName);	
	/**
	 * The method get current auth version 
	 * 
	 * @return string
	 */
	public abstract String getAuthVersion();
	/**
	 * The method set current auth version from config file
	 * 
	 * @param authVersion
	 */
	public abstract void setAuthVersion(String authVersion);
	
	/**
	 * The method get error config path
	 * 
	 * @return string
	 */
	public abstract String getErrorsConfigPath();	
	public abstract String getClientidsecretkey();
	public abstract void setClientidsecretkey(String clientidsecretkey);
	public abstract String getModuleFolderName();
	public abstract void setModuleFolderName(String moduleFolderName);
	public abstract String getGenerateVIDPath();
	public abstract void setGenerateVIDPath(String generateVIDPath);
	public abstract String getIdRepoCreateVIDRecordPath();
	public abstract void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath);
	public abstract String getIdRepoUpdateVIDStatusPath();
	public abstract void setIdRepoUpdateVIDStatusPath(String IdRepoUpdateVIDStatusPath);
	public abstract String getIdRepoVersion();
	public abstract void setIdRepoVersion(String idRepoVersion);
	public abstract String getDecryptPath();
	public abstract void setDecryptPath(String decryptPath);
	public abstract void setUinIdentityMapper(String uinIdentityMapper);
	public abstract String getUinIdentityMapper();
	public abstract String getInternalEncryptionPath();
	public abstract void setInternalEncryptionPath(String internalEncryptionPath);
	public abstract String getValidateSignaturePath();
	public abstract void setValidateSignaturePath(String validateSignaturePath);
	public abstract String getEncryptionPort();
	public abstract void setEncryptionPort(String encryptionPort);
	public abstract String getIdRepoRetrieveIdentityByUin();
	public abstract void setIdRepoRetrieveIdentityByUin(String idRepoRetrieveIdentityByUin);
	public abstract String getIdRepoRetrieveUINByVIDPath();
	public abstract void setIdRepoRetrieveUINByVIDPath(String idRepoRetrieveIdentityByRid);
	public abstract String getIdRepoRetrieveIdentityByRid();
	public abstract void setIdRepoRetrieveIdentityByRid(String idRepoRetrieveIdentityByRid);
	public abstract String getIdRepoRegenerateVID();
	public abstract void setIdRepoRegenerateVID(String idRepoRegenerateVID);
}
