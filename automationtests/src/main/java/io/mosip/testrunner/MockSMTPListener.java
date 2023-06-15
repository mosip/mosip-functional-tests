package io.mosip.testrunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Reporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import io.mosip.admin.fw.util.AdminTestUtil;
import io.mosip.kernel.util.ConfigManager;
import io.mosip.pojo.Root;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;


public class MockSMTPListener{
	Logger logger = Logger.getLogger(MockSMTPListener.class);
	//static HashMap emailNotificationMapS=new HashMap<Object, Object>();
	
	 
	public static Map<Object, Object> emailNotificationMapS = Collections.synchronizedMap(new HashMap<Object, Object>()); 
	public static Boolean bTerminate = false;

	public void run() {
		try {
			  Properties kernelprops=ConfigManager.propsKernel;
				String a1="wss://smtp.";
				String externalurl=kernelprops.getProperty("keycloak-external-url");
			    String a2=	externalurl.substring(externalurl.indexOf(".")+1);
			    String a3="/mocksmtp/websocket"; 
				  
			WebSocket ws = HttpClient
					.newHttpClient()
					.newWebSocketBuilder()
					.buildAsync(URI.create(a1+a2+a3), new WebSocketClient())
					.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static class WebSocketClient implements WebSocket.Listener {
		Long count=(long) 00;
		Root root =new Root();
		public WebSocketClient() {  

		}

		@Override
		public void onOpen(WebSocket webSocket) {
			System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
			WebSocket.Listener.super.onOpen(webSocket);
		}


		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			// TODO Auto-generated method stub
			return Listener.super.onClose(webSocket, statusCode, reason);
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			if(bTerminate) {
				System.out.println(emailNotificationMapS);
				System.out.println("End Closure of listner " );
				onClose(webSocket, 0, "After suite invoked closing");
			}
			try {       
				ObjectMapper om = new ObjectMapper();

				root = om.readValue(data.toString(), Root.class);
				// As we need only OTP notifications skip all other notifications
				if (!parseOtp(root.html).isEmpty()) {
					emailNotificationMapS.put(root.to.value.get(0).address,root.html);
					System.out.println(" After adding to emailNotificationMap key = " + root.to.value.get(0).address
							+ " data " + data + " root " + root );
				}
				else {
					System.out.println(" Skip adding to emailNotificationMap key = " + root.to.value.get(0).address
							+ " data " + data + " root " + root );
				}
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} 	catch(JSONException e)
			{
				e.printStackTrace();
			}

			return WebSocket.Listener.super.onText(webSocket, data, last);
		}
	
		

		@Override
		public void onError(WebSocket webSocket, Throwable error) {

			System.out.println("Bad day! " + webSocket.toString());
			error.printStackTrace();
			WebSocket.Listener.super.onError(webSocket, error);
		}
	}
	
	public static String getOtp(String emailId){
		int otpExpTime = AdminTestUtil.getOtpExpTimeFromActuator();
		int otpCheckLoopCount = (otpExpTime * 1000)/AdminTestUtil.OTP_CHECK_INTERVAL;
		
		int counter = 0;
		
		//HashMap m=new HashMap<Object, Object>();
		String otp = "";
		while (counter < otpCheckLoopCount) {
		//	m= emailNotificationMap;
			if(emailNotificationMapS.get(emailId)!=null) {
				String html=(String) emailNotificationMapS.get(emailId);
				//as we alredy consumed notification removed from map
				emailNotificationMapS.remove(emailId);	
				otp = parseOtp(html);
				if (otp != null && otp.length()>0) {
//					Got the required OTP Ignore in between notification which doesn't have OTP
					return otp;
				}
				
			}
			System.out.println("*******Checking the email for OTP...*******");
			counter++;
			try {
				System.out.println("Not received Otp yet, waiting for 10 Sec");
				Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
		}
		System.out.println("OTP not found even after " + otpCheckLoopCount + " retries");
		return otp;
	}
	
	public static String parseOtp(String message){
		
		// To Do Key entry found add parsing logic for OTP
		
//		Dear FR OTP for UIN XXXXXXXX02 is 111111 and is valid for 3 minutes. (Generated on 16-03-2023 at 15:43:39 Hrs)
//
//		عزيزي $ name OTP لـ $ idvidType $ idvid هو $ otp وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)
//
//		Cher $name_fra, OTP pour UIN XXXXXXXX02 est 111111 et est valide pour 3 minutes. (Généré le 16-03-2023 à 15:43:39 Hrs)
		
//		"Dear FR OTP for UIN XXXXXXXX02 is 111111 and is valid for 3 minutes. (Generated on 16-03-2023 at 15:43:39 Hrs)\r\n"
//		+ "\r\n"
//		+ "عزيزي $ name OTP لـ $ idvidType $ idvid هو $ 101010 وهو صالح لـ $ validTime دقيقة. (تم إنشاؤه في $ date في $ time Hrs)\r\n"
//		+ "\r\n"
//		+ "Cher $name_fra, OTP pour UIN XXXXXXXX02 est 123456 et est valide pour 3 minutes. (Généré le 16-03-2023 à 15:43:39 Hrs)";
		
		//find any 6 digit number
		Pattern mPattern = Pattern.compile("(|^)\\s\\d{6}\\s");
		String otp = "";
		if(message!=null) {
		    Matcher mMatcher = mPattern.matcher(message);
		    if(mMatcher.find()) {
		        otp = mMatcher.group(0);
		        otp = otp.trim();
		        System.out.println("Extracted OTP: "+ otp+ " message : "+ message);
		    }else {
		        //something went wrong
		    	System.out.println("Failed to extract the OTP!! "+ "message : " + message);
		    	
		    }
		}
		return otp;
	}
	
}