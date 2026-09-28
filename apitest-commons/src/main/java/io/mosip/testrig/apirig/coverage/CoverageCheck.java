package io.mosip.testrig.apirig.coverage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.mosip.testrig.apirig.coverage.ControllerScanner.Endpoint;
import io.mosip.testrig.apirig.coverage.Inventory.Subject;
import io.mosip.testrig.apirig.coverage.Inventory.TestCase;
import io.mosip.testrig.apirig.coverage.Inventory.YamlFile;
import io.mosip.testrig.apirig.coverage.MatrixFile.Row;
import io.mosip.testrig.apirig.coverage.MatrixFile.Status;

/**
 * Read-only gap report. Every gap has a stable {@code type} + {@code key}; a gap listed in the
 * baseline file (with a reason) is reported as accepted instead of failing the run.
 */
public final class CoverageCheck {

	public static final class Gap {
		public final String type;
		public final String key;
		public final String message;
		/** A warning is reported but doesn't fail `check` (unless --fail-on-warnings). */
		public final boolean warning;

		Gap(String type, String key, String message) {
			this(type, key, message, false);
		}

		Gap(String type, String key, String message, boolean warning) {
			this.type = type;
			this.key = key;
			this.message = message;
			this.warning = warning;
		}

		String baselineKey() {
			return type + " " + key;
		}
	}

	private final Estate estate;
	private final List<Gap> gaps = new ArrayList<>();

	private CoverageCheck(Estate estate) {
		this.estate = estate;
	}

	public static List<Gap> run(Estate estate) {
		CoverageCheck c = new CoverageCheck(estate);
		c.inventoryGaps();
		c.matrixGaps();
		c.endpointGaps();
		c.legacyGaps();
		c.baselineGaps();
		return c.gaps;
	}

	// ---------------------------------------------------------------- legacy test-case IDs

	/**
	 * Every ID in {@code legacy-ids.txt} (test-case numbers from a retired sheet, e.g. an Excel master
	 * test-case list) must appear in some matrix file — typically as {@code Legacy: <id>} in a row's
	 * Notes — unless its line gives a reason it lives outside the matrix. Proves a migration lost nothing,
	 * and keeps proving it.
	 */
	private void legacyGaps() {
		if (estate.legacy == null || estate.legacy.entries.isEmpty())
			return;
		StringBuilder all = new StringBuilder();
		for (Estate.Loaded lf : estate.files.values())
			all.append(lf.text).append('\n');
		String text = all.toString();
		for (Baseline.Entry e : estate.legacy.entries) {
			if (!e.reason.isBlank())
				continue;
			java.util.regex.Pattern p = java.util.regex.Pattern
					.compile("(?<![A-Za-z0-9_])" + java.util.regex.Pattern.quote(e.key) + "(?![A-Za-z0-9_])");
			if (!p.matcher(text).find())
				add("legacy-id-missing", e.key, estate.legacy.relPath + ":" + e.line + " legacy test case `" + e.key
						+ "` appears in no matrix file — add `Legacy: " + e.key
						+ "` to the Notes of the row that covers it (or a new row), or give the line a '# reason' it is out of scope");
		}
	}

	private void add(String type, String key, String message) {
		gaps.add(new Gap(type, key, message));
	}

	private void warn(String type, String key, String message) {
		gaps.add(new Gap(type, key, message, true));
	}

	// ---------------------------------------------------------------- api-test side

