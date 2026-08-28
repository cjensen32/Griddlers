# Nonogram — Learning Guide

**Version:** 0.4
**Companion to:** `nonogram-spec.md`
**Purpose:** how to work on this project so that you end up knowing how to build it.
**Assumes:** Chapter 1 (Java Foundations) is complete. See `CHAPTER-1-REVIEW.md`.

---

## 1. The rule

**Every line of code in this project gets typed by you, from your own understanding.**

Not "typed by you after reading a generated version." The distinction is the whole project. Reading
working code produces a warm feeling of comprehension that evaporates the moment you face a blank
file — and you will not notice the gap until you are four chapters deep and cannot debug your own
program.

If you break the rule once for a genuinely stuck moment, fine. If you break it habitually, you will
have an app and no skills, which is the outcome you are specifically trying to avoid.

**The rule now covers tests.** This is the change from Chapter 1, and it is the change that matters.

---

## 2. Tests are the deliverable

Chapter 1 ended with 49 passing tests. Thirty-nine were in the agent-authored capstone grader. You
wrote ten, across four files, for thirteen production classes.

That is not a failure — the grader existed to prove the tracker worked, and it did. But it means the
skill you practised was *making someone else's tests pass*, and the skill you did not practise was
*deciding what to test*. Those are different skills, and only the second one is available to you on
a codebase nobody has written a grader for, which is every codebase you will ever be paid to work on.

So in this project you write the tests. All of them, at three tiers, described in the spec:

**Tier 1 — every file.** One class in isolation. Cheap, fast, dozens of them.
**Tier 2 — every flow.** Real collaborators on one complete path. A handful per chapter.
**Tier 3 — end to end.** The real `main` in a real JVM through `ProcessBuilder`. One or two, no more.

### How to decide what to test

This is the actual skill, and it is mostly a small number of habits.

**Start from the contract, not the code.** Read the signature and ask what it promises. `get(int x,
int y)` promises a value for every in-bounds coordinate and an exception otherwise — that is two
tests before you have looked at a single line of the body. Tests derived from the contract survive
refactoring; tests derived from the implementation break every time you tidy anything.

**Enumerate the edges the same way every time.** Chapter 1's standards already list them: empty,
null, whitespace, boundary numbers, invalid syntax, unknown identifiers, retries, duplicate actions,
quit, and EOF. This project adds: zero-size, one-size, non-square, the transposed case, the
all-empty and all-full case, and the clue that fills the line exactly. Keep the list somewhere and
run down it mechanically. Inspiration is not a testing strategy.

**Ask which tier would have caught it.** When something breaks, before fixing it, decide where the
test belongs. A wrong glyph is tier 1. A board that renders correctly but wires up wrong is tier 2.
A board that is right in tests and mangled in your terminal is tier 3 — a charset problem that no
in-process test can see. Getting this classification right is most of what "comprehensive" means.

**Test the boring class anyway.** "Too simple to break" is a prediction. `CellState` is an enum with
three constants and it still gets a test, because in Chapter 12 its ordinals become a save format
and that test is the thing standing between you and silently corrupted saved games.

**Watch the Gate 3 number.** Every capstone, the agent writes hidden tests against your contract and
some of them fail. That count is a direct measurement of what your own test design missed. Record it
in `JOURNAL.md` every chapter. If it is trending down, this is working. If Chapter 8's count is the
same as Chapter 3's, something in how you are choosing tests has not changed and it is worth
stopping to ask why.

---

## 3. How to use an AI assistant here

Assistants are enormously useful on this project. Just not for writing it — and, now, not for
testing it either.

### Ask for these

**Concepts, when you are stuck.** "Why would a line solver return a wrong answer on a clue of `[0]`?"
You get the idea; you write the fix.

**Shape before code.** "What data structure should hold the undo stack, and what are the tradeoffs?"
Most of the actual engineering thinking lives in these decisions, and having them explained does not
rob you of the implementation.

**Code review after the fact.** Write it, get it working, *then* ask what is wrong with it. This is
the highest-value use in the whole list and it is the one people skip.

**Test review after the fact.** Same move, applied to the new skill. "Here is my `GridPrinterTest`.
What cases am I not covering?" Note the ordering: after you have written yours, never before. This
is the single most useful thing an assistant can do for you on this project.

**Explanation of an error message.** Stack traces are a language you do not speak fluently yet.
Translation is fine.

### Do not ask for these

**"Write the line solver."** Obviously.

