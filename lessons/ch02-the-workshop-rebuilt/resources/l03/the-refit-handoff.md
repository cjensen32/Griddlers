# Closing 2.3 — the standing guide

Learner reference note. Not authority — `../../../TESTING_STANDARDS.md` owns the rubric, `.agents/PROJECT.md` owns the architecture, `.agents/CONTEXT.md` owns the commit grammar, and `../../03-the-three-packages.md` owns the assignment. Where this note and one of those disagree, this note is the one that is out of date.

This replaces the four dated notes that used to live in this directory. They were written on 2026-09-12, -13, -14 and -16, and the checkout has overtaken almost all of them; `git log` holds them if you ever want one back. The technique that outlived them was promoted out of the lesson and into two files you will reach for after Chapter 2 is closed:

- [`../test-shapes.md`](../test-shapes.md) — what a finished test file looks like for an enum, a record, a class, or an interface.
- [`../changing-code.md`](../changing-code.md) — moving code that already works, reading a failure before reading the code, and where a seam belongs.

Everything below was measured against the checkout on **2026-09-18**. Where a claim came from running something, the command is in the line.

## 1. Where it stands

**The suite is healthy.** 126 tests, 0 failures, 0 errors, 0 skips. JaCoCo's `LINE` and `BRANCH` rules both clear `0.9`.

```
mvn -B verify -Dspotless.check.skip=true -Dcheckstyle.skip=true
  Tests run: 126, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS
```

(ARCHIVED CONTENT REMOVED)

**Almost everything the old notes asked for has landed.** All of this is verified in the tree and comes off your list for good: seeded randomness through a `Random` parameter, `clues()` and `deriveClueList` split out of the old interleaved pass, one render path, `measure` and `bottomAlign` as separately tested helpers, a rectangular `Griddler` carrying `width` and `height`, `Gutter`'s defensive copy at both levels, `TerminalGriddler.write`, and the `-r` flag-order bug — `-r -s 20` and `-s 20 -r` now agree.

What follows is what is left.

## 2. The one line between you and green (ARCHIVED)

## 3. Ticked boxes that are no longer true (RESOLVED 2026-09-19)

Four were found and all four are now corrected in `../../03-the-three-packages.md` itself, so the lesson is the place to read them: `Griddler` rejecting non-square shapes, `Main` printing an `x` that neither generator can produce, a coverage rule described as unmoved when it went `0.8` → `0.9`, and a ticked item whose own text admitted the default output path was still open.

One survives as a live finding rather than a lesson correction, because it is in the suite rather than in the lesson: `GriddlerTest:81` carries `@DisplayName("Constructor rejects Ragged, Rectangular, & Uneven cells")` over a method that only asserts ragged. A display name is what a reader sees in the report, so it is worse than a stale comment. §5 has it.

The open work those corrections exposed became the lesson's **Part 3**.

## 4. Close-out questions still open

Material to answer from. The answers are yours to write, and as of 2026-09-19 they live in `../../03-the-three-packages.md` — the four that needed rewriting are blanked there, each with a note saying what the previous attempt got wrong. Those notes carry their own evidence; what is below is the longer working for the four questions this guide had already looked into.

**Q4 — what would keeping `UUID` have cost the Part 1 test? (ANSWERED)** Your answer moved into the lesson on 2026-09-19. It is right; the note beside it only makes the mechanism exact, because the mechanism is what generalises. `Gutter` is a record and gets value equality free, which is why `cluesDerivesRowAndColumnClues` asserts a whole expected `Gutter` against a derived one in a single `assertEquals`. `Griddler` is a class with no `equals`, which is why every test in `GriddlerTest` goes through `getCells()` or `getCell()`. An identity field is what puts a type permanently in the second category.

**Q5 — should 2.4 assert stdout for one fixed size?** Answerable from the checkout now. `Main` has two paths: the default is fully deterministic and safe to freeze into a golden string; the `-r` path calls `new Random()` at `Main:33` with no seed and cannot be asserted at all. So the question is really "does 2.4 freeze the deterministic path, or do you make the other one deterministic first" — and the `[!?]` below is the second option.

**Q6 — pull the Unicode glyphs forward, or keep ASCII through Chapter 2?** Your answer says pull them forward if unresolved. The argument against still holds and is worth stating in writing either way: 2.4's "Reach for" section puts the four Appendix A glyphs in `HarnessProbe`, not in `Main`, so **2.4 already proves your UTF-8 handling without `Main` printing a single non-ASCII character.** Deferring costs one rename of `Cell.asciiStr()`. One thing to notice while you decide: Appendix A lists four glyphs — `█`, `·`, `✕`, `▓` — and `Cell` has three constants. The fourth is "deduced this pass", which is Chapter 3's solver, not yours.

