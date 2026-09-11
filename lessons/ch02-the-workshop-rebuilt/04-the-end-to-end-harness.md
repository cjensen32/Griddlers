# 2.4 — The end-to-end harness

*New. Chapter 1's `MainProcessTest.java` is the thing you are generalising.*

This is the lesson the chapter exists for. Every tier 3 test you write from Chapter 3 to Chapter 14 calls the thing you build here, so it gets built once, carefully, before there is any nonogram code to distract you.

A tier 3 test launches a real JVM, feeds it stdin, waits, and asserts on what came back. Written inline it is forty lines of plumbing wrapped around three lines of assertion, and the plumbing is where the bugs live — a pipe that fills and deadlocks, a charset that only misbehaves on a different machine, a hang that takes the whole build down with it. Written once as infrastructure, it is one call.

Read `~/repos/JAVA/job-application-tracker/src/test/java/com/connorjensen/jobtracker/MainProcessTest.java` before you start. Every non-obvious line in it is there because of a bug. You are turning that file into something reusable, not copying it.

Lesson 2.3 decided two things that land on this lesson directly. Whichever way you answered the test-sources question, it decides whether `import-control.xml` governs the file you are about to write — and a subprocess harness is the most import-hungry file in the repository. And the `Main` exclusion in your coverage rule was written when `Main` did nothing at all; this is the lesson that gives it something to do.

## Must be true when you're done

- [ ] one class in the test tree whose only job is launching a JVM — it asserts nothing itself, per `lessons/TESTING_STANDARDS.md`, and Surefire does not try to run it as a test
- [ ] it takes the main class to run and the stdin to feed it, and hands back the exit code together with stdout and stderr decoded as UTF-8 rather than as whatever the running machine's default happens to be
- [ ] a child that never exits fails the calling test inside a bounded time instead of hanging `mvn verify` until someone notices
- [ ] the classpath handed to the child is the one Maven actually built, discovered at runtime rather than written into the file
- [ ] stdout and stderr are both readable after the run, and a child that writes more than a pipe holds still completes — and you have run the version that does not, so you can name the line that prevents it
- [ ] one tier 3 test uses the harness against `com.connorjensen.griddlers.Main` and asserts the exit code together with the whole of stdout rather than a fragment of it, which means `Main` prints something worth asserting
- [ ] 2.3's boundary still passes with the harness in the tree, and where the harness is allowed to live is a consequence of the answer you gave there rather than a coincidence
- [ ] the coverage rule and the harness are reconciled on purpose — you can say whether code exercised only inside the child is counted, and the POM says the same thing you just said

## Done when

- [ ] `mvn -B verify` is green, the tier 3 test appears in `target/surefire-reports/` by name, and the collector's test ladder reports a tier 3 file where it previously reported `none`
- [ ] you have watched the timeout fire — a deliberately hanging main fails its test rather than stalling the suite — and then put things back
- [ ] `mvn test` reports the new total with failures, errors, and skips at 0
- [ ] every term this lesson sent you to look up has a `2.4` row in the journal's `Definitions` table with a definition in it
- [ ] `git status` is clean and the commit has landed

## Reach for

`ProcessBuilder`, and the overload of `Process.waitFor` that takes a duration. `destroyForcibly` for what you do when that duration runs out.

The JVM you are already running knows where its own executable lives and what classpath it was started with. Both are system properties, and finding them is the lesson — hardcoding `target/classes` is what breaks the first time someone adds a dependency. Print the classpath property from inside a test under `mvn test`, then print it from the same code run another way, and compare before you decide what to hand the child: only one of the two is a list of directories.

`Process.getInputStream` is the child's *output*. Read the name against the direction of flow once, deliberately, and you will never lose an afternoon to it.

`StandardCharsets.UTF_8` everywhere a byte becomes a character. An `InputStreamReader` built without a charset is a test that passes on your machine and fails on a build server, which is strictly worse than one that fails on both.

The four characters Chapter 3's renderer prints are in `.agents/reference/course-rationale.md`, Appendix A: `█`, `·`, `✕`, `▓`. Not one is ASCII. The charset box above is not hygiene — it is the reason the first renderer test you write in Chapter 3 either works everywhere or works only here.

Look up what happens when a child fills its stdout pipe while the parent is blocked in `waitFor`. That deadlock decides the order of operations in your harness, and it is the single most common way this class is written wrong. The pipe's capacity is a property of the operating system rather than of Java, which is why picking a size you believe is safe is not the fix.

`redirectErrorStream` merges the two streams into one. Decide whether you want that before you write it — the answer changes what you can tell the reader when a test fails at 2am.

Decide what "no input" means to your harness: a stream that is empty, or one that is closed. A child blocking forever on a read from something you never closed is the same hang you already have a timeout for, arriving for a completely different reason, and the timeout cannot tell you which one you got.

Surefire decides what is a test by filename, and your harness is not one. Check the default includes before you name the file, and re-read the `!2` footnote you wrote in the journal about them — you already know more about this than that row does.

`prepare-agent` sets `argLine`, which is the journal row you wrote in 2.2. So the JVM running your tests carries `-javaagent` and the JVM your harness starts does not, unless you put it there. Before deciding whether to, find out what two JVMs writing one `jacoco.exec` do to each other. `RuntimeMXBean#getInputArguments` is the tempting way to copy the parent's arguments wholesale; decide, rather than inheriting by accident.

