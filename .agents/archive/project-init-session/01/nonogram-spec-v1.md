# Nonogram — Course Spec

**Version:** 0.4
**Companion:** `nonogram-learning-guide.md` — how to work. Read it first.
**Shape:** 13 chapters. Chapters 0 and 1 are fully specified. Everything after is a goal statement only.

**Expand one chapter at a time.** When you finish a capstone, write out the next chapter's lessons yourself — that planning is part of the work, and a lesson breakdown written before you understand the problem is usually wrong anyway. Bring the stub here and expand it when you get there.

---

## How this course works

### Three standing rules

**1. The terminal renderer comes before everything else.** Chapter 1 builds a board printer with no clues, no solver, no puzzle behind it. From then on, every single thing you build has a way to be *looked at*, and "print the board and stare at it" is your primary debugging tool. Most nonogram bugs are visible on sight and invisible in a stack trace.

**2. Tests ship with the lesson, not after the chapter.** Every lesson names its verification test. Write it in the same sitting as the code. A lesson isn't done when the code runs; it's done when its test is green.

**3. Functions return Strings; only `main` prints.** `GridPrinter.render(grid)` returns text. `System.out.println` appears in exactly one place per program. This convention is what makes visual output testable — you can assert on a rendered board character-for-character. Break it and half the tests in this spec become impossible to write.

### The capstone protocol

Each chapter ends with something that works and that you can see. Validating it runs four gates in order; a failure means revise and re-run from that gate.

**Gate 1 — Demo.** Run the chapter's command, paste the output. It must match the described visual.

**Gate 2 — Your tests.** `mvn test` green, and every test named in the chapter's lessons exists.

**Gate 3 — Hidden tests.** The agent writes fresh tests against the chapter's **API contract** — signatures only, never your implementation. It writes them to a file; you run them and read *only failure names and messages*, never the test bodies. Every failure is a real bug you fix yourself.

This is the gate that actually establishes validity. Gate 2 only proves your code passes tests you thought of, which is precisely the blind spot worth finding.

**Gate 4 — Viva.** The agent asks the chapter's questions. You answer from memory, editor closed.

### The prompt to paste

> I've finished Chapter N of my Nonogram course. Grade it in four gates, in order, and stop at the first failure.
>
> **Gate 1:** My terminal output — [paste]. Expected visual — [paste from spec].
>
> **Gate 2:** My test list and `mvn test` output — [paste]. Check every test named in the chapter's lessons exists.
>
> **Gate 3:** Here is the API contract — [paste contract block]. Write 12–15 JUnit 5 tests against these signatures targeting this chapter's edge cases, plus at least 3 cases you think I probably didn't consider. Write them to `src/test/java/nonogram/CapstoneNTest.java`. Do not explain them, do not summarize what they cover, and do not look at or ask for my implementation.
>
> **Gate 4:** Ask me these questions one at a time and judge my answers: [paste viva questions].
>
> **Rules for you:** Do not write, rewrite, or show me any implementation code at any point. If I fail a gate, tell me *what* is wrong and *which concept* to revisit — not how to fix it. If I ask for the fix, refuse and restate the concept.

Gate 3 runs on the honor system. Reading the hidden tests is possible; it just means the gate stops testing anything.

### Notation

- **Contract** — public signatures for the chapter. You write the bodies; the agent writes tests against them. Keep them stable once a chapter starts, or Gate 3 won't compile.
- **Verify** — the test that closes a lesson.
- **See** — what appears in your terminal when it works.

---

# Chapter 0 — Workshop Setup

**Goal:** a Maven project with a green test and a git history, so "run the tests" is already a reflex before there's any code worth testing.

### Lessons

**0.1 — The project.** `mvn archetype:generate` or a hand-written `pom.xml`. Java 17, package `nonogram`. Understand what `pom.xml` declares, and why `src/main/java` and `src/test/java` are separate trees.
*Verify:* `mvn compile` succeeds.

**0.2 — JUnit 5.** Add `junit-jupiter` test-scoped, plus the Surefire plugin. Write `SetupTest` asserting `2 + 2 == 4`.
*Verify:* `mvn test` reports 1 test, 0 failures.
*See:* the Surefire summary block.

**0.3 — Git.** `git init`, a `.gitignore` covering `target/`, first commit. Learn `status`, `add`, `commit`, `log`, `diff`.
*Verify:* `git log` shows your commit; `git status` is clean.

**0.4 — The failure you'll see constantly.** Deliberately break the test (`2 + 2 == 5`). Read the entire failure output. Locate the assertion message, expected vs. actual, line number, class name. Then fix it.
*Verify:* you can point at each part of a JUnit failure and say what it means.

### Capstone

A repo where `mvn test` is green and one commit exists.

**Capstone test:** Gates 2 and 4 only.
**Viva:** What does test *scope* mean on a dependency? What's in `target/`, and why isn't it committed? What's the difference between `mvn compile` and `mvn test`?

---

# Chapter 1 — Seeing the Board

**Goal:** print a nonogram grid to the terminal. No clues, no solving, no puzzle — just a board you can look at. Everything in the rest of the course is debugged through this output.

### Lessons

**1.1 — Cell states.** `enum CellState { EMPTY, FILLED, MARKED }`. Write down why MARKED is player bookkeeping and never part of a solution.
*Verify:* `CellStateTest` asserting `values().length == 3` and the ordinal order — you'll depend on those ordinals when you serialize saves later.

**1.2 — The indexing convention.** `CellState[][] cells` indexed `[y][x]`. Row first, always. Put it in a comment at the top of the class.
*Verify:* `GridTest#getSetRoundTrip` — set (x=3, y=1), assert it reads back at (3,1) and that (1,3) is untouched. This test catches the transposition bug that will otherwise haunt you for a week.

