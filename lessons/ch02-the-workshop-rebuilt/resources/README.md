# Chapter 2 fixtures

Files here exist to be copied into `src/`, run against the build, read, and deleted. None of them is production code, none is a model for code you are about to write, and none should ever appear in a commit alongside `src/`.

**The standing rule:** a fixture in the working tree makes `git status` dirty on purpose. Every lesson that uses one carries a `Homework — must be true` box saying the probe is out of the tree and in no commit. That box is the cleanup.

**A probe is for one goal, not for the build.** Several of these import types they never use, or types that are not on the compile classpath at all, because the thing being observed is what Checkstyle says about an import rather than whether it links. Run `mvn -B checkstyle:check` with a probe in the tree; do not run `mvn verify`.

## Lesson 2.2 — the formatter and the linter

| File                            | Feeds                                                      |
|---------------------------------|------------------------------------------------------------|
| `l02/StyleViolations.java`      | the module table — one violation per Checkstyle module     |
| `l02/ImportOrderTest.java`      | the import-group check, with every group populated at once |
| `l02/testpackage/TestEnum.java` | the symbol `ImportOrderTest.java` has to resolve           |

## Lesson 2.3 — the package boundary

`l03/main/` and `l03/test/` mirror `src/main/java` and `src/test/java`, so copying a probe in is a copy rather than a decision about where it goes. `l02/` has no such split — 2.2's fixtures all went to one place.

| File                      | Matrix rows      | Observation it exists for                                        |
|---------------------------|------------------|------------------------------------------------------------------|
| `EngineProbe.java`        | 1, 3, 4, 5, 6, 7, 10 | which filesystem and network APIs the rule actually names    |
| `ToolsProbe.java`         | 2                | the legal direction, which is the box a broken rule fails first  |
| `ModelProbe.java`         | 8, 9             | a package that is neither the engine root nor `tools`            |
| `FullyQualifiedProbe.java`| 12               | a reference with no import line — the rule's blind spot          |
| `StreamImportProbe.java`  | 11               | the test tree, and the row Lesson 2.4 depends on                 |

## Lesson 2.4 — the subprocess harness

| File                | Feeds                                                                           |
|---------------------|---------------------------------------------------------------------------------|
| `HarnessProbe.java` | all nine matrix rows: one argument, one behaviour, and no imports at all        |

`HarnessProbe.java` is deliberately dumber than the harness that drives it. It has no imports, so it takes no position on where your harness is allowed to live, and it decides nothing about timeouts, charsets, or stream order — those are the lesson.
