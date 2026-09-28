## Commands

The tooling is a small CLI (`io.mosip.testrig.apirig.coverage.CoverageCli`) in `apitest-commons`
(source: `mosip-functional-tests/apitest-commons/src/main/java/io/mosip/testrig/apirig/coverage/`).
It's a dev-time and CI tool — not wired into any Suite.xml. Run it from `apitest-commons`:

```powershell
cd mosip-functional-tests/apitest-commons
mvn -q compile -Dgpg.skip=true -Dmaven.gitcommitid.skip=true
mvn -q org.codehaus.mojo:exec-maven-plugin:3.1.0:java `
  "-Dexec.mainClass=io.mosip.testrig.apirig.coverage.CoverageCli" `
  "-Dexec.args=<command> --module-root {{MODULE_ROOT_FROM_COMMONS}} --module-code {{MODULE_CODE}}"
```

| Command | What it does |
|---|---|
| `init` | One-time bootstrap for a module: writes `README.md`, `AGENTS.md`, `check-baseline.txt`, `legacy-ids.txt` and `.gitattributes` into this directory and the CI caller workflow into the repo's `.github/workflows/` (each only if missing), then runs `scaffold`. |
| `scaffold` | Creates a matrix file for every wired subject that doesn't have one, **and** a `planned/<VERB>_<path>.md` file for every real controller endpoint that no wired YAML targets (unless baselined) — so an untested endpoint gets a home for its intended scenarios instead of staying invisible. Never overwrites. |
| `sync` | Parse → merge → render every matrix file. Adds stub rows only for `(unit, category)` pairs with no row yet; never rewrites an existing row's text; re-links `Test` cells to the YAML line. Bumps `last_updated` only on files whose content actually changed. Idempotent — run it twice, get the same bytes. |
| `rollup` | Aggregates all matrix files (including `planned/`) into `Summary.md`. "As of" is the max `last_updated` across files, never wall-clock. |
| `check` | Read-only. Reports every gap below and exits non-zero if any gap isn't accepted in `check-baseline.txt`. `--keys` also prints each gap's exact baseline line. |

Options:

| Option | Meaning |
|---|---|
| `--module-root <dir>` / `--module-code <CODE>` | Required. The module's `api-test` dir, and its subject-code prefix. |
| `--out <dir>` | Matrix directory. Default `<module-root>/src/main/resources/coverage/` (this directory). |
| `--app-source <dir>` | Real service source (repeatable). **Default: auto-discovered** — every `src/main/java` beside `api-test` in the same repo. |
| `--path-prefix <prefix>` | Gateway prefix the YAML `endPoint:` carries and controllers don't. **Default: inferred per service** from the data (the prefix that lines most YAML endpoints up with that service's mappings); `check` prints what it inferred and marks services it had to guess for. On Git Bash pass `//v1/x` (MSYS rewrites a bare `/...`); the tool also undoes that rewrite if it already happened. |
| `--no-app-source` / `--require-app-source` | Turn endpoint checks off / make "no service source found" a gap (CI uses the latter). |
| `--baseline <file>` | Default `<out>/check-baseline.txt`. |
| `--legacy-ids <file>` | Default `<out>/legacy-ids.txt` (see "Legacy test-case IDs" below). |
| `--date <yyyy-MM-dd>` | Date stamped on files `sync`/`scaffold` changes (default today). |
| `--no-planned` | `scaffold`/`init`: don't create `planned/` files. |
| `--fail-on-warnings` | `check`: fail on warnings (see below) too, not only on gaps. |

Default `--out` is this directory, which gets bundled into the module jar like any other main
resource; `ExtractResource` never extracts it.

## Gap types `check` reports

API-test side:

