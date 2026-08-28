# Nonogram — Course Spec

**Version:** 0.6
**Course position:** Chapters 2–14. Chapter 1 (Java Foundations) is complete in the
`job-application-tracker` repository.
**Companion:** `nonogram-learning-guide.md` — how to work. Read it first.
**Prerequisite:** `CHAPTER-1-REVIEW.md` — what you already know, and how to prove you still know it.

**Chapters 2 and 3 are expanded. Chapter 4 is a manifest. Chapters 5–14 are syllabus lines.**
That is deliberate and permanent policy, not an unfinished document. See "How a chapter works"
below.

---

## Where this course picks up

Chapter 1 delivered a complete plain-Java CRUD console application: object modelling, collections,
`Optional`, an interface-backed repository, constructor injection, a composition root, input
validation with local retries, EOF safety, a pure text renderer, JUnit 5, Checkstyle, Spotless, and
JaCoCo. Its closing verification was 49 passing tests with every quality gate green.

This course inherits all of it. Nothing from Chapter 1 is re-taught as new material. Appendix D maps
every lesson here to the Chapter 1 lesson it reviews.

### What carries over unchanged

Java 21 and Maven with a pinned Surefire. Checkstyle, Spotless, and JaCoCo. Reverse-domain packages
matching directory paths. One-way dependency direction. Constructor injection and a composition
root. Pure renderers that return `String`. Injected streams for in-process tests and a subprocess
for the real `main`. Test naming and body shape from `TESTING_STANDARDS.md`. The `C###` commit
grammar and its hook.

### What is genuinely new

Two-dimensional arrays. Algorithms with a correctness argument — enumeration, constraint
propagation, fixed-point iteration, generate-and-test, dynamic programming. Serialization to a
format you have to read by eye. The browser: DOM, pointer events, storage, service workers. And,
threaded through all of it, writing a comprehensive test suite by hand rather than inheriting one.

---

## Why this course is structured differently from Chapter 1

Chapter 1 published its capstone and its grader **before** the lessons. That had three costs, all of
them observed rather than theorised:

- Code got shaped to fit the box the grader defined, instead of the problem.
- Responsibility for initial testing moved to whoever wrote the grader.
- Help got asked for more often than it was needed, because the target was someone else's.

The final tally makes it concrete: 49 tests, of which 39 lived in the agent-authored
`Chapter01CapstoneTest`. Ten tests were written by hand, in four files, for thirteen production
classes — no `TextTableTest`, no `ApplicationServiceTest`, no `ConsolePrompterTest`, no
`InMemoryApplicationRepositoryTest`. The skill practised was *satisfying a suite*. The skill not
practised was *designing one*.

So this course inverts it: **loose at the front, rigorous at the end.**

Nothing at the start of a chapter tells you what to name a class or which methods to write. What a
chapter publishes up front is only the list of things that **have to be true when you're done, so
that later chapters can succeed.** How you get there is yours. The rigour arrives at the capstone,
as a gap analysis of what you actually built.

---

## How a chapter works

A chapter passes through four states. Only one chapter is ever in state 3.

**State 1 — Syllabus line.** One sentence: "Ch 9: emitting puzzles as JSON the browser can load."
Every chapter has this from day one. It exists so you know where the course is going, and for no
other reason.

**State 2 — Manifest.** Goal, plus **Must be true when you're done** — the guarantees later chapters
depend on — plus a note on which later chapter depends on each one. No lessons, no signatures, no
class names, no implementation. Written two or three chapters ahead of where you are, no further,
because a lesson breakdown written before you understand the problem is usually wrong.

**State 3 — Expanded.** Lessons with named verification, written when you arrive at the chapter. You
draft the lesson breakdown yourself; that planning is part of the work. An agent may review your
draft. An agent may not write it.

**State 4 — Capstone, written last.** After every lesson in the chapter is done, *then* the capstone
gets authored, from the code and tests that actually exist. It is a gap analysis, not a
specification. It cannot be a box you shape code to fit, because the code already exists when it is
written.

`CAPSTONE.md` for a chapter you have not finished is a stub containing only that chapter's
"Must be true" list. That is not an oversight. Filling it in early is the failure mode this
structure exists to prevent.

### The "Must be true" contract

This replaces the signature block that Chapter 1 used. The difference matters.

A signature block says `public static String render(Grid grid)`. That names your class, your method,
your parameter order, and your return type — four decisions taken away from you before you have
thought about any of them.