	private void inventoryGaps() {
		Inventory inv = estate.inventory;
		for (String p : inv.problems)
			add("malformed-file", p, p);
		for (YamlFile yf : inv.yamlFiles.values()) {
			if (yf.parseError != null)
				add("malformed-file", yf.relPath, yf.relPath + ": YAML does not parse — " + yf.parseError);
			else if (yf.cases.isEmpty())
				add("empty-yml", yf.relPath, yf.relPath + " holds no test cases — accidentally emptied? "
						+ "Recover it from git or the built JAR (see CLAUDE.md § File Editing Rules)");
		}

		for (String yml : inv.unwiredYamlFiles())
			add("unwired-yml", yml, yml + " is on disk but no Suite.xml <test> runs it (wire it or delete it)");

		// Unique identifiers are the only link from a matrix row to a test — they must be unique module-wide.
		Map<String, List<String>> byUid = new LinkedHashMap<>();
		for (YamlFile yf : inv.yamlFiles.values())
			for (TestCase tc : yf.cases)
				if (tc.uniqueIdentifier != null)
					byUid.computeIfAbsent(tc.uniqueIdentifier.toLowerCase(Locale.ROOT), k -> new ArrayList<>())
							.add(yf.relPath + ":" + tc.line + " (" + tc.uniqueIdentifier + ")");
		byUid.forEach((uid, where) -> {
			if (where.size() > 1)
				add("duplicate-unique-identifier", uid,
						"uniqueIdentifier used by " + where.size() + " cases (case-insensitive): " + String.join(", ", where));
		});

		for (Subject s : inv.subjects) {
			for (TestCase tc : s.yaml.cases)
				if (tc.uniqueIdentifier == null || tc.uniqueIdentifier.isBlank())
					add("case-without-unique-identifier", s.yaml.relPath + "::" + tc.name,
							s.yaml.relPath + ":" + tc.line + " case " + tc.name
									+ " has no uniqueIdentifier — it can never be traced to a matrix row");
			String classVerb = s.classVerb();
			if (classVerb == null)
				continue;
			Set<String> wrong = new LinkedHashSet<>();
			for (TestCase tc : s.yaml.cases)
				if (tc.restMethod != null && !tc.restMethod.equalsIgnoreCase(classVerb))
					wrong.add(tc.restMethod.toLowerCase(Locale.ROOT));
			if (!wrong.isEmpty())
				add("verb-mismatch", s.yaml.relPath,
						s.yaml.relPath + " declares restMethod " + wrong + " but is wired to " + s.scriptClass
								+ ", which always sends " + classVerb + " — fix restMethod so the matrix Units are true");
		}
	}

	// ---------------------------------------------------------------- matrix side

	private void matrixGaps() {
		Inventory inv = estate.inventory;
		Set<String> referenced = new HashSet<>();

		for (Estate.Loaded lf : estate.files.values()) {
			if (lf.error != null) {
				add("malformed-file", lf.relPath, lf.relPath + ": " + lf.error);
				continue;
			}
			MatrixFile mf = lf.matrix;
			Subject s = inv.subject(mf.subject);
			if (s == null && !mf.planned) {
				add("stale-matrix-file", lf.relPath, lf.relPath + " is for subject " + mf.subject
						+ ", which no Suite.xml <test> runs any more — move its rows or delete it");
			}
			for (Row r : mf.rows)
				for (String ref : r.testRefs())
					referenced.add(ref);
			rowGaps(lf, mf);
			if (s != null)
				syncGaps(lf, mf, s.units,
						CategoryChecklist.categoriesFor(s.multilang, s.dependencyState, mf.categories), s);
			else if (mf.planned) // planned files get the same completeness rules, units kept from the file
				syncGaps(lf, mf, mf.units, CategoryChecklist.categoriesFor(false, false, mf.categories), null);
		}

		for (Subject s : inv.subjects) {
			if (!estate.files.containsKey(estate.matrixRelPath(s.name))) {
				add("no-matrix-file", s.name, "wired subject " + s.name + " has no matrix file — run scaffold");
				continue;
			}
			for (TestCase tc : s.yaml.cases)
				if (tc.uniqueIdentifier != null && !referenced.contains(s.yaml.relPath + "::" + tc.uniqueIdentifier))
					add("orphan-test-case", s.yaml.relPath + "::" + tc.uniqueIdentifier,
							s.yaml.relPath + "::" + tc.uniqueIdentifier + " is not referenced by any matrix row");
		}
	}

