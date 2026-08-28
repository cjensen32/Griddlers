# Nonogram — Course Spec

**Version:** 0.5
**Course position:** Chapters 2–14. Chapter 1 (Java Foundations) is complete in the
`job-application-tracker` repository.
**Companion:** `nonogram-learning-guide.md` — how to work. Read it first.
**Prerequisite:** `CHAPTER-1-REVIEW.md` — what you already know, and how to prove you still know it.
**Shape:** 13 chapters. Chapters 2 and 3 are fully specified. Everything after is a goal statement only.

**Expand one chapter at a time.** When you finish a capstone, write out the next chapter's lessons
yourself — that planning is part of the work, and a lesson breakdown written before you understand
the problem is usually wrong anyway. Bring the stub here and expand it when you get there.

---

## Where this course picks up

Chapter 1 delivered a complete plain-Java CRUD console application: object modelling, collections,
`Optional`, an interface-backed repository, constructor injection, a composition root, input
validation with local retries, EOF safety, a pure text renderer, JUnit 5, Checkstyle, Spotless, and
JaCoCo. Its closing verification was 49 passing tests with every quality gate green.

This course inherits all of it. Nothing from Chapter 1 is re-taught as new material.

### What carries over unchanged

| Inherited | Where it came from | How it appears here |
|---|---|---|
| Java 21, Maven, pinned Surefire | Ch1 L0 | Chapter 2, rebuilt from memory |
| Checkstyle, Spotless, JaCoCo | Ch1 L8 | Chapter 2, rebuilt from memory |
| Reverse-domain packages matching paths | Ch1 `COURSE_STANDARDS.md` | `com.connorjensen.nonogram` |
| One-way dependency direction | Ch1 L3, L6 | engine must not depend on `tools`, ever |
| Constructor injection, composition root | Ch1 L3 | every `main` in `tools/` |
| Pure renderers that return `String` | Ch1 L6 `TextTable` | `GridPrinter`, and every renderer after it |
| Injected streams; subprocess for real `main` | Ch1 L7 | the test ladder, tier 2 and tier 3 |
| Test naming and body shape | Ch1 `TESTING_STANDARDS.md` | unchanged, and now enforced per file |
| `C###` commit grammar and hook | Ch1 `CONTEXT.md` | Chapter 2, rebuilt from memory |

### What is genuinely new

Two-dimensional arrays. Algorithms with a correctness argument — enumeration, constraint
propagation, fixed-point iteration, generate-and-test, dynamic programming. Serialization to a
format you have to read by eye. The browser: DOM, pointer events, storage, service workers. And,
threaded through all of it, **writing a comprehensive test suite by hand rather than inheriting one.**

### Why the testing thesis exists

Chapter 1's final tally was 49 tests. Thirty-nine of them were in `Chapter01CapstoneTest` — the
agent-authored grader. You wrote ten, in four files, covering thirteen production classes. There is
no `TextTableTest`, no `ApplicationServiceTest`, no `ConsolePrompterTest`, and no
`InMemoryApplicationRepositoryTest` anywhere in that repository.

Chapter 1 demonstrated all three testing tiers, one specimen each: `ApplicationTest` tested a file,
`ConsoleSessionTest` tested a flow, `MainProcessTest` tested the program end to end in a real
subprocess. What it never asked for was *coverage of a whole codebase by your own hand*. Passing a
grader someone else wrote proves the code works. It does not prove you can decide what to test.

That is the skill this course is built around.

---

## How this course works

### The four standing rules

**1. The terminal renderer comes before everything else.** Chapter 3 builds a board printer with no
clues, no solver, and no puzzle behind it. From then on, every single thing you build has a way to
be *looked at*, and "print the board and stare at it" is your primary debugging tool. Most nonogram
bugs are visible on sight and invisible in a stack trace.

**2. Every chapter climbs the whole test ladder, by hand.** Not "there are tests." Three tiers,
described below, written by you, with no agent-authored test entering the repository outside Gate 3.
A chapter is not finished when its code works; it is finished when you can point at each tier and
say what it proves that the other two cannot.

**3. Tests ship with the lesson, not after the chapter.** Every lesson names its verification test.
Write it in the same sitting as the code. A lesson is done when its test is green.

**4. Functions return Strings; only `main` prints.** `GridPrinter.render(grid)` returns text.
`System.out.println` appears in exactly one place per program. You already built this rule in
Chapter 1 as `TextTable` — this is a restatement, not a lesson. Break it and half the tests in this
spec become impossible to write.

