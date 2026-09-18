# 2026-09-15 — What 2.3 leaves behind, and what 2.4 will trip over

Learner reference note. Not authority — `../../../TESTING_STANDARDS.md` owns the rubric, `.agents/PROJECT.md` owns the architecture, `.agents/CONTEXT.md` owns the commit grammar, and `../../03-the-three-packages.md` owns the assignment. Where this note and one of those disagree, this note is the one that is out of date.

Written against the checkout, not against the lesson's checkboxes. Everything below was run; where a claim came from executing something, the evidence is in the line.

## 1. Where it actually stands

`mvn -B verify` is green: 72 tests, 0 failures, 0 skips, Spotless and Checkstyle clean, coverage gate passing. Every blocker the previous two notes recorded is closed — the build is green, the ragged-grid crash is gone, `Main` prints the string it holds, and `CLIParser` has a tier 1 file.

Two things the old note listed as open are already resolved in the tree and can come off your list:

- **The default output path belongs to the composition root.** `Main` builds `ParsedArgs defaults` and hands them to `CLIParser.parse(args, defaults)`, so the parser only reports what it was told. That is the right shape. The *value* of that default changed on 2026-09-15 and is worth a read — see §2.1.
- **`-s abc` names the flag now.** The `NumberFormatException` is caught and rethrown as `'5s' is not an integer`. The old note's item is stale.

What follows is what is left.

## 2. Before 2.4 — four things, and the order matters

2.4 freezes the whole of stdout into a golden string. Everything that changes stdout wants to land on the near side of that line, or you pay for it twice.

### 2.1 The default output path — `createTempDirectory` was the right call

*Updated 2026-09-15, after the switch away from `user.home`.*

The default used to be `$HOME/Downloads/griddler-example.txt`, which needed a directory only your Mac guarantees — a fresh container has no `~/Downloads`, and `Files.writeString` does not create parent directories. `Main.java:20` now reads:

```java
Path tempDir = Files.createTempDirectory("Griddlers_");
Path outPath = tempDir.resolve("Griddler.txt");
```

**This does fix the cross-platform problem, and it is the better default.** `createTempDirectory` resolves against `java.io.tmpdir`, which every OS supplies and which is always writable, and it creates the directory rather than assuming it. Verified across three runs: exit 0 every time, no `user.home` left anywhere in `src/` (`TerminalGriddler` never had it — it only ever took the `Path` it was handed). A no-argument tier 3 run is now portable, which is exactly what 2.4 needs.

Two small costs came with it, both worth one line each:

1. **The temp directory is created before the arguments are parsed,** so a run that passes `-o` creates a directory it never uses. Verified — three runs, two with defaults and one with `-o`, left three directories, one of them empty:

   ```
   <tmp>/Griddlers_7069928088069368060/Griddler.txt
   <tmp>/Griddlers_5921243362129116725/Griddler.txt
   <tmp>/Griddlers_17620675111100737572/          <-- empty, the -o run
   ```

2. **Nothing ever cleans them up.** One directory per run, accumulating. The JVM does not remove temp directories on exit unless you ask it to.

Both have the same fix, and it is the shape the earlier note argued for: let the parser report that *no path was given* rather than handing it a default it might discard, and have `Main` create the temp directory only in that case. Then the directory is created exactly when it is used, and a run with `-o` leaves nothing behind. `ParsedArgs` can carry a null or an `Optional<Path>` for this — `parse` already leaves `outPath` untouched unless `-o` appears, so the change is small.

One usability note while you are in there: `Main` writes to a path with a random number in it and never says where. Printing the destination — to stderr, so the golden stdout string stays clean — costs one line and saves you hunting through `/var/folders`.

### 2.2 `Main` still writes before it prints

`Main.java:41` writes the file, `Main.java:42` prints. When the write throws, the grid has already been rendered into a local variable and is simply never printed:

