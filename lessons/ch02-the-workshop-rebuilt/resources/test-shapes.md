# A test file per type shape

Learner reference. Not authority — `../../TESTING_STANDARDS.md` owns the rubric this works from, and `.agents/PROJECT.md` owns the architecture it assumes. Where this file and one of those disagree, this one is out of date.

Reach for this when you are writing a test file and want to know what a finished one looks like for the kind of type in front of you. Its companion, [`changing-code.md`](changing-code.md), is for when the code already exists and you are moving it.

Every example belongs to an invented **Clock Project** and shares no code with this repository. Read them for shape. Copying one into `../../../src` would be copying the wrong domain.

## The rule everything else follows from

> A test file is finished when every decision a future edit could plausibly change by accident has exactly one test that fails — and nothing else.

Both halves carry weight.

**"has one test that fails"** is completeness. Walk the production file and ask of each line: if someone deleted this, changed this number, or dropped this guard, would a test go red? If no, that decision is unprotected, and coverage percentage will not tell you — a line can be executed by a test that asserts nothing about it.

**"and nothing else"** is restraint. A test that cannot fail except by deliberate redesign is dead weight. Testing an enum's `ordinal()` when nothing persists ordinals, or asserting that a generated record accessor returns what the constructor stored, adds lines to maintain and protects nothing.

The practical form of the question is not "what does this class do?" but **"what would a careless edit break, and who would notice?"**

## 1. Enum

An enum holds two categorically different kinds of fact, and they want different tools. Confusing them is the most common way an enum test ends up green and useless.

**Per-constant facts** are a fixed table: this constant carries that payload. Carry the expectation *in the data*, not in the method body, so a new constant either gets a row or is visibly missing one.

**Whole-type invariants** are properties of the type rather than of any constant: every payload is printable, no two constants collide, the roster is the size you think. These keep holding when someone adds a fourth constant a year from now — which is exactly when you are not looking.

```java
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

The per-constant table is a `@CsvSource`; the invariants are an `@EnumSource` and a distinctness `@Test` over `values()`; the behaviour method wants a boundary table including the awkward row — for `Meridiem` that is `12`, where the `% 12` wraps.

## 2. Record

A record generates its accessors, `equals`, `hashCode`, and `toString`. Testing generated members individually is testing `javac`. **The only hand-written logic in a record is the compact constructor and any methods you added** — that is the whole test surface.

```java
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

Notice:

- **A "components survive construction" test looks like testing generated code, and nearly is** — but this compact constructor reassigns `label`, so it proves normalization did not eat the value. On a record with no compact constructor, skip it entirely.
- **Null and blank are separate tests because they are separate contracts.** `NullPointerException` for a missing component and `IllegalArgumentException` for a present-but-invalid one is a deliberate choice; whichever you pick, the test is what pins it. `Objects.requireNonNull` throwing NPE is the idiom the JDK itself uses.
- **`@ValueSource(strings = {"", " ", "\t", "\n"})` covers the blank family.** Stack `@NullSource` or `@EmptySource` onto a `@ParameterizedTest` when you want them in the same table; `@NullAndEmptySource` is the composed shorthand.
- **No `equals`/`hashCode` test.** Write one only when you put the record in a `HashSet` or use it as a `Map` key — then the test documents that you depend on it, and it fails if someone converts the record to a class and loses the generated implementation.

## 3. Class

A class with mutable state has four contract categories, and a complete test file has at least one method in each.

```java
/** Accumulates lap times against an injected clock. */
public final class Stopwatch {
  private final TimeSource timeSource;
  private final List<Duration> laps = new ArrayList<>();
  private Instant markedAt;

  public Stopwatch(TimeSource timeSource) {
    this.timeSource = Objects.requireNonNull(timeSource, "timeSource is required");
  }

  public void start() { /* rejects a second start */ }

  public Duration lap() { /* rejects a lap before start; measures from the last mark */ }

  public List<Duration> laps() {
    return List.copyOf(laps);
  }
}
```

1. **Construction and representation.** Does the object hold what you gave it, with the shape you expect? This is where a constructor assigning the wrong source to the wrong field dies — and where you discover that a field with no accessor is *unobservable*, which is design feedback, not a testing problem.
2. **State transitions.** Does a sequence of calls accumulate correctly? Single-call tests miss off-by-one accumulation. A test named for the second lap measuring from the first — not from the start — exists only because a one-call test passes against a wrong implementation that measures from the start every time.
3. **Boundaries from the contract.** Zero, negative, empty, null, out-of-order calls. `assertThrows` is the tool, and **choosing which exception type is the design decision the test forces you to make.** A JVM-thrown `NegativeArraySizeException` or `ArrayIndexOutOfBoundsException` leaking out of your constructor is an accident, not a contract.
4. **Encapsulation.** If a getter hands out a collection or array, a caller can reach through it and mutate your object. Write the test that mutates what came back — whatever it asserts *is* your contract, and writing it is what makes you choose between `List.copyOf`, `Arrays.copyOf`, and a documented "this is live, handle with care". Note that `List.copyOf` wraps the **outer** list only, so a test that mutates an inner row proves something weaker than one that mutates the outer.

The injected `TimeSource` is what makes any of this assertable. Its fixed implementation is shared test infrastructure, lives in its own file, and asserts nothing itself:

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

## 4. Interface

**You never test an interface directly.** You test two things: the default methods it ships, and the contract every implementation must honour.

Default methods are real code and want a real test. A functional interface lets a lambda stand in as the implementation, so the test needs no fixture class at all — `TimeSource frozen = () -> Instant.parse("2026-01-01T00:00:00Z");` is the entire arrange step.