	private void rowGaps(Estate.Loaded lf, MatrixFile mf) {
		Map<String, Integer> ids = new HashMap<>();
		for (Row r : mf.rows) {
			String where = lf.relPath + ":" + r.line();
			if (!r.wellFormed()) {
				add("malformed-row", lf.relPath + "#" + r.id(), where + " row " + r.id() + " has " + r.cells.size()
						+ " cells, expected 8 — escape any '|' inside a cell as '\\|'");
			}
			if (ids.merge(r.id(), 1, Integer::sum) == 2)
				add("duplicate-row-id", lf.relPath + "#" + r.id(), where + " ID " + r.id() + " appears more than once");
			if (r.scenario().startsWith("(TODO)") || r.expected().equals("TODO"))
				add("unfilled-placeholder", lf.relPath + "#" + r.id(), where + " row " + r.id() + " is still a stub");
			if (r.status() == Status.UNKNOWN)
				add("unfilled-placeholder", lf.relPath + "#" + r.id() + "#status",
						where + " row " + r.id() + " has no recognizable Status");
			if (r.status() == Status.NOT_AUTOMATABLE && r.notes().isBlank())
				add("unfilled-placeholder", lf.relPath + "#" + r.id() + "#reason",
						where + " row " + r.id() + " is not_automatable but gives no reason in Notes");
			if (r.status() == Status.AUTOMATED) {
				List<String> refs = r.testRefs();
				if (refs.isEmpty())
					add("unresolvable-test-ref", lf.relPath + "#" + r.id(),
							where + " row " + r.id() + " is automated but its Test column names no <ymlPath>::<uniqueIdentifier>");
				for (String ref : refs)
					if (estate.resolve(ref) == null)
						add("unresolvable-test-ref", lf.relPath + "#" + r.id() + "#" + ref,
								where + " row " + r.id() + " references " + ref + ", which no YAML case has");
			}
		}
		int[] counted = mf.countStatuses();
		if (mf.declaredSummary != null && !java.util.Arrays.equals(mf.declaredSummary, counted))
			add("summary-drift", lf.relPath, lf.relPath + " front-matter summary "
					+ java.util.Arrays.toString(mf.declaredSummary) + " ≠ grid " + java.util.Arrays.toString(counted)
					+ " — run sync");
	}

	private void syncGaps(Estate.Loaded lf, MatrixFile mf, List<String> units, List<String> categories, Subject s) {
		for (Row r : mf.rows)
			if (!categories.contains(r.type()))
				add("unknown-category", lf.relPath + "#" + r.id(), lf.relPath + ":" + r.line() + " row " + r.id()
						+ " has Type `" + r.type() + "`, which isn't one of this file's categories " + categories);
		for (DuplicateDetector.Pair p : DuplicateDetector.find(mf.rows, units))
			warn("possible-duplicate-row", lf.relPath + "#" + p.a.id() + "#" + p.b.id(), lf.relPath + ": rows "
					+ p.a.id() + " (line " + p.a.line() + ") and " + p.b.id() + " (line " + p.b.line() + ") may describe "
					+ "the same scenario — " + p.reason + ". If they really differ, sharpen the Scenario text or baseline "
					+ "the pair; if not, merge them (keep the older row, move any `Legacy:`/story onto it)");
		for (String unit : units) {
			boolean anyRow = false;
			for (Row r : mf.rows)
				if (units.size() <= 1 || r.scenario().contains(unit))
					anyRow = true;
			if (!anyRow) {
				add("unit-no-row", lf.relPath + "#" + unit, lf.relPath + ": unit `" + unit + "` has no rows"
						+ (units.size() > 1 ? " (multi-unit subject: each row's Scenario must contain the exact unit label)" : ""));
				continue;
			}
			MatrixFile probe = new MatrixFile();
			probe.units = units;
			probe.rows.addAll(mf.rows);
			for (String cat : categories)
				if (!probe.hasRow(unit, cat))
					add("category-missing", lf.relPath + "#" + unit + "#" + cat,
							lf.relPath + ": unit `" + unit + "` has no `" + cat + "` row — run sync, then author it");
		}
		if (!units.equals(mf.units) || !categories.equals(mf.categories)
				|| !estate.renderSynced(lf, s, false).equals(lf.text))
			add("not-synced", lf.relPath, lf.relPath + " is out of date with its YAML/Suite.xml (units, categories, "
					+ "summary or Test links) — run sync");
	}

	// ---------------------------------------------------------------- service side

