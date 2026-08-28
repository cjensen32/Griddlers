# Chapter 1 Test-Writing Standards

This learner-facing reference collects the testing conventions used by the console tracker and its capstone.

## Organization

- Mirror production packages when package-private access is genuinely part of the test boundary.
- Group a large behavior contract with `@Nested` classes and plain-language `@DisplayName` labels.
- Name test methods as observable outcomes, such as `deleteSucceedsOnceAndThenReportsMissing`.
- Keep one primary behavior per test; use `assertAll` only for several facets of that same behavior.

## Test body shape

- Separate arrange, act, and assert with blank lines rather than comments that repeat the code.
- Make the action visually obvious and keep setup smaller than the behavior under test.
- Assert returned values, persisted state, and stable output rather than private helper calls.
- Prefer exact equality for small stable contracts and ordered fragments for long interactive output.

## Fixtures and isolation

- Build fresh mutable state per test and never make test order significant.
- Let helpers remove mechanical setup without hiding scenario values.
- Use `StandardCharsets.UTF_8` for byte/string conversion.
- Inject streams, clocks, repositories, and other boundaries when the design provides a seam.
- Avoid replacing `System.in` or `System.out`; restore them in `finally` when replacement is unavoidable.
- Use a bounded subprocess for JVM startup, classpath, exit-code, and EOF behavior.
- Do not share writable static fixtures.

## Scope and edge cases

- Use a unit test for a pure function or one object with controlled collaborators.
- Use an integration test for collaboration among real objects.
- Use a subprocess only for behavior narrower tests cannot prove.
- Fake an architectural boundary to prove dependency direction, not merely to increase isolation.
- Derive edge cases from the contract: empty, null, whitespace, boundary numbers, invalid syntax, unknown IDs, retries, duplicate actions, quit, and EOF.
- Add a regression test that fails for an observed bug before changing production code.

## Test tiers

- Write a tier 1 file test for every production class: one class in isolation, collaborators controlled, named `<ClassName>Test` and mirroring the production package.
- Write a tier 2 flow test for every user-visible flow: real collaborators wired the way `main` wires them, one complete path, named for the behavior — `SolveFlowTest`, not `SolverAndGridTest`.
- Write a tier 3 end-to-end test for every runnable `main`: a real JVM through `ProcessBuilder`, asserting the exit code and exact stdout.
- Keep the proportion honest: dozens of tier 1, a handful of tier 2 per chapter, one or two tier 3 and no more.
- Choose a tier by the bug it would catch, not by convenience. A wrong rendered character is tier 1; correct parts wired together wrongly is tier 2; correct in tests but wrong in a terminal is tier 3.
- Keep shared test infrastructure in its own file and do not let it assert anything itself.
- Treat a tier 1 file past roughly 200 lines as evidence the production class has two responsibilities, not as a reason to split the test file.

## Maintenance

- Expect to revisit earlier test files to rewrite, extend, restructure, or delete as behavior grows. Test files are not append-only.
- Let a test against boilerplate stay boring. Rigour arrives with behavior, not in advance of it.
- Do not write speculative tests for behavior that does not exist yet.
- Delete a test when it asserts something no longer true, when a later test covers it better, when it is pinned to an implementation detail you have replaced, or when it exists only to move a coverage number.
- Treat one production-line change turning six tests red — five of them testing the same thing — as maintenance already overdue.

## Quality

- Apply production naming, imports, indentation, line length, braces, whitespace, and warning discipline to tests.
- Keep tests independent of locale, default charset, current date, filesystem order, and network access unless that dependency is under test.
- Close owned resources, never catch an assertion failure to keep a test green, and treat coverage as exercised-code evidence rather than correctness proof.

## Reference examples

Read these in `~/repos/JAVA/job-application-tracker`, under `src/test/java/com/connorjensen/jobtracker/`. They are examples of shape, on code unrelated to this project.

- `ApplicationTest.java` — tier 1. One class, no collaborators, values in and values out.
- `cli/ConsoleSessionTest.java` — tier 2. Real objects wired together, streams injected, one complete path asserted.
- `MainProcessTest.java` — tier 3. `ProcessBuilder`, stdin closed, a timeout, explicit UTF-8, exit code and exact stdout.

No agent-authored grader exists in this project and none will. Every test here is learner-written; the capstone grades the suite rather than supplying one.
