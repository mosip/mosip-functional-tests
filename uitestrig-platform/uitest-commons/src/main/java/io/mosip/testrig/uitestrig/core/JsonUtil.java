package io.mosip.testrig.uitestrig.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {}

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON", e);
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (JsonMappingException | com.fasterxml.jackson.core.JsonParseException e) {
            logger.error("Failed to deserialize JSON to {}", cls.getSimpleName(), e);
            return null;
        } catch (java.io.IOException e) {
            logger.error("IO error deserializing JSON", e);
            return null;
        }
    }

    /**
     * Extracts a simple-type field from the identity block, returning the value
     * for the login language or the last value seen.
     */
    public static String getIdentitySimpleField(String identityJson, String field) {
        try {
            JSONObject json = new JSONObject(identityJson);
            JSONArray items = json.getJSONObject("identity").getJSONArray(field);
            String loginLang = UiConfigManager.getEffectiveLoginLang();
            String last = null;
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                last = item.getString("value");
                if (item.getString("language").equalsIgnoreCase(loginLang)) return last;
            }
            return last != null ? last : "";
        } catch (Exception e) {
            logger.error("getIdentitySimpleField({}) failed", field, e);
            return "";
        }
    }

    /**
     * Extracts a non-array string field from the identity block.
     */
    public static String getIdentityField(String identityJson, String field) {
        try {
            return new JSONObject(identityJson).getJSONObject("identity").getString(field);
        } catch (Exception e) {
            logger.error("getIdentityField({}) failed", field, e);
            return "";
        }
    }

    public static double getIdentityDoubleField(String identityJson, String field) {
        try {
            return new JSONObject(identityJson).getJSONObject("identity").getDouble(field);
        } catch (Exception e) {
            logger.error("getIdentityDoubleField({}) failed", field, e);
            return 0;
        }
    }

    public static List<String> getIdentityArrayField(String identityJson, String field) {
        List<String> list = new LinkedList<>();
        try {
            JSONArray items = new JSONObject(identityJson).getJSONObject("identity").getJSONArray(field);
            for (int i = 0; i < items.length(); i++) list.add(items.getString(i));
        } catch (Exception e) {
            logger.error("getIdentityArrayField({}) failed", field, e);
        }
        return list;
    }
}
