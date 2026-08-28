# Handoff — Breaking the Nonogram Course Out Into Its Own Repository

**Source of this document:** turn 06 of `Nonogram-Spec-Creation-Chat`, conducted inside the
closed-out `job-application-tracker` repository with the full Chapter 1 tree available for
inspection. Turns 01–05 were conducted without it, which is why v0.4 mis-estimated what was already
known.

**Status of the v0.4 FINAL files:** superseded, not deleted. They remain the frozen endpoint of the
web chat. `nonogram-spec-v5.md` and `nonogram-learning-guide-v4.md` are the current artifacts.

---

## 1. What changed from v0.4, and why

### Structural

| Change                                             | Reason                                                                                                                                              |
|----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Chapters renumbered `0–12` → `2–14`                | One course across two repositories. "Chapter 1" now unambiguously means the tracker, and the review chapter sits immediately after what it reviews. |
| Ch0 "Workshop Setup" → Ch2 "The Workshop, Rebuilt" | The learner is rebuilding the toolchain by hand, from memory, rather than following a setup walkthrough. The chapter survives; its purpose inverts. |
| Three standing rules → four                        | The test ladder is promoted to a standing rule, per the turn-06 thesis.                                                                             |
| New section: "Where this course picks up"          | Nothing in v0.4 acknowledged that Chapter 1 existed.                                                                                                |
| New section: "The test ladder"                     | The three tiers were one paragraph in the tracker's `TESTING_STANDARDS.md`; they are now the spine of every chapter.                                |
| New Appendix D: Chapter 1 review map               | Per-lesson review/new labelling, so no lesson is silently re-taught.                                                                                |
| Gate 2 "Your tests" → "The ladder audit"           | A gate that only checks `mvn test` is green cannot detect a missing tier.                                                                           |
| Gate 3 gains a recorded failure count              | It is the only direct measurement of what the learner's own test design missed. Tracking it across chapters is the course's feedback signal.        |

### Corrections

| v0.4                                                               | v0.5                                                                                                        | Why                                                                                                       |
|--------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| Java 17                                                            | Java 21                                                                                                     | Chapter 1 shipped on 21 (`maven.compiler.release`). Downgrading is a regression.                          |
| package `nonogram`                                                 | `com.connorjensen.nonogram`                                                                                 | The tracker's `COURSE_STANDARDS.md` requires reverse-domain packages aligned with paths.                  |
| No quality gates                                                   | Checkstyle, Spotless, JaCoCo, pinned Surefire                                                               | Chapter 1 ended with all four mandatory. Silently dropping them is the single largest regression in v0.4. |
| `NOTES.md` as the stuck/unstuck log                                | `JOURNAL.md`                                                                                                | The tracker already uses `NOTES.md` for Java version history. One name, two meanings, one course.         |
| Test paths `src/test/java/nonogram/`                               | `src/test/java/com/connorjensen/nonogram/`                                                                  | Follows the package correction.                                                                           |
| Ch0 lesson 0.2: "write `SetupTest` asserting `2 + 2 == 4`"         | Ch2 lesson 2.4: three tests at three tiers plus `ProcessProbe`                                              | A tautology test teaches nothing to someone who has already shipped 49.                                   |
| Ch0 lesson 0.4: "deliberately break the test and read the failure" | Ch2 lesson 2.5: a five-entry failure catalogue across JUnit, Checkstyle, Spotless, compile, and silent skip | The single-failure version is Chapter-1 material; the catalogue is not.                                   |

### Bloat and stale content removed

- **Learning guide §4 and §5 used a "step 1–11" numbering** that had not matched the spec since turn
  04 converted it to chapters. Every cross-reference was off by one or more. Renumbered to chapters.
- **Learning guide §7 referenced "spec §10."** The spec has no numbered sections. Now points to
  Chapter 13 and Appendix C.
- **Learning guide §6 said "at your level"** and sized the early steps for a beginner. Recalibrated
  to someone who has finished Chapter 1.
- **Learning guide §2 said to ask an assistant for failing tests** — "Huge leverage, gives away
  nothing." This directly contradicts the turn-06 thesis. It was true when the goal was a working
  app; it is false when the goal includes learning to test. Now inverted, with Gate 3 fenced as the
  single exception. **This was the sharpest conflict in the v0.4 pair.**
- **Learning guide §9's completion criteria** carried a personal detail; generalised to "someone who
  has never seen the spec."
- Spec's Chapter 1 lessons 1.1, 1.3, 1.4, and 1.6 explained enums, constructors, guards, and pure
  renderers from scratch. Compressed to review with a Chapter 1 callback, keeping full rigour on
  their verification tests.