### The test ladder

Chapter 1's `TESTING_STANDARDS.md` names three test scopes in a single paragraph. This course
promotes that paragraph into the spine of every chapter.

**Tier 1 — File tests. Every production file has a test file.**
One class, in isolation, with collaborators controlled. Named `<ClassName>Test`, mirroring the
production package. This is the tier Chapter 1 skipped, and the tier that catches the bug on the
line you just typed. No production file ships without one — including the boring ones, because
"this class is too simple to test" is a prediction, not an observation.

**Tier 2 — Flow tests. Every user-visible flow has a test.**
Real collaborators, wired the way `main` wires them, exercising one complete path: derive clues from
a grid and render them; solve a line and propagate the result; play three moves and undo two. Named
for the flow, not the classes — `SolveFlowTest`, not `SolverAndGridTest`. This is the tier that
catches the bug that lives *between* two files that each pass their own tests.

**Tier 3 — End-to-end tests. Every runnable `main` has a subprocess test.**
Launch the real class in a real JVM through `ProcessBuilder`, feed it stdin, assert on exact stdout
bytes and the exit code. Chapter 1's `MainProcessTest` is the working template; you rebuild it as a
reusable `ProcessProbe` helper in Chapter 2 and reuse it for the next twelve chapters. This is the
tier that catches the bug that only exists once wiring, charset, and classpath are real.

**The regression rule.** Any bug you find outside a test gets a failing test *first*, at whichever
tier would have caught it. Choosing that tier correctly is the exercise; the fix is the easy part.

**What the tiers cost.** Tier 1 is cheap and you will write dozens. Tier 2 is moderate and you will
write a handful per chapter. Tier 3 is slow — seconds per test — and you will write one or two per
chapter and no more. A suite that is all tier 1 misses integration bugs; a suite that is all tier 3
takes four minutes to run and you will stop running it. The proportion is the judgement being taught.

### The capstone protocol

Each chapter ends with something that works and that you can see. Validating it runs four gates in
order; a failure means revise and re-run from that gate.

**Gate 1 — Demo.** Run the chapter's command, paste the output. It must match the described visual.

**Gate 2 — The ladder audit.** `mvn verify` green, and the agent checks three things: every
production file added this chapter has a tier 1 test file; every flow named in the chapter has a
tier 2 test; every runnable `main` has a tier 3 subprocess test. A missing tier fails the gate even
when everything is green, because the point is coverage you chose, not coverage you inherited.

**Gate 3 — Hidden tests.** The agent writes fresh tests against the chapter's **API contract** —
signatures only, never your implementation. It writes them to a file; you run them and read *only
failure names and messages*, never the test bodies. Every failure is a real bug you fix yourself.

This is the gate that establishes validity. Gate 2 proves your suite is complete in shape; Gate 3
proves it is complete in content. The interesting number is how many Gate 3 tests fail — that is a
direct measurement of what your own test design missed, and it should trend toward zero across
chapters. Record it in `JOURNAL.md` every chapter. It is the single best signal of whether this
course is working.

**Gate 4 — Viva.** The agent asks the chapter's questions. You answer from memory, editor closed.

### The prompt to paste

> I've finished Chapter N of my Nonogram course. Grade it in four gates, in order, and stop at the
> first failure.
>
> **Gate 1:** My terminal output — [paste]. Expected visual — [paste from spec].
>
> **Gate 2:** My `mvn verify` output and my full test file list — [paste]. Audit the ladder: every
> production file I added this chapter needs a tier 1 test file, every flow named in the chapter
> needs a tier 2 test, and every runnable `main` needs a tier 3 subprocess test. Name anything
> missing. Do not write any of them for me.
>
> **Gate 3:** Here is the API contract — [paste contract block]. Write 12–15 JUnit 5 tests against
> these signatures targeting this chapter's edge cases, plus at least 3 cases you think I probably
> didn't consider. Write them to `src/test/java/com/connorjensen/nonogram/CapstoneNTest.java`. Do
> not explain them, do not summarize what they cover, and do not look at or ask for my
> implementation. After I run them, tell me how many failed, and I'll record it.
>
> **Gate 4:** Ask me these questions one at a time and judge my answers: [paste viva questions].
>
> **Rules for you:** Do not write, rewrite, or show me any implementation code at any point, and do
> not write any test outside Gate 3. If I fail a gate, tell me *what* is wrong and *which concept*
> to revisit — not how to fix it. If I ask for the fix, refuse and restate the concept.

