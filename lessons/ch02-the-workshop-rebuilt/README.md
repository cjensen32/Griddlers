# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, then build the end-to-end test harness you will use for the next twelve chapters — before there is any nonogram code worth testing.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker. When you are stuck for more than twenty minutes, open it, look at exactly the thing you are stuck on, close it, and type your own version.

## Lessons

Two are written at a time. When the current lesson goes green, the queued one is refitted to what you actually built and the next stub is written out. Hours are yours to fill in as each lesson closes; `.agents/tools/roll_up_hours.py` sums the column and carries the total to the course map when the chapter closes.

| #   | Lesson                                                   | Hours    | State    | Done | Open |
|-----|----------------------------------------------------------|----------|----------|------|------|
| 2.1 | [The POM, from memory](01-the-pom.md)                    | 4.80     | green    | ☑   | 6    |
| 2.2 | [Quality gates, from memory](02-the-quality-gates.md)    |          | current  | ☐   | 3    |
| 2.3 | [The two packages](03-the-two-packages.md)               |          | queued   | ☐   | —    |
| 2.4 | [The end-to-end harness](04-the-end-to-end-harness.md)   |          | ahead    | ☐   | —    |
| 2.5 | [The failure catalogue](05-the-failure-catalogue.md)     |          | stub     | ☐   | —    |
| 2.6 | [Git + commit grammar](06-git-and-the-commit-grammar.md) | —        | optional | ☐   | —    |
| ★  | [Must be true](CAPSTONE.md)                              |          | stub     | ☐   | —    |
|     | **Total**                                                | **4.8**  |          |      |      |

A lesson closes when its test is green, its pop quiz is answered from memory, and the commit has landed.

2.1 closed on 2026-08-29 at roughly 4.80 hours, over three submissions. `Open` counts what it left for a later lesson because no checkbox named it; `lessons/JOURNAL.md` itemises the six under `Open gaps`, five of which were routed to 2.2.

## What changed on 2026-09-07

The chapter was reordered at your request. Git and the commit grammar was 2.3; it is now 2.6 and it is optional, because the machinery it describes is already installed and working, and you would rather spend the chapter writing Java. Everything after it moved up one. No content was dropped — the old 2.3 became a read-through of the conventions instead of an assignment to build them.

2.4 is written ahead of the two-at-a-time rule, also at your request. That is a deliberate exception and the collector flags it by design; it is not a bug to fix. The risk the rule exists to prevent is real here — 2.4 was written before 2.3's classes exist, so its file names and its assumptions get refitted against what you actually build before you open it.

## What already exists

`checkstyle.xml`, `.editorconfig`, and `.githooks/prepare-commit-msg` were copied from Chapter 1 as frozen reference, not exercises. `.gitignore` exists and already covers everything the build writes. `pom.xml`, `src/main`, and `src/test` were created by Lesson 2.1; Lessons 2.2 and 2.3 build on them.

## The real content

The chapter is not the `pom.xml`. It is the subprocess harness in 2.4, the enforced package boundary in 2.3, and the failure catalogue in 2.5 — all three of which you lean on for the next year.
