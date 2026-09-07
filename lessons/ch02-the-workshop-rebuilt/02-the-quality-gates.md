# 2.2 — The quality gates, from memory

*Review — Chapter 1, Lesson 8.*

Three plugins that disagree about what they are allowed to touch. Checkstyle reads and complains. Spotless rewrites, but only when you ask it to. JaCoCo watches the tests run and reports what they never reached. Wire all three into the POM you just wrote, and understand the split before you type it — the split is the lesson, the XML is not.

Lesson 2.1 left you a POM where nothing is bound to a phase by hand. That was never asked for, so it is not there. All three of these plugins need it.

## Must be true when you're done

- [x] `mvn validate` alone fails on a style violation, before anything is compiled
- [ ] `mvn spotless:check` fails on a misformatted file, and `mvn spotless:apply` fixes it — a plugin with nothing to format reports `BUILD SUCCESS` on a file it never opened, so the proof is a run you watched go red and then green, not a version number in the POM
- [ ] Spotless runs inside `mvn -B verify` and can fail it, in the mode that reports rather than the mode that rewrites — a declared plugin that no phase invokes is not a gate
- [ ] `mvn spotless:apply` followed by `mvn validate` comes back green — the formatter and the linter agree about imports, and you can say in one sentence which of the two you moved and why it was that one
- [x] a coverage report on disk that names `Main` and the percentage its tests reached
- [x] every plugin version is set in one place, so changing one means editing one line **[!?] – Aside from the maven compiler plugin, which isn't as ephemeral as the others **
- [ ] the compiler's exception above is a decision you can state in one sentence, recorded in the journal, rather than an aside — it is Lesson 2.1's `two version-pinning idioms, not one`, and this box is where it settles
- [x] `pom.xml` still does everything Lesson 2.1 made it do

## Done when

- [x] `mvn -B verify` is green and prints no `[WARNING]` you cannot explain
- [x] `mvn test` still reports `Tests run: 1` with failures, errors, and skips at 0
- [?] `mvn help:effective-pom` read once, and the `<packaging>` value you never typed recorded in the journal
- [ ] every `Must be true` box above was closed by a command you ran and read, not by a block you can see in the POM
- [x] `git status` is clean and the commit has landed
- [ ] the twelve `2.2` rows in the journal's `Definitions` table have definitions in them

## Reach for

`checkstyle.xml` is already in the repository root, copied from Chapter 1 — 31 modules you extend rather than rebuild. For JaCoCo, the `prepare-agent` goal has to run before the tests do; work out why from what the agent has to attach to.

Surefire ran in Lesson 2.1 without you binding it to anything. None of these three will, and `mvn help:effective-pom` shows you the difference.

Your POM already contains both `<properties>` and `<pluginManagement>`, doing this job for exactly one plugin. The compiler is pinned the other way.

Spotless is the one of the three that will not tell you it is idle. Checkstyle names the file it read and JaCoCo writes a report you can open; Spotless with nothing configured prints one line about an index and exits zero. Decide what set of files it owns before you decide what it does to them, and check the journal row you already opened for `google-java-format`.

A green build is not a correct POM — Lesson 2.1 ended with one that passed while carrying a scope Maven does not recognise. Read the warnings.

Spotless has no configuration file of its own. There is no `spotless.xml` to write and no equivalent of `checkstyle.xml` to point at — every step lives in the plugin's own `<configuration>` block, and the only files Spotless reads off disk belong to one specific step rather than to Spotless itself: an Eclipse formatter profile, an import-order file, a licence header. The reference for every available step and its options is the plugin's README, [spotless-maven-plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven). The same list is available offline, from the descriptor inside the JAR you have already downloaded: `mvn help:describe` with `-Dplugin=com.diffplug.spotless:spotless-maven-plugin -Ddetail`.

So the block goes in the POM. What cannot go in it is a 1:1 copy of `checkstyle.xml`, and pop quiz question 2 is where you already wrote down why. Sort the modules in that file into two piles: the ones a formatter can satisfy by rewriting the file, and the ones that name something no formatter will ever invent for you — a variable's name, a missing `hashCode`, a `case` that falls through. The second pile is the reason Checkstyle stays after Spotless is configured. What you are aiming for is not equality but containment: Spotless output that Checkstyle never rejects. Every module is indexed at [Checkstyle checks](https://checkstyle.org/checks.html), and the import rules specifically at [config_imports](https://checkstyle.org/config_imports.html).

One module in the first pile will not agree with google-java-format as configured, and imports are where to look. Group a file's imports by hand so `mvn validate` passes, run `mvn spotless:apply` on it, then run `mvn validate` again and read what comes back. The properties in play are documented on [CustomImportOrder](https://checkstyle.org/checks/imports/customimportorder.html) — pay attention to which of them have defaults you never wrote down.