A "Must be true" entry says: *something in the engine turns a grid into a deterministic string, with
no printing inside it, and a test asserts that string character-for-character.* That is a real
constraint — Chapter 4 will render clues into the gutters of exactly that string, so it has to
exist — but every naming and shape decision stays yours.

Write it as a promise to your future self, and keep it to promises that later chapters would break
without.

---

## The four standing rules

**1. The terminal renderer comes before everything else.** Chapter 3 builds a board printer with no
clues, no solver, and no puzzle behind it. From then on, every single thing you build has a way to
be *looked at*, and "print the board and stare at it" is your primary debugging tool. Most nonogram
bugs are visible on sight and invisible in a stack trace.

**2. You write every test.** No agent-authored test enters this repository, ever — not as a grader,
not as a hidden capstone file, not as a favour when you are tired. An agent may tell you a test is
missing, may review a test you wrote, and may show you an example on unrelated code. It may not
write one for you.

**3. Tests ship with the lesson, not after the chapter.** A lesson is done when its test is green.

**4. Functions return Strings; only `main` prints.** You already built this rule in Chapter 1 as
`TextTable` — this is a restatement, not a lesson. Break it and most of the tests in this course
become impossible to write.

---

## The test ladder

Chapter 1's `TESTING_STANDARDS.md` names three test scopes in a single paragraph. This course
promotes that paragraph into the spine of every chapter.

**Tier 1 — File tests.** One class in isolation, collaborators controlled. Catches the bug on the
line you just typed. Cheap; you will write dozens.

**Tier 2 — Flow tests.** Real collaborators wired the way `main` wires them, exercising one complete
path. Catches the bug that lives *between* two files that each pass their own tests. A handful per
chapter.

**Tier 3 — End-to-end tests.** The real class in a real JVM through `ProcessBuilder`, asserting exact
stdout and exit code. Catches the bug that only exists once wiring, charset, and classpath are real.
Slow; one or two per chapter and no more.

A suite that is all tier 1 misses integration bugs. A suite that is all tier 3 takes four minutes to
run and you will stop running it. The proportion is the judgement being taught.

**The regression rule.** Any bug you find outside a test gets a failing test *first*, at whichever
tier would have caught it. Choosing the tier correctly is the exercise; the fix is the easy part.

### Barebones is correct at the start

A test written against boilerplate should be boring, and that is not a failure. When a class does
one trivial thing, its test asserts that one trivial thing. Rigour arrives with behaviour: as a
class grows a second responsibility, an edge case, or a caller with different expectations, its
tests grow with it.

Do not write speculative tests for behaviour that does not exist yet. Do not apologise for a
three-line test on a three-line class. The failure mode to avoid is not "this test is too simple" —
it is "this class grew and its test didn't."

### Test maintenance is part of the work

Test files are not append-only. A lesson's test-writing segment routinely means going back into a
file you wrote three lessons ago to rewrite, fix, extend, restructure, or **delete**.

Delete a test when it asserts something that is no longer true, when it duplicates a better test
written later, when it tests an implementation detail you have since replaced, or when it exists
only to raise a coverage number. A suite you trust is more valuable than a suite that is large, and
every test you keep is a test you have to keep correct.

The tell that maintenance is overdue: you change one line of production code and six tests go red,
five of which were testing the same thing.

### Organising tests into files

Sort by what the test *proves*, not by what it *touches*.

- One tier 1 file per production class, named `<ClassName>Test`, mirroring the production package.
- Tier 2 files named for the flow — `SolveFlowTest`, not `SolverAndGridTest`. A flow test that spans
  four classes belongs in one file named after the behaviour, not split across four.
- Tier 3 files named `<MainClass>ProcessTest`, one per runnable main.
- Shared test infrastructure lives in its own file and is not a test — the tier 3 harness, fixture
  builders, and custom assertions.
- When a tier 1 file passes roughly 200 lines, that is usually the class telling you it has two
  responsibilities, not the test file telling you to split it.

### The ladder after Chapter 10

The browser has no JUnit, and the ladder has to be rebuilt rather than ported. Plan it as part of
expanding Chapter 10, before writing that chapter's code. The shape that fits this course's
no-build-step constraint:

- **Tier 1** — keep play logic in plain `.mjs` modules that never touch the DOM, and run them with
  `node --test`. It ships with Node; no npm install, no config, no build step.
