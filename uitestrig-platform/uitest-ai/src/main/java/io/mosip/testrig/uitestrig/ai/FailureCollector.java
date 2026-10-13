package io.mosip.testrig.uitestrig.ai;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Thread-local collector that accumulates browser console errors and network failures
 * throughout a Cucumber scenario and then builds a {@link FailureContext} on demand.
 *
 * <p>Lifecycle — called from {@link AiHooks}:
 * <ol>
 *   <li>{@code FailureCollector.register(page)} in {@code @Before(order=10)} — after the page
 *       is initialised by {@code CucumberHooks.@Before(0)}</li>
 *   <li>{@code FailureCollector.buildContext(...)} in {@code @After(order=15)} — before the page
 *       is closed by {@code CucumberHooks.@After(0)}</li>
 *   <li>{@code FailureCollector.reset()} at end of {@code @After} — clears state for next scenario</li>
 * </ol>
 */
public class FailureCollector {

    private static final Logger logger = LoggerFactory.getLogger(FailureCollector.class);

    private static final ThreadLocal<List<String>> consoleTL = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<List<String>> networkTL = ThreadLocal.withInitial(ArrayList::new);

    private FailureCollector() {}

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Registers Playwright event listeners on the given page to collect
     * console {@code error} messages and HTTP 4xx/5xx responses.
     * Safe to call multiple times — clears any previously accumulated data first.
     */
    public static void register(Page page) {
        reset();
        page.onConsoleMessage(FailureCollector::onConsole);
        page.onResponse(FailureCollector::onResponse);
        logger.debug("FailureCollector registered on page");
    }

    /** Clears accumulated console and network data for the current thread. */
    public static void reset() {
        consoleTL.get().clear();
        networkTL.get().clear();
    }

    // ── Context builder ───────────────────────────────────────────────────────

    /**
     * Captures the current page state and builds a {@link FailureContext}.
     * Attempts to take a full-page screenshot; continues without it on error.
     *
     * @param page         open Playwright page (must not be closed)
     * @param scenarioName Cucumber scenario name
     * @param failedStep   text of the failing step (may be empty)
     * @param stackTrace   exception stack trace string (may be empty)
     * @return assembled context snapshot
     */
    public static FailureContext buildContext(Page page,
                                              String scenarioName,
                                              String failedStep,
                                              String stackTrace) {
        String pageUrl         = "";
        String screenshotBase64 = "";
        String pageHtml        = "";

        try { pageUrl = page.url(); } catch (Exception e) {
            logger.debug("Could not get page URL: {}", e.getMessage());
        }

        try {
            byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            screenshotBase64 = Base64.getEncoder().encodeToString(png);
        } catch (Exception e) {
            logger.debug("Could not capture screenshot: {}", e.getMessage());
        }

        try {
            String html = page.content();
            if (html.length() > FailureContext.HTML_TRUNCATE_CHARS) {
                html = html.substring(0, FailureContext.HTML_TRUNCATE_CHARS) + "\n<!-- truncated -->";
            }
            pageHtml = html;
        } catch (Exception e) {
            logger.debug("Could not capture page HTML: {}", e.getMessage());
        }

        return new FailureContext(
                scenarioName,
                failedStep,
                pageUrl,
                stackTrace,
                screenshotBase64,
                pageHtml,
                Collections.unmodifiableList(new ArrayList<>(consoleTL.get())),
                Collections.unmodifiableList(new ArrayList<>(networkTL.get()))
        );
    }

    // ── Playwright event handlers ─────────────────────────────────────────────

    private static void onConsole(ConsoleMessage msg) {
        if ("error".equalsIgnoreCase(msg.type())) {
            String text = msg.text();
            if (text != null && !text.isBlank()) consoleTL.get().add(text);
        }
    }

    private static void onResponse(Response response) {
        int status = response.status();
        if (status >= 400) {
            networkTL.get().add(status + " " + response.url());
        }
    }
}
