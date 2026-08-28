# Turn 06 — Recalibration against the Chapter 1 endpoint

*(Conducted in Claude Code, inside the closed-out `job-application-tracker` repository, with the
full ch01 tree available for inspection. Earlier turns were conducted in the web chat without it.)*

Now that this project has been closed out, I want to introduce the new project to this context to
clean it up and perfect it before breaking out into a brand new project. In
`./Nonogram-Spec-Creation-Chat` is a full chat history and artifacts from my chat with Claude to
brainstorm this project.

Firstly it should be a continuation in spirit from chapter 01, treat topics we covered as a review
rather than NEW content. The end goal with this chat is to map out the project with better context
found at the endpoint of chapter 01 and this project as a whole.

## Decisions made this turn

**Numbering — continue as Chapters 2–14.** One course across two repositories. Chapter 1 always
means the tracker. The setup chapter survives rather than dissolving, because I intend to write out
all the same scaffolding by hand (`pom.xml`, `src/test/`, `src/main/java/com/connorjensen/...`)
rather than copy it.

**Toolchain — full stack carries over.** Java 21, pinned Surefire, Checkstyle, Spotless, JaCoCo,
the `prepare-commit-msg` hook and commit grammar, and an `.agents/` backbone. Nothing earned in
Chapter 1 regresses.

**Chapter 3 (Seeing the Board) — kept as-is, reframed.** Same six lessons, each labelled review or
new, with review lessons compressed to a contract plus a verification test and the genuinely new
material given the page space.

**Review references — hybrid.** An authored, recall-first `CHAPTER-1-REVIEW.md` in the new repo,
plus verbatim copies of the three frozen reference documents (`GLOSSARY.md`,
`COURSE_STANDARDS.md`, `TESTING_STANDARDS.md`). Deep links point back to the tracker repo as an
answer key. This repository is the reference and template for the design of the review content.

**The thesis change.** The main change for Chapter 1's review material and every chapter after it
is a focus on teaching how to write effective, useful, and comprehensive tests by hand — test each
file, test each flow, test end to end — and that starts with the Chapter 1 content as well (`Main`,
`nonogram`, and everything under them).

**Bloat and conflicts.** Remove any bloat or stale/conflicting content found in the v0.4 artifacts.
