# 2.2 — The quality gates, from memory

*Review — Chapter 1, Lesson 8.*

Three plugins that disagree about what they are allowed to touch. Checkstyle reads and complains. Spotless rewrites, but only when you ask it to. JaCoCo watches the tests run and reports what they never reached. Wire all three into the POM you just wrote, and understand the split before you type it — the split is the lesson, the XML is not.

Lesson 2.1 left you a POM where nothing is bound to a phase by hand. That was never asked for, so it is not there. All three of these plugins need it.

## Must be true when you're done

- [x] `mvn validate` alone fails on a style violation before anything is compiled
- [x] `mvn spotless:check` fails on a misformatted file, and `mvn spotless:apply` fixes it — a plugin with nothing to format reports `BUILD SUCCESS` on a file it never opened, so the proof is a run you watched go red and then green, not a version number in the POM
- [x] Spotless runs inside `mvn -B verify` and can fail it, in the mode that reports rather than the mode that rewrites — a declared plugin that no phase invokes is not a gate
- [x] `mvn spotless:apply` followed by `mvn validate` comes back green — the formatter and the linter agree about imports, and you can say in one sentence which of the two you moved and why it was that one **[!] - Spotless does resolve all import issues as defined by `checkstyle.xml`. it doesn't however come back completely "green" when trying to run `mvn checkstyle:check` on [StyleViolations.java](resources/l02/StyleViolations.java)**
- [x] a coverage report on disk that names `Main` and the percentage its tests reached
- [x] every plugin version is set in one place, so changing one means editing one line
- [x] the compiler's exception above is a decision you can state in one sentence, recorded in the journal, rather than an aside — it is Lesson 2.1's `two version-pinning idioms, not one`, and this box is where it settles **[!!] - Decided to define the maven compiler version in the properties block as `3.13.0` for a commonly used version (`3.16.0` is the newest, but most downloaded is `3.13.0` for current-ish versions)**
- [x] `pom.xml` still does everything Lesson 2.1 made it do

## Done when

- [x] `mvn -B verify` is green and prints no `[WARNING]` you cannot explain
- [x] `mvn test` still reports `Tests run: 1` with failures, errors, and skips at 0
- [x] every `Must be true` box above was closed by a command you ran and read, not by a block you can see in the POM 
- [x] `git status` is clean and the commit has landed
- [x] the twelve `2.2` rows in the journal's `Definitions` table have definitions in them

## Reach for

`checkstyle.xml` is already in the repository root, copied from Chapter 1, 31 modules you extend rather than rebuild. For JaCoCo, the `prepare-agent` goal has to run before the tests do; work out why from what the agent has to attach to.

Surefire ran in Lesson 2.1 without you binding it to anything. None of these three will, and `mvn help:effective-pom` shows you the difference.

Your POM already contains both `<properties>` and `<pluginManagement>`, doing this job for exactly one plugin. The compiler is pinned the other way.

Spotless is the one of the three that will not tell you it is idle. Checkstyle names the file it read, and JaCoCo writes a report you can open; Spotless with nothing configured prints one line about an index and exits zero. Decide what set of files it owns before you decide what it does to them, and check the journal row you already opened for `google-java-format`.

A green build is not a correct POM — Lesson 2.1 ended with one that passed while carrying a scope Maven does not recognize. Read the warnings.

Spotless has no configuration file of its own. The only files Spotless reads: an Eclipse formatter profile, an import-order file, a license header. The reference: `mvn help:describe -Dplugin=spotless -Ddetail`.

Sort the modules in `checkstyle.xml` into three piles: 
  - (`F`) - Formatter can satisfy by rewriting the file
  - (`N`) - Ones that name something no formatter will ever invent for you — a variable's name, a missing `hashCode`, a `case` that falls through.
  - (`W`) - Ones that spotless formats to something checkstyle doesn't like
The second pile is the reason Checkstyle stays after Spotless is configured. What you are aiming for is not equality but containment: Spotless output that Checkstyle never rejects. 

