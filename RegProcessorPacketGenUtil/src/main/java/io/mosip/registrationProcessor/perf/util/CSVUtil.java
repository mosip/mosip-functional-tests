package io.mosip.registrationProcessor.perf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.dto.UserIdDto;

public class CSVUtil {
	private static Logger logger = Logger.getLogger(CSVUtil.class);
	ResourcePathUtil resourcePathUtil = new ResourcePathUtil();

	public List<String> loadCSVData(String csvFilepath) throws FileNotFoundException {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeObjectListToCsv(List<RegDataCSVDto> list, String path)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		Charset charset = Charset.forName("Us-ASCII");

		try (Writer writer = Files.newBufferedWriter(Paths.get(path), StandardOpenOption.APPEND);) {

			StatefulBeanToCsv<RegDataCSVDto> beanToCsv = new StatefulBeanToCsvBuilder(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
			beanToCsv.write(list);
		}

	}

	public void writeObjectsToCsv(List<String[]> csvDtoData, String filePath) throws IOException {

		File file = new File(filePath);
		FileWriter outputfile = new FileWriter(file, true);
		// CSVWriter writer = new CSVWriter(outputfile);
		CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
		writer.writeAll(csvDtoData);
		System.out.println("written to csv");
		writer.close();
	}

	@SuppressWarnings("deprecation")
	public List<RegDataCSVDto> loadRegistrationDataFromCSV(String filePath) {
		List<RegDataCSVDto> list = new ArrayList<RegDataCSVDto>();
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("fullName", "fullName");
		mapping.put("dateOfBirth", "dateOfBirth");
		mapping.put("age", "age");
		mapping.put("gender", "gender");
		mapping.put("residenceStatus", "residenceStatus");
		mapping.put("addressLine1", "addressLine1");
		mapping.put("addressLine2", "addressLine2");
		mapping.put("addressLine3", "addressLine3");
		mapping.put("region", "region");
		mapping.put("province", "province");
		mapping.put("city", "city");
		mapping.put("zone", "zone");
		mapping.put("postalCode", "postalCode");
		mapping.put("phone", "phone");
		mapping.put("email", "email");
		mapping.put("cnieNumber", "cnieNumber");

		HeaderColumnNameTranslateMappingStrategy<RegDataCSVDto> strategy = new HeaderColumnNameTranslateMappingStrategy<RegDataCSVDto>();
		strategy.setType(RegDataCSVDto.class);
		strategy.setColumnMapping(mapping);

		CSVReader csvReader = null;

		try {
			csvReader = new CSVReader(new FileReader(filePath));
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}

		CsvToBean<RegDataCSVDto> csvToBean = new CsvToBean<>();
		try {
			list = csvToBean.parse(strategy, csvReader);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public UserIdDto loadCenterMachineUserFromCSV() {
		List<UserIdDto> userList = new ArrayList<>();
		String baseResourcePath = resourcePathUtil.getResourcePath();
		//System.out.println("baseResourcePath: " + baseResourcePath);
		String csvFile = baseResourcePath + File.separator + "centerID_machineID_userID.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] keys = line.split(cvsSplitBy);
				String centerId = keys[0];
				String machineId = keys[1];
				String userId = keys[2];
				UserIdDto userDto = new UserIdDto();
				userDto.setCenterId(centerId);
				userDto.setMachineId(machineId);
				userDto.setUserId(userId);
				userList.add(userDto);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(userList.size());
		UserIdDto userObj = userList.get(randomInt);
		return userObj;
	}

}
