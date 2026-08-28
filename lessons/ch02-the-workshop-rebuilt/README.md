# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, then build the end-to-end test harness you will use for the next twelve chapters — before there is any nonogram code worth testing.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker. When you are stuck for more than twenty minutes, open it, look at exactly the thing you are stuck on, close it, and type your own version.

## Lessons

Two are written at a time. When the current lesson goes green, the queued one is refitted to what you actually built and the next stub is written out.

| #   | Lesson                                                         | State   | Done |
|-----|----------------------------------------------------------------|---------|------|
| 2.1 | [The POM, from memory](01-the-pom.md)                          | current | ☐    |
| 2.2 | [The quality gates, from memory](02-the-quality-gates.md)      | queued  | ☐    |
| 2.3 | [Git and the commit grammar](03-git-and-the-commit-grammar.md) | stub    | ☐    |
| 2.4 | [The two packages](04-the-two-packages.md)                     | stub    | ☐    |
| 2.5 | [The end-to-end harness](05-the-end-to-end-harness.md)         | stub    | ☐    |
| 2.6 | [The failure catalogue](06-the-failure-catalogue.md)           | stub    | ☐    |
| ★   | [Must be true](CAPSTONE.md)                                    | stub    | ☐    |

A lesson closes when its test is green, its pop quiz is answered from memory, and the commit has landed.

## What already exists

`config/checkstyle/checkstyle.xml`, `.editorconfig`, and `.githooks/prepare-commit-msg` were copied from Chapter 1 as frozen reference, not exercises. `.gitignore` exists and Lesson 2.3 extends it. `pom.xml`, `src/main`, and `src/test` are deliberately absent — Lessons 2.1, 2.2, and 2.4 create them.

## The real content

The chapter is not the `pom.xml`. It is the subprocess harness in 2.5, the enforced package boundary in 2.4, and the failure catalogue in 2.6 — all three of which you lean on for the next year.
