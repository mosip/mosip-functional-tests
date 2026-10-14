package io.mosip.testrig.uitestrig.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.mosip.testrig.apirig.utils.S3Adapter;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extent Reports manager for MOSIP UI test suites (Playwright + Cucumber BDD).
 *
 * Modelled on the inji-web {@code ExtentReportManager} reference, adapted for:
 *   - ExtentSparkReporter (Extent 5.x) instead of the legacy v3 HtmlReporter
 *   - the Cucumber JUnit Platform engine
 *   - Playwright screenshots
 *
 * Lifecycle (driven by the module's Cucumber hooks):
 *   suiteSetup    → initReport()
 *   @Before       → createTest(scenarioName)
 *   @BeforeStep   → logStepStart()
 *   per action    → logStep(...) / logStepWithLocator(...)   (from BasePage)
 *   @After        → increment counters, set status, removeTest()
 *   suiteTeardown → flush()
 *
 * Threading: a single {@link ExtentTest} is active per scenario, held in a ThreadLocal (fast
 * path) plus a {@code volatile} field (cross-thread fallback). Cucumber's plugin events
 * (TestStepStarted/Finished) aren't used — under parallel execution the JUnit5 engine buffers
 * and replays them later, often after the scenario's own @Before/@After. @BeforeStep/@After
 * run synchronously on the scenario's own thread instead, keeping everything ordered and timed.
 */
public final class UiExtentReportManager {

    private static final Logger logger = LoggerFactory.getLogger(UiExtentReportManager.class);

    private static final String DEFAULT_REPORT_DIR = "target/extent-reports/";

    /** Cyan used for the numbered step header, matching the inji-web reference report. */
    private static final String STEP_COLOR = "#00bcd4";

    private static ExtentReports extent;
    private static String gitBranch   = "unknown";
    private static String gitCommitId = "unknown";
    private static final String reportDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    // Active scenario test — ThreadLocal fast path + volatile cross-thread fallback.
    private static final ThreadLocal<ExtentTest>   testThread  = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> stepCounter = new ThreadLocal<>();
    private static volatile ExtentTest currentTest;

    // Lets teardownScenario() skip its own fallback screenshot when logFailureWithScreenshot()
    // already captured one for this scenario.
    private static final ThreadLocal<Boolean> stepFailureScreenshotTaken = ThreadLocal.withInitial(() -> false);

    private static volatile boolean browserVersionRecorded = false;

    private static volatile int passedCount = 0;
    private static volatile int failedCount = 0;
    private static volatile int skippedCount = 0;
    private static volatile int knownIssueCount = 0;

    private UiExtentReportManager() {}

    // ── Report lifecycle ───────────────────────────────────────────────────────

    public static synchronized void initReport() {
        if (extent != null) return;
        getGitDetails();

        String moduleName = orDefault(UiConfigManager.getproperty("moduleName"), "mosip-ui");
        String envName    = parseEnvName(UiConfigManager.getproperty("apiInternalEndPoint"));
        String reportName = orDefault(UiConfigManager.getproperty("reportName"), "MOSIP UI Automation");

        String fullTitle = reportName + " | " + envName + " | " + reportDate
                + " | Branch: " + gitBranch + " | Commit: " + gitCommitId;

        new File(reportDir()).mkdirs();
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile());
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("UI Automation Report");
        spark.config().setReportName(fullTitle);
        // Spark's own test-switching JS doesn't reset the detail panel's scroll position, so
        // scrolling through a long test and then selecting a different one leaves the new
        // panel scrolled to the same offset instead of starting at the top.
        spark.config().setJs(
                "document.addEventListener('click', function(e) {"
                        + "  var item = e.target.closest('.test-item');"
                        + "  if (!item) return;"
                        + "  setTimeout(function() {"
                        + "    var panel = document.querySelector('.test-contents:not(.d-none)');"
                        + "    if (panel) panel.scrollTop = 0;"
                        + "    window.scrollTo(0, 0);"
                        + "  }, 50);"
                        + "});");

        String device = UiConfigManager.getMobileDevice();
        AppVersionResolver.AppInfo appInfo = AppVersionResolver.resolve();

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Module", moduleName);
        extent.setSystemInfo("Environment", envName);
        extent.setSystemInfo("App Version (under test)", appInfo.version());
        extent.setSystemInfo("App Commit", appInfo.commit());
        extent.setSystemInfo("Language", UiConfigManager.getLoginLang());
        extent.setSystemInfo("Browser", UiConfigManager.getBrowserType());
        extent.setSystemInfo("Headless", String.valueOf(UiConfigManager.isHeadless()));
        extent.setSystemInfo("Device", device.isBlank() ? "desktop" : device);
        extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")");
        extent.setSystemInfo("Portal URL", UiConfigManager.getproperty("baseUrl"));
        extent.setSystemInfo("Git Branch", gitBranch);
        extent.setSystemInfo("Git Commit", gitCommitId);

        logger.info("Extent report initialised — {}", fullTitle);
    }

    public static synchronized ExtentTest createTest(String testName) {
        if (extent == null) initReport();
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
        currentTest = test;
        stepCounter.set(new AtomicInteger(0));
        stepFailureScreenshotTaken.set(false);
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest test = testThread.get();
        return test != null ? test : currentTest;
    }

    /** Assigns Extent categories (e.g. from Gherkin tags) to a test, just created via
     *  {@link #createTest(String)}. */
    public static synchronized void assignCategories(ExtentTest test, Iterable<String> categories) {
        if (test == null) return;
        for (String category : categories) {
            test.assignCategory(category);
        }
    }

    /**
     * Records the resolved browser name + version in the System/Environment table.
     * Called once from {@code PlaywrightFactory} after the first browser launches, because the
     * version is only known after launch (not at {@link #initReport()} time). Subsequent calls
     * are ignored so the row is not duplicated.
     */
    public static synchronized void setBrowserVersion(String browserWithVersion) {
        if (browserVersionRecorded || extent == null
                || browserWithVersion == null || browserWithVersion.isBlank()) return;
        extent.setSystemInfo("Browser Version", browserWithVersion);
        browserVersionRecorded = true;
    }

    public static synchronized void removeTest() {
        ExtentTest mine = testThread.get();
        testThread.remove();
        stepCounter.remove();
        stepFailureScreenshotTaken.remove();
        if (currentTest == mine) currentTest = null;
    }

    // ── Step / action logging ───────────────────────────────────────────────────

    /** Writes a numbered step divider (e.g. {@code STEP 3:}) marking the start of a new
     *  Gherkin step. Called from the module's {@code @BeforeStep} hook; the following
     *  action-detail logs from BasePage convey what the step actually does. */
    public static synchronized void logStepStart() {
        ExtentTest test = getTest();
        if (test == null) return;
        AtomicInteger counter = stepCounter.get();
        int num = (counter != null) ? counter.incrementAndGet() : 0;
        test.info("<br><b style='color:" + STEP_COLOR + ";'>STEP " + num + ":</b>");
    }

    /** Logs a plain action/assertion detail line under the current step. */
    public static synchronized void logStep(String message) {
        ExtentTest test = getTest();
        if (test != null) test.info(message);
    }

    /**
     * Logs a successful verification as a green pass line in the report.
     * Use from the assertion library so checkpoints are visible on pass, not only on failure.
     */
    public static synchronized void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) test.pass("✅ " + message);
    }

    /**
     * Logs an action detail with a collapsible locator/selector block, matching the
     * inji-web reference report style:
     * <pre>
     *   Click reg center search btn
     *   ▶ Locator Details   →  #regCenterSearchBtn
     * </pre>
     */
    public static synchronized void logStepWithLocator(String description, String locator) {
        ExtentTest test = getTest();
        if (test == null) return;
        test.info(description
                + "<br><details style='margin-left:20px;'>"
                + "<summary style='cursor:pointer;'>Locator Details</summary>"
                + "<pre>" + escapeHtml(locator) + "</pre></details>");
    }

    /** Logs a non-fatal warning line (e.g. a self-healing fallback was used). */
    public static synchronized void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) test.warning(message);
    }

    /** Logs a skip line. Unlike logPass/logFailure, the caller supplies the full message
     *  (including any emoji/HTML prefix), since callers need different prefixes here. */
    public static synchronized void logSkip(String message) {
        ExtentTest test = getTest();
        if (test != null) test.skip(message);
    }

    /** Logs a failure line with the locator and exception message in a collapsible block. */
    public static synchronized void logFailure(String description, String locator, Throwable error) {
        ExtentTest test = getTest();
        if (test == null) return;
        StringBuilder html = new StringBuilder("❌ ").append(description);
        if (locator != null || error != null) {
            html.append("<br><details style='margin-left:20px;'>")
                .append("<summary style='cursor:pointer;'>Details</summary><pre>");
            if (locator != null) html.append(escapeHtml(locator));
            if (error != null)   html.append("\n\n").append(escapeHtml(String.valueOf(error.getMessage())));
            html.append("</pre></details>");
        }
        test.fail(html.toString());
    }

    /** Logs a failure with a pre-captured screenshot (base64 PNG) attached — capture the
     *  screenshot before calling this, since only the ExtentTest mutation needs to be locked. */
    public static synchronized void logFailureWithScreenshot(String description, String locator, Throwable error, String screenshotBase64) {
        logFailure(description, locator, error);
        if (screenshotBase64 != null) {
            attachScreenshot(screenshotBase64, "Failure Screenshot");
            stepFailureScreenshotTaken.set(true);
        }
    }

    /** Whether a step-level failure screenshot was already taken for the current scenario —
     *  lets the @After hook skip its own fallback capture when one isn't needed. */
    public static boolean wasStepFailureScreenshotTaken() {
        return stepFailureScreenshotTaken.get();
    }

    public static synchronized void attachScreenshot(String screenshotBase64, String title) {
        ExtentTest test = getTest();
        if (test == null || screenshotBase64 == null) return;
        try {
            test.addScreenCaptureFromBase64String(screenshotBase64, title);
        } catch (Exception e) {
            logger.warn("Could not attach screenshot: {}", e.getMessage());
        }
    }

    // ── Result counters ─────────────────────────────────────────────────────────

    public static synchronized void incrementPassed()     { passedCount++; }
    public static synchronized void incrementFailed()     { failedCount++; }
    public static synchronized void incrementSkipped()    { skippedCount++; }
    public static synchronized void incrementKnownIssue() { knownIssueCount++; }

    public static int getPassedCount()     { return passedCount; }
    public static int getFailedCount()     { return failedCount; }
    public static int getSkippedCount()    { return skippedCount; }
    public static int getKnownIssueCount() { return knownIssueCount; }
    public static int getTotalCount()      { return passedCount + failedCount + skippedCount + knownIssueCount; }

    // ── Flush + publish ──────────────────────────────────────────────────────────

    public static synchronized void flush() {
        if (extent == null) return;
        extent.flush();
        renameAndUpload();
        logger.info("Extent report flushed — T:{} P:{} F:{} S:{} KI:{}",
                getTotalCount(), passedCount, failedCount, skippedCount, knownIssueCount);
    }

    private static void renameAndUpload() {
        String timestamp  = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
        String moduleName = orDefault(UiConfigManager.getproperty("moduleName"), "mosip-ui");
        String envName    = parseEnvName(UiConfigManager.getproperty("apiInternalEndPoint"));

        String newFileName = moduleName + "-" + envName + "-" + timestamp
                + "-T-" + getTotalCount()
                + "-P-" + passedCount
                + "-F-" + failedCount
                + "-S-" + skippedCount
                + "-KI-" + knownIssueCount + ".html";

        File original = new File(reportFile());
        File renamed  = new File(reportDir() + newFileName);
        if (original.exists() && original.renameTo(renamed)) {
            logger.info("Report renamed to: {}", newFileName);
        } else {
            renamed = original;
            newFileName = original.getName();
            logger.warn("Could not rename report file; using original path");
        }

        if ("yes".equalsIgnoreCase(UiConfigManager.getproperty("push-reports-to-s3"))) {
            try {
                boolean uploaded = new S3Adapter().putObject(
                        UiConfigManager.getproperty("s3-account"), "", null, null, newFileName, renamed);
                if (uploaded) logger.info("Report uploaded to S3: {}", newFileName);
                else logger.error("S3 upload returned false for {}", newFileName);
            } catch (Exception e) {
                logger.error("Failed to upload report to S3: {}", e.getMessage());
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private static String orDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /** Output directory for the Extent report, configurable via "reportOutputDir". */
    private static String reportDir() {
        String dir = orDefault(UiConfigManager.getproperty("reportOutputDir"), DEFAULT_REPORT_DIR);
        return dir.endsWith("/") ? dir : dir + "/";
    }

    private static String reportFile() {
        return reportDir() + "ExtentReport.html";
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String parseEnvName(String url) {
        if (url == null || url.isBlank()) return "unknown";
        try {
            String host = new URL(url).getHost();
            host = host.replaceFirst("^api-internal\\.", "");
            host = host.replaceFirst("\\.mosip\\.net$", "");
            return host;
        } catch (MalformedURLException e) {
            return "unknown";
        }
    }

    private static void getGitDetails() {
        try {
            gitBranch   = runCommand("git", "rev-parse", "--abbrev-ref", "HEAD");
            gitCommitId = runCommand("git", "rev-parse", "--short", "HEAD");
            logger.info("Git details — branch: {}, commit: {}", gitBranch, gitCommitId);
        } catch (Exception e) {
            logger.warn("Git CLI unavailable: {}. Falling back to git.properties", e.getMessage());
            loadFromGitProperties();
        }
    }

    private static String runCommand(String... command) throws IOException {
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return line != null ? line.trim() : "unknown";
        }
    }

    private static void loadFromGitProperties() {
        Properties props = new Properties();
        try (InputStream is = UiExtentReportManager.class.getClassLoader()
                .getResourceAsStream("git.properties")) {
            if (is != null) {
                props.load(is);
                gitBranch   = props.getProperty("git.branch", "unknown");
                gitCommitId = props.getProperty("git.commit.id.abbrev", "unknown");
            }
        } catch (IOException e) {
            logger.error("Failed to load git.properties: {}", e.getMessage());
        }
    }
}
