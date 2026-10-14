package io.mosip.testrig.uitestrig.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Loads known-issue mappings from {@code config/Known_Issues.txt} on the classpath.
 *
 * File format (one mapping per line):
 *   bugId------Scenario name exactly as written in the feature file
 *   # lines starting with # are comments
 *
 * Example:
 *   RES-1234------Verify OTP login with perpetual VID
 *   RES-1235------Verify temporary VID generation and deletion
 *
 * A scenario whose name appears in this file is automatically skipped and
 * reported as "Known Issue" with a JIRA link in the Extent report.
 */
public class KnownIssueManager {

    private static final Logger logger = LoggerFactory.getLogger(KnownIssueManager.class);
    private static final String SEPARATOR = "------";
    private static final Map<String, String> knownIssues = new ConcurrentHashMap<>();

    static {
        loadKnownIssues();
    }

    private KnownIssueManager() {}

    /**
     * Returns the bug ID for a scenario, or {@code null} if it is not a known issue.
     *
     * @param scenarioName exact scenario name as it appears in the feature file
     */
    public static String getBugId(String scenarioName) {
        if (scenarioName == null) return null;
        return knownIssues.get(scenarioName.replaceAll("\\s+", " ").trim());
    }

    public static boolean isKnownIssue(String scenarioName) {
        return getBugId(scenarioName) != null;
    }

    public static int size() {
        return knownIssues.size();
    }

    private static void loadKnownIssues() {
        try (InputStream is = KnownIssueManager.class.getClassLoader()
                .getResourceAsStream("config/Known_Issues.txt")) {
            if (is == null) {
                logger.debug("config/Known_Issues.txt not found on classpath — no known issues configured");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#") || !line.contains(SEPARATOR)) continue;
                    String[] parts = line.split(SEPARATOR, 2);
                    if (parts.length == 2) {
                        String bugId       = parts[0].trim();
                        String scenarioName = parts[1].trim().replaceAll("\\s+", " ");
                        knownIssues.put(scenarioName, bugId);
                    }
                }
            }
            logger.info("Known Issues loaded: {} entries", knownIssues.size());
        } catch (IOException e) {
            logger.error("Failed to load Known_Issues.txt: {}", e.getMessage());
        }
    }
}