**1.3 — The Grid class.** Constructor takes width and height and fills with EMPTY. `get(x, y)`, `set(x, y, state)`, `width()`, `height()`. Bounds checks that throw `IndexOutOfBoundsException` with a message naming the offending coordinate.
*Verify:* `GridTest#outOfBoundsThrows` for all four edges, asserting the message contains the bad value.

**1.4 — The renderer.** `GridPrinter.render(Grid) → String`. One character per cell, `\n` between rows, no trailing newline.

Glyphs: `█` filled, `·` empty, `✕` marked. If your terminal mangles Unicode, use `#`, `.`, `X`. Pick a set now — changing it later breaks every string-match test you write.
*Verify:* `GridPrinterTest#rendersKnownGrid` — hand-build a 3×3, assert the exact expected string.
*See:*
```
·█·
███
·█·
```

**1.5 — Rulers.** A heavier separator every 5th row and column so you can count without losing your place, plus column numbers across the top and row numbers down the side.
*Verify:* `GridPrinterTest#rulerAppearsEveryFifth` on a 10×10.
*See:*
```
    0 1 2 3 4 │ 5 6 7 8 9
 0  · · █ █ · │ · █ █ · ·
 1  · █ █ █ █ │ █ █ █ █ ·
```

**1.6 — A main you can run.** `BoardDemo.main` builds a hand-authored 5×5 and prints it. The one and only `System.out.println` in the chapter.
*Verify:* run it; see a shape.

### Contract

```java
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

### Capstone

`BoardDemo` prints a recognizable 5×5 shape — a smiley, an arrow, your initial.

**Capstone test:** all four gates. Hidden tests will probe 1×1 grids, non-square grids (where `[y][x]` errors surface), and exact glyph output.
**Viva:** Why `[y][x]` and not `[x][y]` — what breaks if you mix them? Why does `render` return a String instead of printing? What in this chapter would change if you later wanted colored nonograms?

---

# Chapters 2–12 — Goals only

Expand each one when you reach it.

**Chapter 2 — Clues.** Derive clue arrays from a solved grid by run-length encoding, and render them in gutters around the board. Clues are always derived from a solution, never authored, which makes an entire category of bug impossible.

**Chapter 3 — The Brute-Force Line Solver.** Given one line and its clue, enumerate every valid placement and intersect them to find the cells that are certain. Correct and slow, on purpose — it becomes the reference oracle that proves the fast solver right in Chapter 12.

**Chapter 4 — Propagation.** Run the line solver over rows and columns until nothing changes. Reaching a full solution this way proves the puzzle has exactly one answer *and* that it's reachable by logic alone; recording each deduction in order is what later lets the browser give hints without a solver.

**Chapter 5 — Generation.** Random fill → derive clues → propagate → keep only what solves. Seeded and reproducible, measured rather than guessed at.

**Chapter 6 — The Terminal Game.** Play a generated puzzle to completion with moves, strokes, undo, and hints. **The checkpoint that matters** — when this works, the whole engine is proven before a single line of UI exists, so every bug after it is a UI bug and you'll know it.

**Chapter 7 — Data on Disk.** Emit puzzles as readable JSON the browser can load, with a round-trip test. Clues aren't stored — they're derived at load time so they can never drift out of sync with the answer.

**Chapter 8 — The Board in a Browser.** One HTML file, no build step: fetch the JSON, render a DOM grid, play it with a mouse. Testing shifts from JUnit to browser checks here.

**Chapter 9 — Touch.** Pointer events, a Fill/Mark mode toggle, drag-painting with axis lock, stroke-level undo. Budget more time for this than the entire solver took — touch has more edge cases than any algorithm in the course.

**Chapter 10 — Persistence and Assists.** localStorage saves, a timer, crosshair highlight, auto-cross, and hints read straight from the deduction list.

**Chapter 11 — Shipping.** Manifest plus service worker, deployed to a free static host and installed to your home screen. No App Store, no $99, no Xcode.

**Chapter 12 — The Fast Solver.** Rewrite the line solver as dynamic programming and prove it correct by differential testing against the brute-force version. Doing DP last, with a trusted reference in hand, is the best way to actually learn it.

---

# Appendix A — Glyphs

| Meaning | Unicode | ASCII fallback |
|---|---|---|
| Filled | `█` | `#` |
| Empty | `·` | `.` |
| Marked | `✕` | `X` |
| Deduced this pass | `▓` | `+` |

Choose in Chapter 1 and don't revisit it.

# Appendix B — Repository layout

```
nonogram/
├─ NOTES.md          stuck/unstuck log — see the learning guide
├─ pom.xml
├─ src/main/java/nonogram/       engine classes; no java.io, ever
│  └─ tools/                     mains, file I/O, terminal game
├─ src/test/java/nonogram/       mirrors the above
├─ puzzles/                      generated JSON, committed
└─ web/                          index.html, manifest.json, sw.js, icons/
```

# Appendix C — Deferred, with the trigger that un-defers it

| Deferred | Revisit when |
|---|---|
| 15×15+ grids | 10×10 is finished and you want the pan/zoom problem |
| Canvas rendering | You're at 15×15+ and DOM repaint is visibly janky |
| React + Vite | You want menus, settings, and routing |
| IndexedDB | localStorage limits bite, or iOS evicts saves |
| Difficulty rating | You have a corpus you've played and can label |
| Picture puzzles | You want a PNG → 1-bit → verify-solvable pipeline |
| Capacitor / App Store | You've played the PWA for a month and still want it |
| Dirty-line queue, bitsets | You have measured a problem |
| Colored nonograms | Everything above is done |
