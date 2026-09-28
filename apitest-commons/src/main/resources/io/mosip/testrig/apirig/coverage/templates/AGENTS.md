# Authoring playbook — {{MODULE_NAME}} coverage matrix

Read this before filling in a single row. It assumes you've already read `README.md` in this
directory for the format/legend and `docs/coverage-matrix/design-record.md` in `mosip-functional-tests` for the design
decisions this all follows.

## Every change that touches a test case — the non-negotiable checklist

This matrix is the **only** place test cases are recorded (it replaced the Excel master sheet).
Whenever you add, rename, move or delete a YAML case — or add a scenario that isn't automated —
finish all of these in the **same** change. CI runs `check` on the PR and fails on any miss, so
skipping a step only moves the failure later.

1. **`uniqueIdentifier` is unique module-wide.** `grep -ri "uniqueIdentifier: <id>" src/main/resources`
   before using one. Never copy a case block from another YAML without giving it a new ID (that's
   exactly how pre-registration's `GetAllDocForPrId.yml` ended up reusing `TC_prereg_UploadDocument_01`/`_02`). Every
   wired case must have one.
2. **`restMethod:` matches the wired script class's verb** (`SimplePost` → post,
   `PutWithPathParam` → put, `DeleteWithParam` → delete, ...).
3. **Read the subject's existing rows first, then author only what's missing** (see "Filling a row").
   Before adding a scenario — especially a batch of them from a new story — list which ones the file
   already covers (same endpoint, same condition, often the same error code); for those, add the
   story key / `Legacy:` note to the existing row instead of a new one. `check` warns
   (`possible-duplicate-row`) when a new row looks like an existing one — resolve every such warning
   before you finish. Then author the row: a new case gets its own
   row or is added to the `Test` of the row it genuinely covers; a renamed case gets its `Test`
   reference updated; a deleted case's row goes to `🟡 not_automated` (never deleted).
4. **Not automated yet?** It's still a row: `🟡 not_automated` in the subject's file, or — if no
   YAML targets that endpoint at all — in its planned file (`<VERB>_<path>.md` in the same folder; run `scaffold` if it doesn't exist).
5. Run `sync`, then `check`. Done means `check` exits 0 — or every remaining gap is one you
   intentionally added to `check-baseline.txt` with a reason.
6. **Commit the matrix files with the YAML.** Uncommitted work here has been lost before.

### Scenarios from outside the code (manual tests, the old Excel sheet, a story)

- Find the matching row first (same endpoint, same condition). If one exists, don't duplicate it —
  append `Legacy: <old test-case no.>` to its Notes so the old reference stays searchable
  (several: `Legacy: A, B`). Every old number is listed in `legacy-ids.txt`, and `check` fails
  (`legacy-id-missing`) until each one appears in some matrix file or carries an out-of-scope reason.
- Otherwise add a `🟡 not_automated` row with a full Given/When/Then, in the subject whose endpoint
  it calls, or the planned file for an endpoint with no YAML.
- Put the story key in the file's `stories:` front-matter list (see `README.md` § Governance).
- Out of scope for this matrix: UI flows, batch-job end-to-end journeys, other modules' APIs (e.g.
  masterdata) — record those in the owning module's matrix, not here; in `legacy-ids.txt` their
  line gets `# out of scope: <why / where it lives>`.

## Adding a subject

Subjects come from the Suite.xml, not from you. If a new `<test>` entry is added to
`{{SUITE_FILE}}`, run `scaffold` — it picks up the new subject automatically and
creates a stub matrix file with placeholder rows for every `(unit, category)` pair. You never
hand-create a matrix file.

If a YAML file exists on disk but no `<test>` entry references it, it's `unwired` — `check`
reports it, but it gets no matrix file (decision 7). If you want it covered, wire it into the
Suite.xml first, then `scaffold`.

A real endpoint with **no** YAML gets a planned matrix file (`<VERB>_<path>.md`, beside the other matrix files) from `scaffold` (unless it's
baselined as intentionally untested). Author its rows like any other subject — they're all
`🟡 not_automated` until YAML exists. When you later wire YAML for it, `check` raises
`planned-endpoint-now-tested`: move the rows (keeping their IDs) into the new subject's file and
delete the planned file.

## Filling a row

1. **Read the real code first** — the YAML test case(s), the `.hbs` request/result templates, and
   the test script class (e.g. `SimplePost`, `GetWithParamForAutoGenId`, `PostWithFormPathParamAndFile`).
   Do not write a scenario from the row's placeholder text alone.
