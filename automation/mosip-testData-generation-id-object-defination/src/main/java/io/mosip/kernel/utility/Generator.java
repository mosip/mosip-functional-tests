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
	 * @param gender
	 * @param fieldName
	 * @param prop
	 * @return
	 * 		return the random name based on gender
	 */
	public String randomName(ClientInformationDTO clientInformationDTO, Random random, String gender, String fieldName, Properties prop) {
		String name=null;
		switch (prop.getProperty(fieldName)) {
		case "valid":
			if(gender.equals("Male")) {
				String valid_nameArray[]=clientInformationDTO.getValid_Male_Name();
				name = valid_nameArray[random.nextInt(valid_nameArray.length)];
			}
			else if(gender.equals("Female")){
				String valid_nameArray[]=clientInformationDTO.getValid_Female_Name();
				name = valid_nameArray[random.nextInt(valid_nameArray.length)];
			}
			break;
		case "invalid":
			String invalid_nameArray[]=clientInformationDTO.getInvalid_Name();
			name = invalid_nameArray[random.nextInt(invalid_nameArray.length)];

		default:
			break;
		}
		return name;
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
	public String randomEmailId(ClientInformationDTO clientInformationDTO, Random random, Properties prop, ArrayList<String> outputPropertyList, String firstName, String lastName) {
		String emailId=null;
	
		switch(prop.getProperty("emailId")) {
		case "valid":
			if(outputPropertyList.contains("firstName") && outputPropertyList.contains("lastName") && prop.getProperty("firstName").equals("valid") && prop.getProperty("lastName").equals("valid")) 
				emailId = firstName+"."+lastName+"@mydomain.com";
			else {
				String valid_nameArray[]=clientInformationDTO.getValid_Male_Name();
				 firstName=valid_nameArray[random.nextInt(valid_nameArray.length)];
				 lastName=valid_nameArray[random.nextInt(valid_nameArray.length)];
				 emailId = firstName+"."+lastName+"@mydomain.com";
			}
			break;
		case "invalid":
			String invalid_emailId[]=clientInformationDTO.getInvalid_emailId();
			emailId = invalid_emailId[random.nextInt(invalid_emailId.length)];
			break;
		default:
			System.out.println("Invalid case for Email id ");
			System.exit(0);
		}
		return emailId;
	}
	public String randomLeftEye() {
		System.out.println("left eye generated");
		return null;
	}
	public String randomLeftSlap() {
		System.out.println("Left slap generated");
		return null;
	}
	
	
	public String randomRightEye() {
		System.out.println("right eye generated");
		return null;
	}
	public String randomRightSlap() {
		System.out.println("right slap generated");
		return null;
	}
	public String randomThumbs() {
		System.out.println("random thumbs");
		return null;
	}
	

}
