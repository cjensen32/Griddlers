# Course rationale and reference

Why the course is shaped the way it is, plus the reference tables that no longer sit in the student-facing syllabus. This is background, not instruction. `lessons/SYLLABUS.md` is what the learner reads.

---

## Where this course picks up

Chapter 1 delivered a complete plain-Java CRUD console application: object modelling, collections, `Optional`, an interface-backed repository, constructor injection, a composition root, input validation with local retries, EOF safety, a pure text renderer, JUnit 5, Checkstyle, Spotless, and JaCoCo. Its closing verification was 49 passing tests with every quality gate green.

This course inherits all of it. Nothing from Chapter 1 is re-taught as new material. Appendix D maps every lesson here to the Chapter 1 lesson it reviews.

### What carries over unchanged

Java 21 and Maven with a pinned Surefire. Checkstyle, Spotless, and JaCoCo. Reverse-domain packages matching directory paths. One-way dependency direction. Constructor injection and a composition root. Pure renderers that return `String`. Injected streams for in-process tests and a subprocess for the real `main`. Test naming and body shape from `TESTING_STANDARDS.md`. The `C###` commit grammar and its hook.

### What is genuinely new

Two-dimensional arrays. Algorithms with a correctness argument — enumeration, constraint propagation, fixed-point iteration, generate-and-test, dynamic programming. Serialization to a format you have to read by eye. The browser: DOM, pointer events, storage, service workers. And, threaded through all of it, writing a comprehensive test suite by hand rather than inheriting one.

---

## Why this course is structured differently from Chapter 1

Chapter 1 published its capstone and its grader **before** the lessons. That had three costs, all of them observed rather than theorised:

- Code got shaped to fit the box the grader defined, instead of the problem.
- Responsibility for initial testing moved to whoever wrote the grader.
- Help got asked for more often than it was needed, because the target was someone else's.

The final tally makes it concrete: 49 tests, of which 39 lived in the agent-authored `Chapter01CapstoneTest`. Ten tests were written by hand, in four files, for thirteen production classes — no `TextTableTest`, no `ApplicationServiceTest`, no `ConsolePrompterTest`, no `InMemoryApplicationRepositoryTest`. The skill practised was *satisfying a suite*. The skill not practised was *designing one*.

So this course inverts it: **loose at the front, rigorous at the end.**

Nothing at the start of a chapter tells you what to name a class or which methods to write. What a chapter publishes up front is only the list of things that **have to be true when you're done, so that later chapters can succeed.** How you get there is yours. The rigour arrives at the capstone, as a gap analysis of what you actually built.

---

## The test ladder

Chapter 1's `TESTING_STANDARDS.md` names three test scopes in a single paragraph. This course promotes that paragraph into the spine of every chapter.

**Tier 1 — File tests.** One class in isolation, collaborators controlled. Catches the bug on the line you just typed. Cheap; you will write dozens.

**Tier 2 — Flow tests.** Real collaborators wired the way `main` wires them, exercising one complete path. Catches the bug that lives *between* two files that each pass their own tests. A handful per chapter.

**Tier 3 — End-to-end tests.** The real class in a real JVM through `ProcessBuilder`, asserting exact stdout and exit code. Catches the bug that only exists once wiring, charset, and classpath are real. Slow; one or two per chapter and no more.

A suite that is all tier 1 misses integration bugs. A suite that is all tier 3 takes four minutes to run and you will stop running it. The proportion is the judgement being taught.

**The regression rule.** Any bug you find outside a test gets a failing test *first*, at whichever tier would have caught it. Choosing the tier correctly is the exercise; the fix is the easy part.

### Barebones is correct at the start

A test written against boilerplate should be boring, and that is not a failure. When a class does one trivial thing, its test asserts that one trivial thing. Rigour arrives with behaviour: as a class grows a second responsibility, an edge case, or a caller with different expectations, its tests grow with it.

Do not write speculative tests for behaviour that does not exist yet. Do not apologise for a three-line test on a three-line class. The failure mode to avoid is not "this test is too simple" — it is "this class grew and its test didn't."

### Test maintenance is part of the work

Test files are not append-only. A lesson's test-writing segment routinely means going back into a file you wrote three lessons ago to rewrite, fix, extend, restructure, or **delete**.

Delete a test when it asserts something that is no longer true, when it duplicates a better test written later, when it tests an implementation detail you have since replaced, or when it exists only to raise a coverage number. A suite you trust is more valuable than a suite that is large, and every test you keep is a test you have to keep correct.

The tell that maintenance is overdue: you change one line of production code and six tests go red, five of which were testing the same thing.

### Organising tests into files

Sort by what the test *proves*, not by what it *touches*.

- One tier 1 file per production class, named `<ClassName>Test`, mirroring the production package.
- Tier 2 files named for the flow — `SolveFlowTest`, not `SolverAndGridTest`. A flow test that spans four classes belongs in one file named after the behaviour, not split across four.
- Tier 3 files named `<MainClass>ProcessTest`, one per runnable main.
- Shared test infrastructure lives in its own file and is not a test — the tier 3 harness, fixture builders, and custom assertions.
- When a tier 1 file passes roughly 200 lines, that is usually the class telling you it has two responsibilities, not the test file telling you to split it.

### The ladder after Chapter 10

