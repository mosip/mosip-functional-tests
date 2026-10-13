package io.mosip.testrig.uitestrig.accessibility;

import java.util.List;

/**
 * Immutable representation of a single axe-core accessibility violation.
 *
 * Populated by {@link AccessibilityValidator#runAxeCore} from the JSON object
 * returned by {@code axe.run()}.  One {@code AxeViolation} maps to one entry
 * in the {@code violations} array of the axe result.
 *
 * @param id              axe rule ID, e.g. {@code "color-contrast"}, {@code "button-name"}
 * @param impact          severity — {@code "critical"}, {@code "serious"}, {@code "moderate"}, {@code "minor"}
 * @param description     human-readable rule description
 * @param help            short help text (what to fix)
 * @param helpUrl         link to the Deque rule reference page
 * @param affectedNodes   list of CSS target paths identifying the failing DOM nodes
 * @param failureSummary  plain-text summary of why the nodes fail
 */
public record AxeViolation(
        String       id,
        String       impact,
        String       description,
        String       help,
        String       helpUrl,
        List<String> affectedNodes,
        String       failureSummary
) {

    /**
     * Returns {@code true} if this violation has {@code critical} or {@code serious} impact.
     * Useful for quick filtering when only blockers should fail a build.
     */
    public boolean isBlocker() {
        return "critical".equalsIgnoreCase(impact) || "serious".equalsIgnoreCase(impact);
    }

    @Override
    public String toString() {
        return "[" + impact.toUpperCase() + "] " + id + ": " + help
               + " — nodes: " + affectedNodes
               + " (" + helpUrl + ")";
    }
}