**Q8 — does the model owe you a second type?** Your answer is right, and the decision is now recorded in code: `deriveClueList` treats anything that is not `FILLED` as a gap, and `cluesDerivesLineClues` pins that with a `[FILLED, UNKNOWN, FILLED] -> [1, 1]` row. What is missing is the *name*. The choice lives inside a general case rather than in a test named for it, so a reader finds the behaviour by accident. A case named for the decision is where a decision becomes findable.

**The `[!?]` on seeded randomness — half landed, and the better half is done.** `randomizeCells(int, Random)` takes the generator, and `randomizeCellIsDeterministicForSameSeed` asserts exact grids for five seeds. That is the part that was hard. What is missing is the part that is one line each: nothing supplies a seed. `Main:33` constructs a bare `new Random()` and `CLIParser` has no `--seed` flag. Add the flag and a field on `ParsedArgs`, and you get a reproducible randomized run, an answer to Q5, and Chapter 9's "regenerate a puzzle from a seed" arriving three chapters early. `../changing-code.md` §5 has the one caveat: seeding the generator only helps if nothing downstream reintroduces ambiguity.

## 5. What is left in the code

Open items only. Line numbers are current as of 2026-09-18.

### `model`

| Item                       | Verdict  | Why                                                                                                                                                                                                                                                                                                                 |
|----------------------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Cell.asciiStr()` :14      | RENAME   | `glyph()` is true today and still true after Chapter 3. `asciiStr` names an encoding that is about to stop being the encoding.                                                                                                                                                                                      |
| `Griddler` :23-24          | TEST GAP | The in-loop `row == null` guard is unreachable by the current fixture, because `constructorRejectsNullRow` puts the null row *first*, where `:17` catches it. JaCoCo reports 1 line and 1 branch missed, and they are these. A null in a later row is the case that reaches it.                                     |
| `Griddler` :17             | REVIEW   | That first-row guard duplicates the loop's. One of the two is redundant; deciding which is what §5's coverage gap is really telling you.                                                                                                                                                                            |
| `Gutter.deepCopyClues` :17 | REVIEW   | A null inner list is dropped rather than rejected, so the copy comes back shorter than the input. `dropsNullInnerLists` now pins that as intended behaviour — which is fine as a decision and bad as an accident. A shortened clue list is how a `Gutter` whose row count disagrees with its `Griddler` gets built. |

### `engine`

| Item                              | Verdict  | Why                                                                                                                                                                                                                                                                                                        |
|-----------------------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Layout` access :142, :145, :166  | FIX      | The private fields are read directly at `:142`, `:145` and `:166`, while `layout.cellWidth()` on that same line `:145` uses the accessor. Both compile **only** because `Layout` is nested inside `GriddlerEngine`. See `../changing-code.md` §8 — this is the trap, and today is the cheap day to fix it. |
| `randomizeCells(int size, …)` :34 | REFACTOR | Takes one dimension while `alternatingCells(int width, int height)` takes two. Rectangles are now a `Griddler` fact, and the two generators disagree about that.                                                                                                                                           |
| `render` :98 and its helpers      | KEEP     | One path, one place, each helper directly tested. Item 7.2 is honest.                                                                                                                                                                                                                                      |

### `tools`