```
java -cp target/classes ...Main -o "$D/nope/ok.txt"
exit=1   stdout_bytes=0   stderr: NoSuchFileException
```

§2.1 makes this much less likely to fire on the *default* path, so this is no longer a blocker — but it is still reachable any time `-o` names a directory that does not exist, and 2.4 asserts the exit code **together with** stdout. Swap the two lines. Print, then write. It costs nothing and it makes the failure legible.

Separately, give the 2.4 harness a varargs `String... args`. 2.4's spec as written — "takes the main class to run and the stdin to feed it" — has no argv parameter, and you now have three flags worth asserting. That is no longer a portability requirement, just a coverage one.

### 2.3 Where the harness may live is already decided — by a rule you wrote

`pom.xml:123` sets `includeTestSourceDirectory=true`, so `import-control.xml` governs the test tree. That answers 2.4's open question for you, and the answer is not "anywhere":

- **Root package, named `*Test`** — gets `org.junit.jupiter` only. No `java.io`. And Surefire would try to run it as a test, which 2.4 forbids.
- **Root package, not named `*Test`** — matches no `<file>` block, so it falls back to the root node: `tools` and `java.util`. Still no `java.io`. A harness needs streams. Fails.
- **`tools` test package, not named `*Test`** — the `tools` subpackage allows `<allow pkg="java" regex="true"/>`, which covers `java.io`, `java.nio`, and `java.util.concurrent`. Works today with **zero changes to the rule file.**

So: `src/test/java/com/connorjensen/griddlers/tools/JvmHarness.java`. It is a consequence of the boundary rather than a coincidence, which is precisely what 2.4's checklist asks you to be able to say. Check the name against Surefire's default includes (`*Test`, `Test*`, `*Tests`, `*TestCase`) before you commit to it — `JvmHarness` clears all four.

### 2.4 There is no tier 2 test anywhere in the repo

`TESTING_STANDARDS.md` asks for a tier 2 flow test per user-visible flow. The tree has tier 1 files and nothing else. That means 2.4's subprocess test will be the **first** thing in the suite that ever exercises parse → generate → `Griddler` → `gutters` → render → write wired together — and a subprocess is the most expensive place to discover a wiring bug, because all it can tell you is that 400 bytes of stdout didn't match.

Write one tier 2 `RenderFlowTest` before 2.4. Real objects, wired the way `Main` wires them, one complete path, asserting the rendered string. It costs you twenty lines and it is what makes the tier 3 failure interpretable when it comes.

## 3. Two bugs your suite does not catch

This is the "one-point failure" question answered honestly: the suite is broad but it has two holes where the code is silently wrong.

### 3.1 `-r` is silently ignored whenever another flag follows it

23 parser cases and none of them catches this. Verified:

```
-r -s 20          ==>  randomize=false      <-- silently dropped
-s 20 -r          ==>  randomize=true
--random -o q.txt ==>  randomize=false      <-- silently dropped
-r                ==>  randomize=true
```

`CLIParser.java:43-52`: when the next token starts with `-`, no branch assigns anything, so `randomize` keeps its default. No throw, no message, wrong answer. It is order-dependent behaviour in a parser whose entire job is to not be order-dependent.

The fix is one `else` — a following token that starts with `-` means "no value given", which is the same case as running off the end of the array. The test that would have caught it is a flag-order-independence case: assert `-r -s 20` and `-s 20 -r` parse equal.

### 3.2 `randomizeCells` is untestable by construction, and its test knows it

`new Random()` inside `GriddlerEngine.randomizeCells` (line 203) makes `engine` non-deterministic, which contradicts both `.agents/PROJECT.md` and `.agents/CONTEXT.md`. Look at what the test can assert as a result:

```java
int actualCount = cells.stream().mapToInt(List::size).sum();
assertEquals(size * size, actualCount);
```

That the grid is the right *shape*. Not one cell value is ever checked, because no cell value is knowable. The method has the weakest test in the repo and that is a direct consequence of where it gets its randomness.

