package io.mosip.testrig.apirig.kernel.util;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackChannelIntegration {
	private static final Logger logger = LoggerFactory.getLogger(SlackChannelIntegration.class);
	static String reportUrl="http://automation.mosip.net/MosipTestResource/mosip_e2e_emailable_report.html";
	static Slack slack = Slack.getInstance();
	static String token  = "xoxb-831351446070-1751917508130-IAFVzzZlKb5dEMwQKmfdcWAP";
	static String defaultChannel = "#automation-slack-integration";
	
	public static Boolean postMessage(String channelName, String message) {
	
		MethodsClient methods = slack.methods(token);
	    // Build a request object
	    ChatPostMessageRequest request = ChatPostMessageRequest.builder()
	   .channel(channelName) // Use a channel ID `C1234567` is preferrable
	   .text(message)
	   .build();

	    // Get a response as a Java object
	    try {
			ChatPostMessageResponse response = methods.chatPostMessage(request);
			if(response.isOk())
				return true;
			
	    } catch (IOException | SlackApiException e) {
			logger.error(e.getMessage());
		}
	    return false;
	}
	
	 public static void main(String[] args) throws Exception {
		 
		 postMessage(defaultChannel, "Test message from Automation");
	 }
}
