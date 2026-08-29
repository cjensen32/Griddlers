# 2.1 — The POM, from memory

*Review — Chapter 1, Lesson 0.*

Maven will not compile a line until a project descriptor tells it what this project is called and what it is built against. You wrote one of these in Chapter 1. Write this one without opening that one, then diff the two and account for every difference — a POM you copied teaches nothing, a POM you reconstructed teaches the build twice.

## Must exist when you're done

- [x] `pom.xml` at the repo root, with coordinates `com.connorjensen` / `griddlers`, `maven.compiler.release` 21, `junit-jupiter` at test scope, and a pinned Surefire version
- [x] one test class under `src/test/.../griddlers/` asserting something trivially true, so `mvn test` has something to report

## Done when

- [x] `mvn compile` succeeds
- [x] `mvn test` reports `[INFO] Tests run: 1` with failures, errors, and skips at 0
- [x] `git status` is clean and the commit has landed

## Reach for

`@Test` and `assertEquals` from `org.junit.jupiter.api`. For the Surefire version, look at what `<pluginManagement>` does that a bare `<plugins>` block does not.

Chapter 1's `pom.xml` is the reference — after you have written yours, not before.

## Pop quiz

Answer these from memory, editor closed. They are the reason this lesson is not just typing.

1. What are the phases of the default Maven lifecycle, and what decides which plugin goals run in each one?
  A. there are 20+ actual phases of the full default Maven lifecycle, mainly including: `mvn validate`, `mvn compile`, `mvn test`, `mvn package`, `mvn verify`, and `mvn install` (90% confident)
  B. What decides which plugin goals run in each phase? the plugins and their lifecycle bindings determine what plugins, which phase, and how are they going to run the chosen goals. (90% confident) 
2. What does `mvn clean install` do that `mvn install` does not, and when does the difference bite?
  - `mvn clean` is a lifecycle that removes old artifacts and builds, which adds extra compile time to the complete maven lifecycles (`mvn clean` + `mvn install`); it bites if your build DID require rebuilding and you chose just to install over it without removing old content. (85% confident) 
3. What does `test` scope actually change about a dependency, and what breaks if JUnit were left at the default scope?
  - It helps with the transivity of dependencies as well as default scope packaging conflicts. The default packaging phase would go through all files in the src/main/java packages and package them as a whole group. Which can be problematic for code analysis and execution as all tests would be included in the main area of the target/ output classes instead of with the tests classes (50% confident) 
4. Why does Surefire need a pinned version at all? What is the symptom when it is not pinned, and why is that symptom worse than a build failure?
  - Surefire needs a pinned version to ensure a reproducible build (no guessing which version) and compatibility issues (plugin conflicts that may be silently ignored)  (85% confident) 
5. Where do compiled classes and test reports land, and why is none of it committed?
  - they land in the designated `target/` directory. None of it is commited as they are artifacts, or snapshots of the repo status AT point of compiling. (94% confident)  
