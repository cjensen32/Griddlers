# Project Agent Context

This repository is the active continuation of the learner's Java course. Chapter 1 is complete in the separate Job Application Tracker repository. Griddlers owns Chapters 2-14 and changes the domain to a Nonogram engine, terminal tools, and browser client.

## Repository boundaries

The repository has three ownership surfaces:

- Code and build: future `src/`, `pom.xml`, `config/`, `puzzles/`, `web/`, and portable root tooling.
- Learning: learner-facing material and progress records under `lessons/`.
- Agent backbone: project authority, course-state policy, and provenance under `.agents/`.

Documentation dependency direction is `.agents -> lessons -> code`. Lessons may reference code paths, but code and build must not depend on either documentation surface. Lessons must remain understandable without opening `.agents/`.

The production dependency direction is `com.connorjensen.griddlers.tools -> com.connorjensen.griddlers`, never the reverse. The engine is deterministic and performs no file, network, or standard-stream I/O.

Do not create `CLAUDE.md`, `AGENTS.md`, `.claude/`, `.codex/`, `.opencode/`, or another provider-specific repository authority. Tool-mandated local caches may exist only as ignored local state. This file is the canonical repository context.

## Sources of truth

- `.agents/PROJECT.md` defines scope, architecture, definition of done, and non-goals.
- `.agents/CONTEXT.md` defines agent behavior, ownership, and repository boundaries.
- `.agents/README.md` maps the agent backbone and ownership surfaces.
- `.agents/COURSE_MAINTENANCE.md` defines the chapter and lesson states and who may change them.
- `lessons/SYLLABUS.md` is the student-facing course map: chapter goals, weights, and how a chapter runs.
- `.agents/reference/LEARNING_GUIDE.md` defines how the learner and assistants work during the course.
- `lessons/JOURNAL.md` records stuck/unstuck notes and capstone gap counts.
- `.agents/reference/COURSE_STANDARDS.md` defines repository conventions; `lessons/TESTING_STANDARDS.md` is the student-facing test rubric.
- `.agents/reference/course-rationale.md` records why the course is shaped this way, plus the glyph, deferral, and review-map tables.
- `.agents/archive/project-init-session/` preserves the source handoff history, excluding local-only environment files.
- Once the learner creates it, `pom.xml` owns the installed build configuration and dependencies.

When authorities disagree, preserve learner ownership, verify the current checkout, and resolve the conflict explicitly. Historical chat files are provenance, not current instructions.

## Agent constraints

The learner owns `src/` in full.

- Never write, edit, move, or delete anything under `src/` - implementation and tests alike, at any gate, in any file, for any reason. Naming a gap in the test suite is the help being asked for; filling it is not.
- Example, never solution. When a concept needs demonstrating, demonstrate it on unrelated subject matter, never on the class or test the learner is about to write.
- Never author a capstone before its chapter's lessons are complete, and never fill in a `CAPSTONE.md` stub beyond its "Must be true" list.
- Author the open chapter's lesson files: what to build, what must exist, and what closes the lesson. The learner owns the chapter's shape and may rewrite any of it, and an agent writing the assignment is not an agent deciding the course.
- Name the tools a test needs, never the test itself. Pointing at `assertThrows`, `@Nested`, or a `ProcessBuilder` timeout is the help being asked for; a written assertion is not.
- When the learner asks for a fix to learner code, refuse and restate the concept. Diagnose the failure, point to relevant evidence, and let the learner implement the correction.
- Preserve learner progress edits and unrelated work. Inspect Git status and relevant authority before changing repository-owned documentation or tooling.
- Never hard-wrap Markdown. Write one physical line per logical line and never break mid-sentence, in any `.md` file this repository owns, unless the learner asks for a wrapped file. `.agents/reference/COURSE_STANDARDS.md` owns the rule; `.editorconfig` sets `max_line_length = off` for `*.md`.

## Chapter 2 bootstrap boundary

Until the learner reaches the named lesson, agents must not create or scaffold `pom.xml`, edit or replace `.gitignore`, create `src/main` or `src/test`, create Java package directories, add Checkstyle `ImportControl`, or fill any `CAPSTONE.md` past its published guarantees. Do not run `git init`, `git add`, `git commit`, or rewrite history unless the learner explicitly requests and approves that Git action.

## Commit messages

The tracked hook prepends `C###`. Write the subject as `SCOPE(area): summary`, where scope is `COURSE` for course and repository documentation, `GAME` for learner implementation, `PROGRESS` for learner progress, and `FIX` for corrections to earlier implementation, and area is the lesson or surface being worked on.

A subject that already says what changed and why is a finished commit message. Prefer it. Add a body only when the subject leaves something genuinely unclear, and keep it to what the diff cannot say for itself.

- One clarifying line, when the subject alone is not enough: `path/file.md - what exactly changed and for whom`. Truncate long Java paths to `src/test/.../Name.java`.
- `WHY:` when the reason is not evident from the change itself.
- `CHANGES:` as bullets when one commit touches several surfaces. Prefix a file-specific bullet with its truncated path. Do not enumerate files the diff already lists.
- `VERIFY:` only when the output is worth keeping: a problem this commit could not fix and is recording as evidence, or a checkpoint worth pinning, such as a passing suite at the end of a lesson. Paste the interesting lines, not the whole run.

Derive every message from the current staged diff, and commit only with the learner's approval or a standing approval they have given for the sequence in progress.

## Verification

Before Chapter 2 creates the build, verify documentation work with `git diff --check`, relative-link and unresolved-variable audits, exact source comparisons for frozen files, an inventory proving that no prohibited learner file was created, and `.agents/tools/unwrap_markdown.py --check` over every Markdown file outside `.agents/archive/`.

After the learner installs the build, `mvn verify` must run JUnit 5 tests, Checkstyle, Spotless, and JaCoCo. A deliberately failing test must fail the build. Lesson 2.4 adds the separate proof that a forbidden engine import fails while the same import is permitted in `tools`.

When `.codegraph/` exists, use CodeGraph before broad code searches. Do not create, regenerate, delete, or commit its index unless the learner asks.
