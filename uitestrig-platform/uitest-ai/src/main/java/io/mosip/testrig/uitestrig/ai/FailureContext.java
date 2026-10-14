package io.mosip.testrig.uitestrig.ai;

import java.util.List;

/**
 * Immutable snapshot of everything observable at the point of a Cucumber scenario failure.
 *
 * <p>Built by {@link FailureCollector#buildContext} immediately before the page is closed.
 * Passed to {@link FailureAnalyzer#analyze} which sends it to the configured LLM provider.
 *
 * <p>Fields that could not be captured (e.g. screenshot when page was already closed) are
 * represented as empty strings or empty lists — never {@code null}.
 *
 * @param scenarioName      Cucumber scenario name
 * @param failedStep        text of the step that failed (best-effort — may be empty)
 * @param pageUrl           URL at point of failure
 * @param stackTrace        exception stack trace from the failing step
 * @param screenshotBase64  full-page PNG encoded as Base64, or {@code ""} if unavailable
 * @param pageHtml          page HTML at point of failure, truncated to
 *                          {@value #HTML_TRUNCATE_CHARS} characters
 * @param consoleErrors     browser console {@code error} messages collected during the scenario
 * @param networkFailures   HTTP 4xx/5xx responses observed during the scenario
 */
public record FailureContext(
        String       scenarioName,
        String       failedStep,
        String       pageUrl,
        String       stackTrace,
        String       screenshotBase64,
        String       pageHtml,
        List<String> consoleErrors,
        List<String> networkFailures
) {

    static final int HTML_TRUNCATE_CHARS = 4000;

    public FailureContext {
        scenarioName    = scenarioName    != null ? scenarioName    : "";
        failedStep      = failedStep      != null ? failedStep      : "";
        pageUrl         = pageUrl         != null ? pageUrl         : "";
        stackTrace      = stackTrace      != null ? stackTrace      : "";
        screenshotBase64= screenshotBase64!= null ? screenshotBase64: "";
        pageHtml        = pageHtml        != null ? pageHtml        : "";
        consoleErrors   = consoleErrors   != null ? List.copyOf(consoleErrors)   : List.of();
        networkFailures = networkFailures != null ? List.copyOf(networkFailures) : List.of();
    }

    /** Returns {@code true} if a screenshot was captured. */
    public boolean hasScreenshot() {
        return !screenshotBase64.isEmpty();
    }

    /** Returns the number of observable signals (console + network errors). */
    public int signalCount() {
        return consoleErrors.size() + networkFailures.size();
    }
}
