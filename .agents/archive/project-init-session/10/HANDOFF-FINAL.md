# Handoff — Breaking the Nonogram Course Out Into Its Own Repository

**Version:** 0.3
**Source:** turns 06–07 of this directory, conducted inside the closed-out Chapter 1 repository at
`${CH_01_LOCAL_REPO}` with its full tree available for inspection. Turns 01–05 were conducted
without it, which is why v0.4 mis-estimated what was already known.

**Current artifacts:** `project.env`, `${DOC_SPEC}`, `${DOC_GUIDE}`, `${DOC_REVIEW}`, and this file. Numbered
directories hold the per-turn versions; everything earlier is superseded but retained as provenance.

**Environment:** `project.env` — every name, path, link, and locked stylistic choice these
documents refer to by variable.


---

## 1. The structural change in v0.6

Turn 07 rewrote how a chapter works, in response to a specific, observed problem with Chapter 1:
**the capstone and its grader were published before the lessons.** Three costs followed — code
shaped to fit the grader rather than the problem, testing responsibility displaced onto whoever
wrote the grader, and more help requested than was needed, because the target belonged to someone
else.

The fix is *loose at the front, rigorous at the end*.

### The four chapter states

| State | Contains | Written when |
|---|---|---|
| 1 — Syllabus line | One sentence | Day one, for all fourteen chapters |
| 2 — Manifest | Goal, plus "Must be true when you're done" and what depends on each entry | Two or three chapters ahead |
| 3 — Expanded | Lessons with named verification | On arrival, drafted by the learner |
| 4 — Capstone | Gap analysis of what was actually built | **After every lesson in the chapter is done** |

Only one chapter is ever in state 3. `CAPSTONE.md` for an unfinished chapter is a stub holding only
the "Must be true" list. Filling it in early is the failure mode the structure exists to prevent.

Currently: Chapters 2 and 3 expanded, Chapter 4 a manifest, Chapters 5–14 syllabus lines.

### "Must be true" replaces the signature contract

The old contract block named classes, methods, parameter order, and return types — four decisions
taken from the learner before they had thought about any of them. A "Must be true" entry states a
guarantee a later chapter depends on, and nothing else:

> *Something in the engine turns a grid into a deterministic string, with no printing inside it, and
> a test asserts that string character-for-character.*

Real constraint, zero naming. The rule for writing them: include a promise only if a later chapter
would break without it, and name that chapter.

### Gate 3 no longer writes tests

The agent-authored hidden-test gate is gone. Test writing is exclusively the learner's. Gate 3 is
now a **gap analysis**: the agent reads the code and the suite and names uncovered behaviour,
miscategorised tests, tests to delete, and brittleness — then the learner writes the missing tests.

Same measurement value (the gap count is still recorded per chapter in `JOURNAL.md`), zero
agent-authored test code, and the learner still does the writing.

### The standing rule for agents

**Example, never solution.** An agent may demonstrate a concept on unrelated subject matter. It may
not write, or show, implementation or test code for the thing being built. Appendix E of the spec
routes this through the tracker's own three test files, which are safe examples by construction —
real, in the learner's hand, and containing nothing about nonograms.

---

## 2. Question answered: why the engine cannot touch `java.io`

The code splits in two. The **engine** holds the grid, clue derivation, solvers, and the generator.
**`tools`** holds mains, file reading and writing, and the terminal game. Only `tools` performs I/O.
Dependency direction is `tools → engine`, never back — the same rule as Chapter 1's
`Main → cli → service → repository/model`, with fewer layers.

Three reasons, all of which Chapter 1 already demonstrated:

**Testing cost.** `TextTable` was pure, so its test asserted a string and finished.
`ConsoleApplication` touched streams, so it needed injected `Scanner` and `PrintStream`. `Main`
needed an entire subprocess. Same behaviour, an order of magnitude more scaffolding. A solver that
reads a file inherits that cost and buys nothing with it.

**Determinism.** Pure code returns the same answer for the same input forever. Once a method reads a
file, the answer depends on the disk, and a failing test stops being reproducible — which is the
property the whole three-tier ladder rests on.

**Portability.** Chapter 9 writes JSON; Chapter 10 reads it in a browser with no filesystem at all.
Engine logic has to be expressible in a runtime where `java.io` does not exist as a concept.

### Make it enforceable