Gate 3 runs on the honor system. Reading the hidden tests is possible; it just means the gate stops
testing anything.

### Notation

- **Contract** — public signatures for the chapter. You write the bodies; the agent writes tests
  against them at Gate 3. Keep them stable once a chapter starts, or Gate 3 won't compile.
- **Verify** — the test that closes a lesson, with its tier.
- **See** — what appears in your terminal when it works.
- **Review / New** — whether a lesson revisits Chapter 1 material or introduces something you have
  not built before. Review lessons are short on explanation and identical in rigour.

---

# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, then install the three-tier test ladder as
reusable infrastructure — before there is any nonogram code worth testing.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker
repository. When you are stuck for more than twenty minutes, open it, look at exactly the thing you
are stuck on, close it, and type your own version. A `pom.xml` you copied teaches nothing; a
`pom.xml` you reconstructed and then diffed teaches the whole build system twice.

Chapter 1 also ends with a `RECALIBRATION.md`, a `GLOSSARY.md`, and standards documents. Copy those
three verbatim — they are frozen reference, not exercises. The build files are the exercise.

### Lessons

**2.1 — The POM, from memory.** *(Review — Ch1 L0.)* Coordinates, `maven.compiler.release` 21,
`junit-jupiter` at test scope, and Surefire pinned to a JUnit 5-capable version. Write it before you
look. Then diff against the tracker's `pom.xml` and write down every difference and why it exists.
*Verify:* `mvn compile` succeeds, and `mvn test` reports the tests it should rather than silently
reporting zero. A Surefire that is not pinned skips JUnit 5 tests without failing — you have seen
this fail before, which is why it is lesson one.

**2.2 — The quality gates, from memory.** *(Review — Ch1 L8.)* Checkstyle bound to `validate` with
the tracker's `config/checkstyle/checkstyle.xml`, Spotless with `google-java-format`, and JaCoCo
reporting. Understand the split before you write it: Checkstyle reports and never edits; Spotless
rewrites, but only when you run `spotless:apply`.
*Verify:* `mvn verify` runs all three and passes on an almost-empty project.

**2.3 — Git and the commit grammar.** *(Review — Ch1 `CONTEXT.md`.)* `git init`, a `.gitignore`
covering `target/`, and the `prepare-commit-msg` hook that prepends `C###`. Port the grammar and
decide this project's commit groups — Chapter 1 used `COURSE`, `TRACKER`, `PROGRESS`, `STYLE`, and
`FIX`; you own fewer surfaces here, so pick fewer.
*Verify:* a first commit whose message the hook numbered, and `git status` clean.

**2.4 — The test ladder, installed.** *(New.)* This is the chapter's real content. Build
`Workshop`, a class that exists only to be tested, and prove all three tiers work before any real
code depends on them.

- **Tier 1:** `WorkshopTest` asserts `banner()` returns the expected string. Pure function, no I/O.
- **Tier 2:** a flow test that composes `banner()` into whatever `main` prints, through real
  objects, asserting the composed text — without launching a process.
- **Tier 3:** `ProcessProbe`, a hand-written test helper that launches a named main class in a real
  JVM with `ProcessBuilder`, closes stdin, waits with a timeout, and returns the exit code and UTF-8
  stdout. Then `WorkshopProcessTest` uses it to assert `Workshop.main` prints the banner and exits 0.

Write `ProcessProbe` carefully. You will use it in every chapter from here to the end, and its three
non-obvious details — closing stdin so the child sees EOF, a timeout with `destroyForcibly` so a
hung child fails instead of hanging your build, and decoding as explicit UTF-8 rather than the
platform default — are each a bug you would otherwise ship.
*Verify:* three green tests, one per tier, and you can state in one sentence what each proves that
the other two cannot.

**2.5 — The failure catalogue.** *(New.)* Break each gate deliberately, one at a time, and read the
whole failure before fixing it: a failing JUnit assertion, a Checkstyle violation, Spotless drift, a
compile error, and a test that Surefire silently does not run. For each, write down in `JOURNAL.md`
where the failure names the file, where it names the line, and what the first line of output tells
you that the last line does not.
*Verify:* five catalogue entries, and you can identify any of the five from its output alone.

