# 2.2 — The quality gates, from memory

*Review — Chapter 1, Lesson 8.*

Three plugins that disagree about what they are allowed to touch. Checkstyle reads and complains. Spotless rewrites, but only when you ask it to. JaCoCo watches the tests run and reports what they never reached. Wire all three into the POM you just wrote, and understand the split before you type it — the split is the lesson, the XML is not.

Lesson 2.1 left you a POM where nothing is bound to a phase by hand. That was never asked for, so it is not there. All three of these plugins need it.

## Must be true when you're done

- [ ] `mvn validate` alone fails on a style violation, before anything is compiled
- [ ] `mvn spotless:check` fails on a misformatted file, and `mvn spotless:apply` fixes it
- [ ] a coverage report on disk that names `Main` and the percentage its tests reached
- [ ] every plugin version set in one place, so changing one means editing one line
- [ ] `pom.xml` still does everything Lesson 2.1 made it do

## Done when

- [ ] `mvn -B verify` is green and prints no `[WARNING]` you cannot explain
- [ ] `mvn test` still reports `Tests run: 1` with failures, errors, and skips at 0
- [ ] `mvn help:effective-pom` read once, and the `<packaging>` value you never typed recorded in the journal
- [ ] `git status` is clean and the commit has landed

## Reach for

`config/checkstyle/checkstyle.xml` is already in the repository, copied from Chapter 1 — 31 modules you extend rather than rebuild. For JaCoCo, the `prepare-agent` goal has to run before the tests do; work out why from what the agent has to attach to.

Surefire ran in Lesson 2.1 without you binding it to anything. None of these three will, and `mvn help:effective-pom` shows you the difference.

Your POM already contains both `<properties>` and `<pluginManagement>`, doing this job for exactly one plugin. The compiler is pinned the other way.

A green build is not a correct POM — Lesson 2.1 ended with one that passed while carrying a scope Maven does not recognise. Read the warnings.

## Pop quiz

1. Why does the whole build fail when Checkstyle finds a violation during `mvn test`, when Checkstyle never looks at a test result?
2. Checkstyle and Spotless both have opinions about formatting. What does each one do that the other cannot, and why keep both?
3. Why is Spotless not bound to a phase that runs during `mvn verify` in rewrite mode? What would break if it were?
4. JaCoCo reports 100% coverage on a class. Name two bugs that number does not rule out.
5. What is the difference between `validate` and `verify`, and roughly what has already happened by the time each one runs?
6. Surefire ran in Lesson 2.1 without you binding it to a phase; Checkstyle will not. Name both places a goal-to-phase binding can come from.