2. Replace the placeholder `ID`, `Scenario`, `Expected result`, and `Status` with the real thing:
   - **ID**: for an `✅ automated` row, reuse the real YAML `uniqueIdentifier` verbatim (e.g.
     `TC_Prereg_CreatePrereg_01`) instead of the mechanical `API-<SUBJECT>-NNN` scheme — it's one
     less name to keep in sync, and it makes the ID column double as a search key back into the
     YAML. If two rows are legitimately backed by the same `uniqueIdentifier` (e.g. a positive row
     and a multilang row over the same case), disambiguate with a suffix:
     `TC_prereg_AddUpdateRegistration_01` / `TC_prereg_AddUpdateRegistration_01-multilang`. For a
     `🟡 not_automated`/`⛔ not_automatable` placeholder row (no real case yet), use
     `TC_<Module>_<Subject>_NNN` in the same style, numbered starting **one past the highest real
     `uniqueIdentifier` number already used in that subject's YAML** — e.g. if real cases run
     `_01`…`_30`, placeholders start at `_31`. Check the actual YAML (`grep uniqueIdentifier:`) for
     the true max before assigning; don't assume the matrix's current automated-row count is it.
   - **Scenario / Expected result**: write full Given/When/Then prose a non-automation reader can
     follow — "Given \<precondition/payload\>, when \<the call is made\>" / "Then \<concrete status
     code + response fields that matter\>". Not "positive case" / "should succeed", and not a
     terse fragment either. Since [[coverage-matrix-scenario-attribution]] loosened `sync`'s
     matching for single-unit subjects, the Scenario text does **not** need to contain the subject
     code or any boilerplate token anymore — write it for a human. (Multi-unit subjects — check the
     `Units` list — still need each row's Scenario to naturally mention that unit's exact
     method+path somewhere, since that's still how `sync` tells rows belonging to different units
     apart; this reads naturally anyway, e.g. "Given a `GET` request to `/v1/x/{id}`...". The match
     is a literal substring check against the exact `label()` string in the front-matter `units`
     list — full method plus the complete `endPoint:` path, base prefix included (e.g.
     `GET /preregistration/v1/applications`, not the shortened `GET /applications`). Copy the unit
     string straight out of the `Units` list at the top of the file rather than retyping a
     shortened form — a near-miss (missing base path, `{id}` instead of the YAML's real
     `{preRegistrationId}`) silently fails to match and `sync` appends a full duplicate stub set on
     the next run. Caught and fixed this way while authoring `GetAllApplications.md` and
     `GetPreRegDemographicDataByPrid.md` — always re-run `sync` once after authoring a multi-unit
     subject and confirm `written 0` / no stray `API-<SUBJECT>-NNN` rows before moving on.)
   - **Status**: `✅ automated` only if a real YAML case backs it (fill `Test`); otherwise
     `🟡 not_automated`, or `⛔ not_automatable` with a mandatory reason in `Notes`.
   - **Test**: `<ymlPath>::<uniqueIdentifier>` — copy the exact `uniqueIdentifier` from the YAML,
     don't invent one. `check`'s `unresolvable-test-ref` gap catches a typo here. Write it as
     plain text; `sync` automatically turns it into a link to that case's exact line in the YAML
     (self-healing — re-derived every `sync`, never hand-maintained). Don't hand-write the
     `[...](...)` link syntax yourself.
3. If a category legitimately doesn't apply to a given unit for this subject, don't delete the
   row — set `⛔ not_automatable` with the reason in Notes. Removing rows loses the audit trail.
4. Multiple real YAML cases can back the same scenario row, and one YAML case can be the
   strongest evidence for more than one row — reference it from each row it genuinely covers.
   Every real `uniqueIdentifier` in the subject's YAML should end up referenced by at least one
   row somewhere (`check`'s `orphan-test-case` gap flags ones that aren't).

## "About this endpoint" — write it for two different readers

Every matrix file has a hand-authored prose block (between `<!-- ENDPOINT:details -->` markers,
rendered after the grid) that `sync` preserves verbatim once it's non-placeholder. It exists
because the grid tells you *what's tested*, not *what the endpoint is* — someone who isn't reading
Java should be able to open this file and understand the endpoint on its own. Structure it as:

- **What this endpoint does** — plain-language purpose, no jargon, no class names. Say why it
  exists and what it fits into, not just what it technically does.
- **Request & response** — the actual wire-format JSON a caller sends and gets back, with a short
  plain-English description of the fields that matter. This is what the endpoint *is*, not how our
  test suite happens to build a request for it — leave automation mechanics out of this section.
- **Who can call it** — plain English: which kind of user is allowed, what happens if they're not
  (typically HTTP 403). Name the roles, that's it — **don't re-explain how the credential is
  transported here.** The cookie-vs-bearer-header mechanism, how our tests acquire the token, and
  the `kernel-auth-adapter` caveat are the same fact for every subject in this module, so they're
  written **once** in `README.md`'s "How authentication works in this module" section — link to it
  (`see README.md § How authentication works in this module`) instead of restating it. If you catch
  yourself typing the word "cookie" in a subject file, stop and check whether README.md already
  says it.

**No inline "(for engineers: ...)" asides, and no re-derived module-wide facts.** Every citation
that's genuinely *specific to this endpoint* (the exact `@PreAuthorize` role list, the exact config
property key backing it, an unusual request-building quirk like a dynamically-generated template)
goes into **one** collapsed section at the end:
```
<details>
<summary>Technical details (automation + verification notes)</summary>

...
</details>
```
Rationale: scattering a technical parenthetical after every plain paragraph defeats the point of
writing plain paragraphs — a non-coding reader hits code the moment they finish a sentence. One
collapsed section means the plain read stays clean all the way through. And keeping shared facts
(auth transport, token acquisition) in README.md instead of copy-pasted into all 41 files means
this section only ever holds what's *actually different* about this one endpoint — otherwise
41 near-identical paragraphs about the same cookie mechanism is pure duplication weight, not
useful detail. Skip the whole section for a subject where there's nothing endpoint-specific left
to say after that split (automation is just "post this JSON," authorization is just the one
annotation already named in "Who can call it") — don't pad it out for the sake of having one.

