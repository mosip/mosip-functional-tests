package io.mosip.testrig.apirig.utils;

import java.io.FileReader;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class DependencyResolver {
	
	private static Map<String, List<String>> dependecyData = null;
	
	public static void loadDependencies(String jsonFilePath) throws Exception {
		AdminTestUtil.generateDependency = false;
	    // Parse the JSON file into a Map
	    Gson gson = new Gson();
	    dependecyData = gson.fromJson(
	        new FileReader(jsonFilePath),
	        new TypeToken<Map<String, List<String>>>(){}.getType()
	    );
	}

	public static List<String> getDependencies(String testCaseIdsCsv) {
	    Set<String> result = new LinkedHashSet<>(); // to maintain order and avoid duplicates
	    if (dependecyData != null){
		    // Split comma-separated test case IDs
		    String[] testCaseIds = testCaseIdsCsv.split(",");
	
		    for (String testCaseId : testCaseIds) {
		        resolveDependencies(testCaseId.trim(), dependecyData, result);
		    }
	    }
	    return new ArrayList<>(result);
	}

	private static void resolveDependencies(String testCaseId, Map<String, List<String>> data, Set<String> result) {
		if (!result.contains(testCaseId)) {
			result.add(testCaseId);
			List<String> dependencies = data.getOrDefault(testCaseId, new ArrayList<>());
			for (String dep : dependencies) {
				resolveDependencies(dep, data, result);
			}
		}
	}

	// Example usage
	public static void main(String[] args) throws Exception {
		String pathToJson = "C:\\id-repository\\id-repository\\api-test\\testCaseInterDepndency.json";
		
		loadDependencies(pathToJson);
		

		System.out.println(getDependencies("TC_IDRepo_AuthInternalock_02,TC_IDRepo_AuthInternalock_01"));
		System.out.println(getDependencies("TC_IDRepo_AuthInternalock_01"));
		System.out.println(getDependencies("TC_IDRepo_AddIdentity_32"));
	}
}
