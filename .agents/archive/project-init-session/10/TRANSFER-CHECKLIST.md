# Transfer Checklist — what to bring over besides this directory

Every path, name, and value below that comes from `project.env` names its variable inline. Current
values, so you can verify without opening it:

| Variable | Current value |
|---|---|
| `${PROJECT_NAME}` | `Griddlers` |
| `${REPO_NAME}` | `griddlers` |
| `${COURSE_NAME}` | `JAVA-foundations-to-deployment` |
| `${PACKAGE_ROOT}` | `com.connorjensen.griddlers` |
| `${PACKAGE_PATH}` | `com/connorjensen/griddlers` |
| `${ENGINE_PACKAGE}` | `com.connorjensen.griddlers` |
| `${TOOLS_PACKAGE}` | `com.connorjensen.griddlers.tools` |
| `${ENGINE_FORBIDDEN_IMPORTS}` | `java.io,java.nio.file,java.net` |
| `${CH_01_LOCAL_REPO}` | `~/repos/JAVA/job-application-tracker` |
| `${CH_01_GH_REPO}` | `https://github.com/cjensen32/job-application-tracker.git` |
| `${CH_01_LESSONS_DIR}` | `lessons/ch01-java-foundations` |
| `${PROJECT_LOCAL_REPO}` | `~/repos/JAVA/griddlers` |
| `${PROJECT_GH_REPO}` | *(empty — needs your answer)* |
| `${LESSONS_DIR}` | `lessons` |
| `${JOURNAL_FILE}` | `JOURNAL.md` |
| `${CHECKSTYLE_CONFIG}` | `config/checkstyle/checkstyle.xml` |
| `${COMMIT_HOOK}` | `.githooks/prepare-commit-msg` |
| `${SPEC_HISTORY_DIR}` | `.agents/archive/project-init-session` |

Left-hand paths are relative to `${CH_01_LOCAL_REPO}`; right-hand to `${PROJECT_LOCAL_REPO}`.

## Who does what

Every section below is tagged **Agent** or **Learner**. The split is not about difficulty — it is
about which tasks are lessons.

- **Agent** — mechanical. Copying files, applying edits that are written out verbatim below, moving
  directories, and creating governance documents. An agent may do all of it; so may you.
- **Learner** — a Chapter 2 lesson, or a commit in another repository. Yours alone.

**An agent must never**, at any point in this checklist: write or scaffold a Java file, a test file,
or `pom.xml`; create or edit `.gitignore`; add a module to `${CHECKSTYLE_CONFIG}`; create
`src/main`, `src/test`, or any package directory; run `git init`, `git add`, or `git commit`; fill
in a `CAPSTONE.md` beyond its "Must be true" list; or create `CLAUDE.md`, `AGENTS.md`, or `.claude/`
as repository authority. `README.md` in this directory has the paste-ready prompt that carries these
constraints.

---

## 1. Copy verbatim — frozen reference, not exercises  ·  **Agent**

### [x] `${CH_01_LESSONS_DIR}/GLOSSARY.md` → `${LESSONS_DIR}/ch01-GLOSSARY.md`

Fast recall for Lessons 0–8. No changes. Never edit it again.

---

### [x] `${CH_01_LESSONS_DIR}/TESTING_STANDARDS.md` → `${LESSONS_DIR}/TESTING_STANDARDS.md`

**Not finished if you only copied it.** Two sections to append, and one to replace.

**Append after `## Scope and edge cases`:**

