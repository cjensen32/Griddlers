# 2.3 — The three packages

End goal is to give `model.Griddler` a real grid and derive its clues in `engine`, so `tools` can print a nonogram that the dependency rules were written to protect.

Chapter 1 taught you the direction. This lesson makes the build enforce it, and — more to the point — gives it something worth enforcing. Until now the rules in `import-control.xml` guarded three packages that barely did anything, which is why they were so easy to satisfy and so hard to believe.

`import-control.xml` and `checkstyle.xml` were rewritten and simplified before this lesson opened. Read both once before you start, comments included. The short version: your rule file is a **four-node tree** — the root package holding `Main`, then `engine`, `model`, and `tools` beneath it — and `Main` is allowed to reach `tools` precisely because wiring the program together is its entire job. The engine is not.

Each part below ends the same way, so it is stated once here rather than repeated as a step: **tier 1 tests green, `mvn -B verify` green, and the part's work committed.** Three parts. Parts 1 and 2 built the thing; Part 3 closes it, and it was added on 2026-09-19 when the lesson was read back against the code and several of its boxes turned out to be ticked against statements the tree contradicts.

On commits: a commit ends at a green `mvn -B verify`, not at a time of day. Part 3 is more than one commit, and [`resources/l03/the-refit-handoff.md`](resources/l03/the-refit-handoff.md) §10 has the split.

## Reach for

