# 2.3 — The two packages

*Review — Chapter 1, Lessons 3 and 6 (dependency direction). New: the build enforces it.*

You have one package and one class in it. That is a boundary nobody can cross yet, which is the same as no boundary at all. This lesson creates the second package and then makes the build refuse the wrong direction — because a rule you keep by remembering it is a rule you break the week you stop remembering it.

The direction is fixed, and it is not yours to choose: `com.connorjensen.griddlers.tools` may depend on `com.connorjensen.griddlers`, never the reverse.

The engine also does no I/O — no files, no sockets, no `System.in`, no `System.out`. That is not tidiness. It is what makes the engine testable without a fixture, deterministic under a seed, and reusable from a terminal client and a browser client that have nothing else in common.

Lesson 2.2 handed over three things it could not settle, and adding classes to the tree is what makes all three yours. Checkstyle has still never read `src/test/java`. JaCoCo's `0.8` line rule has never fired, because the only class in the tree was the one line in the POM excludes. And the versions you pinned are the versions of the *plugins* — the analyzers inside them are pinned somewhere else, or not at all.

## Must be true when you're done

- [x] two packages exist under `src/main/java`: the engine at `com.connorjensen.griddlers`, and `com.connorjensen.griddlers.tools` beneath it, each holding at least one class you wrote for this lesson
- [x] the `tools` class calls into an engine class, so the dependency direction is a fact about compiled code rather than a claim in a document
- [x] no engine class reads a file, opens a socket, or touches `System.in`, `System.out`, or `System.err` — and no engine class behaves differently depending on which machine it is running on
- [x] an import of a `tools` type written inside an engine class fails `mvn validate`, and the output names the import it rejected
- [ ] that same import, written inside a `tools` class, passes
- [ ] the rule rejects the wrong direction and nothing else — a rule that refuses every import in the tree closes the box above by accident, and one that names a single package closes it only for the imports you thought of: it has to hold for packages you have not written yet, and for every API that reaches a file rather than the one package named after it
- [ ] whether the rule governs `src/test/java` is a line in `pom.xml` you wrote on purpose rather than a default you inherited, and you have read what the audit says about a test in the engine package that imports `java.io`
- [ ] `mvn -B verify` is green and still prints no `[WARNING]` you cannot explain

## Done when

- [ ] every production class you added has a tier 1 test mirroring its package, per `lessons/TESTING_STANDARDS.md`, and the `0.8` rule passes without the number moving
- [ ] `mvn test` reports the new total with failures, errors, and skips at 0
- [ ] the journal's `dependency scope` row is rewritten against what you saw here, and the `?3` mark comes off
- [ ] the twelve `2.3` rows in the journal's `Definitions` table have definitions in them
- [ ] `git status` is clean and the commit has landed

## Reach for

Checkstyle has exactly one module that reads a second configuration file instead of taking its rules inline. It describes packages as a tree and says, per subtree, what may be imported. Everything else in `checkstyle.xml` is a single element; this one is a pointer.

That tree is walked from the deepest declared node containing the file, upward, stopping at the first rule that matches — and if nothing matches anywhere, one attribute on the root decides. Find that attribute's name and its default, then work out what happens to a package you declared and a package you did not.

A blocklist says what is forbidden and permits the rest. An allowlist says what is permitted and forbids the rest. Read your file and say out loud which one you wrote, then ask the question this lesson opened with: which of the two survives the week you stop remembering?

The package is not the API. Count the ways Java reaches a filesystem, then count how many of them your rule names. `java.io` is the oldest of them and the only one whose package is spelled the way the constraint is.

Before you assume the rule covers everything, find out which directories Checkstyle actually reads. The plugin has separate settings for main sources and test sources, only one is on by default, and that one has no `-D` shortcut — so turning it on is an edit to `pom.xml`, not a flag on one run. It also decides whether the harness you build in 2.4 is governed at all.

A Checkstyle module that needs a path can get one from the plugin through `<propertyExpansion>`, and the module documentation names the property it wants. You did not need it: a bare relative path loads, because Checkstyle resolves it against the directory Maven started from. Run `mvn -B -f pom.xml checkstyle:check` from anywhere else and read the error. That is the whole argument for the property, one command long.

The rule living in its own file is what makes a third package an edit to that file and nothing else. You have a third package already; check whether adding it was one edit or none.

The failure message names the version of Checkstyle that read your rules, and it is not `${checkstyle.version}` — that number pins the Maven plugin, and the plugin ships an analyzer of its own. A plugin's own dependency is overridable in the plugin's `<dependencies>` block, the same idiom as the `<pluginManagement>` you already wrote, one level down.

Two gates are bound to `validate` now, and Maven runs executions in the order the POM declares them within a phase, so one decides whether the other ever runs. You wrote this warning into 2.2's last `Reach for` paragraph yourself. `mvn -B validate` against a tree with one misformatted file tells you which order you have.

The `0.8` rule from 2.2 is a `CLASS`-element rule with one exclusion, and it has never fired because the only class in the tree was the excluded one. Every class you add with no test is a class at `0.0`. Read those lines before deciding what to do about them, and notice that the build prints them as `[WARNING]` and then fails anyway — the severity in the log is not the severity in the exit code.

`.agents/CONTEXT.md` owns the direction and the no-I/O constraint. `lessons/TESTING_STANDARDS.md` owns the tier 1 rule and the naming.

Give the classes something small and real to do. A boundary demonstrated on `Foo` and `Bar` proves the build works and teaches you nothing about where the seam belongs; a `tools` class that formats what an engine class computes will still make sense in Chapter 9.

