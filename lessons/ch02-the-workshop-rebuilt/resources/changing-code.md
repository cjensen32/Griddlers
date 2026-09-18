# Changing code that already works

Learner reference. Not authority — `../../TESTING_STANDARDS.md` owns the test rubric, `.agents/PROJECT.md` owns the architecture, and `.agents/reference/COURSE_STANDARDS.md` owns the conventions. Where this file and one of those disagree, this one is out of date.

Reach for this when the code exists and you are moving it, or when a change has gone red and you are working out why. Its companion, [`test-shapes.md`](test-shapes.md), is for when you are writing the test file in the first place.

The examples belong to an invented **platform board** — a railway departures display — and share no code with this repository. Read them for shape.

## 1. The rule

> **Never change structure and behaviour in the same step.** A step that moves code must not also change what the code computes. A step that changes what the code computes must not also move it.

The way this gets broken is rarely deliberate. An edit that extracts a helper is a refactoring; an edit that extracts a helper *and* improves the algorithm inside it while it is passing through is two changes wearing one diff. When the suite goes red there are then two candidate causes and no way to separate them, because nothing was green in between.

The version that costs less:

**Write the characterization test first.** A golden string that pins the entire observable output is the right safety net for this kind of work — any behavioural change at all shows up. Know what it cannot do, though: it pins the *composition*, so it tells you *that* you broke something and never *what*.

**Test the extracted helper directly, before the call site uses it.** A pure function — values in, values out — takes about ten lines to put a table around, and its failure message names the defect instead of showing you four hundred characters of grid. A golden master tells you the output changed; a unit test on the piece tells you which piece.

**One transformation per run of the suite.** Extract the helper as an exact behavioural copy of the code it replaces — warts and all. Run the suite. Green. *Then* change the algorithm inside it, with its own test. Green. *Then* convert the second call site. Green. Three green bars, three small diffs, and if any one of them goes red there is exactly one thing it can be.

**Delete the commented-out block.** It is not a safety net; Git is the safety net, and the old version is one `git diff` away. What a commented-out block actually does is make the file longer at the moment it is hardest to read, and it will be stale within a day. If the old implementation is worth keeping, it is worth keeping as a commit.

### Why several small bugs are worse than the sum of them

Each defect alone is a two-minute fix with an obvious symptom. Landing four in one edit produces **one** wrong string, and that string is the composition of all four. You cannot read it and work backwards to any single one, because each defect scrambles the evidence for the others — one may be invisible in your fixture, two may partly cancel, and a third may change the dimensions you would otherwise count. The cost is paid in debugging time rather than in the code.

## 2. Read the failure before you read the code

When output is wrong rather than absent, measure its shape against the expected shape *first*. You can usually name the class of defect before opening a file.

| What you see                    | What it means          |
|---------------------------------|------------------------|
| Wrong dimensions                | a wrong index          |
| Wrong count in one dimension    | an off-by-one          |
| Right shape, wrong contents     | a wrong value          |
| Right everywhere but one region | only that region's code is suspect |

That last row is the one people skip. If the body of a rendered block is byte-identical to the expectation and only the header differs, then the body's code is not implicated at all — and everything the body depends on is thereby proven still correct. You have a control in your experiment; use it, and do not start "fixing" the parts that are working.

**When the two dimensions of a rectangular output swap places, suspect a transposition** — a `[row][column]` structure being read `[column][row]`. It costs nothing to check. The confirming tell lives in the loop bounds, independent of the access: if a loop is bounded by the size of one axis and subscripts the other, one of the two is wrong. A bound and its subscript disagreeing about which axis they are on is a defect you can see without running anything.

A second reading of the same bug arrives as an exception rather than wrong output. `Index 6 out of bounds for length 6`, where 6 is the length of a lane list and the index is a column number, is the transposition stated out loud.

**Beware the fixture that cannot detect the bug.** A two-element palindrome is the same reversed. A one-element list cannot be observed to be reversed at all. A square grid cannot distinguish rows from columns. If your only fixture has those properties, a passing suite proves less than it looks like it proves — which is why the non-square case and the empty case earn their place.

## 3. Extract a function that is worth extracting

The platform board: each service occupies a column, and above it sit its calling points, bottom-aligned against the board so the last stop before the destination is always on the row nearest the grid, no matter how many stops a service makes.