| Resource                                                                                                       | What it's for                                                                         |
|----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| [`resources/l03/the-refit-handoff.md`](resources/l03/the-refit-handoff.md)                                     | where this lesson stands, what is left, and the commit split that closes it           |
| [`resources/test-shapes.md`](resources/test-shapes.md)                                                         | what a finished test file looks like for an enum, a record, a class, or an interface  |
| [`resources/changing-code.md`](resources/changing-code.md)                                                     | moving code that already works, and reading a failure before reading the code         |
| [Nonogram](https://en.wikipedia.org/wiki/Nonogram)                                                             | Runs of filled cells, in order, separated by at least one gap                         |
| [Survey of Paint-by-Number Puzzle Solvers](https://webpbn.com/survey/)                                         | Where this goes in Chapter 3. Read the problem statement now and the algorithms later |
| [`ImportControl`](https://checkstyle.org/checks/imports/importcontrol.html)                                    | `strategyOnMismatch` — the three things your rewritten rule file leans on             |
| [`checkstyle:check`](https://maven.apache.org/plugins/maven-checkstyle-plugin/check-mojo.html)                 | What `failOnViolation` does that `failsOnError` does not                              |
| [`jacoco:check`](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html)                                     | `CLASS` elements, `COVEREDRATIO`, and what an exclusion costs you                     |
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)                                        | `@TempDir`, for testing a file write without leaving a file behind                    |
| [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)           | `List.of` and `List.copyOf`, and why a clue list wants to be unmodifiable             |
| [`java.nio.file.Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) | `writeString`, and the charset argument worth passing explicitly                      |
| [`java.util.Random`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Random.html)       | the reproducibility a seed actually guarantees, and across what                       |
| [JLine](https://github.com/jline/jline3) and [Lanterna](https://github.com/mabe02/lanterna)                    | **reference, not a dependency** — what terminal rendering looks like once it grows up |

## Part 1 — a grid you can see

### The work

1. [x] **Create `model/Cell.java`.**
   1. Three constants: `FILLED`, `EMPTY`, `UNKNOWN`.
   2. Each carries the glyph it renders as.
2. [x] **Reshape `Griddler` into a grid.**
   1. **Add** `List<List<Cell>>` and the dimensions the grid needs. *(Built as two: `width` and `height`.)*
   2. **Remove** `UUID` and `Long size`.
   3. Reject an empty or ragged shape.
   4. Copy on the way in and out.
   5. *(landed early)* Rectangles are accepted, not only squares. That was close-out question 7's decision, made here rather than in 2.5.
3. [x] **Build a deterministic 4x4.**
   1. The same grid on every run.
   2. **Remove** the `engineName` placeholder.
4. [x] **Render the grid to a string.**
   1. `#` filled, `.` empty, `x` unknown.
   2. Return the string rather than print it.
5. [x] **Print and save it from `tools`.**
   1. `TerminalGriddler` writes to a `Path` it is given.
   2. A failed write is not silent — `write` throws rather than swallowing. *(The behaviour is there; nothing tests it yet. Part 3 item 6.)*
   3. `Main` wires it, runs it, and prints.
6. [x] *(optional)* **Watch the rule bite.**
   1. Reach across the boundary in `GriddlerEngine`.
   2. Run `mvn -B checkstyle:check`, read it, revert.

### Must be true

- [x] `Main` prints a grid to the terminal, and from Part 2 onward that grid carries its clue gutters
- [x] the renderer maps `#` to filled, `.` to empty and `x` to unknown, and a tier 1 test pins all three
- [ ] `Cell.UNKNOWN` is reachable from something other than a test fixture, or it is written down as a state the model does not need yet — **neither generator emits it, so `Main` cannot print an `x`** (Part 3 item 7, and close out Q8)
- [x] the same text is written to a file, and two runs produce identical bytes
- [x] the rendered text comes back from a method that returns it, so a test reads it without capturing stdout
- [x] no class under `engine/` or `model/` names `System`, a file, a socket, or a stream
- [x] every class you added has a tier 1 test mirroring its package, per `lessons/TESTING_STANDARDS.md`
- [x] `mvn -B verify` is green and the coverage rule passes — `LINE` and `BRANCH`, both `0.9` since `C049`
- [x] Part 1's work is committed, and `git status` is clean after it

## Part 2 — a nonogram

A grid is not yet a nonogram. The puzzle is the clues: for each row and each column, the lengths of the filled runs, in order. `##.#` is `2 1`. An empty row is one clue of `0`, or an empty list — that is your call, and the test you write for it is where you make the call stick.

*You made it: `deriveClueList` returns an empty list, and `cluesDerivesLineClues` holds the case that pins it.*

### The refit this part needed

*Resolved in Part 2. Kept because the reasoning is the answer to the last `Must be true` box below, and a decision without its reasoning is a coin flip you cannot defend later.*

There were two render paths sharing a private border helper, and only one of them was the program's output — `Main` called the gutter version and nothing called the other. They also disagreed about how wide a cell is: one hardcoded three, the other measured. That was one path too many, and both were in `engine`.

Where rendering lives had been left open on the grounds that the split only gets decided by a second presentation. The syllabus has three of them queued: Chapter 3 replaces `#`, `.`, and `x` with `█`, `·`, and `✕`; Chapter 9 writes the same puzzle as JSON; Chapter 10 draws it in a browser. None of those three calls a method that turns a grid into a bordered ASCII block, and all three call the clue derivation. That was the decision arriving, not a preference.

`import-control.xml` did not settle it — a `String` is not a stream, so the rule that keeps `engine` away from files has no opinion about a renderer. It was settled here, and it stays settled until a second presentation actually exists.

### The work

1. [x] **Derive the row clues.**
2. [x] **Derive the column clues.**
3. [x] **Add clue gutters to the render.**
   1. Column clues above, row clues to the left.
4. [x] **Take a size argument in `Main`.**
   1. `-s 10` renders a 10x10, not only the 4x4.
5. [x] **Green the build before you move anything.**
   1. The unused import first, then what the plain-render test is for.
6. [x] **Render a ragged grid.**
   1. Rows with different clue counts are the ordinary case, not the edge.
7. [x] **Settle where rendering lives, and leave `Main` printing.**
   1. [x] `Main` prints the string it holds, rather than reading the file back.
   2. [x] One render path, in one place, named for what it does (in `GriddlerEngine`).
   3. [x] `engine` keeps the clue derivation; Chapter 3's solver is what lands next to it.
   4. [x] Clues read toward the grid — bottom-aligned above it, right-aligned beside it.
8. [x] **Move argument parsing out of the composition root.**
   1. [x] It landed in `tools`, which pulls 2.4's question about the `Main` exclusion forward.
   2. [ ] Where the default output path is built is still open, and it is still in `Main`. Part 3 item 2.

### Must be true

- [x] `GriddlerEngine` returns row clues and column clues for any grid it builds
- [x] the rendered output shows both gutters, and a hand-checked 4x4 matches what you expected
- [x] `Main` accepts a size argument and `10` prints a 10x10
- [x] a tier 1 test covers an empty row, a fully filled row, and a row with two separated runs
- [x] the clue methods are covered by assertions on returned values, not by a test that only runs them
- [x] a grid whose rows carry different clue counts renders instead of throwing, and a tier 1 test holds that grid
- [x] every class you moved or extracted meets the same coverage gate as the rest, or the POM says in one line why it does not
- [x] rendering has one home and one path, and you can say in one sentence why that home is not `engine`
- [x] `mvn -B verify` is green and the coverage rule (`LINE` and `BRANCH`, both `0.9`) still passes
- [x] Part 2's work is committed, and `git status` is clean after it

## Part 3 — closing it out

*Added 2026-09-19.* Parts 1 and 2 built a program that works. Everything here is something the code does not yet do, or does in a way that will cost you in 2.4 — and 2.4 is the lesson that freezes `Main`'s entire stdout into a golden string, so anything that changes stdout wants to land on the near side of that line.

Every item is stated as an outcome. How to get there is yours; [`resources/l03/the-refit-handoff.md`](resources/l03/the-refit-handoff.md) §5 has the evidence behind each one, with line numbers.

### The work

1. [ ] **`Main` prints before it writes, and says where it wrote.**
   1. A failed write currently means exit 1 and zero bytes of stdout, with the rendered grid discarded unseen. 2.4 asserts the exit code and stdout together.
   2. The destination goes somewhere that does not pollute the golden stdout.
2. [ ] **A run leaves behind only what it was asked to write.**
   1. The default output directory is created before the arguments are parsed, so a run passing `-o` creates one it never uses, and nothing removes them.
3. [ ] **A randomized run is reproducible from a value the caller supplies.**
   1. The engine already takes a `Random`; nothing supplies a seeded one.
   2. This is the `[!?]` at the bottom of this file, and it is what makes close-out question 5 answerable.
4. [ ] **Every name describes what the thing does.**
   1. `TerminalGriddler` writes a file and never touches a terminal.
   2. `CellTest`'s three method names still say `AsciiStr` after the accessor was renamed.
   3. `GriddlerTest.getSizeReturnsGridDimension` asserts a width, and `Griddler` has no size.
5. [ ] **The two branches JaCoCo still reports missed are covered.**
   1. A null row that is not the first row.
   2. A size below the minimum — note which invalid sizes the flag parser rejects before the range check ever sees them.
6. [ ] **A failed write and a null destination each have a test.**
   1. Part 1 item 5.2 is ticked for behaviour that nothing asserts.
7. [ ] **`Cell.UNKNOWN` is produced by something, or recorded as a state the model does not need yet.**
   1. Whichever you choose, a test named for the decision is what makes it findable. See close out Q8.
8. [ ] **The suite obeys its own rubric.**
   1. No shared writable static fixture — `lessons/TESTING_STANDARDS.md` forbids it by name, and three fields currently are one.
   2. Test-class visibility is consistent across the six test files, rather than split three to three.
   3. The duplicate cases and the dead fixture are gone. Deleting a test is maintenance, not loss.

### Must be true

- [ ] one tier 2 flow test wires parse → generate → clues → render → write with real objects, the way `Main` wires them, per `lessons/TESTING_STANDARDS.md` — there is no tier 2 test anywhere in the repo, and without one 2.4's subprocess is the first thing that ever exercises the whole path
- [ ] a randomized run with the same seed produces identical stdout twice, and no `Random` in `src/main` is constructed without one
- [ ] a run that names its own output path leaves nothing behind that it did not write
- [ ] a failed write fails loudly, and a test proves it rather than the code merely allowing it
- [ ] `mvn -B verify` is green with `LINE` and `BRANCH` both at `0.9`, and every coverage exclusion in `pom.xml` has a sentence saying why it is there
- [ ] every close-out question below is answered, and no box anywhere in this file is ticked against a statement the tree contradicts
- [ ] each commit in Part 3 left `mvn -B verify` green, and `git status` is clean after the last one

## Close out

Four answers were blanked on 2026-09-19 because they were wrong, stale, or a deferral. Each carries a **[!]** note saying what the previous attempt got wrong and what to reconsider — the note is not the answer, and writing the answer is still yours.

**Did it land**

1. Why may `Main` import `tools` when `engine` may not?

  A.

  **[!]** *Your earlier answer had this backwards. It argued `Main` gets leniency because it is an unsettled shell whose contents will move elsewhere later — which would mean the permission expires once `Main` settles down. The opposite is true: the permission is permanent and definitional. Read the comment above the root `<allow>` lines in `import-control.xml`, then say what job `Main` has that `engine` does not.*

2. `import-control.xml` declares the three subpackages before the root's `<file>` block. What breaks if you swap that order?

  A.

  **[!]** *Half of your earlier answer was right, and it was the half you listed second. Both halves were re-tested on 2026-09-19 by reordering the real file and running `mvn -B checkstyle:check`.*

  *You wrote that the root `<file>` block "has to come after all `<subpackage>` blocks (DTD/syntax breakage)". It does not. The DTD's content model is `((allow|disallow)*,(subpackage|file)*)` — `subpackage` and `file` share a group and may interleave freely. What the DTD forbids is `<file>` before `<allow>`, and that reorder fails to parse with exactly that message.*

  *Your half B is the real one, and it is worse than "may conflict". Hoisting the root `<file name=".*Test">` above the `<subpackage>` blocks is perfectly legal XML and produces **19 violations**, because the root test rule then claims every `*Test` file in every subpackage before the subpackage can, and those files lose `java.nio` and `model`.*

  *Rewrite the answer around that: the dangerous reorder is the one the DTD lets through, and what protects you is node precedence, not syntax.*

3. The engine cannot open a file. Name one thing that buys your test suite.

  A. engine not being allowed to use I/O, Files, and Net alllows the subpackage to focus on the core generation of the Nonograms/Cells which makes the tests purely about testing the generation, and not about delivery/distribution/consumption, just about generation (and printing)

  **[!]** *Right, and worth one correction at the end: `engine` does not print. It returns a `String` and `Main` prints it. That distinction is the entire reason the rule is satisfiable — a renderer that printed could not live in a package barred from streams.*

4. You removed `UUID` from `Griddler`. What would keeping it have cost the test you wrote in Part 1?

  A. UUID would have made the testing comparisons non-deterministic because UUID is randomly generated, and the way it was held meant generating a new one every time a new instance was made, making it impossible to compare them easily.

  **[!]** *Right, and one step from exact. "Impossible to compare" is the symptom; name the mechanism and it generalises. `Griddler` is a class with no `equals`, so its tests already compare through `getCells()` and `getCell()`. `Gutter` is a record, so it gets value equality generated, which is why one `assertEquals` can compare a whole expected `Gutter` against a derived one. An identity field is what permanently puts a type in the first category — and it is why you could never give `Griddler` the shape `Gutter` has.*

**For the next lesson**

5. 2.4 runs `Main` in a real JVM and asserts its output. `Main` now takes a size argument — should 2.4 assert stdout for one fixed size, or go straight to reading input interactively?

  A.

  **[!]** *Your earlier answer deferred this — "bring this question back with more context". It is answerable from the checkout now, and the material is in front of you: `Main` has two paths. The default is fully deterministic and safe to freeze into a golden string. The `-r` path constructs an unseeded `Random` and cannot be asserted at all. So the real question is narrower than the one written above — does 2.4 freeze the deterministic path, or does Part 3 item 3 land first and make both assertable?*

6. Chapter 3 owns the Unicode glyph choice (`█`, `·`, `✕`, `▓`). Keep ASCII until then, or pull that decision forward into 2.5?

  A.

  **[!]** *Your earlier answer rested on a premise the syllabus contradicts. It assumed Chapter 3 and beyond move off the terminal into JavaFX or a Spring API, so the glyphs would stop mattering after Chapter 2. `lessons/SYLLABUS.md` has Chapter 3 as "Seeing the Board" — a terminal renderer — and the browser does not arrive until Chapter 10. Nothing leaves the terminal for seven more chapters, so the glyphs matter more after Chapter 2, not less.*

  *The argument actually worth weighing: 2.4's "Reach for" section puts all four Appendix A glyphs in `HarnessProbe`, not in `Main`, so 2.4 proves your UTF-8 handling without `Main` printing a single non-ASCII character. Deferring costs you one line in an enum. Decide on that, and write down which you chose.*

7. `Griddler` is square by construction, and Chapter 3's solver survey works on rectangular puzzles. Does it stay square through Chapter 2, or take a width and a height in 2.5?

  A.

  **[!]** *The question has been overtaken, and your earlier answer with it. Rectangles landed here, in 2.3: `Griddler` carries `width` and `height` and accepts a 5×4 today. And neither `Griddler(10)` nor `Griddler(10, 10)` exists — the only constructor takes a `List<List<Cell>>`, so the shape comes from the cells rather than from a pair of numbers.*

  *The live question is smaller and worth answering in its place: `alternatingCells` takes a width and a height, `randomizeCells` takes one size. The two generators disagree about whether rectangles are real. Which one is wrong?*

8. `clues` counts `UNKNOWN` as a gap, so clues derived from a half-solved grid are wrong and nothing complains. Does the model owe you a second type — the puzzle's solution against the board a player is working on — and is that 2.5's or Chapter 3's?

  A. the `Cell.UNKNOWN` status is there as a player marker status, not really important for the backend "solved" puzzles, but inclued for the states that cells can be in on a player Nonogram grid (player note value). I believe that a solved nonogram grid will consist of only the first two `Cell` values (`EMPTY` and `FILLED`) but explain if not or why it might be useful to have more?

  **[!]** *Right, and it is now recorded in code: `deriveClueList` treats anything that is not `FILLED` as a gap, and `cluesDerivesLineClues` pins that with a `[FILLED, UNKNOWN, FILLED] -> [1, 1]` case. What is missing is the name — the decision lives inside a general case, so a reader finds it by accident. Still yours to answer: which of the two types is 2.5's and which is Chapter 3's.*

### Answering the question you asked back in 8

*"I believe that a solved nonogram grid will consist of only the first two `Cell` values but explain if not or why it might be useful to have more."*

**A solved grid needs exactly two states.** You have that right, and it is why `deriveClueList` can treat everything that is not `FILLED` as a gap without ever being wrong about a finished puzzle.

**A player's board needs more than three,** and Appendix A of `.agents/reference/course-rationale.md` already says so. Its four glyphs are Filled `█`/`#`, Empty `·`/`.`, Marked `✕`/`X`, and Deduced-this-pass `▓`/`+`. Notice what is not in that table: there is no "unknown" row. Then notice what `Cell.UNKNOWN` actually carries — `"x"`, which is Marked's ASCII fallback in lowercase. The constant is named for one concept and glyphed as another.

**The two are genuinely different cells.** *Marked* is a player's assertion: "I have worked out that this is empty." *Unknown* is the absence of an assertion: "I have not looked at this yet." A solver needs to tell them apart, because one is information it can propagate and the other is the hole it is trying to fill. A renderer needs to tell them apart, because `·` cannot legibly mean both "empty, proven" and "untouched". That is the second type your question is circling, and it is why the answer is "more than three" rather than "more than two".

**Nothing in the program produces `UNKNOWN` today.** Neither generator emits it; it is reachable only from test fixtures. A constant no execution path can emit is either a state the model has not grown into yet or it is dead, and Part 3 item 7 is where that gets decided rather than drifting.

The fourth glyph, `▓`, is Chapter 3's solver showing its working — cells it deduced on the current pass, so you can watch it think. Not yours to build.

## An aside, answered

**[!?] On another note, we probably should have a lesson (either directly after this one or before the capstone) that goes over how to have "Seeded randomness" so that if I give a certain ID or Key or Hash, it will produce the same result. I don't quite get what exactly it will take/cost/change but I want to learn how (for the end product) we would be able to "seed" the nonogram.**

It does not need a lesson, because you have already built the half that needed one.

**What "seeding" means.** The randomness arrives as a parameter instead of being created on the spot. `new Random(42)` produces the identical sequence of numbers on every machine, every run, forever — that is a documented guarantee of `java.util.Random`, not an accident of the implementation you happen to be running.

**What you already have.** `randomizeCells` takes a `Random` rather than constructing one, and a parameterized test asserts the exact grid for five different seeds. That is the part that required a design change, and it is done — it is also what turned the weakest test in the suite, which could only check that the grid was the right *shape*, into one that checks every cell.

**What is missing.** A caller that supplies a seeded generator. `Main` still constructs a bare `new Random()`, and the flag parser has no `--seed`. One flag and one field on the parsed-arguments record.

**What it costs and buys.** Cost: one flag, one field. Immediately: close-out question 5 becomes answerable, and a randomized run becomes something 2.4 can assert. Later: a puzzle identified by a number you can text to someone — which is Chapter 9's "regenerate a puzzle from a seed", arriving six chapters early and for free.

**The one caveat.** Seeding the generator only helps if nothing downstream reintroduces ambiguity. `new Random(seed)` is reproducible across machines and JDK versions; `Math.random()`, a bare `new Random()`, and `HashSet` iteration order are not. [`resources/changing-code.md`](resources/changing-code.md) §5 has this alongside the same argument about file destinations, which is the same rule wearing different clothes: *a boundary that cannot be pointed somewhere else cannot be tested.*

It lands as Part 3 item 3.
