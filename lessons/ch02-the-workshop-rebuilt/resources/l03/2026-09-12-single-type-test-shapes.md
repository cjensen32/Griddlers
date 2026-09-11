# 2026-09-12 — Single-type test shapes, and what a public field costs

Learner reference note. Not authority — `../../../TESTING_STANDARDS.md` owns the rubric this note works from, and `.agents/PROJECT.md` owns the architecture it assumes.

Written during the 2.3 Part 1 test pass, after a coverage audit and two rounds on `CellTest`. Every code example below belongs to an invented **Clock Project** and shares no code with this repository. Read them for shape. Copying one into `../../../../src` would be copying the wrong domain.

## The rule everything else follows from

> A test file is finished when every decision a future edit could plausibly change by accident has exactly one test that fails — and nothing else.

Both halves carry weight.

**"has one test that fails"** is completeness. Walk the production file and ask of each line: if someone deleted this, changed this number, or dropped this guard, would a test go red? If no, that decision is unprotected, and coverage percentage will not tell you — a line can be executed by a test that asserts nothing about it.

**"and nothing else"** is restraint. A test that cannot fail except by deliberate redesign is dead weight. Testing an enum's `ordinal()` when nothing persists ordinals, or asserting that a generated record accessor returns what the constructor stored, adds lines to maintain and protects nothing.

The practical form of the question is not "what does this class do?" but **"what would a careless edit break, and who would notice?"**

## 1. The four type shapes

### Enum

An enum holds two categorically different kinds of fact, and they want different tools. Confusing them is the most common way an enum test ends up green and useless.

**Per-constant facts** are a fixed table: this constant carries that payload. The table changes only when you deliberately change the alphabet. Carry the expectation *in the data*, not in the method body, so a new constant either gets a row or is visibly missing one.

**Whole-type invariants** are properties of the type rather than of any constant: every payload is printable, no two constants collide, the roster is the size you think. These are the ones that keep holding when someone adds a fourth constant a year from now — which is exactly when you are not looking.

The Clock Project enum, carrying a printed label and a behaviour:

```java
package com.example.clock;

/** Which half of the day a 12-hour clock reading falls in. */
public enum Meridiem {
  AM("a.m.", 0),
  PM("p.m.", 12);

  private final String label;
  private final int hourOffset;

  Meridiem(String label, int hourOffset) {
    this.label = label;
    this.hourOffset = hourOffset;
  }

  public String label() {
    return label;
  }

  /** Converts a 12-hour clock hour (1-12) into a 24-hour hour-of-day (0-23). */
  public int toHourOfDay(int clockHour) {
    if (clockHour < 1 || clockHour > 12) {
      throw new IllegalArgumentException("clock hour out of range: " + clockHour);
    }
    return (clockHour % 12) + hourOffset;
  }
}
```

Its test:

```java
package com.example.clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class MeridiemTest {

  @ParameterizedTest(name = "{0} prints as {1}")
  @CsvSource({"AM, a.m.", "PM, p.m."})
  void eachConstantCarriesItsPrintedLabel(Meridiem meridiem, String expected) {
    assertEquals(expected, meridiem.label());
  }

  @ParameterizedTest
  @EnumSource(Meridiem.class)
  void everyConstantHasAPrintableLabel(Meridiem meridiem) {
    assertFalse(meridiem.label().isBlank());
  }

  @Test
  void noTwoConstantsShareALabel() {
    long distinct = Arrays.stream(Meridiem.values()).map(Meridiem::label).distinct().count();

    assertEquals(Meridiem.values().length, distinct);
  }

  @ParameterizedTest(name = "{0} {1} o''clock is hour {2}")
  @CsvSource({
    "AM,  1,  1",
    "AM, 11, 11",
    "AM, 12,  0",
    "PM,  1, 13",
    "PM, 11, 23",
    "PM, 12, 12",
  })
  void twelveHourReadingsMapToHourOfDay(Meridiem meridiem, int clockHour, int expected) {
    assertEquals(expected, meridiem.toHourOfDay(clockHour));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 13, -1, 24})
  void hoursOutsideOneToTwelveAreRejected(int clockHour) {
    assertThrows(IllegalArgumentException.class, () -> Meridiem.AM.toHourOfDay(clockHour));
  }
}
```

Five methods, and each protects a different decision. Things to notice:

