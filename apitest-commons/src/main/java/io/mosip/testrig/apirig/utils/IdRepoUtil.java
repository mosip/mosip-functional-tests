package io.mosip.testrig.apirig.utils;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;

/**
 * Class to store all the UIN data or json using idrepo
 * 
 * @author Athila
 *
 */
public class IdRepoUtil extends AuthTestsUtil {

	private static final Logger IDREPO_UTILITY_LOGGER = Logger.getLogger(IdRepoUtil.class);

	/**
	 * Get the uin data or json using idrepo api and save it in output file
	 * 
	 * @param uinNumber
	 * @return true or false
	 */
	public static boolean retrieveIdRepo(String uinNumber) {
		String retrievePath = RunConfigUtil.objRunConfig.getIdRepoRetrieveDataPath().replace("$uin$", uinNumber);
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + retrievePath;
		String cookieValue = getAuthorizationCookie(getCookieRequestFilePathForResidentAuth(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
		if (!FileUtil.checkFileExistForIdRepo(uinNumber + ".json")) {
			IDREPO_UTILITY_LOGGER.info("To retrieve identity Sending get request to: " + url);
			if (FileUtil.createAndWriteFileForIdRepo(uinNumber + ".json",
					getResponseWithCookie(url + "?type=all", AuthTestsUtil.AUTHORIZATHION_COOKIENAME, cookieValue)))
				return true;
			else
				return false;
		}
		return true;
	}

	/**
	 * Get field data for the key from saved uin data or json
	 * 
	 * @param mapping
	 * @param uinNumber
	 * @return uin data or json
	
	 * Generate uin number using generate_uin api
	 * 
	 * @return UIN Number
	 */
	public static String generateUinNumberForIda() {
		return JsonPrecondtion
				.getValueFromJson(
						getResponseWithCookieForIdaUinGenerator(RunConfigUtil.objRunConfig.getEndPointUrl()
								+ RunConfigUtil.objRunConfig.getGenerateUINPath(), AUTHORIZATHION_COOKIENAME),
						"response.uin");
	}

	/**
	 * Generate uin number using generate_uin api
	 * 
	 * @return UIN Number
	 */
	public static String generateUinNumberForIdRepo() {
		return JsonPrecondtion
				.getValueFromJson(
						getResponseWithCookieForIdRepoUinGenerator(RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
								+ RunConfigUtil.objRunConfig.getGenerateUINPath(), AUTHORIZATHION_COOKIENAME),
						"response.uin");
	}

	/**
	 * Get Create UIN api Path for the generated uin number
	 * 
	 * @param UinNumber
	 * @return create uin path
	 */
	public static String getCreateUinPath() {
		return RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoCreateUINRecordPath();
	}

	

	/**
	 * Get create VID api path
	 *
	 * @return url
	 */
	public static String getCreateVidPath() {
		 return RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoCreateVIDRecordPath();
		
	}

	/**
	 * Get update VID api path
	 * 
	 * @param vid number
	 * @return url
	 */
	public static String getUpdateVidStatusPath(String vidNumer) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoUpdateVIDStatusPath();
		url = url.replace(GlobalConstants.$VID$, vidNumer);
		return url;
	}

	/**
	 * Get Retrieve VID api path
	 * 
	 * @param vid number
	 * @return url
	 */
	public static String getRetrieveUINByVIDPath(String vidNumer) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRetrieveUINByVIDPath();
		url = url.replace(GlobalConstants.$VID$, vidNumer);
		return url;
	}

	/**
	 * Get Retrieve Identity api path
	 * 
	 * @param uin number
	 * @return url
	 */
	public static String getRetrieveIdentityByUIN(String uinNumer) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRetrieveDataPath();
		url = url.replace(GlobalConstants.$UIN$, uinNumer);
		return url;
	}

	/**
	 * Get Retrieve Identity api path
	 * 
	 * @param uin number
	 * @return url
	 */
	public static String getRetrieveIdentityByRid(String ridNumer) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRetrieveIdentityByRid();
		url = url.replace(GlobalConstants.$RID$, ridNumer);
		return url;
	}

	/**
	 * Get Retrieve Identity api path
	 * 
	 * @param uin number
	 * @return url
	 */
	public static String getRegenerateVid(String vidNumer) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRetrieveIdentityByRid();
		url = url.replace(GlobalConstants.$VID$, vidNumer);
		return url;
	}

	public static String getIdrepoUinRefIdQuery(String regId) {
		return "select uin_ref_id from idrepo.uin where reg_id='" + regId + "'";
	}

	public static String deleteIdrepoBiometricQuery(String uinRefId) {
		return "delete from idrepo.uin_biometric where uin_ref_id='" + uinRefId + "'";
	}

	public static String deleteIdrepoUin(String uinRefId) {
		return "delete from idrepo.uin where uin_ref_id='" + uinRefId + "'";
	}

}
