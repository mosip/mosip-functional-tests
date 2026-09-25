package io.mosip.testrig.apirig.coverage;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

/**
 * One matrix markdown file: parse → merge → render.
 *
 * <p>Rendering is non-destructive and idempotent: every existing row's raw source line is
 * re-emitted verbatim (only its Test cell is rewritten, and only when the derived link differs),
 * and the hand-authored "About this endpoint" block is kept byte-for-byte. Everything above the
 * grid is regenerated.
 */
public final class MatrixFile {

	public static final String GRID_START = "<!-- GENERATED:grid -->";
	public static final String GRID_END = "<!-- /GENERATED:grid -->";
	public static final String EP_START = "<!-- ENDPOINT:details -->";
	public static final String EP_END = "<!-- /ENDPOINT:details -->";
	static final String GRID_HEADER = "| ID | Scenario (given/when) | Expected result (then) | Type | Tier | Status | Test | Notes |";
	static final String GRID_SEPARATOR = "|---|---|---|---|---|---|---|---|";
	static final String ENDPOINT_PLACEHOLDER = "_TODO — describe this endpoint for a non-automation reader (see `AGENTS.md` § \"About this endpoint\")._\n";
	static final String AUTOMATED = "✅ automated";
	static final String NOT_AUTOMATED = "🟡 not_automated";
	static final String NOT_AUTOMATABLE = "⛔ not_automatable";

	public enum Status {
		AUTOMATED, NOT_AUTOMATED, NOT_AUTOMATABLE, UNKNOWN;

		/** Most-specific-first: "automated" is a substring of "not_automated". */
		static Status of(String cell) {
			String c = cell == null ? "" : cell.toLowerCase(Locale.ROOT);
			if (c.contains("not_automatable"))
				return NOT_AUTOMATABLE;
			if (c.contains("not_automated"))
				return NOT_AUTOMATED;
			if (c.contains("automated"))
				return AUTOMATED;
			return UNKNOWN;
		}
	}

	public static final class Row {
		String raw;
		final List<String> cells; // trimmed; exactly 8 when well-formed
		final int line; // 1-based line in the file (0 for a new stub)

		Row(String raw, int line) {
			this.raw = raw;
			this.line = line;
			this.cells = splitCells(raw);
		}

		public boolean wellFormed() {
			return cells.size() == 8;
		}

		public String id() {
			return cell(0);
		}

		public String scenario() {
			return cell(1);
		}

		public String expected() {
			return cell(2);
		}

		public String type() {
			return cell(3);
		}

		public Status status() {
			return Status.of(cell(5));
		}

		public String test() {
			return cell(6);
		}

		/** Notes, folding any overflow cells back in (a stray unescaped pipe in the Notes text). */
		public String notes() {
			if (cells.size() <= 7)
				return "";
			return String.join(" | ", cells.subList(7, cells.size()));
		}

		public int line() {
			return line;
		}

		/** Every {@code <ymlPath>::<uniqueIdentifier>} reference in the Test cell. */
		public List<String> testRefs() {
			List<String> refs = new ArrayList<>();
			Matcher m = TEST_REF.matcher(test().replaceAll("\\]\\([^)]*\\)", "]"));
			while (m.find())
				refs.add(m.group(1));
			return refs;
		}

		private String cell(int i) {
			return i < cells.size() ? cells.get(i) : "";
		}
	}

	/** A ymlPath::uid token; the ymlPath part never contains whitespace, brackets or pipes. */
	static final Pattern TEST_REF = Pattern.compile("([^\\s\\[\\]()|,`]+\\.ya?ml::[A-Za-z0-9_.\\-]+)");
	private static final Pattern STUB_ID = Pattern.compile("^API-(.+)-(\\d+)$");

	// ---------------------------------------------------------------- state

	String subject;
	String subjectCode;
	String domain = "api";
	String owner = "unassigned";
	String lastUpdated;
	String unitType = "endpoint";
	boolean planned;
	List<String> units = new ArrayList<>();
	List<String> stories = new ArrayList<>();
	List<String> categories = new ArrayList<>();
	int[] declaredSummary; // total, automated, not_automated, not_automatable — null if absent
	String gridHeader = GRID_HEADER;
	String gridSeparator = GRID_SEPARATOR;
	final List<Row> rows = new ArrayList<>();
	String endpointDetails = ENDPOINT_PLACEHOLDER;

