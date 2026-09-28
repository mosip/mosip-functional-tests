package io.mosip.testrig.apirig.coverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.mosip.testrig.apirig.coverage.MatrixFile.Row;
import io.mosip.testrig.apirig.coverage.MatrixFile.Status;

/**
 * Flags pairs of rows in one matrix file that probably describe the same scenario — the typical
 * result of adding a story's scenarios without first reading the rows already there.
 *
 * <p>A heuristic, so it only warns. A pair is considered only when both rows have the same Type, target
 * the same unit, neither is {@code ⛔ not_automatable}, and at least one isn't backed by a YAML case (two
 * YAML-backed rows are two distinct tests). It is flagged when:
 * <ol>
 * <li>both expect the same error code(s) on a shared request field ({@code `version`}, {@code `langCode`},
 * ...) — ignoring fields that are the endpoint's own path/query parameters, which every row mentions;</li>
 * <li>it's a second {@code authn}/{@code authz} row for the same unit — one per unit is the norm; or</li>
 * <li>the Scenario wording is at least 70% the same.</li>
 * </ol>
 * Tuned against the pre-registration matrix: zero hits on its 753 authored rows, while catching
 * paraphrased duplicates ("`version` is missing" vs "`version` is omitted", same error code).
 */
final class DuplicateDetector {

	static final class Pair {
		final Row a;
		final Row b;
		final String reason;

		Pair(Row a, Row b, String reason) {
			this.a = a;
			this.b = b;
			this.reason = reason;
		}
	}

	private static final Pattern CODE = Pattern.compile("\\b(?:[A-Z]{2,}[-_])+[A-Z0-9]*\\d{2,}\\b");
	private static final Pattern FIELD = Pattern.compile("`([A-Za-z_][A-Za-z0-9_.]*)`");
	private static final Pattern PARAM = Pattern.compile("\\{([^}]+)\\}|[?&]([A-Za-z0-9_]+)=");
	private static final Pattern WORD = Pattern.compile("[a-z0-9_]+");
	private static final Set<String> SINGLETON = Set.of("authn", "authz");
	private static final Set<String> STOP = Set.of(("a an the is are be to of in on for with and or when then given caller "
			+ "submits request its it that this as by via same any value set applicant user").split(" "));
	static final double WORDING_THRESHOLD = 0.7;

	private DuplicateDetector() {
	}

	static List<Pair> find(List<Row> rows, List<String> units) {
		Set<String> unitParams = new HashSet<>();
		for (String u : units) {
			Matcher m = PARAM.matcher(u);
			while (m.find())
				unitParams.add((m.group(1) != null ? m.group(1) : m.group(2)).toLowerCase(Locale.ROOT));
		}
		List<Pair> out = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++)
			for (int j = i + 1; j < rows.size(); j++) {
				String why = reason(rows.get(i), rows.get(j), units, unitParams);
				if (why != null)
					out.add(new Pair(rows.get(i), rows.get(j), why));
			}
		return out;
	}

	private static String reason(Row a, Row b, List<String> units, Set<String> unitParams) {
		if (!a.wellFormed() || !b.wellFormed() || !a.type().equals(b.type()))
			return null;
		if (!a.testRefs().isEmpty() && !b.testRefs().isEmpty())
			return null;
		if (a.status() == Status.NOT_AUTOMATABLE || b.status() == Status.NOT_AUTOMATABLE)
			return null;
		if (units.size() > 1 && !java.util.Objects.equals(unitOf(a.scenario(), units), unitOf(b.scenario(), units)))
			return null;

		Set<String> codesA = codes(a.expected()), codesB = codes(b.expected());
		Set<String> shared = fields(a.scenario(), unitParams);
		shared.retainAll(fields(b.scenario(), unitParams));
		if (!codesA.isEmpty() && codesA.equals(codesB) && !shared.isEmpty())
			return "same error code " + String.join("/", codesA) + " on the same field " + String.join("/", shared);
		if (SINGLETON.contains(a.type()))
			return "a second `" + a.type() + "` row for the same endpoint";
		double sim = jaccard(words(a.scenario()), words(b.scenario()));
		if (sim >= WORDING_THRESHOLD)
			return "Scenario wording " + Math.round(sim * 100) + "% the same";
		return null;
	}

	/** The longest unit label the Scenario mentions — "/applications?type=" must not count as "/applications". */
	private static String unitOf(String scenario, List<String> units) {
		String best = null;
		for (String u : units)
			if (scenario.contains(u) && (best == null || u.length() > best.length()))
				best = u;
		return best;
	}

	private static Set<String> codes(String s) {
		Set<String> out = new TreeSet<>();
		Matcher m = CODE.matcher(s);
		while (m.find())
			out.add(m.group());
		return out;
	}

	private static Set<String> fields(String s, Set<String> exclude) {
		Set<String> out = new TreeSet<>();
		Matcher m = FIELD.matcher(s);
		while (m.find()) {
			String f = m.group(1).toLowerCase(Locale.ROOT);
			if (!exclude.contains(f) && !CODE.matcher(m.group(1)).matches())
				out.add(f);
		}
		return out;
	}

	private static Set<String> words(String s) {
		Set<String> out = new HashSet<>();
		Matcher m = WORD.matcher(s.toLowerCase(Locale.ROOT).replace("`", " "));
		while (m.find())
			if (m.group().length() > 1 && !STOP.contains(m.group()))
				out.add(m.group());
		return out;
	}

	private static double jaccard(Set<String> a, Set<String> b) {
		Set<String> union = new HashSet<>(a);
		union.addAll(b);
		if (union.isEmpty())
			return 0;
		Set<String> inter = new HashSet<>(a);
		inter.retainAll(b);
		return (double) inter.size() / union.size();
	}
}
