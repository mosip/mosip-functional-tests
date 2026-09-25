package io.mosip.testrig.apirig.coverage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

/**
 * Mechanical inventory of a module's api-test side: which YAML files the Suite.xml actually runs
 * (subjects), their units, their test cases, and every YAML on disk.
 *
 * <p>Built only from {@code testNgXmlFiles/*.xml} and {@code src/main/resources/**}/*.yml — it never
 * reads the matrix files. Works for any module laid out the standard way.
 */
public final class Inventory {

	public static final class TestCase {
		public final String name;
		public final String uniqueIdentifier; // null when the case has none
		public final String endPoint;
		public final String restMethod;
		public final int line; // 1-based line of "uniqueIdentifier: ..." (or of the case key)
		final boolean templateFields;
		final boolean dependencyToken;

		TestCase(String name, String uniqueIdentifier, String endPoint, String restMethod, int line,
				boolean templateFields, boolean dependencyToken) {
			this.name = name;
			this.uniqueIdentifier = uniqueIdentifier;
			this.endPoint = endPoint;
			this.restMethod = restMethod;
			this.line = line;
			this.templateFields = templateFields;
			this.dependencyToken = dependencyToken;
		}
	}

	public static final class YamlFile {
		/** Path relative to src/main/resources, forward slashes (e.g. preReg/X/X.yml). */
		public final String relPath;
		public final List<TestCase> cases = new ArrayList<>();
		public String parseError;

		YamlFile(String relPath) {
			this.relPath = relPath;
		}

		public TestCase byUniqueIdentifier(String uid) {
			for (TestCase tc : cases)
				if (uid.equals(tc.uniqueIdentifier))
					return tc;
			return null;
		}
	}

	public static final class Subject {
		/** e.g. preReg/AddLostUinApplication — also the matrix file path under --out, minus ".md". */
		public final String name;
		public final String code;
		public final YamlFile yaml;
		/** Simple name of the wired test-script class; null if the suite entry had none. */
		public final String scriptClass;
		/** Distinct "VERB endPoint" labels in first-appearance order, verb taken from restMethod. */
		public final List<String> units = new ArrayList<>();
		public boolean multilang;
		public boolean dependencyState;

		Subject(String name, String code, YamlFile yaml, String scriptClass) {
			this.name = name;
			this.code = code;
			this.yaml = yaml;
			this.scriptClass = scriptClass;
		}

		/** The HTTP verb the wired class really sends, or null when its name doesn't say. */
		public String classVerb() {
			return verbFromClassName(scriptClass);
		}
	}

	private static final Pattern UID_LINE = Pattern.compile("^\\s*uniqueIdentifier\\s*:\\s*(\\S+)\\s*$");
	private static final Set<String> VERBS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

	public final Path moduleRoot;
	public final Path resourcesRoot;
	public final String moduleCode;
	/** Wired subjects, in suite order. */
	public final List<Subject> subjects = new ArrayList<>();
	/** Every YAML on disk that holds at least one test case, keyed by relPath. */
	public final Map<String, YamlFile> yamlFiles = new LinkedHashMap<>();
	/** Suite/YAML problems that stop a file being read (reported as malformed-file). */
	public final List<String> problems = new ArrayList<>();

	private Inventory(Path moduleRoot, String moduleCode) {
		this.moduleRoot = moduleRoot;
		this.resourcesRoot = moduleRoot.resolve("src/main/resources");
		this.moduleCode = moduleCode;
	}

	public static Inventory load(Path moduleRoot, String moduleCode) throws IOException {
		Inventory inv = new Inventory(moduleRoot.toAbsolutePath().normalize(), moduleCode);
		inv.loadYamlFiles();
		inv.loadSuites();
		return inv;
	}

	public List<String> unwiredYamlFiles() {
		Set<String> wired = subjects.stream().map(s -> s.yaml.relPath).collect(Collectors.toSet());
		return yamlFiles.keySet().stream().filter(p -> !wired.contains(p)).sorted().collect(Collectors.toList());
	}

	public Subject subject(String name) {
		for (Subject s : subjects)
			if (s.name.equals(name))
				return s;
		return null;
	}

	// ---------------------------------------------------------------- YAML