The tracker's `${CHECKSTYLE_CONFIG}` already runs `TreeWalker` import modules (`AvoidStarImport`,
`UnusedImports`, `CustomImportOrder`, `RedundantImport`), so Checkstyle's `ImportControl` module
drops straight in. Configure it to fail the build when anything in `${ENGINE_PACKAGE}` imports
`${ENGINE_FORBIDDEN_IMPORTS}`. A boundary a human has to remember is a boundary that
erodes; this one fails `mvn verify` instead. Lesson 2.4 covers it.

`System.out` is not an import and `ImportControl` will not catch it. Either add a
`RegexpSinglelineJava` check scoped to the engine, or accept that one as a habit backed by the
"exactly one place prints" rule and the tier 3 tests that would catch a stray print.

---

## 3. Question answered: testing the browser chapters

The constraint is the spec's own: one HTML file, no build step. That rules out anything requiring a
bundler and rules in surprisingly capable options.

| Tier | Approach | Cost |
|---|---|---|
| 1 — file | Keep play logic in plain `.mjs` modules that never touch the DOM; run with `node --test` | Zero. Ships with Node, no npm install, no config |
| 2 — flow | `tests.html`: a page loading the same modules plus a hand-written assert harness, printing results into the page | One afternoon, and it is the same exercise as the Chapter 2 subprocess harness in a different runtime |
| 3 — end to end | A real browser driver such as Playwright | Deferred. Trigger: verifying touch on an actual device, or wanting CI |

The tier 2 recommendation is the interesting one. A hand-written in-page harness keeps the
no-build-step promise, runs in the real browser rather than a simulation of one, and — because the
learner writes the harness — teaches what a test runner actually is. It parallels the Chapter 2
subprocess harness deliberately.

The load-bearing design decision that makes any of this possible is keeping DOM code and play logic
in separate modules. That belongs in Chapter 10's manifest when it gets written, as a "Must be true"
entry, because Chapter 11's touch handling and Chapter 12's persistence both depend on it.

---

## 4. Flagged incompatibilities

**Resolved this turn — `ordinal()` as a save format.** v0.5's Lesson 3.1 justified testing enum
ordinals because "Chapter 12 makes them a save format." That is wrong: Chapter 12's saves are
JavaScript, which has no concept of a Java enum ordinal. The lesson is now inverted, and is better
for it — *never derive a wire format from `ordinal()`; give each state an explicit external code.*
The test's purpose is unchanged (fail loudly if someone reorders the constants), but its reasoning
is now correct.

**Open, low priority — Chapter 14 ships nothing.** The DP solver arrives after Chapter 13's
deployment, and the browser never runs Java, so it has no product impact. This is defensible as a
learning finale and the spec now says so explicitly rather than leaving it implied. Worth revisiting
only if finishing on something that does not ship turns out to sap momentum.

**Open — Chapters 10–13 need a testing manifest before Chapter 9 is finished.** The approach in
section 3 is a recommendation, not yet a "Must be true" list. It has to become one while there is
still time to shape Chapter 9's JSON around it.

---

## 5. What to copy into the new repository

Destination paths are relative to `${PROJECT_LOCAL_REPO}`.

### Verbatim, from `${CH_01_LOCAL_REPO}/`

| Source | Destination | Notes |
|---|---|---|
| `${CH_01_LESSONS_DIR}/GLOSSARY.md` | `${LESSONS_DIR}/ch01-GLOSSARY.md` | Frozen reference. Do not edit. |
| `${CH_01_LESSONS_DIR}/TESTING_STANDARDS.md` | `${LESSONS_DIR}/TESTING_STANDARDS.md` | Still correct. Add the three-tier naming convention and the maintenance/deletion rules as new sections; do not rewrite what is there. |
| `${CH_01_LESSONS_DIR}/COURSE_STANDARDS.md` | `${LESSONS_DIR}/COURSE_STANDARDS.md` | Retitle; rewrite only the dependency-direction section for the engine/`tools` split, and add the forbidden-import rule. |
| `${CHECKSTYLE_CONFIG}` | same path | Then add `ImportControl`. Rebuilding a ruleset from memory teaches nothing; extending one does. |
| `.editorconfig` | same path | |

### From this directory

| Source | Destination |
|---|---|
| `${DOC_SPEC}` | `${LESSONS_DIR}/SPEC.md` |
| `${DOC_GUIDE}` | `${LESSONS_DIR}/LEARNING_GUIDE.md` |
| `${DOC_REVIEW}` | `${LESSONS_DIR}/CHAPTER-1-REVIEW.md` |
| `${DOC_HANDOFF}` | `${SPEC_HISTORY_DIR}/` — provenance, not a lesson |
| `project.env` | repository root — re-point every path in it after the move |
| `${DOC_CHECKLIST}` | working document; discard once every box is ticked |
| `${DOC_INDEX}` | entry point for this directory; travels with it into `${SPEC_HISTORY_DIR}/` |
| this whole directory | `${SPEC_HISTORY_DIR}/` — provenance, read-only |