Google publishes both halves of this disagreement, which makes it findable rather than guessable. [google-java-format](https://github.com/google/google-java-format) states that its algorithm is deliberately not configurable, so when the two tools disagree only one of them is able to move. The Checkstyle configuration Google ships to match that formatter is `google_checks.xml`, described at [Google's Style](https://checkstyle.org/google_style.html) and shipped inside the Checkstyle JAR rather than published as a file you download — `unzip -l` the copy already in your `~/.m2` to find it, `unzip -p` to read it, and compare its import block against the one you inherited. That comparison is the whole answer to this question, and reading it is the exercise. Resolving it the other way is also legitimate: the plugin README lists a step that runs after the formatter and can regroup what it emits. Pick one deliberately and record which.

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
  - [ ] (clarify) from `mvn help:effective-pom` alone, list every goal that will run during `mvn verify` in order, and say for each whether its phase came from the packaging's own lifecycle or from a line you typed — then name the plugin that is in the file but not in that list

## Homework — what each tool detects, and what only one of them fixes

`75` violations before `mvn spotless:apply` and `16` after is a headline, not a result. It does not say which modules the formatter satisfied, which ones it could never satisfy, whether any of the 16 are new, or whether the 59 that vanished were the ones you cared about. This section turns that one number into the two piles the lesson body asked for, and then into a POM decision you can defend.

Everything below runs on `StyleViolations.java`. It lives under `src/main/java`, which is the only reason every tool in the build already reads it, and is also the reason it must not reach a commit — a deliberately broken file in `main` is a gate that fails for everyone forever. Decide before you start how you are keeping it out: a scratch copy outside the repository, an ignore entry, or a deletion at the end of every session. Pick one and hold to it.

Two habits make the rest of this possible. Work from a fresh copy every time, because `spotless:apply` is destructive and you cannot re-run a comparison against a file you already rewrote. And redirect both audits to files — `mvn checkstyle:checkstyle > before.txt`, apply, `> after.txt` — because several tasks below are a `diff` and you cannot diff what scrolled past.

### 1. Sort the modules — there are three piles, not two

The lesson body asked for two piles: what a formatter can rewrite, and what names something no formatter will invent. Run the probe honestly and you will need a third, for anything Spotless left in a state Checkstyle likes *less* than what it started with.

One row per module in `checkstyle.xml`. In `Pile`, write `F` if `spotless:apply` fixed it, `N` if nothing a formatter does could, and `W` if the violation exists only after formatting. In `Step`, name the specific Spotless step that did the work — `googleJavaFormat`, `removeUnusedImports`, `expandWildcardImports`, `importOrder` — or, for `N`, one clause on what the tool would have to understand to fix it.

| Module | Pile | Step, or why none can | Line before → after |
| --- | --- | --- | --- |
| `LineLength` | | | |
| `PackageName` | | | |
| `TypeName` | | | |
| `MethodName` | | | |
| `MemberName` | | | |
| `ParameterName` | | | |
| `LocalVariableName` | | | |
| `ConstantName` | | | |
| `AvoidStarImport` | | | |
| `RedundantImport` | | | |
| `UnusedImports` | | | |
| `CustomImportOrder` | | | |
| `Indentation` | | | |
| `NeedBraces` | | | |
| `LeftCurly` | | | |
| `RightCurly` | | | |
| `EmptyBlock` | | | |
| `WhitespaceAfter` | | | |
| `WhitespaceAround` | | | |
| `GenericWhitespace` | | | |
| `MethodParamPad` | | | |
| `NoWhitespaceAfter` | | | |
| `NoWhitespaceBefore` | | | |
| `OneStatementPerLine` | | | |
| `MultipleVariableDeclarations` | | | |
| `EmptyCatchBlock` | | | |
| `FallThrough` | | | |
| `EqualsHashCode` | | | |
| `HideUtilityClassConstructor` | | | |

Two rows will tempt you to write `F` and are not: one where Spotless changed the line and Checkstyle still rejects it, and one where Spotless changed the line and Checkstyle rejects it for a reason that was not there before. Both are worth more than the twenty rows that behaved.

### 2. The count is not the score

`diff before.txt after.txt` and answer in the journal:

- Of the 16 that remain, how many appear in the list of 75? Do the subtraction and say what the missing ones have in common.
- Which violations appear only in `after.txt`? Name the module, the line, and the exact rewrite that produced them.
- One construct is responsible for all of them. Write out what it looked like before formatting and after, in two lines.

Then the judgement call, which is the actual exercise: that same line is *already* illegal under a different module, and would be whether or not Spotless ever touched it. So decide whether what you found is a defect in the pairing of these two tools, or an artefact of a probe file that is broken in more ways than real code ever is. The answer decides whether you change the POM, change `checkstyle.xml`, or change neither and write down why. `google_checks.xml` has an opinion about this exact construct in its `WhitespaceAround` message text — read it before you decide.

### 3. The module that will not agree

The lesson body said one module in pile 1 will not agree with google-java-format, and that imports are where to look. `StyleViolations.java` is too easy a case to show it: it imports nothing but `java.*`, so both tools happen to agree by accident. Build a probe that removes the accident.

Write one file that imports, in some order, a static member, something from `java.*`, something from `javax.*`, a third-party class, and a class from this repository's own package. Group and sort it by hand until `mvn validate` passes. Run `mvn spotless:apply`. Run `mvn validate` again.

- Record every message that comes back, verbatim. There is more than one, and they are not all the same complaint.
- Count the groups Spotless emitted, separated by blank lines. Count the groups `customImportOrderRules` in `checkstyle.xml` demands. Write both numbers down before you read further.
- `<importOrder/>` with nothing inside it is not "no opinion" — it is a specific default. Find it in the plugin README and write out the order string it stands for.
- Now `unzip -p` `google_checks.xml` out of the Checkstyle JAR in `~/.m2` and read its `CustomImportOrder` block. Its rule string is shorter than yours. Say why it is shorter, and what that tells you about which of your two tools was designed to agree with the other.
- Resolve it in one direction, deliberately, per the lesson body: either give `<importOrder>` an explicit order that reproduces what `CustomImportOrder` wants, or move `CustomImportOrder` to the rule Google ships for this formatter. Record the choice and one sentence of why in the journal. Both are defensible; an unrecorded choice is not.

While you are there, finish the `specialImportsRegExp` reading the lesson body sent you to do. Predict first, then measure: if you correct that regexp and change nothing else, does the number of import failures on your probe go up or down? Say why before you run it.

### 4. What Spotless is still leaving on the table

`mvn help:describe -Dplugin=com.diffplug.spotless:spotless-maven-plugin -Ddetail` and the plugin README both list more than you configured.

- The `googleJavaFormat` step takes three sub-options you have not set. Name all three and say what each one is for.
- One of them retires a violation of a pile-1 module that currently survives formatting. Find it by construction: write a file with one string literal longer than 100 characters and one arithmetic expression longer than 100 characters, and run the gate with the option and without it. Which of the two does the formatter already wrap unasked? Which needs the option? Then answer the interesting part — why the formatter is willing to break one of them by default and not the other.
- Two steps in the `<java>` block do the same job from opposite directions: one rewrites the problem away, one refuses to build. Your POM already carries a comment about swapping them. Say what changes about the *gate* when you do, not just about the file — who finds out, and when.
- One value in the Spotless block is a pinned version that is not a plugin version and is not in `<properties>`. `Must be true` box 6 says versions live in one place. Decide whether that box covers this one, and record the decision either way.

### 5. Which gate fires first

`Reach for` warns that a plugin bound to a phase where something else fails first is not a gate. Measure it rather than reasoning about it. Three runs of `mvn compile`, each on a different file, recording the order of the `--- plugin:goal (execution-id) ---` lines:

| Run | File under test | Goals that ran, in order | Did `spotless:check` run? |
| --- | --- | --- | --- |
| a | misformatted, zero Checkstyle violations | | |
| b | misformatted **and** has a Checkstyle violation | | |
| c | does not compile | | |

Then: what phase does your Spotless execution name, what else is already bound to that phase, and which of the two does Maven run first? Note that the answer to that last one is not in `checkstyle.xml` or in any phase name — it comes from somewhere else in the POM, and `mvn help:effective-pom` shows you where. Say what you would have to change, and in what order, for `spotless:check` to be the first thing a developer hears about. Then say whether it should be, given that only one of these three tools can fix what it finds.

### 6. The files each tool declines to read

Look back at the `git diff` from the very first `spotless:apply` you ran. It reformatted a file that no `mvn checkstyle:checkstyle` run has ever mentioned.

- Name the file, and say how you can tell from the diff alone that Checkstyle never read it.
- Find the Checkstyle parameter that decides this, and its default, with `mvn help:describe -Dplugin=org.apache.maven.plugins:maven-checkstyle-plugin -Dgoal=check -Ddetail`.
- Find the Spotless equivalent — the default `<includes>` of the `<java>` format — in the README.
- The two defaults disagree. Decide whether they should, and record it. A test that Spotless formats and Checkstyle never inspects is a file with one gate on it instead of two, and Chapter 2 has already asked you to hold tests to a standard.

## Homework — must be true when you're done

- [ ] the module table has a pile letter in every row and no blank cells
- [ ] you can name, without looking it up, the one construct where Spotless left Checkstyle angrier than it found it, and say whether you decided to act on it
- [ ] the import disagreement is resolved in exactly one direction, on purpose, and the journal names which tool moved and why it was that one — this is `Must be true` box 4, and this task is what closes it
- [ ] `mvn spotless:apply` followed by `mvn validate` comes back green on a file that imports from every group your `customImportOrderRules` names, not just on a file that imports from one
- [ ] the phase table has all three runs in it, and you can state the ordering rule that decides run `a` from memory
- [ ] `StyleViolations.java` is out of the working tree and appears in no commit
- [ ] what remains is a written list, and every line on it names something a formatter could not invent — a name, a missing method, a branch that falls through