- **Tier 2** — needs a DOM. Write `tests.html`: a page that loads the same modules plus a small
  assert harness you write by hand, and prints results into the page. This is the tier 3 harness
  exercise again, in a different runtime.
- **Tier 3** — a real browser driver such as Playwright. Deferred, with a trigger: when you need to
  verify touch behaviour on an actual device, or when you want this running in CI.

---

## The capstone protocol

The capstone is written after the chapter's lessons are finished, and it grades what exists rather
than prescribing what should. It is a conversation, not a file — though it may produce files.

**The ask:** *"Grade my work for Chapter N."*

**Gate 1 — Demo.** Run it, paste the output. It does the thing the chapter set out to do, and you
can see it.

**Gate 2 — Your suite runs.** `mvn verify` green. Every "Must be true" entry for the chapter has at
least one test that would fail if it stopped being true. You point at which test covers which entry.
If you cannot, that entry is untested regardless of what the coverage report says.

**Gate 3 — Gap analysis.** This is the gate that replaces Chapter 1's hidden grader, and the reason
the whole structure changed.

The agent reads your production code and your tests and reports, without writing any code:

- **Uncovered behaviour** — cases your suite does not exercise, named specifically enough that you
  can go write them. It names the gap; you write the test. Both halves matter.
- **Miscategorised tests** — things tested at tier 1 that need tier 2, or tier 3 tests doing work an
  in-process test would do faster.
- **Tests to delete** — trivial, duplicated, outdated, or coverage-chasing.
- **Brittleness** — tests coupled to implementation detail that will break on your next refactor.
- **Shape observations on the production code**, offered as questions rather than corrections.

Then you go write the missing tests, and some of them fail, and you fix the bugs they found.

**Record the gap count in `JOURNAL.md` every chapter.** It is a direct measurement of what your own
test design missed, and it is the single best signal of whether this course is working. If Chapter
8's count matches Chapter 3's, stop and ask why.

**Gate 4 — Viva.** Questions, answered from memory, editor closed. The agent drafts them from the
chapter you actually built, not from a list written in advance.

### The prompt to paste

> Grade my work for Chapter N of my Nonogram course. Four gates, in order, stop at the first
> failure.
>
> **Gate 1:** My terminal output — [paste]. Here's what the chapter set out to do — [paste goal].
>
> **Gate 2:** My `mvn verify` output and my full test file list — [paste]. Here is the chapter's
> "Must be true" list — [paste]. For each entry, I claim this test covers it: [list]. Tell me where
> I'm wrong.
>
> **Gate 3:** Read my production code and my tests and give me a gap analysis: behaviour I haven't
> covered, tests at the wrong tier, tests I should delete, and tests that are coupled to my
> implementation. Name the gaps specifically. **Do not write any test code.** Tell me how many gaps
> you found so I can record it.
>
> **Gate 4:** Draft viva questions from what I actually built, ask them one at a time, and judge my
> answers.
>
> **Rules for you:** Do not write, rewrite, or show me any implementation or test code at any point.
> If you need to show me what a concept looks like, use an example on unrelated code — never on
> mine. If I fail a gate, tell me *what* is wrong and *which concept* to revisit, not how to fix it.
> If I ask for the fix, refuse and restate the concept.

---

## Notation

- **Must be true** — the guarantees later chapters depend on. Not signatures, not class names.
- **Verify** — the test that closes a lesson, with its tier.
- **See** — what appears in your terminal when it works.
- **Review / New** — whether a lesson revisits Chapter 1 material or introduces something you have
  not built before. Review lessons are short on explanation and identical in rigour.

---

# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, then build the end-to-end test harness you
will use for the next twelve chapters — before there is any nonogram code worth testing.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker.
When you are stuck for more than twenty minutes, open it, look at exactly the thing you are stuck
on, close it, and type your own version. A `pom.xml` you copied teaches nothing; a `pom.xml` you
reconstructed and then diffed teaches the build system twice.

Copy verbatim rather than rebuilding: `GLOSSARY.md`, `COURSE_STANDARDS.md`, `TESTING_STANDARDS.md`,
and `config/checkstyle/checkstyle.xml`. Those are frozen reference, not exercises.

### Lessons

**2.1 — The POM, from memory.** *(Review — Ch1 L0.)* Coordinates, `maven.compiler.release` 21,
`junit-jupiter` at test scope, a pinned Surefire. Write it before you look. Then diff against the
tracker's and write down every difference and why it exists.
*Verify:* `mvn compile` succeeds, and `mvn test` reports the tests it should rather than silently
reporting zero.