- **`@CsvSource` converts the first column to the enum automatically.** JUnit's implicit conversion calls `valueOf` for you, so a table row reads as `constant, expectation` with no lookup code.
- **The labels deliberately differ from the constant names.** If `AM.label()` returned `"AM"`, the table test would be asserting a string against itself and would survive almost any refactor. Make the expectation something the test could actually disagree with.
- **`@EnumSource` earns its place only when the parameter is bound and used.** A `@ParameterizedTest` whose body never mentions the injected parameter is a loop, not a parameterized test: it runs N times and proves one thing.
- **The distinctness test is a plain `@Test`,** because it is a fact about the set, not about any member. No per-constant source can express it.
- **`12` appears in the boundary table for both constants,** because `(12 % 12) + 0 == 0` is the one row where the arithmetic is not obvious, and it is the row a rewrite gets wrong.

**Do not test:** `ordinal()` unless something persists ordinals; that `values()` returns a fresh array; the private constructor; `name()` unless you parse it back. `valueOf`/`name` round-tripping earns a test the day you serialize, not before.

### Record

A record generates its accessors, `equals`, `hashCode`, and `toString`. Testing generated members individually is testing `javac`. **The only hand-written logic in a record is the compact constructor and any methods you added** — that is the whole test surface.

```java
package com.example.clock;

import java.time.LocalTime;
import java.util.Objects;

/** A single alarm: when it fires, and what it says. */
public record Alarm(LocalTime at, String label) {

  public Alarm {
    Objects.requireNonNull(at, "alarm time is required");
    Objects.requireNonNull(label, "alarm label is required");
    if (label.isBlank()) {
      throw new IllegalArgumentException("alarm label must not be blank");
    }
    label = label.trim();
  }

  public boolean firesBefore(Alarm other) {
    return at.isBefore(other.at);
  }
}
```

```java
class AlarmTest {

  @Test
  void componentsSurviveConstruction() {
    Alarm alarm = new Alarm(LocalTime.of(6, 30), "wake up");

    assertAll(
        () -> assertEquals(LocalTime.of(6, 30), alarm.at()),
        () -> assertEquals("wake up", alarm.label()));
  }

  @Test
  void surroundingWhitespaceIsStrippedFromTheLabel() {
    Alarm alarm = new Alarm(LocalTime.NOON, "  lunch  ");

    assertEquals("lunch", alarm.label());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t", "\n"})
  void blankLabelsAreRejected(String label) {
    assertThrows(IllegalArgumentException.class, () -> new Alarm(LocalTime.NOON, label));
  }

  @Test
  void nullComponentsAreRejected() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> new Alarm(null, "wake up")),
        () -> assertThrows(NullPointerException.class, () -> new Alarm(LocalTime.NOON, null)));
  }

  @Test
  void anAlarmFiresBeforeALaterOne() {
    Alarm early = new Alarm(LocalTime.of(6, 0), "gym");
    Alarm late = new Alarm(LocalTime.of(7, 0), "commute");

    assertTrue(early.firesBefore(late));
  }
}
```

Notice:

- **`componentsSurviveConstruction` looks like testing generated code, and nearly is** — but the compact constructor reassigns `label`, so it is proving that normalization did not eat the value. On a record with no compact constructor, skip it entirely.
- **Null and blank are separate tests because they are separate contracts.** `NullPointerException` for a missing component and `IllegalArgumentException` for a present-but-invalid one is a deliberate choice; whichever you pick, the test is what pins it. `Objects.requireNonNull` throwing NPE is the idiom the JDK itself uses.
- **`@ValueSource(strings = ...)` covers the blank family** — empty, space, tab, newline. Stack `@NullSource` or `@EmptySource` onto a `@ParameterizedTest` when you want them in the same table; `@NullAndEmptySource` is the composed shorthand.
- **No `equals`/`hashCode` test.** Write one only when you put the record in a `HashSet` or use it as a `Map` key — then the test documents that you depend on it, and it fails if someone converts the record to a class and loses the generated implementation.

### Class

A class with mutable state has four contract categories, and a complete test file has at least one method in each.

```java
package com.example.clock;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Accumulates lap times against an injected clock. */
public final class Stopwatch {
  private final TimeSource timeSource;
  private final List<Duration> laps = new ArrayList<>();
  private Instant markedAt;

  public Stopwatch(TimeSource timeSource) {
    this.timeSource = Objects.requireNonNull(timeSource, "timeSource is required");
  }

  public void start() {
    if (markedAt != null) {
      throw new IllegalStateException("stopwatch is already running");
    }
    markedAt = timeSource.now();
  }

  public Duration lap() {
    if (markedAt == null) {
      throw new IllegalStateException("stopwatch is not running");
    }
    Instant now = timeSource.now();
    Duration sinceLastMark = Duration.between(markedAt, now);
    laps.add(sinceLastMark);
    markedAt = now;
    return sinceLastMark;
  }

  public List<Duration> laps() {
    return List.copyOf(laps);
  }
}
```

