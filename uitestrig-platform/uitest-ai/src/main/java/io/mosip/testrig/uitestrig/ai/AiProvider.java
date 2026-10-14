package io.mosip.testrig.uitestrig.ai;

/**
 * LLM provider options for AI failure analysis.
 *
 * <p>Set via {@code ai.provider} in {@code uitest.properties} or the {@code AI_PROVIDER}
 * environment variable.  Defaults to {@link #OLLAMA} when not specified.
 *
 * <ul>
 *   <li>{@link #OLLAMA}  — free, privacy-safe, runs locally via Ollama.
 *       Install: {@code curl -fsSL https://ollama.com/install.sh | sh && ollama pull llama3}
 *       Config: {@code ai.ollamaBaseUrl=http://localhost:11434}, {@code ai.model=llama3}</li>
 *   <li>{@link #CLAUDE}  — Anthropic Claude API (paid, best diagnostic quality).
 *       Config: {@code AI_API_KEY} env var, {@code ai.model=claude-haiku-4-5-20251001}</li>
 *   <li>{@link #OPENAI}  — OpenAI chat completions API (paid).
 *       Config: {@code AI_API_KEY} env var, {@code ai.model=gpt-4o-mini}</li>
 * </ul>
 */
public enum AiProvider {

    OLLAMA("llama3"),
    CLAUDE("claude-haiku-4-5-20251001"),
    OPENAI("gpt-4o-mini");

    private final String defaultModel;

    AiProvider(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    /** Default model ID to use when {@code ai.model} is not explicitly configured. */
    public String getDefaultModel() {
        return defaultModel;
    }

    /**
     * Parses a provider name case-insensitively.
     * Returns {@link #OLLAMA} as the default when the value is blank or unrecognised.
     */
    public static AiProvider fromConfig(String value) {
        if (value == null || value.isBlank()) return OLLAMA;
        return switch (value.toLowerCase().trim()) {
            case "claude", "anthropic" -> CLAUDE;
            case "openai", "gpt"       -> OPENAI;
            default                    -> OLLAMA;
        };
    }
}
