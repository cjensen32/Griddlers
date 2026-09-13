# 2026-09-13 — From a tested model to a program that runs

Learner reference note. Not authority — `../../../TESTING_STANDARDS.md` owns the test rubric, `.agents/PROJECT.md` owns the architecture, and `../../03-the-three-packages.md` owns the assignment.

Written for the point where the bottom layer is finished and nothing above it works yet: two model types, two test files that genuinely hold them, a `Main` that constructs and returns, and `checkstyle.xml`, `import-control.xml`, and `pom.xml` already enforcing a direction the code has not yet had a chance to violate. The question this note answers is the one that state leaves open — **what actually has to be decided to get from there to a program that prints a grid and saves it?**

Every code example belongs to one of two invented projects, a **café receipt printer** and a **library fines desk**, and neither shares a line with this repository. Read them for shape. §9 is the exception: it is about this checkout, it is dated, and it is the part that expires.

## 1. The starting line

A model layer at 100% line and branch coverage is a strong claim and a narrow one. It says these types hold what you handed them, reject what they should, and give back copies rather than handles. It says nothing about where the next behavior goes, because the next behavior does not exist yet and a model has no way to ask for it.

That is the real shape of this gap. It is not "write three classes" — the three classes are about forty minutes of typing. It is four decisions, each cheap now and expensive in Chapter 3, that the three classes fall out of:

1. Does the computation layer own anything yet, or is it a name with a constructor call inside it?
2. Does turning state into characters belong to the model, to the computation layer, or to the layer that prints?
3. What does the write look like, and can a test point it anywhere?
4. What is the composition root allowed to be, and what does the build already believe about it?

Answer those four and the code is obvious. Skip them and you will write code that is also obvious, and unwind it in 2.5.

## 2. The gap, as a table

| Have                                                              | Missing                                            | The decision underneath                                                        |
|-------------------------------------------------------------------|----------------------------------------------------|--------------------------------------------------------------------------------|
| a value type per cell state, carrying a payload                   | a fixture that produces the same grid on every run | who owns a deterministic demo — computation, I/O, or the composition root (§3) |
| a validated grid that copies on the way in and on the way out     | a rendered string                                  | which layer turns state into characters (§4)                                   |
| a build that forbids `engine`/`model` from touching a file/stream | a file on disk, written the same way twice         | whether the destination is a parameter or an ambient fact (§5)                 |
| a `Main` that compiles                                            | a program that runs                                | what wiring is, and why it is one class's entire job (§6)                      |
| two tier 1 test files that hold their classes                     | a tier 1 test per new class                        | what makes a test file belong to the class in its filename (§7)                |

The right-hand column is the work. The middle column is what the middle of the lesson looks like when the right-hand column has been answered badly — everything present, everything green, nothing where it belongs.

## 3. What a computation layer is for

The first thing written into an empty computation class is almost always this:

```java
package com.example.cafe.billing;

import com.example.cafe.menu.LineItem;
import com.example.cafe.menu.Order;
import java.util.List;

public final class Tally {

  public Order open(List<LineItem> items) {
    return new Order(items);
  }
}
```

`open` takes the constructor's arguments and returns the constructor's result. It is a forwarding method: it adds a name and nothing else. Three honest responses, and the wrong one is to leave it unexamined.

**Delete it.** Callers can call the constructor, and a layer that exists to be a layer is a layer that has not started yet. Cheapest answer, and usually right.

**Keep it and make the name carry a decision the constructor cannot.** `Tally.forTakeaway(items)` and `Tally.withHappyHour(items, clock)` are factories: each one means something, and the meaning lives in the name rather than in an argument the caller has to get right. A static factory also gets to return a cached instance or a subtype; a constructor cannot.

**Keep it because the layer is about to earn it.** Legitimate for exactly one lesson. Write down which lesson.

What a computation layer is actually for is deriving facts the model does not store:

```java
public final class Tally {
  private static final int TAX_BASIS_POINTS = 875;

  public int subtotalCents(Order order) {
    return order.items().stream().mapToInt(item -> item.unitCents() * item.quantity()).sum();
  }

  public int taxCents(Order order) {
    return Math.round(subtotalCents(order) * TAX_BASIS_POINTS / 10_000f);
  }

  public int totalCents(Order order) {
    return subtotalCents(order) + taxCents(order);
  }
}
```

Nothing here is stored on `Order`, everything here is a function of it, and every one of these is deterministic — no clock, no locale, no file. That is what makes the layer testable with values in and values out, and it is the same property `import-control.xml` is protecting when it refuses the computation layer a stream.