### Contract

```java
package com.connorjensen.nonogram.tools;

public final class Workshop {
    public static String banner();            // "nonogram <version> · Java <release>"
    public static void main(String[] args);
}
```

```java
package com.connorjensen.nonogram;           // src/test/java — test infrastructure

final class ProcessProbe {
    record Result(int exitCode, String output) { }

    static Result run(Class<?> mainClass, String stdin, long timeoutSeconds);
}
```

### Capstone

A repository where `mvn verify` is green across all four gates, three tests exist at three distinct
tiers, `ProcessProbe` works, and the commit history starts with a properly numbered message.

**Capstone test:** Gates 1, 2, and 4. Gate 3 is skipped — `banner()` is too small to hide a bug, and
a gate that cannot fail teaches nothing.
**Viva:** What does test *scope* mean on a dependency? What is in `target/`, and why is it not
committed? What is the difference between what Checkstyle does and what Spotless does? What does
JaCoCo prove, and what does it specifically not prove? Why is Surefire pinned, and what is the
symptom when it is not? Name the three tiers and, for each, one bug it catches that neither of the
others would.

---

# Chapter 3 — Seeing the Board

**Goal:** print a nonogram grid to the terminal. No clues, no solving, no puzzle — just a board you
can look at. Everything in the rest of the course is debugged through this output.

Four of these six lessons revisit Chapter 1 mechanisms. They are short, and they are not optional:
each one still owes a tier 1 test, and the chapter still owes tier 2 and tier 3 tests. Reviewing a
mechanism is not the same as reviewing the discipline of testing it.

### Lessons

**3.1 — Cell states.** *(Review — Ch1 L2 enums. New — ordinal stability as a contract.)*
`enum CellState { EMPTY, FILLED, MARKED }`. You built `Status` in Chapter 1, so the enum itself is
familiar. What is new: this enum's *ordinals* become a persisted save format in Chapter 12, which
means reordering the constants later silently corrupts saved games. Chapter 1's `Status` was never
serialized, so it never had this constraint. Write down why `MARKED` is player bookkeeping and never
part of a solution.
*Verify:* **Tier 1** — `CellStateTest` asserting `values().length == 3` and the exact ordinal order,
with a comment naming the save format as the reason the test exists.

**3.2 — The indexing convention.** *(New.)* `CellState[][] cells`, indexed `[y][x]`. Row first,
always. Chapter 1 used `List` and streams throughout and never touched a two-dimensional array, so
this is unfamiliar ground: array covariance, the fact that a `[][]` is an array of row references
rather than a rectangle, and the transposition bug that follows from mixing the order. Put the
convention in a comment at the top of the class.
*Verify:* **Tier 1** — `GridTest#getSetRoundTrip` — set `(x=3, y=1)`, assert it reads back at
`(3, 1)` and that `(1, 3)` is untouched. This test catches the transposition bug that will otherwise
haunt you for a week.

**3.3 — The Grid class.** *(Review — Ch1 L1 classes, L4 guards. New — a domain type that throws.)*
Constructor takes width and height and fills with `EMPTY`. `get(x, y)`, `set(x, y, state)`,
`width()`, `height()`. Bounds checks that throw `IndexOutOfBoundsException` with a message naming
the offending coordinate.

Note what changed since Chapter 1. There, the rule was *catch an exception only where you can
translate it into behaviour the caller understands*, and every throw lived at the console boundary.
`Grid` is the other half of that rule: a domain type throws on a programming error and does not
apologise, because there is no user input here to retry. Be able to say which of your classes may
throw and which must translate.
*Verify:* **Tier 1** — `GridTest#outOfBoundsThrows` for all four edges, asserting the message
contains the bad value.

**3.4 — The renderer.** *(Review — Ch1 L6 `TextTable`.)* `GridPrinter.render(Grid) → String`. One
character per cell, `\n` between rows, no trailing newline. This is `TextTable` again: a pure
renderer, no state, no printing, deterministic output, asserted character-for-character. If it feels
familiar, that is the point — the rule generalises, and you are meant to notice.

Glyphs: `█` filled, `·` empty, `✕` marked. If your terminal mangles Unicode, use `#`, `.`, `X`. Pick
a set now — changing it later breaks every string-match test you write.
*Verify:* **Tier 1** — `GridPrinterTest#rendersKnownGrid` — hand-build a 3×3, assert the exact
expected string.
*See:*
```
·█·
███
·█·
```

