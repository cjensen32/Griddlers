# Journal

## Stuck and unstuck

One line each time: what broke, what fixed it.

## Definitions

A row is added when a lesson first requires the term. Scope and definition are filled in by hand, from your own reading; a term that comes back in a later lesson keeps the row it already has.

| Lesson | Scope | Term                        | Definition                                                                      |
|--------|-------|-----------------------------|---------------------------------------------------------------------------------|
| 2.1    | junit | JUnit Framework             | Open-Source architecture composed of: Platform, Jupiter, and Vintage            |
| 2.1    | junit | JUnit Platform              | Structural foundation of starting the test framework on jvm                     |
| 2.1    | junit | JUnit Jupiter               | Core of the JUnit Framework, contains annotations, models, and extensions       |
| 2.1    | junit | aggregator artifact (junit) | Import artifact that exists to import several different packages together !4 ?1 |
| 2.1    | mvn   | Surefire                    | Standard plugin for executing tests during `mvn test` lifecycle phase           |
| 2.1    | mvn   | Surefire default includes   | Surefire includes all test classes containing `**/*Test.java` !2 ?2             |
| 2.1    | mvn   | `target/`                   | Maven default output for build-generated artifacts/files                        |
| 2.1    | mvn   | POM                         | Project Object Model; an XML config file; utilized by Maven                     |
| 2.1    | mvn   | test vs main classpaths     | test classpath inherits from main; main classpath does not inherit from test    |
| 2.1    | mvn   | Transivity                  | Logic: If A->B and B->C, then A->C; Used for automatic dependency inclusion     |
| 2.1    | mvn   | transitive dependency       | A library included in project because it is required by a direct dependency;    |
| 2.1    | mvn   | coordinates                 | id's to locate specific packages (Group, ArtifactId, Version)                   |
| 2.1    | mvn   | plugin                      | Build tools that can usually be bound to the Lifecycle                          |
| 2.1    | mvn   | `<plugins>`                 | Inside the `<build>` section; contains `<plugin>` blocks with coords + configs  |
| 2.1    | mvn   | `<pluginManagement>`        | Inside `<build>`; defines 'default' config for child `<plugin>` blocks          |
| 2.1    | mvn   | dependency scope            | Controls transivity of libraries across stages (`runtime` vs `test`) ?3         |
| 2.1    | mvn   | super-POM                   | The file that a pom.xml file inherits from. lives in mvn install location       |
| 2.1    | mvn   | effective POM               | Result of merging the pom.xml with the super-POM file                           |
| 2.1    | mvn   | goal                        | A single, specific, granular task; building blocks of phases                    |
| 2.1    | mvn   | phase                       | A single (Build) step in a lifecycle; made of Plugin Goals                      |
| 2.1    | mvn   | lifecycle (default)         | Core (default) 'pipelines' that validate, compile, test, and package a project  |
| 2.1    | mvn   | lifecycle mapping           | Binds goals to phases of the Maven Lifecycle                                    |
| 2.1    | mvn   | `<packaging>`               | Property to determine package type for project; default of `jar` !3             |
| 2.1    | mvn   | local repository            | Where all downloaded deps are stored                                            |
| 2.1    | mvn   | `maven.compiler.release`    | Maven cross-compile solution to safely validate/check backwards compatibility   |
| 2.1    | mvn   | `mvn clean`                 | A mvn lifecycle/phase/goal; removes old files and build artifacts               |
| 2.1    | mvn   | `mvn validate`              | 1st !1 (default) phase; internal; verifies metadata and project correctness     |
| 2.1    | mvn   | `mvn compile`               | 2nd !1 (defualt) phase; src/main/java code -> .class files to output dir        |
| 2.1    | mvn   | `mvn test`                  | 3rd !1 (default) phase; compile src/test/java tests -> execute unit tests       |
| 2.1    | mvn   | `mvn package`               | 4th !1 (defualt) phase; compiled code -> bundle into .jar/.war/.etc dist.       |
| 2.1    | mvn   | `mvn verify`                | 5th !1 (default) phase; runs integration tests, code metrics, standards         |
| 2.1    | mvn   | `mvn install`               | 6th !1 (default) phase; dist copy .jar/.war/.etc  -> local repository (.m2)     |
| 2.1    | mvn   | SNAPSHOT                    | Version designator; marks project as still in active development                |
| 2.1    | mvn   | plugin execution            | In the `<plugin>` block; links a plugin goal to a phase in mvn lifecycle        |
| 2.1    | mvn   | property / `${...}`         | found in `<properites>` block; `pom.xml` "local" variables; pin dep versions    |
| 2.2    | mvn   | `<executions>`              | Property inside `<plugin>` blocks; contains the `<execution>` definitions       |
| 2.2    | mvn   | execution `<id>`            | Defines when/how/where to execute a(n) `<goal>`/ `<goals>`; `<id>` is a name    |
| 2.2    | mvn   | default lifecycle binding   | plugin goals may have a phase binding preset, can be overriden in `<phase>` def |
| 2.2    | mvn   | `prepare-agent`             | goal from jacoco; sets surefire's `argLine` property                            |
| 2.1    | git   | ignored vs untracked        | Ignored: files skip indexing, Untracked: files aren't yet indexed               |
| 2.1    | java  | reproducible build          | Given the same source code, compilation produces the same output bit-for-bit    |
| 2.1    | java  | utility class               | Class to hold a collection of static, reusable, methods/constants;              |
| 2.1    | java  | `-Xlint`                    | `javac` flag to control warnings categories; `-Xlint` = all reccomended         |
| 2.2    | java  | Java agent (`-javaagent`)   | load a language agent from a `.jar` with options (see `java.lang.instrument`??) |
| 2.2    | java  | bytecode instrumentation    | allows intercepting of the `.class` files before loading to memory              |
| 2.2    | qa    | Checkstyle                  | plugin to report on violations of code style; can fail if configured to do so   |
| 2.2    | qa    | Spotless                    | plugin to format code; `apply` goal rewrites files with configuration defined   |
| 2.2    | qa    | JaCoCo                      | plugin to create reports on the code coverage                                   |
| 2.2    | qa    | `google-java-format`        | `spotless` has an integrated ruleset to format code to google java standards    |
| 2.2    | qa    | check goal vs apply goal    | `check` looks for unformatted code; `apply` reformats all according to config   |
| 2.2    | qa    | line vs branch coverage     | test coverage: `line` is % lines covered; `branch` is % if/else/switch coverage |