```java
class StopwatchTest {

  private FixedTimeSource clock;
  private Stopwatch stopwatch;

  @BeforeEach
  void setUp() {
    clock = new FixedTimeSource(Instant.parse("2026-01-01T00:00:00Z"));
    stopwatch = new Stopwatch(clock);
  }

  // 1. Construction and representation
  @Test
  void aFreshStopwatchHasRecordedNoLaps() {
    assertTrue(stopwatch.laps().isEmpty());
  }

  @Test
  void aNullClockIsRejected() {
    assertThrows(NullPointerException.class, () -> new Stopwatch(null));
  }

  // 2. State transitions
  @Test
  void theFirstLapMeasuresFromTheStart() {
    stopwatch.start();
    clock.advance(Duration.ofSeconds(30));

    Duration first = stopwatch.lap();

    assertEquals(Duration.ofSeconds(30), first);
  }

  @Test
  void eachLapMeasuresFromThePreviousLapNotFromTheStart() {
    stopwatch.start();
    clock.advance(Duration.ofSeconds(30));
    stopwatch.lap();
    clock.advance(Duration.ofSeconds(10));

    Duration second = stopwatch.lap();

    assertEquals(Duration.ofSeconds(10), second);
  }

  // 3. Boundaries and illegal transitions
  @Test
  void startingAnAlreadyRunningStopwatchIsRejected() {
    stopwatch.start();

    assertThrows(IllegalStateException.class, stopwatch::start);
  }

  @Test
  void lappingBeforeStartingIsRejected() {
    assertThrows(IllegalStateException.class, stopwatch::lap);
  }

  // 4. Encapsulation
  @Test
  void theReturnedLapListCannotBeUsedToMutateTheStopwatch() {
    stopwatch.start();
    clock.advance(Duration.ofSeconds(5));
    stopwatch.lap();

    List<Duration> exposed = stopwatch.laps();

    assertThrows(UnsupportedOperationException.class, () -> exposed.add(Duration.ZERO));
    assertEquals(1, stopwatch.laps().size());
  }
}
```

The four categories, stated plainly so they travel to any class:

1. **Construction and representation.** Does the object hold what you gave it, with the shape you expect? This is where a constructor assigning the wrong source to the wrong field dies — and where you discover that a field with no accessor is *unobservable*, which is design feedback, not a testing problem.
2. **State transitions.** Does a sequence of calls accumulate correctly? Single-call tests miss off-by-one accumulation; `eachLapMeasuresFromThePreviousLap` exists only because a one-call test would pass against a wrong implementation that measures from the start every time.
3. **Boundaries from the contract.** Zero, negative, empty, null, out-of-order calls. `assertThrows` is the tool, and **choosing which exception type is the design decision the test forces you to make.** A JVM-thrown `NegativeArraySizeException` or `ArrayIndexOutOfBoundsException` leaking out of your constructor is an accident, not a contract.
4. **Encapsulation.** If a getter hands out a collection or array, a caller can reach through it and mutate your object. Write the test that mutates what came back — whatever it asserts *is* your contract, and writing it is what makes you choose between `List.copyOf`, `Arrays.copyOf`, and a documented "this is live, handle with care".

`FixedTimeSource` is shared test infrastructure and lives in its own file, asserting nothing itself:

```java
/** Test double. Never asserts. */
final class FixedTimeSource implements TimeSource {
  private Instant now;

  FixedTimeSource(Instant start) {
    this.now = start;
  }

  @Override
  public Instant now() {
    return now;
  }

  void advance(Duration amount) {
    now = now.plus(amount);
  }
}
```

### Interface

**You never test an interface directly.** You test two things: the default methods it ships, and the contract every implementation must honour.

```java
package com.example.clock;

import java.time.Instant;

/** The one place the program is allowed to ask what time it is. */
@FunctionalInterface
public interface TimeSource {
  Instant now();

  default boolean isBefore(Instant moment) {
    return now().isBefore(moment);
  }
}
```

Default methods are real code and want a real test. A functional interface lets a lambda stand in as the implementation, so the test needs no fixture class at all:

