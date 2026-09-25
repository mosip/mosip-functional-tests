package io.mosip.testrig.apirig.coverage;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.mosip.testrig.apirig.coverage.ControllerScanner.Endpoint;
import io.mosip.testrig.apirig.coverage.CoverageCheck.Gap;
import io.mosip.testrig.apirig.coverage.Inventory.Subject;
import io.mosip.testrig.apirig.coverage.MatrixFile.Row;

/**
 * Coverage-matrix CLI. Dev-time and CI tool — never wired into a Suite.xml.
 *
 * <pre>
 * scaffold  create a matrix file for every wired subject without one, and a planned file for
 *           every real endpoint with no YAML (unless baselined). Never overwrites.
 * sync      parse → merge → render every matrix file. Idempotent; never rewrites a row's text.
 * rollup    write Summary.md from the matrix files.
 * check     read-only gap report; exits 1 on any gap not accepted in the baseline.
 *
 * --module-root &lt;dir&gt;      the module's api-test directory (required)
 * --module-code &lt;CODE&gt;     subject-code prefix, e.g. PREREG (required)
 * --out &lt;dir&gt;              matrix directory (default &lt;module-root&gt;/src/main/resources/coverage)
 * --app-source &lt;dir&gt;       real service source; repeatable. Default: auto-discover every
 *                          src/main/java beside the module root (the service lives in the same repo)
 * --no-app-source          skip controller scanning (endpoint gaps off)
 * --require-app-source     make "no controller source found" a gap (use in CI)
 * --path-prefix &lt;prefix&gt;   gateway prefix, e.g. /preregistration/v1 (default: inferred per service)
 * --baseline &lt;file&gt;        accepted gaps (default &lt;out&gt;/check-baseline.txt)
 * --date &lt;yyyy-MM-dd&gt;      date stamped on changed files (default: today)
 * --no-planned             scaffold: don't create planned files for untested endpoints
 * --keys                   check: print each gap's exact baseline line
 * </pre>
 */
public final class CoverageCli {

	private final PrintStream out;

	private CoverageCli(PrintStream out) {
		this.out = out;
	}

	public static void main(String[] args) {
		int code;
		try {
			// UTF-8 regardless of the JVM default (Maven on Windows defaults stdout to cp1252).
			PrintStream utf8 = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
			code = new CoverageCli(utf8).run(args);
		} catch (IllegalArgumentException e) {
			System.err.println("error: " + e.getMessage());
			code = 2;
		} catch (IOException e) {
			System.err.println("error: " + e);
			code = 2;
		}
		System.exit(code);
	}

	int run(String[] args) throws IOException {
		if (args.length == 0)
			throw new IllegalArgumentException("usage: <scaffold|sync|rollup|check> --module-root <dir> --module-code <CODE> [options]");
		String command = args[0];
		Map<String, List<String>> opts = parseOptions(args);
		Path moduleRoot = Path.of(required(opts, "--module-root")).toAbsolutePath().normalize();
		String moduleCode = required(opts, "--module-code").toUpperCase(Locale.ROOT);
		Path outDir = opts.containsKey("--out") ? Path.of(last(opts, "--out")).toAbsolutePath().normalize()
				: moduleRoot.resolve("src/main/resources/coverage");
		String date = opts.containsKey("--date") ? last(opts, "--date") : LocalDate.now().toString();

		Estate estate = new Estate(moduleRoot, outDir, Inventory.load(moduleRoot, moduleCode));
		estate.loadMatrixFiles();
		Path baselineFile = opts.containsKey("--baseline") ? Path.of(last(opts, "--baseline"))
				: outDir.resolve("check-baseline.txt");
		estate.baseline = CoverageCheck.Baseline.load(baselineFile,
				baselineFile.getFileName() == null ? baselineFile.toString() : baselineFile.getFileName().toString());
		estate.requireAppSource = opts.containsKey("--require-app-source");
		if (!opts.containsKey("--no-app-source")) {
			List<Path> roots = new ArrayList<>();
			for (String r : opts.getOrDefault("--app-source", List.of()))
				roots.add(Path.of(unMsys(r)));
			String prefix = opts.containsKey("--path-prefix") ? unMsys(last(opts, "--path-prefix")) : null;
			estate.loadControllers(roots, true, prefix);
			describeAppSource(estate);
		}

		switch (command) {
		case "scaffold":
			return scaffold(estate, date, !opts.containsKey("--no-planned"));
		case "sync":
			return sync(estate, date);
		case "rollup":
			return rollup(estate);
		case "check":
			return check(estate, opts.containsKey("--keys"));
		default:
			throw new IllegalArgumentException("unknown command: " + command);
		}
	}