**"Write me 15 JUnit cases for this."** This was allowed in the previous version of this guide, on
the grounds that tests give nothing away. That was true when the goal was a working app. It is false
now that the goal includes learning to test, because it outsources exactly the judgement the project
exists to build. There is one exception and it is fenced: **Gate 3**, after your own suite is green,
where hidden tests function as a measurement of what you missed rather than a substitute for trying.

**"Fix this bug."** Ask *why it is broken* instead. The debugging skill is worth more than the fix,
and it is the skill that transfers to every job you will ever have.

**"Is this right?"** — before running it. Run it. Tests answer this question better than an
assistant does, and building the reflex to reach for a test instead of an oracle is most of the
point.

**Anything at the "just this once, I'm tired" moment.** That moment is where the rule actually gets
decided.

### A trick worth knowing

When you *do* want to see how an algorithm works, ask for it as **pseudocode** or **in a language
you are not using for that layer**. You get the idea without a copy-paste path. Python pseudocode
for a DP you are about to write in Java is close enough to learn from and far enough that you have
to think.

---

## 4. Getting unstuck, in order

Work down this ladder. Do not skip to the bottom.

1. **Re-read the error message.** Actually read it. Line number, exception type, the whole thing.
   Chapter 2's failure catalogue exists so this step is fast.
2. **Print the state.** Print the grid, print the clue array, print the line before and after. Most
   nonogram bugs are visible the instant you look at the board.
3. **Shrink the case.** Does it fail on a 3×3? A single line? One clue? A bug you can reproduce in
   five cells takes ten minutes; the same bug in a 10×10 takes an hour.
4. **Write the test that reproduces it.** At the tier that would have caught it. Now it is
   permanent, and you will never ship that bug twice.
5. **Rubber-duck it in writing.** Type out what you expect to happen and what happens instead. This
   solves it maybe a third of the time before you finish typing.
6. **Twenty minutes, then ask** — for the concept, not the code.

The twenty-minute rule matters in both directions. Under twenty minutes, you are outsourcing
thinking you were about to do anyway. Past an hour of no progress, you are not learning, you are
grinding, and grinding is how projects get abandoned.

---

## 5. What each chapter actually teaches

Worth knowing, because if a chapter feels tedious this is the reason to push through it.

| Chapter | The real lesson |
|---|---|
| 2 — The Workshop, Rebuilt | That you can reconstruct a toolchain rather than inherit one; and the three-tier test ladder you will use for the next twelve chapters |
| 3 — Seeing the Board | Two-dimensional indexing, and how a bad index convention poisons everything downstream |
| 4 — Clues | Run-length encoding, and deriving data rather than storing it |
| 5 — Brute-force solver | Combinatorial enumeration; writing the correct-but-slow version first as a reference |
| 6 — Propagation | Constraint propagation and fixed-point iteration — "loop until nothing changes" is everywhere in real software |
| 7 — Generation | Generate-and-test; why proving a property is different from hoping for it |
| 8 — Terminal game | Separating engine from interface. This is the architecture lesson of the project |
| 9 — JSON on disk | Serialization, and designing a format you will have to read by eye for months |
| 10 — Web grid | DOM manipulation, event handling, the browser's coordinate system — and rebuilding the test ladder in a runtime that has no JUnit |
| 11 — Touch input | Pointer events, gesture state machines, why mobile input is genuinely hard |
| 12 — Persistence | State that outlives the process |
| 13 — PWA deploy | Service workers, caching, offline-first, what "shipping" means |
| 14 — DP solver | Dynamic programming and memoization, with a reference implementation to check against |

Chapter 14 is the one to look forward to. Writing a DP algorithm when you already have a slow correct
version to test against is the single best way to learn dynamic programming, because you get
instant, unambiguous feedback on whether you understood it.

---

## 6. Checkpoints

At the end of each chapter, close the editor and answer out loud. If you cannot, you copied
something without absorbing it — go back.

**After 2:** Which of your three tests would still pass if you deleted the `pom.xml`'s Surefire
pin? (Trick question — think about what "pass" means when a test does not run.)

**After 3:** Which of your tests would catch a `[y][x]` / `[x][y]` swap, and which would silently
still pass on a square grid?

**After 5:** Why does a clue of `[7]` in a 10-cell line force four cells filled, before you know
anything else? Draw it.

**After 6:** Why does solving rows and columns repeatedly find things that one pass misses?

