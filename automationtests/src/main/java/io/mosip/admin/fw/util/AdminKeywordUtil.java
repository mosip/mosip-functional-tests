package io.mosip.admin.fw.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import org.testng.Assert;

import java.util.Map.Entry;

import io.mosip.authentication.fw.dto.ErrorsDto;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

public class AdminKeywordUtil extends KeywordUtil{
	
	private static Map<String,String> currentTestData = new HashMap<String,String>();
	/**
	 * The method return precondtion message or keyword from yml test data file
	 * 
	 * @return map
	 */
	@Override
	public Map<String, String> precondtionKeywords(Map<String, String> map) {
		TreeMap<String, String> returnMap = getSortedTreeMap(map);
		boolean flag = false;
		for (Entry<String, String> entry : returnMap.entrySet()) {
			if (entry.getValue().contains("TestData:") && !entry.getValue().contains("+")
					&& entry.getValue().startsWith("$TestData:")) {
				String dataParam = entry.getValue().replace("$", "").replace("TestData:", "");
					returnMap.put(entry.getKey(), TestDataProcessor.getYamlData("admin", "TestData",
							"RunConfig/adminTestdata", dataParam));
			} else if (entry.getValue().contains("$errors:") && entry.getValue().startsWith("$errors:")) {
				String errorValue = entry.getValue();
				String value = entry.getValue().replace("$", "");
				String[] key = value.split(":");
				String scenario = key[1];
				String expValue = key[2];
				returnMap.put(entry.getKey(),
						ErrorsDto.getErrors().get("errors").get(scenario).get(expValue).toString());
			} 
			else if (entry.getValue().contains("$SPACE$") && !entry.getValue().contains("+") && entry.getValue().contains("$")) {
				returnMap.put(entry.getKey(), " ");
			} else if (entry.getValue().equals("$TIMESTAMP$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateCurrentLocalTimeStampWithZTimeZone());
			}else if (entry.getValue().contains("$TIMESTAMPZ$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateCurrentUTCTimeStampWithZTimeZone());
			} else if (entry.getValue().contains("$") && entry.getValue().contains(":")
					&& (entry.getValue().startsWith("$input") || entry.getValue().startsWith("$end")
							|| entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String jsonFileName = keys[0];
				String fieldName = keys[1];
				String val=null;
				if (TestDataUtil.getCurrTestDataDic()!=null && TestDataUtil.getCurrTestDataDic().containsKey(jsonFileName))
					val = TestDataUtil.getCurrTestDataDic().get(jsonFileName).get(fieldName);
				else
					val=currentTestData.get(fieldName).toString();
				returnMap.put(entry.getKey(), val);
			} else
				returnMap.put(entry.getKey(), entry.getValue());
			currentTestData=returnMap;
		}
		if (flag)
			precondtionKeywords(returnMap);
		return returnMap;
	}

	/**
	 * The method generate current timestamp
	 * 
	 * @return string
	 */
	private String generateCurrentLocalTimeStampWithZTimeZone()
	{
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return dateFormat.format(date);
	}
	
	/**
E	 * 
	 * @return string
	 */
	private String generateCurrentUTCTimeStampWithZTimeZone() {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			return dateFormat.format(date);
	}
	
	private TreeMap<String, String> getSortedTreeMap(Map<String, String> map) {
		SortedSet<String> sortedKey = new TreeSet<>();
		map.forEach((key, value) -> {
			sortedKey.add(key);
		});
		TreeMap<String, String> sortedMap = new TreeMap<String, String>();
		sortedKey.forEach(sortKey -> {
			map.forEach((key, value) -> {
				if (key.equals(sortKey))
					sortedMap.put(key, value);
			});
		});
		return sortedMap;
	}
}
