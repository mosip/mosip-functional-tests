package io.mosip.testrig.uitestrig.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import java.nio.file.Path;

/**
 * Public façade for all browser lifecycle operations.
 *
 * Modules use BrowserManager instead of touching PlaywrightFactory directly.
 * This keeps Playwright as an implementation detail hidden behind a stable API.
 *
 * Typical per-scenario usage (handled by CucumberHooks):
 *   Page page = BrowserManager.initPage();
 *   // ... test runs ...
 *   BrowserManager.closePage();
 *
 * For parallel scenarios, use newIsolatedContext() instead.
 * For pre-authenticated flows, use initPageWithAuthState().
 */
public class BrowserManager {

    private BrowserManager() {}

    /** Initialises a fresh browser page for the current thread. */
    public static Page initPage() {
        return PlaywrightFactory.initPage();
    }

    /** Returns the active page for the current thread. Throws if none exists. */
    public static Page getPage() {
        return PlaywrightFactory.getPage();
    }

    /** Closes the current thread's page, context, browser, and Playwright instance. */
    public static void closePage() {
        PlaywrightFactory.closePage();
    }

    /**
     * Creates an isolated browser context and page, independent of the thread-local session.
     * Use this for parallel scenario execution where each scenario needs its own browser state.
     *
     * The caller is responsible for closing the returned context when done.
     */
    public static BrowserContext newIsolatedContext() {
        Browser browser = PlaywrightFactory.getBrowser();
        return browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true));
    }

    /**
     * Creates a page pre-loaded with saved authentication state (cookies + localStorage).
     * Use this to skip the login flow in scenarios that require an authenticated user.
     *
     * Typical flow:
     *   1. Run a login scenario once and save state: context.storageState(options)
     *   2. In subsequent scenarios: Page page = BrowserManager.initPageWithAuthState(statePath)
     *
     * @param storageStateFile path to a JSON file saved by Playwright's storageState()
     */
    public static Page initPageWithAuthState(Path storageStateFile) {
        Browser browser = PlaywrightFactory.getBrowser();
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(storageStateFile)
                .setIgnoreHTTPSErrors(true));
        Page page = context.newPage();
        page.setDefaultTimeout(30_000);
        page.setDefaultNavigationTimeout(60_000);
        return page;
    }
}
