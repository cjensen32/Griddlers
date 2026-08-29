# Lesson 2.1 — definitions table, and an adaptive follow-up step

*Sourced from Chapter 2, Lesson 2.1, on 2026-08-28. Approved and landed as `C016`, after the learner's critique pass rewrote the follow-up step, the naming, and the memory decision. The `.agents/COURSE_MAINTENANCE.md` steps 3 and 5 described here are live; the authorship rule that followed is in `.agents/CONTEXT.md` § Commit messages.*

## Context

Lesson 2.1 (`lessons/ch02-the-workshop-rebuilt/01-the-pom.md`) is green on its two build boxes — `mvn compile` succeeds and `target/surefire-reports/com.connorjensen.griddlers.MainTest.txt` records `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`. The pop quiz is answered, and the answers show the recall is real but the vocabulary underneath it is fuzzy in specific, nameable places: phase versus goal, what `test` scope actually changes, and what an unpinned Surefire does.

Two things follow. The learner wants a place to record the definitions a lesson turns on, written by hand. And the lesson cycle currently ends the same way whether a submission was clean or shaky — so this plan adds one optional follow-up step, fitted to the review's outcome.

Decisions taken by the learner:

- The definitions table is learner-facing and learner-owned. An agent architects it; the learner fills every cell.
- Columns are `Lesson | Term | Scope | Definition`. `Lesson` and `Term` are seeded; **`Scope` and `Definition` stay blank** — the learner inferred `maven` for *phase vs goal* and `pom.xml` for `pluginManagement`, both correct, so deducing scope is theirs too.
- Reviews passively audit filled rows rather than waiting to be asked.
- Commit-message guidance: point at what exists (`.agents/CONTEXT.md` § Commit messages, plus `git log`). Do not write lesson 2.3 early.

## Changes

### 1. `lessons/JOURNAL.md` — add a `## Definitions` section

Insert between `## Stuck and unstuck` and `## Capstone gap counts`.

- Header row: `| Lesson | Term | Scope | Definition |`.
- Lesson tag is `2.1` — the identifier the repo already uses, in the lesson file's own H1 and the chapter README's `#` column. Not `ch2.01` or `ch02.01`; two spellings of one thing is a trip hazard, and `2.1` is what gets said out loud.
- Sort by lesson, then alphabetically within the lesson. Grouping by lesson is safe; grouping by *topic* is not, because a topic subheading hands over the `Scope` cell the learner is meant to deduce. No subheadings inside the table.
- A term that recurs in a later lesson keeps its original row. One row per term, tagged with the lesson that first required it.
- One short line above the table: rows are added per lesson, cells are filled by hand.
- Follow the Markdown rule in `.agents/reference/COURSE_STANDARDS.md`: one physical line per logical line, no hard wrapping. Pad table columns to align, matching the existing table style in this repo.

Seed rows, all tagged `2.1`, alphabetical:

`-Xlint`, aggregator artifact (`junit-jupiter`), coordinates, dependency scope, effective POM, goal, ignored vs untracked, JUnit Platform, Jupiter, lifecycle (default), lifecycle mapping, local repository, `maven.compiler.release`, `mvn clean`, `mvn install`, `mvn package`, `mvn verify`, packaging, phase, plugin, plugin execution, `<pluginManagement>`, `<plugins>`, POM, property / `${...}`, reproducible build, SNAPSHOT, super-POM, Surefire, Surefire default includes, `target/`, test classpath vs main classpath, transitive dependency, utility class.

Nothing else in `JOURNAL.md` is touched. The stuck/unstuck lines for this lesson get offered once the learner closes what 2.1 still has open, not written now.

### 2. `.agents/COURSE_MAINTENANCE.md` — two additions to the lesson cycle

**Step 3** already offers a `JOURNAL.md` line when an answer amounts to "this broke, this fixed it". Append one sentence: when the learner submits pop-quiz answers for review, also read the filled `Definitions` rows and flag a wrong scope or definition, so the table is checked without being asked about.

**Step 5** is where the agent approves a submission or returns it. Append a short paragraph, roughly:

> On a first submission only, close the review by offering one optional extra step, written into the lesson file as a checkbox tagged `(mastery)`, `(reinforce)`, or `(clarify)`. `(mastery)` when the lesson passed and the quiz held up: one task combining two things the lesson covered, harder than either alone. `(reinforce)` when it passed but an answer was weak: re-derive the weakest one. `(clarify)` when it did not pass: name the observation and the concept to revisit, never the file and never the fix. Judge "weak" from the review rather than from the stated confidence percentage — a confidently wrong answer is the case this exists for. The learner accepts or declines, and a declined step does not block green.

