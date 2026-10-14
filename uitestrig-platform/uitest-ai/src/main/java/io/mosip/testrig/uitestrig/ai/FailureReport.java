package io.mosip.testrig.uitestrig.ai;

/**
 * AI-generated failure analysis report for a failing Cucumber scenario.
 *
 * <p>Produced by {@link FailureAnalyzer#analyze(FailureContext)}.
 * All fields are guaranteed non-null; unknown or inapplicable fields default to empty string / 0.
 *
 * @param likelyCause       plain-English description of the probable root cause
 * @param confidencePercent LLM's confidence in the diagnosis (0–100)
 * @param suggestedFix      concrete action the developer should take to resolve the failure
 * @param suggestedLocator  alternative CSS/XPath selector to try when the failure looks like a
 *                          locator breakage; {@code ""} for non-locator failures
 * @param provider          name of the LLM provider that produced this report
 * @param modelId           model ID used, e.g. {@code "llama3"}, {@code "claude-haiku-4-5-20251001"}
 */
public record FailureReport(
        String likelyCause,
        int    confidencePercent,
        String suggestedFix,
        String suggestedLocator,
        String provider,
        String modelId
) {

    public FailureReport {
        likelyCause      = likelyCause      != null ? likelyCause      : "";
        suggestedFix     = suggestedFix     != null ? suggestedFix     : "";
        suggestedLocator = suggestedLocator != null ? suggestedLocator : "";
        provider         = provider         != null ? provider         : "";
        modelId          = modelId          != null ? modelId          : "";
        confidencePercent = Math.max(0, Math.min(100, confidencePercent));
    }

    /**
     * Returns a compact text summary for logging and Extent report entries.
     */
    public String toSummary() {
        String locatorPart = suggestedLocator.isEmpty()
                ? "" : "\n  Suggested locator: " + suggestedLocator;
        return "🤖 AI Analysis [" + provider + "/" + modelId + "] (confidence " + confidencePercent + "%)" +
               "\n  Likely cause: "    + likelyCause  +
               "\n  Suggested fix: "  + suggestedFix  +
               locatorPart;
    }

    /** A synthetic report used when the AI call fails or is unavailable. */
    static FailureReport unavailable(String reason) {
        return new FailureReport(
                "AI analysis unavailable: " + reason, 0, "", "", "n/a", "n/a");
    }
}