**Coverage cannot see the difference.** A class whose only method forwards to a constructor reads 100% the moment anything at all constructs it. The number tells you a line executed; it does not tell you the line was worth writing.

## 4. Where rendering lives

Three splits, all defensible, with different bills:

| The glyph lives          | Reads as                                  | The bill                                                                                                        |
|--------------------------|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| on the model type        | an intrinsic property of the state        | a second presentation forces a second field, or a rewrite of every constant                                     |
| in the computation layer | a transform, next to the other transforms | the layer required to stay deterministic now owns a display concern, and Chapter 3's solver has to walk past it |
| in the printing layer    | presentation, where presentation goes     | one more class, and the model stays a pure alphabet                                                             |

The café's version of this argument is money. The model stores `int unitCents`, because cents are what a price *is*. `"£4.50"`, `"4,50 €"`, and a right-aligned column of the same number are what a price *looks like*, and the moment there are two of them a stored string is the wrong shape:

```java
// printing/MoneyFormat.java — the only place in the program where cents become characters.
final class MoneyFormat {
  private MoneyFormat() {}

  static String plain(int cents) {
    return "%d.%02d".formatted(cents / 100, Math.abs(cents % 100));
  }
}
```

The question that settles the split is not "which feels tidier" but **what happens when a second presentation exists**. One theme, one terminal, one locale: any of the three works. Two: the payload moves out to the renderer, and the usual shape is an `EnumMap` or a `switch` in the presentation layer — the `switch` also buys exhaustiveness checking over the constants, so adding a fourth state becomes a compile error instead of a blank cell.

Whichever you choose, **write the choice down with a date.** "The glyph stays on the model until a second theme exists" is a fine answer. Rediscovering it under pressure in Chapter 3 is not.

### One naming trap on the way past

```java
// Reads as an override of Object.render(). Is not one — the signatures differ,
// so this is an overload, and @Override on it would not compile.
public String toString(Order order) { ... }

// Reads as what it is.
public String render(Order order) { ... }
```

Java will let you do the first without a murmur, and every reader's first guess will be wrong. Name a transform for the transform.

## 5. The write seam

Here is the version that gets written first, every time:

```java
public final class ReceiptWriter {

  public void write(String receipt) {
    try {
      FileWriter out = new FileWriter(System.getProperty("user.home") + "/receipts/last.txt");
      out.write(receipt);
      out.close();
    } catch (IOException e) {
      // ignore
    }
  }
}
```

Four separate problems, each with its own price:

1. **The destination is an ambient fact, not a parameter.** There is no seam, so no test can aim this at a `@TempDir`. Running the suite writes into a real home directory, and running it twice on two machines proves two different things.
2. **`FileWriter(String)` takes no charset,** so the bytes depend on the default charset of whatever JVM ran it. A test that passes on your machine is not evidence about anyone else's.
3. **`close()` is not in a `finally` or a try-with-resources,** so a throw from `write` leaks the handle.
4. **The failure is swallowed.** This is the expensive one: "wrote the file" and "did not write the file" now produce the same observable outcome, and **no assertion you can write distinguishes them.** A test for this method can only ever prove that it returned.

The version with a seam:

```java
public final class ReceiptWriter {
  private final Path destination;

  public ReceiptWriter(Path destination) {
    this.destination = Objects.requireNonNull(destination, "destination is required");
  }

  public void write(String receipt) throws IOException {
    Files.writeString(destination, receipt, StandardCharsets.UTF_8);
  }
}
```

`Files.writeString` names the charset, creates or truncates, and closes for you — three of the four problems gone in one call. The fourth goes because `write` now throws: the layer that can do something useful about a failed write is the composition root, not this class, and passing the exception up is how it gets the chance.

What that buys, immediately:

```java
@Test
void theFileOnDiskIsExactlyWhatWasRendered(@TempDir Path dir) throws IOException {
  Path target = dir.resolve("receipt.txt");

  new ReceiptWriter(target).write("TOTAL  4.50\n");

  assertEquals("TOTAL  4.50\n", Files.readString(target, StandardCharsets.UTF_8));
}

@Test
void twoRunsProduceIdenticalBytes(@TempDir Path dir) throws IOException {
  Path first = dir.resolve("one.txt");
  Path second = dir.resolve("two.txt");

  new ReceiptWriter(first).write(SAMPLE);
  new ReceiptWriter(second).write(SAMPLE);

  assertArrayEquals(Files.readAllBytes(first), Files.readAllBytes(second));
}
```

