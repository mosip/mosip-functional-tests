package io.mosip.testrig.uitestrig.browserstack;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.mosip.testrig.uitestrig.browser.PlaywrightFactory;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks for BrowserStack session management.
 *
 * <p>When {@code browserstack.enabled=yes} is configured, these hooks replace the local browser
 * launch/close behavior from {@code CucumberHooks} with a BrowserStack cloud session.
 *
 * <h2>Hook ordering</h2>
 * <pre>
 *   @Before(order=-5)  BrowserStackHooks  — registers BS session into PlaywrightFactory BEFORE
 *   @Before(order= 0)  CucumberHooks      — calls initPage() which finds existing session, skips launch
 *   ... scenario runs on BrowserStack ...
 *   @After(order= 5)   BrowserStackHooks  — marks session passed/failed, logs session URL
 *   @After(order= 0)   CucumberHooks      — closes the page/browser/playwright
 * </pre>
 *
 * <h2>Glue path activation</h2>
 * Add this package to the Cucumber {@code glue} configuration in your test runner
 * alongside {@code io.mosip.testrig.uitestrig.listeners}:
 * <pre>
 *   @ConfigurationParameter(key = GLUE_PROPERTY_NAME,
 *       value = "io.mosip.testrig.uitestrig.listeners," +
 *               "io.mosip.testrig.uitestrig.browserstack," +   // add this
 *               "io.mosip.testrig.residentui.steps,...")
 * </pre>
 *
 * <h2>When BrowserStack is disabled</h2>
 * Both hooks are no-ops when {@code browserstack.enabled != yes}, so this class can remain in the
 * glue path permanently — enabling/disabling is purely a configuration change.
 */
public class BrowserStackHooks {

    private static final Logger logger = LoggerFactory.getLogger(BrowserStackHooks.class);

    /**
     * Runs before {@code CucumberHooks.@Before(order=0)}.
     * Initialises a BrowserStack session and registers it in {@link PlaywrightFactory} so that
     * the standard {@code CucumberHooks.initPage()} call finds the session already registered
     * and skips local browser launch.
     */
    @Before(order = -5)
    public void startBrowserStackSession(Scenario scenario) {
        if (!BrowserStackManager.isEnabled()) return;

        logger.info("BrowserStack enabled — initialising remote session for: {}", scenario.getName());
        try {
            BrowserStackManager.initPage(scenario.getName());
        } catch (Exception e) {
            logger.error("Failed to initialise BrowserStack session: {}", e.getMessage());
            throw new RuntimeException("BrowserStack session init failed: " + e.getMessage(), e);
        }
    }

    /**
     * Runs before {@code CucumberHooks.@After(order=0)} (higher order = runs first for @After).
     *
     * <ul>
     *   <li>Marks the BrowserStack session as passed or failed.</li>
     *   <li>Logs the session video and public URL to the Extent report.</li>
     * </ul>
     *
     * Must execute BEFORE {@code CucumberHooks} closes the page, since marking requires the
     * page's CDP connection to still be open.
     */
    @After(order = 5)
    public void closeBrowserStackSession(Scenario scenario) {
        if (!BrowserStackManager.isEnabled()) return;

        Page page;
        try {
            page = PlaywrightFactory.getPage();
        } catch (IllegalStateException e) {
            logger.warn("BrowserStack @After: no page available — session may already be closed");
            return;
        }

        try {
            if (scenario.isFailed()) {
                BrowserStackManager.markTestFailed(page,
                        "Scenario failed: " + scenario.getName());
            } else {
                BrowserStackManager.markTestPassed(page,
                        "Scenario passed: " + scenario.getName());
            }

            String videoUrl  = BrowserStackManager.getSessionVideoUrl(page);
            String publicUrl = BrowserStackManager.getSessionPublicUrl(page);

            if (!publicUrl.isBlank()) {
                logger.info("BrowserStack session: {}", publicUrl);
                var test = UiExtentReportManager.getTest();
                if (test != null) {
                    test.info("BrowserStack session: <a href='" + publicUrl
                            + "' target='_blank'>View on Automate</a>");
                    if (!videoUrl.isBlank()) {
                        test.info("Session video: <a href='" + videoUrl
                                + "' target='_blank'>Watch recording</a>");
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error during BrowserStack session teardown: {}", e.getMessage());
        }
    }
}
