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
 * HTTP client for the <a href="https://docs.anthropic.com/en/api">Anthropic Claude API</a>.
 *
 * <p>Sends a {@code POST /v1/messages} request and returns the assistant's text response.
 *
 * <p>Configuration:
 * <pre>
 *   AI_API_KEY env var — Anthropic API key (never put in properties files)
 *   ai.model           — Claude model ID, default {@code claude-haiku-4-5-20251001}
 * </pre>
 *
 * <p>Vision is supported for models that include it (Haiku 4.5 and above).
 * Set {@code ai.includeScreenshot=yes} to include the failure screenshot in the prompt.
 */
public class ClaudeClient {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeClient.class);
    private static final String ENDPOINT  = "https://api.anthropic.com/v1/messages";
    private static final String API_VERSION = "2023-06-01";
    private static final int    MAX_TOKENS  = 1024;
    private static final int    TIMEOUT_SEC = 60;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ClaudeClient() {}

    /**
     * Sends the prompt to Claude and returns the assistant's text response.
     *
     * @param model  Claude model ID, e.g. {@code "claude-haiku-4-5-20251001"}
     * @param prompt full prompt text
     * @param ctx    failure context (screenshot included when {@code ai.includeScreenshot=yes})
     * @return assistant response text, or {@code ""} on error
     */
    public static String call(String model, String prompt, FailureContext ctx) {
        String apiKey = System.getenv("AI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("AI_API_KEY env var not set — Claude call skipped");
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
                    .header("Content-Type",    "application/json")
                    .header("x-api-key",       apiKey)
                    .header("anthropic-version", API_VERSION)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                logger.warn("Claude API returned HTTP {}: {}", resp.statusCode(), resp.body());
                return "";
            }
            JsonNode json = MAPPER.readTree(resp.body());
            return json.path("content").path(0).path("text").asText("");

        } catch (Exception e) {
            logger.warn("Claude API call failed: {}", e.getMessage());
            return "";
        }
    }

    private static String buildTextRequest(String model, String prompt) {
        var node = MAPPER.createObjectNode();
        node.put("model", model);
        node.put("max_tokens", MAX_TOKENS);
        var message = node.putArray("messages").addObject();
        message.put("role", "user");
        message.put("content", prompt);
        return node.toString();
    }

    private static String buildVisionRequest(String model, String prompt, String screenshotBase64) {
        var node = MAPPER.createObjectNode();
        node.put("model", model);
        node.put("max_tokens", MAX_TOKENS);
        var message = node.putArray("messages").addObject();
        message.put("role", "user");
        var content = message.putArray("content");
        var image = content.addObject();
        image.put("type", "image");
        image.putObject("source")
                .put("type", "base64")
                .put("media_type", "image/png")
                .put("data", screenshotBase64);
        content.addObject().put("type", "text").put("text", prompt);
        return node.toString();
    }
}
