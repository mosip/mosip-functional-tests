# Test-Coverage Matrix Estate — Work-In-Progress Handoff

> **Versioned home of this document.** Moved here from an untracked `COVERAGE_MATRIX_HANDOFF.md` at
> the workspace root on 2026-09-25, so the design record lives in git next to the tooling it
> describes. For the day-to-day process start with `overview.md` in this folder, then a module's
> `coverage/README.md`.

**Status:** Design locked. Pilot module switched from keymanager to **apitest-prereg**
(pre-registration/api-test) — larger surface (40 wired subjects), authored in batches with
check-ins. Tooling build starting in `apitest-commons`. Resume at "Next steps".
**Last updated:** 2026-09-25 — tooling rebuilt after it was found lost (never committed) and gates
added (CI workflow, endpoint checks on by default, planned subjects, baseline) — see gotchas 24–26.

---

## Resume prompt (paste into a fresh Claude Code session at the repo root)

> Read `docs/coverage-matrix/design-record.md` in `mosip-functional-tests`. All §0 design questions are
> already answered and locked in that file — do not re-ask them. Continue from the
> "Next steps" section: build the Java coverage tooling in apitest-commons, then
> author the **prereg** pilot (batched — see "prereg pilot profile" and the batch
> groupings under "Next steps" step 6).

---

## What this is

Building a **living, in-repo test-coverage matrix** estate: one markdown matrix file per
testable subject, recording coverage **INTENT** (what scenarios must exist), generated and
reconciled by tooling so it cannot drift. Coverage % comes from the matrix
(`automated ÷ (automated + not_automated)`), never from line coverage.

The full spec was supplied by the user in the originating prompt. The sections below record
only what was **decided for this repo** and what was **discovered about this repo**.

---

## Locked decisions (all confirmed by the user — do not re-litigate)

| # | Decision | Choice |
|---|----------|--------|
| 1 | **Subject grain** | **Per test-folder / suite entry** — one matrix per YAML test folder (e.g. `keymanager/GenerateCSR`), mapping 1:1 to a `<test>` entry in the Suite.xml. Units within a subject = the distinct `(restMethod, endPoint)` pairs its YAML touches. Estate size ≈ **1,290 matrix files**. |
| 2 | **Traceability** | **Reuse the existing `uniqueIdentifier`** already present on every YAML case. **Zero edits to the 10,500 existing YAML cases.** The matrix `Test` column holds `<ymlPath>::<uniqueIdentifier>`, hand-authored as plain text — **revised 2026-08-06:** `sync` then automatically wraps a resolvable reference into `[<ymlPath>::<uniqueIdentifier>](<relative-path>#L<line>)`, a clickable link straight to that case's line in the YAML. Re-derived fresh on every `sync` (self-healing if the YAML's line numbers shift), never hand-maintained — see gotcha 16. |
| 3 | **Tooling language/location** | **Java, in `apitest-commons`**, new package `io.mosip.testrig.apirig.coverage`. Ships in the commons jar every module already depends on. |
| 4 | **Scope for this phase** | **Lean core + prereg pilot.** (See "Next steps".) Switched from the originally-scoped keymanager pilot (4 subjects) to prereg (40 subjects) — keymanager profile below is kept as the next module for Phase 2. Dashboard HTML, CI gates, pre-commit hook and the other 10 modules are **Phase 2**, after user review. |
| 5 | **Matrix file location** | `<module>/api-test/src/main/resources/coverage/` — committed in each module's own git repo, under `src/` per Java-project convention. **Revised 2026-08-06** (originally `<module>/api-test/tests/coverage/`, outside `src/main/resources`, specifically to dodge packaging/extraction — see below). |
| 6 | **Category checklist** | **Core 8 + surface-detected extras.** (See below.) |
| 7 | **Unwired YAMLs** | Authoritative subject list = **what the Suite.xml actually runs**. The 68 unwired YAMLs get no matrix file but ARE reported by `check` as a distinct gap type (`unwired-yml`). |

### Category checklist (decision 6)

**CORE 8 — seeded on every API subject:**
```
positive     authn        authz          validation
not_found    boundary     idempotency    data_isolation
```

**EXTRAS — added only where the module's code/YAML actually shows the surface:**
| Category | Added when |
|---|---|
| `multilang` | YAML uses `templateFields` / `$1STLANG$` |
| `crypto_integrity` | test script does cert/CSR/JWT validation |
| `dependency_state` | subject depends on a prereq / `$ID:...$` value |
| `injection` | a free-text field reaches a query/search API |

This honours the spec rule: **read the code to confirm the surface — never invent a vuln.**

### Matrix location revision (2026-08-06)

Decision 5 originally put matrix files at `<module>/api-test/tests/coverage/`, deliberately
outside `src/main/resources`, to dodge two things: `maven-shade-plugin` bundling them into the
module's `jar-with-dependencies`, and `ExtractResource` extracting them at runtime. On review,
`ExtractResource.extractCommonResourceFromJar()` turned out to only pull a fixed whitelist of
keys (`config/`, `dbFiles/`, a handful of named files) — it was never going to touch a `coverage/`
folder regardless of where it lived, so that half of the original rationale didn't hold. The
shade-plugin packaging concern is real but minor (41+ markdown files add negligible size to the
jar). Given that, the user opted to keep matrix files under `src/` for Java-project convention:
**`<module>/api-test/src/main/resources/coverage/`**. `CoverageCli`'s default `--out` was updated
to match; `--out` can still override it per-invocation.

