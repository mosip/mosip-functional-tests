package io.mosip.testrig.apirig.kernel.util;
import okhttp3.*;
import java.io.IOException;

import io.mosip.testrig.apirig.service.BaseTestCase;
public class SlackChannelIntegration extends BaseTestCase  {
    private static final String SLACK_WEBHOOK_URL =propsKernel.getProperty("slackWebHookUrl");

	/*
	 * public static void main(String[] args) {
	 * sendMessageToSlack("Hey, How are you doing!"); }
	 */
    public static void sendMessageToSlack(String message) {
        OkHttpClient client = new OkHttpClient();
        // Create JSON body for the request
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\"text\":\"" + message + "\"}";
        RequestBody body = RequestBody.create(json, mediaType);
        // Create the HTTP request
        Request request = new Request.Builder()
                .url(SLACK_WEBHOOK_URL)
                .post(body)
                .build();
        // Execute the request and handle the response
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}