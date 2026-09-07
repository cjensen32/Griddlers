# 2.3 — The two packages

*Review — Chapter 1, Lessons 3 and 6 (dependency direction). New: the build enforces it.*

You have one package and one class in it. That is a boundary nobody can cross yet, which is the same as no boundary at all. This lesson creates the second package and then makes the build refuse the wrong direction — because a rule you keep by remembering it is a rule you break the week you stop remembering it.

The direction is fixed and it is not yours to choose: `com.connorjensen.griddlers.tools` may depend on `com.connorjensen.griddlers`, never the reverse. `.agents/CONTEXT.md` owns that sentence. Chapters 9 through 13 lean on it hard enough that discovering it was never enforced would cost you a week.

The engine also does no I/O — no files, no sockets, no `System.in`, no `System.out`. That is not tidiness. It is what makes the engine testable without a fixture, deterministic under a seed, and reusable from a terminal client and a browser client that have nothing else in common.

## Must be true when you're done

- [ ] two packages exist under `src/main/java`: the engine at `com.connorjensen.griddlers`, and `com.connorjensen.griddlers.tools` beneath it, each holding at least one class you wrote for this lesson
- [ ] the `tools` class calls into an engine class, so the dependency direction is a fact about compiled code rather than a claim in a document
- [ ] no engine class reads a file, opens a socket, or touches `System.in`, `System.out`, or `System.err`
- [ ] an import of a `tools` type written inside an engine class fails `mvn validate`, and the output names the import it rejected
- [ ] that same import, written inside a `tools` class, passes
- [ ] the rule lives in a configuration file of its own rather than in `pom.xml`, so adding a third package later is an edit to that file and nothing else
- [ ] `mvn -B verify` is green and still prints no `[WARNING]` you cannot explain

## Done when

- [ ] every production class you added has a tier 1 test mirroring its package, per `lessons/TESTING_STANDARDS.md`
- [ ] `mvn test` reports the new total with failures, errors, and skips at 0
- [ ] you can reproduce the rejected build on demand — the capstone asks you to point at the output, not to describe it
- [ ] the journal's `dependency scope` row is rewritten against what you saw here, and the `?3` mark comes off
- [ ] `git status` is clean and the commit has landed

## Reach for

Checkstyle has exactly one module that reads a second configuration file instead of taking its rules inline. It describes packages as a tree and says, per subtree, what may be imported. Everything else in `checkstyle.xml` is a single element; this one is a pointer.

A Checkstyle module that needs a path gets it from the plugin through `<propertyExpansion>`, and the module documentation names the property it is looking for. Work out from that why the path cannot simply be hardcoded the way `configLocation` is.

Before you assume the rule covers everything, find out which directories Checkstyle actually reads. The plugin has separate settings for main sources and test sources, and only one of them is on by default — which decides whether your `tools` import in a *test* is governed by this rule at all.

`.agents/CONTEXT.md` owns the direction and the no-I/O constraint. `lessons/TESTING_STANDARDS.md` owns the tier 1 rule and the naming.

Give the two classes something small and real to do. A boundary demonstrated on `Foo` and `Bar` proves the build works and teaches you nothing about where the seam belongs; a `tools` class that formats what an engine class computes will still make sense in Chapter 9.

## Pop quiz

Answer these from memory, editor closed.

1. Why does `tools` depend on the engine and not the reverse? Name one concrete thing you lose the day someone inverts it.
2. Which classpath does `test` scope put JUnit on, and which one does it keep JUnit off? Answer without using the word "phase" — this is the row your journal still has marked `?3`.
3. Name two ways other than Checkstyle to enforce a package boundary in Java, and say what each one costs you.
4. Your boundary rule rejects a build. Did it reject it for `src/test/java` as well as `src/main/java`? What setting decided that, and which answer do you actually want here?
5. The engine does no I/O. Name two things that buys you in a test suite that an engine which merely *avoids* I/O by convention does not.
6. A `tools` class and an engine class both need the same constant. Where does it go? What happens to the boundary if you put it in the other place?