```java
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

**It returns exactly `laneCount` entries.** The loop is bounded `lane < laneCount`, so the size of the result is *stated* by the loop header rather than inferred. There is no way for it to be off by one without the bound being visibly wrong. Compare a descending `for (int i = n; i >= 0; i--)`, which runs `n + 1` times — the shape that silently produces one item too many.

**It does one job.** It pads; it does not format. The empty lane is `""`, not a pre-padded run of spaces, because "there is no stop here" and "here is a field three characters wide" are different facts and the caller needs them separately. A single space used as a "nothing here" sentinel produces the right characters by arithmetic accident, and only for one field width.

**It is testable in one line per case**, with no board around it:

```java
assertEquals(List.of("", "", "REDHILL", "GATWICK"), Lanes.bottomAlign(List.of("REDHILL", "GATWICK"), 4));
assertEquals(List.of("", "", "", ""),               Lanes.bottomAlign(List.of(), 4));
assertEquals(List.of("CREWE"),                      Lanes.bottomAlign(List.of("CREWE"), 1));
```

The empty-list case is the boundary most likely to break under a rewrite, and the one a real fixture supplies by accident rather than on purpose.

### Padding is not formatting

Welding the two together is what stops one helper serving two call sites. A header centres its values in a cell width; a left gutter right-aligns its values in a gutter width. One function that pads and one that formats serve both; one function that does both serves one, and the other call site keeps its hand-rolled arithmetic forever.

Format afterwards, in the layer that knows how wide a column is. `java.util.Formatter` carries width and justification — `%5s` right-aligns in five, `%-5s` left-aligns — and the width can be computed by building the format string. There is no built-in centre specifier, which is the one case you have to hand-write.

### Two inversions that cancel

A loop that reverses a list *and* walks its index downward is correct, because two inversions annihilate — and it is very hard to read, because the reader has to hold both to see that they do. Remove one of them and the output silently comes out backwards.

The replacement is an offset applied to an ascending loop, which is what `bottomAlign` above does. Ascending loop, one subtraction, no reversal, no descending counter:

```
stops = [A, B]      laneCount = 4      offset = 4 - 2 = 2

lane 0  ->  blank        0 - 2 < 0
lane 1  ->  blank        1 - 2 < 0
lane 2  ->  stops[0]     2 - 2 = 0
lane 3  ->  stops[1]     3 - 2 = 1
```

## 4. Naming, while you are in there

- **Name a helper for its result, not its mechanism.** The caller does not want an offset; it wants a fixed number of lanes. A name describing the return value makes an off-by-one visible at the call site.
- **Say which index is which.** A two-dimensional structure whose name does not state its axis order earns one line of comment — `[column][lane]` on the declaration — and that line is what makes a transposition readable rather than discoverable.
- **Do not imply control you do not have.** `list.addLast(x)` is identical to `add` for an `ArrayList`. It reads as though insertion order is being deliberately managed, which is exactly the impression you do not want while the order is in fact wrong.
- **A `test` prefix on a production method** is a promise to the reader that the method is scaffolding. If it ships and the composition root calls it, name it for what it does.

## 5. Where a seam belongs: the write

Here is the version that gets written first, every time:

```java
public final class DepartureLog {

  public void write(String board) {
    try {
      FileWriter out = new FileWriter(System.getProperty("user.home") + "/logs/last.txt");
      out.write(board);
      out.close();
    } catch (IOException e) {
      // ignore
    }
  }
}
```

Four separate problems, each with its own price:

1. **The destination is an ambient fact, not a parameter.** There is no seam, so no test can aim this at a `@TempDir`. Running the suite writes into a real home directory, and running it on two machines proves two different things.
2. **`FileWriter(String)` takes no charset,** so the bytes depend on the default charset of whatever JVM ran it. A test that passes on your machine is not evidence about anyone else's.
3. **`close()` is not in a `finally` or a try-with-resources,** so a throw from `write` leaks the handle.
4. **The failure is swallowed.** This is the expensive one: "wrote the file" and "did not write the file" now produce the same observable outcome, and **no assertion you can write distinguishes them.** A test for this method can only ever prove that it returned.

The version with a seam:

```java
public final class DepartureLog {
  private final Path destination;

  public DepartureLog(Path destination) {
    this.destination = Objects.requireNonNull(destination, "destination is required");
  }

