# 2.1 — The POM, from memory

*Review — Chapter 1, Lesson 0.*

Maven will not compile a line until a project descriptor tells it what this project is called and what it is built against. You wrote one of these in Chapter 1. Write this one without opening that one, then diff the two and account for every difference — a POM you copied teaches nothing, a POM you reconstructed teaches the build twice.

## Must exist when you're done

- [ ] `pom.xml` at the repo root, with coordinates `com.connorjensen` / `griddlers`, `maven.compiler.release` 21, `junit-jupiter` at test scope, and a pinned Surefire version
- [ ] one test class under `src/test/.../griddlers/` asserting something trivially true, so `mvn test` has something to report

## Done when

- [ ] `mvn compile` succeeds
- [ ] `mvn test` reports `[INFO] Tests run: 1` with failures, errors, and skips at 0
- [ ] `git status` is clean and the commit has landed

## Reach for

`@Test` and `assertEquals` from `org.junit.jupiter.api`. For the Surefire version, look at what `<pluginManagement>` does that a bare `<plugins>` block does not.

Chapter 1's `pom.xml` is the reference — after you have written yours, not before.

## Pop quiz

Answer these from memory, editor closed. They are the reason this lesson is not just typing.

1. What are the phases of the default Maven lifecycle, and what decides which plugin goals run in each one?
2. What does `mvn clean install` do that `mvn install` does not, and when does the difference bite?
3. What does `test` scope actually change about a dependency, and what breaks if JUnit were left at the default scope?
4. Why does Surefire need a pinned version at all? What is the symptom when it is not pinned, and why is that symptom worse than a build failure?
5. Where do compiled classes and test reports land, and why is none of it committed?
