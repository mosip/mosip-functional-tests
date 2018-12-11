package io.mosip.kernel.utility;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import io.mosip.kernel.helper.PropertyFileLoader;

/**
 * This class provide the random DOB based on input parameter
 * 
 * @author Arjun Chandramohan
 *
 */

public class DOBGenerator {
	static Properties prop=null;
	/**
	 * setting object for reading property file
	 */
	public DOBGenerator() {
		prop=new PropertyFileLoader().configFileReaderObject("FieldConfig");
	}
	
	/**
	 * @param genderAgeGroup
	 * 	        gender age group can be Adult or child
	 * @param dateFormat
	 * 			Can be any valid date format(yyyy/mm/dd,dd-mm-yyyy...)
	 * @param prop 
	 * @return
	 * 			return DOB in required format
	 */
	public String randomDOBGenerator(String genderAgeGroup,String dateFormat, Properties prop) {
		LocalDate startDate=null;LocalDate endDate = null;
		switch(prop.getProperty("dateOfBirth")) {
		case "valid":
		if(genderAgeGroup.equals("adult")) {
			startDate = LocalDate.now().minusYears(new Integer(prop.getProperty("adult.age.upper.limit"))); 
			endDate = LocalDate.now().minusYears(new Integer(prop.getProperty("adult.age.lower.limit")));
		}
		else if(genderAgeGroup.equals("child")) {
			startDate = LocalDate.now().minusYears(new Integer(prop.getProperty("child.age.upper.limit"))); 
			endDate = LocalDate.now();
		}
		else {
			System.out.println("Invalid Age category");
			System.exit(0);
		}
		break;
		case "invalid":
			if(genderAgeGroup.equals("adult")) {
				startDate = LocalDate.now().minusYears(new Integer(prop.getProperty("child.age.upper.limit"))); 
				endDate = LocalDate.now();
			}
			else if(genderAgeGroup.equals("child")) {
				startDate = LocalDate.now().minusYears(new Integer(prop.getProperty("adult.age.upper.limit"))); 
				endDate = LocalDate.now().minusYears(new Integer(prop.getProperty("adult.age.lower.limit")));
			}
			else {
				System.out.println("Invalid Age category");
				System.exit(0);
			}
			break;
			default:
				System.out.println("Invalid case for DOB");
				System.exit(0);
		}
		long randomEpochDay = ThreadLocalRandom.current().longs(startDate.toEpochDay(), endDate.toEpochDay()).findAny().getAsLong();

		if(dateFormat.equals("yyyy-mm-dd"))
			return LocalDate.ofEpochDay(randomEpochDay).toString();

		String date=dateFormatConveter(LocalDate.ofEpochDay(randomEpochDay).toString(),dateFormat);
		return date;
	}


	/**
	 * @param randomDOB
	 * 			accept random DOB generated from randomDOBGenerator method
	 * @param dateFormat
	 * 			accept date format
	 * @return
	 */
	public String dateFormatConveter(String randomDOB, String dateFormat) {
		DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;DateFormat destDf=null;
		try {
			date = srcDf.parse(randomDOB);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		switch (dateFormat) {

		case "dd-mm-yyyy":
			destDf = new SimpleDateFormat("dd-MM-yyyy");
			break;
		case "dd/mm/yyyy":
			destDf = new SimpleDateFormat("dd/MM/yyyy");
			break;

		case "mm-dd-yyyy":
			destDf = new SimpleDateFormat("MM-dd-yyyy");
			break;

		case "mm/dd/yyyy":
			destDf = new SimpleDateFormat("MM/dd/yyyy");
			break;

		case "mm-yyyy-dd":
			destDf = new SimpleDateFormat("MM-yyyy-dd");
			break;

		case "mm/yyyy/dd":
			destDf = new SimpleDateFormat("MM/yyyy/dd");
			break;

		case "yyyy/mm/dd":
			destDf = new SimpleDateFormat("yyyy/MM/dd");
			break;
		case "yyyy-dd-mm":
			destDf = new SimpleDateFormat("yyyy-dd-MM");
			break;

		case "yyyy/dd/mm":
			destDf = new SimpleDateFormat("yyyy/dd/MM");
			break;

		case "dd-yyyy-mm":
			destDf = new SimpleDateFormat("dd-yyyy-MM");
			break;
		case "dd/yyyy/mm":
			destDf = new SimpleDateFormat("dd/yyyy/MM");
			break;

		default:
			System.out.println("Invalid date format");
			System.exit(0);
			break;
		}
		randomDOB = destDf.format(date);
		return randomDOB;
	}

}
