package io.mosip.testrig.uitestrig.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.testrig.uitestrig.ai.FailureContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP client for the <a href="https://platform.openai.com/docs/api-reference/chat">OpenAI
 * Chat Completions API</a>.
 *
 * <p>Sends a {@code POST /v1/chat/completions} request and returns the assistant's text response.
 *
 * <p>Configuration:
 * <pre>
 *   AI_API_KEY env var — OpenAI API key (never put in properties files)
 *   ai.model           — model ID, default {@code gpt-4o-mini}
 * </pre>
 *
 * <p>Vision is supported for {@code gpt-4o} and {@code gpt-4o-mini}.
 * Set the {@code AI_INCLUDE_SCREENSHOT=yes} environment variable to include the failure
 * screenshot as an image URL in the prompt.
 */
public class OpenAiClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiClient.class);
    private static final String ENDPOINT   = "https://api.openai.com/v1/chat/completions";
    private static final int    MAX_TOKENS  = 1024;
    private static final int    TIMEOUT_SEC = 60;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OpenAiClient() {}

    /**
     * Sends the prompt to OpenAI and returns the assistant message content.
     *
     * @param model  OpenAI model ID, e.g. {@code "gpt-4o-mini"}, {@code "gpt-4o"}
     * @param prompt full prompt text
     * @param ctx    failure context (screenshot included when {@code AI_INCLUDE_SCREENSHOT=yes})
     * @return assistant content text, or {@code ""} on error
     */
    public static String call(String model, String prompt, FailureContext ctx) {
        String apiKey = System.getenv("AI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("AI_API_KEY env var not set — OpenAI call skipped");
            return "";
        }

        boolean includeScreenshot = ctx.hasScreenshot()
                && "yes".equalsIgnoreCase(System.getenv("AI_INCLUDE_SCREENSHOT"));

        String requestJson = includeScreenshot
                ? buildVisionRequest(model, prompt, ctx.screenshotBase64())
                : buildTextRequest(model, prompt);

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Content-Type",  "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                logger.warn("OpenAI API returned HTTP {}: {}", resp.statusCode(), resp.body());
                return "";
            }
            JsonNode json = MAPPER.readTree(resp.body());
            return json.path("choices").path(0).path("message").path("content").asText("");

        } catch (Exception e) {
            logger.warn("OpenAI API call failed: {}", e.getMessage());
            return "";
        }
    }

    private static String buildTextRequest(String model, String prompt) {
        String p = OllamaClient.jsonEscape(prompt);
        return "{\"model\":\"" + model + "\",\"max_tokens\":" + MAX_TOKENS + "," +
               "\"messages\":[{\"role\":\"user\",\"content\":\"" + p + "\"}]}";
    }

    private static String buildVisionRequest(String model, String prompt, String screenshotBase64) {
        String p = OllamaClient.jsonEscape(prompt);
        return "{\"model\":\"" + model + "\",\"max_tokens\":" + MAX_TOKENS + "," +
               "\"messages\":[{\"role\":\"user\",\"content\":[" +
               "{\"type\":\"image_url\",\"image_url\":{\"url\":\"data:image/png;base64," +
               screenshotBase64 + "\"}}," +
               "{\"type\":\"text\",\"text\":\"" + p + "\"}" +
               "]}]}";
    }
}
