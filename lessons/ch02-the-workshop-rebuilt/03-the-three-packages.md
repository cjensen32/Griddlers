# 2.3 — The three packages

End goal is to give `model.Griddler` a real grid and derive its clues in `engine`, so `tools` can print a nonogram that the dependency rules were written to protect.

Chapter 1 taught you the direction. This lesson makes the build enforce it, and — more to the point — gives it something worth enforcing. Until now the rules in `import-control.xml` guarded three packages that barely did anything, which is why they were so easy to satisfy and so hard to believe.

`import-control.xml` and `checkstyle.xml` were rewritten and simplified before this lesson opened. Read both once before you start, comments included. The short version: your rule file is a **four-node tree** — the root package holding `Main`, then `engine`, `model`, and `tools` beneath it — and `Main` is allowed to reach `tools` precisely because wiring the program together is its entire job. The engine is not.

Each part below ends the same way, so it is stated once here rather than repeated as a step: **tier 1 tests green, `mvn -B verify` green, and one commit carrying that part's work.** Two parts, two commits, two sittings.

## Reach for

| Resource                                                                                                       | What it's for                                                                                           |
|----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| [Nonogram](https://en.wikipedia.org/wiki/Nonogram)                                                             | the clue rule you implement in Part 2 — runs of filled cells, in order, separated by at least one gap   |
| [Survey of Paint-by-Number Puzzle Solvers](https://webpbn.com/survey/)                                         | where this goes in Chapter 3. Read the problem statement now and the algorithms later                   |
| [`ImportControl`](https://checkstyle.org/checks/imports/importcontrol.html)                                    | `strategyOnMismatch`, `<subpackage>`, and `<file>` — the three things your rewritten rule file leans on |
| [`checkstyle:check`](https://maven.apache.org/plugins/maven-checkstyle-plugin/check-mojo.html)                 | `includeTestSourceDirectory`, and what `failOnViolation` does that `failsOnError` does not              |
| [`jacoco:check`](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html)                                     | `CLASS` elements, `COVEREDRATIO`, and what an exclusion costs you                                       |
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)                                        | `@TempDir`, for testing a file write without leaving a file behind                                      |
| [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)           | `List.of` and `List.copyOf`, and why a clue list wants to be unmodifiable                               |
| [`java.nio.file.Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) | `writeString`, and the charset argument worth passing explicitly                                        |
| [JLine](https://github.com/jline/jline3) and [Lanterna](https://github.com/mabe02/lanterna)                    | **reference, not a dependency** — what terminal rendering looks like once it grows up                   |

## Part 1 — a grid you can see

### The work

1. **Create `model/Cell.java`.**
    a. Three constants: `FILLED`, `EMPTY`, `UNKNOWN`.
2. **Reshape `Griddler` into a grid.**
    a. **Add** `int width`, `int height`, `Cell[][]`.
    b. **Remove** `UUID` and `Long size`.
3. **Build a deterministic 4x4.**
    a. In `GriddlerEngine`, same grid every run.
    b. **Remove** the `engineName` placeholder.
4. **Render the grid to a string.**
    a. `#` for filled, `.` for empty.
    b. Return the string rather than print it.
5. **Print and save it from `tools`.**
    a. `TerminalGriddler` prints the string.
    b. It writes the same string to a `Path`.
    c. `Main` runs it.
6. *(optional)* **Watch the rule bite.**
    a. Reach across the boundary in `GriddlerEngine`.
    b. Run `mvn -B checkstyle:check`, read it, revert.

### Must be true

- [ ] `Main` prints a 4x4 grid of `#` and `.` to the terminal
- [ ] the same text is written to a file, and two runs produce identical bytes
- [ ] the rendered text comes back from a method that returns it, so a test reads it without capturing stdout
- [ ] no class under `engine/` or `model/` names `System`, a file, a socket, or a stream
- [ ] every class you added has a tier 1 test mirroring its package, per `lessons/TESTING_STANDARDS.md`
- [ ] `mvn -B verify` is green and the `0.8` rule passes without the number moving
- [ ] Part 1 is one commit, and `git status` is clean after it

## Part 2 — a nonogram

A grid is not yet a nonogram. The puzzle is the clues: for each row and each column, the lengths of the filled runs, in order. `##.#` is `2 1`. An empty row is one clue of `0`, or an empty list — that is your call, and the test you write for it is where you make the call stick.

### The work

1. **Derive the row clues.**
    a. Run-lengths per row, in the engine.
2. **Derive the column clues.**
    a. Same rule, walking columns.
3. **Add clue gutters to the render.**
    a. Column clues above, row clues to the left.
4. **Take a size argument in `Main`.**
    a. `10` prints a 10x10, not only the 4x4.

### Must be true

- [ ] `GriddlerEngine` returns row clues and column clues for any grid it builds
- [ ] the rendered output shows both gutters, and a hand-checked 4x4 matches what you expected
- [ ] `Main` accepts a size argument and `10` prints a 10x10
- [ ] a tier 1 test covers an empty row, a fully filled row, and a row with two separated runs
- [ ] the clue methods are covered by assertions on returned values, not by a test that only runs them
- [ ] `mvn -B verify` is green and the `0.8` rule still passes without the number moving
- [ ] Part 2 is one commit, and `git status` is clean after it

## Close out

**Did it land**

1. Why may `Main` import `tools` when `engine` may not?
2. `import-control.xml` declares the three subpackages before the root's `<file>` block. What breaks if you swap that order?
3. The engine cannot open a file. Name one thing that buys your test suite.
4. You removed `UUID` from `Griddler`. What would keeping it have cost the test you wrote in Part 1?

**For the next lesson**

5. 2.4 runs `Main` in a real JVM and asserts its output. `Main` now takes a size argument — should 2.4 assert stdout for one fixed size, or go straight to reading input interactively?
6. Chapter 3 owns the Unicode glyph choice (`█`, `·`, `✕`, `▓`). Keep ASCII until then, or pull that decision forward into 2.5?