The contract test is the more valuable half. Write the rules once as an abstract class with one abstract `subject()` method, then make every implementation prove it obeys them by extending it:

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
```

Adding a third implementation is now a two-line test file, and it either honours the contract or goes red on arrival. That is the payoff: **the contract test is the only test shape that protects code that does not exist yet.** `@Nested` with a `@ParameterizedTest` over a `@MethodSource` of implementations is the alternative wiring if you prefer composition to inheritance.

## 5. Change-detection tables

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

## 6. Public field versus accessor

A `public final String` on an enum is **safe but inflexible**, and the two halves get conflated.

**Why it is safe.** The field is `final` and its type is immutable, so nothing mutable escapes. That condition is doing all the work. `final` protects the *reference*, never the contents — `public final List<String> tags` or `public final int[] counts` is an outright leak, because a caller can call `.add` or assign an element without ever reassigning the field. The rule: a public field is only ever defensible when it is `final` **and** its type is deeply immutable.

**Why it is still the wrong default.** Five costs, roughly in order of how soon they bite:

1. **A field is not a seam; a method is.** The moment the value depends on anything — a theme, a terminal's capabilities, a locale, a lookup — a field cannot compute and a method can. Converting a public field to a method later is a breaking change at every call site. A method was never a cost.
2. **A field cannot satisfy an interface.** `interface Printable { String symbol(); }` can be implemented by an accessor and never by a field, so a public field quietly closes the door on abstracting over the type.
3. **A field cannot be overridden per constant.** Enum constants may carry constant-specific class bodies that override a method; they cannot override a field. A public field forecloses the one polymorphism enums actually offer.
4. **No validation, normalization, or lazy computation** can ever be inserted without changing the API.
5. **The name describes storage, not concept.** `value` says "a thing is stored here". `label()`, `symbol()`, `glyph()`, `code()` say what it *is*.

**The efficiency argument is a myth.** A trivial accessor costs nothing at runtime — HotSpot inlines it during JIT compilation and the emitted machine code is identical to a direct field read. `-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining` shows the inlining decisions, and JMH is the tool for measuring rather than guessing. **Reach for a measurement before a micro-optimization, every time.**

*Effective Java*, Item 16 states the mainstream rule directly: in public classes, use accessor methods, not public fields. Item 17 adds that immutable fields make exposure *less* harmful, which is exactly the position a `public final String` occupies — less harmful, still not recommended.

| Situation                                           | Use                                                            |
|-----------------------------------------------------|----------------------------------------------------------------|
| immutable data with no behaviour, purely structural | a `record` — accessors generated, debate settled               |
| enum carrying data                                  | a private field plus an accessor named for the concept         |
| anything mutable, or any collection or array        | private field, accessor returns a copy or an unmodifiable view |
| a genuinely public compile-time constant            | `public static final`, `SCREAMING_SNAKE_CASE`                  |

On naming style: modern Java favours the record-accessor form — `label()`, not `getLabel()`. The `get` prefix is JavaBeans convention, required only by frameworks that reflect over it.

## 7. Does the payload belong to this layer?

A three-constant enum carrying one payload is the right *size*. The question worth asking is not "is it too simple?" but **"does the payload belong to this layer?"**

An enum's payload should be something the type *is*, not something a consumer wants to *display*. A trichotomy of states is intrinsic. A rendered glyph is presentation — and presentation belongs wherever the architecture puts rendering, which in a layered design is not the model.

The tell that this matters is a future requirement the payload cannot absorb: two themes, a terminal that cannot print a symbol, a locale-specific label. The moment a second presentation exists, a single stored string is the wrong shape and the mapping moves out to a renderer — typically an `EnumMap` or a `switch` in the presentation layer, which also gives the compiler exhaustiveness checking over the constants.

Put the decision in writing when you make it, with a date. "The glyph lives in the model for now, and moves to the renderer when a second theme exists" is a fine, deliberate answer. Discovering it under pressure three chapters later is not.

## 8. Symptoms of a test file that is not finished

- **A parameterized test whose body ignores its parameter.** Runs N times, proves one thing. JUnit tolerates the arity mismatch silently — if the method declares no parameter at all, the surplus argument is discarded and the suite reports N green tests. Green test count is not evidence of N distinct facts.
- **A method name that promises more than the body delivers.** `...NonBlank` paired with `isEmpty()` is a real mismatch: a single-space payload passes `isEmpty()` and violates the name. The name is the specification — when they disagree, one of them is a bug, and it is usually not the name.
- **Sequential bare assertions in one method.** The first failure masks the rest. `assertAll` reports every facet of the same behaviour in one pass, and `../../TESTING_STANDARDS.md` limits it to exactly that: "several facets of that same behavior".
- **Expectations hardcoded in the body rather than carried in data.** The file grows a line per constant forever, and a newly added constant is silently untested.
- **The `test` method-name prefix.** A JUnit 3 holdover; JUnit 5 finds tests by annotation. Name the observable outcome instead.
- **`public class SomethingTest`.** JUnit 5 discovers package-private classes and methods. Public adds nothing and breaks consistency with the rest of a suite.
- **A shared writable static fixture** — a `private static` field reassigned in `@BeforeEach`. `../../TESTING_STANDARDS.md` forbids it by name. Make it an instance field.
- **Arrange steps that do no work.** Assigning constants to local variables before asserting on them is noise; setup should be smaller than the behaviour under test.
- **A test file named for one class that constructs another.** Coverage gets borrowed: the class in the filename reads 0% while the one it actually exercises reads 100%. The filename and the `new` must agree.

## 9. JUnit 5 tools worth having in reach

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
