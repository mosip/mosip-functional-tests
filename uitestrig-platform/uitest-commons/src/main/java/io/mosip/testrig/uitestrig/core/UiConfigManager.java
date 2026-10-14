package io.mosip.testrig.uitestrig.core;

import io.mosip.testrig.apirig.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Three-layer config for UI automation (lowest → highest priority):
 *   1. uitest.properties     — UI defaults bundled in uitest-commons jar
 *   2. module's properties file — loaded explicitly by the module's XxxConfigManager.init()
 *   3. Environment variables  — override any key at runtime / Docker
 *
 * Module-specific setup: each module creates its own XxxConfigManager that calls
 *   UiConfigManager.loadUitestDefaults()  (layer 1)
 *   ConfigManager.init(modulePropsMap)    (layers 2 + 3)
 */
public class UiConfigManager extends ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(UiConfigManager.class);

    private static final String BASE_URL      = "baseUrl";
    private static final String BROWSER       = "browser";
    private static final String HEADLESS      = "headless";
    private static final String MOBILE_DEVICE = "mobileDevice";
    private static final String LOGIN_LANG    = "loginlang";

    /** Portal base URL — must be set in the module properties file or as an env var. */
    public static String getBaseUrl() {
        String val = getproperty(BASE_URL);
        if (val != null && !val.isEmpty()) return val;
        throw new IllegalStateException(
            "'baseUrl' is not configured. Set it in the module's properties file or as an environment variable.");
    }

    /** Browser engine: chromium (default), firefox, webkit, safari, chrome, msedge, edge. */
    public static String getBrowserType() {
        String val = getproperty(BROWSER);
        return (val == null || val.isEmpty()) ? "chromium" : val.toLowerCase();
    }

    /** Whether to run headless. Set headless=yes to enable. */
    public static boolean isHeadless() {
        return "yes".equalsIgnoreCase(getproperty(HEADLESS));
    }

    /** Mobile device name for emulation (e.g. "iPhone 13"). Empty means desktop. */
    public static String getMobileDevice() {
        String val = getproperty(MOBILE_DEVICE);
        return (val == null) ? "" : val.trim();
    }

    /** Login language code (e.g. eng, hin, fra). Defaults to eng. */
    public static String getLoginLang() {
        String val = getproperty(LOGIN_LANG);
        return (val == null || val.isEmpty()) ? "eng" : val;
    }

    /** Login language normalised for field ID construction — "sin" maps to "eng". */
    public static String getEffectiveLoginLang() {
        String lang = getLoginLang();
        return "sin".equalsIgnoreCase(lang) ? "eng" : lang;
    }

    /** Whether to regenerate visual baselines. Set via -Dplaywright.update.snapshots=true. */
    public static boolean isUpdateSnapshots() {
        return "true".equalsIgnoreCase(System.getProperty("playwright.update.snapshots"));
    }

    /**
     * Whether Playwright browser contexts should ignore TLS certificate errors.
     * Defaults to {@code yes} because MOSIP test environments commonly serve internal
     * domains with self-signed certificates. Set {@code ignoreHttpsErrors=no} in the
     * module's properties file to enforce real certificate verification (e.g. when testing
     * against a production-like environment with a trusted CA chain).
     */
    public static boolean isIgnoreHttpsErrors() {
        String val = getproperty("ignoreHttpsErrors");
        return val == null || val.isBlank() || !"no".equalsIgnoreCase(val.trim());
    }

    // ── Protected — called by module XxxConfigManager.init() ──────────────────

    protected static void loadUitestDefaults() {
        Properties defaults = new Properties();
        try (InputStream in = UiConfigManager.class.getClassLoader()
                .getResourceAsStream("config/uitest.properties")) {
            if (in != null) {
                defaults.load(in);
                for (String key : defaults.stringPropertyNames()) {
                    propertiesMap.putIfAbsent(key, defaults.getProperty(key));
                }
                logger.info("Loaded uitest.properties — {} defaults", defaults.size());
            } else {
                logger.warn("config/uitest.properties not found on classpath");
            }
        } catch (IOException e) {
            logger.warn("Failed to load uitest.properties: {}", e.getMessage());
        }
    }
}