	// ---------------------------------------------------------------- scaffold

	private int scaffold(Estate estate, String date, boolean planned) throws IOException {
		int created = 0;
		for (Subject s : estate.inventory.subjects) {
			String rel = estate.matrixRelPath(s.name);
			if (estate.files.containsKey(rel))
				continue;
			MatrixFile mf = MatrixFile.create(s.name, s.code, s.units,
					CategoryChecklist.categoriesFor(s.multilang, s.dependencyState, null), false);
			created += write(estate, rel, mf, date);
		}
		if (planned && estate.controllers != null) {
			List<Gap> gaps = CoverageCheck.run(estate);
			for (Endpoint e : estate.controllers.endpoints) {
				String label = estate.endpointMap.externalLabel(e);
				boolean open = false;
				for (Gap g : gaps)
					if ("unmapped-endpoint".equals(g.type) && g.key.equals(label) && !estate.baseline.accepts(g))
						open = true;
				if (!open)
					continue;
				String slug = plannedSlug(e, label);
				String rel = Estate.PLANNED_DIR + "/" + slug + ".md";
				if (estate.files.containsKey(rel))
					continue;
				MatrixFile mf = MatrixFile.create(Estate.PLANNED_DIR + "/" + slug, plannedCode(estate, e),
						List.of(label), CategoryChecklist.categoriesFor(false, false, null), true);
				created += write(estate, rel, mf, date);
			}
		}
		out.println("scaffold: created " + created + " file(s)");
		return 0;
	}

	private int write(Estate estate, String rel, MatrixFile mf, String date) throws IOException {
		mf.lastUpdated = date;
		mf.addMissingStubs();
		Path p = estate.out.resolve(rel);
		Files.createDirectories(p.getParent());
		Files.writeString(p, mf.render(), StandardCharsets.UTF_8);
		out.println("  created " + rel);
		Estate.Loaded lf = new Estate.Loaded(rel, p);
		lf.text = mf.render();
		lf.matrix = mf;
		estate.files.put(rel, lf);
		return 1;
	}

	/** Short, readable subject code: {@code MODULE-PLANNED-<VERB><last two path words>}, de-duplicated. */
	private static String plannedCode(Estate estate, Endpoint e) {
		List<String> words = new ArrayList<>();
		for (String seg : e.path.split("/"))
			if (!seg.isEmpty() && !seg.startsWith("{") && !seg.equals("**"))
				words.add(seg);
		String tail = words.isEmpty() ? "ROOT" : String.join("", words.subList(Math.max(0, words.size() - 2), words.size()));
		String base = estate.inventory.moduleCode + "-PLANNED-"
				+ (e.verb + tail).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
		Set<String> taken = new LinkedHashSet<>();
		for (Estate.Loaded lf : estate.files.values())
			if (lf.matrix != null)
				taken.add(lf.matrix.subjectCode);
		String code = base;
		for (int n = 2; taken.contains(code); n++)
			code = base + n;
		return code;
	}

	static String plannedSlug(Endpoint e, String label) {
		String path = e.path.replaceAll("\\{([^}]*)\\}", "$1").replace("**", "all");
		String slug = (e.verb + "_" + path).replaceAll("[^A-Za-z0-9]+", "_").replaceAll("_+$", "");
		return slug.isEmpty() ? e.verb : slug;
	}

	// ---------------------------------------------------------------- sync

	private int sync(Estate estate, String date) throws IOException {
		int written = 0, unchanged = 0, stubs = 0;
		for (Estate.Loaded lf : estate.files.values()) {
			if (lf.error != null) {
				out.println("  skipped " + lf.relPath + " (malformed: " + lf.error + ")");
				continue;
			}
			Subject s = estate.inventory.subject(lf.matrix.subject);
			if (s == null && !lf.matrix.planned) {
				out.println("  skipped " + lf.relPath + " (subject no longer wired — see check: stale-matrix-file)");
				continue;
			}
			MatrixFile mf = lf.matrix; // already parsed from lf.text in loadMatrixFiles
			Estate.apply(mf, s);
			stubs += mf.addMissingStubs();
			mf.linkifyTestRefs(lf.path.getParent(), estate::resolve);
			String rendered = mf.render();
			if (!rendered.equals(lf.text)) {
				mf.lastUpdated = date;
				rendered = mf.render();
			}
			if (rendered.equals(lf.text)) {
				unchanged++;
				continue;
			}
			Files.writeString(lf.path, rendered, StandardCharsets.UTF_8);
			lf.text = rendered;
			lf.matrix = mf;
			written++;
			out.println("  wrote " + lf.relPath);
		}
		out.println("sync: written " + written + ", unchanged " + unchanged + ", stub rows added " + stubs);
		return 0;
	}

