package io.mosip.testrig.uitestrig.accessibility;

import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Accessibility validator powered by <a href="https://github.com/dequelabs/axe-core">axe-core</a>.
 *
 * <p>axe-core is injected into the page at runtime — no additional Maven dependency is required.
 * The injection strategy:
 * <ol>
 *   <li>Classpath first — if {@code axe-core.min.js} is present in the test resources it is used
 *       (offline, deterministic version).</li>
 *   <li>CDN fallback — loads axe-core from the URL configured by
 *       {@code accessibility.axeCoreCdnUrl} (default: jsDelivr CDN, axe-core 4.9.1).</li>
 * </ol>
 *
 * <h2>Usage</h2>
 * <pre>
 *   // Assert no WCAG 2.1 AA violations on the current page
 *   AccessibilityValidator.assertNoViolations(page);
 *
 *   // Assert no violations, ignoring known decorative issues
 *   AccessibilityValidator.assertNoViolations(page, WcagLevel.LEVEL_AA,
 *       List.of("color-contrast", "region"));
 *
 *   // Get violation list for custom reporting
 *   List<AxeViolation> violations = AccessibilityValidator.runAxeCore(page, WcagLevel.LEVEL_AA);
 *   AccessibilityValidator.generateReport(violations, "target/a11y-report.html");
 *
 *   // Assert only blocker-severity violations fail the test
 *   AccessibilityValidator.assertNoBlockerViolations(page, WcagLevel.LEVEL_AA);
 * </pre>
 *
 * <h2>Configuration</h2>
 * <pre>
 *   # uitest.properties
 *   accessibility.axeCoreCdnUrl=https://cdn.jsdelivr.net/npm/axe-core@4.9.1/axe.min.js
 *   accessibility.reportDir=target/a11y-reports
 * </pre>
 */