	public static final class MalformedException extends Exception {
		private static final long serialVersionUID = 1L;

		MalformedException(String msg) {
			super(msg);
		}
	}

	static MatrixFile create(String subject, String subjectCode, List<String> units, List<String> categories,
			boolean planned) {
		MatrixFile mf = new MatrixFile();
		mf.subject = subject;
		mf.subjectCode = subjectCode;
		mf.units = new ArrayList<>(units);
		mf.categories = new ArrayList<>(categories);
		mf.planned = planned;
		return mf;
	}

	// ---------------------------------------------------------------- parse

	@SuppressWarnings("unchecked")
	static MatrixFile parse(String text) throws MalformedException {
		text = text.replace("\r\n", "\n");
		MatrixFile mf = new MatrixFile();

		int fmStart = text.indexOf("```yaml\n");
		if (fmStart < 0)
			throw new MalformedException("no ```yaml front-matter block");
		int fmEnd = text.indexOf("\n```", fmStart + 8);
		if (fmEnd < 0)
			throw new MalformedException("unterminated ```yaml front-matter block");
		Object fm;
		try {
			fm = new Yaml().load(text.substring(fmStart + 8, fmEnd + 1));
		} catch (RuntimeException e) {
			throw new MalformedException("front-matter is not valid YAML: " + e.getMessage().split("\n")[0]);
		}
		if (!(fm instanceof Map))
			throw new MalformedException("front-matter is not a mapping");
		Map<String, Object> m = (Map<String, Object>) fm;
		mf.subject = str(m.get("subject"));
		if (mf.subject == null)
			throw new MalformedException("front-matter has no 'subject'");
		mf.subjectCode = str(m.get("subject_code"));
		mf.domain = orDefault(str(m.get("domain")), "api");
		mf.owner = orDefault(str(m.get("owner")), "unassigned");
		mf.lastUpdated = date(m.get("last_updated"));
		mf.unitType = orDefault(str(m.get("unit_type")), "endpoint");
		mf.planned = Boolean.TRUE.equals(m.get("planned"));
		mf.units = strList(m.get("units"));
		mf.stories = strList(m.get("stories"));
		mf.categories = strList(m.get("categories"));
		if (m.get("summary") instanceof Map) {
			Map<String, Object> s = (Map<String, Object>) m.get("summary");
			mf.declaredSummary = new int[] { num(s.get("total")), num(s.get("automated")),
					num(s.get("not_automated")), num(s.get("not_automatable")) };
		}

		String[] lines = text.split("\n", -1);
		int gs = indexOfLine(lines, GRID_START, 0);
		int ge = gs < 0 ? -1 : indexOfLine(lines, GRID_END, gs + 1);
		if (gs < 0 || ge < 0)
			throw new MalformedException("missing " + GRID_START + " / " + GRID_END + " markers");
		if (ge - gs < 3)
			throw new MalformedException("grid has no header/separator row");
		mf.gridHeader = lines[gs + 1];
		mf.gridSeparator = lines[gs + 2];
		for (int i = gs + 3; i < ge; i++) {
			if (lines[i].trim().isEmpty())
				continue;
			mf.rows.add(new Row(lines[i], i + 1));
		}

		int es = text.indexOf(EP_START + "\n");
		int ee = es < 0 ? -1 : text.indexOf(EP_END, es);
		if (es >= 0 && ee >= 0)
			mf.endpointDetails = text.substring(es + EP_START.length() + 1, ee);
		return mf;
	}

	// ---------------------------------------------------------------- merge

	/**
	 * Adds a stub row for every (unit, category) pair with no row yet. Never touches an existing
	 * row. Returns the number of stubs added.
	 */
	int addMissingStubs() {
		int added = 0;
		int next = maxStubSequence() + 1;
		for (String unit : units) {
			for (String category : categories) {
				if (hasRow(unit, category))
					continue;
				String id = String.format(Locale.ROOT, "API-%s-%03d", subjectCode, next++);
				String scenario = "(TODO) Given ..., when `" + unit + "` is called — write the `" + category
						+ "` scenario";
				rows.add(new Row("| " + id + " | " + scenario + " | TODO | " + category + " | integration | "
						+ NOT_AUTOMATED + " |  |  |", 0));
				added++;
			}
		}
		return added;
	}