Neither test is possible against the first version. Not harder — **not possible**, because the seam it would need does not exist. That is the general rule worth keeping: *a boundary that cannot be pointed somewhere else cannot be tested*, and the fix is always a parameter, never a cleverer test.

## 6. What the composition root is allowed to be

`import-control.xml` grants the root package one permission nothing below it gets: it may reach the I/O layer. That permission is the definition of the class, not a convenience — wiring concrete things together and starting one program is its whole job.

```java
public final class Main {
  private Main() {}

  public static void main(String[] args) throws IOException {
    Order order = new Order(MorningRush.items());
    String receipt = new ReceiptFormatter(new Tally()).render(order);

    System.out.print(receipt);
    new ReceiptWriter(Path.of("receipt.txt")).write(receipt);
  }
}
```

Construct, wire, run, print. No branch, no derivation, nothing a unit test would have caught.

**That is also why it is the one JaCoCo exclusion.** The exclusion in `pom.xml` is not a favour; it is a promise that everything this class does is trivial enough not to need a test. The moment a real decision moves in — parsing an argument, choosing a size, handling a missing file — the exclusion stops being an accurate description and starts being a place for bugs to sit unmeasured. `../../04-the-end-to-end-harness.md` is where that gets picked up, with a tier 3 subprocess that asserts the exit code and the exact stdout.

Two smaller things that live here:

- **A `test` prefix on a production method** is a promise to the reader that the method is scaffolding. If it ships and `Main` calls it, name it for what it does.
- **Decide which layer prints.** The I/O layer returning a string that the composition root prints is one answer; the I/O layer printing and returning nothing is another. What you cannot have is a class in `tools` named for printing that does not print, while the root does it instead — that is two answers at once, and the name is the one that is lying.

## 7. Testing three classes that do not exist yet

One rule does most of the work here:

> A tier 1 test file is named for the class it constructs. If `ReceiptWriterTest` never writes `new ReceiptWriter(...)`, it is a `Tally` test with the wrong filename.

Two things follow from that miss, and they are both invisible in a green run. `ReceiptWriter` now has no test at all, and `Tally` has two — one of which nobody will think to update when `Tally` changes.

**Coverage hides this in both directions.** A class can read 100% because some *other* class's test happened to execute it, and a class can read 0% while the test file bearing its name passes. The percentage measures which lines ran, never which file meant to run them. The check costs nothing: open each new test file and search it for `new ClassName`. If the name in the filename never appears after `new`, the file is misnamed.

| Class | Tier | What its own test file must own |
|---|---|---|
| the computation class | 1 | values in, values out; each derived fact; the boundary rows of the derivation |
| the rendering class | 1 | exact text for one small fixture, trailing newline included |
| the writing class | 1 | a `@TempDir` destination, the bytes on disk, the charset, and what a failed write does |
| the composition root | 3 | a real JVM through `ProcessBuilder`, exit code and exact stdout |
| all of them wired | 2 | one complete path, real collaborators, assembled the way `main` assembles them |

`../../../TESTING_STANDARDS.md` sets the proportion: dozens of tier 1, a handful of tier 2 per chapter, one or two tier 3 and no more. Part 1 owes tier 1 for each new class. Tier 3 is 2.4's.

### A fixture welded into a static initialiser

```java
public final class ReceiptPrinter {
  private static final Order ORDER;

  static {
    try {
      ORDER = new Order(MorningRush.items());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
```

This compiles, runs, and cannot be tested with any other order. The fixture is welded to the class rather than passed to it, so there is one scenario forever; a failure surfaces on class load as `ExceptionInInitializerError` with the real cause one `getCause()` down; and `catch (Exception)` around a constructor that throws `IllegalArgumentException` catches a programming error and renames it at runtime.

A demo fixture is data. Data arrives through a parameter, and a deterministic demo grid is not less deterministic for being handed in.

## 8. What Part 2 will ask of these shapes

Part 2 derives clues and puts them in gutters. None of that is here — this is only the three questions Part 2 will ask of whatever you build in Part 1, so that the answers are not already foreclosed.

**What does a derived collection return?** The library fines desk:

```java
package com.example.library.fines;

public final class FineSchedule {

  /** Every fine owed on this loan, oldest first. Empty when nothing is owed. */
  public List<Fine> assess(Loan loan) { ... }
}
```

