package io.mosip.testrig.uitestrig.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.testrig.uitestrig.ai.FailureContext;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP client for the <a href="https://ollama.com">Ollama</a> local inference API.
 *
 * <p>Sends a {@code POST /api/generate} request to the locally running Ollama server and
 * returns the raw text response from the model.
 *
 * <p>Configuration ({@code uitest.properties} / env vars):
 * <pre>
 *   ai.ollamaBaseUrl = http://localhost:11434   (default)
 *   ai.model         = llama3                  (default; any model pulled in Ollama)
 * </pre>
 *
 * <p>Install Ollama and pull a model once:
 * <pre>
 *   curl -fsSL https://ollama.com/install.sh | sh
 *   ollama pull llama3        # text analysis
 *   ollama pull llava         # vision (includes screenshot in prompt)
 * </pre>
 */
public class OllamaClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final int    TIMEOUT_SECONDS  = 120;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OllamaClient() {}

    /**
     * Sends the prompt to Ollama and returns the model's raw text response.
     *
     * @param model  Ollama model name, e.g. {@code "llama3"}, {@code "mistral"}, {@code "llava"}
     * @param prompt the full prompt string
     * @param ctx    failure context (used to optionally include the screenshot for vision models)
     * @return model response text, or {@code ""} on error
     */
    public static String call(String model, String prompt, FailureContext ctx) {
        String baseUrl = UiConfigManager.getproperty("ai.ollamaBaseUrl");
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = DEFAULT_BASE_URL;

        boolean isVisionModel = model.contains("llava") || model.contains("vision");
        String requestJson = buildRequest(model, prompt, isVisionModel ? ctx.screenshotBase64() : "");

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/generate"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                logger.warn("Ollama returned HTTP {}: {}", resp.statusCode(), resp.body());
                return "";
            }
            JsonNode json = MAPPER.readTree(resp.body());
            return json.path("response").asText("");

        } catch (Exception e) {
            logger.warn("Ollama call failed: {}", e.getMessage());
            return "";
        }
    }

    private static String buildRequest(String model, String prompt, String screenshotBase64) {
        String escapedPrompt = jsonEscape(prompt);
        if (screenshotBase64 != null && !screenshotBase64.isBlank()) {
            return "{\"model\":\"" + model + "\",\"prompt\":\"" + escapedPrompt + "\"," +
                   "\"images\":[\"" + screenshotBase64 + "\"],\"stream\":false}";
        }
        return "{\"model\":\"" + model + "\",\"prompt\":\"" + escapedPrompt + "\",\"stream\":false}";
    }

    static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
