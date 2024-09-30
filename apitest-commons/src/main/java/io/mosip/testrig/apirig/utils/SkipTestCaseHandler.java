package io.mosip.testrig.apirig.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.testrunner.MosipTestRunner;

public class SkipTestCaseHandler {
	private static final Logger logger = Logger.getLogger(SkipTestCaseHandler.class);
	private static List<String> testcaseToBeSkippedList = new ArrayList<>();

	public static void main(String[] args) {
		String filePath = "C:\\Users\\sivan\\Downloads\\SynData\\testCaseSkippedList.txt"; // Replace with your file
																							// path

		loadTestcaseToBeSkippedList(filePath);

		// Example usage of the checkStringInList method
		String searchString = "IdRepository_UpdateIdentity_handle_value_value_withupdatevalues";
		boolean exists = isTestCaseInSkippedList(searchString);
		logger.info("Does the string exist? " + exists);

		searchString = "IdRepository_UpdateIdentity_handle_value_value_withupdatevalues";
		exists = isTestCaseInSkippedList(searchString);
		logger.info("Does the string exist? " + exists);

	}

	// load test cases to be skipped in the execution in the list
	public static void loadTestcaseToBeSkippedList(String fileName) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(MosipTestRunner.getGlobalResourcePath() + "/" + fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Ignore lines starting with # as it is commented line
				if (line.startsWith("#")) {
					continue;
				}

				// Split the line by "------" and store the second part
				if (line.contains("------")) {
					String[] parts = line.split("------");
					if (parts.length > 1) {
						testcaseToBeSkippedList.add(parts[1].trim());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to check if a given test case exists in the list
	public static boolean isTestCaseInSkippedList(String strTestCase) {
		return testcaseToBeSkippedList.contains(strTestCase);
	}
}