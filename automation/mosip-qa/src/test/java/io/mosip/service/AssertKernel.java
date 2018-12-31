package io.mosip.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.restassured.response.Response;


public class AssertKernel {
	ObjectMapper oMapper = new ObjectMapper();

	public boolean assertKernel(Response response, JSONObject object)
			throws JsonProcessingException, IOException, ParseException {

		JSONObject obj1 = AssertKernel.getComparableBody(response.asString());
		JSONObject obj2 = AssertKernel.getComparableBody(object.toString());
		  Gson g = new Gson();
		  Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		  Map<String, Object> firstMap = g.fromJson(obj1.toJSONString(), mapType);
		  Map<String, Object> secondMap = g.fromJson(obj2.toJSONString(), mapType);
		  System.out.println(com.google.common.collect.Maps.difference(firstMap,secondMap));
		try {
			if(obj1.hashCode()==obj2.hashCode()) {
			Assert.assertTrue("Response Body Matches", obj1.equals(obj2));
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
		object.remove("otp");

		for (Object keys : object.keySet()) {
			// if(keys.c)

			try {
				JSONObject parseble = (JSONObject) object.get(keys.toString());
			} catch (ClassCastException exp) {
				// System.out.println("Cannot Be Casted to JSONObject : "+keys);
			}
			
		}
		return object;
	}

	
}
