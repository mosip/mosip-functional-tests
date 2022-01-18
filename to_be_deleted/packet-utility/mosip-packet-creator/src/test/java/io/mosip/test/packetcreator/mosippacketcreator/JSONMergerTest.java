package io.mosip.test.packetcreator.mosippacketcreator;


import io.mosip.test.packetcreator.mosippacketcreator.service.JsonMerger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class JSONMergerTest {

    @Test
    public void testMissingKey() throws JSONException {
        JSONObject template = new JSONObject();
        template.put("lastName", "sunkada");
        JSONObject inputData = new JSONObject();
        inputData.put("name", "anusha");
        JsonMerger jsonMerger = new JsonMerger();
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }

    @Test
    public void testExistingKey() throws JSONException {
        JSONObject template = new JSONObject();
        template.put("name", "Sharan");
        template.put("lastName", "sunkada");
        JSONObject inputData = new JSONObject();
        inputData.put("name", "anusha");
        JsonMerger jsonMerger = new JsonMerger();
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }

    @Test
    public void testArrayMissingKey() throws JSONException {
        JSONObject template = new JSONObject();
        JSONObject inputData = new JSONObject();
        inputData.put("name", "anusha");
        inputData.put("testkey", new JSONArray("[\"data1\",\"data2\"]"));
        JsonMerger jsonMerger = new JsonMerger();
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }

    @Test
    public void testArrayExistingKey() throws JSONException {
        JSONObject template = new JSONObject();
        template.put("testkey", new JSONArray("[\"data1\",\"data2\"]"));
        JSONObject inputData = new JSONObject();
        inputData.put("name", "anusha");
        inputData.put("testkey", new JSONArray("[\"data4\",\"data5\"]"));
        JsonMerger jsonMerger = new JsonMerger();
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }

    @Test
    public void testArrayExistingKeyWithOption() throws JSONException {
        JSONObject template = new JSONObject("{\"testkey\":[\"d1\",\"d2\"]}");
        JSONObject inputData = new JSONObject("{\"testkey\":[\"d3\",\"d4\"]}");
        JsonMerger jsonMerger = new JsonMerger(JsonMerger.ArrayMergeMode.MERGE_ARRAY,
                JsonMerger.ObjectMergeMode.MERGE_OBJECT);
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }

    @Test
    public void testObjectArrayExistingKeyWithOption() throws JSONException {
        JSONObject template = new JSONObject("{\"testkey\":[{\"k1\":\"d1\"}]}");
        JSONObject inputData = new JSONObject("{\"testkey\":[{\"k2\":\"d2\"},{\"k1\":\"d2\"}]}");
        JsonMerger jsonMerger = new JsonMerger(JsonMerger.ArrayMergeMode.MERGE_ARRAY,
                JsonMerger.ObjectMergeMode.MERGE_OBJECT);
        JSONObject mergedJson = jsonMerger.merge(template, inputData);
        System.out.println(mergedJson);
    }
}