Take a `Random` as a parameter. `randomizeCells(int size, Random rng)`, `Main` constructs it. Then the test seeds it and asserts the exact grid, and the method joins the rest of the suite.

## 4. Delete, rename, standardize

Small, mechanical, and worth doing in one commit before 2.4.

**Delete:**

1. **`GriddlerEngine.render` is dead.** The only caller in the entire repo is `GriddlerEngineTest:44`. Lesson item 7.2 — "One render path, in one place" — is ticked but not true; there are still two. Delete the method and `testConversionToString` with it. `TESTING_STANDARDS.md` names this case exactly: delete a test that exists only to move a coverage number.
2. **`GutterTest.gutterSurvivesConstruction()` is an empty method body.** It asserts nothing and does not even construct a `Gutter`. It exists to satisfy the tier 1 rule and satisfies nothing. Fix it by giving `Gutter` something worth testing (below) or delete the file.
3. **`TerminalGriddlerTest:19-25`** builds a `CELLS`/`GRIDDLER` fixture that no test in the file references. Dead, along with the `Cell` and `Griddler` imports it drags in.
4. **`TerminalGriddlerTest.griddlerOutputsAreEqual`** asserts that writing the same string twice produces the same bytes. That is a test of `Files.writeString`, not of your code, and the other test in the file already covers the real contract.

**The test that should replace it:** "a failed write is not silent" is Part 1 item 5.2, ticked, and nothing tests it. `assertThrows(IOException.class, ...)` against a path whose parent does not exist. `Objects.requireNonNull` in the constructor is untested too.

**Rename:**

5. **`Gutter.xAxisGutters` / `columnClues` → `rowClues` / `columnClues`.** A gutter is the strip a renderer prints into; these are clues. Chapter 9's JSON and Chapter 10's DOM both carry these numbers and neither has a gutter. And "x-axis holds row clues" reads backwards to most people.
6. **`Gutter` has no defensive copy.** The record hands out the engine's own mutable `ArrayList`s at both levels. A compact constructor copying both levels closes it — and gives `GutterTest` a real thing to assert, which resolves item 2 above.
7. **`TerminalGriddler` writes a file and never touches a terminal.** Rename it for the write it performs. Do it before 2.4 asserts stdout, so the class that owns output isn't a surprise by then.
8. **`Cell.asciiStr()` names an encoding.** See §6 — ASCII is staying through Chapter 2, so the name is true today and wrong the moment Chapter 3 lands. `glyph()` is true in both.

**Standardize:**

9. **`GriddlerEngine` and `CLIParser` are `public class` with private constructors.** Both want `final`.
10. **`CLIParserTest.ParseTests` holds `private static ParsedArgs DEFAULTS` reassigned in `@BeforeEach`** — a shared writable static fixture, which `TESTING_STANDARDS.md` forbids by name. Make it an instance field.
11. **Test class visibility is split three ways.** `public class` on `GriddlerEngineTest`, `CellTest`, `GutterTest`, `CLIParserTest`; package-private on `GriddlerTest`, `TerminalGriddlerTest`. JUnit 5 needs neither. Pick package-private everywhere.

**One structural simplification worth the time:**

12. **`gutters()` derives both axes in a single interleaved pass** (`GriddlerEngine.java:15-61`, 47 lines) and the column half only works because the grid is square. Your own close-out answer to question 7 puts rectangular grids in 2.5 — this method is what breaks when they arrive. Split it: one private `runs(List<Cell> line)` returning the run lengths, then `rowClues` maps it over rows and `columnClues` maps it over columns. Roughly 15 lines, each testable on a single line of cells, and rectangles become free.

13. **`getCells()` deep-copies the whole grid once per row** inside both render loops (`GriddlerEngine.java:68` and `:137`). Hoist it to one call per render.

## 5. Stale content in the lesson and the journal

