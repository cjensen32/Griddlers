# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, then build the end-to-end test harness you will use for the next twelve chapters — before there is any nonogram code worth testing.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker. When you are stuck for more than twenty minutes, open it, look at exactly the thing you are stuck on, close it, and type your own version.

## Lessons

Two are written at a time. When the current lesson goes green, the queued one is refitted to what you actually built and the next stub is written out. Hours are yours to fill in as each lesson closes; `.agents/tools/roll_up_hours.py` sums the column and carries the total to the course map when the chapter closes.

| #   | Lesson                                                   | Hours    | State   | Done |
|-----|----------------------------------------------------------|----------|---------|------|
| 2.1 | [The POM, from memory](01-the-pom.md)                    | 4.55     | green   | ☑   |
| 2.2 | [Quality gates, from memory](02-the-quality-gates.md)    |          | current | ☐   |
| 2.3 | [Git + commit grammar](03-git-and-the-commit-grammar.md) |          | queued  | ☐   |
| 2.4 | [The two packages](04-the-two-packages.md)               |          | stub    | ☐   |
| 2.5 | [The end-to-end harness](05-the-end-to-end-harness.md)   |          | stub    | ☐   |
| 2.6 | [The failure catalogue](06-the-failure-catalogue.md)     |          | stub    | ☐   |
| ★  | [Must be true](CAPSTONE.md)                              |          | stub    | ☐   |
|     | **Total**                                                | **4.55** |         |      |

A lesson closes when its test is green, its pop quiz is answered from memory, and the commit has landed.

2.1 closed on 2026-08-29 at roughly 4.55 hours, with two optional `(reinforce)` boxes left open in its file. 2.2 has not yet been refitted to what 2.1 actually built, and 2.3 is still a stub in its file - both are the next turn of the cycle.

## What already exists

`config/checkstyle/checkstyle.xml`, `.editorconfig`, and `.githooks/prepare-commit-msg` were copied from Chapter 1 as frozen reference, not exercises. `.gitignore` exists and Lesson 2.3 extends it further. `pom.xml`, `src/main`, and `src/test` were created by Lesson 2.1; Lessons 2.2 and 2.4 build on them.

## The real content

The chapter is not the `pom.xml`. It is the subprocess harness in 2.5, the enforced package boundary in 2.4, and the failure catalogue in 2.6 — all three of which you lean on for the next year.
