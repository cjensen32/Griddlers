# Provenance — the ten turns that produced this handoff

The `01/` through `10/` directories held the chat that produced the documents beside this file: ten turns, each carrying the learner's message, the response, and whatever document versions that turn revised. Ten drafts of the same five documents, 9,385 lines, of which every surviving decision is already in the `*-FINAL.md` files.

They were removed on 2026-09-18 and distilled into the table below. Recover any of them with `git show b681a82:.agents/archive/project-init-session/<turn>/<file>`; `git log --diff-filter=D --stat -- .agents/archive/project-init-session/` finds the commit that removed them.

This file is a record of how the handoff arrived at its shape. Like everything under `archive/`, it is dated evidence, never live authority — `.agents/CONTEXT.md` and `.agents/PROJECT.md` own the current statement of every decision below.

## The turns

| # | What was asked | What changed |
|---|---|---|
| 01 | A spec for a Nonogram game in Java, eventually a mobile-focused website or app for iOS and browser | `nonogram-spec-v1` — the first course outline |
| 02 | The goal is learning to code this rather than having agents do it; how should the workflow and toolchain adapt for an inexperienced developer with no app-store deployment experience? | No document. The answer set the premise everything after it rests on |
| 03 | Split the learning-focused context out of the spec, into its own file | `nonogram-learning-guide` created; `nonogram-spec-v2` |
| 04 | Terminal output first, because a visual reference is what makes each step understandable. Verification tests alongside each step's code. Restructure the spec as a course: chapters, lessons per concept, a capstone per chapter with an agent-managed test | `nonogram-learning-guide-v2`, `nonogram-spec-v3` — the chapter/lesson/capstone shape arrives |
| 05 | Keep the spec short: only Chapters 0 and 1 fully specified, the rest as end-goal concepts in a sentence or two | `nonogram-learning-guide-v3`, `nonogram-spec-v4` |
| 06 | **Recalibration against the finished Chapter 1**, conducted inside the closed-out tracker repository with the real tree to inspect. Continue as Chapters 2–14, one course across two repositories. Full toolchain carries over — Java 21, pinned Surefire, Checkstyle, Spotless, JaCoCo, the commit hook, an `.agents/` backbone. Treat Chapter 1 topics as review, not new content. The thesis: teach writing effective tests by hand — per file, per flow, end to end | `CHAPTER-1-REVIEW` and `HANDOFF` created; `nonogram-learning-guide-v4`, `nonogram-spec-v5`. The spec more than doubles, from 216 lines to 518 |
| 07 | **Loosen the front, sharpen the end.** Chapter 1's capstone existed before its lessons, so code got shaped to fit the capstone test — less ownership of testing, less ownership of code shape, more help asked for than needed. So: only the current chapter and lesson are set in stone; future ones are manifests carrying the contract that must hold, nothing more; the capstone is written *after* its lessons. Test writing is exclusively the student's. **Write an example, not a solution. Always.** | `CHAPTER-1-REVIEW-v2`, `HANDOFF-v2`, `nonogram-learning-guide-v5`, `nonogram-spec-v6` |
| 08 | Pull the project's definition variables out of the documents and into one environment file, so names, paths and links are defined once and reviewable in one place. Point every cross-reference at either a `*-FINAL.md` in this directory or at that file | `CHAPTER-1-REVIEW-v3`, `HANDOFF-v3`, `nonogram-learning-guide-v6`, `nonogram-spec-v7`. **`PROJECT_NAME="Griddlers"` decided this turn** |
| 09 | Can this directory be copied into a blank repository with all context intact? Tell me which files to transfer rather than shipping a starter kit — a short list that can be checked against what I already have beats a twenty-file list of things to implement | `TRANSFER-CHECKLIST` created, as Markdown, with every expected change written out inline. All four documents promoted to `-FINAL` |
| 10 | Make the directory usable by an agent as well as by hand, with the agent's scope limited to unpacking context, agent configs and lessons — no scaffolding, no code. Then point out what still needs answers | `README` created; the root `*-FINAL.md` files refreshed. This is the state the directory was copied over in |

## What survived, and where it went

| Document | Became |
|---|---|
| `nonogram-spec-FINAL.md` | `lessons/SYLLABUS.md`, and the chapter directories under `lessons/` |
| `nonogram-learning-guide-FINAL.md` | `.agents/reference/LEARNING_GUIDE.md` |
| `CHAPTER-1-REVIEW-FINAL.md` | `.agents/archive/chapter-01/CHAPTER-1-REVIEW.md` |
| `HANDOFF-FINAL.md` | The design reasoning behind `.agents/CONTEXT.md` and `.agents/PROJECT.md`; kept here as the record of *why* |
| `TRANSFER-CHECKLIST.md` | Executed; kept here as the record of *what moved* |
| `project.env` | `project.env` at the repository root — live configuration, not history |

The `${VARIABLE}` placeholders and the hard wrapping throughout these files are the format they were frozen in, not defects. `.agents/reference/COURSE_STANDARDS.md` exempts `.agents/archive/**` from the unwrapping rule, and `.agents/tools/unwrap_markdown.py` says the same in its docstring. Do not run it here.

## The three decisions worth carrying forward

Everything else in those ten turns was iteration on wording. These three changed the course:

1. **Turn 02 — the learner writes the code.** Every constraint in `.agents/CONTEXT.md` about what an agent may not touch descends from this one question.
2. **Turn 07 — the capstone comes last.** Chapter 1 failed by defining the target before the work, so the learner shaped code to fit a test someone else wrote. The manifest model, the "must be true" lists, and the rule that only the current lesson is fixed all exist to prevent a repeat.
3. **Turn 07 — example, never solution.** The single most-cited rule in the repository, and the reason a reference note that worked through the lesson's own classes would be the solution with a date on it.