- **The lesson says `0.8` twice and the POM says `0.9`.** Lines 59 and 104 both read "the `0.8` rule passes without the number moving." The gate is now `LINE` at `0.9` **and** `BRANCH` at `0.9`. The number moved. I corrected both lines in the lesson; flagging it because a ticked box against a false statement is the thing this course keeps trying not to do.
- **`JOURNAL.md:62` defines run-length encoding as "StringBuilder uses this algorithm to minimize runtime garbage/heap size."** That is not what RLE is and not what `StringBuilder` does. RLE is precisely what `gutters()` computes — runs of identical values stored as (value, length). It is this lesson's core algorithm, so it is worth having right.
- **Close-out question 4 is answered "Don't know; believe this is stale/resolved."** It is answerable in one line: a `UUID` field makes two `Griddler`s with identical cells unequal, so `assertEquals(INITIAL_CELLS, grid.getCells())` would have had to become field-by-field comparison. Keep the question or cut it, but it does have an answer.

## 6. Your close-out answers — two corrections

**Question 2 is right in both halves, but the DTD constraint is not the one you named.** I tested both reorderings.

Moving the root `<file>` blocks above the root `<allow>` lines fails to parse:

```
The content of element type "import-control" must match "((allow|disallow)*,(subpackage|file)*)"
```

So the DTD forbids `file` before `allow` — it says nothing about `file` before `subpackage`; those two are in the same group and may interleave freely. Your half A is real but it fires on a different swap than you described.

Half B is the one that bites, and it is worse than "may conflict". Moving the root `<file name=".*Test">` block above the `<subpackage>` blocks is **perfectly legal XML** and produces 10 violations, because the root test rule now claims every `*Test` file in every subpackage before the subpackage can, and those files lose `java.nio` and `model`:

```
TerminalGriddlerTest.java:6: Disallowed import - java.io.IOException
GriddlerEngineTest.java:13: Disallowed import - ...model.Cell
```

That is the real lesson: the dangerous reorder is the one the DTD lets through. Worth adding to your answer.

**Question 8 — your answer is right, and it has a consequence you should record.** Yes: a solved puzzle is `FILLED`/`EMPTY` only, and `UNKNOWN` is a player-board marker. Which means `gutters()` today derives clues from whatever it is handed, and handed a half-solved board it returns confidently wrong clues. You do not need the second type in Chapter 2. You do need the current behaviour to be a decision rather than an accident: `GriddlerEngineTest`'s fixture contains `UNKNOWN` cells and expects them counted as gaps, so name that test `unknownCellsCountAsGaps`. Then the choice is written down where it will be found.

**Question 6 — don't pull the glyphs forward.** Read 2.4's "Reach for" section: it uses the four Appendix A glyphs in the *`HarnessProbe`*, not in `Main`. So 2.4 already proves your UTF-8 handling without needing `Main` to print Unicode. Keeping ASCII through Chapter 2 costs you nothing, and Chapter 3's swap stays a one-file change. Say so in writing and close the question.

## 7. Your `[!?]` on seeded randomness

It is not a lesson — it is §3.2, and it is smaller than you think.

"Seeding" means the randomness comes in as a parameter instead of being created on the spot. `new Random(42)` produces the identical sequence of numbers on every machine, every run, forever; that is a documented guarantee of `java.util.Random`, not an accident of implementation. So the entire feature is: `randomizeCells(int size, Random rng)`, and `Main` passes `new Random(seed)` where `seed` comes from a `--seed` flag.

What it costs: one parameter, one flag. What it buys, immediately: the test in §3.2 can assert an exact grid. What it buys later: a puzzle identified by a number you can text to someone. That is Chapter 9's "regenerate a puzzle from a seed" arriving early and for free, and it is the same change that fixes the determinism claim `PROJECT.md` makes on your behalf.