	// ---------------------------------------------------------------- rollup

	private int rollup(Estate estate) throws IOException {
		List<MatrixFile> mfs = new ArrayList<>();
		for (Estate.Loaded lf : estate.files.values())
			if (lf.matrix != null)
				mfs.add(lf.matrix);
		mfs.sort(Comparator.comparing(m -> m.subjectCode == null ? "" : m.subjectCode));

		String asOf = "";
		int[] total = new int[4];
		Map<String, int[]> byCategory = new LinkedHashMap<>();
		Set<String> categories = new LinkedHashSet<>();
		for (MatrixFile m : mfs) {
			if (m.lastUpdated != null && m.lastUpdated.compareTo(asOf) > 0)
				asOf = m.lastUpdated;
			int[] c = m.countStatuses();
			for (int i = 0; i < 4; i++)
				total[i] += c[i];
			for (Row r : m.rows) {
				categories.add(r.type());
				int[] bc = byCategory.computeIfAbsent(r.type(), k -> new int[3]);
				switch (r.status()) {
				case AUTOMATED:
					bc[0]++;
					break;
				case NOT_AUTOMATED:
					bc[1]++;
					break;
				case NOT_AUTOMATABLE:
					bc[2]++;
					break;
				default:
					break;
				}
			}
		}

		StringBuilder b = new StringBuilder();
		b.append("# Coverage Summary\n\n");
		b.append("As of: ").append(asOf).append("\n\n");
		b.append("## Estate totals\n\n");
		b.append("| Total | ✅ Automated | 🟡 Not automated | ⛔ Not automatable | Coverage |\n|---|---|---|---|---|\n");
		b.append("| ").append(total[0]).append(" | ").append(total[1]).append(" | ").append(total[2]).append(" | ")
				.append(total[3]).append(" | ").append(MatrixFile.percent(total[1], total[2])).append(" |\n\n");
		b.append("## By subject\n\n");
		b.append("| Subject | Automated | Total scenarios | Coverage |\n|---|---|---|---|\n");
		for (MatrixFile m : mfs) {
			int[] c = m.countStatuses();
			b.append("| `").append(m.subjectCode).append("` | ").append(c[1]).append('/').append(c[1] + c[2])
					.append(" | ").append(c[0]).append(" | ").append(MatrixFile.percent(c[1], c[2])).append(" |\n");
		}
		b.append("\n## By category\n\n");
		b.append("| Category | ✅ Automated | 🟡 Not automated | ⛔ Not automatable | Coverage |\n|---|---|---|---|---|\n");
		List<String> cats = new ArrayList<>(categories);
		cats.sort(Comparator.comparingInt(CategoryChecklist::order));
		for (String cat : cats) {
			int[] bc = byCategory.get(cat);
			b.append("| `").append(cat).append("` | ").append(bc[0]).append(" | ").append(bc[1]).append(" | ")
					.append(bc[2]).append(" | ").append(MatrixFile.percent(bc[0], bc[1])).append(" |\n");
		}
		b.append("\n## Flagged findings\n\n");
		b.append("Rows whose Notes are flagged ⚠️ (fragility/quality concern) or 🛑 (confirmed bug) during the cycle-2 "
				+ "semantic audit — see COVERAGE_MATRIX_HANDOFF.md gotcha 15 for the practice behind this. Pulled "
				+ "mechanically from row Notes, not hand-maintained here.\n\n");
		for (MatrixFile m : mfs)
			for (Row r : m.rows) {
				String notes = r.notes();
				if (notes.contains("🛑") || notes.contains("⚠️"))
					b.append("- ").append(notes.contains("🛑") ? "🛑" : "⚠️").append(" **").append(m.subject)
							.append("** (`").append(r.id()).append("`): ").append(notes).append('\n');
			}
		b.append('\n');

		Path summary = estate.out.resolve("Summary.md");
		String rendered = b.toString();
		if (Files.isRegularFile(summary) && Files.readString(summary, StandardCharsets.UTF_8).equals(rendered)) {
			out.println("rollup: Summary.md unchanged");
		} else {
			Files.createDirectories(estate.out);
			Files.writeString(summary, rendered, StandardCharsets.UTF_8);
			out.println("rollup: wrote Summary.md (" + mfs.size() + " subjects)");
		}
		return 0;
	}