```java
class TimeSourceDefaultsTest {

  @Test
  void isBeforeComparesAgainstTheCurrentReading() {
    TimeSource frozen = () -> Instant.parse("2026-01-01T00:00:00Z");

    assertAll(
        () -> assertTrue(frozen.isBefore(Instant.parse("2026-01-02T00:00:00Z"))),
        () -> assertFalse(frozen.isBefore(Instant.parse("2025-12-31T00:00:00Z"))));
  }
}
```

The contract test is the more valuable half. Write the rules once, then make every implementation prove it obeys them — including the ones added after you stop thinking about this:

```java
/** Every TimeSource must satisfy this, whatever it is built on. */
abstract class TimeSourceContract {

  abstract TimeSource subject();

  @Test
  void nowIsNeverNull() {
    assertNotNull(subject().now());
  }

  @Test
  void nowNeverGoesBackwards() {
    TimeSource source = subject();

    Instant first = source.now();
    Instant second = source.now();

    assertFalse(second.isBefore(first));
  }
}

class SystemTimeSourceTest extends TimeSourceContract {
  @Override
  TimeSource subject() {
    return new SystemTimeSource();
  }
}

class FixedTimeSourceTest extends TimeSourceContract {
  @Override
  TimeSource subject() {
    return new FixedTimeSource(Instant.parse("2026-01-01T00:00:00Z"));
  }
}
```

Adding a third implementation is now a two-line test file, and it either honours the contract or goes red on arrival. That is the payoff: **the contract test is the only test shape that protects code that does not exist yet.** `@Nested` with a `@ParameterizedTest` over a `@MethodSource` of implementations is the alternative wiring if you prefer composition to inheritance.

## 2. Change-detection tables

The fastest way to check a test file for completeness: list the edits a tired version of you could make next month, and name the test that goes red.

**Enum**

| Accidental edit                                    | Test that must fail                                    |
|----------------------------------------------------|--------------------------------------------------------|
| a payload typo (`"a.m."` becomes `"am"`)           | per-constant `@CsvSource` table                        |
| a new constant added with a duplicate payload      | distinctness `@Test` over `values()`                   |
| a new constant added with a null or blank payload  | `@EnumSource` invariant                                |
| a constant deleted                                 | its table row fails to resolve at compile time         |
| a behaviour method's boundary arithmetic rewritten | boundary `@CsvSource` table, including the awkward row |
| the guard on an argument removed                   | `assertThrows` with `@ValueSource`                     |

**Record**

| Accidental edit                            | Test that must fail                                |
|--------------------------------------------|----------------------------------------------------|
| compact-constructor null check deleted     | null-component test                                |
| blank/range validation deleted             | `@ValueSource` rejection test                      |
| normalization (trim, round, clamp) removed | normalization test                                 |
| a component added or reordered             | every `new` in the suite fails to compile          |
| record converted to a class, `equals` lost | equality test — **only if** you actually key on it |

**Class**

| Accidental edit                                   | Test that must fail               |
|---------------------------------------------------|-----------------------------------|
| constructor stops storing a field                 | construction/representation test  |
| a defensive copy replaced with the live reference | encapsulation/aliasing test       |
| a state guard removed                             | illegal-transition `assertThrows` |
| accumulation measured from the wrong anchor       | multi-step transition test        |
| a boundary check loosened                         | boundary test                     |

**Interface**

| Accidental edit                                              | Test that must fail                         |
|--------------------------------------------------------------|---------------------------------------------|
| a new implementation breaks an unwritten rule                | contract test it inherits                   |
| a default method's logic changes                             | default-method test (lambda implementation) |
| an implementation returns null where the contract forbids it | contract test                               |

If a row has no test, that is the next thing to write. If a test maps to no row, ask what it is for.

## 3. Public field versus accessor

A `public final String` on an enum is **safe but inflexible**, and the two halves are worth separating because they get conflated.

**Why it is safe here.** The field is `final` and its type is immutable, so nothing mutable escapes and no caller can corrupt the constant. That condition is doing all the work. `final` protects the *reference*, never the contents — `public final List<String> tags` or `public final int[] counts` is an outright leak, because a caller can call `.add` or assign an element without ever reassigning the field. The rule: a public field is only ever defensible when it is `final` **and** its type is deeply immutable.

**Why it is still the wrong default.** Five costs, roughly in order of how soon they bite:

