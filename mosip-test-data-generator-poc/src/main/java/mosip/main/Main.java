package mosip.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import mosip.dto.ClientInformationDTO;
import mosip.helper.MoveingFileToAnotherDirectory;
import mosip.helper.PacketCreation;

/**
 * This is the application starter class for test data generation.
 * 
 * @author Arjun chandramohan
 *
 */
public class Main {
	
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the input data:");
		String inputValue=scanner.next();
		String result=Main.createTestData(inputValue);
		System.out.println(result);
		scanner.close();
	}
	
	public static String createTestData(String inputValue) {
		String firstName = null,middleName=null,lastName = null,dateOfBirth=null,gender=null,addressLine1=null,addressLine2=null,addressLine3=null,
				region=null,province=null,city=null,localAdministrativeAuthority=null,emailId = null,mobileNumber = null,CNEOrPINNumber=null,
				parentOrGuardianName=null,parentOrGuardianRIDOrUIN=null,leftEye=null,rightEye=null,leftSlap=null,rightSlap=null,thumbs=null;
		int noOfJsonOutput=0;

		
		Random random=new Random();
		
		try {
			noOfJsonOutput = new Integer(inputValue.substring(6));
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid input");
			System.exit(0);
		}

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		ClientInformationDTO clientInformationDTO;
		try {
			clientInformationDTO = mapper.readValue(new File("src//main//resources//Name-Address.yml"), ClientInformationDTO.class);
			//clientInformationDTO = mapper.readValue(new File("..\\TestDataGeneration\\Resources\\MasterData\\Name-Address.yml"), ClientInformationDTO.class);
			String valid_firtNameArray[]=clientInformationDTO.getValid_firstName();
			String invalid_firtNameArray[]=clientInformationDTO.getInvalid_firstName();

			String valid_middleNameArray[]=clientInformationDTO.getValid_middleName();
			String invalid_middleNameArray[]=clientInformationDTO.getInvalid_middleName();

			String valid_lastNameArray[]=clientInformationDTO.getValid_lastName();
			String invalid_lastNameArray[]=clientInformationDTO.getInvalid_lastName();

			String valid_dateOfBirthArray[]=clientInformationDTO.getValid_dateOfBirth();
			String invalid_dateOfBirthArray[]=clientInformationDTO.getInvalid_dateOfBirth();

			String valid_genderArray[]=clientInformationDTO.getValid_gender();
			String invalid_genderArray[]=clientInformationDTO.getInvalid_gender();

			String valid_addressLine1Array[]=clientInformationDTO.getValid_addressLine1();
			String invalid_addressLine1Array[]=clientInformationDTO.getInvalid_addressLine1();

			String valid_addressLine2Array[]=clientInformationDTO.getValid_addressLine2();
			String invalid_addressLine2Array[]=clientInformationDTO.getInvalid_addressLine2();

			String valid_addressLine3Array[]=clientInformationDTO.getValid_addressLine3();
			String invalid_addressLine3Array[]=clientInformationDTO.getInvalid_addressLine3();

			String valid_regionArray[]=clientInformationDTO.getValid_region();
			String invalid_regionArray[]=clientInformationDTO.getInvalid_region();

			String valid_provinceArray[]=clientInformationDTO.getValid_province();
			String invalid_provinceArray[]=clientInformationDTO.getInvalid_province();

			String valid_cityArray[]=clientInformationDTO.getValid_city();
			String invalid_cityArray[]=clientInformationDTO.getInvalid_city();

			String valid_localAdministrativeAuthorityArray[]=clientInformationDTO.getValid_localAdministrativeAuthority();
			String invalid_localAdministrativeAuthorityArray[]=clientInformationDTO.getInvalid_localAdministrativeAuthority();

			String valid_mobileNumberArray[]=clientInformationDTO.getValid_mobileNumber();
			String invalid_mobileNumberArray[]=clientInformationDTO.getInvalid_mobileNumber();

			String valid_emailIdArray[]=clientInformationDTO.getValid_emailId();
			String invalid_emailIdArray[]=clientInformationDTO.getInvalid_emailId();

			String valid_CNEOrPINNumberArray[]=clientInformationDTO.getValid_CNEOrPINNumber();
			String invalid_CNEOrPINNumberArray[]=clientInformationDTO.getInvalid_CNEOrPINNumber();

			String valid_parentOrGuardianNameArray[]=clientInformationDTO.getValid_parentOrGuardianName();
			String invalid_parentOrGuardianNameArray[]=clientInformationDTO.getInvalid_parentOrGuardianName();

			String valid_parentOrGuardianRIDOrUINArray[]=clientInformationDTO.getValid_parentOrGuardianRIDOrUIN();
			String invalid_parentOrGuardianRIDOrUINArray[]=clientInformationDTO.getInvalid_parentOrGuardianRIDOrUIN();

			String valid_leftEyeArray[]=clientInformationDTO.getValid_leftEye();
			String invalid_leftEyeArray[]=clientInformationDTO.getInvalid_leftEye();

			String valid_rightEyeArray[]=clientInformationDTO.getValid_rightEye();
			String invalid_rightEyeArray[]=clientInformationDTO.getInvalid_rightEye();

			String valid_leftSlapArray[]=clientInformationDTO.getValid_leftSlap();
			String invalid_leftSlapArray[]=clientInformationDTO.getInvalid_leftSlap();

			String valid_rightSlapArray[]=clientInformationDTO.getValid_rightSlap();
			String invalid_rightSlapArray[]=clientInformationDTO.getInvalid_rightSlap();

			String valid_thumbsArray[]=clientInformationDTO.getValid_thumbs();
			String invalid_thumbsArray[]=clientInformationDTO.getInvalid_thumbs();






			for(int i=1;i<=noOfJsonOutput;i++) {
				PacketCreation.createFolder("D:\\TestDataGenerator\\"+inputValue+"\\"+"TestData"+i);
				switch(inputValue.substring(0,6)) {
				case "111100":{
					firstName = valid_firtNameArray[random.nextInt(valid_firtNameArray.length)];
					middleName=valid_middleNameArray[random.nextInt(valid_middleNameArray.length)];
					lastName = valid_lastNameArray[random.nextInt(valid_lastNameArray.length)];
					dateOfBirth=valid_dateOfBirthArray[random.nextInt(valid_dateOfBirthArray.length)];
					gender=valid_genderArray[random.nextInt(valid_genderArray.length)];
					addressLine1=valid_addressLine1Array[random.nextInt(valid_addressLine1Array.length)];
					addressLine2=valid_addressLine2Array[random.nextInt(valid_addressLine2Array.length)];
					addressLine3=valid_addressLine3Array[random.nextInt(valid_addressLine3Array.length)];
					region=valid_regionArray[random.nextInt(valid_regionArray.length)];
					province=valid_provinceArray[random.nextInt(valid_provinceArray.length)];
					city=valid_cityArray[random.nextInt(valid_cityArray.length)];
					localAdministrativeAuthority=valid_localAdministrativeAuthorityArray[random.nextInt(valid_localAdministrativeAuthorityArray.length)];
					mobileNumber=valid_mobileNumberArray[random.nextInt(valid_mobileNumberArray.length)];
					emailId=valid_emailIdArray[random.nextInt(valid_emailIdArray.length)];
					CNEOrPINNumber=valid_CNEOrPINNumberArray[random.nextInt(valid_CNEOrPINNumberArray.length)];
					parentOrGuardianName=valid_parentOrGuardianNameArray[random.nextInt(valid_parentOrGuardianNameArray.length)];
					parentOrGuardianRIDOrUIN=valid_parentOrGuardianRIDOrUINArray[random.nextInt(valid_parentOrGuardianRIDOrUINArray.length)];
					leftEye=valid_leftEyeArray[random.nextInt(valid_leftEyeArray.length)];
					rightEye=valid_rightEyeArray[random.nextInt(valid_rightEyeArray.length)];
					leftSlap=valid_leftSlapArray[random.nextInt(valid_leftSlapArray.length)];
					rightSlap=valid_rightSlapArray[random.nextInt(valid_rightSlapArray.length)];
					thumbs=valid_thumbsArray[random.nextInt(valid_thumbsArray.length)];

				}
				break;
				case "121100":{
					firstName = valid_firtNameArray[random.nextInt(valid_firtNameArray.length)];
					middleName=valid_middleNameArray[random.nextInt(valid_middleNameArray.length)];
					lastName = invalid_lastNameArray[random.nextInt(invalid_lastNameArray.length)];
					dateOfBirth=valid_dateOfBirthArray[random.nextInt(valid_dateOfBirthArray.length)];
					gender=valid_genderArray[random.nextInt(valid_genderArray.length)];
					addressLine1=valid_addressLine1Array[random.nextInt(valid_addressLine1Array.length)];
					addressLine2=valid_addressLine2Array[random.nextInt(valid_addressLine2Array.length)];
					addressLine3=valid_addressLine3Array[random.nextInt(valid_addressLine3Array.length)];
					region=valid_regionArray[random.nextInt(valid_regionArray.length)];
					province=valid_provinceArray[random.nextInt(valid_provinceArray.length)];
					city=valid_cityArray[random.nextInt(valid_cityArray.length)];
					localAdministrativeAuthority=valid_localAdministrativeAuthorityArray[random.nextInt(valid_localAdministrativeAuthorityArray.length)];
					mobileNumber=valid_mobileNumberArray[random.nextInt(valid_mobileNumberArray.length)];
					emailId=valid_emailIdArray[random.nextInt(valid_emailIdArray.length)];
					CNEOrPINNumber=valid_CNEOrPINNumberArray[random.nextInt(valid_CNEOrPINNumberArray.length)];
					parentOrGuardianName=valid_parentOrGuardianNameArray[random.nextInt(valid_parentOrGuardianNameArray.length)];
					parentOrGuardianRIDOrUIN=valid_parentOrGuardianRIDOrUINArray[random.nextInt(valid_parentOrGuardianRIDOrUINArray.length)];
					leftEye=valid_leftEyeArray[random.nextInt(valid_leftEyeArray.length)];
					rightEye=valid_rightEyeArray[random.nextInt(valid_rightEyeArray.length)];
					leftSlap=valid_leftSlapArray[random.nextInt(valid_leftSlapArray.length)];
					rightSlap=valid_rightSlapArray[random.nextInt(valid_rightSlapArray.length)];
					thumbs=valid_thumbsArray[random.nextInt(valid_thumbsArray.length)];
				}
				break;
				case "311100":{
					firstName = invalid_firtNameArray[random.nextInt(invalid_firtNameArray.length)];
					middleName=invalid_middleNameArray[random.nextInt(invalid_middleNameArray.length)];
					lastName = invalid_lastNameArray[random.nextInt(invalid_lastNameArray.length)];
					dateOfBirth=invalid_dateOfBirthArray[random.nextInt(invalid_dateOfBirthArray.length)];
					gender=invalid_genderArray[random.nextInt(invalid_genderArray.length)];
					addressLine1=invalid_addressLine1Array[random.nextInt(invalid_addressLine1Array.length)];
					addressLine2=invalid_addressLine2Array[random.nextInt(invalid_addressLine2Array.length)];
					addressLine3=invalid_addressLine3Array[random.nextInt(invalid_addressLine3Array.length)];
					region=invalid_regionArray[random.nextInt(invalid_regionArray.length)];
					province=invalid_provinceArray[random.nextInt(invalid_provinceArray.length)];
					city=invalid_cityArray[random.nextInt(invalid_cityArray.length)];
					localAdministrativeAuthority=invalid_localAdministrativeAuthorityArray[random.nextInt(invalid_localAdministrativeAuthorityArray.length)];
					mobileNumber=invalid_mobileNumberArray[random.nextInt(invalid_mobileNumberArray.length)];
					emailId=invalid_emailIdArray[random.nextInt(invalid_emailIdArray.length)];
					CNEOrPINNumber=invalid_CNEOrPINNumberArray[random.nextInt(invalid_CNEOrPINNumberArray.length)];
					parentOrGuardianName=invalid_parentOrGuardianNameArray[random.nextInt(invalid_parentOrGuardianNameArray.length)];
					parentOrGuardianRIDOrUIN=invalid_parentOrGuardianRIDOrUINArray[random.nextInt(invalid_parentOrGuardianRIDOrUINArray.length)];
					leftEye=valid_leftEyeArray[random.nextInt(valid_leftEyeArray.length)];
					rightEye=valid_rightEyeArray[random.nextInt(valid_rightEyeArray.length)];
					leftSlap=valid_leftSlapArray[random.nextInt(valid_leftSlapArray.length)];
					rightSlap=valid_rightSlapArray[random.nextInt(valid_rightSlapArray.length)];
					thumbs=valid_thumbsArray[random.nextInt(valid_thumbsArray.length)];
				}
				break;
				case "321100":{
					firstName = valid_firtNameArray[random.nextInt(valid_firtNameArray.length)];
					middleName=valid_middleNameArray[random.nextInt(valid_middleNameArray.length)];
					lastName = valid_lastNameArray[random.nextInt(valid_lastNameArray.length)];
					dateOfBirth=valid_dateOfBirthArray[random.nextInt(valid_dateOfBirthArray.length)];
					gender=valid_genderArray[random.nextInt(valid_genderArray.length)];
					addressLine1=valid_addressLine1Array[random.nextInt(valid_addressLine1Array.length)];
					addressLine2=valid_addressLine2Array[random.nextInt(valid_addressLine2Array.length)];
					addressLine3=valid_addressLine3Array[random.nextInt(valid_addressLine3Array.length)];
					region=valid_regionArray[random.nextInt(valid_regionArray.length)];
					province=valid_provinceArray[random.nextInt(valid_provinceArray.length)];
					city=valid_cityArray[random.nextInt(valid_cityArray.length)];
					localAdministrativeAuthority=valid_localAdministrativeAuthorityArray[random.nextInt(valid_localAdministrativeAuthorityArray.length)];
					mobileNumber=valid_mobileNumberArray[random.nextInt(valid_mobileNumberArray.length)];
					emailId=invalid_emailIdArray[random.nextInt(invalid_emailIdArray.length)];
					CNEOrPINNumber=valid_CNEOrPINNumberArray[random.nextInt(valid_CNEOrPINNumberArray.length)];
					parentOrGuardianName=valid_parentOrGuardianNameArray[random.nextInt(valid_parentOrGuardianNameArray.length)];
					parentOrGuardianRIDOrUIN=valid_parentOrGuardianRIDOrUINArray[random.nextInt(valid_parentOrGuardianRIDOrUINArray.length)];
					leftEye=valid_leftEyeArray[random.nextInt(valid_leftEyeArray.length)];
					rightEye=valid_rightEyeArray[random.nextInt(valid_rightEyeArray.length)];
					leftSlap=valid_leftSlapArray[random.nextInt(valid_leftSlapArray.length)];
					rightSlap=valid_rightSlapArray[random.nextInt(valid_rightSlapArray.length)];
					thumbs=valid_thumbsArray[random.nextInt(valid_thumbsArray.length)];

				}
				break;
				case "113100":{
					firstName = valid_firtNameArray[random.nextInt(valid_firtNameArray.length)];
					middleName=valid_middleNameArray[random.nextInt(valid_middleNameArray.length)];
					lastName = valid_lastNameArray[random.nextInt(valid_lastNameArray.length)];
					dateOfBirth=valid_dateOfBirthArray[random.nextInt(valid_dateOfBirthArray.length)];
					gender=valid_genderArray[random.nextInt(valid_genderArray.length)];
					addressLine1=valid_addressLine1Array[random.nextInt(valid_addressLine1Array.length)];
					addressLine2=valid_addressLine2Array[random.nextInt(valid_addressLine2Array.length)];
					addressLine3=valid_addressLine3Array[random.nextInt(valid_addressLine3Array.length)];
					region=valid_regionArray[random.nextInt(valid_regionArray.length)];
					province=valid_provinceArray[random.nextInt(valid_provinceArray.length)];
					city=valid_cityArray[random.nextInt(valid_cityArray.length)];
					localAdministrativeAuthority=valid_localAdministrativeAuthorityArray[random.nextInt(valid_localAdministrativeAuthorityArray.length)];
					mobileNumber=valid_mobileNumberArray[random.nextInt(valid_mobileNumberArray.length)];
					emailId=invalid_emailIdArray[random.nextInt(invalid_emailIdArray.length)];
					CNEOrPINNumber=valid_CNEOrPINNumberArray[random.nextInt(valid_CNEOrPINNumberArray.length)];
					parentOrGuardianName=valid_parentOrGuardianNameArray[random.nextInt(valid_parentOrGuardianNameArray.length)];
					parentOrGuardianRIDOrUIN=valid_parentOrGuardianRIDOrUINArray[random.nextInt(valid_parentOrGuardianRIDOrUINArray.length)];
					leftEye=invalid_leftEyeArray[random.nextInt(invalid_leftEyeArray.length)];
					rightEye=invalid_rightEyeArray[random.nextInt(invalid_rightEyeArray.length)];
					leftSlap=invalid_leftSlapArray[random.nextInt(invalid_leftSlapArray.length)];
					rightSlap=invalid_rightSlapArray[random.nextInt(invalid_rightSlapArray.length)];
					thumbs=invalid_thumbsArray[random.nextInt(invalid_thumbsArray.length)];

				}
				break;

				default:
					System.out.println("Invalid input format");
					System.exit(0);
					break;
				}
				
				JSONObject obj=new JSONObject();
				obj.put("firstName", firstName);
				obj.put("middleName", middleName);
				obj.put("lastName", lastName);
				obj.put("dateOfBirth", dateOfBirth);
				obj.put("gender", gender);
				obj.put("addressLine1", addressLine1);
				obj.put("addressLine2", addressLine2);
				obj.put("addressLine3", addressLine3);
				obj.put("region", region);
				obj.put("province", province);
				obj.put("city", city);
				obj.put("localAdministrativeAuthority", localAdministrativeAuthority);
				obj.put("mobileNumber", mobileNumber);
				obj.put("emailId", emailId);
				obj.put("CNEOrPINNumber", CNEOrPINNumber);
				obj.put("parentOrGuardianName", parentOrGuardianName);
				obj.put("parentOrGuardianRIDOrUIN", parentOrGuardianRIDOrUIN);
				obj.put("LeftEye", leftEye);
				obj.put("rightEye", rightEye);
				obj.put("leftSlap", leftSlap);
				obj.put("rightSlap", rightSlap);
				obj.put("thumbs", thumbs);

				PacketCreation.createFolder("D:\\TestDataGenerator\\"+inputValue+"\\"+"TestData"+i+"\\Biometrics");

				String locationToCopyImages="D:\\TestDataGenerator\\"+inputValue+"\\"+"TestData"+i+"\\Biometrics";
				MoveingFileToAnotherDirectory.copyFile(locationToCopyImages,leftEye);
				MoveingFileToAnotherDirectory.copyFile(locationToCopyImages,rightEye);
				MoveingFileToAnotherDirectory.copyFile(locationToCopyImages,leftSlap);
				MoveingFileToAnotherDirectory.copyFile(locationToCopyImages,rightSlap);
				MoveingFileToAnotherDirectory.copyFile(locationToCopyImages,thumbs);

				String jsonLocation="D:\\TestDataGenerator\\"+inputValue+"\\"+"TestData"+i;
				try (FileWriter file = new FileWriter(jsonLocation+"\\MosipTestData"+i+".json")) {
					file.write(obj.toJSONString());
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Test datas has created sucessfull please check D drive TestDataGenerator folder";
	}

	

}