**3.5 — Rulers.** *(New.)* A heavier separator every 5th row and column so you can count without
losing your place, plus column numbers across the top and row numbers down the side. The alignment
arithmetic is the work: column labels reaching two digits change the gutter width, and the ruler
must land in the same place on a 10×10 as on a 3×7.
*Verify:* **Tier 1** — `GridPrinterTest#rulerAppearsEveryFifth` on a 10×10, plus a non-square case
where a transposed width and height would still produce a plausible-looking board.
*See:*
```
    0 1 2 3 4 │ 5 6 7 8 9
 0  · · █ █ · │ · █ █ · ·
 1  · █ █ █ █ │ █ █ █ █ ·
```

**3.6 — A main you can run.** *(Review — Ch1 L3 composition root. New — demo main versus composition
root.)* `BoardDemo.main` builds a hand-authored 5×5 and prints it. The one and only
`System.out.println` in the chapter.

Chapter 1's `Main` was a composition root: it constructed the object graph and started the
application. `BoardDemo` is a *demo* main — it exists to make one thing visible, and it is
disposable. Both live in `tools/`, both are the only place printing happens, and neither contains
logic worth testing except through tier 3. Knowing which kind you are writing tells you how much to
put in it.
*Verify:* **Tier 2** — `BoardRenderFlowTest` composes a real `Grid` and a real `GridPrinter` and
asserts the full rendered board, with no process launched. **Tier 3** — `BoardDemoProcessTest` uses
`ProcessProbe` from Chapter 2 to run `BoardDemo` in a real JVM and assert the exact stdout and a
zero exit code. Note that the tier 3 test is the only one that would catch a UTF-8 glyph mangled by
the platform default charset.

### Contract

```java
package com.connorjensen.nonogram;

public enum CellState { EMPTY, FILLED, MARKED }

public final class Grid {
    public Grid(int width, int height);
    public int width();
    public int height();
    public CellState get(int x, int y);
    public void set(int x, int y, CellState state);
}

public final class GridPrinter {
    public static String render(Grid grid);   // no trailing newline
}
```

```java
package com.connorjensen.nonogram.tools;

public final class BoardDemo {
    public static void main(String[] args);
}
```

### Capstone

`BoardDemo` prints a recognizable 5×5 shape — a smiley, an arrow, your initial.

**Capstone test:** all four gates. Gate 2 audits three tier 1 files (`CellStateTest`, `GridTest`,
`GridPrinterTest`), one tier 2 flow test, and one tier 3 subprocess test. Gate 3 hidden tests will
probe 1×1 grids, non-square grids where `[y][x]` errors surface, and exact glyph output. Record the
Gate 3 failure count in `JOURNAL.md`.
**Viva:** Why `[y][x]` and not `[x][y]` — what breaks if you mix them? Why does `render` return a
String instead of printing, and which Chapter 1 class taught you that? Which of your classes may
throw, and which must translate an exception instead? What would the tier 3 test catch that the
tier 2 test cannot? What in this chapter would change if you later wanted coloured nonograms?

---

# Chapters 4–14 — Goals only

Expand each one when you reach it. Each expansion owes the same thing: lessons with named
verification tests, a contract, a capstone, and viva questions — plus an explicit ladder plan
naming which files get tier 1 tests, which flows get tier 2 tests, and which main gets tier 3.

**Chapter 4 — Clues.** Derive clue arrays from a solved grid by run-length encoding, and render them
in gutters around the board. Clues are always derived from a solution, never authored, which makes
an entire category of bug impossible.

**Chapter 5 — The Brute-Force Line Solver.** Given one line and its clue, enumerate every valid
placement and intersect them to find the cells that are certain. Correct and slow, on purpose — it
becomes the reference oracle that proves the fast solver right in Chapter 14.

**Chapter 6 — Propagation.** Run the line solver over rows and columns until nothing changes.
Reaching a full solution this way proves the puzzle has exactly one answer *and* that it is
reachable by logic alone; recording each deduction in order is what later lets the browser give
hints without a solver.

**Chapter 7 — Generation.** Random fill → derive clues → propagate → keep only what solves. Seeded
and reproducible, measured rather than guessed at.

