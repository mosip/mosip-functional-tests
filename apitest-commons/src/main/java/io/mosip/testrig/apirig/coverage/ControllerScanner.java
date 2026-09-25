package io.mosip.testrig.apirig.coverage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Finds every real HTTP endpoint in a service's Spring controller source.
 *
 * <p>A regex scan, not a Java parser: it blanks out comments (so commented-out mappings don't
 * count), combines the class-level {@code @RequestMapping} with each method-level
 * {@code @Get/Post/Put/Patch/Delete/RequestMapping}, and records paths given as string literals.
 * A mapping whose path is a constant ({@code path = ApiPaths.X}) can't be resolved statically and
 * is reported in {@link Result#unresolved} rather than guessed.
 */
public final class ControllerScanner {

	public static final class Endpoint {
		public final String verb; // GET/POST/... or ANY
		public final String path; // controller-relative, e.g. /applications/{preRegistrationId}
		/** Service directory owning the file (parent of src/main/java) — context paths are per service. */
		public final Path serviceRoot;
		public final Path file;
		public final int line;

		Endpoint(String verb, String path, Path serviceRoot, Path file, int line) {
			this.verb = verb;
			this.path = path;
			this.serviceRoot = serviceRoot;
			this.file = file;
			this.line = line;
		}

		public String label() {
			return verb + " " + path;
		}
	}

	public static final class Result {
		public final List<Endpoint> endpoints = new ArrayList<>();
		public final List<String> unresolved = new ArrayList<>();
	}

	private static final Pattern MAPPING = Pattern
			.compile("@(?:[\\w.]+\\.)?(Get|Post|Put|Patch|Delete|Request)Mapping\\b");
	private static final Pattern CLASS_DECL = Pattern.compile("\\b(?:class|interface)\\s+\\w+");
	private static final Pattern NAMED_PATH = Pattern.compile("\\b(?:value|path)\\s*=\\s*");
	private static final Pattern STRING = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"");
	private static final Pattern REQUEST_METHOD = Pattern.compile("RequestMethod\\.(\\w+)");

	private ControllerScanner() {
	}

	/** Every {@code src/main/java} directory under the given roots, skipping build output and tests. */
	public static List<Path> findSourceRoots(Path root, Path exclude) throws IOException {
		if (!Files.isDirectory(root))
			return List.of();
		Path ex = exclude == null ? null : exclude.toAbsolutePath().normalize();
		try (Stream<Path> s = Files.walk(root, 8)) {
			return s.filter(Files::isDirectory)
					.filter(p -> p.endsWith(Path.of("src", "main", "java")))
					.map(p -> p.toAbsolutePath().normalize())
					.filter(p -> ex == null || !p.startsWith(ex))
					.filter(p -> !p.toString().replace('\\', '/').matches(".*/(target|node_modules|\\.git)/.*"))
					.sorted().collect(Collectors.toList());
		}
	}

	public static Result scan(List<Path> sourceRoots) throws IOException {
		Result result = new Result();
		for (Path root : sourceRoots) {
			Path serviceRoot = root.getParent().getParent().getParent(); // <service>/src/main/java
			List<Path> files;
			try (Stream<Path> s = Files.walk(root)) {
				files = s.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
			}
			for (Path f : files) {
				String text = Files.readString(f, StandardCharsets.UTF_8);
				if (!text.contains("Controller"))
					continue;
				scanFile(blankComments(text), serviceRoot, f, result);
			}
		}
		return result;
	}

	private static void scanFile(String src, Path serviceRoot, Path file, Result result) {
		if (!src.contains("@RestController") && !src.contains("@Controller"))
			return;
		Matcher cd = CLASS_DECL.matcher(src);
		int classDecl = cd.find() ? cd.start() : 0;

		List<String> basePaths = new ArrayList<>(List.of(""));
		Matcher m = MAPPING.matcher(src);
		while (m.find()) {
			String kind = m.group(1);
			String args = parenArgs(src, m.end());
			int line = lineOf(src, m.start());
			List<String> paths = paths(args);
			if (paths == null) {
				result.unresolved.add(Inventory.rel(serviceRoot.getParent(), file) + ":" + line
						+ " — mapping path is not a string literal");
				continue;
			}
			if (m.start() < classDecl) {
				if ("Request".equals(kind) && !paths.isEmpty())
					basePaths = paths;
				continue;
			}
			if (paths.isEmpty())
				paths = List.of("");
			Set<String> verbs = new LinkedHashSet<>();
			if ("Request".equals(kind)) {
				Matcher rm = REQUEST_METHOD.matcher(args == null ? "" : args);
				while (rm.find())
					verbs.add(rm.group(1).toUpperCase(Locale.ROOT));
				if (verbs.isEmpty())
					verbs.add("ANY");
			} else {
				verbs.add(kind.toUpperCase(Locale.ROOT));
			}
			for (String base : basePaths)
				for (String p : paths)
					for (String v : verbs)
						result.endpoints.add(new Endpoint(v, join(base, p), serviceRoot, file, line));
		}
	}

	/**
	 * String-literal paths of a mapping's arguments; empty when the mapping has no path (inherits the
	 * class path); null when the path is given but isn't a literal.
	 */
	static List<String> paths(String args) {
		if (args == null || args.isBlank())
			return new ArrayList<>();
		String expr;
		Matcher named = NAMED_PATH.matcher(args);
		if (named.find()) {
			expr = expression(args, named.end());
		} else {
			String t = args.trim();
			if (!(t.startsWith("\"") || t.startsWith("{")))
				return t.matches("[\\w.]+") ? null : new ArrayList<>(); // positional constant vs. only other attrs
			expr = expression(t, 0);
		}
		List<String> out = new ArrayList<>();
		Matcher s = STRING.matcher(expr);
		while (s.find())
			out.add(s.group(1));
		if (out.isEmpty() && !expr.trim().isEmpty() && !expr.trim().equals("{}"))
			return null;
		return out;
	}

	/** One annotation-attribute expression starting at {@code from}: a {...} array or up to the next top-level comma. */
	private static String expression(String s, int from) {
		int i = from;
		while (i < s.length() && Character.isWhitespace(s.charAt(i)))
			i++;
		if (i < s.length() && s.charAt(i) == '{') {
			int depth = 0;
			for (int j = i; j < s.length(); j++) {
				char c = s.charAt(j);
				if (c == '"')
					j = skipString(s, j);
				else if (c == '{')
					depth++;
				else if (c == '}' && --depth == 0)
					return s.substring(i, j + 1);
			}
			return s.substring(i);
		}
		for (int j = i; j < s.length(); j++) {
			char c = s.charAt(j);
			if (c == '"')
				j = skipString(s, j);
			else if (c == ',')
				return s.substring(i, j);
		}
		return s.substring(i);
	}

	/** Text inside the balanced parentheses right after an annotation name, or null if none. */
	private static String parenArgs(String s, int from) {
		int i = from;
		while (i < s.length() && Character.isWhitespace(s.charAt(i)))
			i++;
		if (i >= s.length() || s.charAt(i) != '(')
			return null;
		int depth = 0;
		for (int j = i; j < s.length(); j++) {
			char c = s.charAt(j);
			if (c == '"')
				j = skipString(s, j);
			else if (c == '(')
				depth++;
			else if (c == ')' && --depth == 0)
				return s.substring(i + 1, j);
		}
		return null;
	}

	private static int skipString(String s, int quote) {
		for (int j = quote + 1; j < s.length(); j++) {
			if (s.charAt(j) == '\\')
				j++;
			else if (s.charAt(j) == '"')
				return j;
		}
		return s.length() - 1;
	}

	/** Replaces comment characters with spaces (keeping newlines, so line numbers stay right). */
	static String blankComments(String src) {
		StringBuilder b = new StringBuilder(src);
		int i = 0;
		while (i < b.length()) {
			char c = b.charAt(i);
			if (c == '"') {
				i = skipString(src, i) + 1;
			} else if (c == '\'' && i + 2 < b.length()) {
				i += b.charAt(i + 1) == '\\' ? 4 : 3;
			} else if (c == '/' && i + 1 < b.length() && b.charAt(i + 1) == '/') {
				while (i < b.length() && b.charAt(i) != '\n')
					b.setCharAt(i++, ' ');
			} else if (c == '/' && i + 1 < b.length() && b.charAt(i + 1) == '*') {
				int end = src.indexOf("*/", i + 2);
				end = end < 0 ? b.length() : end + 2;
				for (; i < end; i++)
					if (b.charAt(i) != '\n')
						b.setCharAt(i, ' ');
			} else {
				i++;
			}
		}
		return b.toString();
	}

	private static String join(String base, String path) {
		String p = ("/" + base + "/" + path).replaceAll("/+", "/");
		return p.length() > 1 && p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
	}

	private static int lineOf(String s, int index) {
		int line = 1;
		for (int i = 0; i < index; i++)
			if (s.charAt(i) == '\n')
				line++;
		return line;
	}
}
