package io.mosip.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.beust.jcommander.internal.Maps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.restassured.response.Response;


public class AssertPreReg {
	ObjectMapper oMapper = new ObjectMapper();

	public boolean assertPreRegistration(Response response, JSONObject object)
			throws JsonProcessingException, IOException, ParseException {
	
		JSONObject obj1 = AssertPreReg.getComparableBody(response.asString());
		JSONObject obj2 = AssertPreReg.getComparableBody(object.toString());
		System.out.println(obj1);
		System.out.println(obj2);
		  Gson g = new Gson();
		  Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		  Map<String, Object> firstMap = g.fromJson(obj1.toJSONString(), mapType);
		  Map<String, Object> secondMap = g.fromJson(obj2.toJSONString(), mapType);
		  System.out.println(com.google.common.collect.Maps.difference(firstMap,secondMap));
		try {
			if(obj1.hashCode()==obj2.hashCode()) {
			Assert.assertEquals(obj1, obj2);
			return true;
			}
			else {
				return false;
			}
		} catch (AssertionError e) {
			System.out.println("Assertion fails");
			return false;
		}
		
	}

	public static JSONObject getComparableBody(String input) throws ParseException {

		JSONObject object = (JSONObject) new JSONParser().parse(input);
		object.remove("resTime");

		for (Object keys : object.keySet()) {

			try {
				JSONObject parseble = (JSONObject) object.get(keys.toString());
			} catch (ClassCastException exp) {
				// System.out.println("Cannot Be Casted to JSONObject : "+keys);
			}
			// isJSONArray
			try {
				JSONArray parsebleArray = (JSONArray) object.get(keys);
				if (parsebleArray != null)
					recursiveArray(parsebleArray);
			} catch (ClassCastException exp1) {
				// System.out.println("Cannot be Casted to JSONArray : "+keys);
			}
		}
		return object;
	}

	private static void recursiveArray(JSONArray parsebleArray) {
		boolean breaker = false;

		for (Object jsonObject : parsebleArray) {
			JSONObject jsonObjectCpy = (JSONObject) jsonObject;
			for (Object keys : jsonObjectCpy.keySet()) {
				if (keys.toString().toLowerCase().contains("time") || keys.toString().toLowerCase().contains("prid")) {
					jsonObjectCpy.remove(keys);
					breaker = true;
					break;
				}
			}
			if (breaker) {
				breaker = false;
				recursiveArray(parsebleArray);
			}
		}
	}
}