  public void write(String board) throws IOException {
    Files.writeString(destination, board, StandardCharsets.UTF_8);
  }
}
```

`Files.writeString` names the charset, creates or truncates, and closes for you — three of the four problems gone in one call. The fourth goes because `write` now throws: the layer that can do something useful about a failed write is the composition root, not this class, and passing the exception up is how it gets the chance.

Two tests become possible that were not merely harder before but impossible — one asserting that the file on disk is exactly what was rendered, read back with an explicit `StandardCharsets.UTF_8`, and one asserting that a failed write is not silent, with `assertThrows(IOException.class, …)` against a path whose parent does not exist. That is the general rule worth keeping: **a boundary that cannot be pointed somewhere else cannot be tested, and the fix is always a parameter, never a cleverer test.**

The same rule covers randomness. A method that constructs its own `new Random()` cannot be asserted beyond its output's *shape*; a method that takes a `Random` can be seeded and asserted exactly. `new Random(seed)` produces an identical sequence on every machine and JDK version — a documented guarantee, not an accident of implementation. Seeding only helps if nothing downstream reintroduces ambiguity: `Math.random()`, a bare `new Random()`, and `HashSet` iteration order are all still non-deterministic.

## 6. What the composition root is allowed to be

The root package gets one permission nothing below it has: it may reach the I/O layer. That permission is the *definition* of the class, not a convenience — wiring concrete things together and starting one program is its whole job.

```java
public final class Main {
  private Main() {}

  public static void main(String[] args) throws IOException {
    Board board = new Board(EveningPeak.services());
    String rendered = new BoardRenderer(new Widths()).render(board);

    System.out.print(rendered);
    new DepartureLog(Path.of("board.txt")).write(rendered);
  }
}
```

Construct, wire, run, print. No branch, no derivation, nothing a unit test would have caught.

**That is also why it is the one JaCoCo exclusion.** A coverage exclusion on the composition root is not a favour; it is a promise that everything the class does is trivial enough not to need a test. The moment a real decision moves in — parsing an argument, choosing a size, handling a missing file — the exclusion stops being an accurate description and starts being a place for bugs to sit unmeasured. A tier 3 subprocess asserting the exit code and the exact stdout is what replaces it.

Three smaller things that live at this layer:

- **Print before you write.** Write-then-print means a failed write produces exit 1 with zero bytes of stdout, and the thing you rendered is discarded unseen. Swapping two lines costs nothing and makes the failure legible.
- **Do work lazily, after parsing.** Creating a resource before reading the arguments means a run that overrides it creates something it never uses — and, for anything that touches the filesystem, leaves it behind.
- **Decide which layer prints.** The I/O layer returning a string that the root prints is one answer; the I/O layer printing and returning nothing is another. What you cannot have is a class named for printing that does not print while the root does it instead — that is two answers at once, and the name is the one that is lying.

## 7. Pointers

| Pointer                                                                                                                        | What to look for                                                                     |
|--------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| [Refactoring catalog — *Extract Function*](https://refactoring.com/catalog/extractFunction.html)                               | the step that moves code without changing it, and why that is the whole discipline |
| [*Characterization test*](https://en.wikipedia.org/wiki/Characterization_test)                                                 | the name for a golden string, and what it can and cannot tell you                  |
| [`java.util.Formatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html)                 | width, the `-` flag, and building a format string from a computed width            |
| [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)                           | `reversed()` returns a *view*, not a copy — and `addLast` is `add`                 |
| [`List.copyOf`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)) | immutable at one level only, and that it rejects nulls                             |
| [`java.nio.file.Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html)                 | `writeString`, and the charset argument worth passing explicitly                   |
| [`java.util.Random`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Random.html)                       | the reproducibility guarantee a seed actually gives you                            |
| [JLS 6.6.1 — Determining Accessibility](https://docs.oracle.com/javase/specs/jls/se21/html/jls-6.html#jls-6.6.1)               | why a nested type's private field is readable from the class enclosing it          |
| [`@ParameterizedTest` / `@MethodSource`](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)  | the shape a table of cases over an extracted helper wants                          |

## 8. One trap worth knowing before it bites

A record nested inside another class can have its components read two ways: `layout.cellWidth()` through the accessor, and `layout.cellWidth` straight at the private field. **Both compile** — a class may read the private members of a type nested within it. Move that record to its own file and the field access stops compiling while the accessor beside it keeps working.

It is not a bug on the day you write it. It is worth fixing to the accessor immediately, because the inconsistency is invisible until the day it becomes a compile error, and the day it becomes a compile error is the day you are doing something else.