1. **A field is not a seam; a method is.** The moment the value depends on anything — a theme, a terminal's capabilities, a locale, a lookup — a field cannot compute and a method can. Converting a public field to a method later is a breaking change at every call site. A method was never a cost.
2. **A field cannot satisfy an interface.** Interfaces declare methods. `interface Printable { String symbol(); }` can be implemented by an accessor and can never be implemented by a field, so a public field quietly closes the door on abstracting over the type.
3. **A field cannot be overridden per constant.** Enum constants may carry constant-specific class bodies that override a method; they cannot override a field. A public field forecloses the one polymorphism enums actually offer.
4. **No validation, normalization, or lazy computation** can ever be inserted without changing the API.
5. **The name describes storage, not concept.** `value` says "a thing is stored here". `label()`, `symbol()`, `glyph()`, `code()` say what it *is*. Naming for the concept is what makes code readable to a person; the computer is indifferent either way.

**The efficiency argument is a myth, and worth killing explicitly.** A trivial accessor costs nothing at runtime — HotSpot inlines it during JIT compilation and the emitted machine code is identical to a direct field read. Choosing a public field for speed optimizes something that was never slow. If you ever want to see this rather than take it on faith, `-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining` shows the inlining decisions, and JMH is the tool for measuring rather than guessing. **Reach for a measurement before a micro-optimization, every time.**

**Effective Java, Item 16** states the mainstream rule directly: in public classes, use accessor methods, not public fields. Item 17 adds that immutable fields make exposure *less* harmful, which is exactly the position a `public final String` occupies — less harmful, still not recommended.

**Decision rule worth internalizing:**

| Situation                                           | Use                                                            |
|-----------------------------------------------------|----------------------------------------------------------------|
| immutable data with no behaviour, purely structural | a `record` — accessors generated, debate settled               |
| enum carrying data                                  | a private field plus an accessor named for the concept         |
| anything mutable, or any collection or array        | private field, accessor returns a copy or an unmodifiable view |
| a genuinely public compile-time constant            | `public static final`, `SCREAMING_SNAKE_CASE`                  |

On naming style: modern Java favours the record-accessor form — `label()`, not `getLabel()`. The `get` prefix is JavaBeans convention, required only by frameworks that reflect over it. Records made the bare noun idiomatic, and it reads better in a chain.

## 4. Balancing simplicity against purpose in an enum

A three-constant enum carrying one payload is the right *size*. The question worth asking is not "is it too simple?" but **"does the payload belong to this layer?"**

An enum's payload should be something the type *is*, not something a consumer wants to *display*. A trichotomy of states is intrinsic. A rendered glyph is presentation — and presentation belongs wherever the architecture puts rendering, which in a layered design is not the model.

The tell that this matters is a future requirement the payload cannot absorb: two themes, a terminal that cannot print a symbol, a locale-specific label. The moment a second presentation exists, a single stored string is the wrong shape and the mapping moves out to a renderer — typically an `EnumMap` or a `switch` in the presentation layer, which also gives the compiler exhaustiveness checking over the constants.

Put the decision in writing when you make it, with a date. "The glyph lives in the model for now, and moves to the renderer when a second theme exists" is a fine, deliberate answer. Discovering it under pressure three chapters later is not.

**Generally: keep the enum as small as the domain allows, and put the payload at the layer that owns the concern.** Every field on an enum is a claim that all constants have that property forever.

## 5. Reading a test file against this rubric

The failure modes seen this session, stated as general symptoms rather than as fixes:

- **A parameterized test whose body ignores its parameter.** Runs N times, proves one thing. JUnit tolerates the arity mismatch silently — if the method declares no parameter at all, the surplus argument is discarded and the suite reports N green tests. Green test count is not evidence of N distinct facts.
- **A method name that promises more than the body delivers.** `...NonBlank` paired with `isEmpty()` is a real mismatch: a single-space payload passes `isEmpty()` and violates the name. `isBlank()` covers whitespace; `isEmpty()` covers only zero length. The name is the specification — when they disagree, one of them is a bug, and it is usually not the name.
- **Sequential bare assertions in one method.** The first failure masks the rest, so a run teaches you about one broken value at a time. `assertAll` reports every facet of the same behaviour in one pass. `../../../TESTING_STANDARDS.md` names this directly: "use `assertAll` only for several facets of that same behavior."
- **Expectations hardcoded in the body rather than carried in data.** The file grows a line per constant forever, and a newly added constant is silently untested because nobody remembers to extend the body.
- **The `test` method-name prefix.** A JUnit 3 holdover; JUnit 5 finds tests by annotation. `TESTING_STANDARDS.md` asks for observable outcomes — `deleteSucceedsOnceAndThenReportsMissing`, not `testDelete`.
- **`public class SomethingTest`.** JUnit 5 discovers package-private classes and methods. Public adds nothing and breaks consistency with the rest of a suite.
- **Arrange steps that do no work.** Assigning constants to local variables before asserting on them is noise; setup should be smaller than the behaviour under test.

