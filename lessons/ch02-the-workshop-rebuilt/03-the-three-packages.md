# 2.3 — The three packages

End goal is to give `model.Griddler` a real grid and derive its clues in `engine`, so `tools` can print a nonogram that the dependency rules were written to protect.

Chapter 1 taught you the direction. This lesson makes the build enforce it, and — more to the point — gives it something worth enforcing. Until now the rules in `import-control.xml` guarded three packages that barely did anything, which is why they were so easy to satisfy and so hard to believe.

`import-control.xml` and `checkstyle.xml` were rewritten and simplified before this lesson opened. Read both once before you start, comments included. The short version: your rule file is a **four-node tree** — the root package holding `Main`, then `engine`, `model`, and `tools` beneath it — and `Main` is allowed to reach `tools` precisely because wiring the program together is its entire job. The engine is not.

Each part below ends the same way, so it is stated once here rather than repeated as a step: **tier 1 tests green, `mvn -B verify` green, and one commit carrying that part's work.** Two parts, two commits, two sittings.

## Reach for

| Resource                                                                                                       | What it's for                                                                         |
|----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| [`resources/l03/`](resources/l03/)                                                                             | etc files                                                                             |
| [Nonogram](https://en.wikipedia.org/wiki/Nonogram)                                                             | Runs of filled cells, in order, separated by at least one gap                         |
| [Survey of Paint-by-Number Puzzle Solvers](https://webpbn.com/survey/)                                         | Where this goes in Chapter 3. Read the problem statement now and the algorithms later |
| [`ImportControl`](https://checkstyle.org/checks/imports/importcontrol.html)                                    | `strategyOnMismatch` — the three things your rewritten rule file leans on             |
| [`checkstyle:check`](https://maven.apache.org/plugins/maven-checkstyle-plugin/check-mojo.html)                 | What `failOnViolation` does that `failsOnError` does not                              |
| [`jacoco:check`](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html)                                     | `CLASS` elements, `COVEREDRATIO`, and what an exclusion costs you                     |
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)                                        | `@TempDir`, for testing a file write without leaving a file behind                    |
| [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)           | `List.of` and `List.copyOf`, and why a clue list wants to be unmodifiable             |
| [`java.nio.file.Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) | `writeString`, and the charset argument worth passing explicitly                      |
| [JLine](https://github.com/jline/jline3) and [Lanterna](https://github.com/mabe02/lanterna)                    | **reference, not a dependency** — what terminal rendering looks like once it grows up |

## Part 1 — a grid you can see

### The work

1. [x] **Create `model/Cell.java`.**
   1. Three constants: `FILLED`, `EMPTY`, `UNKNOWN`.
   2. Each carries the glyph it renders as.
2. [x] **Reshape `Griddler` into a grid.**
   1. **Add** `List<List<Cell>>` and one `int size`.
   2. **Remove** `UUID` and `Long size`.
   3. Reject an empty, ragged, or non-square shape.
   4. Copy on the way in and out.
3. [x] **Build a deterministic 4x4.**
   1. The same grid on every run.
   2. **Remove** the `engineName` placeholder.
4. [x] **Render the grid to a string.**
   1. `#` filled, `.` empty, `x` unknown.
   2. Return the string rather than print it.
5. [x] **Print and save it from `tools`.**
   1. `TerminalGriddler` writes to a `Path` it is given.
   2. A failed write is not silent.
   3. `Main` wires it, runs it, and prints.
6. [x] *(optional)* **Watch the rule bite.**
   1. Reach across the boundary in `GriddlerEngine`.
   2. Run `mvn -B checkstyle:check`, read it, revert.

### Must be true

- [x] `Main` prints a 4x4 grid of `#`, `.`, and `x` to the terminal
- [x] the same text is written to a file, and two runs produce identical bytes
- [x] the rendered text comes back from a method that returns it, so a test reads it without capturing stdout
- [x] no class under `engine/` or `model/` names `System`, a file, a socket, or a stream
- [x] every class you added has a tier 1 test mirroring its package, per `lessons/TESTING_STANDARDS.md`
- [x] `mvn -B verify` is green and the coverage rule passes without the number moving
- [x] Part 1 is one commit, and `git status` is clean after it

## Part 2 — a nonogram

A grid is not yet a nonogram. The puzzle is the clues: for each row and each column, the lengths of the filled runs, in order. `##.#` is `2 1`. An empty row is one clue of `0`, or an empty list — that is your call, and the test you write for it is where you make the call stick.

### The refit this part needs

`render` and `render` are two render paths sharing a private border helper, and only one of them is the program's output — `Main` calls the gutter version and nothing calls the other. They also disagree about how wide a cell is: one hardcodes three, the other measures. That is one path too many, and both of them are in `engine`.

Where rendering lives was left open in [`resources/l03/2026-09-13-model-to-runnable.md`](resources/l03/2026-09-13-model-to-runnable.md) §4, on the grounds that the split only gets decided by a second presentation. The syllabus has three of them queued: Chapter 3 replaces `#`, `.`, and `x` with `█`, `·`, and `✕`; Chapter 9 writes the same puzzle as JSON; Chapter 10 draws it in a browser. None of those three calls a method that turns a grid into a bordered ASCII block, and all three call the clue derivation. That is the decision arriving, not a preference.

So settle it here rather than in 2.5, and write the answer down with a date. `import-control.xml` will not settle it for you — a `String` is not a stream, so the rule that keeps `engine` away from files has no opinion about a renderer, and this one is yours.

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
   2. [x] One render path, in one place, named for what it does (in GriddlerEngine).
   3. [x] `engine` keeps the clue derivation; Chapter 3's solver is what lands next to it.
   4. [x] Clues read toward the grid — bottom-aligned above it, right-aligned beside it.
8. [x] **Move argument parsing out of the composition root.**
   1. It landed in `tools`, which pulls 2.4's question about the `Main` exclusion forward. Where the default output path belongs is still open.

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
- [x] Part 2's two commits have landed, and `git status` is clean after them

## Close out

**Did it land**

1. Why may `Main` import `tools` when `engine` may not?
  A. `Main` can import `tools` because `Main` is not critiqued the same as `engine` is. `Main` is a class that exists much as a shell currently that is constantly changing and has content that could be classified into various other parts of the project but AREN'T YET. This means that we should allow `Main.java` more leniency when it comes to how it uses dependencies because it doesn't directly feed into or feed off of different areas of the project, merely a launcher for the project locally. 
2. `import-control.xml` declares the three subpackages before the root's `<file>` block. What breaks if you swap that order?
  A. `import-control.xml` declares the subpackages before root `file` block because `import-control.xml` only cares about the top-to-bottom FIRST declaration of allow/disallow. If we put the root file's allow's at the top it would break in two ways, A. `<file>` for root has to come after all `<subpackages>` blocks (DTD/syntax breakage), and B. rules declared above may conflict with other subpackage's rules below the root file's rules.
3. The engine cannot open a file. Name one thing that buys your test suite.
  A. engine not being allowed to use I/O, Files, and Net alllows the subpackage to focus on the core generation of the Nonograms/Cells which makes the tests purely about testing the generation, and not about delivery/distribution/consumption, just about generation (and printing) 
4. You removed `UUID` from `Griddler`. What would keeping it have cost the test you wrote in Part 1?
  A. Don't know; believe this is stale/resolved. 

**For the next lesson**

5. 2.4 runs `Main` in a real JVM and asserts its output. `Main` now takes a size argument — should 2.4 assert stdout for one fixed size, or go straight to reading input interactively?
  A. bring this question back with more context, but believe this might be resolved now. currently there are two methods of using `Main.java`; A) non-interactive, it uses defaults values for the output directory, size, and randomization (tempdir, 4, false). sitting in Main.java currently but looking to move it to `tools/` for preservation/frameworking it in a more permenant place. 
6. Chapter 3 owns the Unicode glyph choice (`█`, `·`, `✕`, `▓`). Keep ASCII until then, or pull that decision forward into 2.5?
  A. Pull it forwards if unresolved. This should be before the end of chapter 2 as chapter 3 and beyond (assumed) will be moving beyond simple terminal output and into some form of JavaFX or Spring Boot/API so assuming it won't matter much after chapter 02??  
7. `Griddler` is square by construction, and Chapter 3's solver survey works on rectangular puzzles. Does it stay square through Chapter 2, or take a width and a height in 2.5?
  A. Currently just based on preference, in 2.5 we can do rectangular inputs, but should be an optional constructor for it so that both `Griddler(10)` and `Griddler(10, 10)` can produce square results.
8. `gutters` counts `UNKNOWN` as a gap, so clues derived from a half-solved grid are wrong and nothing complains. Does the model owe you a second type — the puzzle's solution against the board a player is working on — and is that 2.5's or Chapter 3's?
   A. the `Cell.UNKNOWN` status is there as a player marker status, not really important for the backend "solved" puzzles, but inclued for the states that cells can be in on a player Nonogram grid (player note value). I believe that a solved nonogram grid will consist of only the first two `Cell` values (`EMPTY` and `FILLED`) but explain if not or why it might be useful to have more?

**[!?] On another note, we probably should have a lesson (either directly after this one or before the capstone that goes over how to have "Seeded randomness" so that if I give a certain ID or Key or Hash, it will produce the same result. I don't quite get what exaclty it will take/cost/change but I want to learn how (for the end product) we would be able to "seed" the nonogram.**