	private void loadYamlFiles() throws IOException {
		if (!Files.isDirectory(resourcesRoot))
			throw new IOException("Not a module root (no src/main/resources): " + moduleRoot);
		List<Path> files;
		try (Stream<Path> s = Files.walk(resourcesRoot)) {
			files = s.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
					.filter(Files::isRegularFile).sorted().collect(Collectors.toList());
		}
		for (Path f : files) {
			String text = Files.readString(f, StandardCharsets.UTF_8);
			if (!text.contains("uniqueIdentifier") && !text.contains("endPoint") && !text.isBlank()
					&& !hasTemplateSibling(f))
				continue; // config/other YAML — not a test-case file. An emptied test YAML is kept (empty-yml gap).
			String rel = rel(resourcesRoot, f);
			YamlFile yf = new YamlFile(rel);
			parseYaml(yf, text);
			yamlFiles.put(rel, yf);
		}
	}

	private static boolean hasTemplateSibling(Path yml) throws IOException {
		try (Stream<Path> s = Files.list(yml.getParent())) {
			return s.anyMatch(p -> p.toString().endsWith(".hbs"));
		}
	}

	@SuppressWarnings("unchecked")
	private void parseYaml(YamlFile yf, String text) {
		if (text.isBlank())
			return; // no cases; reported as empty-yml
		Object loaded;
		try {
			loaded = new Yaml().load(text);
		} catch (RuntimeException e) {
			yf.parseError = e.getMessage() == null ? e.toString() : e.getMessage().split("\n")[0];
			return;
		}
		if (!(loaded instanceof Map)) {
			yf.parseError = "top level is not a mapping";
			return;
		}
		String[] lines = text.split("\r?\n", -1);
		for (Object root : ((Map<String, Object>) loaded).values()) {
			if (!(root instanceof Map))
				continue;
			for (Map.Entry<String, Object> e : ((Map<String, Object>) root).entrySet()) {
				if (!(e.getValue() instanceof Map))
					continue;
				Map<String, Object> c = (Map<String, Object>) e.getValue();
				String uid = str(c.get("uniqueIdentifier"));
				String endPoint = str(c.get("endPoint"));
				String restMethod = str(c.get("restMethod"));
				boolean tf = c.get("templateFields") != null && !String.valueOf(c.get("templateFields")).isBlank()
						&& !"[]".equals(String.valueOf(c.get("templateFields")).trim());
				String payload = str(c.get("input")) + str(c.get("output")) + endPoint;
				int line = uid != null ? uidLine(lines, uid) : keyLine(lines, e.getKey());
				yf.cases.add(new TestCase(e.getKey(), uid, endPoint, restMethod, line, tf, payload.contains("$ID:")));
			}
		}
	}

	/** Exact trimmed-line match, so TC_1 can never resolve to TC_10's line. */
	private static int uidLine(String[] lines, String uid) {
		for (int i = 0; i < lines.length; i++) {
			Matcher m = UID_LINE.matcher(lines[i]);
			if (m.matches() && m.group(1).equals(uid))
				return i + 1;
		}
		return 0;
	}

	private static int keyLine(String[] lines, String key) {
		for (int i = 0; i < lines.length; i++)
			if (lines[i].trim().equals(key + ":"))
				return i + 1;
		return 0;
	}

	// ---------------------------------------------------------------- Suite.xml