1. `no-matrix-file` — a wired subject has no matrix file (run `scaffold`).
2. `unwired-yml` — a test YAML on disk that no Suite.xml `<test>` runs. These get no matrix file: wire it or delete it.
3. `empty-yml` — a test YAML with no cases, typically one accidentally emptied (see the repo `CLAUDE.md` § File Editing Rules for recovery).
4. `malformed-file` — a Suite.xml, YAML or matrix file that doesn't parse, or a `<test>` pointing at a missing YAML. Reported, never a crash.
5. `duplicate-unique-identifier` — two cases (anywhere in the module, compared case-insensitively) share a `uniqueIdentifier`. The ID is the only link from a matrix row to a test, so it must be unique module-wide.
6. `case-without-unique-identifier` — a wired case with no `uniqueIdentifier` can never be traced to a row.
7. `verb-mismatch` — a YAML's `restMethod:` disagrees with the verb its wired script class always sends (`PutWithPathParam` → PUT, ...). The class decides the real verb; fix `restMethod` so the matrix `Units` are true.

Matrix side:

8. `unit-no-row` — a unit has zero rows at all.
9. `category-missing` — a unit is missing a row for one of its required categories.
10. `unknown-category` — a row's `Type` isn't one of the file's categories (usually a typo — the row then silently drops out of every per-category count).
11. `unfilled-placeholder` — a stub row (`(TODO)` scenario or `TODO` expected result) is still unfilled, a row has no recognizable Status, or a `⛔ not_automatable` row gives no reason.
12. `unresolvable-test-ref` — an `✅ automated` row names no `<ymlPath>::<uniqueIdentifier>`, or names one no YAML case has.
13. `orphan-test-case` — a real, wired YAML case that no matrix row references. **This is the gate that stops a new test being added without its row.**
14. `summary-drift` — the front-matter `summary` block doesn't match the actual row counts.
15. `not-synced` — the file isn't what `sync` would write (units, categories, summary or Test links are stale) — run `sync`.
16. `malformed-row` — a grid row without exactly 8 cells, almost always an unescaped `|` in a cell (write `\|`, even inside backticks).
17. `duplicate-row-id` — the same `ID` twice in one file.
18. `stale-matrix-file` — a matrix file whose subject no Suite.xml runs any more.
19. `legacy-id-missing` — a test-case number listed in `legacy-ids.txt` appears in no matrix file.

Service side (on by default whenever service source is found):

20. `unmapped-endpoint` — a real controller endpoint no wired YAML targets **and** no `planned/` file covers: an endpoint with zero coverage, invisible to every other gap type. Resolve it by `scaffold` (plan it) or a baseline entry (intentionally untested — internal/deprecated).
21. `unreachable-endpoint` — a wired YAML `endPoint:` (or planned unit) that matches no real controller mapping — usually a wrong path. Matching is verb + path shape (path variables as wildcards, most specific mapping wins), so it complements, but doesn't replace, reading the controller in cycle 2. Paths routed by a servlet filter rather than a controller show up here too; baseline those with the reason.
22. `planned-endpoint-now-tested` — a `planned/` endpoint now has wired YAML: move its rows into that subject's file and delete the planned file.
23. `unscannable-mapping` — a controller mapping whose path is a constant, not a string literal, so the scan can't see it.
24. `app-source-missing` — only with `--require-app-source`: no service source was found.

Baseline hygiene (never baselinable themselves):

25. `baseline-entry-without-reason` — every accepted gap must say why.
26. `stale-baseline-entry` — an entry that no longer matches any gap; delete it.

