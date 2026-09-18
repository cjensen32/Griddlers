# Chapter 2 fixtures

Fixtures here exist to be copied into `src/`, run against the build, read, and deleted. None of them is production code, none is a model for code you are about to write, and none should ever appear in a commit alongside `src/`.

Not everything under `resources/` is a fixture. Two reference files sit beside this one, and `l03/` holds a third; all three are tracked, are copied nowhere, and follow none of the rules below. The rest of this file is about fixtures; the reference files have their own sections at the end.

**The standing rule:** a fixture in the working tree makes `git status` dirty on purpose. Every lesson that uses one carries a `Must be true` box saying the fixture is out of the tree and in no commit. That box is the cleanup.

**A fixture is for one goal, not for the build.** Several of these import types they never use, or types that are not on the compile classpath at all, because the thing being observed is what a tool says about the file rather than whether it links. Run `mvn -B checkstyle:check` with a fixture in the tree; do not run `mvn verify`.

**What a fixture is not for.** A fixture is worth writing when the observation cannot come from real code — a rejected import cannot live in the tree, because a file carrying one fails the build by definition. Everything a passing build already demonstrates needs no fixture at all. Lesson 2.3 originally shipped five of them to probe its own `import-control.xml`; they were deleted when the lesson was rebuilt around production code that exercises the same rules on its way to doing something useful.

## Lesson 2.2 — the formatter and the linter

| File                            | Feeds                                                      |
|---------------------------------|------------------------------------------------------------|
| `l02/StyleViolations.java`      | the module table — one violation per Checkstyle module     |
| `l02/ImportOrderTest.java`      | the import-group check, with every group populated at once |
| `l02/testpackage/TestEnum.java` | the symbol `ImportOrderTest.java` has to resolve           |

## Lesson 2.3 — the standing guide

Not a fixture. [`l03/the-refit-handoff.md`](l03/the-refit-handoff.md) is the one file a 2.3 sitting opens: where the checkout stands, which ticked boxes are no longer true, what is left in each production file, which close-out questions are still open, and the commit split that closes the lesson.

It is a learner reference rather than authority — `../../TESTING_STANDARDS.md` owns the test rubric, `.agents/PROJECT.md` owns the architecture, and `../03-the-three-packages.md` owns the assignment. It carries no Griddlers code at all. A note that worked through the lesson's own classes would be the solution with a date on it.

It replaces four dated notes written on 2026-09-12, -13, -14 and -16, which were consolidated on 2026-09-18 once the checkout had overtaken them. `git log` holds them.

## Reference — not tied to a lesson

These two outlive Chapter 2 and are the reason the dated notes could be retired. Both use invented domains for every code example.

| File                                 | Reach for it when                                                                  |
|--------------------------------------|------------------------------------------------------------------------------------|
| [`test-shapes.md`](test-shapes.md)   | writing a test file, and you want to know what a finished one looks like for an enum, a record, a class, or an interface |
| [`changing-code.md`](changing-code.md) | the code already works and you are moving it, or a change has gone red and you are working out why |

## Lesson 2.4 — the subprocess harness

| File                    | Feeds                                              |
|-------------------------|----------------------------------------------------|
| `l04/HarnessProbe.java` | one argument, one behaviour, and no imports at all |

`HarnessProbe.java` is deliberately dumber than the harness that drives it. It has no imports, so it takes no position on where your harness is allowed to live, and it decides nothing about timeouts, charsets, or stream order — those are the lesson.
