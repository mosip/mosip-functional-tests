package io.mosip.testrig.apirig.coverage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.testrig.apirig.coverage.Inventory.Subject;
import io.mosip.testrig.apirig.coverage.Inventory.TestCase;
import io.mosip.testrig.apirig.coverage.Inventory.YamlFile;
import io.mosip.testrig.apirig.coverage.MatrixFile.RefResolver;

/** Everything one command needs, loaded once: inventory, matrix files, controllers, baseline. */
final class Estate {

	/** Never parsed as matrix files, at any depth under --out. */
	static final Set<String> RESERVED = Set.of("README.md", "AGENTS.md", "Summary.md", "CLAUDE.md");
	static final String PLANNED_DIR = "planned";

	static final class Loaded {
		final String relPath; // relative to --out, forward slashes
		final Path path;
		String text;
		MatrixFile matrix;
		String error;

		Loaded(String relPath, Path path) {
			this.relPath = relPath;
			this.path = path;
		}
	}

	final Path moduleRoot;
	final Path out;
	final Inventory inventory;
	final Map<String, Loaded> files = new LinkedHashMap<>();
	ControllerScanner.Result controllers; // null when no app source
	EndpointMap endpointMap;
	List<Path> appSourceRoots = new ArrayList<>();
	CoverageCheck.Baseline baseline;
	/** legacy-ids.txt — same line format as the baseline ({@code <id>  # optional reason}); null if absent. */
	CoverageCheck.Baseline legacy;
	boolean requireAppSource;

	Estate(Path moduleRoot, Path out, Inventory inventory) {
		this.moduleRoot = moduleRoot;
		this.out = out;
		this.inventory = inventory;
	}

	void loadMatrixFiles() throws IOException {
		files.clear();
		if (!Files.isDirectory(out))
			return;
		List<Path> mds;
		try (Stream<Path> s = Files.walk(out)) {
			mds = s.filter(p -> p.toString().endsWith(".md"))
					.filter(p -> !RESERVED.contains(p.getFileName().toString())).sorted().collect(Collectors.toList());
		}
		for (Path p : mds) {
			Loaded lf = new Loaded(Inventory.rel(out, p), p);
			lf.text = Files.readString(p, StandardCharsets.UTF_8);
			try {
				lf.matrix = MatrixFile.parse(lf.text);
			} catch (MatrixFile.MalformedException e) {
				lf.error = e.getMessage();
			}
			files.put(lf.relPath, lf);
		}
	}

	void loadControllers(List<Path> explicitRoots, boolean autoDiscover, String pathPrefix) throws IOException {
		List<Path> roots = new ArrayList<>();
		for (Path r : explicitRoots) {
			Path abs = r.toAbsolutePath().normalize();
			roots.addAll(abs.endsWith(Path.of("src", "main", "java")) ? List.of(abs)
					: ControllerScanner.findSourceRoots(abs, moduleRoot));
		}
		if (explicitRoots.isEmpty() && autoDiscover && moduleRoot.getParent() != null)
			roots.addAll(ControllerScanner.findSourceRoots(moduleRoot.getParent(), moduleRoot));
		appSourceRoots = roots;
		if (roots.isEmpty())
			return;
		controllers = ControllerScanner.scan(roots);
		if (controllers.endpoints.isEmpty()) {
			controllers = null;
			return;
		}
		List<String> yamlPaths = new ArrayList<>();
		for (Subject s : inventory.subjects)
			yamlPaths.addAll(EndpointMap.pathsOf(s.units));
		endpointMap = new EndpointMap(controllers.endpoints, yamlPaths, pathPrefix);
	}

	String matrixRelPath(String subjectName) {
		return subjectName + ".md";
	}

	/** Resolves {@code <ymlPath>::<uniqueIdentifier>} to the YAML file and the exact line. */
	RefResolver.Target resolve(String ref) {
		int sep = ref.indexOf("::");
		if (sep < 0)
			return null;
		YamlFile yf = inventory.yamlFiles.get(ref.substring(0, sep));
		if (yf == null)
			return null;
		TestCase tc = yf.byUniqueIdentifier(ref.substring(sep + 2));
		if (tc == null || tc.line == 0)
			return null;
		return new RefResolver.Target(inventory.resourcesRoot.resolve(yf.relPath).normalize(), tc.line);
	}

	/**
	 * The file as {@code sync} would write it, keeping its current last_updated. For a wired subject,
	 * units/categories come from the inventory; for a planned file they're kept from the file.
	 */
	String renderSynced(Loaded lf, Subject s, boolean addStubs) {
		MatrixFile mf;
		try {
			mf = MatrixFile.parse(lf.text);
		} catch (MatrixFile.MalformedException e) {
			throw new IllegalStateException(e);
		}
		apply(mf, s);
		if (addStubs)
			mf.addMissingStubs();
		mf.linkifyTestRefs(lf.path.getParent(), this::resolve);
		return mf.render();
	}

	static void apply(MatrixFile mf, Subject s) {
		if (s != null) {
			mf.subject = s.name;
			mf.subjectCode = s.code;
			mf.units = new ArrayList<>(s.units);
			mf.categories = CategoryChecklist.categoriesFor(s.multilang, s.dependencyState, mf.categories);
		} else {
			mf.categories = CategoryChecklist.categoriesFor(false, false, mf.categories);
		}
	}
}
