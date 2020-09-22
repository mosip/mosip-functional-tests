package io.mosip.authentication.demo.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.demo.service.dto.EventModel;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

@RestController
public class WebSub {

	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> pb;

	@Value("${websub.publisher.url}")
	private String publisherUrl;

	@Autowired
	SubscriptionClient<SubscriptionChangeRequest,UnsubscriptionRequest, SubscriptionChangeResponse> sb;
	
	@Value("${websub.hub.url}")
	private String hubUrl;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${websub.secret}")
	private String webSubSecret;
	
	@PostMapping(path = "/registerTopicToPublish", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<String> registerTopicToPublish(@RequestParam(name = "topic", required = true) String topic) {
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();

		pb.registerTopic(topic, publisherUrl);

		responseWrapper.setResponse("Registered topic for Publishing: " + topic);

		return responseWrapper;
	}
	
	@PostMapping(path = "/unregisterTopicToPublish", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<String> unregisterTopicToPublish(@RequestParam(name = "topic", required = true) String topic) {
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();

		pb.unregisterTopic(topic, publisherUrl);

		responseWrapper.setResponse("Unregistered topic for Publishing: " + topic);

		return responseWrapper;
	}

	@PostMapping(path = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<String> publish(@RequestBody EventModel body,
			@RequestParam(name = "topic", required = true) String topic) {
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();

		//pb.registerTopic(topic, publisherUrl);

		pb.publishUpdate(topic, body, MediaType.APPLICATION_JSON_UTF8_VALUE, new HttpHeaders(), publisherUrl);
		
		//pb.unregisterTopic(topic, publisherUrl);

		responseWrapper.setResponse("Published for topic: " + topic);

		return responseWrapper;
	}

	@PostMapping(value = "/callback", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2", callback = "/callback", topic = "mytopic1")
	public void printPost(@RequestBody EventModel body) {
		try {
			System.out.println(objectMapper.writeValueAsString(body));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@PostMapping(path = "/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<String> subscribe(@RequestParam(name = "topic", required = true) String topic) {
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();
		final String baseUrl = 
				ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
        subscriptionRequest.setCallbackURL(baseUrl + "/callback");
        subscriptionRequest.setHubURL(hubUrl);
        subscriptionRequest.setSecret(webSubSecret);
        subscriptionRequest.setTopic(topic);
        sb.subscribe(subscriptionRequest);

		responseWrapper.setResponse("Subscribed for topic: " + topic);

		return responseWrapper;
	}
	
	@PostMapping(path = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<String> publish(@RequestParam(name = "topic", required = true) String topic) {
		ResponseWrapper<String> responseWrapper = new ResponseWrapper<>();

		UnsubscriptionRequest subscriptionRequest = new UnsubscriptionRequest();
        subscriptionRequest.setCallbackURL("/callback");
        subscriptionRequest.setHubURL(hubUrl);
        subscriptionRequest.setTopic(topic);
        sb.unSubscribe(subscriptionRequest);

		responseWrapper.setResponse("Unsubscribed for topic: " + topic);

		return responseWrapper;
	}

}
