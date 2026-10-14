package io.mosip.testrig.uitestrig.ai;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.mosip.testrig.uitestrig.browser.PlaywrightFactory;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks that wire AI failure analysis into every scenario automatically.
 *
 * <h2>Hook ordering</h2>
 * <pre>
 *   @Before(order=-5)  BrowserStackHooks  — registers BS session (if enabled)
 *   @Before(order= 0)  CucumberHooks      — creates Playwright page
 *   @Before(order=10)  AiHooks            — registers console/network listeners on the page
 *   ... scenario runs ...
 *   @After(order=15)   AiHooks            — captures context, runs AI analysis on failure
 *   @After(order= 5)   BrowserStackHooks  — marks BS session passed/failed
 *   @After(order= 0)   CucumberHooks      — closes page
 * </pre>
 *
 * <h2>Activation</h2>
 * <ol>
 *   <li>Add {@code uitest-ai:1.0.0-SNAPSHOT} to your module's {@code pom.xml}.</li>
 *   <li>Add {@code io.mosip.testrig.uitestrig.ai} to the Cucumber glue path.</li>
 *   <li>Set {@code ai.enabled=yes} and {@code ai.provider=ollama|claude|openai} in
 *       {@code uitest.properties} (or env vars).</li>
 * </ol>
 *
 * <h2>When disabled</h2>
 * Both hooks are no-ops when {@code ai.enabled != yes}, so this class can remain in the
 * glue path permanently — enabling/disabling is purely a configuration change.
 */
public class AiHooks {

    private static final Logger logger = LoggerFactory.getLogger(AiHooks.class);

    /**
     * Registers {@link FailureCollector} event listeners on the newly created page.
     * Runs after {@code CucumberHooks.@Before(0)} has initialised the page.
     */
    @Before(order = 10)
    public void registerCollector(Scenario scenario) {
        if (!FailureAnalyzer.isEnabled()) return;
        try {
            Page page = PlaywrightFactory.getPage();
            FailureCollector.register(page);
            logger.debug("AI FailureCollector registered for: {}", scenario.getName());
        } catch (Exception e) {
            logger.warn("Could not register AI FailureCollector: {}", e.getMessage());
        }
    }

    /**
     * On scenario failure: captures context, runs AI analysis, logs the report to the
     * Extent report and saves a text file to {@code target/ai-analysis/}.
     *
     * <p>Runs at {@code @After(order=15)} — before BrowserStack hooks (5) and
     * CucumberHooks page-close (0), so the page is still open for screenshot + HTML capture.
     */
    @After(order = 15)
    public void analyzeFailure(Scenario scenario) {
        if (!FailureAnalyzer.isEnabled()) return;
        if (!scenario.isFailed()) {
            FailureCollector.reset();
            return;
        }

        logger.info("AI failure analysis triggered for: {}", scenario.getName());
        try {
            Page page = PlaywrightFactory.getPage();

            // io.cucumber.java.Scenario has no public API for the causing exception or the
            // failed step text, so this is the status/name only — a real stack trace would
            // need a TestStepFinished event listener registered as a separate Cucumber plugin.
            String stackTrace = "";
            try {
                stackTrace = scenario.getStatus().name() + " — " + scenario.getName();
            } catch (Exception ignored) {}

            FailureContext ctx = FailureCollector.buildContext(
                    page,
                    scenario.getName(),
                    "",    // step text not exposed by Cucumber scenario object
                    stackTrace
            );

            FailureReport report = FailureAnalyzer.analyze(ctx);
            logReportToExtent(report);
            logReportToScenario(report, scenario);

        } catch (Exception e) {
            logger.warn("AI failure analysis error: {}", e.getMessage());
        } finally {
            FailureCollector.reset();
        }
    }

    // ── Report output helpers ─────────────────────────────────────────────────

    private static void logReportToExtent(FailureReport report) {
        try {
            var test = UiExtentReportManager.getTest();
            if (test == null) return;
            test.info(report.toSummary().replace("\n", "<br>"));
            if (!report.suggestedLocator().isEmpty()) {
                test.info("🔧 Healed locator suggestion: <code>" +
                          escapeHtml(report.suggestedLocator()) + "</code>");
            }
        } catch (Exception e) {
            logger.debug("Could not write AI report to Extent: {}", e.getMessage());
        }
    }

    private static void logReportToScenario(FailureReport report, Scenario scenario) {
        try {
            // Attaches as a plain-text entry visible in the Cucumber HTML report
            String text = report.toSummary();
            scenario.attach(text.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    "text/plain", "AI Analysis");
        } catch (Exception e) {
            logger.debug("Could not attach AI report to scenario: {}", e.getMessage());
        }
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
