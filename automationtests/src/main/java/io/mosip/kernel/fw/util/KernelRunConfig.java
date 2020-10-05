package io.mosip.kernel.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.dto.ErrorsDto;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class KernelRunConfig extends RunConfig {
	private Logger logger = Logger.getLogger(KernelRunConfig.class);
	private String testDataPath;
	private String kernelEndPointUrl;
	private String scenarioPath;
	private String testType;
	private String testDataFolderName;
	private String moduleFolderName;
	private String fetchGenderTypePath;

    

	
	@Override
	public void setConfig(String testDataPath, String testDataFileName, String testType) {
		setKernelEndPointUrl(KernelTestUtil.getPropertyValue("kernelEndPointUrl"));
		setTestDataPath(testDataPath);
		File testDataFilePath = new File(RunConfigUtil.getResourcePath() + testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath, testDataPath);
		setTestType(RunConfigUtil.getTestLevel());
		setFetchGenderTypePath(KernelTestUtil.getPropertyValue("fetchGenderTypePath"));
	}

	private void setFilePathFromTestdataFileName(File filePath, String testDataPath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "/" + folderList[i];
		}
		String testDataFolderName = "";
		String moduleFolderName = "";
		if (testDataPath.contains("\\")) {
			String[] list = testDataPath.split(Pattern.quote("\\\\"));
			testDataFolderName = list[1];
		} else if (testDataPath.contains("/")) {
			String[] list = testDataPath.split(Pattern.quote("/"));
			moduleFolderName = list[0];
			testDataFolderName = list[1];
		}
		setTestDataFolderName(testDataFolderName);
		setModuleFolderName(moduleFolderName);
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
		loadErrorsData(getErrorsConfigPath());
	}

	/**
	 * The method load yml error test data
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private void loadErrorsData(String path) {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
			ErrorsDto.setErrors((Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public String getEndPointUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEndPointUrl(String endPointUrl) {
		// TODO Auto-generated method stub
		
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
	public String getEncryptUtilBaseUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncryptUtilBaseUrl(String encryptUtilBaseUrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEncryptionPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncryptionPath(String encryptionPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEncodePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncodePath(String encodePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDecodePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDecodePath(String decodePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getScenarioPath() {
		// TODO Auto-generated method stub
		return this.scenarioPath;
	}

	@Override
	public void setScenarioPath(String scenarioPath) {
		// TODO Auto-generated method stub
		this.scenarioPath = scenarioPath;
	}

	@Override
	public String getSrcPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSrcPath(String srcPath) {
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
	public String getTestDataPath() {
		// TODO Auto-generated method stub
		return this.testDataPath;
	}

	@Override
	public void setTestDataPath(String testDataPath) {
		// TODO Auto-generated method stub
		this.testDataPath = testDataPath;
	}

	@Override
	public String getUserDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserDirectory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoEndPointUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoEndPointUrl(String idRepoEndPointUrl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTestType() {
		// TODO Auto-generated method stub
		return this.testType;
	}

	@Override
	public void setTestType(String testType) {
		// TODO Auto-generated method stub
		this.testType = testType;
	}

	@Override
	public String getGenerateUINPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenerateUINPath(String generateUINPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoRetrieveDataPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoCreateUINRecordPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStoreUINDataPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStoreUINDataPath(String storeUINDataPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEncodeFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncodeFilePath(String encodeFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDecodeFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDecodeFilePath(String decodeFilePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getVidGenPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVidGenPath(String vidGenPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTestDataFolderName() {
		// TODO Auto-generated method stub
		return this.testDataFolderName;
	}

	@Override
	public void setTestDataFolderName(String testDataFolderName) {
		// TODO Auto-generated method stub
		this.testDataFolderName = testDataFolderName;
	}

	@Override
	public String getAuthVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthVersion(String authVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getErrorsConfigPath() {
		// TODO Auto-generated method stub
		return "kernel/TestData/RunConfig/kernelErrorCodeMsg.yml";
	}

	@Override
	public String getClientidsecretkey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClientidsecretkey(String clientidsecretkey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getModuleFolderName() {
		// TODO Auto-generated method stub
		return this.moduleFolderName;
	}

	@Override
	public void setModuleFolderName(String moduleFolderName) {
		// TODO Auto-generated method stub
		this.moduleFolderName = moduleFolderName;
	}

	@Override
	public String getGenerateVIDPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getFetchGenderTypePath() {
		return fetchGenderTypePath;
	}

	public void setFetchGenderTypePath(String fetchGenderTypePath) {
		this.fetchGenderTypePath = fetchGenderTypePath;
	}


	@Override
	public void setGenerateVIDPath(String generateVIDPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoCreateVIDRecordPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoUpdateVIDStatusPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoUpdateVIDStatusPath(String IdRepoUpdateVIDStatusPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoVersion(String idRepoVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDecryptPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDecryptPath(String decryptPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUinIdentityMapper(String uinIdentityMapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUinIdentityMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInternalEncryptionPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInternalEncryptionPath(String internalEncryptionPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValidateSignaturePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValidateSignaturePath(String validateSignaturePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEncryptionPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncryptionPort(String encryptionPort) {
		// TODO Auto-generated method stub
		
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
	public String getIdRepoRetrieveUINByVIDPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoRetrieveUINByVIDPath(String idRepoRetrieveIdentityByRid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoRetrieveIdentityByRid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoRetrieveIdentityByRid(String idRepoRetrieveIdentityByRid) {
		// TODO Auto-generated method stub
		
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
	public String getIdaInternalOtpPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdaInternalOtpPath(String internalPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoDeactivateVIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoDeactivateVIDs(String idRepoDeactivateVIDs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdRepoReactivateVIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdRepoReactivateVIDs(String idRepoReactivateVIDs) {
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
	public String getAuthManagerUserIdPwd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthManagerUserIdPwd(String authManagerUserIdPwd) {
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
	public String getUpdateMachineTypePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateDeviceSpecificationPath() {
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
	public String getCreateDynamicFieldPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllDynamicFieldPath() {
		// TODO Auto-generated method stub
		return null;
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
	public String getRegisterPartnerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegisterPartnerPath(String registerPartnerPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRetrievePartnerPath() {
		// TODO Auto-generated method stub
		return null;
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
	public String getRetrievePartnerAPIkeyToPolicyMappingsPath() {
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
	public String getKernelEndPointUrl() {
		return kernelEndPointUrl;
	}

	public void setKernelEndPointUrl(String kernelEndPointUrl) {
		this.kernelEndPointUrl = kernelEndPointUrl;
	}

}