**2.2 — The quality gates, from memory.** *(Review — Ch1 L8.)* Checkstyle at `validate`, Spotless
with `google-java-format`, JaCoCo reporting. Understand the split before you write it: Checkstyle
reports and never edits; Spotless rewrites, but only on `spotless:apply`.
*Verify:* `mvn verify` runs all three and passes on an almost-empty project.

**2.3 — Git and the commit grammar.** *(Review — Ch1 `CONTEXT.md`.)* `git init`, a `.gitignore`
covering `target/`, the `prepare-commit-msg` hook that prepends `C###`. Chapter 1 used five commit
groups; you own fewer surfaces here, so pick fewer.
*Verify:* a first commit the hook numbered, and `git status` clean.

**2.4 — The two packages.** *(New — the boundary, not the syntax.)* Create the engine package and a
`tools` subpackage under it. Nothing goes in the engine yet. Write down the rule: **the engine never
imports `java.io`, `java.nio.file`, or `java.net`, and never touches `System.out`.** Everything that
reads, writes, or prints lives in `tools`.

Then make the rule enforceable rather than aspirational. Checkstyle's `ImportControl` module can
fail the build when an engine class imports a forbidden package; your existing ruleset already runs
`TreeWalker` import modules, so it drops straight in. A boundary a human has to remember is a
boundary that erodes.
*Verify:* a deliberately bad import in an engine class fails `mvn verify`, and the same import in a
`tools` class does not.

**2.5 — The end-to-end harness.** *(New.)* This is the chapter's real content, and the thing you
will still be using in Chapter 14.

You need something that launches a named main class in a fresh JVM, feeds it stdin, waits with a
timeout, and hands back the exit code and the UTF-8 text it printed. Chapter 1's `MainProcessTest`
does all of this inline for one specific class; your job is to lift it into something reusable and
understand every line while you do.

Three details are each a bug you would otherwise ship, and none of them are obvious: closing the
child's stdin so it sees EOF rather than blocking forever; a timeout with a forcible kill so a hung
child fails the test instead of hanging your build; and decoding output as explicit UTF-8 rather
than the platform default, which is the only reason your Unicode glyphs survive Chapter 3.

Then prove the harness works. You need something runnable to point it at — a throwaway class in
`tools` that prints one line is enough, and it should stay throwaway. Delete it the moment Chapter 3
gives you a real main to test.
*Verify:* one test at each tier against that throwaway. Barebones is correct here: the subject does
one trivial thing, so the tests assert one trivial thing. What you are proving is that the *ladder*
works, not that the class does.

**2.6 — The failure catalogue.** *(New.)* Break each gate deliberately, one at a time, and read the
whole failure before fixing it: a failing JUnit assertion, a Checkstyle violation, Spotless drift, a
compile error, an import that crosses the engine boundary, and a test that Surefire silently does
not run. For each, record in `JOURNAL.md` where the output names the file, where it names the line,
and what the first line tells you that the last line does not.
*Verify:* six catalogue entries, and you can identify any of the six from its output alone.

### Must be true when you're done

| Guarantee | Depended on by |
|---|---|
| `mvn verify` runs tests, Checkstyle, Spotless, and JaCoCo, and fails the build when any of them fails | every chapter |
| Surefire actually runs JUnit 5 tests — a deliberately failing test fails the build | every chapter |
| Something can launch a named main class in a fresh JVM, feed it stdin, and return exit code plus UTF-8 stdout within a timeout | every tier 3 test from Chapter 3 on |
| An engine package and a `tools` package exist, and a forbidden import in the engine fails the build | Chapters 9–13, where the boundary is load-bearing |
| Git history exists under a commit grammar | nothing technical; it is how you go back to the last version that worked |
| You can identify each of the six failure modes from its output alone | your own debugging speed, for a year |

### Capstone

Written after Lesson 2.6, not before. Gates 1, 2, and 4; Gate 3's gap analysis needs real code to
analyse and a throwaway class does not qualify.

Viva topics to expect: what test *scope* means on a dependency; what is in `target/` and why it is
not committed; what Checkstyle does that Spotless does not; what JaCoCo proves and specifically does
not prove; why Surefire is pinned and what the symptom is when it is not; and, for each tier, one
bug it catches that neither of the others would.

---

