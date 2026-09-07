# 2.4 — The end-to-end harness

*New. Chapter 1's `MainProcessTest.java` is the thing you are generalising.*

This is the lesson the chapter exists for. Every tier 3 test you write from Chapter 3 to Chapter 14 calls the thing you build here, so it gets built once, carefully, before there is any nonogram code to distract you.

A tier 3 test launches a real JVM, feeds it stdin, waits, and asserts on what came back. Written inline it is forty lines of plumbing wrapped around three lines of assertion, and the plumbing is where the bugs live — a pipe that fills and deadlocks, a charset that only misbehaves on a different machine, a hang that takes the whole build down with it. Written once as infrastructure, it is one call.

Read `~/repos/JAVA/job-application-tracker/src/test/java/com/connorjensen/jobtracker/MainProcessTest.java` before you start. Every non-obvious line in it is there because of a bug. You are turning that file into something reusable, not copying it.

## Must be true when you're done

- [ ] one class in the test tree whose only job is launching a JVM — it asserts nothing itself, per `lessons/TESTING_STANDARDS.md`
- [ ] it takes the main class to run and the stdin to feed it, and hands back the exit code together with stdout and stderr decoded as UTF-8 rather than as whatever the running machine's default happens to be
- [ ] a child that never exits fails the calling test inside a bounded time instead of hanging `mvn verify` until someone notices
- [ ] the classpath handed to the child is the one Maven actually built, discovered at runtime rather than written into the file
- [ ] stdout and stderr are both readable after the run, and a child that writes more than a pipe holds still completes
- [ ] one tier 3 test uses the harness against `com.connorjensen.griddlers.Main` and asserts both the exit code and the exact stdout
- [ ] Lesson 2.3's boundary still passes with the harness in the tree

## Done when

- [ ] `mvn -B verify` is green and the tier 3 test appears in `target/surefire-reports/` by name
- [ ] the collector's test ladder reports a tier 3 file where it previously reported `none`
- [ ] you have watched the timeout fire — a deliberately hanging main fails its test rather than stalling the suite — and then put things back
- [ ] `mvn test` reports the new total with failures, errors, and skips at 0
- [ ] `git status` is clean and the commit has landed

## Reach for

`ProcessBuilder`, and the overload of `Process.waitFor` that takes a duration. `destroyForcibly` for what you do when that duration runs out.

The JVM you are already running knows where its own executable lives and what classpath it was started with. Both are system properties. Finding them is the lesson; hardcoding `target/classes` is the thing that breaks the first time someone adds a dependency.

`StandardCharsets.UTF_8` everywhere a byte becomes a character. An `InputStreamReader` built without a charset is a test that passes on your machine and fails on a build server, which is strictly worse than one that fails on both.

Look up what happens when a child process fills its stdout pipe while the parent is blocked in `waitFor`. That deadlock decides the order of operations in your harness, and it is the single most common way this class is written wrong.

`redirectErrorStream` merges the two streams into one. Decide whether you want that before you write it — the answer changes what you can tell the reader when a test fails at 2am.

`Main` currently prints nothing, so there is no stdout worth asserting. That is yours to change; `Main` is your file.

## Pop quiz

Answer these from memory, editor closed.

1. Name a bug this harness catches that a tier 1 and a tier 2 test, both passing, cannot.
2. A child writes 200KB to stdout. The parent calls `waitFor()` and then reads. Describe exactly what happens and why.
3. Your harness decodes stdout with the platform default charset and the test passes. Whose machine does it fail on, and why is a test that fails elsewhere worse than one that fails here?
4. Where does the child's classpath come from? What specifically breaks the day you hardcode it?
5. The harness asserts nothing. Why is that a rule rather than a preference — what goes wrong in a suite where shared infrastructure carries its own assertions?
6. The timeout expires and you call `destroyForcibly`. The process ignores it. What is still true about the test result, and what is not?