If 2.3 left the test tree governed, check what `import-control.xml` says about the engine package before you choose where the harness lives — a harness needs streams, and the rule has an opinion about where streams come from. Moving the file is one answer and writing a rule for the test tree is another; they are not the same answer and only one of them scales to Chapter 9.

`Main` currently prints nothing, so there is no stdout worth asserting. That is yours to change; `Main` is your file.

## Pop quiz

Answer these from memory, editor closed.

1. Name a bug this harness catches that a tier 1 and a tier 2 test, both passing, cannot.
2. A child writes 200KB to stdout. The parent calls `waitFor()` and then reads. Describe exactly what happens and why.
3. Your harness decodes stdout with the platform default charset and the test passes. Whose machine does it fail on, and why is a test that fails elsewhere worse than one that fails here?
4. Where does the child's classpath come from? What specifically breaks the day you hardcode it?
5. The harness asserts nothing. Why is that a rule rather than a preference — what goes wrong in a suite where shared infrastructure carries its own assertions?
6. The timeout expires and you call `destroyForcibly`. The process ignores it. What is still true about the test result, and what is not?

## Homework — eight children, and what the harness says about each

- copy [`resources/l04/HarnessProbe.java`](resources/l04/HarnessProbe.java) into the test tree; it takes one argument and has no imports, so it makes no decisions for you about where the harness lives
- one probe behaviour per run, and record the exit code, both byte counts, and the wall time as you go
- the wall time is the column that catches the bug in section 2, so write it down even when it is boring
- the probe comes out of the tree before the commit, exactly the way `StyleViolations.java` did in 2.2

### 1. The matrix

Predict every row before running any of it.

| # | Argument  | The child does               | Predicted exit | Observed exit | stdout bytes | stderr bytes | Wall time |
|---|-----------|------------------------------|----------------|---------------|--------------|--------------|-----------|
| 1 | `silent`  | nothing, exits 0             |                |               |              |              |           |
| 2 | `line`    | one short line               |                |               |              |              |           |
| 3 | `small`   | 1KB to stdout                |                |               |              |              |           |
| 4 | `flood`   | 200KB to stdout              |                |               |              |              |           |
| 5 | `floode`  | 200KB to stderr              |                |               |              |              |           |
| 6 | `glyphs`  | the four Appendix A glyphs   |                |               |              |              |           |
| 7 | `echo`    | echoes stdin to end of input |                |               |              |              |           |
| 8 | `hang`    | sleeps until killed          |                |               |              |              |           |
| 9 | *(typo)*  | exits 3                      |                |               |              |              |           |

Row 5 tests the `redirectErrorStream` decision you made rather than the one you meant to make. Row 7 tests what you decided "no input" means. Row 8 should cost you the timeout and nothing else. Row 9 exists so that a typo in a test is a failure rather than a pass.

### 2. The deadlock you have to cause on purpose

Write the harness the wrong way round once, deliberately: `waitFor` first, read afterwards. Run rows 3 and 4 against it, then bisect between them until you find the size where the behaviour changes. Record the number and the wall time either side of it.

Then answer three things. Which of `waitFor`, `destroyForcibly`, and your timeout actually ended the failing run. What the *test result* was — not what the terminal looked like, the result Surefire recorded. And whether the number you found is a fact about Java, about your harness, or about the machine you are sitting at, which decides whether it would be the same number in CI.

Then the judgement call, which is the actual exercise. Your timeout turned a deadlock into a test failure, so a suite carrying this bug is not hung — it is red, slowly, in one test, for a reason the failure message never mentions. Decide whether that is the timeout doing its job or the timeout hiding the defect, and say what someone would have to read to tell the difference. Fix the order afterwards and confirm the number stops mattering.

### 3. Two ways to corrupt a glyph

Run row 6 and assert the exact string. Then break it on purpose, twice, by starting the *child* with a property that makes its default encoding something other than UTF-8. There are two such properties — one changes what `Charset.defaultCharset()` reports, the other changes only what reaches the console — and they do not do the same damage.

Record, for each: what `od -c` shows of the bytes that arrived, whether your assertion failed, and whether the original four characters could in principle be recovered from what you received.

The point is the asymmetry. Explicit UTF-8 on your side of the pipe is necessary and it is not sufficient: one of these corruptions is something a decoder can undo, and the other is a lossy substitution that happened before any byte reached you. Say which is which, and say what that does to the sentence "my harness handles encoding correctly."

## Homework — must be true when you're done

- [ ] every row of the matrix has a prediction written before its observation, and no blank cells
- [ ] the size at which row 3's behaviour becomes row 4's behaviour is a number in the table, and you can say what it is a property of
- [ ] you have watched the deliberately wrong harness fail, and can name the line in the right one that makes that size irrelevant
- [ ] both glyph corruptions are recorded with the bytes that arrived, and you can say which one no decoder could have undone
- [ ] the coverage-through-a-fork decision is written down in one direction, and the journal names the two you did not pick
- [ ] the `Main` exclusion in the POM is either justified in one sentence against what `Main` now prints, or gone
- [ ] `git status` shows no probe left in the tree, and the probe appears in no commit