# Chapter 3 — Seeing the Board

**Goal:** print a nonogram grid to the terminal. No clues, no solving, no puzzle — just a board you
can look at. Everything in the rest of the course is debugged through this output.

Four of these lessons revisit Chapter 1 mechanisms. They are short, and they are not optional: each
still owes a tier 1 test, and the chapter still owes tier 2 and tier 3 tests. Reviewing a mechanism
is not the same as reviewing the discipline of testing it.

Nothing below names a class or a method for you. Where this document needs to refer to something, it
describes what it does.

### Lessons

**3.1 — Cell states.** *(Review — Ch1 L2 enums. New — external codes.)* Three states: empty, filled,
and the player's mark. You built `Status` in Chapter 1, so the enum itself is familiar.

What is new is that these states leave the JVM. Chapter 9 writes them into JSON and Chapter 10 reads
that JSON in a browser, which has no idea what a Java enum ordinal is. So give each state an
explicit external code — a character or a small integer you choose deliberately — and **do not
derive it from `ordinal()`**. Ordinals are an implementation detail that changes when you reorder
constants; a wire format has to survive that. Write down why the mark is player bookkeeping and
never part of a solution.
*Verify:* **Tier 1** — assert the external code of each state explicitly, with a comment naming the
JSON format as the reason the test exists. This test's whole job is to fail loudly if someone
reorders the constants in a year.

**3.2 — The indexing convention.** *(New.)* A two-dimensional array of cell states. Chapter 1 used
`List` and streams throughout and never touched a `[][]`, so this is unfamiliar ground: an array of
arrays is an array of row references rather than a rectangle, and mixing the coordinate order
produces a bug that looks like a rendering problem for a week. Pick row-first or column-first, put
it in a comment at the top of the class, and never think about it again.
*Verify:* **Tier 1** — write a value at one coordinate, assert it reads back there, and assert the
transposed coordinate is untouched. On a square grid a transposition bug passes almost every test
you would think to write; this is the one that catches it.

**3.3 — The grid.** *(Review — Ch1 L1 classes, L4 guards. New — a domain type that throws.)* Width,
height, every cell starting empty, read and write by coordinate, and bounds checks that fail loudly
and name the offending coordinate.

Note what changed since Chapter 1. There, the rule was *catch an exception only where you can
translate it into behaviour the caller understands*, and every throw lived at the console boundary.
This is the other half of that rule: a domain type throws on a programming error and does not
apologise, because there is no user input here to retry. Be able to say which of your classes may
throw and which must translate.
*Verify:* **Tier 1** — all four edges, asserting the failure message contains the bad value.

**3.4 — The renderer.** *(Review — Ch1 L6 `TextTable`.)* Something that takes a grid and returns a
string. One character per cell, newline between rows, no trailing newline. This is `TextTable`
again: pure, stateless, no printing, deterministic, asserted character-for-character. If it feels
familiar, that is the point — the rule generalises, and you are meant to notice.

Glyphs: `█` filled, `·` empty, `✕` marked. If your terminal mangles Unicode, use `#`, `.`, `X`. Pick
a set now — changing it later breaks every string-match test you write between here and Chapter 14.
*Verify:* **Tier 1** — hand-build a small grid, assert the exact expected string.
*See:*
```
·█·
███
·█·
```

**3.5 — Rulers.** *(New.)* A heavier separator every fifth row and column so you can count without
losing your place, plus column numbers across the top and row numbers down the side. The alignment
arithmetic is the work: two-digit column labels change the gutter width, and the ruler has to land
correctly on a 3×7 as well as a 10×10.

This is also the lesson where you go back and change tests you already wrote. Adding rulers changes
the output of Lesson 3.4's renderer test. Decide deliberately whether that test should now expect
rulers, whether rulers belong behind a flag so both forms stay testable, or whether the old test
should be deleted because a better one replaces it. Any of the three can be right; picking without
thinking is the thing to avoid.
*Verify:* **Tier 1** — a ruler test on a 10×10, plus a non-square case where a transposed width and
height would still produce a plausible-looking board.
*See:*
```
    0 1 2 3 4 │ 5 6 7 8 9
 0  · · █ █ · │ · █ █ · ·
 1  · █ █ █ █ │ █ █ █ █ ·
```

**3.6 — A main you can run.** *(Review — Ch1 L3 composition root. New — demo main versus composition
root.)* Something in `tools` that builds a hand-authored 5×5 and prints it. The one and only place
this chapter prints.

