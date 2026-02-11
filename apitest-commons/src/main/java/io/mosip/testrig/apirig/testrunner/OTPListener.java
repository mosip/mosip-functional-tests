package io.mosip.testrig.apirig.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
//import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;

public class OTPListener {
	private static Logger logger = Logger.getLogger(OTPListener.class);

	public static Map<Object, Object> emailNotificationMapS = Collections
			.synchronizedMap(new HashMap<Object, Object>());

	public static Map<Object, Object> allNotificationMapS = Collections.synchronizedMap(new HashMap<>());

	public static Boolean bTerminate = false;

	public OTPListener() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public void run() {
		try {
			String a1 = "wss://smtp.";
			String externalurl = ConfigManager.getIAMUrl();

			if (externalurl.contains("/auth")) {
				externalurl = externalurl.replace("/auth", "");
			}
			String a2 = externalurl.substring(externalurl.indexOf(".") + 1);
			String a3 = "/mocksmtp/websocket";

			WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder()
					.buildAsync(URI.create(a1 + a2 + a3), new WebSocketClient()).join();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private static class WebSocketClient implements WebSocket.Listener {

		public WebSocketClient() {
			return;

		}

		@Override
		public void onOpen(WebSocket webSocket) {
			logger.info("onOpen using subprotocol " + webSocket.getSubprotocol());
			WebSocket.Listener.super.onOpen(webSocket);
		}

		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			return Listener.super.onClose(webSocket, statusCode, reason);
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

			if (bTerminate) {
				logger.info("Closing listener");
				onClose(webSocket, 0, "After suite invoked closing");
			}

			try {
				ObjectMapper om = new ObjectMapper();
				Root root = om.readValue(data.toString(), Root.class);

				String otpMessage = "";
				String allMessage = "";
				String address = "";

				if ("SMS".equalsIgnoreCase(root.type)) {
					otpMessage = root.subject;
					allMessage = root.subject;
					address = root.to.text.trim();

				} else if ("MAIL".equalsIgnoreCase(root.type)) {
					otpMessage = root.html; 
					allMessage = root.subject;
					address = root.to.value.get(0).address;

				} else {
					logger.warn("Unsupported notification type. type=" + root.type);
					return WebSocket.Listener.super.onText(webSocket, data, last);
				}

				// Always store ALL notifications (subject)
				allNotificationMapS.put(address, allMessage);
				logger.info("Stored in allNotificationMapS for " + address);

				// Store only OTP / AdditionalReqId (html / subject)
				if (!parseOtp(otpMessage).isEmpty() || !parseAdditionalReqId(otpMessage).isEmpty()) {

					emailNotificationMapS.put(address, otpMessage);
					logger.info("Stored in emailNotificationMapS for " + address);
				}

			} catch (Exception e) {
				logger.error("Error processing WebSocket message", e);
			}

			return WebSocket.Listener.super.onText(webSocket, data, last);
		}

		@Override
		public void onError(WebSocket webSocket, Throwable error) {

			logger.info("Bad day! " + webSocket.toString());
			logger.error(error.getMessage());
			WebSocket.Listener.super.onError(webSocket, error);
		}
	}

	public static String getOtp(String emailId) {
		if (ConfigManager.getUsePreConfiguredOtp().equalsIgnoreCase(GlobalConstants.TRUE_STRING)) {
			return ConfigManager.getPreConfiguredOtp();
		}
		int otpExpTime = AdminTestUtil.getOtpExpTimeFromActuator();
		int otpCheckLoopCount = (otpExpTime * 1000) / AdminTestUtil.OTP_CHECK_INTERVAL;

		int counter = 0;

		String otp = "";

		while (counter < otpCheckLoopCount) {
			logger.info("*******emailNotificationMapS value = " + emailNotificationMapS + " and emailId = " + emailId);
			if (emailNotificationMapS.get(emailId) != null) {
				String html = (String) emailNotificationMapS.get(emailId);
				if (BaseTestCase.currentModule.contains(GlobalConstants.DSL)) {
					emailNotificationMapS.remove(emailId);
				} else {
					emailNotificationMapS.clear();
				}
				otp = parseOtp(html);
				if (otp != null && otp.length() > 0) {
					logger.info("Found the OTP = " + otp);
					return otp;
				} else {
					logger.info("html Message = " + html + " Email = " + emailId);
				}

			}
			logger.info("*******Checking the OTP for " + emailId + "...*******");
			counter++;
			try {
				logger.info("Not received Otp yet, waiting for 10 Sec for " + emailId);
				Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				Thread.currentThread().interrupt();
			}

		}
		logger.info("OTP not found for " + emailId + " even after " + otpCheckLoopCount + " retries");
		return otp;
	}
	
	public static String getNotification(String emailId) {
		int otpExpTime = AdminTestUtil.getOtpExpTimeFromActuator();
		int otpCheckLoopCount = (otpExpTime * 1000) / AdminTestUtil.OTP_CHECK_INTERVAL;
		int counter = 0;
		while (counter < otpCheckLoopCount) {

			if (allNotificationMapS.get(emailId) != null) {
				String message = (String) allNotificationMapS.remove(emailId);
				logger.info("Found notification for " + emailId);
				return message;
			}
			counter++;
			sleep();
		}
		logger.info("Notification not found for " + emailId);
		return "";
	}

	private static void sleep() {
		try {
			Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static String getAdditionalReqId(String emailId) {

		int counter = 0;

		String additionalRequestId = "";

		int additionalRequestIdLoopCount = 10;

		while (counter < additionalRequestIdLoopCount) {
			if (emailNotificationMapS.get(emailId) != null) {
				String html = (String) emailNotificationMapS.get(emailId);
				emailNotificationMapS.remove(emailId);
				additionalRequestId = parseAdditionalReqId(html);
				if (additionalRequestId != null && additionalRequestId.length() > 0) {
					logger.info("Found the additionalRequestId = " + additionalRequestId);
					return additionalRequestId;
				} else {
					logger.info("html Message = " + html + " Email = " + emailId);
				}

			}
			logger.info("*******Checking the email for additionalRequestId...*******");
			counter++;
			try {
				logger.info("Not received additionalRequestId yet, waiting for 10 Sec");
				Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
			} catch (InterruptedException e) {
				logger.info(e.getMessage());
				Thread.currentThread().interrupt();
			}

		}
		logger.info("OTP not found even after " + additionalRequestIdLoopCount + " retries");
		return additionalRequestId;
	}

	public static String parseOtp(String message) {

		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
		String otp = "";
		if (message != null) {
			Matcher mMatcher = mPattern.matcher(message);
			if (mMatcher.find()) {
				otp = mMatcher.group(0);
				otp = otp.trim();
				logger.info("Extracted OTP: " + otp + " message : " + message);
			} else {
				logger.info("Failed to extract the OTP!! " + "message : " + message);

			}
		}
		return otp;
	}

	public static String parseAdditionalReqId(String message) {
		String additionalReqId = StringUtils.substringBetween(message, "AdditionalInfoRequestId",
				"-BIOMETRIC_CORRECTION-1");
		if (additionalReqId == null)
			return "";
		else
			return additionalReqId;
	}

}
