package io.mosip.testrig.apirig.coverage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.mosip.testrig.apirig.coverage.ControllerScanner.Endpoint;

/**
 * Matches the api-test side's endpoint strings against real controller endpoints.
 *
 * <p>Controllers never carry the gateway/context prefix the YAML {@code endPoint:} has (e.g.
 * {@code /preregistration/v1}); the prefix is configured per service (and even per profile), so
 * rather than read properties it is <b>inferred</b> per service from the data: the prefix that
 * makes the most YAML endpoints line up with that service's controller paths. {@code --path-prefix}
 * overrides the inference for every service.
 *
 * <p>Normalization: query string dropped, {@code {var}} collapsed to a wildcard segment, repeated
 * and trailing slashes removed. A controller {@code {}} segment matches any one segment, a
 * trailing {@code **} matches the rest. Each YAML endpoint maps to its <em>most specific</em>
 * match only, so {@code /applications/prereg} is attributed to that literal mapping, not to a
 * sibling {@code /applications/{id}}.
 */
public final class EndpointMap {

	private final List<Endpoint> endpoints;
	private final Map<Path, String> prefixByService = new LinkedHashMap<>();
	private final String globalPrefix;
	private final java.util.Set<Path> fallbackServices = new java.util.HashSet<>();

	public EndpointMap(List<Endpoint> endpoints, List<String> yamlEndpoints, String explicitPrefix) {
		this.endpoints = endpoints;
		if (explicitPrefix != null) {
			globalPrefix = normalize(explicitPrefix);
			for (Endpoint e : endpoints)
				prefixByService.put(e.serviceRoot, globalPrefix);
			return;
		}
		Map<Path, Map<String, Integer>> votes = new LinkedHashMap<>();
		Map<String, Integer> global = new HashMap<>();
		for (String y : yamlEndpoints) {
			String[] ys = segments(normalize(pathOf(y)));
			Map<Path, Map<String, Integer>> seenForThisYaml = new HashMap<>();
			for (Endpoint e : endpoints) {
				String[] cs = segments(normalize(e.path));
				int literals = literalCount(cs);
				if (literals == 0 || Arrays.asList(cs).contains("**") || cs.length > ys.length)
					continue;
				int k = ys.length - cs.length;
				if (!matches(cs, Arrays.copyOfRange(ys, k, ys.length)))
					continue;
				String prefix = "/" + String.join("/", Arrays.copyOfRange(ys, 0, k));
				seenForThisYaml.computeIfAbsent(e.serviceRoot, s -> new HashMap<>()).merge(prefix, literals,
						Math::max);
			}
			seenForThisYaml.forEach((svc, m) -> m.forEach((p, w) -> {
				votes.computeIfAbsent(svc, s -> new HashMap<>()).merge(p, w, Integer::sum);
				global.merge(p, w, Integer::sum);
			}));
		}
		globalPrefix = best(global, "");
		for (Endpoint e : endpoints)
			prefixByService.computeIfAbsent(e.serviceRoot, svc -> {
				if (!votes.containsKey(svc))
					fallbackServices.add(svc); // no YAML hits this service at all — prefix is a guess
				return best(votes.getOrDefault(svc, Map.of()), globalPrefix);
			});
	}

	/** Services whose prefix couldn't be inferred (no YAML targets them) and fell back to the module-wide one. */
	public boolean isFallback(Path serviceRoot) {
		return fallbackServices.contains(serviceRoot);
	}

	public String prefixFor(Endpoint e) {
		return prefixByService.getOrDefault(e.serviceRoot, globalPrefix);
	}

	/** The external form of a controller endpoint, e.g. "GET /preregistration/v1/applications/prereg". */
	public String externalLabel(Endpoint e) {
		String p = prefixFor(e);
		return e.verb + " " + ("/".equals(p) ? "" : p) + e.path;
	}

	public Map<Path, String> prefixes() {
		return prefixByService;
	}

	/**
	 * Most specific controller endpoint a "VERB /path" string reaches, or null.
	 *
	 * @param verb the verb actually sent (the wired class's verb when it has one)
	 */
	public Endpoint match(String verb, String path) {
		String[] ys = segments(normalize(path));
		Endpoint best = null;
		int bestScore = -1;
		for (Endpoint e : endpoints) {
			if (!"ANY".equals(e.verb) && verb != null && !"ANY".equals(verb) && !e.verb.equals(verb))
				continue;
			String[] ps = segments(prefixFor(e));
			if (ys.length < ps.length || !Arrays.equals(ps, Arrays.copyOfRange(ys, 0, ps.length)))
				continue;
			String[] rest = Arrays.copyOfRange(ys, ps.length, ys.length);
			String[] cs = segments(normalize(e.path));
			if (!matches(cs, rest))
				continue;
			int score = literalCount(cs) * 2 + (Arrays.asList(cs).contains("**") ? 0 : 1);
			if (score > bestScore) {
				best = e;
				bestScore = score;
			}
		}
		return best;
	}

	// ---------------------------------------------------------------- normalization

	static String pathOf(String unit) {
		int sp = unit.indexOf(' ');
		return sp >= 0 && unit.substring(0, sp).matches("[A-Z]+") ? unit.substring(sp + 1) : unit;
	}

	static String verbOf(String unit) {
		int sp = unit.indexOf(' ');
		return sp >= 0 && unit.substring(0, sp).matches("[A-Z]+") ? unit.substring(0, sp) : null;
	}

	static String normalize(String path) {
		String p = path.trim();
		int q = p.indexOf('?');
		if (q >= 0)
			p = p.substring(0, q);
		p = p.replaceAll("\\{[^}]*\\}", "{}").replaceAll("\\$[A-Za-z0-9_:]+\\$", "{}");
		p = ("/" + p).replaceAll("/+", "/");
		return p.length() > 1 && p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
	}

	private static String[] segments(String normalizedPath) {
		String p = normalizedPath.startsWith("/") ? normalizedPath.substring(1) : normalizedPath;
		return p.isEmpty() ? new String[0] : p.split("/");
	}

	/** Controller pattern {@code cs} against concrete-ish YAML segments {@code ys}. */
	private static boolean matches(String[] cs, String[] ys) {
		for (int i = 0; i < cs.length; i++) {
			if ("**".equals(cs[i]))
				return true;
			if (i >= ys.length)
				return false;
			if ("{}".equals(cs[i]))
				continue;
			if ("{}".equals(ys[i]) || !cs[i].equals(ys[i]))
				return false;
		}
		return cs.length == ys.length;
	}

	private static int literalCount(String[] cs) {
		int n = 0;
		for (String s : cs)
			if (!"{}".equals(s) && !"**".equals(s))
				n++;
		return n;
	}

	private static String best(Map<String, Integer> votes, String fallback) {
		String best = fallback;
		int max = 0;
		for (Map.Entry<String, Integer> v : votes.entrySet()) {
			if (v.getValue() > max || (v.getValue() == max && best != null && v.getKey().length() > best.length())) {
				best = v.getKey();
				max = v.getValue();
			}
		}
		return best;
	}

	static List<String> pathsOf(List<String> units) {
		List<String> out = new ArrayList<>();
		for (String u : units)
			out.add(pathOf(u));
		return out;
	}

	static String upper(String s) {
		return s == null ? null : s.toUpperCase(Locale.ROOT);
	}
}