	/**
	 * Single-unit subjects match by category alone (any wording is fine). Multi-unit subjects also
	 * need the unit's exact label somewhere in the Scenario — the only way to tell rows apart.
	 */
	boolean hasRow(String unit, String category) {
		for (Row r : rows) {
			if (!category.equals(r.type()))
				continue;
			if (units.size() <= 1 || r.scenario().contains(unit))
				return true;
		}
		return false;
	}

	private int maxStubSequence() {
		int max = 0;
		for (Row r : rows) {
			Matcher m = STUB_ID.matcher(r.id());
			if (m.matches() && m.group(1).equals(subjectCode))
				max = Math.max(max, Integer.parseInt(m.group(2)));
		}
		return max;
	}

	/**
	 * Rewrites each resolvable Test reference into a link to that case's line in the YAML. Rows
	 * whose derived cell is unchanged keep their raw line byte-for-byte.
	 *
	 * @param matrixDir   directory the matrix file lives in
	 * @param resolver    returns the absolute YAML path and line for a ref, or null if unresolvable
	 */
	void linkifyTestRefs(Path matrixDir, RefResolver resolver) {
		for (Row r : rows) {
			if (!r.wellFormed() || r.test().isEmpty())
				continue;
			List<String> refs = r.testRefs();
			if (refs.isEmpty())
				continue;
			// Only a cell made of nothing but refs/links is rewritten — never lose hand-written text.
			String rest = r.test().replaceAll("\\[[^\\]]*\\]\\([^)]*\\)", "");
			for (String ref : refs)
				rest = rest.replace(ref, "");
			if (!rest.replace("<br>", "").replaceAll("[\\s,;]", "").isEmpty())
				continue;
			List<String> parts = new ArrayList<>();
			for (String ref : refs) {
				RefResolver.Target t = resolver.resolve(ref);
				String link = null;
				if (t != null) {
					try {
						link = matrixDir.toAbsolutePath().normalize().relativize(t.file).toString().replace('\\', '/')
								+ "#L" + t.line;
					} catch (IllegalArgumentException differentRoot) {
						link = null; // leave plain text rather than emit a machine-specific path
					}
				}
				parts.add(link == null ? ref : "[" + ref + "](" + link + ")");
			}
			String cell = String.join("<br>", parts);
			if (cell.equals(r.test()))
				continue;
			r.cells.set(6, cell);
			r.raw = "| " + String.join(" | ", r.cells) + " |";
		}
	}

	interface RefResolver {
		final class Target {
			final Path file;
			final int line;

			Target(Path file, int line) {
				this.file = file;
				this.line = line;
			}
		}

		Target resolve(String ref);
	}

	// ---------------------------------------------------------------- counts

	/** total, automated, not_automated, not_automatable — counted from the grid. */
	int[] countStatuses() {
		int a = 0, n = 0, x = 0;
		for (Row r : rows) {
			switch (r.status()) {
			case AUTOMATED:
				a++;
				break;
			case NOT_AUTOMATED:
				n++;
				break;
			case NOT_AUTOMATABLE:
				x++;
				break;
			default:
				break;
			}
		}
		return new int[] { rows.size(), a, n, x };
	}

	static String percent(int automated, int notAutomated) {
		int d = automated + notAutomated;
		return d == 0 ? "n/a" : String.format(Locale.ROOT, "%.1f%%", 100.0 * automated / d);
	}

	// ---------------------------------------------------------------- render