Google publishes both halves of this disagreement [google-java-format](https://github.com/google/google-java-format) states that its algorithm is deliberately not configurable. The Checkstyle configuration is `google_checks.xml`, shipped inside the Checkstyle JAR. `unzip -l` the copy already in your `~/.m2` to find it, `unzip -p` to read it, and compare its import block against the one you inherited. That comparison is the whole answer to this question, and reading it is the exercise. Resolving it the other way is also legitimate: the plugin README lists a step that runs after the formatter and can regroup what it emits. Pick one deliberately and record which.

While you are in `checkstyle.xml`, read the package named in `specialImportsRegExp` and compare it against the package this repository actually builds. The file arrived from Chapter 1 unchanged.

Box three says a declared plugin that no phase invokes is not a gate. There is a quieter version of the same failure: a plugin bound to a phase where something else fails first, so the gate is never reached. Check which phase your Spotless execution names, what else is already bound there, and which of the two Maven runs first — `mvn help:effective-pom` and a run against a file that does not compile will each tell you from a different direction.

## Pop quiz
1. Why does the whole build fail when Checkstyle finds a violation during `mvn test`, when Checkstyle never looks at a test result?
   - Because the phase it runs at is in `validate`, which comes before the `test` phase; thereby failing before a test is run (86%)
2. Checkstyle and Spotless both have opinions about formatting. What does each one do that the other cannot, and why keep both?
   - Checkstyle does the linting; and can fail the build if something is out of place. Spotless is a formatter only. (79%)
3. Why is Spotless not bound to a phase that runs during `mvn verify` in rewrite mode? What would break if it were?
   - If it were bound to `mvn verify` it would cause issues due to it changing code AFTER all reports/tests have been made; which may break the previous linting that `checkstyle` did (71%)
4. JaCoCo reports 100% coverage on a class. Name two bugs that number does not rule out. 
   - If the class has incorrect logic, it doesn't matter that your code is 100% covered because it is falsely passing due to buggy/incorrect tests. The other in the same realm is if you are writing "transparent" or useless tests that just test to see IF a code path is fired, not how/why/accuracy of the code.
5. What is the difference between `validate` and `verify`, and roughly what has already happened by the time each one runs? (70%)
  - `validate` is an internal check to ensure that all other phases can run (maven internal check), while `verify` is a step done after all others to verify and produce reports about the process that was completed. (90%)
6. Surefire ran in Lesson 2.1 without you binding it to a phase; Checkstyle will not. Name both places a goal-to-phase binding can come from.
  - A) defaults, some goals are bound to phases automatically by the source settings, B) dependencies that rely on a specific plugin doing a goal at a specific phase. (80%)
  - [x] (clarify) from `mvn help:effective-pom` alone, list every goal that will run during `mvn verify` in order, and say for each whether its phase came from the packaging's own lifecycle or from a line you typed — then name the plugin that is in the file but not in that list
    - `mvn spotless:check` - validate - me
    - `mvn checkstyle:check` - validate - me
    - `mvn jacoco:prepare-agent` - initialize - default
    - `mvn compiler:compile` - compile - default
    - `mvn resources:testResources` - process-test-resources - default
    - `mvn compiler:testCompile` - test-compile - default
    - `mvn surefire:test` - test - default
    - `mvn jar:jar` - package - default
    - `mvn jacoco:report` - post-integration-test - me
    - `mvn jacoco:check` - verify - goal by me; phase binding by default

## Homework — what each tool detects, and what only one of them fixes

- Copy [`StyleViolations.java`](resources/l02/StyleViolations.java) to `src/main/.../griddlers/` (alongside `Main.java`) after every iteration
- Ensure clean copies of `before.txt` and `after.txt` exist before each iteration (in repo root dir)
- each iteration is `mvn checkstyle:check > before.txt`, then `mvn spotless:apply` -> `mvn checkstyle:check > after.txt`

### 1. Sort the modules — there are three piles, not two

There are three piles: 
  1. (`F`) What a formatter can rewrite
  2. (`N`) What names something no formatter will invent
  3. (`W`) Anything Spotless left in a state Checkstyle likes *less* than what it started with.
In `Step`, name the specific Spotless step that did the work — `googleJavaFormat`, `removeUnusedImports`, `expandWildcardImports`, `importOrder` — or, for `N`, one clause on what the tool would have to understand to fix it.

#### Violations from resources/StyleViolations.java