Verify every factual claim (roles, error codes, request shape) against the real service source the
same way cycle 2 already requires — see pre-registration's `api-test/src/main/resources/coverage/preReg/createPrereg.md` for a worked example of this structure.

## Extras — only where the surface is real

Don't add `crypto_integrity`, `injection`, or any extra to a subject's front-matter `categories`
list because it "might apply". `multilang` and `dependency_state` are detected mechanically by
`Inventory` from `templateFields` / `$ID:` tokens / suite `pathParams` — you shouldn't need to
touch those. `crypto_integrity` and `injection` need a human to confirm against the real
test-script code; add the category line to the front-matter yourself only once you've confirmed
it, and note why in the first row that uses it.

## Batch process (per the handoff doc's Next steps §6)

Per subject, per batch: read the real code → `scaffold` (if not already done) → fill every row →
`sync`. Per batch, once every subject in it is filled:

1. `rollup` then `check` — **cycle 1, structural**. Fix anything `check` reports.
2. **Cycle 2 — MANDATORY semantic self-audit.** A green `check` only proves the matrix is
   structurally complete (every unit×category has a row, every row is filled, no orphans). It
   does **not** prove the rows are *true*. Re-read every row in the batch against the real
   request/response contract:
   - Does the Expected result match what the API actually returns, not what you assumed?
   - Is the Test reference the case that actually exercises this scenario, not just the nearest
     one alphabetically?
   - Did an extra get added without a real code citation?
   - **Don't trust the YAML's own name/description as ground truth** — compare the literal input
     values against what the case claims to test. Test-suite authors make mistakes too: a case
     named/described for one scenario can carry data for a completely different (or no-op) one.
3. **Cycle 2 also cross-checks the real application/service source, not just the automation
   code.** The service implementation is a sibling of `api-test` in this same module's directory
   tree — for this module: {{SERVICE_SOURCE}} (a separate set of Maven modules from `api-test`;
   `check` prints the service directories it auto-discovered).
   Trace at least the error codes / status values / business terms a batch's rows assert against
   back to where the service actually throws or defines them:
   - `grep` the asserted error code (e.g. `PRG_CORE_REQ_002`) across the service source and read
     the call site, not just the enum declaration — an enum constant's *name* and its declared
     `.getCode()`/`.toString()` *value* can diverge, and different call sites can inconsistently
     use one or the other. If a test's expected code only matches because of an incidental
     `.toString()` call instead of the "correct" `.getCode()`, flag that fragility explicitly —
     it's one refactor away from silently breaking the test.
   - `grep` any business term a test name uses (e.g. "Correction Application") across the service
     source. Zero matches means the concept the test claims to exercise may not exist server-side
     at all — the test may be a mislabeled duplicate of a different, real scenario.
   - Where the real validation is schema-driven (e.g. `jsonValidator.validateIdObject(idSchema...)`
     against a live ID Schema fetched at runtime) rather than hardcoded Java, say so plainly in the
     Notes — that behavior isn't verifiable from static repo code, and the row's Expected result
     shouldn't claim more certainty than that.
   - Cite the exact file/method in the Notes when flagging a finding (e.g.
     `BaseValidator.validateVersion()`), so the next auditor can re-verify without redoing the grep.
   - **Check whether the YAML's `restMethod:` field actually matches the real HTTP verb.** It
     isn't authoritative — the wired `<class>` in the Suite.xml decides the real verb, and
     dedicated script classes (`SimplePost`, `PutWithPathParam`, `DeleteWithParam`, `GetWithParam`,
     ...) hardcode their own verb without reading `testCaseDTO.getRestMethod()` at all (confirmed in the
     pre-registration pilot: zero prereg test-script classes reference it). A mismatch here is a YAML documentation bug
     that also makes `Inventory`'s mechanically-derived "Units" line wrong — found once already in
     `UpdatePreRegStatus.md` (`restMethod: get`, wired to `PutWithPathParam`, real verb is `PUT`).
     Cheap check: open the suite entry's `<class>` element and see if its name's verb matches
     `restMethod`.
4. Fix whatever cycle 2 finds, re-run `sync` → `check`, and only then report the batch done.

A batch is not done because `check` is green. It's done after cycle 2 — and cycle 2 is not done
until both the test YAML *and* the real service source have been checked against each other, not
just against themselves.

### Other modules

This same principle applies when this playbook is copied/adapted for other modules in Phase 2:
locate that module's real service source (usually a sibling directory of `<module>/api-test`,
outside the api-test Maven module tree — e.g. `<module>/<module>/` or similar, not inside
`src/main/resources`) before starting cycle 2, and don't skip the cross-check just because `check`
came back green.