### Rebuilt by hand in Chapter 2, never copied

`pom.xml`, `.gitignore`, and the source trees. Copying these skips the chapter.

`${COMMIT_HOOK}` moved out of this list in turn 09 and is now copied: its difficulty is shell
idempotency, not anything this course teaches. `${DOC_CHECKLIST}` is the operational version of this
section.

---

## 6. The new repository's `.agents/` backbone

The tracker's backbone is a good template with one inversion: the tracker's `CONTEXT.md` governs a
**closed** repository under maintenance-only rules, whereas this one is **open and actively under
construction**. The rules are about restraint during teaching rather than restraint during
maintenance.

Carry over in spirit: three ownership surfaces with one-way dependency direction; no
provider-specific context files; one normative rule per authority with links rather than
duplication; the `C###` commit grammar; verification as named commands whose output is pasted rather
than summarised.

Add or invert:

- **The agent never writes implementation code.** The learner owns `src/main` and `src/test` in full.
- **The agent never writes test code**, at any gate, in any file, for any reason.
- **Example, never solution.** Demonstrations use unrelated subject matter.
- **The agent never authors a capstone before its chapter's lessons are complete**, and never fills
  in a `CAPSTONE.md` stub beyond its "Must be true" list.
- **Chapter expansion belongs to the learner.** The agent may review a drafted breakdown; it may not
  produce one.
- **When asked for a fix, refuse and restate the concept.** Standing rule, not capstone-only.
- Record each capstone's gap count in `JOURNAL.md`.

Suggested authority set:

```
.agents/
├─ README.md              backbone map
├─ CONTEXT.md             operating rules, ownership, verification, the do-not-write rules
├─ PROJECT.md             scope, architecture, definition of done, non-goals
├─ COURSE_MAINTENANCE.md  how a chapter moves between its four states, and who moves it
└─ notes/                 transient, ignored except durable records
```

---

## 7. Naming — decided

**`${PROJECT_NAME}`**, a genuine synonym for nonograms. Repository slug `${REPO_NAME}`, package
`${PACKAGE_ROOT}`, Maven coordinates `${GROUP_ID}` / `${ARTIFACT_ID}`. All four live in `project.env`
and are referenced from there rather than repeated in these documents.

`picross` was ruled out: it is Nintendo's branding for the genre.

Still open in `project.env`, marked `# REVIEW`: `COURSE_NAME` (the name for the whole course spanning
Chapter 1 and Chapters ${CHAPTER_FIRST}–${CHAPTER_LAST}), `COMMIT_GROUPS`, `COMMIT_AUTHOR_COURSE`,
`GLYPH_MODE`, `STATIC_HOST`, and the dependency versions inherited from Chapter 1.

---

## 8. Remaining integration with the Chapter 1 repository

Nothing here is committed. This directory is untracked in the Chapter 1 repository, and that
repository's `.agents/CONTEXT.md` explicitly forbids adding a successor roadmap to it.

**One pending change, blocked on `PROJECT_GH_REPO` existing:** a short note in
`${CH_01_LOCAL_REPO}/README.md` pointing at the successor project. It belongs on the line that
currently reads "The project is intentionally closed at the end of Chapter 1. More advanced
framework work belongs in a separate repository." — replacing the vague "a separate repository" with
`${PROJECT_NAME}` and `${PROJECT_GH_REPO}`. That is a one-line `COURSE(repo):` commit, made once the
handoff is finished and the new repository exists.

---

## 9. Change log

### turn 10 — usable by an agent as well as by hand

`${DOC_INDEX}` added as this directory's entry point: what each file is, the read order, where
everything lands, and a paste-ready prompt that scopes an agent to the mechanical unpack.

`${DOC_CHECKLIST}` gained an ownership split. Every section is tagged **Agent** or **Learner**, on
the basis of which tasks are lessons rather than which are hard. The prohibitions are stated once,
early, and repeated in the agent prompt: no Java, no tests, no `pom.xml`, no `.gitignore`, no
`ImportControl` module, no source trees, no commits, no `CAPSTONE.md` filled past its "Must be true"
list, no provider-specific authority file.