```markdown
## Test tiers

- Write a tier 1 file test for every production class: one class in isolation, collaborators
  controlled, named `<ClassName>Test` and mirroring the production package.
- Write a tier 2 flow test for every user-visible flow: real collaborators wired the way `main`
  wires them, one complete path, named for the behavior — `SolveFlowTest`, not `SolverAndGridTest`.
- Write a tier 3 end-to-end test for every runnable `main`: a real JVM through `ProcessBuilder`,
  asserting the exit code and exact stdout.
- Keep the proportion honest: dozens of tier 1, a handful of tier 2 per chapter, one or two tier 3
  and no more.
- Choose a tier by the bug it would catch, not by convenience. A wrong rendered character is tier 1;
  correct parts wired together wrongly is tier 2; correct in tests but wrong in a terminal is tier 3.
- Keep shared test infrastructure in its own file and do not let it assert anything itself.
- Treat a tier 1 file past roughly 200 lines as evidence the production class has two
  responsibilities, not as a reason to split the test file.

## Maintenance

- Expect to revisit earlier test files to rewrite, extend, restructure, or delete as behavior grows.
  Test files are not append-only.
- Let a test against boilerplate stay boring. Rigour arrives with behavior, not in advance of it.
- Do not write speculative tests for behavior that does not exist yet.
- Delete a test when it asserts something no longer true, when a later test covers it better, when
  it is pinned to an implementation detail you have replaced, or when it exists only to move a
  coverage number.
- Treat one production-line change turning six tests red — five of them testing the same thing — as
  maintenance already overdue.
```

**Replace the whole `## Reference examples` section.** As copied it points at
`capstone/Chapter01CapstoneTest.java`, its synchronized executable copy, and
`07-testing-console-applications.md`. None of those exist here, and the first is the agent-authored
grader this course exists to abolish. Replace with:

```markdown
## Reference examples

Read these in `${CH_01_LOCAL_REPO}`, under `src/test/java/com/connorjensen/jobtracker/`. They are
examples of shape, on code unrelated to this project.

- `ApplicationTest.java` — tier 1. One class, no collaborators, values in and values out.
- `cli/ConsoleSessionTest.java` — tier 2. Real objects wired together, streams injected, one
  complete path asserted.
- `MainProcessTest.java` — tier 3. `ProcessBuilder`, stdin closed, a timeout, explicit UTF-8, exit
  code and exact stdout.

No agent-authored grader exists in this project and none will. Every test here is learner-written;
the capstone grades the suite rather than supplying one.
```

---

### [ ] `${CH_01_LESSONS_DIR}/COURSE_STANDARDS.md` → `${LESSONS_DIR}/COURSE_STANDARDS.md`

Six lines change, not two. Everything else is correct as written.

**Line 1 — title**

```diff
-# Chapter 1 Course Standards
+# ${PROJECT_NAME} Course Standards
```

**Line 10 — package root**

```diff
-- Keep package declarations aligned with paths beneath `com.connorjensen.jobtracker`.
+- Keep package declarations aligned with paths beneath `${PACKAGE_ROOT}`.
```

**Lines 15–17 — replace all three dependency-direction bullets**

```diff
-- Treat `Main` as the composition root: it creates concrete dependencies and starts the application.
-- Let `cli` depend on `service`, `service` depend on `repository` and `model`, and repository implementations depend on their contract and model.
-- Do not let `model` depend on CLI, service, repository, build, or framework code.
+- Treat each runnable class in `${TOOLS_PACKAGE}` as a composition root: it creates concrete dependencies and starts one program.
+- Let `${TOOLS_PACKAGE}` depend on `${ENGINE_PACKAGE}`, and never the reverse.
+- Do not let the engine import `${ENGINE_FORBIDDEN_IMPORTS}`, or touch `System.out`, `System.err`, or `System.in`.
+- Keep the engine deterministic: no clock, no unseeded randomness, no filesystem, no network.
```

**Line 29 — drop the chapter reference**

```diff
-- Follow the [Chapter 1 test-writing standards](TESTING_STANDARDS.md).
+- Follow the [test-writing standards](TESTING_STANDARDS.md), including the three tiers and the maintenance rules.
```

**Line 31 — generalize off the console tracker**

```diff
-- Inject `Scanner` and `PrintStream` for deterministic console tests; use a subprocess for the real `Main` or EOF lifecycle.
+- Inject streams and other boundaries for deterministic in-process tests; use a subprocess for a real `main`, its exit code, or an EOF lifecycle.
```

