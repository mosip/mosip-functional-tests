package io.mosip.testrig.uitestrig.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ViewportSize;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Thread-safe Playwright lifecycle management using ThreadLocal.
 * One Playwright + Browser + BrowserContext + Page per test thread.
 *
 * Browser selection (browser property):
 *   chromium  — Playwright-bundled Chromium (default)
 *   firefox   — Playwright-bundled Firefox
 *   webkit    — Playwright-bundled WebKit (Safari engine)
 *   safari    — alias for webkit
 *   chrome    — installed Google Chrome (channel)
 *   msedge    — installed Microsoft Edge (channel)
 *   edge      — alias for msedge
 *
 * Mobile emulation (mobileDevice property):
 *   Leave blank for desktop. Supported values:
 *   iPhone 13, iPhone 14, iPhone 15, Pixel 5, Pixel 7,
 *   Galaxy S9+, Galaxy S21+, iPad Pro 11, iPad Mini
 */
public class PlaywrightFactory {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightFactory.class);

    private static final ThreadLocal<Playwright>     playwrightTL    = new ThreadLocal<>();
    private static final ThreadLocal<Browser>        browserTL       = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextTL       = new ThreadLocal<>();
    private static final ThreadLocal<Page>           pageTL          = new ThreadLocal<>();
    private static final ThreadLocal<Boolean>        externalSession = new ThreadLocal<>();

    private static final Map<String, DeviceProfile> DEVICES = Map.ofEntries(
        Map.entry("iPhone 13",   new DeviceProfile(390,  844,  3.0,  true,  true,  "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1")),
        Map.entry("iPhone 14",   new DeviceProfile(390,  844,  3.0,  true,  true,  "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1")),
        Map.entry("iPhone 15",   new DeviceProfile(393,  852,  3.0,  true,  true,  "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1")),
        Map.entry("Pixel 5",     new DeviceProfile(393,  851,  2.75, true,  true,  "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36")),
        Map.entry("Pixel 7",     new DeviceProfile(412,  915,  2.625,true,  true,  "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36")),
        Map.entry("Galaxy S9+",  new DeviceProfile(320,  658,  4.5,  true,  true,  "Mozilla/5.0 (Linux; Android 8.1.0; SM-G965U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Mobile Safari/537.36")),
        Map.entry("Galaxy S21+", new DeviceProfile(384,  854,  3.5,  true,  true,  "Mozilla/5.0 (Linux; Android 11; SM-G996B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.105 Mobile Safari/537.36")),
        Map.entry("iPad Pro 11", new DeviceProfile(834, 1194,  2.0,  false, true,  "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1")),
        Map.entry("iPad Mini",   new DeviceProfile(744, 1133,  2.0,  false, true,  "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1"))
    );

    private PlaywrightFactory() {}

    /**
     * Registers an externally-managed browser session (e.g. BrowserStack) into the ThreadLocals
     * for this thread.  A subsequent call to {@link #initPage()} will detect the registered session
     * and skip local browser launch, returning the registered page instead.
     *
     * Call {@link #closePage()} as usual — it closes all registered resources regardless of source.
     */
    public static void registerExternalSession(Playwright pw, Browser browser,
                                               BrowserContext ctx, Page page) {
        playwrightTL.set(pw);
        browserTL.set(browser);
        contextTL.set(ctx);
        pageTL.set(page);
        externalSession.set(Boolean.TRUE);
        logger.info("External browser session registered for this thread");
    }

    /**
     * Creates a new Playwright page for the current thread.
     * If an external session is already registered (e.g. BrowserStack), the registered page
     * is returned without launching a local browser.
     * If a local page already exists on this thread, it is closed first.
     */
    public static Page initPage() {
        if (Boolean.TRUE.equals(externalSession.get())) {
            logger.debug("External session active — returning registered page, skipping local launch");
            return getPage();
        }
        Page page = launch(null);
        logger.info("Playwright page initialised — browser: {}, headless: {}, device: {}",
                UiConfigManager.getBrowserType(), UiConfigManager.isHeadless(),
                UiConfigManager.getMobileDevice().isEmpty() ? "desktop" : UiConfigManager.getMobileDevice());
        return page;
    }

    /**
     * Like {@link #initPage()}, but seeds the new BrowserContext from a previously saved
     * {@code storageState} (cookies + localStorage) instead of a blank one, so the resulting
     * page is already authenticated. Same per-call isolation as initPage(); only the starting
     * cookies/local storage differ. Save state first with {@link #saveStorageState(String)}.
     */
    public static Page initPageWithStorageState(String storageStatePath) {
        Page page = launch(storageStatePath);
        logger.info("Playwright page initialised from saved storage state — browser: {}, headless: {}",
                UiConfigManager.getBrowserType(), UiConfigManager.isHeadless());
        return page;
    }

    /** Saves the current thread's BrowserContext cookies + localStorage for later reuse via
     *  {@link #initPageWithStorageState(String)}. */
    public static void saveStorageState(String path) {
        BrowserContext context = contextTL.get();
        if (context == null) throw new IllegalStateException(
                "No BrowserContext for this thread — call initPage() first");
        context.storageState(new BrowserContext.StorageStateOptions().setPath(java.nio.file.Paths.get(path)));
        logger.info("Saved browser storage state to {}", path);
    }

    private static Page launch(String storageStatePath) {
        if (pageTL.get() != null) {
            closePage();
        }

        Playwright pw = Playwright.create();
        playwrightTL.set(pw);

        boolean headless   = UiConfigManager.isHeadless();
        String browserKey  = UiConfigManager.getBrowserType();
        String deviceName  = UiConfigManager.getMobileDevice();

        BrowserType browserType = resolveBrowserType(pw, browserKey);
        BrowserType.LaunchOptions launchOpts = buildLaunchOptions(headless, browserKey);
        Browser browser;
        try {
            browser = browserType.launch(launchOpts);
        } catch (RuntimeException e) {
            closePage();
            throw e;
        }
        browserTL.set(browser);
        UiExtentReportManager.setBrowserVersion(browserKey + " " + browser.version());

        Browser.NewContextOptions contextOptions = buildContextOptions(deviceName, headless);
        if (storageStatePath != null) {
            contextOptions.setStorageStatePath(java.nio.file.Paths.get(storageStatePath));
        }
        BrowserContext context = browser.newContext(contextOptions);
        contextTL.set(context);

        Page page = context.newPage();
        page.setDefaultTimeout(30_000);
        page.setDefaultNavigationTimeout(60_000);
        pageTL.set(page);
        return page;
    }

    public static Page getPage() {
        Page page = pageTL.get();
        if (page == null) throw new IllegalStateException(
                "No Playwright page for this thread — call initPage() first");
        return page;
    }

    public static Browser getBrowser() {
        Browser browser = browserTL.get();
        if (browser == null) throw new IllegalStateException(
                "No Browser for this thread — call initPage() first");
        return browser;
    }

    public static void closePage() {
        try {
            closeQuietly(pageTL.get());
            closeQuietly(contextTL.get());
            closeQuietly(browserTL.get());
            closeQuietly(playwrightTL.get());
        } finally {
            pageTL.remove();
            contextTL.remove();
            browserTL.remove();
            playwrightTL.remove();
            externalSession.remove();
        }
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Exception e) {
            logger.warn("Error during Playwright cleanup: {}", e.getMessage());
        }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static BrowserType resolveBrowserType(Playwright pw, String browser) {
        return switch (browser.toLowerCase()) {
            case "firefox"                                  -> pw.firefox();
            case "webkit", "safari"                          -> pw.webkit();
            case "chromium", "chrome", "msedge", "edge"      -> pw.chromium();
            default -> throw new IllegalArgumentException(
                    "Unknown browser '" + browser + "'. Supported: chromium, chrome, msedge, edge, firefox, webkit, safari");
        };
    }

    private static BrowserType.LaunchOptions buildLaunchOptions(boolean headless, String browser) {
        BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions().setHeadless(headless);
        switch (browser.toLowerCase()) {
            case "chrome"         -> opts.setChannel("chrome");
            case "msedge", "edge" -> opts.setChannel("msedge");
        }
        // In headed mode, start maximized. --start-maximized works for Chromium/Chrome/Edge.
        if (!headless) {
            opts.setArgs(java.util.List.of("--start-maximized"));
        }
        return opts;
    }

    private static Browser.NewContextOptions buildContextOptions(String deviceName, boolean headless) {
        if (deviceName != null && !deviceName.isBlank()) {
            DeviceProfile profile = DEVICES.get(deviceName);
            if (profile == null) {
                throw new IllegalArgumentException(
                    "Unknown mobileDevice '" + deviceName + "'. Supported: " + DEVICES.keySet());
            }
            return new Browser.NewContextOptions()
                    .setViewportSize(profile.width, profile.height)
                    .setUserAgent(profile.userAgent)
                    .setDeviceScaleFactor(profile.deviceScaleFactor)
                    .setIsMobile(profile.isMobile)
                    .setHasTouch(profile.hasTouch)
                    .setIgnoreHTTPSErrors(true);
        }
        if (!headless) {
            // null viewport lets --start-maximized control the window size
            return new Browser.NewContextOptions()
                    .setViewportSize(null)
                    .setIgnoreHTTPSErrors(true);
        }
        return new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true);
    }

    private record DeviceProfile(
            int width, int height,
            double deviceScaleFactor,
            boolean isMobile, boolean hasTouch,
            String userAgent) {}
}
