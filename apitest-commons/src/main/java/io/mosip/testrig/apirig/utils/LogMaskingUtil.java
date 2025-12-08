package io.mosip.testrig.apirig.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class LogMaskingUtil {
	
	private static final List<String> SENSITIVE_KEYS = Arrays.asList(
            "clientSecret", "client_secret", "password", "pwd", 
            "token", "access_token", "refresh_token", "refreshToken", "Authorization", "set-cookie",
            "cookie", "XSRF-TOKEN", "X-XSRF-TOKEN"
            
    );

    public static String maskSensitiveData(String json) {
        if (json == null) return null;

        try {
            JSONObject obj = new JSONObject(json);
            mask(obj);
            return obj.toString(4); // pretty print
        } catch (Exception e) {
            return json; // In case the body is not JSON, just return raw
        }
    }

    private static void mask(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                mask((JSONObject) value);
            } else if (value instanceof JSONArray) {
                JSONArray arr = (JSONArray) value;
                for (int i = 0; i < arr.length(); i++) {
                    if (arr.get(i) instanceof JSONObject) {
                        mask(arr.getJSONObject(i));
                    }
                }
            } else {
                if (SENSITIVE_KEYS.contains(key)) {
                    jsonObject.put(key, maskValue(value.toString()));
                }
            }
        }
    }

    private static String maskValue(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        int unmasked = 3; // last 3 digits shown
        String stars = "*".repeat(value.length() - unmasked);
        return stars + value.substring(value.length() - unmasked);
    }
    
    public static void safeLogInfo(Logger logger, String message) {
        try {
            String masked = maskSensitiveData(message);
            logger.info(masked);
        } catch (Exception e) {
            logger.info(message);
        }
    }
    
    public static String maskSensitiveData(String headerName, String value) {
        if (value == null) return null;

        String maskedValue = value;

        for (String sensitiveKey : SENSITIVE_KEYS) {

            // If header name itself is sensitive -> mask fully
            if (headerName.equalsIgnoreCase(sensitiveKey) && sensitiveKey != "set-cookie") {
                return maskValue(value);
            }

            // If Set-Cookie header -> mask sensitive keys inside cookie value
            if ("set-cookie".equalsIgnoreCase(headerName)) {
                maskedValue = maskedValue.replaceAll("(?i)" + sensitiveKey + "=[^;]*", sensitiveKey + "=********");
            }
        }

        return maskedValue.equals(value) ? maskSensitiveData(value) : maskedValue;
    }

}