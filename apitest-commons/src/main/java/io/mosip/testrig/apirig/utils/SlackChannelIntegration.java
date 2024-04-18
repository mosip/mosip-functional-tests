package io.mosip.testrig.apirig.utils;
import java.io.IOException;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class SlackChannelIntegration extends BaseTestCase  {
    private static final String SLACK_WEBHOOK_URL =ConfigManager.getSlackWebHookUrl();
    

    public static void sendMessageToSlack(String message) {
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
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}