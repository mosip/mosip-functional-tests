package io.mosip.testrig.uitestrig.browserstack;

import io.mosip.testrig.uitestrig.core.UiConfigManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Fluent builder for the BrowserStack Playwright WebSocket endpoint capabilities JSON.
 *
 * <p>BrowserStack Playwright requires all session options to be passed as a URL-encoded JSON
 * object appended to the CDP WebSocket endpoint.  This class builds that JSON from either
 * explicit setter calls or the project's {@code uitest.properties} / environment variables.
 *
 * <h2>Configuration properties (read via UiConfigManager / env vars)</h2>
 * <pre>
 *   browserstack.username       — BrowserStack username (prefer env var BS_USERNAME)
 *   browserstack.accessKey      — BrowserStack access key (prefer env var BS_ACCESS_KEY)
 *   browserstack.buildName      — logical build/CI run name
 *   browserstack.projectName    — project label shown in the Automate dashboard
 *   browserstack.browser        — browser type: chrome (default), firefox, edge
 *   browserstack.browserVersion — e.g. "latest" (default) or "120"
 *   browserstack.os             — Windows (default) or OS X
 *   browserstack.osVersion      — e.g. "11", "10", "Ventura", "Sonoma"
 *   browserstack.networkLogs    — yes/no (default yes)
 *   browserstack.consoleLogs    — errors | warnings | info | verbose | disable (default errors)
 *   browserstack.debug          — yes/no (default no)
 * </pre>
 *
 * <h2>Usage</h2>
 * <pre>
 *   // Auto-populate everything from uitest.properties / env vars
 *   BrowserStackCapabilities caps = BrowserStackCapabilities.fromConfig("Login scenario");
 *
 *   // Explicit overrides
 *   BrowserStackCapabilities caps = BrowserStackCapabilities.builder()
 *       .browser("firefox").browserVersion("latest")
 *       .os("OS X").osVersion("Ventura")
 *       .testName("Login — French locale")
 *       .buildName("CI-#123")
 *       .build();
 *
 *   // Both produce the encoded URL fragment for BrowserStackManager
 *   String encodedCaps = caps.toUrlEncodedJson();
 * </pre>
 */
public class BrowserStackCapabilities {

    private String username;
    private String accessKey;
    private String testName     = "MOSIP UI Test";
    private String buildName    = "uitest-browserstack";
    private String projectName  = "MOSIP UI";
    private String browser      = "chrome";
    private String browserVersion = "latest";
    private String os           = "Windows";
    private String osVersion    = "11";
    private boolean networkLogs = true;
    private String consoleLogs  = "errors";
    private boolean debug       = false;

    private BrowserStackCapabilities() {}

    // ── Factory methods ───────────────────────────────────────────────────────

    /** Returns a new blank builder instance. */
    public static BrowserStackCapabilities builder() {
        return new BrowserStackCapabilities();
    }

    /**
     * Builds capabilities from {@code uitest.properties} / environment variables,
     * setting the test name to the given scenario/test name.
     */
    public static BrowserStackCapabilities fromConfig(String testName) {
        BrowserStackCapabilities caps = new BrowserStackCapabilities();

        String user = System.getenv("BS_USERNAME");
        if (user == null || user.isBlank()) user = UiConfigManager.getproperty("browserstack.username");
        caps.username = user;

        String key = System.getenv("BS_ACCESS_KEY");
        if (key == null || key.isBlank()) key = UiConfigManager.getproperty("browserstack.accessKey");
        caps.accessKey = key;

        String bld = UiConfigManager.getproperty("browserstack.buildName");
        if (bld != null && !bld.isBlank()) caps.buildName = bld;

        String prj = UiConfigManager.getproperty("browserstack.projectName");
        if (prj != null && !prj.isBlank()) caps.projectName = prj;

        String br = UiConfigManager.getproperty("browserstack.browser");
        if (br != null && !br.isBlank()) caps.browser = br;

        String brv = UiConfigManager.getproperty("browserstack.browserVersion");
        if (brv != null && !brv.isBlank()) caps.browserVersion = brv;

        String oss = UiConfigManager.getproperty("browserstack.os");
        if (oss != null && !oss.isBlank()) caps.os = oss;

        String osv = UiConfigManager.getproperty("browserstack.osVersion");
        if (osv != null && !osv.isBlank()) caps.osVersion = osv;

        String netLogs = UiConfigManager.getproperty("browserstack.networkLogs");
        if (netLogs != null && !netLogs.isBlank()) caps.networkLogs = !"no".equalsIgnoreCase(netLogs.trim());

        String conLogs = UiConfigManager.getproperty("browserstack.consoleLogs");
        if (conLogs != null && !conLogs.isBlank()) caps.consoleLogs = conLogs;

        String dbg = UiConfigManager.getproperty("browserstack.debug");
        if (dbg != null && !dbg.isBlank()) caps.debug = "yes".equalsIgnoreCase(dbg.trim());

        caps.testName = testName;
        return caps;
    }