The browser has no JUnit, and the ladder has to be rebuilt rather than ported. Plan it as part of expanding Chapter 10, before writing that chapter's code. The shape that fits this course's no-build-step constraint:

- **Tier 1** — keep play logic in plain `.mjs` modules that never touch the DOM, and run them with `node --test`. It ships with Node; no npm install, no config, no build step.
- **Tier 2** — needs a DOM. Write `tests.html`: a page that loads the same modules plus a small assert harness you write by hand, and prints results into the page. This is the tier 3 harness exercise again, in a different runtime.
- **Tier 3** — a real browser driver such as Playwright. Deferred, with a trigger: when you need to verify touch behaviour on an actual device, or when you want this running in CI.

---

## The capstone protocol

The capstone is written after the chapter's lessons are finished, and it grades what exists rather than prescribing what should. It is a conversation, not a file — though it may produce files. The ask is *"Grade my work for Chapter N."*

Four gates run in order and stop at the first failure: a demo you can see, your own suite checked against the chapter's "Must be true" list, a code-free gap analysis, and a viva answered from memory with the editor closed.

Gate 3 is the one that replaces Chapter 1's hidden grader, and it is the reason the whole structure changed. The agent names what your suite does not cover, specifically enough that you can go write the tests; you write them. Both halves are load-bearing, and some of those tests fail and find real bugs.

**Record the gap count in `JOURNAL.md` every chapter.** It is a direct measurement of what your own test design missed, and it is the single best signal of whether this course is working. If Chapter 8's count matches Chapter 3's, stop and ask why.

The procedure — what each gate checks, what the agent may and may not say at each one, and the prompt you paste to start it — lives in `.agents/skills/review-submission/SKILL.md`.

---

## Notation

- **Must be true** — the guarantees later chapters depend on. Not signatures, not class names.
- **Verify** — the test that closes a lesson, with its tier.
- **See** — what appears in your terminal when it works.
- **Review / New** — whether a lesson revisits Chapter 1 material or introduces something you have not built before. Review lessons are short on explanation and identical in rigour.

---

# Appendix A — Glyphs

| Meaning           | Unicode | ASCII fallback |
|-------------------|---------|----------------|
| Filled            | `█`     | `#`            |
| Empty             | `·`     | `.`            |
| Marked            | `✕`    | `X`            |
| Deduced this pass | `▓`     | `+`            |

Choose in Chapter 3, encode the choice in the renderer's exact-output test, and do not revisit it.

# Appendix C — Deferred, with the trigger that un-defers it

| Deferred                         | Revisit when                                              |
|----------------------------------|-----------------------------------------------------------|
| 15×15+ grids                     | 10×10 is finished and you want the pan/zoom problem       |
| Canvas rendering                 | You are at 15×15+ and DOM repaint is visibly janky        |
| React + Vite                     | You want menus, settings, and routing                     |
| Playwright or any browser driver | You need to verify touch on a real device, or you want CI |
| IndexedDB                        | `localStorage` limits bite, or iOS evicts saves           |
| Difficulty rating                | You have a corpus you have played and can label           |
| Picture puzzles                  | You want a PNG → 1-bit → verify-solvable pipeline         |
| Capacitor / App Store            | You have played the PWA for a month and still want it     |
| Dirty-line queue, bitsets        | You have measured a problem                               |
| Coloured nonograms               | Everything above is done                                  |

# Appendix D — Chapter 1 review map

Rows are added when a chapter opens and its lessons get written, not before.

| This course                            | Chapter 1 source             | Status                                    |
|----------------------------------------|------------------------------|-------------------------------------------|
| 2.1 POM, coordinates, scopes, Surefire | L0 Build system              | review                                    |
| 2.2 Checkstyle, Spotless, JaCoCo       | L8 Java quality standards    | review                                    |
| 2.3 Package boundary                   | L3, L6 dependency direction  | review; enforced by build, which is new   |
| 2.4 Subprocess harness                 | L7 `MainProcessTest`         | pattern known; reusable harness is new    |
| 2.5 Reading failures                   | L7, L8                       | review, catalogued                        |
| 2.6 Git and commit grammar *(opt.)*    | `CONTEXT.md` commit messages | review only; the machinery already runs   |

# Appendix E — The three tiers, in code you already wrote

Examples, not solutions. All three are in the tracker, in your own hand, and none of them touch a nonogram — which makes them safe to read at any point in this course.

All paths are relative to `/Users/connor/repos/JAVA/job-application-tracker`, under `src/test/java/com/connorjensen/jobtracker/`.

| Tier | File                          | What to notice                                                                                 |
|------|-------------------------------|------------------------------------------------------------------------------------------------|
| 1    | `ApplicationTest.java`        | One class, no collaborators, values in and values out.                                         |
| 2    | `cli/ConsoleSessionTest.java` | Real objects wired together, streams injected, one complete path asserted.                     |
| 3    | `MainProcessTest.java`        | `ProcessBuilder`, stdin closed, a timeout of `5`s, explicit UTF-8, exit code and exact stdout. |

Read the tier 3 one properly before Lesson 2.4. It is the thing you are about to generalise, and every non-obvious line in it is there because of a bug.

For anything these three do not cover — parameterized tests, `@Nested` grouping, custom assertions — ask for an example on unrelated subject matter. Never on the class you are about to write.