### Your notes

!1 this is the recorded order of the essential default lifecycle phases [source](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
!2 I know this isn't completely true, Surefire includes by default any files from any subdirectory that start or end with `Test` end with `TestCase` or end with `Tests`. 
!3 Automatically is set to `jar`, so property isn't explicitely defined in the `pom.xml` file. it does determine the type of file in the `target/` directory built with the `package` phase. here is a source for the types of packages that java projects can have (jar, ear, war, pom, maven-plugin, ejb, rar, etc) [source](https://www.baeldung.com/maven-packaging-types) 
!4 I don't exactly understand the scope here. Since talking about the Aggregator Artifact could be talking about how you import a specific one in your `pom.xml`, it could also be talking about how to utilize them in test classes. Went with the latter as it makes more sense with lesson and the imports we used. 

### Agent marks

?1 the definition describes a static import. An aggregator artifact is a dependency that ships no code of its own - `junit-jupiter` exists to pull `junit-jupiter-api`, `-params`, and `-engine` in transitively. Your !4 already suspected this. Settle it against `mvn dependency:tree`.
?2 the row still states the version your own !2 corrects. Fold the footnote into the row so the row is the reference.
?3 `transivity` is right; `phases of the mvn lifecycle` is the wrong axis. Lesson 2.3 puts a hard boundary between two classpaths and will settle which one scope indexes on.

## Open gaps

What a lesson left for a later one, because no checkbox named it. Agent-maintained; the chapter README's `Open` column is this table's row count for that lesson.

| Lesson | Kind  | Open item                                | Settles in |
|--------|-------|------------------------------------------|------------|
| 2.1    | build | explicit `<packaging>` never typed       | 2.2        |
| 2.1    | build | two version-pinning idioms, not one      | 2.2        |
| 2.1    | build | no `<execution>` block written           | 2.2        |
| 2.1    | term  | `aggregator artifact (junit)`, see `?1`  | 2.2        |
| 2.1    | term  | `Surefire default includes`, see `?2`    | 2.2        |
| 2.1    | term  | `dependency scope`, see `?3`             | 2.3        |
| 2.2    | build | Checkstyle never reads `src/test/java`   | 2.3        |
| 2.2    | build | JaCoCo's 0.8 line rule excludes `Main`   | 2.3        |
| 2.2    | git   | `core.hooksPath` documented only in-hook | 2.6        |

## Capstone gap counts

| Chapter | Gaps found at Gate 3 | Notes |
|---------|----------------------|-------|