| Item                  | Verdict  | Why                                                                                                                                                                                                                                                  |
|-----------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CLIParser` :5        | REFACTOR | `public class` with a private constructor; wants `final`. `GriddlerEngine` got it and this did not. Nothing catches it — `FinalClass` is not among the modules in `checkstyle.xml`, and adding it would catch this by rule instead of by inspection. |
| `CLIParser` :39       | TEST GAP | `size < 1` is unreachable through `-s -1`, because the `startsWith("-")` guard at `:31` rejects that first. `-s 0` is the only way in. 1 of 4 branches missed.                                                                                       |
| `TerminalGriddler` :9 | RENAME   | It writes a file and never touches a terminal. Do it before 2.4 freezes stdout, so the class that owns output is not a surprise by then.                                                                                                             |
| *missing*             | ADD      | "A failed write is not silent" is Part 1 item 5.2, ticked, and nothing tests it.                                                                                                                                                                     |
| *missing*             | ADD      | The constructor's `requireNonNull` at `:13` is untested.                                                                                                                                                                                             |

### Root

| Item | Verdict | Why |
|---|---|---|
| `Main` :21 | REFACTOR | The temp directory is created *before* the arguments are parsed, so a run that passes `-o` creates a directory it never uses — and nothing ever removes them. Create it in the branch that needs it. |
| `Main` :42-43 | REFACTOR | Writes, then prints. A failed write therefore means exit 1 with zero bytes of stdout, and the grid you rendered is discarded unseen. 2.4 asserts the exit code **together with** stdout. Swap the two lines. |
| `Main` :33 | ADD | The unseeded `Random`. See §4. |
| `Main` | ADD | It writes to a path with a random number in it and never says where. Print the destination to **stderr**, so 2.4's golden stdout stays clean. |

### Tests

| Item | Verdict | Why |
|---|---|---|
| `GriddlerEngineTest` :34, `CellTest` :13, `CLIParserTest` :17 | REFACTOR | `public class`, where `GriddlerTest`, `GutterTest` and `TerminalGriddlerTest` are package-private. JUnit 5 needs neither. Pick one and apply it. |
| `CLIParserTest` :40, `GriddlerEngineTest` :92-93 | REFACTOR | `private static` fields reassigned in `@BeforeEach` — `DEFAULTS`, `SQUARE_GRIDDLER`, `NON_SQUARE_GRIDDLER`. This is the shared writable static fixture `TESTING_STANDARDS.md` forbids by name. Instance fields. |
| `CLIParserTest` :43 | REVIEW | Class-level `@TempDir DIR`, shared by every case in the nest. |
| `CLIParserTest` :103 | REVIEW | `resolveDirFromArgs` rewrites the first `.txt` token in place and restates production path handling inside the test. It works; it also hides which value a failing case used. |
| `GriddlerTest` :81 | RENAME | The `@DisplayName` claims rectangular grids are rejected. They are not. See §3. |
| `GriddlerTest` :55 / :158, :75 / :118 | MERGE | Two duplicate pairs — the same 1×1 fixture twice, and the same `row.isEmpty()` branch twice. |
| `GriddlerTest` :205 | EXTEND | Proves the inner rows are copies. `getCells` returns `List.copyOf` at the outer level too, so `snapshot.set(0, …)` throws where `snapshot.get(0).set(0, …)` is merely harmless — and only one of those two is tested. |
| `TerminalGriddlerTest` :35 | DELETE | `griddlerOutputsAreEqual` asserts that writing the same string twice produces the same bytes. That is a test of `Files.writeString`. |
| `TerminalGriddlerTest` :19-25 | DELETE | `CELLS` and `GRIDDLER` are referenced by no test in the file, and drag the `Cell` and `Griddler` imports in with them. |

## 6. There is still no tier 2 test anywhere in the repo

`TESTING_STANDARDS.md` asks for a tier 2 flow test per user-visible flow: real collaborators, wired the way `main` wires them, one complete path, named for the behaviour. The tree has tier 1 files and nothing else.

That means 2.4's subprocess test would be the **first** thing in the suite that ever exercises parse → generate → clues → render → write wired together. A subprocess is the most expensive place to discover a wiring bug, because all it can tell you is that several hundred bytes of stdout did not match.

One flow test, before 2.4. It costs twenty lines and it is what makes the tier 3 failure interpretable when it comes.

## 7. What 2.4 needs that today's checkout does not have

- **Deterministic stdout for whatever run it freezes.** §4, Q5.
- **Print before write**, so a failure has an exit code *and* output. §5.
- **A destination on stderr**, so the golden stdout stays clean.
- **A settled name for `TerminalGriddler`**, before the harness starts naming it.

**Where the harness may live is already decided — by a rule you wrote.** `pom.xml` sets `includeTestSourceDirectory=true`, so `import-control.xml` governs the test tree, and the answer is not "anywhere":

- Root package, named `*Test` — gets `org.junit.jupiter` only. No `java.io`. And Surefire would try to run it as a test, which 2.4 forbids.
- Root package, not named `*Test` — matches no `<file>` block, so it falls back to the root node: `tools` and `java.util`. Still no `java.io`.
- **`tools` test package, not named `*Test`** — the `tools` subpackage carries `<allow pkg="java" regex="true"/>`, which covers `java.io`, `java.nio` and `java.util.concurrent`. Works today with **zero changes to the rule file.**

So: `src/test/java/com/connorjensen/griddlers/tools/JvmHarness.java`. It is a consequence of the boundary rather than a coincidence, which is exactly what 2.4's checklist asks you to be able to say. Check the name against Surefire's default includes (`*Test`, `Test*`, `*Tests`, `*TestCase`) before you commit to it — `JvmHarness` clears all four.

One thing to notice on the way: `pom.xml`'s Surefire configuration carries `<exclude>**/Main.java</exclude>`. `Main.java` is not a test and lives under `src/main`, so that exclusion does nothing at all. 2.4 asks you to justify the `Main` exclusion in the coverage rule in one sentence; this is a different one, and it deserves the same sentence or deletion.

## 8. Elsewhere

**`JOURNAL.md:62`** still defines run-length encoding as "StringBuilder uses this algorithm to minimize runtime garbage/heap size." That is not what RLE is and not what `StringBuilder` does. RLE is precisely what `deriveClueList` computes — runs of identical values stored as (value, length). It is this lesson's core algorithm, so it is worth having right.

**The commit scopes have drifted.** `.agents/CONTEXT.md` names four — `COURSE`, `GAME`, `PROGRESS`, `FIX`. The log also carries `INFRA`, `STRUCTURE` and `CONFIGS`. Either extend the grammar or stop using them; a convention nobody follows is worse than no convention.

## 9. Suggested order

Nothing here is large. The sequencing is the point.

1. §2 — the import order. Alone, first, on its own commit.
2. The two uncovered branches, §5. Pure tests, no production change.
3. The `TerminalGriddler` gaps, §5 — the failed write and the null destination — with the two deletions in the same file.
4. `Main`: print before write, and the lazy temp directory.
5. The `--seed` flag, which closes the `[!?]` and Q5 together.
6. The renames, §5. Mechanical, no behaviour.
7. The tier 2 flow test, §6.
8. Then 2.4, where the golden string is safe to freeze.

## 10. Commit guide

The grammar is `.agents/CONTEXT.md`'s and this section only restates how it applies here. The tracked hook prepends `C###` from HEAD's own subject, so you never type the number. The subject is `SCOPE(area): summary`, where scope is `COURSE` for course and repository documentation, `GAME` for your implementation, `PROGRESS` for your progress records, and `FIX` for corrections to earlier implementation. **A subject that already says what changed and why is a finished commit message** — add a body only when the subject leaves something genuinely unclear.

