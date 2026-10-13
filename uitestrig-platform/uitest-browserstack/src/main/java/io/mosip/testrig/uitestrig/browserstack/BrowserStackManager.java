package io.mosip.testrig.uitestrig.browserstack;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.mosip.testrig.uitestrig.browser.PlaywrightFactory;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BrowserStack cloud-grid session manager for Playwright tests.
 *
 * <p>Connects Playwright to BrowserStack's CDP endpoint, registers the session into
 * {@link PlaywrightFactory} so the rest of the framework (BasePage, BrowserManager)
 * continues to work transparently with no changes to test or step-definition code.
 *
 * <h2>Enabling BrowserStack</h2>
 * Add this artifact alongside uitest-commons, add {@link BrowserStackHooks} to the Cucumber
 * glue path, and configure the following properties (env vars take precedence):
 * <pre>
 *   browserstack.enabled     = yes
 *   browserstack.username    = myUser       # or env BS_USERNAME
 *   browserstack.accessKey   = myKey        # or env BS_ACCESS_KEY
 *   browserstack.buildName   = CI-#123
 *   browserstack.projectName = MOSIP UI
 * </pre>
 *
 * <h2>How the connection works</h2>
 * <ol>
 *   <li>Capabilities JSON is built by {@link BrowserStackCapabilities} and URL-encoded.</li>
 *   <li>Playwright's {@code chromium().connect(wsEndpoint)} connects over CDP to BrowserStack's
 *       cloud grid — no local Chrome binary is launched.</li>
 *   <li>The resulting {@link Browser}/{@link BrowserContext}/{@link Page} are registered into
 *       {@link PlaywrightFactory#registerExternalSession} so the thread-local page is available
 *       everywhere without any code changes in the consuming module.</li>
 *   <li>Session status (pass/fail) and video/public URL are communicated via BrowserStack's
 *       JavaScript executor protocol ({@code browserstack_executor: ...}), which the CDP proxy
 *       intercepts before reaching the browser.</li>
 * </ol>
 */
public class BrowserStackManager {

    private static final Logger logger = LoggerFactory.getLogger(BrowserStackManager.class);
    private static final String BS_ENDPOINT = "wss://cdp.browserstack.com/playwright?caps=";

    private BrowserStackManager() {}

    // ── Status checks ─────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when {@code browserstack.enabled=yes} is configured.
     * Checked by {@link BrowserStackHooks} to decide whether to initialise a BS session.
     */
    public static boolean isEnabled() {
        return "yes".equalsIgnoreCase(UiConfigManager.getproperty("browserstack.enabled"));
    }

    // ── Session initialisation ────────────────────────────────────────────────

    /**
     * Opens a new BrowserStack session for the given test/scenario name, loading all
     * remaining capabilities from {@code uitest.properties} / environment variables.
     *
     * @param testName display name shown in the BrowserStack Automate dashboard
     * @return the Playwright {@link Page} for the remote session
     * @throws IllegalStateException if username or access key is not configured
     */
    public static Page initPage(String testName) {
        return initPage(BrowserStackCapabilities.fromConfig(testName).build());
    }

    /**
     * Opens a new BrowserStack session with explicit capabilities.
     *
     * @param caps capabilities built via {@link BrowserStackCapabilities#builder()}
     * @return the Playwright {@link Page} for the remote session
     */
    public static Page initPage(BrowserStackCapabilities caps) {
        String wsEndpoint = BS_ENDPOINT + caps.toUrlEncodedJson();
        logger.info("Connecting to BrowserStack — browser: {}, test: {}",
                caps.getBrowser(), caps.toLoggableJson());

        Playwright pw = Playwright.create();
        try {
            Browser        browser = pw.chromium().connect(wsEndpoint);
            BrowserContext ctx     = browser.newContext(
                    new Browser.NewContextOptions().setIgnoreHTTPSErrors(true));
            Page page              = ctx.newPage();
            page.setDefaultTimeout(30_000);
            page.setDefaultNavigationTimeout(60_000);

            PlaywrightFactory.registerExternalSession(pw, browser, ctx, page);
            logger.info("BrowserStack session initialised for thread {}", Thread.currentThread().getName());
            return page;
        } catch (RuntimeException e) {
            pw.close();
            throw e;
        }
    }

    // ── Session status ────────────────────────────────────────────────────────

    /**
     * Marks the current BrowserStack session as <b>passed</b>.
     * Must be called before {@link PlaywrightFactory#closePage()} closes the session.
     *
     * @param page   the Playwright page for this session
     * @param reason short description shown in the Automate dashboard
     */
    public static void markTestPassed(Page page, String reason) {
        setSessionStatus(page, "passed", reason);
        logger.info("BrowserStack session marked: PASSED — {}", reason);
    }

    /**
     * Marks the current BrowserStack session as <b>failed</b>.
     * Must be called before {@link PlaywrightFactory#closePage()} closes the session.
     *
     * @param page   the Playwright page for this session
     * @param reason short description shown in the Automate dashboard
     */
    public static void markTestFailed(Page page, String reason) {
        setSessionStatus(page, "failed", reason);
        logger.info("BrowserStack session marked: FAILED — {}", reason);
    }

    // ── Session metadata ──────────────────────────────────────────────────────

    /**
     * Returns the direct video URL for the current BrowserStack session.
     *
     * @param page the Playwright page for this session
     * @return video URL string, or {@code ""} if unavailable
     */
    public static String getSessionVideoUrl(Page page) {
        Map<String, String> details = fetchSessionDetails(page);
        return details.getOrDefault("video_url", "");
    }

    /**
     * Returns the public Automate dashboard URL for the current BrowserStack session.
     *
     * @param page the Playwright page for this session
     * @return public URL string, or {@code ""} if unavailable
     */
    public static String getSessionPublicUrl(Page page) {
        Map<String, String> details = fetchSessionDetails(page);
        return details.getOrDefault("public_url", "");
    }

    /**
     * Returns the BrowserStack Automate session ID for the current session.
     *
     * @param page the Playwright page for this session
     * @return session ID string, or {@code ""} if unavailable
     */
    public static String getSessionId(Page page) {
        Map<String, String> details = fetchSessionDetails(page);
        return details.getOrDefault("hashed_id", "");
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static void setSessionStatus(Page page, String status, String reason) {
        String safeReason = reason == null ? "" : reason.replace("\\", "/").replace("\"", "'")
                .replaceAll("[\\p{Cntrl}]", " ");
        String script =
            "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": " +
            "{\"status\": \"" + status + "\", \"reason\": \"" + safeReason + "\"}}";
        try {
            page.evaluate(script);
        } catch (Exception e) {
            logger.warn("Failed to set BrowserStack session status '{}': {}", status, e.getMessage());
        }
    }

    private static Map<String, String> fetchSessionDetails(Page page) {
        try {
            Object result = page.evaluate(
                "browserstack_executor: {\"action\": \"getSessionDetails\"}");
            if (result instanceof Map<?, ?> raw) {
                Map<String, String> out = new java.util.LinkedHashMap<>();
                raw.forEach((k, v) -> out.put(String.valueOf(k), String.valueOf(v)));
                return out;
            }
            // BrowserStack may return the details as a JSON string
            if (result instanceof String json) return parseSimpleJson(json);
        } catch (Exception e) {
            logger.warn("Failed to fetch BrowserStack session details: {}", e.getMessage());
        }
        return Map.of();
    }

    /**
     * Minimal JSON string-to-map parser for BrowserStack session details.
     * Only handles flat string-value objects — sufficient for the session details payload.
     * Unescapes {@code \/}, {@code \"}, and {@code \\} in matched values.
     */
    private static Map<String, String> parseSimpleJson(String json) {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        if (json == null || json.isBlank()) return map;
        Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(json);
        while (m.find()) {
            String value = m.group(2)
                    .replace("\\/", "/")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
            map.put(m.group(1), value);
        }
        return map;
    }
}
