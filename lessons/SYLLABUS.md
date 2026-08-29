# Griddlers — Course Syllabus

**Version:** 0.9
**Course:** Java Foundations to Deployment *(working title)*
**Prerequisite:** Chapter 1 — Java Foundations, complete in the Job Application Tracker repository.

Chapters 2 through 14 build a Nonogram engine, a terminal game, and an installable browser client. Chapter 1's toolchain and habits carry over and are never re-taught.

---

## Course map

Weight is the share of total course time each chapter is expected to take, estimated from the shape of the work rather than from a lesson plan. Hours are what you actually spent, recorded at the end of each chapter. Lessons fill in when a chapter opens.

| Ch | Chapter               | Hours | Weight   | Lessons | Done |
|----|-----------------------|-------|----------|---------|------|
| 2  | The Workshop, Rebuilt | 3.8   | 5%       | 6       | ☐   |
| 3  | Seeing the Board      | —     | 7%       | —       | ☐   |
| 4  | Clues                 | —     | 7%       | —       | ☐   |
| 5  | Brute-force solver    | —     | 7%       | —       | ☐   |
| 6  | Propagation           | —     | 7%       | —       | ☐   |
| 7  | Generation            | —     | 7%       | —       | ☐   |
| 8  | Terminal game         | —     | 8%       | —       | ☐   |
| 9  | JSON on disk          | —     | 5%       | —       | ☐   |
| 10 | Web grid              | —     | 13%      | —       | ☐   |
| 11 | Touch input           | —     | 13%      | —       | ☐   |
| 12 | Persistence           | —     | 7%       | —       | ☐   |
| 13 | PWA deploy            | —     | 4%       | —       | ☐   |
| 14 | DP solver             | —     | 10%      | —       | ☐   |
|    | **Total**             |       | **100%** |         |      |

---

## What each chapter builds

**Ch 2 — The Workshop, Rebuilt** *(open)* — reproduce the Chapter 1 toolchain from memory, then build the end-to-end test harness you will use for the next twelve chapters, before there is any nonogram code worth testing. Lessons: [`ch02-the-workshop-rebuilt/`](ch02-the-workshop-rebuilt/README.md).

**Ch 3 — Seeing the Board** — print a nonogram grid to the terminal. No clues, no solving, no puzzle — just a board you can look at. Everything in the rest of the course is debugged through this output.

**Ch 4 — Clues** — derive clue arrays from a solved grid by run-length encoding, and render them in the gutters around the board. Clues are always derived from a solution, never authored, which makes an entire category of bug impossible.

**Ch 5 — Brute-force solver** — enumerate every valid placement for one line and intersect them. Correct and slow on purpose; it becomes the oracle that proves Chapter 14 right.

**Ch 6 — Propagation** — run the line solver over rows and columns until nothing changes.

**Ch 7 — Generation** — random fill, derive clues, propagate, keep only what solves. Seeded and reproducible.

**Ch 8 — Terminal game** — play a generated puzzle to completion with moves, undo, and hints. **The checkpoint that matters** — after this, the engine is proven and every remaining bug is a UI bug.

**Ch 9 — JSON on disk** — emit puzzles as readable JSON, with a round-trip test.

**Ch 10 — Web grid** — the board in a browser: one HTML file, no build step, fetch the JSON and play with a mouse. The test ladder gets rebuilt here, not ported.

**Ch 11 — Touch input** — pointer events, mode toggle, drag-painting with axis lock, stroke-level undo. Budget more time than the entire solver took.

**Ch 12 — Persistence** — `localStorage` saves, timer, crosshair, auto-cross, and hints read from the deduction list.

**Ch 13 — PWA deploy** — manifest plus service worker, deployed to a free static host and installed to your home screen.

**Ch 14 — DP solver** — rewrite the line solver as dynamic programming and prove it correct by differential testing against Chapter 5. This one never runs in the browser; it is the learning finale, not a product feature.

---

## How this course runs

One chapter is open at a time and owns a directory here. Every chapter below it is a goal line and nothing more — no lessons, no guarantees, no test plan — until you reach it.

Inside the open chapter, two lessons are written at any moment: the one you are on and the one queued behind it. The rest are stubs holding a title. When a lesson closes, the queued one is refitted to what you actually built, then the next stub is written out.

A lesson closes when the test it names is green and the commit has landed. The capstone is written after the chapter's last lesson, from the suite you built rather than as a target handed to you in advance — which is why `CAPSTONE.md` holds only the chapter's "Must be true" list until then.

Test tiers and what makes a test worth keeping are in [`TESTING_STANDARDS.md`](TESTING_STANDARDS.md). Progress and stuck/unstuck notes go in [`JOURNAL.md`](JOURNAL.md).

## The four standing rules

**1. The terminal renderer comes before everything else.** Chapter 3 builds a board printer with no clues, no solver, and no puzzle behind it. From then on, every single thing you build has a way to be *looked at*, and "print the board and stare at it" is your primary debugging tool. Most nonogram bugs are visible on sight and invisible in a stack trace.

**2. You write every test.** No agent-authored test enters this repository, ever — not as a grader, not as a hidden capstone file, not as a favour when you are tired. An agent may tell you a test is missing, may review a test you wrote, and may show you an example on unrelated code. It may not write one for you.

**3. Tests ship with the lesson, not after the chapter.** A lesson is done when its test is green.

**4. Functions return Strings; only `main` prints.** You already built this rule in Chapter 1 as `TextTable` — this is a restatement, not a lesson. Break it and most of the tests in this course become impossible to write.

---
## Repository layout

```
Griddlers/
├─ pom.xml                          you write it in Lesson 2.1
├─ .githooks/prepare-commit-msg
├─ config/checkstyle/checkstyle.xml
├─ lessons/                         this course, student-facing
│  ├─ SYLLABUS.md                   this document
│  ├─ TESTING_STANDARDS.md          the three tiers, and what closes a lesson
│  ├─ JOURNAL.md                    stuck/unstuck log and per-chapter gap counts
│  └─ chNN-<name>/                  one directory, for the open chapter only
│     ├─ README.md                  the chapter's goal, rule, and lesson table
│     ├─ NN-<lesson>.md             written: the current lesson and the one queued
│     ├─ NN-<lesson>.md             stub: a title, until its turn comes
│     └─ CAPSTONE.md                "Must be true" until the chapter ends
├─ src/main/java/com/connorjensen/griddlers/
│  │                                engine; no java.io, java.nio.file, java.net, no printing
│  └─ tools/                        mains, file I/O, terminal game
├─ src/test/java/com/connorjensen/griddlers/
│                                   mirrors the above, file for file, plus shared harnesses
├─ puzzles/                         generated JSON, committed
├─ web/                             index.html, manifest.json, sw.js, icons/
└─ .agents/                         agent instructions, reference, and archive
```

The log is `JOURNAL.md`, not `NOTES.md`. The tracker already uses `NOTES.md` for Java version history, and one name meaning two things across one course is a trip hazard you can avoid for free.

---

## Changing how this course is written

The lesson files are written for you, and you can change how. Nothing here is a rule you have to live with.

- **How lessons are written, and what an agent may never do** — `.agents/CONTEXT.md`, sections "Agent constraints" and "Commit messages".
- **Chapter and lesson states, and the lesson cycle** — `.agents/COURSE_MAINTENANCE.md`.
- **Which file owns which rule** — `.agents/README.md`, section "Where each rule lives". Start there when you are not sure what to edit.

Ask for the change in conversation and it gets made in the right file, or edit the file yourself and ask for the rest to be brought in line.
