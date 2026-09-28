# API Test-Case Coverage Matrix — Overview

A one-page guide for testers, leads and anyone reading coverage numbers. Engineers authoring rows
should continue with a module's `coverage/README.md` (format, commands) and `coverage/AGENTS.md`
(per-change checklist); the design decisions are in `design-record.md` in this folder.

## Why this exists

Test cases used to live in two places that never met: an Excel master sheet per module (written
from stories and manual testing) and the automated YAML tests (written in code). Nothing linked them,
so they drifted. A 2026-09 review of pre-registration found **94 Excel test cases with no match in
the automation records and 23 automated tests missing from the Excel** — plus real endpoints that
neither side tested at all, and Excel rows pointing at test IDs that didn't exist.

The fix: **one place for every test case, next to the code, checked by a tool on every change.**

## What changed

- **Test cases live in markdown files in each module's repo** — in
  `<module>/api-test/src/main/resources/coverage/`. One file per API test subject, one row per
  scenario, each marked `✅ automated`, `🟡 not_automated` or `⛔ not_automatable`. Manual and planned
  scenarios are rows too. **The Excel sheets are retired.**
- **Nothing old is lost.** Every legacy test-case number is listed in the module's `legacy-ids.txt`
  and recorded on the row that covers it (`Legacy: <id>`), and the tool fails if one ever goes
  missing. Search the matrix for an old number to find its scenario.
- **Coverage is measured from the matrix**: `automated ÷ (automated + not_automated)`, shown in each
  module's `Summary.md` — overall, per endpoint and per category (positive, authn, authz, validation,
  not-found, boundary, idempotency, data isolation, …).
- **A tool checks it on every pull request** and fails the PR when something is out of place.

## How to add tests for a new feature

1. **Write the scenarios from the story as rows first** — Given/When/Then plus a concrete expected
   result, marked `🟡 not_automated`, in the file of the endpoint they call, with the story key on
   that file. A brand-new endpoint gets its file generated automatically.
2. **Automate them** (the YAML test, as today).
3. **Link each row to its test** and mark it `✅ automated`.
4. **Run the tool's `sync` and `check`**, and commit the matrix together with the tests.

## What the tool guarantees — and what it can't

It **fails the PR** if an automated test has no row, if a real endpoint in the service code has
neither a test nor a planned row, if two tests share an ID, if a row is left unfilled or a
category is missing, if the matrix is out of sync with the tests, or if a legacy test-case number
disappears. Deliberate exceptions (an internal-only endpoint, say) are listed with a written reason.

It **cannot know about a scenario nobody wrote.** A new rule on an existing endpoint, with no new
test and no new endpoint, looks the same to the tool as no change at all. Step 1 above — scenarios
from the story — and PR review are the guard. The tool also can't judge whether a row is *right*;
the authoring playbook has a mandatory second review pass for that.

## Where things stand

**pre-registration — complete (reference module):**

| | |
|---|---|
| Matrix files | 58 — 41 automated subjects + 17 planned files for endpoints with no test yet |
| Scenarios | 753 — 311 automated, 418 not yet automated, 24 not automatable |
| Coverage | **42.7%** — lower than the earlier 51.2% only because previously invisible endpoints and manual scenarios are now counted |
| Legacy Excel | all 376 test-case numbers accounted for: 371 on matrix rows, 5 out of scope with a reason (UI journeys, another module's API) |
| Open gaps | 0 — 11 deliberate exceptions, each with a reason |

**Other modules — ready to onboard.** One command (`CoverageCli init`) generates a module's matrix,
docs, exception list and CI check from the shared tooling in `apitest-commons`; the team then writes
the scenario rows. List the module's legacy test-case IDs in `legacy-ids.txt` first so the migration
is provably complete.

## Where to look

| Need | Go to |
|---|---|
| What's tested for an endpoint | `<module>/api-test/src/main/resources/coverage/<folder>/<Subject>.md` |
| Coverage numbers | the module's `coverage/Summary.md` |
| An old Excel test-case number | search the module's `coverage/` for it (`Legacy: <id>`) |
| How to write or change rows | the module's `coverage/AGENTS.md` |
| Commands, file format, every check | the module's `coverage/README.md` |
| Why it's designed this way | `design-record.md` (this folder) |
