# Chapter 2 fixtures

Fixtures here exist to be copied into `src/`, run against the build, read, and deleted. None of them is production code, none is a model for code you are about to write, and none should ever appear in a commit alongside `src/`.

Not everything under `resources/` is a fixture. `l03/` also holds dated reference notes, which are tracked, are copied nowhere, and follow none of the rules below. The rest of this file is about fixtures; the notes have their own section.

**The standing rule:** a fixture in the working tree makes `git status` dirty on purpose. Every lesson that uses one carries a `Must be true` box saying the fixture is out of the tree and in no commit. That box is the cleanup.

**A fixture is for one goal, not for the build.** Several of these import types they never use, or types that are not on the compile classpath at all, because the thing being observed is what a tool says about the file rather than whether it links. Run `mvn -B checkstyle:check` with a fixture in the tree; do not run `mvn verify`.

**What a fixture is not for.** A fixture is worth writing when the observation cannot come from real code — a rejected import cannot live in the tree, because a file carrying one fails the build by definition. Everything a passing build already demonstrates needs no fixture at all. Lesson 2.3 originally shipped five of them to probe its own `import-control.xml`; they were deleted when the lesson was rebuilt around production code that exercises the same rules on its way to doing something useful.

## Lesson 2.2 — the formatter and the linter

| File                            | Feeds                                                      |
|---------------------------------|------------------------------------------------------------|
| `l02/StyleViolations.java`      | the module table — one violation per Checkstyle module     |
| `l02/ImportOrderTest.java`      | the import-group check, with every group populated at once |
| `l02/testpackage/TestEnum.java` | the symbol `ImportOrderTest.java` has to resolve           |

## Lesson 2.3 — reference notes

Not fixtures. These are dated notes written during the lesson and kept afterwards, so a later sitting does not re-derive what an earlier one already worked out. They are learner references rather than authority: `../../TESTING_STANDARDS.md` owns the test rubric and `.agents/PROJECT.md` owns the architecture, and a note that disagrees with either is the note that is out of date.

| File                                        | Covers                                                                            |
|---------------------------------------------|-----------------------------------------------------------------------------------|
| `l03/2026-09-12-single-type-test-shapes.md` | a complete test file for an enum, a record, a class, and an interface             |
| `l03/2026-09-13-model-to-runnable.md`       | the four decisions between a finished `model` and a program that prints and saves |

Both use invented domains for every code example. A note that worked through the lesson's own classes would be the solution with a date on it.

## Lesson 2.4 — the subprocess harness

| File                    | Feeds                                              |
|-------------------------|----------------------------------------------------|
| `l04/HarnessProbe.java` | one argument, one behaviour, and no imports at all |

`HarnessProbe.java` is deliberately dumber than the harness that drives it. It has no imports, so it takes no position on where your harness is allowed to live, and it decides nothing about timeouts, charsets, or stream order — those are the lesson.