| Module                         | Pile | Step, or why none can             | Lines affected in `StyleViolations.java`                         |
|--------------------------------|------|-----------------------------------|------------------------------------------------------------------|
| `LineLength`                   | F    | `<googleJavaFormat/>`             | 2 -> 0                                                           |
| `ConstantName`                 | N    | Formatter shouldn't refactor code | 14:28 Name `BadConstant` must match pattern...                   |
| `TypeName`                     | N    | Formatter shouldn't refactor code | 11:7 Name `bad_style` must match pattern...                      |
| `MethodName`                   | N    | Formatter shouldn't refactor code | 17:8 Name `BadMethod` must match pattern...                      |
| `MemberName`                   | N    | Formatter shouldn't refactor code | 13:15 Name `BadMember` must match pattern...                     |
| `ParameterName`                | N    | Formatter shouldn't refactor code | 17:22 Name `BadParameter` must match pattern...                  |
| `LocalVariableName`            | N    | Formatter shouldn't refactor code | 19:9 Name `BadLocal` must match pattern...                       |
| `AvoidStarImport`              | F    | `<expandWildcardImports/>`        | 1 -> 0                                                           |
| `RedundantImport`              | F    | `<removeUnusedImports/>`          | 2 -> 0                                                           |
| `UnusedImports`                | F    | `<removeUnusedImports/>`          | 2 -> 0                                                           |
| `CustomImportOrder`            | F    | `<importOrder/>`                  | 5 -> 0                                                           |
| `Indentation`                  | F    | `<googleJavaFormat/>`             | 35 -> 0                                                          |
| `NeedBraces`                   | N    | Formatter shouldn't refactor code | 32:5 `if` construct must use `{}`s                               |
| `LeftCurly`                    | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `RightCurly`                   | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `EmptyBlock`                   | N    | Formatter shouldn't refactor code | 35:20 Must have at least one statement                           |
| `WhitespaceAfter`              | F    | `<googleJavaFormat/>`             | (2 -> 0)                                                         |
| `WhitespaceAround`             | W    | `<googleJavaFormat/>`             | 6 -> 2 these are non-format/style errors // 35:20, 35:21 !1      |
| `GenericWhitespace`            | F    | `<googleJavaFormat/>`             | 2 -> 0                                                           |
| `MethodParamPad`               | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `NoWhitespaceAfter`            | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `NoWhitespaceBefore`           | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `OneStatementPerLine`          | F    | `<googleJavaFormat/>`             | 1 -> 0                                                           |
| `MultipleVariableDeclarations` | N    | Requrires refactoring             | 19:5 Each variable declaration must be in its...                 |
| `EmptyCatchBlock` `EmptyBlock` | N    | Should be refactored, not format  | 35:20 Must have at least one statement. 47:40 Empty catch block. |
| `FallThrough`                  | N    | Would have to refactor            | 54:7 Fall through from previous branch of switch statement       |
| `EqualsHashCode`               | N    | Would have to refactor            | 67:3 Definition of `equals()` without def of `hashCode()`        |
| `HideUtilityClassConstructor`  | N    | Would have to refactor            | 74:1 Utility classes should not have a public constructor        |

!1 - The remaining two are resolved after completely removing the empty `if (count > 0) {}` block as there should NEVER be an empty block and formatting shouldn't deal with it.

### 2. The count is not the score

`diff before.txt after.txt` and answer in the journal:

- Of the 16 that remain, how many appear in the list of 76? Do the subtraction and say what the missing ones have in common.
- Which violations appear only in `after.txt`? Name the module, the line, and the exact rewrite that produced them.
- One construct is responsible for all of them. Write out what it looked like before formatting and after, in two lines.

Then the judgement call, which is the actual exercise: that same line is *already* illegal under a different module, and would be whether or not Spotless ever touched it. So decide whether what you found is a defect in the pairing of these two tools, or an artefact of a probe file that is broken in more ways than real code ever is. The answer decides whether you change the POM, change `checkstyle.xml`, or change neither and write down why. `google_checks.xml` has an opinion about this exact construct in its `WhitespaceAround` message text — read it before you decide.

### 3. The module that will not agree (ARCHIVED; MOVED GOAL)

### 4. What Spotless is still leaving on the table (ARCHIVED; MOVED GOAL)

### 5. Which gate fires first (ARCHIVED; MOVED GOAL)

### 6. The files each tool declines to read (ARCHIVED; MOVED GOAL)

## Homework — must be true when you're done

- [x] the module table has a pile letter in every row and no blank cells
- [x] you can name, without looking it up, the one construct where Spotless left Checkstyle angrier than it found it, and say whether you decided to act on it
  - **[!] Checkstyle didn't like how Spotless formatted on the `[WhitespaceAround]` construct. The two from `StyleViolations.java` really shouldn't ever happen. It only happened because there was an empty if statement/block; which is bad programming. Decided this is a manual fix to deal with.**
- [x] the import disagreement is resolved in exactly one direction, on purpose, and the journal names which tool moved and why it was that one — this is `Must be true` box 4, and this task is what closes it
- [x] `mvn spotless:apply` followed by `mvn validate` comes back green on a file that imports from every group your `customImportOrderRules` names, not just on a file that imports from one
  - **[!?] - Wrote two files to the [resources](resources/) (testpackage.TestEnum.java, ImportOrderTest.java) to run and verify the import order reorganization. Now have working/matching import ordering as checkstyle expects**
  - [x] (clarify) run the iteration again against the POM as committed, and read the exit status of `mvn spotless:apply` before you read anything it printed — one fixture names a symbol that step has to resolve, and where it looks for it is the observation
- [x] the phase table has all three runs in it, and you can state the ordering rule that decides run `a` from memory
- [x] `StyleViolations.java`, `testpackage.TestEnum.java`, and `ImportOrderTest.java` are out of the working tree and appears in no commit
- [x] what remains is a written list, and every line on it names something a formatter could not invent — a name, a missing method, a branch that falls through
