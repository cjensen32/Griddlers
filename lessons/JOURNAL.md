# Journal

## Stuck and unstuck

One line each time: what broke, what fixed it.

## Definitions

A row is added when a lesson first requires the term. Scope and definition are filled in by hand, from your own reading; a term that comes back in a later lesson keeps the row it already has.

| Lesson | Scope | Term                        | Definition                                                                     |
|--------|-------|-----------------------------|--------------------------------------------------------------------------------|
| 2.1    | junit | JUnit Framework             | Open-Source architecture composed of: Platform, Jupiter, and Vintage           |
| 2.1    | junit | JUnit Platform              | Structural foundation of starting the test framework on jvm                    |
| 2.1    | junit | JUnit Jupiter               | Core of the JUnit Framework, contains annotations, models, and extensions      |
| 2.1    | junit | aggregator artifact (junit) | artifact group import Ex.`import static org...Api.Assertions.*` !4             |
| 2.1    | mvn   | Surefire                    | Standard plugin for executing tests during `mvn test` lifecycle phase          |
| 2.1    | mvn   | Surefire default includes   | Surefire includes all files from any subdirectory that contain `Test.java` !2  |
| 2.1    | mvn   | `target/`                   | Maven default output for build-generated artifacts/files                       |
| 2.1    | mvn   | POM                         | Project Object Model; an XML config file; utilized by Maven                    |
| 2.1    | mvn   | test vs main classpaths     | test classpath inherits from main; main classpath does not inherit from test   |
| 2.1    | mvn   | Transivity                  | Logic: If A->B and B->C, then A->C; Used for automatic dependency inclusion    |
| 2.1    | mvn   | transitive dependency       | A library included in project because it is required by a direct dependency;   |
| 2.1    | mvn   | coordinates                 | id's to locate specific packages (Group, ArtifactId, Version)                  |
| 2.1    | mvn   | plugin                      | Build tools that can usually be bound to the Lifecycle                         |
| 2.1    | mvn   | `<plugins>`                 | Inside the `<build>` section; contains `<plugin>` blocks with coords + configs |
| 2.1    | mvn   | `<pluginManagement>`        | Inside `<build>`; defines 'default' config for child `<plugin>` blocks         |
| 2.1    | mvn   | dependency scope            | Controls transivity across different phases of the mvn lifecycle               |
| 2.1    | mvn   | super-POM                   | The file that a pom.xml file inherits from. lives in mvn install location      |
| 2.1    | mvn   | effective POM               | Result of merging the pom.xml with the super-POM file                          |
| 2.1    | mvn   | goal                        | A single, specific, granular task; building blocks of phases                   |
| 2.1    | mvn   | phase                       | A single (Build) step in a lifecycle; made of Plugin Goals                     |
| 2.1    | mvn   | lifecycle (default)         | Core (default) 'pipelines' that validate, compile, test, and package a project |
| 2.1    | mvn   | lifecycle mapping           | Binds goals to phases of the Maven Lifecycle                                   |
| 2.1    | mvn   | `<packaging>`               | Configs a build lifecycle relating to declared package type jar/war/ear/etc !3 |
| 2.1    | mvn   | local repository            | Where all downloaded deps are stored                                           |
| 2.1    | mvn   | `maven.compiler.release`    | Maven cross-compile solution to safely validate/check backwards compatibility  |
| 2.1    | mvn   | `mvn clean`                 | A mvn lifecycle/phase/goal; removes old files and build artifacts              |
| 2.1    | mvn   | `mvn validate`              | 1st !1 (default) phase; internal; verifies metadata and project correctness    |
| 2.1    | mvn   | `mvn compile`               | 2nd !1 (defualt) phase; src/main/java code -> .class files to output dir       |
| 2.1    | mvn   | `mvn test`                  | 3rd !1 (default) phase; compile src/test/java tests -> execute unit tests      |
| 2.1    | mvn   | `mvn package`               | 4th !1 (defualt) phase; compiled code -> bundle into .jar/.war/.etc dist.      |
| 2.1    | mvn   | `mvn verify`                | 5th !1 (default) phase; runs integration tests, code metrics, standards        |
| 2.1    | mvn   | `mvn install`               | 6th !1 (default) phase; dist copy .jar/.war/.etc  -> local repository (.m2)    |
| 2.1    | mvn   | SNAPSHOT                    | Version designator; marks project as still in active development               |
| 2.1    | mvn   | plugin execution            | In the `<plugin>` block; links a plugin goal to a phase in mvn lifecycle       |
| 2.1    | mvn   | property / `${...}`         | found in `<properites>` block; `pom.xml` "local" variables; pin dep versions   |
| 2.1    | git   | ignored vs untracked        | Ignored: files skip indexing, Untracked: files aren't yet indexed              |
| 2.1    | java  | reproducible build          | Given the same source code, compilation produces the same output bit-for-bit   |
| 2.1    | java  | utility class               | Class to hold a collection of static, reusable, methods/constants;             |
| 2.1    | java  | `-Xlint`                    | `javac` flag to control warnings categories; `-Xlint` = all reccomended        |

!1 this is the recorded order of the essential default lifecycle phases [source](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
!2 I know this isn't completely true, Surefire includes by default any files from any subdirectory that start or end with `Test` end with `TestCase` or end with `Tests`. 
!3 This is not a very useful source, but used it to help correctly define packaging in the scope of Maven [source](https://www.baeldung.com/maven-packaging-types) 
!4 I don't exactly understand the scope here. Since talking about the Aggregator Artifact could be talking about how you import a specific one in your `pom.xml`, it could also be talking about how to utilize them in test classes. Went with the latter as it makes more sense with lesson and the imports we used. 

## Capstone gap counts

| Chapter | Gaps found at Gate 3 | Notes |
|---------|----------------------|-------|