The one thing worth knowing now, so it doesn't surprise you later: `new Random(seed)` is reproducible across machines and JDK versions, but `Math.random()`, `new Random()`, and any use of a `HashSet`'s iteration order are not. Seeding the generator only helps if nothing downstream reintroduces ambiguity.

## 8. Suggested order

Nothing here is large; the sequencing is the whole point.

1. §4 deletes and renames — mechanical, no behaviour, one commit.
2. §3.1 and §3.2 — the two real bugs, each with the test that catches it.
3. §4.12 — split `gutters()` into `runs` + `rowClues` + `columnClues`.
4. §2.2 — swap print and write, and §2.1's lazy temp directory.
5. §2.4 — the tier 2 flow test.
6. Then 2.4, where the golden string is safe to freeze.

---

## 9. The decision table — every item in the repo

*Added 2026-09-15; tables re-cut short 2026-09-15.* One row per thing that has a name.

**Verdicts:** KEEP (correct, no action) · RENAME (code right, name lies) · REFACTOR (shape wrong) ·
DELETE (remove it) · REVIEW (your call, not mine) · ADD (missing) · MERGE / SPLIT / EXTEND (test shape).

**◆** = in the staged diff. **N*n*** = note at the end of the section. Line numbers are current as of today.

### 9.1 `model`

| Item                               | Verdict  | Why                                 | Status           |
|------------------------------------|----------|-------------------------------------|------------------|
| `Cell`                             | KEEP     | enum, three constants, one field    | Reviewed         |
| `Cell.asciiStr()` :14              | RENAME   | `glyph()` — ASCII name dies in Ch3  | Renamed          |
| `Griddler` ctor :10                | REVIEW   | square-only; rectangles land in 2.5 | Done?            |
| `Griddler.getCells()` :43          | REFACTOR | deep copy on every call — N1        | Done? Refactor?  |
| `Griddler.getCell/setCell/getSize` | KEEP     | `setCell` is the Q8 seam            | is this issue??  |
| `Griddler.checkBounds` :36         | KEEP     | private, one job                    | Reviewed         |
| ◆ `Gutter` fields :5               | RENAME   | → `rowClues` / `columnClues`        | Renamed          |
| ◆ `Gutter` :5                      | REFACTOR | compact ctor, copy both levels — N2 | Done, need test? |

### 9.2 `engine` — `GriddlerEngine.java`

| Item                            | Verdict  | Why                                          | Status  |
|---------------------------------|----------|----------------------------------------------|---------|
| ◆ class :12                     | REFACTOR | private ctor wants `final`                   | Added   |
| ◆ `gutters()` :15               | RENAME   | → `clues()`; returns clues, not layout       | Done?   |
| ◆ `gutters()` :20-58            | REFACTOR | split into `runs` + row + column — N3        | Done?   |
| ◆ `render()` :63                | DELETE   | dead; only its own test calls it             | Removed |
| ◆ `renderWithGutters()` :83     | RENAME   | → `render()` once :63 is gone                | Renamed |
| ◆ `renderWithGutters()` :83-170 | REFACTOR | 88 lines, four jobs — N4                     |         |
| ◆ `renderXBorder()` :173        | RENAME   | → `rowSeparator()`; it draws a rule          |         |
| ◆ `center()` :177               | KEEP     | does what it says                            |         |
| ◆ `nonRandomizeCells()` :186    | RENAME   | → `alternatingCells()`; named for a negative |         |
| ◆ `randomizeCells()` :200       | REFACTOR | take a `Random`; `new Random()` at :203 — N5 |         |

### 9.3 `tools`

| Item                                    | Verdict  | Why                                |
|-----------------------------------------|----------|------------------------------------|
| ◆ `CLIParser` :5                        | REFACTOR | private ctor wants `final`         |
| ◆ `CLIParser.parse()` :43-52            | REFACTOR | `-r` dropped before a flag — §3.1  |
| ◆ `CLIParser.ParsedArgs` :8             | REVIEW   | nullable `outPath` if §2.1 lands   |
| `TerminalGriddler` :9                   | RENAME   | never touches a terminal           |
| `TerminalGriddler.griddlerToFile()` :16 | RENAME   | → `write()`; not griddler-specific |

