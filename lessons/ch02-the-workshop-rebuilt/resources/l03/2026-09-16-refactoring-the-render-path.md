# 2026-09-16 — Refactoring `render`, and what a half-finished extraction costs

Learner reference note. Not authority — `../../../TESTING_STANDARDS.md` owns the rubric, `.agents/PROJECT.md` owns the architecture, and `../../03-the-three-packages.md` owns the assignment. Where this note and one of those disagree, this note is the one that is out of date.

Written against the checkout mid-refactor, with the build red. Every claim below came from running something; the evidence is in the line. The invented examples belong to a **platform board** — a railway departures display — and share no code with this repository. Read them for shape.

This note is about one method and its helpers, and about the process that got them into their current state. The process half is the part that transfers.

## 1. Where it stands

`mvn -B test`: **71 tests, 1 failure, 1 error.** Both are in `GriddlerEngineTest`, both are in the render path, and both were introduced by the same edit. Everything else in the suite is green — `model`, `tools`, and the clue derivation are untouched and passing.

Two of the four things the previous note asked for have landed, and they landed well:

- **`Layout` and `measure` exist**, so `render` is four lines instead of thirty. `drawBody` takes three parameters instead of six.
- **`Layout` now lives in `engine`, not `model`.** That is the right call and it closes the layering question the previous note raised — character widths are a rendering concern and `model` should never have known about them. §7 has one consequence of the move that is worth two minutes.

What is red is the third item: converting the clue loops away from the reverse-index pattern. That conversion is half done, and the half-done state is the subject of this note.

## 2. Reading the failure before reading the code

Start here, because the failure output names the defect before you open the file.

```
expected: <    |   | 1 |   |   |        but was: <    |   |   | 2 |
              | 2 | 1 | 2 | 3 |                      |   | 1 | 1 |
                                                     |   |   | 2 |
                                                     |   |   | 3 |
```

The body of the grid below this is **byte-identical to the expectation**. Only the header changed, so only the header code is suspect — `drawBody` is not implicated at all, and `measure` is producing correct widths, because the borders and the left gutter still line up perfectly.

Now count the header:

|          | Lines | Clue columns per line |
|----------|-------|-----------------------|
| Expected | 2     | 4                     |
| Actual   | 4     | 3                     |

**A 2×4 block came out as 4×3.** When the two dimensions of a rectangular output swap places, the first hypothesis is always that something is being indexed transposed — that a `[row][column]` structure is being read `[column][row]`. That hypothesis costs nothing to check and it is correct here.

The second failure confirms it from the other direction. The 10×10 puzzle has 5 clue lanes and 10 columns, so 5 and 10 are not interchangeable the way 2 and 4 nearly were, and it does not produce wrong output at all — it throws:

```
java.lang.IndexOutOfBoundsException: Index 6 out of bounds for length 6
	at com.connorjensen.griddlers.engine.GriddlerEngine.drawTopClues(GriddlerEngine.java:120)
```

Length 6 is a lane list. Index 6 is a column index. **A column index arrived at a lane list**, which is the transposition stated as an exception.

> The general habit worth keeping: when output is wrong rather than absent, measure its shape against the expected shape *first*. Wrong dimensions mean a wrong index. Wrong count in one dimension means an off-by-one. Right shape, wrong contents means a wrong value. You can usually name the class of defect before you read a line of code.

## 3. The four defects

They are separate bugs. They arrived in one edit and they interact, which is what makes this hard rather than tedious.

### 3.1 The reversal that used to cancel itself — `offsetValues:237`

This is the interesting one, and it is the root of the redesign.

The original loop did two inversions: it reversed the clue list, *and* it walked the index downward. Two inversions cancel. That is why the original code worked despite being hard to read — the reader had to hold both in their head to see that they annihilated.

The new `bottomAlign` removed the `.reversed()` call and kept the descending loop:

```java
for (int i = lanes; i >= 0; i--) {
```

One inversion removed, one left standing. The clues now come out backwards. For a column whose clues are `[3, 1]`, the header reads `1` above `3`.

Look at the 4×4 output again: the second header line is `|   | 1 | 1 |`. Column 1's clues are `[1, 1]` — a palindrome, so its inversion is invisible. Every other column in this fixture has a single clue, and a one-element list cannot be observed to be reversed either. **The 4×4 fixture cannot detect this bug at all.** It is being caught only by the transposition sitting on top of it. That is worth knowing before you fix the transposition and assume green means correct.

The replacement is an offset, applied to an ascending loop. Lane `L` of `laneCount` holds clue `L - (laneCount - clues.size())`, and is blank when that number is negative:

