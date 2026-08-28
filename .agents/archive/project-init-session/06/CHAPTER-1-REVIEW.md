# Chapter 1 Review — What You Already Own

Chapter 1 built a complete plain-Java CRUD console application: the Job Application Tracker. This
course does not re-teach any of it. This document exists so you can prove that to yourself before
Chapter 2 asks you to rebuild the toolchain from memory.

## How to use this

**Answer first, then check.** Every section below is recall prompts, not explanation. Write your
answer down — actually write it, not "think it through" — and only then open the answer key. The
answer key is the tracker repository itself:

```
../job-application-tracker/lessons/ch01-java-foundations/
```

If that repository is not on this machine, the three reference documents copied into this project
(`GLOSSARY.md`, `COURSE_STANDARDS.md`, `TESTING_STANDARDS.md`) cover most of it.

A prompt you answer instantly is done. A prompt you answer haltingly is a Chapter 2 diff worth
paying attention to. A prompt you cannot answer at all is worth one lesson re-read before you start.

---

## Part 1 — Recall drills

### Build and tooling *(answer key: Lesson 0, Lesson 8)*

1. What are the three Maven coordinates, and what does each one identify?
2. What does `test` scope mean, concretely — what breaks if `junit-jupiter` is left at default scope?
3. Name the lifecycle phases in order from `validate` through `verify`. Which one runs Checkstyle in
   your setup, and why that one rather than a later one?
4. What is the difference between `mvn compile`, `mvn test`, and `mvn verify`?
5. Why is `maven-surefire-plugin` pinned to an explicit version? What is the *symptom* when it is
   not — and why is that symptom worse than a build failure?
6. What is in `target/`, and why is it not committed?
7. Checkstyle and Spotless both complain about formatting. What is the actual difference between
   what they do?
8. What does JaCoCo measure? State one thing green coverage does *not* prove.

### Language and data *(answer key: Lessons 1–2)*

9. When do you reach for a `record` and when for a mutable class? Give the deciding question, not a
   list of features.
10. What does a `record` generate for you that a class does not?
11. What is the difference between `int` and `Integer`, and when does that difference bite?
12. Why does returning `Optional<T>` beat returning `null`? Why is `Optional` a poor choice for a
    field type?
13. What does an enum give you that a set of string constants does not?
14. Why did `Status` never need a stable ordinal, and what would have had to change if it were
    written to a file?

### Boundaries and control flow *(answer key: Lessons 3–4)*

15. State the rule about where you catch an exception. Say it in one sentence.
16. What is a guard clause, and what does it buy you in a method with six cases?
17. Why is EOF a *normal* exit path rather than an error?
18. What is a composition root? Which class was yours, and what was it not allowed to do?
19. Why inject a collaborator through the constructor rather than construct it inside the class?
    Name the specific thing that becomes possible.
20. `ApplicationService` depends on `ApplicationRepository`, an interface. What would you have had
    to change to swap the in-memory store for a database one?

### Console I/O and rendering *(answer key: Lessons 5–6)*

21. Why does `TextTable.render(...)` return a `String` instead of printing?
22. Where does a retry loop live — around the whole menu, or around one field? Why?
23. Why does explicit `StandardCharsets.UTF_8` matter when a test reads a subprocess's output?
24. Your CLI was split into four classes. Name each one's single reason to change.
25. What is a request record, and what problem did `CreateApplicationRequest` solve that a
    six-parameter method did not?

### Testing *(answer key: Lesson 7, `TESTING_STANDARDS.md`)*

26. Name the three test scopes from the Chapter 1 standards, and one thing each proves that the
    others cannot.
27. Why does a test inject a `Scanner` and a `PrintStream` rather than replacing `System.in` and
    `System.out`?
28. If you must replace a global stream, what must you do, and where?
29. `MainProcessTest` launches a real JVM. Name three things it proves that no in-process test can.
30. Why must tests never depend on execution order? What does a shared writable static field do to
    that guarantee?
31. When do you assert exact output, and when do you assert ordered fragments?

### Quality and conventions *(answer key: Lesson 8, `COURSE_STANDARDS.md`)*

32. What is the naming convention for packages, types, methods, constants?
33. Why no wildcard imports?
34. What is the allowed dependency direction between `model`, `repository`, `service`, and `cli`?
35. Why must `model` never depend on `cli`? Give the concrete cost of breaking it.

---

## Part 2 — The rebuild checklist

