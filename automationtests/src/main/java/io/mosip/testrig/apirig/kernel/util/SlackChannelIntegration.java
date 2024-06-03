package io.mosip.testrig.apirig.kernel.util;
import okhttp3.*;
import java.io.IOException;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.service.BaseTestCase;
public class SlackChannelIntegration extends BaseTestCase  {
	private static final Logger LOGGER = Logger.getLogger(SlackChannelIntegration.class);
    private static final String SLACK_WEBHOOK_URL =ConfigManager.getSlackWebHookUrl();
    

    public static void sendMessageToSlack(String message) {
    	LOGGER.info("SLACK_WEBHOOK_URL is - " + SLACK_WEBHOOK_URL);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\"text\":\"" + message + "\"}";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(SLACK_WEBHOOK_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            	LOGGER.error("Exception on slack integration " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}