# {{MODULE_NAME}} — Coverage Matrix

A living, in-repo test-coverage matrix for `{{MODULE_DIR}}/api-test`. One markdown file per
subject (one YAML test-folder = one `<test>` entry in `{{SUITE_FILE}}`), recording coverage
**intent** — what scenarios must exist for that subject — reconciled against the real
Suite.xml/YAML and the real service controllers by tooling in `apitest-commons`
(`io.mosip.testrig.apirig.coverage`) so it can't silently drift out of date.
Full design record: `docs/coverage-matrix/design-record.md` in `mosip-functional-tests`.

## Start here — how test cases are managed

**This directory is the single source of truth for this module's test cases** — automated, manual
and planned alike. There is no spreadsheet alongside it: a test case that isn't a row here doesn't
exist. (If this module had a legacy test-case sheet, every one of its IDs is listed in
`legacy-ids.txt` and can be found here by searching for `Legacy: <id>`.)

**Reading it.** One file per API test subject (`<folder>/<Subject>.md`), plus `planned/` files for
real endpoints that have no automated test yet. Each file lists its scenarios as rows with a status:
`✅ automated` (a real YAML test backs it), `🟡 not_automated` (known scenario, not automated yet) or
`⛔ not_automatable` (with the reason). `Summary.md` is the dashboard: coverage is
`automated ÷ (automated + not_automated)`, overall, per subject and per category.

**Adding tests for a new feature — the workflow**

1. **Write the scenarios first**, from the story, as rows — before or alongside any automation.
   Put each one in the file of the endpoint it calls (`🟡 not_automated`), add the story key to that
   file's `stories:` list, and give every row a real Given/When/Then and a concrete expected result.
   A brand-new endpoint gets its file from `scaffold` (as a `planned/` file until YAML exists).
2. **Automate** — add the YAML case (unique `uniqueIdentifier`, correct `restMethod`), templates and
   Suite.xml entry as usual.
3. **Link** — flip the row to `✅ automated` and put `<ymlPath>::<uniqueIdentifier>` in its `Test`
   column.
4. **Run `sync`, then `check`**, and commit the matrix files in the same change as the YAML.
   `AGENTS.md` has the full per-change checklist.

**What `check` enforces** (and CI fails the PR on): every YAML test case has a row; every real
controller endpoint has either a test or a planned file; `uniqueIdentifier`s are unique; every row is
filled in and every category is present; files are in sync with the YAML; every legacy test-case ID
is still present. Deliberate exceptions live in `check-baseline.txt`, each with a written reason.

**What no tool can catch — people own this:**

- **A scenario nobody wrote.** A new rule on an *existing* endpoint, with no new YAML and no new
  endpoint, changes nothing `check` can see. Step 1 above — scenarios from the story — is the only
  guard, together with PR review.
- **A row that is wrong** — linked to the wrong test, or expecting something the API doesn't do.
  `AGENTS.md`'s second review pass (cycle 2) exists for this.
- **Test cases kept anywhere else** (a new sheet, a Jira comment). Put them here as rows.

## Scope

- **In scope:** functional API coverage — positive/negative paths, authn/authz, validation,
  not-found, boundary, idempotency, data isolation, plus module-specific extras (multilang,
  crypto integrity, dependency-chained state, injection) wherever the real code shows that
  surface.
- **Out of scope:** performance/load, SAST/DAST, dependency scanning, accessibility, UI journeys.
- Coverage % is **`automated ÷ (automated + not_automated)`** from this matrix — never derived
  from line/branch coverage.

## Legend

| Column | Meaning |
|---|---|
| `ID` | For an `✅ automated` row: the real YAML `uniqueIdentifier` verbatim — not the mechanical `API-<SUBJECT>-NNN` scheme; disambiguate with a suffix (`-multilang`, etc.) if two rows share one case. For a placeholder row: `TC_<Module>_<Subject>_NNN`, numbered starting one past the highest real `uniqueIdentifier` number already used in that subject's YAML. Stable once assigned, never renumbered or reused. `scaffold` seeds brand-new stub rows with `API-<SUBJECT>-NNN` — replace it when you author the row. |
| `Scenario (given/when)` | Full Given/When/Then prose a non-automation reader can follow — the actual precondition/payload and the call being made. For a **multi-unit** subject (more than one entry under `Units`), the text must also contain that unit's exact method+path, since that's how the tooling tells rows of different units apart. |
| `Expected result (then)` | The concrete pass condition — status code, response shape, specific field values — written as a "Then ..." sentence. Not "should work". |
| `Type` | One of the categories in this subject's front-matter `categories` list. |
| `Tier` | Test level, e.g. `integration`. |
| `Status` | Exactly one of `✅ automated` / `🟡 not_automated` / `⛔ not_automatable` (reason required in Notes for the last). |
| `Test` | `<ymlPath>::<uniqueIdentifier>` of the real YAML case this row is backed by, written as plain text — `sync` turns it into a link to that case's exact line in the YAML and re-derives it on every run. |
| `Notes` | Free text — required when Status is `⛔ not_automatable`. `Legacy: <id>` records a migrated legacy test-case number. |

Category checklist — **core 8**, seeded on every subject:
```
positive · authn · authz · validation · not_found · boundary · idempotency · data_isolation
```
**Extras**, appended only where the subject's real code/YAML shows the surface (never invented):

| Extra | Added when |
|---|---|
| `multilang` | YAML uses `templateFields` (detected automatically). |
| `crypto_integrity` | Test script does cert/CSR/JWT validation (human-confirmed; add it to the front-matter). |
| `dependency_state` | Subject depends on a prior case's output via a `$ID:...$` token (detected automatically). |
| `injection` | A free-text field reaches a query/search API (human-confirmed). |

## How authentication works in this module

_TODO — write once, here, how a caller authenticates to every endpoint of this module (token
transport, how the test suite acquires it, what `role: noauth` means), so subject files only state
their endpoint-specific role list. See pre-registration's `coverage/README.md` for a worked example._

## "About this endpoint"

Every matrix file has a hand-authored prose block, rendered after the grid (between
`<!-- ENDPOINT:details -->` markers), that `sync` preserves verbatim once it stops being the
placeholder: **What this endpoint does** → **Request & response** → **Who can call it** → one
collapsed `<details><summary>Technical details (automation + verification notes)</summary>` section
for citations. See `AGENTS.md` for the full guidance.