Three constraints that paragraph is carrying, and why:

- **Never the file and never the fix.** Step 5 already forbids returning a lesson "with the correction written out". A `(clarify)` step that says "check that Surefire is in `build.plugins`" is that correction with a checkbox around it, so the guard belongs inside the new rule, not near it.
- **Review outcome routes, not the stated percentage.** Lesson 2.1's Q4 was answered at 80% confidence and is roughly 20% right; keyed on the number, the worst answer on the sheet routes to `(mastery)`.
- **A declined step does not block green.** `COURSE_MAINTENANCE.md` defines green as the named test passing and the commit landing. Without this clause the two rules disagree about what closes a lesson.

Smallest edit that makes the behavior durable. Do not restructure the steps or the file. Do not restate the existing "push back on conflicting authority" rule — `CONTEXT.md` ("resolve the conflict explicitly") and `COURSE_MAINTENANCE.md` ("the smallest correction that resolves a demonstrated conflict") already carry it, and a third copy is the duplication the repo is trying to avoid.

### 3. No memory files

Both rules land in `.agents/COURSE_MAINTENANCE.md`, which `.agents/CONTEXT.md` names as an authority and which agents are already required to read before touching this repo. A memory would be a second copy of a repo-recorded rule. Dropping them also avoids two problems in the drafted versions: `automatic/feature/feedback` is not one of the four valid `type` values, and a `ch02.01-` filename prefix makes a standing course-wide agreement read as expired the moment Chapter 3 opens.

Tradeoff, stated plainly: a memory would load even in a session that never opens `.agents/`. Given the global instruction to read `.agents/CONTEXT.md` at the start of work in this repository, that gap is narrow enough not to justify the duplication.

## Explicitly not doing

- No edit to `src/`, `pom.xml`, or `.gitignore`. All three are learner-owned, and the 2.1 gaps found in review are the learner's to close.
- No `lessons/ch02-the-workshop-rebuilt/03-git-and-the-commit-grammar.md`. It stays a stub until 2.2 is green — two written lessons at a time.
- No definitions filled in, no scopes filled in, no lesson-2.1 answer written out in corrected form, and no `(mastery)` / `(reinforce)` / `(clarify)` step added to Lesson 2.1 itself as part of this change. The new rule fires at the next submission review, not retroactively.
- No `git add` or `git commit` without explicit approval, per the Chapter 2 bootstrap boundary.

## Verification

```sh
.agents/tools/unwrap_markdown.py --check lessons/JOURNAL.md .agents/COURSE_MAINTENANCE.md
.agents/tools/check_midsentence.py lessons/JOURNAL.md .agents/COURSE_MAINTENANCE.md
git diff --check
git status --short          # expect only the two .md files beyond the learner's own untracked work
```

Then read both files and confirm:

- `JOURNAL.md`: table renders, every `Scope` and `Definition` cell empty, `Lesson` reads `2.1` throughout, rows alphabetical, no topic subheadings.
- `COURSE_MAINTENANCE.md`: steps 3 and 5 each grew, the four-state model and step numbering are unchanged, and the new paragraph contradicts nothing in "Lesson states inside the open chapter".

---

## Outcome, recorded 2026-08-29

The plan executed as written. `C016` landed the definitions table and the two `COURSE_MAINTENANCE.md` additions under the `Course Author` identity; `C017` and `C018` followed with the authorship rule, this archive entry, and the learner's own Lesson 2.1 work.

What the plan did not anticipate, kept here because it is the useful part:

- The authorship convention came out of the first commit rather than out of the plan. `.agents/CONTEXT.md` § Commit messages now carries it, and the memory file the plan had already argued against was written and then deleted once the repository recorded the rule.
- The `(reinforce)` branch fired on a second submission rather than a first, because the learner resubmitted with revised answers. The rule as written says first submission only. That is a real edge the rule does not cover yet, and it is worth deciding deliberately rather than by precedent.
- The definitions table earned its place faster than expected. Two wrong rows, `packaging` and `dependency scope`, turned out to be the same gaps as two weak pop-quiz answers, which is the correlation the passive audit in step 3 exists to catch.
- Lesson 2.1 closed at roughly 3.8 hours, of which about ten minutes was typing code. The rest was the understanding, which is the ratio the chapter was designed around.

Steps 7 and 8 of the cycle - refitting Lesson 2.2 to what was actually built, then writing out the 2.3 stub - were not reached and remain open.
