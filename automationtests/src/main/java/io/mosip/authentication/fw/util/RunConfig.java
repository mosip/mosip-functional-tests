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
	public abstract String getIdaInternalOtpPath();
	public abstract void setIdaInternalOtpPath(String internalPath);
	public abstract String getIdRepoDeactivateVIDs();
	public abstract void setIdRepoDeactivateVIDs(String idRepoDeactivateVIDs);
	public abstract String getIdRepoReactivateVIDs();
	public abstract void setIdRepoReactivateVIDs(String idRepoReactivateVIDs);		
	public abstract String getIdaInternalAuthTransactionWithVID();
	public abstract void setIdaInternalAuthTransactionWithVID(String idaInternalAuthTransactionWithVID);
	public abstract String getIdaInternalAuthTransactionWithUIN();
	public abstract void setIdaInternalAuthTransactionWithUIN(String idaInternalAuthTransactionWithUIN);
	public abstract String getIdaInternalUpdateAuthTypePath();
	public abstract void setIdaInternalUpdateAuthTypePath(String idaInternalUpdateAuthTypePath);
	public abstract String getIdaInternalRetrieveAuthTypeStatusPathForUIN();
	public abstract void setIdaInternalRetrieveAuthTypeStatusPathForUIN(String idaInternalRetrieveAuthTypeStatusPathForUIN);
	public abstract String getIdaInternalRetrieveAuthTypeStatusPathForVID();
	public abstract void setIdaInternalRetrieveAuthTypeStatusPathForVID(String idaInternalRetrieveAuthTypeStatusPathForVID);
	public abstract String getCryptomanagerEncrypt();
	public abstract void setCryptomanagerEncrypt(String cryptomanagerEncrypt);	
	public abstract String getSplitEncryptedData();
	public abstract void setSplitEncryptedData(String splitEncryptedData);
	public abstract String getAuthManagerUserIdPwd();
	public abstract void setAuthManagerUserIdPwd(String authManagerUserIdPwd);
	
	//Admin module
			public abstract String getAdminEndPointUrl();
			public abstract void setAdminEndPointUrl(String adminEndPointUrl);
			public abstract String getAdminCreateRegCentrePath();
			public abstract void setAdminCreateRegCentrePath(String adminCreateRegCentrePath);
			public abstract String getAdminCreateRegistrationCentrePath();
			public abstract void setAdminCreateRegistrationCentrePath(String adminCreateRegistrationCentrePath);
			public abstract String getSearchMachinePath();
			public abstract void setSearchMachinePath(String searchMachinePath);
			public abstract String getDeviceSearchPath();
			public abstract void setDeviceSearchPath(String deviceSearchPath);
			public abstract String getDeviceFilterPath();
			public abstract void setDeviceFilterPath(String deviceFilterPath);
			public abstract String getDeviceSpecSearchPath();
			public abstract void setDeviceSpecSearchPath(String deviceSpecSearchPath);
			public abstract String getDeviceSpecFilterPath();
			public abstract void setDeviceSpecFilterPath(String deviceSpecFilterPath);
			public abstract String getDeviceTypeSearchPath();
			public abstract void setDeviceTypeSearchPath(String deviceTypeSearchPath);
			public abstract String getDeviceTypeFilterPath();
			public abstract void setDeviceTypeFilterPath(String deviceTypeFilterPath);
			public abstract String getTitleSearchPath();
			public abstract void setTitleSearchPath(String titleSearchPath);
			public abstract String getTitleFilterPath();
			public abstract void setTitleFilterPath(String titleFilterPath);
			public abstract void setHolidaySearchPath(String holidaySearchPath);
			public abstract String getHolidaySearchPath();
			public abstract String getSearchDocumentTypePath(); 
			public abstract void setSearchDocumentTypePath(String searchDocumentTypePath);
			public abstract String getAdminMachineTypeSearchPath();
			public abstract void setAdminMachineTypeSearchPath(String adminMachineTypeSearchPath);
			public abstract String getAdminSearchTemplateDetailsPath();
			public abstract void setAdminSearchTemplateDetailsPath(String adminSearchTemplateDetailsPath);
			public abstract String getAdminIndividualTypesSearchPath();
			public abstract void setAdminIndividualTypesSearchPath(String adminIndividualTypesSearchPath);
			public abstract String getAdminRegistrationCentreSearchPath();
			public abstract void setAdminRegistrationCentreSearchPath(String adminRegistrationCentreSearchPath);
			public abstract String getSearchBlackListedWords();
			public abstract void setSearchBlackListedWords(String searchBlackListedWordsPath);
			public abstract String getSearchDocCategories();
			public abstract String getAdminUpdateRegistrationCentrePath();
			public abstract void setAdminUpdateRegistrationCentrePath(String adminUpdateRegistrationCentrePath);
			public abstract String getFilterTempateDetailsPath();
			public abstract String getFilterDocCategory();

			
			public abstract String getAdminGenderSearchPath();
			public abstract void setAdminGenderSearchPath(String adminGenderSearchPath);
			
			public abstract String getAdminGenderFilterPath();
			public abstract void setAdminGenderFilterPath(String adminGenderFilterPath);
			

			public abstract String getAdminRegistrationCentreFilterPath();
			public abstract void setAdminRegistrationCentreFilterPath(String adminRegistrationCentreFilterPath);

			public abstract String MapDocumentCategoryAndDocumentType();
			public abstract void setFilterBlackListedWordsPath(String filterBlackListedWordsPath);
			public abstract String getFilterBlackListedWordsPath();
			public abstract String getFilterDocCatTypMapping();
			public abstract String getFilterDocumentTypePath();
			public abstract void setFilterDocumentTypePath(String filterDocumentTypePath);
			public abstract String getFilterMachinesPath();
			public abstract void setFilterMachinesPath(String filterMachinesPath);
			public abstract String getSearchMachineSpec() ;
			public abstract String getFilterMachineSpec();
			public abstract String getSearchValidDocumentPath();
			public abstract String getUnmapDocCategoryType();
			public abstract String getSearchLocationDataPath();
			public abstract void setSearchLocationDataPath(String searchLocationPath);
			public abstract String getFilterLocationPath();
			public abstract void setFilterLocationPath(String filterLocationPath);
			public abstract String getCreateBlackListedWordsPath();
			public abstract void setCreateBlackListedWordsPath(String createBlackListedWordsPath);
			public abstract String getCreateLocationDataPath();
			public abstract void setCreateLocationDataPath(String createLocationDataPath);
			public abstract String getDecommisionRegCenterPath();
			public abstract String getDecommisionDevicePath();
			public abstract String getSetGetLeafZones();
			public abstract String getSetGetZoneNameBasedOnUserIDAndLangCode();
			public abstract String getSetZoneHierarchy();
			public abstract String getDecommisionMachinePath();
			public abstract String getUpdateLocationData();
			public abstract String getUpdateBlackListedWordsPath();
			public abstract String getDeviceValidatePath();
			public abstract String getFilterIndividualTypePath();
			public abstract String getFilterHolidaysPath();
			public abstract String getSearchRegCenterTypePath();
			public abstract String getFilterRegCenterTypePath();
			public abstract String getDeviceRegisterPath();
			public abstract String getDeviceValidateHistoryPath();
			public abstract String getDeviceDeRegisterPath();
			public abstract String getRegisterDevProviderPath();
			public abstract String getUpdateDevProviderPath();
			public abstract String getUpdateDeviceStatusPath();
			public abstract String getRegisterFTPPath();
			public abstract String getUpdateFTPPath();
			public abstract String getMapDeviceRegCenterPath();
			public abstract String getMapUserRegistrationCenterPath();
			public abstract String getUnmapUserRegistrationCenterPath();
			public abstract String getUpdateMDSPath();
			public abstract String getUnmapDeviceRegCenterPath();
			public abstract String getMapMachineRegCenterPath();
			public abstract String getUnmapMachineRegCenterPath();
			public abstract String getCreateMachinePath();
			public abstract String getUpdateMachinePath();
			public abstract String getCreateDevicePath();
			public abstract String getCreateDocumentTypePath();
			public abstract String getUpdateDocumentTypePath();
			public abstract String getFetchPacketStatusPath();
			public abstract String getUpdateDevicePath();
			public abstract String getFetchHolidayLocationPath();
			public abstract String getFetchRegCenterWorkingDays_kernelPath();
			public abstract String getFetchRegCenterExceptionalHolidays_kernelPath();
			public abstract String getRegisterMDSPath();
			
			public abstract String getUpdateHolidayPath();
			public abstract String getCreateTitlePath();
			public abstract String getUpdateTitlePath();
			public abstract String getCreateMachineTypePath();
			public abstract String getUpdateMachineTypePath();
			public abstract String getCreateDeviceSpecificationPath();
			public abstract String getUpdateDeviceSpecificationPath();
			public abstract String getCreateMachineSpecificationPath();
			public abstract String getUpdateMachineSpecificationPath();
			public abstract String getCreateRegCenterTypePath();
			public abstract String getUpdateRegCenterTypePath();
			public abstract String getCreateTemplateFileFormatPath();
			public abstract String getUpdateTemplateFileFormatPath();
			public abstract String getCreateTemplatePath();
			public abstract String getUpdateDocumentCategoryPath();
			public abstract String getCreateDocumentCategoryPath();
			public abstract String getUpdateGenderPath();
			public abstract String getCreateGenderPath();
			public abstract String getCreateHolidayPath();
			public abstract String getUpdateTemplatePath();
			public abstract String getCreateTemplateTypePath();
			public abstract String getCreateDeviceTypePath();
			public abstract String getUpdateDeviceTypePath();
			public abstract String getCreateIndividualTypePath();
			public abstract String getUpdateIndividualTypePath();
			public abstract String getCreateDynamicFieldPath();
			public abstract String getAllDynamicFieldPath();
			
		
		//Resident
		public abstract String getResidentGenerateVID(); 
		public abstract String getUserIdPwd();
		public abstract void setUserIdPwd(String userIdPwd);
		public abstract void setResidentGenerateVID(String residentGenerateVID);
		public abstract String getResidentRevokeVID();
		public abstract void setResidentRevokeVID(String residentRevokeVID);
		public abstract String getResidentAuthHistory();
		public abstract void setResidentAuthHistory(String residentAuthHistory);
		public abstract String getResidentAuthLock();
		public abstract void setResidentAuthLock(String residentAuthLock);
		public abstract String getResidentAuthUnlock();
		public abstract void setResidentAuthUnlock(String residentAuthUnlock);
		public abstract String getResidenteUin();
		public abstract void setResidenteUin(String residenteUin);
		public abstract String getResidentPrintUin();
		public abstract void setResidentPrintUin(String residentPrintUin);
		public abstract String getResidentUpdateUin();
		public abstract void setResidentUpdateUin(String residentUpdateUin);
		public abstract String getResidentCheckStatus();
		public abstract void setResidentCheckStatus(String residentCheckStatus);
		
		
		// Partner Management
		public abstract String getRegisterPartnerPath();
		public abstract void setRegisterPartnerPath(String registerPartnerPath);
		public abstract String getRetrievePartnerPath();
		public abstract String getUpdatePartnerPath();
		public abstract void setUpdatePartnerPath(String updatePartnerPath);
		public abstract String getSubmitPartnerApiKeyReqPath();
		public abstract void setSubmitPartnerApiKeyReqPath(String submitPartnerApiKeyReq);
		public abstract String getUpdatePartnerApikeyToPolicyMappingsPath();
		public abstract void setUpdatePartnerApikeyToPolicyMappingsPath(String updatePartnerApikeyToPolicyMappingsPath);
		public abstract void setApiKeyReqStatusPath(String apiKeyReqStatusPath);
		public abstract String getApiKeyReqStatusPath();
		public abstract String getActivatePartnerPath();
		public abstract void setActivateDeactivatePartnerAPIKeyPath(String activateDeactivatePartnerAPIKeyPath);
		public abstract String getActivateDeactivatePartnerAPIKeyPath();
		public abstract void setApproveRejectPartnerAPIKeyReqPath(String approveRejectPartnerAPIKeyReqPath);
		public abstract String getApproveRejectPartnerAPIKeyReqPath();
		public abstract String getRetrieveParticularPartnerDetailsForGivenPartnerIdPath();
		public abstract String getRetrieveTheRequestForPartnerAPIKeyForGivenRequestIdPath();
		public abstract String getRetrievePartnerAPIkeyToPolicyMappingsPath();
		
		public abstract String getCreatePolicyGroupPath();
		public abstract void setCreatePolicyGroupPath(String createPolicyGroupPath);
		public abstract String getGetPolicyBasedOnPolicyIdPath();
		public abstract String getRetrievePartnerPolicyDetailsForGivenPartnerAPIKeyPath();
		public abstract String getUpdateExistingPolicyForPolicyGroupPath();
		public abstract void setUpdateExistingPolicyForPolicyGroupPath(String updateExistingPolicyForPolicyGroupPath);
		public abstract void setUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath(String updateTheStatusActivateDeactivateForTheGivenPolicyIdPath);
		public abstract String getUpdateTheStatusActivateDeactivateForTheGivenPolicyIdPath();
		
		
		public abstract String getCreateMISPPath();
		public abstract void setUpdateMISPPath(String updateMISPPath);
		public abstract String getUpdateMISPPath();
		public abstract String getValidateMISPLicensePath();
		public abstract String getApproveMISPPath();
		public abstract String getRejectMISPPath();
		public abstract String getActivateMISPLicense();
		public abstract String getRetrieveMISPByMispIDPath();
		public abstract String getRetrieveMISPsDetailsByGivenNamePath();
		public abstract void setUpdateMispStatusByMispIdPath(String updateMispStatusByMispIdPath);
		public abstract String getUpdateMispStatusByMispIdPath();
		public abstract String getDownloadMispLicenseKeyPath();
		
}