### 9.4 Root

| Item                   | Verdict  | Why                                |
|------------------------|----------|------------------------------------|
| ◆ `Main` :16           | KEEP     | wiring, one branch, a print        |
| ◆ `Main.main()` :41-42 | REFACTOR | print before write — §2.2          |
| ◆ `Main.main()` :20    | REFACTOR | temp dir built before parse — §2.1 |
| ◆ `Main.main()` :20    | ADD      | print destination to stderr — N6   |

### 9.5 Tests — `GriddlerEngineTest.java`

| Item                                        | Verdict  | Why                                   |
|---------------------------------------------|----------|---------------------------------------|
| ◆ class :17                                 | REFACTOR | `public` → package-private            |
| ◆ `GUTTERS` :25                             | REFACTOR | fixture built by code under test — N7 |
| ◆ `testConversionToString` :29              | DELETE   | covers the dead `render()`            |
| ◆ `testConversionToStringWithGutters` :49   | RENAME   | → `rendersClueGuttersAroundGrid`      |
| ◆ `…WithLargeXYGutters` :71                 | RENAME   | → `rendersMultiDigitUnevenGutters`    |
| ◆ `…WithLargeXYGutters` :90-118             | REVIEW   | 27-line golden string; you own it     |
| ◆ `convertGriddlerToGuttersAccurately` :127 | RENAME   | → `derivesRowAndColumnClues`          |
| ◆ `convertGriddlerToGuttersAccurately` :127 | SPLIT    | add `unknownCellsCountAsGaps` — N8    |
| ◆ `createCellGridUsingNonRandomMethod` :138 | RENAME   | named for method, not behaviour       |
| ◆ `createCellGridUsingRandomMethod` :153    | RENAME   | asserts shape only — N9               |

### 9.6 Tests — `CellTest.java`, `GriddlerTest.java`, `GutterTest.java`

| Item                                             | Verdict  | Why                                           |
|--------------------------------------------------|----------|-----------------------------------------------|
| `CellTest` :13                                   | REFACTOR | `public` → package-private                    |
| `everyCellAsciiStrIsNonBlank` :17                | DELETE   | subsumed by :30                               |
| `noCellSharesAsciiStr` :22                       | KEEP     | distinctness; nothing else covers it          |
| `everyCellCarriesExpectedAsciiStr` :30           | KEEP     | pins the exact glyph                          |
| `GriddlerTest` :17                               | KEEP     | good `@Nested` shape, already package-private |
| `constructorPreservesCellValues` :38             | KEEP     |                                               |
| `constructorAcceptsSmallestGrid` :44             | MERGE    | same 1×1 fixture as :154                      |
| `constructorRejectsNullRow` :52                  | KEEP     |                                               |
| `constructorRejectsEmptyRows` :64                | MERGE    | same `row.isEmpty()` branch as :114           |
| `constructorRejectsRaggedRows` :71               | REFACTOR | three scenarios, one method — N10             |
| `constructorRejectsNullCell` :96                 | KEEP     |                                               |
| `constructorRejectsEmptyCells` :107              | RENAME   | → `constructorRejectsEmptyGrid`               |
| `constructorRejectsEmptyCellRow` :114            | MERGE    | duplicate of :64                              |
| `getSizeReturnsGridDimension` :125               | KEEP     |                                               |
| `getCellsReturnsRowsWithExpectedSize` :130       | KEEP     |                                               |
| `getCellReturnsValueAtRequestedCoordinates` :138 | KEEP     | distinct rows catch swapped indices           |
| `getCellRejectsInvalidCoordinates` :149          | KEEP     |                                               |
| `smallestGridSizeAndContent` :154                | MERGE    | duplicate of :44; not an outcome name         |
| `setCellChangesOnlyRequestedCell` :167           | KEEP     |                                               |
| `setCellRejectsInvalidCoordinates` :187          | KEEP     |                                               |
| `setCellRejectsNullCell` :192                    | KEEP     |                                               |
| `modifyingReturnedCellsDoesNotChangeGrid` :201   | EXTEND   | outer list untested — N11                     |
| ◆ `GutterTest` :5                                | REWRITE  | needs a real assertion                        |
| ◆ `gutterSurvivesConstruction` :8                | DELETE   | empty method body — N12                       |

