package io.mosip.testrig.apirig.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Category checklist for a matrix subject: the core 8 seeded on every API subject, plus the
 * extras that only apply where the real code/YAML shows the surface.
 *
 * <p>{@code multilang} and {@code dependency_state} are detected mechanically from the YAML.
 * {@code crypto_integrity} and {@code injection} need a human to confirm them against the real
 * test-script code, so they are never auto-added — once an author puts one into a file's
 * front-matter, it is preserved from then on.
 */
public final class CategoryChecklist {

	public static final List<String> CORE = List.of("positive", "authn", "authz", "validation", "not_found",
			"boundary", "idempotency", "data_isolation");

	/** Fixed render order for extras. */
	public static final List<String> EXTRAS = List.of("multilang", "crypto_integrity", "dependency_state",
			"injection");

	private static final List<String> HUMAN_CONFIRMED_EXTRAS = List.of("crypto_integrity", "injection");

	private CategoryChecklist() {
	}

	/**
	 * @param multilang          subject's YAML uses {@code templateFields}
	 * @param dependencyState    subject's YAML references a prior case via {@code $ID:...$}
	 * @param existingCategories categories already in the matrix file (null for a new file)
	 */
	public static List<String> categoriesFor(boolean multilang, boolean dependencyState,
			Collection<String> existingCategories) {
		List<String> result = new ArrayList<>(CORE);
		for (String extra : EXTRAS) {
			boolean applies = ("multilang".equals(extra) && multilang)
					|| ("dependency_state".equals(extra) && dependencyState)
					|| (HUMAN_CONFIRMED_EXTRAS.contains(extra) && existingCategories != null
							&& existingCategories.contains(extra));
			if (applies)
				result.add(extra);
		}
		return result;
	}

	/** Render order across the estate: core first, then extras, then anything unknown. */
	public static int order(String category) {
		int i = CORE.indexOf(category);
		if (i >= 0)
			return i;
		i = EXTRAS.indexOf(category);
		return i >= 0 ? CORE.size() + i : CORE.size() + EXTRAS.size();
	}
}
