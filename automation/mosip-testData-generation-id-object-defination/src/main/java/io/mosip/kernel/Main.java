package io.mosip.kernel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.mosip.kernel.dto.ClientInformationDTO;
import io.mosip.kernel.helper.PacketCreation;
import io.mosip.kernel.helper.PropertyFileLoader;
import io.mosip.kernel.utility.Generator;
import io.mosip.kernel.utility.IdJsonParser;

/**
 * This is the Main class that generates the test data based upon the Id Object
 * defination file
 * 
 * @author Arjun chandramohan
 *
 */
public class Main {
	@SuppressWarnings("unchecked")
	public void testDataGeneration() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		ClientInformationDTO clientInformationDTO = null;
		try {
			clientInformationDTO = mapper.readValue(
					new File("src//main//resources//MasterFile-idObject-defination.yml"), ClientInformationDTO.class);
			// clientInformationDTO = mapper.readValue(new
			// File("./MasterFile-idObject-defination.yml"), ClientInformationDTO.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Random random = new Random();
		JSONObject obj = new JSONObject();
		ArrayList<String> outputPropertyList = new IdJsonParser().jsonPropertyFields();
		System.err.println(outputPropertyList);

		Generator generator = new Generator();
		Properties prop = new PropertyFileLoader().configFileReaderObject("FieldConfig");
		int k = 0;
		while (k < new Integer(prop.getProperty("NumberofOutputJson"))) {

			if (outputPropertyList.contains("fullName")) {
				String fullName = generator.randomFullName(clientInformationDTO, random, prop);
				obj.put("fullName", fullName);
			}
			if (outputPropertyList.contains("dateOfBirth")) {
				String dateOfBirth = generator.randomDOB(clientInformationDTO, random, prop);
				obj.put("dateOfBirth", dateOfBirth);
			}
			if (outputPropertyList.contains("age")) {
				String age = generator.randomAge(clientInformationDTO, random, prop);
				obj.put("Age", age);
			}

			if (outputPropertyList.contains("gender")) {
				String gender = generator.randomGender(clientInformationDTO, random, prop);
				obj.put("gender", gender);
			}
			if (outputPropertyList.contains("addressLine1")) {
				String addressLine1 = generator.randomAddressLine1(clientInformationDTO, random, prop);
				obj.put("addressLine1", addressLine1);
			}
			if (outputPropertyList.contains("addressLine2")) {
				String addressLine2 = generator.randomAddressLine2(clientInformationDTO, random, prop);
				obj.put("addressLine2", addressLine2);
			}
			if (outputPropertyList.contains("addressLine3")) {
				String addressLine3 = generator.randomAddressLine3(clientInformationDTO, random, prop);
				obj.put("addressLine3", addressLine3);
			}
			if (outputPropertyList.contains("region")) {
				String region = generator.randomRegion(clientInformationDTO, random, prop);
				obj.put("region", region);
			}
			if (outputPropertyList.contains("province")) {
				String province = generator.randomProvince(clientInformationDTO, random, prop);
				obj.put("province", province);
			}

			if (outputPropertyList.contains("city")) {
				String city = generator.randomCity(clientInformationDTO, random, prop);
				obj.put("city", city);
			}

			if (outputPropertyList.contains("postalCode")) {
				String city = generator.randomPostalCode(clientInformationDTO, random, prop);
				obj.put("city", city);
			}

			if (outputPropertyList.contains("mobileNumber")) {
				String mobileNumber = generator.randomMobileNumber(clientInformationDTO, random, prop);
				obj.put("mobileNumber", mobileNumber);
			}

			if (outputPropertyList.contains("emailId")) {
				String emailId = generator.randomEmailId(clientInformationDTO, random, prop);
				obj.put("EmailId", emailId);
			}

			if (outputPropertyList.contains("CNEOrPINNumber")) {
				String CNEOrPINNumber = generator.randomCNEOrPINNumber(clientInformationDTO, random, prop);
				obj.put("CNEOrPINNumber", CNEOrPINNumber);
			}

			if (outputPropertyList.contains("parentOrGuardianName")) {
				String parentOrGuardianName = generator.randomFullName(clientInformationDTO, random, prop);
				obj.put("parentOrGuardianName", parentOrGuardianName);
			}

			if (outputPropertyList.contains("localAdministrativeAuthority")) {
				String localAdministrativeAuthority = generator.randomLocalAdministrativeAuthority(clientInformationDTO,
						random, prop);
				obj.put("localAdministrativeAuthority", localAdministrativeAuthority);
			}

			if (outputPropertyList.contains("parentOrGuardianRIDOrUIN")) {
				String parentOrGuardianRIDOrUIN = generator.randomParentOrGuardianRIDOrUIN(clientInformationDTO, random,
						prop);
				obj.put("parentOrGuardianRIDOrUIN", parentOrGuardianRIDOrUIN);
			}

			k++;

			// Output Json file creater
			PacketCreation.createFolder("D:\\TestDataGenerator");
			String jsonLocation = "D:\\TestDataGenerator\\" + "TestData";
			try (FileWriter file = new FileWriter(jsonLocation + k + ".json")) {
				file.write(obj.toJSONString());
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {

		new Main().testDataGeneration();
	}
}
