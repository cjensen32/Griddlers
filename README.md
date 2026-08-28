# Griddlers

Griddlers is the active continuation of a learner-owned Java course. Chapter 1 built the Job Application Tracker; Chapters 2-14 use a Nonogram generator and game to move from Java foundations through algorithms, serialization, browser interaction, persistence, and deployment.

The course handoff is being prepared before implementation begins. `pom.xml`, Java source and test trees, and package directories are absent on purpose because Chapter 2 asks the learner to build them.

## Three repository surfaces

- **Code and build:** future learner-owned implementation plus portable root tooling and `config/`.
- **Learning:** `lessons/`, including the course specification, references, and learner journal.
- **Agent backbone:** `.agents/`, containing repository authority, course-state policy, and the archived specification-creation session.

These surfaces have separate ownership. Code builds independently; lessons may reference code; `.agents/` may coordinate both.

## Start here

1. Read `lessons/LEARNING_GUIDE.md` for the learner and assistant working rules.
2. Read `lessons/SPEC.md` for the course structure and chapter sequence.
3. Complete the recall drills in `lessons/CHAPTER-1-REVIEW.md`.
4. Begin Chapter 2, Lesson 2.1 by writing the POM from memory before consulting Chapter 1.

Repository scope and architecture are recorded in `.agents/PROJECT.md`. Lesson-owned decisions are recorded in the chapter that makes them.