Three things decided by that one signature. It returns a `List` rather than an array, so it can be unmodifiable and has a usable `equals` for tests. It returns an unmodifiable one — `List.of()` and `List.copyOf` both produce them — so a caller cannot reach back through the result and corrupt a computation. And it never returns `null`, because "nothing owed" is a real answer and `null` forces every caller to write the same guard.

**Where does the "nothing to report" case get pinned?** A loan returned on time: an empty list, or one `Fine` of zero? Both are defensible, and Part 2 says explicitly that this is your call. What makes the call stick is the test, and what pays for it is the renderer — an empty list means the gutter has to decide what an empty gutter looks like, and a zero-valued element means it does not. Decide it in the order the cost appears: renderer first, then the test, then the signature.

**What does a gutter do to a renderer that returns one flat string?** The café's right-aligned price column is the same problem:

```
Flat white          4.50
Croissant           3.25
Toast and jam      12.00
```

A renderer that appends a row and ends it with `\n` cannot right-align anything, because when it emits the first row it does not yet know the widest value. Alignment needs the width before the first character — which means either two passes over the data, or a width computed up front and handed to the row formatter. `String.format` carries the width and alignment specifiers once you have the number.

The consequence for Part 1 is small and worth taking: **if the render method's only parameter is the grid, a gutter width has nowhere to come from.** You do not need the gutter now. You need the method to be the kind of method that can grow one.

The size argument lands in the same place. `Main` taking `10` and printing a 10x10 means an argument gets parsed, which means `args.length == 0` and `args[0]` being `"abc"` are both real inputs — and per §6, a decision in `Main` is a decision inside the JaCoCo exclusion. Where that parsing lives is the same question as where the fixture lives, arriving a second time.

## 9. Findings — 2.3, the engine and tools half

About this checkout on 2026-09-13, not about the café.

**The coverage numbers as they actually stand.** From `target/jacoco/jacoco.csv`: the two model classes are at 100% line and branch, the engine class is at 100%, and the terminal tool is at **0 of 21 lines**. `Main` is the only exclusion in the `0.8` CLASS rule, so `mvn -B verify` does not pass today, and the failing class is the one whose test file is green.

**A green test file that never constructs the class it is named for.** The terminal tool's test builds its own engine and asserts on the engine's output; the class in the filename is never instantiated. §7 of `2026-09-12-single-type-test-shapes.md` recorded this same failure in the opposite direction three weeks ago — a class reading 100% off another class's test. Same cause, both signs: **the filename and the `new` disagree.**

**The render method is an overload of `Object.toString()`.** It takes an argument, so it does not override anything; `@Override` on it would fail to compile. §4.

**The 4x4 fixture lives in the I/O layer, inside a static initialiser that catches `Exception`.** The lesson said the computation layer; the code says otherwise; §3 and §7 are the two halves of why it matters, and the layer question is genuinely open.

**The write is `FileWriter(String)` into `~/Downloads`, with the `IOException` swallowed.** All four problems in §5 at once, and this was already written down on 2026-09-12. A finding that survives a rewrite is a finding the rewrite was not aimed at.

**The production method carries a `test` prefix, and the composition root prints while the tool returns.** §6.

**Boxes settled while writing this note.** Two "must be true" boxes in 2.3 Part 1 were unchecked while already satisfied and provable — the returned-string design, and Checkstyle's import and `System` rules passing over `engine` and `model`. Three work items described a shape that was never built, including one glyph that does not exist. The lesson has been refitted against the checkout. Reading a lesson against the code rather than against memory keeps turning up over-claims and under-claims in roughly equal numbers, which is the argument for doing it at the start of a sitting rather than the end.

## Pointers for this half

| Pointer | What to look for |
|---|---|
| [`java.nio.file.Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) | `writeString` and `readString` with an explicit `Charset`, and what the default `OpenOption`s do |
| [`java.nio.charset.StandardCharsets`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/charset/StandardCharsets.html) | why the constant is better than the string, and what the platform default costs a test |
| [`java.nio.file.Path`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html) | `of` and `resolve`, which is how a `@TempDir` becomes a destination |
| [`java.util.Objects`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Objects.html) | `requireNonNull` with a message, and the exception type it commits you to |
| [`java.util.EnumMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/EnumMap.html) | a presentation-layer mapping that leaves the model alone |
| [`java.util.Formatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html) | width, precision, and the `-` flag — the whole alignment vocabulary in one page |
| [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) | what it replaces, and why a method that closes for you is better than remembering |
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) | `@TempDir` on a parameter versus a field, and when the directory is cleaned up |
| [`jacoco:check`](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html) | what a `CLASS` element rule reports when one class is at zero, and what an exclusion is promising |