Chapter 1's `Main` was a composition root: it constructed the object graph and started the
application. This is a *demo* main — it exists to make one thing visible, and it is disposable.
Knowing which kind you are writing tells you how much to put in it. This is also where Chapter 2's
throwaway class gets deleted, along with its three tests; it has been replaced by something real.
*Verify:* **Tier 2** — compose a real grid and a real renderer and assert the full board, no process
launched. **Tier 3** — run it through your harness in a real JVM and assert exact stdout and a zero
exit code. Note that the tier 3 test is the only one that would catch a glyph mangled by the
platform default charset.

### Must be true when you're done

| Guarantee | Depended on by |
|---|---|
| A grid of cells with three states exists, addressed by coordinate under one documented convention, with a test that fails if the convention is transposed | everything |
| Out-of-bounds access fails loudly and names the offending coordinate | Ch5's solver, which will index past the end while you are getting it wrong |
| Something in the engine turns a grid into a deterministic string, with no printing inside it, asserted character-for-character | Ch4 renders clues into the gutters of exactly that string |
| The glyph set is chosen and recorded | every expected-output test from here to Ch14 |
| Each cell state has an explicit external code not derived from `ordinal()` | Ch9's JSON, Ch10's browser, Ch12's saves |
| Exactly one place in the program prints | Ch8's terminal game, which replaces that one place |
| A runnable main prints a board, and a tier 3 test proves it | Ch8, and every chapter with a demo after it |

### Capstone

Written after Lesson 3.6. All four gates. `BoardDemo` — or whatever you called it — prints a
recognisable 5×5 shape: a smiley, an arrow, your initial.

Gate 3 is the first real gap analysis of the course. Expect it to find things; that is the point,
and the number it produces is the baseline every later chapter is measured against. Record it in
`JOURNAL.md`.

---

# Chapter 4 — Clues

*Manifest. Expand this yourself when you finish Chapter 3.*

**Goal:** derive clue arrays from a solved grid by run-length encoding, and render them in the
gutters around the board. Clues are always derived from a solution, never authored, which makes an
entire category of bug impossible.

### Must be true when you're done

| Guarantee | Depended on by |
|---|---|
| Clues are derived from a grid on demand and never stored as authored data | Ch9, where storing them would let them drift out of sync with the answer |
| Row clues and column clues come from one code path, not two | Ch6, where propagation runs the same solver over both and any divergence becomes a bug you cannot see |
| A line with no filled cells has a defined, tested clue representation | Ch5, whose enumerator hits this case constantly and must not special-case it |
| Deriving clues and rendering clues are separate things | Ch10, which renders clues in a browser and derives nothing |
| The board renderer still produces a deterministic string, now with gutters | Ch8's terminal game |

---

# Chapters 5–14 — Syllabus

One line each. They become manifests when you are two or three chapters away, and lessons when you
arrive. Not before.

**Ch 5** — the brute-force line solver: enumerate every valid placement for one line and intersect
them. Correct and slow on purpose; it becomes the oracle that proves Chapter 14 right.

**Ch 6** — propagation: run the line solver over rows and columns until nothing changes.

**Ch 7** — generation: random fill, derive clues, propagate, keep only what solves. Seeded and
reproducible.

**Ch 8** — the terminal game: play a generated puzzle to completion with moves, undo, and hints.
**The checkpoint that matters** — after this, the engine is proven and every remaining bug is a UI
bug.

**Ch 9** — data on disk: emit puzzles as readable JSON, with a round-trip test.

**Ch 10** — the board in a browser: one HTML file, no build step, fetch the JSON and play with a
mouse. The test ladder gets rebuilt here, not ported.

**Ch 11** — touch: pointer events, mode toggle, drag-painting with axis lock, stroke-level undo.
Budget more time than the entire solver took.

**Ch 12** — persistence and assists: `localStorage` saves, timer, crosshair, auto-cross, and hints
read from the deduction list.

**Ch 13** — shipping: manifest plus service worker, deployed to a free static host and installed to
your home screen.

**Ch 14** — the fast solver: rewrite the line solver as dynamic programming and prove it correct by
differential testing against Chapter 5. Note that this one never runs in the browser — it is the
learning finale, not a product feature.

---

# Appendix A — Glyphs

| Meaning | Unicode | ASCII fallback |
|---|---|---|
| Filled | `█` | `#` |
| Empty | `·` | `.` |
| Marked | `✕` | `X` |
| Deduced this pass | `▓` | `+` |

