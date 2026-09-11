# Chapter 2 — The Workshop, Rebuilt

**Goal:** reproduce the Chapter 1 toolchain from memory, build the end-to-end test harness you will use for the next twelve chapters, and finish with a terminal application that runs, is interactable, and creates basic nonograms. The toolchain is the means; the working program is the point.

**The rule for this chapter:** write every file by hand, from memory, before opening the tracker. When you are stuck for more than twenty minutes, open it, look at exactly the thing you are stuck on, close it, and type your own version.

## Lessons

One is written at a time. When the current lesson goes green, the next is written against the code that actually exists by then. Hours are yours to fill in as each lesson closes; `.agents/tools/roll_up_hours.py` sums the column and carries the total to the course map when the chapter closes.

| #   | Lesson                                                   | Hours   | State    | Done | Open |
|-----|----------------------------------------------------------|---------|----------|------|------|
| 2.1 | [The POM, from memory](01-the-pom.md)                    | 4.80    | green    | ☑   | 6    |
| 2.2 | [Quality gates, from memory](02-the-quality-gates.md)    | 4.50    | green    | ☑   | 4    |
| 2.3 | [The three packages](03-the-three-packages.md)           | 2.7     | current  | ☐   | —    |
| 2.4 | [The end-to-end harness](04-the-end-to-end-harness.md)   |         | ahead    | ☐   | —    |
| 2.5 | [The failure catalogue](05-the-failure-catalogue.md)     |         | stub     | ☐   | —    |
| 2.6 | [Git + commit grammar](06-git-and-the-commit-grammar.md) | —       | optional | ☐   | —    |
| ★  | [Must be true](CAPSTONE.md)                              |         | stub     | ☐   | —    |
|     | **Total**                                                | **9.3** |          |      |      |

A lesson closes when its tests are green, its `Close out` questions are answered, and the commit has landed. A lesson split into parts closes one part at a time, each with its own commit.

2.1 closed on 2026-08-29 at roughly 4.80 hours, over three submissions. `Open` counts what it left for a later lesson because no checkbox named it; `lessons/JOURNAL.md` itemises the six under `Open gaps`, five of which were routed to 2.2.

## What changed on 2026-09-07

The chapter was reordered at your request. Git and the commit grammar was 2.3; it is now 2.6 and it is optional, because the machinery it describes is already installed and working, and you would rather spend the chapter writing Java. Everything after it moved up one. No content was dropped — the old 2.3 became a read-through of the conventions instead of an assignment to build them.

2.4 is written ahead of the two-at-a-time rule, also at your request. That is a deliberate exception and the collector flags it by design; it is not a bug to fix. The risk the rule exists to prevent is real here — 2.4 was written before 2.3's classes exist, so its file names and its assumptions get refitted against what you actually build before you open it.

## What changed on 2026-09-10

2.2 went green, so 2.3 is current and 2.4 is the queued lesson behind it. The `ahead` exception 2.4 was written under has therefore dissolved on its own — two lessons are written, which is the limit, and no further lesson gets drafted until 2.3 closes. Both were refitted against the POM, the `checkstyle.xml`, and the two classes that now actually exist, and both grew the homework shape 2.2 ended up with: a probe, a matrix predicted before it is run, and a judgement call with two legitimate answers.

2.2 closed at roughly 4.50 hours. `Open` counts what it left for a later lesson because no checkbox named it; three of its four route to 2.3.

## What changed on 2026-09-11

2.3 was refit and renamed. It was written as "the two packages" against an engine living in the root package; you built a better shape — `Main` alone at the root with `engine`, `model`, and `tools` as three peers beneath it — so the lesson now describes that, and the file is `03-the-three-packages.md`.

The bigger change is what the lesson asks for. The chapter had drifted into configuration with almost no code behind it: rules nothing exercised, five probe fixtures existing only because real code gave the rules nothing to bite on, and a thirteen-row verdict matrix to be filled once and never reopened. That was a failure in how the lessons were written — they could be completed without producing anything worthwhile. The matrix and the `resources/l03/` fixtures are gone, and 2.3 now builds a `Griddler` with a real grid, derives its clues, and prints a nonogram. The same rules get exercised on the way to something that works.

2.3 ships in two parts, each ending green with its own commit, so neither is a three-hour sitting. `.agents/reference/COURSE_STANDARDS.md` records the lesson format this established.

`import-control.xml` and `checkstyle.xml` were simplified by the course author at your request, and are yours to review. The two-at-a-time rule is dropped: one lesson is written at a time, so 2.4 is `ahead` and gets refit against what you actually build here before you open it.

## What already exists

`.editorconfig` and `.githooks/prepare-commit-msg` were copied from Chapter 1 as frozen reference, not exercises. `.gitignore` exists and already covers everything the build writes. `pom.xml`, `src/main`, and `src/test` were created by Lesson 2.1; Lessons 2.2 and 2.3 build on them. `checkstyle.xml` arrived from Chapter 1 but stopped being frozen in 2.2 — it and `import-control.xml` are working files now, expected to change as the code grows.

## The real content

The chapter is not the `pom.xml`. It is the subprocess harness in 2.4, the enforced package boundary in 2.3, and the failure catalogue in 2.5 — all three of which you lean on for the next year. What makes them stick is that they are built around a program that actually runs, rather than proved against fixtures written to give them something to bite.
