package io.mosip.kernel.utility;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import io.mosip.kernel.dto.ClientInformationDTO;

/**
 * This class generate the random name,gender,mobilenumber,emailid... based on the input
 * @author Arjun chandramohan
 */
public class Generator {
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 *   return full name from master based on valid or invalid
	 */
	public String randomFullName(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String fullName=null;
		if(prop.getProperty("fullName").equals("valid")) {
			String valid_gender[]=clientInformationDTO.getValid_gender();
			fullName = valid_gender[random.nextInt(valid_gender.length)];
		}
		else {
			String invalid_gender[]=clientInformationDTO.getInvalid_gender();
			fullName = invalid_gender[random.nextInt(invalid_gender.length)];
		}
		return fullName;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 *  return DOB from utility if valid else return invalid DOB from master data
	 */
	public String randomDOB(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String DOB=null;
		if(prop.getProperty("dateOfBirth").equals("valid")) {
			DOB = new DOBGenerator().randomDOBGenerator("dd/mm/yyyy", prop);
		}
		else {
			String invalid_DOB[]=clientInformationDTO.getInvalid_DOB();
			DOB = invalid_DOB[random.nextInt(invalid_DOB.length)];
		}
		return DOB;
	}
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 *  return age from master based in valid or invalid
	 */
	public String randomAge(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String age=null;
		if(prop.getProperty("Age").equals("valid")) {
			String valid_Age[]=clientInformationDTO.getValid_Age();
			age = valid_Age[random.nextInt(valid_Age.length)];
		}
		else {
			String invalid_age[]=clientInformationDTO.getInvalid_Age();
			age = invalid_age[random.nextInt(invalid_age.length)];
		}
		return age;
	}

	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return random gender
	 */
	public String randomGender(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String gender=null;
		if(prop.getProperty("gender").equals("valid")) {
			String valid_gender[]=clientInformationDTO.getValid_gender();
			gender = valid_gender[random.nextInt(valid_gender.length)];
		}
		else {
			String invalid_gender[]=clientInformationDTO.getInvalid_gender();
			gender = invalid_gender[random.nextInt(invalid_gender.length)];
		}
		return gender;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		returns random mobile Number
	 */
	public String randomMobileNumber(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String mobileNumber=null;
		if(prop.getProperty("mobileNumber").equals("valid")) {
			String valid_mobileNumber[]=clientInformationDTO.getValid_mobileNumber();
			mobileNumber = valid_mobileNumber[random.nextInt(valid_mobileNumber.length)];
		}
		else {
			String invalid_mobileNumber[]=clientInformationDTO.getInvalid_mobileNumber();
			mobileNumber = invalid_mobileNumber[random.nextInt(invalid_mobileNumber.length)];
		}
		return mobileNumber;
	}

	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return the random address
	 */
	public String randomAddressLine1(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String addressLine1=null;
		if(prop.getProperty("addressLine1").equals("valid")) {
			String valid_addressLine1[]=clientInformationDTO.getValid_addressLine1();
			addressLine1 = valid_addressLine1[random.nextInt(valid_addressLine1.length)];
		}
		else {
			String invalid_addressLine1[]=clientInformationDTO.getInvalid_addressLine1();
			addressLine1 = invalid_addressLine1[random.nextInt(invalid_addressLine1.length)];
		}
		return addressLine1;
	}
	public String randomAddressLine2(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String addressLine2=null;
		if(prop.getProperty("addressLine2").equals("valid")) {
			String valid_addressLine2[]=clientInformationDTO.getValid_addressLine2();
			addressLine2 = valid_addressLine2[random.nextInt(valid_addressLine2.length)];
		}
		else {
			String invalid_addressLine2[]=clientInformationDTO.getInvalid_addressLine2();
			addressLine2 = invalid_addressLine2[random.nextInt(invalid_addressLine2.length)];
		}
		return addressLine2;
	}
	public String randomAddressLine3(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String addressLine3=null;
		if(prop.getProperty("addressLine3").equals("valid")) {
			String valid_addressLine3[]=clientInformationDTO.getValid_addressLine3();
			addressLine3 = valid_addressLine3[random.nextInt(valid_addressLine3.length)];
		}
		else {
			String invalid_addressLine3[]=clientInformationDTO.getInvalid_addressLine3();
			addressLine3 = invalid_addressLine3[random.nextInt(invalid_addressLine3.length)];
		}
		return addressLine3;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return the random region
	 */
	public String randomRegion(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String region=null;
		if(prop.getProperty("region").equals("valid")) {
			String valid_region[]=clientInformationDTO.getValid_region();
			region = valid_region[random.nextInt(valid_region.length)];
		}
		else {
			String invalid_region[]=clientInformationDTO.getInvalid_region();
			region = invalid_region[random.nextInt(invalid_region.length)];
		}
		return region;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return the random city
	 */
	public String randomCity(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String city=null;
		if(prop.getProperty("city").equals("valid")) {
			String valid_city[]=clientInformationDTO.getValid_city();
			city = valid_city[random.nextInt(valid_city.length)];
		}
		else {
			String invalid_city[]=clientInformationDTO.getInvalid_city();
			city = invalid_city[random.nextInt(invalid_city.length)];
		}
		return city;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return the random province
	 */
	public String randomProvince(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String province=null;
		if(prop.getProperty("province").equals("valid")) {
			String valid_province[]=clientInformationDTO.getValid_province();
			province = valid_province[random.nextInt(valid_province.length)];
		}
		else if(prop.getProperty("province").equals("invalid")){
			String invalid_province[]=clientInformationDTO.getInvalid_province();
			province = invalid_province[random.nextInt(invalid_province.length)];
		}
		return province;
	}
	
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return random CNEorPIN number
	 */
	public String randomCNEOrPINNumber(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String CNEOrPINNumber=null;
		if(prop.getProperty("CNEOrPINNumber").equals("valid")) {
			String valid_CNEOrPINNumber[]=clientInformationDTO.getValid_CNEOrPINNumber();
			CNEOrPINNumber = valid_CNEOrPINNumber[random.nextInt(valid_CNEOrPINNumber.length)];
		}
		else {
			String invalid_CNEOrPINNumber[]=clientInformationDTO.getInvalid_CNEOrPINNumber();
			CNEOrPINNumber = invalid_CNEOrPINNumber[random.nextInt(invalid_CNEOrPINNumber.length)];
		}
		return CNEOrPINNumber;
	}
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return the random ParentOrGuardianRIDOrUIN
	 */
	public String randomParentOrGuardianRIDOrUIN(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String parentOrGuardianRIDOrUIN=null;
		if(prop.getProperty("parentOrGuardianRIDOrUIN").equals("valid")) {
			String valid_parentOrGuardianRIDOrUIN[]=clientInformationDTO.getValid_parentOrGuardianRIDOrUIN();
			parentOrGuardianRIDOrUIN = valid_parentOrGuardianRIDOrUIN[random.nextInt(valid_parentOrGuardianRIDOrUIN.length)];
		}
		else {
			String invalid_parentOrGuardianRIDOrUIN[]=clientInformationDTO.getInvalid_parentOrGuardianRIDOrUIN();
			parentOrGuardianRIDOrUIN = invalid_parentOrGuardianRIDOrUIN[random.nextInt(invalid_parentOrGuardianRIDOrUIN.length)];
		}
		return parentOrGuardianRIDOrUIN;
	}
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @return
	 * 		return random LocalAdministrativeAuthority
	 */
	public String randomLocalAdministrativeAuthority(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String localAdministrativeAuthority=null;
		if(prop.getProperty("localAdministrativeAuthority").equals("valid")) {
			String valid_localAdministrativeAuthority[]=clientInformationDTO.getValid_localAdministrativeAuthority();
			localAdministrativeAuthority = valid_localAdministrativeAuthority[random.nextInt(valid_localAdministrativeAuthority.length)];
		}
		else {
			String invalid_localAdministrativeAuthority[]=clientInformationDTO.getInvalid_localAdministrativeAuthority();
			localAdministrativeAuthority = invalid_localAdministrativeAuthority[random.nextInt(invalid_localAdministrativeAuthority.length)];
		}
		return localAdministrativeAuthority;
	}
	/**
	 * @param clientInformationDTO
	 * @param random
	 * @param prop
	 * @param outputPropertyList 
	 * @param firstName
	 * @param lastName
	 * @return
	 * 		return the email is a combination of first name and last name
	 */
	public String randomEmailId(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String emailId=null;
		if(prop.getProperty("emailId").equals("valid")) {
			String valid_emailId[]=clientInformationDTO.getValid_emailId();
			emailId = valid_emailId[random.nextInt(valid_emailId.length)];
		}
		else {
			String invalid_emailId[]=clientInformationDTO.getInvalid_emailId();
			emailId = invalid_emailId[random.nextInt(invalid_emailId.length)];
		}
		
		return emailId;
	}
	
	public String randomPostalCode(ClientInformationDTO clientInformationDTO, Random random, Properties prop) {
		String postalcode=null;
		if(prop.getProperty("postalCode").equals("valid")) {
			String valid_postalCode[]=clientInformationDTO.getValid_postalCode();
			postalcode = valid_postalCode[random.nextInt(valid_postalCode.length)];
		}
		else {
			String invalid_postalCode[]=clientInformationDTO.getInvalid_CNEOrPINNumber();
			postalcode = invalid_postalCode[random.nextInt(invalid_postalCode.length)];
		}
		return postalcode;
	}
	
	

}
