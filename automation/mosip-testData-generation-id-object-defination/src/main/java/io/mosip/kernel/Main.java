package io.mosip.kernel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.mosip.kernel.dto.ClientInformationDTO;
import io.mosip.kernel.helper.PacketCreation;
import io.mosip.kernel.helper.PropertyFileLoader;
import io.mosip.kernel.utility.DOBGenerator;
import io.mosip.kernel.utility.Generator;
import io.mosip.kernel.utility.IdJsonParser;
/**
 * This is the Main class that generates the test data based upon the Id Object defination file
 * 
 * @author Arjun chandramohan
 *
 */
public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		//ClientInformationDTO clientInformationDTO = mapper.readValue(new File("src//main//resources//MasterFile-idObject-defination.yml"), ClientInformationDTO.class);
		ClientInformationDTO clientInformationDTO = mapper.readValue(new File("./MasterFile-idObject-defination.yml"), ClientInformationDTO.class);
		Random random=new Random();
		JSONObject obj=new JSONObject();
		ArrayList<String> outputPropertyList=new IdJsonParser().jsonPropertyFields();
		System.err.println(outputPropertyList);
		
		Generator generator = new Generator();
		Properties prop= new PropertyFileLoader().configFileReaderObject("FieldConfig");
		String gender = "Male";
		int k=0;
		String firstName = null,middleName=null,lastName=null;
		while(k< new Integer(prop.getProperty("NumberofOutputJson"))) {

			if(outputPropertyList.contains("gender")) {
				gender=generator.randomGender(clientInformationDTO,random,prop);
				obj.put("gender",gender);
			}
			if(outputPropertyList.contains("firstName")) {
				firstName=generator.randomName(clientInformationDTO,random,gender,"firstName",prop);
				obj.put("firstName",firstName);
			}
			if(outputPropertyList.contains("middleName"))  {
				middleName=generator.randomName(clientInformationDTO,random,gender,"middleName",prop);
				obj.put("middleName", middleName);
			}
			if(outputPropertyList.contains("lastName")) {
				lastName=generator.randomName(clientInformationDTO,random,gender,"lastName",prop);
				obj.put("lastName", lastName);
			}
			if(outputPropertyList.contains("parentOrGuardianName")) {
				String parentOrGuardianName=generator.randomName(clientInformationDTO,random,"Male","parentOrGuardianName",prop);
				obj.put("parentOrGuardianName", parentOrGuardianName);
			}
			if(outputPropertyList.contains("dateOfBirth")) {
				String dateOfBirth=new DOBGenerator().randomDOBGenerator(prop.getProperty("age.group"),prop.getProperty("DOB.format"),prop);
				obj.put("dateOfBirth",dateOfBirth);
			}
			if(outputPropertyList.contains("addressLine1")) {
				String addressLine1 = generator.randomAddressLine1(clientInformationDTO,random,prop);
				obj.put("addressLine1", addressLine1);
			}
			if(outputPropertyList.contains("addressLine2")) {
				String addressLine2=generator.randomAddressLine2(clientInformationDTO,random,prop);
				obj.put("addressLine2", addressLine2);
			}
			if(outputPropertyList.contains("addressLine3")) {
				String addressLine3 = generator.randomAddressLine3(clientInformationDTO,random,prop);
				obj.put("addressLine3", addressLine3);
			}
			if(outputPropertyList.contains("region")) {
				String region=generator.randomRegion(clientInformationDTO,random,prop);
				obj.put("region", region);
			}
			if(outputPropertyList.contains("city")) {
				String city=generator.randomCity(clientInformationDTO,random,prop);
				obj.put("city", city);
			}
			if(outputPropertyList.contains("province")) {
				String province=generator.randomProvince(clientInformationDTO,random,prop);
				obj.put("province", province);
			}
			if(outputPropertyList.contains("localAdministrativeAuthority")) {
				String localAdministrativeAuthority=generator.randomLocalAdministrativeAuthority(clientInformationDTO,random,prop);
				obj.put("localAdministrativeAuthority", localAdministrativeAuthority);
			}
			if(outputPropertyList.contains("mobileNumber")) {
				String mobileNumber=generator.randomMobileNumber(clientInformationDTO,random,prop);
				obj.put("mobileNumber", mobileNumber);
			}
			if(outputPropertyList.contains("CNEOrPINNumber")) {
				String CNEOrPINNumber=generator.randomCNEOrPINNumber(clientInformationDTO,random,prop);
				obj.put("CNEOrPINNumber", CNEOrPINNumber);
			}
			if(outputPropertyList.contains("parentOrGuardianRIDOrUIN")) {
				String parentOrGuardianRIDOrUIN=generator.randomParentOrGuardianRIDOrUIN(clientInformationDTO,random,prop);
				obj.put("parentOrGuardianRIDOrUIN", parentOrGuardianRIDOrUIN);
			}
			if(outputPropertyList.contains("emailId")) {
				String emailId =generator.randomEmailId(clientInformationDTO,random,prop,outputPropertyList,firstName,lastName);
				obj.put("EmailId", emailId);
			}

			if(outputPropertyList.contains("leftEye")) {
				generator.randomLeftEye();
			}

			if(outputPropertyList.contains("rightEye")) {
				generator.randomRightEye();
			}

			if(outputPropertyList.contains("leftSlap")) {
				generator.randomLeftSlap();
			}

			if(outputPropertyList.contains("rightSlap")) {
				generator.randomLeftSlap();
			}

			if(outputPropertyList.contains("thumbs")) {
				generator.randomThumbs();
			}


			k++;

			//Output Json file creater
			PacketCreation.createFolder("D:\\TestDataGenerator");
			String jsonLocation="D:\\TestDataGenerator\\"+"TestData";
			try (FileWriter file = new FileWriter(jsonLocation+k+".json")) {
				file.write(obj.toJSONString());
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}


		}

	}
}
