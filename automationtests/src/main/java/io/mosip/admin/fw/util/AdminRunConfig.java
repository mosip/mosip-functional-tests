package io.mosip.admin.fw.util;

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

public class AdminRunConfig extends RunConfig {
	private Logger logger = Logger.getLogger(AdminRunConfig.class);
	private String testDataPath;
	private String adminEndPointUrl;
	private String adminCreateRegCentrePath;
	private String scenarioPath;
	private String testType;
	private String testDataFolderName;
	private String moduleFolderName;
	private String searchValidDocumentPath;
	private String searchDocCategories;
	private String searchMachinePath;
	public  String deviceSearchPath;
	public  String deviceFilterPath;
	private String deviceSpecSearchPath;
	private String deviceSpecFilterPath;
	private String deviceTypeSearchPath;
	private String deviceTypeFilterPath;
	private String titleSearchPath;
	private String titleFilterPath;
	private String holidaySearchPath;
	private String FilterTempateDetailsPath;
	private String filterDocCategory;
	public String adminCreateRegistrationCentrePath;
	private String searchDocumentTypePath;
	public String adminMachineTypeSearchPath;
	private String searchBlackListedWordsPath;
	public String adminIndividualTypesSearch;
	public String adminUpdateRegistrationCentrePath;
	public String adminRegistrationCentreSearchPath;

	public String adminRegistrationCentreFilterPath;
	public String adminSearchTemplateDetails;
	public String adminGenderSearchPath;
	public String adminGenderFilterPath;

	public String MapDocumentCategoryAndDocumentType;
	private String filterBlackListedWordsPath;
	private String filterDocCatTypMapping;
	private String filterDocumentTypePath;
	private String filterMachinesPath;
	private String searchMachineSpec;
	private String filterMachineSpec;
	private String searchLocationPath;
	private String filterLocationPath;