### ID scheme

- `scaffold` still seeds every brand-new stub row with `API-<SUBJECT_CODE>-<NNN>` where
  `SUBJECT_CODE` = `<MODCODE>-<SUBJ>` (e.g. `API-KEYMGR-GENCSR-001`) — that's what `maxSequence`/
  `ID_PATTERN` compute against to number the *next* stub. **Revised 2026-08-07 (gotcha 18):**
  when you actually author a row, replace that mechanical ID:
  - `✅ automated` row → the real YAML `uniqueIdentifier` verbatim (e.g.
    `TC_Prereg_CreatePrereg_01`). Two rows genuinely backed by the same case get a disambiguating
    suffix (`TC_prereg_AddUpdateRegistration_01-multilang`).
  - `🟡 not_automated` / `⛔ not_automatable` placeholder row (no real case yet) →
    `TC_<Module>_<Subject>_NNN` in the same style, numbered starting one past the highest real
    `uniqueIdentifier` number already used in that subject's YAML (check the YAML, don't assume
    the matrix's current automated-row count is the ceiling).
- Domain is `api` for the whole estate.
- IDs are stable, never reused, **never renumbered** — this still holds under the new scheme too.

### Matrix file format (frozen — 8 columns, this exact order)

```
ID | Scenario (given/when) | Expected result (then) | Type | Tier | Status | Test | Notes
```
- **No priority column.**
- Status — exactly 3 states: `✅ automated` · `🟡 not_automated` · `⛔ not_automatable`
  (reason **mandatory** in Notes for the last).
- Front-matter: `subject`, `subject_code`, `domain`, `owner`, `last_updated`, `unit_type`,
  `units: [...]`, `summary: {total, automated, not_automated, not_automatable}`, `categories: [...]`.
  **Revised 2026-08-06 (twice):**
  1. Lives inside a fenced ` ```yaml ` … ` ``` ` block, not bare `---`/`---` — a generic
     CommonMark renderer (VS Code's preview, unlike GitHub's blob viewer) doesn't special-case
     dash-delimited front-matter; it reads the first `---` as a thematic break and runs the
     non-list fields together into one paragraph. The fence renders identically everywhere.
  2. The fenced block itself is now tucked inside a `<details>` (collapsed by default) below a
     human-facing summary: a `| Field | Value |` table (subject code/domain/owner/last
     updated/unit type), a `**Units**` bullet list, a `| Total | Automated | ... | Coverage |`
     stats table, and a `**Categories:**` line — all regenerated fresh from `Parsed` on every
     render, never themselves parsed back. `parse()` locates the fenced block by content
     (first ` ```yaml ` … ` ``` ` in the file), not by position, so this prose is free to change
     independent of the parser. This is what a human actually reads; the raw YAML is there only
     for the tool and for anyone who wants to inspect it.
- Generated grid lives between `<!-- GENERATED:grid -->` … `<!-- /GENERATED:grid -->`, and now
  wraps the table header + separator too, not just the rows — an HTML comment sitting *inside* a
  pipe table (i.e. between the separator and the first row) splits it into two broken fragments in
  most renderers. The header/separator must be inside the markers so the whole table is one
  uninterrupted block.

Example row:
```
| API-KEYMGR-GENCSR-001 | POST /v1/keymanager/generateCSR — valid appId, empty referenceId | 200 + PEM CSR, csrIsValid=true | positive | integration | ✅ automated | keymanager/GenerateCSR/GenerateCSR.yml::TC_KeyManager_GenerateCSR_01 | |
```

---

## Repo reconnaissance already done (don't redo)

- **Stack:** Java 21 / Maven 3.9.12 / TestNG / REST Assured; YAML-driven test definitions +
  Handlebars templates. Python 3.13.9 also available locally.
- **Scale:** ~10,500 YAML test cases, ~1,000 distinct endpoints, 12 service modules.
- **CRITICAL — multi-repo:** `apitest-commons` and each of the 12 modules are **separate git
  repos**. There is **no root git repo**. This constrains Phase 2: an aggregate dashboard
  cannot be committed atomically, and CI wiring must be replicated per repo.
- **No OpenAPI spec in this repo** — the services under test live elsewhere. The mechanical
  inventory therefore comes from `testNgXmlFiles/*Suite.xml` + the YAML `endPoint:` /
  `restMethod:` fields.
- **`apitest-commons` had no `src/test` directory** at the start of this work. TestNG and JUnit
  are dependencies. SnakeYAML (`org.yaml.snakeyaml.Yaml`) and `javax.xml.parsers` /
  `org.w3c.dom` are already used in `apitest-commons` main code, so both parsers are available.
  A JUnit self-test suite (`CoverageCliTest`/`InventoryTest`/`MatrixFileTest`, 33 tests) was
  built alongside the tooling during Part B and regression-caught the gotcha 17 bug below — it
  was later removed at the user's request (2026-08-11) once the prereg pilot was fully authored
  and stable, since the tooling itself was judged done and self-tests weren't wanted going
  forward. `src/test` no longer exists in this repo; if the tooling is extended for Phase 2, a
  fresh test suite (or none, per the same call) is a decision to make again at that point.

