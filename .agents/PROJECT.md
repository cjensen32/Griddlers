# Griddlers - Active Course Scope

Status: course handoff prepared; learner implementation has not started.

## Purpose

This repository continues the learner's Java course after the completed Chapter 1 Job Application Tracker. It changes domains to Nonograms so the learner can own the design, implementation, and complete test suite while moving from plain Java through algorithms, serialization, browser work, and deployment.

## Product scope

The finished project will provide:

- A deterministic Java Nonogram engine with grid state, clue derivation, solving, and generation.
- Terminal tools that compose the engine with file and standard-stream I/O.
- A readable JSON puzzle format shared with a browser client.
- A no-build-step browser game with mouse and touch input, persistence, offline support, and static deployment.
- A learner-written test ladder covering individual files, complete flows, and runnable entry points.

## Technical architecture

```text
com.connorjensen.griddlers          deterministic engine; no file, network, or stream I/O
└── tools                           runnable programs, file I/O, and terminal interaction

web/                                browser client introduced in Chapter 10
puzzles/                            generated JSON introduced in Chapter 9
```

Dependency direction is `tools -> engine`, never the reverse. Browser play logic and DOM code will also remain separate so the non-DOM behavior can be tested without a browser.

## Course state

- Chapter 1 is complete and remains in the separate Job Application Tracker repository.
- Chapter 2 is open and owns `lessons/ch02-the-workshop-rebuilt/`. It ends with a terminal application that runs, is interactable, and creates basic nonograms - not a copy of Chapter 1's tracker, but a working program rather than a toolchain with nothing behind it.
- Chapters 3-14 are goal lines in `lessons/SYLLABUS.md` and gain lessons only on arrival.
- Only one chapter is open at a time, and only one of its lessons is written at once. The next lesson is drafted when the current one closes, against the code that actually exists by then.
- A lesson is a checkpoint that establishes real features in real files. Completing one must mean the application does something it could not do before, never that a set of instructions was followed.
- Production code tracks toward roughly 50% complete by the midpoint of a chapter and 80-90% entering its capstone, so the capstone is a welcome challenge that carries the work to finished rather than a build from scratch.

## Definition of done

- The game is installable from its deployed static site and works offline.
- A new player can complete a puzzle without consulting the course specification.
- The learner can explain every implementation and test file without outside help.
- Every chapter guarantee is covered by a learner-written test at the appropriate tier.
- Every completed capstone records its Gate 3 gap count in `lessons/JOURNAL.md`.
- The repository's build, style, test, and deployment gates pass at their applicable milestones.

## Explicit non-goals

Before a measured need exists, this project does not include React, Vite, Canvas rendering, Playwright, IndexedDB, App Store packaging, grids larger than 10x10, colored Nonograms, or performance optimizations. Chapter 14's dynamic-programming solver is a learning finale rather than a browser feature.