	// ---------------------------------------------------------------- check

	private int check(Estate estate, boolean keys) {
		List<Gap> gaps = CoverageCheck.run(estate);
		Map<String, List<Gap>> open = new LinkedHashMap<>();
		int accepted = 0;
		for (Gap g : gaps) {
			if (estate.baseline.accepts(g))
				accepted++;
			else
				open.computeIfAbsent(g.type, t -> new ArrayList<>()).add(g);
		}
		int n = open.values().stream().mapToInt(List::size).sum();
		for (Map.Entry<String, List<Gap>> e : open.entrySet()) {
			out.println(e.getKey() + " (" + e.getValue().size() + ")");
			for (Gap g : e.getValue()) {
				out.println("  " + g.message);
				if (keys && !g.type.startsWith("baseline-") && !g.type.startsWith("stale-baseline"))
					out.println("      baseline: " + g.baselineKey() + "  # <reason>");
			}
		}
		out.println("check: " + n + " gap(s)" + (accepted > 0 ? ", " + accepted + " accepted by baseline" : "")
				+ " — " + estate.inventory.subjects.size() + " wired subjects, " + estate.files.size() + " matrix files"
				+ (estate.controllers == null ? ", endpoint checks OFF" : ", " + estate.controllers.endpoints.size() + " controller endpoints"));
		if (n > 0)
			out.println("To accept a gap on purpose, add `<type> <key>  # reason` to " + estate.baseline.relPath
					+ " (run `check --keys` to print each gap's exact baseline line).");
		return n == 0 ? 0 : 1;
	}

	private void describeAppSource(Estate estate) {
		if (estate.controllers == null) {
			out.println("app-source: none found — endpoint gaps (unmapped/unreachable) are OFF");
			return;
		}
		Set<String> lines = new LinkedHashSet<>();
		estate.endpointMap.prefixes().forEach((svc, prefix) -> lines
				.add("  " + Inventory.rel(estate.moduleRoot.getParent(), svc) + "  prefix " + (prefix.isEmpty() ? "(none)" : prefix)
						+ (estate.endpointMap.isFallback(svc)
								? "  (fallback — no YAML targets this service; pass --path-prefix if wrong)"
								: "")));
		out.println("app-source: " + estate.controllers.endpoints.size() + " endpoints from " + lines.size() + " service(s)");
		lines.forEach(out::println);
	}

	// ---------------------------------------------------------------- args

	private static Map<String, List<String>> parseOptions(String[] args) {
		Set<String> flags = Set.of("--no-app-source", "--require-app-source", "--no-planned", "--keys");
		Map<String, List<String>> opts = new LinkedHashMap<>();
		for (int i = 1; i < args.length; i++) {
			String a = args[i];
			if (!a.startsWith("--"))
				throw new IllegalArgumentException("unexpected argument: " + a);
			if (flags.contains(a)) {
				opts.put(a, List.of());
				continue;
			}
			if (i + 1 >= args.length)
				throw new IllegalArgumentException(a + " needs a value");
			opts.computeIfAbsent(a, k -> new ArrayList<>()).add(args[++i]);
		}
		return opts;
	}

	private static String required(Map<String, List<String>> opts, String name) {
		if (!opts.containsKey(name))
			throw new IllegalArgumentException(name + " is required");
		return last(opts, name);
	}

	private static String last(Map<String, List<String>> opts, String name) {
		List<String> v = opts.get(name);
		return v.get(v.size() - 1);
	}

	/**
	 * Git Bash/MSYS rewrites a bare "/x" argument into "C:/Program Files/Git/x" before Java sees it.
	 * Accept the standard workaround "//x", and undo the rewrite if it already happened.
	 */
	static String unMsys(String arg) {
		if (arg.startsWith("//"))
			return arg.substring(1);
		String a = arg.replace('\\', '/');
		int git = a.toLowerCase(Locale.ROOT).indexOf("/program files/git/");
		if (git >= 0 && a.matches("(?i)^[a-z]:/.*"))
			return a.substring(git + "/program files/git".length());
		return arg;
	}
}