## Pop quiz

Answer these from memory, editor closed.

1. Why does `tools` depend on the engine and not the reverse? Name one concrete thing you lose the day someone inverts it.
2. Which classpath does `test` scope put JUnit on, and which one does it keep JUnit off? Answer without using the word "phase" — this is the row your journal still has marked `?3`.
3. Name two ways other than Checkstyle to enforce a package boundary in Java, and say what each one costs you.
4. Your boundary rule rejects a build. Did it reject it for `src/test/java` as well as `src/main/java`? What setting decided that, and which answer do you actually want here?
5. The engine does no I/O. Name two things that buys you in a test suite that an engine which merely *avoids* I/O by convention does not.
6. A `tools` class and an engine class both need the same constant. Where does it go? What happens to the boundary if you put it in the other place?

## Homework — what the rule rejects, and what it was never told about

- copy the probes from [`resources/l03/`](resources/l03/) into the matching packages under `src/`, one probe file per iteration, never two
- `git diff --stat` is empty before each iteration begins, so the only change in the tree is the probe
- capture every run — `mvn -B checkstyle:check > probe-NN.txt 2>&1` — and keep the files until the table is full
- run the first probe with `mvn -B validate` and every later one with `mvn -B checkstyle:check`, then write one line saying why the second command exists

### 1. The verdict matrix

Predict every row before you run any of it. A prediction written after the observation teaches nothing, and the rows where the two disagree are the only rows that matter.

| #  | The import                                                                       | Written in                  | Predicted | Observed | Which rule decided |
|----|----------------------------------------------------------------------------------|-----------------------------|-----------|----------|--------------------|
| 1  | `com.connorjensen.griddlers.tools.TerminalGriddler`                              | engine, `GriddlersEngine`   |           |          |                    |
| 2  | `com.connorjensen.griddlers.GriddlersEngine`                                     | `tools`, `TerminalGriddler` |           |          |                    |
| 3  | `java.io.File`                                                                   | engine, `GriddlersEngine`   |           |          |                    |
| 4  | `java.nio.file.Files`                                                            | engine, `GriddlersEngine`   |           |          |                    |
| 5  | `java.net.Socket`                                                                | engine, `GriddlersEngine`   |           |          |                    |
| 6  | `java.util.List`                                                                 | engine, `GriddlersEngine`   |           |          |                    |
| 7  | `com.connorjensen.griddlers.model.Griddler`                                      | engine, `GriddlersEngine`   |           |          |                    |
| 8  | `com.connorjensen.griddlers.tools.TerminalGriddler`                              | `model`, `Griddler`         |           |          |                    |
| 9  | `java.io.File`                                                                   | `model`, `Griddler`         |           |          |                    |
| 10 | `org.junit.jupiter.api.Test`                                                     | engine, `GriddlersEngine`   |           |          |                    |
| 11 | `java.io.InputStream`                                                            | test tree, engine package   |           |          |                    |
| 12 | none — `new com.connorjensen.griddlers.tools.TerminalGriddler()` written in full | engine, `GriddlersEngine`   |           |          |                    |

Rows 4 and 5 are the same constraint as row 3 in a different package. Rows 8 and 9 are the same two constraints again, in a package that is neither the engine root nor `tools`. Row 11's answer is decided by a box you closed in the `Must be true` list, and it is the row 2.4 depends on. Row 12 has no import line in it anywhere.

### 2. The two blind spots

Rows 8, 9 and 12 are the exercise, and they fail for two different reasons.

Row 12 fails because the module reads import statements, and a fully-qualified reference in a method body is not one. The engine compiles either way.

Rows 8 and 9 fail because the rule only governs what somebody told it about. Same for rows 4 and 5: the constraint in `.agents/CONTEXT.md` is about reaching a filesystem or a socket, and the rule is about one package that happens to be spelled the way the oldest of those APIs is spelled.

So make one decision, in one direction, and write it down: does your rule enumerate what is forbidden, or enumerate what is permitted? Work out what the other one costs before you pick — the cheap way to find out is to try it and count how many imports in the existing tree suddenly need declaring. Then say whether your build now enforces `CONTEXT.md`'s sentence or a weaker one, and if it is weaker, whether row 12 is a defect you are fixing or a limit you are keeping on purpose.

### 3. The classes with no test yet

`mvn verify` on the tree as it stands prints a `[WARNING]` per untested class and then fails. Read them, and note which classes they name.

Three ways to make it green — a tier 1 test that reaches the threshold, an exclusion, or a lower threshold — and they are not equally honest. Pick one per class. For anything you exclude, write the justifying sentence next to the `Main` exclusion from 2.2, because 2.4 re-opens that one and the two will be read together. A threshold you moved to fit code you had already written is a threshold that will never fail again.

## Homework — must be true when you're done

- [ ] every row of the matrix has a prediction written before its observation, and no blank cells
- [ ] every row where prediction and observation disagree carries a one-line note saying which of the two was wrong: your model of the rule, or the rule
- [ ] the blocklist-or-allowlist decision is made in one direction, on purpose, and the journal says what the other one would have cost
- [ ] the row 12 decision is written down, and it names either the fix or the limit
- [ ] every coverage decision is a test or a justified exclusion, and the `0.8` in the POM is still `0.8`
- [ ] `git status` shows no probe left in the tree, and no probe appears in any commit
- [ ] `mvn -B verify` is green, and the only imports in the repository it rejects are ones going the wrong way