## 6. JUnit 5 tools worth having in reach

| Tool                                                 | Reach for it when                                                             |
|------------------------------------------------------|-------------------------------------------------------------------------------|
| `@Test`                                              | one behaviour, no table                                                       |
| `@ParameterizedTest` + `@CsvSource`                  | a fixed table where each row is `input(s), expectation`                       |
| `@ParameterizedTest` + `@MethodSource`               | rows need real objects, not string literals                                   |
| `@ParameterizedTest` + `@EnumSource`                 | a property that must hold for every constant, present and future              |
| `@ValueSource`                                       | one varying primitive or string, same assertion                               |
| `@NullSource`, `@EmptySource`, `@NullAndEmptySource` | the null/blank family, stackable onto a `@ParameterizedTest`                  |
| `@ParameterizedTest(name = "...")`                   | the failure message should name the row, not say "[2]"                        |
| `assertAll`                                          | several facets of one behaviour, and you want all failures in one run         |
| `assertThrows`                                       | a guard is the behaviour; returns the exception so you can assert its message |
| `@Nested` + `@DisplayName`                           | a class with several distinct contracts; group by contract, not by method     |
| `@BeforeEach`                                        | fresh mutable fixture per test — never `@BeforeAll` for mutable state         |
| `@TempDir`                                           | a file write, without leaving a file behind                                   |
| `assertInstanceOf`                                   | the type is the contract                                                      |
| `assertIterableEquals`                               | ordered collections, with a readable element-wise diff                        |

**Not worth reaching for:** `@RepeatedTest` on deterministic code; a mocking framework for value types; `@Disabled` as anything other than a temporary marker with a dated reason; `@BeforeAll` holding mutable state shared across tests.

## 7. Session findings — 2.3 Part 1

Recorded so the next sitting does not re-derive them. These are about this repository, not the Clock Project.

**Coverage audit (before the `CellTest` work).** `mvn -B verify` failed the JaCoCo `0.8` line rule on two classes. The engine read 100% and the terminal tool read 0%, which was traced to a test file named for one class that constructed and asserted against a different one entirely — borrowed coverage. A class can sit at 100% because something *else's* test happened to execute it.

**The general lesson:** a coverage failure caused by *unused production code* is telling you to delete the code, not to write tests for it. `TESTING_STANDARDS.md` already says "delete a test when it exists only to move a coverage number"; the mirror image is that writing a test for an uncalled method to satisfy a threshold is the same mistake wearing a different hat. Dead state — fields written by every constructor and read by nothing, with no accessor to observe them — is the clearest case.

**Unobservable state is design feedback.** A private field with no accessor cannot be asserted on, so a bug in how it is computed is untestable by construction. The test you cannot write tells you the field is either contract (give it an accessor) or dead (delete it).

**A boundary that cannot be pointed somewhere else cannot be tested.** A method that chooses its own output destination from the environment cannot be aimed at `@TempDir`; there is no seam. Swallowing the exception compounds it — "write failed" and "write succeeded" become the same observable outcome, and no test can distinguish them. `Files.writeString(path, text, StandardCharsets.UTF_8)` takes an explicit charset and closes for you; `FileWriter(String)` does neither, and a platform-default charset makes a test dependent on the machine it runs on.

**Lesson 2.3 accuracy corrections found by reading the code against the checklist:** one work item claimed a fixture was built in the computation layer when it lives in the I/O layer; one claimed a write went to a `Path` when it goes to a string filename; and two unchecked "must be true" boxes were already satisfied and provable — one by the returned-string design, one by Checkstyle's import rules passing. **Checking a lesson against the checkout, rather than against memory, catches over-claims and under-claims in roughly equal numbers.**

**A build gate can hide behind another build gate.** A formatter check running in `validate` fails the build before tests compile, so an empty stub file can mask the state of the entire suite. When a gate fails early, skipping it deliberately to see what is behind it is a legitimate diagnostic move — `-Dspotless.check.skip=true` and `-Dcheckstyle.skip=true` — as long as the real run happens afterwards.
