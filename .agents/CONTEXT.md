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
- `.agents/COURSE_MAINTENANCE.md` defines the four chapter states and who may change them.
- `lessons/SPEC.md` defines the course sequence and chapter guarantees.
- `lessons/LEARNING_GUIDE.md` defines how the learner and assistants work during the course.
- `lessons/JOURNAL.md` records stuck/unstuck notes and capstone gap counts.
- `lessons/COURSE_STANDARDS.md` and `lessons/TESTING_STANDARDS.md` define learner-facing conventions.
- `.agents/archive/project-init-session/` preserves the source handoff history, excluding local-only environment files.
- Once the learner creates it, `pom.xml` owns the installed build configuration and dependencies.

When authorities disagree, preserve learner ownership, verify the current checkout, and resolve the conflict explicitly. Historical chat files are provenance, not current instructions.

## Agent constraints

The learner owns `src/main` and `src/test` in full.

- Never write implementation code, and never write test code - at any gate, in any file, for any reason. Naming a gap in the test suite is the help being asked for; filling it is not.
- Example, never solution. When a concept needs demonstrating, demonstrate it on unrelated subject matter, never on the class or test the learner is about to write.
- Never author a capstone before its chapter's lessons are complete, and never fill in a `CAPSTONE.md` stub beyond its "Must be true" list.
- Chapter expansion belongs to the learner. An agent may review a learner-drafted breakdown but may not produce it.
- When the learner asks for a fix to learner code, refuse and restate the concept. Diagnose the failure, point to relevant evidence, and let the learner implement the correction.
- Preserve learner progress edits and unrelated work. Inspect Git status and relevant authority before changing repository-owned documentation or tooling.

## Chapter 2 bootstrap boundary

Until the learner reaches the named lesson, agents must not create or scaffold `pom.xml`, edit or replace `.gitignore`, create `src/main` or `src/test`, create Java package directories, add Checkstyle `ImportControl`, or fill any `CAPSTONE.md` past its published guarantees. Do not run `git init`, `git add`, `git commit`, or rewrite history unless the learner explicitly requests and approves that Git action.

## Commit messages

The tracked hook prepends `C###`. Use `COURSE(scope)` for course and repository documentation, `GAME(scope)` for learner implementation, `PROGRESS(scope)` for learner progress, and `FIX(scope)` for corrections to earlier implementation. Commit bodies, when requested, use `DESCRIPTION:`, `FILES:`, optional `NOTES(subject):`, and `VERIFY(command):` in that order. Derive every message from the current staged diff and wait for approval before committing.

## Verification

Before Chapter 2 creates the build, verify documentation work with `git diff --check`, relative-link and unresolved-variable audits, exact source comparisons for frozen files, and an inventory proving that no prohibited learner file was created.

After the learner installs the build, `mvn verify` must run JUnit 5 tests, Checkstyle, Spotless, and JaCoCo. A deliberately failing test must fail the build. Lesson 2.4 adds the separate proof that a forbidden engine import fails while the same import is permitted in `tools`.

When `.codegraph/` exists, use CodeGraph before broad code searches. Do not create, regenerate, delete, or commit its index unless the learner asks.
