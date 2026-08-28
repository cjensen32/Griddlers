# Handoff — Breaking the Nonogram Course Out Into Its Own Repository

**Version:** 0.2
**Source:** turns 06–07 of `Nonogram-Spec-Creation-Chat`, conducted inside the closed-out
`job-application-tracker` repository with the full Chapter 1 tree available for inspection. Turns
01–05 were conducted without it, which is why v0.4 mis-estimated what was already known.

**Current artifacts:** `nonogram-spec-v6.md`, `nonogram-learning-guide-v5.md`,
`CHAPTER-1-REVIEW-v2.md`. Everything earlier is superseded but retained as provenance.

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

The tracker's `checkstyle.xml` already runs `TreeWalker` import modules (`AvoidStarImport`,
`UnusedImports`, `CustomImportOrder`, `RedundantImport`), so Checkstyle's `ImportControl` module
drops straight in. Configure it to fail the build when anything in the engine package imports
`java.io`, `java.nio.file`, or `java.net`. A boundary a human has to remember is a boundary that
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

### Verbatim, from `job-application-tracker/`

| Source | Destination | Notes |
|---|---|---|
| `lessons/ch01-java-foundations/GLOSSARY.md` | `lessons/ch01-GLOSSARY.md` | Frozen reference. Do not edit. |
| `lessons/ch01-java-foundations/TESTING_STANDARDS.md` | `lessons/TESTING_STANDARDS.md` | Still correct. Add the three-tier naming convention and the maintenance/deletion rules as new sections; do not rewrite what is there. |
| `lessons/ch01-java-foundations/COURSE_STANDARDS.md` | `lessons/COURSE_STANDARDS.md` | Retitle; rewrite only the dependency-direction section for the engine/`tools` split, and add the forbidden-import rule. |
| `config/checkstyle/checkstyle.xml` | same path | Then add `ImportControl`. Rebuilding a ruleset from memory teaches nothing; extending one does. |
| `.editorconfig` | same path | |

### From this directory

| Source | Destination |
|---|---|
| `nonogram-spec-FINAL.md` | `lessons/SPEC.md` |
| `nonogram-learning-guide-FINAL.md` | `lessons/LEARNING_GUIDE.md` |
| `CHAPTER-1-REVIEW-FINAL.md` | `lessons/CHAPTER-1-REVIEW.md` |
| the whole `Nonogram-Spec-Creation-Chat/` tree | `docs/spec-history/` — provenance, read-only |

### Rebuilt by hand in Chapter 2, never copied

`pom.xml`, `.gitignore`, `.githooks/prepare-commit-msg`, and the source trees. Copying these skips
the chapter.

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

## 7. Naming the project

Repository and package name, and eventually the name on a home screen.

| Name | Why | Against |
|---|---|---|
| **`certainty`** | The line solver's entire job is finding the cells that are *certain* — and so is the test suite's. The double meaning fits a course whose thesis is testing. Reads well as an app name. | Abstract; does not say "puzzle" to a stranger |
| `griddler` | A genuine synonym for nonogram. Short, concrete, and reads as both the puzzle and the person building it | Slightly whimsical |
| `hanjie` | The British name for nonograms. Short, distinctive, no trademark exposure | Opaque unless you already know it |
| `crosshatch` | Evokes filled cells and the mark glyph; pleasant to say | No connection to the puzzle for most people |
| `nonogram-workshop` | Descriptive, ties to Chapter 2 | Long, and dates itself to one chapter |

Avoid `picross` — it is Nintendo's branding for the genre.

Recommendation: **`certainty`** if you want the name to mean something about how you are building it,
**`griddler`** if you want it to mean something about what it is. Package would be
`com.connorjensen.certainty` or `com.connorjensen.griddler`.

---

## 8. Remaining integration with `job-application-tracker`

Nothing here is committed. `Nonogram-Spec-Creation-Chat/` is untracked, and the tracker's
`.agents/CONTEXT.md` explicitly forbids adding a successor roadmap to it.

**One pending change, blocked on the project name:** a short note in the tracker's root `README.md`
pointing at the successor project. It belongs on the line that currently reads "The project is
intentionally closed at the end of Chapter 1. More advanced framework work belongs in a separate
repository." — replacing the vague "a separate repository" with the actual name and link. That is a
one-line `COURSE(repo):` commit, made once the handoff is finished and the new repository exists.

---

## 9. Change log

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
21. Package `nonogram` → `com.connorjensen.nonogram`. Quality gates restored. `NOTES.md` →
`JOURNAL.md`. The test ladder promoted from a paragraph in `TESTING_STANDARDS.md` to the spine of
the course. Learning guide's stale "step 1–11" numbering, its dead `spec §10` reference, and its
beginner-calibrated pacing all corrected — and its instruction to ask an assistant for failing tests
inverted, since it directly contradicted the testing thesis.
