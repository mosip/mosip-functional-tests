package io.mosip.testrig.apirig.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Post-generation HTML integrity validator for the DSL emailable report.
 *
 * Checks that:
 *  - Every sidebar anchor (#mN) has a matching <h3 id="mN"> in the main panel.
 *  - Every <h3 id="mN"> in the main panel has a matching sidebar entry.
 *  - No duplicate scenario anchors exist.
 *  - No duplicate step IDs exist.
 *  - Sidebar scenario count equals rendered scenario count.
 *  - Sidebar step count equals rendered step count.
 *
 * Call {@link #validate(String)} with the fully enhanced HTML string. Any
 * violation is logged at ERROR level; the method returns false if any
 * violation is found so callers can fail-fast.
 */
public final class ReportIntegrityValidator {

    private static final Logger logger = Logger.getLogger(ReportIntegrityValidator.class);

    // Matches sidebar anchor links: href="#mN"
    private static final Pattern SIDEBAR_ANCHOR =
            Pattern.compile("href=\"#(m\\d+)\"", Pattern.CASE_INSENSITIVE);

    // Matches main-panel scenario headers: <h3 id="mN"
    private static final Pattern PANEL_H3 =
            Pattern.compile("<h3[^>]*\\bid\\s*=\\s*['\"]?(m\\d+)['\"]?[^>]*>",
                    Pattern.CASE_INSENSITIVE);

    // Matches injected step IDs: id="mN-sK"
    private static final Pattern STEP_ID =
            Pattern.compile("\\bid\\s*=\\s*['\"]?(m\\d+-s\\d+)['\"]?",
                    Pattern.CASE_INSENSITIVE);

    // Matches sidebar step count summary text: "(N)" inside tree-steps-count span
    private static final Pattern SIDEBAR_STEP_COUNT =
            Pattern.compile("<span class=\"tree-steps-count\">\\((\\d+)\\)</span>",
                    Pattern.CASE_INSENSITIVE);

    private ReportIntegrityValidator() {}

    /**
     * Validates the given enhanced HTML report string.
     *
     * @param html  the fully enhanced HTML (after ReportTreeViewEnhancer.enhance())
     * @return true if all integrity checks pass, false if any violation found
     */
    public static boolean validate(String html) {
        if (html == null || html.isEmpty()) {
            logger.error("[REPORT-INTEGRITY] validate() called with null/empty HTML.");
            return false;
        }

        boolean ok = true;

        // Extract only the sidebar nav so SIDEBAR_ANCHOR matches don't double-count
        // the summary table's <a href="#mN"> links, which use the same href pattern.
        String navHtml = extractNavHtml(html);

        // --- Collect sidebar scenario anchors (only inside the nav) ---
        List<String> sidebarAnchors = new ArrayList<>();
        Set<String> sidebarDuplicates = new LinkedHashSet<>();
        Matcher sm = SIDEBAR_ANCHOR.matcher(navHtml);
        while (sm.find()) {
            String anchor = sm.group(1);
            if (sidebarAnchors.contains(anchor)) {
                sidebarDuplicates.add(anchor);
            }
            sidebarAnchors.add(anchor);
        }

        // --- Collect main-panel scenario H3 ids ---
        List<String> panelIds = new ArrayList<>();
        Set<String> panelDuplicates = new LinkedHashSet<>();
        Matcher pm = PANEL_H3.matcher(html);
        while (pm.find()) {
            String id = pm.group(1);
            if (panelIds.contains(id)) {
                panelDuplicates.add(id);
            }
            panelIds.add(id);
        }

        // --- Collect step IDs ---
        Map<String, Integer> stepIdCounts = new LinkedHashMap<>();
        Matcher stm = STEP_ID.matcher(html);
        while (stm.find()) {
            String sid = stm.group(1);
            stepIdCounts.merge(sid, 1, Integer::sum);
        }
        List<String> duplicateStepIds = new ArrayList<>();
        for (Map.Entry<String, Integer> e : stepIdCounts.entrySet()) {
            if (e.getValue() > 1) {
                duplicateStepIds.add(e.getKey() + "(x" + e.getValue() + ")");
            }
        }

        // --- Collect sidebar step counts (only inside the nav) ---
        int sidebarTotalSteps = 0;
        Matcher ssc = SIDEBAR_STEP_COUNT.matcher(navHtml);
        while (ssc.find()) {
            sidebarTotalSteps += Integer.parseInt(ssc.group(1));
        }
        int renderedStepIds = stepIdCounts.size();

        // --- Check 1: Scenario count match ---
        int sidebarScenarioCount = sidebarAnchors.size();
        int panelScenarioCount = panelIds.size();
        if (sidebarScenarioCount != panelScenarioCount) {
            logger.error("[REPORT-INTEGRITY] FAIL sidebarScenarioCount=" + sidebarScenarioCount
                    + " != renderedScenarioCount=" + panelScenarioCount
                    + ". Missing anchors: " + missingFrom(sidebarAnchors, panelIds)
                    + ". Orphan anchors: " + missingFrom(panelIds, sidebarAnchors));
            ok = false;
        } else {
            logger.info("[REPORT-INTEGRITY] OK  sidebarScenarioCount=" + sidebarScenarioCount
                    + " == renderedScenarioCount=" + panelScenarioCount);
        }

        // --- Check 2: Sidebar anchor order matches panel order ---
        List<String> sidebarUnique = deduplicated(sidebarAnchors);
        List<String> panelUnique = deduplicated(panelIds);
        if (!sidebarUnique.equals(panelUnique)) {
            logger.error("[REPORT-INTEGRITY] FAIL sidebar anchor order differs from panel H3 order."
                    + " sidebar=" + sidebarUnique + " panel=" + panelUnique);
            ok = false;
        } else if (sidebarScenarioCount == panelScenarioCount) {
            logger.info("[REPORT-INTEGRITY] OK  anchor order matches.");
        }

        // --- Check 3: Duplicate sidebar scenario anchors ---
        if (!sidebarDuplicates.isEmpty()) {
            logger.error("[REPORT-INTEGRITY] FAIL duplicate sidebar anchors: " + sidebarDuplicates);
            ok = false;
        }

        // --- Check 4: Duplicate panel H3 ids ---
        if (!panelDuplicates.isEmpty()) {
            logger.error("[REPORT-INTEGRITY] FAIL duplicate panel H3 ids: " + panelDuplicates);
            ok = false;
        }

        // --- Check 5: Duplicate step IDs ---
        if (!duplicateStepIds.isEmpty()) {
            logger.error("[REPORT-INTEGRITY] FAIL duplicate step IDs: " + duplicateStepIds);
            ok = false;
        }

        // --- Check 6: Sidebar step count vs rendered step IDs ---
        if (sidebarTotalSteps != renderedStepIds) {
            logger.error("[REPORT-INTEGRITY] FAIL sidebarStepCount=" + sidebarTotalSteps
                    + " != renderedStepCount=" + renderedStepIds);
            ok = false;
        } else {
            logger.info("[REPORT-INTEGRITY] OK  sidebarStepCount=" + sidebarTotalSteps
                    + " == renderedStepCount=" + renderedStepIds);
        }

        // --- Per-scenario breakdown ---
        for (String anchor : panelUnique) {
            int sidebarStepsForScenario = countStepsInSidebar(navHtml, anchor);
            int renderedStepsForScenario = countRenderedStepsForScenario(stepIdCounts, anchor);
            String status = (sidebarStepsForScenario == renderedStepsForScenario) ? "OK " : "FAIL";
            if ("FAIL".equals(status)) {
                logger.error("[REPORT] Scenario=" + anchor
                        + " ScenarioId=" + anchor
                        + " Thread=report"
                        + " CapturedSteps=" + renderedStepsForScenario
                        + " RenderedSteps=" + renderedStepsForScenario
                        + " Sidebar=" + sidebarAnchors.contains(anchor)
                        + " MainPanel=" + panelIds.contains(anchor)
                        + " SidebarSteps=" + sidebarStepsForScenario
                        + " MISMATCH");
                ok = false;
            } else {
                logger.info("[REPORT] Scenario=" + anchor
                        + " ScenarioId=" + anchor
                        + " CapturedSteps=" + renderedStepsForScenario
                        + " RenderedSteps=" + renderedStepsForScenario
                        + " Sidebar=true MainPanel=true SidebarSteps=" + sidebarStepsForScenario
                        + " OK");
            }
        }

        if (ok) {
            logger.info("[REPORT-INTEGRITY] All checks passed. Scenarios=" + panelScenarioCount
                    + " Steps=" + renderedStepIds);
        }
        return ok;
    }

    /** Extracts the {@code <nav id="report-scenario-tree">…</nav>} block, or returns
     *  an empty string if not found. Used to scope sidebar-only checks so that summary
     *  table links (which use the same {@code href="#mN"} pattern) are not counted. */
    private static String extractNavHtml(String html) {
        int navOpen = indexOfIgnoreCase(html, "<nav", 0);
        while (navOpen >= 0) {
            int navGt = html.indexOf('>', navOpen);
            if (navGt < 0) break;
            String openTag = html.substring(navOpen, navGt + 1);
            if (openTag.toLowerCase().contains("report-scenario-tree")) {
                int navClose = indexOfIgnoreCase(html, "</nav>", navGt + 1);
                if (navClose < 0) return html.substring(navOpen);
                return html.substring(navOpen, navClose + 6);
            }
            navOpen = indexOfIgnoreCase(html, "<nav", navGt + 1);
        }
        return "";
    }

    private static int countStepsInSidebar(String html, String anchor) {
        // find the "(N)" count span that belongs to this scenario's details block in the sidebar
        String marker = "href=\"#" + anchor + "\"";
        int idx = indexOfIgnoreCase(html, marker, 0);
        if (idx < 0) {
            return 0;
        }
        // look for the nearest tree-steps-count after the anchor link
        int countIdx = indexOfIgnoreCase(html, "tree-steps-count", idx);
        if (countIdx < 0) {
            return 0;
        }
        // make sure it belongs to this scenario node, not the next (stop at next scenario anchor)
        String nextMarkerPattern = "href=\"#m";
        int nextAnchorIdx = indexOfIgnoreCase(html, nextMarkerPattern, idx + marker.length());
        if (nextAnchorIdx > 0 && countIdx > nextAnchorIdx) {
            return 0;
        }
        Matcher m = SIDEBAR_STEP_COUNT.matcher(html.substring(countIdx, Math.min(countIdx + 200, html.length())));
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static int countRenderedStepsForScenario(Map<String, Integer> stepIdCounts, String anchor) {
        String prefix = anchor + "-s";
        int count = 0;
        for (String sid : stepIdCounts.keySet()) {
            if (sid.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    private static List<String> missingFrom(List<String> source, List<String> target) {
        List<String> missing = new ArrayList<>(source);
        missing.removeAll(target);
        return missing;
    }

    private static List<String> deduplicated(List<String> list) {
        return new ArrayList<>(new java.util.LinkedHashSet<>(list));
    }

    private static int indexOfIgnoreCase(String haystack, String needle, int from) {
        int max = haystack.length() - needle.length();
        for (int i = Math.max(0, from); i <= max; i++) {
            if (haystack.regionMatches(true, i, needle, 0, needle.length())) {
                return i;
            }
        }
        return -1;
    }
}