    // ── Fluent setters ────────────────────────────────────────────────────────

    public BrowserStackCapabilities username(String username) {
        this.username = username; return this;
    }
    public BrowserStackCapabilities accessKey(String accessKey) {
        this.accessKey = accessKey; return this;
    }
    public BrowserStackCapabilities testName(String testName) {
        this.testName = testName; return this;
    }
    public BrowserStackCapabilities buildName(String buildName) {
        this.buildName = buildName; return this;
    }
    public BrowserStackCapabilities projectName(String projectName) {
        this.projectName = projectName; return this;
    }
    public BrowserStackCapabilities browser(String browser) {
        this.browser = browser; return this;
    }
    public BrowserStackCapabilities browserVersion(String version) {
        this.browserVersion = version; return this;
    }
    public BrowserStackCapabilities os(String os) {
        this.os = os; return this;
    }
    public BrowserStackCapabilities osVersion(String osVersion) {
        this.osVersion = osVersion; return this;
    }
    public BrowserStackCapabilities networkLogs(boolean enabled) {
        this.networkLogs = enabled; return this;
    }
    public BrowserStackCapabilities consoleLogs(String level) {
        this.consoleLogs = level; return this;
    }
    public BrowserStackCapabilities debug(boolean enabled) {
        this.debug = enabled; return this;
    }

    /** Terminal method — validates and returns {@code this} for passing to {@link BrowserStackManager}. */
    public BrowserStackCapabilities build() {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                "BrowserStack username is required. Set browserstack.username property or BS_USERNAME env var.");
        }
        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException(
                "BrowserStack access key is required. Set browserstack.accessKey property or BS_ACCESS_KEY env var.");
        }
        return this;
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    /**
     * Serialises this capabilities object to a JSON string suitable for the BrowserStack endpoint.
     * All string values are JSON-escaped. Package-private — contains the raw access key; use
     * {@link #toLoggableJson()} for anything that gets logged.
     */
    String toJson() {
        return toJson(false);
    }

    /** Same as {@link #toJson()} but with the access key masked — safe to log. */
    public String toLoggableJson() {
        return toJson(true);
    }

    private String toJson(boolean mask) {
        return "{" +
            "\"browser\":\""         + esc(browser)        + "\"," +
            "\"browser_version\":\"" + esc(browserVersion) + "\"," +
            "\"os\":\""              + esc(os)             + "\"," +
            "\"os_version\":\""      + esc(osVersion)      + "\"," +
            "\"name\":\""            + esc(testName)       + "\"," +
            "\"build\":\""           + esc(buildName)      + "\"," +
            "\"project\":\""         + esc(projectName)    + "\"," +
            "\"browserstack.username\":\"" + esc(username) + "\"," +
            "\"browserstack.accessKey\":\"" + (mask ? "***" : esc(accessKey)) + "\"," +
            "\"browserstack.networkLogs\":\""  + networkLogs  + "\"," +
            "\"browserstack.consoleLogs\":\"" + esc(consoleLogs) + "\"," +
            "\"browserstack.debug\":\""        + debug         + "\"" +
            "}";
    }

    /**
     * Returns the URL-encoded JSON string, ready to append to the BrowserStack WebSocket endpoint:
     * {@code wss://cdp.browserstack.com/playwright?caps=<result>}
     */
    public String toUrlEncodedJson() {
        return URLEncoder.encode(toJson(), StandardCharsets.UTF_8);
    }

    // JSON string escaping — backslash, quote, and other control characters.
    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    // ── Accessors for BrowserStackManager ────────────────────────────────────

    String getUsername()  { return username;  }
    String getAccessKey() { return accessKey; }
    String getBrowser()   { return browser;   }
}