```
clues = [A, B]      laneCount = 4      offset = 4 - 2 = 2

lane 0  ->  blank        0 - 2 < 0
lane 1  ->  blank        1 - 2 < 0
lane 2  ->  clues[0]     2 - 2 = 0
lane 3  ->  clues[1]     3 - 2 = 1
```

Ascending loop, one subtraction, no reversal, no descending counter, and the short list ends up flush against the grid where it belongs. This is the whole technique.

### 3.2 One lane too many — `offsetValues:237`

`for (int i = lanes; i >= 0; i--)` runs `lanes + 1` times, so the returned list is always one longer than the lane count it was asked for. A loop that produces `n` items is bounded `i < n`, ascending or descending; `i >= 0` starting at `n` is the shape that produces `n + 1`.

This is the extra header lines in the actual output, doubled by §3.4.

### 3.3 The transposition — `drawTopClues:120`

`organizedTopClues` is built one entry per column, each entry a list of lanes. It is `[column][lane]`.

```java
for (int i = 0; i <= organizedTopClues.getFirst().size(); i++) {   // i counts lanes
  for (int j = 0; j < organizedTopClues.size() - 1; j++) {         // j counts columns
      sb.append(organizedTopClues.get(i).get(j));                  // reads [lane][column]
```

`i` is a lane counter used as a column subscript; `j` is a column counter used as a lane subscript. There is a second tell in the bounds, independent of the access: **each loop is bounded by the dimension it does not index.** `i` is bounded by `getFirst().size()`, the lane count, and `j` by `organizedTopClues.size()`, the column count. Whenever a bound and its subscript disagree about which axis they are on, one of the two is wrong.

This is what threw, and it is why the header came out transposed.

### 3.4 Two off-by-ones, in opposite directions — `drawTopClues:115` and `:118`

`i <= ...size()` is one too many. `j < ...size() - 1` is one too few, and it silently drops the last column — visible in the actual output as three clue columns over a four-column grid.

They point in opposite directions, so a fixture where lanes and columns are close in size can have them partly cancel in the line count while still being wrong. Do not fix them by adjusting until the output looks right; fix them by deciding what each loop is counting and writing the bound that says so.

### 3.5 Why four bugs is more than four times one bug

Each defect alone would have been a two-minute fix with an obvious symptom. Together they produce one wrong string, and the string is the *composition* of all four. You cannot read it and work backwards to any single one, because the transposition scrambles the evidence for the reversal, the reversal is invisible in this fixture anyway, and the two off-by-ones change the dimensions that would otherwise let you count.

That is the actual cost, and it is paid in debugging time rather than in the code. §4 is how it is avoided.

## 4. The process — what went wrong, and the discipline that prevents it

The refactoring rule, stated once:

> **Never change structure and behaviour in the same step.** A step that moves code must not also change what the code computes. A step that changes what the code computes must not also move it.

The edit that produced the red build did four things at once:

1. Introduced a new function that had not existed before (`bottomAlign`).
2. Changed the algorithm inside it — dropping the reversal in favour of an offset.
3. Rewrote the call site's loop structure from one nested loop to a build-then-draw pair.
4. Left the previous implementation commented out beneath it.

Only (3) is a refactoring. (1) and (2) are new code, and new code needs a test before it has a caller. When the suite went red there were four candidate causes and no way to separate them, because nothing was green in between.

The version that costs less:

**Write the characterization test first.** You already have two — the golden strings in `GriddlerEngineTest`. That is exactly the right safety net for this kind of work: they pin the entire observable output, so any behavioural change at all shows up. Note what they cost you here, though: they only pin the *composition*. Which brings us to the second habit.

**Test the extracted helper directly, before the call site uses it.** `bottomAlign` is a pure function — a list and two ints in, a list of strings out. A test table over it would have caught §3.1 and §3.2 in isolation, in about ten lines, with a failure message that names the defect instead of showing you a 400-character grid. A golden master tells you *that* you broke something; a unit test on the piece tells you *what*.

**One transformation per run of the suite.** Extract the helper as an exact behavioural copy of the code it replaces — reversal and all — and run the suite. Green. *Then* change the algorithm inside it, with its own test. Green. *Then* convert the second call site. Green. Three green bars, three small diffs, and if any one of them goes red there is exactly one thing it can be.

**Delete the commented-out block at `:124-141`.** It is not a safety net; Git is the safety net, and the old version is one `git diff` away. What the block actually does is make the file longer at the moment it is hardest to read, and it will be stale within a day of the fix landing. If the old implementation is worth keeping, it is worth keeping as a commit.