### Per-module inventory (properly parsed)

| Module | Suite refs | YAML on disk | Unwired |
|---|---|---|---|
| keymanager | 4 | 4 | 0 |
| inji-verify | 15 | 15 | 0 |
| id-repository | 31 | 33 | 2 |
| admin-services (masterdata) | 200 | 250 | 50 |
| partner-management-services (pmp) | 242 | 258 | 16 |
| pre-registration (prereg) | 41 | 44 | 3 |

Totals across estate: ~10,500 test cases, **68 unwired YAML files** (71 including prereg's 3,
not yet folded into the estate-wide total above; prereg's own ~250 test cases across its 41
subjects are likewise not yet folded into the ~10,500 estate figure above).

### prereg pilot profile (THE pilot module — 41 subjects)

- Suite file: `pre-registration/api-test/testNgXmlFiles/preregSuite.xml` (the non-master one;
  `preregMasterTestSuite.xml` just references it — same rule as every other module).
- 41 wired `<test>` entries (verified via real XML/regex parse of the split `ymlFile`/`value`
  attribute, per gotcha #1), one distinct YAML each, under `src/main/resources/preReg/<Folder>/`.
- 44 YAML files on disk → **3 unwired**: `AuditLog.yml`, `BookMultipleAppointment.yml`,
  `UpdatePridStatus.yml`.
- restMethod mix: post (majority, ~193), get (~56), put (~33), delete (~10).
- role mix: mostly `batch`, some `admin`, one `audit`, one `invalidBatch` (auth negative case).
- Test script classes: `CreatePreReg`, `UpdatePrereg`, `BookAppoinment`, `BookAppoinmentByPrid`,
  `PostWithFormPathParamAndFile`, `PostWithFormDataAndFileForNotificationAPI`,
  `PostWithPathParamsAndBody`, `PutWithPathParam`, `DeleteWithParam`, `GetWithParam`,
  `GetWithParamForAutoGenId`, `SimplePost`, `PreregAuditValidator`.
- `templateFields` (multilang extra) used in `AddLostUinApplication`, `AddUpdateRegistration`,
  `AuditLog` (the last is unwired, so doesn't get a matrix file but confirms the surface exists
  in this module).
- `$ID:...$` cross-test references in 27 YAML files; 60 files carry a `preRegistrationId` path
  param sourced from `CreatePreregistration` → **`dependency_state` extra applies broadly** —
  prereg is a heavily-chained flow (create → upload doc → book appointment → status updates →
  cancel/delete).
- No cert/CSR/JWT validation script in `prereg/testscripts/` → **`crypto_integrity` extra does
  NOT apply** here (unlike keymanager). `injection` extra to be confirmed per-subject during
  authoring — candidates: `Transliteration`, `GetPRIDByDateRange`, and other free-text
  search/date-range GETs.
- Subject code prefix: `PREREG` (e.g. `API-PREREG-CREATEPREREG-001`).
- Batch groupings for authoring (see "Next steps" step 6): registration lifecycle (5 subjects),
  lost UIN (2), application query/listing (11), documents (7), appointments/booking (11),
  OTP/notifications/misc (5) — sums to 41, matching the wired-subject count. `GetPreRegInfo`
  test name maps to YAML folder `GetPreRegInfoByPrid`.

### keymanager pilot profile (reference — next module after prereg, the 4 subjects)

- `GenerateMasterKey`, `GenerateECSignKey`, `GenerateCSRPrereq`, `GenerateCSR`
- 37 test cases, all `restMethod: post`; roles: 34 `admin`, 3 `noauth`
- Endpoints: `/v1/keymanager/generateMasterKey|generateECSignKey` with
  `/CERTIFICATE`, `/CSR`, `/INVALID` object-type suffixes; plus `/v1/keymanager/generateCSR`
- Existing case-name patterns already imply categories: `_Valid_Smoke` (positive),
  `_Unauthenticated_Negative` (authn), `_EmptyApplicationId_/_WhitespaceApplicationId_/
  _InvalidRequesttime_Negative` (validation), `_ForceFalse_Valid` (idempotency),
  `_InvalidObjectType_Negative` (validation/not_found)
- Scripts `SimplePostWithCertValidation` / `SimplePostWithCsrValidation` ⇒ the
  **`crypto_integrity` extra applies** to this module.

---

## Gotchas already paid for — DO NOT REPEAT

1. **Parse Suite.xml with a real XML parser, not line-based grep.**
   `<parameter name="ymlFile"` and `value="..."` are frequently split across two lines.
   A naive line grep undercounts badly (reported id-repository as 2 refs instead of 31).
2. **Status substring bug:** `"automated" in "not_automated"` is `true`. Match status
   **most-specific-first** or every not-automated row counts as automated.
3. **YAML quoting:** unit strings containing `{id}`, commas, colons or braces must be quoted
   in front-matter or the file won't re-parse.
4. **Column positions:** the gap report parses by index. Adding/removing a column requires
   migrating **all** files in one pass.
5. **~~Scenario text must contain the unit string~~ — superseded by gotcha 17.** For a
   **multi-unit** subject, the Scenario still needs to naturally mention that unit's exact
   method+path somewhere (e.g. `POST /v1/keymanager/generateCSR — …`), since that's the only
   signal available to tell rows belonging to different units apart. For a **single-unit**
   subject this is no longer required — see gotcha 17 for why that changed.
6. **Single-unit subjects:** use the subject code as the single implicit unit for
   `attributionTokens`/gap messages, but — since gotcha 17 — the Scenario text does **not** need
   to literally contain it anymore; `rowMatches` treats a blank match-token as "any row of the
   right category belongs to this unit," and only falls back to the subject code for *display* in
   gap messages.
7. **`sync` must be non-destructive:** parse → merge → render, re-emitting each existing row's
   **raw source line verbatim**. Only the grid + front-matter `summary` + `last_updated` are
   regenerated.
8. **Idempotent:** running `sync` twice must produce byte-identical output.
9. **No wall-clock timestamp** in the aggregate — derive "as of" from max `last_updated`,
   otherwise the Phase-2 freshness gate fails daily.
10. **Reserved files:** the tool must skip `README.md`, `AGENTS.md` and the generated rollup in
    the matrix dir, or it will try to parse them and crash.
11. **`check` never writes.**
12. **Repo rule (from CLAUDE.md):** never use PowerShell `Set-Content` for YAML/matrix edits —
    it can silently produce empty files. Use the `Edit` tool or Python via Bash.
13. **Matrix file location:** `<module>/api-test/src/main/resources/coverage/` (revised
    2026-08-06 from an original outside-`src` location — see decision 5 above for why).
14. **Matrix Markdown format quirks, both discovered post-hoc via user review, both now fixed
    in `MatrixFile.java` — don't reintroduce either when touching the renderer:**
    - The `<!-- GENERATED:grid -->` markers must wrap the table **header + separator + rows**,
      not just the rows. An HTML comment sitting between the separator and the first row splits
      the pipe-table into two broken fragments in most renderers (GitHub, VS Code preview).
    - Front-matter metadata must live inside a fenced ` ```yaml ` … ` ``` ` block, not bare
      `---`/`---`. A generic CommonMark renderer doesn't special-case dash-delimited front-matter
      the way GitHub's blob viewer does — it reads the first `---` as a thematic break and runs
      the non-list fields into one squashed paragraph. The human-facing summary (a `Field|Value`
      table, a `Coverage summary` table, a `Categories` line) is generated separately, above the
      fold; the fenced raw YAML is tucked into a collapsed `<details>` below it, for the tool only.
15. **Cycle 2 must cross-check the real application/service source, not just the automation
    code — for both directions of "don't trust it blindly".** The test-script/YAML side can be
    wrong (a case named/described for one scenario but carrying data for another — found in
    prereg batch 1, `createPrereg` TC_26 "with_future_date" whose `dateOfBirth` was never
    actually a future date), and the real application code can *also* be wrong or fragile (found
    in the same batch: `BaseValidator.validateVersion()` emits an error code that only matches
    the test's expectation because it calls `.toString()` instead of `.getCode()` on an enum
    whose constant names diverge from their declared values — a "cleanup" refactor to the
    "correct" accessor would silently break the test). Full playbook for this is in each module's
    `AGENTS.md`, not duplicated here — see `pre-registration/api-test/src/main/resources/coverage/AGENTS.md`.
    For prereg specifically, the real service source lives at `pre-registration/pre-registration/`
    (`pre-registration-application-service`, `pre-registration-core`, etc.) — a sibling of
    `pre-registration/api-test`, not inside it. Other modules will have an equivalent sibling
    service directory; find it before starting cycle 2, don't skip the cross-check because
    `check` came back green.
16. **`Test` column line-linking (added 2026-08-06):** `sync` rewrites a resolvable
    `<ymlPath>::<uniqueIdentifier>` `Test` cell into `[<ymlPath>::<uniqueIdentifier>](<relative
    path>#L<line>)`. The line number comes from `Subject.getTestCaseLines()`
    (`Inventory.findUniqueIdentifierLines`), matched by **exact trimmed-line equality** against
    `uniqueIdentifier: <value>` — not a substring search, so `TC_1` can never match `TC_10`'s
    line. The relative path is computed via `Path.relativize` between the matrix file's directory
    and the target YAML; on a different filesystem root (e.g. a non-default `--out` on a
    different Windows drive than the module), relativize throws — `linkifyTestRefs` catches that
    and leaves the cell as plain text rather than crash or emit a machine-specific absolute path.
    Authors still just write plain `<ymlPath>::<uniqueIdentifier>` text — never hand-write the
    link syntax; `sync` derives it fresh every run, so it self-heals if the YAML's lines shift.
17. **Rephrasing a Scenario into readable prose silently duplicated every category as a stub row
    (found + fixed 2026-08-07, while retrofitting `createPrereg.md`).** The original design
    (gotcha 5/6) required a row's Scenario cell to literally `.contains()` an "attribution token"
    — the subject code, for single-unit subjects — so `merge()`/`check` could tell a category was
    already covered. Rewriting `createPrereg.md`'s 35 rows into real Given/When/Then prose dropped
    that boilerplate token, so `merge()` concluded every one of the 8 core categories was
    uncovered and `sync` silently appended 8 duplicate `API-PREREG-CREATEPREREG-NNN` stub rows to
    the live file — `check` then correctly flagged them as `unfilled-placeholder`, which is what
    surfaced the bug. **Never trust your own tooling blindly either** — this was caught the same
    way an application-code or test-suite bug would be: by not accepting a red flag from `check`
    at face value and tracing *why*, instead of assuming the newly-written content must be at
    fault. Fixed in `MatrixFile.attributionTokens`/`rowMatches`: single-unit subjects now use a
    blank match-token (matches by category alone — there's no ambiguity with only one unit);
    multi-unit subjects still require the unit's method+path to appear in the Scenario text.
    Regression-guarded at the time by a dedicated JUnit test
    (`MatrixFileTest#mergeRecognizesRephrasedScenarioTextAsCoveredEvenWithoutTheSubjectCodeToken`)
    — that test suite was removed on 2026-08-11 (see the recon note above), so this is no longer
    an automated guard, just documented behavior. The bug recurred three more times by
    inspection alone while authoring batches 3, 5, and 6 (always the same root cause: a
    multi-unit row's Scenario text didn't contain the unit's *exact* label string) — each time
    caught by manually re-running `sync` and checking for `written 0` / no stray `API-*` stub
    rows, per the standing rule added to `AGENTS.md`'s cycle-2 checklist.
    **If you ever add a new multi-unit subject's rows**, make sure each row's Scenario naturally
    mentions that unit's exact endpoint label, or the same failure mode recurs for that subject.
18. **`ID` scheme (revised 2026-08-07):** an `✅ automated` row's `ID` is the real YAML
    `uniqueIdentifier` verbatim (e.g. `TC_Prereg_CreatePrereg_01`), not the mechanical
    `API-<SUBJECT>-NNN` scheme `scaffold` still seeds new stub rows with — replace it when you
    author the row. Two rows legitimately backed by the same case get a disambiguating suffix
    (`-multilang`, etc.). A placeholder row (no real case yet) uses `TC_<Module>_<Subject>_NNN`
    in the same style, numbered starting one past the highest real `uniqueIdentifier` number
    already used in that subject's YAML — check the actual YAML, don't assume the matrix's
    automated-row count is the ceiling. `ID_PATTERN`/`maxSequence` (which only recognizes the
    `API-...-NNN` shape) are unaffected — they only compute where a *newly scaffolded* stub's
    number should start, and real/placeholder IDs in the new scheme never collide with that
    pattern.
19. **"About this endpoint" is two audiences, not one (revised 2026-08-07).** First draft mixed
    "what the endpoint does" with "how our test automation happens to build the request" in one
    `Request body` paragraph — technically accurate but unreadable for anyone not reading the
    automation code, which defeats the section's purpose. Fixed structure (see each module's
    `AGENTS.md`): **What this endpoint does** (plain purpose) → **Request & response** (the actual
    wire-format JSON, plain terms, no automation mechanics) → **Who can call it** (plain-English
    authorization + a one-line technical aside) → optional collapsed
    `<details><summary>How our tests build this request (automation notes)</summary>` for
    non-obvious automation mechanics only. Also **rendered after the grid, not before it**
    (`MatrixFile.render` — moved 2026-08-07): this file gets reopened constantly just to check
    coverage status, so the summary/categories/grid stay near the top; the endpoint write-up,
    which can grow a JSON block or two, is reference material read once and then skipped past —
    it belongs at the end, not blocking the frequently-checked content. `parse()` finds the
    `<!-- ENDPOINT:details -->` markers by content, not position, so this reordering needed no
    parser change.
20. **One collapsed technical section, not scattered inline asides (revised 2026-08-07, same
    day as gotcha 19).** First pass at gotcha 19's structure put a `_(For engineers: ...)_` italic
    parenthetical after almost every plain paragraph — user asked directly whether that was
    actually useful. It wasn't wrong (the citations have real audit value — someone modifying the
    code needs to know exactly where to look), but the *placement* defeated the point: a
    non-coding reader hit `@PreAuthorize(...)`/config-key syntax mid-read, every paragraph. Fixed:
    **every** citation and automation mechanic — not just the ones already in the old "How our
    tests build this request" section — now lives in exactly **one** collapsed section at the end,
    renamed `Technical details (automation + verification notes)`. The three plain sections above
    it (What this endpoint does / Request & response / Who can call it) stay 100% free of inline
    technical asides. `Who can call it` also gained a standing requirement: state how the
    credential is actually transported, in plain terms — MOSIP's kernel-backed services read the
    login token from an HTTP **cookie** named `Authorization`, not an `Authorization: Bearer
    <token>` header (confirmed from `RestClient.postRequestWithCookie`'s
    `.cookie(cookieName, cookieValue)` call in `apitest-commons`) — this is shared infrastructure
    so it's the same fact across every subject in a module, not a per-subject discovery.
21. **Module-wide shared facts belong in `README.md`, once — never copy-pasted into every subject
    file (caught 2026-08-07, same day as gotcha 20, by the user directly asking "won't this get
    duplicated across all the md files?").** Gotcha 20's fix (one collapsed technical section)
    stopped the *inline-asides* duplication but didn't address a second kind: the cookie-vs-header
    transport mechanism, the `KernelAuthentication.getTokenByRole` acquisition flow, and the
    `kernel-auth-adapter` caveat are the same fact for **every** subject in a module — restating
    all of that in each of 41 files' "Technical details" section would have been 41 near-identical
    paragraphs, pure weight with no added information. Fixed: added a "How authentication works in
    this module" section to `README.md` as the single source of truth for whatever's genuinely
    shared infrastructure (auth transport mechanism, token acquisition), and trimmed each subject's
    "Who can call it" / "Technical details" down to *only* what's actually specific to that one
    endpoint (the exact role list, the exact config property key, an unusual request-building
    quirk) — with a one-line link back to README.md instead of restating the shared part. General
    principle for any module this gets adapted to: before writing a "fact" into a subject file, ask
    whether it's true of every subject in the module — if so, it belongs in `README.md`, not
    repeated 40+ times.
22. **`restMethod` in the YAML is not authoritative for the real HTTP verb — the wired test-script
    class is (found retrofitting `UpdatePreRegStatus.md`, 2026-08-07).** Every case in
    `updatePreRegStatus.yml` declares `restMethod: get`, and `Inventory` mechanically renders the
    subject's "Units" line as `GET ...` from that field — but the suite wires this subject to
    `PutWithPathParam`, whose `test()` method calls `putWithPathParamAndCookie(...)`
    **unconditionally** and never reads `testCaseDTO.getRestMethod()` anywhere in the class
    (confirmed by reading the full source). The real call sent is a `PUT`, proven independently by
    row `TC_prereg_UpdatePreRegStatus_09`'s expected `PRG_PAM_APP_023` — an error only reachable
    from `CommonServiceUtil.statusCheck()`, which only the real `PUT` handler's code path can
    invoke. Grepped every prereg test-script class for `getRestMethod`/`restMethod`: **zero**
    matches — no class in this module reads it at all. `restMethod` is purely decorative metadata;
    each dedicated script class (`SimplePost`, `PutWithPathParam`, `DeleteWithParam`,
    `GetWithParam`, ...) hardcodes its own verb, and the wired `<class>` element in
    `preregSuite.xml` is what actually determines the HTTP method — not the YAML field. This one
    instance was a documentation inaccuracy with no functional impact (the test still hits the
    intended endpoint), but it means **`Inventory`'s "Units" line can be silently wrong** wherever
    a YAML's `restMethod` doesn't match its wired class's implied verb, and neither `sync` nor
    `check` currently has a rule to catch that class of drift (unlike gotcha 17's category
    duplication, which is now regression-tested). Not exhaustively re-audited across all 41
    subjects this pass — added to the cycle-2 checklist in `AGENTS.md` as something to spot-check
    going forward, starting with batch 3.
23. **The mechanical inventory only ever sees the api-test side — an endpoint with zero YAML has
    zero visibility, not even a `0%` row (found 2026-08-12, after all 41 prereg subjects were
    authored, when the user asked whether a missed endpoint would be caught).** `Inventory` builds
    every `Subject`/`Unit` purely from `testNgXmlFiles/*Suite.xml` + the YAML it points to — it has
    no access to, and never reads, the real service's controller source. Cross-checking prereg's
    real `@GetMapping`/`@PostMapping`/etc. by hand against the 41 subjects turned up **7 real
    endpoints with no YAML at all** (`POST /qrCode/generate`, `GET /login/config`,
    `POST /login/sendOtpWithCaptcha`, `GET`/`POST /proxy/**`, `GET /uispec/latest`/`/all`,
    `DELETE /applications/updateregistration/{applicationId}`, `GET /applications/prereg`) —
    invisible to `Summary.md`'s 51.2%, which only ever measures the denominator of what's already
    represented. Fixed by adding two new opt-in `check` gap types (10/11,
    `unmapped-endpoint`/`unreachable-endpoint`) backed by a new `ControllerScanner` class — a
    regex scan (not a real Java parser) over the real service's controller source, gated behind a
    new `--app-source <path>` flag so every other gap type stays exactly as before when it's
    omitted (the real service source isn't guaranteed to be checked out alongside every module).
    Running it against prereg (`--app-source .../pre-registration-application-service/src/main/java
    --path-prefix //preregistration/v1`, see the Windows/Git-Bash note below) reproduced all 7
    endpoints found by hand, plus 2 more the manual sweep missed (`POST /logAudit`,
    `DELETE /applications/lostuin/{applicationId}` — the real Lost-UIN-delete endpoint
    `DeleteLostUinApplication.md`'s wrong-endpoint bug, gotcha-documented separately, leaves with
    zero genuine coverage) — 16 real gaps total once the 2 internal-only mappings
    (`/internal/notification`, `/internal/applications/appointment/{id}`) and one deprecated
    mapping (`POST /login/sendOtp`) are triaged out as expected, same as `unwired-yml` always
    required.
    - **Windows/Git-Bash gotcha found building this:** a bare `/`-prefixed CLI argument (like
      `--path-prefix /preregistration/v1`) gets silently rewritten by MSYS to a Windows path
      (`C:/Program Files/Git/preregistration/v1`) before `java` ever sees it — no error, just a
      no-op strip and every endpoint looking unreachable. `ControllerScanner.normalize()` now
      auto-detects and un-escapes the standard MSYS workaround (`//preregistration/v1`, a doubled
      leading slash), so the caller only has to remember to double the slash, not why.
    - **`unreachable-endpoint`'s normalization is deliberately coarse** (verb + path-shape only,
      path variables collapsed to `{}`) and can produce a false "reachable" on a coincidence — e.g.
      `UpdatePreRegStatus`'s YAML-declared `restMethod: get` (gotcha 22, known-inaccurate) still
      matched a *real* `GET` at that same path (the read-only status-check endpoint, a different
      real handler from the `PUT` the wired class actually calls), so this check alone doesn't
      catch gotcha 22 — it complements the manual cycle-2 checklist item, doesn't replace it.
    - Not yet run against any module besides prereg; `--app-source`'s path is module-specific
      (`<module>/<module>/<service>/src/main/java`, a sibling of `<module>/api-test`) and assumed
      present in the same checkout, which the original recon note flagged as unverified in general.
      **Superseded by gotcha 24:** app source is now auto-discovered and the prefix inferred per
      service; verified on prereg, keymanager, id-repository and admin-services.
24. **The whole tooling package was lost — it was never committed (found 2026-09-25).** When the
    user asked for gate fixes, `io.mosip.testrig.apirig.coverage` existed nowhere: not in the
    `apitest-commons` tree, any commit/branch/stash/reflog, or any built jar. The matrix files
    (also untracked in the pre-registration repo at that point) survived; the Java did not.
    **Rebuilt** from this document's spec, with acceptance = `sync` + `rollup` on the 41 existing
    prereg files must be byte-identical (it is; `sync` twice → `written 0`). Classes:
    `Inventory`, `MatrixFile`, `CategoryChecklist`, `ControllerScanner`, `EndpointMap` (new —
    prefix inference + most-specific matching), `CoverageCheck` (gaps + baseline), `Estate`,
    `CoverageCli`. Rules re-derived from the files themselves: units are in first-appearance
    order; a subject is named after its YAML's **folder** (`Transliteration/Translate.yml` →
    `preReg/Transliteration`), falling back to `folder/file` only when two wired YAMLs share a
    folder; unit strings are YAML-quoted when they contain `{ } ? & :` etc.; `Summary.md` ends
    with a blank line. **Lesson: commit tooling and matrix files with the change that makes them.**
25. **Gates added in the rebuild — why the Excel master sheet and the matrix drifted apart
    (2026-09-25 reconciliation, `coverage/PreReg-Excel-vs-MD-Reconciliation.xlsx`).** 94 Excel
    rows had no matrix counterpart and 23 automated cases had no Excel row, because nothing
    connected the two: the root `CLAUDE.md`'s "Adding a New Test Case" steps never mentioned the
    matrix, `check` never ran in CI, endpoint checks were opt-in, and the matrix had no home for a
    scenario whose endpoint had no YAML. Fixes: `CLAUDE.md` step 4 + "Coverage Matrix" section;
    an `AGENTS.md` per-change checklist; reusable CI workflow
    `mosip-functional-tests/.github/workflows/coverage-matrix-check.yml` (+ a caller per module
    repo); endpoint checks on by default; `planned/` subjects for endpoints with no YAML; a
    `check-baseline.txt` whose every entry needs a reason and goes stale when unused; and new gap
    types `duplicate-unique-identifier` (live: `GetAllDocForPrId.yml` reuses
    `TC_prereg_UploadDocument_01/_02`), `case-without-unique-identifier`, `verb-mismatch`
    (gotcha 22, now enforced), `empty-yml` (live: `UpdatePridStatus.yml` is 0 bytes),
    `malformed-row` (live: `GetPRIDByDateRange.md:71`, an unescaped `||`), `not-synced`,
    `duplicate-row-id`, `stale-matrix-file`, `planned-endpoint-now-tested`,
    `unscannable-mapping`. Full list in each module's `coverage/README.md`.
27. **prereg finished + harness ready for other modules (2026-09-25, second pass).** Excel master
    sheet fully migrated: all 376 distinct test-case numbers are in `legacy-ids.txt`; 371 appear as
    `Legacy: <id>` on the covering row, 5 carry an out-of-scope reason (2 UI/batch e2e journeys, 3
    masterdata APIs). The Excel's UID column was unreliable — link by Feature (= YAML case name)
    first, and 2 rows (`TC-MOSIP-17633-44/45`) had to be mapped by hand. 17 `planned/` files authored
    from controller source (`TC_Prereg_Planned*` IDs), 4 new rows + 2 enriched rows from Excel
    evidence, `stories:` front-matter from the sheet's Story column. Fixed: duplicate IDs in
    `GetAllDocForPrId.yml`, `restMethod` in `updatePreRegStatus.yml`/`DeleteSpecificDocForaPRID.yml`,
    the tab in `AuditLog.yml`, the broken `GetPRIDByDateRange.md` row. Baselined with reasons: dormant
    unwired YAMLs, the 5 legacy `/appointment/...` paths (**need a live-env check**). New tooling:
    `init` / `refresh-docs` commands, templates under `apitest-commons/src/main/resources/io/mosip/
    testrig/apirig/coverage/templates/`, gap types `unknown-category` and `legacy-id-missing`,
    planned files now held to category/sync rules, baseline staleness not judged for checks that
    didn't run. `check` green with and without `--no-app-source`.
26. **Endpoint-gap caveats.** A path routed by a servlet filter instead of a controller (id-repo's
    `/identity/v2`, rewritten by `BaseIdRepoFilter`) is reported `unreachable-endpoint` — baseline
    it with that reason. A service no YAML targets gets the module-wide prefix as a *fallback*
    (printed as such); its `unmapped-endpoint` labels may carry the wrong prefix — pass
    `--path-prefix` if that matters.

---

## Next steps (Phase 1 — this is where to resume)

Code build starts here (steps 1–5); step 6 targets prereg, not keymanager.

1. **Verify the commons build surface** — ✅ done. `apitest-commons/pom.xml` had no
   `src/test` directory and no Surefire override/skip — default Surefire conventions apply, so
   `*Test.java` under a `src/test/java` runs automatically on `mvn test`/`mvn clean install`.
   `junit:junit` is already a test-scope dependency. `org.yaml.snakeyaml.Yaml`
   (`AdminTestUtil.java`, `new Yaml(new Constructor(LinkedHashMap.class))`) and
   `javax.xml.parsers.DocumentBuilder` (`XmlPrecondtion.java`) are both already used in commons
   main code — reuse those exact patterns, no new dependencies needed. (A `src/test/java` self-test
   suite was in fact added here during Part B and later removed on 2026-08-11 — see the recon
   note above; `src/test` doesn't currently exist in this repo.)
2. **Build the tooling** in `apitest-commons`, package `io.mosip.testrig.apirig.coverage`:
   - `Inventory.java` — parse `testNgXmlFiles/*Suite.xml` (real XML parser) + the referenced
     `*.yml`; yields subjects, their units `(restMethod, endPoint)`, their `uniqueIdentifier`s,
     and detects which category extras apply.
   - `MatrixFile.java` — parse → merge → render, non-destructive, idempotent.
   - `CategoryChecklist.java` — core 8 + surface-detected extras.
   - `CoverageCli.java` — `scaffold` / `sync` / `rollup` / `check`.
3. **Gap types `check` must report:** (1) subject with no matrix file, (2) unit with no row,
   (3) category missing for a present unit, (4) unfilled placeholder rows, (5) `automated` row
   whose Test doesn't resolve to a real YAML case, (6) tagged case with no matrix row (orphan),
   (7) summary drift, (8) **`unwired-yml`** (repo-specific, decision 7). A malformed file is a
   *reported gap*, not a crash. Exit non-zero on any gap.
4. **Self-tests** for the tooling — these test the harness, **not** the product, so they must
   NOT carry coverage-row tags and must not count toward product coverage.
5. **Docs:** hand-written `README.md` (scope, legend, commands, governance) + `AGENTS.md`
   authoring playbook, both in `pre-registration/api-test/src/main/resources/coverage/`.
6. **Author the prereg pilot** — all 41 subjects, batched with check-ins after each batch
   (confirmed with the user — prereg is 10x keymanager's original 4-subject scope, too much for
   one uninterrupted pass). Batches:
   1. Registration lifecycle (5): CreatePreregistration, UpdatePreregistration,
      UpdatePreRegStatus, DeletePreRegistration, AddUpdateRegistration
   2. Lost UIN (2): AddLostUinApplication, DeleteLostUinApplication
   3. Application query/listing (11): GetApplicationWithPrId, FetchApplicationByPrid,
      GetApplicationStatusWithPrId, GetAllApplications, GetAllApplicationsWithapplicationId,
      GetAllPreRegApplications, GetPreRegInfo(ByPrid), GetPreRegDemographicDataByPrid,
      GetApplicationStatusByApplicationID, GetPRIDByDateRange, GetUpdatedDateTimeByPrid
   4. Documents (7): Uploaddocument, UpdateDocRefID, CopyDocument, GetAllDocForPrId,
      GetSpecificDocumentforaPRID, DeleteSpecificDocForaPRID, DeleteAllDocForaPRID
   5. Appointments/booking (11): BookAppointment, BookAppointmentByPRID, GetAppointmentDetails,
      CancelAppointment, CancelApplicationsAppointment, FetchAppointmentDetailsByPrid,
      DeleteBooking, GetAvailableSlotForRegCentreId, GetBookingsForRegCenter, GetPRIDForRegCent,
      FetchAvailabilityData
   6. OTP/notifications/misc (5): SendOtp, ValidateOtp, SendNotifications, InvalidateToken,
      Transliteration

   Per subject, per batch: read the real code first → scaffold → fill every row → `sync`; per
   batch: `rollup` → `check` → **cycle 1 structural** → **cycle 2 MANDATORY semantic
   self-audit** (a green `check` is NOT done) → re-run → report back before starting the next
   batch.
7. **Stop and report** for user review before scaling to the other 10 modules (keymanager
   included).

**Expected and correct:** coverage % reads 0% until tagged tests are green. The matrix is
intent; writing/linking the tests is the next phase.

---

## Phase 2 (deferred, after review)

Dashboard (self-contained HTML, inline CSS) + markdown rollup · CI gap-report job ·
freshness gate · pre-commit hook running `sync` on staged matrix files · auto-filed issues ·
rollout across the remaining 10 modules (keymanager first, since its 4-subject profile is
already scoped above), **security-first, in genuine batches**, with cycle 2
after each batch.

Out of scope entirely: performance/load, SAST/DAST, dependency scanning, accessibility —
those belong to a separate non-functional track. Behavioural security (authz, isolation,
injection resistance) **is** functional and belongs in this matrix.