Chapter 2 asks you to reconstruct these from memory, then diff against the tracker. Attempt each one
on a blank file before looking. The diff is the lesson.

- [ ] `pom.xml` — coordinates, `maven.compiler.release` 21, `junit-jupiter` at test scope, pinned
      Surefire, compiler plugin.
- [ ] Checkstyle plugin bound to `validate`, pointing at `config/checkstyle/checkstyle.xml`.
- [ ] Spotless plugin with `google-java-format`.
- [ ] JaCoCo plugin with report generation.
- [ ] `.gitignore` covering `target/` and local tool state.
- [ ] `.githooks/prepare-commit-msg` and the `C###` commit grammar.
- [ ] `.editorconfig`.
- [ ] The `src/main/java` and `src/test/java` trees under `com/connorjensen/nonogram`.

Copy verbatim, do not rebuild — these are frozen reference, not exercises:

- [ ] `GLOSSARY.md`
- [ ] `COURSE_STANDARDS.md` (retitled for this project; the dependency-direction section changes)
- [ ] `TESTING_STANDARDS.md`
- [ ] `config/checkstyle/checkstyle.xml` (rebuilding a Checkstyle ruleset teaches nothing)

---

## Part 3 — What Chapter 1 did not teach you

Being honest about the gaps is more useful than an inventory of strengths. None of the following
appears anywhere in the tracker, so when you hit them, you are on new ground and should slow down.

**Two-dimensional arrays.** The tracker used `List` and streams exclusively. It never indexed a
`[][]`, never dealt with array covariance, and never had a transposition bug. Chapter 3.2.

**Algorithms with a correctness argument.** Every method in the tracker was obviously correct on
inspection. Nothing enumerated a search space, iterated to a fixed point, or needed a reference
implementation to be believed. Chapters 5, 6, 14.

**Serialization.** Nothing in the tracker outlived the process. Data that has to survive a restart
introduces format stability as a constraint — which is why `CellState`'s ordinals get a test in
Chapter 3 and a file format in Chapter 12. Chapters 9, 12.

**Anything outside the JVM.** No HTTP, no DOM, no browser, no storage, no deployment. Chapters
10–13 are a different runtime with a different testing story, and the test ladder has to be rebuilt
in a language and toolchain that has no JUnit.

**Testing a whole codebase by your own hand.** The tracker has thirteen production classes and four
learner-written test files. There is no `TextTableTest`, no `ApplicationServiceTest`, no
`ConsolePrompterTest`, and no `InMemoryApplicationRepositoryTest`. Chapter 1 demonstrated each of
the three tiers exactly once and then handed you a 39-test grader; you learned to satisfy a suite,
not to design one. **This is the largest gap, and it is the reason this course is shaped the way it
is.**

---

## Part 4 — Answer key index

In the tracker repository, under `lessons/ch01-java-foundations/`:

| Topic | File |
|---|---|
| Build system, POM, classpath, bytecode | `00-build-system.md` |
| Classes, objects, records, equality | `01-classes-and-records.md` |
| Packages, enums, collections, `Optional`, streams | `02-types-enums-collections.md` |
| Interfaces, constructor injection, composition roots | `03-interfaces-and-di.md` |
| Loops, dispatch, guards, retries, EOF | `04-control-flow-and-exception-boundaries.md` |
| `Scanner`, `PrintStream`, UTF-8, validation | `05-console-io-and-validation.md` |
| Cohesion, visibility, request records | `06-refactoring-and-package-design.md` |
| Injected streams, subprocesses, exact output, coverage | `07-testing-console-applications.md` |
| Maven layout, names, imports, Checkstyle, warnings | `08-java-quality-standards.md` |
| Fast recall for all of the above | `GLOSSARY.md` |
| Conventions | `COURSE_STANDARDS.md` |
| Test design | `TESTING_STANDARDS.md` |
| Why the chapter grew the way it did | `RECALIBRATION.md` |

Working reference implementations worth reading *after* you have written your own version:

| What | Where |
|---|---|
| Pure renderer returning a `String` | `src/main/java/com/connorjensen/jobtracker/cli/TextTable.java` |
| Tier 1 file test | `src/test/.../ApplicationTest.java` |
| Tier 2 flow test | `src/test/.../cli/ConsoleSessionTest.java` |
| Tier 3 subprocess test | `src/test/.../MainProcessTest.java` |
| A published behavioural contract | `lessons/ch01-java-foundations/CAPSTONE.md` |