## 5. The technique, on something that is not a nonogram

The platform board. Each service occupies a column; above it sit its calling points, bottom-aligned against the board so the last stop before the destination is always on the row nearest the grid, no matter how many stops a service makes.

```java
package com.example.board.display;

import java.util.ArrayList;
import java.util.List;

final class Lanes {
  private Lanes() {}

  /** Bottom-aligns {@code stops} into exactly {@code laneCount} lanes, blank lanes first. */
  static List<String> bottomAlign(List<String> stops, int laneCount) {
    List<String> lanes = new ArrayList<>(laneCount);
    int offset = laneCount - stops.size();

    for (int lane = 0; lane < laneCount; lane++) {
      lanes.add(lane < offset ? "" : stops.get(lane - offset));
    }
    return lanes;
  }
}
```

Three things about that function are the point, and none of them is the arithmetic.

**It returns exactly `laneCount` entries.** The loop is bounded `lane < laneCount`, so the size of the result is stated by the loop header rather than inferred. There is no way for it to be off by one without the bound being visibly wrong.

**It does one job.** It pads; it does not format. The empty lane is `""`, not a pre-padded run of spaces, because "there is no stop here" and "here is a field three characters wide" are different facts and the caller needs them separately.

**It is testable in one line per case**, with no board around it:

```java
assertEquals(List.of("", "", "REDHILL", "GATWICK"), Lanes.bottomAlign(List.of("REDHILL", "GATWICK"), 4));
assertEquals(List.of("", "", "", ""),               Lanes.bottomAlign(List.of(), 4));
assertEquals(List.of("CREWE"),                      Lanes.bottomAlign(List.of("CREWE"), 1));
```

That second case — the empty list — is the one that matters here, and §8 says why.

### Why the split matters for the second call site

`bottomAlign` currently calls `center` on its way out, which welds padding to formatting. That is why it cannot serve the left gutter: the header centres clues in `cellWidth`, the gutter right-aligns them in `gutterWidth`. One function that pads and one that formats serve both. One function that does both serves one, and the other call site keeps its hand-rolled arithmetic forever.

The board formats after padding, in the layer that knows how wide a column is:

```java
for (String stop : Lanes.bottomAlign(service.callingPoints(), laneCount)) {
  row.append('|').append("%-" + columnWidth + "s").formatted(stop);
}
```

`java.util.Formatter` carries width and justification: `%5s` right-aligns in five, `%-5s` left-aligns. The width can be computed — build the format string, as above. There is no built-in centre specifier, which is why `center` stays; it is the only one of the three that has to be hand-written, and it is already correct.

## 6. `drawBody` is your control — leave it alone until the header is green

`drawBody:151-167` still has the original pattern, untouched: `.reversed()` plus a descending index, both inversions intact, working correctly. Every row of the grid body in the failing output is byte-perfect, which proves it.

Do not convert it yet. Right now it is the control in the experiment — it is the evidence that `measure`, `center`, `drawBorder`, and the left-gutter widths are all still right, and it is what lets you say the header is the only suspect. Convert it after the header is green, as its own step, with the suite green behind it.

When you do convert it, it is the same `bottomAlign` shape with a different formatter, and it has one extra defect of its own that the previous note raised and is still open: `for (int rowIndex = 0; rowIndex < rowClues.size(); rowIndex++)` bounds the row loop by the clue list while indexing `cells`. The number of rows is the griddler's fact, not the gutter's.

## 7. One consequence of `Layout` moving into `GriddlerEngine`

Line 112 passes `layout.yClueSize` — no parentheses — in the same argument list as `layout.cellWidth()`, with parentheses. Both compile.

A record's components are `private final` fields, and the accessor is the public way to read one. `layout.yClueSize` reaches the field directly, which is legal **only because `Layout` is declared inside `GriddlerEngine`**: a class may read the private members of a type nested within it. Move `Layout` to its own file and that line stops compiling, while the one beside it keeps working.

It is not a bug today. It is worth fixing to the accessor now, because the inconsistency is invisible until the day it becomes a compile error, and the day it becomes a compile error is the day you are doing something else.

## 8. Coverage — what is red, and what will still be untested when it goes green

The gate is `LINE` and `BRANCH` at `0.9`. Getting the suite green will not get coverage green, because the golden-master tests exercise one path through a lot of code and assert on its composition.

Named gaps, in the order they are cheapest to close:

- **`bottomAlign` has no direct test.** It is a pure function and it currently holds two of the four defects. This is the highest-value test in the list and the smallest.
- **The empty clue list.** A row or column with no filled cells produces `List.of()`. With the offset formula that is `offset == laneCount` and every lane blank. The 4×4 fixture has one such row and it renders, but nothing asserts the behaviour, and it is the boundary most likely to break under a rewrite.
- **A non-square griddler**, 3 rows by 5 columns. The border bug the previous note found is fixed at `:154` and `:176`; nothing proves it stays fixed, because both render fixtures are square. Both of this note's dimension arguments in §2 also depend on rows and columns being distinguishable.
- **`measure` in isolation.** It is private again after the move, so test it through its effects or leave it to the golden masters — but note that `cellWidth % 2 == 0` is a branch reached only by a two- or four-digit clue, which the 10×10 fixture happens to supply by accident.
- **`Gutter` still drops null rows silently** (`deepCopyClues` adds the copy inside the null check, so a null row shortens the list rather than being rejected), and **still hands out mutable inner lists**. Both were raised on 2026-09-15 and both are open. The first is what makes §6's row-count mismatch reachable from a legal-looking construction.
- **`randomizeCells` still constructs `new Random()` internally**, which contradicts the determinism `.agents/CONTEXT.md:15` claims for `engine` and caps its test at asserting shape.

## 9. Suggested order

The sequencing is most of the value here.

1. **Delete the commented-out block.** No behaviour, smaller file, and the diff for everything after it becomes readable.
2. **Write the `bottomAlign` test table** — including the empty list and the single-element list. It goes red on §3.1 and §3.2, in isolation, with a legible message.
3. **Fix `bottomAlign`**: ascending loop bounded by the lane count, offset arithmetic, blank lanes that mean blank. Its own test goes green. The golden masters are still red — that is correct and expected, because §3.3 and §3.4 are untouched.
4. **Fix `drawTopClues`**: the transposed subscripts and both bounds. Golden masters go green.
5. **Split padding from formatting**, so the helper returns raw values and the caller centres them.
6. **Convert `drawBody`** to the same helper, and fix the row-count bound while you are in it.
7. **Add the non-square fixture**, then the coverage gaps in §8.

Steps 2–4 are the ones that matter. Each has exactly one thing that can go wrong, and the suite tells you between each.

## 10. Pointers

| Pointer                                                                                                                        | What to look for                                                                     |
|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| [`java.util.Formatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html)                 | width, the `-` flag, and building a format string from a computed width              |
| [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)                           | `reversed()` returns a *view*, not a copy — and `addLast` is `add`                   |
| [`List.copyOf`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)) | immutable at one level, and that it rejects nulls — both relevant to `Gutter`        |
| [JLS 6.6.1 — Determining Accessibility](https://docs.oracle.com/javase/specs/jls/se21/html/jls-6.html#jls-6.6.1)               | why a nested record's private field is readable from the class that encloses it — §7 |
| [`@ParameterizedTest` / `@MethodSource`](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)  | the shape a table of lane-padding cases wants                                        |
| [Refactoring catalog — *Extract Function*](https://refactoring.com/catalog/extractFunction.html)                               | the step that moves code without changing it, and why that is the whole discipline   |
| [*Characterization test*](https://en.wikipedia.org/wiki/Characterization_test)                                                 | the name for what the two golden strings are, and what they can and cannot tell you  |

---

## 11. Notes

**N1** — `bottomAlign` is named for its mechanism rather than its result. The caller does not want an offset; it wants a fixed number of lanes. A name describing the return value makes the `lanes + 1` bug in §3.2 visible at the call site.

**N2** — `organizedTopClues` does not say which axis is outer. A name that does — or a comment stating `[column][lane]` on the declaration — is what makes §3.3 readable rather than discoverable. Two-dimensional structures earn one line saying which index is which.

**N3** — `offsetVals.addLast(...)` is identical to `add` for an `ArrayList`. It reads as though insertion order is being deliberately controlled, which is exactly the impression you do not want while the order is in fact wrong.

**N4** — `val = " "` uses a single space as the "no clue here" sentinel, then centres it. It produces the right characters by arithmetic accident. An empty lane should say it is empty and let the formatter decide its width, which is also what lets one padding function serve two differently-sized fields.

**N5** — `organizedTopClues.getFirst()` throws on an empty list. `Griddler` guarantees at least one row, but `drawTopClues` receives a bare `List<List<Integer>>` and so does not inherit the guarantee. Same shape as the `cells.getFirst()` calls at `:154` and `:176`.
