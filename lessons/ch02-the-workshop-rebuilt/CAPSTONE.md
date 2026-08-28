# Chapter 2 Capstone — stub

Written after Lesson 2.6, from the code and tests that exist by then. Until the chapter is over this file holds the "Must be true" list and nothing else.

## Must be true when you're done

| Guarantee | Depended on by |
|---|---|
| `mvn verify` runs tests, Checkstyle, Spotless, and JaCoCo, and fails the build when any of them fails | every chapter |
| Surefire actually runs JUnit 5 tests — a deliberately failing test fails the build | every chapter |
| Something can launch a named main class in a fresh JVM, feed it stdin, and return exit code plus UTF-8 stdout within a timeout | every tier 3 test from Chapter 3 on |
| An engine package and a `tools` package exist, and a forbidden import in the engine fails the build | Chapters 9–13, where the boundary is load-bearing |
| Git history exists under a commit grammar | nothing technical; it is how you go back to the last version that worked |
| You can identify each of the six failure modes from its output alone | your own debugging speed, for a year |
