package io.mosip.testrig.uitestrig.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotAnimations;
import io.cucumber.java.Scenario;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import io.mosip.testrig.uitestrig.browser.PlaywrightFactory;
import io.mosip.testrig.uitestrig.browser.SmartWait;
import io.mosip.testrig.uitestrig.core.MosipTestDataManager;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import io.mosip.testrig.uitestrig.reporting.KnownIssueManager;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Utility methods for Cucumber lifecycle management.
 *
 * No Cucumber hook annotations here — each module owns its own @Before/@After/@BeforeAll/@AfterAll
 * hooks and calls these static methods explicitly. This keeps lifecycle control in the module
 * and makes execution order transparent and debuggable.
 *
 * Running specific scenarios: set the {@value #SCENARIO_FILTER_PROPERTY} system property to a
 * scenario name or comma-separated list of names (case-insensitive) and run the suite normally
 * — every other scenario is excluded before it creates a browser or an Extent entry. Works for
 * any module that calls {@link #setupScenario}/{@link #teardownScenario}; a project can bridge
 * it in from its own properties file via {@code System.setProperty(SCENARIO_FILTER_PROPERTY, value)}.
 *
 * Typical module usage:
 *
 *   @BeforeAll(order = 5)
 *   public static void initSuite() {
 *       XxxConfigManager.init();
 *       MosipTestDataManager.initialize();
 *       UiExtentReportManager.initReport();
 *       CucumberHooks.startOtpListener();
 *   }
 *
 *   @Before
 *   public void scenarioSetup(Scenario scenario) {
 *       CucumberHooks.setupScenario(scenario);
 *   }
 *
 *   @BeforeStep
 *   public void stepSetup() {
 *       CucumberHooks.beforeStep();
 *   }
 *
 *   @After
 *   public void scenarioTeardown(Scenario scenario) {
 *       CucumberHooks.teardownScenario(scenario);
 *   }
 *
 *   @AfterAll
 *   public static void teardownSuite() {
 *       CucumberHooks.teardownSuite();
 *   }
 */
public class CucumberHooks {

    private static final Logger logger = LoggerFactory.getLogger(CucumberHooks.class);
    private static Thread otpListenerThread;
    private static final ThreadLocal<String> knownIssueBugId = new ThreadLocal<>();

    // See class javadoc. A plain system property so it works the same regardless of which
    // project's config manager (or none) sets it.
    public static final String SCENARIO_FILTER_PROPERTY = "scenarioToRun";
    private static final ThreadLocal<Boolean> filteredOut = ThreadLocal.withInitial(() -> false);

    /**
     * Per-scenario setup: scenario-name filter, known-issue check, Extent test creation,
     * browser init, navigation. Call from module's @Before hook.
     */
    public static void setupScenario(Scenario scenario) {
        String only = System.getProperty(SCENARIO_FILTER_PROPERTY);
        if (only != null && !only.isBlank() && !matchesFilter(scenario.getName(), only)) {
            filteredOut.set(true);
            throw new TestAbortedException(SCENARIO_FILTER_PROPERTY + "=\"" + only + "\" does not match this scenario");
        }
        filteredOut.set(false);

        String bugId = KnownIssueManager.getBugId(scenario.getName());
        if (bugId != null) {
            knownIssueBugId.set(bugId);
            logger.info("Known Issue [{}]: skipping '{}'", bugId, scenario.getName());
            throw new TestAbortedException("Known Issue [" + bugId + "]: " + scenario.getName());
        }
        knownIssueBugId.remove();

        logger.info("Starting scenario: {}", scenario.getName());
        ExtentTest test = UiExtentReportManager.createTest(scenario.getName());
        assignTagsAsCategories(test, scenario);

        Page page = PlaywrightFactory.initPage();
        page.navigate(UiConfigManager.getBaseUrl());
        SmartWait.forNetworkIdle(page);
    }

    private static boolean matchesFilter(String scenarioName, String filter) {
        for (String name : filter.split(",")) {
            if (scenarioName.trim().equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }

    /**
     * Fires before each step — logs the numbered step divider. Call from module's @BeforeStep
     * hook. Runs synchronously on the scenario's thread, so the divider lands at the real
     * time the step ran; Cucumber gives no API for the step's own text here.
     */
    public static void beforeStep() {
        UiExtentReportManager.logStepStart();
    }

    /**
     * Per-scenario teardown: screenshot on failure, Extent report update, browser close.
     * Call from module's @After hook.
     */
    public static void teardownScenario(Scenario scenario) {
        if (filteredOut.get()) {
            filteredOut.remove();
            return;
        }

        String bugId = knownIssueBugId.get();
        knownIssueBugId.remove();

        if (bugId != null) {
            UiExtentReportManager.incrementKnownIssue();
            UiExtentReportManager.createTest(scenario.getName());
            String safeId = bugId.matches("[A-Z][A-Z0-9]+-\\d+") ? bugId : "INVALID-ID";
            String bugUrl = "https://mosip.atlassian.net/browse/" + safeId;
            UiExtentReportManager.logSkip("🟠 Known Issue → <a href='" + bugUrl + "' target='_blank'>" + safeId + "</a>");
            UiExtentReportManager.removeTest();
            logger.info("Known Issue [{}]: '{}' reported as skip", bugId, scenario.getName());
            return;
        }

        if (scenario.isFailed()) {
            UiExtentReportManager.incrementFailed();
            UiExtentReportManager.logFailure("Scenario failed: " + scenario.getName(), null, null);
            // Skip if a BasePage action already captured a step-level screenshot for this failure.
            if (!UiExtentReportManager.wasStepFailureScreenshotTaken()) {
                try {
                    byte[] screenshot = PlaywrightFactory.getPage()
                            .screenshot(new Page.ScreenshotOptions()
                                    .setFullPage(true)
                                    .setAnimations(ScreenshotAnimations.DISABLED)
                                    .setTimeout(10_000));
                    String screenshotBase64 = Base64.getEncoder().encodeToString(screenshot);
                    UiExtentReportManager.attachScreenshot(screenshotBase64, "Scenario Failure Screenshot");
                    scenario.attach(screenshot, "image/png", "failure-screenshot");
                    logger.warn("Scenario FAILED: {} — screenshot attached", scenario.getName());
                } catch (Exception e) {
                    logger.error("Could not capture failure screenshot: {}", e.getMessage());
                }
            }
        } else if ("SKIPPED".equalsIgnoreCase(scenario.getStatus().toString())) {
            UiExtentReportManager.incrementSkipped();
            UiExtentReportManager.logSkip("⚠️ Scenario skipped: " + scenario.getName());
        } else {
            UiExtentReportManager.incrementPassed();
            UiExtentReportManager.logPass("Scenario passed: " + scenario.getName());
        }

        UiExtentReportManager.removeTest();
        PlaywrightFactory.closePage();
    }

    /**
     * Suite-level teardown: stop OTP listener, Keycloak cleanup, flush Extent report.
     * Call from module's @AfterAll hook.
     */
    public static void teardownSuite() {
        OTPListener.bTerminate = true;
        try {
            MosipTestDataManager.cleanup();
        } catch (Exception e) {
            logger.error("MosipTestDataManager.cleanup() failed during teardown; report will still be flushed", e);
        }
        UiExtentReportManager.flush();
        logger.info("Suite teardown complete");
    }

    /**
     * Assigns the scenario's Gherkin tags (minus the leading {@code @}) as Extent categories,
     * so the report's Categories dashboard can filter by @smoke, @regression, etc.
     */
    private static void assignTagsAsCategories(ExtentTest test, Scenario scenario) {
        if (test == null) return;
        List<String> categories = new ArrayList<>();
        for (String tag : scenario.getSourceTagNames()) {
            String category = tag.startsWith("@") ? tag.substring(1) : tag;
            if (!category.isBlank()) categories.add(category);
        }
        UiExtentReportManager.assignCategories(test, categories);
    }

    /**
     * Starts the OTP email listener. Call from module's @BeforeAll(order=5) AFTER config is loaded.
     */
    public static void startOtpListener() {
        OTPListener.bTerminate = false;
        OTPListener listener = new OTPListener();
        otpListenerThread = new Thread(listener::run, "otp-listener");
        otpListenerThread.setDaemon(true);
        otpListenerThread.start();
        logger.info("OTP listener started");
    }
}