	private void loadSuites() throws IOException {
		Path dir = moduleRoot.resolve("testNgXmlFiles");
		if (!Files.isDirectory(dir)) {
			problems.add("testNgXmlFiles/ not found under " + moduleRoot);
			return;
		}
		List<Path> suites;
		try (Stream<Path> s = Files.list(dir)) {
			suites = s.filter(p -> p.toString().endsWith(".xml"))
					.filter(p -> !p.getFileName().toString().toLowerCase(Locale.ROOT).contains("mastertestsuite"))
					.sorted().collect(Collectors.toList());
		}
		Map<String, String> wired = new LinkedHashMap<>(); // yml relPath -> script class, suite order
		for (Path suite : suites) {
			Document doc;
			try {
				doc = xmlBuilder().parse(suite.toFile());
			} catch (Exception e) {
				problems.add(rel(moduleRoot, suite) + ": " + e.getMessage());
				continue;
			}
			NodeList tests = doc.getElementsByTagName("test");
			for (int i = 0; i < tests.getLength(); i++) {
				Element test = (Element) tests.item(i);
				String yml = parameter(test, "ymlFile");
				if (yml == null || wired.containsKey(yml.trim()))
					continue;
				yml = yml.trim();
				if (!yamlFiles.containsKey(yml)) {
					problems.add(rel(moduleRoot, suite) + ": <test name=\"" + test.getAttribute("name")
							+ "\"> points at missing YAML " + yml);
					continue;
				}
				wired.put(yml, scriptClass(test));
			}
		}
		// A subject is named after its YAML's folder (Transliteration/Translate.yml -> .../Transliteration);
		// only when two wired YAMLs share a folder does the file name disambiguate.
		Map<String, Integer> perFolder = new HashMap<>();
		for (String yml : wired.keySet())
			perFolder.merge(folderOf(yml), 1, Integer::sum);
		wired.forEach((yml, cls) -> addSubject(yamlFiles.get(yml), cls, perFolder.get(folderOf(yml)) > 1));
	}

	private static String folderOf(String relPath) {
		return relPath.contains("/") ? relPath.substring(0, relPath.lastIndexOf('/')) : "";
	}

	private void addSubject(YamlFile yf, String scriptClass, boolean folderShared) {
		String rel = yf.relPath;
		String dir = folderOf(rel);
		String base = rel.substring(rel.lastIndexOf('/') + 1).replaceFirst("\\.ya?ml$", "");
		String dirLast = dir.contains("/") ? dir.substring(dir.lastIndexOf('/') + 1) : dir;
		boolean byFolder = !dir.isEmpty() && !folderShared;
		String name = byFolder ? dir : (dir.isEmpty() ? base : dir + "/" + base);
		String code = moduleCode + "-"
				+ (byFolder ? dirLast : base).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
		Subject s = new Subject(name, code, yf, scriptClass);
		for (TestCase tc : yf.cases) {
			String unit = (tc.restMethod == null ? "ANY" : tc.restMethod.toUpperCase(Locale.ROOT)) + " "
					+ (tc.endPoint == null ? "?" : tc.endPoint.trim());
			if (!s.units.contains(unit))
				s.units.add(unit);
			s.multilang |= tc.templateFields;
			s.dependencyState |= tc.dependencyToken;
		}
		subjects.add(s);
	}

	private static String parameter(Element test, String name) {
		NodeList params = test.getElementsByTagName("parameter");
		for (int i = 0; i < params.getLength(); i++) {
			Element p = (Element) params.item(i);
			if (name.equals(p.getAttribute("name")))
				return p.getAttribute("value");
		}
		return null;
	}

	private static String scriptClass(Element test) {
		NodeList classes = test.getElementsByTagName("class");
		if (classes.getLength() == 0)
			return null;
		String fqcn = ((Element) classes.item(0)).getAttribute("name");
		return fqcn.substring(fqcn.lastIndexOf('.') + 1);
	}

	private static DocumentBuilder xmlBuilder() throws Exception {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		f.setNamespaceAware(false);
		f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		f.setFeature("http://xml.org/sax/features/external-general-entities", false);
		f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		return f.newDocumentBuilder();
	}

	// ---------------------------------------------------------------- helpers

	/**
	 * The verb a dedicated script class hardcodes (SimplePost → POST, GetWithParamForAutoGenId → GET).
	 * Null when the name carries no verb or more than one (CreatePreReg, BookAppoinment, ...).
	 */
	static String verbFromClassName(String simpleName) {
		if (simpleName == null)
			return null;
		Set<String> found = new LinkedHashSet<>();
		for (String word : simpleName.split("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")) {
			String w = word.toUpperCase(Locale.ROOT);
			if (VERBS.contains(w))
				found.add(w);
		}
		return found.size() == 1 ? found.iterator().next() : null;
	}

	static String rel(Path base, Path p) {
		return base.relativize(p.toAbsolutePath().normalize()).toString().replace('\\', '/');
	}

	private static String str(Object o) {
		return o == null ? null : String.valueOf(o);
	}
}
