# Consolidate `resources/l03/`, promote the durable technique, distil the init-session archive

Dated 2026-09-18. Approved by the learner across two clarification rounds before any file changed. Written after execution; the decisions and the outcome are both recorded here.

## Context

Lesson 2.3 is `current`. `resources/l03/` held four dated reference notes — 2026-09-12, -13, -14 and -16, 1,545 lines — written across the lesson and never retired. Most of their content was perishable status: defect lists, coverage gaps and worklists the checkout had since overtaken. A sitting that opened the directory had to read four notes and diff each against the code to work out what was actually left, which is the opposite of what a reference note is for.

Separately, `.agents/archive/project-init-session/` held 48 tracked files and 10,243 lines, of which ten numbered turn directories were successive drafts of the same five documents. The learner asked for the same treatment there, excluding `plans/` and the turning-point documents.

## State of the checkout, measured this session

Recorded because the new guide's credibility rests on it, and because it corrects a claim in the log.

- `mvn -B verify -Dspotless.check.skip=true -Dcheckstyle.skip=true` — 126 tests, 0 failures, 0 errors, 0 skips, `BUILD SUCCESS`. JaCoCo `LINE` and `BRANCH` both clear `0.9`.
- `mvn -B verify` — **fails**. `GriddlerEngineTest.java:9` has a static import out of lexicographic order; Spotless rejects it in `validate` and Checkstyle's `CustomImportOrder` rejects the same line again. `git status` was clean, so HEAD is red, and C052's "green pipeline" no longer holds.
- Two coverage gaps remain, both confirmed against `target/jacoco/jacoco.xml`: `Griddler:23-24`, where the in-loop null-row guard is unreachable because the only null-row test puts the null first; and `CLIParser:39`, where `size < 1` is reachable only through `-s 0` because the `startsWith("-")` guard rejects `-s -1` first.

Nearly everything the 09-14 and 09-16 notes raised has landed: seeded randomness through a `Random` parameter, `clues()`/`deriveClueList`, a single render path, `measure` and `bottomAlign` as separately tested helpers, a rectangular `Griddler`, `Gutter`'s defensive copy, `TerminalGriddler.write`, and the `-r` flag-order bug.

## Decisions taken

| Question | Answer | Why |
|---|---|---|
| Where the durable technique goes | Promoted out of `l03/`, split two ways | It is neither lesson-scoped nor perishable; burying it in a 2.3 directory meant deleting it or keeping four notes alive |
| Split or single file | Two — `test-shapes.md` and `changing-code.md` | They are reached for at different moments: one when writing a test, one when moving code |
| The single `l03/` filename | `the-refit-handoff.md` | Continuous with the note it grew from; the learner chose it over `completing-2.3.md` and `README.md` |
| The three superseded notes | `git rm` | Git history is the archive; a kept-but-stale note is worse than none |
| Archive scope | `project-init-session/` distilled; `plans/` and `chapter-01/` untouched | The learner excluded plans and turning points by name |
| Whether archived files were edited | No | `.agents/README.md` and `unwrap_markdown.py` both name the archive a frozen record. Files were moved and removed; none was rewritten |

## What changed

| Path | Change |
|---|---|
| `lessons/ch02-the-workshop-rebuilt/resources/l03/the-refit-handoff.md` | Renamed from `2026-09-14-the-refit-handoff.md` and rewritten whole. Ten sections: status, the one line blocking green, three ticked boxes that are no longer true, the five open close-out questions, open items per surface, the missing tier 2 test, what 2.4 needs, the journal's RLE row and the scope drift, a suggested order, and a commit guide |
| `lessons/ch02-the-workshop-rebuilt/resources/l03/2026-09-12`, `-09-13`, `-09-16` | Removed |
| `lessons/ch02-the-workshop-rebuilt/resources/test-shapes.md` | New. Distilled from the 09-12 note: the completeness rule, the four type shapes, change-detection tables, public field versus accessor, whether a payload belongs to its layer, symptoms of an unfinished test file, and the JUnit 5 table. Keeps the invented Clock Project domain |
| `lessons/ch02-the-workshop-rebuilt/resources/changing-code.md` | New. Distilled from 09-16 §2/§4/§5 and 09-13 §5/§6: structure xor behaviour, reading a failure by its shape, what makes an extracted function worth extracting, naming, the write seam, and what a composition root is allowed to be. Keeps the invented platform-board domain |
| `lessons/ch02-the-workshop-rebuilt/resources/README.md` | The "reference notes" section replaced by one section for the standing guide and one for the two unscoped reference files |
| `lessons/ch02-the-workshop-rebuilt/03-the-three-packages.md` | "Reach for" table now names the three reference files instead of the bare directory. The Part 2 sentence citing the deleted 09-13 note §4 rewritten — that question is answered and item 7.2 is now honestly true |
| `.agents/archive/project-init-session/` | `10/`'s three `*-FINAL.md` documents moved up to the top level, which makes the existing README's "What's here" table true without editing it. Turn directories `01/`–`10/` removed |
| `.agents/archive/project-init-session/PROVENANCE.md` | New. One row per turn — what was asked, what changed — plus where each FINAL document ended up and the three decisions that shaped the course |
| `.agents/README.md` | The `archive/` paragraph gained a clause separating "not revised" (content, still forbidden) from a learner-approved distillation (size, permitted with a dated plan) |

## Findings worth keeping

**A lesson's notes rot faster than its lesson.** Four notes written over five days, and by day six the majority of each was wrong — not misleading about intent, simply overtaken. The two that survived consolidation are the two that never mentioned a Griddlers class. Perishability tracks specificity: a note about this repository's state has a half-life of days, and a note about how to shape a test does not.

**The repository's own rule caught the wrong instinct.** The first read of `project-init-session/` flagged its `${VARIABLE}` placeholders and hard wrapping as defects, since `.agents/CONTEXT.md` requires an unresolved-variable audit and forbids hard wrapping. Both exemptions are written down — `COURSE_STANDARDS.md` exempts `.agents/archive/**`, and `unwrap_markdown.py`'s docstring says "do not run this over `.agents/archive/**`". Reading the tool before running it is what stopped a 48-file reformat of a frozen record.

**An index that promises what is not there.** `project-init-session/README.md` listed four `*-FINAL.md` documents at its top level; three existed only inside `09/` and `10/`. The fix was to move the files rather than edit the index, which was both the smaller change and the one the frozen-record rule allowed.

**HEAD was red and the log said otherwise.** C052 is titled "Placeholder; green pipeline". One import line has failed `mvn -B verify` since then. A commit subject asserting a build state is a claim that goes stale silently; the guide now opens with the command that checks it.

## Verification

- `mvn -B verify` — still red on `GriddlerEngineTest.java:9`, unchanged by this session. Nothing here touches `src/`.
- `mvn -B verify -Dspotless.check.skip=true -Dcheckstyle.skip=true` — 126 tests, `BUILD SUCCESS`.
- `unwrap_markdown.py --check` and `check_midsentence.py` over the four new and edited Markdown files outside the archive — clean.
- `grep -rn "2026-09-1[2346]"` across the tree — no dangling reference to a deleted note.
- `git diff --check` — clean.

## Scope not taken

`.agents/archive/session-artifacts/` is untracked and therefore not on the index the learner named; its eight files were left alone. One of them, `2026-09-12-single-type-test-shapes-note.md`, records the authoring of a note this session deleted, and is now the only surviving pointer to that fact outside `git log`.