public class AccessibilityValidator {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityValidator.class);

    private static final String CLASSPATH_AXE_JS    = "/axe-core.min.js";
    private static final String DEFAULT_CDN_URL      =
            "https://cdn.jsdelivr.net/npm/axe-core@4.9.1/axe.min.js";
    private static final String DEFAULT_REPORT_DIR   = "target/a11y-reports";
    private static final int    INJECT_TIMEOUT_MS    = 15_000;

    private AccessibilityValidator() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Runs axe-core at WCAG 2.1 AA and throws if any violations are found.
     * All violations (including minor) cause a failure.
     */
    public static void assertNoViolations(Page page) {
        assertNoViolations(page, WcagLevel.LEVEL_AA, List.of());
    }

    /**
     * Runs axe-core and throws if any violations at the specified WCAG level are found.
     */
    public static void assertNoViolations(Page page, WcagLevel level) {
        assertNoViolations(page, level, List.of());
    }

    /**
     * Runs axe-core and throws if any violations are found, after excluding the given rule IDs.
     *
     * @param excludedRules axe rule IDs to skip, e.g. {@code List.of("color-contrast", "region")}
     */
    public static void assertNoViolations(Page page, WcagLevel level, List<String> excludedRules) {
        List<AxeViolation> violations = runAxeCore(page, level);
        List<AxeViolation> relevant   = violations.stream()
                .filter(v -> !excludedRules.contains(v.id()))
                .toList();

        if (!relevant.isEmpty()) {
            String summary = formatViolationSummary(relevant);
            logger.error("Accessibility violations found:\n{}", summary);
            throw new AssertionError(
                    "Accessibility validation failed — " + relevant.size() + " violation(s):\n" + summary);
        }
        logger.info("No accessibility violations found (WCAG {})", level);
    }

    /**
     * Runs axe-core and throws only if {@code critical} or {@code serious} violations are found.
     * Minor/moderate violations are logged but do not fail the test.
     */
    public static void assertNoBlockerViolations(Page page, WcagLevel level) {
        List<AxeViolation> violations = runAxeCore(page, level);
        List<AxeViolation> blockers   = violations.stream()
                .filter(AxeViolation::isBlocker)
                .toList();

        if (!violations.isEmpty()) {
            logger.warn("Non-blocker violations: {}", violations.size() - blockers.size());
        }
        if (!blockers.isEmpty()) {
            throw new AssertionError(
                    "Accessibility blockers found (" + blockers.size() + "):\n" +
                    formatViolationSummary(blockers));
        }
        logger.info("No blocker accessibility violations (WCAG {})", level);
    }

    /**
     * Injects axe-core, runs it against the full document, and returns the raw violation list.
     *
     * @param page  Playwright page (must be fully loaded)
     * @param level WCAG conformance level to check
     * @return list of violations — empty if the page is fully compliant
     */
    @SuppressWarnings("unchecked")
    public static List<AxeViolation> runAxeCore(Page page, WcagLevel level) {
        injectAxeCore(page);

        String tagsJson = Arrays.stream(level.getAxeTags())
                .map(t -> "\"" + t + "\"")
                .collect(Collectors.joining(",", "[", "]"));

        String script =
            "async () => { " +
            "  const options = { runOnly: { type: 'tag', values: " + tagsJson + " } }; " +
            "  const result  = await axe.run(document, options); " +
            "  return result.violations.map(v => ({ " +
            "    id:          v.id, " +
            "    impact:      v.impact || 'unknown', " +
            "    description: v.description, " +
            "    help:        v.help, " +
            "    helpUrl:     v.helpUrl, " +
            "    nodes:       v.nodes.map(n => n.target.join(' > ')), " +
            "    failure:     v.nodes.length > 0 ? v.nodes[0].failureSummary : '' " +
            "  })); " +
            "}";

        Object rawResult = page.evaluate(script);
        if (rawResult == null) return List.of();

        List<Map<String, Object>> rawList = (List<Map<String, Object>>) rawResult;
        List<AxeViolation> violations = new ArrayList<>(rawList.size());
        for (Map<String, Object> v : rawList) {
            violations.add(new AxeViolation(
                    str(v, "id"),
                    str(v, "impact"),
                    str(v, "description"),
                    str(v, "help"),
                    str(v, "helpUrl"),
                    toStringList(v.get("nodes")),
                    str(v, "failure")
            ));
        }
        logger.info("axe-core found {} violation(s) at WCAG {}", violations.size(), level);
        return violations;
    }

    /**
     * Generates an HTML accessibility report from a list of violations.
     *
     * @param violations list from {@link #runAxeCore}
     * @param outputPath file path for the report HTML, e.g. {@code "target/a11y-reports/login.html"}
     * @return resolved output path string
     */
    public static String generateReport(List<AxeViolation> violations, String outputPath) {
        try {
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());

            try (PrintWriter w = new PrintWriter(path.toFile(), StandardCharsets.UTF_8)) {
                w.println(buildHtml(violations));
            }
            logger.info("Accessibility report written: {}", path.toAbsolutePath());
            return path.toAbsolutePath().toString();
        } catch (IOException e) {
            logger.error("Failed to write accessibility report: {}", e.getMessage());
            return outputPath;
        }
    }

    /**
     * Generates a timestamped report in the configured {@code accessibility.reportDir}.
     *
     * @param violations  violation list
     * @param reportName  base name, e.g. {@code "login-page"}
     * @return absolute path to the generated file
     */
    public static String generateReport(List<AxeViolation> violations, String reportName, Page page) {
        String dir  = UiConfigManager.getproperty("accessibility.reportDir");
        if (dir == null || dir.isBlank()) dir = DEFAULT_REPORT_DIR;
        String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String file = dir + "/" + reportName + "-" + ts + ".html";
        return generateReport(violations, file);
    }

    // ── axe-core injection ────────────────────────────────────────────────────

    private static void injectAxeCore(Page page) {
        if (isAxeAlreadyLoaded(page)) return;

        String classpathJs = loadFromClasspath();
        if (classpathJs != null) {
            page.evaluate(classpathJs);
            logger.debug("axe-core injected from classpath");
            return;
        }

        String cdnUrl = UiConfigManager.getproperty("accessibility.axeCoreCdnUrl");
        if (cdnUrl == null || cdnUrl.isBlank()) cdnUrl = DEFAULT_CDN_URL;

        page.evaluate(
            "async (url) => { " +
            "  return new Promise((resolve, reject) => { " +
            "    const s = document.createElement('script'); " +
            "    s.src = url; " +
            "    s.onload  = () => resolve(true); " +
            "    s.onerror = () => reject('Failed to load axe-core from: ' + url); " +
            "    document.head.appendChild(s); " +
            "  }); " +
            "}",
            cdnUrl
        );
        logger.debug("axe-core injected from CDN: {}", cdnUrl);
    }

    private static boolean isAxeAlreadyLoaded(Page page) {
        Object result = page.evaluate("() => typeof axe !== 'undefined'");
        return Boolean.TRUE.equals(result);
    }

    private static String loadFromClasspath() {
        try (InputStream is = AccessibilityValidator.class.getResourceAsStream(CLASSPATH_AXE_JS)) {
            if (is == null) return null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.debug("Could not load axe-core from classpath: {}", e.getMessage());
            return null;
        }
    }

    // ── HTML report builder ───────────────────────────────────────────────────

    private static String buildHtml(List<AxeViolation> violations) {
        String title = "Accessibility Report — " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, List<AxeViolation>> grouped = violations.stream()
                .collect(Collectors.groupingBy(v -> v.impact().toLowerCase()));

        StringBuilder rows = new StringBuilder();
        List<String> impactOrder = List.of("critical", "serious", "moderate", "minor", "unknown");
        impactOrder.stream()
                .filter(grouped::containsKey)
                .map(grouped::get)
                .flatMap(Collection::stream)
                .forEach(v -> {
                    String badgeClass = switch (v.impact().toLowerCase()) {
                        case "critical" -> "badge-critical";
                        case "serious"  -> "badge-serious";
                        case "moderate" -> "badge-moderate";
                        default         -> "badge-minor";
                    };
                    rows.append("<tr>")
                        .append("<td><span class='badge ").append(badgeClass).append("'>")
                        .append(v.impact()).append("</span></td>")
                        .append("<td><a href='").append(v.helpUrl()).append("' target='_blank'>")
                        .append(escape(v.id())).append("</a></td>")
                        .append("<td>").append(escape(v.help())).append("</td>")
                        .append("<td><code>").append(escape(String.join("<br>", v.affectedNodes())))
                        .append("</code></td>")
                        .append("<td>").append(escape(v.failureSummary())).append("</td>")
                        .append("</tr>\n");
                });

        String status = violations.isEmpty()
                ? "<p class='pass'>No violations found.</p>"
                : "<p class='fail'>" + violations.size() + " violation(s) found.</p>";

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>" +
               "<title>" + title + "</title><style>" +
               "body{font-family:sans-serif;margin:2em;}" +
               "table{border-collapse:collapse;width:100%;}" +
               "th,td{border:1px solid #ccc;padding:8px;text-align:left;vertical-align:top;}" +
               "th{background:#f5f5f5;}" +
               ".badge{padding:2px 8px;border-radius:4px;color:#fff;font-weight:bold;}" +
               ".badge-critical{background:#b71c1c;}" +
               ".badge-serious{background:#e53935;}" +
               ".badge-moderate{background:#f57c00;}" +
               ".badge-minor{background:#7cb342;}" +
               ".pass{color:green;font-weight:bold;}" +
               ".fail{color:red;font-weight:bold;}" +
               "code{font-size:0.85em;word-break:break-all;}" +
               "</style></head><body>" +
               "<h1>Accessibility Report</h1>" +
               "<p>" + title + "</p>" +
               status +
               (violations.isEmpty() ? "" :
               "<table><thead><tr>" +
               "<th>Impact</th><th>Rule ID</th><th>Help</th><th>Affected Nodes</th><th>Failure</th>" +
               "</tr></thead><tbody>" + rows + "</tbody></table>") +
               "</body></html>";
    }

    // ── Formatting helpers ────────────────────────────────────────────────────

    private static String formatViolationSummary(List<AxeViolation> violations) {
        StringBuilder sb = new StringBuilder();
        violations.stream()
                .sorted(Comparator.comparing(v -> impactOrdinal(v.impact())))
                .forEach(v -> sb.append("  [").append(v.impact().toUpperCase()).append("] ")
                        .append(v.id()).append(": ").append(v.help()).append("\n")
                        .append("    Nodes: ").append(v.affectedNodes()).append("\n")
                        .append("    More: ").append(v.helpUrl()).append("\n"));
        return sb.toString();
    }

    private static int impactOrdinal(String impact) {
        return switch (impact.toLowerCase()) {
            case "critical" -> 0;
            case "serious"  -> 1;
            case "moderate" -> 2;
            case "minor"    -> 3;
            default         -> 4;
        };
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }

    @SuppressWarnings("unchecked")
    private static List<String> toStringList(Object v) {
        if (!(v instanceof List)) return List.of();
        return ((List<Object>) v).stream()
                .map(Object::toString)
                .toList();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