**After 7:** Why does "the propagation solver finished" prove the puzzle has exactly one solution?

**After 8:** If you swapped the terminal interface for a web one, which files would change? (Correct
answer: only the interface files under `tools/`. If it is more, your layering leaked — and Chapter
1's dependency-direction rule is the thing that leaked.)

**After 11:** Why does axis-lock exist? What does the interaction feel like without it? (Try it —
actually remove it for a minute.)

**After 13:** What does the service worker do when the phone has no signal, and how does it know
what to serve?

---

## 7. Working habits

**Commit at every green test.** Small commits with real messages, under the grammar you ported in
Chapter 2. You will want the ability to go back to "the last version that worked" more often than
you expect.

**Keep a `JOURNAL.md` in the repo.** One line each time you get stuck and unstick yourself: what
broke, what fixed it. Plus, every capstone, the Gate 3 failure count. Ten minutes a week, and in
three months it is the most valuable file in the project. It is also the thing you will mine for
interview stories.

It is called `JOURNAL.md` rather than `NOTES.md` because the tracker repository already uses
`NOTES.md` for Java version history, and one name meaning two things across one course is a trip
hazard you can avoid for free.

**Play the game.** Regularly, on your actual phone, not the simulator. You are the only playtester
you have, and things that are obviously wrong in your hand are invisible in a desktop browser.

**Set a pace you can hold.** Chapters 3–8 are perhaps a couple of weekends each; Chapters 10–11 are
the long ones because touch input has more edge cases than the entire solver. This is a
several-month project. Treating it as a two-week sprint is how it dies at Chapter 7.

---

## 8. Traps

**Rewriting Chapter 4 because you learned something in Chapter 9.** You will want to. Note it in
`JOURNAL.md` and keep going. Finishing an imperfect version teaches more than perfecting an
unfinished one.

**Treating Chapter 2 as paperwork.** It is review, so it feels like a formality, so the temptation
is to copy the tracker's `pom.xml` and move on. The chapter is not the `pom.xml` — it is
`ProcessProbe` and the failure catalogue, both of which you will lean on for a year. Copy the build
files and you skip the chapter's actual content.

**Starting with the DP solver.** It is the interesting algorithm, so it is tempting. You will write
it, it will be subtly wrong, and you will have nothing to test it against. That is the trap the
brute-force version exists to prevent.

**Optimizing anything before Chapter 14.** Every performance choice in the spec is deliberately
naive with a documented trigger for revisiting (spec Appendix C). If you cannot state which
measurement justifies an optimization, do not do it.

**Building the settings screen.** It is comfortable, low-stakes work that produces a feeling of
progress. So is picking colors. Do the hard thing first; the game has to be good before its options
screen matters.

**Reaching for the App Store early.** Provisioning profiles, code signing, and review rejections are
a genuinely miserable first experience of deployment, and none of it teaches you anything about your
app. The PWA path (spec Chapter 13) gets it on your home screen this month.

---

## 9. Where to look things up

Not everything should come from an assistant. Building the habit of reading primary sources is part
of the job.

- **Java** — the official Javadoc. `java.util.Deque`, `List`, `Arrays` are most of what this project
  needs.
- **JUnit 5** — the user guide's "Writing Tests" section; you need maybe 10% of it. Read the
  parameterized-test and `@Nested` sections properly this time — Chapter 1 used neither much, and
  both pay off once you are writing tests per file.
- **Web APIs** — MDN, always. Pointer Events, CSS Grid, `localStorage`, Service Worker API.
- **PWAs** — web.dev's PWA docs. Manifest and service worker basics, and the offline cookbook.
- **Nonogram theory** — the Wikipedia article, and search "nonogram solver line solving algorithm"
  for writeups of the DP approach. Read *about* it before you write it; do not read someone's Java
  implementation.
- **Your own Chapter 1** — `GLOSSARY.md` for fast recall, `TESTING_STANDARDS.md` for test shape and
  naming, `COURSE_STANDARDS.md` for conventions. Copies live in this repository; the tracker
  repository holds the lessons behind them.

---

## 10. What "done" looks like

You have won when: the game is on your home screen, it works on the subway with no signal, someone
who has never seen the spec plays it without asking how it works, and you can explain any file in
the repo to a stranger without opening it.

And one more, new to this project: you can hand someone a class they have never seen, and they can
tell what it is supposed to do from its test file alone.

The App Store, picture puzzles, and 15×15 grids are all still there afterward. They will be much
easier from that position than from this one.