### 9.7 Tests — `CLIParserTest.java`, `TerminalGriddlerTest.java`

| Item                                      | Verdict  | Why                                        |
|-------------------------------------------|----------|--------------------------------------------|
| ◆ `CLIParserTest` :17                     | REFACTOR | `public` → package-private                 |
| ◆ `constructorPreservesValues` :25        | KEEP     | record is its own JaCoCo `CLASS`           |
| ◆ `DEFAULTS` :40                          | REFACTOR | shared writable static — N13               |
| ◆ `DIR` :43                               | REVIEW   | class-level `@TempDir` shared by all cases |
| ◆ `flagsThrowOnInvalidValues` :68         | KEEP     | 14 cases, accurate name                    |
| ◆ `flagsThrowOnInvalidValues` :51-67      | EXTEND   | add `-s 1`, `-s 50`, `-s 0`                |
| ◆ `argumentsWorkCorrectly` :89            | RENAME   | → `parsesFlagsIntoParsedArgs`              |
| ◆ `argumentsWorkCorrectly` :78-88         | EXTEND   | add flag-order case — §3.1                 |
| ◆ `resolveDirFromArgs` :103               | REVIEW   | mutates `args` in place — N14              |
| `TerminalGriddlerTest` :18                | KEEP     | already package-private                    |
| `CELLS` / `GRIDDLER` :19-25               | DELETE   | unused fixture, drags two imports          |
| `griddlerOutputsAreEqual` :35             | DELETE   | tests `Files.writeString` — N15            |
| `griddlerToFileProducesIdenticalData` :46 | RENAME   | → `writesExactStringAsUtf8`                |
| *missing:* failed write is not silent     | ADD      | Part 1 item 5.2, ticked, untested          |
| *missing:* `requireNonNull` :13           | ADD      | one line                                   |
| *missing:* tier 2 flow test               | ADD      | none in the repo — §2.4                    |

### 9.8 Config and tooling

| Item                                  | Verdict | Why                                    |
|---------------------------------------|---------|----------------------------------------|
| ◆ `pom.xml` — `LINE`/`BRANCH` 0.9     | KEEP    | lesson said `0.8`; corrected           |
| ◆ `pom.xml` — `Main` JaCoCo exclusion | REVIEW  | 2.4: justify in one sentence or delete |
| `checkstyle.xml`                      | REVIEW  | add `FinalClass` — N16                 |
| `import-control.xml`                  | KEEP    | node order is load-bearing — §6        |
| `.githooks/prepare-commit-msg`        | KEEP    |                                        |
| `.editorconfig`, `.gitignore`         | KEEP    |                                        |

### 9.9 Course content

| Item                                                | Verdict | Why                                    |
|-----------------------------------------------------|---------|----------------------------------------|
| ◆ `03-the-three-packages.md` item 7.2               | REVIEW  | ticked; two render paths still exist   |
| ◆ `03-the-three-packages.md` :59, :104              | DONE    | `0.8` corrected 2026-09-15             |
| ◆ `JOURNAL.md` :62 run-length encoding              | FIX     | defines `StringBuilder`, not RLE — N17 |
| `resources/l03/2026-09-12`, `-09-13`                | REVIEW  | superseded; keep as history or archive |
| `resources/l02/*.java`, `l04/HarnessProbe.java`     | KEEP    | outside `src/`; neither tool sees them |
| `TESTING_STANDARDS.md`, `SYLLABUS.md`, `.agents/**` | KEEP    | authority files                        |