	String render() {
		int[] c = countStatuses();
		StringBuilder b = new StringBuilder();
		b.append("# ").append(subject).append(" — Coverage Matrix\n\n");
		b.append("| Field | Value |\n|---|---|\n");
		b.append("| Subject code | `").append(subjectCode).append("` |\n");
		b.append("| Domain | `").append(domain).append("` |\n");
		b.append("| Owner | ").append(owner).append(" |\n");
		b.append("| Last updated | ").append(lastUpdated).append(" |\n");
		b.append("| Unit type | `").append(unitType).append("` |\n");
		if (planned)
			b.append("| Planned | yes — no wired YAML targets this endpoint yet |\n");
		if (!stories.isEmpty())
			b.append("| Stories | ").append(String.join(", ", stories)).append(" |\n");
		b.append("\n**Units**\n\n");
		for (String u : units)
			b.append("- `").append(u).append("`\n");
		b.append("\n**Coverage summary**\n\n");
		b.append("| Total | ✅ Automated | 🟡 Not automated | ⛔ Not automatable | Coverage |\n|---|---|---|---|---|\n");
		b.append("| ").append(c[0]).append(" | ").append(c[1]).append(" | ").append(c[2]).append(" | ").append(c[3])
				.append(" | ").append(percent(c[1], c[2])).append(" |\n\n");
		b.append("**Categories:**");
		for (String cat : categories)
			b.append(" `").append(cat).append('`');
		b.append("\n\n<details>\n<summary>Raw metadata (machine-generated by <code>sync</code> — do not hand-edit)</summary>\n\n");
		b.append("```yaml\n");
		b.append("subject: ").append(yamlScalar(subject)).append('\n');
		b.append("subject_code: ").append(yamlScalar(subjectCode)).append('\n');
		b.append("domain: ").append(yamlScalar(domain)).append('\n');
		b.append("owner: ").append(yamlScalar(owner)).append('\n');
		b.append("last_updated: \"").append(lastUpdated).append("\"\n");
		b.append("unit_type: ").append(yamlScalar(unitType)).append('\n');
		if (planned)
			b.append("planned: true\n");
		b.append("units:\n");
		for (String u : units)
			b.append("  - ").append(yamlScalar(u)).append('\n');
		b.append("summary:\n");
		b.append("  total: ").append(c[0]).append('\n');
		b.append("  automated: ").append(c[1]).append('\n');
		b.append("  not_automated: ").append(c[2]).append('\n');
		b.append("  not_automatable: ").append(c[3]).append('\n');
		b.append("categories:\n");
		for (String cat : categories)
			b.append("  - ").append(yamlScalar(cat)).append('\n');
		if (!stories.isEmpty()) {
			b.append("stories:\n");
			for (String s : stories)
				b.append("  - ").append(yamlScalar(s)).append('\n');
		}
		b.append("```\n\n</details>\n\n");
		b.append(GRID_START).append('\n').append(gridHeader).append('\n').append(gridSeparator).append('\n');
		for (Row r : rows)
			b.append(r.raw).append('\n');
		b.append(GRID_END).append("\n\n");
		b.append("**About this endpoint**\n\n");
		b.append(EP_START).append('\n').append(endpointDetails).append(EP_END).append('\n');
		return b.toString();
	}

	/** Plain when unambiguous in YAML, double-quoted otherwise (braces, '?', '&', ':', ...). */
	static String yamlScalar(String s) {
		if (s == null)
			return "\"\"";
		if (!s.isEmpty() && s.matches("[A-Za-z0-9_][A-Za-z0-9_ ./\\-]*") && !s.endsWith(" ")
				&& !s.matches("(?i)true|false|yes|no|null|~|on|off|[0-9.\\-]+"))
			return s;
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	// ---------------------------------------------------------------- helpers

	/** Splits a pipe-table row on unescaped pipes, dropping the empty outer cells. */
	static List<String> splitCells(String raw) {
		List<String> cells = new ArrayList<>();
		String line = raw.trim();
		if (!line.startsWith("|"))
			return cells;
		StringBuilder cur = new StringBuilder();
		for (int i = 1; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\\' && i + 1 < line.length() && line.charAt(i + 1) == '|') {
				cur.append("\\|");
				i++;
			} else if (ch == '|') {
				cells.add(cur.toString().trim());
				cur.setLength(0);
			} else {
				cur.append(ch);
			}
		}
		if (cur.toString().trim().length() > 0)
			cells.add(cur.toString().trim()); // row without a closing pipe
		return cells;
	}

	private static int indexOfLine(String[] lines, String marker, int from) {
		for (int i = from; i < lines.length; i++)
			if (lines[i].trim().equals(marker))
				return i;
		return -1;
	}

	private static String str(Object o) {
		return o == null ? null : String.valueOf(o);
	}

	private static String orDefault(String s, String d) {
		return s == null || s.isBlank() ? d : s;
	}

	private static int num(Object o) {
		try {
			return o == null ? -1 : Integer.parseInt(String.valueOf(o).trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static String date(Object o) {
		if (o instanceof Date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
			f.setTimeZone(TimeZone.getTimeZone("UTC"));
			return f.format((Date) o);
		}
		return str(o);
	}

	private static List<String> strList(Object o) {
		List<String> out = new ArrayList<>();
		if (o instanceof List)
			for (Object e : (List<?>) o)
				out.add(String.valueOf(e));
		return out;
	}
}