---

## 2. What to copy into the new repository

### Verbatim, from `job-application-tracker/`

| Source                                               | Destination                    | Notes                                                                                          |
|------------------------------------------------------|--------------------------------|------------------------------------------------------------------------------------------------|
| `lessons/ch01-java-foundations/GLOSSARY.md`          | `lessons/ch01-GLOSSARY.md`     | Frozen reference. Do not edit.                                                                 |
| `lessons/ch01-java-foundations/TESTING_STANDARDS.md` | `lessons/TESTING_STANDARDS.md` | Still correct. Add the three-tier naming convention as a new section rather than rewriting it. |
| `lessons/ch01-java-foundations/COURSE_STANDARDS.md`  | `lessons/COURSE_STANDARDS.md`  | Retitle; rewrite only the dependency-direction section for `nonogram` / `nonogram.tools`.      |
| `config/checkstyle/checkstyle.xml`                   | same path                      | Rebuilding a ruleset from memory teaches nothing.                                              |
| `.editorconfig`                                      | same path                      |                                                                                                |

### From this directory

| Source                                        | Destination                                  |
|-----------------------------------------------|----------------------------------------------|
| `06/nonogram-spec-v5.md`                      | `lessons/SPEC.md`                            |
| `06/nonogram-learning-guide-v4.md`            | `lessons/LEARNING_GUIDE.md`                  |
| `06/CHAPTER-1-REVIEW.md`                      | `lessons/CHAPTER-1-REVIEW.md`                |
| the whole `Nonogram-Spec-Creation-Chat/` tree | `docs/spec-history/` — provenance, read-only |

### Rebuilt by hand in Chapter 2, never copied

`pom.xml`, `.gitignore`, `.githooks/prepare-commit-msg`, and the source trees. Copying these skips
the chapter.

---

## 3. What the new repository's `.agents/` backbone should say

The tracker's backbone is a good template with one inversion: the tracker's `CONTEXT.md` describes a
**closed** repository under maintenance-only rules. The nonogram repository is **open and actively
under construction**, so the corresponding rules are about restraint during teaching rather than
restraint during maintenance.

Carry over unchanged in spirit:

- Three ownership surfaces (code/build, lessons, agent backbone) with one-way dependency direction.
- No provider-specific context files; `.agents/CONTEXT.md` is the single repository authority.
- One normative rule lives in exactly one authority; everything else links to it.
- Commit grammar with the `C###` hook.
- Verification as named commands whose output is pasted, not summarised.

Invert or add:

- **The agent must not write implementation code, ever.** The tracker permitted agents to maintain
  test infrastructure; here, the learner owns `src/main` *and* `src/test` in full.
- **The agent must not write tests outside Gate 3**, and Gate 3 tests go to a named capstone file
  that is clearly marked as agent-authored.
- **When the learner asks for a fix, refuse and restate the concept.** This is a standing rule, not
  a capstone-only rule.
- **Chapter expansion is the learner's job.** The agent may review a chapter breakdown the learner
  drafts; it may not draft one.
- Record each capstone's Gate 3 failure count in `JOURNAL.md`.

Suggested authority set, mirroring the tracker's:

```
.agents/
├─ README.md              backbone map
├─ CONTEXT.md             agent operating rules, ownership, verification, the do-not-write rules
├─ PROJECT.md             scope, architecture, definition of done, non-goals
├─ COURSE_MAINTENANCE.md  how a chapter gets expanded, and by whom
└─ notes/                 transient, ignored except durable records
```

---

## 4. Open items for the next session

1. **`COURSE_STANDARDS.md` dependency direction** needs rewriting for this project's two layers
   (`nonogram` engine, `nonogram.tools` interface). The rule "engine must not import `java.io`" is
   stronger than anything Chapter 1 enforced and deserves to be stated as a Checkstyle rule if one
   can express it.
2. **Chapters 10–13 have no test-ladder equivalent yet.** The spec flags this as part of expanding
   Chapter 10, but it is worth a decision before then: the browser has no JUnit, and "the ladder
   changes shape" is currently a promise rather than a plan.
3. **`Workshop.banner()` in the Chapter 2 contract is deliberately trivial.** If it feels too thin
   to justify three tests, the alternative is to move `CellState` and its test forward into Chapter
   2 and let the ladder be demonstrated on real code. That trades a cleaner Chapter 3 for a less
   artificial Chapter 2.
4. **Nothing here has been committed.** `Nonogram-Spec-Creation-Chat/` is untracked in the tracker
   repository, and the tracker's `CONTEXT.md` explicitly forbids adding a successor roadmap to it.
   These files are staged for extraction, not for commit here.