	@Override
	public void setConfig(String testDataPath, String testDataFileName, String testType) {
		setAdminEndPointUrl(AdminTestUtil.getPropertyValue("adminEndpointUrl"));
		setAdminCreateRegCentrePath(AdminTestUtil.getPropertyValue("adminCreateRegCentrePath"));
		setAdminCreateRegistrationCentrePath(AdminTestUtil.getPropertyValue("adminCreateRegistrationCentrePath"));
		setAdminUpdateRegistrationCentrePath(AdminTestUtil.getPropertyValue("adminUpdateRegistrationCentrePath"));
		setAdminRegistrationCentreSearchPath(AdminTestUtil.getPropertyValue("adminRegistrationCentreSearchPath"));
		setTestDataPath(testDataPath);
		File testDataFilePath = new File(RunConfigUtil.getResourcePath() + testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath, testDataPath);
		setTestType(RunConfigUtil.getTestLevel());
		setSearchMachinePath(AdminTestUtil.getPropertyValue("searchMachinePath"));
		setDeviceSearchPath(AdminTestUtil.getPropertyValue("deviceSearchPath"));
		setDeviceFilterPath(AdminTestUtil.getPropertyValue("deviceFilterPath"));
		setSearchValidDocumentPath(AdminTestUtil.getPropertyValue("searchValidDocumentPath"));
		setDeviceSpecSearchPath(AdminTestUtil.getPropertyValue("deviceSpecSearchPath"));
		setDeviceSpecFilterPath(AdminTestUtil.getPropertyValue("deviceSpecFilterPath"));
		setDeviceTypeSearchPath(AdminTestUtil.getPropertyValue("deviceTypeSearchPath"));
		setDeviceTypeFilterPath(AdminTestUtil.getPropertyValue("deviceTypeFilterPath"));
		setTitleSearchPath(AdminTestUtil.getPropertyValue("titleSearchPath"));
		setHolidaySearchPath(AdminTestUtil.getPropertyValue("holidaySearchPath"));		
		setSearchDocumentTypePath(AdminTestUtil.getPropertyValue("searchDocumentTypePath"));
		setAdminMachineTypeSearchPath(AdminTestUtil.getPropertyValue("adminMachineTypeSearch"));
		setFilterDocCategory(AdminTestUtil.getPropertyValue("filterDocCategory"));
		setAdminIndividualTypesSearch(AdminTestUtil.getPropertyValue("adminIndividualTypesSearch"));
		setAdminSearchTemplateDetails(AdminTestUtil.getPropertyValue("adminSearchTemplateDetails"));
		setTitleFilterPath(AdminTestUtil.getPropertyValue("titleFilterPath"));
		setSearchBlackListedWords(AdminTestUtil.getPropertyValue("searchBlacklistedWord"));
		setSearchDocCategories(AdminTestUtil.getPropertyValue("searchDocCategories"));
		setAdminMachineTypeSearchPath(AdminTestUtil.getPropertyValue("adminIndividualTypesSearch"));

		setAdminGenderSearchPath(AdminTestUtil.getPropertyValue("adminGenderSearchPath"));
		setAdminGenderFilterPath(AdminTestUtil.getPropertyValue("adminGenderFilterPath"));
		setAdminRegistrationCentreFilterPath(AdminTestUtil.getPropertyValue("adminRegistrationCentreFilterPath"));

		setFilterTempateDetailsPath(AdminTestUtil.getPropertyValue("filterTemplateDetails"));
		setFilterBlackListedWordsPath(AdminTestUtil.getPropertyValue("filterBlackListedWords"));
		setFilterDocCatTypMapping(AdminTestUtil.getPropertyValue("filterDocCatTypMapping"));
		setFilterDocumentTypePath(AdminTestUtil.getPropertyValue("filterDocumentType"));
		setFilterMachinesPath(AdminTestUtil.getPropertyValue("filterMachinePath"));
		setSearchMachineSpec(AdminTestUtil.getPropertyValue("searchMachineSpec"));
		setFilterMachineSpec(AdminTestUtil.getPropertyValue("filterMachineSpec"));
		setSearchLocationDataPath(AdminTestUtil.getPropertyValue("searchLocationDataPath"));
		setFilterLocationPath(AdminTestUtil.getPropertyValue("filterLocationDataPath"));
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
	
	public String getAdminSearchTemplateDetails() {
		return adminSearchTemplateDetails;
	}

	public void setAdminSearchTemplateDetails(String adminSearchTemplateDetails) {
		this.adminSearchTemplateDetails = adminSearchTemplateDetails;
	}


	
	
	@Override
	public String getSearchDocCategories() {
		return searchDocCategories;
	}
	
	public String getAdminUpdateRegistrationCentrePath() {
		return adminUpdateRegistrationCentrePath;
	}

	public void setAdminUpdateRegistrationCentrePath(String adminUpdateRegistrationCentrePath) {
		this.adminUpdateRegistrationCentrePath = adminUpdateRegistrationCentrePath;
	}
	


	public String getAdminRegistrationCentreSearchPath() {
		return adminRegistrationCentreSearchPath;
	}

	public void setAdminRegistrationCentreSearchPath(String adminRegistrationCentreSearchPath) {
		this.adminRegistrationCentreSearchPath = adminRegistrationCentreSearchPath;
	}


	public String getAdminIndividualTypesSearch() {
		return adminIndividualTypesSearch;
	}

	public void setAdminIndividualTypesSearch(String adminIndividualTypesSearch) {
		this.adminIndividualTypesSearch = adminIndividualTypesSearch;
	}

	public void setSearchDocCategories(String searchDocCategories) {
		this.searchDocCategories = searchDocCategories;
	}
	public String getAdminMachineTypeSearchPath() {
		return adminMachineTypeSearchPath;
	}

	public void setAdminMachineTypeSearchPath(String adminMachineTypeSearchPath) {
		this.adminMachineTypeSearchPath = adminMachineTypeSearchPath;
	}

	public String getAdminCreateRegistrationCentrePath() {
		return adminCreateRegistrationCentrePath;
	}

	public void setAdminCreateRegistrationCentrePath(String adminCreateRegistrationCentrePath) {
		this.adminCreateRegistrationCentrePath = adminCreateRegistrationCentrePath;
	}
	
	@Override
	public String getSearchValidDocumentPath() {
		return searchValidDocumentPath;
	}

	public void setSearchValidDocumentPath(String searchValidDocumentPath) {
		this.searchValidDocumentPath = searchValidDocumentPath;
	}
	@Override
	public String getModuleFolderName() {
		// TODO Auto-generated method stub
		return this.moduleFolderName;
	}

	@Override
	public void setModuleFolderName(String moduleFolderName) {
		this.moduleFolderName = moduleFolderName;

	}
	@Override
	public String getErrorsConfigPath() {

		return "admin/TestData/RunConfig/adminErrorCodeMsg.yml";
	}

	@Override
	public String getTestType() {
		// TODO Auto-generated method stub
		return this.testType;
	}

	@Override
	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getAdminEndPointUrl() {
		return adminEndPointUrl;
	}

	public void setAdminEndPointUrl(String adminEndPointUrl) {
		this.adminEndPointUrl = adminEndPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}

	public String getAdminCreateRegCentrePath() {
		return adminCreateRegCentrePath;
	}

	
	public void setAdminCreateRegCentrePath(String adminCreateRegCentrePath) {
		this.adminCreateRegCentrePath = adminCreateRegCentrePath;
	}

	@Override
	public String getScenarioPath() {
		// TODO Auto-generated method stub
		return this.scenarioPath;
	}

	@Override
	public void setScenarioPath(String scenarioPath) {
		this.scenarioPath = scenarioPath;

	}

	@Override
	public String getTestDataPath() {
		// TODO Auto-generated method stub
		return this.testDataPath;
	}

	@Override
	public void setTestDataPath(String testDataPath) {
		this.testDataPath = testDataPath;
	}

	@Override
	public String getTestDataFolderName() {
		// TODO Auto-generated method stub
		return this.testDataFolderName;
	}

	@Override
	public void setTestDataFolderName(String testDataFolderName) {
		this.testDataFolderName = testDataFolderName;

	}
	
	@Override
	public String getSearchDocumentTypePath() {
		return searchDocumentTypePath;
	}
	@Override
	public void setSearchDocumentTypePath(String searchDocumentTypePath) {
		this.searchDocumentTypePath = searchDocumentTypePath;
	}
	@Override
	public String getSearchBlackListedWords() {
		return searchBlackListedWordsPath;
	}
	@Override
	public void setSearchBlackListedWords(String searchBlackListedWordsPath) {
		this.searchBlackListedWordsPath = searchBlackListedWordsPath;
	}
	@Override
	public String getFilterBlackListedWordsPath() {
		return filterBlackListedWordsPath;
	}
	@Override
	public void setFilterBlackListedWordsPath(String filterBlackListedWordsPath) {
		this.filterBlackListedWordsPath = filterBlackListedWordsPath;
	}
	@Override
	public String getFilterDocumentTypePath() {
		return filterDocumentTypePath;
	}
	@Override
	public void setFilterDocumentTypePath(String filterDocumentTypePath) {
		this.filterDocumentTypePath = filterDocumentTypePath;
	}
	@Override
	public String getFilterMachinesPath() {
		return filterMachinesPath;
	}
	@Override
	public void setFilterMachinesPath(String filterMachinesPath) {
		this.filterMachinesPath = filterMachinesPath;
	}
	@Override
	public String getSearchLocationDataPath() {
		return searchLocationPath;
	}
	@Override
	public void setSearchLocationDataPath(String searchLocationPath) {
		this.searchLocationPath = searchLocationPath;
	}
	@Override
	public String getFilterLocationPath() {
		return filterLocationPath;
	}
	@Override
	public void setFilterLocationPath(String filterLocationPath) {
		this.filterLocationPath = filterLocationPath;
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
	public String getUserDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserDirectory() {

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
	public String getAuthVersion() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void setAuthVersion(String authVersion) {
		// TODO Auto-generated method stub

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
		return "";
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
	public String getDeviceSearchPath() {
	    return deviceSearchPath;
	}

	@Override
	public void setDeviceSearchPath(String deviceSearchPath) {
		this.deviceSearchPath=deviceSearchPath;
		
	}

	@Override
	public String getSearchMachinePath() {
		return searchMachinePath;
	}

	@Override
	public void setSearchMachinePath(String  searchMachinePath) {
		this.searchMachinePath=searchMachinePath;
		
	}

	@Override
	public String getDeviceFilterPath() {
		return deviceFilterPath;
	}

	@Override
	public void setDeviceFilterPath(String deviceFilterPath) {
		this.deviceFilterPath=deviceFilterPath;		
	}

	@Override
	public String getDeviceSpecSearchPath() {
		return deviceSpecSearchPath;
	}

	@Override
	public void setDeviceSpecSearchPath(String deviceSpecSearchPath) {
		this.deviceSpecSearchPath=deviceSpecSearchPath;
		
	}

	@Override
	public String getDeviceTypeSearchPath() {
		return this.deviceTypeSearchPath;
	}

	@Override
	public void setDeviceTypeSearchPath(String deviceTypeSearchPath) {
		this.deviceTypeSearchPath = deviceTypeSearchPath;
		
	}

	@Override
	public String getTitleSearchPath() {
		return this.titleSearchPath;
	}

	@Override
	public void setTitleSearchPath(String titleSearchPath) {
		this.titleSearchPath = titleSearchPath;		
	}

	@Override
	public String getTitleFilterPath() {
		return titleFilterPath;
	}

	@Override
	public void setTitleFilterPath(String titleFilterPath) {
		this.titleFilterPath=titleFilterPath;
	}

	public String getAdminIndividualTypesSearchPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminIndividualTypesSearchPath(String adminMachineTypeSearchPath) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setHolidaySearchPath(String holidaySearchPath) {
		this.holidaySearchPath=holidaySearchPath;
		
	}

	@Override
	public String getAdminSearchTemplateDetailsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdminSearchTemplateDetailsPath(String adminSearchTemplateDetailsPath) {
		
	}

	@Override
	public String getHolidaySearchPath() {
		return holidaySearchPath;
	}
	@Override
	public String getFilterTempateDetailsPath() {
		return FilterTempateDetailsPath;
	}
	public String getFilterDocCategory() {
		return filterDocCategory;
	}

	@Override
	public String getDeviceTypeFilterPath() {
		return deviceTypeFilterPath;
	}

	@Override
	public void setDeviceTypeFilterPath(String deviceTypeFilterPath) {
		this.deviceTypeFilterPath=deviceTypeFilterPath;
		
	}

	public void setFilterDocCategory(String filterDocCategory) {
		this.filterDocCategory = filterDocCategory;
	}




	public String getAdminGenderSearchPath() {
		return adminGenderSearchPath;
	}

	
	public void setAdminGenderSearchPath(String adminGenderSearchPath) {
		this.adminGenderSearchPath = adminGenderSearchPath;
	}
	
	public String getAdminGenderFilterPath() {
		return adminGenderFilterPath;
	}

	public void setAdminGenderFilterPath(String adminGenderFilterPath) {
		this.adminGenderFilterPath = adminGenderFilterPath;
	}
	

	public String getAdminRegistrationCentreFilterPath() {
		return adminRegistrationCentreFilterPath;
	}

	public void setAdminRegistrationCentreFilterPath(String adminRegistrationCentreFilterPath) {
		this.adminRegistrationCentreFilterPath = adminRegistrationCentreFilterPath;
	}

	

	@Override
	public String getDeviceSpecFilterPath() {
		return deviceSpecFilterPath;
	}

	@Override
	public void setDeviceSpecFilterPath(String deviceSpecFilterPath) {
		this.deviceSpecFilterPath=deviceSpecFilterPath;
		
	}
	public void setFilterTempateDetailsPath(String filterTempateDetailsPath) {
		FilterTempateDetailsPath = filterTempateDetailsPath;
	}
	@Override
	public String getFilterDocCatTypMapping() {
		return filterDocCatTypMapping;
	}

	public void setFilterDocCatTypMapping(String filterDocCatTypMapping) {
		this.filterDocCatTypMapping = filterDocCatTypMapping;
	}
	public String getMapDocumentCategoryAndDocumentType() {
		return MapDocumentCategoryAndDocumentType;
	}

	public void setMapDocumentCategoryAndDocumentType(String mapDocumentCategoryAndDocumentType) {
		MapDocumentCategoryAndDocumentType = mapDocumentCategoryAndDocumentType;
	}

	@Override
	public String MapDocumentCategoryAndDocumentType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getSearchMachineSpec() {
		return searchMachineSpec;
	}

	public void setSearchMachineSpec(String searchMachineSpec) {
		this.searchMachineSpec = searchMachineSpec;
	}
	@Override
	public String getFilterMachineSpec() {
		return filterMachineSpec;
	}

	public void setFilterMachineSpec(String filterMachineSpec) {
		this.filterMachineSpec = filterMachineSpec;
	}

}