Two rules are worth stating plainly, because end-of-day commits break both:

> **A commit ends at a green `mvn -B verify`, not at a time of day.**

> **If the subject needs an "and", it is two commits.**

An end-of-day commit bundles a rename, a bug fix and three tests under one vague subject. Nothing in that commit can be reverted without reverting the rest, `git log` stops being a record of decisions, and the one line that broke the build is buried among forty that did not. The unit is the change, not the sitting — and most of the rows below are twenty minutes of work.

| # | Subject | Carries |
|---|---|---|
| 1 | `FIX(ch02.3): import order in GriddlerEngineTest` | the one line from `spotless:apply`, alone, before anything else |
| 2 | `GAME(ch02.3): cover the late null row and a zero size` | the two branches JaCoCo still reports missed |
| 3 | `GAME(ch02.3): TerminalGriddler proves a failed write and a null destination` | plus deleting `griddlerOutputsAreEqual` and the dead fixture |
| 4 | `GAME(ch02.3): Main prints before it writes, and says where` | plus the temp directory moving after the parse |
| 5 | `GAME(ch02.3): a --seed flag makes a randomized run reproducible` | `CLIParser`, `ParsedArgs`, `Main` — the `[!?]` answered in code |
| 6 | `GAME(ch02.3): rename asciiStr and TerminalGriddler` | renames only, no behaviour, so the diff stays readable |
| 7 | `GAME(ch02.3): tier 2 flow test for parse through write` | the first one in the repo |
| 8 | `PROGRESS(ch02.3): close-out answers and the corrected boxes` | the lesson file's three stale ticks, Q4 through Q8, the journal's RLE row |

Only the ordering of 1 matters; the rest can be resequenced freely as long as each one leaves `mvn -B verify` green and `git status` clean. Every one of them is your own work and carries you as author.

Two things worth adding to a body when they apply. `WHY:` when the reason is not evident from the change itself — commit 4 is a candidate, because "print before write" reads as cosmetic until you know 2.4 asserts both together. And `VERIFY:` for a checkpoint worth pinning, such as the passing suite at the end of the lesson — paste the interesting lines, not the whole run.