Warnings (reported, but don't fail `check` unless `--fail-on-warnings`):

27. `possible-duplicate-row` — two rows in one file probably describe the same scenario, the usual
    result of adding a story's scenarios without reading the rows already there. Only pairs with the
    same `Type` on the same unit, where at least one row isn't backed by a YAML case, are considered;
    a pair is flagged when both expect the same error code on the same request field (ignoring the
    endpoint's own path/query parameters), when it's a second `authn`/`authz` row for one endpoint, or
    when the Scenario wording is ≥70% the same. It's a heuristic: if the rows really differ, make the
    Scenario text say how (or baseline the pair with the reason); if they don't, merge them — keep
    the older row and move any `Legacy:` note and story key onto it.

## Accepted gaps — `check-baseline.txt`

One line per intentionally accepted gap: `<gap-type> <key>  # reason` (`check --keys` prints the
exact line). This is for things that are *meant* to stay that way — an internal-only endpoint, a
deprecated one, a filter-routed path — never for "will fix later". Baseline entries are reviewed
like code.

## Legacy test-case IDs — `legacy-ids.txt`

When test cases are migrated in from a retired source (an Excel master test-case sheet, a test
management tool), list every old test-case number here, one per line. `check` then proves none
was lost: each ID must appear somewhere in a matrix file — by convention as `Legacy: <id>` in the
Notes of the row that covers it (several IDs: `Legacy: A, B`). An ID that genuinely belongs outside
this matrix (a UI journey, another module's API) keeps its line with a reason:
`<id>  # out of scope: <why and where it lives>`. Delete the file once nobody needs the old
numbers any more.

## Planned subjects — endpoints with no YAML yet

`scaffold` puts every untested real endpoint under `planned/` as a normal matrix file (front-matter
`planned: true`, all 8 core categories as `🟡 not_automated` stubs). It is where manual test cases,
exploratory scenarios and anything migrated from a legacy sheet go when the endpoint has no
automation yet. They count toward `Summary.md` like any subject, so an untested endpoint reads as
0%, not as absent. When YAML is later added for that endpoint, `check` raises
`planned-endpoint-now-tested`: move the rows into the real subject file and delete the planned one.

## CI

`.github/workflows/coverage-matrix-check.yml` in this repo calls the reusable workflow in
`mosip-functional-tests`, which builds the CLI from source and runs
`check --require-app-source` on every PR touching `api-test/**` or service Java. Any open gap
fails the PR.

## Governance

- **Nothing above `<!-- GENERATED:grid -->` is hand-edited — not even just the Coverage summary
  table.** The `Field | Value` table, `Units`, `Coverage summary`, `Categories`, and the raw YAML
  inside `<details>` are all regenerated by `sync` on every run:
  - `Subject` / `Subject code` / `Domain` / `Unit type` / `Units` / `Categories` come from the real
    Suite.xml + YAML (via `Inventory`), never from anything written in the file — hand-editing them
    does nothing lasting.
  - The `Coverage summary` numbers are recomputed every `sync` by counting the `Status` cells in
    the grid below — whatever you type there is discarded and replaced with the real count.
  - `Last updated` is fully tool-managed.
  - `Owner` is the one partial exception — `sync` does preserve it, but reads it from the `owner:`
    line in the raw YAML inside `<details>`, **not** from the visible table row. Edit it there if
    you want to set one; editing the visible "Owner" cell alone doesn't stick — the next `sync`
    regenerates that row from the (unchanged) raw value and silently discards your edit.
  - `stories:` is the second exception — an optional list of Jira story keys (e.g.
    `- MOSIP-17633`) in the raw YAML inside `<details>`, preserved by `sync` and rendered as a
    `Stories` row. It carries the requirement traceability a legacy sheet's `Story` column had;
    add it when a subject's scenarios come from a story.
  - The two things you ever hand-edit are (a) inside the generated grid table — the `ID`,
    `Scenario` / `Expected result` / `Status` / `Test` / `Notes` cells of a row (never `Type`,
    never the markers) — and (b) the prose inside the `<!-- ENDPOINT:details -->` block, which
    `sync` also preserves verbatim once authored. Nothing else in the file sticks across a `sync`.
- **Never renumber or reuse an `ID`.** Deleting a scenario leaves a gap in the sequence; that's fine.
- Reserved filenames `README.md`, `AGENTS.md`, `Summary.md`, `CLAUDE.md` are skipped by the tooling —
  don't name a subject folder to collide with these.
- Matrix files are LF-only (`.gitattributes` here pins it); a CRLF checkout would make every file
  look `not-synced`.
- See `AGENTS.md` in this directory for the row-authoring playbook and the mandatory two-cycle
  audit before a batch is considered done.
