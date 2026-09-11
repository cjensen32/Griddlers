# Course State and Maintenance

This file governs changes to the course structure. Learner-facing instructions remain under `lessons/` and must not require this file to be understood.

## Ownership

- The learner owns everything under `src/`, every test, lesson progress, and the decisions recorded by the lesson that makes them.
- Agents write the open chapter's lesson files and keep them fitted to what the learner has actually built.
- The learner may rewrite any lesson file. An assignment is a proposal about how to spend the next session, not a contract.
- Agents may name missing behavior during a capstone gap analysis but ma not write the missing test.

## Chapter states

1. **Goal:** one sentence stating the chapter's destination, and nothing else. Every chapter carries this from day one and keeps it until it opens.
2. **Open:** the chapter owns a directory under `lessons/` holding its README, its lesson files, and a stub `CAPSTONE.md`.
3. **Closed:** every lesson is green and the capstone has been written from the code and tests that exist.

Only one chapter is open at a time. A chapter that is not open has no lessons, no guarantees, and no test plan anywhere in the repository. Do not draft them, and do not restore them from the archive or from Git history into a live document.

## Lesson states inside the open chapter

1. **Stub:** a title and an empty verification line. Every lesson starts here when the chapter opens.
2. **Written:** the lesson's task and its named verification with test tier.
3. **Green:** the named test passes and the commit has landed.

Exactly one lesson is written at a time. When the current lesson goes green, the next one is written against the code that actually exists by then. Writing ahead is the failure this model exists to prevent: a lesson drafted before the one under it is green assumes choices that have not been made yet, and when they were made differently, either the earlier lesson gets padded to fit or the later one stops making sense. Writing one at a time also removes the refit that a queue makes necessary, which was the larger cost in practice.

Keep lesson files short. A lesson states what to build and what to verify; the explanation belongs in the conversation, and the proof belongs in the test.

Two states sit outside the three above, and both exist because the learner asked for them rather than because the model needed them.

- **Optional:** a lesson the chapter does not depend on. It is written, it is skippable, and it does not block the capstone. Chapter 2's Git lesson became one on 2026-09-07: the learner was already fluent in the material and the machinery it described was already installed and working, so the assignment became a read-through of the conventions and moved to the end of the chapter. Give an optional lesson an em-dash in the Hours column, mark it `optional` in the chapter README, and say in the lesson's own first paragraph that it builds nothing and blocks nothing.
- **Ahead:** a lesson written past the current one at the learner's explicit request. It is a declared exception, not a mistake, and the collector notes it rather than warning about it. The risk the limit exists to prevent still applies in full: a lesson written before the one under it is green assumes choices that have not been made yet, so an `ahead` lesson is refitted against what was actually built before it is opened. Do not write a second lesson `ahead`.

Neither state exempts a lesson from the refit. An `optional` lesson still gets corrected when the code it describes changes, and an `ahead` lesson is refitted when the lesson under it goes green.

A checkbox specifies what must exist, never how to build it. The learner solves a lesson the shortest way its boxes allow, which is the correct way to solve it - so anything a later lesson or the capstone depends on that no box names will not be there when that lesson opens. Write the box as the outcome and let the mechanism that satisfies it be what the learner has to find. "`mvn validate` fails the build on a style violation" is a specification; "add an `<execution>` binding Checkstyle to `validate`" is a step-by-step guide wearing a checkbox, and teaches nothing the learner did not already read. Be thorough in the requirements, never in the instructions.

## Moving a chapter forward

### Goal to open

- The learner opens the chapter on arrival, never earlier.
- Create the directory, the stub per lesson, and the stub `CAPSTONE.md`.
- The chapter's "Must be true" list is authored at this point and lives only in that stub.

### The lesson cycle

One turn of this loop per lesson. The agent writes the assignment; the learner writes every line of code and every test in it.

1. The agent writes the lesson file: what it builds, what must exist when it is done, what closes it, and which tools to reach for.
2. The learner works on it.
3. The learner asks questions against the lesson, which the agent answers with concepts and examples on unrelated code.
4. The learner submits the lesson.
5. The agent reviews the submission and approves it, or returns it with what is missing and which concept to revisit - never with the correction written out.
6. The learner commits, and the lesson's hours are recorded in the chapter README's per-lesson Hours column. A guess is fine; an unrecorded lesson is not.
7. **The agent writes the next lesson against what was actually built.** This is the step that keeps the chapter coherent, and writing one at a time is what makes it a single pass rather than a correction: the next lesson's file names, assumptions, and verification come from the approved result rather than from a guess made before it existed.

Steps 3 and 5 through 7 are a procedure rather than a state rule, and `.agents/skills/review-submission/SKILL.md` owns it: what to collect before reviewing anything, the journal audit that runs without being asked for, how a lesson is returned, the optional follow-up step and its three tags, the feedback the learner raises at submission and the consensus it must reach before anything is edited, and what closes the cycle afterward. Read that skill before processing a submission.

Every lesson names observable verification and its test tier. The learner writes the test; naming a gap is help, filling it is not.

### Open to closed

- Wait until every lesson and its learner-written tests are complete.
- Grade the code and suite that actually exist instead of prescribing a target in advance.
- Run the capstone lane of `.agents/skills/review-submission/SKILL.md`: the four gates in order, the Gate 3 count recorded in `lessons/JOURNAL.md`, and the hours rolled up into the chapter README and the syllabus course map.

## Maintenance rules

- Example, never solution. Demonstrate difficult concepts only on unrelated subject matter.
- Preserve the four-state model and the learner's naming and architecture decisions.
- Treat prior chats and the archived handoff as dated evidence, not live authority.
- Make the smallest correction that resolves a demonstrated conflict or stale reference.
- Keep test maintenance explicit: tests may be revised, restructured, or deleted as behavior grows.
- Verify course edits with `git diff --check`, a relative-link audit, an unresolved-variable audit, a scan for implementation or test leakage, and a check that no Markdown line ends mid-sentence.
- Follow the Markdown rule in `.agents/reference/COURSE_STANDARDS.md`: one line per logical line, no hard wrapping, and reflows committed separately from content changes.