**Line 45 — replace the closing Boundary paragraph**

```diff
-Chapter 1 owns the complete plain-Java CRUD console experience. Framework, database, browser, and automation implementations are outside this repository; references to them explain the value of the boundaries built here.
+This repository owns the ${PROJECT_NAME} engine, its terminal tools, and its browser client. Chapter 1's console tracker is a separate, closed repository; references to it are review material and worked examples, never a build or runtime dependency.
```

---

### [ ] `${CHECKSTYLE_CONFIG}` → same path

31 modules. Rebuilding a ruleset from memory teaches nothing; extending one does.

**No changes at transfer time.** Adding the `ImportControl` module that enforces
`${ENGINE_FORBIDDEN_IMPORTS}` is Lesson 2.4's exercise, and its verification is that a deliberately
bad import in an engine class fails `mvn verify` while the same import in `${TOOLS_PACKAGE}` does
not. Writing that config here would delete the lesson, so it is deliberately absent.

---

### [ ] `.editorconfig` → same path

Five lines. No changes. Nothing to learn by retyping it.

---

### [ ] `${COMMIT_HOOK}` → same path  ·  copy **Agent**, read it **Learner**

Copy rather than rebuild. Its difficulty is shell idempotency, not anything this course teaches: the
counter is derived from HEAD's own subject so no stored state can drift, merge and squash messages
are left alone, and an already-numbered subject is never re-bumped so amend and rebase do not
double-count.

**No changes to the file.** Enable it once per clone:

```bash
git config core.hooksPath .githooks
```

Read it until you can explain those three behaviors, then move on. Lesson 2.3 asks for exactly that.

---

## 2. Do not copy — Chapter 2 rebuilds these by hand  ·  **Learner**

| File | Lesson | Why |
|---|---|---|
| `pom.xml` | 2.1 | Write it blind, then diff against Chapter 1's and account for every difference |
| `.gitignore` | 2.3 | Has real decisions in it beyond `target/`; re-derive them |
| `src/main`, `src/test` trees | 2.4 | The two packages get created empty, as a boundary exercise |

Copying any of these skips the chapter, which is the entire point of the chapter.

---

## 3. Move this directory  ·  **Agent**

### [ ] `project.env` → repository root of `${PROJECT_LOCAL_REPO}`

Live config, not history — it does not belong in `${SPEC_HISTORY_DIR}` with everything else.

After the move: re-point `${CH_01_LOCAL_REPO}` if the tracker lives elsewhere, set
`${PROJECT_GH_REPO}` and `${PROJECT_LOCAL_REPO}`, and work through every `# REVIEW` entry. Leave
every `# RECORD` entry blank — those are filled by the lesson that makes the decision.

An agent may move the file and report which entries are still `# REVIEW`. Deciding them is
**Learner** — they are stylistic choices and links, which is the whole reason they were extracted.

### [ ] Everything else here → `${SPEC_HISTORY_DIR}/`

Read-only provenance. Numbered directories `01`–`09` are the chat history; the `*-FINAL.md` files
are the live documents.

Then promote the three live documents out of history:

| From | To |
|---|---|
| `nonogram-spec-FINAL.md` | `${LESSONS_DIR}/SPEC.md` |
| `nonogram-learning-guide-FINAL.md` | `${LESSONS_DIR}/LEARNING_GUIDE.md` |
| `CHAPTER-1-REVIEW-FINAL.md` | `${LESSONS_DIR}/CHAPTER-1-REVIEW.md` |

`HANDOFF-FINAL.md`, `${DOC_INDEX}`, and this checklist stay in `${SPEC_HISTORY_DIR}`. Discard this
checklist once every box is ticked; keep the other two as the record of why the course is shaped the
way it is.

---

## 4. Create fresh — before any lesson work  ·  **Agent**

### [ ] `.agents/CONTEXT.md` — first, before anything else

