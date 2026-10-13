package io.mosip.testrig.uitestrig.reporting;

import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * Resolves details of the <em>application under test</em> (the deployed MOSIP service) for the
 * report's System/Environment table — distinct from this test suite's own git commit.
 *
 * <p>Two facts are resolved from a single Spring Boot {@code /actuator/info} call:</p>
 * <ul>
 *   <li><b>version</b> — {@code build.version} (prefixed with {@code build.artifact} when present).</li>
 *   <li><b>commit</b>  — {@code git.commit.id} (abbreviated), when the service exposes a git block.</li>
 * </ul>
 *
 * <p>Resolution order per fact (first non-blank wins):</p>
 * <ol>
 *   <li>Explicit override property — {@code appVersion} / {@code appCommit} (env var / module props).</li>
 *   <li>{@code appVersionEndpoint} — an {@code /actuator/info} path fragment appended to
 *       {@code apiInternalEndPoint} (e.g. {@code /resident/v1/actuator/info}).</li>
 *   <li>{@code "n/a"} — nothing configured, unreachable, or the field is absent.</li>
 * </ol>
 *
 * <p>Fully fail-soft: any network or parse error is logged and yields {@code "n/a"} so report
 * generation is never blocked. The backend service version/commit is used as the proxy for the
 * release under test — the SPA frontend does not expose a machine-readable version.</p>
 */
public final class AppVersionResolver {

    private static final Logger logger = LoggerFactory.getLogger(AppVersionResolver.class);
    private static final String NA = "n/a";

    private AppVersionResolver() {}

    /** Immutable holder for the two resolved facts. */
    public record AppInfo(String version, String commit) {}

    public static AppInfo resolve() {
        String manualVersion = trimToNull(UiConfigManager.getproperty("appVersion"));
        String manualCommit  = trimToNull(UiConfigManager.getproperty("appCommit"));

        // Fetch the actuator/info body once, only if something still needs resolving.
        String body = null;
        if (manualVersion == null || manualCommit == null) {
            body = fetchInfoBody();
        }

        String version = (manualVersion != null) ? manualVersion : orNa(parseVersion(body));
        String commit  = (manualCommit  != null) ? manualCommit  : orNa(parseCommit(body));
        return new AppInfo(version, commit);
    }

    private static String fetchInfoBody() {
        String endpoint = trimToNull(UiConfigManager.getproperty("appVersionEndpoint"));
        String base     = trimToNull(UiConfigManager.getproperty("apiInternalEndPoint"));
        if (endpoint == null || base == null) return null;

        String url = base.replaceAll("/+$", "") + endpoint;
        try {
            String body = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON)
                    .asString();
            logger.info("Fetched app-under-test info from {}", url);
            return body;
        } catch (Exception e) {
            logger.warn("Could not fetch app info from {}: {}", url, e.getMessage());
            return null;
        }
    }

    /** Reads {@code build.version} (prefixed with {@code build.artifact} when present). */
    private static String parseVersion(String infoJson) {
        if (infoJson == null || infoJson.isBlank()) return null;
        try {
            JSONObject build = new JSONObject(infoJson).optJSONObject("build");
            if (build == null) return null;
            String version = build.optString("version", "");
            if (version.isBlank()) return null;
            String artifact = build.optString("artifact", "");
            return artifact.isBlank() ? version : artifact + " " + version;
        } catch (Exception e) {
            logger.warn("Could not parse build.version from actuator/info: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Reads the abbreviated {@code git.commit.id}. Handles both Spring Boot "simple" mode
     * ({@code commit.id} is a string) and "full" mode ({@code commit.id.abbrev}).
     */
    private static String parseCommit(String infoJson) {
        if (infoJson == null || infoJson.isBlank()) return null;
        try {
            JSONObject git = new JSONObject(infoJson).optJSONObject("git");
            if (git == null) return null;
            JSONObject commit = git.optJSONObject("commit");
            if (commit == null) return null;

            Object id = commit.opt("id");
            if (id instanceof JSONObject idObj) {          // full mode: { "abbrev": "...", "full": "..." }
                String abbrev = idObj.optString("abbrev", "");
                if (!abbrev.isBlank()) return abbrev;
                String full = idObj.optString("full", "");
                return abbreviate(full);
            }
            return (id != null) ? abbreviate(id.toString()) : null;   // simple mode: "id": "<sha>"
        } catch (Exception e) {
            logger.warn("Could not parse git.commit.id from actuator/info: {}", e.getMessage());
            return null;
        }
    }

    private static String abbreviate(String sha) {
        if (sha == null || sha.isBlank()) return null;
        return sha.length() > 8 ? sha.substring(0, 8) : sha;
    }

    private static String trimToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static String orNa(String s) {
        return (s == null || s.isBlank()) ? NA : s;
    }
}