The reasoning is worth keeping explicit, because it is the whole thesis: Chapter 1 handed over a
published contract and a 39-test grader, and the learner shaped code to fit someone else's box. An
agent that helpfully scaffolds a `pom.xml` on day one reproduces that failure exactly. The bootstrap
order matters for the same reason — an unpacking agent is constrained only by its prompt until it
has written `.agents/CONTEXT.md`, so that file is the first task, not the last.

Nothing was rolled back. The four-state chapter model, the "Must be true" contracts, the
learner-writes-every-test rule, example-never-solution, and capstone-last all stand unchanged.

### turn 09 — transfer checklist

`${DOC_CHECKLIST}` added: the operational list of what must move into the new repository besides this
directory. Six files copied, three rebuilt by hand in Chapter 2, three created fresh.

Two corrections came out of writing it. The commit hook moved from "rebuild by hand" to "copy" —
Lesson 2.3 now rebuilds only the `.gitignore`. And `.agents/CONTEXT.md` is now called out as the
first file the new repository needs, because the learner's global agent instructions route every
agent to it for repository authority; until it exists, agents there run with no boundary at all.

Also verified this turn: `origin/main` and `main` in the Chapter 1 repository are both at `2043a77`,
so the closeout is published and `${CH_01_GH_REPO}` is a working answer key. The claim in that
repository's `PROJECT_CLOSEOUT.md` that remote publication awaits approval is stale.

### turn 08 — project environment extracted

`projectEnvrionment` renamed to `project.env` (typo fixed, made shell-sourceable) and promoted from
four variables to the single source of truth for every name, path, link, and locked stylistic choice
across these documents. Project named **${PROJECT_NAME}**; package root, Maven coordinates, and repo
slug follow from it.

Every reference in these documents now either points at a sibling `*-FINAL.md` in this directory or
resolves through `project.env`, so the folder can be pasted into the new repository and re-pointed in
one place. Entries are marked `# REVIEW` where they need a decision before the repository is created,
`# RECORD` where a named lesson makes the decision and the value is deliberately blank until then,
and `# LOCKED` where changing the value would invalidate existing tests.

Document versions bumped: spec 0.6 → 0.7, learning guide 0.5 → 0.6, Chapter 1 review 0.2 → 0.3,
handoff 0.2 → 0.3. No content changed beyond the variable extraction and the resolved naming
section.

### v0.5 → v0.6 (turn 07)

| Change | Reason |
|---|---|
| Chapters gained four explicit states; only the current one is expanded | Only the current chapter/lesson is set in stone |
| Signature `Contract` blocks → `Must be true` guarantees | Naming decisions belong to the learner; only forward-compatibility promises are published |
| Capstone authored after the chapter's lessons, not before | The observed Chapter 1 failure: shaping code to fit a published box |
| Gate 3 hidden tests → Gate 3 gap analysis | Test writing is exclusively the learner's |
| Standing rule 2 rewritten: "You write every test" | Same |
| New: "Barebones is correct at the start" | Boilerplate deserves boring tests; rigour arrives with behaviour |
| New: "Test maintenance is part of the work" | Rewriting and deleting tests is normal work, not regression |
| New: "Organising tests into files" | Requested: help organise test ideas into files effectively |
| New: "The ladder after Chapter 10" | Answers the browser-testing question |
| New Lesson 2.4 — the enforced package boundary | Answers the `java.io` question, and makes the rule fail the build |
| Old Ch2 lessons renumbered; failure catalogue grew to six entries | Boundary violation added as a failure mode worth recognising |
| Lesson 3.1 inverted: never derive a wire format from `ordinal()` | The old justification was factually wrong |
| Lesson 3.5 now includes revising the Lesson 3.4 test | First deliberate test-maintenance exercise |
| Chapters 5–14 reduced to one-line syllabus entries; Ch4 kept as a manifest | Nothing beyond the first two or three chapters should be more than a syllabus line |
| New Appendix E — the three tiers in code already written | Example, never solution |

### v0.4 → v0.5 (turn 06)

Chapters renumbered 0–12 → 2–14. Ch0 "Workshop Setup" became Ch2 "The Workshop, Rebuilt". Java 17 →
${JAVA_VERSION}. Package `nonogram` → `${PACKAGE_ROOT}`. Quality gates restored. `NOTES.md` →
`${JOURNAL_FILE}`. The test ladder promoted from a paragraph in `TESTING_STANDARDS.md` to the spine of
the course. Learning guide's stale "step 1–11" numbering, its dead `spec §10` reference, and its
beginner-calibrated pacing all corrected — and its instruction to ask an assistant for failing tests
inverted, since it directly contradicted the testing thesis.