Your global agent instructions route every agent to `.agents/CONTEXT.md` for repository authority.
Until it exists, agents in the new repo run with no boundary and no rules at all — which is why it is
the first task in this section and not the last. An agent doing the unpack is constrained only by the
prompt it was given until it has written this file; writing it is what makes the constraints durable.

`/init` will infer layout, build, and conventions. It cannot infer the four below, because nothing
in an empty repository implies them. Paste them in:

```markdown
## Agent constraints

The learner owns `src/main` and `src/test` in full.

- Never write implementation code, and never write test code — at any gate, in any file, for any
  reason. Naming a gap in the test suite is the help being asked for; filling it is not.
- Example, never solution. When a concept needs demonstrating, demonstrate it on unrelated subject
  matter. Never on the class or test the learner is about to write.
- Never author a capstone before its chapter's lessons are complete, and never fill in a
  `CAPSTONE.md` stub beyond its "Must be true" list. A target published in advance becomes a box the
  learner shapes code to fit, which is the specific failure this course is structured to prevent.
- Never create `CLAUDE.md`, `AGENTS.md`, `.claude/`, `.codex/`, `.opencode/`, or any other
  provider-specific file as repository authority. This file is the only authority.

When the learner asks for a fix, refuse and restate the concept.
```

`HANDOFF-FINAL.md` section 6 has the rest: the remaining `.agents` documents, what each owns, and
which Chapter 1 conventions carry over versus invert.

### [ ] `${JOURNAL_FILE}`

Two jobs, so give it two sections up front:

```markdown
# Journal

## Stuck and unstuck

One line each time: what broke, what fixed it.

## Capstone gap counts

| Chapter | Gaps found at Gate 3 | Notes |
|---|---|---|
```

The gap count is the course's feedback signal — a direct measurement of what your own test design
missed. If Chapter 8's count matches Chapter 3's, stop and ask what has not changed.

### [ ] `README.md`

Neutral entry point across the three surfaces: code and build, `${LESSONS_DIR}/`, and `.agents/`.
Model it on the tracker's root `README.md`, which does exactly this in 39 lines and owns no rule of
its own.

---

## 5. Back in the Chapter 1 repository — once the new one exists  ·  **Learner**

Two edits and a commit in a repository outside this one. Not an agent's to make.

### [ ] `README.md`, line 5

```diff
-The project is intentionally closed at the end of Chapter 1. More advanced framework work belongs in a separate repository.
+The project is intentionally closed at the end of Chapter 1. The course continues in ${PROJECT_NAME} (${PROJECT_GH_REPO}), which covers Chapters 2–14.
```

One-line `COURSE(repo):` commit. This is the only change the Chapter 1 repository ever receives from
this project.

### [ ] `.agents/notes/PROJECT_CLOSEOUT.md`, line 3

Stale — `origin/main` and `main` are both at `2043a77`, so publication already happened.

```diff
-Status: candidate verified; canonical `main` and remote publication await approval.
+Status: complete. Canonical `main` published at `2043a77`.
```

---

## 6. Verify  ·  **Agent** runs them, **Learner** owns the first

- [ ] `${CH_01_GH_REPO}` is reachable **without your credentials**. If it is private, an agent in the
      new repo cannot open the answer key that `CHAPTER-1-REVIEW.md` and the spec's Appendix E both
      depend on. Either make it public or accept that the answer key is local-only.

- [ ] No dangling variables after the move:

      ```bash
      source ./project.env
      grep -rohE '\$\{[A-Z0-9_]+\}' "$LESSONS_DIR" "$SPEC_HISTORY_DIR" | sort -u \
        | tr -d '${}' | while read -r v; do
            grep -qE "^$v=" project.env || echo "MISSING: $v"
          done
      ```

- [ ] Chapter 2's own gates close this out: `mvn verify` runs tests, Checkstyle, Spotless, and
      JaCoCo; a deliberately failing test fails the build; and a forbidden import in the engine fails
      the build while the same import in `${TOOLS_PACKAGE}` does not.

---

**Six files copied, three rebuilt by hand, three created fresh.**