**Chapter 8 — The Terminal Game.** Play a generated puzzle to completion with moves, strokes, undo,
and hints. **The checkpoint that matters** — when this works, the whole engine is proven before a
single line of UI exists, so every bug after it is a UI bug and you will know it.

**Chapter 9 — Data on Disk.** Emit puzzles as readable JSON the browser can load, with a round-trip
test. Clues are not stored — they are derived at load time so they can never drift out of sync with
the answer.

**Chapter 10 — The Board in a Browser.** One HTML file, no build step: fetch the JSON, render a DOM
grid, play it with a mouse. The test ladder changes shape here rather than disappearing — plan its
browser equivalent as part of expanding this chapter, before writing the chapter's code.

**Chapter 11 — Touch.** Pointer events, a Fill/Mark mode toggle, drag-painting with axis lock,
stroke-level undo. Budget more time for this than the entire solver took — touch has more edge cases
than any algorithm in the course.

**Chapter 12 — Persistence and Assists.** `localStorage` saves, a timer, crosshair highlight,
auto-cross, and hints read straight from the deduction list. This is where `CellState`'s ordinals
become a file format, so the test written in Lesson 3.1 finally earns its keep.

**Chapter 13 — Shipping.** Manifest plus service worker, deployed to a free static host and
installed to your home screen. No App Store, no $99, no Xcode.

**Chapter 14 — The Fast Solver.** Rewrite the line solver as dynamic programming and prove it
correct by differential testing against the brute-force version. Doing DP last, with a trusted
reference in hand, is the best way to actually learn it.

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
nonogram/
├─ JOURNAL.md                              stuck/unstuck log and Gate 3 failure counts
├─ CHAPTER-1-REVIEW.md                     what carries over from the tracker
├─ pom.xml
├─ .githooks/prepare-commit-msg
├─ config/checkstyle/checkstyle.xml
├─ src/main/java/com/connorjensen/nonogram/
│  │                                       engine classes; no java.io, ever
│  └─ tools/                               mains, file I/O, terminal game
├─ src/test/java/com/connorjensen/nonogram/
│  │                                       mirrors the above, file for file
│  └─ ProcessProbe.java                    tier 3 harness, built in Chapter 2
├─ lessons/                                spec, learning guide, copied ch1 references
├─ puzzles/                                generated JSON, committed
└─ web/                                    index.html, manifest.json, sw.js, icons/
```

The log is `JOURNAL.md`, not `NOTES.md`. Chapter 1's repository uses `NOTES.md` for Java version
history, and two files with one name across one course is a needless trip hazard.

# Appendix C — Deferred, with the trigger that un-defers it

| Deferred | Revisit when |
|---|---|
| 15×15+ grids | 10×10 is finished and you want the pan/zoom problem |
| Canvas rendering | You are at 15×15+ and DOM repaint is visibly janky |
| React + Vite | You want menus, settings, and routing |
| IndexedDB | `localStorage` limits bite, or iOS evicts saves |
| Difficulty rating | You have a corpus you have played and can label |
| Picture puzzles | You want a PNG → 1-bit → verify-solvable pipeline |
| Capacitor / App Store | You have played the PWA for a month and still want it |
| Dirty-line queue, bitsets | You have measured a problem |
| Coloured nonograms | Everything above is done |

# Appendix D — Chapter 1 review map

What each Chapter 3 lesson assumes you already own, and where to check yourself. Full recall
prompts are in `CHAPTER-1-REVIEW.md`; the tracker repository is the answer key you open *after*
attempting.

| This course | Chapter 1 source | Status |
|---|---|---|
| 2.1 POM, coordinates, scopes, Surefire | L0 Build system | review |
| 2.2 Checkstyle, Spotless, JaCoCo | L8 Java quality standards | review |
| 2.3 Git and commit grammar | `CONTEXT.md` commit messages | review |
| 2.4 The three tiers | L7 Testing console applications | pattern known, coverage new |
| 2.5 Reading failures | L7, L8 | review, catalogued |
| 3.1 Enums | L2 Types, enums, collections | review; ordinals new |
| 3.2 Two-dimensional arrays | — | **new** |
| 3.3 Classes, constructors, guards | L1, L4 | review; domain throws new |
| 3.4 Pure renderer returning `String` | L6 `TextTable` | review |
| 3.5 Ruler alignment | — | new |
| 3.6 Runnable `main` | L3 composition root | review; demo-main distinction new |