### 9.10 Notes

**N1** — `getCells()` rebuilds the whole grid per call, and both render loops call it once per row (`GriddlerEngine:68`, `:137`). Hoist to one call per render. **[!?] - please explain, do you mean to have it stored as another private variable or what would be the ideal way to store an immutable/copy of the cells?**

**N2** — The record hands out the engine's own mutable `ArrayList`s at both levels, so a caller can rewrite clues after they were derived. A compact constructor copying both levels closes it — and gives `GutterTest` something real to assert.

**N3** — One interleaved pass derives both axes, and the column half only works because the grid is square. Split into `runs(List<Cell>)` + `rowClues` + `columnClues`: ~15 lines, each testable on a single line of cells, and 2.5's rectangles come free.

**N4** — Measure widths, draw top gutter, draw body rows, close bottom border. Split it *before* moving it anywhere — four short methods is a reviewable diff; one long one is a file appearing in a new place.

**N5** — `new Random()` inside `engine` breaks the determinism claim both `PROJECT.md` and `CONTEXT.md` make. It is also why the test at :153 can only assert shape. See §3.2 and §7.

**N6** — The output path now carries a random number and `Main` never says where it wrote. stderr keeps 2.4's golden stdout clean.

**N7** — `GUTTERS = GriddlerEngine.gutters(GRIDDLER)` makes the input to both render tests the output of a method under test, so one bug in `gutters()` moves three tests and none of them points at the cause. Build the expected `Gutter` as a literal.

**N8** — The fixture contains `UNKNOWN` cells and the expectation counts them as gaps. That is the §6 decision, currently recorded nowhere. A test named for it is where the choice becomes findable.

**N9** — `assertEquals(size * size, actualCount)` checks the shape and never a cell value, because no value is knowable while N5 stands. Rename to `randomCellsReturnsRequestedShape` so the name stops overselling, then rewrite against a seed.

**N10** — Ragged, rectangular, and uneven run through three bare `assertThrows` in one method. When it fails you cannot tell which shape broke. Parameterize.

**N11** — It proves the inner rows are copies. `List.copyOf` wraps the **outer** list only, so `snapshot.set(0, …)` throws while `snapshot.get(0).set(0, …)` is merely harmless. Add the outer case so the test describes the real guarantee.

**N12** — Asserts nothing, constructs nothing. Checkstyle's `EmptyBlock` does not inspect method bodies, which is how it passed the gate.

**N13** — `private static` reassigned in `@BeforeEach`. `TESTING_STANDARDS.md` forbids shared writable static fixtures by name. Make it an instance field.

**N14** — Rewrites the first `.txt` token to an absolute path and mutates the array in place, restating production path handling inside the test. It works; it also hides which value a failing case used.

**N15** — Asserts that writing the same string twice yields the same bytes — a test of `Files.writeString`, not of your code. Its near-synonym name beside :46 is how two very different tests became indistinguishable.

**N16** — `FinalClass` would catch both utility classes in §9.2 and §9.3 automatically instead of by inspection.

**N17** — Defined as something `StringBuilder` does for heap management. RLE is what `gutters()` computes: runs of identical values as (value, length). It is this lesson's core algorithm.

### 9.11 Count

89 rows, 45 of them staged.

| KEEP | RENAME | REFACTOR | REVIEW | DELETE | ADD | MERGE | EXTEND | other |
|------|--------|----------|--------|--------|-----|-------|--------|-------|
| 27   | 17     | 16       | 9      | 6      | 4   | 4     | 3      | 4     |

*other* = 1 each of REWRITE, SPLIT, FIX, DONE.

Most of this is right — 27 KEEPs say so. The RENAMEs are the largest actionable group and the cheapest: no behaviour moves. The DELETEs touch nothing else, so do them first and the tree gets smaller before it gets better.