	private void endpointGaps() {
		if (estate.controllers == null) {
			if (estate.requireAppSource)
				add("app-source-missing", "-", "no controller source found (pass --app-source) — endpoint gaps can't be checked");
			return;
		}
		for (String u : estate.controllers.unresolved)
			add("unscannable-mapping", u, u);

		EndpointMap map = estate.endpointMap;
		Set<Endpoint> tested = new HashSet<>();
		for (Subject s : estate.inventory.subjects) {
			for (String unit : s.units) {
				String verb = s.classVerb() != null ? s.classVerb() : EndpointMap.verbOf(unit);
				Endpoint e = map.match(verb, EndpointMap.pathOf(unit));
				if (e == null)
					add("unreachable-endpoint", s.name + " " + unit, s.name + ": `" + (verb == null ? "" : verb + " ")
							+ EndpointMap.pathOf(unit) + "` matches no real controller mapping — wrong path/verb in the YAML?");
				else
					tested.add(e);
			}
		}
		Map<Endpoint, String> planned = new HashMap<>();
		for (Estate.Loaded lf : estate.files.values()) {
			if (lf.matrix == null || !lf.matrix.planned)
				continue;
			for (String unit : lf.matrix.units) {
				Endpoint e = map.match(EndpointMap.verbOf(unit), EndpointMap.pathOf(unit));
				if (e == null)
					add("unreachable-endpoint", lf.relPath + " " + unit,
							lf.relPath + ": planned unit `" + unit + "` matches no real controller mapping");
				else if (tested.contains(e))
					add("planned-endpoint-now-tested", lf.relPath, lf.relPath + ": `" + unit
							+ "` now has wired YAML — move these rows into that subject's matrix file and delete this one");
				else
					planned.put(e, lf.relPath);
			}
		}
		for (Endpoint e : estate.controllers.endpoints) {
			if (tested.contains(e) || planned.containsKey(e))
				continue;
			String label = map.externalLabel(e);
			add("unmapped-endpoint", label, "`" + label + "` (" + Inventory.rel(estate.moduleRoot.getParent(), e.file) + ":"
					+ e.line + ") has no wired YAML and no planned matrix file — run scaffold to plan it, or baseline it with a reason");
		}
	}

	// ---------------------------------------------------------------- baseline

	private void baselineGaps() {
		Set<String> keys = new HashSet<>();
		for (Gap g : gaps)
			keys.add(g.baselineKey());
		Set<String> endpointTypes = Set.of("unmapped-endpoint", "unreachable-endpoint", "planned-endpoint-now-tested",
				"unscannable-mapping", "app-source-missing");
		for (Baseline.Entry e : estate.baseline.entries) {
			// With endpoint checks off (--no-app-source / no source found) their entries can't match — not stale.
			boolean notEvaluated = estate.controllers == null && endpointTypes.contains(e.key.split(" ", 2)[0]);
			if (e.reason.isBlank())
				add("baseline-entry-without-reason", e.key, estate.baseline.relPath + ":" + e.line + " `" + e.key
						+ "` has no '# reason' — every accepted gap must say why");
			else if (!notEvaluated && !keys.contains(e.key))
				add("stale-baseline-entry", e.key, estate.baseline.relPath + ":" + e.line + " `" + e.key
						+ "` no longer matches any gap — remove it");
		}
	}

	/** Parsed {@code check-baseline.txt}: one {@code <gap-type> <key>  # reason} per line. */
	public static final class Baseline {
		static final class Entry {
			final String key;
			final String reason;
			final int line;

			Entry(String key, String reason, int line) {
				this.key = key;
				this.reason = reason;
				this.line = line;
			}
		}

		final List<Entry> entries = new ArrayList<>();
		final String relPath;

		Baseline(String relPath) {
			this.relPath = relPath;
		}

		static Baseline load(Path file, String relPath) throws IOException {
			Baseline b = new Baseline(relPath);
			if (file == null || !Files.isRegularFile(file))
				return b;
			List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
			for (int i = 0; i < lines.size(); i++) {
				String l = lines.get(i).trim();
				if (l.isEmpty() || l.startsWith("#"))
					continue;
				int hash = l.indexOf(" #");
				String key = (hash >= 0 ? l.substring(0, hash) : l).trim().replaceAll("[ \\t]{2,}|\\t", " ");
				String reason = hash >= 0 ? l.substring(hash + 2).trim() : "";
				b.entries.add(new Entry(key, reason, i + 1));
			}
			return b;
		}

		boolean accepts(Gap g) {
			if (g.type.startsWith("baseline-") || g.type.equals("stale-baseline-entry"))
				return false; // the baseline can't excuse its own hygiene
			String k = g.baselineKey().replaceAll("\\s+", " ");
			for (Entry e : entries)
				if (e.key.equals(k) && !e.reason.isBlank())
					return true;
			return false;
		}
	}
}
