# 2.2 — The quality gates, from memory

*Review — Chapter 1, Lesson 8.*

Three plugins that disagree about what they are allowed to touch. Checkstyle reads and complains. Spotless rewrites, but only when you ask it to. JaCoCo watches the tests run and reports what they never reached. Wire all three into the POM you just wrote, and understand the split before you type it — the split is the lesson, the XML is not.

## Must exist when you're done

- [ ] Checkstyle bound to the `validate` phase, reading `config/checkstyle/checkstyle.xml`
- [ ] Spotless configured with `google-java-format`
- [ ] JaCoCo producing a coverage report
- [ ] `pom.xml` still passing everything from Lesson 2.1

## Done when

- [ ] `mvn verify` runs all three plugins and passes on an almost-empty project
- [ ] `mvn spotless:check` fails on a deliberately misformatted file, and `mvn spotless:apply` fixes it
- [ ] you can point at the JaCoCo report on disk and say what it measures

## Reach for

`config/checkstyle/checkstyle.xml` is already in the repository, copied from Chapter 1 — 31 modules you extend rather than rebuild. For JaCoCo, the `prepare-agent` goal has to run before the tests do; work out why from what the agent has to attach to.

## Pop quiz

1. Why does the whole build fail when Checkstyle finds a violation during `mvn test`, when Checkstyle never looks at a test result?
2. Checkstyle and Spotless both have opinions about formatting. What does each one do that the other cannot, and why keep both?
3. Why is Spotless not bound to a phase that runs during `mvn verify` in rewrite mode? What would break if it were?
4. JaCoCo reports 100% coverage on a class. Name two bugs that number does not rule out.
5. What is the difference between `validate` and `verify`, and roughly what has already happened by the time each one runs?
