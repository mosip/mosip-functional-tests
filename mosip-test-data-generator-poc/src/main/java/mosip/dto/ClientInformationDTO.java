package mosip.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * This is the dto class for fething objects from yaml file.
 * 
 * @author Arjun chandramohan
 *
 */
@Getter
@Setter
public class ClientInformationDTO {
	private String[] valid_firstName;
	private String[] invalid_firstName;

	private String[] valid_middleName;
	private String[] invalid_middleName;

	private String[] valid_lastName;
	private String[] invalid_lastName;

	private String[] valid_dateOfBirth;
	private String[] invalid_dateOfBirth;

	private String[] valid_gender;
	private String[] invalid_gender;

	private String[] valid_addressLine1;
	private String[] invalid_addressLine1;

	private String[] valid_addressLine2;
	private String[] invalid_addressLine2;

	private String[] valid_addressLine3;
	private String[] invalid_addressLine3;

	private String[] valid_region;
	private String[] invalid_region;

	private String[] valid_province;
	private String[] invalid_province;

	private String[] valid_city;
	private String[] invalid_city;

	private String[] valid_localAdministrativeAuthority;
	private String[] invalid_localAdministrativeAuthority;

	private String[] valid_mobileNumber;
	private String[] invalid_mobileNumber;

	private String[] valid_emailId;
	private String[] invalid_emailId;

	private String[] valid_CNEOrPINNumber;
	private String[] invalid_CNEOrPINNumber;

	private String[] valid_parentOrGuardianName;
	private String[] invalid_parentOrGuardianName;

	private String[] valid_parentOrGuardianRIDOrUIN;
	private String[] invalid_parentOrGuardianRIDOrUIN;

	private String[] valid_leftEye;
	private String[] invalid_leftEye;

	private String[] valid_rightEye;
	private String[] invalid_rightEye;

	private String[] valid_leftSlap;
	private String[] invalid_leftSlap;

	private String[] valid_rightSlap;
	private String[] invalid_rightSlap;

	private String[] valid_thumbs;
	private String[] invalid_thumbs;

}
