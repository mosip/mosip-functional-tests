/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.mosip.registrationProcessor.perf.dto.DOBDto;
import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.registrationProcessor.perf.util.CSVUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TestDataUtility;

/**
 * @author Gaurav Sharan
 *
 */
public class TestDataGenerator {

	private static Logger logger = Logger.getLogger(TestDataGenerator.class);

	public TestDataGenerator() {

	}

//	public void readDataFromCSV() {
//		String filePath = "E:\\MOSIP_PT\\Data\\reg_data_sample.csv";
//		CSVUtil.loadObjectsFromCSV(filePath);
//	}

	public void generateTestDataInCSV(String csvPath, PropertiesUtil prop, Session session) {
		TestDataUtility testDataUtil = new TestDataUtility();

		CSVUtil csvUtil = new CSVUtil();
		List<RegDataCSVDto> csvDtoList = new ArrayList<>();
		List<String[]> csvDtoData = new ArrayList<String[]>();

		for (int i = 0; i <= prop.NUMBER_OF_TEST_DATA; i++) {
//			if (i == 0) {
//				String[] header = new String[] { "fullName", "dateOfBirth", "age", "gender", "residenceStatus",
//						"addressLine1", "addressLine2", "addressLine3", "region", "province", "city", "postalCode",
//						"phone", "email", "zone", "cnieNumber" };
//				csvDtoData.add(header);
//			}
			RegDataCSVDto csvDto = new RegDataCSVDto();

			String fullName = testDataUtil.generateFullName();
			csvDto.setFullName(fullName);

			DOBDto dobDto = testDataUtil.generateDOB();
			String dateOfBirth = dobDto.getDate();
			csvDto.setDateOfBirth(dateOfBirth);
			Integer age = dobDto.getAge();
			csvDto.setAge(age.toString());

			String gender = testDataUtil.genrateGenderEng();
			csvDto.setGender(gender);
			String residenceStatus = testDataUtil.generateResidenceStatusEng();
			csvDto.setResidenceStatus(residenceStatus);
			String addressLine1 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine1(addressLine1);
			String addressLine2 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine2(addressLine2);
			String addressLine3 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine3(addressLine3);

			String countryCode = testDataUtil.getCountryCode(session,prop);
			Location region = testDataUtil.getLocation(countryCode, 1, session);
			String regionName = region.getName();
			csvDto.setRegion(regionName);

			Location province = testDataUtil.getLocation(region.getCode(), 2, session);
			String provinceName = province.getName();
			csvDto.setProvince(provinceName);

			Location city = testDataUtil.getLocation(province.getCode(), 3, session);
			String cityName = city.getName();
			csvDto.setCity(cityName);

			Location zone = testDataUtil.getLocation(city.getCode(), 4, session);
			String zoneName = zone.getName();
			csvDto.setZone(zoneName);

			Location postalCode = testDataUtil.getLocation(zone.getCode(), 5, session);
			String postalCodeStr = postalCode.getName();
			csvDto.setPostalCode(postalCodeStr);
			String phone = testDataUtil.generatePhoneNumber().toString();
			csvDto.setPhone(phone);
			String[] names = fullName.split(" ");
			String email = testDataUtil.generateEmailAddress(names[0], names[1]);
			csvDto.setEmail(email);
			String cnieNumber = testDataUtil.generateCnieNumber();
			csvDto.setCnieNumber(cnieNumber);
			String[] rowData = new String[] { fullName, dateOfBirth, age.toString(), gender, residenceStatus,
					addressLine1, addressLine2, addressLine3, regionName, provinceName, cityName, postalCodeStr, phone,
					email, zoneName, cnieNumber };

			csvDtoList.add(csvDto);
			csvDtoData.add(rowData);
			if (csvDtoData.size() >= 10) {
				System.out.println("Test data generation; iteration " + i + " over");

				try {
					csvUtil.writeObjectsToCsv(csvDtoData, csvPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				csvDtoData.clear();
			}

			logger.debug("Test data generation; iteration " + i + " over");
		}

//		String path = "E:\\MOSIP_PT\\Data\\reg_data_1.csv";
//		try {
//			CSVUtil.writeObjectListToCsv(csvDtoList, path);
//		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
//			e.printStackTrace();
//		}
	}

}