Choose in Chapter 3 and do not revisit it.

# Appendix B — Repository layout

```
<project>/
├─ JOURNAL.md                       stuck/unstuck log and per-chapter gap counts
├─ pom.xml
├─ .githooks/prepare-commit-msg
├─ config/checkstyle/checkstyle.xml
├─ lessons/
│  ├─ SPEC.md                       this document
│  ├─ LEARNING_GUIDE.md
│  ├─ CHAPTER-1-REVIEW.md
│  ├─ COURSE_STANDARDS.md           adapted from Chapter 1
│  ├─ TESTING_STANDARDS.md          copied from Chapter 1, plus the tier convention
│  ├─ ch01-GLOSSARY.md              copied from Chapter 1, frozen
│  └─ chNN-<name>/
│     ├─ README.md                  lessons, once expanded
│     └─ CAPSTONE.md                stub holding "Must be true" until the chapter ends
├─ src/main/java/com/connorjensen/<project>/
│  │                                engine; no java.io, no java.nio.file, no java.net, no printing
│  └─ tools/                        mains, file I/O, terminal game
├─ src/test/java/com/connorjensen/<project>/
│                                   mirrors the above, file for file, plus shared harnesses
├─ puzzles/                         generated JSON, committed
└─ web/                             index.html, manifest.json, sw.js, icons/
```

The log is `JOURNAL.md`, not `NOTES.md`. The tracker already uses `NOTES.md` for Java version
history, and one name meaning two things across one course is a trip hazard you can avoid for free.

# Appendix C — Deferred, with the trigger that un-defers it

| Deferred | Revisit when |
|---|---|
| 15×15+ grids | 10×10 is finished and you want the pan/zoom problem |
| Canvas rendering | You are at 15×15+ and DOM repaint is visibly janky |
| React + Vite | You want menus, settings, and routing |
| Playwright or any browser driver | You need to verify touch on a real device, or you want CI |
| IndexedDB | `localStorage` limits bite, or iOS evicts saves |
| Difficulty rating | You have a corpus you have played and can label |
| Picture puzzles | You want a PNG → 1-bit → verify-solvable pipeline |
| Capacitor / App Store | You have played the PWA for a month and still want it |
| Dirty-line queue, bitsets | You have measured a problem |
| Coloured nonograms | Everything above is done |

# Appendix D — Chapter 1 review map

| This course | Chapter 1 source | Status |
|---|---|---|
| 2.1 POM, coordinates, scopes, Surefire | L0 Build system | review |
| 2.2 Checkstyle, Spotless, JaCoCo | L8 Java quality standards | review |
| 2.3 Git and commit grammar | `CONTEXT.md` commit messages | review |
| 2.4 Package boundary | L3, L6 dependency direction | review; enforced by build, which is new |
| 2.5 Subprocess harness | L7 `MainProcessTest` | pattern known; reusable harness is new |
| 2.6 Reading failures | L7, L8 | review, catalogued |
| 3.1 Enums | L2 Types, enums, collections | review; external codes new |
| 3.2 Two-dimensional arrays | — | **new** |
| 3.3 Classes, constructors, guards | L1, L4 | review; domain throws new |
| 3.4 Pure renderer returning a string | L6 `TextTable` | review |
| 3.5 Ruler alignment, and editing old tests | — | new |
| 3.6 Runnable `main` | L3 composition root | review; demo-main distinction new |

# Appendix E — The three tiers, in code you already wrote

Examples, not solutions. All three are in the tracker, in your own hand, and none of them touch a
nonogram — which makes them safe to read at any point in this course.

| Tier | File | What to notice |
|---|---|---|
| 1 | `src/test/.../ApplicationTest.java` | One class, no collaborators, values in and values out. |
| 2 | `src/test/.../cli/ConsoleSessionTest.java` | Real objects wired together, streams injected, one complete path asserted. |
| 3 | `src/test/.../MainProcessTest.java` | `ProcessBuilder`, stdin closed, a timeout, explicit UTF-8, exit code and exact stdout. |

Read the tier 3 one properly before Lesson 2.5. It is the thing you are about to generalise, and
every non-obvious line in it is there because of a bug.

For anything these three do not cover — parameterized tests, `@Nested` grouping, custom assertions —
ask for an example on unrelated subject matter. Never on the class you are about to write.
